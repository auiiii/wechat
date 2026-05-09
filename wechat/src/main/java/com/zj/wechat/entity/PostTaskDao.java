package com.zj.wechat.entity;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PostTaskDao {

    int insert(PostTask task);

    List<PostTask> queryAll(@Param("limit") Long limit, @Param("keyWord") String keyWord);

    PostTask queryById(@Param("id") Long id);
}
