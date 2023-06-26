package com.zj.wechat.controller;

import com.alibaba.fastjson.JSONObject;
import com.zj.wechat.entity.R;
import com.zj.wechat.entity.WeChatMovie;
import com.zj.wechat.service.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movie")
public class WeChatMovieController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeChatMovieController.class);

    @Resource
    ResourceService service;

    @PostMapping("add")
    public R<?> addMovie(@RequestBody WeChatMovie body) {
        LOGGER.info("[IN-req]/movie/add, req-body is:{}", JSONObject.toJSONString(body));
        try {
            service.insertMovie(body);
            LOGGER.info("[IN-rsp]/movie/add done");
            return R.ok();
        } catch (Exception e) {
            LOGGER.error("", e);
            return R.fail();
        }
    }

    @PostMapping("update")
    public R<?> updateMovie(@RequestBody WeChatMovie body, @RequestParam("id")Long id)
    {
        LOGGER.info("[IN-req]/movie/update,req-body is:{},id is:{}", JSONObject.toJSONString(body), id);
        try
        {
            service.updateMovie(body,id);
            LOGGER.info("[IN-rsp]/music/update done");
            return R.ok();
        } catch (Exception e) {
            LOGGER.error("", e);
            return R.fail(500, "internal server error");
        }
    }

    @PostMapping("delete")
    public R<?> batchDelete(@RequestBody Map<String,Object> body)
    {
        LOGGER.info("[IN-req]/movie/delete,req-body is:{}", JSONObject.toJSONString(body));
        try
        {
            List<Long> ids = (List<Long>) body.get("ids");
            service.batchDeleteMovie(ids);
            LOGGER.info("[IN-rsp]/music/delete done");
            return R.ok();
        } catch (Exception e) {
            LOGGER.error("", e);
            return R.fail(500, "internal server error");
        }
    }

    /**
     * 道路列表查询
     * @param body
     * @param pageSize
     * @param pageNum
     * @return
     */
    @GetMapping("list")
    public R<?> queryMovie(@RequestParam("limit")Long limit, @RequestParam(value = "keyWord", required = false)String keyWord) {
        LOGGER.info("[IN-req]/movie/list,req-keyWord is:{}", keyWord);
        try {
            List<WeChatMovie> list = service.queryMovieList(limit, keyWord);
            LOGGER.info("[IN-rsp]musics/list,rsp is {}", JSONObject.toJSONString(list));
            return R.ok(list);
        } catch (Exception e) {
            LOGGER.error("", e);
            return R.fail(500, "internal server error");
        }
    }
}
