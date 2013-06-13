package com.norteksoft.portal.base.enumeration;

public enum ControlType {

	PT_TEXT("parameter.type.text"),//文本框
	PT_SELECT("parameter.type.select");//下拉框
	//PT_CHECKBOX("parameter.type.checkbox"),//多选框
	//PT_RADIO("parameter.type.radio");//单选框
	
	
	String code;
	ControlType(String code){
        this.code = code;
    }
	public String getCode(){
		return this.code;
	}

    @Override
    public String toString() {
        return this.code;
    }
}
