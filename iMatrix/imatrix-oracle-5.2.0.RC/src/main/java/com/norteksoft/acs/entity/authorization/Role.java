package com.norteksoft.acs.entity.authorization;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.norteksoft.acs.entity.IdEntity;

/**
 * 角色实体
 * 
 */
@Entity
@Table(name = "ACS_ROLE")
public class Role extends IdEntity{
	private static final long serialVersionUID = 1L;

	private String code;
	//角色名称
	private String name;
	
	//角色和业务系统的多对一关系
	private BusinessSystem businessSystem;

	//角色和角色组多对一的关系
	private RoleGroup roleGroup;

	private Set<RoleUser> roleUsers = new HashSet<RoleUser>(0);
	
	private Set<RoleDepartment> roleDepartments = new HashSet<RoleDepartment>(0);
	
	private Set<RoleFunction> roleFunctions = new HashSet<RoleFunction>(0);
	
	private Set<RoleWorkgroup> roleWorkgroups = new HashSet<RoleWorkgroup>(0);

	private Role parentRole;
	
	private Set<Role> subRoles = new HashSet<Role>();
	
	private Long companyId;
	
	private Integer weight=0;//权重
	
	public Role(){}
	
	
	public Role(String code, String name){
		this.name = name;
		this.code = code;
	}
	
	@OneToMany(mappedBy="role")
	public Set<RoleFunction> getRoleFunctions() {
		return roleFunctions;
	}

	public void setRoleFunctions(Set<RoleFunction> roleFunctions) {
		this.roleFunctions = roleFunctions;
	}

	private Set<RoleWorkgroup> workGroups = new HashSet<RoleWorkgroup>(0);
	
	@OneToMany(mappedBy="role")
	public Set<RoleWorkgroup> getWorkGroups() {
		return workGroups;
	}

	public void setWorkGroups(Set<RoleWorkgroup> workGroups) {
		this.workGroups = workGroups;
	}

	@OneToMany(mappedBy = "role")
	public Set<RoleUser> getRoleUsers() {
		return roleUsers;
	}

	public void setRoleUsers(Set<RoleUser> roleUsers) {
		this.roleUsers = roleUsers;
	}
	
	@OneToMany(mappedBy = "role")
	public Set<RoleDepartment> getRoleDepartments() {
		return roleDepartments;
	}

	public void setRoleDepartments(Set<RoleDepartment> roleDepartments) {
		this.roleDepartments = roleDepartments;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*
	 * 多对一(业务系统)
	 */
	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
	@JoinColumn(name="FK_SYSTEM_ID")
	public BusinessSystem getBusinessSystem() {
		return businessSystem;
	}

	public void setBusinessSystem(BusinessSystem businessSystem) {
		this.businessSystem = businessSystem;
	}

	/*
	 * 多对一(角色组)
	 */
	@ManyToOne(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name="FK_ROLE_GROUP_ID")
	public RoleGroup getRoleGroup() {
		return roleGroup;
	}

	public void setRoleGroup(RoleGroup roleGroup) {
		this.roleGroup = roleGroup;
	}
	
	@OneToMany(mappedBy="parentRole")
	@OrderBy("id")
	public Set<Role> getSubRoles() {
		return subRoles;
	}

	public void setSubRoles(Set<Role> subRoles) {
		this.subRoles = subRoles;
	}

	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="FK_PARENT_ROLE_ID")
	public Role getParentRole() {
		return parentRole;
	}

	
	public void setParentRole(Role parentRole) {
		this.parentRole = parentRole;
	}

	@OneToMany(mappedBy="role")
	public Set<RoleWorkgroup> getRoleWorkgroups() {
		return roleWorkgroups;
	}

	public void setRoleWorkgroups(Set<RoleWorkgroup> roleWorkgroups) {
		this.roleWorkgroups = roleWorkgroups;
	}

	@Column(name="FK_COMPANY_ID")
	public Long getCompanyId() {
		return companyId;
	}


	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}


	public Integer getWeight() {
		return weight;
	}


	public void setWeight(Integer weight) {
		this.weight = weight;
	}
}