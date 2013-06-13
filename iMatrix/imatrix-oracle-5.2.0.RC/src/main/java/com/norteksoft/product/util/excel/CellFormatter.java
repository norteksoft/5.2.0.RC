package com.norteksoft.product.util.excel;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Workbook;

public abstract class CellFormatter {
    private Workbook wb;
    protected Cell cell;
    
    
    public CellFormatter(Cell cell) {
        this.cell = cell;
        this.wb=cell.getSheet().getWorkbook();
    }


    public void formatValue(Object value, String format){
    	boolean isDefault=(format.startsWith("func:")||StringUtils.isEmpty(format));
        //对于mms中的自定义格式或者没有设置格式的情况下，处理成默认的格式类型
        if(isDefault){
            format=getDefaultFormat();
        }
       
        
//      处理值为null或者空串的情况,FormHtmlParser中会把数字，日期null值转换为&nbsp;
        if((value==null||StringUtils.isEmpty(value.toString()))||"&nbsp;".equals(value.toString())){
            cell.setCellValue("");
        }else{
        	if(!isDefault){//如果 设置了格式类型时
        		CellStyle dataStyle = wb.createCellStyle();
        		DataFormat dataFormat=  wb.createDataFormat();
        		dataStyle.setDataFormat(dataFormat.getFormat(format));
        		cell.setCellStyle(dataStyle); 
        		setCellValue(value);
        	}else{
        		setDefaultCellValue(value);
        	}
            
        }
        
        
    }

    /**
     * 当mms中的format为自定义格式或者没有设置时，根据单元格类型返回一个默认格式
     * @return
     */
    abstract protected String  getDefaultFormat();
    /**
     * 当value不为空时，给cell赋值（这时需要进行正确的类型转换）
     * POI的cell.setCellValue不能传递null参数，并且参数只能是具体的类型
     * @param value
     */
    abstract protected void setCellValue(Object value);
    abstract protected void setDefaultCellValue(Object value);

}
