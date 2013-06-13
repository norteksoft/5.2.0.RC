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
@Table(name="WF_DEFINITION_TEMPLATE_FILE")
public class WorkflowDefinitionTemplateFile extends IdEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Long templateId;
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
    @Column(columnDefinition="CLOB", nullable=true)
	private String xml;                         //xml文件

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
}
