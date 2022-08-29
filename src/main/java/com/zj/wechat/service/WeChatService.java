package com.zj.wechat.service;

import com.alibaba.fastjson.JSONObject;
import com.zj.wechat.entity.*;
import com.zj.wechat.pojo.Constants;
import com.zj.wechat.pojo.MsgEntity;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class WeChatService {

    private static final Logger logger = LoggerFactory.getLogger(WeChatService.class);

    @Resource
    WeChatMediaInfoDao mediaInfoDao;

    private final ExpiringMap<String, String> map = ExpiringMap.builder()
            .maxSize(1)
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .variableExpiration()
            .build();

    @Value("${cfg.appId}")
    private String appId;

    @Value("${cfg.appSecret}")
    private String appSecret;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private MsgHandler handler;

    /**
     * 处理消息
     *
     * @param xmlStr
     */
    public String handleMsg(String xmlStr) {
        MsgEntity entity = JSONObject.parseObject(xmlStr, MsgEntity.class);
        return handler.handleMsg(entity);
    }

    /**
     * 获取token,有时效
     *
     * @return
     */
    public String getAccessToken() {
        String accessToken = map.get("token");
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
        //存入内存中，获取token每日限制次数的
        map.put("token", token, ExpirationPolicy.ACCESSED, obj.getLong("expires_in"), TimeUnit.MILLISECONDS);
        return token;
    }

    /**
     * 支持上传图片到微信素材库
     *
     * @param file
     * @param use
     * @return
     * @throws IOException
     */
    public String uploadFile(MultipartFile file, String name) throws IOException {
        String token = map.get("token");
        if ("".equals(token) || null == token) {
            token = getAccessToken();
        }
        String url = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=" + token + "&type=image";
        logger.info("upload:{}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        byte[] bytes = file.getBytes();
         // 将字节数组转成 ByteArrayResource
        ByteArrayResource byteArrayResource = new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }};
        map.add("media", byteArrayResource);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);
        String body = responseEntity.getBody();
        logger.info(JSONObject.toJSONString(body));
        JSONObject obj = JSONObject.parseObject(body);
        write2DB(obj, name);
        return obj.getString("media_id");
    }

    @Async
    public void write2DB(JSONObject obj, String name) {
        WeChatMediaInfo dto = new WeChatMediaInfo();
        dto.setName(name);
        dto.setMediaId(obj.getString("media_id"));
        dto.setUrl(obj.getString("url"));
        dto.setMediaType(Constants.MEDIA_TYPE_IMAGE);
        mediaInfoDao.insert(dto);
    }

    /**
     * 创建菜单
     *
     * @param jsonMenu
     * @return
     */
    public Integer createMenu(String jsonMenu) {
        int result = 0;
        // 拼装创建菜单的url
        String url = Constants.MENU_CREATE_URL + getAccessToken();
        // 调用接口创建菜单
        JSONObject jsonObject = null;
        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, jsonMenu, String.class);
            String body = responseEntity.getBody();
            logger.info(JSONObject.toJSONString(body));
            jsonObject = JSONObject.parseObject(body);
        } catch (Exception e) {
            logger.error("", e);
        }
        if (null != jsonObject) {
            if (0 != jsonObject.getInteger("errcode")) {
                result = jsonObject.getInteger("errcode");
                logger.error("创建菜单失败 errcode:" + jsonObject.getInteger("errcode")
                        + "，errmsg:" + jsonObject.getString("errmsg"));
            }
        }
        return result;
    }

    /**
     * 是否打开自定义菜单，否的话才调用创建接口
     *
     * @return
     */
    public boolean isOpenMenu() {
        String url = Constants.MENU_GET_URL + getAccessToken();
        logger.info("getMenu:{}", url);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String body = responseEntity.getBody();
        logger.info(JSONObject.toJSONString(responseEntity));
        JSONObject obj = JSONObject.parseObject(body);
        //0为未打开
        return 0 != obj.getInteger("is_menu_open");
    }

    /**
     * 从微信平台获取发布文章列表
     * @return
     */
    public List<ArticleNewsItem> getArticle() {
        List<ArticleNewsItem> result = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        map.put("offset", 0);
        map.put("count", 50);
        map.put("no_content", 1);
        String url = Constants.ARTICLE_GET_URL + getAccessToken();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, JSONObject.toJSONString(map), String.class);
        String body = responseEntity.getBody();
        //中文乱码
        byte[] bytes = body.getBytes(StandardCharsets.ISO_8859_1);
        body = new String(bytes, StandardCharsets.UTF_8);
        logger.info(JSONObject.toJSONString(body));
        ArticleRsp rsp = JSONObject.parseObject(body, ArticleRsp.class);
        List<ArticleItem> list = rsp.getItem();
        for (ArticleItem item:list) {
            //里层数据直接拼接id组装返回
            ArticleNewsItem news_item = item.getContent().getNews_item().get(0);
            news_item.setArticle_id(item.getArticle_id());
            result.add(news_item);
        }
        return result;
    }
}
