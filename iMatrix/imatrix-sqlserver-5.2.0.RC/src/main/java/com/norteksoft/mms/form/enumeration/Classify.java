package com.norteksoft.mms.form.enumeration;
/**
 * 格式设置中的分类
 * @author Administrator
 *
 */
public enum Classify {
	/**
	 * 数值
	 */
	NUMERICAL_VALUE("classify.numerical.value"),
	/**
	 * 货币
	 */
	CURRENCY("classify.currency"),
	/**
	 * 日期
	 */
	DATE("classify.date"),
	/**
	 * 时间
	 */
	TIME("classify.time"),
	/**
	 * 百分比
	 */
	PERCENT("classify.percent"),
	/**
	 * 自定义
	 */
	CUSTOM("classify.custom");
	
	public String code;
	
	Classify(String code){
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
