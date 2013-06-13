package com.norteksoft.wf.engine.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.IdEntity;

@Entity
@Table(name="WF_DOCUMENT_TEMPLATE")
public class DocumentTemplate extends IdEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Long recordId;//webOffice用到的recordId，实体中那个记录的是当前的毫秒数
	
	private String fileName ;
	
	@Column(length=55)
	private String fileType;
	
	private Integer fileSize;
	
	@Enumerated(EnumType.STRING)
	private DataState status = DataState.DISABLE;
	
	private String filePath;
	
	@Column(length=600)
	private String description;
	
	private Long typeId;
	
	private Long systemId; //系统ID

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Integer getFileSize() {
		return fileSize;
	}

	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}

	public DataState getStatus() {
		return status;
	}

	public void setStatus(DataState status) {
		this.status = status;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}
}
