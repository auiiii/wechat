package com.zj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 该工程用来实现nacos打破分组RPC调用
 */
@SpringBootApplication
@MapperScan(basePackages = "com.zj.entity")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.zj"})
@EnableHystrix
@EnableScheduling
public class DemoApp
{
    public static void main( String[] args )
    {
        System.out.println( "demo running" );
        SpringApplication.run(DemoApp.class, args);
    }
}
