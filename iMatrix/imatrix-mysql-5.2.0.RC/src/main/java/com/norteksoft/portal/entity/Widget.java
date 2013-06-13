package com.norteksoft.portal.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.product.orm.IdEntity;


/**
 * 小窗口
 */
@Entity
@Table(name="PORTAL_WIDGET")
public class Widget extends IdEntity implements Comparable<Widget>{

	private static final long serialVersionUID = 1L;
	private String name;      //窗口名称
	private String url;       //窗口内容的URL
	private Boolean acquiescent = false; //是否默认显示(所有人都有)
	private String code;//小窗体编码,确定窗口唯一
	private String systemCode;//系统id
	private Boolean pageVisible = false; //是否显示分页
	private Boolean borderVisible = true;//一栏页签中，小窗体是否显示边框
	private Boolean iframeable=false;//小窗体的内容是否以iframe方式获得
	
	@OneToMany(mappedBy="widget", cascade=CascadeType.REMOVE)
	private List<WidgetParameter> parameters = new ArrayList<WidgetParameter>(); //窗口参数
	@Transient
	private String systemUrl;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<WidgetParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<WidgetParameter> parameters) {
		this.parameters = parameters;
	}

	public int compareTo(Widget widget) {
		Long result = this.getId()-widget.getId();
		return result.intValue() ;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getAcquiescent() {
		return acquiescent;
	}

	public void setAcquiescent(Boolean acquiescent) {
		this.acquiescent = acquiescent;
	}

	public Boolean getPageVisible() {
		return pageVisible;
	}

	public void setPageVisible(Boolean pageVisible) {
		this.pageVisible = pageVisible;
	}

	public Boolean getBorderVisible() {
		return borderVisible;
	}

	public void setBorderVisible(Boolean borderVisible) {
		this.borderVisible = borderVisible;
	}

	public Boolean getIframeable() {
		return iframeable;
	}

	public void setIframeable(Boolean iframeable) {
		this.iframeable = iframeable;
	}

	public String getSystemUrl() {
		return systemUrl;
	}

	public void setSystemUrl(String systemUrl) {
		this.systemUrl = systemUrl;
	}
	
}
