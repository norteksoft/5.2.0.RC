package com.norteksoft.product.api.entity;
/**
 * 任务的权限
 * @author liudongxia
 *
 */
public class TaskPermission {
	private Boolean countersignResultVisible=false;//查看会签结果
	private Boolean voteResultVisible=false;//查看投票结果
	private Boolean opinionEditable=false;//编辑意见
	private Boolean opinionRequired=false;//意见是否必填
	private Boolean historyVisible=false;//查看流转历史的权限
	private Boolean formPrintable=false;//打印表单的权限
	
	private Boolean documentCreateable=false;//是否可以创建正文
	private Boolean documentDeletable=false;//是否可以删除正文
	private Boolean documentEditable=false;//是否可以编辑正文
	private Boolean documentPrintable=false;//是否可以打印正文
	private Boolean documentDownloadable=false;//是否可以下载正文	
	
	
	private Boolean attachmentCreateable=false;//是否可以创建附件
	private Boolean attachmentDeletable=false;//是否可以删除附件
	private Boolean attachmentDownloadable=false;//是否可以下载附件

	public Boolean getCountersignResultVisible() {
		return countersignResultVisible;
	}

	public void setCountersignResultVisible(Boolean countersignResultVisible) {
		this.countersignResultVisible = countersignResultVisible;
	}

	public Boolean getVoteResultVisible() {
		return voteResultVisible;
	}

	public void setVoteResultVisible(Boolean voteResultVisible) {
		this.voteResultVisible = voteResultVisible;
	}

	public Boolean getOpinionEditable() {
		return opinionEditable;
	}

	public void setOpinionEditable(Boolean opinionEditable) {
		this.opinionEditable = opinionEditable;
	}

	public Boolean getOpinionRequired() {
		return opinionRequired;
	}

	public void setOpinionRequired(Boolean opinionRequired) {
		this.opinionRequired = opinionRequired;
	}

	public Boolean getHistoryVisible() {
		return historyVisible;
	}

	public void setHistoryVisible(Boolean historyVisible) {
		this.historyVisible = historyVisible;
	}

	public Boolean getFormPrintable() {
		return formPrintable;
	}

	public void setFormPrintable(Boolean formPrintable) {
		this.formPrintable = formPrintable;
	}

	public Boolean getDocumentCreateable() {
		return documentCreateable;
	}

	public void setDocumentCreateable(Boolean documentCreateable) {
		this.documentCreateable = documentCreateable;
	}

	public Boolean getDocumentDeletable() {
		return documentDeletable;
	}

	public void setDocumentDeletable(Boolean documentDeletable) {
		this.documentDeletable = documentDeletable;
	}

	public Boolean getDocumentEditable() {
		return documentEditable;
	}

	public void setDocumentEditable(Boolean documentEditable) {
		this.documentEditable = documentEditable;
	}

	public Boolean getDocumentPrintable() {
		return documentPrintable;
	}

	public void setDocumentPrintable(Boolean documentPrintable) {
		this.documentPrintable = documentPrintable;
	}

	public Boolean getDocumentDownloadable() {
		return documentDownloadable;
	}

	public void setDocumentDownloadable(Boolean documentDownloadable) {
		this.documentDownloadable = documentDownloadable;
	}

	public Boolean getAttachmentCreateable() {
		return attachmentCreateable;
	}

	public void setAttachmentCreateable(Boolean attachmentCreateable) {
		this.attachmentCreateable = attachmentCreateable;
	}

	public Boolean getAttachmentDeletable() {
		return attachmentDeletable;
	}

	public void setAttachmentDeletable(Boolean attachmentDeletable) {
		this.attachmentDeletable = attachmentDeletable;
	}

	public Boolean getAttachmentDownloadable() {
		return attachmentDownloadable;
	}

	public void setAttachmentDownloadable(Boolean attachmentDownloadable) {
		this.attachmentDownloadable = attachmentDownloadable;
	}
}
