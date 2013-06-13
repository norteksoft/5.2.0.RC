package com.norteksoft.wf.base.enumeration;

public enum TransactorPermission {
	
	READ_SUBORDINATE("阅示"),
	READ("阅"),
	READ_TRANSACT("阅办"),
	TEAMWORKA("协办"),
	SIGNA("批示"),
	SIGNB("签发"),
	HSIGNB("会签");
	
	private String name;
	
	TransactorPermission(String name){
		this.name = name;
	}
	
	public Integer getCode(){
		return this.ordinal();
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getName(Integer code){
		for(TransactorPermission type : TransactorPermission.values()){
			if(type.ordinal() == code)
				return type.name;
		}
		return null;
	}
}
