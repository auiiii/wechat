package com.zj.wechat.entity;

import java.util.List;

public class ArticleContent {

    private List<ArticleNewsItem> news_item;
    private Long create_time;
    private Long update_time;

    public List<ArticleNewsItem> getNews_item() {
        return news_item;
    }

    public void setNews_item(List<ArticleNewsItem> news_item) {
        this.news_item = news_item;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Long create_time) {
        this.create_time = create_time;
    }

    public Long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Long update_time) {
        this.update_time = update_time;
    }
}
