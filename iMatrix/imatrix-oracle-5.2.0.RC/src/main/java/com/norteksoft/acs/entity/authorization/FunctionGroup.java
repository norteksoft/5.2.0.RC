package com.norteksoft.acs.entity.authorization;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.norteksoft.acs.entity.IdEntity;

/**
 * 功能包
 * 
 *@author 陈成虎 2009-3-9下午01:57:25
 */
@Entity
@Table(name = "ACS_FUNCTION_GROUP")
public class FunctionGroup extends IdEntity { 
	private static final long serialVersionUID = 1L;
	
	private String code;
	private String name;
	private Set<Function> functions = new HashSet<Function>(0);
	private BusinessSystem businessSystem = null;
   

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

	/**
	 * 权限和业务系统的多对一的关系
	 * @return
	 */
	@ManyToOne
	@JoinColumn(name="FK_SYSTEM_ID")
	public BusinessSystem getBusinessSystem() {
		return businessSystem;
	}

	public void setBusinessSystem(BusinessSystem businessSystem) {
		this.businessSystem = businessSystem;
	}
    
	/**
	 * 权限组和权限和一对多关系
	 * @return
	 */
    @OneToMany(mappedBy="functionGroup")
	public Set<Function> getFunctions() {
		return functions;
	}

	public void setFunctions(Set<Function> functions) {
		this.functions = functions;
	}

}