package com.norteksoft.acs.entity.authorization;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.norteksoft.acs.entity.IdEntity;

/**
 * 业务系统管理
 * 
 * @author 陈成虎
 * 
 */
@Entity
@Table(name = "ACS_BUSINESS_SYSTEM")
public class BusinessSystem extends IdEntity {
	private static final long serialVersionUID = 1L;
	
	//系统编号
	private String code;

	//业务系统名称
	private String name;

	//业务系统访问路径
	private String path;
	
	//是否是产品
	private Boolean product = false;

	//设置业务系统和角色(父角色)的关系（一对多）
	private Set<Role> roles = new HashSet<Role>();

	//设置业务系统和功能包一对多的关系
	private Set<FunctionGroup> functionGroups = new HashSet<FunctionGroup>(0);

	//设置业务系统和角色组一对多的关系
	private Set<RoleGroup> roleGroups = new HashSet<RoleGroup>(0);
	
	private String parentCode;//父系统编码
	private Boolean imatrixable=false;//是否是平台,只有是底层imatrix系统时才会是true
	

	@OneToMany(mappedBy = "businessSystem", cascade = CascadeType.ALL)
	public Set<FunctionGroup> getFunctionGroups() {
		return functionGroups;
	}

	public void setFunctionGroups(Set<FunctionGroup> functionGroups) {
		this.functionGroups = functionGroups;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 业务系统和角色多对多的关系
	 * 
	 * @return
	 */
	@OneToMany(mappedBy = "businessSystem")
	@OrderBy("id")
	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * 设置业务系统和角色组的一对多的关系
	 * 
	 * @return
	 */
	@OneToMany(mappedBy = "businessSystem")
	public Set<RoleGroup> getRoleGroups() {
		return roleGroups;
	}

	public void setRoleGroups(Set<RoleGroup> roleGroups) {
		this.roleGroups = roleGroups;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean isProduct() {
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
	
	public boolean equals(BusinessSystem system) {
		if(system.getCode().equals(code))return true;
		return false;
	}

}
