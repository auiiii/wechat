package com.cbiot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@MapperScan(basePackages = "com.cbiot.entity")
@EnableDiscoveryClient
public class DemoApp
{
    public static void main( String[] args )
    {
        System.out.println( "demo running" );
        SpringApplication.run(DemoApp.class, args);
    }
}
