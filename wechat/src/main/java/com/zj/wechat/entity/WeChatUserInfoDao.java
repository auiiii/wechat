package com.zj.wechat.entity;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WeChatUserInfoDao {

    int insert(WeChatUserInfo weChatMediaInfo);

    int insertBatch(@Param("entities") List<WeChatUserInfo> entities);

    int update(WeChatUserInfo weChatMediaInfo);

    int deleteBatchByIds(@Param("ids") List<Long> ids);

    List<WeChatUserInfo> queryAll(WeChatUserInfo weChatMediaInfo);

    WeChatUserInfo queryByUserName(String name);

}
