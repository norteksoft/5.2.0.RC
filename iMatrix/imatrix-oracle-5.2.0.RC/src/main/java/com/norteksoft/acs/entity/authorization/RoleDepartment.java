package com.norteksoft.acs.entity.authorization;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.norteksoft.acs.entity.IdEntity;
import com.norteksoft.acs.entity.organization.Department;

/**
 * 角色部门中间实体 
 */
@Entity
@Table(name = "ACS_ROLE_DEPARTMENT")
public class RoleDepartment extends IdEntity {
	private static final long serialVersionUID = 1L;
	private Role role;
	private Department department;
	private Long companyId;

	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="FK_ROLE_ID")
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	
	@Column(name="FK_COMPANY_ID")
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="FK_DEPARTMENT_ID")
	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}
	
}
