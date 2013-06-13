package com.norteksoft.tags.tree;

/**
 * 树部门显示类型
 */
public enum DepartmentDisplayType {
	/**
	 * 编号
	 */
	CODE("departmentCode"),
	/**
	 *名称
	 */
	NAME("departmentName"),
	/**
	 * 简称
	 */
	SHORTTITLE("departmentShortTitle"),
	/**
	 * 概要
	 */
	SUMMARY("departmentSummary");

	
	public String code;
	DepartmentDisplayType(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
