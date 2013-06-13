package com.norteksoft.mms.form.enumeration;
/**
 *列表 编辑时默认值设置
 * @author Administrator
 *
 */
public enum DefaultValue {
	/**
	 * 无
	 */
	CURRENT_NOTHING("default.value.current.nothing"),
	/**
	 * 当前日期
	 */
	CURRENT_DATE("default.value.current.date"),
	/**
	 * 当前时间
	 */
	CURRENT_TIME("default.value.current.time"),
	/**
	 * 当前用户名
	 */
	CURRENT_USER_NAME("default.value.current.user.name"),
	/**
	 * 当前登录名
	 */
	CURRENT_LOGIN_NAME("default.value.current.login.name");
	
	public String code;
	
	DefaultValue(String code){
		this.code=code;
	}
	
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	
	public String getCode(){
		return this.code;
	}
	
	/**
	 * 返回枚举的名称
	 * @return
	 */
	public String getEnumName(){
		return this.toString();
	}
}
