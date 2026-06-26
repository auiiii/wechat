package com.zj.wechat.controller.sportal;

import com.zj.wechat.dto.*;
import com.zj.wechat.service.sportal.FeedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/feed")
public class FeedController {

    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

    @Resource
    private FeedService feedService;

    @PostMapping("/create")
    public ApiResponse<FeedVO> createFeed(@RequestBody FeedCreateRequest request,
                                          HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            FeedVO vo = feedService.createFeed(userId, request);
            return ApiResponse.ok(vo);
        } catch (Exception e) {
            logger.error("创建动态失败", e);
            return ApiResponse.fail(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ApiResponse<FeedService.PageInfo<FeedVO>> listFeeds(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        FeedService.PageInfo<FeedVO> pageInfo = feedService.listFeeds(page, size, userId);
        return ApiResponse.ok(pageInfo);
    }

    @GetMapping("/{id}")
    public ApiResponse<FeedDetailVO> getFeedDetail(
            @PathVariable("id") Long feedId,
            HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            FeedDetailVO detail = feedService.getFeedDetail(feedId, userId);
            return ApiResponse.ok(detail);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/like")
    public ApiResponse<Boolean> toggleLike(@RequestBody FeedLikeRequest request,
                                           HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            boolean liked = feedService.toggleLike(userId, request.getFeedId());
            return ApiResponse.ok(liked);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/comment")
    public ApiResponse<CommentVO> addComment(@RequestBody FeedCommentRequest request,
                                             HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            CommentVO vo = feedService.addComment(userId, request);
            return ApiResponse.ok(vo);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/comment/like")
    public ApiResponse<Boolean> toggleCommentLike(@RequestBody CommentLikeRequest request,
                                                   HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            boolean liked = feedService.toggleCommentLike(userId, request.getCommentId());
            return ApiResponse.ok(liked);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteFeed(@PathVariable("id") Long feedId,
                                           HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            boolean deleted = feedService.deleteFeed(userId, feedId);
            return ApiResponse.ok(deleted);
        } catch (Exception e) {
            logger.error("删除动态失败", e);
            return ApiResponse.fail(e.getMessage());
        }
    }
}
