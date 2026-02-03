package com.zj.wechat.config;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.*;

import java.util.List;

/**
 * 支持easyExcel导出时设置列宽、列高、表头字体大小、填充色
 * 限制是基于cell回写，最后一列无法覆盖格式，处理方式在列最后加一列空列并隐藏
 * 待优化点，添加传入对columnWidths的校验
 */
public class CustomSheetWriteHandler implements CellWriteHandler {

    //外围调用时传入各列的列宽
    private final List<Integer> columnWidths;

    // 表头是否已设置样式（防重）
    private volatile boolean headerStyled = false;


    public CustomSheetWriteHandler(List<Integer> columnWidths) {
        this.columnWidths = columnWidths;
    }

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List<WriteCellData<?>> cellDataList,
                                 Cell cell, Head head, Integer relativeRowIndex, Boolean isHead)
    {
        if (!Boolean.TRUE.equals(isHead) || headerStyled) {
            return;
        }

        int currentColIndex = cell.getColumnIndex();
        int totalColumns = columnWidths.size();

        // 必须是最后一列才触发样式设置
        if (currentColIndex == totalColumns - 1) {
            Sheet sheet = writeSheetHolder.getSheet();
            Workbook workbook = sheet.getWorkbook();
            Row headerRow = cell.getRow();

            // 1. 设置列宽（必须覆盖所有列）
            for (int i = 0; i < totalColumns; i++) {
                sheet.setColumnWidth(i, columnWidths.get(i) * 256);
            }
            sheet.setColumnHidden(totalColumns-1, true);

            // 2. 设置行高
            headerRow.setHeight((short) 600);

            // 3. 创建样式（取消加粗）
            CellStyle headerStyle = createHeaderStyle(workbook);

            // 4. 应用到所有列（确保创建缺失的 Cell）
            for (int i = 0; i < totalColumns; i++) {
                Cell c = headerRow.getCell(i);
                System.out.println(c.getStringCellValue());
                c.setCellStyle(headerStyle);
            }

            headerStyled = true;
        }
    }

    /**
     * 表头的格式,正文列也可以参考复用
     * @param workbook
     * @return
     */
        private CellStyle createHeaderStyle(Workbook workbook) {
            CellStyle style = workbook.createCellStyle();
            // 字体
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 11);
            //font.setBold(true); //字体加粗
            font.setFontName("微软雅黑");
            style.setFont(font);
            // 背景 + 对齐
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setWrapText(true);
            return style;
        }
}