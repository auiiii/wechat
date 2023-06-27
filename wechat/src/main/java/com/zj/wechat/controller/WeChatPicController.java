package com.zj.wechat.controller;

import com.alibaba.fastjson.JSONObject;
import com.zj.common.entity.R;
import com.zj.wechat.entity.WeChatMediaInfo;
import com.zj.wechat.service.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pic")
public class WeChatPicController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeChatPicController.class);

    @Resource
    ResourceService service;

    @PostMapping("add")
    public R<?> addPic(@RequestBody WeChatMediaInfo body) {
        LOGGER.info("[IN-req]/pic/add, req-body is:{}", JSONObject.toJSONString(body));
        try {
            service.insertPic(body);
            LOGGER.info("[IN-rsp]/pic/add done");
            return R.ok();
        } catch (Exception e) {
            LOGGER.error("", e);
            return R.fail();
        }
    }

    @PostMapping("update")
    public R<?> updatePic(@RequestBody WeChatMediaInfo body, @RequestParam("id")Long id)
    {
        LOGGER.info("[IN-req]/pic/update,req-body is:{},id is:{}", JSONObject.toJSONString(body), id);
        try
        {
            service.updatePic(body,id);
            LOGGER.info("[IN-rsp]/pic/update done");
            return R.ok();
        } catch (Exception e) {
            LOGGER.error("", e);
            return R.fail(500, "internal server error");
        }
    }

    @PostMapping("delete")
    public R<?> batchDelete(@RequestBody Map<String,Object> body)
    {
        LOGGER.info("[IN-req]/pic/delete,req-body is:{}", JSONObject.toJSONString(body));
        try
        {
            List<Long> ids = (List<Long>) body.get("ids");
            service.batchDeletePic(ids);
            LOGGER.info("[IN-rsp]/pic/delete done");
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
    public R<?> queryPic(@RequestParam("limit")Long limit, @RequestParam(value = "keyWord", required = false)String keyWord) {
        LOGGER.info("[IN-req]/pic/list,req-keyWord is:{}", keyWord);
        try {
            List<WeChatMediaInfo> list = service.queryPicList(limit, keyWord);
            LOGGER.info("[IN-rsp]pic/list,rsp is {}", JSONObject.toJSONString(list));
            return R.ok(list);
        } catch (Exception e) {
            LOGGER.error("", e);
            return R.fail(500, "internal server error");
        }
    }
}
