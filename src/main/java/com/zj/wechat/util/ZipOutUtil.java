package com.zj.wechat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipOutUtil {

    private static final Logger logger = LoggerFactory.getLogger(ZipOutUtil.class);

    private static final int  BUFFER_SIZE = 2 * 1024;

    /**
     * 输出响应zip包
     * @param response
     * @param list
     * @throws Exception
     */
    public static void toZip(HttpServletResponse response, List<byte[]> list, List<String>nameList) throws Exception {
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null;
        response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
        response.setHeader("Content-Disposition", "attachment;filename=attachment.zip");// 设置在下载框默认显示的文件名
        try {
            zos = new ZipOutputStream(response.getOutputStream());
            for (int i = 0; i < list.size(); i++) {
                byte[] bytes = list.get(i);
                String fileName = nameList.get(i);
                byte[] buf = new byte[BUFFER_SIZE];
                zos.putNextEntry(new ZipEntry(fileName));
                int len;
                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                while ((len = in.read(buf)) != -1){
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
            long end = System.currentTimeMillis();
            logger.info("压缩完成，耗时：" + (end - start) +" ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils",e);
        }finally{
            if(zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    logger.error("",e);
                }
            }
        }
    }
}
