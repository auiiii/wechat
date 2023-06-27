package com.zj.out.service;

import com.zj.out.entity.WeChatProcessDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OutService {

    private static final Logger logger = LoggerFactory.getLogger(OutService.class);

    @Resource
    private WeChatProcessDao processDao;



}
