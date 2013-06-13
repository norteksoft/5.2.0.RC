package com.norteksoft.wf.base.enumeration;

/**
 * 文本类型的操作符
 */
public enum TextOperator {
	/**
	 * 包含
	 */
	CONTAINS("operator.text.contain"),//包含
	/**
	 * 不包含
	 */
	NOT_CONTAINS("operator.text.contain.not"),//不包含
	/**
	 * 等于
	 */
	ET("operator.text.et"),//等于
	/**
	 * 不等于
	 */
	NET("operator.text.et.not");//不等于
	public String code;
	TextOperator(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
