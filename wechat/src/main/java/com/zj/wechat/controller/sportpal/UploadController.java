package com.zj.wechat.controller.sportpal;

import com.zj.wechat.dto.ApiResponse;
import com.zj.wechat.service.sportpal.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    @Resource
    private UploadService uploadService;

    @PostMapping("/image")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = uploadService.upload(file);
            return ApiResponse.ok(url);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        } catch (Exception e) {
            logger.error("图片上传失败", e);
            return ApiResponse.fail("上传失败");
        }
    }
}
