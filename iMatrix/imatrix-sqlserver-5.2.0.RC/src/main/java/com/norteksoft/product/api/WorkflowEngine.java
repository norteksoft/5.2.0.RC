package com.norteksoft.product.api;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.product.util.ContextUtils;

/**
 *用于获得工作流引擎所需的api
 * @author wurong
 */
@Deprecated
@Service
@Transactional
public class WorkflowEngine {

	/**
	 * 获得工作流实例api
	 */
	public WorkflowInstanceService getInstanceService(){
		return (WorkflowInstanceService) ContextUtils.getBean("workflowClientManager");
	}
	
	/**
	 * 获得工作流定义api
	 */
	public WorkflowDefinitionService getDefinitionService(){
		return (WorkflowDefinitionService)ContextUtils.getBean("workflowClientManager");
	}
	
	/**
	 * 获得任务api
	 */
	public WorkflowTaskService getTaskService(){
		return (WorkflowTaskService)ContextUtils.getBean("workflowClientManager");
	}
	
	/**
	 * 获得表单api
	 */
	public WorkflowFormService getFormService(){
		return (WorkflowFormService)ContextUtils.getBean("workflowClientManager");
	}
	
	/**
	 * 获得流转历史api
	 */
	public WorkflowHistoryService getHistoryService(){
		return (WorkflowHistoryService)ContextUtils.getBean("workflowClientManager");
	}
	
	/**
	 * 获得附件api
	 */
	public WorkflowAttachmentService getAttachmentService(){
		return (WorkflowAttachmentService)ContextUtils.getBean("workflowClientManager");	
	}
	
	/**
	 * 获得正文api
	 */
	public WorkflowDocumentService getDocumentService(){
		return (WorkflowDocumentService)ContextUtils.getBean("workflowClientManager");
	}
	
	/**
	 * 获得意见api
	 */
	public WorkflowOpinionService getOpinionService(){
		return (WorkflowOpinionService)ContextUtils.getBean("workflowClientManager");
	}
	
	/**
	 * 获取数据字典api
	 * @return
	 */
	public WorkflowDataDictService getDataDictService(){
		return (WorkflowDataDictService)ContextUtils.getBean("workflowClientManager");
	}
	
	/**
	 * 获取权限api
	 * @return
	 */
	public WorkflowRightService getRightService(){
		return (WorkflowRightService)ContextUtils.getBean("workflowClientManager");
	}
}
