package com.norteksoft.portal.web.index;


import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;

@Namespace("/student")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "user", type = "redirectAction")})
public class UserAction extends CrudActionSupport<User>{
	
	private static final long serialVersionUID = 1L;
	
	private User user;
	
	private String oldPassword;
	
	private String newPassword;
	
	private UserManager userManager;
	
	@Override
	public String delete() throws Exception {
		return null;
	}

	@Override
	public String input() throws Exception {
		return null;
	}

	@Override
	public String list() throws Exception {
		return SUCCESS;
	}

	@Override
	protected void prepareModel() throws Exception {
		
	}

	@Override
	public String save() throws Exception {
		user = userManager.getUserById(ContextUtils.getUserId());
		user.setPassword(newPassword);
		userManager.saveUser(user);
		renderText(user.getId()+"");
		ApiFactory.getBussinessLogService().log("portal", "保存用户密码", ContextUtils.getSystemId("portal"));
		return null;
	}

	/**
	 * 验证旧密码是否输入正�?
	 */
	public String validateOldPassword() throws Exception {
		user = userManager.getUserById(ContextUtils.getUserId());
		if (user.getPassword().equals(oldPassword)) {
			renderText("true");
		} else {
			renderText("false");
		}
		return null;
	}

	/**
	 * 修改密码
	 * 
	 * @return
	 * @throws Exception
	 */
	public String alterPassword() throws Exception {
		return "password";
	}
	
	/**
	 * 修改密码保存
	 * 
	 * @return
	 * @throws Exception
	 */
	public String savePassword() throws Exception {
		user = userManager.getUserById(ContextUtils.getUserId());
		user.setPassword(newPassword);
		userManager.saveUser(user);
		renderText(user.getId()+"");
		return null;
	}
	
	public User getModel() {
		return null;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	@Required
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
}
