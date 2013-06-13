package com.norteksoft.task.entity;

import java.io.Serializable;
public class TaskSetting implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String transactor;  //办理人
	private String creator; //任务发起人
	private String title; //任务标题
	private String url;   //任务打开链接
	private boolean visible = true; //任务的可见性
	private boolean moreTransactor = false;//是否多人办理 ， true为多个办理人
	private String variable;
	private String transitionName;//指定的流向名
	private String assignmentTransactors;//指定的办理人,多个办理人以逗号隔开
	private boolean returnUrl = true;//是否对返回url设置解析
	private String allOriginalUsers;//指定的原办理人
	
	/**
	 * 流程实例获得类
	 * @return 流程实例
	 */
	public static TaskSetting getTaskSettingInstance(){
		return new TaskSetting();
	}
	
	public String getTransactor() {
		return transactor;
	}
	public TaskSetting setTransactor(String transactor) {
		this.transactor = transactor;
		return this;
	}
	public String getCreator() {
		return creator;
	}
	public TaskSetting setCreator(String creator) {
		this.creator = creator;
		return this;
	}
	public String getTitle() {
		return title;
	}
	public TaskSetting setTitle(String title) {
		this.title = title;
		return this;
	}
	public String getUrl() {
		return url;
	}
	public TaskSetting setUrl(String url) {
		this.url = url;
		return this;
	}
	public boolean isVisible() {
		return visible;
	}
	public TaskSetting setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}
	public boolean isMoreTransactor() {
		return moreTransactor;
	}
	public TaskSetting setMoreTransactor(boolean moreTransactor) {
		this.moreTransactor = moreTransactor;
		return this;
	}
	public String getVariable() {
		return variable;
	}
	public TaskSetting setVariable(String variable) {
		this.variable = variable;
		return this;
	}
	public String getTransitionName() {
		return transitionName;
	}
	public TaskSetting setTransitionName(String transitionName) {
		this.transitionName = transitionName;
		return this;
	}
	public String getAssignmentTransactors() {
		return assignmentTransactors;
	}
	public TaskSetting setAssignmentTransactors(String assignmentTransactors) {
		this.assignmentTransactors = assignmentTransactors;
		return this;
	}

	public boolean isReturnUrl() {
		return returnUrl;
	}

	public TaskSetting setReturnUrl(boolean returnUrl) {
		this.returnUrl = returnUrl;
		return this;
	}

	public String getAllOriginalUsers() {
		return allOriginalUsers;
	}

	public TaskSetting setAllOriginalUsers(String allOriginalUsers) {
		this.allOriginalUsers = allOriginalUsers;
		return this;
	}

	@Override
	public String toString() {
		return "[allOriginalUsers=" +this.allOriginalUsers+",assignmentTransactors="+this.assignmentTransactors+",creator="+this.creator
		+",title="+this.title+",transactor="+this.transactor+",transitionName="+this.transitionName+",url="+this.url+",variable="+this.variable+"]";
	}
	
}

