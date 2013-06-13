package com.norteksoft.wf.engine.client;


/**
 * 流程正常结束接口
 */
public interface EndInstanceInterface {
	/**
	 * 工作流调用，流程正常结束时调用
	 * 该接口的实现者应该完成自己的一些业务补偿操作，如改变表单状态等
	 * @param entityId 当前正在走流程的实体id
	 */
	public void endInstanceExecute(Long entityId);
	
}
