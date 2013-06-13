package com.norteksoft.product.api;

import java.util.List;

import com.norteksoft.product.api.entity.WorkflowAttachment;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.wf.engine.client.FormFlowable;

/**
 * 附件的api
 * @author wurong
 *
 */
public interface WorkflowAttachmentService {
	
	
	/**
	 * 保存附件，环节的办理模式为当前环节办理模式,且任务名为当前任务名
	 * @param attachment taskId是必须传的设置的参数，注意在修改附件时，设置附件id,即：document.setId(documentId);
	 */
	public void saveAttachment(WorkflowAttachment attachment );
	
	/**
	 * 保存附件，环节的办理模式为当前环节办理模式,且任务名为当前任务名
	 * @param attachment 
	 */
	public void saveAttachment(WorkflowAttachment attachment ,FormFlowable entity);
	
	/**
	 * 保存附件，环节的办理模式为当前环节办理模式,且任务名为当前任务名
	 * @param attachment 
	 */
	public void saveAttachment(WorkflowAttachment attachment ,String instanceId);
	/**
	 * 保存附件
	 * @param attachment 
	 */
	public void saveAttachment(WorkflowAttachment attachment ,Long taskId);
	
	/**
	 * 删除附件
	 * @param attachmentId 附件id
	 */
	public void deleteAttachment(Long attachmentId);
	
	/**
	 * 获得附件
	 * @param attachmentId
	 * @return 附件
	 */
	public WorkflowAttachment getAttachment(Long attachmentId) ;
	
	/**
	 * 获得某个任务的附件
	 * @param taskId 任务id
	 * @return 附件列表
	 */
	public List<WorkflowAttachment> getAttachments(Long taskId);
	
	/**
	 * 查询整个实例中的附件
	 * @param entity 业务实体
	 * @return  附件列表
	 */
	public List<WorkflowAttachment> getAttachments(FormFlowable entity);
	
	/**
	 * 查询整个实例中的附件
	 * @param entity 业务实体
	 * @return  附件列表
	 */
	public List<WorkflowAttachment> getAllAttachments(String instanceId);
	
	/**
	 * 查询整个实例中具体办理模式的附件
	 * @param entity 业务实体
	 * @param taskMode 任务办理方式
	 * @return  附件列表
	 */
	public List<WorkflowAttachment> getAttachments(FormFlowable entity,TaskProcessingMode taskMode);
	/**
	 * 查询整个实例中具体办理模式的附件
	 * @param entity 业务实体
	 * @param taskMode 任务办理方式
	 * @return  附件列表
	 */
	public List<WorkflowAttachment> getAllAttachments(String instanceId,TaskProcessingMode taskMode);
	
	/**
	 * 修改为 {@link #getAttachmentsExcludeTaskMode(FormFlowable,TaskProcessingMode) </code>getAttachmentsExcludeTaskMode<code>}
	 * @param entity 业务实体
	 * @param taskMode 任务办理方式
	 * @return  附件列表
	 */
	@Deprecated
	public List<WorkflowAttachment> getAttachmentsExceptTaskMode(FormFlowable entity,TaskProcessingMode taskMode);
	
	/**
	 * 查询整个实例中不是该办理模式的附件
	 * @param entity 业务实体
	 * @param taskMode 任务办理方式
	 * @return  附件列表
	 */
	public List<WorkflowAttachment> getAttachmentsExcludeTaskMode(FormFlowable entity,TaskProcessingMode taskMode);
	
	/**
	 * 查询整个实例中不是该办理模式的附件
	 * @param entity 业务实体
	 * @param taskMode 任务办理方式
	 * @return  附件列表
	 */
	public List<WorkflowAttachment> getAttachmentsExcludeTaskMode(String instanceId,TaskProcessingMode taskMode);
	
	
	/**
	 * 查询整个实例中具体环节的附件
	 * @param entity 业务实体
	 * @param taskName 任务名称
	 * @return  附件列表
	 */
	public List<WorkflowAttachment> getAttachments(FormFlowable entity,String taskName);
	/**
	 * 查询整个实例中具体环节的附件
	 * @param entity 业务实体
	 * @param taskName 任务名称
	 * @return  附件列表
	 */
	public List<WorkflowAttachment> getAllAttachments(String instanceId,String taskName);
	
	/**
	 * 修改为 {@link #getAttachmentsExcludeTaskName(FormFlowable,String) </code>getAttachmentsExcludeTaskName<code>}
	 * @param entity 业务实体
	 * @param taskName 任务名称
	 * @return  附件列表
	 */
	@Deprecated
	public List<WorkflowAttachment> getAttachmentsExceptTaskName(FormFlowable entity,String taskName);
	
	/**
	 * 查询整个实例中不是该环节的附件
	 * @param entity 业务实体
	 * @param taskName 任务名称
	 * @return  附件列表
	 */
	public List<WorkflowAttachment> getAttachmentsExcludeTaskName(FormFlowable entity,String taskName);
	/**
	 * 查询整个实例中不是该环节的附件
	 * @param entity 业务实体
	 * @param taskName 任务名称
	 * @return  附件列表
	 */
	public List<WorkflowAttachment> getAttachmentsExcludeTaskName(String instanceId,String taskName);
	
	/**
	 * 查询整个实例中“自定义类别”的正文
	 * @param entity 业务实体
	 * @param customField 自定义类别
	 * @return  附件列表
	 */
	public List<WorkflowAttachment> getAttachmentsByCustomField(FormFlowable entity,String customField);
	
	/**
	 * 查询整个实例中“自定义类别”的正文
	 * @param entity 业务实体
	 * @param customField 自定义类别
	 * @return  附件列表
	 */
	public List<WorkflowAttachment> getAllAttachmentsByCustomField(String instanceId,String customField);
	
	/**
	 * 修改为 {@link #getAttachmentsExcludeCustomField(FormFlowable,String) </code>getAttachmentsExcludeCustomField<code>}
	 * @param entity 业务实体
	 * @param customField 自定义类别
	 * @return  附件列表
	 */
	@Deprecated
	public List<WorkflowAttachment> getAttachmentsExceptCustomField(FormFlowable entity,String customField);
	
	/**
	 * 查询整个实例中不是“自定义类别”的正文
	 * @param entity 业务实体
	 * @param customField 自定义类别
	 * @return  附件列表
	 */
	public List<WorkflowAttachment> getAttachmentsExcludeCustomField(FormFlowable entity,String customField);
	/**
	 * 查询整个实例中不是“自定义类别”的正文
	 * @param entity 业务实体
	 * @param customField 自定义类别
	 * @return  附件列表
	 */
	public List<WorkflowAttachment> getAttachmentsExcludeCustomField(String instanceId,String customField);
	
	/**
	 * 流程还未启动时上传附件的权限
	 * @param workflowDefinitionName 流程定义名字
	 * @return 有权限返回true，否则返回false
	 */
	@Deprecated
	public boolean attachmentAddRightNotStarted(String workflowDefinitionCode);
	
	/**
	 * 流程还未启动时上传附件的权限
	 * @param workflowDefinitionId 流程定义id
	 * @return 有权限返回true，否则返回false
	 */
	@Deprecated
	public boolean attachmentAddRightNotStarted(Long workflowDefinitionId);
	
	/**
	 * 上传附件的权限
	 * @param taskId 任务id
	 * @return 有权限返回true，否则返回false
	 */
	@Deprecated
	public boolean attachmentAddRight(Long taskId);
	
	/**
	 * 删除附件的权限
	 * @param taskId 任务id
	 * @return 有权限返回true，否则返回false
	 */
	@Deprecated
	public boolean attachmentDeleteRight(Long taskId );
	
	/**
	 * 下载附件的权限
	 * @param taskId 任务id
	 * @return 有权限返回true，否则返回false
	 */
	@Deprecated
	public boolean attachmentDownloadRight(Long taskId );
}
