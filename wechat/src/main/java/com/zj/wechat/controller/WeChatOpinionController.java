package com.zj.wechat.controller;

import com.alibaba.fastjson.JSONObject;
import com.zj.common.entity.R;
import com.zj.wechat.entity.ArticleNewsItem;
import com.zj.wechat.entity.WeChatOpinion;
import com.zj.wechat.service.ResourceService;
import com.zj.wechat.service.WeChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("")
public class WeChatOpinionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeChatOpinionController.class);

    @Resource
    ResourceService service;

    @Resource
    WeChatService weChatService;

    @PostMapping("/opinion/submit")
    public R<?> addOpinion(@RequestBody WeChatOpinion body) {
        LOGGER.info("[IN-req]/opinion/submit, req-body is:{}", JSONObject.toJSONString(body));
        try {
            service.insertOpinion(body);
            LOGGER.info("[IN-rsp]/opinion/submit done");
            return R.ok();
        } catch (Exception e) {
            LOGGER.error("", e);
            return R.fail();
        }
    }

    /**
     * 道路列表查询
     * @param body
     * @param pageSize
     * @param pageNum
     * @return
     */
    @GetMapping("/opinion/list")
    public R<?> queryList() {
        LOGGER.info("[IN-req]/opinion/list");
        try {
            List<String> list = service.queryOpinionList();
            LOGGER.info("[IN-rsp]opinion/list,rsp is {}", JSONObject.toJSONString(list));
            return R.ok(list);
        } catch (Exception e) {
            LOGGER.error("", e);
            return R.fail(500, "internal server error");
        }
    }

    @PostMapping("/money/submit")
    public R<?> submitEmail(@RequestBody Map<String,Object> map) {
        LOGGER.info("[IN-req]/money/submit:{}", JSONObject.toJSONString(map));
        try {
            service.submitEmail(map);
            LOGGER.info("[IN-rsp]money/submit, done");
            return R.ok();
        } catch (Exception e) {
            LOGGER.error("", e);
            return R.fail(500, "internal server error");
        }
    }

    @PostMapping("/article/submit")
    public R<?> submitArticle(@RequestBody Map<String,Object> map) {
        LOGGER.info("[IN-req]/article/submit:{}", JSONObject.toJSONString(map));
        try {
            service.submitArticle(map);
            LOGGER.info("[IN-rsp]article/submit, done");
            return R.ok();
        } catch (Exception e) {
            LOGGER.error("", e);
            return R.fail(500, "internal server error");
        }
    }

    @GetMapping("/article/list")
    public R<?> articleList(){
        LOGGER.info("[IN-req]/article/list:{}");
        try {
            List<ArticleNewsItem> list = weChatService.getArticle();
            LOGGER.info("[IN-rsp]article/list,{}", JSONObject.toJSONString(list));
            return R.ok(list);
        } catch (Exception e) {
            LOGGER.error("", e);
            return R.fail(500, "internal server error");
        }
    }

}
