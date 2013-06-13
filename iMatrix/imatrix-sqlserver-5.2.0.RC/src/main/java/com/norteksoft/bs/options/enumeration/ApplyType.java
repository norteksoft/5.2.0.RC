package com.norteksoft.bs.options.enumeration;

/**
 * 定时请求类型
 */
public enum ApplyType {

	HTTP_APPLY("bs.httpApply"),

	RESTFUL_APPLY("bs.restfulApply");


	public String code;
	ApplyType(String code){
		this.code=code;
	}
	public String getCode(){
		return this.code;
	}
}
