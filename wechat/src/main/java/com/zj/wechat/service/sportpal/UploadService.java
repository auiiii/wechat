package com.zj.wechat.service.sportpal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class UploadService {

    private static final Logger logger = LoggerFactory.getLogger(UploadService.class);

    private static final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/png", "image/webp");
    private static final long MAX_SIZE = 20 * 1024 * 1024;

    @Value("${cfg.sportpal.uploadPath}")
    private String uploadPath;

    @Resource
    private ImageContentAuditService imageContentAuditService;

    public String upload(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过50MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("仅支持JPG/PNG/WEBP格式");
        }

        //内容安全审核：fail-closed，审核失败/异常都拒绝上传
        imageContentAuditService.checkImage(file);

        String originalName = file.getOriginalFilename();
        String ext = "jpg";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;

        File dir = new File(uploadPath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("无法创建上传目录: " + dir.getAbsolutePath());
        }

        File dest = new File(dir, fileName);
        file.transferTo(dest);

        String url = "/uploads/sportpal/" + fileName;
        logger.info("文件上传成功: {}", url);
        return url;
    }
}
