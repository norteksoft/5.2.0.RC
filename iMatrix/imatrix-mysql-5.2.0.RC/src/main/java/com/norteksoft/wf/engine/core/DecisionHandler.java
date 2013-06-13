package com.norteksoft.wf.engine.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.jbpm.api.activity.ActivityExecution;
import org.jbpm.api.model.OpenExecution;
import org.jbpm.internal.log.Log;

import com.norteksoft.mms.form.dao.GeneralDao;
import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.jdbc.JdbcSupport;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.wf.base.enumeration.CommonStrings;
import com.norteksoft.wf.base.enumeration.LogicOperator;
import com.norteksoft.wf.base.exception.DecisionException;
import com.norteksoft.wf.base.utils.BeanShellUtil;
import com.norteksoft.wf.engine.core.impl.UserParseCalculator;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;

import edu.emory.mathcs.backport.java.util.Arrays;


/**
 * 根据transition的条件，决定执行Decision的transition
 */
public class DecisionHandler implements org.jbpm.api.jpdl.DecisionHandler {

	private static final long serialVersionUID = 1L;
	
	private static final Log log = Log.getLog(DecisionHandler.class.getName());
	private static String LOGMESSAGE_INVOKING_METHOD = " invoking method: ";
	private static String LOGMESSAGE_METHOD_OVER = " method over. ";
	private static String LOGMESSAGE_METHOD_PARAMETER = " method parameter: ";
	private static String LOGMESSAGE_METHOD_RESULT = " method result: ";
	
	private static final String SQUARE_BRACKETS_LEFT = "[";
	private static final String SQUARE_BRACKETS_RIGHT = "]";
	private static final String AND = "&&";
	private static final String OR = "||";
	
	
	private FormViewManager formManager;
	private TaskService taskService;
	private WorkflowInstanceManager workflowInstanceManager;
	private GeneralDao generalDao ;
	private WorkflowInstance wi;
	private String previousTransactor;
	private String currentTransactor;
	private String approvalResult;
	private String processId;
	private String preTaskName;//上一环节任务名
	private String activityName;
	private String transitionName;
	private Long dataId;
	private FormView form;
	private List<FormControl> fields;
	private Long favorCount,againstCount,abstentionCount,countersignatureAgreeCount,countersignatureDisagreeCount;
	
	public String decide(OpenExecution execution) {
		log.info("开始处理判断环节...");
		log.info("开始初始化数据");
		init(execution);
		log.info("初始化数据结束.");
		
		if(StringUtils.isNotEmpty(transitionName)) return transitionName;
		
		//获得该决断下的所有流向节点的扩展条件
		List<String> conditions = DefinitionXmlParse.getDecisionConditions(execution.getProcessDefinitionId(),activityName);
		log.info("该判断环节的所有流向条件：" + conditions.toString());
		List<String[]> transitionNames = new ArrayList<String[]>();//存储流向条件为真的流向名
		List<String[]> allTransitionNames = new ArrayList<String[]>();//存放该判断环节所有的流向的流向名
		for (int i=0;i<conditions.size();i++) {
			String[] transitionSetting  = DefinitionXmlParse.getDecisionTransition(execution.getProcessDefinitionId(),activityName,i+1);
			if (parseExpression(conditions.get(i))) {
				//如果条件符合，则得到该transition，并返回它的名字
				transitionNames.add(transitionSetting);
			}
			allTransitionNames.add(transitionSetting);
		}
		if(transitionNames.size()==1){
			return transitionNames.get(0)[0];
		}else if(transitionNames.size()>1){
			throw new DecisionException(DecisionException.MORE_TRANSITION,transitionNames);
		}else if(transitionNames.size()==0){
			throw new DecisionException(DecisionException.NO_TRANSITION,allTransitionNames);		
		}
		return null;
	}
	
	
	public  Boolean parseExpression(String express){
		log.info(LOGMESSAGE_INVOKING_METHOD + "Boolean parseExpression(String express)");
		log.info(LOGMESSAGE_METHOD_PARAMETER + "String express" + express);
		if(StringUtils.isEmpty(express)) return false;
		
		String temp = express;
		String[] strs = BeanShellUtil.splitExpression(express);
		log.info("分割后的原子表达式为：" + Arrays.toString(strs));
		
		UserParseCalculator upc = new UserParseCalculator();
		upc.setDataId(wi.getDataId());
		upc.setFormView(form);
		upc.setDocumentCreator(wi.getCreator());
		upc.setCurrentTransactor(currentTransactor);
		upc.setPreviousTransactor(previousTransactor);
		Boolean result = false;
		for(int i=0;i<strs.length;i++){
			log.info("开始分析原子表达式：" + strs[i]);
			result = computeAtomicExpression(strs[i],upc);
			log.info("原子表达式：" + strs[i] + "的分析结果为 " + result);
			temp = StringUtils.replace(temp, strs[i].trim(), result.toString());
			log.info("将原子表达式替换为它的结果后：" + temp );
		}
		temp = temp.replaceAll(LogicOperator.AND.getCode(), AND);
		temp = temp.replaceAll(LogicOperator.OR.getCode(), OR);
		log.info("最终该流向的表达式为：" + temp);
		boolean expressResult = BeanShellUtil.evel(temp);
		log.info(LOGMESSAGE_METHOD_RESULT + expressResult);
		return expressResult;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	private boolean computeAtomicExpression(String atomicExpress,UserParseCalculator upc){
		log.info(LOGMESSAGE_INVOKING_METHOD + "boolean computeAtomicExpression(String atomicExpress)");
		log.info(LOGMESSAGE_METHOD_PARAMETER + "String atomicExpress:" + atomicExpress);
		if(StringUtils.isEmpty(atomicExpress)) return false;
		boolean result = false;
		atomicExpress = atomicExpress.trim();
		if(StringUtils.contains(atomicExpress, CommonStrings.DOCUMENT_CREATOR_NAME)
			||StringUtils.contains(atomicExpress, CommonStrings.DOCUMENT_CREATOR_ROLE)
			||StringUtils.contains(atomicExpress, CommonStrings.DOCUMENT_CREATOR_DEPARTMENT)
			||StringUtils.contains(atomicExpress, CommonStrings.DOCUMENT_SUPERIOR_DEPARTMENT)
			||StringUtils.contains(atomicExpress, CommonStrings.DOCUMENT_CREATOR_UPSTAGE_DEPARTMENT)
			||StringUtils.contains(atomicExpress, CommonStrings.DOCUMENT_CREATOR_WORKGROUP)
			||StringUtils.contains(atomicExpress, CommonStrings.DOCUMENT_CREATOR_DIRECT_SUPERIOR_DEPARTMENT)
			||StringUtils.contains(atomicExpress, CommonStrings.DOCUMENT_CREATOR_DIRECT_SUPERIOR_NAME)
			||StringUtils.contains(atomicExpress, CommonStrings.DOCUMENT_CREATOR_DIRECT_SUPERIOR_ROLE)
			||StringUtils.contains(atomicExpress, CommonStrings.DOCUMENT_CREATOR_DIRECT_SUPERIOR_WORKGROUP)
			||StringUtils.contains(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_NAME)
			||StringUtils.contains(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_ROLE)
			||StringUtils.contains(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_DEPARTMENT)
			||StringUtils.contains(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_SUPERIOR_DEPARTMENT)
			||StringUtils.contains(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_UPSTAGE_DEPARTMENT)
			||StringUtils.contains(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_WORKGROUP)
			||StringUtils.contains(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_DIRECT_SUPERIOR_DEPARTMENT)
			||StringUtils.contains(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_DIRECT_SUPERIOR_NAME)
			||StringUtils.contains(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_DIRECT_SUPERIOR_ROLE)
			||StringUtils.contains(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_DIRECT_SUPERIOR_WORKGROUP)
			||StringUtils.contains(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_NAME)
			||StringUtils.contains(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_ROLE)
			||StringUtils.contains(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_DEPARTMENT)
			||StringUtils.contains(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_SUPERIOR_DEPARTMENT)
			||StringUtils.contains(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_UPSTAGE_DEPARTMENT)
			||StringUtils.contains(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_WORKGROUP)
			||StringUtils.contains(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_DEPARTMENT)
			||StringUtils.contains(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_NAME)
			||StringUtils.contains(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_ROLE)
			||StringUtils.contains(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_WORKGROUP)){
			result = upc.execute(atomicExpress);
		}else if(StringUtils.contains(atomicExpress, CommonStrings.FAVOR_COUNT)){
			log.info("对赞成票数判断");
			log.info("赞成票数:" + favorCount);
			result = BeanShellUtil.execute(atomicExpress,DataType.LONG,CommonStrings.FAVOR_COUNT,favorCount.toString());
			log.info("判断结果为：" + result);
		}else if(StringUtils.contains(atomicExpress, CommonStrings.AGAINST_COUNT)){
			log.info("反对票数"+ againstCount);
			result = BeanShellUtil.execute(atomicExpress,DataType.LONG,CommonStrings.AGAINST_COUNT,againstCount.toString());
			log.info("判断结果为：" + result);
		}else if(StringUtils.contains(atomicExpress, CommonStrings.ABSTENTION_COUNT)){
			log.info("弃权票数：" + abstentionCount);
			result = BeanShellUtil.execute(atomicExpress,DataType.LONG,CommonStrings.ABSTENTION_COUNT,abstentionCount.toString());
			log.info("判断结果为：" + result);
		}else if(StringUtils.contains(atomicExpress, CommonStrings.FAVOR_PERCENTAGE)){
			Double percentage = (favorCount+againstCount+abstentionCount)==0?0.0:Double.valueOf(favorCount)/Double.valueOf(favorCount+againstCount+abstentionCount)*100;
			log.info("赞成票百分比:" + percentage);
			result = BeanShellUtil.execute(atomicExpress,DataType.DOUBLE,CommonStrings.FAVOR_PERCENTAGE,percentage.toString());
			log.info("判断结果为：" + result);
		}else if(StringUtils.contains(atomicExpress, CommonStrings.AGAINST_PERCENTAGE)){
			Double percentage = (favorCount+againstCount+abstentionCount)==0?0.0:Double.valueOf(againstCount)/Double.valueOf(favorCount+againstCount+abstentionCount)*100;
			log.info("反对票百分比：" + percentage);
			result = BeanShellUtil.execute(atomicExpress,DataType.DOUBLE,CommonStrings.AGAINST_PERCENTAGE,percentage.toString());
			log.info("判断结果为：" + result);
		}else if(StringUtils.contains(atomicExpress, CommonStrings.ABSTENTION_PERCENTAGE)){
			Double percentage = (favorCount+againstCount+abstentionCount)==0?0.0:Double.valueOf(abstentionCount)/Double.valueOf(favorCount+againstCount+abstentionCount)*100;
			log.info("弃权票百分比：" + percentage);
			result = BeanShellUtil.execute(atomicExpress,DataType.DOUBLE,CommonStrings.ABSTENTION_PERCENTAGE,percentage.toString());
			log.info("判断结果为：" + result);
		}else if(StringUtils.contains(atomicExpress, CommonStrings.COUNTERSIGNATURE_AGREE_PERCENTAGE)){
			Double percentage = (countersignatureAgreeCount+countersignatureDisagreeCount)==0?0.0:Double.valueOf(countersignatureAgreeCount)/Double.valueOf(countersignatureAgreeCount+countersignatureDisagreeCount)*100;
			log.info("会签同意百分比：" + percentage);
			result = BeanShellUtil.execute(atomicExpress,DataType.DOUBLE,CommonStrings.COUNTERSIGNATURE_AGREE_PERCENTAGE,percentage.toString());
			log.info("判断结果为：" + result);
		}else if(StringUtils.contains(atomicExpress, CommonStrings.COUNTERSIGNATURE_DISAGREE_PERCENTAGE)){
			Double percentage = (countersignatureAgreeCount+countersignatureDisagreeCount)==0?0.0:Double.valueOf(countersignatureDisagreeCount)/Double.valueOf(countersignatureAgreeCount+countersignatureDisagreeCount)*100;
			log.info("会签不同意百分比：" + percentage);
			result = BeanShellUtil.execute(atomicExpress,DataType.DOUBLE,CommonStrings.COUNTERSIGNATURE_DISAGREE_PERCENTAGE,percentage.toString());
			log.info("判断结果为：" + result);
		}else if(StringUtils.contains(atomicExpress, CommonStrings.COUNTERSIGNATURE_AGREE_COUNT)){
			log.info("会签同意数：" + countersignatureAgreeCount);
			result = BeanShellUtil.execute(atomicExpress,DataType.LONG,CommonStrings.COUNTERSIGNATURE_AGREE_COUNT,countersignatureAgreeCount.toString());
			log.info("判断结果为：" + result);
		}else if(StringUtils.contains(atomicExpress, CommonStrings.COUNTERSIGNATURE_DISAGREE_COUNT)){
			log.info("会签不同意数：" + countersignatureDisagreeCount);
			result = BeanShellUtil.execute(atomicExpress,DataType.LONG,CommonStrings.COUNTERSIGNATURE_DISAGREE_COUNT,countersignatureDisagreeCount.toString());
			log.info("判断结果为：" + result);
		}else if(StringUtils.contains(atomicExpress, CommonStrings.APPROVAL_RESULT)){
			log.info("办理结果：" + approvalResult);
			result = BeanShellUtil.execute(atomicExpress,DataType.TEXT,CommonStrings.APPROVAL_RESULT,approvalResult);
			log.info("判断结果为：" + result);
		}else{
			if(form.isStandardForm()){
				log.info("标准表单");
				//标准表单的处理
				
				//根据表单id获得对应的类
				if(form.getDataTable()==null){throw new RuntimeException("条件判断监听中，表单对应的数据表不能为null");}
				String className = form.getDataTable().getEntityName();
				if(className==null){throw new RuntimeException("条件判断监听中，表单对应的数据表的实体类名不能为null");}
				log.info("实体类名：" + className);
				//根据表名和id获得实体
				Object entity = generalDao.getObject(className, dataId);
				log.info("查询得到的实体:" + entity);
				String name = StringUtils.substringBetween(atomicExpress, SQUARE_BRACKETS_LEFT, SQUARE_BRACKETS_RIGHT);
				log.info("字段名：" + name);
				FormControl field = getFormControl(name);
				log.info("对应字段为：" + field);
				try {
					Object value = BeanUtils.getProperty(entity, name);
					if(value!=null){
						log.info("自动对应的值" + value.toString());
						result = BeanShellUtil.execute(atomicExpress,field.getDataType(),field.getTitle()+SQUARE_BRACKETS_LEFT+name+SQUARE_BRACKETS_RIGHT,value.toString());
					}
					log.info("判断结果为：" + result);
				} catch (Exception e) {
					throw new RuntimeException(e);
				} 
				
			}else if(!form.isStandardForm()){
				log.info("自定义表单处理");
			//自定义表单的处理
				//根据表单id获得对应的表名
				String tableName = form.getDataTable().getName();
				log.info("表名：" + tableName);
				log.info("数据ID：" + dataId);
				//根据表名和id获得对应记录数据封装的MAP
				Map dataMap = formManager.getDataMap(tableName,dataId);
				log.info("数据map：" + dataMap);
				String ch_name = StringUtils.substringBefore(atomicExpress, SQUARE_BRACKETS_LEFT);
				String name =StringUtils.substringBetween(atomicExpress, SQUARE_BRACKETS_LEFT, SQUARE_BRACKETS_RIGHT);
				FormControl field = getFormControl(name);
				String value = "";
				if(dataMap.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+name)==null){
						if(field.getDataType()==DataType.AMOUNT||field.getDataType()==DataType.NUMBER){
							value = "0";
						}else if(field.getDataType().equals(DataType.DATE.toString())||field.getDataType().equals(DataType.TIME.toString())){
							if(dataMap.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+name)==null) throw new RuntimeException("Field:"+ch_name+" no value.");
						}
				}else{
					value = dataMap.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+name).toString();
				}
				log.info("对应值为：" + value);
				result = BeanShellUtil.execute(atomicExpress,field.getDataType(),ch_name + SQUARE_BRACKETS_LEFT+name+SQUARE_BRACKETS_RIGHT,value);
				log.info("判断结果为：" + result);
			}
		}
		return result;
	}
	
	
	
	private FormControl getFormControl(String name){
		for(FormControl formControl : fields){
			if(formControl.getName().equals(name)) return formControl;
		}
		return null;
	}
	

	/*
	 *获得变量 
	 */
	private void getVariables(OpenExecution execution){
		log.info(LOGMESSAGE_INVOKING_METHOD + "getVariables(OpenExecution execution)");
		
		
		
		//变量中取上一环节办理人
		Object obj = execution.getVariable(CommonStrings.PREVIOUS_TASK_PRINCI_TRANSACTOR);
		if(obj==null){//上一环节办理人委托人为空，取办理人
			obj = execution.getVariable(CommonStrings.PREVIOUS_TASK_TRANSACTOR);
			if(obj!=null){
				previousTransactor = obj.toString();
			}
		}else{//上一环节办理人委托人即真实办理人
			previousTransactor = obj.toString();
		}
		//取出流向名，并删除该变量中的值
		obj = execution.getVariable(CommonStrings.TRANSITION_NAME);
		if(obj!=null){
			transitionName = obj.toString();
			execution.removeVariable(CommonStrings.TRANSITION_NAME);
		}else{
			transitionName = null;
		}
		log.info("从变量中获取上一环节办理人为：" + previousTransactor);
		//变量中取上一环节办理结果
		obj = execution.getVariable(CommonStrings.CURRENT_OPERATTION_STRING);
		if(obj!=null){
			approvalResult = obj.toString();
			if(approvalResult.equals(TaskProcessingResult.APPROVE.toString())){
				approvalResult = TaskProcessingResult.APPROVE.getName();
			}else if(approvalResult.equals(TaskProcessingResult.REFUSE.toString())){
				approvalResult = TaskProcessingResult.REFUSE.getName();
			}
		}
		log.info("从变量中获取上一环节办理结果：" + approvalResult);
		
		//变量中取上一环节名字
		obj = execution.getVariable(CommonStrings.PREVIOUS_TASK_NAME);
		if(obj!=null){
			preTaskName = obj.toString();
		}
		log.info("从变量中获取上一环节名字：" + preTaskName);
		
		Long companyId=ContextUtils.getCompanyId();
		if(companyId==null){
			Object compIdStr=execution.getVariable(CommonStrings.COMPANY_ID);
			if(compIdStr!=null){
				companyId=Long.parseLong(compIdStr.toString()); 
			}
		}
		//会签结果
		long[] countersignatureResults = taskService.getCountersignatureResult(processId,preTaskName,companyId);
		log.info("会签结果:" + Arrays.toString(countersignatureResults));
		if(countersignatureResults.length==2){
			countersignatureAgreeCount = countersignatureResults[0];
			countersignatureDisagreeCount = countersignatureResults[1];
		}
		log.info("同意数：" + countersignatureAgreeCount +";不同意数：" + countersignatureDisagreeCount);
		log.info(LOGMESSAGE_METHOD_OVER);
		//投票结果
		long[] voteresults = taskService.getVoteResults(processId,preTaskName,companyId);
		log.info("投票结果:" + Arrays.toString(voteresults));
		if(voteresults.length==3){
			favorCount = voteresults[0];
			againstCount = voteresults[1];
			abstentionCount = voteresults[2];
		}
		log.info("反对票："+ againstCount + ";赞成票 ：" + favorCount + ";弃权票:"+ abstentionCount);
		
	}
	
	/*
	 *初始化 
	 */
	private void init(OpenExecution execution){
		log.info(LOGMESSAGE_INVOKING_METHOD+"init(OpenExecution execution)");
		formManager = (FormViewManager)ContextUtils.getBean("formViewManager");
		workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
		taskService = (TaskService) ContextUtils.getBean("taskService");
		generalDao = (GeneralDao)ContextUtils.getBean("generalDao");
		
		currentTransactor = ContextUtils.getLoginName();
		log.info("当前办理人：" + currentTransactor);
		processId = execution.getProcessInstance().getId();
		log.info("processId:" + processId);
		wi = workflowInstanceManager.getWorkflowInstance(processId);
		log.info("find workflowInstance by processId("+processId+"). workflowInstance:"+wi);
		ActivityExecution activityExecution = (ActivityExecution)execution;
		activityName = activityExecution.getActivityName();
		log.info("当前环节名字：" + activityName);
		//获取变量
		log.info("开始获取变量。");
		getVariables(execution);
		log.info("变量获取完成");
		dataId = wi.getDataId();//
		log.info("数据id：" + dataId);
		form = formManager.getFormView(wi.getFormId());
		log.info("表单实体:" + form);
		fields = formManager.getControls(form.getId());
		log.info("表单的所有字段:" + fields);
		log.info(LOGMESSAGE_METHOD_OVER);
	}
}