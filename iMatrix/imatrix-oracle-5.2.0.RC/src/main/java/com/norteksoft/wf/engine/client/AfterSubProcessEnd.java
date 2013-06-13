package com.norteksoft.wf.engine.client;


/**
 * 子流程结束返回到父流程时将要调用的接口
 */
public interface AfterSubProcessEnd {
	/**
	 * 自动执行该方法
	 * @param dataId 当前正在走流程的实体id
	 */
	public void execute(Long dataId);
	
}
