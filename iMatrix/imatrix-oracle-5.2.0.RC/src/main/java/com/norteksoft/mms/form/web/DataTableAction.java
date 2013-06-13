package com.norteksoft.mms.form.web;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.mms.base.data.DataHandle;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.GenerateSetting;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.service.DataTableManager;
import com.norteksoft.mms.form.service.ImportDataTableManager;
import com.norteksoft.mms.form.service.SheetManager;
import com.norteksoft.mms.form.service.TableColumnManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.ZipUtils;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.product.util.freemarker.TemplateRender;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Namespace("/form")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "data-table", type = "redirectAction") })
public class DataTableAction extends CrudActionSupport<DataTable> {
	private static final long serialVersionUID = 1L;

	private DataTable dataTable;

	private Long tableId;
	
	private Long menuId;
	
	private MenuManager menuManager;

	private DataHandle dataHandle;
	
	private Page<DataTable> dataTables = new Page<DataTable>(0, true);

	private List<Long> tableIds;

	private DataTableManager dataTableManager;

	private TableColumnManager tableColumnManager;
	
	private SheetManager sheetManager;
	
	@Autowired
	private ImportDataTableManager importDataTableManager;

	private String tableName;

	private List<TableColumn> columns;

	private String states;

	private boolean canChange;
	private boolean deleteEnable = false;
	private String ids;
	
	private File file;
	private Long id;//数据表字段id
	private String fileName;
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	
	private Log log = LogFactory.getLog(DataTableAction.class);
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
	@Autowired
	public void setDataHandle(DataHandle dataHandle) {
		this.dataHandle = dataHandle;
	}
	
	@Override
	@Action("data-table-list-data")
	public String list() throws Exception {
		List<Menu> menus = menuManager.getEnabledStandardRootMenuByCompany();
		if(menuId==null&&menus.size()>0){
			menuId = menus.get(0).getId();
		}
		if(menuId!=null){
			if(dataTables.getPageSize()>1){
				dataTableManager.getSystemAllDataTables(dataTables, menuId);
				ApiFactory.getBussinessLogService().log("数据表管理", 
						"数据表列表", 
						ContextUtils.getSystemId("mms"));
				this.renderText(PageUtils.pageToJson(dataTables));
				return null;
			}
		}
		return "data-table";
	}
	
	@Action("data-table-defaultDataTableList")
	public String defaultDataTableList() throws Exception {
		if(menuId!=null){
			if(dataTables.getPageSize()>1){
				dataTableManager.getSystemDefaultDataTables(dataTables, menuId);
				ApiFactory.getBussinessLogService().log("数据表管理", 
						"数据表列表", 
						ContextUtils.getSystemId("mms"));
				this.renderText(PageUtils.pageToJson(dataTables));
				return null;
			}
		}
		return "data-table-default";
	}

	public String findAllEnableDateTable() throws Exception {
		dataTableManager.getAllEnableDataTables(dataTables);
		return "enable";
	}

	@Override
	@Action("data-table-input")
	public String input() throws Exception {
		ApiFactory.getBussinessLogService().log("数据表管理", 
				"数据表表单", 
				ContextUtils.getSystemId("mms"));
		return "data-table-input";
	}
	
	@Action("data-table-viewCustom")
	public String viewCustom() throws Exception {
		dataTable = dataTableManager.getDataTable(tableId);
		return "data-table-view";
	}

	public String checkTableName() throws Exception {
		renderText(String.valueOf(dataTableManager.getDataTableByName(
				tableName, tableId)));
		return null;
	}

	/**
	 * 保存只存在改表结构，在草稿到启用时才是建表
	 */
	@Override
	@Action("data-table-save")
	public String save() throws Exception {
		dataTableManager.saveDataTable(dataTable);
		ApiFactory.getBussinessLogService().log("数据表管理", 
				"保存数据表", 
				ContextUtils.getSystemId("mms"));
		addSuccessMessage("保存成功");
		return "data-table-input";
	}

	public void prepareDealWithTableColumn() throws Exception {
		dataTable = dataTableManager.getDataTable(tableId);
	}

	/**
	 * 字段设置页面
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("data-table-dealWithTableColumn")
	public String dealWithTableColumn() throws Exception {
		dataTable = dataTableManager.getDataTable(tableId);
		columns=tableColumnManager.getTableColumnByDataTableId(tableId);
		ApiFactory.getBussinessLogService().log("数据表管理", 
				"字段信息设置页面", 
				ContextUtils.getSystemId("mms"));
		return "data-table-columns";
	}
	/**
	 * 自定义数据表查看页面字段设置页面
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("data-table-viewCustomTableColumn")
	public String viewCustomTableColumn() throws Exception {
		dataTable = dataTableManager.getDataTable(tableId);
		columns=tableColumnManager.getTableColumnByDataTableId(tableId);
		ApiFactory.getBussinessLogService().log("数据表管理", 
				"字段信息设置页面", 
				ContextUtils.getSystemId("mms"));
		return "data-table-custom-columns";
	}

	/**
	 * 删除数据表信息--只能为草稿状态的
	 */
	@Override
	public String delete() throws Exception {
		if(deleteEnable){
			deleteEnable();
		}else{
			deleteDraft();
		}
		ApiFactory.getBussinessLogService().log("数据表管理", 
				"删除数据表", 
				ContextUtils.getSystemId("mms"));
		return list();
	}
	private void deleteEnable(){
		dataTableManager.deleteEnableDataTables(tableIds);
		addSuccessMessage("删除成功");
	}
	private void deleteDraft(){
		boolean canDelete = true;
		for (Long tableId : tableIds) {
			dataTable = dataTableManager.getDataTable(tableId);
			if (dataTable.getTableState() != DataState.DRAFT) {
				canDelete = false;
			}
		}
		if (canDelete) {
			dataTableManager.deleteDataTables(tableIds);
			addSuccessMessage("删除成功");
		} else {
			addErrorMessage("不能删除已启用和禁用的数据表");
		}
	}
	
	/**
	 * 删除启用或草稿状态的自定义数据表
	 */
	@Action("data-table-deleteCustom")
	public String deleteCustom() throws Exception {
		if(deleteEnable){
			deleteEnable();
		}else{
			deleteDraft();
		}
		ApiFactory.getBussinessLogService().log("数据表管理", 
				"删除数据表", 
				ContextUtils.getSystemId("mms"));
		return defaultDataTableList();
	}

	public void prepareSaveColumns() throws Exception {
		dataTable = dataTableManager.getDataTable(tableId);
	}

	/**
	 * 保存字段信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public String saveColumns() throws Exception {
		tableColumnManager.saveTableColumns(dataTable);
		columns=tableColumnManager.getTableColumnByDataTableId(tableId);
		ApiFactory.getBussinessLogService().log("数据表管理", 
				"保存字段信息", 
				ContextUtils.getSystemId("mms"));
		addSuccessMessage("保存成功");
		return "columns";
	}

//	public void prepareChangeTableState() throws Exception {
//		dataTable = dataTableManager.getDataTable(tableId);
//	}

	/**
	 * 改变数据表的状态(草稿->启用;启用->禁用;禁用->启用)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String changeTableState() throws Exception {
		//dataTableManager.changeTableState(tableIds);
		addSuccessMessage(dataTableManager.changeTableState(tableIds,menuId));
		ApiFactory.getBussinessLogService().log("数据表管理", 
				"改变数据表的状态", 
				ContextUtils.getSystemId("mms"));
		log.debug("table info has saved");
		return list();
	}
	
	public String exportToExcel() throws Exception{
		HttpServletResponse response = Struts2Utils.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode("字段信息.xls","UTF-8"));
		sheetManager.exportToExcel(response.getOutputStream());
		return null;
	}
	
	public String showImport() throws Exception{
		return "import";
	}
	
	public String importInto() throws Exception{
		dataTable = dataTableManager.getDataTable(tableId);
		columns=sheetManager.importIntoData(file,dataTable);
		addSuccessMessage("导入成功");
		return "import";
	}

	@Override
	protected void prepareModel() throws Exception {
		if (tableId != null) {
			dataTable = dataTableManager.getDataTable(tableId);
			menuId=dataTable.getMenuId();
		} else {
			dataTable = new DataTable();
		}
		if(menuId!=null && menuId.intValue()!=0){
			dataTable.setMenuId(menuId);
		}
	}
	/**
	 * 数据表管理的系统树
	 */
	public String dataTableStandardSysTree() throws Exception {
		StringBuilder tree = new StringBuilder("[ ");
		
		List<Menu> menus1 = menuManager.getEnabledStandardRootMenuByCompany();
		java.util.Collections.sort(menus1);
		StringBuilder subNodes1 = new StringBuilder();
		for(Menu menu :menus1){
			subNodes1.append(JsTreeUtils.generateJsTreeNodeNew(menu.getId().toString()+"_STANDARD", null, menu.getName(),"")).append(",");
		}
		JsTreeUtils.removeLastComma(subNodes1);
		
		if(menus1.isEmpty()){
			tree.append(JsTreeUtils.generateJsTreeNodeDefault("STANDARD_SYSTEM", "close", 
					"标准数据表",subNodes1.toString())).append(",");
		}else{
		    tree.append(JsTreeUtils.generateJsTreeNodeDefault("STANDARD_SYSTEM", "open", 
				    "标准数据表",subNodes1.toString())).append(",");
		}
		
		List<Menu> menus2 = menuManager.getEnabledCustomRootMenuByCompany();
		StringBuilder subNodes2 = new StringBuilder();
		for(Menu menu :menus2){
			subNodes2.append(JsTreeUtils.generateJsTreeNodeNew(menu.getId().toString()+"_CUSTOM", null, menu.getName(),"")).append(",");
		}
		JsTreeUtils.removeLastComma(subNodes2);
		
		if(menus2.isEmpty()){
			tree.append(JsTreeUtils.generateJsTreeNodeDefault("CUSTOM_SYSTEM", "close", 
					"自定义数据表",subNodes2.toString())).append(",");
		}else{
		    tree.append(JsTreeUtils.generateJsTreeNodeDefault("CUSTOM_SYSTEM", "open", 
				    "自定义数据表",subNodes2.toString())).append(",");
		}
		
		JsTreeUtils.removeLastComma(tree);
		
		
		tree.append(" ]");
		renderText(tree.toString());
		return null;
	}
	
	/**
	 * 数据表管理的系统树
	 */
	public String dataTableTree() throws Exception {
		List<Menu> menus = menuManager.getEnabledRootMenuByCompany();
		java.util.Collections.sort(menus);
		StringBuilder tree = new StringBuilder("[ ");
		for(Menu menu :menus){
			tree.append(JsTreeUtils.generateJsTreeNodeNew(menu.getId().toString(), "root", menu.getName(),"")).append(",");
//				tree.append(JsTreeUtil.generateJsTreeNode(menu.getId().toString(), "root", menu.getName())).append(",");
		}
		JsTreeUtils.removeLastComma(tree);
		tree.append(" ]");
		renderText(tree.toString());
		return null;
	}
	/**
	 * 删除数据表字段
	 * @return
	 * @throws Exception
	 */
	@Action("delete-table-column")
	public String deleteTableColumn() throws Exception {
		tableColumnManager.deleteTableColumn(id);
		ApiFactory.getBussinessLogService().log("数据表管理", 
				"删除数据表字段", 
				ContextUtils.getSystemId("mms"));
		String callback=Struts2Utils.getParameter("callback");
		this.renderText(callback+"({msg:'删除成功'})");
		return null;
	}

	/**
	 * 导出数据表及字段信息
	 * @return
	 * @throws Exception
	 */
	@Action("export-data-table")
	public String exportDataTable() throws Exception{
		HttpServletResponse response = ServletActionContext.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		Menu menu=menuManager.getMenu(menuId);
		String name="data-table";
		if(menu!=null)name=name+"-"+menu.getCode();
		response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode(name+".xls","UTF-8"));
		dataHandle.exportDataTable(response.getOutputStream(),tableIds,menuId);
		ApiFactory.getBussinessLogService().log("数据表管理", 
				"导出数据表", 
				ContextUtils.getSystemId("mms"));
		return null;
	}
	@Action("show-import-data-table")
	public String showImportDataTable() throws Exception{
		return "show-import-data-table";
	}
	/**
	 * 导入数据表及字段信息
	 * @return
	 * @throws Exception
	 */
	@Action("import-data-table")
	public String importDataTable() throws Exception{
		String result = "";
		try {
			result = ApiFactory.getDataImporterService().importData(file, fileName,importDataTableManager);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		renderText(result);
		return null;
	}
	@Action("generate-code")
	public String generateCode() throws Exception{
		try {
			String[] tempIds=ids.split(",");
			HttpServletResponse response = Struts2Utils.getResponse();
			response.reset();
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/x-download");
			response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode("generate-code.zip","utf-8"));
			for(String dataTableId:tempIds){
				boolean processFlag=false;//表示是否走流程
				GenerateSetting setting = dataTableManager.getGenerateSettingByTable(Long.valueOf(dataTableId));
				String workflowCode="";
				if(setting!=null){
					processFlag=setting.getFlowable();
					workflowCode=setting.getWorkflowCode();
				}
				dataTable=dataTableManager.getDataTable(Long.valueOf(dataTableId));
				Map<String, Object> dataModel= new HashMap<String, Object>();
				if(setting==null||setting.getEntitative()){//如果对应数据表没有设置代码生成配置 ，或者生成代码配置里设置生成实体，则生成实体代码
					dataModel =dataTableManager.generateEntity(dataTable,processFlag);
					TagUtil.generateFile(dataModel, dataModel.get("filePath").toString(), dataModel.get("fileName").toString(), dataModel.get("templateName").toString());
				}
				dataModel = dataTableManager.generateDao(dataTable);
				TagUtil.generateFile(dataModel, dataModel.get("filePath").toString(), dataModel.get("fileName").toString(), dataModel.get("templateName").toString());
				dataModel = dataTableManager.generateService(dataTable,processFlag);
				TagUtil.generateFile(dataModel, dataModel.get("filePath").toString(), dataModel.get("fileName").toString(), dataModel.get("templateName").toString());
				dataModel=dataTableManager.generateAction(dataTable,processFlag,menuId,workflowCode);
				TagUtil.generateFile(dataModel, dataModel.get("filePath").toString(), dataModel.get("fileName").toString(), dataModel.get("templateName").toString());
				TagUtil.generateFile(dataModel, "menus/", "header.jsp", "generateHeader.ftl");
				TagUtil.generateFile(null, "menus/", "second-menu.jsp", "generateSecondMenu.ftl");
				TagUtil.generateFile(null, "menus/", "third-menu.jsp", "generateThirdMenu.ftl");
				dataModel=dataTableManager.generateList(dataTable,processFlag,menuId,workflowCode);
				TagUtil.generateFile(dataModel, dataModel.get("filePath").toString(), dataModel.get("fileName").toString(), dataModel.get("templateName").toString());
				dataModel=dataTableManager.generateInput(dataTable,processFlag,menuId,workflowCode);
				TagUtil.generateFile(dataModel, dataModel.get("filePath").toString(), dataModel.get("fileName").toString(), dataModel.get("templateName").toString());
				dataModel=dataTableManager.generateEditableList(dataTable,processFlag);
				TagUtil.generateFile(dataModel, dataModel.get("filePath").toString(), dataModel.get("fileName").toString(), dataModel.get("templateName").toString());
				if(processFlag){
					dataModel=dataTableManager.generateTask(dataTable);
					TagUtil.generateFile(dataModel, dataModel.get("filePath").toString(), dataModel.get("fileName").toString(), dataModel.get("templateName").toString());
					TagUtil.generateFile(dataModel, "jsp/"+dataModel.get("nameSpace").toString()+"/", dataModel.get("lowCaseEntityName").toString()+"-history.jsp", "generateHistory.ftl");
				}
			}
			OutputStream fileOut=response.getOutputStream();
			ZipUtils.zipFolder(TemplateRender.GENERATE_DIR, fileOut);
			
			if(fileOut!=null)fileOut.close();
			FileUtils.deleteDirectory(new File(TemplateRender.GENERATE_DIR));//删除文件夹
		}catch (Exception e) {
			e.printStackTrace(); 
		}
		return null;
	}
	
	public DataTable getModel() {
		return dataTable;
	}

	@Autowired
	public void setDataTableManager(DataTableManager dataTableManager) {
		this.dataTableManager = dataTableManager;
	}

	@Autowired
	public void setTableColumnManager(TableColumnManager tableColumnManager) {
		this.tableColumnManager = tableColumnManager;
	}
	@Autowired
	public void setSheetManager(SheetManager sheetManager) {
		this.sheetManager = sheetManager;
	}

	public void setDataTables(Page<DataTable> dataTables) {
		this.dataTables = dataTables;
	}

	public Page<DataTable> getDataTables() {
		return dataTables;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public Long getTableId() {
		return tableId;
	}

	public void setTableIds(List<Long> tableIds) {
		this.tableIds = tableIds;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<TableColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<TableColumn> columns) {
		this.columns = columns;
	}

	public String getStates() {
		return states;
	}

	public void setStates(String states) {
		this.states = states;
	}

	public boolean isCanChange() {
		return canChange;
	}
	public void setCanChange(boolean canChange) {
		this.canChange = canChange;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public DataTable getDataTable() {
		return dataTable;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setFileFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setDeleteEnable(boolean deleteEnable) {
		this.deleteEnable = deleteEnable;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	
}
