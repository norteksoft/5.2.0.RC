package com.norteksoft.wf.engine.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.product.orm.IdEntity;


@Entity
@Table(name = "WF_DATA_DICTIONARY")
public class DataDictionary extends IdEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long typeId; //所属数据字典类型id
	private String typeNo; //所属数据字典类型编号
	private String typeName; //所属数据字典类型名称
	private String info;  //
	private Integer type;             //1. 设置办理人   2. 设置正文权限
	private String operation;         //操作权限
	private Integer displayIndex;     //显示顺序
	private Integer processType;  //通用,选择
	@Transient
	private String displayOperation;
	@OneToMany(mappedBy="dataDictionary")
	private List<DataDictionaryProcess> dataDictionaryProcess;
	private String remark;
	private Long systemId;

	public List<DataDictionaryProcess> getDataDictionaryProcess() {
		return dataDictionaryProcess;
	}

	public void setDataDictionaryProcess(
			List<DataDictionaryProcess> dataDictionaryProcess) {
		this.dataDictionaryProcess = dataDictionaryProcess;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getDisplayIndex() {
		return displayIndex;
	}

	public void setDisplayIndex(Integer displayIndex) {
		this.displayIndex = displayIndex;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getProcessType() {
		return processType;
	}

	public void setProcessType(Integer processType) {
		this.processType = processType;
	}

	public String getOperation() {
		return operation;
	}
	
	public String getShowOperation(){
		
		return operation==null?operation:operation.substring(0,operation.lastIndexOf(","));
	}

	public String getDisplayOperation() {
		return displayOperation;
	}

	public void setDisplayOperation(String displayOperation) {
		this.displayOperation = displayOperation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}

	@Override
	public String toString() {
		return "DataDictionary [companyId=" + this.getCompanyId() + ", displayIndex="
				+ displayIndex  + ", info=" + info
				+ ", operation=" + operation + ", processType=" + processType
				+ ", type=" + type + ", typeId=" + typeId + ", typeName="
				+ typeName + "]";
	}
}
