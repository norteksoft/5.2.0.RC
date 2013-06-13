package com.norteksoft.mms.form.enumeration;

public enum MenuType {
	STANDARD("menu.type.standard"),
	CUSTOM("menu.type.custom");
	public String code;
	MenuType(String code){
		this.code=code;
	}
	
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
	
}
