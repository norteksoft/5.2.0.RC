package com.norteksoft.bs.options.enumeration;

/**
 * 星期 
 * @author Administrator
 *
 */
public enum WeekEnum {
	first_1("bs.week.seventh"),
	second_2("bs.week.first"),
	third_3("bs.week.second"),
	fourth_4("bs.week.third"),
	fifth_5("bs.week.fourth"),
	sixth_6("bs.week.fifth"),
	seventh_7("bs.week.sixth");
	//last_L("bs.week.last");//最后一天
	
	public String code;
	WeekEnum(String code){
		this.code=code;
	}
	public String getCode() {
		return code;
	}

}
