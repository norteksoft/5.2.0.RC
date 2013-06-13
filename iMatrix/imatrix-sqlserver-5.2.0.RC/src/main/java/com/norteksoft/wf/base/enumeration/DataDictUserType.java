package com.norteksoft.wf.base.enumeration;

public enum DataDictUserType {
	
	USER("人员"),
	DEPARTMENT("部门"),
	WORKGROUP("工作组"),
	RANK("上下级关系");
	
	private String name;
	
	DataDictUserType(String name){
		this.name = name;
	}
	
	public Integer getCode(){
		return this.ordinal();
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getName(Integer code){
		for(DataDictUserType type : DataDictUserType.values()){
			if(type.ordinal() == code)
				return type.name;
		}
		return null;
	}

}
