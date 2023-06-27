package com.zj.out.controller;

import com.zj.common.entity.R;
import com.zj.out.service.OutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * 功能性及第三方接口
 */
@RestController
@RequestMapping("/out")
public class OutSevController {

    private static final Logger logger = LoggerFactory.getLogger(OutSevController.class);

    @Resource
    OutService service;

    @GetMapping("hello")
    public String hello()
    {
        return "hello-out";
    }


    /**
     * 写流水
     * @return
     */
    @PostMapping("process")
    public R<?> process(@RequestParam("operation")String operation, @RequestParam("operator")String operator)
    {
        logger.info("[IN-req]/out/process:{},{}", operation, operator);
        service.process(operation,operator);
        logger.info("[IN-req]/out/process");
        return R.ok();
    }


}
