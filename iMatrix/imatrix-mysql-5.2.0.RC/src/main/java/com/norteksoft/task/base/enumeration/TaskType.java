package com.norteksoft.task.base.enumeration;

public enum TaskType {
	DEFAULT_TYPE("默认类别"),
	WORKFLOW_NAME("流程名称"),
	CUSTOM_TYPE("自定义类别");
	
	private String name;
	
	TaskType(String name){
		this.name = name;
	}
	
	public String getCode(){
		return this.toString();
	}
	
	public String getName(){
		return name;
	}
}
