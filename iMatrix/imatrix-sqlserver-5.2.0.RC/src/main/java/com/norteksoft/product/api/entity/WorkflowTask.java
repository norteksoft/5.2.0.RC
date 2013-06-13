package com.norteksoft.product.api.entity;

import java.io.Serializable;
import java.util.Date;

import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.task.entity.TaskMark;

public class WorkflowTask implements Serializable{
	private static final long serialVersionUID = 1L;
	//task
	private Long id;
	private boolean deleted;
	private Long companyId;
	private String transactor;  //办理人登录名
	private String transactorName;//办理人姓名
	private Date transactDate;  //办理日期
	private String title; //任务标题
	private String url;   //任务打开链接
	private Integer active; //任务状态 : 0:等待处理  1:等待设置办理人  2:任务完成  3:被取消 4:待领取 5：已指派  6：待选择环节
 	private Boolean read;     //是否已阅
	private TaskMark taskMark;
	private String groupName; //任务组，显示任务列表时按组排列
	private Boolean visible; //任务的可见性
	private Date lastReminderTime;//上次催办时间
	private String reminderStyle;//催办方式
	private Long duedate;//开始催办时限
	private Long repeat;//催办间隔时间
	private Integer reminderLimitTimes;//催办次数上限  0表示一直催办
	private Integer alreadyReminderTimes;//已催办次数
	private String reminderNoticeStyle;//催办次数达到上限后，通知相关人员的方式
	private String reminderNoticeUser;//催办通知用户 登录名 逗号隔开
	private String category;//任务类型，当是流程任务时其值为 流程类别(流程类型名称)，普通任务时其值自己任意取
	private Boolean sendingMessage;//是否发送RTX消息设置
	private Boolean workflowTask;//是否是工作流相关的任务
	private Boolean paused;//实例是否暂停,true是暂停，false是正常
	private String name;   //任务名
	private Integer displayOrder;//排序字段，xtsoa需要
	
	//workflowTask
    private String code;//环节编码
	private boolean effective;//任务是否有效，当环节被退回时，中间环节任务失效
	private TaskProcessingMode processingMode; //任务办理方式
	private boolean specialTask;//是否为特事特办任务， true为特事特办任务
	private Boolean distributable;//是不是分发的 ，true为分发的，分发的任务不会影响流程。办理人只需要查看。如果提交了任务就算完成
	private TaskProcessingResult taskProcessingResult;   //任务处理结果(办理意见  同意   不同意   放弃)
	private String processInstanceId;//流程ID
	private String executionId;
	private String trustor;//委托人登录名
	private String trustorName;//委托人名称
	private String nextTasks; //后面环节
	private String submitButton;
	private String addSignerButton;
	private String removeSignerButton;
	private String agreeButton;
	private String disagreeButton;
	private String signForButton;
	private String approveButton;
	private String opposeButton;
	private String abstainButton;
	private String assignButton;
	private String remark; //扩展字段，bkyOA中用到
	private Integer groupNum;//第几次办理该环节
	private Boolean moreTransactor;//是否是多人办理环节
	private Boolean drawTask;//是否领取任务
	private String customType;//流程自定义类别
	private Boolean assignable;//是否是指派任务，任务委托监控中会用到 
	private String expands;  //扩展
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTransactor() {
		return transactor;
	}
	public void setTransactor(String transactor) {
		this.transactor = transactor;
	}
	public String getTransactorName() {
		return transactorName;
	}
	public void setTransactorName(String transactorName) {
		this.transactorName = transactorName;
	}
	public Date getTransactDate() {
		return transactDate;
	}
	public void setTransactDate(Date transactDate) {
		this.transactDate = transactDate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getActive() {
		return active;
	}
	public void setActive(Integer active) {
		this.active = active;
	}
	public Boolean getRead() {
		return read;
	}
	public void setRead(Boolean read) {
		this.read = read;
	}
	public TaskMark getTaskMark() {
		return taskMark;
	}
	public void setTaskMark(TaskMark taskMark) {
		this.taskMark = taskMark;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public Boolean getVisible() {
		return visible;
	}
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	public Date getLastReminderTime() {
		return lastReminderTime;
	}
	public void setLastReminderTime(Date lastReminderTime) {
		this.lastReminderTime = lastReminderTime;
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
	public Integer getReminderLimitTimes() {
		return reminderLimitTimes;
	}
	public void setReminderLimitTimes(Integer reminderLimitTimes) {
		this.reminderLimitTimes = reminderLimitTimes;
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
	public String getReminderNoticeUser() {
		return reminderNoticeUser;
	}
	public void setReminderNoticeUser(String reminderNoticeUser) {
		this.reminderNoticeUser = reminderNoticeUser;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Boolean getSendingMessage() {
		return sendingMessage;
	}
	public void setSendingMessage(Boolean sendingMessage) {
		this.sendingMessage = sendingMessage;
	}
	public Boolean getWorkflowTask() {
		return workflowTask;
	}
	public void setWorkflowTask(Boolean workflowTask) {
		this.workflowTask = workflowTask;
	}
	public Boolean getPaused() {
		return paused;
	}
	public void setPaused(Boolean paused) {
		this.paused = paused;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public boolean isEffective() {
		return effective;
	}
	public void setEffective(boolean effective) {
		this.effective = effective;
	}
	public TaskProcessingMode getProcessingMode() {
		return processingMode;
	}
	public void setProcessingMode(TaskProcessingMode processingMode) {
		this.processingMode = processingMode;
	}
	public boolean isSpecialTask() {
		return specialTask;
	}
	public void setSpecialTask(boolean specialTask) {
		this.specialTask = specialTask;
	}
	public Boolean getDistributable() {
		return distributable;
	}
	public void setDistributable(Boolean distributable) {
		this.distributable = distributable;
	}
	public TaskProcessingResult getTaskProcessingResult() {
		return taskProcessingResult;
	}
	public void setTaskProcessingResult(TaskProcessingResult taskProcessingResult) {
		this.taskProcessingResult = taskProcessingResult;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getExecutionId() {
		return executionId;
	}
	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}
	public String getTrustor() {
		return trustor;
	}
	public void setTrustor(String trustor) {
		this.trustor = trustor;
	}
	public String getNextTasks() {
		return nextTasks;
	}
	public void setNextTasks(String nextTasks) {
		this.nextTasks = nextTasks;
	}
	public String getSubmitButton() {
		return submitButton;
	}
	public void setSubmitButton(String submitButton) {
		this.submitButton = submitButton;
	}
	public String getAddSignerButton() {
		return addSignerButton;
	}
	public void setAddSignerButton(String addSignerButton) {
		this.addSignerButton = addSignerButton;
	}
	public String getRemoveSignerButton() {
		return removeSignerButton;
	}
	public void setRemoveSignerButton(String removeSignerButton) {
		this.removeSignerButton = removeSignerButton;
	}
	public String getAgreeButton() {
		return agreeButton;
	}
	public void setAgreeButton(String agreeButton) {
		this.agreeButton = agreeButton;
	}
	public String getDisagreeButton() {
		return disagreeButton;
	}
	public void setDisagreeButton(String disagreeButton) {
		this.disagreeButton = disagreeButton;
	}
	public String getSignForButton() {
		return signForButton;
	}
	public void setSignForButton(String signForButton) {
		this.signForButton = signForButton;
	}
	public String getApproveButton() {
		return approveButton;
	}
	public void setApproveButton(String approveButton) {
		this.approveButton = approveButton;
	}
	public String getOpposeButton() {
		return opposeButton;
	}
	public void setOpposeButton(String opposeButton) {
		this.opposeButton = opposeButton;
	}
	public String getAbstainButton() {
		return abstainButton;
	}
	public void setAbstainButton(String abstainButton) {
		this.abstainButton = abstainButton;
	}
	public String getAssignButton() {
		return assignButton;
	}
	public void setAssignButton(String assignButton) {
		this.assignButton = assignButton;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getGroupNum() {
		return groupNum;
	}
	public void setGroupNum(Integer groupNum) {
		this.groupNum = groupNum;
	}
	public Boolean getMoreTransactor() {
		return moreTransactor;
	}
	public void setMoreTransactor(Boolean moreTransactor) {
		this.moreTransactor = moreTransactor;
	}
	public Boolean getDrawTask() {
		return drawTask;
	}
	public void setDrawTask(Boolean drawTask) {
		this.drawTask = drawTask;
	}
	public String getCustomType() {
		return customType;
	}
	public void setCustomType(String customType) {
		this.customType = customType;
	}
	public Boolean getAssignable() {
		return assignable;
	}
	public void setAssignable(Boolean assignable) {
		this.assignable = assignable;
	}
	public String getExpands() {
		return expands;
	}
	public void setExpands(String expands) {
		this.expands = expands;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public String getTrustorName() {
		return trustorName;
	}
	public void setTrustorName(String trustorName) {
		this.trustorName = trustorName;
	}
}
