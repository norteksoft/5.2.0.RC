package com.norteksoft.acs.entity.organization;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.norteksoft.acs.entity.IdEntity;

/**
 * 类说明:部门与用户中间表
 */
@Entity
@Table(name = "ACS_DEPARTMENT_USER")
public class DepartmentUser extends IdEntity{
	
	private static final long serialVersionUID = 1L;
	
	private Long companyId;
	
	private User user=null;
	
	private Department department=null;
	
	
	@Column(name = "FK_COMPANY_ID")
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	
	   /**
	    * 中间表和用户多对一关系
	    */
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="FK_USER_ID")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
    
	   /**
	    * 中间表和部门多对一关系
	    */
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) 
	@JoinColumn(name="FK_DEPARTMENT_ID")
	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

}
