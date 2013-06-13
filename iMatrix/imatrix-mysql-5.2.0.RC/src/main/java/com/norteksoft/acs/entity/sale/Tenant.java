package com.norteksoft.acs.entity.sale;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.norteksoft.acs.entity.organization.Company;

/**
 * 租户实体
 * 
 */
@Entity
@Table(name = "ACS_TENANTS")

public class Tenant implements Serializable{

	private static final long serialVersionUID = 1L;

	//主键
	private Long id;
	
	private boolean deleted = false;
	
	// 租户名称
	private String tenantName;

	// 联系人
	private String linkman;

	// 联系人电话
	private String telephone;

	// 联系人 emil
	private String email;

	// 租户类型 0==试用租户，1==注册租户
	private Integer tenantType;

	// 租户的顶级公司
	private Company company;

	// 租户流水账
	//private Set<Journal> journal = new HashSet<Journal>(0);

	// 租户订单
	//private Set<Subsciber> subscibers = new HashSet<Subsciber>(0);
	
	@Id
	@GeneratedValue(generator="foreign")
	@GenericGenerator(name="foreign", strategy="foreign", parameters={
			@Parameter(name="property" ,value="company")
	})
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public String getLinkman() {
		return linkman;
	}

	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getTenantType() {
		return tenantType;
	}

	public void setTenantType(Integer tenantType) {
		this.tenantType = tenantType;
	}

	@OneToOne(cascade=CascadeType.ALL)
	@PrimaryKeyJoinColumn
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	//================== 关系设置 ======================
//	@OneToMany(mappedBy="tenant")
//	@OrderBy("id")
//	public Set<Journal> getJournal() {
//		return journal;
//	}
//
//	public void setJournal(Set<Journal> journal) {
//		this.journal = journal;
//	}

//	@OneToMany(mappedBy="tenant")
//	@OrderBy("id")
//	public Set<Subsciber> getSubscibers() {
//		return subscibers;
//	}
//
//	public void setSubscibers(Set<Subsciber> subscibers) {
//		this.subscibers = subscibers;
//	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}