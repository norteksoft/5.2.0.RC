package com.norteksoft.wf.base.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.AsyncMailUtils;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.base.enumeration.CommonStrings;
import com.norteksoft.wf.base.enumeration.TrustRecordState;
import com.norteksoft.wf.engine.entity.TrustRecord;
import com.norteksoft.wf.engine.service.DataDictionaryManager;
import com.norteksoft.wf.engine.service.DelegateMainManager;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;

@Service
@Transactional(readOnly=false)
public class TimerUtils {

	private Log log = LogFactory.getLog(DataDictionaryManager.class);
	private DelegateMainManager delegateMainManager;
	private WorkflowInstanceManager workflowInstanceManager;
	private TaskService taskService;
	private UserManager userManager;
	private AcsUtils acsUtils;
	private static final long  MILLI_SECOND = 24*60*60*1000;
	@Autowired
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}
	
	@Autowired
	public void setDelegateMainManager(DelegateMainManager delegateMainManager) {
		this.delegateMainManager = delegateMainManager;
	}
	
	@Autowired
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	@Autowired
	public void setWorkflowInstanceManager(
			WorkflowInstanceManager workflowInstanceManager) {
		this.workflowInstanceManager = workflowInstanceManager;
	}
	@Autowired
	public void setAcsUtils(AcsUtils acsUtils) {
		this.acsUtils = acsUtils;
	}
	
	@Transactional(readOnly=false)
	public void run() throws Exception{
		List<Company> companys=acsUtils.getAllCompanys();
		for(Company company:companys){
			ThreadParameters parameters=new ThreadParameters(company.getId());
			ParameterUtils.setParameters(parameters);
			String systemAdmin = ApiFactory.getAcsService().getSystemAdminLoginName();
			parameters=new ThreadParameters(company.getId());
			parameters.setUserName("系统");
			parameters.setLoginName(systemAdmin);
			ParameterUtils.setParameters(parameters);

			//委托
			delegateMain();
			
			//催办
			List<WorkflowTask> result=new ArrayList<WorkflowTask>();
			result.addAll(workflowInstanceManager.getNeedReminderTasksByInstance());
			result.addAll(taskService.getNeedReminderTasks());
			reminder(result);
			
		}
		deleteExportTempFile();
		
		//清空同步处理时的实例map,见TaskService中的completeInteractiveWorkflowTask方法
		TaskService.instanceIds.clear();
	}
	
	/**
	 * 删除导出的临时文件
	 */
	public void deleteExportTempFile(){
		String path =  PropUtils.getProp("excel.export.file.path");
		if(StringUtils.isNotEmpty(path)){
			File file = new File(path);
			if(file.isDirectory()){
				String[] tempList=file.list();
				File temp=null;
				for(String t:tempList){
					temp=new File(path+t);
					if(temp.isFile()){
						temp.delete();
					}
				}
			}
		}
	}
	@Transactional(readOnly=false)
	public void delegateMain(){
		try {
			//权限委托
			List<TrustRecord> delegateMains = delegateMainManager.getDelegateMainsOnAssign();
			for(TrustRecord dm:delegateMains){
				com.norteksoft.product.api.entity.User trustee=ApiFactory.getAcsService().getUserByLoginName(dm.getTrustee());//受托人
				com.norteksoft.product.api.entity.User trustor=ApiFactory.getAcsService().getUserByLoginName(dm.getTrustor());//委托人
				if(needEfficient(dm)){
					ApiFactory.getAcsService().assignTrustedRole(trustor.getId(), dm.getRoleIds().split(","), trustee.getId());
					dm.setState(TrustRecordState.EFFICIENT);
					delegateMainManager.saveDelegateMain(dm);
				}
				if(needEnd(dm)){
					ApiFactory.getAcsService().deleteTrustedRole(trustor.getId(), dm.getRoleIds().split(","),trustee.getId() );
					dm.setState(TrustRecordState.END);
					delegateMainManager.saveDelegateMain(dm);
				}
			}
			
			//流程委托
			List<TrustRecord> workflowDelegateMains = delegateMainManager.getAllStartWorkflowDelegateMain();
			for(TrustRecord wfdm : workflowDelegateMains){
				if(needEfficient(wfdm)){
					wfdm.setState(TrustRecordState.EFFICIENT);
					delegateMainManager.saveDelegateMain(wfdm);
				}
				if(needEnd(wfdm)){
					wfdm.setState(TrustRecordState.END);
					delegateMainManager.saveDelegateMain(wfdm);
					//委托结束时取回任务
					taskService.recieveDelegateTask(wfdm);
				}
			}
		} catch (Exception e) {
			log.error("定时委托异常："+e.getMessage());
		}
	}
	
	/*
	 * 判断是需要结束委托
	 * 当委托处于生效状态，并且当前日期大于或等于截至日期时就需要结束
	 */
	public boolean needEnd(TrustRecord dm){
		return (dm.getState()==TrustRecordState.EFFICIENT || dm.getState()==TrustRecordState.STARTED)
				&&dm.getEndTime().compareTo(new Date(System.currentTimeMillis()))<=0;
	}
	
	/*
	 * 判断是需要生效
	 * 当委托处于启用状态，并且当前日期在生效日期和截至日期之间时就需要生效
	 */
	public boolean needEfficient(TrustRecord dm){
		return dm.getState()==TrustRecordState.STARTED
				&&dm.getBeginTime().compareTo(new Date(System.currentTimeMillis()))<=0
				&&dm.getEndTime().compareTo(new Date(System.currentTimeMillis()))>=0;
	}
	
	
	
	
	public void reminder(List<WorkflowTask> tasks){
		try {
			for(WorkflowTask task : tasks){
				if(neetReminder(task)){
					if(task.getReminderLimitTimes()!=0&&task.getReminderLimitTimes().equals(task.getAlreadyReminderTimes())){
						if(StringUtils.isNotEmpty(task.getReminderNoticeStyle())){
							informSettingUser(task);
						}
					}
					if(task.getReminderLimitTimes()==0||task.getReminderLimitTimes()>task.getAlreadyReminderTimes()){
							if(StringUtils.isNotEmpty(task.getReminderStyle())){
								reminder(task);
							}
							task.setLastReminderTime(new Date(System.currentTimeMillis()));
							task.setAlreadyReminderTimes(task.getAlreadyReminderTimes()+1);
						} 
				}
			}
			taskService.saveTasks(tasks);
		} catch (Exception e) {
			log.error("定时催办异常："+e.getMessage());
		}
	}
	/*
	 * 催办超出次数限制，通知相关人员
	 */
	public void informSettingUser(WorkflowTask task) throws Exception{
		String[] reminderNoticeStyle = task.getReminderNoticeStyle().split(",");
		for(String style:reminderNoticeStyle){
			if(style.equalsIgnoreCase(CommonStrings.EMAIL_STYLE)){
				emailInform(task);
			}else if(style.equalsIgnoreCase(CommonStrings.RTX_STYLE)){
				RtxInform(task);
			}else if(style.equalsIgnoreCase(CommonStrings.SMS_STYLE)){
				smsInform(task);
			}else if(style.equalsIgnoreCase(CommonStrings.SWING_STYLE)){
				swingInform(task);
			}
		}
	}
	
	public void RtxInform(WorkflowTask task) {
		String msg = new StringBuilder( "任务：").append(task.getTitle()).append("的办理人").append(task.getTransactorName()).append( "被催办次数已经超过设置上限，请您核实情况。").toString();
		if(StringUtils.isNotEmpty(task.getReminderNoticeUser())){
			for(String userLoginName : task.getReminderNoticeUser().split(",")){
				rtx.RtxMsgSender.sendNotify(userLoginName, "催办超期提醒", "1", msg , "",task.getCompanyId());
			}
		}	
	}
	public void smsInform(WorkflowTask task) {
		// TODO Auto-generated method stub
	}
	
	public void emailInform(WorkflowTask task) {
		if(StringUtils.isNotEmpty(task.getReminderNoticeUser())){
			Set<String> informUserEmails = new HashSet<String>();
			User temp ;
			for(String userLoginName : task.getReminderNoticeUser().split(",")){
				temp = userManager.getUserByLoginName(userLoginName);
				if(temp!=null)informUserEmails.add(temp.getEmail());
			}
			String msg = new StringBuilder( "任务：").append(task.getTitle()).append("的办理人").append(task.getTransactorName()).append( "被催办次数已经超过设置上限，请您核实情况。").toString();
			AsyncMailUtils.sendMail(informUserEmails,"催办超期提醒", msg);
		}
	}
	public void swingInform(WorkflowTask task) throws Exception {
		String msg = new StringBuilder( "任务：").append(task.getTitle()).append("的办理人").append(task.getTransactorName()).append( "被催办次数已经超过设置上限，请您核实情况。").toString();
		if(StringUtils.isNotEmpty(task.getReminderNoticeUser())){
			for(String userLoginName : task.getReminderNoticeUser().split(",")){
				ApiFactory.getPortalService().addMessage("task", "系统管理员", ContextUtils.getLoginName(), userLoginName,"催办超期提醒", msg, "/task/message-task.htm?id="+task.getId());
			}
		}	
		
	}
	
	public void reminder(WorkflowTask task) throws Exception{
		String[] reminderStyles = task.getReminderStyle().split(",");
		for(String style:reminderStyles){
			if(StringUtils.trim(style).equalsIgnoreCase(CommonStrings.EMAIL_STYLE)){
				emailReminder(task);
			}else if(StringUtils.trim(style).equalsIgnoreCase(CommonStrings.RTX_STYLE)){
				rtxReminder(task);
			}else if(StringUtils.trim(style).equalsIgnoreCase(CommonStrings.SMS_STYLE)){
				smsReminder(task);
			}else if(StringUtils.trim(style).equalsIgnoreCase(CommonStrings.SWING_STYLE)){
				swingReminder(task);
			}
		}
	}
	
	public void emailReminder(WorkflowTask task){
		User user = userManager.getUserByLoginName(task.getTransactor());
		String msg = new StringBuilder( "任务：").append(task.getTitle()).append( "已经生成")
		.append(((System.currentTimeMillis()-task.getCreatedTime().getTime())/MILLI_SECOND)).append("天了。请尽快办理！").toString();
		AsyncMailUtils.sendMail(user.getEmail(),"催办超期提醒", msg);
	}
	
	public void rtxReminder(WorkflowTask task){
		String msg = new StringBuilder("(").append(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()))).append( ")任务：").append(task.getTitle()).append( "已经生成")
						.append(((System.currentTimeMillis()-task.getCreatedTime().getTime())/MILLI_SECOND)).append("天了。请尽快办理！").toString();
		String url = SystemUrls.getSystemUrl(StringUtils.substring(task.getUrl(), 0,task.getUrl().indexOf('/')))
					+StringUtils.substring(task.getUrl(), task.getUrl().indexOf('/'));
		if(url.contains("?")){
			url=url+task.getId();
		}else{
			url=url+"?taskId="+task.getId();
		}
		User user = userManager.getUserByLoginName(task.getTransactor());
		rtx.RtxMsgSender.sendNotify(task.getTransactor(), "任务办理提醒", "1", msg , url,user.getCompanyId());
	}
	public void swingReminder(WorkflowTask task) throws Exception{
		if(StringUtils.isNotEmpty(task.getTransactor())){
			String msg = new StringBuilder("(").append(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()))).append( ")任务：").append(task.getTitle()).append( "已经生成")
			.append(((System.currentTimeMillis()-task.getCreatedTime().getTime())/MILLI_SECOND)).append("天了。请尽快办理！").toString();
			ApiFactory.getPortalService().addMessage("task", "系统管理员", ContextUtils.getLoginName(), task.getTransactor(),"待办任务催办提醒", msg, "/task/message-task.htm?id="+task.getId());
		}
	}
	public void smsReminder(WorkflowTask task){
		//TODO
	}
	
	public boolean neetReminder(WorkflowTask task){
		return (task.getLastReminderTime()== null && (System.currentTimeMillis()-task.getCreatedTime().getTime())>task.getDuedate()*MILLI_SECOND) ||
		(task.getLastReminderTime()!= null && (System.currentTimeMillis()-task.getLastReminderTime().getTime())>task.getRepeat()*MILLI_SECOND);
	}
}
