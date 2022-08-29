package com.zj.wechat.entity;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WeChatMovieDao {
    int insert(WeChatMovie weChatMovie);

    int insertBatch(@Param("entities") List<WeChatMovie> entities);

    int update(WeChatMovie weChatMovie);

    int deleteBatchByIds(@Param("ids") List<Long> ids);

    List<WeChatMovie> queryAll(@Param("limit")Long limit, @Param("keyWord")String keyWord);

    WeChatMediaInfo queryByName(String name);

    WeChatMovie queryByRandom();
}
