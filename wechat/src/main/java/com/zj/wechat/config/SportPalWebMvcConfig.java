package com.zj.wechat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class SportPalWebMvcConfig implements WebMvcConfigurer {

    @Resource
    private JwtAuthInterceptor jwtAuthInterceptor;

    @Value("${cfg.sportpal.uploadPath}")
    private String uploadPath;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/api/**")
                // 公开接口的放行逻辑收敛到拦截器内部判断（GET /api/feed/list、GET /api/feed/{数字id}），
                // 这里不再用 excludePathPatterns("/api/feed/{id}") —— 路径变量 {id} 会匹配 /api/feed/create、DELETE /api/feed/{id} 等需要鉴权的请求，造成安全漏洞。
                .excludePathPatterns("/api/auth/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 统一使用绝对路径，避免不同操作系统相对路径解析差异
        String location = new java.io.File(uploadPath).getAbsolutePath()
                .replace('\\', '/');
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        registry.addResourceHandler("/uploads/sportpal/**")
                .addResourceLocations("file:" + location);
    }
}
