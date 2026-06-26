package com.zj.wechat.controller;

import com.zj.common.entity.R;
import com.zj.wechat.entity.WeChatMusic;
import com.zj.wechat.service.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/music")
public class WeChatMusicController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeChatMusicController.class);

    @Resource
    ResourceService service;

    @PostMapping("add")
    public R<?> addZgfTempBridge(@RequestBody WeChatMusic body) {
        try {
            service.insertMusic(body);
            return R.ok();
        } catch (Exception e) {
            LOGGER.error("", e);
            return R.fail();
        }
    }

    @PostMapping("update")
    public R<?> updateRoad(@RequestBody WeChatMusic body, @RequestParam("id")Long id)
    {
        try
        {
            service.updateMusic(body,id);
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
            service.batchDeleteMusic(ids);
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
    public R<?> queryDossierPage(@RequestParam("limit")Long limit, @RequestParam(value = "keyWord", required = false)String keyWord) {
        try {
            List<WeChatMusic> list = service.queryMusicList(limit, keyWord);
            return R.ok(list);
        } catch (Exception e) {
            LOGGER.error("", e);
            return R.fail(500, "internal server error");
        }
    }
}
