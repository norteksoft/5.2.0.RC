package com.norteksoft.bs.options.enumeration;

/**
 * 定时类型 
 * @author Administrator
 *
 */
public enum TimingType {
	everyDate("bs.everyData"),//每天
	everyWeek("bs.everyWeek"),//每周
	everyMonth("bs.everyMonth"),//每月
	appointTime("bs.appointTime"),//指定时间
	appointSet("bs.appointSet");//高级设置
	
	public String code;
	TimingType(String code){
		this.code=code;
	}
	public String getCode() {
		return code;
	}

}
