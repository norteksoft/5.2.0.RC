package com.norteksoft.portal.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.product.orm.IdEntity;


/**
 * 页签
 */
@Entity
@Table(name="PORTAL_WEBPAGE")
public class Webpage extends IdEntity{

	private static final long serialVersionUID = 1L;
	private String name;      //页签名称
	private String code;
	private Long userId;       //用户的ID
	private String url;       //页签链接
	private Integer displayOrder;     //页签顺序
	private Integer columns;       //显示栏目个数
	private Boolean acquiescent;//是否默认
	@Column(length=2000)
	private String widgetPosition;//保存小窗体位置的字符串
	@Transient
	private List<Widget> leftWidgets = new ArrayList<Widget>(); //窗口左栏
	@Transient
	private List<Widget> centerWidgets = new ArrayList<Widget>(); //窗口左栏
	@Transient
	private List<Widget> rightWidgets = new ArrayList<Widget>(); //窗口左栏

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public Integer getColumns() {
		return columns;
	}

	public void setColumns(Integer columns) {
		this.columns = columns;
	}

	public List<Widget> getLeftWidgets() {
		return leftWidgets;
	}

	public void setLeftWidgets(List<Widget> leftWidgets) {
		this.leftWidgets = leftWidgets;
	}

	public List<Widget> getCenterWidgets() {
		return centerWidgets;
	}

	public void setCenterWidgets(List<Widget> centerWidgets) {
		this.centerWidgets = centerWidgets;
	}

	public List<Widget> getRightWidgets() {
		return rightWidgets;
	}

	public void setRightWidgets(List<Widget> rightWidgets) {
		this.rightWidgets = rightWidgets;
	}



	@Override
	public Webpage clone() throws CloneNotSupportedException {
		return (Webpage) super.clone();
	}

	public String getWidgetPosition() {
		return widgetPosition;
	}

	public void setWidgetPosition(String widgetPosition) {
		this.widgetPosition = widgetPosition;
	}

	public Boolean getAcquiescent() {
		return acquiescent;
	}

	public void setAcquiescent(Boolean acquiescent) {
		this.acquiescent = acquiescent;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
}
