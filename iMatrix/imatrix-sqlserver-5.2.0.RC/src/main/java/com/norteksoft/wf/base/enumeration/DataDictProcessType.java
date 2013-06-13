package com.norteksoft.wf.base.enumeration;

public enum DataDictProcessType {
	COMMON("通用"),
	SELECT("选择");
	
	private String name;
	
	DataDictProcessType(String name){
		this.name = name;
	}
	
	public Integer getCode(){
		return this.ordinal();
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getName(Integer code){
		for(DataDictProcessType type : DataDictProcessType.values()){
			if(type.ordinal() == code)
				return type.name;
		}
		return null;
	}

}
