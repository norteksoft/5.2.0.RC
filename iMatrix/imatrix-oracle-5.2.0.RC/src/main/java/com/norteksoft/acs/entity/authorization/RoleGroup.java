package com.norteksoft.acs.entity.authorization;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.norteksoft.acs.entity.IdEntity;

/**
 * 角色组实体
 * 
 */

@Entity
@Table(name = "ACS_ROLE_GROUP")
public class RoleGroup extends IdEntity {
	private static final long serialVersionUID = 1L;

	// 名称
	private String name;

	// 角色
	private Set<Role> roles = new HashSet<Role>(0);

	// 业务系统
	private BusinessSystem businessSystem = null;

	private Long companyId;

	@Column(name = "FK_COMPANY_ID")
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*
	 * 一对多并在多端维护关系
	 */
	@OneToMany(mappedBy = "roleGroup")
	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "FK_SYSTEM_ID")
	public BusinessSystem getBusinessSystem() {
		return businessSystem;
	}

	public void setBusinessSystem(BusinessSystem businessSystem) {
		this.businessSystem = businessSystem;
	}
   
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
