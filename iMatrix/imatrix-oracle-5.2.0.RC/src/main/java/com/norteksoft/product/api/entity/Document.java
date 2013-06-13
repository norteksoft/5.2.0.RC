package com.norteksoft.product.api.entity;

import java.io.Serializable;
import java.util.Date;

public class Document implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private String fileName;//文件名
	private String fileType;//文件类型
	private String filePath;//文件路径
	private Integer fileSize;//文件大小
	private String status;//状态
	private String descript;//文件描述
	private byte[] fileBody;//文件内容
	private String customField;//自定义类型
	private String subject;//主题 预留
	private Long taskId;
	private String taskName;
	private String workflowId;
	private Boolean deleteSetting;//删除正文权限
	private Date createDate;//创建日期
	
	public Document() {
	}

	public Document(String fileName, byte[] fileBody, Long taskId) {
		this.fileName = fileName;
		this.fileBody = fileBody;
		this.taskId = taskId;
	}
	
	public Document(Long id, String fileName, byte[] fileBody,
			Long taskId) {
		this.id = id;
		this.fileName = fileName;
		this.fileBody = fileBody;
		this.taskId = taskId;
	}

	public Document(String fileName, byte[] fileBody,
			String customField, Long taskId) {
		this.fileName = fileName;
		this.fileBody = fileBody;
		this.customField = customField;
		this.taskId = taskId;
	}

	public Document(Long id, String fileName, byte[] fileBody,
			String customField, Long taskId) {
		this.id = id;
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
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Integer getFileSize() {
		return fileSize;
	}
	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDescript() {
		return descript;
	}
	public void setDescript(String descript) {
		this.descript = descript;
	}
	public byte[] getFileBody() {
		return fileBody;
	}
	public void setFileBody(byte[] fileBody) {
		this.fileBody = fileBody;
	}
	public String getCustomField() {
		return customField;
	}
	public void setCustomField(String customField) {
		this.customField = customField;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
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

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
}
