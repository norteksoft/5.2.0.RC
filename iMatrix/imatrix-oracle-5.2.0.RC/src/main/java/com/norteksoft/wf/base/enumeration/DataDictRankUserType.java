package com.norteksoft.wf.base.enumeration;

public enum DataDictRankUserType {
	USER("人员"),
	DEPARTMENT("部门"),
	WORKGROUP("工作组");
	
	
	private String name;
	
	DataDictRankUserType(String name){
		this.name = name;
	}
	
	public Integer getCode(){
		return this.ordinal();
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getName(Integer code){
		for(DataDictRankUserType type : DataDictRankUserType.values()){
			if(type.ordinal() == code)
				return type.name;
		}
		return null;
	}
}
