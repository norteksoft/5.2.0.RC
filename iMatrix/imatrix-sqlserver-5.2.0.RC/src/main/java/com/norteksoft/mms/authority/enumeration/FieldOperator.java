package com.norteksoft.mms.authority.enumeration;
/**
 * 字段操作符
 * @author Administrator
 *
 */
public enum FieldOperator {
	/**
	 * 等于
	 */
	ET("field.operator.et","="),//等于
	/**
	 * 不等于
	 */
	NET("field.operator.et.not","!="),//不等于
	/**
	 * 大于
	 */
	GT("field.operator.gt",">"),//大于
	/**
	 * 小于
	 */
	LT("field.operator.lt","<"),//小于
	/**
	 * 大于等于
	 */
	GET("field.operator.gt.et",">="),//大于等于
	/**
	 * 小于等于
	 */
	LET("field.operator.lt.et","<="),//小于等于
	/**
	 * 为空
	 */
	IS_NULL("field.operator.is.null", " is null "),//为空
	/**
	 * 不为空
	 */
	NOT_NULL("field.operator.not.null"," is not null "),//不为空
	/**
	 * 包含
	 */
	CONTAIN("field.operator.contain", " like "),//包含
	/**
	 * 不包含
	 */
	NOT_CONTAIN("field.operator.not.contain", " not like ");//不包含
	
	public String code;
	public String sign;
	
	FieldOperator(String code, String sign){
		this.code=code;
		this.sign=sign;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
