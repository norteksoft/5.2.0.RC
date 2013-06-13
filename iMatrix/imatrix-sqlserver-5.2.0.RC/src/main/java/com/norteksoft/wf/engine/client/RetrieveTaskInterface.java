package com.norteksoft.wf.engine.client;


/**
 * 取回任务接口
 */
public interface RetrieveTaskInterface {
	/**
	 * 工作流调用，取回任务时调用
	 * 该接口的实现者应该完成自己的一些业务补偿操作，如改变表单状态等
	 * @param entityId 当前正在走流程的实体id
	 * @param taskId 当前取回的任务id
	 */
	public void retrieveTaskExecute(Long entityId,Long taskId);
	
}
