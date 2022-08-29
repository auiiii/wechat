package com.zj.wechat.entity;

import java.util.List;

/**
 * 微信平台返回的json格式
 */
public class ArticleRsp {

    private Long total_count;
    private Long item_count;
    private List<ArticleItem> item;

    public Long getTotal_count() {
        return total_count;
    }

    public void setTotal_count(Long total_count) {
        this.total_count = total_count;
    }

    public Long getItem_count() {
        return item_count;
    }

    public void setItem_count(Long item_count) {
        this.item_count = item_count;
    }

    public List<ArticleItem> getItem() {
        return item;
    }

    public void setItem(List<ArticleItem> item) {
        this.item = item;
    }
}
