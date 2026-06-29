package com.zj.wechat.service.sportpal;

import com.alibaba.fastjson.JSONObject;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 小程序专用 access_token 服务。
 *
 * 注意：不能复用 {@link com.zj.wechat.service.WeChatTokenService}，
 * 该服务取的是公众号（cfg.appId）token，而小程序接口必须使用小程序专用 token（cfg.sportpal.appId）。
 * 两个 token 体系相互独立，混用会导致接口调用失败（40001 invalid credential）。
 */
@Service
public class MiniProgramTokenService {

    private static final Logger logger = LoggerFactory.getLogger(MiniProgramTokenService.class);

    private final ExpiringMap<String, String> tokenCache = ExpiringMap.builder()
            .maxSize(1)
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .variableExpiration()
            .build();

    @Value("${cfg.sportpal.appId}")
    private String appId;

    @Value("${cfg.sportpal.appSecret}")
    private String appSecret;

    @Resource(name = "restTemplate")
    private RestTemplate restTemplate;

    /**
     * 获取小程序 access_token，带本地缓存。
     * 缓存按微信返回的 expires_in 自动过期，避免触发每日 2000 次的 token 获取频率限制。
     */
    public String getAccessToken() {
        String accessToken = tokenCache.get("token");
        if (null != accessToken) {
            return accessToken;
        }
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
                + appId + "&secret=" + appSecret;
        logger.info("miniProgram getAccessToken:{}", url);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String body = responseEntity.getBody();
        logger.info(JSONObject.toJSONString(responseEntity));
        JSONObject obj = JSONObject.parseObject(body);
        String token = obj.getString("access_token");
        if (token == null) {
            //fail-closed：token 获取失败（密钥错误/频率超限/网络异常）时直接抛出，调用方应拒绝本次业务
            throw new IllegalStateException("获取小程序 access_token 失败: " + body);
        }
        //预留 5 分钟提前量，避免临界过期
        long expiresIn = obj.getLong("expires_in") - 300L;
        tokenCache.put("token", token, ExpirationPolicy.ACCESSED, expiresIn, TimeUnit.SECONDS);
        return token;
    }
}
