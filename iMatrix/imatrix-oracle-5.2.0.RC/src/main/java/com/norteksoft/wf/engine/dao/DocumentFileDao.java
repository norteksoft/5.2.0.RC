package com.norteksoft.wf.engine.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.wf.engine.entity.DocumentFile;

@Repository
public class DocumentFileDao extends HibernateDao<DocumentFile, Long>{
	
	public List<DocumentFile> getDocumentFiles(){
		return this.findNoCompanyCondition("from DocumentFile df ");
	}
	
	 public DocumentFile getDocumentFileByDocumentId(Long documentId){
		  List<DocumentFile> documentFiles= find("from DocumentFile af where af.documentId=?", documentId);
		  if(documentFiles.size()>0)return documentFiles.get(0);
		  return null;
	  }
}
