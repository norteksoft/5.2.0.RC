package com.norteksoft.bs.rank.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;
/**
 * 上级
 * @author Administrator
 *
 */
@Entity
@Table(name = "BS_SUPERIOR")
public class Superior extends IdEntity  implements Serializable {
	private static final long serialVersionUID = 1L;

	@OneToMany(mappedBy="dataDictionaryRank")
	private List<Subordinate> dataDictionaryRankUser;
	private Long userId;//直属上级Id
	private String loginName;//直属上级登录名
	private String name;//直属上级真名
	private String title;
	private Long systemId; //系统ID
	public List<Subordinate> getDataDictionaryRankUser() {
		return dataDictionaryRankUser;
	}
	public void setDataDictionaryRankUser(
			List<Subordinate> dataDictionaryRankUser) {
		this.dataDictionaryRankUser = dataDictionaryRankUser;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public Long getSystemId() {
		return systemId;
	}
	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}
	
	@Override
	public String toString() {
		return "id:"+this.getId()+"；标题："+this.title+"；直属上级："+this.name;
	}
}
