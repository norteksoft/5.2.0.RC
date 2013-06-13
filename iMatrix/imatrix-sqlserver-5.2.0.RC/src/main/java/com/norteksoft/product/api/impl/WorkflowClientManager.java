package com.norteksoft.product.api.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.bs.options.service.OptionGroupManager;
import com.norteksoft.bs.rank.service.RankManager;
import com.norteksoft.mms.form.dao.GeneralDao;
import com.norteksoft.mms.form.entity.AutomaticallyFilledField;
import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.jdbc.JdbcSupport;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.FileService;
import com.norteksoft.product.api.WorkflowAttachmentService;
import com.norteksoft.product.api.WorkflowDataDictService;
import com.norteksoft.product.api.WorkflowDefinitionService;
import com.norteksoft.product.api.WorkflowDocumentService;
import com.norteksoft.product.api.WorkflowFormService;
import com.norteksoft.product.api.WorkflowHistoryService;
import com.norteksoft.product.api.WorkflowInstanceService;
import com.norteksoft.product.api.WorkflowOpinionService;
import com.norteksoft.product.api.WorkflowPermissionService;
import com.norteksoft.product.api.WorkflowRightService;
import com.norteksoft.product.api.WorkflowTaskService;
import com.norteksoft.product.api.entity.BusinessSystem;
import com.norteksoft.product.api.entity.DataDictionary;
import com.norteksoft.product.api.entity.Option;
import com.norteksoft.product.api.entity.OptionGroup;
import com.norteksoft.product.api.entity.TaskPermission;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.api.entity.WorkflowInstance;
import com.norteksoft.product.api.entity.WorkflowTask;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.task.base.enumeration.TaskSource;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.task.entity.TaskSetting;
import com.norteksoft.wf.base.enumeration.CommonStrings;
import com.norteksoft.wf.base.enumeration.CompleteTaskTipType;
import com.norteksoft.wf.base.enumeration.DataDictUseType;
import com.norteksoft.wf.base.enumeration.ProcessState;
import com.norteksoft.wf.base.exception.NotFoundEnabledWorkflowDefinitionException;
import com.norteksoft.wf.engine.client.DictQueryCondition;
import com.norteksoft.wf.engine.client.FormFlowable;
import com.norteksoft.wf.engine.client.WorkflowInfo;
import com.norteksoft.wf.engine.core.DefinitionXmlParse;
import com.norteksoft.wf.engine.core.ExecutionVariableCommand;
import com.norteksoft.wf.engine.dao.WorkflowTypeDao;
import com.norteksoft.wf.engine.entity.Document;
import com.norteksoft.wf.engine.entity.DocumentFile;
import com.norteksoft.wf.engine.entity.Opinion;
import com.norteksoft.wf.engine.entity.WorkflowAttachment;
import com.norteksoft.wf.engine.entity.WorkflowAttachmentFile;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.entity.WorkflowType;
import com.norteksoft.wf.engine.service.DataDictionaryManager;
import com.norteksoft.wf.engine.service.OfficeManager;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowAttachmentManager;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;
import com.norteksoft.wf.engine.service.WorkflowRightsManager;
import com.norteksoft.wf.engine.service.WorkflowTypeManager;
/**
 * 流程管理
 * 管理流程的部署、启动、结束等
 * @author x
 * 	<br>com.norteksoft.api包下的类才是公开提供给用户使用的api。
 */
@Service
@Transactional
public class WorkflowClientManager implements WorkflowDefinitionService,WorkflowInstanceService,WorkflowTaskService,
					WorkflowHistoryService,WorkflowFormService,WorkflowOpinionService,WorkflowDocumentService,WorkflowAttachmentService,WorkflowDataDictService,WorkflowRightService,WorkflowPermissionService{
	private Log log = LogFactory.getLog(WorkflowClientManager.class);
	private static String LOGMESSAGE_METHOD_PARAMETER = " method parameter: ";
	private static final String LOG_METHOD_BEGIN=PropUtils.LOG_METHOD_BEGIN;
	private static final String LOG_METHOD_END=PropUtils.LOG_METHOD_END;
	private static final String LOG_CONTENT=PropUtils.LOG_CONTENT;
	private static final String LOG_FLAG=PropUtils.LOG_FLAG;
	
	private TaskService taskService;
	private WorkflowInstanceManager workflowInstanceManager;
	private WorkflowDefinitionManager workflowDefinitionManager;
//	private BasicTypeManager basicTypeManager;
	private DataDictionaryManager dataDictionaryManager;
	private FormViewManager formManager;
	private WorkflowRightsManager workflowRightsManager;
	private OfficeManager officeManager;
	private WorkflowAttachmentManager workflowAttachmentManager;
	private GeneralDao generalDao;
	private OptionGroupManager optionGroupManager;
	private RankManager rankManager;
	private WorkflowTypeManager workflowTypeManager;
	private AcsUtils acsUtils;
	@Autowired
	private WorkflowTypeDao workflowTypeDao;
	@Autowired
	private JdbcSupport jdbcDao;
	@Autowired
	public void setRankManager(RankManager rankManager) {
		this.rankManager = rankManager;
	}
	
	@Autowired
	public void setOptionGroupManager(OptionGroupManager optionGroupManager) {
		this.optionGroupManager = optionGroupManager;
	}
	
	@Autowired
	public void setGeneralDao(GeneralDao generalDao) {
		this.generalDao = generalDao;
	}
	
	@Autowired
	public void setWorkflowAttachmentManager(
			WorkflowAttachmentManager workflowAttachmentManager) {
		this.workflowAttachmentManager = workflowAttachmentManager;
	}
	
	@Autowired
	public void setOfficeManager(OfficeManager officeManager) {
		this.officeManager = officeManager;
	}
	
	@Autowired
	private ProcessEngine processEngine;
	
	
	@Autowired
	public void setWorkflowRightsManager(
			WorkflowRightsManager workflowRightsManager) {
		this.workflowRightsManager = workflowRightsManager;
	}
	@Autowired
	public void setWorkflowTypeManager(WorkflowTypeManager workflowTypeManager) {
		this.workflowTypeManager = workflowTypeManager;
	}

	@Autowired
	public void setTaskService(TaskService taskService) {
		log.debug("taskService" + taskService);
		this.taskService = taskService;
	}
	
	@Autowired
	public void setFormViewManager(FormViewManager formManager) {
		this.formManager = formManager;
	}

	@Autowired
	public void setWorkflowInstanceManager(
			WorkflowInstanceManager workflowInstanceManager) {
		log.debug("workflowInstanceManager" + workflowInstanceManager);
		this.workflowInstanceManager = workflowInstanceManager;
	}
	
	@Autowired
	public void setWorkflowDefinitionManager(
			WorkflowDefinitionManager workflowDefinitionManager) {
		log.debug("workflowDefinitionManager" + workflowDefinitionManager);
		this.workflowDefinitionManager = workflowDefinitionManager;
	}
	@Autowired
	public void setAcsUtils(AcsUtils acsUtils) {
		this.acsUtils = acsUtils;
	}
	public void setProcessEngine(ProcessEngine processEngine) {
		this.processEngine = processEngine;
	}

	public WorkflowTask getTask(Long taskId){
		Assert.notNull(taskId, "获得task时taskId不能为null");
		return BeanUtil.turnToModelTask(taskService.getWorkflowTask(taskId));
	}
	
	/**
	 * 查询当前用户所在公司
	 * @return
	 */
	public Long getCompanyId(FormFlowable entity){
		Assert.notNull(ContextUtils.getCompanyId(), "companyId不能为null");
		if(ContextUtils.getCompanyId()!=null){
			return ContextUtils.getCompanyId();
		}else if(entity!=null){
			return entity.getCompanyId();
		}
		return null;
	}
	
	/**
	 * 查询当前用户登录名
	 * @return
	 */
	public String getLoginName(){
		return ContextUtils.getLoginName();
	}
	
	/**
	 * 手动结束流程 ，entity.workflowInfo.processState将被设置为手动结束
	 * @param entity 走流程的实体 
	 */
	public void endInstance(FormFlowable entity){
		log.debug(LOG_METHOD_BEGIN+"WorkflowClientManager+endInstance(FormFlowable entity)"+LOG_FLAG);
		log.debug(LOG_CONTENT+"entity"+LOG_FLAG+entity);
		log.debug(LOG_CONTENT+"entity.getWorkflowInfo()***"+LOG_FLAG+entity.getWorkflowInfo());
		log.debug(LOG_CONTENT+"entity.getWorkflowInfo().getWorkflowId()"+LOG_FLAG+entity.getWorkflowInfo().getWorkflowId());
		Assert.notNull(entity,"FormFlowable不能为空");
		if(entity.getWorkflowInfo()==null || entity.getWorkflowInfo().getWorkflowId()==null){
			log.debug("entity.getWorkflowInfo()不能为null或entity.getWorkflowInfo().getWorkflowId()不能为null");
			log.debug(LOG_METHOD_END+"WorkflowClientManager+endInstance(FormFlowable entity)"+LOG_FLAG);
			throw new RuntimeException("entity.getWorkflowInfo()不能为null或entity.getWorkflowInfo().getWorkflowId()不能为null");
		}
		WorkflowInstance workflow =  getInstance(entity.getWorkflowInfo().getWorkflowId());
		if(workflow==null){
			log.debug("流程实例不能为null");
			throw new RuntimeException("流程实例不能为null");
		}
		log.debug(LOG_CONTENT+"结束JBPM流程实例开始"+LOG_FLAG);
		processEngine.getExecutionService().endProcessInstance(workflow.getProcessInstanceId(),"cancel");
		log.debug(LOG_CONTENT+"结束JBPM流程实例结束"+LOG_FLAG);
		workflow.setProcessState(ProcessState.MANUAL_END);
		workflow.setEndTime(new Date(System.currentTimeMillis()));
		workflowInstanceManager.saveWorkflowInstance(BeanUtil.turnToWorkflowInstance(workflow));
		log.debug(LOG_CONTENT+"结束当前实例对应的当前任务开始"+LOG_FLAG);
		taskService.endTasks(workflow.getProcessInstanceId(),workflow.getCompanyId());
		log.debug(LOG_CONTENT+"结束当前实例对应的当前任务开始"+LOG_FLAG);
		entity.getWorkflowInfo().setProcessState(ProcessState.MANUAL_END);
		log.debug(LOG_CONTENT+"workflow.getProcessState"+LOG_FLAG+workflow.getProcessState());
		log.debug(LOG_CONTENT+"entity.getWorkflowInfo().getProcessState"+LOG_FLAG+entity.getWorkflowInfo().getProcessState());
		log.debug(LOG_METHOD_END+"WorkflowClientManager+endInstance(FormFlowable entity)"+LOG_FLAG);
	}
	
	
	/**
	 * 手动结束流程
	 * @param workflowId 流程实例id
	 */
	public void endInstance(String workflowId){
		log.debug(LOG_METHOD_BEGIN+"WorkflowClientManager+endInstance(String workflowId)"+LOG_FLAG);
		log.debug(LOG_CONTENT+"workflowId"+LOG_FLAG+workflowId);
		WorkflowInstance wi =  getInstance(workflowId);
		log.debug(LOG_CONTENT+"WorkflowInstance"+LOG_FLAG+wi);
		endInstance(wi);
		log.debug(LOG_METHOD_END+"WorkflowClientManager+endInstance(String workflowId)"+LOG_FLAG);
	}
	
	/**
	 * 手动结束流程
	 * @param workflow 流程实例
	 */
	public void endInstance(WorkflowInstance workflow){
		log.debug(LOG_METHOD_BEGIN+"WorkflowClientManager+endInstance(WorkflowInstance workflow)"+LOG_FLAG);
		log.debug(LOG_CONTENT+"结束JBPM流程实例开始"+LOG_FLAG);
		processEngine.getExecutionService().endProcessInstance(workflow.getProcessInstanceId(),"cancel");
		log.debug(LOG_CONTENT+"结束JBPM流程实例结束"+LOG_FLAG);
		workflow.setProcessState(ProcessState.MANUAL_END);
		workflow.setEndTime(new Date(System.currentTimeMillis()));
		workflowInstanceManager.saveWorkflowInstance(BeanUtil.turnToWorkflowInstance(workflow));
		log.debug(LOG_CONTENT+"结束当前实例对应的当前任务开始"+LOG_FLAG);
		taskService.endTasks(workflow.getProcessInstanceId(),workflow.getCompanyId());
		log.debug(LOG_CONTENT+"结束当前实例对应的当前任务开始"+LOG_FLAG);
		FormView form = formManager.getFormView(workflow.getFormId());
		if(StringUtils.isNotEmpty(form.getDataTable().getEntityName())){
			Object entity = generalDao.getObject(form.getDataTable().getEntityName(), workflow.getDataId());
			try {
				BeanUtils.setProperty(entity, "workflowInfo.processState", ProcessState.MANUAL_END);
				generalDao.save(entity);
				log.debug(LOG_CONTENT+"entity.getWorkflowInfo().getProcessState"+LOG_FLAG+BeanUtils.getProperty(entity, "workflowInfo.processState"));
			} catch (IllegalAccessException e) {
				log.debug("为bean设置属性异常:" + e.getMessage());
			} catch (InvocationTargetException e) {
				log.debug("为bean设置属性异常:" +e.getMessage());
			}catch (NoSuchMethodException e) {
				log.debug("获得bean属性workflowInfo.processState时的异常:" +e.getMessage());
			}
		}else{
			jdbcDao.updateTable("UPDATE "+form.getDataTable().getName()+" SET PROCESS_STATE="+ProcessState.MANUAL_END.getIndex()+" WHERE  ID="+workflow.getDataId());
		}
		log.debug(LOG_CONTENT+"workflow.getProcessState"+LOG_FLAG+workflow.getProcessState());
		log.debug(LOG_METHOD_END+"WorkflowClientManager+endInstance(WorkflowInstance workflow)"+LOG_FLAG);
	}
	
	@Deprecated
	public void endInstance(com.norteksoft.wf.engine.entity.WorkflowInstance workflow){
		log.debug(LOG_METHOD_BEGIN+"WorkflowClientManager+endInstance(WorkflowInstance workflow)"+LOG_FLAG);
		log.debug(LOG_CONTENT+"结束JBPM流程实例开始"+LOG_FLAG);
		processEngine.getExecutionService().endProcessInstance(workflow.getProcessInstanceId(),"cancel");
		log.debug(LOG_CONTENT+"结束JBPM流程实例结束"+LOG_FLAG);
		workflow.setProcessState(ProcessState.MANUAL_END);
		workflow.setEndTime(new Date(System.currentTimeMillis()));
		workflowInstanceManager.saveWorkflowInstance(workflow);
		log.debug(LOG_CONTENT+"结束当前实例对应的当前任务开始"+LOG_FLAG);
		taskService.endTasks(workflow.getProcessInstanceId(),workflow.getCompanyId());
		log.debug(LOG_CONTENT+"结束当前实例对应的当前任务开始"+LOG_FLAG);
		FormView form = formManager.getFormView(workflow.getFormId());
		if(StringUtils.isNotEmpty(form.getDataTable().getEntityName())){
			Object entity = generalDao.getObject(form.getDataTable().getEntityName(), workflow.getDataId());
			try {
				BeanUtils.setProperty(entity, "workflowInfo.processState", ProcessState.MANUAL_END);
				generalDao.save(entity);
				log.debug(LOG_CONTENT+"entity.getWorkflowInfo().getProcessState"+LOG_FLAG+BeanUtils.getProperty(entity, "workflowInfo.processState"));
			} catch (IllegalAccessException e) {
				log.debug("为bean设置属性异常:" + e.getMessage());
			} catch (InvocationTargetException e) {
				log.debug("为bean设置属性异常:" +e.getMessage());
			}catch (NoSuchMethodException e) {
				log.debug("获得bean属性workflowInfo.processState时的异常:" +e.getMessage());
			}
		}else{
			jdbcDao.updateTable("UPDATE "+form.getDataTable().getName()+" SET PROCESS_STATE="+ProcessState.MANUAL_END.getIndex()+" WHERE  ID="+workflow.getDataId());
		}
		log.debug(LOG_CONTENT+"workflow.getProcessState"+LOG_FLAG+workflow.getProcessState());
		log.debug(LOG_METHOD_END+"WorkflowClientManager+endInstance(WorkflowInstance workflow)"+LOG_FLAG);
	}
	/**
	 * 查询所有启用的流程定义
	 * @return
	 */
	public List<com.norteksoft.product.api.entity.WorkflowDefinition> getWorkflowDefinitions() {
		List<WorkflowDefinition> list=workflowDefinitionManager.getActiveDefinition();
		List<com.norteksoft.product.api.entity.WorkflowDefinition> result=new ArrayList<com.norteksoft.product.api.entity.WorkflowDefinition>();
		for(WorkflowDefinition definition:list){
			result.add(getWorkflowDefinitionParameter(definition));
		}
		return result;
	}
	
	/**
	 * 用户保存实体后,根据流程定义名称来启动流程,如果流程启动，则不做任何处理直接返回
	 * @param workflowDefinitionCode
	 */
	public void startInstance(String workflowDefinitionCode, FormFlowable entity){
		log.debug(LOG_METHOD_BEGIN+"发起实例,WorkflowClientManager+startInstance(String workflowDefinitionCode, FormFlowable entity)"+LOG_FLAG);
		log.debug(LOG_CONTENT+"workflowDefinitionCode"+LOG_FLAG+workflowDefinitionCode);
		Assert.notNull(entity,"FormFlowable实体不能为null");
		log.debug(LOG_CONTENT+"entity"+LOG_FLAG+entity);
		WorkflowDefinition definition = getEnabledHighestVersionDefinition(workflowDefinitionCode,getCompanyId(entity));
		log.debug(LOG_CONTENT+"发起的流程"+LOG_FLAG+definition);
		startInstance(definition,entity);
		log.debug(LOG_METHOD_END+"发起实例,WorkflowClientManager+startInstance(String workflowDefinitionCode, FormFlowable entity)"+LOG_FLAG);
	}
	
	/**
	 * 用户保存实体后,根据流程定义名称来启动流程,如果流程启动，则不做任何处理直接返回
	 * @param workflowDefinitionName
	 */
	public void startInstance(Long workflowDefinitionId,FormFlowable entity){
		Assert.notNull(workflowDefinitionId,"流程定义id不能为null");
		Assert.notNull(entity,"FormFlowable实体不能为null");
		log.debug(LOG_METHOD_BEGIN+"发起实例,WorkflowClientManager+startInstance(Long workflowDefinitionId,FormFlowable entity)"+LOG_FLAG);
		WorkflowDefinition definition = this.getWorkflowDefinitionById(workflowDefinitionId);
		log.debug(LOG_CONTENT+"发起的流程"+LOG_FLAG+definition);
		startInstance(definition, entity);
		log.debug(LOG_METHOD_END+"发起实例,WorkflowClientManager+startInstance(Long workflowDefinitionId,FormFlowable entity)"+LOG_FLAG);
	}
	
	
	public String startInstance(WorkflowDefinition workflowDefinition,FormFlowable entity){
		Assert.notNull(workflowDefinition,"流程定义不能为null");
		Assert.notNull(entity,"FormFlowable实体不能为null");
		log.debug(LOG_METHOD_BEGIN+"发起实例,WorkflowClientManager+startInstance(WorkflowDefinition workflowDefinition,FormFlowable entity)"+LOG_FLAG);
		log.debug(LOG_CONTENT+"entity.getWorkflowInfo()"+LOG_FLAG+entity.getWorkflowInfo());
		int priority = 6;
		//如果流程已经启动 直接返回实例的id
		if(entity.getWorkflowInfo()!=null&&entity.getWorkflowInfo().getWorkflowId()!=null){
			log.debug(LOG_CONTENT+"entity.getWorkflowInfo().getWorkflowId()"+LOG_FLAG+entity.getWorkflowInfo().getWorkflowId());
			log.debug(LOG_CONTENT+"entity.getWorkflowInfo().getPriority()"+LOG_FLAG+entity.getWorkflowInfo().getPriority());
			log.debug(LOG_CONTENT+"该流程已发起"+LOG_FLAG);
			log.debug(LOG_METHOD_END+"发起实例,WorkflowClientManager+startInstance(WorkflowDefinition workflowDefinition,FormFlowable entity)"+LOG_FLAG);
			return entity.getWorkflowInfo().getWorkflowId();
		}
		if(entity.getWorkflowInfo()!=null&&entity.getWorkflowInfo().getPriority()!=null) priority = entity.getWorkflowInfo().getPriority();
		log.debug(LOG_CONTENT+"发起JBPM实例开始"+LOG_FLAG);
		String instanceId = workflowInstanceManager.startWorkflowInstance(workflowDefinition.getProcessId(),priority,entity.getId());
		log.debug(LOG_CONTENT+"流程实例Id,instanceId"+LOG_FLAG+instanceId);
		log.debug(LOG_CONTENT+"发起JBPM实例结束"+LOG_FLAG);
		com.norteksoft.wf.engine.entity.WorkflowInstance workflow = workflowInstanceManager.getWorkflowInstance(instanceId);
		if(workflow==null){
			log.debug("流程实例不能为null");
			throw new RuntimeException("流程实例不能为null");
		}
		Assert.notNull(getLoginName(), "当前登录名不可为null");
		com.norteksoft.task.entity.WorkflowTask task= taskService.getFirstTask(instanceId,getLoginName());
		if(task==null){
			log.debug("第一环节任务不能为null");
			throw new RuntimeException("第一环节任务不能为null");
		}
		log.debug(LOG_CONTENT+"任务task"+LOG_FLAG+task);
		log.debug(LOG_CONTENT+"entity.getId()"+LOG_FLAG+entity.getId());
		workflow.setDataId(entity.getId());
		WorkflowInfo workflowInfo = entity.getWorkflowInfo();
		log.debug(LOG_CONTENT+"entity.getWorkflowInfo()"+LOG_FLAG+workflowInfo);
		if(workflowInfo==null){
			workflowInfo = 	new WorkflowInfo();
		}
		workflow.setCurrentActivity(task.getName());
		workflowInfo.setWorkflowDefinitionId(workflowDefinition.getProcessId());
		workflowInfo.setWorkflowDefinitionName(workflowDefinition.getName());
		workflowInfo.setWorkflowDefinitionCode(workflowDefinition.getCode());
		workflowInfo.setWorkflowDefinitionVersion(workflowDefinition.getVersion());
		workflowInfo.setWorkflowId(instanceId);
		workflowInfo.setFirstTaskId(task.getId());
		workflowInfo.setCurrentActivityName(task.getName());
		workflowInfo.setFormId(workflow.getFormId());
		entity.setWorkflowInfo(workflowInfo);
		workflowInstanceManager.saveFormFlowable(entity);
		workflowInstanceManager.saveWorkflowInstance(workflow);
		log.debug(LOG_CONTENT+"流程实例workflow"+LOG_FLAG+workflow);
		log.debug(LOG_METHOD_END+"发起实例,WorkflowClientManager+startInstance(WorkflowDefinition workflowDefinition,FormFlowable entity)"+LOG_FLAG);
		return instanceId;
	}
	
	/**
	 * 启动子流程
	 * @param subWorkflowDefinition
	 * @param entity
	 * @param parentWorkflowId
	 * @param parentExcutionId
	 * @return
	 */
	public String startSubProcess(String subWorkflowDefinitionId,FormFlowable entity,Map<String,Object> subNeedVariableMap ){
		log.debug(LOG_METHOD_BEGIN+"发起子流程,WorkflowClientManager+startSubProcess(String subWorkflowDefinitionId,FormFlowable entity,Map<String,Object> subNeedVariableMap )"+LOG_FLAG);
		if(subWorkflowDefinitionId==null){
			log.debug(LOG_METHOD_END+"子流程Id为null，发起无效"+LOG_FLAG);
			log.debug(LOG_METHOD_END+"发起子流程,WorkflowClientManager+startInstance(WorkflowDefinition workflowDefinition,FormFlowable entity)"+LOG_FLAG);
			return null;
		}
		WorkflowDefinition wd = workflowDefinitionManager.getWorkflowDefinitionByProcessId(subWorkflowDefinitionId);
		String result=startSubProcess(wd,entity,subNeedVariableMap);
		log.debug(LOG_CONTENT+"WorkflowClientManager+发起子流程的返回结果result"+LOG_FLAG+result);
		log.debug(LOG_METHOD_END+"发起子流程,WorkflowClientManager+startInstance(WorkflowDefinition workflowDefinition,FormFlowable entity)"+LOG_FLAG);
		return result;
	}
	
	/**
	 * 启动子流程
	 * @param subWorkflowDefinition
	 * @param entity
	 * @param parentWorkflowId
	 * @param parentExcutionId
	 * @return
	 */
	public String startSubProcess(WorkflowDefinition subWorkflowDefinition,FormFlowable entity,Map<String,Object> subNeedVariableMap){
		log.debug(LOG_METHOD_BEGIN+"发起子流程,WorkflowClientManager+startSubProcess(WorkflowDefinition subWorkflowDefinition,FormFlowable entity,Map<String,Object> subNeedVariableMap)"+LOG_FLAG);
		log.debug(LOG_CONTENT+"entity.getWorkflowInfo()"+LOG_FLAG+entity.getWorkflowInfo());
		int priority = 6;
		//如果流程已经启动 直接返回实例的id
		if(entity.getWorkflowInfo()!=null&&entity.getWorkflowInfo().getPriority()!=null) priority = entity.getWorkflowInfo().getPriority();
		log.debug(LOG_CONTENT+"子流程实例变量CommonStrings.PRIORITY"+LOG_FLAG+priority);
		log.debug(LOG_CONTENT+"子流程实例变量CommonStrings.FORM_DATA_ID"+LOG_FLAG+entity.getId());
		subNeedVariableMap.put("priority", priority);
		subNeedVariableMap.put(CommonStrings.FORM_DATA_ID, entity.getId());
		log.debug(LOG_CONTENT+"发起子流程JBPM实例开始"+LOG_FLAG);
		ProcessInstance pi = processEngine.getExecutionService()
		.startProcessInstanceById(subWorkflowDefinition.getProcessId(),subNeedVariableMap);
		log.debug(LOG_CONTENT+"发起子流程JBPM实例结束"+LOG_FLAG);
		String instanceId = pi.getId();
		log.debug(LOG_CONTENT+"子流程实例ID,instanceId"+LOG_FLAG+instanceId);
		com.norteksoft.wf.engine.entity.WorkflowInstance workflow = workflowInstanceManager.getWorkflowInstance(instanceId);
		workflow.setDataId(entity.getId());
		WorkflowInfo workflowInfo = entity.getWorkflowInfo();
		log.debug(LOG_CONTENT+"entity.getWorkflowInfo()"+LOG_FLAG+workflowInfo);
		//TODO workflowInfo在父子流程公用实体时，会存在冲突问题
		if(workflowInfo==null){
			workflowInfo = 	new WorkflowInfo();
		}
		workflowInfo.setWorkflowDefinitionId(subWorkflowDefinition.getProcessId());
		workflowInfo.setWorkflowDefinitionName(subWorkflowDefinition.getName());
		workflowInfo.setWorkflowDefinitionCode(subWorkflowDefinition.getCode());
		workflowInfo.setWorkflowDefinitionVersion(subWorkflowDefinition.getVersion());
		workflowInfo.setWorkflowId(instanceId);
		workflowInfo.setFirstTaskId(workflow.getFirstTaskId());
		workflowInfo.setCurrentActivityName(workflow.getCurrentActivity());
		workflowInfo.setFormId(workflow.getFormId());
		entity.setWorkflowInfo(workflowInfo);
		Map<String,String> parameterSetting=DefinitionXmlParse.getParameterSetting(workflow.getProcessDefinitionId());
		String formViewUrl = parameterSetting.get(DefinitionXmlParse.FORM_VIEW_URL);
		if(StringUtils.isEmpty(formViewUrl)){
			formViewUrl = PropUtils.getProp(CommonStrings.WORKFLOW_PARAMETER_URL, CommonStrings.FORM_VIEW_URL);
		}
		String parameterName = parameterSetting.get(DefinitionXmlParse.FORM_VIEW_URL_PARAMETER_NAME);
		if(StringUtils.isEmpty(parameterName)){
			parameterName = PropUtils.getProp(CommonStrings.WORKFLOW_PARAMETER_URL, CommonStrings.FORM_VIEW_PARAMTER_NAME);
		}
		if(StringUtils.isNotEmpty(formViewUrl)){
			String joinSign = StringUtils.contains(formViewUrl, "?") ? "&" : "?";
			formViewUrl = ContextUtils.getSystemCode()+formViewUrl + joinSign + parameterName + "=";
		}
		log.debug(LOG_CONTENT + "查看表单的url"+LOG_FLAG + formViewUrl);
		workflow.setFormUrl(formViewUrl);
		String urgenUrl = parameterSetting.get(DefinitionXmlParse.URGEN_URL);
		if(StringUtils.isEmpty(urgenUrl)){
			urgenUrl = PropUtils.getProp(CommonStrings.WORKFLOW_PARAMETER_URL, CommonStrings.FORM_URGEN_URL);
		}
		String urgenParameterName = parameterSetting.get(DefinitionXmlParse.URGEN_URL_PARAMETER_NAME);
		if(StringUtils.isEmpty(urgenParameterName)){
			urgenParameterName = PropUtils.getProp(CommonStrings.WORKFLOW_PARAMETER_URL, CommonStrings.FORM_URGEN_PARAMTER_NAME);
		}
		if(StringUtils.isNotEmpty(urgenUrl)){
			String joinSign = StringUtils.contains(urgenUrl, "?") ? "&" : "?";
			urgenUrl = ContextUtils.getSystemCode()+urgenUrl + joinSign + urgenParameterName + "=";
		}
		log.debug(LOG_CONTENT + "应急处理的url"+LOG_FLAG + urgenUrl);
		workflow.setEmergencyUrl(urgenUrl);
		workflowInstanceManager.saveFormFlowable(entity);
		workflowInstanceManager.saveWorkflowInstance(workflow);
		log.debug(LOG_CONTENT+"子流程实例workflow"+LOG_FLAG+workflow);
		log.debug(LOG_METHOD_END+"发起子流程,WorkflowClientManager+startSubProcess(WorkflowDefinition subWorkflowDefinition,FormFlowable entity,Map<String,Object> subNeedVariableMap)"+LOG_FLAG);
		return instanceId;
	}
	/**
	 * 提交该流程实例的第一个任务
	 * @param workflowDefinitionName
	 * @param entity
	 * @param urlMap
	 * @return
	 */
	public CompleteTaskTipType submitWorkflowById(String workflowId,FormFlowable entity){
		log.debug(LOG_METHOD_BEGIN+"提交流程,WorkflowClientManager+submitWorkflowById(String workflowId,FormFlowable entity)"+LOG_FLAG);
		log.debug(LOG_CONTENT + "String workflowId:" +LOG_FLAG+ workflowId);
		log.debug(LOG_CONTENT + "FormFlowable entity:"+LOG_FLAG + entity);
		Assert.notNull(entity, "FormFlowable实体不可为null");
		Assert.notNull(entity.getWorkflowInfo(), "entity.getWorkflowInfo()不可为null");
		log.debug(LOG_CONTENT + "entity.getWorkflowInfo().getProcessState()"+LOG_FLAG + entity.getWorkflowInfo().getProcessState());
		if(entity.getWorkflowInfo().getProcessState()!=ProcessState.UNSUBMIT){
			log.debug(LOG_CONTENT + "流程已经被提交,此次提交无效"+LOG_FLAG );
			log.debug(LOG_METHOD_END+"提交流程,WorkflowClientManager+submitWorkflowById(String workflowId,FormFlowable entity)"+LOG_FLAG);
			return CompleteTaskTipType.MESSAGE.setContent("任务已完成");
		}
		String processId = null;
		com.norteksoft.wf.engine.entity.WorkflowInstance workflow = workflowInstanceManager.getWorkflowInstance(workflowId);
		if(workflow==null){
			log.debug("流程实例不能为null");
			throw new RuntimeException("流程实例不能为null");
		}
		processId = workflow.getProcessDefinitionId();
		log.debug(LOG_CONTENT+"流程实例workflow" +LOG_FLAG + workflow);
		
		Assert.notNull(getLoginName(), "当前登录名不可为空");
		log.debug(LOG_CONTENT+"当前登录名getLoginName():" +LOG_FLAG+getLoginName());
		com.norteksoft.task.entity.WorkflowTask task = taskService.getFirstTask(workflowId, getLoginName());
		Assert.notNull(task,"查询的第一个任务不应该为空");
		//设置第一个任务可见
		task.setVisible(true);
		taskService.addTitle(task, processId,null);
		taskService.saveTask(task);
		log.debug(LOG_CONTENT+"firstTask:"+LOG_FLAG + task);
		//如果实体中设置了流向名，设置流向名给引擎
		if(StringUtils.isNotEmpty(entity.getWorkflowInfo().getTransitionName()))this.setTransitionName(task.getId(), entity.getWorkflowInfo().getTransitionName());
		//如果实体中设置了新的办理人，设置办理人给引擎
		if(StringUtils.isNotEmpty(entity.getWorkflowInfo().getNewTransactor()))this.setNewTransactor(task.getId(), entity.getWorkflowInfo().getNewTransactor());
		
		CompleteTaskTipType completeTaskTipType = taskService.completeWorkflowTask(task, TaskProcessingResult.SUBMIT);
		log.debug(LOG_CONTENT+"第一环节任务完成结果"+LOG_FLAG + completeTaskTipType);
		workflow.setSubmitTime(new Date(System.currentTimeMillis()));
		if(completeTaskTipType==CompleteTaskTipType.OK&&workflow.getProcessState()==ProcessState.UNSUBMIT){
				workflow.setProcessState(ProcessState.SUBMIT);
				
				workflowInstanceManager.saveWorkflowInstance(workflow);
				
				entity.getWorkflowInfo().setProcessState(ProcessState.SUBMIT);
				entity.getWorkflowInfo().setState(workflow.getCurrentCustomState());
				entity.getWorkflowInfo().setSubmitTime(workflow.getSubmitTime());
				workflowInstanceManager.saveFormFlowable(entity);
		}
		Map<String,String> parameterSetting=DefinitionXmlParse.getParameterSetting(workflow.getProcessDefinitionId());
		String formViewUrl = parameterSetting.get(DefinitionXmlParse.FORM_VIEW_URL);
		if(StringUtils.isEmpty(formViewUrl)){
			formViewUrl = PropUtils.getProp(CommonStrings.WORKFLOW_PARAMETER_URL, CommonStrings.FORM_VIEW_URL);
		}
		String parameterName = parameterSetting.get(DefinitionXmlParse.FORM_VIEW_URL_PARAMETER_NAME);
		if(StringUtils.isEmpty(parameterName)){
			parameterName = PropUtils.getProp(CommonStrings.WORKFLOW_PARAMETER_URL, CommonStrings.FORM_VIEW_PARAMTER_NAME);
		}
		if(StringUtils.isNotEmpty(formViewUrl)){
			String joinSign = StringUtils.contains(formViewUrl, "?") ? "&" : "?";
			formViewUrl = ContextUtils.getSystemCode()+formViewUrl + joinSign + parameterName + "=";
		}
		log.debug(LOG_CONTENT+"查看表单url"+LOG_FLAG + formViewUrl);
		workflow.setFormUrl(formViewUrl);
		String urgenUrl = parameterSetting.get(DefinitionXmlParse.URGEN_URL);
		if(StringUtils.isEmpty(urgenUrl)){
			urgenUrl = PropUtils.getProp(CommonStrings.WORKFLOW_PARAMETER_URL, CommonStrings.FORM_URGEN_URL);
		}
		String urgenParameterName = parameterSetting.get(DefinitionXmlParse.URGEN_URL_PARAMETER_NAME);
		if(StringUtils.isEmpty(urgenParameterName)){
			urgenParameterName = PropUtils.getProp(CommonStrings.WORKFLOW_PARAMETER_URL, CommonStrings.FORM_URGEN_PARAMTER_NAME);
		}
		if(StringUtils.isNotEmpty(urgenUrl)){
			String joinSign = StringUtils.contains(urgenUrl, "?") ? "&" : "?";
			urgenUrl = ContextUtils.getSystemCode()+urgenUrl + joinSign + urgenParameterName + "=";
		}
		log.debug(LOG_CONTENT+"应急处理url"+LOG_FLAG + urgenUrl);
		workflow.setEmergencyUrl(urgenUrl);
		workflowInstanceManager.saveWorkflowInstance(workflow);
		log.debug(LOG_CONTENT+"流程实例设置了值后workflow" +LOG_FLAG + workflow);
		log.debug(LOG_CONTENT+"提交流程返回结果" +LOG_FLAG + completeTaskTipType);
		log.debug(LOG_METHOD_END+"提交流程,WorkflowClientManager+submitWorkflowById(String workflowId,FormFlowable entity)"+LOG_FLAG);
		return completeTaskTipType;
	}
	
	@Deprecated
	public CompleteTaskTipType submitWorkflowById(String workflowId,FormFlowable entity, Map<String,String> urlMap){
		log.debug(LOGMESSAGE_METHOD_PARAMETER + "String workflowId:" + workflowId);
		log.debug(LOGMESSAGE_METHOD_PARAMETER + "FormFlowable entity:" + entity);
		log.debug(LOGMESSAGE_METHOD_PARAMETER + "Map<String,String> urlMap:" + urlMap.toString());
		Assert.notNull(entity, "表单实体不可为空");
		
		Assert.notNull(entity.getWorkflowInfo(), "entity.getWorkflowInfo()不可为空");
		if(entity.getWorkflowInfo().getProcessState()!=ProcessState.UNSUBMIT) return CompleteTaskTipType.MESSAGE.setContent("任务已完成");
		
		com.norteksoft.wf.engine.entity.WorkflowInstance workflow = workflowInstanceManager.getWorkflowInstance(workflowId);
		Assert.notNull(workflow, "workflow不可为空");
		log.debug("workflow" + workflow);
		
		
		
		Assert.notNull(getLoginName(), "当前登录名不可为null");
		log.debug("getLoginName():" +getLoginName());
		com.norteksoft.task.entity.WorkflowTask task = taskService.getFirstTask(workflowId, getLoginName());
		Assert.notNull(task,"查询的第一个任务不应该为空");
		log.debug("firstTask:" + task);
		Assert.notNull(getProcessUrl(urlMap),"url不应该为null");

		taskService.executionVariableCommand(new ExecutionVariableCommand(workflowId, getProcessUrl(urlMap)));
		
		CompleteTaskTipType completeTaskTipType = taskService.completeWorkflowTask(task, TaskProcessingResult.SUBMIT);
		if(completeTaskTipType==CompleteTaskTipType.OK){
			workflow.setProcessState(ProcessState.SUBMIT);
			workflow.setSubmitTime(new Date(System.currentTimeMillis()));
			
			entity.getWorkflowInfo().setProcessState(ProcessState.SUBMIT);
			entity.getWorkflowInfo().setState(workflow.getCurrentCustomState());
			entity.getWorkflowInfo().setSubmitTime(workflow.getSubmitTime());
			workflowInstanceManager.saveFormFlowable(entity);
		}
		
		Map<String,String> parameterSetting=DefinitionXmlParse.getParameterSetting(workflow.getProcessDefinitionId());
		String formViewUrl = parameterSetting.get(DefinitionXmlParse.FORM_VIEW_URL);
		if(StringUtils.isEmpty(formViewUrl)){
			formViewUrl = PropUtils.getProp(CommonStrings.WORKFLOW_PARAMETER_URL, CommonStrings.FORM_VIEW_URL);
		}
		String parameterName = parameterSetting.get(DefinitionXmlParse.FORM_VIEW_URL_PARAMETER_NAME);
		if(StringUtils.isEmpty(parameterName)){
			parameterName = PropUtils.getProp(CommonStrings.WORKFLOW_PARAMETER_URL, CommonStrings.FORM_VIEW_PARAMTER_NAME);
		}
		if(StringUtils.isNotEmpty(formViewUrl)){
			String joinSign = StringUtils.contains(formViewUrl, "?") ? "&" : "?";
			formViewUrl = ContextUtils.getSystemCode()+formViewUrl + joinSign + parameterName + "=";
		}
		
		workflow.setFormUrl(formViewUrl);
		workflowInstanceManager.saveWorkflowInstance(workflow);
		return completeTaskTipType;
	}
	
	
	public CompleteTaskTipType submitInstance(String workflowDefinitionCode, FormFlowable entity){
		log.debug(LOG_METHOD_BEGIN+"提交流程,WorkflowClientManager+submitInstance(String workflowDefinitionCode, FormFlowable entity)"+LOG_FLAG);
		log.debug(LOG_CONTENT + "String workflowDefinitionName:"+LOG_FLAG + workflowDefinitionCode);
		log.debug(LOG_CONTENT + "FormFlowable entity:" +LOG_FLAG+ entity);
		Assert.notNull(entity, "FormFlowable实体不能为null");
		log.debug(LOG_CONTENT +"entity.getWorkflowInfo()" +LOG_FLAG+ entity.getWorkflowInfo());
		String workflowId = null;
		if(entity.getWorkflowInfo()==null || entity.getWorkflowInfo().getWorkflowId() == null){
			log.debug(LOG_CONTENT +"compayId" +LOG_FLAG+ getCompanyId(entity));
			WorkflowDefinition definition = getEnabledHighestVersionDefinition(workflowDefinitionCode,getCompanyId(entity));
			log.debug(LOG_CONTENT +"提交的流程" +LOG_FLAG+ definition);
			workflowId = startInstance(definition, entity) ;
		}else{
			Assert.notNull(entity.getWorkflowInfo(), "entity.getWorkflowInfo()不能为null");
			log.debug(LOG_CONTENT +"实例Id，entity.getWorkflowInfo().getWorkflowId()" +LOG_FLAG+ entity.getWorkflowInfo().getWorkflowId());
			workflowId = entity.getWorkflowInfo().getWorkflowId();
		}
		log.debug(LOG_CONTENT+"实例Id，workflowId"+LOG_FLAG + workflowId);
		CompleteTaskTipType result=submitWorkflowById(workflowId,entity);
		log.debug(LOG_CONTENT+"提交流程返回结果result"+LOG_FLAG + result);
		log.debug(LOG_METHOD_END+"提交流程,WorkflowClientManager+submitInstance(String workflowDefinitionCode, FormFlowable entity)"+LOG_FLAG);
		return result;
	}
	/**
	 *  提交流程
	 * 提交前用户保存自己的实体；
	 * 如果流程没有启动，则启动流程并提交
	 * 若流程已经在保存时启动了，则只提交流程
	 */
	@Deprecated
	public CompleteTaskTipType submitInstance(String definitionCode, FormFlowable entity, Map<String,String> urlMap){
		log.debug(LOGMESSAGE_METHOD_PARAMETER + "String workflowDefinitionName:" + definitionCode);
		log.debug(LOGMESSAGE_METHOD_PARAMETER + "FormFlowable entity:" + entity);
		log.debug(LOGMESSAGE_METHOD_PARAMETER + "Map<String,String> urlMap:" + urlMap);
		Assert.notNull(entity, "entity不能为空");
		log.debug("workflowInfo:" + entity.getWorkflowInfo());
		String workflowId = null;
		if(entity.getWorkflowInfo()==null || entity.getWorkflowInfo().getWorkflowId() == null){
			WorkflowDefinition definition = getEnabledHighestVersionDefinition(definitionCode,getCompanyId(entity));
			workflowId = startInstance(definition, entity) ;
		}else{
			workflowId = entity.getWorkflowInfo().getWorkflowId();
		}
		log.debug("workflowId" + workflowId);
		return submitWorkflowById(workflowId,entity,urlMap);
	}
	
	/**
	 * 提交流程
	 * 提交前用户保存自己的实体；
	 * 如果流程没有启动，则启动流程并提交
	 * 若流程已经在保存时启动了，则只提交流程
	 * @param workflowDefinitionId 工作流定义id
	 * @param entity 
	 * @param urlMap
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
	 */
	public CompleteTaskTipType submitInstance(Long workflowDefinitionId, FormFlowable entity){
		log.debug(LOG_METHOD_BEGIN+"提交流程,WorkflowClientManager+submitInstance(Long workflowDefinitionId, FormFlowable entity)"+LOG_FLAG);
		WorkflowDefinition definition = this.getWorkflowDefinitionById(workflowDefinitionId);
		log.debug(LOG_CONTENT + "提交的流程definition"+LOG_FLAG + definition);
		log.debug(LOG_CONTENT + "entity.getWorkflowInfo()"+LOG_FLAG + entity.getWorkflowInfo());
		String instanceId = entity.getWorkflowInfo() == null ? startInstance(definition, entity) : entity.getWorkflowInfo().getWorkflowId();
		log.debug(LOG_CONTENT + "流程实例Id,instanceId"+LOG_FLAG +instanceId);
		CompleteTaskTipType result=submitWorkflowById(instanceId,entity);
		log.debug(LOG_CONTENT+"提交流程返回结果result"+LOG_FLAG + result);
		log.debug(LOG_METHOD_END+"提交流程,WorkflowClientManager+submitInstance(Long workflowDefinitionId, FormFlowable entity)"+LOG_FLAG);
		return result;
	}
	
	@Deprecated
	public CompleteTaskTipType submitInstance(Long workflowDefinitionId, FormFlowable entity, Map<String,String> urlMap){
		WorkflowDefinition definition = this.getWorkflowDefinitionById(workflowDefinitionId);
		String instanceId = entity.getWorkflowInfo() == null ? startInstance(definition, entity) : entity.getWorkflowInfo().getWorkflowId();
		return submitWorkflowById(instanceId,entity,urlMap);
	}
	
	/**
	 * 提交流程
	 * 提交前用户保存自己的实体；
	 * 如果流程没有启动，则启动流程并提交
	 * 若流程已经在保存时启动了，则只提交流程
	 * @param workflowDefinition 流程定义
	 * @param entity
	 * @param urlMap
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
	 */
	public CompleteTaskTipType submitInstance(WorkflowDefinition workflowDefinition, FormFlowable entity){
		log.debug(LOG_METHOD_BEGIN+"提交流程,WorkflowClientManager+submitInstance(WorkflowDefinition workflowDefinition, FormFlowable entity)"+LOG_FLAG);
		log.debug(LOG_CONTENT + "entity.getWorkflowInfo()"+LOG_FLAG + entity.getWorkflowInfo());
		String instanceId = entity.getWorkflowInfo() == null ? startInstance(workflowDefinition, entity) : entity.getWorkflowInfo().getWorkflowId();
		log.debug(LOG_CONTENT + "流程实例Id,instanceId"+LOG_FLAG +instanceId);
		CompleteTaskTipType result=submitWorkflowById(instanceId,entity);
		log.debug(LOG_CONTENT+"提交流程返回结果result"+LOG_FLAG + result);
		log.debug(LOG_METHOD_END+"提交流程,WorkflowClientManager+submitInstance(WorkflowDefinition workflowDefinition, FormFlowable entity)"+LOG_FLAG);
		return result;
	}
	@Deprecated
	public CompleteTaskTipType submitInstance(WorkflowDefinition workflowDefinition, FormFlowable entity, Map<String,String> urlMap){
		String instanceId = entity.getWorkflowInfo() == null ? startInstance(workflowDefinition, entity) : entity.getWorkflowInfo().getWorkflowId();
		return submitWorkflowById(instanceId,entity,urlMap);
	}
	
	/* 将用户定义的任务办理URL处理为流程Execution中的变量URL
	 * @param urlMap
	 * @return
	 */
	private Map<String, String> getProcessUrl(Map<String,String> urlMap){
		Map<String,String> map = new HashMap<String,String>();
		WorkflowDefinition wfd = null;
		for(String wfdName:urlMap.keySet()){
			if(wfdName.equals(WorkflowInstanceService.WF_FORM_URL)) continue;
			wfd = getEnabledHighestVersionDefinition(wfdName);//workflowDefinitionManager.getLatestVersion(wfdName, this.getCompanyId());
			log.debug("url的表单名："+wfdName);
			log.debug("workflowDefinition:"+wfd);
			map.put(CommonStrings.TASK_URL_PREFIX + wfd.getProcessId(), urlMap.get(wfdName));
		}
		return map;
	}
	
	/**
	 * 获取最新流程定义
	 * @param workflowDefinitionName
	 * @param companyId
	 * @return
	 */
	public WorkflowDefinition getLatestVersionWorkflowDefinition(String workflowDefinitionName,Long companyId){
		return workflowDefinitionManager.getLatestVersion(workflowDefinitionName, companyId);
	}
	/**
	 * 获取启用且版本最高的流程定义
	 * @param workflowDefinitionCode
	 * @param companyId
	 * @exception NotFoundEnabledWorkflowDefinitionException if not found started workflowDefinition by workflowDefinitionName
	 * @return 流程定义
	 */
	public com.norteksoft.product.api.entity.WorkflowDefinition getEnabledHighestVersionWorkflowDefinition(String workflowDefinitionCode){
		WorkflowDefinition definition = workflowDefinitionManager.getEnabledHighestVersionWorkflowDefinition(workflowDefinitionCode);
		if(definition==null) throw new NotFoundEnabledWorkflowDefinitionException("not found started workflowDefinition by name:"+workflowDefinitionCode);
		return getWorkflowDefinitionParameter(definition);
	}
	
	/**
	 * 获取启用且版本最高的流程定义
	 * @param workflowDefinitionCode
	 * @param companyId
	 * @exception NotFoundEnabledWorkflowDefinitionException if not found started workflowDefinition by workflowDefinitionName
	 * @return 流程定义
	 */
	private WorkflowDefinition getEnabledHighestVersionDefinition(String workflowDefinitionCode){
		WorkflowDefinition definition = workflowDefinitionManager.getEnabledHighestVersionWorkflowDefinition(workflowDefinitionCode);
		if(definition==null) throw new NotFoundEnabledWorkflowDefinitionException("not found started workflowDefinition by name:"+workflowDefinitionCode);
		return definition;
	}
	/**
	 * 获取启用且版本最高的流程定义
	 * @param workflowDefinitionCode
	 * @param companyId
	 * @exception NotFoundEnabledWorkflowDefinitionException if not found started workflowDefinition by workflowDefinitionName
	 * @return 流程定义
	 */
	private WorkflowDefinition getEnabledHighestVersionDefinitionBySystem(String workflowDefinitionCode,Long systemId){
		WorkflowDefinition definition = workflowDefinitionManager.getEnabledHighestVersionWorkflowDefinitionBySystem(workflowDefinitionCode,systemId);
		if(definition==null) throw new NotFoundEnabledWorkflowDefinitionException("not found started workflowDefinition by name:"+workflowDefinitionCode);
		return definition;
	}
	/**
	 * 获取启用且版本最高的流程定义
	 * @param workflowDefinitionCode
	 * @param companyId
	 * @exception NotFoundEnabledWorkflowDefinitionException if not found started workflowDefinition by workflowDefinitionName
	 * @return 流程定义
	 */
	private WorkflowDefinition getEnabledHighestVersionDefinition(String workflowDefinitionCode,Long companyId){
		Assert.notNull(companyId,"获取启用且版本最高的流程定义时公司id不能为null");
		WorkflowDefinition definition = workflowDefinitionManager.getEnabledHighestVersionWorkflowDefinition(workflowDefinitionCode,companyId);
		if(definition==null){
			log.debug("not found started workflowDefinition by name:"+workflowDefinitionCode);
			throw new NotFoundEnabledWorkflowDefinitionException("not found started workflowDefinition by name:"+workflowDefinitionCode);
		}
		return definition;
	}
	
	private com.norteksoft.product.api.entity.WorkflowDefinition getWorkflowDefinitionParameter(WorkflowDefinition definition){
		com.norteksoft.product.api.entity.WorkflowDefinition wdp=new com.norteksoft.product.api.entity.WorkflowDefinition();
		wdp.setCode(definition.getCode());
		wdp.setId(definition.getId());
		wdp.setName(definition.getName());
		wdp.setVersion(definition.getVersion());
		wdp.setFormCode(definition.getFormCode());
		wdp.setFromVersion(definition.getFromVersion());
		return wdp;
	}
	
	/**
	 * 删除流程实例 。如果业务实体的workflowInfo为空或workflowInfo.workflowId为空，将不会发生任何操作
	 * @param entity 业务实体
	 */
	public void deleteInstance(FormFlowable entity){
		log.debug(LOG_METHOD_BEGIN+"删除实例任务等,WorkflowClientManager+deleteInstance(FormFlowable entity)"+LOG_FLAG);
		log.debug(LOG_CONTENT + "entity"+LOG_FLAG + entity);
		log.debug(LOG_CONTENT + "entity.getWorkflowInfo()"+LOG_FLAG + entity.getWorkflowInfo());
		log.debug(LOG_CONTENT + "entity.getWorkflowInfo().getWorkflowId()"+LOG_FLAG + entity.getWorkflowInfo().getWorkflowId());
		if(entity==null || entity.getWorkflowInfo()==null || entity.getWorkflowInfo().getWorkflowId() == null){
			log.debug(LOG_CONTENT + "该表单未发起对应的流程实例,所以不需删除实例"+LOG_FLAG );
			log.debug(LOG_METHOD_END+"提交流程,WorkflowClientManager+deleteInstance(FormFlowable entity)"+LOG_FLAG);
			return ;
		}
		workflowInstanceManager.deleteWorkflowInstance(workflowInstanceManager.getWorkflowInstance(entity.getWorkflowInfo().getWorkflowId()),false);
		log.debug(LOG_METHOD_END+"删除实例任务等,WorkflowClientManager+deleteInstance(FormFlowable entity)"+LOG_FLAG);
	}
	
	
	
	/**
	 * 删除流程实例
	 * @param workflowId 工作流id
	 */
	public void deleteInstance(String workflowId){
		log.debug(LOG_METHOD_BEGIN+"删除实例任务等,WorkflowClientManager+deleteInstance(String workflowId)"+LOG_FLAG);
		com.norteksoft.wf.engine.entity.WorkflowInstance wfi = workflowInstanceManager.getWorkflowInstance(workflowId);
		log.debug(LOG_CONTENT + "要删除的实例WorkflowInstance"+LOG_FLAG + wfi);
		workflowInstanceManager.deleteWorkflowInstance(wfi,false);
		log.debug(LOG_METHOD_END+"删除实例任务等,WorkflowClientManager+deleteInstance(String workflowId)"+LOG_FLAG);
	}
	
	
	
	 /**
     * 根据TaskId完成任务，没有办理意见
     * @param taskId
     * @param result 
     * @return  false时，需要设置办理人，true时任务完成
     */
    public CompleteTaskTipType completeWorkflowTask(Long taskId, TaskProcessingResult result,String allOriginalUsers){
    	Assert.notNull(taskId,"completeWorkflowTask中，taskId(任务id不能为null");
    	log.debug(LOG_METHOD_BEGIN+"完成任务,WorkflowClientManager+completeWorkflowTask(Long taskId, TaskTransact result,String allOriginalUsers)"+LOG_FLAG);
    	com.norteksoft.task.entity.WorkflowTask task=taskService.getWorkflowTask(taskId);
    	log.debug(LOG_CONTENT + "当前任务"+LOG_FLAG + task);
    	CompleteTaskTipType completeResult=taskService.completeWorkflowTask(task,result,TaskSetting.getTaskSettingInstance().setAllOriginalUsers(allOriginalUsers));
    	log.debug(LOG_CONTENT + "完成任务的返回值"+LOG_FLAG + completeResult);
    	log.debug(LOG_METHOD_END+"完成任务,WorkflowClientManager+completeWorkflowTask(Long taskId, TaskTransact result,String allOriginalUsers)"+LOG_FLAG);
    	return completeResult;
    }
	
	/**
	 * 完成交互的任务
	 * @param taskId
	 * @param operation
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
	 */
	public CompleteTaskTipType completeInteractiveWorkflowTask(Long taskId ,String allOriginalUsers){
    	log.debug(LOG_METHOD_BEGIN+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(Long taskId ,String allOriginalUsers)"+LOG_FLAG);
    	log.debug(LOG_CONTENT + "当前任务Id"+LOG_FLAG + taskId);
    	log.debug(LOG_CONTENT + "原办理人登录名"+LOG_FLAG + allOriginalUsers);
    	CompleteTaskTipType completeResult=taskService.completeInteractiveWorkflowTask(taskId,allOriginalUsers);
    	log.debug(LOG_CONTENT + "完成任务的返回值"+LOG_FLAG + completeResult);
    	log.debug(LOG_METHOD_END+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(Long taskId ,String allOriginalUsers)"+LOG_FLAG);
		return completeResult;
	}
	/**
	 * 完成交互的任务
	 * @param taskId
	 * @param operation
	 * @param transcators 下一环节办理人
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
	 */
	public CompleteTaskTipType completeInteractiveWorkflowTask(Long taskId, Collection<String> transcators,String allOriginalUsers){
		Assert.notNull(taskId,"任务id不能为null");
		log.debug(LOG_METHOD_BEGIN+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(Long taskId, Collection<String> transcators,String allOriginalUsers)"+LOG_FLAG);
    	log.debug(LOG_CONTENT + "当前任务Id"+LOG_FLAG + taskId);
    	log.debug(LOG_CONTENT + "下一环节办理人登录名"+LOG_FLAG + transcators);
    	log.debug(LOG_CONTENT + "原办理人登录名"+LOG_FLAG + allOriginalUsers);
		CompleteTaskTipType completeResult=taskService.completeInteractiveWorkflowTask(taskId, transcators,allOriginalUsers);
		log.debug(LOG_CONTENT + "完成任务的返回值"+LOG_FLAG + completeResult);
    	log.debug(LOG_METHOD_END+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(Long taskId, Collection<String> transcators,String allOriginalUsers)"+LOG_FLAG);
		return completeResult;
	}
	
	/**
	 * 完成交互的任务
	 * @param taskId
	 * @param operation
	 * @param transcators 下一环节办理人
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
	 */
	public CompleteTaskTipType completeInteractiveWorkflowTask(Long taskId,String allOriginalUsers, String... transcators){
		Assert.notNull(taskId,"任务id不能为null");
		log.debug(LOG_METHOD_BEGIN+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(Long taskId,String allOriginalUsers, String... transcators)"+LOG_FLAG);
    	log.debug(LOG_CONTENT + "当前任务Id"+LOG_FLAG + taskId);
    	log.debug(LOG_CONTENT + "下一环节办理人登录名"+LOG_FLAG + transcators);
    	log.debug(LOG_CONTENT + "原办理人登录名"+LOG_FLAG + allOriginalUsers);
		CompleteTaskTipType completeResult= taskService.completeInteractiveWorkflowTask(taskId, allOriginalUsers,transcators);
		log.debug(LOG_CONTENT + "完成任务的返回值"+LOG_FLAG + completeResult);
		log.debug(LOG_METHOD_END+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(Long taskId,String allOriginalUsers, String... transcators)"+LOG_FLAG);
		return completeResult;
	}
	
	@Deprecated
	public CompleteTaskTipType completeInteractiveWorkflowTask(com.norteksoft.task.entity.WorkflowTask task,String allOriginalUsers){
		log.debug(LOG_METHOD_BEGIN+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(WorkflowTask task,String allOriginalUsers)"+LOG_FLAG);
    	log.debug(LOG_CONTENT + "当前任务"+LOG_FLAG + task);
    	log.debug(LOG_CONTENT + "原办理人登录名"+LOG_FLAG + allOriginalUsers);
    	CompleteTaskTipType completeResult= taskService.completeInteractiveWorkflowTask(task,allOriginalUsers);
    	log.debug(LOG_CONTENT + "完成任务的返回值"+LOG_FLAG + completeResult);
    	log.debug(LOG_METHOD_END+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(WorkflowTask task,String allOriginalUsers)"+LOG_FLAG);
		return completeResult;
	}
	/**
	 * 完成交互的任务
	 * @param task
	 * @param operation
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
	 */
	public CompleteTaskTipType completeInteractiveWorkflowTask(WorkflowTask task,String allOriginalUsers){
		log.debug(LOG_METHOD_BEGIN+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(WorkflowTask task,String allOriginalUsers)"+LOG_FLAG);
    	log.debug(LOG_CONTENT + "当前任务"+LOG_FLAG + task);
    	log.debug(LOG_CONTENT + "原办理人登录名"+LOG_FLAG + allOriginalUsers);
    	CompleteTaskTipType completeResult= taskService.completeInteractiveWorkflowTask(BeanUtil.turnToTask(task),allOriginalUsers);
    	log.debug(LOG_CONTENT + "完成任务的返回值"+LOG_FLAG + completeResult);
    	log.debug(LOG_METHOD_END+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(WorkflowTask task,String allOriginalUsers)"+LOG_FLAG);
		return completeResult;
	}
	/**
	 * 完成交互的任务
	 * @param task
	 * @param operation
	 * @param transcators 下一环节办理人
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
	 */
	public CompleteTaskTipType completeInteractiveWorkflowTask(WorkflowTask task,Collection<String> transcators,String allOriginalUsers){
		log.debug(LOG_METHOD_BEGIN+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(WorkflowTask task,Collection<String> transcators,String allOriginalUsers)"+LOG_FLAG);
    	log.debug(LOG_CONTENT + "当前任务"+LOG_FLAG + task);
    	log.debug(LOG_CONTENT + "原办理人登录名"+LOG_FLAG + allOriginalUsers);
		CompleteTaskTipType completeResult= taskService.completeInteractiveWorkflowTask(BeanUtil.turnToTask(task), transcators,allOriginalUsers);
		log.debug(LOG_CONTENT + "完成任务的返回值"+LOG_FLAG + completeResult);
    	log.debug(LOG_METHOD_END+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(WorkflowTask task,Collection<String> transcators,String allOriginalUsers)"+LOG_FLAG);
		return completeResult;
	}
	
	@Deprecated
	public CompleteTaskTipType completeInteractiveWorkflowTask(com.norteksoft.task.entity.WorkflowTask task,Collection<String> transcators,String allOriginalUsers){
		log.debug(LOG_METHOD_BEGIN+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(WorkflowTask task,Collection<String> transcators,String allOriginalUsers)"+LOG_FLAG);
    	log.debug(LOG_CONTENT + "当前任务"+LOG_FLAG + task);
    	log.debug(LOG_CONTENT + "原办理人登录名"+LOG_FLAG + allOriginalUsers);
		CompleteTaskTipType completeResult= taskService.completeInteractiveWorkflowTask(task, transcators,allOriginalUsers);
		log.debug(LOG_CONTENT + "完成任务的返回值"+LOG_FLAG + completeResult);
    	log.debug(LOG_METHOD_END+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(WorkflowTask task,Collection<String> transcators,String allOriginalUsers)"+LOG_FLAG);
		return completeResult;
	}
	
	public CompleteTaskTipType completeInteractiveWorkflowTask(WorkflowTask task,String allOriginalUsers,String... transcators){
		log.debug(LOG_METHOD_BEGIN+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(WorkflowTask task,String allOriginalUsers,String... transcators)"+LOG_FLAG);
    	log.debug(LOG_CONTENT + "当前任务"+LOG_FLAG + task);
    	log.debug(LOG_CONTENT + "原办理人登录名"+LOG_FLAG + allOriginalUsers);
		CompleteTaskTipType completeResult=taskService.completeInteractiveWorkflowTask(BeanUtil.turnToTask(task), allOriginalUsers,transcators);
		log.debug(LOG_CONTENT + "完成任务的返回值"+LOG_FLAG + completeResult);
    	log.debug(LOG_METHOD_END+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(WorkflowTask task,String allOriginalUsers,String... transcators)"+LOG_FLAG);
		return completeResult;
	}
	
	@Deprecated
	public CompleteTaskTipType completeInteractiveWorkflowTask(com.norteksoft.task.entity.WorkflowTask task,String allOriginalUsers,String... transcators){
		log.debug(LOG_METHOD_BEGIN+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(WorkflowTask task,String allOriginalUsers,String... transcators)"+LOG_FLAG);
    	log.debug(LOG_CONTENT + "当前任务"+LOG_FLAG + task);
    	log.debug(LOG_CONTENT + "原办理人登录名"+LOG_FLAG + allOriginalUsers);
		CompleteTaskTipType completeResult=taskService.completeInteractiveWorkflowTask(task, allOriginalUsers,transcators);
		log.debug(LOG_CONTENT + "完成任务的返回值"+LOG_FLAG + completeResult);
    	log.debug(LOG_METHOD_END+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(WorkflowTask task,String allOriginalUsers,String... transcators)"+LOG_FLAG);
		return completeResult;
	}
	
	
	/**
	 * 完成交互的任务
	 * @param task
	 * @param operation
	 * @param transcators 下一环节办理人
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
	 */
	public CompleteTaskTipType completeInteractiveWorkflowTask(WorkflowTask task,String transcators,String allOriginalUsers){
		log.debug(LOG_METHOD_BEGIN+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(WorkflowTask task,String transcators,String allOriginalUsers)"+LOG_FLAG);
    	log.debug(LOG_CONTENT + "当前任务"+LOG_FLAG + task);
    	log.debug(LOG_CONTENT + "原办理人登录名"+LOG_FLAG + allOriginalUsers);
		CompleteTaskTipType completeResult=taskService.completeInteractiveWorkflowTask(BeanUtil.turnToTask(task), transcators,allOriginalUsers);
		log.debug(LOG_CONTENT + "完成任务的返回值"+LOG_FLAG + completeResult);
    	log.debug(LOG_METHOD_END+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(WorkflowTask task,String transcators,String allOriginalUsers)"+LOG_FLAG);
		return completeResult;
	}
	
	@Deprecated
	public CompleteTaskTipType completeInteractiveWorkflowTask(com.norteksoft.task.entity.WorkflowTask task,String transcators,String allOriginalUsers){
		log.debug(LOG_METHOD_BEGIN+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(WorkflowTask task,String transcators,String allOriginalUsers)"+LOG_FLAG);
    	log.debug(LOG_CONTENT + "当前任务"+LOG_FLAG + task);
    	log.debug(LOG_CONTENT + "原办理人登录名"+LOG_FLAG + allOriginalUsers);
		CompleteTaskTipType completeResult=taskService.completeInteractiveWorkflowTask(task, transcators,allOriginalUsers);
		log.debug(LOG_CONTENT + "完成任务的返回值"+LOG_FLAG + completeResult);
    	log.debug(LOG_METHOD_END+"完成交互任务,WorkflowClientManager+completeInteractiveWorkflowTask(WorkflowTask task,String transcators,String allOriginalUsers)"+LOG_FLAG);
		return completeResult;
	}
    
    /**
     * 完成任务 
     */
	public CompleteTaskTipType completeWorkflowTask(WorkflowTask task, TaskProcessingResult result,String allOriginalUsers){
		log.debug(LOG_METHOD_BEGIN+"完成任务,WorkflowClientManager+completeWorkflowTask(WorkflowTask task, TaskTransact result,String allOriginalUsers)"+LOG_FLAG);
		CompleteTaskTipType completeResult=taskService.completeWorkflowTask(BeanUtil.turnToTask(task),result,TaskSetting.getTaskSettingInstance().setAllOriginalUsers(allOriginalUsers));
		log.debug(LOG_CONTENT + "完成任务的返回值"+LOG_FLAG + completeResult);
		log.debug(LOG_METHOD_BEGIN+"完成任务,WorkflowClientManager+completeWorkflowTask(WorkflowTask task, TaskTransact result,String allOriginalUsers)"+LOG_FLAG);
		return completeResult;
    }
    
	@Deprecated
	public CompleteTaskTipType completeWorkflowTask(com.norteksoft.task.entity.WorkflowTask task, TaskProcessingResult result,String allOriginalUsers){
		log.debug(LOG_METHOD_BEGIN+"完成任务,WorkflowClientManager+completeWorkflowTask(WorkflowTask task, TaskTransact result,String allOriginalUsers)"+LOG_FLAG);
		CompleteTaskTipType completeResult=taskService.completeWorkflowTask(task,result,TaskSetting.getTaskSettingInstance().setAllOriginalUsers(allOriginalUsers));
		log.debug(LOG_CONTENT + "完成任务的返回值"+LOG_FLAG + completeResult);
		log.debug(LOG_METHOD_BEGIN+"完成任务,WorkflowClientManager+completeWorkflowTask(WorkflowTask task, TaskTransact result,String allOriginalUsers)"+LOG_FLAG);
		return completeResult;
    }
    
    
    /**
     * 查询下环节任务的办理人
     * @param taskId
     * @return Map<String[办理模式，任务名]  ,List<String[用户名称，用户登录名称] >>
     */
    public Map<String[], List<String[]>> getNextTasksCandidates(Long taskId){
    	Assert.notNull(taskId,"查询下环节任务的办理人时，任务id不能为null");
    	return taskService.getNextTasksCandidates(taskService.getWorkflowTask(taskId));
    }
    
    /**
     * 根据任务id获得流程定义的id. 如果对应的任务不存在，则返回null
     * @param taskId 任务id
     * @return 流程定义id
     */
    public Long getWorkflowDefinitionIdByTask(Long taskId){
    	com.norteksoft.task.entity.WorkflowTask task = taskService.getWorkflowTask(taskId);
    	if(task==null) return null;
    	com.norteksoft.wf.engine.entity.WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
    	Assert.notNull(instance,"任务id为"+taskId+"的任务存在，而实例却不存在。");
    	return instance.getWorkflowDefinitionId();
    }
    
    /**
     * 用定义id查询流程定义
     * @param workflowDefinitionId 定义id
     * @return 流程定义
     */
    public com.norteksoft.product.api.entity.WorkflowDefinition getWorkflowDefinition(Long workflowDefinitionId){
    	WorkflowDefinition wfd = workflowDefinitionManager.getWfDefinition(workflowDefinitionId);
    	if(wfd == null) throw new RuntimeException("没有查询到流程定义，流程定义ID["+workflowDefinitionId+"]");
    	return getWorkflowDefinitionParameter(wfd);
    }
    
    /**
     * 用定义id查询流程定义
     * @param workflowDefinitionId 定义id
     * @return 流程定义
     */
    public WorkflowDefinition getWorkflowDefinitionById(Long workflowDefinitionId){
    	Assert.notNull(workflowDefinitionId,"流程定义id不能为null");
    	return workflowDefinitionManager.getWfDefinition(workflowDefinitionId);
    }
    
    /**
     * 根据流程实例ID查询流程定义.如果对应的工作流实例不存在，则返回null；
     * @param workflowId
     * @return 流程定义
     */
    public WorkflowDefinition getWorkflowDefinitionByWorkflowId(String workflowId){
    	WorkflowInstance workflow = getInstance(workflowId);
    	log.debug("工作流实例："+workflow);
    	if(workflow==null) return null;
    	log.debug("对应流程定义id为："+workflow.getWorkflowDefinitionId());
    	return getWorkflowDefinitionById(workflow.getWorkflowDefinitionId());
    }
    
    /**
     * 根据实例id查询流程实例
     * @param workflowId 流程实例的唯一标识
     * @return 流程实例
     */
    public WorkflowInstance getInstance(String workflowId){
    	return BeanUtil.turnToModelWorkflowInstance(workflowInstanceManager.getWorkflowInstance(workflowId));
    }
    
    /**
     * 查询流程定义
     * @param processId 流程id
     * @return 流程定义
     */
    public WorkflowDefinition getWorkflowDefinition(String processId){
    	return workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
    }
    
    /**
     * 根据task查询流程实例表单ID.
     * @param taskId 任务id
     * @return 表单id
     */
    public Long getFormIdByTask(Long taskId){
    	com.norteksoft.task.entity.WorkflowTask task = taskService.getWorkflowTask(taskId);
    	if(task==null) return null;
    	return getFormIdByTask(task);
    }
    
    /**
     * 根据task查询流程实例表单ID
     * @param task 任务
     * @return 表单id
     */
    public Long getFormIdByTask(WorkflowTask task){
    	com.norteksoft.wf.engine.entity.WorkflowInstance workflow = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
    	if(workflow==null) return null;
    	return workflow.getFormId();
    }
    
    @Deprecated
    public Long getFormIdByTask(com.norteksoft.task.entity.WorkflowTask task){
    	com.norteksoft.wf.engine.entity.WorkflowInstance workflow = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
    	if(workflow==null) return null;
    	return workflow.getFormId();
    }
    /**
     * 根据task查询业务实体的ID
     * @param taskId 任务id
     * @return 业务实体的ID
     */
    public Long getDataIdByTask(Long taskId){
    	com.norteksoft.task.entity.WorkflowTask task = taskService.getWorkflowTask(taskId);
    	return getDataIdByTask(task);
    }
    /**
     * 根据task查询流程业务实体的ID
     * @param task 任务
     * @return 业务实体的ID
     */
    public Long getDataIdByTask(WorkflowTask task){
    	Assert.notNull(task, "任务不能为null");
    	com.norteksoft.wf.engine.entity.WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
    	if(instance==null){
			log.debug("WorkflowInstance实体不能为null");
			throw new RuntimeException("任务不能为null");
		}
    	return instance.getDataId();
    }
    
    @Deprecated
    public Long getDataIdByTask(com.norteksoft.task.entity.WorkflowTask task){
    	Assert.notNull(task, "任务不能为null");
    	com.norteksoft.wf.engine.entity.WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
    	if(instance==null){
			log.debug("WorkflowInstance实体不能为null");
			throw new RuntimeException("任务不能为null");
		}
    	return instance.getDataId();
    }
    /**
     * 根据task查询流程业务实体的ID
     * @param task 任务
     * @param companyId
     * @return 业务实体的ID
     */
    public Long getDataIdByTask(WorkflowTask task,Long companyId){
    	Assert.notNull(task, "任务不能为null");
    	com.norteksoft.wf.engine.entity.WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId(),companyId);
    	if(instance==null){
			log.debug("WorkflowInstance实体不能为null");
			throw new RuntimeException("WorkflowInstance实体不能为null");
		}
    	return instance.getDataId();
    }
    
    @Deprecated
    public Long getDataIdByTask(com.norteksoft.task.entity.WorkflowTask task,Long companyId){
    	Assert.notNull(task, "任务不能为null");
    	com.norteksoft.wf.engine.entity.WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId(),companyId);
    	if(instance==null){
			log.debug("WorkflowInstance实体不能为null");
			throw new RuntimeException("WorkflowInstance实体不能为null");
		}
    	return instance.getDataId();
    }
    
    /**
     * 通过流程定义ID查询所有的环节名称
     * @param workflowDefinitionId 定义id
     * @return 环节名称封装成的集合
     */
    public List<String> getTachesByProcessDefinition(Long workflowDefinitionId){
    	WorkflowDefinition wfd = workflowDefinitionManager.getWfDefinition(workflowDefinitionId);
    	return getTachesByprocessId(wfd.getProcessId());
    }
    
    /**
     * 通过流程定义ID查询所有的环节名称
     * @param jbpmDefinitionId 
     * @return 环节名封装的集合
     */
    public List<String> getTachesByprocessId(String processId){
		return DefinitionXmlParse.getTaskNames(processId);
    }
    
    /**
     * 完成任务并加签(目前收文使用)
     * @param taskId
     * @param users
     */
    public void additional(Long taskId, List<String> users, Integer grade){
    	taskService.generateTask(taskId, users,TaskSource.ADD_SIGN);
    }
    
    /**
     * 加签
     * @param taskId 任务id
     * @param users 需要加签的人
     */
    @Deprecated
    public void additional(Long taskId, Collection<String> users){
    	taskService.generateTask(taskId, users,TaskSource.ADD_SIGN);
    }
    
    /**
     * 加签
     * @param taskId 任务id
     * @param users 需要加签的人
     */
    public void addSign(Long taskId, Collection<String> users){
    	addSigner(taskId, users);
    }
    
    public void addSigner(Long taskId, Collection<String> users){
    	Assert.notNull(taskId,"加签时，任务id不能为null");
    	taskService.generateTask(taskId, users,TaskSource.ADD_SIGN);
    }
    
    /**
     * 加签
     * @param taskId 任务id
     * @param users 需要加签的人
     */
    @Deprecated
    public void additional(Long taskId, String... users){
    	taskService.generateTask(taskId, Arrays.asList(users),TaskSource.ADD_SIGN);
    }
    
    /**
     * 加签
     * @param taskId 任务id
     * @param users 需要加签的人
     */
    public void addSign(Long taskId, String... users){
    	addSigner(taskId, Arrays.asList(users));
    }
    
    public void addSigner(Long taskId,  String... users){
    	Assert.notNull(taskId,"加签时任务id不能为null");
    	taskService.generateTask(taskId, Arrays.asList(users),TaskSource.ADD_SIGN);
    }
    
    /**
     * 减签
     * @param taskId 任务id
     * @param users 需要减签的人
     */
    public void reduceHandlers(Long taskId,Collection<String> users){
    	removeSigner(taskId, users);
    }
    
    public void removeSigner(Long taskId,Collection<String> users){
    	Assert.notNull(taskId,"减签时任务id不能为null");
    	taskService.deleteCountersignHandler(taskId, users);
    }
    
    /**
     * 减签
     * @param taskId 任务id
     * @param users 需要减签的人
     */
    public void reduceHandlers(Long taskId,String... users){
    	removeSigner(taskId, Arrays.asList(users));
    }
    
    public void removeSigner(Long taskId,String... users){
    	Assert.notNull(taskId,"减签时任务id不能为null");
    	taskService.deleteCountersignHandler(taskId, Arrays.asList(users));
    }
    
    /**
     * 获得该会签环节的会签办理人
     * @param taskId
     * @param handlingState 该办理人对任务的完成状态  {@link com.norteksoft.task.entity.Task#COMPLETED}}、 {@link com.norteksoft.task.entity.Task#WAIT_TRANSACT}}
     * @return 办理人登录名的集合
     */
    public List<String> getCountersignHandlers(Long taskId,Integer handlingState){
    	return getCountersignTransactors(taskId,handlingState);
    }
    
    public List<String> getCountersignTransactors(Long taskId,Integer handlingState){
    	Assert.notNull(taskId,"获得会签环节的会签办理人时任务id不能为null");
    	return taskService.getCountersignsHandler(taskId,handlingState);
    }
    
	/**
	 * 取回任务
	 */
	public String retrieve(Long taskId){
		Assert.notNull(taskId,"取回任务时任务id不能为null");
		return taskService.retrieve(taskId);
	}

	/**
	 * 给流程实例设置数据ID
	 * @param wfInstanceId
	 * @param id
	 */
	public void setDataId(String wfInstanceId, Long id) {
		com.norteksoft.wf.engine.entity.WorkflowInstance workflowInstance = workflowInstanceManager.getWorkflowInstance(wfInstanceId);
		if(workflowInstance==null||workflowInstance.getDataId()!=null) return;
		workflowInstance.setDataId(id);
		workflowInstanceManager.saveWorkflowInstance(workflowInstance);
	}

	
	  /**
     * 根据task查询业务实体的ID
     * @param taskId 任务id
     * @return 业务实体的ID
     */
    public Long getFormFlowableIdByTask(Long taskId){
    	com.norteksoft.task.entity.WorkflowTask task = taskService.getWorkflowTask(taskId);
    	return getDataIdByTask(task);
    }
    /**
     * 根据task查询流程业务实体的ID
     * @param task 任务
     * @return 业务实体的ID
     */
    public Long getFormFlowableIdByTask(WorkflowTask task){
    	com.norteksoft.wf.engine.entity.WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
    	return instance.getDataId();
    }
	
    @Deprecated
    public Long getFormFlowableIdByTask(com.norteksoft.task.entity.WorkflowTask task){
    	com.norteksoft.wf.engine.entity.WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
    	return instance.getDataId();
    }
	
	/**
     * 将任务交办给指定的人员
     * @param taskId 需指派的任务
     * @param transactor 指定的人员
     */
	public void assign(Long taskId, String transactor){
		Assert.notNull(taskId,"将任务交办或指派给指定的人员时，任务id不能为null");
		taskService.assign(taskId, transactor);
	}
	
	/**
     * 将任务交办给指定的人员
     * @param taskId 需指派的任务
     * @param transactor 指定的人员
     */
	public void assign(Long taskId, List<String> transactors){
		taskService.assign(taskId, transactors);
	}
	
	/**
	 * 流程实例能退回到的环节名称
	 * @param taskId 任务ID
	 * @return List<String> size等于0时表示当前不能退回到任何环节
	 */
	@Deprecated
	public List<String> canBackNames(Long taskId){
		return getReturnableTaskNames(taskId);
	}
	
	/**
	 * 流程实例能退回到的环节名称
	 * @param taskId 任务ID
	 * @return List<String> size等于0时表示当前不能退回到任何环节
	 */
	@Deprecated
	public List<String> backToTaskNames(Long taskId){
		return getReturnableTaskNames(taskId);
	}
	
	public List<String> getReturnableTaskNames(Long taskId){
		Assert.notNull(taskId,"获得流程实例能退回到的环节名称时，任务id不能为null");
		com.norteksoft.task.entity.WorkflowTask task = taskService.getWorkflowTask(taskId);
		Assert.notNull(task,"获得流程实例能退回到的环节名称时，任务不能为null");
		return taskService.canBackNames(task.getProcessInstanceId());
	}
	
	/**
	 * 退回到某环节
	 * @param taskId 任务ID
	 * @param backTo 退回到的环节名称
	 */
	public void goBack(Long taskId, String backTo){
		returnTaskTo(taskId, backTo);
	}
	
	public void returnTaskTo(Long taskId, String taskName){
		Assert.notNull(taskId,"退回到某环节时，任务id不能为null");
		WorkflowTask task = this.getTask(taskId);
		Assert.notNull(task,"退回到某环节时，任务不能为null");
		taskService.goBack(task.getProcessInstanceId(), taskName);
	}
	
	/**
	 * 在环节办理时，当前环节办理人是否有权删除流程实例
	 * @param workflowId
	 * @param taskName
	 * @return true 为可以，false为不可以
	 */
	public boolean canDeleteInstanceInTask(FormFlowable entity, String taskName){
		String workflowId=entity.getWorkflowInfo()==null?null:entity.getWorkflowInfo().getWorkflowId();
		return canDeleteInstanceInTask(workflowId,taskName);
	}
	/**
	 * 在环节办理时，当前环节办理人是否有权删除流程实例
	 * @param workflowId
	 * @param taskName
	 * @return true 为可以，false为不可以
	 */
	public boolean canDeleteInstanceInTask(String instanceId, String taskName){
		if(StringUtils.isNotEmpty(instanceId)){
			com.norteksoft.wf.engine.entity.WorkflowInstance workflow = workflowInstanceManager.getWorkflowInstance(instanceId);
			if(workflow==null) return false;
			return taskService.canDeleteByTask(workflow, taskName);
		}
		return false;
	}
	
	/**
	 * 根据流程名称查询第一环节的字段编辑权限,以JSON格式返回
	 * @param definitionCode 流程编号
	 * @return json格式的字段编辑权限
	 */
	public String getFieldPermissionNotStarted(String definitionCode){
		WorkflowDefinition definition = this.getEnabledHighestVersionDefinition(definitionCode);
		return workflowRightsManager.getFieldPermissionNotStarted(definition);
	}
	
	public String getFieldPermissionNotStarted(String definitionCode, Integer version){
		WorkflowDefinition definition = getWfdByCodeAndVersion(definitionCode, version,ContextUtils.getCompanyId(),ContextUtils.getSystemId());
		return workflowRightsManager.getFieldPermissionNotStarted(definition);
	}
	
	
	/**
	 * 根据流程定义查询第一个环节的权限
	 * @param definitionId
	 * @return 返回json格式表示的字段可编辑状态信息
	 */
	public String getFieldPermissionNotStarted(Long definitionId){
		WorkflowDefinition definition = workflowDefinitionManager.getWfDefinition(definitionId);
		return workflowRightsManager.getFieldPermissionNotStarted(definition);
	}
	
	/**
	 * 查询流程中环节的字段编辑权限
	 * @param entity
	 * @param task
	 * @return 返回json格式表示的字段可编辑状态信息
	 */
	public String getFieldPermission(Long taskId){
		com.norteksoft.task.entity.WorkflowTask task = taskService.getTask(taskId);
		if(viewForm(task))return getFieldPermission(false);
		return workflowRightsManager.getFieldPermission(task);
	}
	/**
	 * 当前表单是否是查看
	 * @param task
	 * @return
	 */
	private boolean viewForm(WorkflowTask task){
		return task.getProcessingMode()==TaskProcessingMode.TYPE_READ||task.getActive().equals(TaskState.COMPLETED.getIndex())||task.getActive().equals(TaskState.ASSIGNED.getIndex())
				||task.getActive().equals(TaskState.CANCELLED.getIndex())||task.getActive().equals(TaskState.DRAW_WAIT.getIndex());
	}
	@Deprecated
	private boolean viewForm(com.norteksoft.task.entity.WorkflowTask task){
		return task.getProcessingMode()==TaskProcessingMode.TYPE_READ||task.getActive().equals(TaskState.COMPLETED.getIndex())||task.getActive().equals(TaskState.ASSIGNED.getIndex())
				||task.getActive().equals(TaskState.CANCELLED.getIndex())||task.getActive().equals(TaskState.DRAW_WAIT.getIndex());
	}
	
	/**
	 * 所有字段可编辑状态信息查询
	 * @param editable 当editable为false时 表示所有字段都禁止填写
	 * @return 返回json格式表示的字段可编辑状态信息
	 */
	public String getFieldPermission(boolean editable){
		return workflowRightsManager.getFieldPermission(editable);
	}
	//======================================================================================
	
	
	/**
	 * 根据流程名称查询最新的流程定义表单
	 * @param workflowDefinitionName
	 * @return html
	 */
    public String getLastHtmlByWorkflowDefinitionName(String processDefinitionName){
		return getLastHtmlByWorkflowDefinitionName(processDefinitionName, null);
	}
	
    /**
     *  @return html
     */
    public String getLastHtmlByWorkflowDefinitionName(String processDefinitionName, FormFlowable entity){
		return taskService.getLastHtmlByWorkflowDefinitionName(entity, processDefinitionName);
	}

    /**
     * 通过自定义实体获取填写了数据的HTML片段
     * @param entity extends FormFlowable
     * @param htmlParameterName html片段中变量的key eg: obj.name,则htmlParameterName为obj
     * @return
     */
    public String getHtmlByData(FormFlowable entity){
		return taskService.getHtmlByData(entity);
	}
	private List<com.norteksoft.product.api.entity.Opinion> getOpinions(List<Opinion> opinions){
		List<com.norteksoft.product.api.entity.Opinion> opins=new ArrayList<com.norteksoft.product.api.entity.Opinion>();
		for(Opinion opinion:opinions){
			opins.add(getOpinion(opinion));
		}
		return opins;
	}
	
	private com.norteksoft.product.api.entity.Opinion getOpinion(Opinion opinion){
		com.norteksoft.product.api.entity.Opinion opin=new com.norteksoft.product.api.entity.Opinion(opinion.getId(),opinion.getOpinion(),opinion.getCreatedTime(),opinion.getTaskId(),opinion.getCustomField(),opinion.getTaskName(),opinion.getTaskCode(),opinion.getTransactor());
		opin.setDepartmentName(opinion.getDepartmentName());
		com.norteksoft.acs.entity.organization.User user=acsUtils.getUser(opinion.getCompanyId(), opin.getTransactor());
		if(user!=null)opin.setTransactorName(user.getName());
		opin.setDelegateFlag(opinion.getDelegateFlag());
		return opin;
	}
	/**
	 * 查询表单的办理意见
	 * @param workflowId
	 */
	public List<com.norteksoft.product.api.entity.Opinion> getOpinions(FormFlowable entity) {
		if(entity == null) throw new RuntimeException("没有给定查询办理意见的查询条件：流程流转实体(FormFlowable)");
		if(entity.getWorkflowInfo() == null) throw new RuntimeException("流程流转实体(FormFlowable)[id:"+entity.getId()+"]还没有启动流程(WorkflowInfo为null).");
		return getOpinions(workflowInstanceManager.getOpinionsByInstanceId(entity.getWorkflowInfo().getWorkflowId(),getCompanyId(entity)));
	}
	/**
	 * 查询表单的办理意见
	 * @param workflowId
	 */
	public List<Opinion> getOpinions(String workflowId) {
		com.norteksoft.wf.engine.entity.WorkflowInstance wi=workflowInstanceManager.getWorkflowInstance(workflowId);
		return workflowInstanceManager.getOpinionsByInstanceId(workflowId,wi.getCompanyId());
	}
	
	public List<com.norteksoft.product.api.entity.Opinion> getOpinions(Long taskId) {
		com.norteksoft.task.entity.WorkflowTask task=taskService.getWorkflowTask(taskId);
		if(task == null) throw new RuntimeException("没有查询到任务，任务ID["+taskId+"]");
		return getOpinions(workflowInstanceManager.getOpinions(taskId,task.getCompanyId()));
	}
	/**
	 * 查询整个实例中具体办理模式的附件
	 * @param workflowId
	 * @return 附件列表
	 */
	public List<Opinion> getOpinions(String workflowId, TaskProcessingMode taskMode){
		com.norteksoft.wf.engine.entity.WorkflowInstance wi=workflowInstanceManager.getWorkflowInstance(workflowId);
		if(wi == null) throw new RuntimeException("没有查询到流程实例，实例ID["+workflowId+"]");
		return this.workflowInstanceManager.getOpinions(workflowId, wi.getCompanyId(), taskMode);
	}
	
	/**
	 * 查询整个实例中具体办理模式的附件
	 * @param entity
	 * @return  附件列表
	 */
	public List<com.norteksoft.product.api.entity.Opinion> getOpinions(FormFlowable entity,TaskProcessingMode taskMode){
		Assert.notNull(entity,"查询意见列表的流程实体不可为空");
		Assert.notNull(taskMode,"查询意见列表的环节办理方式不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getOpinions(this.getOpinions(entity.getWorkflowInfo().getWorkflowId(),   taskMode));
	}
	
	
	/**
	 * 查询整个实例中具体环节的附件
	 * @param workflowId
	 * @return 附件列表
	 */
	public List<Opinion> getOpinions(String workflowId, String taskName){
		com.norteksoft.wf.engine.entity.WorkflowInstance wi=workflowInstanceManager.getWorkflowInstance(workflowId);
		if(wi == null) throw new RuntimeException("没有查询到流程实例，实例ID["+workflowId+"]");
		return this.workflowInstanceManager.getOpinions(workflowId, wi.getCompanyId(), taskName);
	}
	
	public List<com.norteksoft.product.api.entity.Opinion> getOpinions(FormFlowable entity,String taskNam){
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(taskNam,"环节名称不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getOpinions(getOpinions(entity.getWorkflowInfo().getWorkflowId(),  taskNam));
	}
	

	public List<Opinion> getOpinionsExceptTaskMode(String workflowId,
			TaskProcessingMode taskMode) {
		com.norteksoft.wf.engine.entity.WorkflowInstance wi=workflowInstanceManager.getWorkflowInstance(workflowId);
		if(wi == null) throw new RuntimeException("没有查询到流程实例，实例ID["+workflowId+"]");
		return workflowInstanceManager.getOpinionsExceptTaskMode(workflowId, wi.getCompanyId(), taskMode);
	}

	public List<com.norteksoft.product.api.entity.Opinion> getOpinionsExceptTaskMode(FormFlowable entity,
			TaskProcessingMode taskMode) {
		return getOpinionsExcludeTaskMode(entity, taskMode);
	}
	
	public List<com.norteksoft.product.api.entity.Opinion> getOpinionsExcludeTaskMode(FormFlowable entity,TaskProcessingMode taskMode){
		Assert.notNull(entity,"查询意见列表的流程实体不可为空");
		Assert.notNull(taskMode,"查询意见列表的环节办理方式不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getOpinions(getOpinionsExceptTaskMode(entity.getWorkflowInfo().getWorkflowId(),taskMode));
	}


	public List<com.norteksoft.product.api.entity.Opinion> getOpinionsExceptTaskName(FormFlowable entity,
			String taskName) {
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getOpinions(workflowInstanceManager.getOpinionsExceptTaskName(entity.getWorkflowInfo().getWorkflowId(), getCompanyId(entity), taskName));
	}

	/**
	 * 查询表单的办理意见
	 * @param workflowId
	 */
	public com.norteksoft.product.api.entity.Opinion getOpinionById(Long opinionId) {
		Assert.notNull(opinionId,"意见ID不能为空");
		Opinion op = workflowInstanceManager.getOpinionsById(opinionId);
		Assert.notNull(op,"没有查询到意见，意见ID["+opinionId+"]");
		return getOpinion(op);
	}

	/**
	 * 保存意见
	 * @param opinion
	 */
	public void saveOpinion(com.norteksoft.product.api.entity.Opinion opinionp){
		Opinion opinion=null;
		if(opinionp.getId()==null){
			opinion=new Opinion();
			opinion.setCreatedTime(new Date());
		}else{
			opinion=workflowInstanceManager.getOpinionsById(opinionp.getId());
		}
		opinion.setCustomField(opinionp.getCustomField());
		opinion.setOpinion(opinionp.getOpinion());
		if(opinionp.getTaskId()!=null){
			WorkflowTask task = this.getTask(opinionp.getTaskId());
			opinion.setTaskMode(task.getProcessingMode());
			opinion.setTaskName(task.getName());
			opinion.setCompanyId(task.getCompanyId());
			opinion.setWorkflowId(task.getProcessInstanceId());
			opinion.setTaskId(opinionp.getTaskId());
			opinion.setTransactor(task.getTransactor());
			opinion.setDelegateFlag(StringUtils.isEmpty(task.getTrustor())?false:true);
			Long mainDepartmentId = acsUtils.getUserByLoginName(task.getTransactor()).getMainDepartmentId();
			if(mainDepartmentId!=null){
				Department dept=acsUtils.getDepartmentById(mainDepartmentId);
				opinion.setDepartmentName(dept.getName());
			}
			opinion.setTaskCode(task.getCode());
		}
		workflowInstanceManager.saveOpinion(opinion);
	}
	
	/**
	 * 删除意见
	 * @param opinionId
	 */
	public void deleteOpinion(Long opinionId) {
		workflowInstanceManager.deleteOpinion(opinionId);
	}

	/**
	 * 查询表单的附件列表
	 * @param loanForm
	 * @return
	 */
	public List<WorkflowAttachment> getAttachments(String workflowId) {
		com.norteksoft.wf.engine.entity.WorkflowInstance wi=workflowInstanceManager.getWorkflowInstance(workflowId);
		return workflowInstanceManager.getAttachments(workflowId,wi.getCompanyId());
	}
	
	
	public List<com.norteksoft.product.api.entity.WorkflowAttachment> getAttachments(Long taskId) {
		Assert.notNull(taskId, "taskId不能为null");
		Assert.notNull(getLoginName(), "当前登录名不可为null");
		com.norteksoft.task.entity.WorkflowTask task=taskService.getTask(taskId);
		List<WorkflowAttachment> attchments=workflowInstanceManager.getAttachments(taskId,task.getCompanyId());
		boolean delRight=attachmentDeleteRight(taskId);
		for(WorkflowAttachment att:attchments){
			//用户有创建附件权限,即使没有删除权限也可删除自己当前环节创建的附件;用户有删除权限,可删除当前环节的所有附件
			if(getLoginName().equals(att.getTransactor())||delRight){
				att.setDeleteSetting(true);
			}
		}
		return getAttachments(attchments);
	}
	
	/**
	 * 查询表单的附件列表
	 * @param loanForm
	 * @return
	 */
	public List<com.norteksoft.product.api.entity.WorkflowAttachment> getAttachments(FormFlowable entity) {
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		ThreadParameters params=new ThreadParameters(getCompanyId(entity));
		ParameterUtils.setParameters(params);
		return getAllAttachments(entity.getWorkflowInfo().getWorkflowId());
	}
	
	/**
	 * 查询整个实例中具体办理模式的附件
	 * @param workflowId
	 * @return 附件列表
	 */
	public List<WorkflowAttachment> getAttachments(String workflowId, TaskProcessingMode taskMode){
		return workflowAttachmentManager.getAttachments(workflowId, taskMode);
	}
	
	/**
	 * 查询整个实例中具体办理模式的附件
	 * @param entity
	 * @return  附件列表
	 */
	public List<com.norteksoft.product.api.entity.WorkflowAttachment> getAttachments(FormFlowable entity,TaskProcessingMode taskMode){
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(taskMode,"任务模式不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getAllAttachments(entity.getWorkflowInfo().getWorkflowId());
	}
	
	
	/**
	 * 查询整个实例中具体环节的附件
	 * @param workflowId
	 * @return 附件列表
	 */
	public List<WorkflowAttachment> getAttachments(String workflowId, String taskName){
		return  workflowAttachmentManager.getAttachments(workflowId, taskName);
	}
	
	/**
	 * 查询整个实例中具体环节的附件
	 * @param entity
	 * @return  附件列表
	 */
	public List<com.norteksoft.product.api.entity.WorkflowAttachment> getAttachments(FormFlowable entity,String taskName){
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(taskName,"任务名称不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getAllAttachments(entity.getWorkflowInfo().getWorkflowId(),taskName);
	}

	/**
	 * 保存附件
	 * @param attachment
	 */
	public void saveAttachment(com.norteksoft.product.api.entity.WorkflowAttachment doc){
		WorkflowAttachment document=null;
		Assert.notNull(doc, "附件workflowAttachment不能为null");
		if(doc.getId()==null){
			document=new WorkflowAttachment();
			document.setCreatedTime(new Date());
		}else{
			document=workflowInstanceManager.getAttachment(doc.getId());
			if(document==null){
				log.debug("附件不能为null");
				throw new RuntimeException("附件不能为null");
			}
		}
		document.setCustomField(doc.getCustomField());
		document.setFileName(doc.getFileName());
		document.setFileSize(doc.getFileSize());
		document.setFileType(doc.getFileType());
		if(doc.getTaskId()!=null){
			com.norteksoft.task.entity.WorkflowTask task = taskService.getTask(doc.getTaskId());
			if(task==null){
				log.debug("任务不能为null");
				throw new RuntimeException("任务不能为null");
			}
			document.setTaskId(doc.getTaskId());
			document.setTaskMode(task.getProcessingMode());
			document.setTaskName(task.getName());
			document.setWorkflowId(task.getProcessInstanceId());
			document.setCompanyId(task.getCompanyId());
		}
		FileService fileService =(FileService)ContextUtils.getBean("fileService");
		document.setFilePath(fileService.saveFile(doc.getFileBody()));
		workflowInstanceManager.saveAttachment(document);
	}
	
	
	/**
	 * 删除附件
	 * @param attachmentId
	 */
	public void deleteAttachment(Long attachmentId){
		workflowInstanceManager.deleteAttachment(attachmentId);
	}
	/**
	 * 获得附件
	 * @param attachmentId
	 * @return 附件实体
	 */
	public com.norteksoft.product.api.entity.WorkflowAttachment getAttachment(Long attachmentId){
		return getAttach(workflowInstanceManager.getAttachment(attachmentId));
	}
	
	private com.norteksoft.product.api.entity.WorkflowAttachment getAttachment(WorkflowAttachment doc){
		com.norteksoft.product.api.entity.WorkflowAttachment docment=new com.norteksoft.product.api.entity.WorkflowAttachment();
		docment.setId(doc.getId());
		docment.setFileName(doc.getFileName());
		docment.setFileSize(doc.getFileSize());
		docment.setFileType(doc.getFileType());
		docment.setTaskId(doc.getTaskId());
		docment.setDepartmentName(doc.getDepartmentName());
		docment.setTransactor(doc.getTransactor());
		docment.setDeleteSetting(doc.getDeleteSetting());
		docment.setTaskName(doc.getTaskName());
		docment.setWorkflowId(doc.getWorkflowId());
		docment.setFilePath(doc.getFilePath());
//		//得到文件内容
//		WorkflowAttachmentFile file=getAttachmentFile(doc.getId());
//		docment.setFileBody(file.getContent());
		docment.setCreateDate(doc.getCreatedTime());
		return docment;
	}
	
	private com.norteksoft.product.api.entity.WorkflowAttachment getAttach(WorkflowAttachment doc){
		com.norteksoft.product.api.entity.WorkflowAttachment docment=new com.norteksoft.product.api.entity.WorkflowAttachment();
		docment.setId(doc.getId());
		docment.setFileName(doc.getFileName());
		docment.setFileSize(doc.getFileSize());
		docment.setFileType(doc.getFileType());
		docment.setTaskId(doc.getTaskId());
		docment.setDepartmentName(doc.getDepartmentName());
		docment.setTransactor(doc.getTransactor());
		docment.setDeleteSetting(doc.getDeleteSetting());
		docment.setTaskName(doc.getTaskName());
		docment.setWorkflowId(doc.getWorkflowId());
		docment.setFilePath(doc.getFilePath());
		//得到文件内容
		WorkflowAttachmentFile file=getAttachmentFile(doc.getId());
		if(file!=null){
			docment.setFileBody(file.getContent());
		}else{
			FileService fileService =(FileService)ContextUtils.getBean("fileService");
			byte[] fileBody = fileService.getFile(doc.getFilePath());
			docment.setFileBody(fileBody);
		}
		docment.setCreateDate(doc.getCreatedTime());
		return docment;
	}
	
	private List<com.norteksoft.product.api.entity.WorkflowAttachment> getAttachments(List<WorkflowAttachment> docs){
		List<com.norteksoft.product.api.entity.WorkflowAttachment> results=new ArrayList<com.norteksoft.product.api.entity.WorkflowAttachment>();
		for(WorkflowAttachment doc:docs){
			results.add(getAttachment(doc));
		}
		return results;
	}

	/**
	 * 获得正文列表
	 * @param loanForm
	 * @return
	 */
	public List<com.norteksoft.product.api.entity.Document> getDocuments(FormFlowable formFlowable) {
		Assert.notNull(formFlowable, "FormFlowable实体不能为null");
		if(ContextUtils.getCompanyId()==null){
			ThreadParameters params=new ThreadParameters(formFlowable.getCompanyId());
			ParameterUtils.setParameters(params);
		}
		return getDocuments(formFlowable.getWorkflowInfo().getWorkflowId());
	}
	
	public List<com.norteksoft.product.api.entity.Document> getDocuments(Long taskId) {
		Assert.notNull(taskId, "taskId不能为null");
		Assert.notNull(getLoginName(), "当前登录名不可为null");
		com.norteksoft.task.entity.WorkflowTask task=taskService.getTask(taskId);
		if(task==null){
			log.debug("任务不能为null");
			throw new RuntimeException("任务不能为null");
		}
		List<Document> docs=workflowInstanceManager.getDocuments(taskId,task.getCompanyId());
		boolean delRight=officialTextDeleteRight(taskId);
		for(Document doc:docs){
			//用户有创建正文权限,即使没有删除权限也可删除自己当前环节创建的正文;用户有删除权限,可删除当前环节的所有正文
			if(getLoginName().equals(doc.getCreator())||delRight){
				doc.setDeleteSetting(true);
			}
		}
		return getDocuments(docs);
	}
	
	private List<com.norteksoft.product.api.entity.Document> getDocuments(List<Document> docs){
		List<com.norteksoft.product.api.entity.Document> results=new ArrayList<com.norteksoft.product.api.entity.Document>();
		for(Document doc:docs){
			results.add(getDocument(doc));
		}
		return results;
	}
	
	private com.norteksoft.product.api.entity.Document getDocument(Document doc){
		com.norteksoft.product.api.entity.Document docment=new com.norteksoft.product.api.entity.Document();
		docment.setId(doc.getId());
		docment.setCustomField(doc.getCustomField());
		docment.setDescript(doc.getRemark());
		docment.setFileName(doc.getFileName());
		docment.setFilePath(doc.getFilePath());
		docment.setFileSize(doc.getFileSize());
		docment.setFileType(doc.getFileType());
		docment.setStatus(doc.getStatus());
		docment.setSubject(doc.getSubject());
		docment.setTaskId(doc.getTaskId());
		docment.setDeleteSetting(doc.getDeleteSetting());
		docment.setTaskName(doc.getTaskName());
		docment.setWorkflowId(doc.getWorkflowId());
		//得到文件内容
//		DocumentFile file=getDocumentFile(doc.getId());
//		docment.setFileBody(file.getFileBody());
		docment.setCreateDate(doc.getCreatedTime());
		return docment;
	}
	
	private com.norteksoft.product.api.entity.Document getDoc(Document doc){
		com.norteksoft.product.api.entity.Document docment=new com.norteksoft.product.api.entity.Document();
		docment.setId(doc.getId());
		docment.setCustomField(doc.getCustomField());
		docment.setDescript(doc.getRemark());
		docment.setFileName(doc.getFileName());
		docment.setFilePath(doc.getFilePath());
		docment.setFileSize(doc.getFileSize());
		docment.setFileType(doc.getFileType());
		docment.setStatus(doc.getStatus());
		docment.setSubject(doc.getSubject());
		docment.setTaskId(doc.getTaskId());
		docment.setDeleteSetting(doc.getDeleteSetting());
		docment.setTaskName(doc.getTaskName());
		docment.setWorkflowId(doc.getWorkflowId());
		//得到文件内容
		DocumentFile file=getDocumentFile(doc.getId());
		if(file!=null){
			docment.setFileBody(file.getFileBody());
		}else{
			FileService fileService =(FileService)ContextUtils.getBean("fileService");
			byte[] fileBody = fileService.getFile(doc.getFilePath());
			docment.setFileBody(fileBody);
		}
		docment.setCreateDate(doc.getCreatedTime());
		return docment;
	}
	
	/**
	 * 返回包装好的正文实例
	 * @param loanForm
	 * @param fileType
	 * @param taskId
	 * @return 正文实例
	 */
	public com.norteksoft.product.api.entity.Document createDocument(FormFlowable entity,String fileType) {
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"必须先启动流程，才能上传正文");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"必须先启动流程，才能上传正文");
		return createDocument(entity.getWorkflowInfo().getWorkflowId(),fileType,getCompanyId(entity));
	}
	
	public com.norteksoft.product.api.entity.Document createDocument(String workflowId,String fileType,Long companyId) {
		Document document = new Document();
		java.util.Date dt = new java.util.Date();
        Long recordId = new Long(dt.getTime());
        String fileName = recordId+fileType;
//        document.setRecordId(recordId);
        document.setFileType(fileType);
        document.setFileName(fileName);
        document.setWorkflowId(workflowId);
        WorkflowInstance instance = this.getInstance(workflowId);
        document.setTaskName(instance.getCurrentActivity());
        document.setCompanyId(companyId);
		return getDocument(document);
	}

	/**
	 * 查询正文实例
	 * @param documentId
	 * @return 正文实例
	 */
	public com.norteksoft.product.api.entity.Document getDocument(Long documentId) {
		return getDoc(workflowInstanceManager.getDocument(documentId));
	}
	
	public DocumentFile getDocumentFile(Long documentId){
		return workflowInstanceManager.getDocumentFile(documentId);
	}

	public void deleteDocument(Long documentId){
		workflowInstanceManager.deleteDocument(documentId);
	}
	
	/**
	 * 根据formId查询所有表单字段
	 * @param formId
	 * @return 字段列表
	 */
	public List<FormControl> getFormControls(Long formId){
		return formManager.getControls(formId);
	}
	
	
	/**
	 * 查询办理人的当前任务
	 * @param entity 走流程的表单
	 * @param loginname 办理人的登录名
	 * @return 如果流程已经启动 ，返回当前任务；否则，返回null
	 */
	@Deprecated
	public WorkflowTask getMyTask(FormFlowable entity,String loginname){
		return getActiveTaskByLoginName(entity, loginname);
	}
	
	public WorkflowTask getActiveTaskByLoginName(FormFlowable entity,String loginName){
		if(entity.getWorkflowInfo()==null) return null;
		return BeanUtil.turnToModelTask(taskService.getMyTask(entity.getWorkflowInfo().getWorkflowId(), getCompanyId(entity),loginName));
	}
	public WorkflowTask getActiveTaskByLoginName(String workflowId,String loginName){
		if(workflowId==null) return null;
		return  BeanUtil.turnToModelTask(taskService.getMyTask(workflowId, getCompanyId(null),loginName));
	}
	
	/**
	 * 完成分发任务
	 * @param taskId 
	 * @param receivers 分发到的用户登录名列表
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
	 */
	public CompleteTaskTipType completeDistributeTask(Long taskId, List<String> receivers){
		Assert.notNull(taskId,"完成分发任务时，任务id不能为null");
		return taskService.completeDistributeTask(taskId, receivers);
	}
	
	public CompleteTaskTipType completeDistributeTask(Long taskId, String... receivers){
		Assert.notNull(taskId,"完成分发任务时，任务id不能为null");
		return taskService.completeDistributeTask(taskId, Arrays.asList(receivers));
	}
	
	/**
	 * 返回当前用户编辑意见的权限
	 */
	public boolean editOpinion( Long taskId ){
		TaskPermission taskPermission=getActivityPermission(taskId);
		return taskPermission.getOpinionEditable();
	}
	
	/**
	 * 返回当前用户编辑意见的权限
	 */
	public boolean mustOpinion(Long taskId ){
		TaskPermission taskPermission=getActivityPermission(taskId);
		return taskPermission.getOpinionRequired();
	}
	
	/**
	 * 流程还未启动时编辑意见的权限
	 */
	public boolean editOpinionNotStarted(String definitionCode ){
		TaskPermission taskPermission=getActivityPermission(definitionCode);
		return taskPermission.getOpinionEditable();
	}
	
	/**
	 * 流程还未启动时编辑意见的权限
	 */
	public boolean editOpinionNotStarted(Long workflowDefinitionId ){
		WorkflowDefinition definition = this.getWorkflowDefinitionById(workflowDefinitionId);
		TaskPermission taskPermission=getActivityPermission(definition.getCode(),definition.getVersion());
		return taskPermission.getOpinionEditable();
	}
	
	/**
	 * 流程还未启动时意见是否必填
	 */
	public boolean mustOpinionNotStarted(String definitionCode){
		TaskPermission taskPermission=getActivityPermission(definitionCode);
		return taskPermission.getOpinionRequired();
	}
	
	/**
	 * 流程还未启动时意见是否必填
	 */
	public boolean mustOpinionNotStarted(Long workflowDefinitionId){
		WorkflowDefinition definition = this.getWorkflowDefinitionById(workflowDefinitionId);
		TaskPermission taskPermission=getActivityPermission(definition.getCode(),definition.getVersion());
		return taskPermission.getOpinionRequired();
	}
	
	
//----------- 附件	
	public boolean attachmentAddRightNotStarted(String definitionCode) {
		Assert.notNull(definitionCode, "流程定义编号definitionCode不能为null");
		TaskPermission taskPermission=getActivityPermission(definitionCode);
		return taskPermission.getAttachmentCreateable();
	}

	public boolean attachmentAddRightNotStarted(Long workflowDefinitionId) {
		Assert.notNull(workflowDefinitionId, "workflowDefinitionId不能为null");
		WorkflowDefinition definition = this.getWorkflowDefinitionById(workflowDefinitionId);
		TaskPermission taskPermission=getActivityPermission(definition.getCode(),definition.getVersion());
		return taskPermission.getAttachmentCreateable();
	}

	public boolean attachmentDeleteRightNotStarted(String definitionCode) {
		TaskPermission taskPermission=getActivityPermission(definitionCode);
		return taskPermission.getAttachmentDeletable();
	}

	public boolean attachmentDeleteRightNotStarted(Long workflowDefinitionId) {
		WorkflowDefinition definition = this.getWorkflowDefinitionById(workflowDefinitionId);
		TaskPermission taskPermission=getActivityPermission(definition.getCode(),definition.getVersion());
		return taskPermission.getAttachmentDeletable();
	}

	public boolean attachmentDownloadRightNotStarted(String definitionCode) {
		TaskPermission taskPermission=getActivityPermission(definitionCode);
		return taskPermission.getAttachmentDownloadable();
	}

	public boolean attachmentDownloadRightNotStarted(Long workflowDefinitionId) {
		WorkflowDefinition definition = this.getWorkflowDefinitionById(workflowDefinitionId);
		TaskPermission taskPermission=getActivityPermission(definition.getCode(),definition.getVersion());
		return taskPermission.getAttachmentDownloadable();
	}

	
	/**
	 * 上传附件的权限
	 * @param workflowId 工作流id
	 * @param taskId 任务id
	 * @param companyId 公司id
	 * @return 有权限返回true，否则返回false
	 */
	public boolean attachmentAddRight(Long taskId){
		Assert.notNull(taskId, "taskId不能为null");
		TaskPermission taskPermission=getActivityPermission(taskId);
		return taskPermission.getAttachmentCreateable();
	}
	
	/**
	 * 删除附件的权限
	 * @param taskId 任务id
	 * @param companyId 公司id
	 * @return 有权限返回true，否则返回false
	 */
	public boolean attachmentDeleteRight(Long taskId ){
		Assert.notNull(taskId, "taskId不能为null");
		TaskPermission taskPermission=getActivityPermission(taskId);
		return taskPermission.getAttachmentDeletable();
	}
	/**
	 * 下载附件的权限
	 * @param taskId 任务id
	 * @return 有权限返回true，否则返回false
	 */
	public boolean attachmentDownloadRight(Long taskId ){
		Assert.notNull(taskId, "taskId不能为null");
		TaskPermission taskPermission=getActivityPermission(taskId);
		return taskPermission.getAttachmentDownloadable();
	}
	//----------- 附件 end-----
	
	//----------- 正文 --------
	public boolean officialTextCreateRightNotStarted(String definitionCode) {
		Assert.notNull(definitionCode, "definitionCode不能为null");
		TaskPermission taskPermission=getActivityPermission(definitionCode);
		if(taskPermission==null){
			log.debug("TaskPermission实体不能为null");
			throw new RuntimeException("TaskPermission实体不能为null");
		}
		return taskPermission.getDocumentCreateable();
	}

	public boolean officialTextCreateRightNotStarted(Long workflowDefinitionId) {
		WorkflowDefinition definition = this.getWorkflowDefinitionById(workflowDefinitionId);
		TaskPermission taskPermission=getActivityPermission(definition.getCode(),definition.getVersion());
		if(taskPermission==null){
			log.debug("TaskPermission实体不能为null");
			throw new RuntimeException("TaskPermission实体不能为null");
		}
		return taskPermission.getDocumentCreateable();
	}
	
	/**
	 * 返回环节办理人是否具有创建正文的权限 
	 */
	public boolean officialTextCreateRight(Long taskId){
		Assert.notNull(taskId, "taskId不能为null");
		TaskPermission taskPermission=getActivityPermission(taskId);
		if(taskPermission==null){
			log.debug("TaskPermission实体不能为null");
			throw new RuntimeException("TaskPermission实体不能为null");
		}
		return taskPermission.getDocumentCreateable();
	}
	public boolean officialTextEditRight(Long taskId){
		Assert.notNull(taskId, "taskId不能为null");
		TaskPermission taskPermission=getActivityPermission(taskId);
		if(taskPermission==null){
			log.debug("TaskPermission实体不能为null");
			throw new RuntimeException("TaskPermission实体不能为null");
		}
		return taskPermission.getDocumentEditable();
	}
	
	/**
	 * 返回环节办理人是否具有创建正文的权限 
	 */
	public boolean officialTextDeleteRight(Long taskId){
		Assert.notNull(taskId, "taskId不能为null");
		TaskPermission taskPermission=getActivityPermission(taskId);
		if(taskPermission==null){
			log.debug("TaskPermission实体不能为null");
			throw new RuntimeException("TaskPermission实体不能为null");
		}
		return taskPermission.getDocumentDeletable();
	}
	
	public boolean officialTextDownloadRight(Long taskId) {
		TaskPermission taskPermission=getActivityPermission(taskId);
		return taskPermission.getDocumentDownloadable();
	}

	public boolean officialTextPrintRight(Long taskId) {
		TaskPermission taskPermission=getActivityPermission(taskId);
		return taskPermission.getDocumentPrintable();
	}
	

	public String officialTextRights(Long taskId) {
		return getDocumentPermission(taskId);
	}

	
//----正文权限 end-- --	
	/**
	 * 自动填写实体，在转向办理页面时，根据本环节定义的自动填写字段，来自动填充实体
	 * @param entity 需要自动填写的实体
	 * @param wfDefinationName 流程定义名称
	 * @param taskId 任务id 如果该id为空，将填充第一个环节的值
	 */
	public void autoFilledEntityBeforeByDefinationName(FormFlowable entity,String definitionCode){
		fillEntityByDefinition(entity, definitionCode);
	}
	
	public void fillEntityByDefinition(FormFlowable entity,String wfDefinationCode){
		autoFilledEntityBeforeByDefinitionId(entity, getEnabledHighestVersionDefinition(wfDefinationCode).getId(),null);
	}
	
	public void fillEntityByDefinition(FormFlowable entity,String wfDefinationCode, Integer version){
		autoFilledEntityBeforeByDefinitionId(entity, getWorkflowDefinitionByCodeAndVersion(wfDefinationCode, version).getId(),null);
	}
	
	/**
	 * 自动填写实体，在转向办理页面时，根据本环节定义的自动填写字段，来自动填充实体
	 * @param entity 需要自动填写的实体  流程必须启动
	 * @param taskId 任务id 
	 */
	public void autoFilledEntityBefore(FormFlowable entity,Long taskId){
		fillEntityByTask(entity, taskId,null);
	}
	
	public void fillEntityByTask(FormFlowable entity,Long taskId){
		Assert.notNull(entity, "FormFlowable实体不能为null");
		com.norteksoft.task.entity.WorkflowTask task = taskService.getTask(taskId);
		if(task==null){
			log.debug("任务不能为null");
			throw new RuntimeException("任务不能为null");
		}
		if(entity.getWorkflowInfo()==null){
			log.debug("WorkflowInfo实体不能为null");
			throw new RuntimeException("WorkflowInfo实体不能为null");
		}
		com.norteksoft.wf.engine.entity.WorkflowInstance instance=workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		String processId=null;
		if(entity==null){
			if(instance!=null)
			processId=instance.getProcessDefinitionId();
		}else{
			processId=entity.getWorkflowInfo().getWorkflowDefinitionId();
		}
		WorkflowDefinition definition = workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
		if(definition==null){
			log.debug("WorkflowDefinition实体不能为null");
			throw new RuntimeException("WorkflowDefinition实体不能为null");
		}
		log.debug("WorkflowDefinition:"+ definition);
		FormView form = formManager.getCurrentFormViewByCodeAndVersion(definition.getFormCode(), definition.getFromVersion());
		if(form==null){
			log.debug("FormView实体不能为null");
			throw new RuntimeException("FormView实体不能为null");
		}
		log.debug("Form" + form);
		Object value;
		Map<String,Object> valueMap = new HashMap<String,Object>();
 		List<AutomaticallyFilledField> filledField = DefinitionXmlParse.getBeforeFilledFields(definition.getProcessId(),task.getName());
 		log.debug("filledField:" + filledField);
 		for(AutomaticallyFilledField aff :filledField){
 				value = getAutoFilledFieldValue(formManager.getControls(form.getId()),aff,entity,null);
 				log.debug("value:" + value);
 				valueMap.put(aff.getName(), value);
 		}
 		try {
			BeanUtils.populate(entity, valueMap);
		} catch (IllegalAccessException e) {
			log.debug("为bean设置属性异常:" + e.getMessage());
		} catch (InvocationTargetException e) {
			log.debug("为bean设置属性异常:" +e.getMessage());
		}
	}
	public void fillEntityByTask(FormFlowable entity,Long taskId,Map data){
		com.norteksoft.task.entity.WorkflowTask task = taskService.getTask(taskId);
		if(task==null){
			log.debug("任务不能为null");
			throw new RuntimeException("任务不能为null");
		}
		
		com.norteksoft.wf.engine.entity.WorkflowInstance instance=workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		String processId=null;
		if(entity==null){
			if(instance!=null)
				processId=instance.getProcessDefinitionId();
		}else{
			if(entity.getWorkflowInfo()==null){
				log.debug("WorkflowInfo实体不能为null");
				throw new RuntimeException("WorkflowInfo实体不能为null");
			}
			processId=entity.getWorkflowInfo().getWorkflowDefinitionId();
		}
		WorkflowDefinition definition = workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
		if(definition==null){
			log.debug("WorkflowDefinition实体不能为null");
			throw new RuntimeException("WorkflowDefinition实体不能为null");
		}
		log.debug("WorkflowDefinition:"+ definition);
		FormView form = formManager.getCurrentFormViewByCodeAndVersion(definition.getFormCode(), definition.getFromVersion());
		if(form==null){
			log.debug("FormView实体不能为null");
			throw new RuntimeException("FormView实体不能为null");
		}
		log.debug("Form" + form);
		Object value;
		List<AutomaticallyFilledField> filledField = DefinitionXmlParse.getBeforeFilledFields(definition.getProcessId(),task.getName());
		log.debug("filledField:" + filledField);
		if(entity!=null){
			Map<String,Object> valueMap = new HashMap<String,Object>();
			try {
				for(AutomaticallyFilledField aff :filledField){
					value = getAutoFilledFieldValue(formManager.getControls(form.getId()),aff,entity,null);
					log.debug("value:" + value);
					valueMap.put(aff.getName(), value);
				}
				BeanUtils.populate(entity, valueMap);
			} catch (IllegalAccessException e) {
				log.debug("为bean设置属性异常:" + e.getMessage());
			} catch (InvocationTargetException e) {
				log.debug("为bean设置属性异常:" +e.getMessage());
			}
		}else{
			if(data!=null){
				for(AutomaticallyFilledField aff :filledField){
					value = getAutoFilledFieldValue(formManager.getControls(form.getId()),aff,null,data);
					log.debug("value:" + value);
					data.put(JdbcSupport.FORM_FIELD_PREFIX_STRING+aff.getName(), value);
				}
			}
		}
	}
	
	/**
	 * 自动填写实体，在转向办理页面时，根据本环节定义的自动填写字段，来自动填充实体
	 * @param entity 需要自动填写的实体
	 * @param wfDefinationId 流程定义ID
	 * @param taskId 任务id 如果该id为空，将填充第一个环节的值
	 */
	public void autoFilledEntityBeforeByDefinitionId(FormFlowable entity,Long definitionId){
		log.debug(LOGMESSAGE_METHOD_PARAMETER + "FormFlowable entity" + entity);
		WorkflowDefinition definition = workflowDefinitionManager.getWfDefinition(definitionId);
		if(definition==null){
			log.debug("WorkflowDefinition实体不能为null");
			throw new RuntimeException("WorkflowDefinition实体不能为null");
		}
		log.debug("WorkflowDefinition:"+ definition);
		FormView form = formManager.getCurrentFormViewByCodeAndVersion(definition.getFormCode(), definition.getFromVersion());
		if(form==null){
			log.debug("FormView实体不能为null");
			throw new RuntimeException("FormView实体不能为null");
		}
		log.debug("Form" + form);
		String taskName = null;
		taskName = DefinitionXmlParse.getFirstTaskName(definition.getProcessId());
		Object value;
		Map<String,Object> valueMap = new HashMap<String,Object>();
 		List<AutomaticallyFilledField> filledField = DefinitionXmlParse.getBeforeFilledFields(definition.getProcessId(),taskName);
 		log.debug("filledField:" + filledField);
		try {
			for(AutomaticallyFilledField aff :filledField){
				value = getAutoFilledFieldValue(formManager.getControls(form.getId()),aff,entity,null);
				log.debug("value:" + value);
				valueMap.put(aff.getName(), value);
			}
			BeanUtils.populate(entity, valueMap);
		} catch (IllegalAccessException e) {
			log.debug("为bean设置属性异常:" + e.getMessage());
		} catch (InvocationTargetException e) {
			log.debug("为bean设置属性异常:" +e.getMessage());
		}
	}
	/**
	 * 自动填写实体，在转向办理页面时，根据本环节定义的自动填写字段，来自动填充实体
	 * @param entity 需要自动填写的实体
	 * @param wfDefinationId 流程定义ID
	 * @param taskId 任务id 如果该id为空，将填充第一个环节的值
	 */
	public void autoFilledEntityBeforeByDefinitionId(FormFlowable entity,Long definitionId,Map data){
		log.debug(LOGMESSAGE_METHOD_PARAMETER + "FormFlowable entity" + entity);
		WorkflowDefinition definition = workflowDefinitionManager.getWfDefinition(definitionId);
		if(definition==null){
			log.debug("WorkflowDefinition实体不能为null");
			throw new RuntimeException("WorkflowDefinition实体不能为null");
		}
		log.debug("WorkflowDefinition:"+ definition);
		FormView form = formManager.getCurrentFormViewByCodeAndVersion(definition.getFormCode(), definition.getFromVersion());
		if(form==null){
			log.debug("FormView实体不能为null");
			throw new RuntimeException("FormView实体不能为null");
		}
		log.debug("Form" + form);
		String taskName = null;
		taskName = DefinitionXmlParse.getFirstTaskName(definition.getProcessId());
		Object value;
		List<AutomaticallyFilledField> filledField = DefinitionXmlParse.getBeforeFilledFields(definition.getProcessId(),taskName);
		log.debug("filledField:" + filledField);
		if(entity!=null){
			Map<String,Object> valueMap = new HashMap<String,Object>();
			try {
				for(AutomaticallyFilledField aff :filledField){
					value = getAutoFilledFieldValue(formManager.getControls(form.getId()),aff,entity,null);
					log.debug("value:" + value);
					valueMap.put(aff.getName(), value);
				}
				BeanUtils.populate(entity, valueMap);
			} catch (IllegalAccessException e) {
				log.debug("为bean设置属性异常:" + e.getMessage());
			} catch (InvocationTargetException e) {
				log.debug("为bean设置属性异常:" +e.getMessage());
			}
		}else{
			if(data!=null){
				for(AutomaticallyFilledField aff :filledField){
					value = getAutoFilledFieldValue(formManager.getControls(form.getId()),aff,null,data);
					log.debug("value:" + value);
					Object myobj = data.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+aff.getName());
					if(myobj!=null){//是数组
						String str=myobj.getClass().getName();
						if(str.indexOf("[")==0){//是数组
							data.put(JdbcSupport.FORM_FIELD_PREFIX_STRING+aff.getName(), value==null?new String[]{""}:new String[]{value.toString()});
						}
					}else{//不是数组
						data.put(JdbcSupport.FORM_FIELD_PREFIX_STRING+aff.getName(), value);
					}
				}
			}
		}
	}
	
	private  Object getAutoFilledFieldValue(List<FormControl> fields,AutomaticallyFilledField aff,FormFlowable entity,Map data){
		log.debug(LOGMESSAGE_METHOD_PARAMETER + fields);
		if(StringUtils.isEmpty(aff.getValue()))return null;
		FormControl field = getFieldbyName(fields,aff.getName() );
		log.debug("field:" + field);
		Object value ;
		log.debug("dataType:" + field.getDataType());
		if(field.getDataType()==DataType.TIME||field.getDataType()==DataType.DATE){
			value = getFormatCurrentTime(aff);
		}else if(field.getDataType()==DataType.TEXT||field.getDataType()==DataType.CLOB){
			value = getValue(aff);
			try{
				String originalValue = "";
				if(entity==null){//自定义表单
					Object myobj = data.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+aff.getName());
					if(myobj!=null){//是数组
						String str=myobj.getClass().getName();
						if(str.indexOf("[")==0){//是数组
							if(value==null){
								originalValue="";
							}else{
								originalValue=((String[])myobj)[0];
							}
						}else{
							originalValue=myobj.toString();
						}
					}else{//不是数组
						originalValue = "";
					}
				}else{//标准表单
					originalValue = BeanUtils.getProperty(entity, aff.getName());
				}
				if(aff.getFillType().equals(AutomaticallyFilledField.AUTO_FILLED_FILL_TYPE_ADDITIONAL)){
					if(value!=null&&StringUtils.isNotEmpty(originalValue)){//如果配的值不为空且数据库中的值也不为空时，则其值为追加后的值
						value = originalValue+","+value;
					}else if(value==null&&StringUtils.isNotEmpty(originalValue)){//如果配的值为空且数据库的值不为空时，则其值为数据库中的值不作修改
						value= originalValue;
					}
				}else if(aff.getFillType().equals(AutomaticallyFilledField.AUTO_FILLED_FILL_TYPE_ADDED_TO_THE_BEGINNING)){
					if(value!=null&&StringUtils.isNotEmpty(originalValue)){
						value = value + ","+originalValue;
					}else if(value==null&&StringUtils.isNotEmpty(originalValue)){
						value= originalValue;
					}
				}
			}catch (Exception e) {
				log.debug(PropUtils.getExceptionInfo(e));
			}
		}else{
			value = aff.getValue();
		}
		return value;
	}
	
	/*
	 * 从List中取出英文名为enName的Field
	 */
	private FormControl getFieldbyName(List<FormControl> fields , String enName){
		for(FormControl field:fields){
			if(field.getName().trim().equals(enName.trim())) return field;
		}
		return null;
	}
	
	private Date getFormatCurrentTime(AutomaticallyFilledField aff){
		return StringUtils.contains(aff.getValue(), CommonStrings.CURRENTTIME) ? new Date() :null;
	}
	
	 private String getValue(AutomaticallyFilledField aff){
		 return taskService.getValue(aff, null);
	}

	public List<WorkflowAttachment> getAttachmentsExceptTaskMode(String workflowId,
			TaskProcessingMode taskMode) {
		return workflowAttachmentManager.getAttachmentsExceptTaskMode(workflowId,taskMode);
	}

	public List<com.norteksoft.product.api.entity.WorkflowAttachment> getAttachmentsExceptTaskMode(FormFlowable entity,
			TaskProcessingMode taskMode) {
		return getAttachmentsExceptTaskMode(entity, taskMode);
	}
	
	public List<com.norteksoft.product.api.entity.WorkflowAttachment> getAttachmentsExcludeTaskMode(FormFlowable entity,TaskProcessingMode taskMode){
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(taskMode,"任务模式不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getAttachmentsExcludeTaskMode(entity.getWorkflowInfo().getWorkflowId(),taskMode);
	}

	public List<WorkflowAttachment> getAttachmentsExceptTaskName(String workflowId,
			String taskName) {
		return workflowAttachmentManager.getAttachmentsExceptTaskName(workflowId,taskName);
	}

	public List<com.norteksoft.product.api.entity.WorkflowAttachment> getAttachmentsExceptTaskName(FormFlowable entity,
			String taskName) {
		return getAttachmentsExcludeTaskName(entity, taskName);
	}
	
	public List<com.norteksoft.product.api.entity.WorkflowAttachment> getAttachmentsExcludeTaskName(FormFlowable entity,String taskName){
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(taskName,"任务名称不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getAttachmentsExcludeTaskName(entity.getWorkflowInfo().getWorkflowId(),taskName);
	}

	/**
	 * 根据条件查询数据字典
	 */
	public List<DataDictionary> queryDataDict(DictQueryCondition condition) {
		if(condition == null) throw new RuntimeException(" 没有给定查询数据字典的条件： DictQueryCondition. ");
		return BeanUtil.turnToModelDataDictionaryList(dataDictionaryManager.queryDataDict(condition.getCondition(), condition.getConditionValues()));
	}
	
	@Autowired
	public void setDataDictionaryManager(
			DataDictionaryManager dataDictionaryManager) {
		this.dataDictionaryManager = dataDictionaryManager;
	}

	/**
	 * 根据数据字典IDs查询办理人登录名
	 */
	public List<String> getCandidate(List<Long> dictIds) {
		List<String> result = new ArrayList<String>();
		Set<String[]> users = dataDictionaryManager.getCandidate(dictIds);
		for(String[] user : users){
			result.add(user[0]);
		}
		return result;
	}

	/**
	 * 根据数据字典IDs查询办理人登录名及用户名
	 */
	@Deprecated
	public Set<String[]> getCandidateNames(List<Long> dictIds) {
		return dataDictionaryManager.getCandidate(dictIds);
	}
	
	/**
	 * 根据数据字典ID查询办理人登录名
	 */
	public List<String> getCandidate(Long dictId) {
		List<String> result = new ArrayList<String>();
		Set<String[]> users = dataDictionaryManager.getCandidate(dictId, new HashSet<String[]>());
		for(String[] user : users){
			result.add(user[0]);
		}
		return result;
	}

	/**
	 * 根据数据字典ID查询办理人登录名及用户名
	 */
	@Deprecated
	public Set<String[]> getCandidateNames(Long dictId) {
		Set<String[]> loginNames = new HashSet<String[]>();
		return dataDictionaryManager.getCandidate(dictId, loginNames);
	}

	/**
	 * 根据数据字典IDs查询数据字典
	 */
	public List<DataDictionary> queryDataDict(List<Long> ids) {
		if(ids == null) throw new RuntimeException("没有给定查询数据字的查询条件： 数据字典的ID集合. ");
		List<DataDictionary> dicts = new ArrayList<DataDictionary>();
		for(Long id : ids){
			dicts.add(queryDataDict(id));
		}
		return dicts;
	}

	/**
	 * 根据数据字典ID查询数据字典
	 */
	public DataDictionary queryDataDict(Long id) {
		return BeanUtil.turnToModelDataDictionary(dataDictionaryManager.getDataDict(id));
	}

	/**
     * 根据用户ID查询该用户的直属领导
     * @param userId
     * @return
     */
	public User getDirectLeader(Long userId) {
		return BeanUtil.turnToModelUser(rankManager.getDirectLeader(userId));
	}

	/**
     * 根据用户登录名查询该用户的直属领导
     * @param loginName
     * @return
     */
	public User getDirectLeader(String loginName) {
		return BeanUtil.turnToModelUser(rankManager.getDirectLeader(loginName));
	}
	public List<User> getDirectLeaders(String loginName) {
		return BeanUtil.turnToModelUserList(rankManager.getDirectLeaders(loginName));
	}
	
	public WorkflowAttachmentFile getAttachmentFile(Long attachmentId) {
		return workflowInstanceManager.getAttachmentFileByAttachmentId(attachmentId);
	}
	
	public void saveAttachmentFile(com.norteksoft.product.api.entity.WorkflowAttachment doc,WorkflowAttachment document) {
		Assert.notNull(document.getId(), "附件id不能为null");
		WorkflowAttachmentFile attachmentFile=workflowInstanceManager.getAttachmentFileByAttachmentId(document.getId());
		if(attachmentFile==null){
			attachmentFile=new WorkflowAttachmentFile();
		}
		attachmentFile.setAttachmentId(document.getId());
		attachmentFile.setCompanyId(document.getCompanyId());
		attachmentFile.setContent(doc.getFileBody());
		workflowInstanceManager.saveAttachmentFile(attachmentFile);
	}

	/**
	 * 返回表单打印权限
	 */
	
	public boolean formPrintRightNotStarted(String definitionCode){
		TaskPermission taskPermission=getActivityPermission(definitionCode);
		return taskPermission.getFormPrintable();
	}
	/**
	 * 返回表单打印权限
	 */
	public boolean formPrintRightNotStarted(Long definitionId){
		WorkflowDefinition definition = this.getWorkflowDefinitionById(definitionId);
		if(definition==null){
			log.debug("WorkflowDefinition实体不能为null");
			throw new RuntimeException("WorkflowDefinition实体不能为null");
		}
		TaskPermission taskPermission=getActivityPermission(definition.getCode(),definition.getVersion());
		return taskPermission.getFormPrintable();
	}
	
	/**
	 * 返回表单权限
	 */
	public boolean formPrintRight(Long taskId ){
		TaskPermission taskPermission=getActivityPermission(taskId);
		return taskPermission.getFormPrintable();
	}

	
	/**
	 * 返回流转历史的查看权限
	 */
	public boolean historyAuthorization(Long taskId ){
		TaskPermission taskPermission=getActivityPermission(taskId);
		return taskPermission.getHistoryVisible();
	}

	/**
	 * 返回查看会签结果的权限
	 */
	public boolean viewMeetingResultRight(Long taskId ){
		TaskPermission taskPermission=getActivityPermission(taskId);
		return taskPermission.getCountersignResultVisible();
	}

	/**
	 * 返回查看投票结果的权限
	 */
	public boolean viewVoteResultRight(Long taskId ){
		TaskPermission taskPermission=getActivityPermission(taskId);
		return taskPermission.getVoteResultVisible();
	}

	public List<Document> getDocuments(String workflowId,
			TaskProcessingMode taskMode) {
		return officeManager.getDocuments(workflowId,taskMode);
	}

	public List<com.norteksoft.product.api.entity.Document> getDocuments(FormFlowable entity,
			TaskProcessingMode taskMode) {
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getAllDocuments(entity.getWorkflowInfo().getWorkflowId(),taskMode);
	}

	public List<Document> getDocuments(String workflowId, String taskName) {
		return officeManager.getDocuments(workflowId,taskName);
	}

	public List<com.norteksoft.product.api.entity.Document> getDocuments(FormFlowable entity, String taskName) {
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getAllDocuments(entity.getWorkflowInfo().getWorkflowId(),taskName);
	}

	public List<Document> getDocumentsByCustomField(String workflowId,
			String customField) {
		return officeManager.getDocumentsByCustomField(workflowId,customField);
	}

	public List<com.norteksoft.product.api.entity.Document> getDocumentsByCustomField(FormFlowable entity,
			String customField) {
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getAllDocumentsByCustomField(entity.getWorkflowInfo().getWorkflowId(),customField);
	}

	public List<Document> getDocumentsExceptCustomField(String workflowId,
			String customField) {
		return officeManager.getDocumentsExceptCustomField(workflowId,customField);
	}
	
	public List<com.norteksoft.product.api.entity.Document> getDocumentsExceptCustomField(FormFlowable entity,
			String customField) {
		return getDocumentsExcludeCustomField(entity, customField);
	}

	public List<com.norteksoft.product.api.entity.Document> getDocumentsExcludeCustomField(FormFlowable entity,
			String customField) {
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getDocumentsExcludeCustomField(entity.getWorkflowInfo().getWorkflowId(),customField);
	}

	public List<Document> getDocumentsExceptTaskMode(String workflowId,
			TaskProcessingMode taskMode) {
		return officeManager.getDocumentsExceptTaskMode(workflowId,taskMode);
	}

	public List<com.norteksoft.product.api.entity.Document> getDocumentsExceptTaskMode(FormFlowable entity,
			TaskProcessingMode taskMode) {
		return getDocumentsExcludeTaskMode(entity, taskMode);
	}
	
	public List<com.norteksoft.product.api.entity.Document> getDocumentsExcludeTaskMode(FormFlowable entity,TaskProcessingMode taskMode){
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getDocumentsExcludeTaskMode(entity.getWorkflowInfo().getWorkflowId(),taskMode);
	}

	public List<Document> getDocumentsExceptTaskName(String workflowId,
			String taskName) {
		return officeManager.getDocumentsExceptTaskName(workflowId,taskName);
	}

	public List<com.norteksoft.product.api.entity.Document> getDocumentsExceptTaskName(FormFlowable entity,
			String taskName) {
		return getDocumentsExcludeTaskName(entity, taskName);
	}
	
	public List<com.norteksoft.product.api.entity.Document> getDocumentsExcludeTaskName(FormFlowable entity,String taskName){
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getDocumentsExcludeTaskName(entity.getWorkflowInfo().getWorkflowId(),taskName);
	}

	public Collection<String> getNeedFillFields(Long taskId) {
		com.norteksoft.task.entity.WorkflowTask task = taskService.getTask(taskId);
		if(task==null){
			log.debug("任务不能为null");
			throw new RuntimeException("任务不能为null");
		}
		if(!(task.getActive().equals(TaskState.WAIT_TRANSACT.getIndex())||task.getActive().equals(TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex())))return new ArrayList<String>();
		return workflowRightsManager.getNeedFillFields(task);
	}

	public Collection<String> getNeedFillFieldsNotStarted(String definitionCode) {
		WorkflowDefinition definition = this.getEnabledHighestVersionDefinition(definitionCode);
		return workflowRightsManager.getNeedFillFieldsNotStarted(definition);
	}

	public Collection<String> getNeedFillFieldsNotStarted(Long definitionId) {
		WorkflowDefinition definition = workflowDefinitionManager.getWfDefinition(definitionId);
		return workflowRightsManager.getNeedFillFieldsNotStarted(definition);
	}
	@Deprecated
	public Collection<String> getforbiddenFields(Long taskId) {
		com.norteksoft.task.entity.WorkflowTask task = taskService.getTask(taskId);
		return workflowRightsManager.getforbiddenFields(task);
	}
	
	public Collection<String> getForbiddenFields(Long taskId) {
		com.norteksoft.task.entity.WorkflowTask task = taskService.getTask(taskId);
		return workflowRightsManager.getforbiddenFields(task);
	}

	@Deprecated
	public Collection<String> getforbiddenFieldsNotStarted(String definitionCode) {
		WorkflowDefinition definition = this.getEnabledHighestVersionDefinition(definitionCode);
		return workflowRightsManager.getforbiddenFieldsNotStarted(definition);
	}
	
	public Collection<String> getForbiddenFieldsNotStarted(String definitionCode) {
		WorkflowDefinition definition = this.getEnabledHighestVersionDefinition(definitionCode);
		return workflowRightsManager.getforbiddenFieldsNotStarted(definition);
	}

	@Deprecated
	public Collection<String> getforbiddenFieldsNotStarted(Long definitionId) {
		WorkflowDefinition definition = workflowDefinitionManager.getWfDefinition(definitionId);
		return workflowRightsManager.getforbiddenFieldsNotStarted(definition);
	}
	
	public Collection<String> getForbiddenFieldsNotStarted(Long definitionId) {
		WorkflowDefinition definition = workflowDefinitionManager.getWfDefinition(definitionId);
		return workflowRightsManager.getforbiddenFieldsNotStarted(definition);
	}
	/**
	 * 设置新的办理人到工作流引擎中
	 * @param taskId 任务id
	 * @param newTransactor 新的办理人
	 */
	public void setNewTransactor(Long taskId, String newTransactor) {
		taskService.setNewTransactor(taskId,newTransactor);	
		
	}
	/**
	 * 设置新的流向名到工作流引擎中
	 * @param taskId 任务id
	 * @param transitionName 流向名
	 */
	public void setTransitionName(Long taskId, String transitionName) {
		taskService.setTransitionName(taskId,transitionName);
	}

	public String drawTask(Long taskId) {
		Assert.notNull(taskId,"领取任务时任务id不能为null");
		return taskService.receive(taskId);
	}
	
	/**
	 * 查询所有的选项组
	 */
	public List<OptionGroup> getOptionGroups() {
		return BeanUtil.turnToModelOptionGroupList(optionGroupManager.getOptionGroups());
	}

	/**
	 * 根据选项组查询选项
	 */
	public List<Option> getOptionsByGroup(Long optionGroupId) {
		return BeanUtil.turnToModelOptionList(optionGroupManager.getOptionsByGroup(optionGroupId));
	}

	public OptionGroup getOptionGroupByCode(String code) {
		return BeanUtil.turnToModelOptionGroup(optionGroupManager.getOptionGroupByCode(code));
	}

	public OptionGroup getOptionGroupByName(String name) {
		return BeanUtil.turnToModelOptionGroup(optionGroupManager.getOptionGroupByName(name));
	}

	public List<Option> getOptionsByGroupCode(String code) {
		return BeanUtil.turnToModelOptionList(optionGroupManager.getOptionsByGroupCode(code));
	}

	public List<Option> getOptionsByGroupName(String name) {
		return BeanUtil.turnToModelOptionList(optionGroupManager.getOptionsByGroupName(name));
	}

	public CompleteTaskTipType completeTacheChoice(Long taskId,
			String transitionName) {
		return selectActivity(taskId, transitionName);
	}
	
	public CompleteTaskTipType selectActivity(Long taskId, String transitionName){
		Assert.notNull(taskId,"完成选择环节的任务时，任务id不能为null");
		return taskService.completeTacheChoice(taskId, transitionName);
	}
	
	public User getDirectLeader(Long userId, Long companyId) {
		return BeanUtil.turnToModelUser(rankManager.getDirectLeader(userId,companyId));
	}

	public User getDirectLeader(String loginName, Long companyId) {
		return BeanUtil.turnToModelUser(rankManager.getDirectLeader(loginName,companyId));
	}

	public List<com.norteksoft.product.api.entity.Opinion> getOpinionsByCustomField(FormFlowable entity,
			String customField) {
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getOpinions(workflowInstanceManager.getOpinionsByCustomField(entity.getWorkflowInfo().getWorkflowId(),customField));
	}

	public List<com.norteksoft.product.api.entity.Opinion> getOpinionsExceptCustomField(FormFlowable entity,
			String customField) {
		return getOpinionsExcludeCustomField(entity, customField);
	}
	
	public List<com.norteksoft.product.api.entity.Opinion> getOpinionsExcludeCustomField(FormFlowable entity,String customField){
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getOpinions(workflowInstanceManager.getOpinionsExceptCustomField(entity.getWorkflowInfo().getWorkflowId(),customField));
	}

	public List<WorkflowAttachment> getAttachmentsByCustomField(
			String workflowId, String customField) {
		return workflowAttachmentManager.getAttachmentsByCustomField(workflowId,customField);
	}

	public List<com.norteksoft.product.api.entity.WorkflowAttachment> getAttachmentsByCustomField(
			FormFlowable entity, String customField) {
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(customField,"自定义类别不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getAllAttachmentsByCustomField(entity.getWorkflowInfo().getWorkflowId(),customField);
	}

	public List<WorkflowAttachment> getAttachmentsExceptCustomField(
			String workflowId, String customField) {
		return workflowAttachmentManager.getAttachmentsExceptCustomField(workflowId,customField);
	}

	public List<com.norteksoft.product.api.entity.WorkflowAttachment> getAttachmentsExceptCustomField(
			FormFlowable entity, String customField) {
		return getAttachmentsExcludeCustomField(entity, customField);
	}
	
	public List<com.norteksoft.product.api.entity.WorkflowAttachment> getAttachmentsExcludeCustomField(FormFlowable entity,String customField){
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(customField,"自定义正文类别不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getAttachmentsExcludeCustomField(entity.getWorkflowInfo().getWorkflowId(),customField);
	}
	
	/**
	 * 根据数据字典typNo和用途查询数据字典
	 * @param ids
	 * @return
	 */
	public List<DataDictionary> queryDataDict(String typeNo,DataDictUseType dataDictUseType){
		return BeanUtil.turnToModelDataDictionaryList(dataDictionaryManager.queryDataDict(typeNo, dataDictUseType));
	}

	public List<WorkflowTask> getWorkflowTasksByDefinitonName(String definitionName,
			String loginName) {
		return BeanUtil.turnToModelTaskList(taskService.getWorkflowTasksByDefinitonName(definitionName,loginName));
	}

	/**
	 * 生成抄送任务
	 * @param taskId 当前任务的id
	 * @param transactors 将该任务抄送给谁
	 */
	@Deprecated
	public void createCopyTaches(Long taskId, List<String> transactors) {
		taskService.createCopyTaches(taskId,transactors,null,null);
	}
	/**
	 * 生成抄送任务
	 * @param taskId 当前任务的id
	 * @param transactors 将该任务抄送给谁
	 * @param title
	 * @param url
	 */
	@Deprecated
	public void createCopyTaches(Long taskId, List<String> transactors,String title,String url) {
		createCopyTasks(taskId,transactors,title,url);
	}
	
	/**
	 * 生成抄送任务
	 * @param taskId 当前任务的id
	 * @param transactors 将该任务抄送给谁
	 * @param title
	 * @param url
	 */
	public void createCopyTasks(Long taskId, List<String> transactors,String title,String url){
		Assert.notNull(taskId,"生成抄送任务时，任务id不能为null");
		taskService.createCopyTaches(taskId,transactors,title,url);
	}
	
	@Deprecated
	public List<WorkflowTask> getOverdueTasks(Long companyId) {
		return BeanUtil.turnToModelTaskList(taskService.getOverdueTasks(companyId));
	}
	
	public List<WorkflowTask> getOverdueTasks() {
		return getActiveOverdueTasks();
	}
	
	public List<WorkflowTask> getActiveOverdueTasks(){
		return BeanUtil.turnToModelTaskList(taskService.getOverdueTasks());
	}
	
	@Deprecated
	public Map<String, Integer> getOverdueTasksNumByTransactor(Long companyId) {
		return taskService.getOverdueTasksNumByTransactor(companyId);
	}
	public Map<String, Integer> getOverdueTasksNumByTransactor() {
		return taskService.getOverdueTasksNumByTransactor();
	}
	@Deprecated
	public Integer getTasksNumByTransactor(Long companyId, String loginName) {
		return taskService.getTasksNumByTransactor(companyId, loginName);
	}
	@Deprecated
	public Integer getTasksNumByTransactor(String loginName ) {
		return getActiveTaskCountByTransactor(loginName);
	}
	public Integer getActiveTaskCountByTransactor(String loginName){
		return taskService.getTasksNumByTransactor(loginName);
	}
	@Deprecated
	public List<WorkflowTask> getTotalOverdueTasks(Long companyId) {
		return BeanUtil.turnToModelTaskList(taskService.getTotalOverdueTasks(companyId));
	}
	
	@Deprecated
	public List<WorkflowTask> getTotalOverdueTasks() {
		return getAllOverdueTasks();
	}
	
	public List<WorkflowTask> getAllOverdueTasks(){
		return BeanUtil.turnToModelTaskList(taskService.getTotalOverdueTasks());
	}
	
	@Deprecated
	public Map<String, Integer> getTotalOverdueTasksNumByTransactor(
			Long companyId) {
		return taskService.getTotalOverdueTasksNumByTransactor(companyId);
	}
	
	public Map<String, Integer> getTotalOverdueTasksNumByTransactor() {
		return getOverdueTaskCountGroupByTransactor();
	}
	
	public Map<String,Integer> getOverdueTaskCountGroupByTransactor(){
		return taskService.getTotalOverdueTasksNumByTransactor();
	}

	@Deprecated
	public Long getFormFlowableIdByTask(Long taskId, Long companyId) {
		com.norteksoft.task.entity.WorkflowTask task = taskService.getWorkflowTask(taskId);
    	return getDataIdByTask(task,companyId);
	}

	public List<Object[]> getTaskAndOpinion(Long taskId) {
		com.norteksoft.task.entity.WorkflowTask task = taskService.getWorkflowTask(taskId);
		return workflowInstanceManager.getTaskAndOpinion( task);
	}
	@Deprecated
	public void getAllTasksByUser(Long companyId, String loginName,
			Page<com.norteksoft.task.entity.WorkflowTask> page) {
		taskService.getAllTasksByUser(companyId, loginName,page);
		
	}
	@Deprecated
	public List<com.norteksoft.task.entity.WorkflowTask> getAllTasksByUser(Long companyId, String loginName) {
		return taskService.getAllTasksByUser(companyId, loginName);
	}

	@Deprecated
	public void getUnDoneTasksByUser(String loginName,
			Page<com.norteksoft.task.entity.WorkflowTask> page) {
		taskService.getAllTasksByUser(ContextUtils.getCompanyId(), loginName,page);
		
	}
	
	public List<WorkflowTask> getUnDoneTasksByUser( String loginName) {
		return getActiveTasksByLoginName(loginName);
	}
	
	public List<WorkflowTask> getActiveTasksByLoginName(String loginName){
		Assert.notNull(ContextUtils.getCompanyId(),"公司id不能为null");
		return BeanUtil.turnToModelTaskList(taskService.getAllTasksByUser(ContextUtils.getCompanyId(), loginName));
	}

	public void startInstance(String definitionCode,
			Integer definitionVersion, FormFlowable entity) {
		Assert.notNull(entity,"FormFlowable实体不能为null");
		WorkflowDefinition definition = getEnabledWorkflowDefinitionByCodeAndVersion(definitionCode,definitionVersion,getCompanyId(entity));
		startInstance(definition,entity);
	}
	
	public WorkflowDefinition getEnabledWorkflowDefinitionByCodeAndVersion(String definitionCode,
			Integer definitionVersion,Long companyId){
		WorkflowDefinition definition = workflowDefinitionManager.getEnabledWorkflowDefinitionByCodeAndVersion(definitionCode,definitionVersion,companyId);
		Assert.notNull(companyId,"companyId不能为null");
		if(definition==null){
			log.debug("not found started workflowDefinition by code:"+definitionCode);
			throw new NotFoundEnabledWorkflowDefinitionException("not found started workflowDefinition by code:"+definitionCode);
		}
		return definition;
	}
	
	public WorkflowDefinition getWfdByCodeAndVersion(String definitionCode,
			Integer definitionVersion,Long companyId,Long systemId){
		Assert.notNull(definitionVersion, "definitionVersion不能为null");
		Assert.notNull(companyId, "companyId不能为null");
		Assert.notNull(systemId, "systemId不能为null");
		WorkflowDefinition definition = workflowDefinitionManager.getWorkflowDefinitionByCodeAndVersion(definitionCode,definitionVersion,companyId,systemId);
		if(definition==null) throw new NotFoundEnabledWorkflowDefinitionException("not found started workflowDefinition by code:"+definitionCode);
		return definition;
	}

	public CompleteTaskTipType submitInstance(String definitionCode,
			Integer definitionVersion, FormFlowable entity) {
		log.debug(LOGMESSAGE_METHOD_PARAMETER + "String workflowDefinitionCode:" + definitionCode);
		log.debug(LOGMESSAGE_METHOD_PARAMETER + "Integer workflowDefinitionVersion:" + definitionVersion);
		log.debug(LOGMESSAGE_METHOD_PARAMETER + "FormFlowable entity:" + entity);
		Assert.notNull(entity, "entity不能为空");
		log.debug("workflowInfo:" + entity.getWorkflowInfo());
		String workflowId = null;
		if(entity.getWorkflowInfo()==null || entity.getWorkflowInfo().getWorkflowId() == null){
			WorkflowDefinition definition = getEnabledWorkflowDefinitionByCodeAndVersion(definitionCode,definitionVersion,getCompanyId(entity));
			workflowId = startInstance(definition, entity) ;
		}else{
			workflowId = entity.getWorkflowInfo().getWorkflowId();
		}
		log.debug("workflowId" + workflowId);
		return submitWorkflowById(workflowId,entity);
	}
	
	public List<String> getCandidateAddition(List<Long> dictIds) {
		return dataDictionaryManager.getCandidateAddition(dictIds);
	}
	public List<String> getCandidateAddition(Long dictId) {
		List<String> loginNames=new ArrayList<String>();
		return dataDictionaryManager.getCandidateAddition(dictId, loginNames);
	}

	public void saveDocument(com.norteksoft.product.api.entity.Document doc) {
		if(doc==null){
			log.debug("com.norteksoft.product.api.entity.Document实体不能为null");
			throw new RuntimeException("com.norteksoft.product.api.entity.Document实体不能为null");
		}
		Document document=null;
		if(doc.getId()==null){
			document=new Document();
			document.setCreatedTime(new Date());
		}else{
			document=workflowInstanceManager.getDocument(doc.getId());
		}
		if(document==null){
			log.debug("com.norteksoft.product.api.entity.Document实体不能为null");
			throw new RuntimeException("com.norteksoft.product.api.entity.Document实体不能为null");
		}
		document.setCustomField(doc.getCustomField());
		document.setRemark(doc.getDescript());
		document.setFileName(doc.getFileName());
		document.setFilePath(doc.getFilePath());
		document.setFileSize(doc.getFileSize());
		document.setFileType(doc.getFileType());
		document.setStatus(doc.getStatus());
		document.setSubject(doc.getSubject());
		if(doc.getTaskId()!=null){
			document.setTaskId(doc.getTaskId());
			com.norteksoft.task.entity.WorkflowTask task = taskService.getTask(doc.getTaskId());
			document.setTaskMode(task.getProcessingMode());
			document.setTaskName(task.getName());
			document.setWorkflowId(task.getProcessInstanceId());
			document.setCompanyId(task.getCompanyId());
			document.setCreator(task.getTransactor());
			document.setCreatorName(task.getTransactorName());
		}
		workflowInstanceManager.saveDocument(document);
		DocumentFile file=workflowInstanceManager.getDocumentFile(document.getId());
		if(file!=null){//兼容xtsoa历史文
			saveDocumentFile(doc,document,file);
		}else{
			FileService fileService =(FileService)ContextUtils.getBean("fileService");
			document.setFilePath(fileService.saveFile(doc.getFileBody()));
		}
		workflowInstanceManager.saveDocument(document);
	}
	
	private void saveDocumentFile(com.norteksoft.product.api.entity.Document doc,Document document,DocumentFile file){
		file.setDocumentId(document.getId());
		file.setCompanyId(document.getCompanyId());
		file.setFileBody(doc.getFileBody());
		workflowInstanceManager.saveDocumentFile(file);
	}
	
	public List<com.norteksoft.product.api.entity.WorkflowDefinition> getWorkflowDefinitionsByTypeCode(String typeNo) {
		WorkflowType type = workflowTypeManager.getWorkflowType(typeNo);
		if(type == null) throw new RuntimeException("没有查询到流程类型，流程编号["+typeNo+"]");
		List<WorkflowDefinition> wfds=workflowDefinitionManager.getWfDefinitionsByType(type.getCompanyId(), type.getId());
		List<com.norteksoft.product.api.entity.WorkflowDefinition> result=new ArrayList<com.norteksoft.product.api.entity.WorkflowDefinition>();
		for(WorkflowDefinition definition:wfds){
			result.add(getWorkflowDefinitionParameter(definition));
		}
		return result;
	}

	public HashMap<String, String> getUserNames(List<Long> dictIds) {
		return dataDictionaryManager.getUserNames(dictIds);
	}

	public HashMap<String, String> getUserNames(Long dictId) {
		HashMap<String, String> loginNames = new HashMap<String, String>();
		return dataDictionaryManager.getUser(dictId, loginNames);
	}
	
	private TaskProcessingMode getTaskMode(FormFlowable entity){
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"必须先启动流程 ");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"必须先启动流程 ");
		String activityTaskName = entity.getWorkflowInfo().getCurrentActivityName();
		log.debug("当前环节的名字：" + activityTaskName);
		return getTaskMode(activityTaskName,entity.getWorkflowInfo().getWorkflowId());
	}
	private TaskProcessingMode getTaskMode(String activityTaskName,String instanceId ){
		Assert.notNull(instanceId,"参数instanceId不可为空");
		WorkflowInstance instance = this.getInstance(instanceId);
		String processingMode = DefinitionXmlParse.getTaskProcessingMode(
				instance.getProcessDefinitionId(), activityTaskName);
		return TaskProcessingMode.getTaskModeFromStringToEnum(processingMode);
	}

	public void saveDocument(com.norteksoft.product.api.entity.Document doc, FormFlowable entity) {
		if(doc==null){
			log.debug("com.norteksoft.product.api.entity.Document实体不能为null");
			throw new RuntimeException("com.norteksoft.product.api.entity.Document实体不能为null");
		}
		Document document=null;
		if(doc.getId()==null){
			document=new Document();
			document.setCreatedTime(new Date());
		}else{
			document=workflowInstanceManager.getDocument(doc.getId());
		}
		if(document==null){
			log.debug("com.norteksoft.product.api.entity.Document实体不能为null");
			throw new RuntimeException("com.norteksoft.product.api.entity.Document实体不能为null");
		}
		document.setCustomField(doc.getCustomField());
		document.setRemark(doc.getDescript());
		document.setFileName(doc.getFileName());
		document.setFilePath(doc.getFilePath());
		document.setFileSize(doc.getFileSize());
		document.setFileType(doc.getFileType());
		document.setStatus(doc.getStatus());
		document.setSubject(doc.getSubject());
		if(entity.getWorkflowInfo()!=null){
			document.setTaskMode(getTaskMode(entity));
			document.setTaskName(entity.getWorkflowInfo().getCurrentActivityName());
			document.setWorkflowId(entity.getWorkflowInfo().getWorkflowId());
		}
		document.setCompanyId(getCompanyId(entity));
		workflowInstanceManager.saveDocument(document);
		DocumentFile file=workflowInstanceManager.getDocumentFile(document.getId());
		if(file!=null){//兼容xtsoa历史文
			saveDocumentFile(doc,document,file);
		}else{
			FileService fileService =(FileService)ContextUtils.getBean("fileService");
			document.setFilePath(fileService.saveFile(doc.getFileBody()));
		}
		workflowInstanceManager.saveDocument(document);
	}

	public void saveAttachment(com.norteksoft.product.api.entity.WorkflowAttachment attachment,
			FormFlowable entity) {
		Assert.notNull(attachment, "附件attachment不能为null");
		Assert.notNull(entity, "实体entity不能为null");
		Assert.notNull(entity.getWorkflowInfo(), "entity.getWorkflowInfo()不能为null");
		saveAttachment(attachment,entity.getWorkflowInfo().getWorkflowId());
	}

	public List<String> getCandidate(String title) {
		return dataDictionaryManager.getCandidate(title);
	}
	public boolean isFirstTask(Long taskId) {
		if(taskId==null)return true;
		WorkflowTask task=getTask(taskId);
		Assert.notNull(task,"判断是否是第一环节任务时，任务不能为null");
		WorkflowInstance wi=getInstance(task.getProcessInstanceId());
		Assert.notNull(wi,"判断是否是第一环节任务时，流程实例不能为null");
		String firstTaskName=DefinitionXmlParse.getFirstTaskName(wi.getProcessDefinitionId());
		if(task.getName().equals(firstTaskName)){
			return true;
		}
		return false;
	}
	
	public List<String> getTransactorsExceptTask(Long taskId) {
		return getTransactorsExcludeGivenTask(taskId);
	}
	
	public List<String> getTransactorsExcludeGivenTask(Long taskId){
		Assert.notNull(taskId,"任务id不能为null");
		return taskService.getTransactorsExceptTask(taskId);
	}
	
	@Deprecated
	public CompleteTaskTipType assignTask(Long taskId,
			Collection<String> transcators) {
		return taskService.assignTask(taskId, transcators);
	}

	public CompleteTaskTipType assignTask(Long taskId, String transcator) {
		return taskService.assignTask(taskId, transcator);
	}

	public List<com.norteksoft.product.api.entity.Opinion> getOpinions(FormFlowable entity, String... taskNames) {
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getOpinions(workflowInstanceManager.getOpinions(entity.getWorkflowInfo().getWorkflowId(), getCompanyId(entity), taskNames));
	}

	public List<com.norteksoft.product.api.entity.Opinion> getOpinionsExceptTaskName(
			FormFlowable entity, String... taskNames) {
		return getOpinionsExcludeTaskName(entity, taskNames);
	}
	
	public List<com.norteksoft.product.api.entity.Opinion> getOpinionsExcludeTaskName(FormFlowable entity,String... taskNames){
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getOpinions(workflowInstanceManager.getOpinionsExceptTaskName(entity.getWorkflowInfo().getWorkflowId(), this.getCompanyId(entity), taskNames));
	}

	public List<com.norteksoft.product.api.entity.Opinion> getOpinionsByTacheCode(FormFlowable entity,
			String tacheCode) {
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getOpinions(workflowInstanceManager.getOpinionsByTacheCode(entity.getWorkflowInfo().getWorkflowId(), this.getCompanyId(entity), tacheCode));
	}

	public List<com.norteksoft.product.api.entity.Opinion> getOpinionsByTacheCode(FormFlowable entity,
			String... tacheCodes) {
		return getOpinionsByTaskCode(entity, tacheCodes);
	}
	
	public List<com.norteksoft.product.api.entity.Opinion> getOpinionsByTaskCode(FormFlowable entity,String... tacheCodes){
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getOpinions(workflowInstanceManager.getOpinionsByTacheCode(entity.getWorkflowInfo().getWorkflowId(), this.getCompanyId(entity), tacheCodes));
	}

	public List<com.norteksoft.product.api.entity.Opinion> getOpinionsByTaskName(FormFlowable entity,
			String taskName) {
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getOpinions(workflowInstanceManager.getOpinionsByTaskName(entity.getWorkflowInfo().getWorkflowId(), this.getCompanyId(entity), taskName));
	}

	public List<com.norteksoft.product.api.entity.Opinion> getOpinionsByTaskName(FormFlowable entity,
			String... taskNames) {
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getOpinions(workflowInstanceManager.getOpinionsByTaskName(entity.getWorkflowInfo().getWorkflowId(), this.getCompanyId(entity), taskNames));
	}

	public List<com.norteksoft.product.api.entity.WorkflowDefinition> getWorkflowDefinitionsByCode(
			String workflowDefinitionCode) {
		List<WorkflowDefinition> wfds=workflowDefinitionManager.getWfDefinitionsByCode(workflowDefinitionCode);
		List<com.norteksoft.product.api.entity.WorkflowDefinition> result=new ArrayList<com.norteksoft.product.api.entity.WorkflowDefinition>();
		for(WorkflowDefinition definition:wfds){
			result.add(getWorkflowDefinitionParameter(definition));
		}
		return result;
	}
	public CompleteTaskTipType completeWorkflowTask(Long taskId,
			TaskProcessingResult result) {
		return completeWorkflowTask(taskId, result, null);
	}
	public CompleteTaskTipType completeWorkflowTask(WorkflowTask task,
			TaskProcessingResult result) {
		return completeWorkflowTask(task,result,null);
	}
	public List<DataDictionary> queryDataDicts(String loginName) {
		return BeanUtil.turnToModelDataDictionaryList(dataDictionaryManager.queryDataDicts(loginName));
	}

	public void setTaskRead(Long taskId) {
		Assert.notNull(taskId,"任务id不能为null");
		WorkflowTask task=getTask(taskId);
		Assert.notNull(task,"任务不能为null");
		task.setRead(true);
		taskService.saveTask(BeanUtil.turnToTask(task));
		
	}

	public com.norteksoft.product.api.entity.WorkflowDefinition getWorkflowDefinitionByCodeAndVersion(
			String workflowDefinitionCode, Integer workflowDefinitionVersion) {
		WorkflowDefinition wfd = getWfdByCodeAndVersion(workflowDefinitionCode, workflowDefinitionVersion,ContextUtils.getCompanyId(),ContextUtils.getSystemId());
		if(wfd == null) throw new RuntimeException(
				"没有查询到流程定义，流程编号["+workflowDefinitionCode+
				"], 流程版本号["+workflowDefinitionVersion+"], 公司ID["+ContextUtils.getCompanyId()+"], 系统ID["+ContextUtils.getSystemId()+"]");
		return getWorkflowDefinitionParameter(wfd);
	}
	public com.norteksoft.product.api.entity.WorkflowDefinition getWorkflowDefinitionByCodeAndVersion(
			String workflowDefinitionCode, Integer workflowDefinitionVersion,Long systemId) {
		WorkflowDefinition wfd = getWfdByCodeAndVersion(workflowDefinitionCode, workflowDefinitionVersion,ContextUtils.getCompanyId(),systemId);
		if(wfd == null) throw new RuntimeException(
				"没有查询到流程定义，流程编号["+workflowDefinitionCode+
				"], 流程版本号["+workflowDefinitionVersion+"], 公司ID["+ContextUtils.getCompanyId()+"], 系统ID["+ContextUtils.getSystemId()+"]");
		return getWorkflowDefinitionParameter(wfd);
	}

	public CompleteTaskTipType taskJump(String workflowId, String backTo,
			Long companyId) {
		Assert.notNull(companyId,"环节跳转时，公司id不能为null");
		return taskService.taskJump(workflowId, backTo, companyId);
	}

	public boolean isInstanceComplete(FormFlowable entity) {
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		com.norteksoft.wf.engine.entity.WorkflowInstance instance=workflowInstanceManager.getWorkflowInstance(entity.getWorkflowInfo().getWorkflowId());
		Assert.notNull(instance,"instance不能为空 ");
		if(instance.getProcessState()==ProcessState.END||instance.getProcessState()==ProcessState.MANUAL_END)return true;
		return false;
	}


	public void saveAttachment(com.norteksoft.product.api.entity.WorkflowAttachment attachment,
			Long taskId) {
		Assert.notNull(taskId, "taskId不能为null");
		Assert.notNull(attachment, "附件attachment不能为null");
		WorkflowTask task = this.getTask(taskId);
		WorkflowAttachment document=null;
		if(attachment.getId()==null){
			document=new WorkflowAttachment();
		}else{
			document=workflowInstanceManager.getAttachment(attachment.getId());
		}
		document.setFileName(attachment.getFileName());
		document.setFileSize(attachment.getFileSize());
		document.setFileType(attachment.getFileType());
		
		attachment.setTaskId(taskId);
		if(task!=null){
			document.setTaskMode(task.getProcessingMode());
			document.setTaskName(task.getName());
			document.setWorkflowId(task.getProcessInstanceId());
		}
		document.setCompanyId(task.getCompanyId());
		FileService fileService =(FileService)ContextUtils.getBean("fileService");
		document.setFilePath(fileService.saveFile(attachment.getFileBody()));
		workflowInstanceManager.saveAttachment(document);
	}

	public void saveOpinion(com.norteksoft.product.api.entity.Opinion opinionp, Long taskId) {
		Opinion opinion=null;
		if(opinionp.getId()==null){
			opinion=new Opinion();
		}else{
			opinion=workflowInstanceManager.getOpinionsById(opinionp.getId());
		}
		opinion.setCustomField(opinionp.getCustomField());
		opinion.setOpinion(opinionp.getOpinion());
		opinion.setCreatedTime(new Date());
		if(taskId!=null){
			WorkflowTask task = this.getTask(taskId);
			opinion.setTaskMode(task.getProcessingMode());
			opinion.setTaskName(task.getName());
			opinion.setCompanyId(task.getCompanyId());
			opinion.setWorkflowId(task.getProcessInstanceId());
			opinion.setTaskId(taskId);
			opinion.setTransactor(task.getTransactor());
			opinion.setDelegateFlag(StringUtils.isEmpty(task.getTrustor())?false:true);
			Long mainDepartmentId = acsUtils.getUserByLoginName(task.getTransactor()).getMainDepartmentId();
			if(mainDepartmentId!=null){
				Department dept=acsUtils.getDepartmentById(mainDepartmentId);
				opinion.setDepartmentName(dept.getName());
			}
			opinion.setTaskCode(task.getCode());
		}
		workflowInstanceManager.saveOpinion(opinion);
		
	}

	public CompleteTaskTipType isNeedChoiceTache(WorkflowTask task) {
		return getOptionalTasks(task);
	}
	
	@Deprecated
	public CompleteTaskTipType isNeedChoiceTache(com.norteksoft.task.entity.WorkflowTask task) {
		return getOptionalTasks(task);
	}
	
	public CompleteTaskTipType getOptionalTasks(WorkflowTask task){
		return taskService.isNeedChoiceTache(BeanUtil.turnToTask(task));
	}
	@Deprecated
	public CompleteTaskTipType getOptionalTasks(com.norteksoft.task.entity.WorkflowTask task){
		return taskService.isNeedChoiceTache(task);
	}

	public List<String[]> getActivityTaskTransactors(FormFlowable entity) {
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getActivityTaskTransactors(entity.getWorkflowInfo().getWorkflowId());
	}
	public Set<String> getHandledTransactors(FormFlowable entity) {
		return getCompletedTaskTransactor(entity);
	}
	
	public Set<String> getCompletedTaskTransactor(FormFlowable entity){
		String workflowId=null;
		if(entity.getWorkflowInfo()!=null){
			workflowId=entity.getWorkflowInfo().getWorkflowId();
		}
		if(workflowId==null)return null;
		WorkflowInstance wi=getInstance(workflowId);
		return taskService.getHandledTransactors(BeanUtil.turnToWorkflowInstance(wi));
	}
	
	public Set<String> getHandledTransactors(Long taskId) {
		return getCompletedTaskTransactor(taskId);
	}
	
	public Set<String> getCompletedTaskTransactor(Long taskId){
		if(taskId==null)return null;
		WorkflowTask task=getTask(taskId);
		Assert.notNull(task,"任务不能为null");
		WorkflowInstance wi=getInstance(task.getProcessInstanceId());
		Assert.notNull(wi,"流程实例不能为null");
		return taskService.getHandledTransactors(BeanUtil.turnToWorkflowInstance(wi));
	}

	public TaskPermission getActivityPermission(Long taskId) {
		return workflowRightsManager.getActivityPermission(taskId);
	}

	public TaskPermission getActivityPermission(String definitionCode) {
		WorkflowDefinition definition = this.getEnabledHighestVersionDefinition(definitionCode);
		return workflowRightsManager.getActivityPermission(definition);
	}

	public TaskPermission getActivityPermission(String definitionCode,
			Integer definitionVersion) {
		WorkflowDefinition definition = this.getWfdByCodeAndVersion(definitionCode, definitionVersion,ContextUtils.getCompanyId(),ContextUtils.getSystemId());
		return workflowRightsManager.getActivityPermission(definition);
	}

	public String getDocumentPermission(Long taskId) {
		StringBuilder editType = new StringBuilder("-1");// "-1,0,1,1,0,0,1,1";//查看保留痕迹
		if(taskId==null)return null;
		com.norteksoft.task.entity.WorkflowTask task=taskService.getTask(taskId);
		if(task==null)return null;
		 //是否保户文档
		 if((TaskState.WAIT_TRANSACT.getIndex().equals(task.getActive())|| TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex().equals(task.getActive()))&&workflowRightsManager.officialTextEditRight(task)){
			 editType.append(",0");
		 }else{
			 editType.append(",1");
		 }
		 //是否显示痕迹
		 if(workflowRightsManager.officialTextViewTrace(task)){
			 editType.append(",1");
		 }else{
			 editType.append(",0");
		 }
		 //是否保留痕迹
		 if(workflowRightsManager.officialTextRetainTrace(task)){
			 editType.append(",1");
		 }else{
			 editType.append(",0");
		 }
		 editType.append(",0,0,1,1");
		 return editType.toString();
	}

	public void saveTask(WorkflowTask task) {
		taskService.saveTask(BeanUtil.turnToTask(task));
		
	}
	
	@Deprecated
	public void saveTask(com.norteksoft.task.entity.WorkflowTask task) {
		taskService.saveTask(task);
		
	}

	public Map<String, String> getExtendFields(FormFlowable entity) {
		if(entity.getWorkflowInfo()==null)return new HashMap<String, String>();
		WorkflowInstance instance = this.getInstance(entity.getWorkflowInfo().getWorkflowId());
		return getExtendFields(instance);
	}
	
	public Map<String, String> getExtendFields(Long taskId) {
		if(taskId==null)return new HashMap<String, String>();
		com.norteksoft.task.entity.WorkflowTask task=taskService.getTask(taskId);
		if(task==null)return new HashMap<String, String>();
		WorkflowInstance instance = this.getInstance(task.getProcessInstanceId());
		return getExtendFields(instance);
	}
	
	private Map<String, String> getExtendFields(WorkflowInstance instance){
		if(instance==null)return new HashMap<String, String>();
		Long definitionId=instance.getWorkflowDefinitionId();
		if(definitionId==null)return new HashMap<String, String>();
		WorkflowDefinition definition = workflowDefinitionManager.getWfDefinition(definitionId);
		if(definition==null)return new HashMap<String, String>();
		return DefinitionXmlParse.getExtendFields(definition.getProcessId());
	}

	public void fillEntityByDefinition(Map data, String wfDefinationCode,Long... systemId) {
		Long sysId=ContextUtils.getSystemId();
		if(systemId.length>0){
			sysId=systemId[0];
		}
		autoFilledEntityBeforeByDefinitionId(null, getEnabledHighestVersionDefinitionBySystem(wfDefinationCode,sysId).getId(),data);
	}

	public void fillEntityByDefinition(Map data, String wfDefinationCode,
			Integer version,Long... systemId) {
		Long sysId=ContextUtils.getSystemId();
		if(systemId.length>0){
			sysId=systemId[0];
		}
		autoFilledEntityBeforeByDefinitionId(null, getWorkflowDefinitionByCodeAndVersion(wfDefinationCode, version,sysId).getId(),data);
	}

	public void fillEntityByTask(Map data, Long taskId) {
		fillEntityByTask(null, taskId,data);
	}

	public com.norteksoft.product.api.entity.Document createDocument(
			String instanceId, String fileType) {
		Assert.notNull(instanceId,"参数实例id不可为空");
		Assert.notNull(ContextUtils.getCompanyId(), "公司Id不能为null");
		return createDocument(instanceId,fileType,ContextUtils.getCompanyId());
	}

	public List<com.norteksoft.product.api.entity.Document> getDocuments(
			String instanceId) {
		Assert.notNull(instanceId, "instanceId实例id不能为null");
		Assert.notNull(ContextUtils.getCompanyId(), "公司Id不能为null");
		Assert.notNull(getLoginName(), "当前用户登录名不能为null");
		List<Document> docs=workflowInstanceManager.getDocuments(instanceId,ContextUtils.getCompanyId());
		for(Document doc:docs){
			//用户有创建正文权限,即使没有删除权限也可删除自己当前环节创建的正文
			if(getLoginName().equals(doc.getCreator())){
				doc.setDeleteSetting(true);
			}
		}
		return getDocuments(docs);
	}

	public List<com.norteksoft.product.api.entity.Document> getDocumentsExcludeCustomField(
			String instanceId, String customField) {
		Assert.notNull(instanceId, "instanceId实例id不能为null");
		Assert.notNull(getLoginName(), "当前用户登录名不能为null");
		List<Document> docs=getDocumentsExceptCustomField(instanceId,customField);
		for(Document doc:docs){
			//用户有创建正文权限,即使没有删除权限也可删除自己当前环节创建的正文
			if(getLoginName().equals(doc.getCreator())){
				doc.setDeleteSetting(true);
			}
		}
		return getDocuments(docs);
	}

	public List<com.norteksoft.product.api.entity.Document> getDocumentsExcludeTaskMode(
			String instanceId, TaskProcessingMode taskMode) {
		Assert.notNull(instanceId, "instanceId实例id不能为null");
		Assert.notNull(getLoginName(), "当前用户登录名不能为null");
		List<Document> docs=getDocumentsExceptTaskMode(instanceId,taskMode);
		for(Document doc:docs){
			//用户有创建正文权限,即使没有删除权限也可删除自己当前环节创建的正文
			if(getLoginName().equals(doc.getCreator())){
				doc.setDeleteSetting(true);
			}
		}
		return getDocuments(docs);
	}

	public List<com.norteksoft.product.api.entity.Document> getDocumentsExcludeTaskName(
			String instanceId, String taskName) {
		Assert.notNull(instanceId, "instanceId实例id不能为null");
		Assert.notNull(getLoginName(), "当前用户登录名不能为null");
		List<Document> docs=getDocumentsExceptTaskName(instanceId,taskName);
		for(Document doc:docs){
			//用户有创建正文权限,即使没有删除权限也可删除自己当前环节创建的正文
			if(getLoginName().equals(doc.getCreator())){
				doc.setDeleteSetting(true);
			}
		}
		return getDocuments(docs);
	}

	public List<com.norteksoft.product.api.entity.Document> getAllDocuments(
			String instanceId, String taskName) {
		Assert.notNull(instanceId, "instanceId实例id不能为null");
		Assert.notNull(getLoginName(), "当前用户登录名不能为null");
		List<Document> docs=getDocuments(instanceId,taskName);
		for(Document doc:docs){
			//用户有创建正文权限,即使没有删除权限也可删除自己当前环节创建的正文
			if(getLoginName().equals(doc.getCreator())){
				doc.setDeleteSetting(true);
			}
		}
		return getDocuments(docs);
	}

	public List<com.norteksoft.product.api.entity.Document> getAllDocumentsByCustomField(
			String instanceId, String customField) {
		Assert.notNull(instanceId, "instanceId实例id不能为null");
		Assert.notNull(getLoginName(), "当前用户登录名不能为null");
		List<Document> docs=getDocumentsByCustomField(instanceId,customField);
		for(Document doc:docs){
			//用户有创建正文权限,即使没有删除权限也可删除自己当前环节创建的正文
			if(getLoginName().equals(doc.getCreator())){
				doc.setDeleteSetting(true);
			}
		}
		return getDocuments(docs);
	}

	public List<com.norteksoft.product.api.entity.Document> getAllDocuments(
			String instanceId, TaskProcessingMode taskMode) {
		Assert.notNull(instanceId, "instanceId实例id不能为null");
		Assert.notNull(getLoginName(), "当前用户登录名不能为null");
		List<Document> docs=getDocuments(instanceId,taskMode);
		for(Document doc:docs){
			//用户有创建正文权限,即使没有删除权限也可删除自己当前环节创建的正文
			if(getLoginName().equals(doc.getCreator())){
				doc.setDeleteSetting(true);
			}
		}
		return getDocuments(docs);
	}

	public List<com.norteksoft.product.api.entity.WorkflowAttachment> getAttachmentsExcludeCustomField(
			String instanceId, String customField) {
		Assert.notNull(instanceId, "instanceId实例id不能为null");
		Assert.notNull(getLoginName(), "当前用户登录名不能为null");
		List<WorkflowAttachment> attchments=workflowAttachmentManager.getAttachmentsExceptCustomField(instanceId,customField);
		for(WorkflowAttachment att:attchments){
			//用户有创建附件权限,即使没有删除权限也可删除自己当前环节创建的附件
			if(getLoginName().equals(att.getTransactor())){
				att.setDeleteSetting(true);
			}
		}
		return getAttachments(attchments);
	}

	public List<com.norteksoft.product.api.entity.WorkflowAttachment> getAttachmentsExcludeTaskMode(
			String instanceId, TaskProcessingMode taskMode) {
		Assert.notNull(taskMode,"任务模式不可为空");
		Assert.notNull(instanceId, "instanceId实例id不能为null");
		Assert.notNull(getLoginName(), "当前用户登录名不能为null");
		List<WorkflowAttachment> attchments=getAttachmentsExceptTaskMode(instanceId,taskMode);
		for(WorkflowAttachment att:attchments){
			//用户有创建附件权限,即使没有删除权限也可删除自己当前环节创建的附件
			if(getLoginName().equals(att.getTransactor())){
				att.setDeleteSetting(true);
			}
		}
		return getAttachments(attchments);
	}

	public List<com.norteksoft.product.api.entity.WorkflowAttachment> getAttachmentsExcludeTaskName(
			String instanceId, String taskName) {
		Assert.notNull(taskName,"任务名称不可为空");
		Assert.notNull(instanceId, "instanceId实例id不能为null");
		Assert.notNull(getLoginName(), "当前用户登录名不能为null");
		List<WorkflowAttachment> attchments=getAttachmentsExceptTaskName(instanceId,taskName);
		for(WorkflowAttachment att:attchments){
			//用户有创建附件权限,即使没有删除权限也可删除自己当前环节创建的附件
			if(getLoginName().equals(att.getTransactor())){
				att.setDeleteSetting(true);
			}
		}
		return getAttachments(attchments);
	}

	public void saveAttachment(
			com.norteksoft.product.api.entity.WorkflowAttachment attachment,
			String instanceId) {
		Assert.notNull(attachment, "附件attachment不能为null");
		Assert.notNull(instanceId, "instanceId实例id不能为null");
		WorkflowAttachment document=null;
		if(attachment.getId()==null){
			document=new WorkflowAttachment();
		}else{
			document=workflowInstanceManager.getAttachment(attachment.getId());
		}
		document.setFileName(attachment.getFileName());
		document.setFileSize(attachment.getFileSize());
		document.setFileType(attachment.getFileType());
		WorkflowInstance instance=getInstance(instanceId);
		if(instance!=null){
			document.setTaskMode(getTaskMode(instance.getCurrentActivity(),instanceId));
			document.setTaskName(instance.getCurrentActivity());
			document.setWorkflowId(instanceId);
			document.setCompanyId(instance.getCompanyId());
		}
		FileService fileService =(FileService)ContextUtils.getBean("fileService");
		document.setFilePath(fileService.saveFile(attachment.getFileBody()));
		workflowInstanceManager.saveAttachment(document);
		
	}

	public List<com.norteksoft.product.api.entity.WorkflowAttachment> getAllAttachments(
			String instanceId) {
		Assert.notNull(instanceId,"参数instanceId不可为空");
		Assert.notNull(getLoginName(), "当前用户登录名不能为null");
		Assert.notNull(ContextUtils.getCompanyId(), "公司id不能为null");
		List<WorkflowAttachment> attchments=workflowInstanceManager.getAttachments(instanceId,ContextUtils.getCompanyId());
		for(WorkflowAttachment att:attchments){
			//用户有创建附件权限,即使没有删除权限也可删除自己当前环节创建的附件
			if(getLoginName().equals(att.getTransactor())){
				att.setDeleteSetting(true);
			}
		}
		return getAttachments(attchments);
	}

	public List<com.norteksoft.product.api.entity.WorkflowAttachment> getAllAttachments(
			String instanceId, TaskProcessingMode taskMode) {
		Assert.notNull(taskMode,"任务模式不可为空");
		Assert.notNull(instanceId,"参数instanceId不可为空");
		Assert.notNull(getLoginName(), "当前用户登录名不能为null");
		List<WorkflowAttachment> attchments=getAttachments(instanceId, taskMode);
		for(WorkflowAttachment att:attchments){
			//用户有创建附件权限,即使没有删除权限也可删除自己当前环节创建的附件
			if(getLoginName().equals(att.getTransactor())){
				att.setDeleteSetting(true);
			}
		}
		return getAttachments(attchments);
	}

	public List<com.norteksoft.product.api.entity.WorkflowAttachment> getAllAttachments(
			String instanceId, String taskName) {
		Assert.notNull(taskName,"任务名称不可为空");
		Assert.notNull(instanceId,"参数instanceId不可为空");
		Assert.notNull(getLoginName(), "当前用户登录名不能为null");
		List<WorkflowAttachment> attchments=getAttachments(instanceId, taskName);
		for(WorkflowAttachment att:attchments){
			//用户有创建附件权限,即使没有删除权限也可删除自己当前环节创建的附件
			if(getLoginName().equals(att.getTransactor())){
				att.setDeleteSetting(true);
			}
		}
		return getAttachments(attchments);
	}

	public List<com.norteksoft.product.api.entity.WorkflowAttachment> getAllAttachmentsByCustomField(
			String instanceId, String customField) {
		Assert.notNull(customField,"自定义类别不可为空");
		Assert.notNull(instanceId,"参数instanceId不可为空");
		Assert.notNull(getLoginName(), "当前用户登录名不能为null");
		List<WorkflowAttachment> attchments=workflowAttachmentManager.getAttachmentsByCustomField(instanceId,customField);
		for(WorkflowAttachment att:attchments){
			//用户有创建附件权限,即使没有删除权限也可删除自己当前环节创建的附件
			if(getLoginName().equals(att.getTransactor())){
				att.setDeleteSetting(true);
			}
		}
		return getAttachments(attchments);
	}

	public Map  startCustomInstance(String definitionCode) {
		Assert.notNull(definitionCode,"definitionCode不可为空");
		WorkflowDefinition definition = getEnabledHighestVersionDefinition(definitionCode,ContextUtils.getCompanyId());
		Assert.notNull(definition,"definition不可为空");
		return startMyCustomInstance(definition.getProcessId());
	}
	// processId jBPM流程定义ID
	private Map startMyCustomInstance(String processId){
		Map myresult=new HashMap();
		
		Map<String,String[]> parameterMap = Struts2Utils.getRequest().getParameterMap();
		Map<String,String[]> parameter = new HashMap<String, String[]>();
		parameter.putAll(parameterMap);
		
		String instanceId = null;
		// 从参数中获取 instanceId
		String[] instanceIds = parameter.get("instance_id");
		if(instanceIds != null && StringUtils.isNotEmpty(instanceIds[0])){
			instanceId = instanceIds[0];
		}
		
		// 是否新发起流程
		boolean isNewStart = false;
		if(instanceId == null){
			//  发起流程
			instanceId = workflowInstanceManager.startWorkflowInstance(processId, 6,null);
			parameter.put("instance_id", new String[]{instanceId});
			isNewStart = true;
		}
		// 保存数据
		Long dataId = saveData(parameter);
		parameter.put("id", new String[]{dataId+""});
		
		if(isNewStart){
			com.norteksoft.wf.engine.entity.WorkflowInstance workflow = workflowInstanceManager.getWorkflowInstance(instanceId);
			Assert.notNull(workflow,"发起流程后流程实例不可为空");
			Assert.notNull(getLoginName(), "当前登录名不可为null");
			com.norteksoft.task.entity.WorkflowTask task= taskService.getFirstTask(instanceId,getLoginName());
			if(task==null){
				log.debug("第一环节任务不能为null");
				throw new RuntimeException("第一环节任务不能为null");
			}
			workflow.setDataId(dataId);
			workflow.setProcessState(ProcessState.UNSUBMIT);
			workflow.setCurrentActivity(task.getName());
			workflowInstanceManager.saveWorkflowInstance(workflow);
			//设置工作流相关的一些字段的值
			FormView formView=formManager.getFormView(workflow.getFormId());
			//Object data = jdbcDao.getDataMap(formView.getDataTable().getName(), dataId);
			//Map<String, String[]> result=(Map<String, String[]>)data;
			WorkflowDefinition workflowDefinition = workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
			parameter.put("form_code", new String[]{formView.getCode()});
			parameter.put("form_version", new String[]{formView.getVersion()+""});
			parameter.put("workflow_definition_id", new String[]{processId});
			parameter.put("workflow_definition_name", new String[]{workflowDefinition.getName()});
			parameter.put("workflow_definition_code", new String[]{workflowDefinition.getCode()});
			parameter.put("workflow_definition_version", new String[]{workflowDefinition.getVersion()+""});
			parameter.put("first_task_id", new String[]{task.getId()+""});
			parameter.put("current_activity_name", new String[]{task.getName()});
			parameter.put("form_id", new String[]{workflow.getFormId()+""});
			parameter.put("process_state", new String[]{ProcessState.UNSUBMIT.getIndex()+""});
			saveData(parameter);
		}
		myresult.put("dataId", dataId);
		myresult.put("instanceId", instanceId);
		return myresult;
	}
	
	
	public Long saveData(Map<String,String[]> parameter){
		String formCode=null;
		Integer formVersion=null;
		
		String[] formCodes=parameter.get(JdbcSupport.FORM_CODE);
		if(formCodes!=null){
			formCode=formCodes[0];
		}
		String[] formVersions=parameter.get(JdbcSupport.FORM_VERSION);
		if(formVersions!=null){
			formVersion=Integer.valueOf(formVersions[0]);
		}
		Long id = null;
		if(StringUtils.isNotEmpty(formCode)&&formVersion!=null){
			FormView form = formManager.getFormViewByCodeAndVersion(ContextUtils.getCompanyId(), formCode, formVersion);
			if(form!=null){
				List<FormControl> controls = formManager.getControls(form);
				String[] ids = new String[1];
				Object idobj=parameter.get("id");
				if(idobj instanceof Long){
					ids[0]=idobj+"";
				}else{
					ids=(String[])idobj;
				}
				if(ids != null && StringUtils.isNotEmpty(ids[0])){
					id = jdbcDao.updateTable(parameter, form, controls, Long.parseLong(ids[0]));
				}else{
					id = jdbcDao.insertTable(parameter, form, controls);
				}
			}
		}
		return id;
	}

	public Map startCustomInstance(String definitionCode, Integer definitionVersion) {
		Assert.notNull(definitionCode,"definitionCode不可为空");
		Assert.notNull(definitionVersion,"definitionVersion不可为空");
		Assert.notNull(ContextUtils.getCompanyId(),"公司id不可为空");
		WorkflowDefinition definition = getEnabledWorkflowDefinitionByCodeAndVersion(definitionCode,definitionVersion,ContextUtils.getCompanyId());
		Assert.notNull(definition,"definition不可为空");
		return startMyCustomInstance(definition.getProcessId());
	}

	public Map startCustomInstance(Long definitionId) {
		Assert.notNull(definitionId,"definitionId不可为空");
		WorkflowDefinition definition = this.getWorkflowDefinitionById(definitionId);
		Assert.notNull(definition,"definition不可为空");
		return startMyCustomInstance(definition.getProcessId());
	}

	public Map submitCustomInstance(String definitionCode) {
		Assert.notNull(definitionCode,"definitionCode不可为空");
		Assert.notNull(ContextUtils.getCompanyId(),"公司id不可为空");
		
		WorkflowDefinition definition = getEnabledHighestVersionDefinition(definitionCode,ContextUtils.getCompanyId());
		Assert.notNull(definition,"definition不可为空");
		Map myresult=startMyCustomInstance(definition.getProcessId());
		String instanceId=(String)myresult.get("instanceId");
		
		com.norteksoft.wf.engine.entity.WorkflowInstance workflow = workflowInstanceManager.getWorkflowInstance(instanceId);
		CompleteTaskTipType result=submitCustomInstance(workflow);
		Map completeResult= new HashMap();
		completeResult.put("dataId",(Long)myresult.get("dataId"));
		completeResult.put("instanceId",instanceId);
		completeResult.put("result",result);
		return completeResult;
	}
	
	private CompleteTaskTipType submitCustomInstance(com.norteksoft.wf.engine.entity.WorkflowInstance workflow){
		String workflowId = workflow.getProcessInstanceId();
		if(workflow==null){
			log.debug("流程实例不能为null");
			throw new RuntimeException("流程实例不能为null");
		}
		String processId = workflow.getProcessDefinitionId();
		log.debug(LOG_CONTENT+"流程实例workflow" +LOG_FLAG + workflow);
		
		Assert.notNull(getLoginName(), "当前登录名不可为空");
		log.debug(LOG_CONTENT+"当前登录名getLoginName():" +LOG_FLAG+getLoginName());
		com.norteksoft.task.entity.WorkflowTask task = taskService.getFirstTask(workflowId, getLoginName());
		Assert.notNull(task,"查询的第一个任务不应该为空");
		//设置第一个任务可见
		task.setVisible(true);
		taskService.addTitle(task, processId,null);
		taskService.saveTask(task);
		log.debug(LOG_CONTENT+"firstTask:"+LOG_FLAG + task);
		
		CompleteTaskTipType completeTaskTipType = taskService.completeWorkflowTask(task, TaskProcessingResult.SUBMIT);
		log.debug(LOG_CONTENT+"第一环节任务完成结果"+LOG_FLAG + completeTaskTipType);
		workflow.setSubmitTime(new Date(System.currentTimeMillis()));
		if(completeTaskTipType==CompleteTaskTipType.OK&&workflow.getProcessState()==ProcessState.UNSUBMIT){
				workflow.setProcessState(ProcessState.SUBMIT);
		}
		
		Map<String,String> parameterSetting=DefinitionXmlParse.getParameterSetting(workflow.getProcessDefinitionId());
		String formViewUrl = parameterSetting.get(DefinitionXmlParse.FORM_VIEW_URL);
		if(StringUtils.isEmpty(formViewUrl)){
			formViewUrl = PropUtils.getProp(CommonStrings.WORKFLOW_PARAMETER_URL, CommonStrings.FORM_VIEW_URL);
		}
		String parameterName = parameterSetting.get(DefinitionXmlParse.FORM_VIEW_URL_PARAMETER_NAME);
		if(StringUtils.isEmpty(parameterName)){
			parameterName = PropUtils.getProp(CommonStrings.WORKFLOW_PARAMETER_URL, CommonStrings.FORM_VIEW_PARAMTER_NAME);
		}
		if(StringUtils.isNotEmpty(formViewUrl)){
			String joinSign = StringUtils.contains(formViewUrl, "?") ? "&" : "?";
			String systemCode=ContextUtils.getSystemCode();
			WorkflowDefinition definition = workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
			if(definition!=null){
				BusinessSystem system=ApiFactory.getAcsService().getSystemById(definition.getSystemId());
				if(system!=null)systemCode=system.getCode();
			}
			formViewUrl = systemCode+formViewUrl + joinSign + parameterName + "=";
		}
		log.debug(LOG_CONTENT+"查看表单url"+LOG_FLAG + formViewUrl);
		workflow.setFormUrl(formViewUrl);
		String urgenUrl = parameterSetting.get(DefinitionXmlParse.URGEN_URL);
		if(StringUtils.isEmpty(urgenUrl)){
			urgenUrl = PropUtils.getProp(CommonStrings.WORKFLOW_PARAMETER_URL, CommonStrings.FORM_URGEN_URL);
		}
		String urgenParameterName = parameterSetting.get(DefinitionXmlParse.URGEN_URL_PARAMETER_NAME);
		if(StringUtils.isEmpty(urgenParameterName)){
			urgenParameterName = PropUtils.getProp(CommonStrings.WORKFLOW_PARAMETER_URL, CommonStrings.FORM_URGEN_PARAMTER_NAME);
		}
		if(StringUtils.isNotEmpty(urgenUrl)){
			String joinSign = StringUtils.contains(urgenUrl, "?") ? "&" : "?";
			String systemCode=ContextUtils.getSystemCode();
			WorkflowDefinition definition = workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
			if(definition!=null){
				BusinessSystem system=ApiFactory.getAcsService().getSystemById(definition.getSystemId());
				if(system!=null)systemCode=system.getCode();
			}
			urgenUrl = systemCode+urgenUrl + joinSign + urgenParameterName + "=";
		}
		log.debug(LOG_CONTENT+"应急处理url"+LOG_FLAG + urgenUrl);
		workflow.setEmergencyUrl(urgenUrl);
		workflowInstanceManager.saveWorkflowInstance(workflow);
		log.debug(LOG_CONTENT+"流程实例设置了值后workflow" +LOG_FLAG + workflow);
		log.debug(LOG_CONTENT+"提交流程返回结果" +LOG_FLAG + completeTaskTipType);
		log.debug(LOG_METHOD_END+"提交流程,WorkflowClientManager+submitWorkflowById(String workflowId,FormFlowable entity)"+LOG_FLAG);
		return completeTaskTipType;
	}

	public Map submitCustomInstance(String definitionCode,
			Integer definitionVersion) {
		Assert.notNull(definitionCode,"definitionCode不可为空");
		Assert.notNull(definitionVersion,"definitionVersion不可为空");
		Assert.notNull(ContextUtils.getCompanyId(),"公司id不可为空");
		
		WorkflowDefinition definition = getEnabledWorkflowDefinitionByCodeAndVersion(definitionCode,definitionVersion,ContextUtils.getCompanyId());
		Assert.notNull(definition,"definition不可为空");
		Map myresult=startMyCustomInstance(definition.getProcessId());
		String instanceId=(String)myresult.get("instanceId");
		
		com.norteksoft.wf.engine.entity.WorkflowInstance workflow = workflowInstanceManager.getWorkflowInstance(instanceId);
		CompleteTaskTipType result=submitCustomInstance(workflow);
		Map completeResult= new HashMap();
		completeResult.put("dataId",(Long)myresult.get("dataId"));
		completeResult.put("instanceId",instanceId);
		completeResult.put("result",result);
		return completeResult;
	}

	public Map submitCustomInstance(Long definitionId) {
		Assert.notNull(definitionId,"definitionId不可为空");
		
		WorkflowDefinition definition = this.getWorkflowDefinitionById(definitionId);
		Assert.notNull(definition,"definition不可为空");
		Map myresult=startMyCustomInstance(definition.getProcessId());
		String instanceId=(String)myresult.get("instanceId");
		
		com.norteksoft.wf.engine.entity.WorkflowInstance workflow = workflowInstanceManager.getWorkflowInstance(instanceId);
		CompleteTaskTipType result=submitCustomInstance(workflow);
		Map completeResult= new HashMap();
		completeResult.put("dataId",(Long)myresult.get("dataId"));
		completeResult.put("instanceId",instanceId);
		completeResult.put("result",result);
		return completeResult;
	}

	

	public List<com.norteksoft.product.api.entity.Opinion> getOpinions(
			String instanceId, String... taskNames) {
		Assert.notNull(instanceId,"参数instanceId不可为空");
		Assert.notNull(ContextUtils.getCompanyId(),"公司id不可为空");
		return getOpinions(workflowInstanceManager.getOpinions(instanceId, ContextUtils.getCompanyId(), taskNames));
	}

	public List<com.norteksoft.product.api.entity.Opinion> getOpinionsByCustomField(
			String instanceId, String customField) {
		Assert.notNull(instanceId,"参数instanceId不可为空");
		Assert.notNull(customField,"参数customField不可为空");
		return getOpinions(workflowInstanceManager.getOpinionsByCustomField(instanceId,customField));
	}

	public List<com.norteksoft.product.api.entity.Opinion> getOpinionsByTaskCode(
			String instanceId, String... taskCodes) {
		Assert.notNull(instanceId,"参数instanceId不可为空");
		Assert.notNull(ContextUtils.getCompanyId(),"公司id不可为空");
		return getOpinions(workflowInstanceManager.getOpinionsByTacheCode(instanceId,ContextUtils.getCompanyId(), taskCodes));
	}

	public List<com.norteksoft.product.api.entity.Opinion> getOpinionsExcludeCustomField(
			String instanceId, String customField) {
		Assert.notNull(instanceId,"参数instanceId不可为空");
		Assert.notNull(customField,"参数customField不可为空");
		return getOpinions(workflowInstanceManager.getOpinionsExceptCustomField(instanceId,customField));
	}

	public List<com.norteksoft.product.api.entity.Opinion> getOpinionsExcludeTaskMode(
			String instanceId, TaskProcessingMode taskMode) {
		Assert.notNull(instanceId,"参数instanceId不可为空");
		Assert.notNull(taskMode,"参数taskMode不可为空");
		return getOpinions(getOpinionsExceptTaskMode(instanceId,taskMode));
	}

	public List<com.norteksoft.product.api.entity.Opinion> getOpinionsExcludeTaskName(
			String instanceId, String... taskName) {
		Assert.notNull(instanceId,"参数instanceId不可为空");
		Assert.notNull(ContextUtils.getCompanyId(),"公司id不可为空");
		return getOpinions(workflowInstanceManager.getOpinionsExceptTaskName(instanceId, ContextUtils.getCompanyId(), taskName));
	}

	public List<com.norteksoft.product.api.entity.Opinion> getAllOpinions(
			String instanceId) {
		Assert.notNull(instanceId,"参数instanceId不可为空");
		Assert.notNull(ContextUtils.getCompanyId(),"公司id不可为空");
		return getOpinions(workflowInstanceManager.getOpinionsByInstanceId(instanceId,ContextUtils.getCompanyId()));
	}

	public List<com.norteksoft.product.api.entity.Opinion> getAllOpinions(
			String instanceId, TaskProcessingMode taskMode) {
		Assert.notNull(instanceId,"参数instanceId不可为空");
		Assert.notNull(taskMode,"参数taskMode不可为空");
		return getOpinions(this.getOpinions(instanceId,   taskMode));
	}

	public List<com.norteksoft.product.api.entity.Opinion> getAllOpinions(
			String instanceId, String taskName) {
		Assert.notNull(instanceId,"参数instanceId不可为空");
		Assert.notNull(taskName,"参数taskName不可为空");
		return getOpinions(getOpinions(instanceId,  taskName));
	}

	public List<WorkflowType> getApproveSystemWorkflowTypes() {
		return workflowTypeDao.getApproveSystemWorkflowTypes();
	}

	public List<String> getActivityTaskPrincipals(String workflowId) {
		Assert.notNull(workflowId,"workflowId不能为空 ");
		return taskService.getActivityTaskPrincipals(workflowId);
	}

	public List<String[]> getActivityTaskTransactors(String workflowId) {
		Assert.notNull(workflowId,"workflowId不能为空 ");
		return taskService.getActivityTaskTransactors(workflowId);
	}

	public List<com.norteksoft.product.api.entity.WorkflowDefinition> getWorkflowDefinitionsByFormCodeAndVersion(
			String formCode, Integer version) {
		List<WorkflowDefinition> list= workflowDefinitionManager.getAllEnableDefinitionsByformCodeAndVersion(formCode, version);
		List<com.norteksoft.product.api.entity.WorkflowDefinition> result=new ArrayList<com.norteksoft.product.api.entity.WorkflowDefinition>();
		for(WorkflowDefinition definition:list){
			result.add(getWorkflowDefinitionParameter(definition));
		}
		return result;
	}

	public Map getDataByTaskId(Long taskId) {
		WorkflowTask task = getTask(taskId);
		com.norteksoft.wf.engine.entity.WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		WorkflowDefinition def = workflowDefinitionManager.getWfDefinition(wi.getWorkflowDefinitionId());
		FormView formview = formManager.getCurrentFormViewByCodeAndVersion(def.getFormCode(), def.getFromVersion());
		return jdbcDao.getDataMap(formview.getDataTable().getName(), wi.getDataId());
	}

	public List<WorkflowTask> getActivityTasks(FormFlowable entity) {
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return BeanUtil.turnToModelTaskList(taskService.getActivityTasks(entity.getWorkflowInfo().getWorkflowId(), getCompanyId(entity)));
	}
	public List<WorkflowTask> getActivityTasks(String workflowId) {
		Assert.notNull(workflowId,"workflowId不能为空 ");
		return BeanUtil.turnToModelTaskList(taskService.getActivityTasks(workflowId, getCompanyId(null)));
	}

	public String abandonReceive(Long taskId) {
		return taskService.abandonReceive(taskId);
	}

	public List<com.norteksoft.product.api.entity.WorkflowDefinition> getWorkflowDefinitionsByName(
			String typeNo, String name) {
		WorkflowType type = workflowTypeManager.getWorkflowType(typeNo);
		if(type == null) throw new RuntimeException("没有查询到流程类型，流程编号["+typeNo+"]");
		List<WorkflowDefinition> wfds = workflowDefinitionManager.getWfDefinitionsByName(type.getCompanyId(), type.getId(), name);
		List<com.norteksoft.product.api.entity.WorkflowDefinition> result=new ArrayList<com.norteksoft.product.api.entity.WorkflowDefinition>();
		for(WorkflowDefinition definition:wfds){
			result.add(getWorkflowDefinitionParameter(definition));
		}
		return result;
	}
	
	public String returnTask( Long taskId) {
		return taskService.goBackTask(taskId);
		
	}

	public List<String[]> getActivityTaskPrincipalsDetail(FormFlowable entity) {
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getActivityTaskPrincipalsDetail(entity.getWorkflowInfo().getWorkflowId());
	}

	public List<String[]> getActivityTaskPrincipalsDetail(String workflowId) {
		Assert.notNull(workflowId,"workflowId不能为空 ");
		return taskService.getActivityTaskPrincipalsDetail(workflowId);
	}

	public List<String> getActivityTaskPrincipals(FormFlowable entity) {
		Assert.notNull(entity,"参数不可为空");
		Assert.notNull(entity.getWorkflowInfo(),"workflowInfo不能为空");
		Assert.notNull(entity.getWorkflowInfo().getWorkflowId(),"workflowInfo的workflowId不能为空 ");
		return getActivityTaskPrincipals(entity.getWorkflowInfo().getWorkflowId());
	}
}
