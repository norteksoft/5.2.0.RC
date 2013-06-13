package com.norteksoft.task.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.norteksoft.product.orm.IdEntity;
import com.norteksoft.task.base.enumeration.TaskState;

@SuppressWarnings("serial")
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@Table(name = "PRODUCT_TASK")
public class Task  extends IdEntity   implements Serializable{
	private String transactor;  //办理人登录名
	private String transactorName;//办理人姓名
	private Date transactDate;  //办理日期
	private String title; //任务标题
	private String url;   //任务打开链接
	private Integer active = TaskState.WAIT_TRANSACT.getIndex();   //任务状态 : 0:等待处理  1:等待设置办理人  2:任务完成  3:被取消 4:待领取 5：已指派  6：待选择环节
	@Column(name="IS_READ")
 	private Boolean read = false;     //是否已阅
	private TaskMark taskMark = TaskMark.CANCEL;
	private String groupName; //任务组，显示任务列表时按组排列

	private Boolean visible = true; //任务的可见性
	
	private Date lastReminderTime;//上次催办时间
	@Column(length=64)
	private String reminderStyle;//催办方式
	private Long duedate = 0l;//开始催办时限
	@Column(name="urge_interval")
	private Long repeat = 0l;//催办间隔时间
	private Integer reminderLimitTimes = 0;//催办次数上限  0表示一直催办
	private Integer alreadyReminderTimes = 0;//已催办次数
	private String reminderNoticeStyle;//催办次数达到上限后，通知相关人员的方式
	private String reminderNoticeUser;//催办通知用户 登录名 逗号隔开
	
	private String category;//任务类型，当是流程任务时其值为 流程类别(流程类型名称)，普通任务时其值自己任意取
	
	@Transient
	private Boolean sendingMessage = false;//是否发送RTX消息设置
	@Column(name="is_workflow_task")
	private Boolean workflowTask=true;//是否是工作流相关的任务
	private Boolean paused=false;//实例是否暂停,true是暂停，false是正常
	private String name;   //任务名
	
	private Integer displayOrder=0;//排序字段，xtsoa需要

	public String getTransactor() {
		return transactor;
	}

	public void setTransactor(String transactor) {
		this.transactor = transactor;
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



	public Boolean getSendingMessage() {
		return sendingMessage;
	}

	public void setSendingMessage(Boolean sendingMessage) {
		this.sendingMessage = sendingMessage;
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

	

	public String getTransactorName() {
		return transactorName;
	}

	public void setTransactorName(String transactorName) {
		this.transactorName = transactorName;
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

	/**
	 * 查看当前任务是否被完成、被取消或者被指派(这些状态下的任务是不能够再对表单操作的)
	 * @return 如果满足一种状态，返回true，否则返回false
	 */
	public boolean isCompleted(){
		return TaskState.COMPLETED.getIndex().equals(this.getActive())||TaskState.CANCELLED.getIndex().equals(this.getActive())||TaskState.ASSIGNED.getIndex().equals(this.getActive());
	}

	public Boolean getRead() {
		return read;
	}

	public void setRead(Boolean read) {
		this.read = read;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
