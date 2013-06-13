package com.norteksoft.wf.engine.core;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.activity.ActivityExecution;
import org.jbpm.api.listener.EventListener;
import org.jbpm.api.listener.EventListenerExecution;
import org.jbpm.internal.log.Log;
import org.springframework.dao.DataAccessException;
import org.springframework.util.Assert;

import com.norteksoft.mms.form.dao.GeneralDao;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.jdbc.JdbcSupport;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.base.enumeration.CommonStrings;
import com.norteksoft.wf.base.enumeration.ProcessState;
import com.norteksoft.wf.base.utils.WebUtil;
import com.norteksoft.wf.engine.client.EndInstanceInterface;
import com.norteksoft.wf.engine.core.impl.UserParseCalculator;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;

import edu.emory.mathcs.backport.java.util.Arrays;


public class ProcessEndListener  implements EventListener  {

  private static final long serialVersionUID = 1L;
  private static final Log log = Log.getLog(ProcessEndListener.class.getName());
  public static final SimpleDateFormat SIMPLEDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  
  private String creator;
  private String processAdmin;
  private TaskService taskService;
  private String processId;
  
  private String parentDefinitionId;
  private String subDefinitionId;
  private WorkflowInstance parentWorkflow;
  private FormView parentForm;
  private FormView subForm;
  
  private WorkflowInstance workflow;
  
  private String parentActivityName;
  
  
public void notify(EventListenerExecution execution) {
	Assert.notNull(execution,"流程结束监听中，execution不能为null");
	  WorkflowDefinitionManager workflowDefinitionManager = (WorkflowDefinitionManager)ContextUtils.getBean("workflowDefinitionManager");
	    WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
	   FormViewManager formManager = (FormViewManager)ContextUtils.getBean("formViewManager");
	   ProcessEngine processEngine = (ProcessEngine)ContextUtils.getBean("processEngine");
	   JdbcSupport jdbcDao=(JdbcSupport)ContextUtils.getBean("jdbcDao");
	//数据库方言
	   boolean isOracle = PropUtils.DATABASE_ORACLE.equals(PropUtils.getDataBase());
	  taskService = (TaskService) ContextUtils.getBean("taskService");
	  processId = execution.getProcessInstance().getId();
	  creator = execution.getVariable("creator").toString();
	  WorkflowDefinition workflowDefinition = workflowDefinitionManager.getWorkflowDefinitionByProcessId(execution.getProcessDefinitionId());
	  if(workflowDefinition==null){throw new RuntimeException("流程结束监听中，流程定义实体不能为null");}
	  processAdmin = workflowDefinition.getAdminLoginName();
	  workflow = workflowInstanceManager.getWorkflowInstance(processId);
	  subForm = formManager.getFormView(workflow.getFormId());//子流程表单
	  Object obj=execution.getVariable(CommonStrings.CANCEL_FLAG);
	  execution.removeVariable(CommonStrings.CANCEL_FLAG);
	  Object comobj=execution.getVariable(CommonStrings.COMPEL_END_FLAG);
	  execution.removeVariable(CommonStrings.COMPEL_END_FLAG);
	  if(StringUtils.isNotEmpty(workflow.getParentProcessId())&&StringUtils.isNotEmpty(workflow.getParentExcutionId())){
		ExecutionService executionService = processEngine.getExecutionService();
		
		subDefinitionId = execution.getProcessDefinitionId();
		parentWorkflow = workflowInstanceManager.getWorkflowInstance(workflow.getParentProcessId());
		parentDefinitionId = parentWorkflow.getProcessDefinitionId();
		
		parentForm = formManager.getFormView(parentWorkflow.getFormId());
		ActivityExecution exe=((ActivityExecution)executionService.findExecutionById(workflow.getParentExcutionId()));
		 
		if(exe!=null){
			parentActivityName = exe.getActivityName();
			 if(!(obj!=null||comobj!=null)){//正常结束时才进行主子表单赋值操作
				 subProcessEnd((ActivityExecution)execution);
			 }
			if(workflowInstanceManager.getActivityWorkflowInstance(parentWorkflow.getProcessInstanceId(), parentActivityName).size()==1
					&&workflowInstanceManager.getActivityWorkflowInstance(parentWorkflow.getProcessInstanceId(), parentActivityName).get(0).equals(workflow)){
				String transitionName = DefinitionXmlParse.getSubProcessTransition(parentDefinitionId,parentActivityName);
				executionService.signalExecutionById(workflow.getParentExcutionId(),transitionName);
			}
		  }
		}
		
	  ActivityExecution ae = (ActivityExecution)execution;
	  Timestamp submitTime = new Timestamp(System.currentTimeMillis());
	  Timestamp endTime = new Timestamp(System.currentTimeMillis());
	 
	  if(obj!=null||comobj!=null){//取消流程或强制结束时
		  if((obj !=null&& "true".equals(obj.toString()))||(comobj!=null&&"true".equals(comobj.toString()))){
			  if(subForm.isStandardForm()){
				  try{
					  StringBuilder sql = new StringBuilder("UPDATE ").append(subForm.getDataTable().getName()).append(" SET ");
					  if(workflow.getProcessState()==ProcessState.UNSUBMIT){
						  if(isOracle){
							  sql.append("submit_time=to_timestamp('"+SIMPLEDATEFORMAT.format(submitTime)+"','yyyy-MM-dd hh24:mi:ss')").append(",");
						  }else{
							  sql.append("submit_time='"+SIMPLEDATEFORMAT.format(submitTime)+"'").append(",");
						  }
					  }
					  sql.append("current_activity_name='"+ae.getActivityName()+"'");
					  sql.append(",process_state=2");
					  if(isOracle){
						  sql.append(",end_time=to_timestamp('"+SIMPLEDATEFORMAT.format(endTime)+"','yyyy-MM-dd hh24:mi:ss')");
					  }else{
						  sql.append(",end_time='"+SIMPLEDATEFORMAT.format(endTime)+"'");
					  }
					  sql.append(" where id="+workflow.getDataId());
					  jdbcDao.updateTable(sql.toString());
			  } catch (NumberFormatException e) {
					 throw new RuntimeException("numberFormatException",e);
				} catch (DataAccessException e) {
					 throw new RuntimeException("update Exception",e);
				} 
			  }else{
				  StringBuilder sql = new StringBuilder("UPDATE ").append(subForm.getDataTable().getName()).append(" SET ");
				  sql.append("current_activity_name='"+ae.getActivityName()+"'");
				  sql.append(",process_state=2");
				  sql.append(" where id="+workflow.getDataId());
				  jdbcDao.updateTable(sql.toString());
			  }
		  }
	  }else{//流程正常结束
		  if(subForm.isStandardForm()){
			  try {
				  GeneralDao generalDao = (GeneralDao)ContextUtils.getBean("generalDao");
				  Object entity = generalDao.getObject(subForm.getDataTable().getEntityName(), workflow.getDataId());
				  
				  if(workflow.getProcessState()==ProcessState.UNSUBMIT){
					  BeanUtils.setProperty(entity, "workflowInfo.submitTime", submitTime);
				  }
				  BeanUtils.setProperty(entity, "workflowInfo.currentActivityName", ae.getActivityName());
				  BeanUtils.setProperty(entity, "workflowInfo.processState",ProcessState.END);
				  BeanUtils.setProperty(entity, "workflowInfo.endTime",endTime);
				  generalDao.save(entity);
			  } catch (IllegalAccessException e) {
					log.error(e.getMessage());
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					log.error(e.getMessage());
					throw new RuntimeException(e);
				} 
		  }else{
			  StringBuilder sql = new StringBuilder("UPDATE ").append(subForm.getDataTable().getName()).append(" SET ");
			  sql.append("current_activity_name='"+ae.getActivityName()+"'");
			  sql.append(",process_state=2");
			  sql.append(" where id="+workflow.getDataId());
			  jdbcDao.updateTable(sql.toString());
		  }
	  }
	  
	  
	  if(workflow.getProcessState()==ProcessState.UNSUBMIT){
		  workflow.setSubmitTime(submitTime);
	  }
	  workflow.setEndTime(endTime);
	  if(workflow.getProcessState()!=ProcessState.MANUAL_END){
		  workflow.setProcessState(ProcessState.END);
	  }
	  workflow.setCurrentActivity("流程结束");
	  if(workflow.getProcessState()!=ProcessState.MANUAL_END){
		  workflowInstanceManager.setWorkflowInstanceEnd(workflow);
	  }
	  workflowInstanceManager.saveWorkflowInstance(workflow);
	  List<WorkflowTask> tasks=taskService.getActivityTasks(processId, workflow.getCompanyId());
	 if(tasks.size()>0){
		 WorkflowTask task=tasks.get(0);
		 if(task.getProcessingMode()!=TaskProcessingMode.TYPE_READ){//当前任务不是抄送环节、分发环节时，设置当前任务的状态为已完成
			 task.setActive(TaskState.COMPLETED.getIndex());
		 }
	 }
	 //流程正常结束时业务补偿
	 if(workflow.getProcessState()==ProcessState.END){
		  if(!(comobj!=null && "true".equals(comobj.toString()))){//不是强制结束流程时
			  endInstanceSet(workflow);
		  }
	 }
	  inform(execution.getProcessDefinitionId());
	  
  }

	/**
	 * 当是流程正常结束时业务补偿
	 * @param instance
	 * @param form
	 */
	private void endInstanceSet(WorkflowInstance instance){
		String endInstanceBeanName = DefinitionXmlParse.getEndInstanceBean(instance.getProcessDefinitionId());
		if(StringUtils.isNotEmpty(endInstanceBeanName)){
			EndInstanceInterface endInstanceBean=(EndInstanceInterface)ContextUtils.getBean(endInstanceBeanName);
			if(endInstanceBean==null){throw new RuntimeException("流程结束监听中，流程结束时业务补偿bean不能为null");}
			endInstanceBean.endInstanceExecute(instance.getDataId());
		}
	}
	/*
	 * 判断是否共用表单
	 * 如果共用表单，不做处理
	 * 如果不公用表单，判断父子表单类型，然后赋值
	 */
	private void subProcessEnd(ActivityExecution execution){
		if(!DefinitionXmlParse.isSharedForm(parentDefinitionId,subDefinitionId)){
			 if(parentForm==null){throw new RuntimeException("流程结束监听中，父流程对应的表单不能为null");}
			if(parentForm.isStandardForm()){
				fillParentEntity();
				
			}else if(!parentForm.isStandardForm()){
				fillSubDefaultForm();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void fillParentEntity(){
	   FormViewManager formManager = (FormViewManager)ContextUtils.getBean("formViewManager");
	   GeneralDao generalDao = (GeneralDao)ContextUtils.getBean("generalDao");
		try {
			Object parentFormEntity = generalDao.getObject(parentForm.getDataTable().getEntityName(), parentWorkflow.getDataId());
			Map<String,String> subToMainMap = DefinitionXmlParse.getSubToMain(parentDefinitionId, parentActivityName);
			Map<String,Object> valueMap = new HashMap<String,Object>();
			 if(subForm==null){throw new RuntimeException("流程结束监听中，子流程对应的表单不能为null");}
			if(subForm.isStandardForm()){
				if(subForm.getDataTable()==null){throw new RuntimeException("流程结束监听中，子流程表单对应的数据表不能为null");}
				if(subForm.getDataTable().getEntityName()==null){throw new RuntimeException("流程结束监听中，表单对应的数据表实体类名不能为null");}
				Object subEntity = generalDao.getObject(subForm.getDataTable().getEntityName(), workflow.getDataId());
				for(String subFieldName:subToMainMap.keySet()){
					Object subFieldValue = BeanUtils.getProperty(subEntity, subFieldName);
					if(subFieldValue!=null){
						valueMap.put(subToMainMap.get(subFieldName),subFieldValue);
					}
				}
			}else if(!subForm.isStandardForm()){
				Map dataMap = formManager.getDataMap(subForm.getDataTable().getName(), workflow.getDataId());
				for(String subFieldName:subToMainMap.keySet()){
					Object subFieldValue = dataMap.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+subFieldName);
					if(subFieldValue!=null){
						valueMap.put(subToMainMap.get(subFieldName),subFieldValue);
					}
				}
			}
			
			BeanUtils.populate(parentFormEntity, valueMap);
			generalDao.save(parentFormEntity);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void fillSubDefaultForm(){
	   FormViewManager formManager = (FormViewManager)ContextUtils.getBean("formViewManager");
	   GeneralDao generalDao = (GeneralDao)ContextUtils.getBean("generalDao");
		Map<String,String> subToMainMap = DefinitionXmlParse.getSubToMain(parentDefinitionId, parentActivityName);
		 Map<String,String[]> valueMap = new HashMap<String,String[]>();
		try {
			if(subForm.isStandardForm()){
				if(subForm.getDataTable()==null){throw new RuntimeException("流程结束监听中，子流程表单对应的数据表不能为null");}
				if(subForm.getDataTable().getEntityName()==null){throw new RuntimeException("流程结束监听中，表单对应的数据表实体类名不能为null");}
				Object subEntity = generalDao.getObject(subForm.getDataTable().getEntityName(), workflow.getDataId());
				for(String subFieldName:subToMainMap.keySet()){
					Object subFieldValue = BeanUtils.getProperty(subEntity, subFieldName);
					if(subFieldValue!=null){
						valueMap.put(subToMainMap.get(subFieldName), new String[]{subFieldValue.toString()});
					}
				}
			}else if(!subForm.isStandardForm()){
				Map dataMap = formManager.getDataMap(subForm.getDataTable().getName(), workflow.getDataId());
				for(String subFieldName:subToMainMap.keySet()){
					Object subFieldValue = dataMap.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+subFieldName);
					if(subFieldValue!=null){
						valueMap.put(subToMainMap.get(subFieldName),new String[]{subFieldValue.toString()});
					}
				}
			}
		} catch (IllegalAccessException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
		if(!valueMap.isEmpty()){
			formManager.saveFormContentToTable(valueMap,parentForm.getId(),parentWorkflow.getDataId());
		}
	}

  	/*
	 * 通知
	 */
	@SuppressWarnings("unchecked")
	private void inform( String myProcessId){
		if(DefinitionXmlParse.processInform(myProcessId)){
			log.info("流程结束时有需要通知的用户");
			String informType=DefinitionXmlParse.getProcessInformType(myProcessId);
			String[] types=informType.split(",");
			List<String> list = Arrays.asList(types);
			if(list.contains(CommonStrings.EMAIL_STYLE)){
				Set<String> emails = getEmailsInformCondition(myProcessId);//根据users得到所有结束通知的email地址
				log.info("需要通知的用户email地址有：" + emails.toString());
				DefinitionXmlParse.processInformMail(myProcessId,emails);
			}
			if(list.contains(CommonStrings.RTX_STYLE)){
				String loginNames = getLoginNameInformCondition(myProcessId);//根据users得到得到登录名
				log.info("需要通知的用户登录名有：" + loginNames);
				DefinitionXmlParse.processInformRTX(myProcessId,loginNames);
			}
			if(list.contains(CommonStrings.SWING_STYLE)){
				String loginNames = getLoginNameInformCondition(myProcessId);//根据users得到得到登录名
				log.info("需要通知的用户登录名有：" + loginNames);
				DefinitionXmlParse.processInformSwing(myProcessId,loginNames,workflow);
			}
		}
	}
	
	/*
	 * 解析通知条件获得需要通知用户的邮件地址
	 */
	private Set<String> getEmailsInformCondition(String myProcessId){
		WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
		WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(processId);
		String condition = DefinitionXmlParse.getProcessInformUserCondition(myProcessId);
		log.info("根据流程定义文件得到流程结束时需要通知的用户条件为：" + condition);
		UserParseCalculator upc = WebUtil.getUserParseInfor(processId,creator,processAdmin);
		return WebUtil.getEmailsInformCondition(condition, wi.getSystemId(), wi.getCompanyId(),upc);
	}
	
	/*
	 * 解析通知条件获得需要通知用户的登录名
	 */
	private String getLoginNameInformCondition(String myProcessId){
		WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
		WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(processId);
		String condition = DefinitionXmlParse.getProcessInformUserCondition(myProcessId);
		log.info("根据流程定义文件得到流程结束时需要通知的用户条件为：" + condition);
		UserParseCalculator upc = WebUtil.getUserParseInfor(processId,creator,processAdmin);
		return WebUtil.getLoginNameInformCondition(condition, wi.getSystemId(), wi.getCompanyId(),upc);
	}
}
