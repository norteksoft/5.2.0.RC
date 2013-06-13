package com.norteksoft.acs.entity.authorization;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.norteksoft.acs.entity.IdEntity;

/**
 * 资源管理(权限管理)
 * 
 * @author chenchenghu
 * 
 */
@Entity
@Table(name = "ACS_FUNCTION")
public class Function extends IdEntity {

	private static final long serialVersionUID = 1L;

	private String code;
	private String name;
	private String path;
	private FunctionGroup functionGroup = null;
	private Set<RoleFunction> roleFunctions;
	private BusinessSystem businessSystem = null;

	@OneToMany(mappedBy = "function")
	public Set<RoleFunction> getRoleFunctions() {
		return roleFunctions;
	}

	public void setRoleFunctions(Set<RoleFunction> roleFunctions) {
		this.roleFunctions = roleFunctions;
	}

	
	/**
	 * 权限和权限组一对多关系
	 * 
	 * @param functionGroup
	 */
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "FK_FUNCTION_GROUP_ID")
	public FunctionGroup getFunctionGroup() {
		return functionGroup;
	}

	public void setFunctionGroup(FunctionGroup functionGroup) {
		this.functionGroup = functionGroup;
	}

	@ManyToOne
	@JoinColumn(name = "FK_SYSTEM_ID")
	public BusinessSystem getBusinessSystem() {
		return businessSystem;
	}

	public void setBusinessSystem(BusinessSystem businessSystem) {
		this.businessSystem = businessSystem;
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
