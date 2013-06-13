package com.norteksoft.wf.engine.entity;

/**
 * 流程监控删除实例时，对实例相关数据删除的设置
 * @author wurong
 *
 */
public class InstanceDataDeleteSetting {

	private boolean deleteFormData;
	private boolean deleteHistory;
	private boolean deleteDocument;
	private boolean deleteAttachment;
	private boolean deleteOpinion;
	public boolean isDeleteFormData() {
		return deleteFormData;
	}
	public void setDeleteFormData(boolean deleteFormData) {
		this.deleteFormData = deleteFormData;
	}
	public boolean isDeleteHistory() {
		return deleteHistory;
	}
	public void setDeleteHistory(boolean deleteHistory) {
		this.deleteHistory = deleteHistory;
	}
	public boolean isDeleteDocument() {
		return deleteDocument;
	}
	public void setDeleteDocument(boolean deleteDocument) {
		this.deleteDocument = deleteDocument;
	}
	public boolean isDeleteAttachment() {
		return deleteAttachment;
	}
	public void setDeleteAttachment(boolean deleteAttachment) {
		this.deleteAttachment = deleteAttachment;
	}
	public boolean isDeleteOpinion() {
		return deleteOpinion;
	}
	public void setDeleteOpinion(boolean deleteOpinion) {
		this.deleteOpinion = deleteOpinion;
	}
	
	
}
