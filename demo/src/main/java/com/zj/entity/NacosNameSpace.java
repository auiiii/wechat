package com.zj.entity;

import lombok.Data;

@Data
public class NacosNameSpace {
    private String namespaceShowName;
    private String namespace;
    private Integer configCount;
    private Integer type;
}
