package com.norteksoft.acs.entity.authorization;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.norteksoft.acs.entity.IdEntity;
import com.norteksoft.acs.entity.organization.User;

/**
 * 用户、角色中间表实体
 * 
 */
@Entity
@Table(name = "ACS_ROLE_USER")
public class RoleUser extends IdEntity {
	private static final long serialVersionUID = 1L;
	private Role role;
	private User user;
	private Long companyId;
	private Long consigner; //分配人ID，即：谁给他分配的权限

	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="FK_ROLE_ID")
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="FK_USER_ID")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Column(name="FK_COMPANY_ID")
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getConsigner() {
		return consigner;
	}

	public void setConsigner(Long consigner) {
		this.consigner = consigner;
	}
	
}
