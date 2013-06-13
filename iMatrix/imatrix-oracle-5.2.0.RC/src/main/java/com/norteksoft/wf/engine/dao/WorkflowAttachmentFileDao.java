package com.norteksoft.wf.engine.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.wf.engine.entity.WorkflowAttachmentFile;

@Repository
public class WorkflowAttachmentFileDao extends HibernateDao<WorkflowAttachmentFile, Long>{
	
  public WorkflowAttachmentFile getAttachmentFileByAttachmentId(Long attachmentId){
	  return findUnique("from WorkflowAttachmentFile af where af.attachmentId=?", attachmentId);
  }	
  
  public List<WorkflowAttachmentFile> getAttachmentFiles(){
	  return findNoCompanyCondition("from WorkflowAttachmentFile af ");
  }	
}
