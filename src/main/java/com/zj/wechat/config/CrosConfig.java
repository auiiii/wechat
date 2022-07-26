package com.zj.wechat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CrosConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                //是否发送Cookie
                //.allowCredentials(true)
                //放行哪些原始域
                .allowedOrigins("*")
                .allowedMethods("GET", "POST")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }
}
