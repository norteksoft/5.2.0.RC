package com.norteksoft.wf.base.enumeration;

/**
 * 流程流转过程的中标准状态 值
 * @author X J
 *
 */
public enum ProcessState {
	/**
	 * 流程未提交
	 */
	UNSUBMIT("process.unsubmit"),
	
	/**
	 * 流程已提交
	 */
	SUBMIT("process.submit"),
	
	/**
	 * 流程已结束
	 */
	END("process.end"),
	
	/**
	 * 流程被取消及管理员中途结束了该流程
	 */
	MANUAL_END("process.manual.end"),
	/**
	 * 流程已暂停
	 */
	PAUSE("process.pause");
	
	private String code;
	
	ProcessState(String code){
		this.code = code;
	}

	public short getIndex(){
		return (short)(this.ordinal());
	}
	
	/**
	 * 返回该枚举值的名称的国际化资源key
	 * @return 国际化资源key
	 */
	public String getCode() {
		return code;
	}
	
	public static ProcessState valueOf(short ordinal){
		for(ProcessState ps:ProcessState.values()){
			if(ps.getIndex()==ordinal)return ps;
		}
		return UNSUBMIT;
	}
}
