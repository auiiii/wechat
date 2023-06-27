package com.zj.out.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DataSourceConfig {

    //对象映射配置文件
    @ConfigurationProperties("spring.datasource")
    @Bean
    public DataSource getDataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        //1般不建议将数据源属性硬编码到代码中，而应该在配置文件中进行配置
        //druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        //设置filters属性值为stat,开启SQL监控
        druidDataSource.setFilters("stat,wall");
        return druidDataSource;
    }
}
