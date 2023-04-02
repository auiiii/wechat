package com.zj.wechat.rpc;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(contextId = "remoteService", value = "wechat", fallbackFactory = RemoteTestFactory.class)
public interface TestFeign {

    @GetMapping (value = "/wechat/hello")
    String process();
}
