package com.zj.wechat.service.sportal;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.zj.wechat.dto.*;
import com.zj.wechat.entity.sportal.*;
import com.zj.wechat.util.TimeAgoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class FeedService {

    private static final Logger logger = LoggerFactory.getLogger(FeedService.class);

    @Resource
    private SpFeedDao spFeedDao;

    @Resource
    private SpFeedLikeDao spFeedLikeDao;

    @Resource
    private SpFeedCommentDao spFeedCommentDao;

    @Resource
    private SpUserDao spUserDao;

    @Transactional
    public FeedVO createFeed(Long userId, FeedCreateRequest request) {
        SpFeed feed = new SpFeed();
        feed.setUserId(userId);
        feed.setExerciseType(request.getExerciseType());
        feed.setContent(request.getContent());
        feed.setImages(request.getImages() != null ? JSON.toJSONString(request.getImages()) : null);
        feed.setLocationName(request.getLocationName());
        feed.setLikes(0);
        feed.setCommentCount(0);
        feed.setViewCount(0);

        spFeedDao.insert(feed);
        return toFeedVO(feed, userId, false);
    }

    public PageInfo<FeedVO> listFeeds(int page, int size, Long currentUserId) {
        PageHelper.offsetPage((page - 1) * size, size);
        List<SpFeed> feeds = spFeedDao.queryFeedList((page - 1) * size, size);
        List<FeedVO> voList = new ArrayList<>();
        for (SpFeed feed : feeds) {
            boolean liked = false;
            if (currentUserId != null) {
                liked = spFeedLikeDao.queryByUserAndFeed(currentUserId, feed.getId()) != null;
            }
            voList.add(toFeedVO(feed, currentUserId, liked));
        }
        PageInfo<FeedVO> pageInfo = new PageInfo<>();
        pageInfo.setList(voList);
        pageInfo.setTotal(voList.size());
        pageInfo.setPage(page);
        pageInfo.setSize(size);
        return pageInfo;
    }

    public FeedDetailVO getFeedDetail(Long feedId, Long currentUserId) {
        SpFeed feed = spFeedDao.queryById(feedId);
        if (feed == null) {
            throw new IllegalArgumentException("动态不存在");
        }
        spFeedDao.incrementViewCount(feedId);

        boolean liked = false;
        if (currentUserId != null) {
            liked = spFeedLikeDao.queryByUserAndFeed(currentUserId, feedId) != null;
        }
        FeedVO feedVO = toFeedVO(feed, currentUserId, liked);

        List<SpFeedComment> comments = spFeedCommentDao.queryByFeedId(feedId);
        List<CommentVO> commentVOs = new ArrayList<>();
        for (SpFeedComment c : comments) {
            boolean commentLiked = false;
            if (currentUserId != null) {
                commentLiked = spFeedLikeDao.queryByUserAndComment(currentUserId, c.getId()) != null;
            }
            commentVOs.add(toCommentVO(c, currentUserId, commentLiked));
        }

        FeedDetailVO detail = new FeedDetailVO();
        detail.setFeed(feedVO);
        detail.setComments(commentVOs);
        return detail;
    }

    @Transactional
    public boolean toggleLike(Long userId, Long feedId) {
        SpFeed feed = spFeedDao.queryById(feedId);
        if (feed == null) {
            throw new IllegalArgumentException("动态不存在");
        }

        SpFeedLike existing = spFeedLikeDao.queryByUserAndFeed(userId, feedId);
        if (existing != null) {
            spFeedLikeDao.deleteFeedLike(userId, feedId);
            spFeedDao.decrementLikes(feedId);
            return false;
        } else {
            SpFeedLike like = new SpFeedLike();
            like.setFeedId(feedId);
            like.setUserId(userId);
            spFeedLikeDao.insert(like);
            spFeedDao.incrementLikes(feedId);
            return true;
        }
    }

    @Transactional
    public CommentVO addComment(Long userId, FeedCommentRequest request) {
        SpFeed feed = spFeedDao.queryById(request.getFeedId());
        if (feed == null) {
            throw new IllegalArgumentException("动态不存在");
        }

        SpFeedComment comment = new SpFeedComment();
        comment.setFeedId(request.getFeedId());
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setLikes(0);

        spFeedCommentDao.insert(comment);
        spFeedDao.incrementCommentCount(request.getFeedId());

        return toCommentVO(comment, userId, false);
    }

    @Transactional
    public boolean toggleCommentLike(Long userId, Long commentId) {
        SpFeedLike existing = spFeedLikeDao.queryByUserAndComment(userId, commentId);
        if (existing != null) {
            spFeedLikeDao.deleteCommentLike(userId, commentId);
            spFeedCommentDao.decrementLikes(commentId);
            return false;
        } else {
            SpFeedLike like = new SpFeedLike();
            like.setCommentId(commentId);
            like.setUserId(userId);
            spFeedLikeDao.insert(like);
            spFeedCommentDao.incrementLikes(commentId);
            return true;
        }
    }

    @Transactional
    public boolean deleteFeed(Long userId, Long feedId) {
        return spFeedDao.deleteById(feedId, userId) > 0;
    }

    private FeedVO toFeedVO(SpFeed feed, Long currentUserId, boolean liked) {
        FeedVO vo = new FeedVO();
        vo.setId(feed.getId());
        vo.setUserId(feed.getUserId());
        vo.setExerciseType(feed.getExerciseType());
        vo.setContent(feed.getContent());

        if (feed.getImages() != null && !feed.getImages().isEmpty()) {
            try {
                vo.setImages(JSON.parseArray(feed.getImages(), String.class));
            } catch (Exception e) {
                vo.setImages(Collections.emptyList());
            }
        } else {
            vo.setImages(Collections.emptyList());
        }

        vo.setLocationName(feed.getLocationName());
        vo.setLikes(feed.getLikes() != null ? feed.getLikes() : 0);
        vo.setCommentCount(feed.getCommentCount() != null ? feed.getCommentCount() : 0);
        vo.setViewCount(feed.getViewCount() != null ? feed.getViewCount() : 0);
        vo.setLiked(liked);
        vo.setCreatedAt(feed.getCreatedAt() != null ? feed.getCreatedAt().toString() : null);
        vo.setTimeAgo(TimeAgoUtil.format(feed.getCreatedAt()));

        SpUser author = spUserDao.queryById(feed.getUserId());
        if (author != null) {
            vo.setNickname(author.getNickname());
            vo.setAvatar(author.getAvatar());
        }

        return vo;
    }

    private CommentVO toCommentVO(SpFeedComment comment, Long currentUserId, boolean liked) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setUserId(comment.getUserId());
        vo.setContent(comment.getContent());
        vo.setLikes(comment.getLikes() != null ? comment.getLikes() : 0);
        vo.setLiked(liked);
        vo.setCreatedAt(comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : null);
        vo.setTimeAgo(TimeAgoUtil.format(comment.getCreatedAt()));

        SpUser author = spUserDao.queryById(comment.getUserId());
        if (author != null) {
            vo.setNickname(author.getNickname());
            vo.setAvatar(author.getAvatar());
        }

        return vo;
    }

    public static class PageInfo<T> {
        private List<T> list;
        private long total;
        private int page;
        private int size;

        public List<T> getList() {
            return list;
        }

        public void setList(List<T> list) {
            this.list = list;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }
}
