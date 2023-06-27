package com.zj.out.controller;

import com.zj.out.service.OutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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



}
