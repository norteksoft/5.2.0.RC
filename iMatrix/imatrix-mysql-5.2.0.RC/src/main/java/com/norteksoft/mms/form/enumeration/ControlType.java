package com.norteksoft.mms.form.enumeration;

/**
 * 控件的类型
 * @author wurong
 *
 */
public enum ControlType {
	/**
	 * 文本框
	 */
	TEXT("文本框"),
	/**
	 * 隐藏域
	 */
	HIDDEN("隐藏域"),
	/**
	 * 密码框
	 */
	PASSWORD("密码框"),
	/**
	 * 单选框
	 */
	RADIO("单选框"),
	/**
	 * 复选框
	 */
	CHECKBOX("复选框"),
	/**
	 * 下拉框
	 */
	SELECT("下拉框"),
	/**
	 * 按钮控件
	 */
	BUTTON("按钮"),
	/**
	 * 文本域
	 */
	TEXTAREA("文本域"),
	/**
	 * 日期时间控件
	 */
	TIME("日期时间控件"),
	/**
	 * 部门人员控件
	 */
	SELECT_MAN_DEPT("部门人员控件"),
	/**
	 * 计算控件
	 */
	CALCULATE_COMPONENT("计算控件"),
	/**
	 *下拉菜单控件
	 */
	PULLDOWNMENU("下拉菜单控件"),
	/**
	 * 数据选择控件
	 */
	DATA_SELECTION("数据选择控件"),
	/**
	 * 数据获取控件
	 */
	DATA_ACQUISITION("数据获取控件"),
	/**
	 * 紧急程度设置控件
	 */
	URGENCY("紧急程度设置控件"),
	/**
	 * 特事特办控件
	 */
	CREATE_SPECIAL_TASK("特事特办控件"),
	/**
	 * 特事特办人员选择
	 */
	SPECIAL_TASK_TRANSACTOR("特事特办人员选择"),
	/**
	 * 自定义列表控件
	 */
	LIST_CONTROL("自定义列表控件"),
	/**
	 * 标准列表控件
	 */
	STANDARD_LIST_CONTROL("标准列表控件"),
	/**
	 * 标签控件
	 */
	LABEL("标签")
	;
	public String code;
	ControlType(String code){
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
	public String getEnumName(){
		return this.toString();
	}

	
}
