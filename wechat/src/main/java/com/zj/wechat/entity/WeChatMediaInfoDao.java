package com.zj.wechat.entity;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WeChatMediaInfoDao {

    int insert(WeChatMediaInfo weChatMediaInfo);

    int insertBatch(@Param("entities") List<WeChatMediaInfo> entities);

    int update(WeChatMediaInfo weChatMediaInfo);

    int deleteBatchByIds(@Param("ids") List<Long> ids);

    List<WeChatMediaInfo> queryAll(@Param("limit")Long limit, @Param("keyWord")String keyWord);

    WeChatMediaInfo queryByName(String name);

    WeChatMediaInfo queryByRandom();
}
