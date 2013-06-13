package com.norteksoft.acs.entity.organization;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.norteksoft.acs.entity.IdEntity;

/**
 * 创建时间:2009-3-27
 * 类说明:工作组与用户中间表
 */
@Entity
@Table(name = "ACS_WORKGROUP_USER")
public class WorkgroupUser extends IdEntity{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 公司主键
	 */
	private Long companyId;
	
	/**
	 * 用户属性
	 */
	private User user=null;
	
	/**
	 * 工作组属性
	 */
	private Workgroup workgroup=null;
	
   
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
	    * 中间表和工作组多对一关系
	    */
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="FK_WORKGROUP_ID")
	public Workgroup getWorkgroup() {
		return workgroup;
	}

	public void setWorkgroup(Workgroup workgroup) {
		this.workgroup = workgroup;
	}
	
}
