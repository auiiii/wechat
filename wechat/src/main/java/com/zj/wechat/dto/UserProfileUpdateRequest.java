package com.zj.wechat.dto;

/**
 * 用户资料更新请求，支持部分更新：
 * <ul>
 *   <li>未传 / null / 纯空白字符串 均视为「不更新该字段」。</li>
 *   <li>至少需要传一个字段，由 Service 层校验。</li>
 *   <li>字段长度由 Service 层校验。</li>
 * </ul>
 */
public class UserProfileUpdateRequest {

    private String nickname;
    private String avatar;

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
}
