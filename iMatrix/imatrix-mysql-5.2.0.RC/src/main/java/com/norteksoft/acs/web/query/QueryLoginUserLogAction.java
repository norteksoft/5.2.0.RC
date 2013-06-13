package com.norteksoft.acs.web.query;

import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.log.LoginLog;
import com.norteksoft.acs.service.query.QueryManager;
import com.norteksoft.product.orm.Page;

@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "query-login-user-log", type="redirectAction") })
public class QueryLoginUserLogAction extends CRUDActionSupport<LoginLog>{

	
	private static final long serialVersionUID = -4208719370540909565L;
    
	private Page<LoginLog> page = new Page<LoginLog>(20, true);//每页5项，自动查询计算总页数.
	private LoginLog entity;
	
	public void setEntity(LoginLog entity) {
		this.entity = entity;
	}
	private QueryManager queryManager;

	@Override
	public String delete() throws Exception {
	
		return null;
	}
    
	
	@Override
	public String list() throws Exception {
		return SUCCESS;
	}

	public void prepareLoginUserLoglist()throws Exception{
		prepareModel();
	}
	
	public String loginUserLoglist() throws Exception {
		queryManager.getListByLoginUserLog(page, entity); 
		return SUCCESS;
	}
	@Override
	protected void prepareModel() throws Exception {
		entity = new LoginLog();
		
	}

	@Override
	public String save() throws Exception {
		
		return null;
	}

	public LoginLog getModel() {
		
		return entity;
	}

	public QueryManager getQueryManager() {
		return queryManager;
	}
	@Required
	public void setQueryManager(QueryManager queryManager) {
		this.queryManager = queryManager;
	}


	public Page<LoginLog> getPage() {
		return page;
	}


	public void setPage(Page<LoginLog> page) {
		this.page = page;
	}
	
}
