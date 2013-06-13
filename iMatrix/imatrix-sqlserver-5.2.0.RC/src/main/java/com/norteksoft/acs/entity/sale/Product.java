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

/**
 * 产品实体
 * 
 */
@Entity
@Table(name = "ACS_PRODUCTS")
public class Product extends IdEntity {
	private static final long serialVersionUID = 1L;

	//产品名称
	private String productName;

	//产品版本
	private String version;

	//产品包含的销售模块
	private Set<SalesModule> salesModuels = new HashSet<SalesModule>(0);
	
	//所属系统
	private Long systemId;

	
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@ManyToMany(cascade={CascadeType.PERSIST })
	@JoinTable(name = "ACS_PRODUCTS_MODULE", joinColumns = { @JoinColumn(name = "FK_PRODUCT_ID") }, 
			inverseJoinColumns = { @JoinColumn(name = "FK_SALES_MODULE_ID") })
	@OrderBy("id")
	public Set<SalesModule> getSalesModuels() {
		return salesModuels;
	}

	public void setSalesModuels(Set<SalesModule> salesModuels) {
		this.salesModuels = salesModuels;
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