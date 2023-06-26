package com.zj.wechat.entity;

public class ArticleItem {

    private String article_id;
    private ArticleContent content;
    private Long update_time;

    public String getArticle_id() {
        return article_id;
    }

    public void setArticle_id(String article_id) {
        this.article_id = article_id;
    }

    public ArticleContent getContent() {
        return content;
    }

    public void setContent(ArticleContent content) {
        this.content = content;
    }

    public Long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Long update_time) {
        this.update_time = update_time;
    }
}
