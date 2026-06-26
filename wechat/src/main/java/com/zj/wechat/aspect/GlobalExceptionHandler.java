package com.zj.wechat.aspect;

import com.zj.wechat.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 全局异常处理器。
 * <p>
 * 兜底 {@link WebAccessLogAspect} 无法覆盖的早期异常：
 * 例如 {@link MaxUploadSizeExceededException} 发生在 DispatcherServlet 的 multipart 解析阶段，
 * 早于 Controller 方法代理调用，AOP 切面无法拦截。这里统一捕获并记录日志、返回友好响应。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ApiResponse<?> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        logger.warn("文件上传超过大小限制: {}", e.getMessage());
        return ApiResponse.fail("文件过大，请压缩后上传");
    }
}
