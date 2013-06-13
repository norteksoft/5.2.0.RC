package com.norteksoft.acs.web.query;



import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.log.LoginLog;
import com.norteksoft.acs.service.query.QueryManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PageUtils;
/**
 * 
 */

@SuppressWarnings("unchecked")
@Namespace("/query")
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "query", type = "redirectAction") })
public class QueryAction extends CRUDActionSupport {

	private static final long serialVersionUID = 6243522614297434118L;

	private Page<LoginLog> page = new Page<LoginLog>(0, true);//每页5项，自动查询计算总页数.
	private LoginLog entity;
	private QueryManager queryManager;
	@Override
	protected void prepareModel() throws Exception {
		entity = new LoginLog();
	}

	public LoginLog getModel() {

		
		return entity;
	}

	@Override
	public String delete() throws Exception {

		return null;
	}
	public void prepareLoginUserLoglist()throws Exception{
		prepareModel();
	}
	
	public String loginUserLoglist() throws Exception {
		queryManager.getListByLoginUserLog(page, entity); 
		return SUCCESS;
	}
	
	@Action("list")
	public String toList() throws Exception{
		return SUCCESS;
	}
	
	@Override
	@Action("query")
	public String list() throws Exception {
		if(page.getPageSize() <= 1){
			return SUCCESS; 
		}else{
			queryManager.getListByLoginUserLog(page, entity); 
//			renderHtml(PageUtils.pageToJson(page));
			ApiFactory.getBussinessLogService().log("在线用户查询", 
					"查看在线用户列表",ContextUtils.getSystemId("acs"));
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
	}

	@Override
	public String save() throws Exception {

		return null;
	}

	public Page<LoginLog> getPage() {
		return page;
	}

	public void setPage(Page<LoginLog> page) {
		this.page = page;
	}

	public LoginLog getEntity() {
		return entity;
	}

	public void setEntity(LoginLog entity) {
		this.entity = entity;
	}

	public QueryManager getQueryManager() {
		return queryManager;
	}
	
	@Required
	public void setQueryManager(QueryManager queryManager) {
		this.queryManager = queryManager;
	}

	
}
