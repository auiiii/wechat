package com.zj.wechat.service.sportal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zj.wechat.dto.LoginResponse;
import com.zj.wechat.dto.UserInfo;
import com.zj.wechat.entity.sportal.SpUser;
import com.zj.wechat.entity.sportal.SpUserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.regex.Pattern;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Value("${cfg.sportpal.appId:wxc3a0e0b55a494e89}")
    private String appId;

    @Value("${cfg.sportpal.appSecret:}")
    private String appSecret;

    @Resource
    private SpUserDao spUserDao;

    @Resource
    private JwtService jwtService;

    @Resource
    private SmsCodeService smsCodeService;

    /**
     * BCryptPasswordEncoder 是无状态的，线程安全，可作为单例持有。
     */
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    /** 中国大陆手机号格式：1开头、第二位3-9、共11位数字。 */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public LoginResponse loginByPhone(String phone, String smsCode) {
        if (!smsCodeService.verifyCode(phone, smsCode)) {
            throw new IllegalArgumentException("验证码错误或已过期");
        }

        SpUser user = spUserDao.queryByPhone(phone);
        boolean isNewUser = false;
        if (user == null) {
            user = createPhoneUser(phone);
            spUserDao.insert(user);
            isNewUser = true;
        }

        return buildLoginResponse(user, isNewUser);
    }

    @Transactional
    public LoginResponse loginByWx(String code) {
        String openId = getWxOpenId(code);
        if (openId == null) {
            throw new IllegalArgumentException("微信登录失败");
        }

        SpUser user = spUserDao.queryByOpenId(openId);
        boolean isNewUser = false;
        if (user == null) {
            user = createWxUser(openId);
            spUserDao.insert(user);
            isNewUser = true;
        }

        return buildLoginResponse(user, isNewUser);
    }

    /**
     * 账号密码登录：校验用户名与密码，不自动注册。
     * 用户不存在与密码错误返回相同提示，避免用户名被枚举。
     */
    public LoginResponse loginByUsername(String username, String password) {
        SpUser user = spUserDao.queryByUsername(username);
        // 用户不存在、未设置密码、密码不匹配，统一提示，防止用户名枚举
        if (user == null
                || user.getPasswordHash() == null
                || !PASSWORD_ENCODER.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        return buildLoginResponse(user, false);
    }

    /**
     * 账号密码注册：校验参数、保证用户名与手机号唯一、加密密码并落库，注册成功即返回登录态。
     * 先查后插，并以数据库唯一索引兜底并发注册场景（username/phone 均唯一）。
     */
    @Transactional
    public LoginResponse register(String username, String password, String phone) {
        if (username == null || username.trim().length() < 4 || username.trim().length() > 32) {
            throw new IllegalArgumentException("用户名长度需为4-32位");
        }
        if (password == null || password.length() < 6 || password.length() > 32) {
            throw new IllegalArgumentException("密码长度需为6-32位");
        }
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("手机号格式不正确");
        }
        String normalizedUsername = username.trim();

        if (spUserDao.queryByUsername(normalizedUsername) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (spUserDao.queryByPhone(phone) != null) {
            throw new IllegalArgumentException("手机号已注册");
        }

        SpUser user = createUsernameUser(normalizedUsername, password, phone);
        try {
            spUserDao.insert(user);
        } catch (DuplicateKeyException e) {
            // 并发注册下「先查后插」的竞态兜底：重新定位冲突字段给出准确提示
            if (spUserDao.queryByUsername(normalizedUsername) != null) {
                throw new IllegalArgumentException("用户名已存在");
            }
            throw new IllegalArgumentException("手机号已注册");
        }
        return buildLoginResponse(user, true);
    }

    private LoginResponse buildLoginResponse(SpUser user, boolean isNewUser) {
        String token = jwtService.generateToken(user.getId(), user.getOpenId());

        UserInfo userInfo = new UserInfo(
                user.getId(),
                user.getNickname(),
                user.getAvatar(),
                maskPhone(user.getPhone())
        );

        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setUserInfo(userInfo);
        resp.setNewUser(isNewUser);
        return resp;
    }

    /**
     * 手机号脱敏：保留前3后4，中间用 **** 替代，如 138****0000。
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    private SpUser createPhoneUser(String phone) {
        SpUser user = new SpUser();
        user.setPhone(phone);
        user.setNickname("动友用户");
        user.setAvatar("");
        return user;
    }

    private SpUser createWxUser(String openId) {
        SpUser user = new SpUser();
        user.setOpenId(openId);
        user.setNickname("动友用户");
        user.setAvatar("");
        return user;
    }

    private SpUser createUsernameUser(String username, String rawPassword, String phone) {
        SpUser user = new SpUser();
        user.setUsername(username);
        user.setPhone(phone);
        user.setPasswordHash(PASSWORD_ENCODER.encode(rawPassword));
        user.setNickname("动友用户");
        user.setAvatar("");
        return user;
    }

    private String getWxOpenId(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId
                + "&secret=" + appSecret
                + "&js_code=" + code
                + "&grant_type=authorization_code";
        try {
            String result = restTemplate.getForObject(url, String.class);
            logger.info("微信jscode2session响应: {}", result);
            JSONObject json = JSON.parseObject(result);
            return json.getString("openid");
        } catch (Exception e) {
            logger.error("微信登录异常", e);
            return null;
        }
    }
}
