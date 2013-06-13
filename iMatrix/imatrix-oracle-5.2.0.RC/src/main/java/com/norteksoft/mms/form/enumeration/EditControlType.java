package com.norteksoft.mms.form.enumeration;

/**
 * 编辑列表时控件的类型
 * @author wurong
 *
 */
public enum EditControlType {
	/**
	 * 文本框
	 */
	TEXT("edit.control.type.text"),
	/**
	 * 复选框
	 */
	CHECKBOX("edit.control.type.checkbox"),
	/**
	 * 下拉框
	 */
	SELECT("edit.control.type.select"),
	/**
	 * 多选下拉框
	 */
	MULTISELECT("edit.control.type.multiselect"),
	/**
	 * 文本域
	 */
	TEXTAREA("edit.control.type.textarea"),
	/**
	 * 自定义
	 */
	CUSTOM("edit.control.type.custom"),
	/**
	 * 人员部门树
	 */
	SELECT_TREE("edit.control.type.selectTree");
	public String name;
	public String code;
	EditControlType(String code){
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
