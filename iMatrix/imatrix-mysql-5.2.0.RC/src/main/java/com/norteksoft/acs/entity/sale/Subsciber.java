package com.norteksoft.acs.entity.sale;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.norteksoft.acs.entity.IdEntity;

/**
 * 订购记录
 */
@Entity
@Table(name = "ACS_SUBSCIBERS")
public class Subsciber extends IdEntity {
	private static final long serialVersionUID = 1L;

	//订单编号
	private String orderNo;

	//订购日期
	private Date orderDate;

	//开始日期
	private Date beginDate;

	//有效日期
	private Date validDate;

	//允许使用用户数量
	private Integer useNumber = 3;

	//所属租户
	private Long tenantId;

	private String remark;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getValidDate() {
		return validDate;
	}

	public void setValidDate(Date validDate) {
		this.validDate = validDate;
	}

	public Integer getUseNumber() {
		return useNumber;
	}

	public void setUseNumber(Integer useNumber) {
		this.useNumber = useNumber;
	}

	@Column(name="FK_TENANT_ID")
	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}