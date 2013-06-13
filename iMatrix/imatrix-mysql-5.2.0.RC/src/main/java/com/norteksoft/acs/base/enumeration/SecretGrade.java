package com.norteksoft.acs.base.enumeration;

public enum SecretGrade {
	
	COMMON("secret.common"), // 一般
	MAJOR("secret.major"), // 重要
	CENTRE("secret.centre"); // 核心
	
	private String code;
	
	SecretGrade(String code){
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
}
