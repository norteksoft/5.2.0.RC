package com.norteksoft.mms.module.enumeration;

public enum ButtonType {
	COMMON("button.type.common"),
	CUSTOM("button.type.custom"),
	WORKFLOW("button.type.wf");
	public String code;
	ButtonType(String code){
		this.code=code;
	}
	public int getIndex(){
		return this.ordinal();
	}
	public String getCode() {
		return code;
	}
}
