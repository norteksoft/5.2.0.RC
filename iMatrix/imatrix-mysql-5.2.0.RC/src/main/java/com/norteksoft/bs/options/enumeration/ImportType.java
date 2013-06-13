package com.norteksoft.bs.options.enumeration;

/**
 * 导入类型 
 * @author Administrator
 *
 */
public enum ImportType {
	TXT_DIVIDE("bs.import.type.txtDivide"),//分隔符分隔的文本
	TXT("bs.import.type.txt");//固定长度文本
	
	public String code;
	ImportType(String code){
		this.code=code;
	}
	public String getCode() {
		return code;
	}

}
