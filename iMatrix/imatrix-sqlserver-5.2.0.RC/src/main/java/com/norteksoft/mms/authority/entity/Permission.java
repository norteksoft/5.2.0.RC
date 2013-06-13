package com.norteksoft.mms.authority.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.norteksoft.mms.authority.enumeration.PermissionAuthorize;
import com.norteksoft.product.orm.IdEntity;
import com.norteksoft.product.web.struts2.Struts2Utils;
/**
 * 授权
 * @author Administrator
 *
 */
@Entity
@Table(name="MMS_PERMISSION")
public class Permission extends IdEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer priority;
	private Integer authority;
	@ManyToOne
	@JoinColumn(name="FK_DATA_RULE_ID")
	private DataRule dataRule;
	@OneToMany(mappedBy="permission",cascade=CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.TRUE)
	@OrderBy("displayOrder asc")
	private List<PermissionItem> items;
	@Transient
	private String authorityName;//操作权限名称
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public Integer getAuthority() {
		return authority;
	}
	public void setAuthority(Integer authority) {
		this.authority = authority;
	}
	public DataRule getDataRule() {
		return dataRule;
	}
	public void setDataRule(DataRule dataRule) {
		this.dataRule = dataRule;
	}
	public List<PermissionItem> getItems() {
		return items;
	}
	public void setItems(List<PermissionItem> items) {
		this.items = items;
	}
	public String getAuthorityName() {
		authorityName="";
		for(PermissionAuthorize auth : PermissionAuthorize.values()){
			if((this.authority & auth.getCode()) != 0){//有该权限
				authorityName=authorityName+Struts2Utils.getText(auth.getI18nKey())+",";
			}
		}
		if(authorityName.contains(",")){
			authorityName=authorityName.substring(0,authorityName.lastIndexOf(","));
		}
		return authorityName;
	}
  
}
