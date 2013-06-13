package com.norteksoft.tags.tree;



public class LeafPage  {
	
	private String name;//页签名字
	private String type;//页签树类型
	private String value;//隐藏域中的值
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
