package com.norteksoft.product.api.entity;

import java.io.Serializable;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.product.enumeration.DataState;

public class FormView implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//entity
	private Long id;
	private boolean deleted;
	private Long companyId;
	//FormView
	private String html;
	private Integer version;//如果版本号为0，则保存的时候需要重新生成版本号；否则，保持原来的版本
	private DataState formState;//表单的状态
	//view
    private String code;//编码
	private String name;//名称
	private DataTable dataTable;//数据表
	private String remark;//备注
	private Boolean standard;//是否是标准的视图
	private Long menuId;//菜单列表
	
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
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public DataState getFormState() {
		return formState;
	}
	public void setFormState(DataState formState) {
		this.formState = formState;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public DataTable getDataTable() {
		return dataTable;
	}
	public void setDataTable(DataTable dataTable) {
		this.dataTable = dataTable;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Boolean getStandard() {
		return standard;
	}
	public void setStandard(Boolean standard) {
		this.standard = standard;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	
}
