package com.zj.wechat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan(basePackages = "com.zj.wechat.entity")
@EnableAsync
@EnableDiscoveryClient
@EnableFeignClients
public class WeChatApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(WeChatApplication.class, args);
    }

}
