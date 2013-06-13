package com.norteksoft.acs.entity.organization;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.norteksoft.acs.entity.IdEntity;
import com.norteksoft.acs.entity.authorization.RoleWorkgroup;

/**
 * 工作组管理
 * 
 */
@Entity
@Table(name = "ACS_WORKGROUP")
public class Workgroup extends IdEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 公司属性
	 */
	private Company company = null;

	/**
	 * 工作组编号
	 */
	private String code;

	/**
	 * 工作组名称
	 */
	private String name;

	/**
	 * 工作组描述
	 */
	private String description;
	
	private Integer weight; //权重

	/**
	 *设置工作组和(用户-工作组)中间表一对多的关系
	 */
	private Set<WorkgroupUser> workgroupUsers = new HashSet<WorkgroupUser>(0);
	
	private Set<RoleWorkgroup> roleWorkgroups = new HashSet<RoleWorkgroup>(0);

	
	/**
	 * 公司和工作组的一对多关系
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
  
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 设置用户与(工作组-用户)中间表关系(一对多)
	 */
	@OneToMany(mappedBy="workgroup")
	@OrderBy("id")
	public Set<WorkgroupUser> getWorkgroupUsers() {
		return workgroupUsers;
	}

	public void setWorkgroupUsers(Set<WorkgroupUser> workgroupUsers) {
		this.workgroupUsers = workgroupUsers;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@OneToMany(mappedBy="workgroup")
	public Set<RoleWorkgroup> getRoleWorkgroups() {
		return roleWorkgroups;
	}

	public void setRoleWorkgroups(Set<RoleWorkgroup> roleWorkgroups) {
		this.roleWorkgroups = roleWorkgroups;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	
}