package com.zj.wechat.controller;

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
        try {
            service.insertOpinion(body);
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
        try {
            List<String> list = service.queryOpinionList();
            return R.ok(list);
        } catch (Exception e) {
            LOGGER.error("", e);
            return R.fail(500, "internal server error");
        }
    }

    @PostMapping("/money/submit")
    public R<?> submitEmail(@RequestBody Map<String,Object> map) {
        try {
            service.submitEmail(map);
            return R.ok();
        } catch (Exception e) {
            LOGGER.error("", e);
            return R.fail(500, "internal server error");
        }
    }

    @PostMapping("/article/submit")
    public R<?> submitArticle(@RequestBody Map<String,Object> map) {
        try {
            service.submitArticle(map);
            return R.ok();
        } catch (Exception e) {
            LOGGER.error("", e);
            return R.fail(500, "internal server error");
        }
    }

    @GetMapping("/article/list")
    public R<?> articleList(){
        try {
            List<ArticleNewsItem> list = weChatService.getArticle();
            return R.ok(list);
        } catch (Exception e) {
            LOGGER.error("", e);
            return R.fail(500, "internal server error");
        }
    }

}
