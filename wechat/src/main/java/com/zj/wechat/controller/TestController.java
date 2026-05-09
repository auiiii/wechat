package com.zj.wechat.controller;

import com.zj.wechat.pojo.MsgEntity;
import com.zj.wechat.service.MsgHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    MsgHandler msgHandler;


    @PostMapping(value = "test")
    public String test(@RequestParam("theme") String theme)
    {
        log.info("[IN-req]/func/test?POST");
        String result = null;
        try {
            MsgEntity entity = new MsgEntity();
            result = msgHandler.handlePostGeneration(entity, theme, "无");
            log.info("[IN-rsp]/func/test done");
        } catch (Exception e) {
            log.error("", e);
        }
        return result;
    }
}
