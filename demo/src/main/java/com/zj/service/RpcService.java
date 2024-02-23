package com.zj.service;

import com.zj.api.feign.OutServiceFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class RpcService {

    @Resource
    private OutServiceFeign outServiceFeign;


    /**
     * 调用out服务接口
     */
    public String remoteOut() {
        log.info("[out-req]/out/hello");
        String result = outServiceFeign.hello();
        log.info("[out-rsp]/out/hello:{}", result);
        return result;
    }
}
