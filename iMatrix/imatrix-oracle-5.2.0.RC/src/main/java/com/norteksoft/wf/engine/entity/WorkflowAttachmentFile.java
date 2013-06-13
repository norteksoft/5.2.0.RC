package com.norteksoft.wf.engine.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

/**
 * 附件文件内容
 * @author wurong
 *
 */
@Entity
@Table(name="WF_ATTACHMENT_FILE")
public class WorkflowAttachmentFile extends IdEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Column(name="FK_WF_ATTACHMENT_ID",unique=true)
	private Long attachmentId;
	
	@Lob 
	@Basic(fetch = FetchType.LAZY) 
	@Column(name = "CONTENT", columnDefinition = "BLOB",nullable=true) 
	private byte[] content;

	public Long getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(Long attachmentId) {
		this.attachmentId = attachmentId;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	
}
