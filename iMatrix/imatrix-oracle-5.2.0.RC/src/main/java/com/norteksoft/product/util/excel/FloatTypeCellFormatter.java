package com.norteksoft.product.util.excel;

import org.apache.poi.ss.usermodel.Cell;

public class FloatTypeCellFormatter extends CellFormatter {
	
	public FloatTypeCellFormatter(Cell cell) {
        super(cell);
    }

    @Override
    protected String getDefaultFormat() {
        return "@";//文本格式;
    }

    @Override
    protected void setCellValue(Object value) {
        cell.setCellValue(Float.parseFloat(value.toString()));
    }
    
    @Override
    protected void setDefaultCellValue(Object value) {
    	setCellValue(value);
    }
    
}
