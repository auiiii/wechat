package com.zj.wechat.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * 多数据源时屏蔽该配置类，不用指定给springboot了
 */
//@Configuration
public class DataSourceConfig {

    //对象映射配置文件
    //@ConfigurationProperties("spring.datasource")
    //@Bean
    public DataSource getDataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        //设置filters属性值为stat,开启SQL监控
        //druidDataSource.setFilters("stat,wall");
        return druidDataSource;
    }
}
