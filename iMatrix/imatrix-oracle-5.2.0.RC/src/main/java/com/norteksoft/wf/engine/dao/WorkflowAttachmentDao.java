package com.norteksoft.wf.engine.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.wf.engine.entity.WorkflowAttachment;

@Repository
public class WorkflowAttachmentDao extends HibernateDao<WorkflowAttachment, Long>{
	
	public void deleteAttachment(String workflowId,Long companyId){
		createQuery("delete from WorkflowAttachment u where  u.workflowId = ? and u.companyId = ? ", workflowId,companyId).executeUpdate();
	}
	public List<WorkflowAttachment> getAttachments(String workflowId, Long companyId) {
		return this.find("from WorkflowAttachment u where u.companyId=? and u.workflowId=? order by u.createdTime desc", companyId,workflowId);
	}
	
	public void updateAttachment(Long id,String workflowId){
		createQuery("update WorkflowAttachment u set u.workflowId=? where  u.id = ?  ",workflowId, id).executeUpdate();
	}
	public List<WorkflowAttachment> getAttachments(String workflowId, Long companyId,
			TaskProcessingMode taskMode) {
		StringBuilder hql = new StringBuilder("from WorkflowAttachment u where u.companyId=? and u.workflowId=?  and u.taskMode=? order by u.createdTime desc");
		return this.find(hql.toString(),companyId,workflowId,taskMode);
	}
	public List<WorkflowAttachment> getAttachments(String workflowId, Long companyId,
			String  taskName) {
			StringBuilder hql = new StringBuilder("from WorkflowAttachment u where u.companyId=? and u.workflowId=?  and u.taskName=? order by u.createdTime desc");
			return this.find(hql.toString(),companyId,workflowId,taskName);
	}
	public List<WorkflowAttachment> getAttachments(Long taskId, Long companyId) {
		StringBuilder hql = new StringBuilder("from WorkflowAttachment u where u.companyId=? and u.taskId=?   order by u.createdTime desc");
		return this.find(hql.toString(),companyId,taskId);
	}
	
	public List<WorkflowAttachment> getAttachmentsExceptTaskMode(String workflowId,
			Long companyId, TaskProcessingMode taskMode) {
		StringBuilder hql = new StringBuilder("from WorkflowAttachment u where u.companyId=? and u.workflowId=?  and u.taskMode<>?  order by  u.createdTime desc");
		return find(hql.toString(), companyId,workflowId,taskMode);
	}
	public List<WorkflowAttachment> getAttachmentsExceptTaskName(String workflowId,
			Long companyId, String taskName) {
		StringBuilder hql = new StringBuilder("from WorkflowAttachment u where u.companyId=? and u.workflowId=?  and u.taskName<>?  order by  u.createdTime desc");
		return find(hql.toString(), companyId,workflowId,taskName);
	}
	public List<WorkflowAttachment> getAttachmentsByCustomField(
			String workflowId, String customField) {
		String hql = "from WorkflowAttachment d where d.workflowId=? and d.customField=? order by d.createdTime desc";
		return this.find(hql, workflowId,customField);
	}
	public List<WorkflowAttachment> getAttachmentsExceptCustomField(
			String workflowId, String customField) {
		String hql = "from WorkflowAttachment d where d.workflowId=? and d.customField<>? order by d.createdTime desc";
		return this.find(hql, workflowId,customField);
	}
}
