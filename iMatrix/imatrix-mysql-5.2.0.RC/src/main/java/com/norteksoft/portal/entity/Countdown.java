package com.norteksoft.portal.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

/**
 * 倒计时
 */
@Entity
@Table(name="PORTAL_COUNTDOWN")
public class Countdown extends IdEntity{
	private static final long serialVersionUID = 1L;
	private Long userId;
	private Date targetDate;
	private String title;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getTargetDate() {
		return targetDate;
	}

	public void setTargetDate(Date targetDate) {
		this.targetDate = targetDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
