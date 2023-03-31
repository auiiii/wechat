package com.zj.wechat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * StringRedisSerializer进行序列化的值，在Java和Redis中保存的内容是一样的
     * 用Jackson2JsonRedisSerializer进行序列化的值，在Redis中保存的内容，比Java中多了一对双引号。
     * 用JdkSerializationRedisSerializer进行序列化的值，对于Key-Value的Value来说，是在Redis中是不可读的。对于Hash的Value来说，比Java的内容多了一些字符。
     * 如果Key的Serializer也用和Value相同的Serializer的话，在Redis中保存的内容和上面Value的差异是一样的，所以我们保存时，只用StringRedisSerializer进行序列化
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory factory) {
        // 新建redisTemplate对象
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 设置工厂
        template.setConnectionFactory(factory);
        //序列化配置
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(stringRedisSerializer);
        // 返回redisTemplate对象 初始化，并且填充参数默认值
        template.afterPropertiesSet();
        return template;
    }
}
