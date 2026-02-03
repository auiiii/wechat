package com.zj.wechat.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.zj.common.entity.R;
import com.zj.wechat.config.CustomSheetWriteHandler;
import com.zj.wechat.entity.WeChatMovie;
import com.zj.wechat.pojo.MovieExportDTO;
import com.zj.wechat.service.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Arrays;
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


    @GetMapping("export")
    public void exportExcel(@RequestParam("limit")Long limit, @RequestParam(value = "keyWord", required = false)String keyWord, HttpServletResponse response) {
        LOGGER.info("[IN-req]/movie/export,req-keyWord is:{}", keyWord);
        try {
            List<WeChatMovie> list = service.queryMovieList(limit, keyWord);
            // 1. 设置响应类型为 Excel（.xlsx）
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 2. 设置文件名（关键步骤）
            String fileName = "存档电影表"; // 中文文件名
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8")
                    .replaceAll("\\+", "%20"); // 防止空格变加号（某些浏览器）
            // 3. 设置 Content-Disposition 响应头
            response.setHeader("Content-disposition",
                    "attachment; filename=" + encodedFileName + ".xlsx");

            List<Integer> columnWidths = Arrays.asList(10, 10, 10, 20, 20, 40, 30, 1);
            EasyExcel.write(response.getOutputStream(), MovieExportDTO.class)
                    .sheet("Sheet1")
                    .registerWriteHandler(new CustomSheetWriteHandler(columnWidths))
                    .doWrite(list);
            }
            catch (Exception e)
            {
                LOGGER.warn("", e);
            }
            LOGGER.info("[IN-rsp]movie/export,done");
    }
}
