package org.FastData.Spring.Util;

import org.FastData.Spring.Model.ExcelModel;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ExcelUtil {
    public static ExcelModel init(String headerText, List<LinkedHashMap<String,Object>> title1, LinkedHashMap<String,Object> title2) {
        ExcelModel result = new ExcelModel();
        try {
            HSSFRow row = initTitle(headerText, title2, result);
            final int[] i = {0};
            final int[] step = {0};

            HSSFRow finalRow = row;
            title1.forEach(a -> {
                HSSFCell cell = null;
                if (i[0] == 0)
                    cell = finalRow.createCell(i[0]++);
                else
                    cell = finalRow.createCell(step[0]);
                cell.getRow().setHeight((short) 420);
                cell.setCellValue(a.get("text").toString());
                result.getSheet().addMergedRegion(new CellRangeAddress(1, 1, step[0], step[0] + Integer.parseInt(a.get("step").toString()) - 1));
                cell.setCellStyle(getStyle(result.getWorkbook(), true, false));
                step[0] = step[0] + Integer.parseInt(a.get("step").toString());
            });

            row = result.getSheet().createRow(2);
            i[0] = 0;
            initTitle(title2, result, i, row);
        } catch (Exception ex) {
            LogUtil.error(ex);
        }
        return result;
    }

    public static ExcelModel init(String headerText, LinkedHashMap<String,Object> title1, List<LinkedHashMap<String,Object>> title2){
        ExcelModel result = new ExcelModel();
        try {
            initTitle(headerText, title1, result);
            HSSFRow row = result.getSheet().createRow(1);
            final int[] i = {0};
            final int[] step = {0};
            initTitle(title1, result, i, row);

            i[0] = 0;
            row = result.getSheet().createRow(2);
            if (title2 != null) {
                HSSFRow finalRow = row;
                title2.forEach(a ->
                {
                    HSSFCell cell = null;
                    if (i[0] == 0)
                        cell = finalRow.createCell(i[0]++);
                    else
                        cell = finalRow.createCell(step[0]);
                    cell.getRow().setHeight((short) 420);
                    cell.setCellValue(a.get("text").toString());
                    result.getSheet().addMergedRegion(new CellRangeAddress(2, 2, step[0], step[0] + Integer.parseInt(a.get("step").toString()) - 1));
                    cell.setCellStyle(getStyle(result.getWorkbook(), true, false));
                    step[0] = step[0] + Integer.parseInt(a.get("step").toString());
                });
            }
        }
        catch (Exception ex)
        {
            LogUtil.error(ex);
        }
        return result;
    }

    public static void fillContent(List<LinkedHashMap<String, Object>> listContent, ExcelModel model, String exclude, boolean isSmallTile){
        try {
            int i = 0;
            HSSFCellStyle style_n = getStyle(model.getWorkbook(), true, true);
            HSSFCellStyle style = getStyle(model.getWorkbook(), false, false);
            HSSFRow row = null;
            HSSFCell cell = null;

            for (Map<String, Object> item : listContent) {
                if (isSmallTile)
                    row = model.getSheet().createRow(i + 3);
                else
                    row = model.getSheet().createRow(i + 2);
                int j = 0;

                for (Map.Entry<String, Object> temp : item.entrySet()) {
                    if (temp.getKey().equalsIgnoreCase(exclude))
                        continue;

                    cell = row.createCell(j++);
                    cell.getRow().setHeight((short) 420);

                    if (temp.getValue() instanceof Map) {
                        Map<String, Object> info = (Map<String, Object>) temp.getValue();
                        model.getSheet().addMergedRegion(new CellRangeAddress(
                                Integer.parseInt(info.get("rowbegin").toString()), Integer.parseInt(info.get("rowend").toString()),
                                Integer.parseInt(info.get("colbegin").toString()) - 1, Integer.parseInt(info.get("colend").toString()) - 1));

                        cell.setCellValue(info.get("text").toString());

                        if (info.get("text").toString().contains("\n"))
                            cell.setCellStyle(style_n);
                        else
                            cell.setCellStyle(style);
                    } else {
                        cell.setCellValue(temp.getValue().toString());

                        if (temp.getValue().toString().contains("\n"))
                            cell.setCellStyle(style_n);
                        else
                            cell.setCellStyle(style);
                    }
                }

                i++;
            }
        }
        catch (Exception ex) {
            LogUtil.error(ex);
        }
    }

    public static void fillContent(List<LinkedHashMap<String, Object>> listContent, ExcelModel model){
        fillContent(listContent,model,"",false);
    }

    public static byte[] Result(ExcelModel model, Map<String, Object> title){
        try {
            final int[] i = {0};
            i[0] = 0;
            title.keySet().forEach(a ->
            {
                model.getSheet().autoSizeColumn(i[0]++,true);
                model.getSheet().setAutobreaks(true);
                model.getSheet().setHorizontallyCenter(true);
            });

            for (int rowNum = 2; rowNum <= model.getSheet().getLastRowNum(); rowNum++)
            {
                HSSFRow currentRow = model.getSheet().getRow(rowNum);
                int height = 0;
                for (int col = 0; col < title.size(); col++)
                {
                    HSSFCell currentCell = currentRow.getCell(col);
                    if (currentCell != null)
                    {
                        int length = currentCell.toString().getBytes(StandardCharsets.UTF_8).length;
                        if ((25 * (length / 60 + 1)) > height)
                            height = 25 * (length / 60 + 1);
                    }
                }

                currentRow.setHeightInPoints(height);
            }

            return  model.getWorkbook().getBytes();
        }
        catch (Exception ex)
        {
            LogUtil.error(ex);
            return null;
        }
    }

    private static HSSFRow initTitle(String headerText, LinkedHashMap<String, Object> title1, ExcelModel result) {
        result.setWorkbook(new HSSFWorkbook());
        initializeWorkbook(result.getWorkbook());
        HSSFSheet sheet =result.getWorkbook().createSheet(headerText);
        HSSFRow row =sheet.createRow(0);
        HSSFCell cell =row.createCell(0);
        cell.setCellValue(headerText);
        HSSFCellStyle style = result.getWorkbook().createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont font = result.getWorkbook().createFont();
        font.setFontHeight((short)350);
        style.setFont(font);
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, title1.keySet().size()-1));
        result.setSheet(sheet);
        return row;
    }

    private static HSSFCellStyle getStyle(HSSFWorkbook hssfworkbook, boolean IsHead , boolean IsWrapText ){
        HSSFCellStyle style = hssfworkbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setWrapText(IsWrapText);
        style.setIndention((short)0);

        if (IsHead)
            style.setBorderTop(BorderStyle.THIN);

        return style;
    }

    private static void initializeWorkbook(HSSFWorkbook hssfworkbook){
        DocumentSummaryInformation dsi = PropertySetFactory.newDocumentSummaryInformation();;
        dsi.setCompany("");
        SummaryInformation si = PropertySetFactory.newSummaryInformation();
        si.setSubject("");
    }

    private static void initTitle(LinkedHashMap<String, Object> title2, ExcelModel result, int[] i,HSSFRow row){
        title2.keySet().forEach(a -> {
            HSSFCell cell = row.createCell(i[0]++);
            cell.getRow().setHeight((short)380);
            cell.setCellValue(title2.get(a).toString());
            cell.setCellStyle(getStyle(result.getWorkbook(), true, false));
        });
    }
}
