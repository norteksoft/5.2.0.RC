package com.norteksoft.wf.engine.client;

import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.engine.entity.WorkflowInstance;


/**
 * 任务退回接口
 */
public interface ReturnTaskInterface {
	/**
	 * 退回任务
	 * @param instance 当前实例
	 * @param task   当前任务
	 * @param backTo 退回的任务
	 */
	public void goback(WorkflowInstance instance,WorkflowTask task,WorkflowTask backTo);
	
}
