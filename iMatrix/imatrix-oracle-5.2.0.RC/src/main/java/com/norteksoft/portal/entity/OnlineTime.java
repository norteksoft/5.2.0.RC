package com.norteksoft.portal.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

/**
 * 在线时长排行榜
 */
@Entity
@Table(name="PORTAL_ONLINE_TIME")
public class OnlineTime extends IdEntity{
	private static final long serialVersionUID = 1L;
	private Long userId;//用户ID
	private String userName;//用户名称
	private Integer hour;//在线时长（小时）
	private Integer minute;//在线时长（分）
	private Date recentlyLoginDate;//最近登录日期

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getHour() {
		return hour;
	}

	public void setHour(Integer hour) {
		this.hour = hour;
	}

	public Integer getMinute() {
		return minute;
	}

	public void setMinute(Integer minute) {
		this.minute = minute;
	}

	public Date getRecentlyLoginDate() {
		return recentlyLoginDate;
	}

	public void setRecentlyLoginDate(Date recentlyLoginDate) {
		this.recentlyLoginDate = recentlyLoginDate;
	}

}
