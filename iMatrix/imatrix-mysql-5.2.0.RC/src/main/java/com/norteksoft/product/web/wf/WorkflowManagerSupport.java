package com.norteksoft.product.web.wf;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Document;
import com.norteksoft.product.api.entity.Opinion;
import com.norteksoft.product.api.entity.TaskPermission;
import com.norteksoft.product.api.entity.WorkflowAttachment;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.product.api.entity.WorkflowTask;
import com.norteksoft.wf.base.enumeration.CompleteTaskTipType;
import com.norteksoft.wf.base.exception.NotFoundEnabledWorkflowDefinitionException;
import com.norteksoft.wf.engine.client.FormFlowable;

/**
 * 工作流manager基类
 * @author qiao
 * @param <T>
 */
@Transactional
public abstract class WorkflowManagerSupport<T extends FormFlowable> {

	/**
	 * 保存并初始化流程
	 * @param definitionCode 流程编号
	 * @param t
	 */
	public void saveInstance(String definitionCode, T t) {
		saveEntity(t);
		ApiFactory.getInstanceService().startInstance(definitionCode, t);
	}
	
	/**
	 * 保存并初始化流程
	 * @param definitionCode 流程编号
	 * @param definitionVersion 版本号
	 * @param t
	 */
	public void saveInstance(String definitionCode,Integer definitionVersion, T t) {
		saveEntity(t);
		ApiFactory.getInstanceService().startInstance(definitionCode, definitionVersion, t);
	}
	
	/**
	 * 保存并初始化流程
	 * @param definitionId 流程定义id
	 * @param t
	 */
	public void saveInstance(Long definitionId, T t) {
		saveEntity(t);
		ApiFactory.getInstanceService().startInstance(definitionId, t);
	}
	
	/**
	 *  第一次提交流程
	 * @param opinion
	 * @param definitionCode
	 * @param definitionVersion
	 * @param t
	 */
	public CompleteTaskTipType submitProcess(String opinion,String definitionCode,Integer definitionVersion, T t) {
		saveEntity(t);
		CompleteTaskTipType type = ApiFactory.getInstanceService().submitInstance(definitionCode, definitionVersion, t);
		
		//保存意见
		Long taskId= t.getWorkflowInfo().getFirstTaskId();
		saveOpinion(taskId,opinion);
		return type;
	}
	
	/**
	 * 第一次提交流程
	 * @param expenseReport
	 * @param opinion
	 * @param workFlowCode
	 */
	public CompleteTaskTipType submitProcess(T t,String opinion,String workFlowCode) {
		saveEntity(t);
		CompleteTaskTipType type = ApiFactory.getInstanceService().submitInstance(workFlowCode, t);
		
		//保存意见
		Long taskId= t.getWorkflowInfo().getFirstTaskId();
		saveOpinion(taskId,opinion);
		return type;
	}
	
	/**
	 * 第一次提交流程
	 * @param t
	 * @param opinion
	 * @param definitionId
	 */
	public CompleteTaskTipType submitProcess(T t,String opinion,Long definitionId) {
		saveEntity(t);
		CompleteTaskTipType type = ApiFactory.getInstanceService().submitInstance(definitionId, t);
		//保存意见
		Long taskId= t.getWorkflowInfo().getFirstTaskId();
		saveOpinion(taskId,opinion);
		return type;
	}
	
	/**
	 * 办理任务
	 * @param expenseReport
	 * @param taskId
	 * @param taskTransact
	 * @return
	 */
	public CompleteTaskTipType completeTask(T t, Long taskId,TaskProcessingResult result) {
		saveEntity(t);
		CompleteTaskTipType type= ApiFactory.getTaskService().completeWorkflowTask(taskId,result);
		return type;
	}
	
	/**
	 * 完成任务
	 * @param taskId
	 * @param lists
	 * @param opinion 
	 */
	public CompleteTaskTipType distributeTask(Long taskId, List<String> lists, String opinion) {
		saveOpinion(taskId,opinion);
		return ApiFactory.getTaskService().completeInteractiveWorkflowTask(taskId, lists, null);
	}
	
	/**
	 * 完成任务
	 * @param taskId
	 * @param transitionName
	 */
	public CompleteTaskTipType distributeTask(Long taskId, String transitionName,String opinion) {
		saveOpinion(taskId,opinion);
		return ApiFactory.getTaskService().selectActivity(taskId, transitionName);
	}
	
	/**
	 * 保存意见
	 * @param taskId
	 * @param opinion
	 */
	public void saveOpinion(Long taskId, String opinion) {
		if(StringUtils.isNotEmpty(opinion)){
			Opinion ap=new Opinion(opinion, new Date(), taskId, "");
			ApiFactory.getOpinionService().saveOpinion(ap);
		}
	}
		
	/**
	 * 根据taskId获取所有字段权限
	 * @param taskId
	 * @param workFlowCode
	 * @return
	 */
	public String getFieldPermissionByTaskId(Long taskId) {
		WorkflowTask task = ApiFactory.getTaskService().getTask(taskId);
		if (isTaskCompleted(task)) {
			return ApiFactory.getFormService().getFieldPermission(false);
		} else {
			return ApiFactory.getFormService().getFieldPermission(taskId);
		}
	}
	
	/**
	 * 获得任务是否已完成，已取消，已指派，他人已领取状态
	 * 
	 * @param task
	 * @return
	 */
	private boolean isTaskCompleted(WorkflowTask task) {
		return task.getActive().equals(2) || task.getActive().equals(3)
		|| task.getActive().equals(5) || task.getActive().equals(7);
	}
	
	/**
	 * 获取所有字段权限
	 * @param workFlowCode
	 * @param definitionVersion
	 * @return
	 */
	public String getFieldPermissionByCodeAndVersion(String workFlowCode,Integer definitionVersion) {
		com.norteksoft.product.api.entity.WorkflowDefinition definition = ApiFactory.getDefinitionService().getWorkflowDefinitionByCodeAndVersion(workFlowCode,definitionVersion);
		if(definition==null) throw new NotFoundEnabledWorkflowDefinitionException("not found started workflowDefinition by workFlowCode,definitionVersion:"+workFlowCode+","+definitionVersion);
		Long processId = ApiFactory.getDefinitionService().getWorkflowDefinitionByCodeAndVersion(workFlowCode, definitionVersion).getId();
		return ApiFactory.getFormService().getFieldPermissionNotStarted(processId);
	}
	
	/**
	 * 获取所有字段权限
	 * @param workflowDefinitionId
	 * @return
	 */
	public String getFieldPermission(Long workflowDefinitionId) {
		com.norteksoft.product.api.entity.WorkflowDefinition definition = ApiFactory.getDefinitionService().getWorkflowDefinition(workflowDefinitionId);
		if(definition==null) throw new NotFoundEnabledWorkflowDefinitionException("not found started workflowDefinition by workflowDefinitionId:"+workflowDefinitionId);
		Long processId = ApiFactory.getDefinitionService().getWorkflowDefinition(workflowDefinitionId).getId();
		return ApiFactory.getFormService().getFieldPermissionNotStarted(processId);
	}
	
	/**
	 * 获取所有字段权限，没有流程版本是，默认取流程最高版本的权限
	 * @param defCode 流程编号
	 * @return
	 */
	public String getFieldPermission(String defCode) {
		List<com.norteksoft.product.api.entity.WorkflowDefinition> definitions = ApiFactory.getDefinitionService().getWorkflowDefinitionsByCode(defCode);
		if(definitions.size()<1) throw new NotFoundEnabledWorkflowDefinitionException("not found started workflowDefinition by code:"+defCode);
		Long processId = ApiFactory.getDefinitionService().getWorkflowDefinitionsByCode(defCode).get(0).getId();
		return ApiFactory.getFormService().getFieldPermissionNotStarted(processId);
	}
	
	/**
	 * 办理人正文的所有权限
	 * @return
	 */
	public String getDocumentRight(Long taskId){
		if(taskId!=null){
			return ApiFactory.getPermissionService().getDocumentPermission(taskId);
		}
		return null;
	}
	
	
	/**
	 * 根据流程定义编号取任务的权限
	 * @param definitionCode
	 * @return
	 */
	public TaskPermission getActivityPermission(String definitionCode){
			return ApiFactory.getPermissionService().getActivityPermission(definitionCode);
	}
	
	/**
	 * 根据流程定义编号取任务的权限
	 * @param definitionCode
	 * @param definitionVersion
	 * @return
	 */
	public TaskPermission getActivityPermission(String definitionCode,Integer definitionVersion){
			return ApiFactory.getPermissionService().getActivityPermission(definitionCode, definitionVersion);
	}
	
	/**
	 * 根据流程定义编号取任务的权限
	 * @param definitionCode
	 * @param definitionVersion
	 * @return
	 */
	public TaskPermission getActivityPermission(Long taskId){
			return ApiFactory.getPermissionService().getActivityPermission(taskId);
	}
	
	/**
	 * 根据实体和版本号自动填写实体字段(流程发起前)
	 * @param t
	 * @param wfDefinationCode
	 * @param version
	 */
	public void fillEntityByDefinition(T t,String wfDefinationCode,Integer version){
			ApiFactory.getFormService().fillEntityByDefinition(t, wfDefinationCode, version);
	}
	
	/**
	 * 根据实体和taskId自动填写实体字段(办理任务页面)
	 * @param t
	 * @param taskId
	 */
	public void fillEntityByDefinition(T t,Long taskId){
		ApiFactory.getFormService().fillEntityByTask(t, taskId);
}
	
	/**
	 * 根据taskID取实体
	 * @param taskId
	 * @return
	 */
	public T getEntityByTaskId(Long taskId) {
		if(taskId==null)return null;
		return getEntity(ApiFactory.getFormService().getFormFlowableIdByTask(taskId));
	}
	
	/**
	 * 取回任务
	 * @param taskId
	 */
	public String retrieve(Long taskId){
		return ApiFactory.getTaskService().retrieve(taskId);
	}
	
	/**
	 * 获得loginName用户的该实例的当前任务
	 * 
	 * @param taskId
	 * @return
	 */
	public WorkflowTask getMyTask(T t, String loginName) {
		return ApiFactory.getTaskService().getActiveTaskByLoginName(t,
				loginName);
	}
	
	/**
	 * 加签
	 * @param taskId
	 * @param lists
	 */
	public void addSigner(Long taskId,List<String> lists){
		ApiFactory.getTaskService().addSigner(taskId,lists);
	}
	
	/**
	 * 减签
	 * @param taskId
	 * @param lists
	 */
	public void removeSigner(Long taskId, List<String> lists) {
		ApiFactory.getTaskService().removeSigner(taskId,lists);
	}
	
	/**
	 * 获得任务
	 * @param taskId
	 * @return
	 */
	public WorkflowTask getWorkflowTask(Long taskId) {
		return ApiFactory.getTaskService().getTask(taskId);
	}
	
	/**
	 * 得到当前环节办理人
	 * @param expenseReport
	 * @return
	 */
	public List<String[]> getActivityTaskTransactors(T t) {
		return ApiFactory.getTaskService().getActivityTaskTransactors(t);
	}

	/**
	 * 领取任务
	 * @param taskId
	 * @return task.not.need.receive(不需要领取,可能已被他人领取)
	 *          task.receive.success(领取成功)
	 */
	public String drawTask(Long taskId) {
		return ApiFactory.getTaskService().drawTask(taskId);
	}
	/**
	 * 
	 * @param taskId
	 * @return task.abandon.receive.success(放弃领取成功)
	 */
	public String abandonReceive(Long taskId){
		return ApiFactory.getTaskService().abandonReceive(taskId);
	}
	
	/**
	 * 指派任务
	 * @param taskId
	 * @param assignee 制定人员  
	 */
	public void assign(Long taskId,String assignee) {
		ApiFactory.getTaskService().assign(taskId,assignee);
	}
	
	/**
	 * 抄送任务
	 * @param taskId
	 * @param transactors
	 * @param title
	 * @param url
	 */
	public void createCopyTasks(Long taskId,List<String> transactors,String title,String url) {
		ApiFactory.getTaskService().createCopyTasks(taskId, transactors, title, url);
	}
	
	/**
	 * 当前任务是否是第一环节
	 * @param taskId
	 * @return
	 */
	public boolean isFirstTask(Long taskId){
		return ApiFactory.getTaskService().isFirstTask(taskId);
	}
	
	/**
	 * 根据taskId取当前环节正文列表
	 * @param taskId
	 * @return
	 */
	public List<Document> getDocumentList(Long taskId){
		return ApiFactory.getDocumentService().getDocuments(taskId);
	}
	
	/**
	 * 获得整个实例正文列表
	 * @param t
	 * @return
	 */
	public List<Document> getDocumentList(T t){
		return ApiFactory.getDocumentService().getDocuments(t);
	}
	
	/**
	 * 保存正文
	 * @param document
	 * @return
	 */
	public void saveDocument(Document document){
		 ApiFactory.getDocumentService().saveDocument(document);
	}
	
	/**
	 * 保存正文
	 * @param document
	 * @return
	 */
	public void saveDocument(Document document,T t){
		 ApiFactory.getDocumentService().saveDocument(document, t);
	}
	
	/**
	 * 删除正文
	 * @param documentId
	 * @return
	 */
	public void deleteDocument(Long documentId){
		 ApiFactory.getDocumentService().deleteDocument(documentId);
	}
	
	/**
	 * 根据taskId取当前环节附件列表
	 * @param taskId
	 * @return
	 */
	public List<WorkflowAttachment> getAttachmentList(Long taskId){
		return ApiFactory.getAttachmentService().getAttachments(taskId);
	}
	
	/**
	 * 获得整个实例附件列表
	 * @param t
	 * @return
	 */
	public List<WorkflowAttachment> getAttachmentList(T t){
		return ApiFactory.getAttachmentService().getAttachments(t);
	}
	
	/**
	 * 保存附件
	 * @param attachment
	 * @return
	 */
	public void saveAttachment(WorkflowAttachment attachment){
		 ApiFactory.getAttachmentService().saveAttachment(attachment);
	}
	
	/**
	 * 保存附件
	 * @param attachment
	 * @param t
	 */
	public void saveAttachment(WorkflowAttachment attachment,T t){
		 ApiFactory.getAttachmentService().saveAttachment(attachment, t);
	}
	
	/**
	 * 删除附件
	 * @param attachmentId
	 * @return
	 */
	public void deleteAttachment(Long attachmentId){
		 ApiFactory.getAttachmentService().deleteAttachment(attachmentId);
	}
	
	public String getCompleteTaskTipType(
			CompleteTaskTipType completeTaskTipType, T t) {
		switch (completeTaskTipType) {
		case MESSAGE:
			return t.getId().toString()+";"+completeTaskTipType.getContent();
		case RETURN_URL:
			return t.getId().toString()+";"+completeTaskTipType.getContent();
		case TACHE_CHOICE_URL:
			return t.getId().toString()+";"+completeTaskTipType.getCanChoiceTaches()+";"+"selectTache";
		case SINGLE_TRANSACTOR_CHOICE:
			return t.getId().toString()+";"+completeTaskTipType.getCanChoiceTransactor()+";"+"selectTransactor";
		
		default:
			return t.getId().toString();
		}
	}
	
	protected abstract void saveEntity(T t);
	
	protected abstract T getEntity(Long entityId);
}
