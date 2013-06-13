package com.norteksoft.acs.entity.sale;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.acs.entity.IdEntity;

/**
 * 订购记录及其价格策略
 */
@Entity
@Table(name = "ACS_SUBSCIBER_PRICE_PLCS")
public class SubsciberPricePolicy extends IdEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;

	
	@Column(name="FK_SUBSCIBER_ID")
	private Long subsciberId;
	
	@Column(name="FK_PRICE_POLICY_ID")
	private Long pricePolicyId;


	public Long getSubsciberId() {
		return subsciberId;
	}

	public void setSubsciberId(Long subsciberId) {
		this.subsciberId = subsciberId;
	}

	public Long getPricePolicyId() {
		return pricePolicyId;
	}

	public void setPricePolicyId(Long pricePolicyId) {
		this.pricePolicyId = pricePolicyId;
	}

}
