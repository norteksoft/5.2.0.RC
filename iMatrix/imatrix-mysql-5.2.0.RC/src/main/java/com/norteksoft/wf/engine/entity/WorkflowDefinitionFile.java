package com.norteksoft.wf.engine.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

@Entity
@Table(name="WF_DEFINITION_FILE")
public class WorkflowDefinitionFile extends IdEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Lob
    @Column(columnDefinition="LONGTEXT", nullable=true)
	private String document;                         //xml文件
	
	@Column(name="FK_WF_DEFINITION_ID")
	private Long wfDefinitionId;                     //流程定义ID

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public Long getWfDefinitionId() {
		return wfDefinitionId;
	}

	public void setWfDefinitionId(Long wfDefinitionId) {
		this.wfDefinitionId = wfDefinitionId;
	}
}
