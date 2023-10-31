package com.zj.wechat.controller;

import com.alibaba.fastjson.JSONObject;
import com.zj.common.entity.R;
import com.zj.wechat.service.FuncService;
import com.zj.wechat.service.WeChatService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhujie
 * @ClassName
 * @Description 暴露在公网上
 * @date: 2023/10/31
 */
@Slf4j
@RestController
@RequestMapping("/func")
public class FunctionController {

    @Resource
    WeChatService service;

    @Resource
    FuncService funcService;

    @Value("${cfg.gprToken}")
    private String gprToken;

    /**
     *  调用chatGpr生成一篇doc
     * @param map
     * @return
     */
    @PostMapping("chatGpr")
    public R<?> handler(@RequestBody Map<String,Object> map)
    {
        log.info("[IN-req]/func/chatGpr,reqBody is {}", JSONObject.toJSONString(map));
        try
        {
            Map<String,Object> result = funcService.createDoc(map);
            log.info("[IN-rsp]/func/chatGpr");
            return R.ok(result);
        }
        catch (Exception ex)
        {
            log.error("", ex);
            return R.fail();
        }
    }

    /**
     *  调用chatGpr生成一篇doc
     * @param reqBody
     * @return
     */
    @PostMapping("downloadDoc")
    public void downloadDoc(@RequestBody Map<String,Object>reqBody, HttpServletResponse response) throws IOException {
        log.info("[IN-req]/func/downloadDoc");
        BufferedReader reader = null;
        OutputStream os = null;
        try
        {
            os = response.getOutputStream();
            String path = (String) reqBody.get("path");
            String name = (String) reqBody.get("name");
            String fileContent = "";
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));
            String line;
            while ((line = reader.readLine()) != null)
            {
                fileContent += line;
            }
            XWPFDocument document= new XWPFDocument();
            //分页
            XWPFParagraph firstParagraph = document.createParagraph();
            //格式化段落
            firstParagraph.getStyleID();
            XWPFRun run = firstParagraph.createRun();
            run.setText(fileContent);
            document.createTOC();

            response.setContentType("application/octet-stream;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(name,"utf-8"));
            //遵守缓存规定
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            //输出流
            document.write(os);
            os.flush();
            os.close();
            log.info("[IN-rsp]/wechat/downloadDoc");
        }
        catch (Exception ex)
        {
            log.error("", ex);
        }
        finally {
            if(null != reader)
            {
                reader.close();
            }
            if(null != os)
            {
                os.close();
            }
        }
    }

    /**
     * zip压缩包示意
     * @param response
     * @param params
     */
    @PostMapping("downloadZip")
    public void downloadFile(HttpServletResponse response, @RequestBody Map<String, Object> params)
    {
        log.info("[IN-req]/func/downloadZip,req-body is:{}", JSONObject.toJSONString(params));
        try
        {
            List<String> nameList = new ArrayList<>();
            List<String> urls = (List<String>) params.get("urls");
            for (String url:urls) {
                String[] fileName = url.split("/");
                nameList.add(fileName[fileName.length-1]);
            }
            //List<byte[]> list = service.getFileByteList(params);
            //ZipOutUtils.toZip(response,list,nameList);
            log.info("[IN-rsp]/func/downloadZip done");
        }
        catch(Exception ex)
        {
            log.error("",ex);
        }
    }

    /**
     * 压缩图片,大小为kb
     * @param file
     * @param file
     * @param size
     * @return
     */
    @PostMapping(value = "ysPic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void ysPic(HttpServletResponse response, @RequestParam(value = "file") MultipartFile file, @RequestParam("size") Long size)
    {
        log.info("[IN-req]/func/ysPic?POST");
        try {
            service.ysPic(response, file, size*1024);
            log.info("[IN-rsp]/func/ysPic done");
        } catch (Exception e) {
            log.error("压缩失败", e);
        }
    }
}
