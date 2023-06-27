package com.zj.out;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan(basePackages = "com.zj.out.entity")
@EnableAsync
@EnableDiscoveryClient
public class OutApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(OutApplication.class, args);
    }

}
