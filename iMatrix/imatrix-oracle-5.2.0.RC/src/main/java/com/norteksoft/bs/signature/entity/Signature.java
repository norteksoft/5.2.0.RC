package com.norteksoft.bs.signature.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

/**
 * 签章
 * @author Administrator
 *
 */
@Entity
@Table(name = "BS_SIGNATURE")
public class Signature extends IdEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Long  userId;       //用户ID
	private String userName;    //用户名称
	private String pictureSrc;  //图片路径

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPictureSrc() {
		return pictureSrc;
	}

	public void setPictureSrc(String pictureSrc) {
		this.pictureSrc = pictureSrc;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
}
