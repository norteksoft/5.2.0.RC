package com.norteksoft.mms.form.enumeration;
/**
 * 合计方式
 * @author Administrator
 *
 */
public enum TotalType {
	/**
	 * 当前页
	 */
	CURRENT_PAGE("total.type.current.page"),
	/**
	 * 所有页
	 */
	ALL_PAGE("total.type.all.page");
	
	public String code;
	
	TotalType(String code){
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
