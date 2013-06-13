package com.norteksoft.bs.options.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

/**
 * 国际化类
 * @author liudongxia
 *
 */
@Entity
@Table(name="BS_INTERNATION")
public class Internation extends IdEntity{
	private static final long serialVersionUID = 1L;
	private String code;//编码
	private String remark;//备注
	@OneToMany(mappedBy="internation", cascade=CascadeType.REMOVE)
	private List<InternationOption> internationOptions;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public List<InternationOption> getInternationOptions() {
		return internationOptions;
	}
	public void setInternationOptions(List<InternationOption> internationOptions) {
		this.internationOptions = internationOptions;
	}
	
}
