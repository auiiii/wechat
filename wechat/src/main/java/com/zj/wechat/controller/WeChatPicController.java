package com.zj.wechat.controller;

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
        try {
            service.insertPic(body);
            return R.ok();
        } catch (Exception e) {
            LOGGER.error("", e);
            return R.fail();
        }
    }

    @PostMapping("update")
    public R<?> updatePic(@RequestBody WeChatMediaInfo body, @RequestParam("id")Long id)
    {
        try
        {
            service.updatePic(body,id);
            return R.ok();
        } catch (Exception e) {
            LOGGER.error("", e);
            return R.fail(500, "internal server error");
        }
    }

    @PostMapping("delete")
    public R<?> batchDelete(@RequestBody Map<String,Object> body)
    {
        try
        {
            List<Long> ids = (List<Long>) body.get("ids");
            service.batchDeletePic(ids);
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
        try {
            List<WeChatMediaInfo> list = service.queryPicList(limit, keyWord);
            return R.ok(list);
        } catch (Exception e) {
            LOGGER.error("", e);
            return R.fail(500, "internal server error");
        }
    }
}
