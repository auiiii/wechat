package com.zj.wechat.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;


/**
 * 熔断配置
 */
@Component
public class RemoteTestFactory implements FallbackFactory<TestFeign> {

    private static final Logger logger = LoggerFactory.getLogger(RemoteTestFactory.class);

    @Override
    public TestFeign create(Throwable cause) {
        logger.error("test服务调用失败:{}", cause.getMessage());
        return new TestFeign() {
            @Override
            public String process() {
                return "调用 process 接口失败:";
            }
        };
    }
}
