package com.norteksoft.mms.form.enumeration;
/**
 * 启用查询
 * @author Administrator
 *
 */
public enum StartQuery {
	/**
	 * 内置查询
	 */
	INSIDE_QUERY("start.query.insideQuery"),
	/**
	 * 自定义查询
	 */
	CUSTOM_QUERY("start.query.customQuery"),
	/**
	 * 不启用查询
	 */
	NO_QUERY("start.query.noQuery");
	public String code;
	StartQuery(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
