package com.norteksoft.wf.base.enumeration;

public enum TextPerimssion {

	READ("查看"),
	DOWNLOAD("下载"),
	EDIT("编辑"),
	DELETE("删除");
	
	private String name;
	
	TextPerimssion(String name){
		this.name = name;
	}
	
	public Integer getCode(){
		return this.ordinal();
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getName(Integer code){
		for(TextPerimssion type : TextPerimssion.values()){
			if(type.ordinal() == code)
				return type.name;
		}
		return null;
	}
}
