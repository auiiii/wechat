package com.zj.wechat.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import java.util.Arrays;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Configuration
public class RestTemplateConfig {

    /**
     * 集成cloud-LoadBalanced后会破坏正常调用,重新定义一个解决
     * @param factory
     * @return
     */
    @Bean(value = "restTemplateWithLoad")
    @LoadBalanced
    public RestTemplate restTemplate(ClientHttpRequestFactory factory){
        RestTemplate restTemplate = new RestTemplate(simpleClientHttpRequestFactory());
        //这个地方需要配置消息转换器，不然收到消息后转换会出现异常
        restTemplate.setMessageConverters(getConverts());
        return restTemplate;
    }

    @Bean(value = "restTemplate")
    public RestTemplate restTemplate2(ClientHttpRequestFactory factory){
        RestTemplate restTemplate = new RestTemplate(simpleClientHttpRequestFactory());
        //这个地方需要配置消息转换器，不然收到消息后转换会出现异常
        restTemplate.setMessageConverters(getConverts());
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(150000);//Ai的响应比较慢
        factory.setReadTimeout(5000);
        return factory;
    }

    private List<HttpMessageConverter<?>> getConverts() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();

        // ByteArray转换器，支持所有MediaType（用于下载图片等二进制内容）
        ByteArrayHttpMessageConverter byteArrayConvert = new ByteArrayHttpMessageConverter();
        byteArrayConvert.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.IMAGE_PNG, MediaType.IMAGE_JPEG));
        messageConverters.add(byteArrayConvert);

        // String转换器
        StringHttpMessageConverter stringConvert = new StringHttpMessageConverter();
        List<MediaType> stringMediaTypes = new ArrayList<MediaType>() {{
            //添加响应数据格式，不匹配会报401
            add(MediaType.TEXT_PLAIN);
            add(MediaType.TEXT_EVENT_STREAM);
            add(MediaType.TEXT_HTML);
            add(MediaType.APPLICATION_JSON);
        }};
        stringConvert.setSupportedMediaTypes(stringMediaTypes);
        messageConverters.add(stringConvert);

        // 表单转换器，支持multipart/form-data（用于上传文件等场景）
        messageConverters.add(new AllEncompassingFormHttpMessageConverter());
        return messageConverters;
    }

}
