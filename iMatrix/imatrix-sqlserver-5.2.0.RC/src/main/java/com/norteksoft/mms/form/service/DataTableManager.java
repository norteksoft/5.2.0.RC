package com.norteksoft.mms.form.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.FunctionGroup;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.FunctionGroupManager;
import com.norteksoft.acs.service.authorization.FunctionManager;
import com.norteksoft.mms.base.GenerateCodeUtils;
import com.norteksoft.mms.form.dao.DataTableDao;
import com.norteksoft.mms.form.dao.GenerateSettingDao;
import com.norteksoft.mms.form.dao.ListColumnDao;
import com.norteksoft.mms.form.dao.TableColumnDao;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.GenerateSetting;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.jdbc.JdbcSupport;
import com.norteksoft.mms.module.dao.MenuDao;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;

@Service
public class DataTableManager {

	private DataTableDao dataTableDao;
	private TableColumnDao tableColumnDao;
	private ListColumnDao listColumnDao;
	private JdbcSupport jdbcDao;
	private FormViewManager formViewManager;
	private ListViewManager listViewManager;
	private GenerateSettingDao generateSettingDao;
	private Log log = LogFactory.getLog(DataTableManager.class);
	private FormHtmlParser formHtmlParser = new FormHtmlParser();
	
	@Autowired
	private FunctionManager functionManager;
	@Autowired
	private FunctionGroupManager functionGroupManager;
	@Autowired
	private MenuDao menuDao;
	@Autowired
	private BusinessSystemManager businessSystemManager;
	

	@Autowired
	public void setDataTableDao(DataTableDao dataTableDao) {
		this.dataTableDao = dataTableDao;
	}

	@Autowired
	public void setTableColumnDao(TableColumnDao tableColumnDao) {
		this.tableColumnDao = tableColumnDao;
	}
	
	@Autowired
	public void setFormViewManager(FormViewManager formViewManager) {
		this.formViewManager = formViewManager;
	}
	
	@Autowired
	public void setListViewManager(ListViewManager listViewManager) {
		this.listViewManager = listViewManager;
	}
	@Autowired
	public void setGenerateSettingDao(GenerateSettingDao generateSettingDao) {
		this.generateSettingDao = generateSettingDao;
	}
	@Autowired
	public void setListColumnDao(ListColumnDao listColumnDao) {
		this.listColumnDao = listColumnDao;
	}
	
	public void setJdbcDao(JdbcSupport jdbcDao) {
		this.jdbcDao = jdbcDao;
	}

	/**
	 * 查询数据表实体
	 * 
	 * @param dataTableId
	 * @return 数据表实体
	 */
	public DataTable getDataTable(Long dataTableId) {
		return dataTableDao.get(dataTableId);
	}

	/**
	 * 查询所有的数据表(以createDate排序)
	 * 
	 * @param tables
	 */
	public void getAllDataTables(Page<DataTable> tables) {
		dataTableDao.findAllDataTable(tables);
	}

	/**
	 * 查询所有启用的数据表(以createDate排序)
	 * 
	 * @param tables
	 */
	public void getAllEnableDataTables(Page<DataTable> tables) {
		dataTableDao.findAllEnabledDataTable(tables);
	}

	/**
	 * 查询启用的数据表集合(以createDate排序)
	 * 
	 * @return 返回数据表集合
	 */
	public List<DataTable> getEnabledDataTables() {
		return dataTableDao.getEnabledDataTables();
	}
	
	/**
	 * 查询启用的数据表集合(以createDate排序)
	 * 
	 * @return 返回数据表集合
	 */
	public List<DataTable> getAllEnabledDataTables() {
		return dataTableDao.getAllEnabledDataTables();
	}

	/**
	 * 通过表名查询数据表实体
	 * 
	 * @param tableName
	 * @param tableId
	 * @return 返回是否验证成功
	 */
	public boolean getDataTableByName(String tableName, Long tableId) {
		DataTable dataTable = dataTableDao.findDataTableByName(tableName);
		if (tableId != null) { // 修改
			if (dataTable != null) {
				if (dataTable.getId().longValue() == tableId.longValue()) {
					return true; // 通过
				} else {
					return false; // 不通过
				}
			} else {
				return true;
			}
		} else { // 新建
			if (dataTable != null) {
				return false;
			} else {
				return true;
			}
		}
	}

	/**
	 * 查询数据表字段
	 * 
	 * @param tableColumnId
	 * @return 返回表字段实体
	 */
	public TableColumn getTableColumn(Long tableColumnId) {
		return tableColumnDao.get(tableColumnId);
	}

	/**
	 * 删除草稿状态的数据表
	 * 
	 * @param ids
	 */
	@Transactional(readOnly = false)
	public void deleteDataTables(List<Long> ids) {
		for (Long long1 : ids) {
			dataTableDao.delete(long1);
		}
	}
	
	@Transactional(readOnly = false)
	public void deleteEnableDataTables(List<Long> ids) {
		for (Long long1 : ids) {
			// 删列表、表单、页面
			List<FormView> fvs = formViewManager.getFormViewByDataTable(long1);
			for(FormView fv : fvs){
				formViewManager.deleteFormView(fv.getId());
			}
			List<ListView> lvs = listViewManager.getListViewByDataTable(long1);
			for(ListView lv : lvs){
				listColumnDao.deleteListColumnsByView(lv.getId());
				listViewManager.deleteEnable(lv.getId());
			}
			//删除数据表对应的字段
			tableColumnDao.deleteTableColumnsByTable(long1);
			//删除数据表
			dataTableDao.delete(long1);
		}
	}
	/**
	 * 保存数据表信息
	 * 
	 * @param dataTable
	 */
	@Transactional(readOnly = false)
	public void saveDataTable(DataTable dataTable) {
		if (dataTable.getId() == null) {
			dataTable.setCompanyId(ContextUtils.getCompanyId());
			dataTable.setCreator(ContextUtils.getLoginName());
			dataTable.setCreatorName(ContextUtils.getUserName());
			if(dataTable.getTableState()==null||"".equals(dataTable.getTableState())){
				dataTable.setTableState(DataState.DRAFT);
			}
		}
		dataTable.setCreatedTime(new Date());
		dataTableDao.save(dataTable);
	}

	/**
	 * 获取所有未删除的表字段
	 * 
	 * @param dataTable
	 * @return 返回显示的表字段
	 */
	public List<TableColumn> getAllUnDeleteColumns(DataTable dataTable) {
		List<TableColumn> tableColumns = tableColumnDao.getTableColumnByDataTableId(dataTable.getId());
		return tableColumns;
	}

	/**
	 * 创建数据表
	 * 
	 * @param dataTable
	 */
	@Transactional(readOnly = false)
	public void createTable(FormView formView) {
		try{
			String tableName ="mms_"+formView.getCode();
			List<FormControl> controls=formHtmlParser.getControls(formView.getHtml());
			log.debug("begin to create table :" + tableName);
			jdbcDao.createDefaultTable(tableName, controls);
			log.debug("create table " + tableName + " end");
//			log.debug("begin to create sequence :" + tableName + "_ID");
//			jdbcDao.createSequence(tableName + "_ID");
		}catch (BadSqlGrammarException e) {
			log.debug(e.getMessage());
		}
	}
	
	/**
	 * 根据实体名获取数据表
	 * @param entityName
	 * @return
	 */
	public DataTable getDataTableByEntity(String entityName){
		return dataTableDao.getDataTableByEntity(entityName);
	}

	/**
	 * 生成默认视图
	 * 
	 * @param dataTable
	 */
	@Transactional(readOnly = false)
	public void createDefaultView(DataTable dataTable, Long menuId) {
		String tableAlias = dataTable.getAlias();
		String name = dataTable.getName();
		String remark = dataTable.getRemark();
		FormView formView = new FormView();
		formView.setDataTable(dataTable);
		formView.setCode(name);
		formView.setName(tableAlias);
		formView.setRemark(remark);
		List<TableColumn> columns=tableColumnDao.getTableColumnByDataTableId(dataTable.getId());
		StringBuilder html=new StringBuilder();
		for(TableColumn col:columns){
			if(DataType.TEXT.equals(col.getDataType())||DataType.ENUM.equals(col.getDataType())){//文本、枚举
				html.append(getTextControlHtml(col,"TEXT"));
			}else if(DataType.DATE.equals(col.getDataType())){//日期
				html.append(getDateControlHtml(col,"DATE"));
			}else if(DataType.TIME.equals(col.getDataType())){//时间
				html.append(getDateControlHtml(col,"TIME"));
			}else if(DataType.INTEGER.equals(col.getDataType())||DataType.NUMBER.equals(col.getDataType())){//整型、数字
				html.append(getTextControlHtml(col,"INTEGER"));
			}else if(DataType.LONG.equals(col.getDataType())){//长整型
				html.append(getTextControlHtml(col,"LONG"));
			}else if(DataType.DOUBLE.equals(col.getDataType())||DataType.FLOAT.equals(col.getDataType())||DataType.AMOUNT.equals(col.getDataType())){//浮点数、金额
				html.append(getTextControlHtml(col,"DOUBLE"));
			}else if(DataType.BOOLEAN.equals(col.getDataType())){//布尔型
				html.append(getRadioControlHtml(col));
			}else if(DataType.CLOB.equals(col.getDataType())){//大文本
				html.append(getTextareaControlHtml(col));
			}else if(DataType.COLLECTION.equals(col.getDataType())){//集合
				html.append(getCollectionControlHtml(col,name));
			}
			
		}
		formView.setHtml(html.toString());
		formView.setStandard(true);
		formView.setMenuId(menuId);
		formView.setFormState(DataState.ENABLE);
		formViewManager.saveFormView(formView, menuId,null,html.toString());
		listViewManager.createDefaultListView(dataTable, name, tableAlias, remark, menuId);
	}
	private String getCollectionControlHtml(TableColumn col,String dataTableName) {
		StringBuilder html=new StringBuilder();
		html.append("<p>");
		html.append(getInternation(col.getAlias())).append(":");
		html.append("<input");
		html.append(" id=\"").append(col.getName()).append("\"");
		html.append(" title=\"").append(getInternation(col.getAlias())).append("\"");
		html.append(" value=\"").append(getInternation(col.getAlias())).append("\"");
		html.append(" type=\"button\" datatype=\"COLLECTION\" plugintype=\"STANDARD_LIST_CONTROL\" dbname=\"\"");
		html.append(" name=\"").append(col.getName()).append("\"");
		html.append(" columnid=\"").append(col.getId()).append("\"");
		html.append(" listviewcode=\"").append(dataTableName).append("\"");//listviewcode默认为dataTableName
		html.append("/>");
		html.append("</p>");
		return html.toString();
	}
	private String getRadioControlHtml(TableColumn col) {
		StringBuilder html=new StringBuilder();
		html.append("<p>");
		html.append(getInternation(col.getAlias())).append(":");
		html.append(packagingRadio(col,"是"));
		html.append(packagingRadio(col,"否"));
		html.append("</p>");
		return html.toString();
	}
	
	private String packagingRadio(TableColumn col,String type){
		StringBuilder html=new StringBuilder();
		html.append(type);
		html.append("<input");
		if("是".equals(type)){
			html.append(" id=\"").append(col.getName()).append("1\"");
			html.append(" title=\"是\"");
			html.append(" value=\"true\"");
		}else{
			html.append(" id=\"").append(col.getName()).append("2\"");
			html.append(" title=\"否\"");
			html.append(" value=\"false\"");
		}
		html.append(" type=\"radio\"");
		html.append(" name=\"").append(col.getName()).append("\"");
		html.append(" datatype=\"BOOLEAN\"");
		html.append(" format=\"number\" request=\"false\" signaturevisible=\"false\" formattip=\"数字\" readolny=\"false\" formattype=\"null\" plugintype=\"TEXT\"");
		html.append("/>");
		return html.toString();
	}
	
	private String getTextControlHtml(TableColumn col,String datatype) {
		StringBuilder html=new StringBuilder();
		html.append("<p>");
		html.append(getInternation(col.getAlias())).append(":");
		html.append("<input");
		html.append(" id=\"").append(col.getName()).append("\"");
		html.append(" title=\"").append(getInternation(col.getAlias())).append("\"");
		if(StringUtils.isNotEmpty(col.getDefaultValue())){
			html.append(" value=\"").append(col.getDefaultValue()).append("\"");
		}
		if(col.getMaxLength()!=null){
			html.append(" maxlength=\"").append(col.getMaxLength()).append("\"");
		}
		html.append(" name=\"").append(col.getName()).append("\"");
		html.append(" datatype=\"").append(datatype).append("\"");
		html.append(" format=\"number\" request=\"false\" signaturevisible=\"false\" formattip=\"数字\" readolny=\"false\" formattype=\"null\" plugintype=\"TEXT\"");
		html.append("/>");
		html.append("</p>");
		return html.toString();
	}
	
	private String getTextareaControlHtml(TableColumn col) {
		StringBuilder html=new StringBuilder();
		html.append("<p>");
		html.append(getInternation(col.getAlias())).append(":");
		html.append("<textarea");
		html.append(" id=\"").append(col.getName()).append("\"");
		html.append(" title=\"").append(getInternation(col.getAlias())).append("\"");
		html.append(" name=\"").append(col.getName()).append("\"");
		html.append(" dataType=\"").append(col.getDataType()).append("\"");
		if(col.getMaxLength()==null){
			html.append(" maxlength=\"\"");
		}else{
			html.append(" maxlength=\"").append(col.getMaxLength()).append("\"");
			html.append(" onkeyup=\"calTextareaLen(value,").append(col.getMaxLength()).append(",this);\" ");
		}
		html.append(" style=\"width:354px;height:139px;\"  plugintype=\"textarea\" ");
		html.append(" defaultvalue=\"").append(col.getDefaultValue()==null?"":col.getDefaultValue()).append("\"");
		html.append(">");
		html.append(col.getDefaultValue()==null?"":col.getDefaultValue());
		html.append("</textarea> ");
		html.append("</p>");
		return html.toString();
	}
	private String getDateControlHtml(TableColumn col,String datatype) {
		StringBuilder html=new StringBuilder();
		html.append("<p>");
		html.append(getInternation(col.getAlias())).append(":");
		html.append("<input");
		html.append(" id=\"").append(col.getName()).append("\"");
		html.append(" title=\"").append(getInternation(col.getAlias())).append("\"");
		if(StringUtils.isNotEmpty(col.getDefaultValue())){
			html.append(" value=\"").append(col.getDefaultValue()).append("\"");
		}
		html.append(" datatype=\"").append(datatype).append("\"");
		if("DATE".equals(datatype)){
			html.append(" format=\"yyyy-MM-dd\"");
		}else{
			html.append(" format=\"yyyy-MM-dd HH:mm\"");
		}
		html.append(" request=\"false\"  plugintype=\"TIME\" readonly=\"readonly\" ");
		html.append(" name=\"").append(col.getName()).append("\"");
		html.append("/>");
		html.append("</p>");
		return html.toString();
	}

	@Transactional(readOnly = false)
	public String changeTableState(List<Long> tableIds, Long menuId){
		int draftToEn=0,enToDis=0,disToEn=0;
		StringBuilder sbu=new StringBuilder("");
		for(Long tableId:tableIds){
			DataTable dataTable = getDataTable(tableId);
			if (dataTable.getTableState().equals(DataState.DRAFT)) {// 草稿->启用
				log.debug("table state has change to " + DataState.ENABLE.toString());
				dataTable.setTableState(DataState.ENABLE);
				draftToEn++;
				log.debug("begin to create defaultView");
				createDefaultView(dataTable, menuId);
			} else if (dataTable.getTableState().equals(DataState.ENABLE)) {// 启用->禁用
				log.debug("table state has change to " + DataState.DISABLE.toString());
				dataTable.setTableState(DataState.DISABLE);
				enToDis++;
			} else if (dataTable.getTableState().equals(DataState.DISABLE)) {// 禁用->启用
				log.debug("table state has change to " + DataState.ENABLE.toString());
				dataTable.setTableState(DataState.ENABLE);
				disToEn++;
			}
			saveDataTable(dataTable);
		}
		sbu.append(draftToEn).append("个草稿->启用,")
		.append(enToDis).append("个启用->禁用,")
		.append(disToEn).append("个禁用->启用");
		return sbu.toString();
	}

	/**
	 * 查询标准数据表(以createDate排序)
	 * 
	 * @param tables
	 */
	public List<DataTable> getStandardDataTables() {
		return dataTableDao.getStandardDataTables();
	}
	/**
	 * 查询自定义数据表(以createDate排序)
	 * 
	 * @param tables
	 */
	public List<DataTable> getDefaultDataTables() {
		return dataTableDao.getDefaultDataTables();
	}
	/**
	 * 查询某个系统下所有的数据表(以createDate排序)
	 * 
	 * @param tables
	 */
	public void getSystemAllDataTables(Page<DataTable> tables, Long menuId) {
		dataTableDao.findSystemAllDataTable(tables, menuId);
	}
	/**
	 * 查询某个系统下所有自定义的数据表(以createDate排序)
	 * 
	 * @param tables
	 */
	public void getSystemDefaultDataTables(Page<DataTable> tables, Long menuId) {
		dataTableDao.findSystemDefaultDataTable(tables, menuId);
	}
	/**
	 * 通过表名查询数据表实体
	 * 
	 * @param tableName
	 * @return 返回是否验证成功
	 */
	public DataTable getDataTableByTableName(String tableName) {
		return  dataTableDao.findDataTableByName(tableName);
	}
	/**
	 * 获得该菜单中所有的数据表（包括自定义的数据表）
	 * @return
	 */
	public List<DataTable> getAllDataTablesByMenu(Long menuId){
		return dataTableDao.getAllDataTablesByMenu(menuId);
	}
	public List<DataTable> getUnCompanyAllDataTablesByMenu(Long menuId){
		return dataTableDao.getUnCompanyAllDataTablesByMenu(menuId);
	}
	
	 public String getInternation(String code){
		 return ApiFactory.getSettingService().getInternationOptionValue(code);
	 }
	 public GenerateSetting getGenerateSettingByTable(Long tableId){
		 return generateSettingDao.getGenerateSettingByTable(tableId);
	 }
	 public GenerateSetting getGenerateSetting(Long settingId){
		 return generateSettingDao.getGenerateSetting(settingId);
	 }
	 public void saveGenerateSetting(GenerateSetting generateSetting){
		 generateSettingDao.save(generateSetting);
	 }
	 public Map<String,Object> generateService(DataTable dataTable, boolean processFlag) throws IOException {
			Map<String,Object> dataModel= new HashMap<String,Object>();
			String packageName = GenerateCodeUtils.getLastLayerPath(dataTable.getEntityName());
			String entityName = dataTable.getEntityName().substring(dataTable.getEntityName().lastIndexOf(".")+1,dataTable.getEntityName().length());
			dataModel.put("packageName", packageName);//包名
			dataModel.put("entityPath", dataTable.getEntityName());//实体引入路径
			dataModel.put("entityName", entityName);//实体名
			dataModel.put("lowCaseEntityName", GenerateCodeUtils.firstCharLowerCase(entityName));//小写实体名
			dataModel.put("processFlag", processFlag+"");
			
			dataModel.put("filePath", packageName.replaceAll("\\.", "/")+"/service/");
			dataModel.put("fileName", entityName+"Manager.java");
			dataModel.put("templateName", "generateService.ftl");
			return dataModel;
		}
		
	 public Map<String,Object> generateDao(DataTable dataTable) throws IOException {
		Map<String,Object> dataModel= new HashMap<String,Object>();
		String packageName = GenerateCodeUtils.getLastLayerPath(dataTable.getEntityName());
		String entityName = dataTable.getEntityName().substring(dataTable.getEntityName().lastIndexOf(".")+1,dataTable.getEntityName().length());
		dataModel.put("packageName", packageName);//包名
		dataModel.put("entityPath", dataTable.getEntityName());//实体引入路径
		dataModel.put("entityName", entityName);//实体名
		dataModel.put("lowCaseEntityName", GenerateCodeUtils.firstCharLowerCase(entityName));//小写实体名
		
		dataModel.put("filePath", packageName.replaceAll("\\.", "/")+"/dao/");
		dataModel.put("fileName", entityName+"Dao.java");
		dataModel.put("templateName", "generateDao.ftl");
		return dataModel;
	}
	 public Map<String, Object> generateAction(DataTable dataTable, boolean processFlag,Long menuId,String workflowCode)throws Exception{
		Map<String, Object> root = new HashMap<String, Object>();
		String className=dataTable.getEntityName();
		String entityName=className.substring(className.lastIndexOf(".")+1,className.length());
		String entityPath=className.substring(0,className.lastIndexOf("."));
		String packagePath=entityPath.substring(0,entityPath.lastIndexOf("."));
		String packageName=packagePath+".web";
		String namespace=packagePath.substring(packagePath.lastIndexOf(".")+1,packagePath.length());
		List<String> imports=new ArrayList<String>();
		imports.add(className);
		imports.add(packagePath+".service."+entityName+"Manager");
		String entityAttribute=GenerateCodeUtils.firstCharLowerCase(entityName);
		root.put("packageName", packageName);//包名
		root.put("namespace", namespace);
		root.put("entityName", entityName);//实体名
		root.put("entityAttribute", entityAttribute);//小写实体名
		root.put("imports", imports);
		root.put("containWorkflow", processFlag);
		root.put("workflowCode", workflowCode);
		root.put("ctx", "${ctx}");
		root.put("filePath", packageName.replaceAll("\\.", "/")+"/");
		root.put("fileName", entityName+"Action.java");
		root.put("templateName", "generateAction.ftl");
		addFunctions(namespace,processFlag,entityAttribute,menuId);
		return root;
	}
	
	private void addFunctions(String namespace,boolean processFlag,String entityAttribute,Long menuId) {
		Menu menu=menuDao.getMenu(menuId);
		BusinessSystem businessSystem= businessSystemManager.getBusiness(menu.getSystemId());
		if(processFlag){
			saveFunctions(namespace,entityAttribute,"放弃领取任务","abandonReceive",businessSystem);
			saveFunctions(namespace,entityAttribute,"加签","addSigner",businessSystem);
			saveFunctions(namespace,entityAttribute,"完成交互任务","completeInteractiveTask",businessSystem);
			saveFunctions(namespace,entityAttribute,"完成任务","completeTask",businessSystem);
			saveFunctions(namespace,entityAttribute,"领取任务","drawTask",businessSystem);
			saveFunctions(namespace,entityAttribute,"填写意见","fillOpinion",businessSystem);
			saveFunctions(namespace,entityAttribute,"流程监控中应急处理功能","processEmergency",businessSystem);
			saveFunctions(namespace,entityAttribute,"减签","removeSigner",businessSystem);
			saveFunctions(namespace,entityAttribute,"取回任务","retrieveTask",businessSystem);
			saveFunctions(namespace,entityAttribute,"显示流转历史","showHistory",businessSystem);
			saveFunctions(namespace,entityAttribute,"启动并提交流程","submitProcess",businessSystem);
			saveFunctions(namespace,entityAttribute,"办理任务页面","task",businessSystem);
			saveFunctions(namespace,entityAttribute,"抄送","copyTask",businessSystem);
			saveFunctions(namespace,entityAttribute,"退回任务","goback",businessSystem);
			saveFunctions(namespace,entityAttribute,"减签树","cutsignTree",businessSystem);
			saveFunctions(namespace,entityAttribute,"指派","assign",businessSystem);
		}
		saveFunctions(namespace,entityAttribute,"删除","delete",businessSystem);
		saveFunctions(namespace,entityAttribute,"新建页面","input",businessSystem);
		saveFunctions(namespace,entityAttribute,"列表页面","list",businessSystem);
		saveFunctions(namespace,entityAttribute,"可编辑列表页面","listEditable",businessSystem);
		saveFunctions(namespace,entityAttribute,"列表数据","listDatas",businessSystem);
		saveFunctions(namespace,entityAttribute,"保存","save",businessSystem);
		saveFunctions(namespace,entityAttribute,"编辑-保存","editSave",businessSystem);
		saveFunctions(namespace,entityAttribute,"编辑-删除","editDelete",businessSystem);
	}
	
	private void saveFunctions(String namespace,String entityAttribute,String functionName,String actionFunctionName,BusinessSystem businessSystem){
		String systemByCode=businessSystem.getCode();
		String functionGroupCode=systemByCode+"-default-functionGroup";
		FunctionGroup functionGroup=functionGroupManager.getFuncGroupByCode(functionGroupCode,businessSystem.getId());
		if(functionGroup==null){
			functionGroup=new FunctionGroup();
		}
		functionGroup.setCode(functionGroupCode);
		functionGroup.setName("默认资源组");
		functionGroup.setBusinessSystem(businessSystem);
		functionGroupManager.saveFunGroup(functionGroup);
		
		String functionPath="/"+namespace+"/"+entityAttribute+"-"+actionFunctionName+".htm";
		Function function=functionManager.getFunctionByPath(functionPath, businessSystem.getId());
		if(function==null){
			function=new Function();
		}
		function.setCode(systemByCode+"-"+namespace+"-"+entityAttribute+"-"+actionFunctionName);
		function.setName(functionName);
		function.setPath(functionPath);
		function.setFunctionGroup(functionGroup);
		function.setBusinessSystem(businessSystem);
		
		functionManager.saveFunction(function);
	}
	
	public Map<String, Object> generateList(DataTable dataTable, boolean processFlag,Long menuId,String workflowCode)throws Exception{
		Map<String, Object> root = new HashMap<String, Object>();
		String className=dataTable.getEntityName();
		String entityName=className.substring(className.lastIndexOf(".")+1,className.length());
		String entityPath=className.substring(0,className.lastIndexOf("."));
		String packagePath=entityPath.substring(0,entityPath.lastIndexOf("."));
		String namespace=packagePath.substring(packagePath.lastIndexOf(".")+1,packagePath.length());
		List<String> imports=new ArrayList<String>();
		imports.add(className);
		imports.add(packagePath+".service."+entityName+"Manager");
		String entityAttribute=GenerateCodeUtils.firstCharLowerCase(entityName);
		root.put("entityName", entityName);//实体名
		root.put("entityAttribute", entityAttribute);//小写实体名
		root.put("namespace", namespace);
		root.put("containWorkflow", processFlag);
		root.put("workflowCode", workflowCode);
		root.put("listCode", dataTable.getName());
		root.put("ctx", "${ctx}");
		root.put("resourcesCtx", "${resourcesCtx}");
		root.put("imatrixCtx", "${imatrixCtx}");
		root.put("filePath", "jsp/"+namespace+"/");
		root.put("fileName", entityAttribute+"-list.jsp");
		root.put("templateName", "generateList.ftl");
		return root;
	}
	public Map<String, Object> generateInput(DataTable dataTable, boolean processFlag,Long menuId,String workflowCode)throws Exception{
		Map<String, Object> root = new HashMap<String, Object>();
		String className=dataTable.getEntityName();
		String entityName=className.substring(className.lastIndexOf(".")+1,className.length());
		String entityPath=className.substring(0,className.lastIndexOf("."));
		String packagePath=entityPath.substring(0,entityPath.lastIndexOf("."));
		String namespace=packagePath.substring(packagePath.lastIndexOf(".")+1,packagePath.length());
		List<String> imports=new ArrayList<String>();
		imports.add(className);
		imports.add(packagePath+".service."+entityName+"Manager");
		String entityAttribute=GenerateCodeUtils.firstCharLowerCase(entityName);
		root.put("entityName", entityName);//实体名
		root.put("entityAttribute", entityAttribute);//小写实体名
		root.put("namespace", namespace);
		root.put("containWorkflow", processFlag);
		root.put("workflowCode", workflowCode);
		root.put("formCode", dataTable.getName());
		root.put("ctx", "${ctx}");
		root.put("id", "${id}");
		root.put("fieldPermission", "${fieldPermission}");
		root.put("resourcesCtx", "${resourcesCtx}");
		root.put("entityObject", "${"+entityAttribute+"}");
		root.put("filePath", "jsp/"+namespace+"/");
		root.put("fileName", entityAttribute+"-input.jsp");
		root.put("templateName", "generateInput.ftl");
		root.put("taskId", "${taskId }");
		root.put("taskTransact", "${taskTransact }");
		return root;
	}

	/**
	 * 生成实体代码
	 * @param dataTable
	 * @param processFlag2 
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public  Map<String,Object> generateEntity(DataTable dataTable, boolean processFlag) throws IOException {
			Map<String,Object> dataModel= new HashMap<String,Object>();
			Set<String> importList = new HashSet<String>();//需要引用类的集合
			List<String> attrList = new ArrayList<String>();//类属性集合
			List<String> methodList = new ArrayList<String>();//get和set方法集合
			//获取数据库类型（不同数据库大字段和大文本生成代码不同）
			String dataBase = PropUtils.getDataBase();
			String clob="", blob="";
			if(PropUtils.DATABASE_MYSQL.equals(dataBase)){
				clob="LONGTEXT";
				blob="LONGBLOB";
			}else if(PropUtils.DATABASE_ORACLE.equals(dataBase)){
				clob="CLOB";
				blob="BLOB";
			}else if(PropUtils.DATABASE_SQLSERVER.equals(dataBase)){
				clob="NTEXT";
				blob="image";
			}
			String packageName = dataTable.getEntityName().substring(0,dataTable.getEntityName().lastIndexOf("."));
			String entityName = dataTable.getEntityName().substring(dataTable.getEntityName().lastIndexOf(".")+1,dataTable.getEntityName().length());
			List<TableColumn> columns=tableColumnDao.getTableColumnByDataTableId(dataTable.getId());
			for (TableColumn column : columns) {
				if(column.getCasual()) importList.add("import javax.persistence.Transient;");
				String attrName = column.getName();
				String methodName = GenerateCodeUtils.firstCharUpperCase(attrName);//方法中大写的变量名
				if(attrName.contains(".")) continue;
				switch (column.getDataType()) {
				case TEXT:
					attrList.add("String_"+attrName+"_"+column.getCasual());//数据类型_属性名称_是否在数据库中生成
					methodList.add("String_"+methodName+"_"+attrName);//数据类型_属性名（第一个字母大写）_属性名
					break;
				case DATE:
					attrList.add("Date_"+attrName+"_"+column.getCasual());
					importList.add("import java.util.Date;");
					methodList.add("Date_"+methodName+"_"+attrName);
					break;
				case TIME:
					attrList.add("Date_"+attrName+"_"+column.getCasual());
					importList.add("import java.util.Date;");
					methodList.add("Date_"+methodName+"_"+attrName);
					break;
				case INTEGER:
					attrList.add("Integer_"+attrName+"_"+column.getCasual());
					methodList.add("Integer_"+methodName+"_"+attrName);
					break;
				case LONG:
					attrList.add("Long_"+attrName+"_"+column.getCasual());
					methodList.add("Long_"+methodName+"_"+attrName);
					break;
				case DOUBLE:
					attrList.add("Double_"+attrName+"_"+column.getCasual());
					methodList.add("Double_"+methodName+"_"+attrName);
					break;
				case FLOAT:
					attrList.add("Float_"+attrName+"_"+column.getCasual());
					methodList.add("Float_"+methodName+"_"+attrName);
					break;
				case BOOLEAN:
					attrList.add("Boolean_"+attrName+"_"+column.getCasual());
					methodList.add("Boolean_"+methodName+"_"+attrName);
					break;
				case CLOB:
					attrList.add("CLOB_"+attrName+"_"+column.getCasual());
					importList.add("import javax.persistence.Lob;");
					importList.add("import javax.persistence.Column;");
					
					methodList.add("String_"+methodName+"_"+attrName);
					break;
				case BLOB:
					attrList.add("BLOB_"+attrName+"_"+column.getCasual());
					importList.add("import javax.persistence.Lob;");
					importList.add("import javax.persistence.Column;");
					importList.add("import javax.persistence.Basic;");
					importList.add("import javax.persistence.FetchType;");
					methodList.add("byte[]_"+methodName+"_"+attrName);
					break;
				case COLLECTION:
					String collectionName = column.getObjectPath().substring(column.getObjectPath().lastIndexOf(".")+1,column.getObjectPath().length());
					attrList.add("List<"+collectionName+">"+"_"+attrName+"_"+column.getCasual());
					methodList.add("List<"+collectionName+">"+"_"+methodName+"_"+attrName);
					importList.add("import java.util.List;");
					break;
				case ENUM:
					String enumName = column.getObjectPath().substring(column.getObjectPath().lastIndexOf(".")+1,column.getObjectPath().length());
					attrList.add(enumName+"_"+attrName+"_"+column.getCasual());
					methodList.add(enumName+"_"+methodName+"_"+attrName);
					break;
				case REFERENCE:
					String refName = column.getObjectPath().substring(column.getObjectPath().lastIndexOf(".")+1,column.getObjectPath().length());
					attrList.add(refName+"_"+attrName+"_"+column.getCasual());
					methodList.add(refName+"_"+methodName+"_"+attrName);
					break;
				case AMOUNT:
					attrList.add("Float_"+attrName+"_"+column.getCasual());
					methodList.add("Float_"+methodName+"_"+attrName);
					break;
				case NUMBER:
					attrList.add("Integer_"+attrName+"_"+column.getCasual());
					methodList.add("Integer_"+methodName+"_"+attrName);
					break;
				default:
					break;
				}
				if(StringUtils.isNotEmpty(column.getObjectPath())){
					String path = column.getObjectPath();
					importList.add("import "+path+";");
				}
			}
			if(processFlag){
				importList.add("import com.norteksoft.wf.engine.client.ExtendField;");
				importList.add("import com.norteksoft.wf.engine.client.FormFlowable;");
				importList.add("import com.norteksoft.wf.engine.client.WorkflowInfo;");
				importList.add("import javax.persistence.Embedded;");
			}
			dataModel.put("packageName", packageName);//包名
			dataModel.put("tableName", dataTable.getName());//数据表名
			dataModel.put("entityName", entityName);//实体名
			dataModel.put("importList", importList);
			dataModel.put("attrList", attrList);
			dataModel.put("methodList", methodList);
			dataModel.put("clob", clob);
			dataModel.put("blob", blob);
			dataModel.put("processFlag", processFlag+"");
			
			dataModel.put("filePath", packageName.replaceAll("\\.", "/")+"/");
			dataModel.put("fileName", entityName+".java");
			dataModel.put("templateName", "generateEntity.ftl");
			return dataModel;
	}

	/**
	 * 生成可编辑list列表模板
	 * @param dataTable
	 * @param processFlag
	 * @return
	 */
	public Map<String, Object> generateEditableList(DataTable dataTable,boolean processFlag) {
		Map<String,Object> dataModel= new HashMap<String,Object>();
		String packageName = GenerateCodeUtils.getLastLayerPath(dataTable.getEntityName());
		String entityName = dataTable.getEntityName().substring(dataTable.getEntityName().lastIndexOf(".")+1,dataTable.getEntityName().length());
		String nameSpace = packageName.substring(packageName.lastIndexOf(".")+1,packageName.length());
		String lowCaseEntityName = GenerateCodeUtils.firstCharLowerCase(entityName);
		dataModel.put("processFlag", processFlag+"");
		dataModel.put("fileName", lowCaseEntityName+"-listEditable.jsp");
		dataModel.put("templateName", "generateEditableList.ftl");
		dataModel.put("entityName", entityName);//实体名
		dataModel.put("lowCaseEntityName", lowCaseEntityName);
		dataModel.put("ctx", "${ctx}");
		dataModel.put("resourcesCtx", "${resourcesCtx}");
		dataModel.put("nameSpace", nameSpace);
		dataModel.put("listCode", dataTable.getName());
		dataModel.put("filePath", "jsp/"+nameSpace);
		return dataModel;
	}

	public Map<String, Object> generateTask(DataTable dataTable) {
		Map<String,Object> dataModel= new HashMap<String,Object>();
		String packageName = GenerateCodeUtils.getLastLayerPath(dataTable.getEntityName());
		String entityName = dataTable.getEntityName().substring(dataTable.getEntityName().lastIndexOf(".")+1,dataTable.getEntityName().length());
		String nameSpace = packageName.substring(packageName.lastIndexOf(".")+1,packageName.length());
		String lowCaseEntityName = GenerateCodeUtils.firstCharLowerCase(entityName);
		dataModel.put("fileName", lowCaseEntityName+"-task.jsp");
		dataModel.put("templateName", "generateTask.ftl");
		dataModel.put("lowCaseEntityName",lowCaseEntityName );//实体名
		dataModel.put("ctx", "${ctx}");
		dataModel.put("fieldPermission", "${fieldPermission}");
		dataModel.put("taskId", "${taskId}");
		dataModel.put("companyId", "${"+lowCaseEntityName+".companyId}");
		dataModel.put("id", "${id}");
		dataModel.put("resourcesCtx", "${resourcesCtx}");
		dataModel.put("imatrixCtx", "${imatrixCtx}");
		dataModel.put("nameSpace", nameSpace);
		dataModel.put("formCode", dataTable.getName());
		dataModel.put("filePath", "jsp/"+nameSpace);
		dataModel.put("entity", "${"+lowCaseEntityName+"}");
		return dataModel;
	}
}
