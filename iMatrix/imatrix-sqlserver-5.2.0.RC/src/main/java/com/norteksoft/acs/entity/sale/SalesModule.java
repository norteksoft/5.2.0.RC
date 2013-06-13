package com.norteksoft.acs.entity.sale;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.norteksoft.acs.entity.IdEntity;
import com.norteksoft.acs.entity.authorization.Function;

/**
 * 销售包实体
 */
@Entity
@Table(name = "ACS_SALES_MODULES")
public class SalesModule extends IdEntity {
	private static final long serialVersionUID = 1L;

	//销售包名称
	private String moduleName;
	
	//所在产品
	private Set<Product> products = null;
	
	//所属系统
	private Long systemId;
	
	//设置产品和销售的关系
	private Set<Function> functions = new HashSet<Function>(0);
	

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	@ManyToMany(mappedBy="salesModuels")	
	public Set<Product> getProducts() {
		return products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}
	
	@ManyToMany(targetEntity=Function.class, cascade = { CascadeType.PERSIST })
	@JoinTable(name = "ACS_FUNCS_SALES", joinColumns = { @JoinColumn(name = "FK_SALESMODUEL_ID") }, inverseJoinColumns = { @JoinColumn(name = "FK_FUNCTION_ID") })
	@OrderBy("id")
	public Set<Function> getFunctions() {
		return functions;
	}

	public void setFunctions(Set<Function> functions) {
		this.functions = functions;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Column(name="FK_BIZSYSTEM_ID")
	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}

}
