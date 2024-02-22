package com.cbiot.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${druidDataSource.maxActive:50}")
    private Integer maxActive;

    @Value("${druidDataSource.initialSize:10}")
    private Integer initialSize;

    @Value("${druidDataSource.minIdle:1}")
    private Integer minIdle;//有点像核心线程池，预防突发流量

    @Value("${druidDataSource.maxPoolPreparedStatementPerConnectionSize:20}")
    private Integer maxPoolPreparedStatementPerConnectionSize;

    @Value("${druidDataSource.maxWait:60000}")
    private Long maxWait;

    @Value("${druidDataSource.minEvictableIdleTimeMillis:40000}")
    private Long minEvictableIdleTimeMillis;

    @Value("${druidDataSource.poolPreparedStatements:true}")
    private boolean poolPreparedStatements;
    //对象映射配置文件
    @ConfigurationProperties("spring.datasource")
    @Bean
    public DataSource getDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setMaxActive(maxActive);
        druidDataSource.setInitialSize(initialSize);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        druidDataSource.setMinIdle(minIdle);
        druidDataSource.setMaxWait(maxWait);
        druidDataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        druidDataSource.setPoolPreparedStatements(poolPreparedStatements);
        return druidDataSource;
    }
}

