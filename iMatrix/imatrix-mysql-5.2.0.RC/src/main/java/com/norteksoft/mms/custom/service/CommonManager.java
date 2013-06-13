package com.norteksoft.mms.custom.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.norteksoft.mms.custom.dao.CommonDao;
import com.norteksoft.mms.form.dao.ListColumnDao;
import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.View;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.service.FormHtmlParser;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.module.entity.ModulePage;
import com.norteksoft.mms.module.service.ModulePageManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Document;
import com.norteksoft.product.api.entity.WorkflowAttachment;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.web.wf.WorkflowManagerSupport;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.base.enumeration.CompleteTaskTipType;
import com.norteksoft.wf.engine.client.EndInstanceInterface;
import com.norteksoft.wf.engine.client.FormFlowable;
import com.norteksoft.wf.engine.client.FormFlowableDeleteInterface;
import com.norteksoft.wf.engine.client.RetrieveTaskInterface;
import com.norteksoft.wf.engine.dao.WorkflowAttachmentDao;
import com.norteksoft.wf.engine.entity.Opinion;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;
import com.norteksoft.wf.engine.service.WorkflowRightsManager;

@Service
public class CommonManager extends WorkflowManagerSupport<FormFlowable> implements FormFlowableDeleteInterface,RetrieveTaskInterface,EndInstanceInterface  {

	@Autowired
	private CommonDao commonDao;
	@Autowired
	private FormViewManager formViewManager;
	@Autowired
	private ListColumnDao listColumnDao;
	@Autowired
	private ModulePageManager modulePageManager;
	
	@Autowired
	private WorkflowInstanceManager workflowInstanceManager;
	@Autowired 
	private WorkflowDefinitionManager workflowDefinitionManager;
	@Autowired
	private TaskService taskService;
	@Autowired
	private WorkflowRightsManager workflowRightsManager;
	@Autowired
	private WorkflowAttachmentDao workflowAttachmentDao;
	/**
	 * 根据列表编号查询数据
	 * @param page
	 * @param listCode
	 * @return
	 */
	@Transactional
	public Page<Object> list(Page<Object> page, View listView){
		if(listView.getStandard()){
			return commonDao.listEntity(page, listView.getDataTable().getEntityName());
		}else{
			if(StringUtils.isNotBlank(page.getOrderBy())){
				if(!page.getOrderBy().startsWith("dt_") && !FormHtmlParser.isDefaultField(page.getOrderBy())){
					page.setOrderBy("dt_"+page.getOrderBy());
				}
			}
			commonDao.list(page, listView.getCode());
			if(StringUtils.isNotBlank(page.getOrderBy())){
				if(page.getOrderBy().startsWith("dt_")){
					page.setOrderBy(page.getOrderBy().replaceFirst("dt_", ""));
				}
			}
			return page;
		}
	}

	public Long getCompanyId() {
		return ContextUtils.getCompanyId();
	}
	
	/**
	 * 根据ID，表单视图查询数据
	 * @param formView
	 * @param id
	 * @return
	 */
	@Transactional
	public Object getDateById(View formView, Long id){
		if(formView.getStandard()){
			return commonDao.getEntityById(formView.getDataTable().getEntityName(), id);
		}else{
			return commonDao.getDateById(formView.getDataTable().getName(), id);
		}
	}
	
	/**
	 * 保存表单数据
	 */
	@Transactional
	public Long save(Map<String,String[]> parameter){
		String[] pageIds = parameter.get("pageId");
		
		ModulePage modulePage = modulePageManager.getModulePage(Long.valueOf(pageIds[0]));
		FormView form = (FormView) modulePage.getView();
		
		return saveDate(parameter, form);
	}
	
	@Transactional
	public Long saveDate(Map<String,String[]> parameter, FormView form){
		String[] ids = parameter.get("id");
		List<FormControl> controls = formViewManager.getControls(form);
		Long id = null;
		if(ids != null && StringUtils.isNotBlank(ids[0])){
			id = commonDao.update(parameter, form, controls, Long.parseLong(ids[0]));
		}else{
			id = commonDao.save(parameter, form, controls);
		}
		return id;
	}
	
	/**
	 * 根据列表编号获取查询String
	 */
	@Transactional
	public String getQueryString(String listCode){
		List<ListColumn> columns = listColumnDao.getQueryColumnsByCode(listCode);
		StringBuilder query = new StringBuilder();
		query.append("[");
		boolean isFirst = true;
		for(ListColumn lc : columns){
			if(!isFirst) query.append(","); 
			query.append("{");
			query.append("enName:").append("dt_").append(lc.getTableColumn().getName());
			query.append(",chName:").append(getInternation(lc.getHeaderName()));
			if(DataType.TEXT == lc.getTableColumn().getDataType()){
				query.append(",propertyType:").append("STRING");
			}else{
				query.append(",propertyType:").append(lc.getTableColumn().getDataType());
			}
			query.append(",fixedField:false");
			query.append("}");
			isFirst = false;
		}
		query.append("]");
		return query.toString();
	}
	
	 public String getInternation(String code){
		 return ApiFactory.getSettingService().getInternationOptionValue(code);
	 }
	
	/**
	 * 删除
	 */
	@Transactional
	public String delete(View formView, List<Long> ids){
		if(ids != null && !ids.isEmpty()){
			for(Long id : ids){
				Object obj=commonDao.getDateById(formView.getDataTable().getName(),id);
				com.norteksoft.product.api.entity.WorkflowInstance wi=ApiFactory.getInstanceService().getInstance((String)((Map)obj).get("instance_id"));
				if(wi==null){
					commonDao.delete(formView.getDataTable().getName(), id);
				}else{
					ApiFactory.getInstanceService().deleteInstance((String)((Map)obj).get("instance_id"));
				}
			}
		}
		return "删除成功";
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	public List<WorkflowDefinition> getWorkflows(String code, Integer version){
		return workflowDefinitionManager.getCommonEnableDefinitionsByformCodeAndVersion(code, version);
	}
	
	/**
	 * 保存表单，启动流程
	 */
	@Transactional
	public Long startWorkflow(Map<String, String[]> parameter){
		// 从参数中获取 instanceId
		String instanceId = null;
		String[] instanceIds = parameter.get("instance_id");
		if(instanceIds != null && StringUtils.isNotEmpty(instanceIds[0])){
			instanceId = instanceIds[0];
		}
		// jBPM流程定义ID
		String processId = parameter.get("processId")[0];
		if(StringUtils.isNotEmpty(instanceId)&&StringUtils.isEmpty(processId)){
			WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(instanceId);
			processId=wi.getProcessDefinitionId();
		}
		
		WorkflowDefinition def=workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
		Long dataId = null;
		if(def!=null){
			Map result = ApiFactory.getInstanceService().startCustomInstance(def.getId());
			dataId = (Long)result.get("dataId");
			com.norteksoft.product.api.entity.WorkflowInstance workflow = ApiFactory.getInstanceService().getInstance((String)result.get("instanceId"));
			WorkflowTask task = getFirstTask(workflow.getProcessInstanceId());
			parameter.put("taskId",  new String[]{task.getId().toString()});
		}
		return dataId;
	}
	
	public WorkflowTask getFirstTask(String instanceId){
		WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(instanceId);
		return taskService.getFirstTask(instanceId, wi.getCreator());
	}
	
	/**
	 * 保存表单，启动流程，并提交第一环节任务
	 */
	@Transactional
	public Map submitWorkflow(Map<String,String[]> parameter){
		// jBPM流程定义ID
		String processId = parameter.get("processId")[0];
		WorkflowDefinition def=workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
		if(def!=null){
			return ApiFactory.getInstanceService().submitCustomInstance(def.getId());
		}
		return null;
	}
	
	/**
	 * 根据任务查询流程对应的表单
	 * @param taskId
	 * @return
	 */
	public FormView getViewByTask(Long taskId){
		WorkflowTask task = taskService.getTask(taskId);
		WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		FormView view = formViewManager.getFormView(wi.getFormId());
		return view;
	}
	
	/**
	 * 根据任务ID查询数据
	 * @param taskId
	 * @return
	 */
	public Object getDataByTaskId(Long taskId){
		WorkflowTask task = taskService.getTask(taskId);
		WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		FormView view = formViewManager.getFormView(wi.getFormId());
		return commonDao.getDateById(view.getDataTable().getName(), wi.getDataId());
	}
	
	/**
	 * 提交任务
	 */
	@Transactional
	public CompleteTaskTipType submitTask(Map<String,String[]> parameter){
		ApiFactory.getFormService().saveData(parameter);
		// 从参数中获取 instanceId
		String[] taskIds = parameter.get("taskId");
		Long taskId = Long.valueOf(taskIds[0]);
		if(parameter.get("transactor")!=null&&parameter.get("transactor").length>0){
			String tr = parameter.get("transactor")[0];
			return ApiFactory.getTaskService().completeInteractiveWorkflowTask(taskId, "", tr);
		}
		WorkflowTask task = taskService.getTask(taskId);
		TaskProcessingResult transact = TaskProcessingResult.valueOf(parameter.get("transact")[0]);
		return taskService.completeWorkflowTask(task, transact);
	}
	
	/**
	 * 取回
	 * @param taskId
	 */
	public String getBack(Long taskId){
		return taskService.retrieve(taskId);
	}
	
	/**
	 * 是否需要指定办理人
	 */
	public CompleteTaskTipType isNeedAssigningTransactor(Long taskId){
		WorkflowTask task = taskService.getTask(taskId);
		WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		CompleteTaskTipType result = taskService.isNeedAssigningTransactor(instance, task);
		if(result==null){
			result=taskService.isSubProcessNeedChoiceTransactor(task);
		}
		return result;
	}
	
	/**
	 * 抄送
	 */
	public void createCopyTaches(Long taskId, List<String> transactors,String title,String url){
		taskService.createCopyTaches(taskId, transactors, title, url);
	}
	/**
	 * 领取任务
	 * @return
	 */
	public String receive(Long taskId){
		return ApiFactory.getTaskService().drawTask(taskId);
	}
	/**
	 * 放弃领取任务
	 * @param taskId
	 * @return
	 */
	public String abandonReceive(Long taskId){
		return ApiFactory.getTaskService().abandonReceive(taskId);
	}
	
	/**
	 * 获取任务下环节办理人
	 */
	public Map<String[], List<String[]>> getNextTasksCandidates(Long taskId){
		WorkflowTask task = taskService.getTask(taskId);
		return taskService.getNextTasksCandidates(task);
	}
	
	/**
	 * 任务是否已完成
	 */
	public boolean isTaskComplete(Long taskId){
		WorkflowTask task = taskService.getTask(taskId);
		return TaskState.COMPLETED.getIndex().equals(task.getActive())||TaskState.CANCELLED.getIndex().equals(task.getActive())||TaskState.ASSIGNED.getIndex().equals(task.getActive())||TaskState.HAS_DRAW_OTHER.getIndex().equals(task.getActive());
	}
	
	/**
	 * 查询字段编辑权限
	 */
	public String getFieldPermision(Long taskId){
		WorkflowTask task = taskService.getTask(taskId);
		return workflowRightsManager.getFieldPermission(task);
	}

	/**
	 * 为任务的下环节指定办理人
	 */
	public CompleteTaskTipType setTasksTransactor(Long taskId, List<String> transactors ) {
		return ApiFactory.getTaskService().completeInteractiveWorkflowTask(taskId, transactors, null);
	}
	/**
	 * 意见权限（查看、编辑、必填  read edit must）
	 */
	public List<String> opinionRightByTask(Long taskId){
		List<String> result = new ArrayList<String>();
		WorkflowTask task = taskService.getWorkflowTask(taskId);
		if(workflowRightsManager.viewOpinionRight(task)){
			result.add("view");
		}
		if(task.getActive()!=2){
			if(workflowRightsManager.editOpinionRight(task)){
				result.add("edit");
			}
			if(workflowRightsManager.mustOpinionRight(task)){
				result.add("must");
			}
		}
		return result;
	}
	
	/**
	 * 正文权限（创建,删除 create delete）
	 */
	public List<String> textRightByTask(Long taskId){
		List<String> result = new ArrayList<String>();
		WorkflowTask task = taskService.getWorkflowTask(taskId);
		if(task.getActive()!=2&&task.getActive()!=3&&task.getActive()!=5&&task.getActive()!=7){
			if(workflowRightsManager.officialTextCreateRight(task)){
				result.add("create");
			}
			if(workflowRightsManager.officialTextDeleteRight(task)){
				result.add("delete");
			}
			if(workflowRightsManager.officialTextDownloadRight(task)){
				result.add("downLoad");
			}
			if(workflowRightsManager.officialTextEditRight(task)){
				result.add("edit");
			}
		}else if(task.getActive()==2){
			if(workflowRightsManager.officialTextDownloadRight(task)){
				result.add("downLoad");
			}
		}
		return result;
	}
	
	/**
	 * 附件权限
	 */
	public List<String> attachmentRightByTask(Long taskId){
		List<String> result = new ArrayList<String>();
		WorkflowTask task = taskService.getWorkflowTask(taskId);
		if(task.getActive()!=2&&task.getActive()!=3&&task.getActive()!=5&&task.getActive()!=7){
			if(workflowRightsManager.attachmentAddRight(task)){
				result.add("create");
			}
			if(workflowRightsManager.attachmentDeleteRight(task)){
				result.add("delete");
			}
			if(workflowRightsManager.attachmentDownloadRight(task)){
				result.add("downLoad");
			}
		}else if(task.getActive()==2){
			if(workflowRightsManager.attachmentDownloadRight(task)){
				result.add("downLoad");
			}
		}
		return result;
	}
	/**
	 * 根据taskId得到task
	 */
	public WorkflowTask getTaskByTaskId(Long taskId){
		return taskService.getWorkflowTask(taskId);
	}
	/**
	 * 根据任务查询意见
	 */
	public List<Opinion> getOpinions(String workflowId,Long companyId){
		return workflowInstanceManager.getOpinionsByInstanceId(workflowId, companyId);
	}
	
	/**
	 * 保存意见
	 */
	public void saveOpinion(Opinion opi){
		WorkflowTask task = taskService.getTask(opi.getTaskId());
		opi.setWorkflowId(task.getProcessInstanceId());
		opi.setTaskName(task.getName());
		workflowInstanceManager.saveOpinion(opi);
	}
	 /**
     * 通过流程定义的Key和version查询WorkflowDefinition
     * @param key
     * @param version
     * @return
     */
	 public WorkflowDefinition getWorkflowDefinitionByProcessId(String processId){
		 return workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
	 }
	 /**
	  * 发起前自动填写字段
	  */
	 public void fillEntityByDefinition(Map data,String wfDefinationCode, Integer version,Long... systemId){
		 ApiFactory.getFormService().fillEntityByDefinition(data, wfDefinationCode, version,systemId);
	 }
	 /**
	  * 办理前自动填写字段
	  */
	 public void fillEntityByTask(Map data,Long taskId){
		 ApiFactory.getFormService().fillEntityByTask(data, taskId);
	 }
	 
	 public List<Document> getDocumentsByInstance(String instanceId){
		 return ApiFactory.getDocumentService().getDocuments(instanceId);
	 }
	 
	 public List<WorkflowAttachment> getAttachments(String instanceId){
		 return ApiFactory.getAttachmentService().getAllAttachments(instanceId);
	 }
	 

	@Override
	protected FormFlowable getEntity(Long entityId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void saveEntity(FormFlowable t) {
		// TODO Auto-generated method stub
		
	}

	public void deleteFormFlowable(Long dataId) {
		// TODO Auto-generated method stub
		
	}

	public void retrieveTaskExecute(Long entityId, Long taskId) {
		// TODO Auto-generated method stub
		
	}

	public void endInstanceExecute(Long entityId) {
		// TODO Auto-generated method stub
		
	}

	public WorkflowInstance getWorkflowInforById(String instanceId) {
		return workflowInstanceManager.getWorkflowInstance(instanceId);
	}
	public void deleteAttachment(Long id){
		Assert.notNull(id, "附件id不能为null");
		this.workflowAttachmentDao.delete(id);
	}

	public Map<String, Object> getAmountTotal(List<String> names) {
		return commonDao.getAmountTotal(names);
	}
}
