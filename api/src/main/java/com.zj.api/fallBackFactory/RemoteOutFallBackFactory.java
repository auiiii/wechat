package com.zj.api.fallBackFactory;

import com.zj.api.feign.OutServiceFeign;
import com.zj.common.entity.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * 熔断配置
 */
@Component
public class RemoteOutFallBackFactory implements FallbackFactory<OutServiceFeign> {

    private static final Logger logger = LoggerFactory.getLogger(RemoteOutFallBackFactory.class);

    @Override
    public OutServiceFeign create(Throwable cause) {
        logger.error("out服务调用失败:{}", cause.getMessage());
        return new OutServiceFeign() {
            @Override
            public String hello() {
                return "调用hello接口失败:";
            }

            @Override
            public Map<String,Object> process(String operation, String operator) {
                logger.error("调用process接口失败:");
                HashMap<String, Object> result = new HashMap<>();
                result.put("errMsg","调用process接口失败:");
                return result;
            }
        };
    }
}
