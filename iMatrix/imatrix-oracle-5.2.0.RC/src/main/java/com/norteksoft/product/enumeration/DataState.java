package com.norteksoft.product.enumeration;

/**
 * 表单的状态
 * @author 
 *
 */
public enum DataState {
	/**
	 * 启用
	 */
	ENABLE("view.state.enable"),
	/**
	 * 禁用
	 */
	DISABLE("view.state.disable"),
	/**
	 * 草稿
	 */
	DRAFT("view.state.draft");
	public String code;
	DataState(String code){
		this.code=code;
	}
	public int getIndex(){
		return this.ordinal();
	}
	public String getCode() {
		return code;
	}
}