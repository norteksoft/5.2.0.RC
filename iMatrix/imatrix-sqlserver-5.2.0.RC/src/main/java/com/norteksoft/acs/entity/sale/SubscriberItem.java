package com.norteksoft.acs.entity.sale;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.norteksoft.acs.entity.IdEntity;

/**
 * <p>
 * 订单订购项，取消原来滴价格策略
 * 
 * @author xiao
 * 
 *         2012-4-25
 */
@Entity
@Table(name = "ACS_SUBSCRIBER_ITEMS")
public class SubscriberItem extends IdEntity {
	private static final long serialVersionUID = 1L;

	private Double amount; // 金额
	private Integer concurrency; // 并发数
	private Date effectDate; // 订单生效日期
	private Date invalidDate; // 订单失效日期
	private Subsciber subsciber; // 订单
	private Product product; // 产品

	@ManyToOne()
	@JoinColumn(name="FK_SUBSCIBER_ID")
	public Subsciber getSubsciber() {
		return subsciber;
	}

	public void setSubsciber(Subsciber subsciber) {
		this.subsciber = subsciber;
	}

	@ManyToOne()
	@JoinColumn(name="FK_PRODUCT_ID")
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Integer getConcurrency() {
		return concurrency;
	}

	public void setConcurrency(Integer concurrency) {
		this.concurrency = concurrency;
	}

	public Date getEffectDate() {
		return effectDate;
	}

	public void setEffectDate(Date effectDate) {
		this.effectDate = effectDate;
	}

	public Date getInvalidDate() {
		return invalidDate;
	}

	public void setInvalidDate(Date invalidDate) {
		this.invalidDate = invalidDate;
	}

}
