package com.zj.entity;

import lombok.Data;

import java.util.List;

@Data
public class NacosNameSpaceRsp {
    private String code;
    private String message;

    private List<NacosNameSpace> data;
}
