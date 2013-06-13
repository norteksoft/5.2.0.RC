package com.norteksoft.wf.engine.client;

import java.util.Collection;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Opinion;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.wf.base.enumeration.CompleteTaskTipType;

public abstract class WorkflowBaseManager<T extends FormFlowable> implements FormFlowableDeleteInterface{

	
	/**
	 * 删除实体
	 */
	public abstract void deleteEntity(T entity);
	
	/**
	 * 保存实体
	 */
	public abstract void saveEntity(T entity);
	
	/**
	 * 根据ID查询实体
	 */
	public abstract T getEntity(Long id);
	
	/**
	 * 删除流程实例时删除实体
	 */
	public void deleteFormFlowable(Long dataId){
		deleteEntity(getEntity(dataId));
	}

	/**
	 * 第一次保存实体，并发起流程
	 */
	public void startWorkflow(T entity, Long wfDefinitionId){
		saveEntity(entity);
		ApiFactory.getInstanceService().startInstance(wfDefinitionId, entity);
	}
	
	/**
	 * 第一次保存实体，发起流程(如果需要)，并提交第一环节任务
	 */
	public CompleteTaskTipType submit(T entity, Long wfDefinitionId){
		return ApiFactory.getInstanceService().submitInstance(wfDefinitionId, entity);
	}
	
	/**
	 * 根据任务ID查询关联的实体
	 */
	public T getEntityByTask(Long taskId){
		if(taskId==null) return null;
		Long entityId = ApiFactory.getFormService().getFormFlowableIdByTask(taskId);
		return getEntity(entityId);
	}
	
	/**
	 * 提交任务
	 */
	public CompleteTaskTipType submitTask(T entity, Long taskId, TaskProcessingResult result){
		saveEntity(entity);
		return ApiFactory.getTaskService().completeWorkflowTask(taskId, result);
	}
	
	/**
	 * 带下一环节办理人    提交任务
	 */
	public CompleteTaskTipType submitTask(T entity, Long taskId, TaskProcessingResult result, Collection<String> users){
		saveEntity(entity);
		return ApiFactory.getTaskService().completeInteractiveWorkflowTask(taskId, users, "");
	}
	
	/**
	 * 加签
	 */
	public void countersign(Long taskId, Collection<String> users){
		ApiFactory.getTaskService().addSigner(taskId, users);
	}
	
	/**
	 * 根据工作流ID获取第一环节的字段编辑权限
	 * @param workflowId
	 * @return
	 */
	public String getFieldPermissionOfFirstTask(Long workflowId){
		if(workflowId==null) return getFieldPermission(false);
		return ApiFactory.getFormService().getFieldPermissionNotStarted(workflowId);
	}
	
	/**
	 * 根据任务ID获取表单的字段编辑权限
	 * @param taskId
	 * @return
	 */
	public String getFieldPermission(Long taskId){
		if(taskId==null) return getFieldPermission(false);
		return ApiFactory.getFormService().getFieldPermission(taskId);
	}
	
	/*
	 * 字段编辑权限
	 * @param editable 为true，所有字段可以编辑，为false，所有字段禁止编辑
	 * @return
	 */
	private String getFieldPermission(boolean editable){
		return ApiFactory.getFormService().getFieldPermission(editable);
	}
	
	/**
	 * 根据任务ID取回任务，返回取回结果[成功或失败原因]
	 */
	public String retrieve(Long taskId){
		return ApiFactory.getTaskService().retrieve(taskId);
	}
	
	/**
	 * 保存用户办理意见
	 */
	public void save(Opinion opinion){
		ApiFactory.getOpinionService().saveOpinion(opinion);
	}
	
}
