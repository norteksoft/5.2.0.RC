package com.norteksoft.product.api;

import com.norteksoft.wf.base.exception.NotFoundEnabledWorkflowDefinitionException;
import com.norteksoft.wf.engine.client.FormFlowable;

/**
 * 任务权限api
 * @author wurong
 * @deprecated  替换为{@com.norteksoft.product.api.WorkflowPermissionService}
 */
public interface WorkflowRightService {

	/**
	 * 返回查看会签结果的权限
	 */
	public boolean viewMeetingResultRight(Long taskId );
	
	/**
	 * 返回查看投票结果的权限
	 */
	public boolean viewVoteResultRight(Long taskId );
	
	/**
	 * 返回当前用户编辑意见的权限
	 * @param taskId 任务id
	 */
	public boolean editOpinion( Long taskId );
	
	/**
	 * 流程还未启动时编辑意见的权限
	 * @param definitionId 流程定义id
	 */
	public boolean editOpinionNotStarted(Long definitionId );
	
	/**
	 * 流程还未启动时编辑意见的权限
	 * @param definitionCode 流程定义编号
	 */
	public boolean editOpinionNotStarted(String definitionCode );
	
	/**
	 * 返回意见是否必填
	 * @param taskId 任务id
	 */
	public boolean mustOpinion(Long taskId );
	
	/**
	 * 流程还未启动时意见是否必填
	 * @param definitionCode 流程定义编号
	 */
	public boolean mustOpinionNotStarted(String definitionCode);
	
	/**
	 * 流程还未启动时意见是否必填
	 * @param definitionId 流程定义id
	 */
	public boolean mustOpinionNotStarted(Long definitionId);
	
	 /**
	 * 在环节办理时，当前环节办理人是否有权删除流程实例
	 * @param entity 业务实体
	 * @param taskName 当前环节名称
	 * @return true 为可以，false为不可以
	 */
	public boolean canDeleteInstanceInTask(FormFlowable entity, String taskName);
	
	/**
	 * 返回流转历史的查看权限
	 * @param taskId 任务id
	 */
	public boolean historyAuthorization(Long taskId );
	
	/**
	 * 返回表单打印权限
	 */
	public boolean formPrintRight(Long taskId);
	
	/**
	 * 返回表单打印权限
	 */
	public boolean formPrintRightNotStarted(Long definitionId);
	
	/**
	 * 返回表单打印权限
	 * @exception NotFoundEnabledWorkflowDefinitionException if not found started workflowDefinition by workflowDefinitionName
	 */
	public boolean formPrintRightNotStarted(String workflowDefinitionName);
	
	/**
	 * 返回环节办理人是否具有创建正文的权限 
	 * @param taskId
	 * 
	 */
	public boolean officialTextCreateRight(Long taskId);
	
	/**
	 * 流程还未启动时创建正文的权限
	 * @param workflowDefinitionName 流程定义名字
	 * @return 有权限返回true，否则返回false
	 */
	public boolean officialTextCreateRightNotStarted(String workflowDefinitionCode);

	/**
	 * 流程还未启动时创建正文的权限
	 * @param workflowDefinitionId 流程定义id
	 * @return 有权限返回true，否则返回false
	 */
	public boolean officialTextCreateRightNotStarted(Long workflowDefinitionId);
	
	/**
	 * 返回环节办理人是否具有删除正文的权限 
	 * @param taskId
	 * @return
	 */
	public boolean officialTextDeleteRight(Long taskId);
	
	/**
	 * 返回环节办理人是否具有编辑正文的权限 
	 * @param taskId
	 */
	public boolean officialTextEditRight(Long taskId);
	/**
	 * 返回环节办理人是否具有下载正文的权限 
	 * @param taskId
	 */
	public boolean officialTextDownloadRight(Long taskId);
	/**
	 * 返回环节办理人是否具有打印正文的权限 
	 * @param taskId
	 */
	public boolean officialTextPrintRight(Long taskId);
	/**
	 * 返回环节办理人的正文权限,正文控件规定的形式(-1,0,1,1,0,0,1,1)
	 * @param taskId
	 * @return
	 */
	public String officialTextRights(Long taskId);
	
	/**
	 * 上传附件的权限
	 * @param taskId 任务id
	 * @return 有权限返回true，否则返回false
	 */
	public boolean attachmentAddRight(Long taskId);
	
	/**
	 * 流程还未启动时上传附件的权限
	 * @param workflowDefinitionName 流程定义名字
	 * @return 有权限返回true，否则返回false
	 */
	public boolean attachmentAddRightNotStarted(String workflowDefinitionCode);
	
	/**
	 * 流程还未启动时上传附件的权限
	 * @param workflowDefinitionId 流程定义id
	 * @return 有权限返回true，否则返回false
	 */
	public boolean attachmentAddRightNotStarted(Long workflowDefinitionId);
	
	/**
	 * 删除附件的权限
	 * @param taskId 任务id
	 * @return 有权限返回true，否则返回false
	 */
	public boolean attachmentDeleteRight(Long taskId );
	
	/**
	 * 下载附件的权限
	 * @param taskId 任务id
	 * @return 有权限返回true，否则返回false
	 */
	public boolean attachmentDownloadRight(Long taskId );
}
