package com.zj.task;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.fastjson.JSONObject;
import com.zj.entity.NacosNameSpace;
import com.zj.entity.NacosNameSpaceRsp;
import com.zj.entity.NacosService;
import com.zj.entity.NacosServiceRsp;
import com.zj.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class NacosRegisterTask {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private NacosDiscoveryProperties discoveryProperties;

    @Scheduled(cron = "${customization.dd.syncWhitelist.cron:0 0/1 * * * ? }")
    public void syncWhitelist() {
        log.info("定时nacos心跳:" + LocalDateTime.now() + "," + Thread.currentThread().getName());
        Map<String, Object> clusterInfo = new HashMap<>();
        String addr = discoveryProperties.getServerAddr();
        String address = "http://" + addr + "/nacos/v1/console/namespaces";
        ResponseEntity<NacosNameSpaceRsp> rsp = restTemplate.getForEntity(address, NacosNameSpaceRsp.class);
        log.info("获取全部命名空间信息:{}", JSONObject.toJSONString(rsp.getBody()));
        List<NacosNameSpace> allNameSpaces = rsp.getBody().getData();
        for (NacosNameSpace nameSpace:allNameSpaces)
        {
            //根据集群获取其中的服务
            String serviceAddr = "http://" + addr + "/nacos/v1/ns/catalog/services?pageNo=1&pageSize=10&&namespaceId=" + nameSpace.getNamespace();
            ResponseEntity<NacosServiceRsp> serviceRsp = restTemplate.getForEntity(serviceAddr, NacosServiceRsp.class);
            log.info("获取服务列表信息:{}", JSONObject.toJSONString(serviceRsp.getBody()));
            List<NacosService> serviceList = serviceRsp.getBody().getServiceList();
            if(CollectionUtil.isNotEmpty(serviceList))
            {
                serviceList.forEach(
                        x->{
                            if(StringUtils.isNotEmpty(x.getName()))
                            {
                                clusterInfo.put(x.getName(), nameSpace.getNamespace() +  "&" + nameSpace.getNamespaceShowName());
                            }
                        }
                );
            }
        }
        redisUtils.del("nacos.namespace.info");
        redisUtils.addRedisHash("nacos.namespace.info", clusterInfo);
        log.info("nacos心跳缓存完毕");
    }

}
