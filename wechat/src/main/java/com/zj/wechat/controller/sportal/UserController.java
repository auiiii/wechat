package com.zj.wechat.controller.sportal;

import com.zj.wechat.dto.ApiResponse;
import com.zj.wechat.dto.UserProfileUpdateRequest;
import com.zj.wechat.dto.UserProfileVO;
import com.zj.wechat.service.sportal.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserService userService;

    @GetMapping("/profile")
    public ApiResponse<UserProfileVO> getProfile(HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            UserProfileVO profile = userService.getProfile(userId);
            return ApiResponse.ok(profile);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ApiResponse<UserProfileVO> updateProfile(@RequestBody UserProfileUpdateRequest request,
                                                     HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            UserProfileVO profile = userService.updateProfile(userId,
                    request.getNickname(), request.getAvatar());
            return ApiResponse.ok(profile);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }
}
