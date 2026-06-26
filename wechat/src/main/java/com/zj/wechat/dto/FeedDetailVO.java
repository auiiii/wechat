package com.zj.wechat.dto;

import java.util.List;

public class FeedDetailVO {

    private FeedVO feed;
    private List<CommentVO> comments;

    public FeedVO getFeed() {
        return feed;
    }

    public void setFeed(FeedVO feed) {
        this.feed = feed;
    }

    public List<CommentVO> getComments() {
        return comments;
    }

    public void setComments(List<CommentVO> comments) {
        this.comments = comments;
    }
}
