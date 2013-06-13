package com.norteksoft.acs.entity.organization;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.norteksoft.acs.entity.IdEntity;

/**
 * 公司实体
 */
@Entity
@Table(name = "ACS_COMPANY")
public class Company extends IdEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	// 编码
	private String code;
	
	// 公司名称
	private String name;
	
	//国家
	private String country;
	
	//城市
	private String city;

	// 公司地址
	private String address;
	
	//创建时间
	private Date createdDate;

	// 所属行业
	private String industry;

	// 公司人数
	private Integer peopleNumber;

	// 公司电话
	private String telephone;
	
	// 公司传真
	private String facsimile;

	// 邮件地址
	private String postAddress;

	// 邮编
	private String postCode;

	// 单位备注
	private String remark;
	

	// 子公司集合(一对多)
	private Set<Company> children = new HashSet<Company>(0);

	// 父公司(多对一)
	private Company parent = null;

	// 部门(一对多)
	private Set<Department> departments = new HashSet<Department>(0);

	private Long companyId;
	
	@Column(name="FK_COMPANY_ID")
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}


	/*
	 * 一对多并在多端维护关系
	 */
	@OneToMany(mappedBy="parent")
	public Set<Company> getChildren() {
		return children;
	}

	public void setChildren(Set<Company> children) {
		this.children = children;
	}

	/*
	 * 多对一
	 */
	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="FK_PARENT_ID")
	public Company getParent() {
		return parent;
	}

	public void setParent(Company parent) {
		this.parent = parent;
	}

	/*
	 * 一对多并在多端维护关系
	 */
	@OneToMany(mappedBy="company")
	@OrderBy("weight desc")
	public Set<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(Set<Department> departments) {
		this.departments = departments;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public Integer getPeopleNumber() {
		return peopleNumber;
	}

	public void setPeopleNumber(Integer peopleNumber) {
		this.peopleNumber = peopleNumber;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getFacsimile() {
		return facsimile;
	}

	public void setFacsimile(String facsimile) {
		this.facsimile = facsimile;
	}

	public String getPostAddress() {
		return postAddress;
	}

	public void setPostAddress(String postAddress) {
		this.postAddress = postAddress;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}


	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
