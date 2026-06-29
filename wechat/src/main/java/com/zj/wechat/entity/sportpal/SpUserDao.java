package com.zj.wechat.entity.sportpal;

import org.apache.ibatis.annotations.Param;

public interface SpUserDao {

    int insert(SpUser user);

    SpUser queryById(@Param("id") Long id);

    SpUser queryByOpenId(@Param("openId") String openId);

    SpUser queryByUsername(@Param("username") String username);

    SpUser queryByPhone(@Param("phone") String phone);

    int updateProfile(SpUser user);

    int countFeedsByUserId(@Param("userId") Long userId);

    int countCheckinsByUserId(@Param("userId") Long userId);

    int countLikesReceivedByUserId(@Param("userId") Long userId);
}
