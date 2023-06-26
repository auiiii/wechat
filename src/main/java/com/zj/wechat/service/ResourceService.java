package com.zj.wechat.service;

import com.zj.wechat.entity.*;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

/**
 * 资源类管理service
 */
@Service
public class ResourceService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);

    @Resource
    WeChatMusicDao musicDao;

    @Resource
    WeChatMovieDao movieDao;

    @Resource
    WeChatMediaInfoDao mediaInfoDao;

    @Resource
    WeChatOpinionDao opinionDao;

    @Resource
    JavaMailSender sender;

    @Value("${spring.mail.username}")
    private String address;

    public void insertMusic(WeChatMusic body) throws Exception{
        musicDao.insert(body);
    }

    public void updateMusic(WeChatMusic body, Long id) throws Exception{
        body.setId(id);
        musicDao.update(body);
    }

    public void batchDeleteMusic(List<Long> ids) throws Exception{
        musicDao.deleteBatchByIds(ids);
    }

    public List<WeChatMusic> queryMusicList(Long limit, String keyWord) throws Exception{
        return musicDao.queryAll(limit, keyWord);
    }

    @GlobalTransactional
    public void insertMovie(WeChatMovie body) throws Exception{
        movieDao.add2Master(body);
        int i = 10/0;
        movieDao.insert(body);
    }

    public void insertPic(WeChatMediaInfo body) throws Exception{
        mediaInfoDao.insert(body);
    }

    public void updateMovie(WeChatMovie body, Long id) throws Exception{
        body.setId(id);
        movieDao.update(body);
    }

    public void updatePic(WeChatMediaInfo body, Long id) throws Exception{
        body.setId(id);
        mediaInfoDao.update(body);
    }

    public void batchDeleteMovie(List<Long> ids) throws Exception{
        movieDao.deleteBatchByIds(ids);
    }

    public void batchDeletePic(List<Long> ids) throws Exception{
        mediaInfoDao.deleteBatchByIds(ids);
    }

    public List<WeChatMovie> queryMovieList(Long limit, String keyWord) throws Exception{
        return movieDao.queryAll(limit, keyWord);
    }

    public List<WeChatMediaInfo> queryPicList(Long limit, String keyWord) throws Exception{
        return mediaInfoDao.queryAll(limit, keyWord);
    }

    public void insertOpinion(WeChatOpinion body) {
        opinionDao.insert(body);
    }

    public List<String> queryOpinionList() {
        return opinionDao.queryList();
    }

    public void submitEmail(Map<String, Object> map) {
        String code = map.get("code").toString();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("有人来打赏了亲");
        message.setText("口令为:"+code);
        message.setFrom(address);
        message.setTo(address);
        sender.send(message);
    }

    /**
     * 通用信息文本
     * @param text
     * @param title
     */
    public void submitEmail(String text, String title) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(text);
        message.setFrom(address);
        message.setTo(address);
        sender.send(message);
    }

    /**
     *
     * @param map
     */
    public void submitArticle(Map<String, Object> map) throws Exception{
        String title = map.get("title").toString();
        String type = map.get("type").toString();
        String text = map.get("text").toString();
        String name = title + "-" + type + ".txt";
        try(FileWriter fileWriter = new FileWriter("/article/" + name);
            BufferedWriter bw = new BufferedWriter(fileWriter);){
            bw.write(text);
        }
        catch(Exception ex)
        {
            logger.error("", ex);
        }
    }


}
