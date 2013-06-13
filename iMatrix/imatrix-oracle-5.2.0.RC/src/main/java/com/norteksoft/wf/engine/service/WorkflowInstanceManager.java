package com.norteksoft.wf.engine.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.norteksoft.mms.form.dao.GeneralDao;
import com.norteksoft.mms.form.entity.AutomaticallyFilledField;
import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.jdbc.JdbcSupport;
import com.norteksoft.mms.form.service.FormHtmlParser;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.product.api.FileService;
import com.norteksoft.product.api.WorkflowInstanceService;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.task.dao.WorkflowTaskDao;
import com.norteksoft.task.entity.TaskMark;
import com.norteksoft.task.entity.TaskSetting;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.base.enumeration.CommonStrings;
import com.norteksoft.wf.base.enumeration.CompleteTaskTipType;
import com.norteksoft.wf.base.enumeration.LogicOperator;
import com.norteksoft.wf.base.enumeration.NumberOperator;
import com.norteksoft.wf.base.enumeration.ProcessState;
import com.norteksoft.wf.base.enumeration.TaskTransactorCondition;
import com.norteksoft.wf.base.enumeration.TextOperator;
import com.norteksoft.wf.base.utils.WebUtil;
import com.norteksoft.wf.engine.client.FormFlowable;
import com.norteksoft.wf.engine.client.FormFlowableDeleteInterface;
import com.norteksoft.wf.engine.client.SingleTransactorSelector;
import com.norteksoft.wf.engine.core.DefinitionXmlParse;
import com.norteksoft.wf.engine.core.ExecutionVariableCommand;
import com.norteksoft.wf.engine.core.impl.UserParseCalculator;
import com.norteksoft.wf.engine.dao.OpinionDao;
import com.norteksoft.wf.engine.dao.WorkflowAttachmentDao;
import com.norteksoft.wf.engine.dao.WorkflowAttachmentFileDao;
import com.norteksoft.wf.engine.dao.WorkflowInstanceDao;
import com.norteksoft.wf.engine.entity.Document;
import com.norteksoft.wf.engine.entity.DocumentFile;
import com.norteksoft.wf.engine.entity.InstanceHistory;
import com.norteksoft.wf.engine.entity.Opinion;
import com.norteksoft.wf.engine.entity.TrustRecord;
import com.norteksoft.wf.engine.entity.WorkflowAttachment;
import com.norteksoft.wf.engine.entity.WorkflowAttachmentFile;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import org.apache.commons.lang.builder.CompareToBuilder;
@Service
@Transactional
public class WorkflowInstanceManager implements Comparable, SingleTransactorSelector{
	private Log log=LogFactory.getLog(WorkflowInstanceManager.class);
	
	public static final String FORMID = "formId";
	public static final String DATAID = "dataId";
	public static final String INSTANCEID = "instanceId";
	public static final String PROCESSID = "processId";
	public static final String INSTANCE_ID = "instance_id";
	public static final String RESULT = "result";
	public static final String TASKID = "taskId";
	
	private static final String SQL_OPERATOR_LESS_THAN = " < ";
	private static final String SQL_OPERATOR_GREATER_THAN = " > ";
	private static final String SQL_OPERATOR_EQUAL = " = ";
	private static final String SQL_PERCENT_SIGN = "%";
	private static final String SQL_SINGLE_QUOTATION_MARKS = "'";
	private static final String SQL_OPERATOR_LIKE = " like ";
	private static final String ASTERISK_REGEX = "\\*";
	private static final String PARENTHESES_REGEX = "[)(]";
	private static final String EMPTY_STRING = "";
	private static final String SQL_OPERATOR_AND = " and ";
	private static final String SQL_OPERATOR_OR = " or ";
	
	public static final String DATABASE_ORACLE="oracle";
	public static final String DATABASE_MYSQL="mysql";
	public static final String DATABASE_SQLSERVER="sqlserver";
	
	
	private WorkflowInstanceDao workflowInstanceDao;
	private ProcessEngine processEngine;
	private FormViewManager formViewManager;
	private WorkflowAttachmentDao workflowAttachmentDao;
	private WorkflowAttachmentFileDao workflowAttachmentFileDao;
	private OfficeManager officeManager;
	private OpinionDao opinionDao;
	private JdbcSupport jdbcDao;
	private GeneralDao generalDao;
	private FormHtmlParser formHtmlParser;
	private WorkflowDefinitionManager workflowDefinitionManager;
	private InstanceHistoryManager instanceHistoryManager;
	private WorkflowTaskDao workflowTaskDao;
	@Autowired
	public void setFormHtmlParser(FormHtmlParser formHtmlParser) {
		this.formHtmlParser = formHtmlParser;
	}
	@Autowired
	public void setInstanceHistoryManager(
			InstanceHistoryManager instanceHistoryManager) {
		this.instanceHistoryManager = instanceHistoryManager;
	}
	@Autowired
	public void setJdbcDao(JdbcSupport jdbcDao) {
		this.jdbcDao = jdbcDao;
	}

	@Autowired
	public void setFormViewManager(FormViewManager formManager) {
		this.formViewManager = formManager;
	}

	@Autowired
	public void setWorkflowDefinitionManager(
			WorkflowDefinitionManager workflowDefinitionManager) {
		this.workflowDefinitionManager = workflowDefinitionManager;
	}

	@Autowired
	public void setWorkflowInstanceDao(WorkflowInstanceDao workflowInstanceDao) {
		this.workflowInstanceDao = workflowInstanceDao;
	}
	
	@Autowired
	public void setProcessEngine(ProcessEngine processEngine) {
		this.processEngine = processEngine;
	}
	
	@Autowired
	public void setOfficeManager(OfficeManager officeManager) {
		this.officeManager = officeManager;
	}
	@Autowired
	public void setOpinionDao(OpinionDao opinionDao) {
		this.opinionDao = opinionDao;
	}
	@Autowired
	public void setGeneralDao(GeneralDao generalDao) {
		this.generalDao = generalDao;
	}
	@Autowired
	public void setWorkflowAttachmentDao(WorkflowAttachmentDao workflowAttachmentDao) {
		this.workflowAttachmentDao = workflowAttachmentDao;
	}
	@Autowired
	public void setWorkflowTaskDao(WorkflowTaskDao workflowTaskDao) {
		this.workflowTaskDao = workflowTaskDao;
	}
	
	private Long getCompanyId(){
		return ContextUtils.getCompanyId();
	}
	
	private Long getSystemId(){
		return ContextUtils.getSystemId();
	}
	
	private String getLoginName(){
		return ContextUtils.getLoginName();
	}
	
	
	/**
	 * 得到填充值的html,自定义表单使用
	 * 
	 * @param formId
	 * @param dataId
	 * @return
	 */
	public String getHtml(WorkflowInstance wi,WorkflowTask task){
		FormView form = formViewManager.getFormView(wi.getFormId());
		log.debug("The form what finded with formId  is " + form);
		String html=formViewManager.setDefaultVal(form, form.getHtml());
		html= setValueForHtml(wi,form,html);
		if(task!=null){
			html = initHtml(form,task.getName(),wi.getProcessDefinitionId(),html);
		}
		return html;
	}
	 public String initHtml(FormView form,String taskName,String wfDefinitionId,String html){
 		List<AutomaticallyFilledField> filledField = DefinitionXmlParse.getBeforeFilledFields(wfDefinitionId,taskName);
 		return formViewManager.initHtml(form,filledField, html);
	 }
	
	@SuppressWarnings("unchecked")
	public String setValueForHtml(WorkflowInstance wi,FormView form,String html){
		Map map=jdbcDao.getDataMap(form.getDataTable().getName(), wi.getDataId());
		log.debug("The dataMap is " + map);
		formHtmlParser.setFormHtml(html);
		String result = formHtmlParser.setFieldValue(map);
		return result;
	}
	/**
	 * 
	 * @param processName
	 * @param entity
	 * @param task
	 * @return
	 */
	
	public String getFirstTaskName(String processId){
		return DefinitionXmlParse.getFirstTaskName(processId);
	}
	
	 /**
     *  根据流程实例ID查询流程定义文件（flex用）
     */
    public String getXmlByInstanceId(String instanceId, Long companyId){
    	WorkflowInstance wi = workflowInstanceDao.getInstanceByJbpmInstanceId(instanceId, companyId);
    	return workflowDefinitionManager.getXmlByDefinitionId(wi.getWorkflowDefinitionId(), companyId);
    }
	
	/**
	 * 保存实体数据
	 * @param entity
	 */
    @Transactional(readOnly=false)
	public void saveFormFlowable(FormFlowable entity){
		generalDao.save(entity);
	}
	
	public Object getFormFlowable(String className,Long dataId){
		return generalDao.getObject(className, dataId);
	}
	
	@SuppressWarnings("unchecked")
	public List searchWorkflow(Long workflowDefinitionId,String searchCdn){
		//数据库方言
		boolean isOracle=PropUtils.isOracle();
		if(searchCdn==null || searchCdn.length() < 4) return null;
		WorkflowDefinition wd = workflowDefinitionManager.getWfDefinition(workflowDefinitionId);
		FormView form = formViewManager.getCurrentFormViewByCodeAndVersion(wd.getFormCode(), wd.getFromVersion());
		List<FormControl> fields = formViewManager.getControls(form.getId());
		List<FormControl> displayFields = new ArrayList<FormControl>();
		displayFields = fields;
		String tableName = form.getDataTable().getName();
		String tempStr = searchCdn;
		tempStr = tempStr.replaceAll(LogicOperator.AND.getCode(), ASTERISK_REGEX);
		tempStr = tempStr.replaceAll(LogicOperator.OR.getCode(), ASTERISK_REGEX);
		tempStr = tempStr.replaceAll(PARENTHESES_REGEX, EMPTY_STRING);
		String[] strs = tempStr.split(ASTERISK_REGEX);
		StringBuilder sqlCdn = new StringBuilder(); 
		sqlCdn.append("select wi.form_Name,wi.creator,wi.start_Time,wi.current_activity,wi.process_State");
		for(FormControl displayField:displayFields){
			if(DataType.DATE==displayField.getDataType()){
				sqlCdn.append(",to_char(t.").append(JdbcSupport.FORM_FIELD_PREFIX_STRING).append(displayField.getName()).append(",'yyyy-MM-dd')");
			}else if(DataType.TIME==displayField.getDataType()){
				sqlCdn.append(",to_char(t.").append(JdbcSupport.FORM_FIELD_PREFIX_STRING).append(displayField.getName()).append(",'yyyy-MM-dd HH24:mi')");
			}else{
				sqlCdn.append(",t.").append(JdbcSupport.FORM_FIELD_PREFIX_STRING).append(displayField.getName());
			}
			
		}
		
		sqlCdn.append(" from WF_INSTANCE wi , ");
		
		sqlCdn.append(tableName).append(" t where wi.data_Id=t.id and wi.company_Id = ").append(this.getCompanyId()).append(" and wi.workflow_Definition_Id= ").append(workflowDefinitionId).append(" and ");
		for(int i=0;i<strs.length;i++){
			 if(strs[i].contains(TextOperator.CONTAINS.getCode())){
				String[] tempArray = strs[i].split(TextOperator.CONTAINS.getCode());
				searchCdn = searchCdn.replaceAll(strs[i].trim(), " t."+ tempArray[0].trim()+ SQL_OPERATOR_LIKE +SQL_SINGLE_QUOTATION_MARKS+SQL_PERCENT_SIGN +tempArray[1].trim()+SQL_PERCENT_SIGN +SQL_SINGLE_QUOTATION_MARKS );
			 }else if(strs[i].contains(TextOperator.ET.getCode())){
				 String[] tempArray = strs[i].split(TextOperator.ET.getCode());
				searchCdn = searchCdn.replaceAll(strs[i].trim(), " t."+  tempArray[0].trim() + SQL_OPERATOR_EQUAL +SQL_SINGLE_QUOTATION_MARKS+tempArray[1].trim()+SQL_SINGLE_QUOTATION_MARKS);
			 }else if(strs[i].contains("operator.date.et")){
				 String[] tempArray = strs[i].split("operator.date.et");//to_date('2010-09-17','yyyy-mm-dd')
				 if(isOracle){
					 searchCdn = searchCdn.replaceAll(strs[i].trim(), " t."+  tempArray[0].trim() + SQL_OPERATOR_EQUAL +"to_date('"+tempArray[1].trim()+"','yyyy-MM-dd')");
				 }else{
					 searchCdn = searchCdn.replaceAll(strs[i].trim(), " t."+  tempArray[0].trim() + SQL_OPERATOR_EQUAL +"'"+tempArray[1].trim()+"'");
				 }
			}else if(strs[i].contains("operator.date.gt")){
				 String[] tempArray = strs[i].split("operator.date.gt");
				 if(isOracle){
					 searchCdn = searchCdn.replaceAll(strs[i].trim(), " t."+  tempArray[0].trim() + SQL_OPERATOR_GREATER_THAN +"to_date('"+tempArray[1].trim()+"','yyyy-MM-dd')");
				 }else{
					 searchCdn = searchCdn.replaceAll(strs[i].trim(), " t."+  tempArray[0].trim() + SQL_OPERATOR_GREATER_THAN +"'"+tempArray[1].trim()+"'");
				 }
			}else if(strs[i].contains("operator.date.lt")){
				 String[] tempArray = strs[i].split("operator.date.lt");
				 if(isOracle){
					 searchCdn = searchCdn.replaceAll(strs[i].trim(), " t."+  tempArray[0].trim() + SQL_OPERATOR_LESS_THAN +"to_date('"+tempArray[1].trim()+"','yyyy-MM-dd')");
				 }else{
					 searchCdn = searchCdn.replaceAll(strs[i].trim(), " t."+  tempArray[0].trim() + SQL_OPERATOR_LESS_THAN +"'"+tempArray[1].trim()+"'");
					 
				 }
			}else if(strs[i].contains("operator.time.et")){
				 String[] tempArray = strs[i].split("operator.time.et");
				 if(isOracle){
					 searchCdn = searchCdn.replaceAll(strs[i].trim(), " t."+  tempArray[0].trim() + SQL_OPERATOR_EQUAL +"to_date('"+tempArray[1].trim()+"','yyyy-MM-dd hh24:mi')");
				 }else{
					 searchCdn = searchCdn.replaceAll(strs[i].trim(), " t."+  tempArray[0].trim() + SQL_OPERATOR_EQUAL +"'"+tempArray[1].trim()+"'");
				 }
			}else if(strs[i].contains("operator.time.gt")){
				 String[] tempArray = strs[i].split("operator.time.gt");
				 if(isOracle){
					 searchCdn = searchCdn.replaceAll(strs[i].trim(), " t."+  tempArray[0].trim() + SQL_OPERATOR_GREATER_THAN +"to_date('"+tempArray[1].trim()+"','yyyy-MM-dd hh24:mi')");
				 }else{
					 searchCdn = searchCdn.replaceAll(strs[i].trim(), " t."+  tempArray[0].trim() + SQL_OPERATOR_GREATER_THAN +"'"+tempArray[1].trim()+"'");
				 }
			}else if(strs[i].contains("operator.time.lt")){
				 String[] tempArray = strs[i].split("operator.time.lt");
				 if(isOracle){
					 searchCdn = searchCdn.replaceAll(strs[i].trim(), " t."+  tempArray[0].trim() + SQL_OPERATOR_LESS_THAN +"to_date('"+tempArray[1].trim()+"','yyyy-MM-dd hh24:mi')");
				 }else{
					 searchCdn = searchCdn.replaceAll(strs[i].trim(), " t."+  tempArray[0].trim() + SQL_OPERATOR_LESS_THAN +"'"+tempArray[1].trim()+"'");
				 }
			}else if(strs[i].contains(NumberOperator.GT.getCode())){
				 String[] tempArray = strs[i].split(NumberOperator.GT.getCode());
				searchCdn = searchCdn.replaceAll(strs[i].trim(), " t."+ tempArray[0].trim()+ SQL_OPERATOR_GREATER_THAN +tempArray[1]);
			 }else if(strs[i].contains(NumberOperator.LT.getCode())){
				 String[] tempArray = strs[i].split(NumberOperator.LT.getCode());
				searchCdn = searchCdn.replaceAll(strs[i].trim(), " t."+ tempArray[0].trim()+ SQL_OPERATOR_LESS_THAN +tempArray[1]);
			 }else if(strs[i].contains(NumberOperator.ET.getCode())){
				 String[] tempArray = strs[i].split(NumberOperator.ET.getCode());
					searchCdn = searchCdn.replaceAll(strs[i].trim(), " t."+ tempArray[0].trim()+ SQL_OPERATOR_EQUAL +tempArray[1]);
			}
		}
		
		searchCdn = searchCdn.replaceAll(LogicOperator.AND.getCode(), SQL_OPERATOR_AND);
		searchCdn = searchCdn.replaceAll(LogicOperator.OR.getCode(), SQL_OPERATOR_OR);
		sqlCdn.append(searchCdn);
		return jdbcDao.excutionSql(sqlCdn.toString());
	}

	/**
	 * 分页查询所有提交的流程实例
	 */
	public void getAllWorkflowInstances(Page<WorkflowInstance> page,Long workflowDefinitionId){
		workflowInstanceDao.getAllWorkflowInstances(page,workflowDefinitionId,getCompanyId(),getSystemId());
	}
	
	/**
	 * 查询所有提交的流程实例
	 */
	public List<WorkflowInstance> getAllWorkflowInstances(Long workflowDefinitionId,Long systemId){
		return workflowInstanceDao.getAllWorkflowInstances(workflowDefinitionId,getCompanyId(),systemId);
	}
	
	/**
	 * 分页查询所有流程实例
	 */
	public List<WorkflowInstance> getAllEndWorkflowInstances(Long workflowDefinitionId,Long systemId){
		return workflowInstanceDao.getAllEndWorkflowInstances(workflowDefinitionId,getCompanyId(),systemId);
	}
	
	/**
	 * 保存流程实例
	 */
	@Transactional(readOnly=false)
	public void saveWorkflowInstance(WorkflowInstance workflowInstance){
		workflowInstanceDao.save(workflowInstance);
	}
	
	/**
	 * 保存表单数据
	 * @param workflowInstanceId
	 */
	@Transactional(readOnly=false)
	public void saveFormData(Long taskId){
		WorkflowTask task  = taskService.getTask(taskId);
		WorkflowInstance workflowInstance = saveData(task,null);
		saveWorkflowInstance(workflowInstance);
	}
	
	@Transactional(readOnly=false)
	@SuppressWarnings("unchecked")
	public WorkflowInstance saveData(WorkflowTask task,Map<String,String[]> map){
		WorkflowInstance workflowInstance = getWorkflowInstance(task.getProcessInstanceId());
		Map<String, String[]> parameterMap =  Struts2Utils.getRequest().getParameterMap();
		if(map==null) map = new HashMap<String,String[]>();
		map.putAll(parameterMap);
		if(workflowInstance.getDataId()==null){
			Long dataId = formViewManager.saveFormContentToTable(map,workflowInstance.getFormId(),null);
			workflowInstance.setDataId(dataId);
		}else{
			formViewManager.saveFormContentToTable(map,workflowInstance.getFormId(),workflowInstance.getDataId());
			//设置“流程紧急状态”
			String[] priority=map.get("priority");
			if(priority!=null){
				String value =map.get("priority")[0];
				workflowInstance.setPriority(Integer.valueOf(value));
				List<WorkflowTask> tasks=workflowTaskDao.getAllTasksByInstance(workflowInstance.getCompanyId(), workflowInstance.getProcessInstanceId());
				for(WorkflowTask wfTask:tasks){
					wfTask.setTaskMark(TaskMark.valueOf(workflowInstance.getPriority()));
				}
			}
		}
		return workflowInstance;
	}
	
	@Transactional(readOnly=false)
	@SuppressWarnings("unchecked")
	public Long savaData(){
		Map<String, String[]> parameterMap =  Struts2Utils.getRequest().getParameterMap();
		Long formId = null;
		String[] formIds = parameterMap.get("formId");
		if(formIds!=null&&formIds.length>0) formId = Long.valueOf(formIds[0]);
		Long dataId = null;
		String[] dataIds = parameterMap.get("dataId");
		if(dataIds!=null&&dataIds.length>0&&StringUtils.isNotEmpty(dataIds[0])) formId = Long.valueOf(dataIds[0]);
		return formViewManager.saveFormContentToTable(parameterMap,formId,dataId);
	}

	/**
	 * 负责保存数据，启动自由流
	 * @param parameterMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=false)
	public Map<String,String> saveCustomProcess(Map<String, String[]> parameterMap){
		boolean start = false;
		WorkflowInstance instance;
		Map<String ,String[]> value_map = new HashMap<String ,String[]>();
		Map<String,String> resultMap = new HashMap<String,String>();
		String workflowId = null;
		Long formId = null;
		String[] formIds = parameterMap.get(FORMID);
		if(formIds!=null&&formIds.length>0) formId = Long.valueOf(formIds[0]);
		Long dataId = null;
		String[] dataIds = parameterMap.get(DATAID);
		if(dataIds!=null&&dataIds.length>0&&StringUtils.isNotEmpty(dataIds[0])) dataId = Long.valueOf(dataIds[0]);
		if(dataId==null){
			String processId = null;
			String[] processIds = parameterMap.get(PROCESSID);
			if(processIds!=null&&processIds.length>0&&StringUtils.isNotEmpty(processIds[0])) processId = processIds[0];
			Integer priority = 6;
			String[] prioritys = parameterMap.get(CommonStrings.PRIORITY);
			if(prioritys!=null&&prioritys.length>0&&StringUtils.isNotEmpty(prioritys[0])) priority = Integer.valueOf(prioritys[0]);
			instance = startCustomProcessWorkflowInstance(processId,priority);//启动流程
			workflowId = instance.getProcessInstanceId();
			start = true;
			value_map.put(INSTANCE_ID, new String[]{workflowId});
		}else{
			FormView form = formViewManager.getFormView(formId);
			Map map = formViewManager.getDataMap(form.getDataTable().getName(), dataId);
			workflowId = map.get(INSTANCE_ID).toString();
			instance = getWorkflowInstance(workflowId);
		}
		value_map.putAll(parameterMap);
		dataId = formViewManager.saveFormContentToTable(value_map,formId,dataId);
		resultMap.put(DATAID, dataId.toString());
		resultMap.put(INSTANCEID, workflowId);
		if(start){
			instance.setDataId(dataId);
			instance.setProcessState(ProcessState.UNSUBMIT);
		}
		this.saveWorkflowInstance(instance);
		return resultMap;
	}
	
	/**
	 * 结束自由流
	 */
	@Transactional(readOnly=false)
	public void completeCustomProcess(Long taskId,TaskProcessingResult operation){
		WorkflowTask task = taskService.getTask(taskId);
		taskService.completeCustomProcess(task,  operation, TaskSetting.getTaskSettingInstance());
		WorkflowInstance instance = this.getWorkflowInstance(task.getProcessInstanceId());
		instance.setProcessState(ProcessState.END);
		instance.setEndTime(new Timestamp(System.currentTimeMillis()));
		this.saveWorkflowInstance(instance);
		
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=false)
	public Map<String,String> submitCustomProcess(Map<String, String[]> parameterMap){
		Map<String ,String[]> value_map = new HashMap<String ,String[]>();
		String workflowId = null;
		WorkflowInstance instance;
		boolean start = false;
		Map<String,String> resultMap = new HashMap<String,String>();
		Long formId = null;
		String[] formIds = parameterMap.get(FORMID);
		if(formIds!=null&&formIds.length>0) formId = Long.valueOf(formIds[0]);
		Long dataId = null;
		String[] dataIds = parameterMap.get(DATAID);
		if(dataIds!=null&&dataIds.length>0&&StringUtils.isNotEmpty(dataIds[0])) dataId = Long.valueOf(dataIds[0]);
		if(dataId==null){
			String processId = null;
			String[] processIds = parameterMap.get(PROCESSID);
			if(processIds!=null&&processIds.length>0&&StringUtils.isNotEmpty(processIds[0])) processId = processIds[0];
			Integer priority = 6;
			String[] prioritys = parameterMap.get(CommonStrings.PRIORITY);
			if(prioritys!=null&&prioritys.length>0&&StringUtils.isNotEmpty(prioritys[0])) priority = Integer.valueOf(prioritys[0]);
			instance = this.startCustomProcessWorkflowInstance(processId, priority);
			workflowId = instance.getProcessInstanceId();
			start = true;
			value_map.put(INSTANCE_ID, new String[]{workflowId});
		}else{
			FormView form = formViewManager.getFormView(formId);
			Map map = formViewManager.getDataMap(form.getDataTable().getName(), dataId);
			workflowId = map.get(INSTANCE_ID).toString();
			instance = this.getWorkflowInstance(workflowId);
		}
		value_map.putAll(parameterMap);
		dataId = formViewManager.saveFormContentToTable(value_map,formId,dataId);
		resultMap.put(DATAID, dataId.toString());
		resultMap.put(INSTANCEID, workflowId);
		Long taskId = null;
		String[] taskIds = parameterMap.get(TASKID);
		if(taskIds!=null&&StringUtils.isNotEmpty(taskIds[0])&&taskIds.length>0) taskId = Long.valueOf(taskIds[0]);
		taskId = taskId==null ? instance.getFirstTaskId():taskId;
		
		if(start){
			instance.setDataId(dataId);
		}
		
		resultMap.put(TASKID, taskId.toString());
		CompleteTaskTipType result =  taskService.completeCustomProcess(taskId, TaskProcessingResult.SUBMIT,TaskSetting.getTaskSettingInstance());
		
		if(result==CompleteTaskTipType.OK||result==CompleteTaskTipType.RETURN_URL){
			if(instance.getProcessState()==ProcessState.UNSUBMIT){
				instance.setProcessState(ProcessState.SUBMIT);
				instance.setSubmitTime(new Timestamp(System.currentTimeMillis()));
			}
		}
		this.saveWorkflowInstance(instance);
		
		resultMap.put(RESULT, result.getContent());
		return resultMap;
	}
	
	/**
	 * 保存数据，启动流程
	 * @return
	 */
	@Transactional(readOnly=false)
	@SuppressWarnings("unchecked")
	public Map<String ,String> save(Map<String, String[]> parameterMap){
		boolean start = false;
		Map<String ,String[]> value_map = new HashMap<String ,String[]>();
		String instanceId = null;
		Map<String,String> resultMap = new HashMap<String,String>();
		Long formId = null;
		String[] formIds = parameterMap.get(FORMID);
		if(formIds!=null&&formIds.length>0) formId = Long.valueOf(formIds[0]);
		Long dataId = null;
		String[] dataIds = parameterMap.get(DATAID);
		if(dataIds!=null&&dataIds.length>0&&StringUtils.isNotEmpty(dataIds[0])) dataId = Long.valueOf(dataIds[0]);
		if(dataId==null){
			String processId = null;
			String[] processIds = parameterMap.get(PROCESSID);
			if(processIds!=null&&processIds.length>0&&StringUtils.isNotEmpty(processIds[0])) processId = processIds[0];
			Integer priority = 6;
			String[] prioritys = parameterMap.get(CommonStrings.PRIORITY);
			if(prioritys!=null&&prioritys.length>0&&StringUtils.isNotEmpty(prioritys[0])) priority = Integer.valueOf(prioritys[0]);
			instanceId = startWorkflowInstance(processId,priority,null);
			start = true;
			value_map.put(INSTANCE_ID, new String[]{instanceId});
		}else{
			FormView form = formViewManager.getFormView(formId);
			Map map = formViewManager.getDataMap(form.getDataTable().getName(), dataId);
			instanceId = map.get(INSTANCE_ID).toString();
		}
		value_map.putAll(parameterMap);
		dataId = formViewManager.saveFormContentToTable(value_map,formId,dataId);
		resultMap.put(DATAID, dataId.toString());
		resultMap.put(INSTANCEID, instanceId);
		WorkflowInstance wi = this.getWorkflowInstance(instanceId);
		if(start){
			wi.setDataId(dataId);
			wi.setProcessState(ProcessState.UNSUBMIT);
		}
		WorkflowTask task = taskService.getFirstTask(instanceId, ContextUtils.getLoginName());
		this.saveWorkflowInstance(wi);
		resultMap.put(TASKID, task.getId().toString());
		return resultMap;
	}
	
	@Transactional(readOnly=false)
	public Map<String,Object> submit(Map<String, String[]> parameterMap,String url){
		return submit(parameterMap,url,null,null);
	}
	
	/**
	 * 启动流程，完成第一个任务
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=false)
	public Map<String,Object> submit(Map<String, String[]> parameterMap,String url,String transitionName,String assignmentTransactors){
		Map<String ,String[]> value_map = new HashMap<String ,String[]>();
		String instanceId = null;
		boolean start = false;
		Map<String,Object> resultMap = new HashMap<String,Object>();
		Long formId = null;
		String[] formIds = parameterMap.get(FORMID);
		if(formIds!=null&&formIds.length>0) formId = Long.valueOf(formIds[0]);
		Long dataId = null;
		String[] dataIds = parameterMap.get(DATAID);
		if(dataIds!=null&&dataIds.length>0&&StringUtils.isNotEmpty(dataIds[0])) dataId = Long.valueOf(dataIds[0]);
		String processId = null;
		if(dataId==null){
			String[] processIds = parameterMap.get(PROCESSID);
			if(processIds!=null&&processIds.length>0&&StringUtils.isNotEmpty(processIds[0])) processId = processIds[0];
			Integer priority = 6;
			String[] prioritys = parameterMap.get(CommonStrings.PRIORITY);
			if(prioritys!=null&&prioritys.length>0&&StringUtils.isNotEmpty(prioritys[0])) priority = Integer.valueOf(prioritys[0]);
			instanceId = startWorkflowInstance(processId,priority,null);
			start = true;
			value_map.put(INSTANCE_ID, new String[]{instanceId});
		}else{
			FormView form = formViewManager.getFormView(formId);
			Map map = formViewManager.getDataMap(form.getDataTable().getName(), dataId);
			instanceId = map.get(INSTANCE_ID).toString();
		}
		value_map.putAll(parameterMap);
		dataId = formViewManager.saveFormContentToTable(value_map,formId,dataId);
		resultMap.put(DATAID, dataId.toString());
		resultMap.put(INSTANCEID, instanceId);
		Long taskId = null;
		String[] taskIds = parameterMap.get(TASKID);
		if(taskIds!=null&&StringUtils.isNotEmpty(taskIds[0])&&taskIds.length>0) taskId = Long.valueOf(taskIds[0]);
		WorkflowTask firstTask=taskService.getFirstTask(resultMap.get(WorkflowInstanceManager.INSTANCEID).toString(),getLoginName());
		taskId = taskId==null ? firstTask.getId():taskId;
		
		WorkflowInstance instance = this.getWorkflowInstance(instanceId);
		processId = instance.getProcessDefinitionId();
		WorkflowDefinition wd = workflowDefinitionManager.getWorkflowDefinitionByProcessId(instance.getProcessDefinitionId());
		Map<String,String> urlMap = new HashMap<String,String>();
		if(StringUtils.isNotEmpty(wd.getCode())){
			urlMap.put(wd.getCode(), url);
		}else{
			urlMap.put(wd.getName(), url);
		}
		taskService.executionVariableCommand(new ExecutionVariableCommand(instanceId, getProcessUrl(urlMap)));
		
		if(start){
			instance.setDataId(dataId);
		}
		
		resultMap.put(TASKID, taskId.toString());
		CompleteTaskTipType result =  taskService.completeWorkflowTask(taskId, TaskProcessingResult.SUBMIT,TaskSetting.getTaskSettingInstance().setTransitionName(transitionName).setAssignmentTransactors(assignmentTransactors));
		
		if(result==CompleteTaskTipType.OK){
			if(instance.getProcessState()==ProcessState.UNSUBMIT){
				instance.setProcessState(ProcessState.SUBMIT);
				instance.setSubmitTime(new Timestamp(System.currentTimeMillis()));
				Map<String,String > reminderSetting = DefinitionXmlParse.getReminderSetting(processId);
				instance.setReminderStyle(reminderSetting.get(DefinitionXmlParse.REMIND_STYLE));
				if(reminderSetting.get(DefinitionXmlParse.REMIND_REPEAT)!=null)instance.setRepeat(Long.valueOf(reminderSetting.get(DefinitionXmlParse.REMIND_REPEAT)));
				if(reminderSetting.get(DefinitionXmlParse.REMIND_DUEDATE)!=null)instance.setDuedate(Long.valueOf(reminderSetting.get(DefinitionXmlParse.REMIND_DUEDATE)));
				if(reminderSetting.get(DefinitionXmlParse.REMIND_TIME)!=null)instance.setReminderLimitTimes(Integer.valueOf(reminderSetting.get(DefinitionXmlParse.REMIND_TIME)));
				if(reminderSetting.get(DefinitionXmlParse.REMIND_NOTICE_TYPE)!=null)instance.setReminderNoticeStyle(reminderSetting.get(DefinitionXmlParse.REMIND_NOTICE_TYPE));
				if(reminderSetting.get(DefinitionXmlParse.REMIND_NOTICE_USER_CONDITION)!=null)instance.setReminderNoticeUserCondition(reminderSetting.get(DefinitionXmlParse.REMIND_NOTICE_USER_CONDITION));
			}
		}
		this.saveWorkflowInstance(instance);
		firstTask.setVisible(true);
		taskService.saveTask(firstTask);
		resultMap.put(RESULT, result);
		return resultMap;
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
			wfd = workflowDefinitionManager.getEnabledHighestVersionWorkflowDefinition(wfdName);
			map.put(CommonStrings.TASK_URL_PREFIX + wfd.getProcessId(), urlMap.get(wfdName));
		}
		return map;
	}
	
	/**
	 * 提交表单
	 */
	@Transactional(readOnly=false)
	public CompleteTaskTipType submitForm(WorkflowTask task){
		WorkflowInstance workflowInstance = saveData(task,null);
		saveWorkflowInstance(workflowInstance);
		return taskService.completeWorkflowTask(task, TaskProcessingResult.SUBMIT);
	} 
	
	/**
	 * 根据ID查询对象
	 * @param id
	 * @return
	 */
	public WorkflowInstance getWorkflowInstance(Long id){
		return workflowInstanceDao.get(id);
	}
	

	@Transactional(readOnly=false)
	public String deleteWorkflowInstances(Set<WorkflowInstance> workflowInstances ){
		StringBuilder result = new StringBuilder();
		int  failed = 0;
		for(WorkflowInstance workflow : workflowInstances){
			//判断当前实例是否是子流程实例
			if(StringUtils.isNotEmpty(workflow.getParentProcessId())){
				WorkflowInstance parentIns =  getWorkflowInstance(workflow.getParentProcessId());
				if(parentIns!=null){
					if((workflow.getProcessState()==ProcessState.SUBMIT||workflow.getProcessState()==ProcessState.UNSUBMIT) &&
							parentIns!=null && parentIns.getProcessState()==ProcessState.SUBMIT){
						failed++;
						continue;
					}
				}
				
			}
			//当前实例不是子流程实例，可以删除
			deleteWorkflowInstance(workflow,true);
		}
		result.append("删除成功"+(workflowInstances.size()-failed)+"个，失败"+failed+"个，失败是由于某些子流程实例无法删除，请删除相应的主流程实例来实现该操作。");
		return result.toString();
	}
	
	/**
	 * 删除流程实例
	 * @param id
	 */
	@Transactional(readOnly=false)
	public String deleteWorkflowInstance(Long id){
		WorkflowInstance wi =  getWorkflowInstance(id);
		boolean isSubprocess = false;
		if(StringUtils.isNotEmpty(wi.getParentProcessId())){
			WorkflowInstance parentIns =  getWorkflowInstance(wi.getParentProcessId());
			if((wi.getProcessState()==ProcessState.SUBMIT||wi.getProcessState()==ProcessState.UNSUBMIT) &&
				 parentIns!=null && parentIns.getProcessState()==ProcessState.SUBMIT){
				isSubprocess=true;
			}
		}
		if(isSubprocess){
			return "当前实例为子流程实例，不能删除。";
		}else{
			deleteWorkflowInstance(wi,false);
			return "删除成功";
		}
	}
	/**
	 * 删除实例
	 * @param wi
	 * @param isWf 是否是在wf项目中删除实例
	 */
	@Transactional(readOnly=false)
	public void deleteWorkflowInstance(WorkflowInstance wi,boolean isWf){	
		if(wi==null){log.debug("deleteWorkflowInstance中，流程实例不能为null");throw new RuntimeException("deleteWorkflowInstance中，流程实例不能为null");}
		log.debug(PropUtils.LOG_METHOD_BEGIN+"WorkflowInstanceManager+deleteWorkflowInstance(WorkflowInstance wi,boolean isWf)"+PropUtils.LOG_FLAG);
		log.debug(PropUtils.LOG_CONTENT+"companyId"+PropUtils.LOG_FLAG+getCompanyId());
		log.debug(PropUtils.LOG_CONTENT+"删除JBPM实例开始"+PropUtils.LOG_FLAG);
		if(wi.getProcessState()!=ProcessState.MANUAL_END&&wi.getProcessState()!=ProcessState.END){
			ProcessInstance jbpmInstance = processEngine.getExecutionService().findProcessInstanceById(wi.getProcessInstanceId());
			if(jbpmInstance!=null){
				processEngine.getExecutionService().deleteProcessInstance(wi.getProcessInstanceId());//删除JBPM流程实例
			}
		}
		log.debug(PropUtils.LOG_CONTENT+"删除JBPM实例结束"+PropUtils.LOG_FLAG);
		log.debug(PropUtils.LOG_CONTENT+"删除所有子流程开始"+PropUtils.LOG_FLAG);
		//删除所有子流程
		List<WorkflowInstance> subWorkflowInstances = getSubWorkflowInstances(wi.getProcessInstanceId(),wi.getSystemId());
		for(WorkflowInstance subWi : subWorkflowInstances){
			deleteWorkflowInstance(subWi,isWf);
		}
		log.debug(PropUtils.LOG_CONTENT+"删除所有子流程结束"+PropUtils.LOG_FLAG);
		log.debug(PropUtils.LOG_CONTENT+"删除任务开始"+PropUtils.LOG_FLAG);
		taskService.deleteTaskByProcessId(wi.getProcessInstanceId(),getCompanyId());//删除任务
		log.debug(PropUtils.LOG_CONTENT+"删除任务结束"+PropUtils.LOG_FLAG);
		log.debug(PropUtils.LOG_CONTENT+"删除意见开始"+PropUtils.LOG_FLAG);
		opinionDao.deleteAllOpinionsByWorkflowInstanceId(wi.getProcessInstanceId(),getCompanyId());//删除意见
		log.debug(PropUtils.LOG_CONTENT+"删除意见结束"+PropUtils.LOG_FLAG);
		log.debug(PropUtils.LOG_CONTENT+"删除正文开始"+PropUtils.LOG_FLAG);
		officeManager.deleteAllOfficesByWorkflowInstanceId(wi.getProcessInstanceId(),getCompanyId());//删除正文
		log.debug(PropUtils.LOG_CONTENT+"删除正文结束"+PropUtils.LOG_FLAG);
		log.debug(PropUtils.LOG_CONTENT+"删除附件开始"+PropUtils.LOG_FLAG);
		workflowAttachmentDao.deleteAttachment(wi.getProcessInstanceId(), getCompanyId());//删除附件
		log.debug(PropUtils.LOG_CONTENT+"删除附件结束"+PropUtils.LOG_FLAG);
		log.debug(PropUtils.LOG_CONTENT+"删除流转历史开始"+PropUtils.LOG_FLAG);
		instanceHistoryManager.deleteHistoryByworkflowId(wi.getProcessInstanceId());//删除流转历史
		log.debug(PropUtils.LOG_CONTENT+"删除流转历史结束"+PropUtils.LOG_FLAG);
		log.debug(PropUtils.LOG_CONTENT+"删除表单开始"+PropUtils.LOG_FLAG);
		deleteFormData(wi,isWf);
		log.debug(PropUtils.LOG_CONTENT+"删除表单结束"+PropUtils.LOG_FLAG);
		log.debug(PropUtils.LOG_CONTENT+"删除实例开始"+PropUtils.LOG_FLAG);
		workflowInstanceDao.delete(wi);
		log.debug(PropUtils.LOG_CONTENT+"删除实例结束"+PropUtils.LOG_FLAG);
		log.debug(PropUtils.LOG_METHOD_END+"WorkflowInstanceManager+deleteWorkflowInstance(WorkflowInstance wi,boolean isWf)"+PropUtils.LOG_FLAG);
	}
	//删除该流程实例的表单数据
	@Transactional(readOnly=false)
	public void deleteFormData(WorkflowInstance instance,boolean isWf){
		log.debug(PropUtils.LOG_METHOD_BEGIN+"WorkflowInstanceManager+deleteFormData(WorkflowInstance instance,boolean isWf)"+PropUtils.LOG_FLAG);
		log.debug(PropUtils.LOG_CONTENT+"isWf是否是在流程监控中,true表示在流程监控中,反之是在业务系统中"+PropUtils.LOG_FLAG+isWf);
		FormView form = formViewManager.getFormView(instance.getFormId());
		log.debug(PropUtils.LOG_CONTENT+"表单form"+PropUtils.LOG_FLAG+form);
		log.debug(PropUtils.LOG_CONTENT+"form.isStandardForm(),是否是标准表单"+PropUtils.LOG_FLAG+form.isStandardForm());
		if(form!=null){
			if(isWf){
				monitorDeleteInstanceSet(instance,form);
			}else{
				if(!form.isStandardForm()){
					jdbcDao.deleteData(form.getDataTable().getName(),instance.getDataId());
				}else if(form.isStandardForm()){
					String formFlowableDeleteBeanName = DefinitionXmlParse.getFormFlowableDeleteBeanName(instance.getProcessDefinitionId());
					log.debug(PropUtils.LOG_CONTENT+"formFlowableDeleteBeanName,流程属性中配置的‘删除实例的bean’"+PropUtils.LOG_FLAG+formFlowableDeleteBeanName);
					if(StringUtils.isNotEmpty(formFlowableDeleteBeanName)){
						FormFlowableDeleteInterface formFlowableDeleteBean = (FormFlowableDeleteInterface)ContextUtils.getBean(formFlowableDeleteBeanName);
						if(formFlowableDeleteBean==null){log.debug("deleteFormData中，删除实例对应的bean不能为null");throw new RuntimeException("deleteFormData中，删除实例对应的bean不能为null");}
						formFlowableDeleteBean.deleteFormFlowable(instance.getDataId());
					}else{
						monitorDeleteInstanceSet(instance,form);
					}
				}
			}
		}
		log.debug(PropUtils.LOG_METHOD_END+"WorkflowInstanceManager+deleteFormData(WorkflowInstance instance,boolean isWf)"+PropUtils.LOG_FLAG);
	}
	/**
	 * 流程监控/当是标准表单时删除实例的业务补偿
	 * @param instance
	 * @param form
	 */
	private void monitorDeleteInstanceSet(WorkflowInstance instance,FormView form){
		Map<String,String> monitorDeleteInstanceSet = DefinitionXmlParse.getMonitorDeleteInstanceSet(instance.getProcessDefinitionId());
		String setType=monitorDeleteInstanceSet.get(DefinitionXmlParse.SET_TYPE);
		String monitorDeleteInstanceSetUrl=monitorDeleteInstanceSet.get(DefinitionXmlParse.DELETE_INSTANCE_MONITOR);
		if(StringUtils.isNotEmpty(monitorDeleteInstanceSetUrl)){
			String systemCode=WebUtil.getSystemCodeByDef(instance.getProcessDefinitionId());
			if(setType.equals("http")){
				WebUtil.getHttpConnection(monitorDeleteInstanceSetUrl,ContextUtils.getCompanyId(),instance.getDataId(),systemCode);
			}else if(setType.equals("RESTful")){
				WebUtil.restful(monitorDeleteInstanceSetUrl,ContextUtils.getCompanyId(),instance.getDataId(),systemCode);
			}
		}else{
			jdbcDao.deleteData(form.getDataTable().getName(),instance.getDataId());
		}
	}
	
	public List<WorkflowInstance> getSubWorkflowInstances(String processInstanceId,Long systemId){
		return workflowInstanceDao.getSubWorkflowInstances(processInstanceId,getCompanyId(),systemId);
	}
	/**
	 * 手动结束流程
	 * @param id
	 */
	@Transactional(readOnly=false)
	public String endWorkflowInstance(Collection<Long>  workflowIds){
		if(workflowIds==null) return "请选择一个流程实例";
		int deleteNum=0,notDeleteNum=0;
		Integer num=0;
		ArrayList<Integer> ints=new ArrayList<Integer>();
		StringBuilder result = new StringBuilder();
		for(Long workflowId:workflowIds){
			num++;
			WorkflowInstance wi =  getWorkflowInstance(workflowId);
			if(wi.getParentProcessId()!=null){
				WorkflowInstance parentIns =  getWorkflowInstance(wi.getParentProcessId());
				if((wi.getProcessState()==ProcessState.SUBMIT||wi.getProcessState()==ProcessState.UNSUBMIT) &&
					 parentIns!=null && parentIns.getProcessState()==ProcessState.SUBMIT){
					ints.add(num);
					continue;
				}
			}
			if(endWorkflowInstance(wi)){
				deleteNum++;
			}else{
				notDeleteNum++;
			}
		}
		
		if(deleteNum!=0)result.append(deleteNum).append("个已取消");
		if(deleteNum!=0&&notDeleteNum!=0)result.append(",");
		if(notDeleteNum!=0)result.append(notDeleteNum).append("个不需要结束");
		if(ints.size()>0){
			result.append("请先取消第");
			for(Integer n:ints){
				result.append(n).append(",");
			}
			result.deleteCharAt(result.length()-1);
			result.append("个子流程实例的主流程实例");
		}
		return result.toString();
	}
	@Transactional(readOnly=false)
	public void endWorkflowInstance(String  workflow){
		WorkflowInstance wi =  getWorkflowInstance(workflow);
		endWorkflowInstance(wi);
	}
	/**
	 * 手动结束流程
	 * @param id
	 */
	@Transactional(readOnly=false)
	public void endWorkflowInstance(Long id){
		WorkflowInstance wi =  getWorkflowInstance(id);
		endWorkflowInstance(wi);
	}
	@Transactional(readOnly=false)
	public boolean endWorkflowInstance(WorkflowInstance wi){
		switch (wi.getProcessState()) {
		case SUBMIT:
			//取消所有子流程
			List<WorkflowInstance> subWorkflowInstances = getSubWorkflowInstances(wi.getProcessInstanceId(),wi.getSystemId());
			for(WorkflowInstance subWi : subWorkflowInstances){
				endWorkflowInstance(subWi);
			}
			taskService.endTasks(wi.getProcessInstanceId(),getCompanyId());
			wi.setProcessState(ProcessState.MANUAL_END);
			//设置"流程取消"为“true”
			processEngine.getExecutionService().setVariable(wi.getProcessInstanceId(), CommonStrings.CANCEL_FLAG, "true");
			processEngine.getExecutionService().endProcessInstance(wi.getProcessInstanceId(),"cancel");
			wi.setEndTime(new Date());
			this.saveWorkflowInstance(wi);
			generateTaskHistory(wi);
			FormView form = formViewManager.getFormView(wi.getFormId());
			if(form.isStandardForm()){
				StringBuilder sql = new StringBuilder("UPDATE ").append(form.getDataTable().getName()).append(" SET process_state=3,state='流程已取消' where workflow_id='"+wi.getProcessInstanceId()+"'");
				jdbcDao.updateTable(sql.toString());
			}
			monitorCancelInstanceSet(wi);
			return true;
		default:
			return false;
		}
		
	}
	@Transactional(readOnly=false)
	public boolean compelEndWorkflowInstance(WorkflowInstance wi){
		switch (wi.getProcessState()) {
		case SUBMIT:
			//强制结束所有子流程
			List<WorkflowInstance> subWorkflowInstances = getSubWorkflowInstances(wi.getProcessInstanceId(),wi.getSystemId());
			for(WorkflowInstance subWi : subWorkflowInstances){
				compelEndWorkflowInstance(subWi);
			}
			taskService.compelEndTasks(wi.getProcessInstanceId(),getCompanyId());
			wi.setProcessState(ProcessState.END);
			//设置"流程强制结束"为“true”
			processEngine.getExecutionService().setVariable(wi.getProcessInstanceId(), CommonStrings.COMPEL_END_FLAG, "true");
			processEngine.getExecutionService().endProcessInstance(wi.getProcessInstanceId(),"cancel");
			wi.setEndTime(new Date());
			this.saveWorkflowInstance(wi);
			generateTaskHistory(wi);
			FormView form = formViewManager.getFormView(wi.getFormId());
			if(form.isStandardForm()){
				StringBuilder sql = new StringBuilder("UPDATE ").append(form.getDataTable().getName()).append(" SET process_state=2,state='流程已结束' where workflow_id='"+wi.getProcessInstanceId()+"'");
				jdbcDao.updateTable(sql.toString());
			}
			monitorCancelInstanceSet(wi);
			return true;
		default:
			return false;
		}
		
	}
	
	/**
	 * 当是流程监控/取消实例的业务补偿
	 * @param instance
	 * @param form
	 */
	private void monitorCancelInstanceSet(WorkflowInstance instance){
		Map<String,String> monitorCancelInstanceSet = DefinitionXmlParse.getMonitorCancelInstancSet(instance.getProcessDefinitionId());
		String setType=monitorCancelInstanceSet.get(DefinitionXmlParse.SET_TYPE);
		String monitorCancelInstanceSetUrl=monitorCancelInstanceSet.get(DefinitionXmlParse.CANCEL_INSTANCE_MONITOR);
		if(StringUtils.isNotEmpty(monitorCancelInstanceSetUrl)){
			String systemCode=WebUtil.getSystemCodeByDef(instance.getProcessDefinitionId());
			if(setType.equals("http")){
				WebUtil.getHttpConnection(monitorCancelInstanceSetUrl,ContextUtils.getCompanyId(),instance.getDataId(),systemCode);
			}else if(setType.equals("RESTful")){
				WebUtil.restful(monitorCancelInstanceSetUrl,ContextUtils.getCompanyId(),instance.getDataId(),systemCode);
			}
		}
	}
	@Transactional(readOnly=false)
	private void generateTaskHistory(WorkflowInstance wi){
		List<WorkflowTask> tasks = taskService.getActivityTasks(wi.getProcessInstanceId(),wi.getCompanyId());
		for(WorkflowTask task:tasks){
			InstanceHistory ih = new InstanceHistory();
			ih.setCompanyId(task.getCompanyId());
			ih.setType(InstanceHistory.TYPE_TASK);
			ih.setInstanceId(task.getProcessInstanceId());
			ih.setExecutionId(task.getExecutionId());
			ih.setTaskName(task.getName());
			ih.setCreatedTime(new Date());
			ih.setSpecialTask(task.isSpecialTask());
			StringBuilder msg = new StringBuilder();
			msg.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ih.getCreatedTime()))
				.append("流程管理员(").append(ContextUtils.getUserName()).append(")强制结束了该流程.")
				.append(task.getTransactorName()).append("的任务被取消.");
			ih.setTransactionResult(msg.toString());
			instanceHistoryManager.saveHistory(ih);
		}
		
	}
	
	/**
	 *  自动结束流程
	 * @param processInstance
	 */
	@Transactional(readOnly=false)
	public void setWorkflowInstanceEnd(String processInstance){
		setWorkflowInstanceEnd(getWorkflowInstance(processInstance));
	}
	
	/**
	 *  自动结束流程
	 * @param processInstance
	 */
	@Transactional(readOnly=false)
	public void setWorkflowInstanceEnd(WorkflowInstance wi){
		wi.setProcessState(ProcessState.END);
		wi.setEndTime(new Timestamp(System.currentTimeMillis()));
		saveWorkflowInstance(wi);
	}

	/**
	 *  根据processInstanceId查询流程实例
	 * @param processId
	 * @return
	 */
	public WorkflowInstance getWorkflowInstance(String processInstanceId){
		Assert.notNull(ContextUtils.getCompanyId(),"companyId不能为null");
		return workflowInstanceDao.getInstanceByJbpmInstanceId(processInstanceId,ContextUtils.getCompanyId());
	}
	/**
	 *  根据processInstanceId查询流程实例
	 * @param processId
	 * @param companyId
	 * @return
	 */
	public WorkflowInstance getWorkflowInstance(String processInstanceId,Long companyId){
		Assert.notNull(processInstanceId, "processInstanceId不能为null");
		Assert.notNull(companyId, "companyId不能为null");
		return workflowInstanceDao.getInstanceByJbpmInstanceId(processInstanceId,companyId);
	}
	
	/**
	 * 列出我启动的流程实例
	 * @param workflowInstances
	 * @param documentCreatorId
	 */
	public void listMyStartWorkflowInstance(
			Page<WorkflowInstance> workflowInstances,String documentCreator) {
		workflowInstanceDao.findPage(workflowInstances, "from WorkflowInstance wi where wi.companyId = ? and  wi.documentCreator=?", 
				ContextUtils.getCompanyId(), documentCreator);
	}
	/**
	 * 列出状态为end，创建人为documentCreator的WorkflowInstance
	 * 返回(Page)
	 */
	public void listEndWorkflowInstance(Page<WorkflowInstance> workflowInstances,String documentCreator,Long companyId){
		workflowInstanceDao.listEndWorkflowInstance(workflowInstances, documentCreator, companyId, getSystemId());
	}
	/**
	 * 列出状态为end，创建人为documentCreator，类型为type的WorkflowInstance
	 * 返回(Page)
	 */
	public void listEndWorkflowInstance(Page<WorkflowInstance> workflowInstances,Long type,String documentCreator,Long companyId){
		workflowInstanceDao.listEndWorkflowInstance(workflowInstances, type, documentCreator, companyId, getSystemId());
	}
	
	/**
	 * 列出状态为未结束，创建人为documentCreator的WorkflowInstance
	 * 返回(Page)
	 */
	public void listNotEndWorkflowInstance(Page<WorkflowInstance> workflowInstances,String documentCreator,Long companyId){
		workflowInstanceDao.listNotEndWorkflowInstance(workflowInstances, documentCreator, companyId, getSystemId());
	}
	/**
	 * 列出状态为未结束，创建人为documentCreator，类型为type的WorkflowInstance
	 * 返回(Page)
	 */
	public void listNotEndWorkflowInstance(Page<WorkflowInstance> workflowInstances,Long type,String documentCreator,Long companyId){
		workflowInstanceDao.listNotEndWorkflowInstance(workflowInstances, type, documentCreator, companyId, getSystemId());
	}
	
	/**
	 * 列出状态为未结束，流程定义id
	 * 返回(Page)
	 */
	public void listNotEndWorkflowInstanceByDefinitionId(Page<WorkflowInstance> workflowInstances,Long workflowDefinitionId,Long companyId,String documentCreator){
		workflowInstanceDao.listNotEndWorkflowInstanceByDefinitionId(workflowInstances,workflowDefinitionId,companyId, getSystemId(),documentCreator);
	}
	
	/**
	 * 列出状态为结束，流程定义id
	 * 返回(Page)
	 */
	public void listEndWorkflowInstanceByDefinitionId(Page<WorkflowInstance> workflowInstances,Long workflowDefinitionId,Long companyId,String documentCreator){
		workflowInstanceDao.listEndWorkflowInstanceByDefinitionId(workflowInstances,workflowDefinitionId,companyId, getSystemId(),documentCreator);
	}
	
	@Transactional(readOnly=false)
	public CompleteTaskTipType startAndSubmitWorkflow(String processId,Map<String,String> urlMap,Integer priority,Long dataId){
		String instanceId = this.startWorkflowInstance(processId, priority,dataId);
		WorkflowTask task = this.taskService.getFirstTask(instanceId, getLoginName());
		return submitForm(task);
	}
	
	/**
	 * 根据流程定义ID启动的流程实例并返回流程实例的processInstanceId
	 * @param workflowDefinitionId
	 * @return
	 */
	@Transactional(readOnly=false)
	public String startWorkflowInstance(String processId, Integer priority,Long dataId){
		log.debug(PropUtils.LOG_METHOD_BEGIN+"发起JBPM实例开始,WorkflowInstanceManager+startWorkflowInstance(String processId, Integer priority,Long dataId)"+PropUtils.LOG_FLAG);
		log.debug(PropUtils.LOG_CONTENT+"发起实例变量CommonStrings.CREATOR"+PropUtils.LOG_FLAG+ContextUtils.getLoginName());
		log.debug(PropUtils.LOG_CONTENT+"发起实例变量CommonStrings.PRIORITY"+PropUtils.LOG_FLAG+priority);
		log.debug(PropUtils.LOG_CONTENT+"发起实例变量CommonStrings.FORM_DATA_ID"+PropUtils.LOG_FLAG+dataId);
		Assert.notNull(ContextUtils.getLoginName(),"发起实例时文档创建人不能为null");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(CommonStrings.CREATOR, ContextUtils.getLoginName());
		map.put(CommonStrings.PRIORITY, priority);
		if(dataId!=null) map.put(CommonStrings.FORM_DATA_ID, dataId);
		String result=processEngine.getExecutionService()
		.startProcessInstanceById(processId,map).getId();
		log.debug(PropUtils.LOG_CONTENT+"发起实例返回结果"+PropUtils.LOG_FLAG+result);
		log.debug(PropUtils.LOG_METHOD_END+"发起JBPM实例结束,WorkflowInstanceManager+startWorkflowInstance(String processId, Integer priority,Long dataId)"+PropUtils.LOG_FLAG);
		return result;
	}
	
	/**
	 * 根据流程定义ID启动的流程实例并返回流程实例的processInstanceId
	 * @param workflowDefinitionId
	 * @return
	 */
	/**
	 * @param processId
	 * @param priority
	 * @return
	 */
	@Transactional(readOnly=false)
	public WorkflowInstance startCustomProcessWorkflowInstance(String processId, Integer priority){
		WorkflowDefinition definition = workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
		FormView form =  formViewManager.getCurrentFormViewByCodeAndVersion(definition.getFormCode(), definition.getFromVersion());
		WorkflowInstance instance = new WorkflowInstance();
		instance.setProcessDefinitionId(definition.getProcessId());
		instance.setWorkflowDefinitionId(definition.getId());
		instance.setCreator(ContextUtils.getLoginName());
		instance.setCreatorName(ContextUtils.getUserName());
		instance.setStartTime(new Timestamp(System.currentTimeMillis()));
		instance.setProcessName(definition.getName());
		instance.setProcessState(ProcessState.UNSUBMIT);
		instance.setCurrentActivity("第一步");
		instance.setCompanyId(this.getCompanyId());
		instance.setSystemId(this.getSystemId());
		instance.setTypeId(definition.getTypeId());
		instance.setPriority(priority);
		instance.setFormName(definition.getFormName());
		instance.setFormId(form.getId());
		this.saveWorkflowInstance(instance);
		String workflowId = definition.getProcessId()+"."+instance.getId();
		
		WorkflowTask task = new WorkflowTask();
		task.setCreator(instance.getCreator());
		task.setCreatorName(instance.getCreatorName());
		task.setActive(TaskState.WAIT_TRANSACT.getIndex());
		task.setCompanyId(this.getCompanyId());
		task.setCreatedTime(new Date());
		task.setTransactor(this.getLoginName());
		task.setTransactorName(ContextUtils.getUserName());
		task.setTitle("第一步");
		task.setUrl(TaskService.DEFAULT_DO_TASK_URL);
		task.setGroupName(definition.getName());
		task.setName(task.getTitle());
		task.setCode("diyibu");
		task.setProcessingMode(TaskProcessingMode.TYPE_EDIT);
		task.setProcessInstanceId(instance.getProcessInstanceId());
		task.setExecutionId(task.getProcessInstanceId());
		task.setVisible(false);
		task.setProcessInstanceId(workflowId);
		taskService.saveTask(task);
		
		instance.setCurrentActivity(task.getName());
		instance.setTotalStep(1);
		instance.setCurrentStep(1);
		instance.setProcessInstanceId(workflowId);
		
		instance.setFirstTaskId(task.getId());
		this.saveWorkflowInstance(instance);
		return instance;
	}
	

	
	
	/**
	 * 启动流程
	 * @param processId
	 * @param creator
	 * @param priority
	 * @return
	 */
	private TaskService taskService;
	
	@Autowired
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}
	
	/**
	 * 查询流程实例的办理意见
	 */
	public List<Opinion> getOpinionsByInstanceId(String workflowId,Long companyId) {
		if(workflowId == null) throw new RuntimeException("没有给定查询意见集合的查询条件：流程实例ID");
		if(companyId == null) throw new RuntimeException("没有给定查询意见集合的查询条件：公司ID.");
		return opinionDao.getOpinionsByInstanceId(workflowId,companyId);
	}
	
	/**
	 * 查询某个任务的办理意见
	 */
	public List<Opinion> getOpinionsByTaskId(Long taskId,Long companyId) {
		return opinionDao.getOpinionsByTaskId(taskId,companyId);
	}
	
	/**
	 * 根据Id查询办理意见
	 */
	public Opinion getOpinionsById(Long opinionId) {
		return opinionDao.getOpinionsById(opinionId);
	}
	
	/**
	 *	保存审批意见 
	 */
	@Transactional(readOnly=false)
	public void saveOpinion(Opinion opi) {
		opinionDao.save(opi);
	}
	/**
	 * 删除审批意见
	 * @param opinionId
	 */
	@Transactional(readOnly=false)
	public void deleteOpinion(Long opinionId) {
		opinionDao.delete(opinionId);
	}
	
	/**
	 * 保存附件
	 * @param attachment
	 */
	@Transactional(readOnly=false)
	public void saveAttachment(WorkflowAttachment attachment){
		this.workflowAttachmentDao.save(attachment);
	}
	/**
	 *删除附件
	 * @param attachment
	 */
	@Transactional(readOnly=false)
	public void deleteAttachment(Long id){
		Assert.notNull(id, "附件id不能为null");
		WorkflowAttachmentFile file = workflowAttachmentFileDao.getAttachmentFileByAttachmentId(id);
		if(file==null){
			WorkflowAttachment attach=getAttachment(id);
			FileService fileService =(FileService)ContextUtils.getBean("fileService");
			if(attach!=null)
			  fileService.deleteFile(attach.getFilePath());
		}else{
			this.workflowAttachmentFileDao.delete(file);
		}
		this.workflowAttachmentDao.delete(id);
	}
	/**
	 *得到附件
	 * @param attachment
	 */
	public WorkflowAttachment getAttachment(Long id){
		Assert.notNull(id, "附件id不能为null");
		return this.workflowAttachmentDao.get(id);
	}
	public List<WorkflowAttachment> getAttachments(Long taskId, Long companyId) {
		Assert.notNull(companyId, "companyId不能为null");
		return workflowAttachmentDao.getAttachments(taskId,companyId);
	}
	/**
	 *得到附件列表
	 * @param attachment
	 */
	public List<WorkflowAttachment> getAttachments(String workflowId,Long companyId) {
		return workflowAttachmentDao.getAttachments(workflowId,companyId);
	}
	public List<Document> getDocuments(Long taskId, Long companyId) {
		return officeManager.getDocuments(taskId,companyId);
	}	
	/**
	 * 得到正文
	 * @param workflowId
	 * @param companyId
	 * @return
	 */
	public List<Document> getDocuments(String workflowId, Long companyId) {
		return officeManager.getAllDocumentsByWorkflowInstanceId(workflowId,companyId);
	}
	
	public List<DocumentFile> getDocumentFiles(String workflowId, Long companyId) {
		return officeManager.getAllDocumentFilesByWorkflowId(workflowId,companyId);
	}
	
	/**
	 * 查询正文实例
	 * @param documentId
	 * @return
	 */
	public Document getDocument(Long documentId) {
		Assert.notNull(documentId, "documentId不能为null");
		return officeManager.getDocument(documentId);
	}
	public DocumentFile getDocumentFile(Long documentId) {
		return officeManager.getDocumentFile(documentId);
	}

	/**
	 * 删除正文
	 * @param documentId
	 */
	@Transactional(readOnly=false)
	public void deleteDocument(Long documentId) {
		officeManager.deleteText(documentId);
	}
	
	/**
	 * 保存正文
	 * @param document
	 */
	@Transactional(readOnly=false)
	public void saveDocument(Document document) {
		officeManager.saveDocument(document);
	}
	
	/**
	 * 保存正文和文件
	 * @param entity
	 * @param file
	 */
	@Transactional(readOnly=false)
	public void saveDocumentFile(DocumentFile file){
		officeManager.saveDocumentFile(file);
	}
	/**
	 * 创建流程实例
	 * @param definitionId
	 * @param instanceId
	 * @param transactor
	 * @param parentProcessId
	 * @return
	 */
	@Transactional(readOnly=false)
	public WorkflowInstance newWorkflowInstance(String definitionId,String workflowId,String parentWorkflowId,String parentExcutionId,String parentTacheName){
		int priority = 6;
		Object priorityObject = processEngine.getExecutionService().getVariable(workflowId, CommonStrings.PRIORITY);
		if(priorityObject != null){
			priority = Integer.valueOf(priorityObject.toString());
		}
		WorkflowDefinition workflowDefinition = workflowDefinitionManager.getWorkflowDefinitionByProcessId(definitionId);
		if(workflowDefinition==null){log.debug("创建流程实例时，流程定义实体不能为null");}
		FormView form = formViewManager.getCurrentFormViewByCodeAndVersion(workflowDefinition.getFormCode(), workflowDefinition.getFromVersion());//表单版本
		if(form==null){log.debug("创建流程实例时，表单不能为null");}
		
		WorkflowInstance workflowInstance = new WorkflowInstance();
		workflowInstance.setProcessInstanceId(workflowId);
		workflowInstance.setStartTime(new Timestamp(System.currentTimeMillis()));
		workflowInstance.setCompanyId(workflowDefinition.getCompanyId());
		workflowInstance.setFormId(form.getId());
		workflowInstance.setFormName(form.getName());
		workflowInstance.setProcessState(ProcessState.UNSUBMIT);
		workflowInstance.setWorkflowDefinitionId(workflowDefinition.getId());
		workflowInstance.setProcessName(workflowDefinition.getName());
		workflowInstance.setProcessCode(workflowDefinition.getCode());
		workflowInstance.setProcessDefinitionId(workflowDefinition.getProcessId());
		workflowInstance.setTypeId(workflowDefinition.getTypeId());
		workflowInstance.setSystemId(workflowDefinition.getSystemId());
		workflowInstance.setPriority(priority);
		if(parentWorkflowId!=null){
			workflowInstance.setSubmitTime(new Timestamp(System.currentTimeMillis()));
			workflowInstance.setProcessState(ProcessState.SUBMIT);	
			workflowInstance.setParentProcessId(parentWorkflowId);
			workflowInstance.setParentExcutionId(parentExcutionId);
			workflowInstance.setParentProcessTacheName(parentTacheName);
		}
		this.saveWorkflowInstance(workflowInstance);
		List<WorkflowTask> tasks = taskService.generateFirstTask(definitionId, workflowId, priority,parentWorkflowId!=null);
		if(tasks.size()==1 && tasks.get(0).getActive().equals(TaskState.WAIT_TRANSACT.getIndex())){
			workflowInstance.setFirstTaskId(tasks.get(0).getId());
			workflowInstance.setCreator(tasks.get(0).getCreator());
			workflowInstance.setCreatorName(tasks.get(0).getCreatorName());
			setInstanceReminder(workflowInstance, tasks.get(0));
		}
		workflowInstance.setCurrentActivity(tasks.get(0).getName());
		workflowInstance.setCustomType(workflowDefinition.getCustomType());
		
		this.saveWorkflowInstance(workflowInstance);
		return workflowInstance;
	}
	
	/**
	 * 根据流程实例ID获取流程发起人
	 * @param instanceId
	 * @return
	 */
	public String getCreatorByProcessInstance(Long companyId, String instanceId){
		return workflowInstanceDao.findUnique(
				"select wfi.documentCreator from WorkflowInstance wfi where wfi.companyId = ? and wfi.processInstanceId = ?", 
				companyId, instanceId);
	}
	
	/**
	 * 根据JBPM流程实例ID，查询流程表单中某个字段的值
	 * @param instanceId
	 * @param fildName
	 * @return
	 */
	public String getFieldValueInForm(String instanceId, String fieldName){
		WorkflowInstance instance = workflowInstanceDao.getInstanceByJbpmInstanceId(
				instanceId, getCompanyId());
		return formViewManager.getFieldValue(instance.getFormId(), instance.getDataId(), fieldName);
	}
	
	/**
	 * 根据类型查询结束的流程实例个数 (不含子流程)
	 * @param companyId
	 * @param creator 创建人
	 * @param typeId 类型ID
	 * @return 
	 */
	public Integer getEndInstanceNumByCreatorAndType(Long companyId, String creator, Long typeId){
		return workflowInstanceDao.getEndInstanceNumByCreatorAndType(companyId, creator, typeId, getSystemId());
	}
	
	/**
	 * 根据类型查询未结束流程实例个数 (不含子流程)
	 * @param companyId
	 * @param creator 创建人
	 * @param typeId 类型ID
	 * @return 
	 */
	public Integer getNotEndInstanceNumByCreatorAndType(Long companyId, String creator, Long typeId){
		return workflowInstanceDao.getNotEndInstanceNumByCreatorAndType(companyId, creator, typeId, getSystemId());
	}
	
	 public Integer getEndInstanceNumByDefinition(Long companyId, String creator, WorkflowDefinition definition){
			return workflowInstanceDao.getEndInstanceNumByDefinition(companyId, creator, definition,  getSystemId());
	}
	 
	 public Integer getNotEndInstanceNumByDefinition(Long companyId, String creator, WorkflowDefinition definition){
			return workflowInstanceDao.getNotEndInstanceNumByDefinition(companyId, creator, definition,  getSystemId());
	}
	 
	 public Integer getEndInstanceNumByEnable(Long companyId, String creator){
			return workflowInstanceDao.getEndInstanceNumByEnable(companyId, creator, getSystemId());
	}
	 
	 public Integer getNotEndInstanceNumByEnable(Long companyId, String creator){
			return workflowInstanceDao.getNotEndInstanceNumByEnable(companyId, creator, getSystemId());
	}
	 
	
	/*
	 * 返回流程没有启动时，第一个环节的办理人
	 * @param workflowDefinition 流程定义
	 * @return 所有第一个环节的办理人
	 */
		private Set<String> getFirstTaskTransactorNotStarted(WorkflowDefinition workflowDefinition){
			Map<TaskTransactorCondition, String> conditions=DefinitionXmlParse.getTaskTransactor(workflowDefinition.getProcessId(), DefinitionXmlParse.getFirstTaskName(workflowDefinition.getProcessId()));
			String userCondition = conditions.get(TaskTransactorCondition.USER_CONDITION);
			//根据条件获取办理人
			Set<String> candidates = new HashSet<String>();

			if("${documentCreator}".equals(userCondition)){
				//文档创建人
				candidates.add(ContextUtils.getLoginName());
			}else if("${previousTransactorAssignment}".equals(userCondition)){
				//上一环节办理人指定
			}else if(userCondition.startsWith("${field[")){
			}else{ 
				UserParseCalculator upc = new UserParseCalculator();
				upc.setDocumentCreator(ContextUtils.getLoginName());
				candidates.addAll(upc.getUsers(userCondition,workflowDefinition.getSystemId(),workflowDefinition.getCompanyId()));
				
			}
			return candidates;
		}
		
		
		public Boolean canStartTask(String creator,WorkflowDefinition wfd){
			Set<String> transactors = getFirstTaskTransactorNotStarted(wfd);
			return transactors.contains(creator);
		}
		
		public String getFormHtml(WorkflowInstance wi,String  formHtml,Long dataId,boolean fieldRight,boolean signatureVisible){
			FormView form = formViewManager.getFormView(wi.getFormId());
			String html="";
			html=formViewManager.getFormHtml(form, formHtml, dataId,fieldRight,signatureVisible);
			return html;
		}

	public List<Opinion> getOpinions(String workflowId, Long companyId,
			String  taskName) {
		return opinionDao.getOpinions(workflowId,companyId,taskName);
	}
	public List<Opinion> getOpinions(Long taskId, Long companyId) {
		if(taskId == null) throw new RuntimeException("没有给定查询意见集合的查询条件：任务ID");
		if(companyId == null) throw new RuntimeException("没有给定查询意见集合的查询条件：公司ID");
		return opinionDao.getOpinions(taskId,companyId);
	}

	public List<Opinion> getOpinions(String workflowId, Long companyId,
			TaskProcessingMode  taskMode) {
		return opinionDao.getOpinions(workflowId,companyId,taskMode);
	}

	public List<Opinion> getOpinionsExceptTaskMode(String workflowId,
			Long companyId, TaskProcessingMode  taskMode) {
		return opinionDao.getOpinionsExceptTaskMode(workflowId,companyId,taskMode);
	}

	public List<Opinion> getOpinionsExceptTaskName(String workflowId,
			Long companyId, String taskName) {
		return opinionDao.getOpinionsExceptTaskName(workflowId,companyId,taskName);
	}

	public WorkflowAttachmentFile getAttachmentFileByAttachmentId(Long attachmentId) {
		return workflowAttachmentFileDao.getAttachmentFileByAttachmentId(attachmentId);
	}
	@Transactional(readOnly=false)
	public void saveAttachmentFile(WorkflowAttachmentFile attachmentFile) {
		workflowAttachmentFileDao.save(attachmentFile);
	}

	@Autowired
	public void setWorkflowAttachmentFileDao(
			WorkflowAttachmentFileDao workflowAttachmentFileDao) {
		this.workflowAttachmentFileDao = workflowAttachmentFileDao;
	}

	public Set<WorkflowInstance> getWorkflowInstances(Set<Long> workflowIds) {
		Set<WorkflowInstance> instances = new HashSet<WorkflowInstance>();
		for(Long workflowId : workflowIds){
			instances.add(this.getWorkflowInstance(workflowId));
		}
		return instances;
	}

	public List<WorkflowInstance> getNeedReminderInstance() {
		return workflowInstanceDao.getNeedReminderInstance();
	}
	
	public List<WorkflowTask> getNeedReminderTasksByInstance() {
		List<WorkflowInstance> list=getNeedReminderInstance();
		List<WorkflowTask> result=new ArrayList<WorkflowTask>();
		for(WorkflowInstance in:list){
			List<WorkflowTask> tasks =taskService.getActivityTasks(in.getProcessInstanceId(), in.getCompanyId());
			for(WorkflowTask task:tasks){
				if(StringUtils.isEmpty(task.getReminderStyle())){//如果任务的催办方式为空，即不催办时，则该任务的催办方式以实例的为准
					task.setReminderStyle(in.getReminderStyle());
					task.setRepeat(in.getRepeat());
					task.setDuedate(in.getDuedate());
					task.setReminderLimitTimes(in.getReminderLimitTimes());
					task.setReminderNoticeStyle(in.getReminderNoticeStyle());
					task.setReminderNoticeUser(in.getReminderNoticeUserCondition());
					result.add(task);
				}
			}
		}
		return result;
	}
	
	public void setInstanceReminder(WorkflowInstance workflow,WorkflowTask task){
		Map<String,String > reminderSetting = DefinitionXmlParse.getReminderSetting(workflow.getProcessDefinitionId());
		workflow.setReminderStyle(reminderSetting.get(DefinitionXmlParse.REMIND_STYLE));
		if(reminderSetting.get(DefinitionXmlParse.REMIND_REPEAT)!=null)workflow.setRepeat(Long.valueOf(reminderSetting.get(DefinitionXmlParse.REMIND_REPEAT)));
		if(reminderSetting.get(DefinitionXmlParse.REMIND_DUEDATE)!=null)workflow.setDuedate(Long.valueOf(reminderSetting.get(DefinitionXmlParse.REMIND_DUEDATE)));
		if(reminderSetting.get(DefinitionXmlParse.REMIND_TIME)!=null)workflow.setReminderLimitTimes(Integer.valueOf(reminderSetting.get(DefinitionXmlParse.REMIND_TIME)));
		if(reminderSetting.get(DefinitionXmlParse.REMIND_NOTICE_TYPE)!=null)workflow.setReminderNoticeStyle(reminderSetting.get(DefinitionXmlParse.REMIND_NOTICE_TYPE));
		if(reminderSetting.get(DefinitionXmlParse.REMIND_NOTICE_USER_CONDITION)!=null)workflow.setReminderNoticeUserCondition(taskService.parseUserCondition( task,reminderSetting.get(DefinitionXmlParse.REMIND_NOTICE_USER_CONDITION)));
	}
	
	/**
	 * 根据父流程的workflowId获得它的所有子流程实例（flex有用）
	 */
	public List<WorkflowInstance> getSubProcessInstance(String parentWorkflowId){
		return workflowInstanceDao.getSubProcessInstance( parentWorkflowId);
	}
	
	/**
	 * 根据父流程的workflowId和环节名获得它的子流程实例
	 * @param parentWorkflowId
	 * @return
	 */
	public List<WorkflowInstance> getSubProcessInstance(String parentWorkflowId,String tacheName){
		return workflowInstanceDao.getSubProcessInstance( parentWorkflowId,tacheName);
	}
	
	public List<WorkflowInstance> getActivityWorkflowInstance(String parentWorkflowId,String tacheName){
		return workflowInstanceDao.getActivityWorkflowInstance( parentWorkflowId,tacheName);
	}
	
	public List<FormControl> getFormDatas(Page<Object> formValues, WorkflowDefinition definition,  boolean isEnd){
		List<Long> definitionIds = workflowDefinitionManager.getAllDefinitionIdNotDraft(definition);
		FormView form = formViewManager.getCurrentFormViewByCodeAndVersion(definition.getFormCode(), definition.getFromVersion());
		List<FormControl> displayField = formViewManager.getControls(form.getId());
		StringBuilder sql = new StringBuilder("SELECT wf.PROCESS_INSTANCE_ID wf_id, tb.id");
		for(FormControl control : displayField){
			if(DataType.DATE==control.getDataType()){
				sql.append(",to_char(tb.").append(JdbcSupport.FORM_FIELD_PREFIX_STRING).append(control.getName()).append(",'yyyy-MM-dd')");
			}else if(DataType.TIME==control.getDataType()){
				sql.append(",to_char(tb.").append(JdbcSupport.FORM_FIELD_PREFIX_STRING).append(control.getName()).append(",'yyyy-MM-dd HH24:mi')");
			}else{
				sql.append(",tb.").append(JdbcSupport.FORM_FIELD_PREFIX_STRING).append(control.getName());
			}
		}
		sql.append(" from ").append(form.getDataTable().getName()).append(" tb");//SELECT id,nm,cp,ag from WF_FORM9550 
		sql.append(" inner join WF_INSTANCE wf on tb.id=wf.data_id where wf.workflow_definition_id in (");
		for(int i=0;i<definitionIds.size();i++){
			if(i!=0)sql.append(",");
			sql.append(definitionIds.get(i));
		}
		sql.append(") and wf.document_creator=? and wf.company_id=?");
		sql.append(" and (wf.process_state=? or wf.process_state=?)");
		if(isEnd){
			generalDao.findPageBySql(sql.toString(), formValues,  getLoginName(), getCompanyId(), ProcessState.END.getIndex(),ProcessState.MANUAL_END.getIndex());
		}else{
			generalDao.findPageBySql(sql.toString(), formValues,  getLoginName(), getCompanyId(), ProcessState.UNSUBMIT.getIndex(),ProcessState.SUBMIT.getIndex());
		}
		return displayField;
	}
	public List<Opinion> getOpinionsExceptCustomField(String workflowId,
			String customField) {
		Assert.notNull(customField,"查询意见列表的customField不能为空");
		return opinionDao.getOpinionsExceptCustomField(workflowId,customField);
	}
	public List<Opinion> getOpinionsByCustomField(String workflowId,
			String customField) {
		Assert.notNull(customField,"查询意见列表的customField不能为空");
		return opinionDao.getOpinionsByCustomField(workflowId,customField);
	}
	public Set<String> filter(Long dataId,  Set<String> transactors,boolean moreTransactor) {
		Set<String> result=new HashSet<String>();
		Iterator<String> it=transactors.iterator();
		if(moreTransactor){
			while(it.hasNext()){
				result.add(it.next());
				if(result.size()==2){
					break;
				}
			}
			return result;
		}else{
			while(it.hasNext()){
				result.add(transactors.iterator().next());
				break;
			}
			
			return result;
		}
		
	}
	public List<Object[]> getTaskAndOpinion(WorkflowTask task){
		return opinionDao.getOpinions( task);
	}
	/**
	 * 给流程实例中的流程编码设值
	 */
	@Transactional(readOnly=false)
	public void initAllWorkflowInstances(){
		List<WorkflowInstance> wis=workflowInstanceDao.getAllWorkflowInstances();
		for(WorkflowInstance wi:wis){
			WorkflowDefinition wfd=workflowDefinitionManager.getWfDefinition(wi.getWorkflowDefinitionId());
			wi.setProcessCode(wfd.getCode());
			this.saveWorkflowInstance(wi);
		}
	}
	
	public Integer getInstancesNumByDefId(
			Long workflowDefinitionId, Long companyId, Long systemId) {
		return workflowInstanceDao.getInstancesNumByDefId(workflowDefinitionId, companyId, systemId);
	}
	/**
	 * 根据dataId得到流程实例
	 */
	public WorkflowInstance getInstancesByDataId(Long dataId){
		return workflowInstanceDao.getInstancesByDataId(dataId);
	}
	
	public List<Opinion> getOpinions(String workflowId, Long companyId,
			String...  taskNames) {
		return opinionDao.getOpinions(workflowId,companyId,Arrays.asList(taskNames));
	}
	
	public List<Opinion> getOpinionsExceptTaskName(String workflowId,
			Long companyId, String... taskNames) {
		Assert.notNull(companyId,"companyId不能为空");
		return opinionDao.getOpinionsExceptTaskName(workflowId,companyId,Arrays.asList(taskNames));
	}
	
	public List<Opinion> getOpinionsByTacheCode(String workflowId, Long companyId,
			String  tacheCode) {
		return opinionDao.getOpinionsByTacheCode(workflowId,companyId,tacheCode);
	}
	
	public List<Opinion> getOpinionsByTacheCode(String workflowId, Long companyId,
			String...  tacheCodes) {
		Assert.notNull(companyId,"companyId不能为空");
		return opinionDao.getOpinionsByTacheCode(workflowId,companyId,Arrays.asList(tacheCodes));
	}
	
	public List<Opinion> getOpinionsByTaskName(String workflowId, Long companyId,
			String  taskName) {
		return opinionDao.getOpinionsByTaskName(workflowId,companyId,taskName);
	}
	
	public List<Opinion> getOpinionsByTaskName(String workflowId, Long companyId,
			String...  taskNames) {
		return opinionDao.getOpinionsByTaskName(workflowId,companyId,Arrays.asList(taskNames));
	}
	
	/**
	 * 暂停流程实例
	 * @param workflowIds
	 * @return
	 */
	@Transactional(readOnly=false)
	public String pauseWorkflowInstance(Collection<Long>  workflowIds){
		if(workflowIds==null) return "请选择一个流程实例";
		int deleteNum=0,notDeleteNum=0;
		for(Long workflowId:workflowIds){
			WorkflowInstance wi =  getWorkflowInstance(workflowId);
			if(pauseWorkflowInstance(wi)){
				deleteNum++;
			}else{
				notDeleteNum++;
			}
		}
		StringBuilder result = new StringBuilder();
		if(deleteNum!=0)result.append(deleteNum).append("个已暂停");
		if(deleteNum!=0&&notDeleteNum!=0)result.append(",");
		if(notDeleteNum!=0)result.append(notDeleteNum).append("个不需要暂停");
		return result.toString();
	}


	@Transactional(readOnly=false)
	public void pauseWorkflowInstance(String  workflow){
		WorkflowInstance wi =  getWorkflowInstance(workflow);
		pauseWorkflowInstance(wi);
	}
	/**
	 * 手动结束流程
	 * @param id
	 */
	@Transactional(readOnly=false)
	public void pauseWorkflowInstance(Long id){
		WorkflowInstance wi =  getWorkflowInstance(id);
		pauseWorkflowInstance(wi);
	}
	@Transactional(readOnly=false)
	public boolean pauseWorkflowInstance(WorkflowInstance wi){
		switch (wi.getProcessState()) {
		case SUBMIT:
			//暂停所有子流程
			List<WorkflowInstance> subWorkflowInstances = getSubWorkflowInstances(wi.getProcessInstanceId(),wi.getSystemId());
			for(WorkflowInstance subWi : subWorkflowInstances){
				pauseWorkflowInstance(subWi);
			}

			wi.setProcessState(ProcessState.PAUSE);
			wi.setEndTime(new Timestamp(System.currentTimeMillis()));
			this.saveWorkflowInstance(wi);
//			generateTaskHistory(wi);
			taskService.pauseTasks(wi.getProcessInstanceId(),getCompanyId());
			boolean isStandard=false;
			Object dataTableId=null;
			List list=jdbcDao.excutionSql("select standard,FK_DATA_TABLE_ID from mms_view where id="+wi.getFormId());
			if(list.size()>0){
				if(DATABASE_ORACLE.equals(PropUtils.getDataBase())){
					isStandard=((BigDecimal)(((Map)list.get(0)).get("standard"))).equals(BigDecimal.valueOf(1l))?true:false;
					dataTableId=(BigDecimal)((Map)list.get(0)).get("FK_DATA_TABLE_ID");
				}else if(DATABASE_MYSQL.equals(PropUtils.getDataBase())){
					isStandard=(Boolean)(((Map)list.get(0)).get("standard"));
					dataTableId=(Long)((Map)list.get(0)).get("FK_DATA_TABLE_ID");
				}else{
					Short standard = (Short)((Map)list.get(0)).get("standard");
					if(standard==1){
						isStandard=true;
					}
					//isStandard=Boolean.valueOf(Short.toString((Short)((Map)list.get(0)).get("standard")));
					dataTableId=(BigDecimal)((Map)list.get(0)).get("FK_DATA_TABLE_ID");
				}
			}
			if(dataTableId!=null){
				List dataTableList=jdbcDao.excutionSql("select name from mms_data_table where id="+dataTableId);
				if(dataTableList.size()>0){
					if(isStandard){
						String tableName=(String)((Map)dataTableList.get(0)).get("name");
						jdbcDao.updateTable("update "+tableName+" set process_state="+ProcessState.PAUSE.getIndex()+" where id="+wi.getDataId());
						return true;
					}
				}
			}
			//暂停实例的业务补偿
			pauseInstanceSet(wi);
			return true;
		default:
			return false;
		}
		
	}
	
	/**
	 * 流程监控/暂停实例的的业务补偿
	 * @param instance
	 * @param form
	 */
	private void pauseInstanceSet(WorkflowInstance instance){
		Map<String,String> pauseInstanceSet = DefinitionXmlParse.getPauseInstancSet(instance.getProcessDefinitionId());
		String setType=pauseInstanceSet.get(DefinitionXmlParse.SET_TYPE);
		String pauseInstanceSetUrl=pauseInstanceSet.get(DefinitionXmlParse.PAUSE_INSTANCE_MONITOR);
		if(StringUtils.isNotEmpty(pauseInstanceSetUrl)){
			String systemCode=WebUtil.getSystemCodeByDef(instance.getProcessDefinitionId());
			if(setType.equals("http")){
				WebUtil.getHttpConnection(pauseInstanceSetUrl,ContextUtils.getCompanyId(),instance.getDataId(),systemCode);
			}else if(setType.equals("RESTful")){
				WebUtil.restful(pauseInstanceSetUrl,ContextUtils.getCompanyId(),instance.getDataId(),systemCode);
			}
		}
	}
	
	/**
	 * 继续暂停的流程实例
	 * @param workflowIds
	 * @return
	 */
	@Transactional(readOnly=false)
	public String continueWorkflowInstance(Collection<Long>  workflowIds){
		if(workflowIds==null) return "请选择一个流程实例";
		int continueNum=0,notContinueNum=0;
		for(Long workflowId:workflowIds){
			WorkflowInstance wi =  getWorkflowInstance(workflowId);
			if(continueWorkflowInstance(wi)){
				continueNum++;
			}else{
				notContinueNum++;
			}
		}
		StringBuilder result = new StringBuilder();
		if(continueNum!=0)result.append(continueNum).append("个已继续");
		if(continueNum!=0&&notContinueNum!=0)result.append(",");
		if(notContinueNum!=0)result.append(notContinueNum).append("个不需要继续");
		return result.toString();
	}
	
	@Transactional(readOnly=false)
	public boolean continueWorkflowInstance(WorkflowInstance wi){
		switch (wi.getProcessState()) {
		case PAUSE:
			//继续所有子流程
			List<WorkflowInstance> subWorkflowInstances = getSubWorkflowInstances(wi.getProcessInstanceId(),wi.getSystemId());
			for(WorkflowInstance subWi : subWorkflowInstances){
				continueWorkflowInstance(subWi);
			}
			
			wi.setProcessState(ProcessState.SUBMIT);
			wi.setEndTime(null);
			this.saveWorkflowInstance(wi);
//			generateTaskHistory(wi);
			taskService.continueTasks(wi.getProcessInstanceId(),getCompanyId());
			boolean isStandard=false;
			Object dataTableId=null;
			List list=jdbcDao.excutionSql("select standard,FK_DATA_TABLE_ID from mms_view where id="+wi.getFormId());
			if(list.size()>0){
				if(DATABASE_ORACLE.equals(PropUtils.getDataBase())){
					isStandard=((BigDecimal)(((Map)list.get(0)).get("standard"))).equals(BigDecimal.valueOf(1l))?true:false;
					dataTableId=(BigDecimal)((Map)list.get(0)).get("FK_DATA_TABLE_ID");
				}else if(DATABASE_MYSQL.equals(PropUtils.getDataBase())){
					isStandard=(Boolean)(((Map)list.get(0)).get("standard"));
					dataTableId=(Long)((Map)list.get(0)).get("FK_DATA_TABLE_ID");
				}else{
					Short standard = (Short)((Map)list.get(0)).get("standard");
					if(standard==1){
						isStandard=true;
					}
					dataTableId=(BigDecimal)((Map)list.get(0)).get("FK_DATA_TABLE_ID");
				}
			}
			if(dataTableId!=null){
				List dataTableList=jdbcDao.excutionSql("select name from mms_data_table where id="+dataTableId);
				if(dataTableList.size()>0){
					if(isStandard){
						String tableName=(String)((Map)dataTableList.get(0)).get("name");
						jdbcDao.updateTable("update "+tableName+" set process_state="+ProcessState.SUBMIT.getIndex()+" where id="+wi.getDataId());
						return true;
					}
				}
			}
			//继续实例时的业务补偿
			continueInstanceSet(wi);
			return true;
		default:
			return false;
		}
		
	}
	
	/**
	 * 流程监控/暂停实例的的业务补偿
	 * @param instance
	 * @param form
	 */
	private void continueInstanceSet(WorkflowInstance instance){
		Map<String,String> continueInstanceSet = DefinitionXmlParse.getContinueInstancSet(instance.getProcessDefinitionId());
		String setType=continueInstanceSet.get(DefinitionXmlParse.SET_TYPE);
		String continueInstanceSetUrl=continueInstanceSet.get(DefinitionXmlParse.CONTINUE_INSTANCE_MONITOR);
		if(StringUtils.isNotEmpty(continueInstanceSetUrl)){
			String systemCode=WebUtil.getSystemCodeByDef(instance.getProcessDefinitionId());
			if(setType.equals("http")){
				WebUtil.getHttpConnection(continueInstanceSetUrl,ContextUtils.getCompanyId(),instance.getDataId(),systemCode);
			}else if(setType.equals("RESTful")){
				WebUtil.restful(continueInstanceSetUrl,ContextUtils.getCompanyId(),instance.getDataId(),systemCode);
			}
		}
	}
	
	public List<String> getInstanceIdByDelegate(TrustRecord delegateMain){
		String[] activityNames = delegateMain.getActivityName().split(",");
		List<String> instanceIds = new ArrayList<String>();
		for(String activityName:activityNames){
			instanceIds.addAll(workflowInstanceDao.getInstanceIdByProcessId(delegateMain.getProcessId(),delegateMain.getCompanyId(),activityName));
		}
		return instanceIds;
	}
	
	
	/**
	 * 强制结束流程
	 * @param workflowIds
	 * @return
	 */
	public String compelEndWorkflowInstance(Set<Long> workflowIds) {
		if(workflowIds==null) return "请选择一个流程实例";
		int deleteNum=0,notDeleteNum=0;
		Integer num=0;
		ArrayList<Integer> ints=new ArrayList<Integer>();
		StringBuilder result = new StringBuilder();
		for(Long workflowId:workflowIds){
			num++;
			WorkflowInstance wi =  getWorkflowInstance(workflowId);
			if(wi.getParentProcessId()!=null){
				WorkflowInstance parentIns =  getWorkflowInstance(wi.getParentProcessId());
				if((wi.getProcessState()==ProcessState.SUBMIT||wi.getProcessState()==ProcessState.UNSUBMIT) &&
					 parentIns!=null && parentIns.getProcessState()==ProcessState.SUBMIT){
					ints.add(num);
					continue;
				}
			}
			if(compelEndWorkflowInstance(wi)){
				deleteNum++;
			}else{
				notDeleteNum++;
			}
		}
		
		if(deleteNum!=0)result.append(deleteNum).append("个已强制结束");
		if(deleteNum!=0&&notDeleteNum!=0)result.append(",");
		if(notDeleteNum!=0)result.append(notDeleteNum).append("个不需要结束");
		if(ints.size()>0){
			result.append("请先强制结束第");
			for(Integer n:ints){
				result.append(n).append(",");
			}
			result.deleteCharAt(result.length()-1);
			result.append("个子流程实例的主流程实例");
		}
		return result.toString();
	}
	/**
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(Object object) {
		WorkflowInstanceManager myClass = (WorkflowInstanceManager) object;
		return new CompareToBuilder().append(this.workflowDefinitionManager,
				myClass.workflowDefinitionManager).append(this.formViewManager,
				myClass.formViewManager).append(this.opinionDao,
				myClass.opinionDao).append(this.generalDao, myClass.generalDao)
				.append(this.workflowAttachmentFileDao,
						myClass.workflowAttachmentFileDao).append(
						this.formHtmlParser, myClass.formHtmlParser).append(
						this.workflowAttachmentDao,
						myClass.workflowAttachmentDao).append(
						this.officeManager, myClass.officeManager).append(
						this.instanceHistoryManager,
						myClass.instanceHistoryManager).append(
						this.workflowInstanceDao, myClass.workflowInstanceDao)
				.append(this.processEngine, myClass.processEngine).append(
						this.jdbcDao, myClass.jdbcDao).append(this.taskService,
						myClass.taskService).append(this.workflowTaskDao,
						myClass.workflowTaskDao).toComparison();
	}
	
	/**
	 * 判断当前环节是否是子流程
	 * @param processInstanceId
	 * @return
	 */
	public List<Object> getActivetySubProcess(String processInstanceId) {
		return workflowInstanceDao.getActivetySubProcess(processInstanceId);
	}
}
	
