package com.norteksoft.wf.engine.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.product.orm.IdEntity;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;

@Entity
@Table(name="WF_DOCUMENT")
public class Document extends IdEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String subject;//主题 预留
	private Long templateId;//模板id;
	private String fileType;//文件类型
	private String status;//状态
	private String fileName;//文件名
	private Integer fileSize;//文件大小
	private String filePath;//文件路径
	private String remark;//文件描述
	private String workflowId; //流程实例id
	private String taskName; //上传环节的任务名
	private String customField;//自定义类别
	@Enumerated(EnumType.STRING)
	private TaskProcessingMode taskMode;//环节的办理模式
	private Long taskId;//任务id
	@Transient
	private String editType;
	@Transient
	private Boolean printSetting;
	@Transient
	private Boolean downloadSetting;
	@Transient
	private Boolean deleteSetting;
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public Integer getFileSize() {
		return fileSize;
	}
	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public TaskProcessingMode getTaskMode() {
		return taskMode;
	}
	public void setTaskMode(TaskProcessingMode taskMode) {
		this.taskMode = taskMode;
	}
	public String getEditType() {
		return editType;
	}
	public void setEditType(String editType) {
		this.editType = editType;
	}
	public Boolean getPrintSetting() {
		return printSetting;
	}
	public void setPrintSetting(Boolean printSetting) {
		this.printSetting = printSetting;
	}
	public Boolean getDownloadSetting() {
		return downloadSetting;
	}
	public void setDownloadSetting(Boolean downloadSetting) {
		this.downloadSetting = downloadSetting;
	}
	public String getCustomField() {
		return customField;
	}
	public void setCustomField(String customField) {
		this.customField = customField;
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
}
