package com.norteksoft.acs.entity;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

/**
 * 统一定义id的entity基类.
 * 
 * @author huhongchun
 */
@SuppressWarnings("serial")
@MappedSuperclass
public class IdEntity implements Serializable{

	private boolean deleted = false;
	private Date ts = new Date();
	
	private Long id;

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Date getTs() {
		return ts;
	}

	public void setTs(Date ts) {
		this.ts = ts;
	}

	@Id
	@GenericGenerator(name="paymentableGenerator",strategy="native")
	@GeneratedValue( generator="paymentableGenerator")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
