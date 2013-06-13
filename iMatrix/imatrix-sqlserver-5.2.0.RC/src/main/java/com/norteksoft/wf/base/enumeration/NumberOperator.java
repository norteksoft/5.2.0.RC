package com.norteksoft.wf.base.enumeration;

/**
 * 数字类型的逻辑操作符
 */
public enum NumberOperator {
	/**
	 * 大于
	 */
	GT("operator.number.gt"),//大于
	/**
	 * 小于
	 */
	LT("operator.number.lt"),//小于
	/**
	 * 等于
	 */
	ET("operator.number.et"),//等于
	/**
	 * 不大于 
	 */
	NMT("operator.number.le"),//不大于
	/**
	 * 不小于
	 */
	NLT("operator.number.ge"),//不小于
	/**
	 * 等于
	 */
	NET("operator.number.ne");//不等于
	public String code;
	NumberOperator(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
