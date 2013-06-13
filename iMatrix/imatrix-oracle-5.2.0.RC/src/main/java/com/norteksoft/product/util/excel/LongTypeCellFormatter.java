package com.norteksoft.product.util.excel;

import org.apache.poi.ss.usermodel.Cell;

public class LongTypeCellFormatter extends CellFormatter {
    public LongTypeCellFormatter(Cell cell) {
        super(cell);
    }
    @Override
    protected String getDefaultFormat() {
        return "0";//数值格式;
    }

    @Override
    protected void setCellValue(Object value) {
        cell.setCellValue(Long.parseLong(value.toString()));

    }
    @Override
    protected void setDefaultCellValue(Object value) {
    	setCellValue(value);
    }

}
