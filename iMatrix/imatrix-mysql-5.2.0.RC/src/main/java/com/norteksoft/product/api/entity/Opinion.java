package com.norteksoft.product.api.entity;

import java.io.Serializable;
import java.util.Date;

public class Opinion implements Serializable{
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String opinion;     // 办理意见
	
	private Date addOpinionDate;  // 添加意见日期
	
	private Long taskId;
	
	private String customField;//自定义类别
	
	private String taskName; //环节名称
	private String departmentName;//部门名称
	
	private String tacheCode;//环节编码
	private String transactor;//任务办理人
	private String transactorName;//办理人真名
	private Boolean delegateFlag;

	public Opinion(String opinion, Long taskId, String customField) {
		this.opinion = opinion;
		this.taskId = taskId;
		this.customField = customField;
	}

	public Opinion(String opinion, Date addOpinionDate, Long taskId,
			String customField) {
		this.opinion = opinion;
		this.addOpinionDate = addOpinionDate;
		this.taskId = taskId;
		this.customField = customField;
	}

	public Opinion(Long id, String opinion, Date addOpinionDate,
			Long taskId, String customField) {
		this.id = id;
		this.opinion = opinion;
		this.addOpinionDate = addOpinionDate;
		this.taskId = taskId;
		this.customField = customField;
	}
	
	public Opinion(Long id, String opinion, Date addOpinionDate,
			Long taskId, String customField,String taskName,String tacheCode,String transactor) {
		this.id = id;
		this.opinion = opinion;
		this.addOpinionDate = addOpinionDate;
		this.taskId = taskId;
		this.customField = customField;
		this.taskName=taskName;
		this.tacheCode=tacheCode;
		this.transactor=transactor;
	}

	public Opinion() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public Date getAddOpinionDate() {
		return addOpinionDate;
	}

	public void setAddOpinionDate(Date addOpinionDate) {
		this.addOpinionDate = addOpinionDate;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getCustomField() {
		return customField;
	}

	public void setCustomField(String customField) {
		this.customField = customField;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTacheCode() {
		return tacheCode;
	}

	public void setTacheCode(String tacheCode) {
		this.tacheCode = tacheCode;
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

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public Boolean getDelegateFlag() {
		return delegateFlag;
	}

	public void setDelegateFlag(Boolean delegateFlag) {
		this.delegateFlag = delegateFlag;
	}
	
	
}
