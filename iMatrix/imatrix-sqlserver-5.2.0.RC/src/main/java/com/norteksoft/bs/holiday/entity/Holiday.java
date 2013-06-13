package com.norteksoft.bs.holiday.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;


@Entity
@Table(name = "BS_HOLIDAY")
public class Holiday extends IdEntity {
	private static final long serialVersionUID = 1L;

	private Date specialDate; // 日期
	@Enumerated(EnumType.STRING)
	private DateType dateType; // 日期类别： 工作日，节假日

	public Date getSpecialDate() {
		return specialDate;
	}

	public void setSpecialDate(Date specialDate) {
		this.specialDate = specialDate;
	}

	public DateType getDateType() {
		return dateType;
	}

	public void setDateType(DateType dateType) {
		this.dateType = dateType;
	}
	
	@Override
	public String toString() {
		return "id:"+this.getId()+"；日期："+this.specialDate+"；日期类别："+this.dateType;
	}
}
