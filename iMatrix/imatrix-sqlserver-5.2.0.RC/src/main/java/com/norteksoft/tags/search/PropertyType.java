package com.norteksoft.tags.search;

public enum PropertyType {
	
	STRING("type.string"),
	NUMBER("type.number"),
	INTEGER("type.integer"),
	LONG("type.long"),
	DOUBLE("type.double"),
	FLOAT("type.float"),
	BOOLEAN("type.boolean"),
	AMOUNT("type.amount"),
	DATE("type.date"),
	TIME("type.time"),
	ENUM("type.enum");
	
	private String code;
	
	PropertyType(String code){
		this.code = code;
	}
	
	public String getCode(){
		return this.code;
	}
}
