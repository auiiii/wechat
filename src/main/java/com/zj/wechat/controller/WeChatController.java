package com.zj.wechat.controller;

import com.alibaba.fastjson.JSONObject;
import com.zj.wechat.entity.R;
import com.zj.wechat.service.WeChatService;
import com.zj.wechat.util.SignatureUtil;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wechat")
public class WeChatController {

    private static final Logger logger = LoggerFactory.getLogger(WeChatController.class);

    @Resource
    WeChatService service;

    @GetMapping("wx")
    public Object verify(@RequestParam("signature") String signature, @RequestParam("timestamp") String timestamp,
                            @RequestParam("nonce") String nonce,  @RequestParam("echostr") String echostr) {
        logger.info("[IN-req]/wechat/wx?signature:{},timestamp:{},nonce:{},echostr:{}",signature,timestamp,nonce,echostr);
        try {
            if (SignatureUtil.checkSignature(signature, timestamp, nonce)) {
                //接入校验成功,返回随机字符串
                logger.info("接入成功, echostr {}", echostr);
                return echostr;
            }
            return "error";
        } catch (Exception e) {
            logger.error("", e);
            return "接入失败";
        }
    }

    @GetMapping("hello")
    public String hello()
    {
        return "hello-myweb";
    }

    @GetMapping("test")
    public String hello(@RequestParam("name")String name)
    {
        return service.setRedis(name);
    }

    @GetMapping("testRpc")
    public String testRpc(@RequestParam("name")String name)
    {
        return service.testRpc(name);
    }

    /**
     * 获取token能力接口
     * @return
     */
    @GetMapping("getToken")
    public String accessToken()
    {
        return service.getAccessToken();
    }

    /**
     * 上传图片
     * @return
     */
    @PostMapping(value = "uploadUrl", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<?> uploadUrl(@RequestParam(value = "file") MultipartFile file, @RequestParam("name") String name)
    {
        logger.info("[IN-req]/wechat/uploadUrl?POST" + name);
        try {
            return R.ok(service.uploadFile(file, name));
        } catch (Exception e) {
            logger.error("", e);
            return R.fail("上传失败");
        }
    }

    /**
     * 回复微信服务器
     * @param body
     * @return
     */
    @PostMapping("wx")
    public String handler(HttpServletRequest request)
    {
        logger.info("[IN-req]/wechat/wx?POST");
        Map<String,Object> map = new HashMap<>();
        try
        {
            SAXReader reader = new SAXReader();
            Document document = reader.read(request.getInputStream());
            // 得到xml根元素
            Element root = document.getRootElement();
            // 得到根元素的所有子节点
            List<Element> elementList = root.elements();
            // 遍历所有子节点,解析打印微信发来的消息
            for (Element e : elementList) {
                logger.info(e.getName() + "|" + e.getText());
                map.put(e.getName(), e.getText());
            }
            return service.handleMsg(JSONObject.toJSONString(map));
        }
        catch(Exception ex)
        {
            logger.error("", ex);
            return "error";
        }
    }

    /**
     *  调用chatGpr生成一篇doc
     * @param request
     * @return
     */
    @PostMapping("chatGpr")
    public R<?> handler(@RequestBody Map<String,Object> map)
    {
        logger.info("[IN-req]/wechat/chatGpr,reqBody is {}", JSONObject.toJSONString(map));
        try
        {
            Map<String,Object> result = service.createDoc(map);
            logger.info("[IN-rsp]/wechat/chatGpr");
            return R.ok(result);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            return R.fail();
        }
    }

    /**
     *  调用chatGpr生成一篇doc
     * @param request
     * @return
     */
    @PostMapping("downloadDoc")
    public void downloadDoc(@RequestBody Map<String,Object>reqBody, HttpServletResponse response) throws IOException {
        logger.info("[IN-req]/wechat/downloadDoc");
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
            logger.info("[IN-rsp]/wechat/downloadDoc");
        }
        catch (Exception ex)
        {
            logger.error("", ex);
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
}
