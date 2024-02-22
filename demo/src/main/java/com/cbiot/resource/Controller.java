package com.cbiot.resource;

import com.alibaba.fastjson.JSONObject;
import com.cbiot.entity.ResponseVO;
import com.cbiot.entity.XgDao;
import com.cbiot.entity.XgEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@Slf4j
@RequestMapping("/shz-npn/dd/")
public class Controller {

    @Resource
    private XgDao xgDao;

    @Value("${test.name:default}")
    private String name;//测试读取配置

    @PostMapping(value="/verify")
    public ResponseVO<String> verify(@RequestBody XgEntity entity){
        log.info("start request /shz-npn/dd/verify:{}",JSONObject.toJSONString(entity));
        if(StringUtils.isEmpty(entity.getMsisdn()) || StringUtils.isEmpty(entity.getSchoolNo()))
        {
            return new ResponseVO<>("400","请求参数不合法，学工号或者号码为空",null);
        }
        XgEntity dto = xgDao.selectByXgh(entity.getMsisdn(),entity.getSchoolNo());
        String result = "0";
        if(null != dto && 1 == dto.getRyzt())
        {
            result = "1";
        }
        log.info("end request /shz-npn/dd/verify,done,dto:{}",JSONObject.toJSONString(dto));
        return new ResponseVO<>("0","success",result);
    }

    @GetMapping("hello")
    public ResponseVO<String> hello(){
        log.info("start request /shz-npn/dd/hello");
        return new ResponseVO<>("0","",name);
    }

    @GetMapping("test")
    public ResponseVO<String> test(){
        log.info("start request /shz-npn/dd/test");
        XgEntity testDto = xgDao.selectOne();
        log.info("end request /shz-npn/dd/test,done,dto:{}",JSONObject.toJSONString(testDto));
        return new ResponseVO<>("0","",testDto.getXm());
    }
}
