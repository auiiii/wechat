package com.zj.entity;

import lombok.Data;

import java.util.List;

@Data
public class NacosServiceRsp {
    private Long count;
    private List<NacosService> serviceList;
}
