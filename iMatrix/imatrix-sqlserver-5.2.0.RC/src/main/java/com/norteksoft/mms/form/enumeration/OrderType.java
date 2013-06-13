package com.norteksoft.mms.form.enumeration;

/**
 * 排序方式
 */
public enum OrderType {
	
	/**
	 * 升序
	 */
	ASC("order.type.asc"),
	/**
	 * 降序
	 */
	DESC("order.type.desc");
	public String code;
	OrderType(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
