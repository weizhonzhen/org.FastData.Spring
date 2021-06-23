package org.FastData.Spring.Model;

import org.apache.poi.hssf.usermodel.*;

public class ExcelModel {
    private HSSFWorkbook workbook ;

    private HSSFSheet sheet ;

    public HSSFWorkbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(HSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public HSSFSheet getSheet() {
        return sheet;
    }

    public void setSheet(HSSFSheet sheet) {
        this.sheet = sheet;
    }
}
