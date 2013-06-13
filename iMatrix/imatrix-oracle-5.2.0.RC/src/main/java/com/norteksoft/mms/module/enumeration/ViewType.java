package com.norteksoft.mms.module.enumeration;

/**
 * 视图的类型
 */
public enum ViewType {
	LIST_VIEW("view.type.list"),
	FORM_VIEW("view.type.form");
	public String code;
	ViewType(String code){
		this.code=code;
	}
	public int getIndex(){
		return this.ordinal();
	}
	public String getCode() {
		return code;
	}
}
