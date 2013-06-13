package com.norteksoft.acs.entity.authorization;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.norteksoft.acs.entity.IdEntity;
import com.norteksoft.acs.entity.organization.Workgroup;
@Entity
@Table(name = "ACS_ROLE_WORKGROUP")

public class RoleWorkgroup extends IdEntity{
	
	private static final long serialVersionUID = 1L;
	
	private Role role;
	
	private Workgroup workgroup;
	
	private Long companyId;


	@Column(name = "FK_COMPANY_ID")
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name="FK_ROLE_ID")
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name="FK_WORKGROUP_ID")
	public Workgroup getWorkgroup() {
		return workgroup;
	}

	public void setWorkgroup(Workgroup workgroup) {
		this.workgroup = workgroup;
	}
	
}
