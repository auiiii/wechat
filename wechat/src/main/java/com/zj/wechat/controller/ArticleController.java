package com.zj.wechat.controller;

import com.zj.wechat.entity.PostTask;
import com.zj.wechat.service.PostTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * H5文章页面控制器
 * 提供文章展示页面和图片资源服务
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @Resource
    private PostTaskService postTaskService;

    @Value("${cfg.image.savePath:./images/post}")
    private String imageSavePath;

    @Value("${cfg.baseUrl:http://180.76.119.15}")
    private String baseUrl;

    /**
     * 展示H5文章页面
     */
    @GetMapping(value = "/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> showArticle(@PathVariable Long id) {
        PostTask task = postTaskService.queryById(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("<h1>文章不存在</h1>");
        }

        // 任务仍在生成中，返回加载提示页（自动刷新）
        if (task.getStatus() != null && task.getStatus() == PostTask.STATUS_GENERATING) {
            String loadingHtml = buildLoadingHtml(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("text/html;charset=UTF-8"))
                    .body(loadingHtml);
        }

        // 任务失败
        if (task.getStatus() != null && task.getStatus() == PostTask.STATUS_FAILED) {
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("text/html;charset=UTF-8"))
                    .body("<h1>推文生成失败，请重新发起</h1>");
        }

        String html = buildArticleHtml(task);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("text/html;charset=UTF-8"))
                .body(html);
    }

    /**
     * 返回图片文件二进制流
     */
    @GetMapping("/image/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(imageSavePath, fileName);
            File file = filePath.toFile();
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = Files.readAllBytes(filePath);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(imageBytes.length);
            headers.setCacheControl("max-age=86400");

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("读取图片文件失败, fileName={}", fileName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 构建H5文章页面HTML
     */
    private String buildArticleHtml(PostTask task) {
        String title = escapeHtml(task.getTitle());
        String content = escapeHtml(task.getContent());
        String tags = escapeHtml(task.getTags());
        String createTime = "";
        if (task.getCreateTime() != null) {
            createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(task.getCreateTime());
        }

        // 图片HTML块：使用绝对路径，确保客户端可直接访问
        String imageBlock = "";
        if (task.getImageUrl() != null && !task.getImageUrl().isEmpty()) {
            String imageSrc = baseUrl + "/article/image/" + task.getId() + ".png";
            imageBlock = "<div class=\"article-image\">" +
                    "<img src=\"" + imageSrc + "\" alt=\"" + title + "\" />keyi" +
                    "</div>";
        }

        // 标签HTML
        String tagsBlock = "";
        if (tags != null && !tags.isEmpty()) {
            StringBuilder tagHtml = new StringBuilder("<div class=\"tags\">");
            for (String tag : tags.split("[,，]")) {
                String trimmed = tag.trim();
                if (!trimmed.isEmpty()) {
                    tagHtml.append("<span class=\"tag\">").append(trimmed).append("</span>");
                }
            }
            tagHtml.append("</div>");
            tagsBlock = tagHtml.toString();
        }

        // 将正文中的换行转为段落
        String contentHtml = Stream.of(content.split("\n"))
                .filter(line -> !line.trim().isEmpty())
                .map(line -> "<p>" + line.trim() + "</p>")
                .collect(Collectors.joining("\n"));

        return "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\" />\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\" />\n" +
                "  <title>" + title + "</title>\n" +
                "  <style>\n" +
                "    * { margin: 0; padding: 0; box-sizing: border-box; }\n" +
                "    body {\n" +
                "      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;\n" +
                "      background: #f5f5f5;\n" +
                "      color: #333;\n" +
                "      line-height: 1.8;\n" +
                "    }\n" +
                "    .article-container {\n" +
                "      max-width: 680px;\n" +
                "      margin: 0 auto;\n" +
                "      background: #fff;\n" +
                "      min-height: 100vh;\n" +
                "    }\n" +
                "    .article-header {\n" +
                "      padding: 32px 20px 16px;\n" +
                "    }\n" +
                "    .article-title {\n" +
                "      font-size: 22px;\n" +
                "      font-weight: 700;\n" +
                "      line-height: 1.4;\n" +
                "      color: #1a1a1a;\n" +
                "      margin-bottom: 12px;\n" +
                "    }\n" +
                "    .article-meta {\n" +
                "      font-size: 13px;\n" +
                "      color: #999;\n" +
                "    }\n" +
                "    .article-image {\n" +
                "      width: 100%;\n" +
                "      margin: 0;\n" +
                "    }\n" +
                "    .article-image img {\n" +
                "      width: 100%;\n" +
                "      display: block;\n" +
                "    }\n" +
                "    .article-body {\n" +
                "      padding: 24px 20px;\n" +
                "      font-size: 16px;\n" +
                "    }\n" +
                "    .article-body p {\n" +
                "      margin-bottom: 16px;\n" +
                "      text-indent: 2em;\n" +
                "    }\n" +
                "    .tags {\n" +
                "      padding: 0 20px 24px;\n" +
                "      display: flex;\n" +
                "      flex-wrap: wrap;\n" +
                "      gap: 8px;\n" +
                "    }\n" +
                "    .tag {\n" +
                "      display: inline-block;\n" +
                "      padding: 4px 12px;\n" +
                "      background: #f0f0f0;\n" +
                "      border-radius: 14px;\n" +
                "      font-size: 13px;\n" +
                "      color: #666;\n" +
                "    }\n" +
                "    .article-footer {\n" +
                "      padding: 16px 20px 32px;\n" +
                "      border-top: 1px solid #eee;\n" +
                "      text-align: center;\n" +
                "      font-size: 12px;\n" +
                "      color: #bbb;\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"article-container\">\n" +
                "    <div class=\"article-header\">\n" +
                "      <h1 class=\"article-title\">" + title + "</h1>\n" +
                "      <div class=\"article-meta\">" + createTime + "</div>\n" +
                "    </div>\n" +
                "    " + imageBlock + "\n" +
                "    <div class=\"article-body\">\n" +
                "      " + contentHtml + "\n" +
                "    </div>\n" +
                "    " + tagsBlock + "\n" +
                "    <div class=\"article-footer\">Powered by ZJ</div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>";
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * 生成"加载中"提示页面，每3秒自动刷新
     */
    private String buildLoadingHtml(Long id) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\" />\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "  <meta http-equiv=\"refresh\" content=\"3;url=/article/" + id + "\" />\n" +
                "  <title>生成中</title>\n" +
                "  <style>\n" +
                "    body { display:flex; justify-content:center; align-items:center; min-height:100vh; " +
                "font-family:-apple-system,sans-serif; background:#f5f5f5; color:#666; }\n" +
                "    .loading { text-align:center; }\n" +
                "    .spinner { width:40px; height:40px; border:4px solid #e0e0e0; " +
                "border-top-color:#333; border-radius:50%; animation:spin 0.8s linear infinite; margin:0 auto 16px; }\n" +
                "    @keyframes spin { to { transform:rotate(360deg); } }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body><div class=\"loading\"><div class=\"spinner\"></div><p>推文生成中，请稍候...</p></div></body>\n" +
                "</html>";
    }
}
