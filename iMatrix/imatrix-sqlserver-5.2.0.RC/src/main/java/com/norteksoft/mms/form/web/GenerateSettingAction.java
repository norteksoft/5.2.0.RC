package com.norteksoft.mms.form.web;

import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.mms.form.entity.GenerateSetting;
import com.norteksoft.mms.form.service.DataTableManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;

@Namespace("/form")
@ParentPackage("default")
public class GenerateSettingAction extends CrudActionSupport<GenerateSetting> {
	private static final long serialVersionUID = 1L;

	private Long tableId;
	
	private MenuManager menuManager;

	private DataTableManager dataTableManager;
	private GenerateSetting generateSetting;//生成代码配置实体
	private Long settingId;//生成代码配置实体id
	private Long menuId;
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	
	private Log log = LogFactory.getLog(GenerateSettingAction.class);
	protected void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	@Autowired
	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}
	@Override
	@Action("data-table-generateSetting")
	public String list() throws Exception {
		prepareModel();
		ApiFactory.getBussinessLogService().log("数据表管理", 
				"生成代码配置页面", 
				ContextUtils.getSystemId("mms"));
		return "data-table-setting";
	}

	@Override
	public String input() throws Exception {
		return null;
	}

	/**
	 * 保存生成代码配置信息
	 */
	@Override
	@Action("data-table-saveSetting")
	public String save() throws Exception {
		dataTableManager.saveGenerateSetting(generateSetting);
		ApiFactory.getBussinessLogService().log("数据表管理", 
				"保存生成代码配置信息", 
				ContextUtils.getSystemId("mms"));
		addSuccessMessage("保存成功");
		return "data-table-setting";
	}


	@Override
	public String delete() throws Exception {
		return null;
	}


	@Override
	protected void prepareModel() throws Exception {
		if(settingId==null){
			if(tableId!=null){
				generateSetting = dataTableManager.getGenerateSettingByTable(tableId);
			}
			if(generateSetting==null){
				generateSetting = new GenerateSetting();
			}
			if(tableId!=null){
				generateSetting.setTableId(tableId);
			}
		}else{
			generateSetting = dataTableManager.getGenerateSetting(settingId);
			if(generateSetting!=null){
				if(tableId!=null){
					generateSetting.setTableId(tableId);
				}
			}
		}
	}
	@Action("generate-code-menu-tree")
	public String generateCodeMenuTree() throws Exception{
		List<Menu> menus=menuManager.getEnabledStandardRootMenuByCompany();
		java.util.Collections.sort(menus);
		StringBuilder tree = new StringBuilder("[ ");
		for(Menu menu :menus){
			if(menu.getChildren()==null||menu.getChildren().isEmpty()){
				tree.append(JsTreeUtils.generateJsTreeNodeNew(menu.getId().toString(), "close",  menu.getName(), "")).append(",");
			}else{
				tree.append(JsTreeUtils.generateJsTreeNodeNew(menu.getId().toString(), "close",  menu.getName(), childMenu(menu.getChildren()),"")).append(",");
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
				tree.append(JsTreeUtils.generateJsTreeNodeNew(menu.getId().toString(), "",  menu.getName(), "")).append(",");
			}else{
				tree.append(JsTreeUtils.generateJsTreeNodeNew(menu.getId().toString(), "close",  menu.getName(), childMenu(menu.getChildren()),"")).append(",");
			}
		}
		JsTreeUtils.removeLastComma(tree);
		return tree.toString();
	}
	public GenerateSetting getModel() {
		return generateSetting;
	}

	@Autowired
	public void setDataTableManager(DataTableManager dataTableManager) {
		this.dataTableManager = dataTableManager;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public Long getTableId() {
		return tableId;
	}
	public GenerateSetting getGenerateSetting() {
		return generateSetting;
	}
	public Long getSettingId() {
		return settingId;
	}
	public void setSettingId(Long settingId) {
		this.settingId = settingId;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	
}
