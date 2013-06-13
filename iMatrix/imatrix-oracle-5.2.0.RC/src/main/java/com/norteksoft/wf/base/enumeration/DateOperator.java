package com.norteksoft.wf.base.enumeration;

/**
 * 日期类型的逻辑操作符
 */
public enum DateOperator {
	/**
	 * 晚于
	 */
	GT("operator.date.gt"),//晚于
	/**
	 * 早于
	 */
	LT("operator.date.lt"),//早于
	/**
	 * 等于
	 */
	ET("operator.date.et"),//等于
	/**
	 * 不晚于
	 */
	NGT("operator.date.le"),//不晚于
	/**
	 * 不早于
	 */
	NLT("operator.date.ge"),//不早于
	
	/**
	 * 等于
	 */
	NET("operator.date.ne");//不等于
	public String code;
	DateOperator(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
