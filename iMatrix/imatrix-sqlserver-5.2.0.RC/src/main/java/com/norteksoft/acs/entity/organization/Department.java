package com.norteksoft.acs.entity.organization;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.norteksoft.acs.entity.IdEntity;
import com.norteksoft.acs.entity.authorization.RoleDepartment;

/**
 * 
 */
@Entity
@Table(name = "ACS_DEPARTMENT")
public class Department extends IdEntity {
	private static final long serialVersionUID = 1L;

	//权重
	private Integer weight=1; 
	
	//部门编号
	private String code;
	
	//部门名称
	private String name;
	
	//部门简称
	private String shortTitle;

	//部门描述
	private String summary;

	//公司属性
	private Company company = null;

	//子公司
	private Department parent = null;

	//子部门
	private Set<Department> children = new HashSet<Department>(0);

	//设置部门和(用户-部门)中间表一对多的关系
	private Set<DepartmentUser> departmentUsers = new HashSet<DepartmentUser>(0);

	private Set<RoleDepartment> roleDepartments = new HashSet<RoleDepartment>(0);
	
	/**
	 * 公司和部门的一对多关系
	 * 
	 * @param company
	 */
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "FK_COMPANY_ID")
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSummary() {
		if(StringUtils.isEmpty(summary)){
			return name;
		}else{
			return summary;
		}
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "FK_PARENT_DEPARTMENT_ID")
	public Department getParent() {
		return parent;
	}

	public void setParent(Department parent) {
		this.parent = parent;
	}

	@OneToMany(mappedBy = "parent")
	public Set<Department> getChildren() {
		return children;
	}

	public void setChildren(Set<Department> children) {
		this.children = children;
	}

	/**
	 * 设置用户与(部门-用户)中间表关系(一对多)
	 */
	@OneToMany(mappedBy = "department")
	public Set<DepartmentUser> getDepartmentUsers() {
		return departmentUsers;
	}

	public void setDepartmentUsers(Set<DepartmentUser> departmentUsers) {
		this.departmentUsers = departmentUsers;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@OneToMany(mappedBy = "department")
	public Set<RoleDepartment> getRoleDepartments() {
		return roleDepartments;
	}

	public void setRoleDepartments(Set<RoleDepartment> roleDepartments) {
		this.roleDepartments = roleDepartments;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public String getShortTitle() {
		if(StringUtils.isEmpty(shortTitle)){
			return name;
		}else{
			return shortTitle;
		}
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}
	
	@Transient
	public String getDefaultShortTitle(){
		if(shortTitle==null){
			return name.substring(0,1);
		}else{
			return shortTitle;
		}
	}
}
