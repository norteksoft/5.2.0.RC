package com.norteksoft.wf.engine.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

@Entity
@Table(name="WF_TEMPLATE_FILE")
public class DocumentTemplateFile extends IdEntity implements Serializable{
	private static final long serialVersionUID = -481152974988137613L;
	
	private Long templateId;
	
	@Lob 
	@Basic(fetch = FetchType.LAZY) 
	@Column(name = "FILEBODY", columnDefinition = "LONGBLOB",nullable=true) 
	private byte[] fileBody;

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public byte[] getFileBody() {
		return fileBody;
	}

	public void setFileBody(byte[] fileBody) {
		this.fileBody = fileBody;
	}
}
