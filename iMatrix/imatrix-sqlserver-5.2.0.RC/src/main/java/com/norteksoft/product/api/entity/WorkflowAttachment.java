package com.norteksoft.product.api.entity;

import java.io.Serializable;
import java.util.Date;

public class WorkflowAttachment implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private String fileName;//文件名
	private String fileType;//文件类型
	private Float fileSize;//文件大小
	private byte[] fileBody;//文件内容
	private String departmentName;//部门名称
	private String transactor ;//办理人
	private String customField;//自定义类别
	private Long taskId;
	private String taskName;
	private String workflowId;
	private Boolean deleteSetting;//删除正文权限
	private Date createDate;//创建日期
	
	private String filePath;//文件路径
	
	public WorkflowAttachment() {
	}
	
	public WorkflowAttachment(String fileName, byte[] fileBody,
			Long taskId) {
		this.fileName = fileName;
		this.fileBody = fileBody;
		this.taskId = taskId;
	}
	
	public WorkflowAttachment(Long id, String fileName,
			byte[] fileBody, Long taskId) {
		this.id = id;
		this.fileName = fileName;
		this.fileBody = fileBody;
		this.taskId = taskId;
	}
	
	public WorkflowAttachment(String fileName, byte[] fileBody,
			String customField, Long taskId) {
		this.fileName = fileName;
		this.fileBody = fileBody;
		this.customField = customField;
		this.taskId = taskId;
	}

	public WorkflowAttachment(Long id, String fileName,
			byte[] fileBody, String customField, Long taskId) {
		this.fileName = fileName;
		this.fileBody = fileBody;
		this.customField = customField;
		this.taskId = taskId;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public Float getFileSize() {
		return fileSize;
	}
	public void setFileSize(Float fileSize) {
		this.fileSize = fileSize;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public String getTransactor() {
		return transactor;
	}
	public void setTransactor(String transactor) {
		this.transactor = transactor;
	}
	public byte[] getFileBody() {
		return fileBody;
	}
	public void setFileBody(byte[] fileBody) {
		this.fileBody = fileBody;
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

	public Boolean getDeleteSetting() {
		return deleteSetting;
	}

	public void setDeleteSetting(Boolean deleteSetting) {
		this.deleteSetting = deleteSetting;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTaskName() {
		return taskName;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}


}
