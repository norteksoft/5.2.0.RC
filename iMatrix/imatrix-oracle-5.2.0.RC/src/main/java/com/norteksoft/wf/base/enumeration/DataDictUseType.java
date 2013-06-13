package com.norteksoft.wf.base.enumeration;

public enum DataDictUseType {
	
	SET_TRANSACTOR("设置办理人"),
	SET_PERMISSION_TEXT("设置附件权限");
	
	private String name;
	
	DataDictUseType(String name){
		this.name = name;
	}
	
	public Integer getCode(){
		return this.ordinal();
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getName(Integer code){
		for(DataDictUseType type : DataDictUseType.values()){
			if(type.ordinal() == code)
				return type.name;
		}
		return null;
	}
}
