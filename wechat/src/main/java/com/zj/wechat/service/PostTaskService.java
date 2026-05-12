package com.zj.wechat.service;

import com.zj.wechat.entity.PostTask;
import com.zj.wechat.entity.PostTaskDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 推文任务服务
 */
@Service
public class PostTaskService {

    private static final Logger logger = LoggerFactory.getLogger(PostTaskService.class);

    @Resource
    private PostTaskDao postTaskDao;

    @Resource
    private ContentGenerateService contentGenerateService;

    @Value("${cfg.image.savePath:./images/post}")
    private String imageSavePath;

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

    /**
     * 根据微信消息ID查询任务（幂等判断）
     *
     * @param msgId 微信消息ID
     * @return 已存在的任务，不存在返回null
     */
    public PostTask queryByMsgId(String msgId) {
        return postTaskDao.queryByMsgId(msgId);
    }

    /**
     * 更新推文任务状态
     *
     * @param id 主键ID
     * @param status 任务状态
     */
    public void updateStatus(Long id, int status) {
        postTaskDao.updateStatus(id, status);
        logger.info("推文任务状态更新, id={}, status={}", id, status);
    }

    /**
     * 根据ID查询推文任务
     *
     * @param id 主键ID
     * @return 推文任务
     */
    public PostTask queryById(Long id) {
        return postTaskDao.queryById(id);
    }

    /**
     * 更新推文任务的图片URL
     *
     * @param id 主键ID
     * @param imageUrl 图片相对路径
     */
    public void updateImageUrl(Long id, String imageUrl) {
        postTaskDao.updateImageUrl(id, imageUrl);
        logger.info("推文图片URL更新成功, id={}, imageUrl={}", id, imageUrl);
    }

    /**
     * 更新推文任务的文本内容（标题、正文、标签）
     *
     * @param task 含id、title、content、tags的任务对象
     */
    public void updateTaskContent(PostTask task) {
        postTaskDao.updateTaskContent(task);
        logger.info("推文内容更新成功, id={}", task.getId());
    }

    /**
     * 异步执行推文生成（文本 + 图片），更新任务状态
     * 注意：此方法由MsgHandler通过Spring代理调用，@Async生效
     */
    @Async
    public void doGenerateAsync(Long taskId, String theme, String imageDesc) {
        try {
            // 1. 生成推文文本
            String textResult = contentGenerateService.generateText(theme);
            String[] parts = textResult.split("\n");
            String title = "推文";
            String tags = parts[parts.length - 1];

            // 2. 生成配图
            byte[] imageBytes = contentGenerateService.generateImage(imageDesc);

            // 3. 更新任务内容
            PostTask updateTask = new PostTask();
            updateTask.setId(taskId);
            updateTask.setTitle(title);
            updateTask.setContent(textResult);
            updateTask.setTags(tags);
            updateTaskContent(updateTask);

            // 4. 保存图片并更新URL
            if (imageBytes != null && imageBytes.length > 0) {
                String imageUrl = saveImageToLocal(taskId, imageBytes);
                updateImageUrl(taskId, imageUrl);
            }

            // 5. 标记为已完成
            updateStatus(taskId, PostTask.STATUS_COMPLETED);
            logger.info("推文生成完成, taskId={}", taskId);
        } catch (Exception e) {
            logger.error("推文生成失败, taskId={}, theme={}", taskId, theme, e);
            updateStatus(taskId, PostTask.STATUS_FAILED);
        }
    }

    /**
     * 将图片字节数组保存到本地文件系统
     */
    private String saveImageToLocal(Long taskId, byte[] imageBytes) throws IOException {
        Path dirPath = Paths.get(imageSavePath);
        File dir = dirPath.toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = taskId + ".png";
        Path filePath = dirPath.resolve(fileName);
        Files.write(filePath, imageBytes);
        logger.info("图片保存成功, path={}", filePath.toAbsolutePath());

        return "/images/post/" + fileName;
    }
}
