package com.norteksoft.wf.engine.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

@Entity
@Table(name="WF_DEFINITION_TEMPLATE")
public class WorkflowDefinitionTemplate extends IdEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static final String CUSTOM_PROCESS_TEMPLATE = "custom_process_template";//自由流模板
	
	public static final String BLANK_TEMPLATE = "blank_template";//空白模板
	
	public static final String PREDEFINED_PROCESS_TEMPLATE = "predefined_process_template";//预定义模板
	
	private String name;//模版名字
	
	
	private String previewImageName;//预览图名称
	
	private String previewImage;//预览图
	
	private Long typeId;//所属流程类型id
	
	private String templateType;//模板类型
	
	@Column(length=600)
	private String description;//文件描述
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPreviewImageName() {
		return previewImageName;
	}

	public void setPreviewImageName(String previewImageName) {
		this.previewImageName = previewImageName;
	}

	public String getPreviewImage() {
		return previewImage;
	}

	public void setPreviewImage(String previewImage) {
		this.previewImage = previewImage;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
