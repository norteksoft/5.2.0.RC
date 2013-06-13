package com.norteksoft.product.api.entity;

import java.io.Serializable;
import java.util.List;

import com.norteksoft.product.api.utils.BeanUtil;

public class OptionGroup implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String code; // 选项组编号
	private String workCode;//业务编码
	private String name; // 页面上显示的NAME
	private String description; // 项目组描述
	private Long systemId;//系统id（所属系统）
	//entity
	private Long id;
	private boolean deleted;
	private Long companyId;

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getWorkCode() {
		return workCode;
	}
	public void setWorkCode(String workCode) {
		this.workCode = workCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getSystemId() {
		return systemId;
	}
	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
}
