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
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.alibaba.nacos.client.naming.NacosNamingService;
import com.zj.entity.NacosNameSpace;
import com.zj.entity.NacosNameSpaceRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.xml.stream.events.Namespace;
import java.util.*;

/**
 * 重写服务发现，该版本不涉及负载均衡
 *
 */
@Slf4j
public class DiyNacosServiceDiscovery extends NacosServiceDiscovery {

    @Resource
    private RestTemplate restTemplate;

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
        String address = "http://" + this.discoveryProperties.getServerAddr() + "/nacos/v1/console/namespaces";
        List<Instance> instances = this.namingService().selectInstances(serviceId, group, true);
        if (CollectionUtil.isEmpty(instances)) {
            // 如果同分组下找不到服务,遍历所有集群
            ResponseEntity<NacosNameSpaceRsp> rsp = restTemplate.getForEntity(address, NacosNameSpaceRsp.class);
            log.info("获取全部命名空间信息:{}", JSONObject.toJSONString(rsp.getBody()));
            List<NacosNameSpace> allNameSpaces = rsp.getBody().getData();
            for (NacosNameSpace nameSpace: allNameSpaces)
            {
                log.info("遍历命名空间:{}", nameSpace);
                Properties properties = new Properties();
                properties.put("serverAddr", this.discoveryProperties.getServerAddr());
                properties.put("username", Objects.toString(this.discoveryProperties.getUsername(), ""));
                properties.put("password", Objects.toString(this.discoveryProperties.getPassword(), ""));
                properties.put("namespace", nameSpace.getNamespace());
                properties.put("com.alibaba.nacos.naming.log.filename", this.discoveryProperties.getLogName());
                properties.put("endpoint", this.discoveryProperties.getEndpoint());
                properties.put("accessKey", this.discoveryProperties.getAccessKey());
                properties.put("secretKey", this.discoveryProperties.getSecretKey());
                properties.put("clusterName", this.discoveryProperties.getClusterName());
                properties.put("namingLoadCacheAtStart", this.discoveryProperties.getNamingLoadCacheAtStart());
                NamingService nameService = NacosFactory.createNamingService(properties);
                instances = nameService.selectInstances(serviceId, nameSpace.getNamespaceShowName(), true);
                if(CollectionUtil.isNotEmpty(instances))
                {
                    log.info("通过遍历命名空间:" + nameSpace + "获得实例:{} ", JSONObject.toJSONString(instances));
                    break;
                }
            }
        }
        return hostToServiceInstanceList(instances, serviceId);
    }

    private NamingService namingService() {
        return this.nacosServiceManager.getNamingService();
    }

    public static List<ServiceInstance> hostToServiceInstanceList(List<Instance> instances, String serviceId) {
        log.info("in");
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
