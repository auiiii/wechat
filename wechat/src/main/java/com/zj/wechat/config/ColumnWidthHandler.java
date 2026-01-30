package com.zj.wechat.config;

import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

public class ColumnWidthHandler implements WriteHandler {

    private List<Integer> columnWidths = null; // 每列的宽度（单位：字符数）

    public ColumnWidthHandler(List<Integer> columnWidths) {
        this.columnWidths = columnWidths;
    }

}