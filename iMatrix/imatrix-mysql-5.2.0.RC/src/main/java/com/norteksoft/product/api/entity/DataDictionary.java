package com.norteksoft.product.api.entity;

import java.io.Serializable;
import java.util.List;

import com.norteksoft.wf.engine.entity.DataDictionaryProcess;

public class DataDictionary implements Serializable{

	private static final long serialVersionUID = 1L;
	//entity
	private Long id;
	private boolean deleted;
	private Long companyId;
	//DataDictionary
	private Long typeId; //所属数据字典类型id
	private String typeNo; //所属数据字典类型编号
	private String typeName; //所属数据字典类型名称
	private String info; 
	private Integer type;//1. 设置办理人   2. 设置正文权限
	private String operation; //操作权限
	private Integer displayIndex;//显示顺序
	private Integer processType;  //通用,选择
	private String displayOperation;
	private List<DataDictionaryProcess> dataDictionaryProcess;
	private String remark;
	private Long systemId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Long getTypeId() {
		return typeId;
	}
	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}
	public String getTypeNo() {
		return typeNo;
	}
	public void setTypeNo(String typeNo) {
		this.typeNo = typeNo;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public Integer getDisplayIndex() {
		return displayIndex;
	}
	public void setDisplayIndex(Integer displayIndex) {
		this.displayIndex = displayIndex;
	}
	public Integer getProcessType() {
		return processType;
	}
	public void setProcessType(Integer processType) {
		this.processType = processType;
	}
	public String getDisplayOperation() {
		return displayOperation;
	}
	public void setDisplayOperation(String displayOperation) {
		this.displayOperation = displayOperation;
	}
	public List<DataDictionaryProcess> getDataDictionaryProcess() {
		return dataDictionaryProcess;
	}
	public void setDataDictionaryProcess(
			List<DataDictionaryProcess> dataDictionaryProcess) {
		this.dataDictionaryProcess = dataDictionaryProcess;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Long getSystemId() {
		return systemId;
	}
	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}
}
