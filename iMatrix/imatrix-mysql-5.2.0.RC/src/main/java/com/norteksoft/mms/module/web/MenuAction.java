package com.norteksoft.mms.module.web;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.mms.base.data.DataHandle;
import com.norteksoft.mms.form.enumeration.MenuType;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
@Namespace("/module")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "http://www.baidu.com", type = "redirectAction")})

public class MenuAction extends CrudActionSupport<Menu>{
	
	private static final long serialVersionUID = 1L;
	
	private Page<Menu> page = new Page<Menu>(Page.EACH_PAGE_TWENTY,true);
	
	private Long menuId;
	
	private Long parentMenuId;
	
	private Menu menu;
	
	private MenuManager menuManager;
	
	private DataHandle dataHandle;
	
	private Long choseSystemId ;
	
	private String isCreateSystem;
	
	private String parentMenuName;
	
	private File file;
	private String fileName;
	
	@Autowired
	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}

	@Autowired
	public void setDataHandle(DataHandle dataHandle) {
		this.dataHandle = dataHandle;
	}
	
	public void prepareDelete() throws Exception {
		this.prepareModel();
	}
	@Override
	public String delete() throws Exception {
		ApiFactory.getBussinessLogService().log("菜单管理", 
				"删除菜单", 
				ContextUtils.getSystemId("mms"));
		if(menu.getLayer()==1&&menu.getEnableState().equals(DataState.ENABLE)){
			this.renderText("false");
		}else{
			this.renderText(menuManager.deleteMenu(menu));
		}
		return null;
	}
	@Override
	@Action("menu-input")
	public String input() throws Exception {
		ApiFactory.getBussinessLogService().log("菜单管理", 
				"菜单表单", 
				ContextUtils.getSystemId("mms"));
		if(menu.getEnableState().equals(DataState.ENABLE)){
			this.renderText("false");
		}else{
			this.renderText("success");
			parentMenuName = menu.getParent()==null?"":menu.getParent().getName();
			return "menu-input";
		}
		return null;
	}
	@Override
	@Action("menu")
	public String list() throws Exception {
		ApiFactory.getBussinessLogService().log("菜单管理", 
				"显示菜单", 
				ContextUtils.getSystemId("mms"));
		return "menu";
	}
	
	@Override
	public String save() throws Exception {
		ApiFactory.getBussinessLogService().log("菜单管理", 
				"保存菜单", 
				ContextUtils.getSystemId("mms"));
		String msg = uniqueMenu();
		if(msg.equals("true")){
			uploadIcon();
			if(choseSystemId!=null){
				menu.setSystemId(choseSystemId);
				menu.setType(MenuType.STANDARD);
			}
			if(menu.getEnableState().equals(DataState.ENABLE)){
				List<Menu> mList = new ArrayList<Menu>();
				menuManager.getMenuParents(mList,menu);
				String data = "";
				for(Menu m : mList){
					m.setEnableState(DataState.ENABLE);
					setMenuEvent(m);
					menuManager.saveMenu(m);
					data += m.getId()+"="+m.getName()+"("+this.getText(m.getEnableState().code)+")"+",";
				}
				this.renderText("enable:"+data.substring(0,data.length()-1)+":"+menu.getId()+"-"+menu.getName()+"("+this.getText(menu.getEnableState().code)+")");
			}else{
				setMenuEvent(menu);
			    menuManager.saveMenu(menu);
			    this.renderText(menu.getId()+":"+menu.getName()+"("+this.getText(menu.getEnableState().code)+")");
			}
			
		}else{
			this.renderText("msg:" + msg);
		}
		return null;
	}
	
	//给menu事件属性添加参数menuId
	private void setMenuEvent(Menu m) {
		if(m.getEvent()!=null && m.getEvent()!=""){
			String [] s = m.getEvent().split("'");
			if(s.length>1){
				String event = "";
				if(s[1].indexOf("menuId")<0){
					if(s[1].indexOf("?")>0){
						s[1]=s[1]+"&menuId="+m.getId();
					}else{
						s[1]=s[1]+"?menuId="+m.getId();
					}
					for (String str : s) {
						event+=str+"'";
					}
					m.setEvent(event.substring(0, event.length()-1));
				}
			}
		}
	}

	private void uploadIcon() throws IOException{
		// 上传菜单标签
		if(StringUtils.isNotEmpty(fileName)){
			String localName = getLocalPath();
			if(menu.getIconName()!=null){
				FileUtils.deleteQuietly(new File(localName+menu.getImageUrl()));
			}
			String[] fs = fileName.split("\\.");
			String iconName = (new Date()).getTime()+"."+fs[fs.length-1];
			FileUtils.copyFile(file, new File(localName+iconName));
			menu.setIconName(fileName);
			menu.setImageUrl(iconName);
		}
	}
	
	private String getLocalPath() {
		String localPath = ServletActionContext.getServletContext().getRealPath("/");
		return localPath+"icons/";
	}
	
	/*
	 * 验证当前菜单的编号和名称是否唯一，如果唯一返回"true"，否则返回消息；
	 */
	private String uniqueMenu(){
		List<Menu> menus = null;
		if(menu.getParent()==null){
			menus = menuManager.getRootMenuByCompany();
		}else{
			menus = menu.getParent().getChildren();
		}
		for(Menu m: menus){
			if(!m.getId().equals(menu.getId())){
				if(choseSystemId!=null &&m.getSystemId().equals(choseSystemId)){
					return this.getText("menu.unique.validate.tip.system.created");
				}
				if(m.getName().equals(menu.getName())&&m.getCode().equals(menu.getCode())){
					return this.getText("menu.unique.validate.tip.name.code.used");
				}
				if(m.getName().equals(menu.getName())){
					return this.getText("menu.unique.validate.tip.name.used");
				}
				if(m.getCode().equals(menu.getCode())){
					return this.getText("menu.unique.validate.tip.code.used");
				}
			}
		}
		return "true";
	}
	
	@Action("menu-tree")
	public String list2() throws Exception {
		return SUCCESS;
	}
	
	/**
	 * 表单菜单树
	 * @return
	 * @throws Exception
	 */
	public String menuTree() throws Exception{
		List<Menu> menus = menuManager.getRootMenuByCompany();
		java.util.Collections.sort(menus);
		StringBuilder tree = new StringBuilder("[ ");
		for(Menu menu :menus){
			if(menu.getChildren()==null||menu.getChildren().isEmpty()){
				tree.append(JsTreeUtils.generateJsTreeNodeNew(menu.getId().toString(), "close",  menu.getName()+"("+this.getText(menu.getEnableState().code)+")", "")).append(",");
			}else{
				tree.append(JsTreeUtils.generateJsTreeNodeNew(menu.getId().toString(), "close",  menu.getName()+"("+this.getText(menu.getEnableState().code)+")", childMenu(menu.getChildren()),"")).append(",");
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
			if(menu.getChildren()==null||menu.getChildren().isEmpty()){
				tree.append(JsTreeUtils.generateJsTreeNodeNew(menu.getId().toString(), "",  menu.getName()+"("+this.getText(menu.getEnableState().code)+")", "")).append(",");
			}else{
				tree.append(JsTreeUtils.generateJsTreeNodeNew(menu.getId().toString(), "close",  menu.getName()+"("+this.getText(menu.getEnableState().code)+")", childMenu(menu.getChildren()),"")).append(",");
			}
		}
		JsTreeUtils.removeLastComma(tree);
		return tree.toString();
	}
	
	public void prepareEnable() throws Exception {
		this.prepareModel();
	}
	/**
	 * 启用菜单
	 */
	public String enable() throws Exception{
		List<Menu> mList = new ArrayList<Menu>();
		menuManager.getMenuParents(mList,menu);
		String data = "";
		for(Menu m : mList){
			m.setEnableState(DataState.ENABLE);
			menuManager.saveMenu(m);
			data += m.getId()+"="+m.getName()+"("+this.getText(m.getEnableState().code)+")"+",";
		}
		ApiFactory.getBussinessLogService().log("菜单管理", 
				"启用菜单", 
				ContextUtils.getSystemId("mms"));
		this.renderText(data.substring(0,data.length()-1));
		return null;
	}
	
	public void prepareDisableMenu() throws Exception {
		this.prepareModel();
	}
	/**
	 * 禁用用菜单
	 */
	public String disableMenu() throws Exception{
		menu.setEnableState(DataState.DISABLE);
		menuManager.saveMenu(menu);
		ApiFactory.getBussinessLogService().log("菜单管理", 
				"禁用菜单", 
				ContextUtils.getSystemId("mms"));
		this.renderText(menu.getName()+"("+this.getText(menu.getEnableState().code)+")");
		return null;
	}
		
	/**
	 * 系统默认跳转
	 */
	public String redirectToSystem() throws Exception{
		HttpServletResponse response = Struts2Utils.getResponse();
		String url=(String)Struts2Utils.getRequest().getRequestURI();
		String[] urls=url.split("/");
		//底层系统应用地址
		String imatrixCode=SystemUrls.getSystemUrl("imatrix");
		imatrixCode=imatrixCode.substring(imatrixCode.lastIndexOf("/")+1);
		String code=urls[1];
		if(imatrixCode.equals(code)){
			code=urls[2];
		}
		Menu lastMenu = menuManager.getDefaultModulePageBySystem(code, ContextUtils.getCompanyId());
		String goldPath = lastMenu.getUrl();
		String[] partPaths = goldPath.split("/");
		response.sendRedirect(SystemUrls.getBusinessPath(ContextUtils.getSystemCode())+"/"+partPaths[1] + "/" + partPaths[2] );
		return null;
	}
	/**
	 * 导出菜单
	 * @return
	 * @throws Exception
	 */
	@Action("export-menu")
	public String exportMenu() throws Exception{
		HttpServletResponse response = ServletActionContext.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode("menu-info.xls","UTF-8"));
		dataHandle.exportMenu(response.getOutputStream());
		ApiFactory.getBussinessLogService().log("菜单管理", 
				"导出菜单", 
				ContextUtils.getSystemId("mms"));

		return null;
	}
	@Action("show-import-menu")
	public String showImportMenu() throws Exception{
		return "show-import-menu";
	}
	/**
	 * 导入菜单
	 * @return
	 * @throws Exception
	 */
	@Action("import-menu")
	public String importMenu() throws Exception{
		if(fileName==null || !fileName.endsWith(".xls")){
			this.addActionMessage("请选择excel文件格式");
			return "show-import-menu";
		}
		boolean success = true;
		try {
			ApiFactory.getBussinessLogService().log("菜单管理", 
					"导入菜单", 
					ContextUtils.getSystemId("mms"));
			dataHandle.importMenu(file,null);
		} catch (Exception e) {
			success = false;
		}
		if(success){
			this.addActionMessage("导入成功");
		}else{
			this.addActionMessage("导入失败，请检查excel文件格式");
		}
		return "show-import-menu";
	}
	@Action("update-url-cache")
	public String updateUrlCache() throws Exception{
		SystemUrls.updateUrls();
		ApiFactory.getBussinessLogService().log("菜单管理", 
				"更新url缓存", 
				ContextUtils.getSystemId("mms"));
		return null;
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if(menuId==null || menuId.intValue()==0){
			menu = new Menu();
		}else{
			menu = menuManager.getMenu(menuId);
		}
		if(parentMenuId!=null && parentMenuId.intValue()!=0){
			menu.setParent(menuManager.getMenu(parentMenuId));
		}
	}	
		
	public Menu getModel() {
		return menu;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	public Page<Menu> getPage() {
		return page;
	}
	public void setParentMenuId(Long parentMenuId) {
		this.parentMenuId = parentMenuId;
	}
	public Long getChoseSystemId() {
		return choseSystemId;
	}

	public void setChoseSystemId(Long choseSystemId) {
		this.choseSystemId = choseSystemId;
	}

	public String getIsCreateSystem() {
		return isCreateSystem;
	}

	public void setIsCreateSystem(String isCreateSystem) {
		this.isCreateSystem = isCreateSystem;
	}

	public String getParentMenuName() {
		return parentMenuName;
	}

	public void setParentMenuName(String parentMenuName) {
		this.parentMenuName = parentMenuName;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setFileFileName(String fileName) {
		this.fileName = fileName;
	}
}
