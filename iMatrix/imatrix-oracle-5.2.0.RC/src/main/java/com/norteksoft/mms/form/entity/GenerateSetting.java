package com.norteksoft.mms.form.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.product.orm.IdEntity;

/**
 * 代码生成设置
 * @author Administrator
 *
 */
@Entity
@Table(name="MMS_GENERATE_SETTING")
public class GenerateSetting extends IdEntity  implements Serializable{
	private static final long serialVersionUID = 1L;
	private Boolean entitative=true;//是否生成实体
	private Boolean flowable=false;//是否走流程
	private Long tableId;//对应的数据表
	private String workflowCode;//对应的流程编码
	@Transient
	private String menuName;//列表对应的菜单
	public Boolean getEntitative() {
		return entitative;
	}
	public void setEntitative(Boolean entitative) {
		this.entitative = entitative;
	}
	public Boolean getFlowable() {
		return flowable;
	}
	public void setFlowable(Boolean flowable) {
		this.flowable = flowable;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public Long getTableId() {
		return tableId;
	}
	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}
	public String getWorkflowCode() {
		return workflowCode;
	}
	public void setWorkflowCode(String workflowCode) {
		this.workflowCode = workflowCode;
	}
	
}
