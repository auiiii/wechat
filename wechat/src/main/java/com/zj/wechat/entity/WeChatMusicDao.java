package com.zj.wechat.entity;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WeChatMusicDao {
    int insert(WeChatMusic weChatMusic);

    int insertBatch(@Param("entities") List<WeChatMusic> entities);

    int update(WeChatMusic weChatMusic);

    int deleteBatchByIds(@Param("ids") List<Long> ids);

    List<WeChatMusic> queryAll(@Param("limit")Long limit, @Param("keyWord")String keyWord);

    WeChatMediaInfo queryByName(String name);

    WeChatMusic queryByRandom();
}
