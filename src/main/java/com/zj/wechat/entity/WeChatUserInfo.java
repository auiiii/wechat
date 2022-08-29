package com.zj.wechat.entity;

/**
 * 扩展用户行为的后续表格
 */
public class WeChatUserInfo {

    private Long id;
    private String userId;
    private String locationX;
    private String locationY;
    private String label;//细化定位
    private Integer isSeSe;//特殊用户标识

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLocationX() {
        return locationX;
    }

    public void setLocationX(String locationX) {
        this.locationX = locationX;
    }

    public String getLocationY() {
        return locationY;
    }

    public void setLocationY(String locationY) {
        this.locationY = locationY;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getIsSeSe() {
        return isSeSe;
    }

    public void setIsSeSe(Integer isSeSe) {
        this.isSeSe = isSeSe;
    }

}
