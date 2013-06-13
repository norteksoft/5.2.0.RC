package com.norteksoft.tags.tree;

/**
 * 文本类型的操作符
 */
public enum TreeType {
	/**
	 * 公司树
	 */
	COMPANY("companyTree"),
	/**
	 *人员部门和工作组树
	 */
	MAN_DEPARTMENT_GROUP_TREE("manDepartmentTree"),
	/**
	 * 人员部门树
	 */
	MAN_DEPARTMENT_TREE("manDepartmentTree"),
	/**
	 * 人员工作组树
	 */
	MAN_GROUP_TREE("manGroupTree"),

	/**
	 * 部门树
	 */
	DEPARTMENT_TREE("departmentTree"),
	/**
	 * 工作组树
	 */
	GROUP_TREE("groupTree"),
	/**
	 * 部门工作组树
	 */
	DEPARTMENT_WORKGROUP_TREE("departmentWorkgroupTree");
	
	
	
	
	
	
	
	public String code;
	TreeType(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
