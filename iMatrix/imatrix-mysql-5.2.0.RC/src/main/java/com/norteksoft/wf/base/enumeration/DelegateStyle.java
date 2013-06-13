package com.norteksoft.wf.base.enumeration;

/**
 * 数字类型的逻辑操作符
 */
public enum DelegateStyle {
	ONLY("指定环节"),
	ALL("全权委托");
	public String code;
	DelegateStyle(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
