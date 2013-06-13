package com.norteksoft.mms.authority.enumeration;

/**
	<p>数据权限</p>
	<ol>
	<li>SEARCH     查询
	<li>ADD       新建
	<li>UPDATE    修改
	<li>DELETE    删除
	</ol>
	@author xiao
	2010-8-20
 */
public enum PermissionAuthorize {
	SEARCH(1, "permission.authorize.search"),
	ADD(2, "permission.authorize.add"),
	UPDATE(4, "permission.authorize.update"),
	DELETE(8, "permission.authorize.delete");
	
	private Integer code;
	private String i18nKey;
	
	private PermissionAuthorize(Integer code, String i18nKey){
		this.code = code;
		this.i18nKey = i18nKey;
	}
	
	public Integer getCode(){
		return code;
	}
	
	public Integer allAuthorize(){
		Integer result = 0;
		for(PermissionAuthorize auth : values()){
			result += auth.getCode();
		}
		return result;
	}
	
	public String getI18nKey(){
		return this.i18nKey;
	}
	
	public PermissionAuthorize getAuthByCode(Integer code){
		for(PermissionAuthorize auth : values()){
			if(code.equals(auth.getCode())){
				return auth;
			}
		}
		return null;
	}
}
