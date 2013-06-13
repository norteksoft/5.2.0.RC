package com.norteksoft.bs.options.enumeration;
/**
 * 主键类型
 * @author Administrator
 *
 */
public enum BusinessType {
	BUSINESS_FIELD("bs.business.type.business.field"),//业务主键
	RELEVANCE_FIELD("bs.business.type.relevance.field");//关联主键
	
	public String code;
	BusinessType(String code){
		this.code=code;
	}
	public String getCode() {
		return code;
	}
}
