package com.norteksoft.wf.engine.dao;

import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.wf.engine.entity.WorkflowDefinitionFile;

@Repository
public class WorkflowDefinitionFileDao extends HibernateDao<WorkflowDefinitionFile, Long>{

	public WorkflowDefinitionFile getWfDefinitionFileByWfdId(Long wfdId){
		return this.findUniqueBy("wfDefinitionId", wfdId);
	}
	
	public void deleteDefinitionFileByWfdId(Long wfdId){
		this.createQuery("delete WorkflowDefinitionFile wfdf where wfdf.wfDefinitionId = ?", wfdId).executeUpdate();
	}
	
	public WorkflowDefinitionFile getWfDefinitionFileByWfdId(Long wfdId, Long companyId){
		return this.findUnique("from WorkflowDefinitionFile wfdf where wfdf.wfDefinitionId = ? and wfdf.companyId = ?", wfdId, companyId);
	}
}
