package com.norteksoft.mms.form.enumeration;
/**
 * 查询显示方式
 * @author Administrator
 *
 */
public enum DefaultSortWay {
	/**
	 * 升序
	 */
	ACS("query.show.way.implant"),
	/**
	 * 降序
	 */
	DESC("query.show.way.popup");
	
	public String code;
	
	DefaultSortWay(String code){
		this.code=code;
	}
	
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	
	public String getCode(){
		return this.code;
	}
	
	/**
	 * 返回枚举的名称
	 * @return
	 */
	public String getEnumName(){
		return this.toString();
	}
}
