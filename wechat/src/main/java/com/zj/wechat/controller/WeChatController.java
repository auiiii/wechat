package com.zj.wechat.controller;

import com.alibaba.fastjson.JSONObject;
import com.zj.common.entity.R;
import com.zj.wechat.service.WeChatService;
import com.zj.wechat.util.SignatureUtil;
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


    @GetMapping("process")
    public R<?> process(@RequestParam("operation")String operation, @RequestParam("operator")String operator)
    {
        service.process(operation,operator);
        return R.ok();
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
     * @param request
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

}
