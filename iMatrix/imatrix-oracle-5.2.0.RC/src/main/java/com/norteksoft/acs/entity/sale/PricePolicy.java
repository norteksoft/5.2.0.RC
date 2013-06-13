package com.norteksoft.acs.entity.sale;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.norteksoft.acs.entity.IdEntity;

/**
 * 价格策略。记录产品的采购价格
 * 
 */

@Entity
@Table(name = "ACS_PRICE_POLICYS")
public class PricePolicy extends IdEntity {
	private static final long serialVersionUID = 1L;

	//名称
	private String priceName;

	//金额
	private BigDecimal amount;

	private Product product;

	private String remark;
	
	private Long companyId;
	
	
	@Column(name = "FK_COMPANY_ID")
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPriceName() {
		return priceName;
	}

	public void setPriceName(String priceName) {
		this.priceName = priceName;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@ManyToOne
	@JoinColumn(name="FK_PRODUCT_ID", nullable=false)
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}