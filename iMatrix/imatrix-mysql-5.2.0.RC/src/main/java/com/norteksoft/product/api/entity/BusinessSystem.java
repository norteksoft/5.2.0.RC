package com.norteksoft.product.api.entity;


public class BusinessSystem {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private boolean deleted;
	private Long companyId;
	private String code;//系统编号
	private String name;//业务系统名称
	private String path;//业务系统访问路径
	private Boolean product = false;//是否是产品
	private String parentCode;//父系统编码
	private Boolean imatrixable=false;//是否是平台,只有是底层imatrix系统时才会是true
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
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Boolean getProduct() {
		return product;
	}
	public void setProduct(Boolean product) {
		this.product = product;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public Boolean getImatrixable() {
		return imatrixable;
	}
	public void setImatrixable(Boolean imatrixable) {
		this.imatrixable = imatrixable;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
