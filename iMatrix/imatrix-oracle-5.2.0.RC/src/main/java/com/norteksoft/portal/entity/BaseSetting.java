package com.norteksoft.portal.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

@Entity
@Table(name="PORTAL_BASE_SETTING")
public class BaseSetting extends IdEntity{
	private static final long serialVersionUID = 1L;
	
	private Boolean messageVisible;//是否显示消息小窗体
	private Integer refreshTime;//刷新间隔时间,单位：秒
	private Integer showRows;//显示条数
	
	public Integer getShowRows() {
		return showRows;
	}
	public void setShowRows(Integer showRows) {
		this.showRows = showRows;
	}
	public Boolean getMessageVisible() {
		return messageVisible;
	}
	public void setMessageVisible(Boolean messageVisible) {
		this.messageVisible = messageVisible;
	}
	public Integer getRefreshTime() {
		return refreshTime;
	}
	public void setRefreshTime(Integer refreshTime) {
		this.refreshTime = refreshTime;
	}
	
	
}
