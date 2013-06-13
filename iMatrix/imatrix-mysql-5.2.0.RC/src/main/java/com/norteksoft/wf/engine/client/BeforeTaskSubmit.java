package com.norteksoft.wf.engine.client;

import com.norteksoft.task.base.enumeration.TaskProcessingResult;


/**
 * 任务执行前，将首先调用该接口
 */
public interface BeforeTaskSubmit {

	/**
	 * 任务执行前，将首先执行该方法
	 * @param dataId 当前正在走流程的实体的id
	 * @param transact 当前办理人执行的操作
	 * @return 是否继续完成任务，如果返回true ，将继续执行；否则，该任务将不会执行完成
	 */
	public boolean execute(Long dataId,TaskProcessingResult transact);
	
}
