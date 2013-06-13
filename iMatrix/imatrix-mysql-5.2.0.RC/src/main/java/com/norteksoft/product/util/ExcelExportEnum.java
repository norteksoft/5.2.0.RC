package com.norteksoft.product.util;

public enum ExcelExportEnum {
	EXCEL2003("excel.2003"),
	EXCEL2007("excel.2007");
	public String code;
	ExcelExportEnum(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
