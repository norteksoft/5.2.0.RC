package com.norteksoft.acs.entity.sale;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.norteksoft.acs.entity.IdEntity;

/** 
 * 流水账实体。用于记录租户流水帐。
 * 
 */

@Entity
@Table(name = "ACS_JOURNALS")
public class Journal extends IdEntity {
	private static final long serialVersionUID = 1L;

	// 租户
	//private Tenant tenant = null;
	
	//记账日期
	private Date tenantDate;

	//操作类型
	private String operateType;
	
	//操作资源
	private String resources;
	
	private Long companyId;
	

	@Column(name = "FK_COMPANY_ID")
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	
	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public String getResources() {
		return resources;
	}

	public void setResources(String resources) {
		this.resources = resources;
	}

	public Date getTenantDate() {
		return tenantDate;
	}

	public void setTenantDate(Date tenantDate) {
		this.tenantDate = tenantDate;
	}

//	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
//	@JoinColumn(name="FK_TENANT_ID")
//	public Tenant getTenant() {
//		return tenant;
//	}
//
//	public void setTenant(Tenant tenant) {
//		this.tenant = tenant;
//	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
