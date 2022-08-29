package com.zj.wechat.entity;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WeChatOpinionDao {

    int insert(WeChatOpinion weChatOpinion);

    List<String> queryList();

}
