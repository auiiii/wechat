package com.zj.wechat.controller.sportal;

import com.zj.wechat.dto.*;
import com.zj.wechat.service.sportal.AuthService;
import com.zj.wechat.service.sportal.SmsCodeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @Resource
    private SmsCodeService smsCodeService;

    @PostMapping("/sms/send")
    public ApiResponse<String> sendSmsCode(@RequestBody SmsCodeRequest request) {
        String code = smsCodeService.sendCode(request.getPhone());
        if (code == null) {
            return ApiResponse.fail("发送过于频繁，请60秒后重试");
        }
        return ApiResponse.ok("验证码已发送");
    }

    @PostMapping("/login/phone")
    public ApiResponse<LoginResponse> loginByPhone(@RequestBody PhoneLoginRequest request) {
        try {
            LoginResponse response = authService.loginByPhone(request.getPhone(), request.getSmsCode());
            return ApiResponse.ok(response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/login/wx")
    public ApiResponse<LoginResponse> loginByWx(@RequestBody WxLoginRequest request) {
        try {
            LoginResponse response = authService.loginByWx(request.getCode());
            return ApiResponse.ok(response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.loginByUsername(request.getUsername(), request.getPassword());
            return ApiResponse.ok(response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@RequestBody RegisterRequest request) {
        try {
            LoginResponse response = authService.register(
                    request.getUsername(), request.getPassword(), request.getPhone());
            return ApiResponse.ok(response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }
}
