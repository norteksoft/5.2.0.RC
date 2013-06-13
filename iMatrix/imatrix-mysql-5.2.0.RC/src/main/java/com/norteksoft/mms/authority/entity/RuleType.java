package com.norteksoft.mms.authority.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

/**
 * 规则类别
 * @author qiao
 *
 */

@Entity
@Table(name="MMS_RULE_TYPE")
public class RuleType extends IdEntity  implements Serializable{
		private static final long serialVersionUID = 1L;
		
		@Column(length=64)
		private String code; //编号
		@Column(length=64)
		private String name; //名称
		@ManyToOne
		@JoinColumn(name="FK_PARENT_TYPE")
		private RuleType parent; //父类别
		
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
		public RuleType getParent() {
			return parent;
		}
		public void setParent(RuleType parent) {
			this.parent = parent;
		}
		
		
}
