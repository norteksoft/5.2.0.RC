package com.norteksoft.mms.module.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

/**
 * 按钮实体
 * @author wurong
 *
 */

@Entity
@Table(name="MMS_BUTTON")
public class Button extends IdEntity  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Column(length=64)
	private String code;//编号
	
	@Column(length=64)
	private String name;//名称
	@Lob
    @Column(columnDefinition="LONGTEXT", nullable=true)
	private String event;//事件
	
	private Integer displayOrder;
	
	@ManyToOne
	@JoinColumn(name="FK_MODULE_PAGE_ID")
	private ModulePage modulePage; // 所属的视图
	
	@ManyToOne
	@JoinColumn(name="FK_TO_PAGE_ID")
	private ModulePage toPage;//转向的页面
	
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

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public ModulePage getModulePage() {
		return modulePage;
	}

	public void setModulePage(ModulePage modulePage) {
		this.modulePage = modulePage;
	}

	public ModulePage getToPage() {
		return toPage;
	}

	public void setToPage(ModulePage toPage) {
		this.toPage = toPage;
	}


	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
}
