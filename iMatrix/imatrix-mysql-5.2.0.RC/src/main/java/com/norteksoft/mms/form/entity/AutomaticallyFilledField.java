package com.norteksoft.mms.form.entity;

import java.io.Serializable;

public class AutomaticallyFilledField implements  Serializable {
	
	private static final long serialVersionUID = 1L;
	
    public static String AUTO_FILLED_FILL_TYPE_COVERAGE = "field.fill.way.cover"; //覆盖
    public static String AUTO_FILLED_FILL_TYPE_ADDITIONAL = "field.fill.way.superaddition";//追加
    public static String AUTO_FILLED_FILL_TYPE_ADDED_TO_THE_BEGINNING = "field.fill.way.append";//添加

	private String name;//字段名
	private String value;//值
	private String fillType;//追加方式
	private String separate;//分隔符号
	
	public AutomaticallyFilledField(String name, String value, String fillType,String separate) {
		super();
		this.name = name;
		this.value = value;
		this.fillType = fillType;
		this.separate = separate;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getFillType() {
		return fillType;
	}
	public void setFillType(String fillType) {
		this.fillType = fillType;
	}

	public String getSeparate() {
		return separate;
	}

	public void setSeparate(String separate) {
		this.separate = separate;
	}

	@Override
	public String toString() {
		return "AutomaticallyFilledField [fillType=" + fillType + ", name="
				+ name + ", value=" + value + "]";
	}
}
