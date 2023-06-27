package com.zj.api.feign;

import com.zj.api.fallBackFactory.RemoteOutFallBackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(contextId = "outServiceFeign", value = "out-service", fallbackFactory = RemoteOutFallBackFactory.class)
public interface OutServiceFeign {

    @GetMapping (value = "/out/hello")
    String hello();

    @PostMapping(value = "/out/process")
    Map<String,Object> process(@RequestParam("operation")String operation, @RequestParam("operator")String operator);
}
