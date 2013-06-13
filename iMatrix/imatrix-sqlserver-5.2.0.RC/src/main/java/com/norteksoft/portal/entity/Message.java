package com.norteksoft.portal.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.portal.base.enumeration.MessageType;
import com.norteksoft.product.orm.IdEntity;


/**
 * 消息小窗口
 */
@Entity
@Table(name="PORTAL_MESSAGE")
public class Message extends IdEntity {
	private static final long serialVersionUID = 1L;
	private String sender;           //发件用户
	private String senderLoginName;  //发件用户登陆名
	private String receiver;         //收件用户名
	private String receiverLoginName;//收件用户登陆名
	private String category;         //信息类别
	private String systemCode;       //系统code
	@Column(length=8000)
	private String content;          //信息
	private String url;              //访问路径
	private String errorInfo;        //错误 信息
	private MessageType messageType; //消息类型
	private Boolean visible=true;    //小窗体是否显示  true为显示，false为不
	
	public String getSenderLoginName() {
		return senderLoginName;
	}
	public void setSenderLoginName(String senderLoginName) {
		this.senderLoginName = senderLoginName;
	}
	public String getSystemCode() {
		return systemCode;
	}
	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getErrorInfo() {
		return errorInfo;
	}
	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}
	public Boolean getVisible() {
		return visible;
	}
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getReceiverLoginName() {
		return receiverLoginName;
	}
	public void setReceiverLoginName(String receiverLoginName) {
		this.receiverLoginName = receiverLoginName;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public MessageType getMessageType() {
		return messageType;
	}
	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}
}
