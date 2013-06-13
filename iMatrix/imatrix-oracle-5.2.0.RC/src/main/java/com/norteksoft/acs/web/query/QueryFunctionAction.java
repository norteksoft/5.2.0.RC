package com.norteksoft.acs.web.query;



import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.RoleFunction;
import com.norteksoft.acs.service.query.QueryManager;
import com.norteksoft.product.orm.Page;

public class QueryFunctionAction extends CRUDActionSupport<RoleFunction> {

	private static final long serialVersionUID = -277247429114432447L;

	private Function entity;
	
	private RoleFunction role_f;
	
	private QueryManager queryManager;

	private Page<RoleFunction> page = new Page<RoleFunction>(20, true);// 每页5项，自动查询计算总页数.
	

	@Override
	protected void prepareModel() throws Exception {
		role_f = new RoleFunction();
	}

	public RoleFunction getModel() {

		return role_f;
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

	/**
	 *权限查询
	 * 
	 * @return
	 * @throws Exception
	 */
	public void prepareFunctionList() throws Exception {

	        prepareModel();
	}

	public String functionList() throws Exception {
		page = queryManager.getListByFunction(page,entity);
		return SUCCESS;
	}
	
	
	public Function getEntity() {
		return entity;
	}

	public void setEntity(Function entity) {
		this.entity = entity;
	}

	public QueryManager getQueryManager() {
		return queryManager;
	}
	@Required
	public void setQueryManager(QueryManager queryManager) {
		this.queryManager = queryManager;
	}

	public Page<RoleFunction> getPage() {
		return page;
	}

	public void setPage(Page<RoleFunction> page) {
		this.page = page;
	}
}
