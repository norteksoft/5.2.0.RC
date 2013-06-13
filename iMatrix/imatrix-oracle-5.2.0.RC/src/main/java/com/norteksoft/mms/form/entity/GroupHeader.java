package com.norteksoft.mms.form.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

/**
 * 组合表头
 */
@Entity
@Table(name="MMS_GROUP_HEADER")
public class GroupHeader extends IdEntity  implements Serializable,Cloneable{
	private static final long serialVersionUID = 1L;
	private String startColumnName;//开始列名
	private Integer numberOfColumns;//合并列数
	private String titleText;//新列名称
	@Column(name="FK_LIST_VIEW_ID")
	private Long listViewId;//列表视图
	
	public String getStartColumnName() {
		return startColumnName;
	}
	public void setStartColumnName(String startColumnName) {
		this.startColumnName = startColumnName;
	}
	public Integer getNumberOfColumns() {
		return numberOfColumns;
	}
	public void setNumberOfColumns(Integer numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
	}
	public String getTitleText() {
		return titleText;
	}
	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}
	public Long getListViewId() {
		return listViewId;
	}
	public void setListViewId(Long listViewId) {
		this.listViewId = listViewId;
	}
	@Override
	public GroupHeader clone(){
		try {
			return (GroupHeader) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new RuntimeException("GroupHeader clone failure");
		}
	}
	
}
