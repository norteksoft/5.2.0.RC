package com.norteksoft.bs.rank.enumeration;
/**
 * 下级类型
 * @author Administrator
 *
 */
public enum SubordinateType {
	USER("人员"),
	DEPARTMENT("部门"),
	WORKGROUP("工作组");
	
	
	private String code;
	
	SubordinateType(String code){
		this.code = code;
	}
	
	public String getCode(){
		return this.code;
	}
	
	public int getIndex(){
		return this.ordinal();
	}
}
