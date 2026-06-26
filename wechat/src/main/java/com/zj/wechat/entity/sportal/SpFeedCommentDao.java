package com.zj.wechat.entity.sportal;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SpFeedCommentDao {

    int insert(SpFeedComment comment);

    List<SpFeedComment> queryByFeedId(@Param("feedId") Long feedId);

    int incrementLikes(@Param("id") Long id);

    int decrementLikes(@Param("id") Long id);

    int deleteById(@Param("id") Long id, @Param("userId") Long userId);
}
