package com.zj.wechat.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class MovieExportDTO {

    @ExcelProperty("ID")
    private Long id;

    @ExcelProperty("名称")
    private String title;

    @ExcelProperty("导演")
    private String author;

    @ExcelProperty("描述")
    private String description;

    @ExcelProperty("链接")
    private String movieUrl;

    @ExcelProperty("海报媒体库ID")
    private String mediaId;

    @ExcelProperty("海报链接")
    private String picUrl;

    @ExcelProperty(" ")
    private String lastRow;//easyExcel流式写入-特殊处理-最后一列补空

    public String getLastRow() {
        return "";
    }

}
