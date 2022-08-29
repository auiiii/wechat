package com.zj.wechat.service;

import com.alibaba.fastjson.JSONObject;
import com.zj.wechat.entity.*;
import com.zj.wechat.pojo.Constants;
import com.zj.wechat.pojo.MsgEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * xml报文交互逻辑抽取
 */
@Service
public class MsgHandler {

    private static final Logger logger = LoggerFactory.getLogger(MsgHandler.class);

    @Resource
    WeChatMediaInfoDao mediaInfoDao;

    @Resource
    WeChatMusicDao musicDao;

    @Resource
    WeChatMovieDao movieDao;

    @Resource
    WeChatUserInfoDao userInfoDao;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private ResourceService service;

    /**
     * 入口逻辑
     * @param entity
     * @return
     */
    public String handleMsg(MsgEntity entity) {
        switch(entity.getMsgType()){
            case "text":
                logger.info("text-in");
                //返回文本信息时
                return handleTextMsg(entity);
            //订阅和取消订阅事件
            case "event":
                if("subscribe".equals(entity.getEvent()))
                {
                    logger.info("subscribe-in");
                    return buildTextMessage(entity,"欢迎关注你的月亮我的心,回复1获得使用指引\n估摸这个点了,你也该emo了");
                }
                else if("unsubscribe".equals(entity.getEvent()))
                {
                    logger.info("unsubscribe-in");
                    return "";
                }
                else
                    return "";
            case "location":
                logger.info("location-in");
                //采集用户信息，返回位置
                return handleLocation(entity);
            case "image":
                logger.info("image-in");
                //随机斗图
                WeChatMediaInfo info  = mediaInfoDao.queryByRandom();
                return buildImageMessage(entity, info.getName());
            case "voice":
                logger.info("voice-in");
                //暂时不处理语音,回复发送方的语音
                return buildVoiceMessage(entity);
            case "video":
                logger.info("video-in");
                //暂时不处理媒体,回复表情包恶搞
                return buildImageMessage(entity,"这啥啊");
            case "link":
                logger.info("link-in");
                //链接类没有互动的必要
                return buildTextMessage(entity,"收手吧,阿祖:");
            default:
                return "";
        }
    }

    /**
     *处理上报定位逻辑,用户量大时采集逻辑需优化
     * @param entity
     */
    private String handleLocation(MsgEntity entity) {
        InsertLocation(entity);
        //service.submitEmail("有用户告警了,请登录处理" + entity.getLabel(),"用户告警");
        return buildTextMessage(entity,"你已经被FBI包围了,你当前在:" + entity.getLabel());
    }

    @Async
    public void InsertLocation(MsgEntity entity) {
        WeChatUserInfo info = userInfoDao.queryByUserName(entity.getFromUserName());
        if(null == info)
        {
            info = new WeChatUserInfo();
            info.setUserId(entity.getFromUserName());
            info.setLocationX(entity.getLocation_X());
            info.setLocationY(entity.getLocation_Y());
            info.setIsSeSe(0);
            info.setLabel(entity.getLabel());
            userInfoDao.insert(info);
        }
        else{
            info.setLocationX(entity.getLocation_X());
            info.setLocationY(entity.getLocation_Y());
            info.setLabel(entity.getLabel());
            userInfoDao.update(info);
        }
    }

    /**
     * 文本信息交互逻辑
     * @return
     * @param entity
     */
    private String handleTextMsg(MsgEntity entity) {
        String content = entity.getContent();
        if(null != content && (content.contains("911")))
        {
            service.submitEmail("有用户告警了,请登录处理","用户告警");
            return buildTextMessage(entity,"管理员已收到告警通知");
        }
        if(null != content && (content.contains("使用") || content.contains("指南") || content.contains("1")))
        {
            return buildTextMessage(entity,"输入歌曲获得每日歌单\n输入电影获得每日推荐\n输入网页获取门户链接\n输入晚安获得问候\n发送位置开启交友\n相册发送表情包陪你斗图\n发送语音听猪叫(慎点)\n输入其他文字解锁AI");
        }
        if(null != content && (content.contains("网页")))
        {
            return buildTextMessage(entity,"http://159.138.46.191:88/#/");
        }
        if(null != content && content.contains("电影"))
        {
            return buildNewsMessage(entity);
        }
        if(null != content && content.contains("晚安"))
        {
            return buildImageMessage(entity,"再见");
        }
        //文本诱导点歌的话分享歌曲
        if(null != content && (content.contains("歌曲") || content.contains("点歌")))
        {
            return sendMusicMessage(entity);
        }
        if(null != content && content.contains("不支持的消息类型"))
        {
            return buildTextMessage(entity,"微信平台识别为不支持的消息类型");
        }
        else {
            try{
                //调用青云等免费AI
                String url = Constants.URL_NLP_FREE + entity.getContent();
                logger.info("AI-CHAT:{}", url);
                ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
                String body = responseEntity.getBody();
                logger.info(JSONObject.toJSONString(responseEntity));
                JSONObject obj = JSONObject.parseObject(body);
                String reply = obj.getString("content");
                return buildTextMessage(entity, reply);
            }
            catch (Exception e)
            {
                logger.error("", e);
                return buildTextMessage(entity,"你我终将落幕, 但浪漫永驻");
            }
        }
    }


    /**
     * 关注时返回的报文
     * @param entity
     * @return
     */
    private String buildTextMessage(MsgEntity entity, String content) {
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[text]]></MsgType>" +
                        "<Content><![CDATA[%s]]></Content>" + "</xml>",
                entity.getFromUserName(), entity.getToUserName(), getUtcTime(), content);
    }

    /**
     * 图片交互报文
     * @param entity
     * @return
     */
    private String buildImageMessage(MsgEntity entity,String name) {
        String mediaId = mediaInfoDao.queryByName(name).getMediaId();
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[image]]></MsgType>" +
                        "<Image>" +
                        "<MediaId><![CDATA[%s]]></MediaId>" +
                        "</Image>" + "</xml>",
                entity.getFromUserName(), entity.getToUserName(), getUtcTime(), mediaId);
    }

    /**
     * 音频交互
     * @param entity
     * @param name
     * @return
     */
    private String buildVoiceMessage(MsgEntity entity) {
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[voice]]></MsgType>" +
                        "<Voice>" +
                        "   <MediaId><![CDATA[%s]]></MediaId>" +
                        "</Voice>" +
                        "</xml>",
                entity.getFromUserName(), entity.getToUserName(), getUtcTime(), entity.getMediaId()
        );
    }

    private String sendMusicMessage(MsgEntity entity) {
        String title = "草东没有派对-大风吹";
        String description = "网抑云";
        String hqMusicUrl = "https://www.kugou.com/song/#hash=4E88A28AB40C682EF6D7F48E70C13C44&album_id=1887385";
        //有数据随机推送
        WeChatMusic music = musicDao.queryByRandom();
        if(null != music)
        {
            title = music.getTitle();
            description = music.getDescription();
            hqMusicUrl = music.getMusicUrl();
        }
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[music]]></MsgType>" +
                        "<Music>" +
                        "   <Title><![CDATA[%s]]></Title>" +
                        "   <Description><![CDATA[%s]]></Description>" +
                        "   <MusicUrl>< ![CDATA[%s] ]></MusicUrl>" +  //非必须项 音乐链接
                        "   <HQMusicUrl><![CDATA[%s]]></HQMusicUrl>" + //非必须项 高质量音乐链接，WIFI环境优先使用该链接播放音乐
                        "</Music>" +
                        "</xml>",
                entity.getFromUserName(), entity.getToUserName(), getUtcTime(), title, description, hqMusicUrl, hqMusicUrl
        );
    }

    /**
     * 返回图文博客
     * @param entity
     * @return
     */
    private String buildNewsMessage(MsgEntity entity) {
        String title = "来了老弟";
        String description = "请你多学技术,麦麦好看不";
        String picUrl = Constants.URL_SE_SE;
        String textUrl = Constants.URL_MY_BLOG;
        WeChatMovie movie = movieDao.queryByRandom();
        if(null != movie)
        {
            title = movie.getTitle();
            description = movie.getDescription();
            picUrl = (null == movie.getPicUrl())? picUrl:movie.getPicUrl();
            textUrl = (null == movie.getMovieUrl())? textUrl:movie.getMovieUrl();
        }
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[news]]></MsgType>" +
                        "<ArticleCount>1</ArticleCount>" + //图文消息个数，限制为8条以内
                        "<Articles>" + //多条图文消息信息，默认第一个item为大图,注意，如果图文数超过8，则将会无响应
                        "<item>" +
                        "<Title><![CDATA[%s]]></Title> " +
                        "<Description><![CDATA[%s]]></Description>" +
                        "<PicUrl><![CDATA[%s]]></PicUrl>" + //图片链接，支持JPG、PNG格式，较好的效果为大图360*200，小图200*200
                        "<Url><![CDATA[%s]]></Url>" + //点击图文消息跳转链接
                        "</item>" +
                        "</Articles>" +
                        "</xml>"
                ,
                entity.getFromUserName(), entity.getToUserName(), getUtcTime(),
                title,description,picUrl,textUrl
        );
    }

    /**
     * 获取当前时间
     * @return
     */
    private static String getUtcTime(){
        // 如果不需要格式,可直接用dt,dt就是当前系统时间
        Date dt = new Date();
        // 设置显示格式
        DateFormat df = new SimpleDateFormat("yyyyMMddhhmm");
        String nowTime = df.format(dt);
        long dd = (long) 0;
        try {
            dd = df.parse(nowTime).getTime();
        } catch (Exception e) {

        }
        return String.valueOf(dd);
    }
}
