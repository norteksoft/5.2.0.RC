package com.norteksoft.portal.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.product.orm.IdEntity;

/**
 * 窗口参数值
 */
@Entity
@Table(name="PORTAL_WIDGET_PARAMETER_VALUE")
public class WidgetParameterValue extends IdEntity{

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(targetEntity=WidgetParameter.class)
	@JoinColumn(name="FK_WIDGET_PARAMETER_ID")
	private WidgetParameter widgetParameter;       //小窗口参数的ID
	
	private Long userId;       
	
	private String value;  //参数值
	
	private String title;//中文名字
	
	private Long webPageId;//页签id
	
	@Transient
	private String display;         //页面展示使用

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public WidgetParameter getWidgetParameter() {
		return widgetParameter;
	}

	public void setWidgetParameter(WidgetParameter widgetParameter) {
		this.widgetParameter = widgetParameter;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getWebPageId() {
		return webPageId;
	}

	public void setWebPageId(Long webPageId) {
		this.webPageId = webPageId;
	}
}
