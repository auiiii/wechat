package com.zj.wechat.config;

import com.alibaba.fastjson.JSON;
import com.zj.wechat.dto.ApiResponse;
import com.zj.wechat.service.sportal.JwtService;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthInterceptor.class);

    @Resource
    private JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        if (isAnonymousAllowed(request)) {
            // 公开读接口：未带 token 也放行，但若带了 token 则解析出 userId 写入 attribute，
            // 供业务层渲染「当前用户是否已点赞」等个性化字段使用
            tryParseUserId(request);
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeUnauthorized(response, "未登录或token已过期");
            return false;
        }

        String token = authHeader.substring(7);
        try {
            Long userId = parseUserId(token);
            request.setAttribute("userId", userId);
            return true;
        } catch (Exception e) {
            logger.warn("JWT解析失败: {}", e.getMessage());
            writeUnauthorized(response, "token无效或已过期");
            return false;
        }
    }

    /**
     * 判断是否为允许匿名访问的公开读接口。
     * <p>
     * 注意：原先用 excludePathPatterns("/api/feed/{id}") 的写法有缺陷 ——
     * {id} 是路径变量占位符，会匹配任意单段路径，导致 POST /api/feed/create
     * 和 DELETE /api/feed/{id} 也被错误放行。这里改为精确按「方法+URI」判断，
     * 只对 GET 形式的动态列表/详情开放匿名访问。
     */
    private boolean isAnonymousAllowed(HttpServletRequest request) {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String uri = request.getRequestURI();
        return "/api/feed/list".equals(uri) || isFeedDetailUri(uri);
    }

    /**
     * 判断是否为 GET /api/feed/{数字id} 形式的详情接口。
     * 用正则约束 id 必须是数字，避免误匹配 /api/feed/create 之类的子路径。
     */
    private boolean isFeedDetailUri(String uri) {
        if (uri == null) {
            return false;
        }
        int prefix = "/api/feed/".length();
        if (uri.length() <= prefix) {
            return false;
        }
        String tail = uri.substring(prefix);
        // /api/feed/{id} 之后不应再有路径段，且 id 必须为纯数字
        if (tail.indexOf('/') >= 0) {
            return false;
        }
        for (int i = 0; i < tail.length(); i++) {
            if (!Character.isDigit(tail.charAt(i))) {
                return false;
            }
        }
        return !tail.isEmpty();
    }

    /**
     * 尝试从 Authorization 头解析 userId；失败时静默忽略（公开接口场景）。
     */
    private void tryParseUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        try {
            Long userId = parseUserId(authHeader.substring(7));
            request.setAttribute("userId", userId);
        } catch (Exception ignore) {
            // 公开接口不强制校验 token，解析失败忽略
        }
    }

    private Long parseUserId(String token) {
        Claims claims = jwtService.parseToken(token);
        Object userIdRaw = claims.get("userId");
        if (userIdRaw == null) {
            return null;
        }
        if (userIdRaw instanceof Number) {
            return ((Number) userIdRaw).longValue();
        }
        // 兜底：理论上 jjwt 解析数字 claim 会返回 Integer/Long，这里防御性兼容字符串
        return Long.valueOf(userIdRaw.toString());
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        ApiResponse<?> body = ApiResponse.fail(401, message);
        response.getWriter().write(JSON.toJSONString(body));
    }
}
