package com.norteksoft.product.util.excel;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;

public class CellFormatterFactory {

    public static CellFormatter getCellFormatter(String dataType, Cell cell,String valueSet) {
    	if(StringUtils.isNotEmpty(valueSet))return new StringTypeCellFormatter(cell);//当值设置不为空时，则导出时以文本格式导出
        if("INTEGER".equals(dataType)){
            return new IntegerTypeCellFormatter(cell);
        }else if("DOUBLE".equals(dataType)||"AMOUNT".equals(dataType)){
            return new DoubleTypeCellFormatter(cell);
        }else if("FLOAT".equals(dataType)||"AMOUNT".equals(dataType)){
            return new FloatTypeCellFormatter(cell);
        }else if("LONG".equals(dataType)||"NUMBER".equals(dataType)){
            return new LongTypeCellFormatter(cell);
        }else if("DATE".equals(dataType)){
            return new DateTypeCellFormatter(cell);
        }else if("TIME".equals(dataType)){
            return new TimeTypeCellFormatter(cell);
        }else{
            return new StringTypeCellFormatter(cell);
        }
    }
}
