package com.zj.wechat.service;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhujie
 * @ClassName
 * @Description
 * @date: 2023/10/31
 */
@Slf4j
@Service
public class FuncService {

    @Resource(name = "restTemplate")
    private RestTemplate restTemplate;

    /**
     * 基于参数提问生成doc-原网站已倒闭
     * @param map
     * @return
     */
    public Map<String, Object> createDoc(Map<String, Object> map) {
        String param = (String) map.get("param");
        String token = (String) map.get("token");
        Map<String, Object> result = new HashMap<>();
        List<String> list = new ArrayList<>();
        String q1 = "英文写一篇3000个单词以上的关于" + param + "的文章";
        String q2 = "英文写世界上关于" + param + "的调查数据";
        String q3 = "英文写人们对于"+ param +"的看法和态度";
        String q4 = "英文写科学家对于"+ param +"的看法和态度";
        list.add(q1);
        list.add(q2);
        list.add(q3);
        list.add(q4);
        for (String question:list) {
            String path = getRspFromGpr(question,true, param, token);
            result.put("path", path);
            result.put("name", param);
        }
        return result;
    }

    /**
     *
     * @param question
     */
    private String getRspFromGpr(String question, Boolean isEnglish, String name, String token) {
        String path = "";
        Map<String,Object> reqBody = new HashMap<>();
        reqBody.put("model", "gpt-3.5-turbo");
        reqBody.put("max_tokens", 1300);
        reqBody.put("top_p", 1);
        reqBody.put("temperature", 0.5);
        reqBody.put("frequency_penalty", 0);
        reqBody.put("presence_penalty", 0);
        reqBody.put("stream", true);
        JSONArray arrayStop = new JSONArray();
        arrayStop.add("ME:");
        arrayStop.add("AI:");
        reqBody.put("stop", arrayStop);
        JSONArray arrayMsg = new JSONArray();
        JSONObject messages = new JSONObject();
        messages.put("role","user");
        messages.put("content", question);
        messages.put("name","ME");
        arrayMsg.add(messages);
        reqBody.put("messages", arrayMsg);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("authorization", token);
        httpHeaders.add("authority","cf-chat.zecoba.cn");
        httpHeaders.add("origin","https://chat.zecoba.cn");
        httpHeaders.add("referer","https://chat.zecoba.cn");
        HttpEntity<String> httpEntity = new HttpEntity<>(JSONObject.toJSONString(reqBody), httpHeaders);
        String fulltext = "";
        String url = "https://cf-chat.zecoba.cn/v1/chat/completions";
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
        String body = responseEntity.getBody();
        log.debug(body);
        if(null != body && !"".equals(body))
        {
            //不规则json段,拆分字符串
            String a[] = body.split("content");
            for (String aa:a) {
                String flag = isEnglish? "\":\"" : "\": \"";
                int index = isEnglish? 3:4;
                if(aa.startsWith(flag))
                {
                    String b = aa.substring(index,aa.length()-1);
                    String content = b.split("\"")[0];
                    log.info("content:" + content);
                    if(null != content)
                    {
                        fulltext += content;
                    }
                }
            }
        }
        log.info("getting rsp from gpr done, fulltext is {}", fulltext);
        if(null != fulltext)
        {
            fulltext = fulltext.replaceAll("\\n","").replaceAll("\\n\\n","");
            path = "/home/zj/demo/article" + name +".txt";
            if(FileUtil.exist(path))
            {
                FileUtil.appendUtf8String(fulltext, path);
            }
            else
            {
                FileUtil.writeUtf8String(fulltext, path);
            }
        }
        return path;
    }
}
