package com.norteksoft.wf.base.enumeration;

/**
 * 逻辑或、与
 */
public enum LogicOperator {
	/**
	 * 逻辑与
	 */
	AND("condition.operator.and"),//并且
	/**
	 * 逻辑或
	 */
	OR("condition.operator.or");//或者
	public String code;
	LogicOperator(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
