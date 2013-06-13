package com.norteksoft.mms.form.enumeration;

/**
 * 查询组件的查询类型
 * @author wurong
 *
 */
public enum QueryType {
	/**
	 * 普通查询
	 */
	FIXED("query.type.fixed"),
	/**
	 * 高级查询
	 */
	CUSTOM("query.type.custom"),
	/**
	 * 不查询
	 */
	NONE("query.type.none");
	public String name;
	public String code;
	QueryType(String code){
		this.code=code;
	}
	public int getIndex(){
		return this.ordinal();
	}
	public String getCode() {
		return code;
	}
	/**
	 * 返回枚举的名称
	 * @return
	 */
	public String getName(){
		return this.toString();
	}

	
}
