package com.zj.wechat.service;

/**
 * AI内容生成服务抽象
 */
public interface ContentGenerateService {

    /**
     * 根据主题生成推文文本
     *
     * @param theme 用户输入的主题描述
     * @return 推文文本，格式：标题\n正文\n标签1,标签2,标签3
     */
    String generateText(String theme);

    /**
     * 根据主题生成配图
     *
     * @param theme 用户输入的主题描述
     * @return 图片字节数组
     */
    byte[] generateImage(String theme);
}
