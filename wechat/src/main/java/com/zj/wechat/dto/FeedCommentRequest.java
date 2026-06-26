package com.zj.wechat.dto;

public class FeedCommentRequest {

    private Long feedId;
    private String content;

    public Long getFeedId() {
        return feedId;
    }

    public void setFeedId(Long feedId) {
        this.feedId = feedId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
