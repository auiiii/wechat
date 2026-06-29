package com.zj.wechat.service.sportpal;

import com.alibaba.fastjson.JSONObject;
import com.zj.common.utils.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片内容安全审核服务（基于微信小程序 img_sec_check 接口）。
 *
 * 设计原则：
 * 1. fail-closed —— 审核服务异常/超时/网络错误一律拒绝上传，宁可误杀不漏放，规避小程序审核下架风险。
 * 2. 仅送审用压缩，落盘仍是原图，保留高清。
 * 3. 不引入任何新中间件，仅依赖现有 RestTemplate + ExpiringMap。
 */
@Service
public class ImageContentAuditService {

    private static final Logger logger = LoggerFactory.getLogger(ImageContentAuditService.class);

    /**
     * 微信 img_sec_check 单文件硬限制 1MB。
     * 取略小于 1MB 作为压缩目标，避免多 multipart 边界编码后体积微增导致超限。
     */
    private static final long WX_IMG_SIZE_LIMIT = 1_000_000L;

    /**
     * 图片涉嫌违规（涉黄/暴恐/政治敏感等），微信文档明确定义。
     */
    private static final int ERR_CODE_VIOLATION = 87014;

    private static final String IMG_SEC_CHECK_URL =
            "https://api.weixin.qq.com/wxa/img_sec_check?access_token=";

    @Resource
    private MiniProgramTokenService miniProgramTokenService;

    @Resource(name = "restTemplate")
    private RestTemplate restTemplate;

    /**
     * 校验图片内容安全。审核不通过或服务异常时抛出 {@link IllegalArgumentException}，
     * 由 {@link com.zj.wechat.controller.sportpal.UploadController} 统一兜底返回前端。
     *
     * @param file 用户上传的图片
     */
    public void checkImage(MultipartFile file) {
        byte[] bytesToSend;
        String filename = file.getOriginalFilename();
        try {
            bytesToSend = prepareBytes(file);
        } catch (IOException e) {
            logger.warn("内容审核图片预处理失败 name={} size={}", filename, file.getSize(), e);
            throw new IllegalArgumentException("内容审核服务异常，请稍后重试");
        }

        String token;
        try {
            token = miniProgramTokenService.getAccessToken();
        } catch (Exception e) {
            logger.warn("获取小程序 access_token 失败，按 fail-closed 拒绝上传", e);
            throw new IllegalArgumentException("内容审核服务异常，请稍后重试");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        final String mediaName = (filename == null || filename.isEmpty()) ? "image.jpg" : filename;
        ByteArrayResource mediaResource = new ByteArrayResource(bytesToSend) {
            @Override
            public String getFilename() {
                return mediaName;
            }
        };
        form.add("media", mediaResource);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(form, headers);

        String body;
        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                    IMG_SEC_CHECK_URL + token, request, String.class);
            body = responseEntity.getBody();
        } catch (Exception e) {
            //网络异常/超时/5xx 等：fail-closed
            logger.warn("调用微信 img_sec_check 异常 name={} size={}", filename, file.getSize(), e);
            throw new IllegalArgumentException("内容审核服务异常，请稍后重试");
        }

        logger.info("img_sec_check resp: {}", body);
        JSONObject obj;
        try {
            obj = JSONObject.parseObject(body);
        } catch (Exception e) {
            logger.warn("img_sec_check 响应解析失败 body={}", body, e);
            throw new IllegalArgumentException("内容审核服务异常，请稍后重试");
        }

        int errcode = obj.getIntValue("errcode");
        if (errcode == 0) {
            return;
        }
        if (errcode == ERR_CODE_VIOLATION) {
            logger.warn("图片内容审核拒绝 name={} size={} errcode={}", filename, file.getSize(), errcode);
            throw new IllegalArgumentException("图片涉嫌违规，请更换");
        }
        //其他错误码（如 40001 token失效 / 41001 缺少access_token 等）一律 fail-closed
        logger.warn("img_sec_check 返回非零错误码 name={} errcode={} errmsg={}",
                filename, errcode, obj.getString("errmsg"));
        throw new IllegalArgumentException("内容审核服务异常，请稍后重试");
    }

    /**
     * 准备送审字节：原图小于限制直接用原图；否则循环压缩到限制以内。
     * 注意：此方法只产生送审副本，不影响上传落盘的原图。
     */
    private byte[] prepareBytes(MultipartFile file) throws IOException {
        if (file.getSize() <= WX_IMG_SIZE_LIMIT) {
            return file.getBytes();
        }
        try (InputStream in = file.getInputStream()) {
            BufferedImage image = ImageIO.read(in);
            if (image == null) {
                //无法解码的图片交给上层 fail-closed
                throw new IOException("无法解码图片");
            }
            double quality = (double) WX_IMG_SIZE_LIMIT / file.getSize();
            //循环压缩，直到小于限制。quality 上限 0.99，避免死循环
            while (true) {
                BufferedImage scaled = ImageUtil.compress(image, quality);
                byte[] out = encodeJpeg(scaled);
                if (out.length <= WX_IMG_SIZE_LIMIT) {
                    return out;
                }
                if (quality <= 0.05) {
                    //已压到极限仍超限，直接返回最后结果，由微信接口做最终判定
                    return out;
                }
                quality = quality * 0.7;
            }
        }
    }

    /**
     * 将 BufferedImage 编码为 JPEG 字节。送审场景 JPEG 体积最优。
     */
    private byte[] encodeJpeg(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (!javax.imageio.ImageIO.write(image, "jpg", baos)) {
            throw new IOException("JPEG 编码失败");
        }
        return baos.toByteArray();
    }
}
