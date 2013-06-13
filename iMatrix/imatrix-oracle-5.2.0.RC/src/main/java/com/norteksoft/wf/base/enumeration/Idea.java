package com.norteksoft.wf.base.enumeration;

public enum Idea {
	
	EGREE("同意"),
	DISAGREE("不同意"),
	INVALID("弃权");
    
    String condition;
    Idea(String condition){
        this.condition = condition;
    }

	@Override
    public String toString() {
        return this.condition;
    }
}
