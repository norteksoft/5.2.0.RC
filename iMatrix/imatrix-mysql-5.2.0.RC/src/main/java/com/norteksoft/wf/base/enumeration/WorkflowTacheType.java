package com.norteksoft.wf.base.enumeration;


/**
 * 工作流环节类型
 * @author wurong
 *
 */
public enum WorkflowTacheType {
	
	/**
	 * 抄送环节
	 */
	COPY_TACHE("copy-tache","抄送环节"),
	/**
	 * 自动环节
	 */
	AUTO_TACHE("auto-tache","自动环节"),
	/**
	 * 人工环节
	 */
	CHOICE_TACHE("choice-tache","选择环节");	
	
	private String code;
	private String description;
	WorkflowTacheType(String code,String description){
		this.code=code;
		this.description = description;
	}
	public int getIndex(){
		return this.ordinal();
	}
	public String getCode(){
		return this.code;
	}
	public String getDescription() {
		return description;
	}
}
