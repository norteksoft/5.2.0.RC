package com.norteksoft.wf.engine.client;

import com.norteksoft.task.base.enumeration.TaskProcessingResult;

/**
 * 任务完成后将要调用的接口
 */
public interface AfterTaskCompleted {
	/**
	 * 任务执行结束后将调用该方法
	 * @param dataId 当前正在走流程的实体id
	 * @param transact 办理任务时执行的操作 
	 */
	public void execute(Long dataId,TaskProcessingResult transact);
	
}
