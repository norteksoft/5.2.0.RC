package com.norteksoft.wf.engine.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

@Entity
@Table(name = "WF_DATA_DICTIONARY_PROCESS")
public class DataDictionaryProcess extends IdEntity  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name = "DATA_DICT_ID")
	private DataDictionary dataDictionary;
	
	private Long processDefinitionId; //流程ID
	private String processDefinitionName; //流程名称
	private String tacheName;         //环节名称
	public DataDictionary getDataDictionary() {
		return dataDictionary;
	}
	public void setDataDictionary(DataDictionary dataDictionary) {
		this.dataDictionary = dataDictionary;
	}
	public Long getProcessDefinitionId() {
		return processDefinitionId;
	}
	public void setProcessDefinitionId(Long processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}
	public String getProcessDefinitionName() {
		return processDefinitionName;
	}
	public void setProcessDefinitionName(String processDefinitionName) {
		this.processDefinitionName = processDefinitionName;
	}
	public String getTacheName() {
		return tacheName;
	}
	public void setTacheName(String tacheName) {
		this.tacheName = tacheName;
	}
	@Override
	public String toString() {
		return "DataDictionaryProcess [dataDict=[" + dataDictionary + "]" 
				+ ", processDefinitionId=" + processDefinitionId
				+ ", processDefinitionName=" + processDefinitionName
				+ ", tacheName=" + tacheName + "]";
	}
}
