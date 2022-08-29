package com.zj.wechat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 通过bean管理工厂，不用手工实现单例了
 */
@Configuration
public class ThreadFactory {

    private final int corePoolSize = 10;
    private final int maximumPoolSize = 20;
    private final long keepAliveTime = 5;
    private ThreadPoolExecutor excutor = null;


    @Bean(name ="MyThreadPoolExecutor")
    public ThreadPoolExecutor getThreadPool()
    {
        BlockingQueue queue = new LinkedBlockingQueue(100);
        if(null == excutor)
        {
            excutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,TimeUnit.SECONDS,queue, new MyRejectHandler(queue));
        }
        return excutor;
    }

}
