package com.norteksoft.product.util.excel;

import java.text.ParseException;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;

import com.ibm.icu.text.SimpleDateFormat;

public class TimeTypeCellFormatter extends CellFormatter {
    private final static String DEFAULT_FORMAT="yyyy-MM-dd hh:mm";//EXCEL格式设置不区分大小写，java区分
    public TimeTypeCellFormatter(Cell cell) {
        super(cell);
    }

    @Override
    protected String getDefaultFormat() {
        return DEFAULT_FORMAT;
    }

    @Override
    protected void setCellValue(Object value) {
    		SimpleDateFormat dft = new SimpleDateFormat(DEFAULT_FORMAT); 
    		Date cellValue;
    		try {
    			cellValue = dft.parse(value.toString());
    		} catch (ParseException e) {
    			throw new RuntimeException("cell value is not a datetime!");
    		}
    		cell.setCellValue(cellValue);
    }
    @Override
    protected void setDefaultCellValue(Object value) {
    	cell.setCellValue(value.toString());
    }
}
