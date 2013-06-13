package com.norteksoft.wf.engine.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.wf.engine.dao.WorkflowAttachmentDao;
import com.norteksoft.wf.engine.entity.WorkflowAttachment;

@Service
@Transactional
public class WorkflowAttachmentManager {
	
	private WorkflowAttachmentDao workflowAttachmentDao;
    
	@Autowired
	public void setWorkflowAttachmentDao(
			WorkflowAttachmentDao workflowAttachmentDao) {
		this.workflowAttachmentDao = workflowAttachmentDao;
	}
	/**
	 * 保存
	 * @param upload
	 */
	@Transactional(readOnly=false)
	public void saveAttachment(WorkflowAttachment upload){
		this.workflowAttachmentDao.save(upload);
	}
	/**
	 *删除
	 * @param upload
	 */
	@Transactional(readOnly=false)
	public void deleteAttachment(Long id){
		this.workflowAttachmentDao.delete(id);
	}
	/**
	 *得到
	 * @param upload
	 */
	public WorkflowAttachment getAttachment(Long id){
		return this.workflowAttachmentDao.get(id);
	}

	public List<WorkflowAttachment> getAttachmentsExceptTaskName(
			String workflowId, String taskName) {
		return workflowAttachmentDao.getAttachmentsExceptTaskName(workflowId,ContextUtils.getCompanyId(),taskName);
	}

	public List<WorkflowAttachment> getAttachmentsExceptTaskMode(
			String workflowId, TaskProcessingMode taskMode) {
		return workflowAttachmentDao.getAttachmentsExceptTaskMode(workflowId, ContextUtils.getCompanyId(), taskMode);
	}

	public List<WorkflowAttachment> getAttachments(String workflowId,
			String taskName) {
		return workflowAttachmentDao.getAttachments(workflowId, ContextUtils.getCompanyId(), taskName);
	}

	public List<WorkflowAttachment> getAttachments(String workflowId,
			TaskProcessingMode taskMode) {
		Assert.notNull(ContextUtils.getCompanyId(), "companyId不能为null");
		return workflowAttachmentDao.getAttachments(workflowId, ContextUtils.getCompanyId(), taskMode);
	}
	public List<WorkflowAttachment> getAttachmentsByCustomField(
			String workflowId, String customField) {
		return workflowAttachmentDao.getAttachmentsByCustomField(workflowId,customField);
	}
	public List<WorkflowAttachment> getAttachmentsExceptCustomField(
			String workflowId, String customField) {
		return workflowAttachmentDao.getAttachmentsExceptCustomField(workflowId,customField);
	}
	
}
