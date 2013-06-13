package com.norteksoft.product.util.excel;

import org.apache.poi.ss.usermodel.Cell;

public class StringTypeCellFormatter extends CellFormatter {

    public StringTypeCellFormatter(Cell cell) {
        super(cell);
    }

    @Override
    protected String getDefaultFormat() {
        return "@";
    }

    @Override
    protected void setCellValue(Object value) {
        cell.setCellValue(value.toString());
    }
    @Override
    protected void setDefaultCellValue(Object value) {
    	setCellValue(value);
    }
}
