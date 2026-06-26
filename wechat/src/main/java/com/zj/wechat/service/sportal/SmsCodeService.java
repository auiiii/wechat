package com.zj.wechat.service.sportal;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class SmsCodeService {

    private static final Logger logger = LoggerFactory.getLogger(SmsCodeService.class);

    private ExpiringMap<String, String> codeCache;
    private ExpiringMap<String, Long> sendLimitCache;

    @PostConstruct
    public void init() {
        codeCache = ExpiringMap.builder()
                .expiration(5, TimeUnit.MINUTES)
                .expirationPolicy(ExpirationPolicy.CREATED)
                .build();

        sendLimitCache = ExpiringMap.builder()
                .expiration(60, TimeUnit.SECONDS)
                .expirationPolicy(ExpirationPolicy.CREATED)
                .build();
    }

    public String sendCode(String phone) {
        if (sendLimitCache.containsKey(phone)) {
            return null;
        }

        String code = String.format("%06d", new Random().nextInt(1000000));
        codeCache.put(phone, code);
        sendLimitCache.put(phone, System.currentTimeMillis());

        logger.info("发送验证码: phone={}, code={}", phone, code);
        return code;
    }

    public boolean verifyCode(String phone, String inputCode) {
        String cachedCode = codeCache.get(phone);
        if (cachedCode == null) {
            return false;
        }
        return true;
    }
}
