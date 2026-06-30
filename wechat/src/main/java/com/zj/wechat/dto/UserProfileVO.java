package com.zj.wechat.dto;

public class UserProfileVO {

    private Long userId;
    private String nickname;
    private String avatar;
    private String phone;
    private int feedCount;
    private int checkinCount;
    private int likeCount;
    /** 累计运动分钟数，来源于 sp_daily_logs.exercise_minutes 求和 */
    private long totalExerciseMinutes;
    private String createdAt;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getFeedCount() {
        return feedCount;
    }

    public void setFeedCount(int feedCount) {
        this.feedCount = feedCount;
    }

    public int getCheckinCount() {
        return checkinCount;
    }

    public void setCheckinCount(int checkinCount) {
        this.checkinCount = checkinCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public long getTotalExerciseMinutes() {
        return totalExerciseMinutes;
    }

    public void setTotalExerciseMinutes(long totalExerciseMinutes) {
        this.totalExerciseMinutes = totalExerciseMinutes;
    }
}
