package com.norteksoft.wf.engine.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.wf.engine.entity.Document;

@Repository
public class OfficeDao extends HibernateDao<Document, Long>{

	public List<Document> getDocumentsExceptTaskName(String workflowId2,
			String taskName2) {
		String hql = "from Document d where d.workflowId=? and d.taskName<>? order by d.createdTime desc";
		return this.find(hql, workflowId2,taskName2);
	}

	public List<Document> getDocumentsExceptTaskMode(String workflowId2,
			TaskProcessingMode taskMode2) {
		String hql = "from Document d where d.workflowId=? and d.taskMode<>? order by d.createdTime desc";
		return this.find(hql, workflowId2,taskMode2);
	}

	public List<Document> getDocumentsExceptCustomField(String workflowId2,
			String customField) {
		String hql = "from Document d where d.workflowId=? and d.customField<>? order by d.createdTime desc";
		return this.find(hql, workflowId2,customField);
	}

	public List<Document> getDocumentsByCustomField(String workflowId2,
			String customField) {
		String hql = "from Document d where d.workflowId=? and d.customField=? order by d.createdTime desc";
		return this.find(hql, workflowId2,customField);
	}

	public List<Document> getDocuments(String workflowId2,
			TaskProcessingMode taskMode2) {
		String hql = "from Document d where d.workflowId=? and d.taskMode=? order by d.createdTime desc";
		return this.find(hql, workflowId2,taskMode2);
	}

	public List<Document> getDocuments(String workflowId2, String taskName2) {
		String hql = "from Document d where d.workflowId=? and d.taskName=? order by d.createdTime desc";
		return this.find(hql, workflowId2,taskName2);
	}

	public List<Document> getDocuments(Long taskId, Long companyId) {
		String hql = "from Document d where d.taskId=? and d.companyId=? order by d.createdTime desc";
		return this.find(hql, taskId,companyId);
	}
	
	
	
}
