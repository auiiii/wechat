package com.zj.wechat.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.ValueFilter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 统一接口出入日志切面。
 * <p>
 * 覆盖所有标注 {@link org.springframework.web.bind.annotation.RestController} 的 Bean，
 * 自动记录 {@code [IN-req]} / {@code [IN-rsp]} 日志，并对敏感字段脱敏。
 * 替换各 Controller 中散落的手写日志。
 * <p>
 * 设计要点：
 * <ul>
 *   <li>不可序列化参数（{@link MultipartFile}、请求/响应对象、流等）记成 {@code [ClassName]}。</li>
 *   <li>敏感字段（password / token / smsCode / appSecret / apiKey / secret / jwtSecret / passwordHash）全脱敏。</li>
 *   <li>手机号字段（phone）中间四位脱敏：{@code 138****0000}。</li>
 *   <li>任何 body 超 {@value #MAX_BODY_LENGTH} 字符截断。</li>
 *   <li>异常路径使用 WARN，并 rethrow 原异常，保证全局异常处理链路不受影响。</li>
 * </ul>
 */
@Aspect
@Component
public class WebAccessLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(WebAccessLogAspect.class);

    /** body 序列化最大长度，超出截断防止日志刷屏。 */
    private static final int MAX_BODY_LENGTH = 2000;

    /** 截断后缀。 */
    private static final String TRUNCATED_SUFFIX = "...[truncated]";

    /** 全脱敏字段名（小写比较）。 */
    private static final Set<String> FULL_MASK_FIELDS = new HashSet<>(Arrays.asList(
            "password", "passwordhash", "smscode", "token",
            "appsecret", "apikey", "secret", "jwtsecret"
    ));

    /** fastjson ValueFilter，递归对敏感字段脱敏。 */
    private static final ValueFilter SENSITIVE_FILTER = (object, name, value) -> {
        if (value == null) {
            return null;
        }
        String key = name == null ? "" : name.toLowerCase();
        if (FULL_MASK_FIELDS.contains(key)) {
            return "****";
        }
        if ("phone".equals(key) && value instanceof CharSequence) {
            return maskPhone(value.toString());
        }
        return value;
    };

    /** 跳过序列化的参数类型集合（按 Class 匹配）。 */
    private static final Set<Class<?>> SKIP_PARAM_TYPES = new HashSet<>(Arrays.asList(
            HttpServletRequest.class,
            HttpServletResponse.class,
            HttpSession.class,
            InputStream.class,
            OutputStream.class,
            Principal.class,
            MultipartFile.class,
            MultipartFile[].class
    ));

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void controllerPointcut() {
        // 仅作为切点标识
    }

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = currentRequest();
        String method = request != null ? request.getMethod() : "UNKNOWN";
        String uri = request != null ? request.getRequestURI() : "UNKNOWN";
        String requestTag = method + " " + uri;

        String reqBody = serializeArgs(joinPoint.getArgs());
        if (reqBody == null) {
            logger.info("[IN-req]{}", requestTag);
        } else {
            logger.info("[IN-req]{}, req-body is:{}", requestTag, reqBody);
        }

        long start = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            long cost = (System.nanoTime() - start) / 1_000_000L;
            String rspBody = serializeResult(result);
            if (rspBody == null) {
                logger.info("[IN-rsp]{} done, cost={}ms", requestTag, cost);
            } else {
                logger.info("[IN-rsp]{} done, cost={}ms, rsp is:{}", requestTag, cost, rspBody);
            }
            return result;
        } catch (IllegalArgumentException bizEx) {
            long cost = (System.nanoTime() - start) / 1_000_000L;
            logger.warn("[IN-rsp]{} biz-error, cost={}ms, msg={}", requestTag, cost, bizEx.getMessage());
            throw bizEx;
        } catch (Throwable ex) {
            long cost = (System.nanoTime() - start) / 1_000_000L;
            logger.warn("[IN-rsp]{} exception, cost={}ms", requestTag, cost, ex);
            throw ex;
        }
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs == null ? null : attrs.getRequest();
    }

    /**
     * 序列化方法参数。全部为不可记录参数时返回 {@code null}（用于省略 body）。
     * 单个不可记录参数记成 {@code [ClassName]}，其余走 fastjson 脱敏序列化。
     */
    private String serializeArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean anySerializable = false;
        for (Object arg : args) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            if (arg == null) {
                sb.append("null");
                anySerializable = true;
                continue;
            }
            Class<?> clazz = arg.getClass();
            if (isSkipType(clazz)) {
                sb.append("[").append(clazz.getSimpleName()).append("]");
                continue;
            }
            // 多参时整体不作为单个 JSON 解析，但仍把每个参数的 JSON 拼起来以便排查
            sb.append(truncate(JSON.toJSONString(arg, SENSITIVE_FILTER)));
            anySerializable = true;
        }
        return anySerializable ? sb.toString() : null;
    }

    /**
     * 序列化返回值。{@code null} 返回值、空 ResponseEntity body 都返回 {@code null} 表示省略日志 body。
     */
    private String serializeResult(Object result) {
        if (result == null) {
            return null;
        }
        if (result instanceof byte[]) {
            return "[binary:" + ((byte[]) result).length + " bytes]";
        }
        if (result instanceof ResponseEntity) {
            Object body = ((ResponseEntity<?>) result).getBody();
            if (body == null) {
                return null;
            }
            if (body instanceof byte[]) {
                return "[ResponseEntity binary:" + ((byte[]) body).length + " bytes]";
            }
            return truncate(JSON.toJSONString(body, SENSITIVE_FILTER));
        }
        return truncate(JSON.toJSONString(result, SENSITIVE_FILTER));
    }

    private boolean isSkipType(Class<?> clazz) {
        for (Class<?> skip : SKIP_PARAM_TYPES) {
            if (skip.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    private String truncate(String body) {
        if (body == null) {
            return null;
        }
        if (body.length() <= MAX_BODY_LENGTH) {
            return body;
        }
        return body.substring(0, MAX_BODY_LENGTH) + TRUNCATED_SUFFIX;
    }

    /**
     * 手机号中间四位脱敏：{@code 138****0000}。
     * 长度小于 7 时原样返回（与 {@code AuthService.maskPhone} 同款逻辑，避免跨层依赖）。
     */
    private static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
