package com.zj.entity;

import lombok.Data;

@Data
public class NacosService {
    private String name;
    private String groupName;
    private Long clusterCount;
    private Long healthyInstanceCount;
}
