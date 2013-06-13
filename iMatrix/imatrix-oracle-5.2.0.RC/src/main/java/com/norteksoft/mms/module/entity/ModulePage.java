package com.norteksoft.mms.module.entity;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.norteksoft.mms.form.entity.View;
import com.norteksoft.mms.module.enumeration.ViewType;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.IdEntity;

import edu.emory.mathcs.backport.java.util.Collections;
/**
 * 页面
 * @author wurong
 */
@Entity
@Table(name="MMS_MODULE_PAGE")
public class ModulePage extends IdEntity  implements Serializable,Comparable<ModulePage> {

	private static final long serialVersionUID = 1L;
	@Column(length=64)
	private String code;//编号
	
	@Column(length=64)
	private String name;//名称
	
	@Enumerated(EnumType.STRING)
	@Column(length=32)
	private ViewType viewType = ViewType.LIST_VIEW;//视图类型
	
	@Enumerated(EnumType.STRING)
	@Column(length=32)
	private DataState enableState = DataState.DRAFT;//启用状态
	
	private Boolean defaultDisplay = false;//默认显示 true为默认显示，false为否
	
	@Column(name="FK_MENU_ID")
	private Long menuId; // 所属的菜单
	@ManyToOne
	@JoinColumn(name="FK_VIEW_ID")
	private View  view;//表单id
	
	@OneToMany(mappedBy="modulePage",cascade=CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<Button> buttons;//按钮
	
	
	private Long systemId;//系统id

	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public ViewType getViewType() {
		return viewType;
	}


	public void setViewType(ViewType viewType) {
		this.viewType = viewType;
	}


	public DataState getEnableState() {
		return enableState;
	}


	public void setEnableState(DataState enableState) {
		this.enableState = enableState;
	}


	public Long getMenuId() {
		return menuId;
	}


	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}


	public View getView() {
		return view;
	}


	public void setView(View view) {
		this.view = view;
	}


	public List<Button> getButtons() {
		Collections.sort(buttons, new Comparator<Button>() {
			public int compare(Button b1, Button b2) {
				return b1.getDisplayOrder().intValue() - b2.getDisplayOrder().intValue();
			}
		});
		return buttons;
	}


	public void setButtons(List<Button> buttons) {
		this.buttons = buttons;
	}

	public Boolean getDefaultDisplay() {
		return defaultDisplay;
	}


	public void setDefaultDisplay(Boolean defaultDisplay) {
		this.defaultDisplay = defaultDisplay;
	}


	public int compareTo(ModulePage modulePage) {
		return this.code.compareTo(modulePage.getCode());
	}


	public Long getSystemId() {
		return systemId;
	}


	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}
	
}
