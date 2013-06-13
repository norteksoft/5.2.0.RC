package com.norteksoft.tags.tree;



public class ZtreeLeafPage  {
	
	private String name;//页签名字
	private String type;//页签树类型
	private String hiddenValue;//隐藏域中的值
	private String showValue;//隐藏域中的值
	
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
	public String getHiddenValue() {
		return hiddenValue;
	}
	public void setHiddenValue(String hiddenValue) {
		this.hiddenValue = hiddenValue;
	}
	public String getShowValue() {
		return showValue;
	}
	public void setShowValue(String showValue) {
		this.showValue = showValue;
	}
}
