package com.zj.wechat.entity.sportal;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SpCheckinDao {

    int insert(SpCheckin checkin);

    List<SpCheckin> queryByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);

    int countTodayByUserId(@Param("userId") Long userId);
}
