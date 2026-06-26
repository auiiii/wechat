package com.zj.wechat.entity.sportal;

import org.apache.ibatis.annotations.Param;

public interface SpFeedLikeDao {

    int insert(SpFeedLike like);

    int deleteFeedLike(@Param("userId") Long userId, @Param("feedId") Long feedId);

    int deleteCommentLike(@Param("userId") Long userId, @Param("commentId") Long commentId);

    SpFeedLike queryByUserAndFeed(@Param("userId") Long userId, @Param("feedId") Long feedId);

    SpFeedLike queryByUserAndComment(@Param("userId") Long userId, @Param("commentId") Long commentId);
}
