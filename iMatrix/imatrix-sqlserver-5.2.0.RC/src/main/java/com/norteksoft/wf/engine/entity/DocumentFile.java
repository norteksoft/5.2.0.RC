package com.norteksoft.wf.engine.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

@Entity
@Table(name="WF_DOCUMENT_FILE")
public class DocumentFile extends IdEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	@Lob 
	@Column(name = "FILEBODY", columnDefinition = "image",nullable=true) 
	private byte[] fileBody;
	
	@Column(name="FK_WF_DOCUMENT_ID",unique=true)
	private Long documentId;//文档描述信息实体id

	public byte[] getFileBody() {
		return fileBody;
	}

	public void setFileBody(byte[] fileBody) {
		this.fileBody = fileBody;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}
	
	
}
