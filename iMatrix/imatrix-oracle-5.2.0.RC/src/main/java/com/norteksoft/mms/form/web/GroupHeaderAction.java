package com.norteksoft.mms.form.web;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.mms.form.entity.GroupHeader;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.service.GroupHeaderManager;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Namespace("/form")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "group-header", type = "redirectAction")})
public class GroupHeaderAction extends CrudActionSupport<GroupHeader>{
	private static final long serialVersionUID = 1L;
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	private Long id;
	private Long viewId;
	private ListView listView;
	private Long menuId;
	private List<GroupHeader> groupHeaders;
	
	@Autowired
	private ListViewManager  listViewManager;
	@Autowired
	private GroupHeaderManager  groupHeaderManager;
	
	
	@Override
	@Action("group-header-delete")
	public String delete() throws Exception {
		groupHeaderManager.delete(id);
		String callback=Struts2Utils.getParameter("callback");
		ApiFactory.getBussinessLogService().log("列表管理", 
				"删除组合头信息", 
				ContextUtils.getSystemId("mms"));
		this.renderText(callback+"({msg:'删除成功'})");
		return null;
	}

	@Override
	public String input() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Action("group-header")
	public String list() throws Exception {
		listView=listViewManager.getView(viewId);
		groupHeaders=groupHeaderManager.getGroupHeadersByViewId(viewId);
		ApiFactory.getBussinessLogService().log("列表管理", 
				"组合头信息列表", 
				ContextUtils.getSystemId("mms"));
		return "group-header";
	}

	@Override
	protected void prepareModel() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Action("group-header-save")
	public String save() throws Exception {
		groupHeaderManager.save(viewId);
		ApiFactory.getBussinessLogService().log("列表管理", 
				"保存组合头信息", 
				ContextUtils.getSystemId("mms"));
		addSuccessMessage("保存成功");
		return list();
	}

	public GroupHeader getModel() {
		// TODO Auto-generated method stub
		return null;
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getViewId() {
		return viewId;
	}

	public void setViewId(Long viewId) {
		this.viewId = viewId;
	}
	
	public ListView getListView() {
		return listView;
	}
	public List<GroupHeader> getGroupHeaders() {
		return groupHeaders;
	}

	public Long getMenuId() {
		return menuId;
	}

	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}

}
