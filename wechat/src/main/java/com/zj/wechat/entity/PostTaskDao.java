package com.zj.wechat.entity;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PostTaskDao {

    int insert(PostTask task);

    int updateImageUrl(@Param("id") Long id, @Param("imageUrl") String imageUrl);

    int updateStatus(@Param("id") Long id, @Param("status") int status);

    int updateTaskContent(PostTask task);

    List<PostTask> queryAll(@Param("limit") Long limit, @Param("keyWord") String keyWord);

    PostTask queryById(@Param("id") Long id);

    PostTask queryByMsgId(@Param("msgId") String msgId);
}
