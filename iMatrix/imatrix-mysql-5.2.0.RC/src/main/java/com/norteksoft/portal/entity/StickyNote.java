package com.norteksoft.portal.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

/**
 * 便签
 */
@Entity
@Table(name="PORTAL_STICKY_NOTE")
public class StickyNote extends IdEntity{
	private static final long serialVersionUID = 1L;
	private Long userId;
	private String content;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
