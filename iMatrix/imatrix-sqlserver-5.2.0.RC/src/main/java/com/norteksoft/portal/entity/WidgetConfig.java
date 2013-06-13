package com.norteksoft.portal.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

/**
 * 用户小窗口配置
 */
@Entity
@Table(name="PORTAL_WIDGET_CONFIG")
public class WidgetConfig extends IdEntity{

	private static final long serialVersionUID = 1L;
	private Long userId;       //用户的ID
	private Long widgetId;     //小窗口ID
	private Long webpageId;   //页签ID
	private Integer position;      //（0：left,1:center,2:right）分栏后 在那一栏里
	private Boolean visible=true;//是否显示（当点击了小窗体的“关闭”按钮后，该值为false）
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getWidgetId() {
		return widgetId;
	}

	public void setWidgetId(Long widgetId) {
		this.widgetId = widgetId;
	}

	public Long getWebpageId() {
		return webpageId;
	}

	public void setWebpageId(Long webpageId) {
		this.webpageId = webpageId;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

}
