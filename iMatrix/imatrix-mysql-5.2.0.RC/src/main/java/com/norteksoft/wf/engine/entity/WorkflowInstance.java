package com.norteksoft.wf.engine.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.product.orm.IdEntity;
import com.norteksoft.wf.base.enumeration.ProcessState;


@Entity
@Table(name="WF_INSTANCE")
public class WorkflowInstance extends IdEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Long workflowDefinitionId;//流程定义扩展类的Id
	
	private String processDefinitionId;//jbpm流程定义的ID
	
	private String  processInstanceId;//流程实例ID jbpm实例的id
	
	private String parentProcessId;//父流程的id
	
	private String parentProcessTacheName;//父流程的环节名
	
	private String parentExcutionId;//当前子流程对应的父流程Excution
	
	
	private Date startTime;//发起日期
	
	private String processName;//流程名字
	
	private String processCode;//流程名字
	
	private String currentCustomState;//当前实例的状态
	
	private ProcessState processState = ProcessState.UNSUBMIT; //流程运行中的状态
	
	private Date submitTime;//提交时间
	
	private Date endTime;//流程结束时间
	
	private Long dataId;//对应数据ID
	
	private Long formId;//表单Id
	
	private String formName;//表单名字
	
	private String currentActivity;//当前环节
	private String currentActivityTitle;//当前环节标题
	private String instanceTitle;//当前实例标题
	
	private Long typeId;//所属流程类型id
	
	private Long systemId; //系统ID
	
	private Boolean sharedForm = true;//是否共享表单，当是子流程时这个字段才有效 true为是，false为否
	
	private String formUrl;//查看表单的url
	
	private Long firstTaskId;//第一个环节的id。新建表单时，第一次提交生成的任务的id
	
	private Integer priority = 6;//紧急程度
	
	private Integer totalStep;//总步数，自由流中有用
	
	private Integer currentStep;//当前步数，自由流中有用
	
	private String emergencyUrl;//应急url
	
	@Column(length=64)
	private String reminderStyle;//催办方式
	private Long duedate = 0l;//开始催办时限
	@Column(name="urge_interval")
	private Long repeat = 0l;//催办间隔时间
	private Date lastReminderTime;//上次催办时间
	private Integer reminderLimitTimes = 0;//催办次数上限  0表示一直催办
	private Integer alreadyReminderTimes = 0;//已催办次数
	private String reminderNoticeStyle;//催办次数达到上限后，通知相关人员的方式
	private String reminderNoticeUserCondition;//催办通知用户 的条件
	@Transient
	private List<Object> dataList = new ArrayList<Object>();//封装显示数据
	
	private String customType;//自定义类别
	
	private String previousActivity;//上一环节名称
	private String previousActivityTitle;//上一环节标题
	
	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}
	
	public Long getSystemId() {
		return systemId;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getCurrentActivity() {
		return currentActivity;
	}

	public void setCurrentActivity(String currentActivity) {
		this.currentActivity = currentActivity;
	}

	public Long getWorkflowDefinitionId() {
		return workflowDefinitionId;
	}

	public void setWorkflowDefinitionId(Long workflowDefinitionId) {
		this.workflowDefinitionId = workflowDefinitionId;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public Long getDataId() {
		return dataId;
	}

	public void setDataId(Long dataId) {
		this.dataId = dataId;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public ProcessState getProcessState() {
		return processState;
	}

	public void setProcessState(ProcessState processState) {
		this.processState = processState;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public String getParentProcessId() {
		return parentProcessId;
	}

	public void setParentProcessId(String parentProcessId) {
		this.parentProcessId = parentProcessId;
	}
	
	public Boolean getSharedForm() {
		return sharedForm;
	}

	public void setSharedForm(Boolean sharedForm) {
		this.sharedForm = sharedForm;
	}

	public List<Object> getDataList() {
		return dataList;
	}

	public void setDataList(List<Object> dataList) {
		this.dataList = dataList;
	}

	public String getFormUrl() {
		return formUrl;
	}

	public void setFormUrl(String formUrl) {
		this.formUrl = formUrl;
	}
	

	public String getParentExcutionId() {
		return parentExcutionId;
	}

	public void setParentExcutionId(String parentExcutionId) {
		this.parentExcutionId = parentExcutionId;
	}
	
	public Long getFirstTaskId() {
		return firstTaskId;
	}

	public void setFirstTaskId(Long firstTaskId) {
		this.firstTaskId = firstTaskId;
	}
	
	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getTotalStep() {
		return totalStep;
	}

	public void setTotalStep(Integer totalStep) {
		this.totalStep = totalStep;
	}

	public Integer getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(Integer currentStep) {
		this.currentStep = currentStep;
	}
	
	public String getReminderStyle() {
		return reminderStyle;
	}

	public void setReminderStyle(String reminderStyle) {
		this.reminderStyle = reminderStyle;
	}

	public Long getDuedate() {
		return duedate;
	}

	public void setDuedate(Long duedate) {
		this.duedate = duedate;
	}

	public Long getRepeat() {
		return repeat;
	}

	public void setRepeat(Long repeat) {
		this.repeat = repeat;
	}

	public Date getLastReminderTime() {
		return lastReminderTime;
	}

	public void setLastReminderTime(Date lastReminderTime) {
		this.lastReminderTime = lastReminderTime;
	}
	
	public String getParentProcessTacheName() {
		return parentProcessTacheName;
	}

	public void setParentProcessTacheName(String parentProcessTacheName) {
		this.parentProcessTacheName = parentProcessTacheName;
	}

	public Integer getAlreadyReminderTimes() {
		return alreadyReminderTimes;
	}

	public void setAlreadyReminderTimes(Integer alreadyReminderTimes) {
		this.alreadyReminderTimes = alreadyReminderTimes;
	}

	public String getReminderNoticeStyle() {
		return reminderNoticeStyle;
	}

	public void setReminderNoticeStyle(String reminderNoticeStyle) {
		this.reminderNoticeStyle = reminderNoticeStyle;
	}

	public String getReminderNoticeUserCondition() {
		return reminderNoticeUserCondition;
	}

	public void setReminderNoticeUserCondition(String reminderNoticeUserCondition) {
		this.reminderNoticeUserCondition = reminderNoticeUserCondition;
	}
	public Integer getReminderLimitTimes() {
		return reminderLimitTimes;
	}

	public void setReminderLimitTimes(Integer reminderLimitTimes) {
		this.reminderLimitTimes = reminderLimitTimes;
	}

	public String getEmergencyUrl() {
		return emergencyUrl;
	}

	public void setEmergencyUrl(String emergencyUrl) {
		this.emergencyUrl = emergencyUrl;
	}

	public String getCurrentActivityTitle() {
		return currentActivityTitle;
	}

	public void setCurrentActivityTitle(String currentActivityTitle) {
		this.currentActivityTitle = currentActivityTitle;
	}


	public String getInstanceTitle() {
		return instanceTitle;
	}

	public void setInstanceTitle(String instanceTitle) {
		this.instanceTitle = instanceTitle;
	}

	public String getProcessCode() {
		return processCode;
	}

	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}

	public String getCurrentCustomState() {
		return currentCustomState;
	}

	public void setCurrentCustomState(String currentCustomState) {
		this.currentCustomState = currentCustomState;
	}

	public String getCustomType() {
		return customType;
	}

	public void setCustomType(String customType) {
		this.customType = customType;
	}

	public String getPreviousActivity() {
		return previousActivity;
	}

	public void setPreviousActivity(String previousActivity) {
		this.previousActivity = previousActivity;
	}

	public String getPreviousActivityTitle() {
		return previousActivityTitle;
	}

	public void setPreviousActivityTitle(String previousActivityTitle) {
		this.previousActivityTitle = previousActivityTitle;
	}

	@Override
	public String toString() {
		return new StringBuilder("WorkflowInstance [companyId=").append(this.getCompanyId())
				.append(", creator=").append(this.getCreator())
				.append(", currentActivity=").append(currentActivity)
				.append(", dataId=").append( dataId )
				.append(", formId=" ).append( formId )
				.append( ", parentProcessId=" ).append( parentProcessId)
				.append( ", processDefinitionId=" ).append( processDefinitionId)
				.append( ", processInstanceId=" ).append( processInstanceId)
				.append( ", processState=" ).append( processState )
				.append(", state=" ).append( currentCustomState)
				.append( ", systemId=" ).append( systemId )
				.append(", typeId=" ).append( typeId)
				.append( ", workflowDefinitionId=" ).append( workflowDefinitionId ).append( "]").toString();
	}
	
	
	
}
