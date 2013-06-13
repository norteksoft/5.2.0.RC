package com.norteksoft.product.util;

import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

class MailSender implements Runnable{
	private String to;//收件人
	private String subject;
	private String content;
	private Boolean isAutheticate;
	private String protocol;
	private String host;
	private Integer port;
	private String user;
	private String password;
	private String from;
	private Map<String, String> filePathMap;
	private Set<String> toEmails;//收件人集合
	private Long companyId;
	private Long userId;
	
	public MailSender(String to, String subject, String content,Long companyId,Long userId){
		this.to=to;
		this.subject=subject;
		this.content=content;
		this.companyId=companyId;
		this.userId=userId;
	}
	public MailSender(Set<String> toEmails, String subject, String content,Long companyId,Long userId){
		this.toEmails=toEmails;
		this.subject=subject;
		this.content=content;
		this.companyId=companyId;
		this.userId=userId;
	}
	public void run() {
		try {
			ThreadParameters tp=new ThreadParameters(companyId,userId);
			ParameterUtils.setParameters(tp);
			if(toEmails!=null&&toEmails.size()>0){
				for(String to:toEmails){
					MailUtils.sendMail(to,subject,content);
				}
			}else{
				MailUtils.sendMail(to,subject,content);
			}
		} catch (AddressException e) {
			throw new RuntimeException("邮件地址错误！",e);
		} catch (MessagingException e) {
			throw new RuntimeException("邮件发送失败！",e);
		}
		
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Boolean getIsAutheticate() {
		return isAutheticate;
	}
	public void setIsAutheticate(Boolean isAutheticate) {
		this.isAutheticate = isAutheticate;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public Map<String, String> getFilePathMap() {
		return filePathMap;
	}
	public void setFilePathMap(Map<String, String> filePathMap) {
		this.filePathMap = filePathMap;
	}
	public Set<String> getToEmails() {
		return toEmails;
	}
	public void setToEmails(Set<String> toEmails) {
		this.toEmails = toEmails;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
}