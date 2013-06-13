package com.norteksoft.mms.authority.enumeration;

/**
 * 文本类型的操作符
 */
public enum UserOperator {
	/**
	 * 等于
	 */
	ET("operator.text.et"),//等于
	/**
	 * 不等于
	 */
	NET("operator.text.et.not");//不等于
	public String code;
	UserOperator(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
