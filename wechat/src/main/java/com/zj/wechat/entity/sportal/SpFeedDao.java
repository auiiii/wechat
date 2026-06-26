package com.zj.wechat.entity.sportal;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SpFeedDao {

    int insert(SpFeed feed);

    SpFeed queryById(@Param("id") Long id);

    List<SpFeed> queryFeedList(@Param("offset") int offset, @Param("limit") int limit);

    List<SpFeed> queryByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);

    int incrementLikes(@Param("id") Long id);

    int decrementLikes(@Param("id") Long id);

    int incrementCommentCount(@Param("id") Long id);

    int incrementViewCount(@Param("id") Long id);

    int deleteById(@Param("id") Long id, @Param("userId") Long userId);
}
