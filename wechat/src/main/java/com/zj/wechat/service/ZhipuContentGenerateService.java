package com.zj.wechat.service;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.image.CreateImageRequest;
import ai.z.openapi.service.image.ImageResponse;
import ai.z.openapi.service.image.ImageResult;
import ai.z.openapi.service.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 智谱AI内容生成服务实现
 * 文本生成和图片生成均通过zai-sdk调用
 */
@Service
public class ZhipuContentGenerateService implements ContentGenerateService {

    private static final Logger logger = LoggerFactory.getLogger(ZhipuContentGenerateService.class);

    private static final String SYSTEM_PROMPT = "你是一个小红书推文创作助手。用户会给你一个主题，你需要生成一篇小红书风格的推文。"
            + "请严格按照以下格式输出，不要输出任何其他内容：\n"
            + "第一行：推文标题（20字以内，吸引眼球）\n"
            + "第二行：推文正文（文字能给出重点，如推广对象的优点是什么，300字以内，带有emoji和换行，口语化风格）\n"
            + "第三行：标签（3-5个标签，用英文逗号分隔，不要带#号）";

    @Value("${cfg.zhipu.apiKey}")
    private String apiKey;

    @Value("${cfg.zhipu.textModel:glm-5.1}")
    private String textModel;

    @Value("${cfg.zhipu.imageModel:glm-image}")
    private String imageModel;

    @Resource(name = "restTemplate")
    private RestTemplate restTemplate;

    @Override
    public String generateText(String theme) {
        logger.info("调用智谱AI生成推文文本, theme={}", theme);
        ZhipuAiClient client = ZhipuAiClient.builder()
                .ofZHIPU()
                .apiKey(apiKey)
                .build();

        List<ChatMessage> messages = Arrays.asList(
                ChatMessage.builder()
                        .role(ChatMessageRole.SYSTEM.value())
                        .content(SYSTEM_PROMPT)
                        .build(),
                ChatMessage.builder()
                        .role(ChatMessageRole.USER.value())
                        .content(theme)
                        .build()
        );

        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                .model(textModel)
                .messages(messages)
                .stream(false)
                .temperature(0.8f)
                //.maxTokens(2048)
                .build();

        ChatCompletionResponse response = client.chat().createChatCompletion(request);
        Object contentObj = response.getData().getChoices().get(0).getMessage().getContent();
        String text = String.valueOf(contentObj);
        logger.info("AI生成推文文本完成, theme={}, text={}", theme, text);
        return text;
    }

    @Override
    public byte[] generateImage(String theme) {
        logger.info("调用智谱AI生成配图, theme={}", theme);
        try {
            ZhipuAiClient client = ZhipuAiClient.builder()
                    .ofZHIPU()
                    .apiKey(apiKey)
                    .build();

            CreateImageRequest imageRequest = CreateImageRequest.builder()
                    .model(imageModel)
                    .prompt("小红书风格封面图，内容要求是：" + theme + "，图片风格要求色彩鲜艳，有氛围感，人物形象精致，可以适当用q版呆萌的人物形象，适合社交媒体分享")
                    .size("1024x1024")
                    .build();

            ImageResponse imageResponse = client.images().createImage(imageRequest);
            ImageResult result = imageResponse.getData();
            if (result == null || result.getData() == null || result.getData().isEmpty()) {
                logger.warn("图片生成返回数据为空, theme={}", theme);
                return new byte[0];
            }

            String imageUrl = result.getData().get(0).getUrl();
            logger.info("图片生成成功, url={}", imageUrl);

            // 下载图片字节
            ResponseEntity<byte[]> downloadResponse = restTemplate.getForEntity(imageUrl, byte[].class);
            byte[] imageBytes = downloadResponse.getBody();
            logger.info("图片下载完成, size={}bytes", imageBytes != null ? imageBytes.length : 0);
            return imageBytes != null ? imageBytes : new byte[0];
        } catch (Exception e) {
            logger.error("图片生成失败, theme={}", theme, e);
            return new byte[0];
        }
    }
}
