package com.zj.config;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceInstance;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.zj.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;

import javax.annotation.Resource;
import java.util.*;

/**
 * 重写服务发现，该版本不涉及负载均衡
 *
 */
@Slf4j
public class DiyNacosServiceDiscovery extends NacosServiceDiscovery {

    @Resource
    private RedisUtils redisUtils;

    public DiyNacosServiceDiscovery(NacosDiscoveryProperties discoveryProperties, NacosServiceManager nacosServiceManager) {
        super(discoveryProperties, nacosServiceManager);
        this.discoveryProperties = discoveryProperties;
        this.nacosServiceManager = nacosServiceManager;
    }

    private NacosDiscoveryProperties discoveryProperties;
    private NacosServiceManager nacosServiceManager;

    @Override
    public List<ServiceInstance> getInstances(String serviceId) throws NacosException {
        String group = this.discoveryProperties.getGroup();
        // 优先保证同分组下的服务调用
        List<Instance> instances = this.namingService().selectInstances(serviceId, group, true);
        if (CollectionUtil.isEmpty(instances)) {
            // 如果同分组下找不到服务,通过定时缓存的集群信息匹配
            String nameSpaceInfo = (String) redisUtils.getRedisHash("nacos.namespace.info", serviceId);
            log.info("通过hash匹配:{}", nameSpaceInfo);
            String nameSpace = nameSpaceInfo.split("&")[0];
            String nameSpaceShowName = nameSpaceInfo.split("&")[1];
            Properties properties = new Properties();
            properties.put("serverAddr", this.discoveryProperties.getServerAddr());
            properties.put("username", Objects.toString(this.discoveryProperties.getUsername(), ""));
            properties.put("password", Objects.toString(this.discoveryProperties.getPassword(), ""));
            properties.put("namespace", nameSpace);
            properties.put("com.alibaba.nacos.naming.log.filename", this.discoveryProperties.getLogName());
            properties.put("endpoint", this.discoveryProperties.getEndpoint());
            properties.put("accessKey", this.discoveryProperties.getAccessKey());
            properties.put("secretKey", this.discoveryProperties.getSecretKey());
            properties.put("clusterName", this.discoveryProperties.getClusterName());
            properties.put("namingLoadCacheAtStart", this.discoveryProperties.getNamingLoadCacheAtStart());
            NamingService nameService = NacosFactory.createNamingService(properties);
            instances = nameService.selectInstances(serviceId, nameSpaceShowName, true);
            if(CollectionUtil.isNotEmpty(instances))
            {
                log.info("通过缓存hash匹配:" + nameSpace + "获得实例:{} ", JSONObject.toJSONString(instances));
            }
        }
        return hostToServiceInstanceList(instances, serviceId);
    }

    private NamingService namingService() {
        return this.nacosServiceManager.getNamingService();
    }

    public static List<ServiceInstance> hostToServiceInstanceList(List<Instance> instances, String serviceId) {
        List<ServiceInstance> result = new ArrayList(instances.size());
        Iterator var3 = instances.iterator();

        while (var3.hasNext()) {
            Instance instance = (Instance) var3.next();
            ServiceInstance serviceInstance = hostToServiceInstance(instance, serviceId);
            if (serviceInstance != null) {
                result.add(serviceInstance);
            }
        }

        return result;
    }

    public static ServiceInstance hostToServiceInstance(Instance instance, String serviceId) {
        if (instance != null && instance.isEnabled() && instance.isHealthy()) {
            NacosServiceInstance nacosServiceInstance = new NacosServiceInstance();
            nacosServiceInstance.setHost(instance.getIp());
            nacosServiceInstance.setPort(instance.getPort());
            nacosServiceInstance.setServiceId(serviceId);
            nacosServiceInstance.setInstanceId(instance.getInstanceId());
            Map<String, String> metadata = new HashMap();
            metadata.put("nacos.instanceId", instance.getInstanceId());
            metadata.put("nacos.weight", instance.getWeight() + "");
            metadata.put("nacos.healthy", instance.isHealthy() + "");
            metadata.put("nacos.cluster", instance.getClusterName() + "");
            if (instance.getMetadata() != null) {
                metadata.putAll(instance.getMetadata());
            }

            metadata.put("nacos.ephemeral", String.valueOf(instance.isEphemeral()));
            nacosServiceInstance.setMetadata(metadata);
            if (metadata.containsKey("secure")) {
                boolean secure = Boolean.parseBoolean((String) metadata.get("secure"));
                nacosServiceInstance.setSecure(secure);
            }

            return nacosServiceInstance;
        } else {
            return null;
        }
    }
}
