package com.norteksoft.mms.authority.enumeration;

public enum ItemType {
	/**
	 * 人员
	 */
	USER("permission.item.type.user"),
	/**
	 * 部门
	 */
	DEPARTMENT("permission.item.type.department"),
	/**
	 * 角色
	 */
	ROLE("permission.item.type.role"),
	/**
	 * 工作组
	 */
	WORKGROUP("permission.item.type.workgroup");
	public String code;
	ItemType(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
