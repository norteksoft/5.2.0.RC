package com.norteksoft.mms.form.enumeration;

public enum EventType {
	/**
	 * 点击
	 */
	ONCLICK("edit.event.type.onclick"),
	/**
	 * 下拉框切换
	 */
	ONCHANGE("edit.event.type.onchange"),
	/**
	 * 双击事件
	 */
	ONDBLCLICK("edit.event.type.ondblclick"),
	/**
	 * 失去焦点事件
	 */
	BLUR("edit.event.type.blur");
	public String name;
	public String code;
	EventType(String code){
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
