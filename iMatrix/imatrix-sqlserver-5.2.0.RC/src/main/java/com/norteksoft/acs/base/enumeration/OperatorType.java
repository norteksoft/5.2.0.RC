package com.norteksoft.acs.base.enumeration;
/**
 * 操作员类型
 * @author Administrator
 *
 */
public enum OperatorType {
	COMMON_USER("operator.type.commonUser"), // 普通用户
	SYSTEM_ADMIN("operator.type.systemAdmin"), // 系统管理员
	SECURITY_ADMIN("operator.type.securityAdmin"), // 安全管理员
	AUDIT_ADMIN("operator.type.auditAdmin"); // 审计管理员
	
	private String code;
	
	OperatorType(String code){
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
}
