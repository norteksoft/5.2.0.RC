package com.norteksoft.acs.web.query;

import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.acs.service.query.QueryManager;
import com.norteksoft.product.orm.Page;

public class QueryUserAction extends CRUDActionSupport<UserInfo> {

	private static final long serialVersionUID = 6842131085592759548L;

	private UserInfo userInfo;

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	private QueryManager queryManager;

	private Page<UserInfo> page = new Page<UserInfo>(20, true);// 每页5项，自动查询计算总页数.
	
	private String initialListView;
	
	
	@Override
	protected void prepareModel() throws Exception {
		userInfo = new UserInfo();
	}

	public UserInfo getModel() {

		return userInfo;
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
	 * 用户查询
	 * 
	 * @return
	 * @throws Exception
	 */
	public void prepareUserList() throws Exception {
		prepareModel();
	}

	public String userList() throws Exception {
		page = queryManager.getListByUser(page,userInfo, initialListView);
		return SUCCESS;

	}
	
	public QueryManager getQueryManager() {
		return queryManager;
	}

	@Required
	public void setQueryManager(QueryManager queryManager) {
		this.queryManager = queryManager;
	}
	
	public Page<UserInfo> getPage() {
		return page;
	}

	public void setPage(Page<UserInfo> page) {
		this.page = page;
	}

	public String getInitialListView() {
		return initialListView;
	}

	public void setInitialListView(String initialListView) {
		this.initialListView = initialListView;
	}


}
