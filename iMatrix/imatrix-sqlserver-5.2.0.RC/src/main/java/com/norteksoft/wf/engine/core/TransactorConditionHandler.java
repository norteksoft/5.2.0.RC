package com.norteksoft.wf.engine.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jbpm.api.activity.ActivityExecution;
import org.jbpm.api.model.OpenExecution;

import com.norteksoft.acs.api.AcsApi;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.base.enumeration.CommonStrings;
import com.norteksoft.wf.base.enumeration.TaskTransactorCondition;
import com.norteksoft.wf.base.exception.WorkflowException;
import com.norteksoft.wf.engine.client.SingleTransactorSelector;
import com.norteksoft.wf.engine.core.impl.UserParseCalculator;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;

public class TransactorConditionHandler {

	public static final String  DOCUMENT_CREATOR = "documentCreator";//文档创建人
	public static final String CURRENT_TRANSACTOR = "currentTransactor";//当前办理人
	public static final String PREVIOUS_TRANSACTOR = "previousTransactor";//上一环节办理人
	public static final String PROCESS_ADMIN = "processAdmin";//流程管理员
	public static final String PROCESS_INSTANCEID = "processInstanceId";//流程管理员
	/*
	 * 根据条件选定办理人
	 * @param conditions
	 * @param creator
	 * @param _wf_transactor
	 * @return
	 */
	public static  Set<String> processCondition(Map<TaskTransactorCondition, String> conditions, OpenExecution execution,Map<String,String> param){
		Long companyId=ContextUtils.getCompanyId();
		if(companyId==null){
			Object compIdStr=execution.getVariable(CommonStrings.COMPANY_ID);
			if(compIdStr!=null){
				companyId=Long.parseLong(compIdStr.toString()); 
			}
		}
		String userCondition = conditions.get(TaskTransactorCondition.USER_CONDITION);
		if(userCondition==null){throw new RuntimeException("办理人设置中，解析条件时，设置的条件不能为null");}
		//根据条件获取办理人
		Set<String> candidates = new HashSet<String>();
		if("${documentCreator}".equals(userCondition)){
			//文档创建人
			candidates.add(param.get(DOCUMENT_CREATOR));
		}else if("${previousTransactorAssignment}".equals(userCondition)){
			//上一环节办理人指定
			candidates.add(CommonStrings.TRANSACTOR_ASSIGNMENT);
		}else if(userCondition.startsWith("${field[")){
			//文档字段中指定//${field[name_zn[name_en]]}
			int start = userCondition.lastIndexOf("[");
			int end = userCondition.indexOf("]");
			String fieldName = userCondition.substring(start + 1, end);
			//根据流程实例ID获取流程表单中指定字段的值
			WorkflowInstanceManager manager = (WorkflowInstanceManager) ContextUtils.getBean("workflowInstanceManager");
			String fieldValues =  manager.getFieldValueInForm(param.get(PROCESS_INSTANCEID), fieldName);
			if(fieldValues==null){throw new RuntimeException("办理人设置中，文档字段中指定时，该字段的值不能为null");}
			for(String fieldValue:fieldValues.split(",")){
				fieldValue = fieldValue.trim();
				if(StringUtils.isNotEmpty(fieldValue)){
					if("ALLCOMPANYID".equals(fieldValue)){//所有人员(不包含无部门人员)
						List<String> loginNames = ApiFactory.getAcsService().getLoginNamesByCompany(companyId);
						candidates.addAll(loginNames);
					}else if("ALLWORKGROUP".equals(fieldValue)){//所有工作组中的人员
						List<String> loginNames = ApiFactory.getAcsService().getLoginNamesByWorkgroup(companyId);
						candidates.addAll(loginNames);
					}else{
						candidates.add(fieldValue);
					}
				}
			}
		}else{ 
			if(execution==null){throw new RuntimeException("办理人设置中，解析条件时，execution不能为null");}
			FormViewManager formManager = (FormViewManager)ContextUtils.getBean("formViewManager");
			WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
			WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(param.get(PROCESS_INSTANCEID),companyId);
			if(wi==null){throw new RuntimeException("办理人设置中，解析条件时，流程实例不能为null");}
			FormView form = formManager.getFormView(wi.getFormId());
			UserParseCalculator upc = new UserParseCalculator();
			upc.setDataId(wi.getDataId());
			upc.setFormView(form);
			upc.setDocumentCreator(param.get(DOCUMENT_CREATOR));
			upc.setPreviousTransactor(param.get(PREVIOUS_TRANSACTOR));
			upc.setCurrentTransactor(param.get(CURRENT_TRANSACTOR));
			upc.setProcessAdmin(param.get(PROCESS_ADMIN));
			candidates.addAll(processAdditionalCondition(conditions,upc.getUsers(userCondition,wi.getSystemId(),wi.getCompanyId()), param.get(DOCUMENT_CREATOR), execution));
			
		}
		return candidates;
	}
	
	/*
	 * 根据附加条件过滤办理人
	 * @param conditions
	 * @param candidates
	 * @param creator
	 * @param _wf_transactor
	 * @return
	 */
	private static  Set<String> processAdditionalCondition(
			Map<TaskTransactorCondition, String> conditions, Set<String> candidates, 
			String creator, OpenExecution execution){
		
		Set<String> results = new HashSet<String>();
		String selectOne = conditions.get(TaskTransactorCondition.SELECT_ONE_FROM_MULTIPLE);
		String onlyInCreatorDept = conditions.get(TaskTransactorCondition.ONLY_IN_CREATOR_DEPARTMENT);
		String withCreatorDept = conditions.get(TaskTransactorCondition.WITH_CREATOR_DEPARTMENT);
		if("true".equals(onlyInCreatorDept) || "true".equals(withCreatorDept)){
			List<String[]> usersInSameDept = AcsApi.getUsersInSameDept(ContextUtils.getCompanyId(), creator);
			
			//只能为创建人部门(交集)
			if("true".equals(onlyInCreatorDept)){
				for(String[] user1 : usersInSameDept){
					if(candidates.contains(user1[1])){
						results.add(user1[1]);
					}
				}
			}else{
				results.addAll(candidates);
			}
			//创建人部门参与(并集)
			if("true".equals(withCreatorDept)){
				for(String[] user1 : usersInSameDept){
						results.add(user1[1]);
				}
			}
		}else{
			results.addAll(candidates);
		}
		Set<String> latest = new HashSet<String>();
		//需要唯一指定办理人
		if("true".equals(selectOne)){
//			  <select-type>autoType</select-type>
//	          <select-bean>workflowInstanceManager</select-bean>
				//只有一个候选人
				if(conditions.get(TaskTransactorCondition.SELECT_TYPE).equals(TaskTransactorCondition.SELECT_TYPE_CUSTOM)){
//					if(results.size() == 1){
//						latest.add(results.iterator().next());
//					}else if(results.size() > 1){
						latest.add(CommonStrings.TRANSACTOR_SINGLE);
						//将候选人加入到execution变量中
						execution.createVariable(CommonStrings.TRANSACTOR_SINGLE_CANDIDATES , results);
//					}
				}else{
					WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
					WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(execution.getProcessInstance().getProcessInstance().getId());
					if(instance==null){throw new RuntimeException("办理人设置中，解析附加条件时，流程实例不能为null");}
					String selectorBeanName = conditions.get(TaskTransactorCondition.SELECT_BEAN);
					ActivityExecution activityExecution = (ActivityExecution)execution;
					String activityName = activityExecution.getActivityName();
					if(StringUtils.isEmpty(selectorBeanName))throw new WorkflowException("环节："+activityName+"没有指定自动选择办理人的bean名");
					SingleTransactorSelector selector = (SingleTransactorSelector)ContextUtils.getBean(selectorBeanName)	;
					boolean moreTransactor = DefinitionXmlParse.hasMoreTransactor(
			    			instance.getProcessDefinitionId(), activityName);
					Set<String> result=selector.filter(instance.getDataId(), results,moreTransactor);
					Iterator<String> it=result.iterator();
					while(it.hasNext()){
						latest.add(it.next());
					}
				}
		}else{
			for(String user : results){
				latest.add(user);
			}
		}
		return latest;
	}
}
