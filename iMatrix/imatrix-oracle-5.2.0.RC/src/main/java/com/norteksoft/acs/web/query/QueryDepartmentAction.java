package com.norteksoft.acs.web.query;

import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.DepartmentUser;
import com.norteksoft.acs.service.query.QueryManager;
import com.norteksoft.product.orm.Page;

public class QueryDepartmentAction extends CRUDActionSupport<DepartmentUser> {

	private static final long serialVersionUID = -5863073849103475381L;

	private Department department;

	private DepartmentUser department_u;
	
	private QueryManager queryManager;

	private Page<DepartmentUser> page = new Page<DepartmentUser>(20, true);// 每页5项，自动查询计算总页数.

	@Override
	protected void prepareModel() throws Exception {

		department_u = new DepartmentUser();
	}

	public DepartmentUser getModel() {

		return department_u;
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
	

	public String departmentList() throws Exception {
		 page = queryManager.getListByDepartment(page,department);
		 return SUCCESS;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public QueryManager getQueryManager() {
		return queryManager;
	}

	@Required
	public void setQueryManager(QueryManager queryManager) {
		this.queryManager = queryManager;
	}

	public Page<DepartmentUser> getPage() {
		return page;
	}

	public void setPage(Page<DepartmentUser> page) {
		this.page = page;
	}

}
