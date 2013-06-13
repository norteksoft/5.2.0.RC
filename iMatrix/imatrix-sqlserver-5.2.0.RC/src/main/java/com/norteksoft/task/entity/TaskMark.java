package com.norteksoft.task.entity;


public enum TaskMark {
	RED("red"),
	BLUE("blue"),
	YELLOW("yellow"),
	GREEN("green"),
	ORANGE("orange"),
	PURPLE("purple"),
	CANCEL("white");
	
	private String name;
	
	TaskMark(String name){
		this.name = name;
	}
	
	public int getCode(){
		return this.ordinal();
	}
	
	public String getName(){
		return name;
	}
	
	public static TaskMark valueOf(int ordinal){
		for(TaskMark mark:TaskMark.values()){
			if(mark.getCode()==ordinal) return mark;
		}
		return CANCEL;
	}
}
