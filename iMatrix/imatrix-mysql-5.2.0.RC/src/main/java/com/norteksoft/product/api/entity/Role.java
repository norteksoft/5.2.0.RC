package com.norteksoft.product.api.entity;

import java.util.Set;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.RoleDepartment;
import com.norteksoft.acs.entity.authorization.RoleFunction;
import com.norteksoft.acs.entity.authorization.RoleGroup;
import com.norteksoft.acs.entity.authorization.RoleUser;
import com.norteksoft.acs.entity.authorization.RoleWorkgroup;
import com.norteksoft.product.api.utils.BeanUtil;


public class Role{
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private boolean deleted;
	private String code;
	private String name;
	private BusinessSystem businessSystem;
	private RoleGroup roleGroup;
	private com.norteksoft.acs.entity.authorization.Role parentRole;
	private Long companyId;
	private Integer weight;
	
    public Role(){}
	public Role(String code, String name){
		this.name = name;
		this.code = code;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public BusinessSystem getBusinessSystem() {
		return businessSystem;
	}
	public void setBusinessSystem(BusinessSystem businessSystem) {
		this.businessSystem = businessSystem;
	}
	public RoleGroup getRoleGroup() {
		return roleGroup;
	}
	public void setRoleGroup(RoleGroup roleGroup) {
		this.roleGroup = roleGroup;
	}
	public Role getParentRole() {
		return BeanUtil.turnToModelRole(parentRole);
	}
	public void setParentRole(com.norteksoft.acs.entity.authorization.Role parentRole) {
		this.parentRole = parentRole;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Integer getWeight() {
		return weight;
	}
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}