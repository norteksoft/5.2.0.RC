package com.norteksoft.portal.entity;


import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

/**
 * 小窗体和角色的关系表
 */
@Entity
@Table(name="PORTAL_WIDGET_ROLE")
public class WidgetRole extends IdEntity{

	private static final long serialVersionUID = 1L;
	
	private Long widgetId ;      //小窗体id
	private Long roleId ; //角色id
	
	public Long getWidgetId() {
		return widgetId;
	}

	public void setWidgetId(Long widgetId) {
		this.widgetId = widgetId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

}
