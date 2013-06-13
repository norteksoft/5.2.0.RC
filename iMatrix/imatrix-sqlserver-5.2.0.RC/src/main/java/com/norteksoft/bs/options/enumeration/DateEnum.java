package com.norteksoft.bs.options.enumeration;

/**
 * 日期
 * @author Administrator
 *
 */
public enum DateEnum {
	first_1("bs.first"),
	second_2("bs.second"),
	third_3("bs.third"),
	fourth_4("bs.fourth"),
	fifth_5("bs.fifth"),
	sixth_6("bs.sixth"),
	seventh_7("bs.seventh"),
	eighth_8("bs.eighth"),
	ninth_9("bs.ninth"),
	tenth_10("bs.tenth"),
	eleventh_11("bs.eleventh"),
	twelfth_12("bs.twelfth"),
	thirteenth_13("bs.thirteenth"),
	fourteenth_14("bs.fourteenth"),
	fifteenth_15("bs.fifteenth"),
	sixteenth_16("bs.sixteenth"),
	seventeenth_17("bs.seventeenth"),
	eighteenth_18("bs.eighteenth"),
	nineteenth_19("bs.nineteenth"),
	twentieth_20("bs.twentieth"),
	twentyFirst_21("bs.twenty-first"),
	twentySecond_22("bs.twenty-second"),
	twentyThird_23("bs.twenty-third"),
	twentyFourth_24("bs.twenty-fourth"),
	twentyFifth_25("bs.twenty-fifth"),
	twentySixth_26("bs.twenty-sixth"),
	twentySeventh_27("bs.twenty-seventh"),
	twentyEighth_28("bs.twenty-eighth"),
	twentyNinth_29("bs.twenty-ninth"),
	thirtieth_30("bs.thirtieth"),
	thirtyFirst_31("bs.thirty-first"),
	last_L("bs.last");//最后一天
	
	public String code;
	DateEnum(String code){
		this.code=code;
	}
	public String getCode() {
		return code;
	}

}
