package com.norteksoft.mms.module.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.mms.module.entity.Button;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.entity.ModulePage;
import com.norteksoft.mms.module.enumeration.ViewType;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.mms.module.service.ModulePageManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;

@Namespace("/module")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "module-page", type = "redirectAction")})
public class ModulePageAction extends CrudActionSupport<ModulePage> {
	private static final long serialVersionUID = 1L;
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String FROM_BOTTON_VIEW = "fromBottonView";
	
	private Page<ModulePage> page=new Page<ModulePage>(0,true);
	private ModulePage modulePage;
	private Long pageId;
	private Long menuId;
	private String pageIds;
	private Long viewId;
	private List<Button> listBtns=new ArrayList<Button>();
	private String myCode;
	private String viewCode;//视图的编号
	private String type;
	private String html;

	private MenuManager menuManager;
	private ModulePageManager modulePageManager;
	private FormViewManager formViewManager;
	private ListViewManager listViewManager;
	
	private List<FormView> formViews;
	private List<ListView> listViews;
	private List<ListColumn> listViewColumns;
	
	private String fromBottonView;//判断是否是button的预览
	
	@Autowired
	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}
	@Autowired
	public void setModulePageManager(ModulePageManager modulePageManager) {
		this.modulePageManager = modulePageManager;
	}
	@Autowired
	public void setFormViewManager(FormViewManager formViewManager) {
		this.formViewManager = formViewManager;
	}
	@Autowired
	public void setListViewManager(ListViewManager listViewManager) {
		this.listViewManager = listViewManager;
	}
	private void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	private void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	@Override
	@Action("module-page-delete")
	public String delete() throws Exception {
		String msg=modulePageManager.deleteModulePages(pageIds);
		if(msg!=null){
			addSuccessMessage(msg);
		}
		ApiFactory.getBussinessLogService().log("页面管理", 
				"删除页面", 
				ContextUtils.getSystemId("mms"));
		return list();
	}

	@Override
	public String input() throws Exception {
		if(pageId==null)pageId=modulePage.getId();
		if(modulePage.getView()!=null){
			viewId=modulePage.getView().getId();
		}
		ApiFactory.getBussinessLogService().log("页面管理", 
				"页面表单", 
				ContextUtils.getSystemId("mms"));
		return INPUT;
	}

	@Override
	@Action("module-page-list")
	public String list() throws Exception {
		List<Menu> menus = menuManager.getEnabledRootMenuByCompany();
		if(menuId==null&&menus.size()>0){
			menuId = menus.get(0).getId();
		}
		if(menuId!=null){
			if(page.getPageSize()>1){
				modulePageManager.getModulePagesByMenuId(page,menuId);
				ApiFactory.getBussinessLogService().log("页面管理", 
						"页面列表", 
						ContextUtils.getSystemId("mms"));
				this.renderText(PageUtils.pageToJson(page));
				return null;
			}
		}
		return "module-page-list";
	}

	@Override
	public String save() throws Exception {
		if(viewId!=null){
			if(ViewType.LIST_VIEW.toString().equals(modulePage.getViewType().toString())){
				modulePage.setView(listViewManager.getView(viewId));
			}else if(ViewType.FORM_VIEW.toString().equals(modulePage.getViewType().toString())){
				modulePage.setView(formViewManager.getFormView(viewId));
			}
		}
		modulePageManager.saveModulePage(modulePage);
		ApiFactory.getBussinessLogService().log("页面管理", 
				"保存页面", 
				ContextUtils.getSystemId("mms"));
		addSuccessMessage("保存成功");
		return input();
	}
	
	/**
	 * 菜单树
	 * 
	 */
	public String tree() throws Exception {
		List<Menu> menus = menuManager.getEnabledRootMenuByCompany();
		java.util.Collections.sort(menus);
		StringBuilder tree = new StringBuilder("[ ");
		for(Menu menu :menus){
			List<Menu> children=menuManager.getChildrenEnabledMenus(menu.getId());
			if(children==null||children.size()<=0){
				tree.append(JsTreeUtils.generateJsTreeNodeNew(menu.getId().toString(), "close", menu.getName(),"")).append(",");
			}else{
				tree.append(JsTreeUtils.generateJsTreeNodeNew(menu.getId().toString(), "close", menu.getName(),childMenu(children),"")).append(",");
			}
		}
		JsTreeUtils.removeLastComma(tree);
		tree.append(" ]");
		renderText(tree.toString());
		return null;
	}
	/*
	 * 递归菜单父子关系，形成tree
	 */
	private String childMenu(List<Menu> menus){
		java.util.Collections.sort(menus);
		StringBuilder tree = new StringBuilder();
		for(Menu menu :menus){
			List<Menu> children=menuManager.getChildrenEnabledMenus(menu.getId());
			if(children==null||children.size()<=0){
				tree.append(JsTreeUtils.generateJsTreeNodeNew(menu.getId().toString(), "", menu.getName(),"")).append(",");
			}else{
				tree.append(JsTreeUtils.generateJsTreeNodeNew(menu.getId().toString(), "close", menu.getName(),childMenu(children),"")).append(",");
			}
		}
		JsTreeUtils.removeLastComma(tree);
		return tree.toString();
	}
	@Action("module-page-defaultDisplaySet")
	public String defaultDisplaySet() throws Exception {
		ModulePage modulePage=modulePageManager.getModulePage(pageId);
		String before=modulePage.getDefaultDisplay()?"是":"否";
		if(modulePageManager.defaultDisplaySet(pageId,menuId)){
			modulePage=modulePageManager.getModulePage(pageId);
			String end=modulePage.getDefaultDisplay()?"是":"否";
			addSuccessMessage("设置默认:"+before+"-->"+end);
		}else{
			addErrorMessage("当前菜单中已有默认显示的页面");
		}
		ApiFactory.getBussinessLogService().log("页面管理", 
				"设置默认页面", 
				ContextUtils.getSystemId("mms"));
		return list();
	}
	@Action("module-page-enableSet")
	public String enableSet() throws Exception {
		addSuccessMessage(modulePageManager.enableSet(pageIds));
		ApiFactory.getBussinessLogService().log("页面管理", 
				"启用页面", 
				ContextUtils.getSystemId("mms"));
		return list();
	}
	
	public String validateCode() throws Exception{
		this.renderText(modulePageManager.isCodeExist(myCode,pageId).toString());
		return null;
	}
	
	public String preview() throws Exception{
		if(pageId!=null){
			ModulePage modulePage = modulePageManager.getModulePage(pageId);
			if(modulePage!=null){
				myCode=modulePage.getCode();
				viewCode = modulePage.getView().getCode();
				type=modulePage.getViewType().toString();
				ApiFactory.getBussinessLogService().log("页面管理", 
						"预览页面", 
						ContextUtils.getSystemId("mms"));
				return "list-view-preview";
			}
		}
		return null;
	}
	/**
	 * 选择表单列表
	 * @return
	 * @throws Exception
	 */
	@Action("module-page-showViews")
	public String showViews() throws Exception{
		StringBuilder tree = new StringBuilder("[ ");
		if(ViewType.LIST_VIEW.toString().equals(type)){
			listViews=listViewManager.getListViewsBySystem(menuId);
			for(ListView view:listViews){
				tree.append(JsTreeUtils.generateJsTreeNodeNew(view.getId()+"-"+view.getName(), "", view.getName(),"")).append(",");
			}
		}else if(ViewType.FORM_VIEW.toString().equals(type)){
			formViews=formViewManager.getFormViewsBySystem(menuId);
			for(FormView view:formViews){
				tree.append(JsTreeUtils.generateJsTreeNodeNew(view.getId()+"-"+view.getName(), "", view.getName()+"("+view.getVersion()+")","")).append(",");
			}
		}
		JsTreeUtils.removeLastComma(tree);
		tree.append(" ]");
		renderText(tree.toString());
		return null;
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if(pageId==null || pageId.intValue()==0){
			modulePage = new ModulePage();
		}else{
			modulePage = modulePageManager.getModulePage(pageId);
		}
		if(menuId!=null && menuId.intValue()!=0){
			modulePage.setMenuId(menuId);
		}
	}
	
	public ModulePage getModel() {
		return modulePage;
	}

	public Long getPageId() {
		return pageId;
	}

	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}

	public Page<ModulePage> getPage() {
		return page;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setPageIds(String pageIds) {
		this.pageIds = pageIds;
	}
	public List<Button> getListBtns() {
		return listBtns;
	}
	public void setListBtns(List<Button> listBtns) {
		this.listBtns = listBtns;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<FormView> getFormViews() {
		return formViews;
	}
	public List<ListView> getListViews() {
		return listViews;
	}
	public void setViewId(Long viewId) {
		this.viewId = viewId;
	}
	public Long getViewId() {
		return viewId;
	}
	public void setMyCode(String myCode) {
		this.myCode = myCode;
	}
	public List<ListColumn> getListViewColumns() {
		return listViewColumns;
	}
	public String getHtml() {
		return html;
	}
	public String getFromBottonView() {
		return fromBottonView;
	}
	public void setFromBottonView(String fromBottonView) {
		this.fromBottonView = fromBottonView;
	}
	public String getMyCode() {
		return myCode;
	}
	public String getViewCode() {
		return viewCode;
	}
	public void setViewCode(String viewCode) {
		this.viewCode = viewCode;
	}
	public String getType() {
		return type;
	}
}
