package com.zj.wechat.service;

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
 * 微信AccessToken管理服务，独立管理token的获取与缓存
 */
@Service
public class WeChatTokenService {

    private static final Logger logger = LoggerFactory.getLogger(WeChatTokenService.class);

    private final ExpiringMap<String, String> tokenCache = ExpiringMap.builder()
            .maxSize(1)
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .variableExpiration()
            .build();

    @Value("${cfg.appId}")
    private String appId;

    @Value("${cfg.appSecret}")
    private String appSecret;

    @Resource(name = "restTemplate")
    private RestTemplate restTemplate;

    /**
     * 获取token，有缓存时效
     */
    public String getAccessToken() {
        String accessToken = tokenCache.get("token");
        if (null != accessToken) {
            return accessToken;
        }
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;
        logger.info("getAccessToken:{}", url);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String body = responseEntity.getBody();
        logger.info(JSONObject.toJSONString(responseEntity));
        JSONObject obj = JSONObject.parseObject(body);
        String token = obj.getString("access_token");
        tokenCache.put("token", token, ExpirationPolicy.ACCESSED, obj.getLong("expires_in"), TimeUnit.MILLISECONDS);
        return token;
    }
}
