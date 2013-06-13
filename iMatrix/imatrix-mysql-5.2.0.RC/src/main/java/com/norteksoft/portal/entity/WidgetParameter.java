package com.norteksoft.portal.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.bs.options.entity.Option;
import com.norteksoft.portal.base.enumeration.ControlType;
import com.norteksoft.product.orm.IdEntity;

/**
 * 窗口参数
 */
@Entity
@Table(name="PORTAL_WIDGET_PARAMETER")
public class WidgetParameter extends IdEntity{

	private static final long serialVersionUID = 1L;
	@ManyToOne(targetEntity=Widget.class)
	@JoinColumn(name="FK_WIDGET_ID")
	private Widget widget;       //小窗口的ID
	private String name;       //参数名称
	private String title;       //标题
	private ControlType controlType;//参数类型
    private String code; //唯一标识    
	private String defaultValue;//参数默认值
	private Long optionGroupId;//选项组id
	private String optionGroupName;//选项组名称
	
	@Transient
	private List<Option> options;
	@OneToMany(mappedBy="widgetParameter", cascade=CascadeType.REMOVE)
	private List<WidgetParameterValue> parameterValues = new ArrayList<WidgetParameterValue>();//参数值
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<WidgetParameterValue> getParameterValues() {
		return parameterValues;
	}

	public void setParameterValues(List<WidgetParameterValue> parameterValues) {
		this.parameterValues = parameterValues;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Widget getWidget() {
		return widget;
	}

	public void setWidget(Widget widget) {
		this.widget = widget;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ControlType getControlType() {
		return controlType;
	}

	public void setControlType(ControlType controlType) {
		this.controlType = controlType;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Long getOptionGroupId() {
		return optionGroupId;
	}

	public void setOptionGroupId(Long optionGroupId) {
		this.optionGroupId = optionGroupId;
	}

	public String getOptionGroupName() {
		return optionGroupName;
	}

	public void setOptionGroupName(String optionGroupName) {
		this.optionGroupName = optionGroupName;
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}
}
