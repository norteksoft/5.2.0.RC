package com.norteksoft.mms.form.enumeration;

/**
 * 表字段的类型
 */
public enum DataType {
	TEXT("文本"),
	DATE("日期"),
	TIME("时间"),
	INTEGER("整型"),
	LONG("长整型"),
	DOUBLE("双精度浮点数"),
	FLOAT("单精度浮点数"),
	BOOLEAN("布尔型"),
	CLOB("大文本"),
	BLOB("大字段"),
	COLLECTION("集合"),
	ENUM("枚举"),
	REFERENCE("引用"),
	@Deprecated
	AMOUNT("金额"),
	@Deprecated
	NUMBER("数字"),
	;
	public String code;
	DataType(String code){
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
