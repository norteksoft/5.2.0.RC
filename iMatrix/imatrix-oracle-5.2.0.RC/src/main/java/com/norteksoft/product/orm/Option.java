package com.norteksoft.product.orm;

public enum Option {

	/**
	 * 等于(==)
	 */
	ET("opt.et", "=="), 
	/**
	 * 大于(>)
	 */
	GT("opt.gt", ">"), 
	/**
	 * 小于(<)
	 */
	LT("opt.lt", "<"), 
	/**
	 * 不小于(>=)
	 */
	NLT("opt.ge", ">="),
	/**
	 * 不大于(<=)
	 */
	NGT("opt.le", "<="),
	/**
	 * 不等于(!=)
	 */
	NET("opt.ne", "!="),
	/**
	 * 包含
	 */
	CT("opt.ct", ""), 
	/**
	 * 并且
	 */
	AND("opt.and", "AND"),
	/**
	 * 或者
	 */
	OR("opt.or", "OR"); 
	
	public String code;
	public String sign;
	
	Option(String code, String sign){
		this.code=code;
		this.sign = sign;
	}
}
