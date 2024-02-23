package com.zj.resource;

import com.zj.entity.ResponseVO;
import com.zj.service.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("")
public class RpcController {

    @Resource
    private RpcService rpcService;

    /**
     * 测试nacos跨组访问的接口
     * @return
     */
    @GetMapping("/rpc/test")
    public ResponseVO<String> rpcTest(){
        log.info("start request /rpc/test");
        String result = rpcService.remoteOut();
        return new ResponseVO<>("0","", result);
    }


}
