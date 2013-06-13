package com.norteksoft.product.util.excel;

import org.apache.poi.ss.usermodel.Cell;

public class DoubleTypeCellFormatter extends CellFormatter {
    
    public DoubleTypeCellFormatter(Cell cell) {
        super(cell);
    }

    @Override
    protected String getDefaultFormat() {
        return "@";//文本格式;
    }

    @Override
    protected void setCellValue(Object value) {
        cell.setCellValue(Double.parseDouble(value.toString()));
    }
    
    @Override
    protected void setDefaultCellValue(Object value) {
    	setCellValue(value);
    }

}
