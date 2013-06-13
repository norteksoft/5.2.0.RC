package com.norteksoft.product.api.entity;

import java.io.Serializable;

public class WorkflowDefinition implements Serializable{
	private static final long serialVersionUID = 1L;
	private String name;
	private Long id;
	private Integer version;
	private String code;
	private String formCode;//表单code
	private Integer fromVersion;//表单版本
	
	public WorkflowDefinition() {
	}
	public WorkflowDefinition(String name, Long id, Integer version,
			String code) {
		this.name = name;
		this.id = id;
		this.version = version;
		this.code = code;
	}
	public WorkflowDefinition(String name, Integer version, String code) {
		this.name = name;
		this.version = version;
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getFormCode() {
		return formCode;
	}
	public void setFormCode(String formCode) {
		this.formCode = formCode;
	}
	public Integer getFromVersion() {
		return fromVersion;
	}
	public void setFromVersion(Integer fromVersion) {
		this.fromVersion = fromVersion;
	}
	
}
