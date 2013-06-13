package com.norteksoft.wf.engine.core;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.jbpm.api.listener.EventListener;
import org.jbpm.api.listener.EventListenerExecution;
import org.jbpm.api.model.OpenExecution;
import org.jbpm.internal.log.Log;
import org.jbpm.pvm.internal.model.ExecutionImpl;
import org.springframework.util.Assert;

import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.mms.form.dao.GeneralDao;
import com.norteksoft.mms.form.entity.AutomaticallyFilledField;
import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.jdbc.JdbcSupport;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.base.enumeration.CommonStrings;
import com.norteksoft.wf.base.utils.WebUtil;
import com.norteksoft.wf.engine.client.OnExecutingTransation;
import com.norteksoft.wf.engine.core.impl.UserParseCalculator;
import com.norteksoft.wf.engine.entity.InstanceHistory;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.service.InstanceHistoryManager;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;

import edu.emory.mathcs.backport.java.util.Arrays;

public class GeneralTransitionListener implements EventListener{

	private static final long serialVersionUID = 1L;
	
	private static final Log log = Log.getLog(GeneralTransitionListener.class.getName());
	
	private String transtionName ;
	private String processDefinitionId;
	private String processId;
	private String creator;
	private String processAdmin;
	private String previousTransactor;//上一环节办理人  
	
	private static final String EXCUTED_MSG = "流程执行了";
	private static final String SQUARE_BRACKETS_LEFT = "[";
	private static final String SQUARE_BRACKETS_RIGHT = "]";
	
	public void notify(EventListenerExecution execution) throws Exception {
		 FormViewManager formManager = (FormViewManager)ContextUtils.getBean("formViewManager");
		 WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
		WorkflowDefinitionManager workflowDefinitionManager = (WorkflowDefinitionManager)ContextUtils.getBean("workflowDefinitionManager");
		 GeneralDao generalDao = (GeneralDao)ContextUtils.getBean("generalDao");
		log.info("流向监听（GeneralTransitionListener）被调用");
		ExecutionImpl activityExecution = (ExecutionImpl)execution;
		transtionName = activityExecution.getTransition().getName();
		log.info("当前流向名：" + transtionName);
		WorkflowDefinition workflowDefinition = workflowDefinitionManager.getWorkflowDefinitionByProcessId(execution.getProcessDefinitionId());
		log.info("流程定义:" + workflowDefinition );
		processAdmin = workflowDefinition.getAdminLoginName();
		log.info("流程管理员:" + processAdmin );
		getVariables(execution);
		String transitionName=((ExecutionImpl)execution).getTransition().getName();
		execution.removeVariable(CommonStrings.IS_ORIGINAL_USER);
		execution.createVariable(CommonStrings.IS_ORIGINAL_USER, DefinitionXmlParse.getTransitionOriginalUser(((ExecutionImpl)execution).getProcessDefinitionId(),transitionName));
		log.info("开始获得业务bean...");
		
		
		processId = execution.getProcessInstance().getId();
		processDefinitionId = execution.getProcessDefinitionId();
		log.info("processId:" + processId);
		WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(processId);
		log.info("The workflowInstance what find by  processId : " + wi);
		
		//流向自动填写域
		FormView form = formManager.getFormView(wi.getFormId());
		log.info("查询的 form: " + form);
		if(wi.getDataId() != null){
			log.info("dataId: " + wi.getDataId());
			String changeStatus = DefinitionXmlParse.getChangeStatus(processDefinitionId,transtionName);
			log.info("需要改变的状态" + changeStatus);
			if(form.isStandardForm()){
				log.info("表单类型为标准表单");
				Object entity = generalDao.getObject(form.getDataTable().getEntityName(), wi.getDataId());
				log.info("可流转表单实例:" + entity );
				try {
					log.info("开始改变状态...");
					if(StringUtils.isNotEmpty(changeStatus)){
						 BeanUtils.setProperty(entity, "workflowInfo.state", changeStatus);
						 wi.setCurrentCustomState(changeStatus);
					}
					
					
					log.info("开始自动填写...");
					entity = saveAutomaticallyFilledFieldEntity(wi,entity);
					log.info("开始保存实体...");
					generalDao.save(entity);
					log.info("状态改变成功");
				} catch (Exception e) {
					throw new RuntimeException(e);
				} 
			}else if(!form.isStandardForm()){
				log.info("表单类型为自定义表单");
				log.info("开始改变状态...");
				if(StringUtils.isNotEmpty(changeStatus))wi.setCurrentCustomState(changeStatus);
				log.info("开始自动填写...");
				Map<String,String[]> automaticallyFilledFieldMap  = getAutomaticallyFilledFields(wi);
				log.info("需要自动填写的字段有" + automaticallyFilledFieldMap.toString());
				if(!automaticallyFilledFieldMap.isEmpty()) formManager.saveFormContentToTable(automaticallyFilledFieldMap,wi.getFormId(),wi.getDataId());
				log.info("状态改变成功");
			}
			workflowInstanceManager.saveWorkflowInstance(wi);
		}
		//流向事件处理
		executeFlowing(wi,transtionName);
		inform();
		generateHistory();
	}
	private Long getCompanyId(){
		return ContextUtils.getCompanyId();
	}
	/*
	 * 生成流转历史
	 */
	private void generateHistory(){
		 InstanceHistoryManager instanceHistoryManager = (InstanceHistoryManager)ContextUtils.getBean("instanceHistoryManager");
		log.info("生成流转历史");
		StringBuilder history = new StringBuilder();
		history.append(EXCUTED_MSG).append(SQUARE_BRACKETS_LEFT).append(transtionName).append(SQUARE_BRACKETS_RIGHT);
		InstanceHistory decisionHistory =  new InstanceHistory(getCompanyId(), processId, InstanceHistory.TYPE_AUTO,history.toString(),transtionName);
		instanceHistoryManager.saveHistory(decisionHistory);
		log.info("流转历史生成结束。");
	}
	
	
	/*
	 * 通知
	 */
	@SuppressWarnings("unchecked")
	private void inform(){
		WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
		WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(processId);
		if(DefinitionXmlParse.transitionInform(processDefinitionId, transtionName)){
			log.info("该流向有需要通知的用户");
			String informType=DefinitionXmlParse.getTransitionInformType(processDefinitionId,transtionName);
			String[] types=informType.split(",");
			List<String> list = Arrays.asList(types);
			if(list.contains(CommonStrings.EMAIL_STYLE)){
				Set<String> emails = getEmailsInformCondition(wi);//根据users得到所有结束通知的email地址
				log.info("需要通知的用户email地址有：" + emails.toString());
				DefinitionXmlParse.transitionInformMail(processDefinitionId,transtionName,emails);
			}
			if(list.contains(CommonStrings.RTX_STYLE)){
				String loginNames = getLoginNameInformCondition(wi);//根据users得到得到登录名
				log.info("需要通知的用户登录名有：" + loginNames);
				DefinitionXmlParse.transitionInformRTX(processDefinitionId,transtionName,loginNames);
			}
			if(list.contains(CommonStrings.SWING_STYLE)){
				String loginNames = getLoginNameInformCondition(wi);//根据users得到得到登录名
				log.info("需要通知的用户登录名有：" + loginNames);
				DefinitionXmlParse.transitionInformSwing(processDefinitionId,transtionName,loginNames,wi);
			}
		}
	}
	
	/*
	 * 解析通知条件获得需要通知用户的邮件地址
	 */
	private Set<String> getEmailsInformCondition(WorkflowInstance wi){
		String condition = DefinitionXmlParse.getNeedInformUserCondition(processDefinitionId, transtionName);
		log.info("根据流程定义文件和流向名解析得到的需要通知的用户条件为：" + condition);
		UserParseCalculator upc = WebUtil.getUserParseInfor(wi.getProcessInstanceId(),creator,processAdmin);
		
		return WebUtil.getEmailsInformCondition(condition, wi.getSystemId(), wi.getCompanyId(),upc);
	}
	
	/*
	 * 解析通知条件获得需要通知用户的登录名
	 */
	private String getLoginNameInformCondition(WorkflowInstance wi){
		String condition = DefinitionXmlParse.getNeedInformUserCondition(processDefinitionId,transtionName);
		log.info("根据流程定义文件得到流程结束时需要通知的用户条件为：" + condition);
		
		UserParseCalculator upc = WebUtil.getUserParseInfor(wi.getProcessInstanceId(),creator,processAdmin);
		return WebUtil.getLoginNameInformCondition(condition, wi.getSystemId(), wi.getCompanyId(),upc);
	}
	/*
	 * 得到需字段填写的字段(自定义表单)
	 */
	private Map<String,String[]> getAutomaticallyFilledFields(WorkflowInstance wi){
		Map<String,String[]> automaticallyFilledFieldMap = new HashMap<String,String[]>();
		List<AutomaticallyFilledField> autoFilledFields =DefinitionXmlParse.getFlowingFilledFields(wi.getProcessDefinitionId(),transtionName);
			for(AutomaticallyFilledField aff : autoFilledFields){
				String value = getAutoFilledFieldValue(wi,aff);
					automaticallyFilledFieldMap.put(aff.getName(),new String[]{value });
			}
		return automaticallyFilledFieldMap;
	}
	
	/*
	 * 返回自动填写域表示的值
	 * @param condition
	 * @param currentOperation
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private  String getAutoFilledFieldValue(WorkflowInstance wi,AutomaticallyFilledField aff){
		 FormViewManager formManager = (FormViewManager)ContextUtils.getBean("formViewManager");
		log.debug("*** getAutoFilledFieldValue 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("wi:").append(wi)
			.append(", aff:").append(aff)
			.append("]").toString());
		
		FormView form = formManager.getFormView(wi.getFormId());
		Map map = formManager.getDataMap(form.getDataTable().getName(), wi.getDataId());
		List<FormControl> fieldsList =formManager.getControls(wi.getFormId());
		FormControl field = getFieldbyName(fieldsList,aff.getName() );
		String value ;
		if(field.getDataType()==DataType.TIME){
			value = getFormatCurrentTime(aff,DataType.TIME);
		}else if(field.getDataType()==DataType.DATE){
			value = getFormatCurrentTime(aff,DataType.DATE);
		}else if(field.getDataType()==DataType.TEXT||field.getDataType()==DataType.CLOB){
			value = getValue(aff);
			if(aff.getFillType().equals(AutomaticallyFilledField.AUTO_FILLED_FILL_TYPE_ADDITIONAL)){//追加
				String originalValue = (map.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+aff.getName())==null?"":map.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+aff.getName()))+"";
				if(StringUtils.isNotEmpty(value)&&StringUtils.isNotEmpty(originalValue)){//如果配的值不为空且数据库中的值也不为空时，则其值为追加后的值
					value = originalValue+","+value;
				}else if(StringUtils.isEmpty(value)&&StringUtils.isNotEmpty(originalValue)){//如果配的值为空且数据库的值不为空时，则其值为数据库中的值不作修改
					value= originalValue;
				}
			}else if(aff.getFillType().equals(AutomaticallyFilledField.AUTO_FILLED_FILL_TYPE_ADDED_TO_THE_BEGINNING)){//添加
				String originalValue = (map.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+aff.getName())==null?"":map.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+aff.getName()))+"";
				if(StringUtils.isNotEmpty(value)&&StringUtils.isNotEmpty(originalValue)){
					value = value + ","+originalValue;
				}else if(StringUtils.isEmpty(value)&&StringUtils.isNotEmpty(originalValue)){
					value= originalValue;
				}
			}
			
		}else{
			value = aff.getValue();
		}
		
		log.debug("*** getAutoFilledFieldValue 方法结束");
		return value;
	}
	
	/*
	 *如果字段类型是text 调用该方法来解析要填写的值 
	 * @param aff
	 * @param taskTransactor
	 * @param currentOperation
	 * @return
	 */
	
	private String getFormatCurrentTime(AutomaticallyFilledField aff,DataType dataType){
		String format ;
		switch(dataType){
		  	case TIME: format = "yyyy-MM-dd HH:mm" ;break;
		  	case DATE: format = "yyyy-MM-dd";break;
		  	default: return "";
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return StringUtils.contains(aff.getValue(), CommonStrings.CURRENTTIME) ? simpleDateFormat.format(new Date()) :"";
	}
	
	/*
	 *自动填写 
	 */
	private Object saveAutomaticallyFilledFieldEntity(WorkflowInstance wi,Object entity) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		log.info("开始自动填写...");
		List<AutomaticallyFilledField> autoFilledFields =DefinitionXmlParse.getFlowingFilledFields(wi.getProcessDefinitionId(),transtionName);
		log.info("需自动填写的字段个数：" + autoFilledFields.size());
		for(AutomaticallyFilledField aff : autoFilledFields){
			log.info("自动填写字段：" + aff);
			Object value = getValueEntity(wi, entity,aff);
			log.info("解析后的值为:" + value);
			PropertyUtils.setProperty(entity, aff.getName(), value);
		}
		 log.info("属性设置完毕");
		 return entity;
	}
	
	/*
	 * 实体类型的自动填写值
	 */
	private Object getValueEntity(WorkflowInstance wi,Object entity,AutomaticallyFilledField aff) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		 FormViewManager formManager = (FormViewManager)ContextUtils.getBean("formViewManager");
		List<FormControl> fieldsList = formManager.getControls(wi.getFormId());
		FormControl field = getFieldbyName(fieldsList,aff.getName() );
		Object value ;
		if(field.getDataType()==DataType.TIME || field.getDataType()==DataType.DATE){
			value = StringUtils.contains(aff.getValue(), CommonStrings.CURRENTTIME) ? new Date() :null;
		}else if(field.getDataType()==DataType.TEXT||field.getDataType()==DataType.CLOB){
			value = getValue(aff);
			if(aff.getFillType().equals(AutomaticallyFilledField.AUTO_FILLED_FILL_TYPE_ADDITIONAL)){
				String originalValue = BeanUtils.getProperty(entity, aff.getName());
				if(value!=null&&StringUtils.isNotEmpty(originalValue)){//如果配的值不为空且数据库中的值也不为空时，则其值为追加后的值
					value = originalValue+","+value;
				}else if(value==null&&StringUtils.isNotEmpty(originalValue)){//如果配的值为空且数据库的值不为空时，则其值为数据库中的值不作修改
					value= originalValue;
				}
			}else if(aff.getFillType().equals(AutomaticallyFilledField.AUTO_FILLED_FILL_TYPE_ADDED_TO_THE_BEGINNING)){
				String originalValue = BeanUtils.getProperty(entity, aff.getName());
				if(value!=null&&StringUtils.isNotEmpty(originalValue)){
					value = value + ","+originalValue;
				}else if(value==null&&StringUtils.isNotEmpty(originalValue)){
					value= originalValue;
				}
			}
		}else if(field.getDataType()==DataType.NUMBER){
			value = Integer.valueOf(aff.getValue());
		}else if(field.getDataType()==DataType.AMOUNT){
			value = Float.valueOf(aff.getValue());
		}else if(field.getDataType()==DataType.INTEGER){
			value = Integer.valueOf(aff.getValue());
		}else if(field.getDataType()==DataType.LONG){
			value = Long.valueOf(aff.getValue());
		}else if(field.getDataType()==DataType.DOUBLE||field.getDataType()==DataType.FLOAT){
			value = Float.valueOf(aff.getValue());
		}else if(field.getDataType()==DataType.BOOLEAN){
			value = Boolean.parseBoolean(aff.getValue());
		}else{
			value = aff.getValue();
		}
		return value;
	}
	
	/*
	 *如果字段类型是text 调用该方法来解析要填写的值 
	 * @param aff
	 * @param taskTransactor
	 * @param currentOperation
	 * @return
	 */
	private String getValue(AutomaticallyFilledField aff){
		StringBuilder builder = new StringBuilder();
		String[] strs = null;
		String condition = aff.getValue();
		if(condition.indexOf('+')==-1){
			strs = new String[]{condition};
		}else{
			strs = condition.split("\\+");
		}
		for(int i=0;i<strs.length;i++){
			if(i!=0) builder.append(aff.getSeparate());
			if(CommonStrings.PREVIOUS_TRANSACTOR.equals(strs[i])){
				builder.append(previousTransactor);
			}else if(CommonStrings.PREVIOUS_TRANSACTOR_NAME.equals(strs[i])){
				com.norteksoft.product.api.entity.User user = ApiFactory.getAcsService().getUserByLoginName(previousTransactor);
				Assert.notNull(user,"公司id为"+ContextUtils.getCompanyId()+"的公司中，用户名为"+previousTransactor+"为空。");
				builder.append(user.getName());
			}else if(CommonStrings.CURRENTTIME.equals(strs[i])){
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				builder.append(simpleDateFormat.format(new Date()));
			}else{
				builder.append(strs[i]);
			}
		}
		return builder.toString();
	}
	
	/*
	 * 从List中取出英文名为enName的Field
	 */
	private FormControl getFieldbyName(List<FormControl> fields , String enName){
		for(FormControl field:fields){
			if(field.getName().equals(enName)) return field;
		}
		return null;
	}

	/*
	 * 流向流过时执行
	 */
	private void executeFlowing(WorkflowInstance instance, String activityName){
		log.info("开始获取流转时执行的beanName");
		String beanName = DefinitionXmlParse.getFlowingExecuteBean(
				instance.getProcessDefinitionId(), activityName);
		log.info("beanName:" + beanName);
		if(!StringUtils.isEmpty(beanName)){
			if(beanName.indexOf(",")!=-1){
				String[] beans=beanName.split(",");
				for(String bean:beans){
					executeBean(bean,instance);
				}
			}else{
				executeBean(beanName,instance);
			}
		}
	}
	
	private void executeBean(String beanName,WorkflowInstance instance){
		log.info("根据beanName获取bean");
		OnExecutingTransation bean = (OnExecutingTransation) ContextUtils.getBean(beanName);
		log.info("bean:" + bean);
		log.info("开始调用bean");
		bean.execute(instance.getDataId());
		log.info("bean调用结束");
	}
	
	/*
	 *获得变量 
	 */
	private void getVariables(OpenExecution execution){
		
		log.info("开始获取变量");
		creator = execution.getVariable("creator").toString();
		log.info("文档创建人:" + creator );
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
		log.info("从变量中获取上一环节办理人为：" + previousTransactor);
	}
}
