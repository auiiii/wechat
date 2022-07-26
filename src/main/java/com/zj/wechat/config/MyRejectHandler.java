package com.zj.wechat.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 默认的是Abort策略，提示满抛异常
 * 另外的为：DisCardPolicy不执行也不抛异常，DisCardOldSetPolicy取代老的执行，CallerRunsPolicy立即执行
 */
public class MyRejectHandler implements RejectedExecutionHandler {

    private static final Logger logger = LoggerFactory.getLogger(MyRejectHandler.class);

    private BlockingQueue queue;

    public MyRejectHandler(BlockingQueue queue) {
        this.queue = queue;
    }

    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        try {
            //阻塞队列即可
            queue.put(runnable);
        } catch (InterruptedException e) {
            logger.error("",e);
        }
    }
}
