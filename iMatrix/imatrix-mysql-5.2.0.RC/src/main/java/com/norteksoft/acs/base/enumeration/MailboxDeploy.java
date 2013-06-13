package com.norteksoft.acs.base.enumeration;
/**
 * 邮箱配置
 * @author Administrator
 *
 */
public enum MailboxDeploy {
	INSIDE("mailbox.deploy.inside"), // 内网
	EXTERIOR("mailbox.deploy.exterior"); // 外网
	
	private String code;
	
	MailboxDeploy(String code){
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
}
