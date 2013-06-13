package com.norteksoft.acs.base.enumeration;


public enum LoginFailSetType {
	/**
	 * 验证码方式
	 */
	VALIDATE_CODE("显示验证码"),
	/**
	 *锁定用户方式
	 */
	LOCK_USER("锁定用户");
	
	public String name;
	LoginFailSetType(String name){
		this.name=name;
	}
	public Integer getIndex(){
		return this.ordinal();
	}
	public String getName(){
		return this.name;
	}
	public String getName(Integer index){
		for(LoginFailSetType type : LoginFailSetType.values()){
			if(type.ordinal() == index)
				return type.name;
		}
		return null;
	}
}
