package com.norteksoft.acs.web.query;

import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleUser;
import com.norteksoft.acs.service.query.QueryManager;
import com.norteksoft.product.orm.Page;

public class QueryRoleAction extends CRUDActionSupport<RoleUser>{
	

	private static final long serialVersionUID = -6605119159080817215L;
	
	private RoleUser role_u;
	
	private Role role;

	private QueryManager queryManager;

	private Page<RoleUser> page = new Page<RoleUser>(20, true);// 每页5项，自动查询计算总页数.
	

	@Override
	protected void prepareModel() throws Exception {
	
		role_u = new RoleUser();
	}
	
	public RoleUser getModel() {
		
		return role_u;
	}
	@Override
	public String delete() throws Exception {
	
		return null;
	}

	@Override
	public String list() throws Exception {
	
		return null;
	}

	@Override
	public String save() throws Exception {
	
		return null;
	}

	
	public String roleList() throws Exception {
		page = queryManager.getListByRole(page,role);
		return SUCCESS;
	}
	
	
	public QueryManager getQueryManager() {
		return queryManager;
	}
	@Required
	public void setQueryManager(QueryManager queryManager) {
		this.queryManager = queryManager;
	}

	public Page<RoleUser> getPage() {
		return page;
	}

	public void setPage(Page<RoleUser> page) {
		this.page = page;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	
}
