package com.norteksoft.product.web.wf.impl;

import com.norteksoft.wf.engine.client.FormFlowable;
/**
 * 工作流Action接口
 * @author qiao
 * @param <T>
 */
public interface WorkflowAction<T extends FormFlowable> {
	
	
	/**
	 * 启动并提交流程
	 * @return
	 * @throws Exception
	 */
	public String submitProcess();
	
	/**
	 * 完成任务
	 * @return
	 * @throws Exception
	 */
	public String completeTask();

	/**
	 * 完成交互任务：用于选人、选环节、填意见
	 * @return
	 */
	public String completeInteractiveTask();
	
	/**
	 * 取回任务
	 * @return
	 * @throws Exception
	 */
	public String retrieveTask();
	
	/**
	 * 减签
	 * @return
	 */
	public String removeSigner();
	
	/**
	 * 加签
	 * @return
	 */
	public String addSigner();
	
	/**
	 * 显示流转历史
	 * @return
	 */
	public  String showHistory();
	
	/**
	 * 填写意见
	 * @return
	 */
	public String fillOpinion();
	
	/**
	 * 流程监控中应急处理功能
	 */
	public String processEmergency();
	
	/**
	 * 领取任务
	 * @return
	 */
	public String drawTask();
	
}
