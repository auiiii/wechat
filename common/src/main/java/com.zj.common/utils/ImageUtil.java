package com.zj.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;

/**
 * @author zhujie
 * @ClassName
 * @Description
 * @date: 2023/10/31
 */
public class ImageUtil {

    private static final Logger logger = LoggerFactory.getLogger(ImageUtil.class);

    /**
     * 输出响应图片
     *
     * @param response
     * @param bufferedImage
     * @throws Exception
     */
    public static void write2Rsp(HttpServletResponse response, BufferedImage bufferedImage, String fileName) throws Exception {
        long start = System.currentTimeMillis();
        ImageOutputStream imageOutputStream = null;
        try {
            // 获取图片写出器
            Iterator<ImageWriter> imageWriterIterator = ImageIO.getImageWritersByFormatName("png");
            if (!imageWriterIterator.hasNext()) {
                logger.warn("未找到图片写入对象");
            } else {
                ImageWriter imageWriter = imageWriterIterator.next();
                // 设置图片输出流
                response.setContentType("image/png");// 指明response的返回对象是文件流
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName);// 设置在下载框默认显示的文件名
                imageOutputStream = ImageIO.createImageOutputStream(response.getOutputStream());
                imageWriter.setOutput(imageOutputStream);
                // 写出图片
                imageWriter.write(bufferedImage);
            }
            long end = System.currentTimeMillis();
            logger.info("写出流完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            throw new RuntimeException("ys error from ImageUtil", e);
        } finally {
            if (imageOutputStream != null) {
                try {
                    imageOutputStream.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
    }

    /**
     * 压缩为指定大小
     * @param image
     * @param quality
     * @return
     */
    public static BufferedImage compress(BufferedImage image, double quality) {
        Image scaledImage = image.getScaledInstance((int) (image.getWidth() * quality), (int) (image.getHeight() * quality), Image.SCALE_SMOOTH);
        BufferedImage bufferedImage = new BufferedImage((int) (image.getWidth() * quality), (int) (image.getHeight() * quality), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);
        graphics.dispose();
        return bufferedImage;
    }
}
