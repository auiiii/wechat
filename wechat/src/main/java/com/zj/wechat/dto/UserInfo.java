package com.zj.wechat.dto;

/**
 * 登录返回的用户信息（脱敏后），供所有登录接口统一返回。
 */
public class UserInfo {

    private Long id;
    private String nickname;
    private String avatar;
    private String phone;

    public UserInfo() {
    }

    public UserInfo(Long id, String nickname, String avatar, String phone) {
        this.id = id;
        this.nickname = nickname;
        this.avatar = avatar;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
