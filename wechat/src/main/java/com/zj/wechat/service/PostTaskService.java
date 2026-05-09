package com.zj.wechat.service;

import com.zj.wechat.entity.PostTask;
import com.zj.wechat.entity.PostTaskDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 推文任务服务
 */
@Service
public class PostTaskService {

    private static final Logger logger = LoggerFactory.getLogger(PostTaskService.class);

    @Resource
    private PostTaskDao postTaskDao;

    /**
     * 创建推文任务并入库
     *
     * @param task 推文内容
     * @return 主键ID
     */
    public Long createTask(PostTask task) {
        postTaskDao.insert(task);
        logger.info("推文任务入库成功, id={}, title={}", task.getId(), task.getTitle());
        return task.getId();
    }
}
