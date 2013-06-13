package com.norteksoft.acs.web.syssetting;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.enumeration.LoginFailSetType;
import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.sysSetting.SecuritySetting;
import com.norteksoft.acs.service.syssetting.SecuritySetManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
/**
 * 
 * @author chenchenhu
 *
 */
@Namespace("/syssetting")
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "/syssetting/security-set.action", type = "redirect") })
public class SecuritySetAction extends CRUDActionSupport<SecuritySetting> {

	private static final long serialVersionUID = 4622265559442003480L;

	private SecuritySetManager securitySetManager;

	public String name;

	private SecuritySetting entity;

	private Long id;
	
	private String passWordLenth;
	
	private String [] prems;
	
	private String passRule;
	
	private String mse;
	private String failType="VALIDATE_CODE";
	private Integer lockTime;
	
	private String logRemainTime;//审计信息保留时间
	private String logRemainTimeRemark;//审计信息保留时间备注

	@Override
	public String delete() throws Exception {

		return null;
	}
	
	@Action("list")
	public String toList() throws Exception{
		return SUCCESS;
	}

	@Override
	@Action("security-set")
	public String list() throws Exception {
		List<SecuritySetting>  list = securitySetManager.getSecuritySetList();
		prems = new String[11];
		prems[0]="3";//登陆次数默认设为3次
		for (SecuritySetting obj : list) {
			if(obj.getName().equals("login-security")){
				prems[0]=obj.getValue();
				prems[2]=obj.getRemarks();
				failType=obj.getFailSetType().toString();
				lockTime=obj.getLockedTime();
			}
			if(obj.getName().equals("loginTimeouts")){
				prems[3]=obj.getValue();
				prems[4]=obj.getRemarks();
			}
			if(obj.getName().equals("password-over-notice")){
				prems[5]=obj.getValue();
				prems[6]=obj.getRemarks();
			}
			if(obj.getName().equals("admin-password-overdue")){
				prems[7]=obj.getValue();
				prems[8]=obj.getRemarks();
			}
			if(obj.getName().equals("user-password-overdue")){
				prems[9]=obj.getValue();
				prems[10]=obj.getRemarks();
			}
			if(obj.getName().equals("password-complexity")){
				passRule=obj.getValue();
				passWordLenth = securitySetManager.getPassWordLength(obj);
			}
			if(obj.getName().equals("log-set")){
				logRemainTime=obj.getValue();
				logRemainTimeRemark=obj.getRemarks();
			}
		}
		ApiFactory.getBussinessLogService().log("参数设置", 
				"查看参数设置",ContextUtils.getSystemId("acs"));
		return SUCCESS;
	}

	@Override
	protected void prepareModel() throws Exception {
		//entity = securitySetManager.getSecuritySetByName(name,null);
	}

	@Override
	public String save() throws Exception {
		List<SecuritySetting> entitys = new ArrayList<SecuritySetting>();
		if(StringUtils.isNotEmpty(prems[0])||StringUtils.isNotEmpty(failType)||lockTime!=null){
			entity = securitySetManager.getSecuritySetByName("login-security",null);
			entity.setValue(prems[0]);
			entity.setRemarks(prems[1]);
			if("VALIDATE_CODE".equals(failType)){
				entity.setFailSetType(LoginFailSetType.VALIDATE_CODE);
			}else if("LOCK_USER".equals(failType)){
				entity.setFailSetType(LoginFailSetType.LOCK_USER);
				entity.setLockedTime(lockTime);
			}
			entitys.add(entity);
		}
			
	    if(StringUtils.isNotEmpty(prems[2])){
		entity = securitySetManager.getSecuritySetByName("loginTimeouts",null);
		entity.setValue(prems[2]);
		entity.setRemarks(prems[3]);
		entitys.add(entity);
	    }
	    
	    if(StringUtils.isNotEmpty(prems[4])){
		entity = securitySetManager.getSecuritySetByName("password-over-notice",null);
		entity.setValue(prems[4]);
		entity.setRemarks(prems[5]);
		entitys.add(entity);
	    }
	    
	    if(StringUtils.isNotEmpty(prems[6])){
		entity = securitySetManager.getSecuritySetByName("admin-password-overdue",null);
		entity.setValue(prems[6]);
		entity.setRemarks(prems[7]);
		entitys.add(entity);
	    }
		
	    if(StringUtils.isNotEmpty(prems[8])){
		entity = securitySetManager.getSecuritySetByName("user-password-overdue",null);
		entity.setValue(prems[8]);
		entity.setRemarks(prems[9]);
		entitys.add(entity);
	    }
		
	    if(StringUtils.isNotEmpty(passRule)){
		entity = securitySetManager.getSecuritySetByName("password-complexity",null);
		entity.setValue(passRule);
		entitys.add(entity);
	    }
	    if(StringUtils.isNotEmpty(logRemainTime)){
	    	entity = securitySetManager.getSecuritySetByName("log-set",null);
	    	entity.setValue(logRemainTime);
	    	entity.setRemarks(logRemainTimeRemark);
	    	entitys.add(entity);
	    }
		securitySetManager.save(entitys);
		
		mse ="ok";
		ApiFactory.getBussinessLogService().log("参数设置", 
				"提交参数设置",ContextUtils.getSystemId("acs"));
		return list();
	}

	public void prepareModifyLoginTimeouts() throws Exception {
		prepareModel();
	}

	
	
	/**
	 * 系统登录超时设置
	 * 
	 * @return
	 * @throws Exception
	 */
	public String modifyLoginTimeouts() throws Exception {
		securitySetManager.writeLog("系统登录超时设置");
		return "logintimeouts";
	}
	public void prepareSaveLoginTimeouts() throws Exception {
		prepareModel();
	}
	
	
	/**
	 * 登陆安全设置方法
	 * 
	 * @return
	 * @throws Exception
	 */
	public void prepareModifyLonginSecuritys() throws Exception {
		
		prepareModel();
	}

	public String modifyLonginSecuritys() throws Exception {
		securitySetManager.writeLog("登陆安全设置");
		return "login-security";
	}

	/**
	 * 管理员密码过期设置
	 * 
	 * @return
	 * @throws Exception
	 */

	public void prepareModifyAdminPasswordOverdue() throws Exception {
		
		prepareModel();
	}

	public String modifyAdminPasswordOverdue() throws Exception {
		securitySetManager.writeLog("管理员密码过期设置");
		return "admin-password-overdue";
	}

	/**
	 * 一般用户密码过期设置
	 * 
	 * @return
	 * @throws Exception
	 */
	public void prepareModifyPasswordOverdue() throws Exception {
		prepareModel();
	}

	public String modifyPasswordOverdue() throws Exception {
		securitySetManager.writeLog("一般用户密码过期设置");
		return "user-password-overdue";
	}

	/**
	 * 密码过期通知设置
	 * 
	 * @return
	 * @throws ExceptionsetPasswordComplexity
	 */

	public void prepareModifyPasswordOverNotice() throws Exception {
		prepareModel();
	}

	public String modifyPasswordOverNotice() throws Exception {
		securitySetManager.writeLog("密码过期通知设置");
		return "password-over-notice";
	}

	/**
	 * 密码复杂度设置
	 * 
	 * @return
	 * @throws Exception
	 */
	public void prepareModifyPasswordComplexity() throws Exception {
		entity = securitySetManager.getSecuritySetByName(name,"(?=(.*\\d){1,}),0");
	}

	public String modifyPasswordComplexity() throws Exception {
		
		
		passWordLenth = securitySetManager.getPassWordLength(entity);
		securitySetManager.writeLog("密码复杂度设置");
		return "password-complexity";
	}

	public SecuritySetting getModel() {

		return entity;
	}

	@Required
	public void setSecuritySetManager(SecuritySetManager securitySetManager) {
		this.securitySetManager = securitySetManager;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassWordLenth() {
		return passWordLenth;
	}

	public void setPassWordLenth(String passWordLenth) {
		this.passWordLenth = passWordLenth;
	}

	public String [] getPrems() {
		return prems;
	}

	public void setPrems(String [] prems) {
		this.prems = prems;
	}

	public String  getPassRule() {
		return passRule;
	}

	public void setPassRule(String  passRule) {
		this.passRule = passRule;
	}

	public String getMse() {
		return mse;
	}

	public void setMse(String mse) {
		this.mse = mse;
	}

	public String getFailType() {
		return failType;
	}

	public void setFailType(String failType) {
		this.failType = failType;
	}

	public Integer getLockTime() {
		return lockTime;
	}

	public void setLockTime(Integer lockTime) {
		this.lockTime = lockTime;
	}

	public String getLogRemainTime() {
		return logRemainTime;
	}

	public void setLogRemainTime(String logRemainTime) {
		this.logRemainTime = logRemainTime;
	}

	public String getLogRemainTimeRemark() {
		return logRemainTimeRemark;
	}

	public void setLogRemainTimeRemark(String logRemainTimeRemark) {
		this.logRemainTimeRemark = logRemainTimeRemark;
	}
}
