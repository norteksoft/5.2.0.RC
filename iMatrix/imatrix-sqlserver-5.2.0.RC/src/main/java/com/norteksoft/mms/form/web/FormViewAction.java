package com.norteksoft.mms.form.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.bs.signature.entity.Signature;
import com.norteksoft.bs.signature.service.SignatureManager;
import com.norteksoft.mms.base.data.DataHandle;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.ControlType;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.service.DataTableManager;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.form.service.ImportFormViewManager;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.mms.form.service.TableColumnManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.ZipUtils;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.product.util.zip.ZipFile;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Namespace("/form")
@ParentPackage("default")
public class FormViewAction extends CrudActionSupport<FormView> {
	private static final long serialVersionUID = 1L;
	
	private static final String INFO_TYPE_SHOW="show";
	private static final String INFO_TYPE_SAVE="save";
	private static final String OCCASION_UPDATE="update";
	private static final String OCCASION_CHANGE_SOURCE="changeSource";
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	protected void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	private Long formId;
	private String code;//formView编号
	private Integer version;//formView版本
	private FormView formView;
	private Page<FormView> page = new Page<FormView>(0,true);
	
	private Long menuId;
	private Long dataTableId;
	private DataTable table;
	private List<DataTable> dataTables;
	private List<DataTable> defaultsTables;
	private FormViewManager formViewManager;
	private DataTableManager dataTableManager;
	private Long signId;
	@Autowired
	private TableColumnManager tableColumnManager;
	@Autowired
	private SignatureManager signatureManager;
	
	private List<Long> formViewIds;
	
	private String editorId;//编辑器id
	private Long tableColumnId;//数据表字段id
	private TableColumn tableColumn;
	private FormControl formControl;//表单控件
	private String occasion;//区别:改变数据来源或修改属性时
	private String[][] selectList;
	private String formHtml;
	private List<DataTable> dataTableList;
	private String formControlType;
	private String formControlId;
	private String referenceControlValue;
	private String formTypeId;//标准或自定义表单的Id
	private String soleCode;//验证编号的唯一
	private List<Long> formViewDeleteIds;//删除表单视图的Id
	private List<String[]> urgencyList=new ArrayList<String[]>();
	
	private List<String[]> properties; //数据选择控件/字段集合
	private List<String[]> dataSelectFields=new ArrayList<String[]>();//数据选择控件/表格信息
	
	private Page<Object> datas =new Page<Object>(0,true);
	
	private boolean existTable;
	
	private String states;
	
	private String infoType;//部门人员控件/显示信息和保存信息
	
	private String validateSetting;//校验设置
	
	private String treeType;//部门人员控件中的树的类型
	
	private String multiple;//部门人员控件中的是否是多选树
	
	private String resultId;//部门人员控件中的用于显示信息的组件id
	
	private String hiddenResultId;//部门人员控件中的隐藏域的name属性值
	
	private String inputType;//部门人员控件中的"输入框类型"(input/textArea)
	
	private MenuManager menuManager;
	
	private ListViewManager listViewManager;//
	private DataHandle dataHandle;
	
	private boolean standard;
	
	private Long listViewId;//“标准列表控件”中选中的列表
	private String listViewCode;//“标准列表控件”中选中的列表
	private List<ListView> listViews;//列表视图集合
	private File file;
	private String fileName;
	String dataBase;//数据库类型：oracle、mysql、sqlserver
	
	private String operation;//是更新版本还是增加版本。
	private String tableName;//数据表名称，数据选择、数据获取控件有用到
	
	private List<TableColumn> columns;
	private String htmlResult;//html结果
	@Autowired
	private ImportFormViewManager importFormViewManager;
	
	@Autowired
	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}
	@Autowired
	public void setFormViewManager(FormViewManager formViewManager) {
		this.formViewManager = formViewManager;
	}
	@Autowired
	public void setDataTableManager(DataTableManager dataTableManager) {
		this.dataTableManager = dataTableManager;
	}
	@Autowired
	public void setListViewManager(ListViewManager listViewManager) {
		this.listViewManager = listViewManager;
	}
	@Autowired
	public void setDataHandle(DataHandle dataHandle) {
		this.dataHandle = dataHandle;
	}
	
	@Override
	@Action("form-view-delete")
	public String delete() throws Exception {
		for(Long fvId:formViewDeleteIds){
			formViewManager.deleteFormView(fvId);
		}
		ApiFactory.getBussinessLogService().log("表单管理", 
				"删除表单", 
				ContextUtils.getSystemId("mms"));
		this.addSuccessMessage("删除成功");
		return list();
	}
	@Override
	public String input() throws Exception {
		//dataTables = dataTableManager.getEnabledDataTables();
		return INPUT;
	}
	@Override
	@Action("list-data")
	public String list() throws Exception {
		List<Menu> menus = menuManager.getEnabledRootMenuByCompany();
		if(menuId==null&&menus.size()>0){
			menuId = menus.get(0).getId();
		}
		if(menuId!=null){
			if(page.getPageSize()>1){
				formViewManager.getFormViewPageByMenu(page, menuId);
				ApiFactory.getBussinessLogService().log("表单管理", 
						"表单列表", 
						ContextUtils.getSystemId("mms"));
				this.renderText(PageUtils.pageToJson(page));
				return null;
			}
		}
		return "list-data";
	}
	@Override
	public String save() throws Exception {
		try {
			ApiFactory.getBussinessLogService().log("表单管理", 
					"保存表单", 
					ContextUtils.getSystemId("mms"));
			String result = formViewManager.validatHtml(htmlResult);
			if(result.equals("ok")){
				formViewManager.saveFormView(formView,menuId,operation,htmlResult);
				this.renderText("id:"+formView.getId().toString());
			}else{
				this.renderText("ms:"+result);
			}
		} catch (Exception e) {
			this.renderText("ms:"+e.getMessage());
		}
		return null;
	}
	public void prepareText() throws Exception {
//		formControl = new FormControl();
		if(StringUtils.isNotEmpty(code)&&version!=null){
			formView = formViewManager.getFormViewByCodeAndVersion(ContextUtils.getCompanyId(),code,version);
			standard=formView.getStandard();
			if(standard){
				table = formView.getDataTable();
				columns=tableColumnManager.getTableColumnByDataTableId(table.getId());
			}
		}
		if(formControl==null){
			formControl = new FormControl();
		}else{
			if(StringUtils.isNotEmpty(formControl.getName())&&standard){
				tableColumn =tableColumnManager.getTableColumnByColName(table.getId(), formControl.getName());
				if(tableColumn!=null)
				tableColumnId=tableColumn.getId();
			}
		}
	}
	
	private void setCommonValue(){
		tableColumn = dataTableManager.getTableColumn(tableColumnId);
		formControl.setControlId(tableColumn.getName());
		if(table!=null)formControl.setTableName(table.getName());
		formControl.setName(tableColumn.getName());
		formControl.setDbName(tableColumn.getDbColumnName());
		formControl.setTitle(dataTableManager.getInternation(tableColumn.getAlias()));
		if(tableColumn.getMaxLength()==null||tableColumn.getMaxLength().equals(0)){
			if(tableColumn.getDataType()==DataType.TEXT){
				formControl.setMaxLength(255);
			}else if(tableColumn.getDataType()==DataType.DOUBLE||tableColumn.getDataType()==DataType.FLOAT||tableColumn.getDataType()==DataType.AMOUNT){
				formControl.setMaxLength(25);
			}else if(tableColumn.getDataType()==DataType.INTEGER||tableColumn.getDataType()==DataType.NUMBER){
				formControl.setMaxLength(10);
			}else if(tableColumn.getDataType()==DataType.LONG){
				formControl.setMaxLength(19);
			}else if(tableColumn.getDataType()==DataType.BOOLEAN){
				formControl.setMaxLength(1);
			}else if(tableColumn.getDataType()==DataType.BLOB||tableColumn.getDataType()==DataType.CLOB||tableColumn.getDataType()==DataType.COLLECTION||tableColumn.getDataType()==DataType.ENUM||tableColumn.getDataType()==DataType.REFERENCE||tableColumn.getDataType()==DataType.TIME){
				formControl.setMaxLength(null);
			}
		}else{
			formControl.setMaxLength(tableColumn.getMaxLength());
		}
		formControl.setDataType(tableColumn.getDataType());
		if(StringUtils.isNotEmpty(tableColumn.getDefaultValue())){
			formControl.setControlValue(tableColumn.getDefaultValue());
		}
	}
	/**
	 * 转向单行文本的设置页面
	 * @return
	 * @throws Exception
	 */
	public String text() throws Exception {
		String result = "text";
		dataBase=PropUtils.getDataBase();
		//计算控件中的计算公式字段里含有符号+时从js中提交到后台为空，所以在js中把符号+替换成了符号@，在此在把@替换回+
		if(StringUtils.isNotEmpty(formControl.getComputational())&&formControl.getComputational().contains("@")){
			formControl.setComputational(formControl.getComputational().replaceAll("@", "+"));
		}
		
		switch (formControl.getControlType()) {
		case SELECT_MAN_DEPT:
//			if(StringUtils.isNotEmpty(formControl.getSaveDeptControlValue())){
//				tableColumn = dataTableManager.getTableColumn(formControl.getSaveDeptControlValue());
//				Long tableId=tableColumn.getDataTableId();
//				table = dataTableManager.getDataTable(tableId);
//				columns=tableColumnManager.getTableColumnByDataTableId(tableId);
//			}
			if(OCCASION_CHANGE_SOURCE.equals(occasion)){
				if(INFO_TYPE_SHOW.equals(infoType)){
					if(StringUtils.isNotEmpty(formControl.getShowDeptControlValue())){
						tableColumn =tableColumnManager.getTableColumnByColName(table.getId(), formControl.getShowDeptControlValue());
						if(tableColumn!=null)
						formControl.setShowDeptControlId(tableColumn.getName());
					}
				}else if(INFO_TYPE_SAVE.equals(infoType)){
					if(StringUtils.isNotEmpty(formControl.getSaveDeptControlValue())){
						tableColumn =tableColumnManager.getTableColumnByColName(table.getId(), formControl.getSaveDeptControlValue());
						if(tableColumn!=null)
						formControl.setSaveDeptControlId(tableColumn.getName());
					}
				}
			}
			result= "selectManOrDept";
			break;
		case CALCULATE_COMPONENT:
			if(OCCASION_CHANGE_SOURCE.equals(occasion)){
				if(tableColumnId!=null&&tableColumnId.intValue()!=0){
					setCommonValue();
				}
			}
			result= "calculateComponent";
			break;
		case PULLDOWNMENU:
			if(OCCASION_CHANGE_SOURCE.equals(occasion)){
				if(tableColumnId!=null&&tableColumnId.intValue()!=0){
					setCommonValue();
				}
			}
			String selectValues=formControl.getSelectValues();
			if(selectValues!=null){
				String[] vals=selectValues.split(",");
				selectList=new String[vals.length][2];
				for(int i=0;i<vals.length;i++){
					if(vals[i].contains(";")){
						selectList[i]=vals[i].split(";");
					}
				}
			}
			result= "pullDownMenu";
			break;
		case DATA_SELECTION:
			dataTableList=dataTableManager.getAllEnabledDataTables();
			if(formControl.getDataSrc()!=null){
				table = dataTableManager.getDataTableByTableName(formControl.getDataSrc());
				if(table!=null)
				columns=tableColumnManager.getTableColumnByDataTableId(table.getId());
			}
			if(formControl.getDataFields()!=null)dataSelectFields.add(formControl.getDataFields().split(","));
			if(formControl.getDataFieldNames()!=null)	dataSelectFields.add(formControl.getDataFieldNames().split(","));
			if(formControl.getDataControlIds()!=null)dataSelectFields.add(formControl.getDataControlIds().split(","));
			if(formControl.getDataQuerys()!=null){
				String[] querys=formControl.getDataQuerys().split(",");
				String[] myQuerys=new String[querys.length];
				for(int i=0;i<querys.length;i++){
					if(querys[i].equals("0")){
						myQuerys[i]="否";
					}else{
						myQuerys[i]="是";
					}
				}
				dataSelectFields.add(myQuerys);
			}
			result= "dataSelection";
			break;
		case DATA_ACQUISITION:
			dataTableList=dataTableManager.getAllEnabledDataTables();
			if(formControl.getDataSrc()!=null){
				table = dataTableManager.getDataTableByTableName(formControl.getDataSrc());
				if(table!=null)
				columns=tableColumnManager.getTableColumnByDataTableId(table.getId());
			}
			if(formControl.getDataFields()!=null)dataSelectFields.add(formControl.getDataFields().split(","));
			if(formControl.getDataFieldNames()!=null)	dataSelectFields.add(formControl.getDataFieldNames().split(","));
			if(formControl.getDataControlIds()!=null)dataSelectFields.add(formControl.getDataControlIds().split(","));
			result= "dataAcquisition";
			break;
		case URGENCY:
			if(formControl.getUrgencyValues()!=null)urgencyList.add(formControl.getUrgencyValues().split(","));
			if(formControl.getUrgencyDescribes()!=null)	urgencyList.add(formControl.getUrgencyDescribes().split(","));
			result= "urgency";
			break;
		case CREATE_SPECIAL_TASK:
			if(tableColumnId!=null&&tableColumnId.intValue()!=0){
				if(OCCASION_CHANGE_SOURCE.equals(occasion)){
					tableColumn = dataTableManager.getTableColumn(tableColumnId);
					formControl.setControlId(tableColumn.getName());
					formControl.setName(tableColumn.getName());
					formControl.setDbName(tableColumn.getDbColumnName());
					formControl.setTitle(dataTableManager.getInternation(tableColumn.getAlias()));
				}
			}
			result= "specialTask";
			break;
		case SPECIAL_TASK_TRANSACTOR:
			result= "specialTaskTransactor";
			break;
		case TEXTAREA:
			if(tableColumnId!=null&&tableColumnId.intValue()!=0){
				if(OCCASION_CHANGE_SOURCE.equals(occasion)){
					setCommonValue();
				}
			}
			result = "textArea";
			break;
		case TIME:
			if(tableColumnId!=null&&tableColumnId.intValue()!=0){
				if(OCCASION_CHANGE_SOURCE.equals(occasion)){
					setCommonValue();
				}
			}
			result = "time";
			break;
		case LIST_CONTROL:
//			lcTitles,lcSums,lcSizes,lcCals,dataFields
			dataTableList=dataTableManager.getAllEnabledDataTables();
			if(formControl.getDataSrc()!=null){
				table = dataTableManager.getDataTableByTableName(formControl.getDataSrc());
				if(table!=null)
				columns=tableColumnManager.getTableColumnByDataTableId(table.getId());
			}
			if(formControl.getDataFields()!=null){
				dataSelectFields.add(formControl.getDataFields().split(","));
			}
			if(formControl.getLcTitles()!=null){
				dataSelectFields.add(formControl.getLcTitles().split(","));
			}
			if(formControl.getLcSums()!=null){
				dataSelectFields.add(formControl.getLcSums().split(","));
			}
			if(formControl.getLcSizes()!=null){
				dataSelectFields.add(formControl.getLcSizes().split(","));
			}
			if(formControl.getLcCals()!=null){
				dataSelectFields.add(formControl.getLcCals().split(","));
			}
			result = "listControl";
			break;
		case STANDARD_LIST_CONTROL:
			if(formView!=null){
				listViews=listViewManager.getListViewsBySystem(formView.getMenuId());
			}
			if(OCCASION_CHANGE_SOURCE.equals(occasion)){
				if(tableColumnId!=null&&tableColumnId.intValue()!=0){
					tableColumn = dataTableManager.getTableColumn(tableColumnId);
					formControl.setControlId(tableColumn.getName());
					formControl.setName(tableColumn.getName());
					formControl.setDbName(tableColumn.getDbColumnName());
					formControl.setTitle(dataTableManager.getInternation(tableColumn.getAlias()));
					formControl.setDataType(tableColumn.getDataType());
				}
			}
			result = "standardListControl";
			break;
		case BUTTON:
			result = "button";
			break;
		case LABEL:
			result = "label";
			break;
		default:
			if(OCCASION_CHANGE_SOURCE.equals(occasion)){
				if(tableColumnId!=null&&tableColumnId.intValue()!=0){
					setCommonValue();
				}
			}
			break;
		}
		return result;
	}
	
	public String validateFormControl() throws Exception{
		return null;
	}
	
	/**
	 * 显示表单属性窗口
	 * @return
	 * @throws Exception
	 */
	public String getTabelColumns() throws Exception {
		StringBuffer str = new StringBuffer();
		if(ControlType.LIST_CONTROL.toString().equals(formControlType)){
			str.append("<option value=\"\">请选择字段</option>");
		}else if(ControlType.DATA_SELECTION.toString().equals(formControlType) ||ControlType.DATA_ACQUISITION.toString().equals(formControlType)){
			str.append("<option value=\"\">请选择字段</option>");
		}
		if(StringUtils.isNotEmpty(tableName)){
			table=dataTableManager.getDataTableByTableName(tableName);
//			table=dataTableManager.getDataTable(dataTableId);
			if(table!=null){
				List<TableColumn> columns=tableColumnManager.getTableColumnByDataTableId(table.getId());
				if(columns!=null && !columns.isEmpty()){
					for (TableColumn field : columns) {
						if("listControl".equals(formControlType)){
							str.append("<option value=\""+field.getDbColumnName()+":"+field.getDataType()+"\">"+dataTableManager.getInternation(field.getAlias())+"</option>");
						}else{
							if(field.getDbColumnName().contains("dt_")){
								str.append("<option value=\""+field.getDbColumnName()+"\">"+dataTableManager.getInternation(field.getAlias())+"</option>");
							}
						}
					}
				}
			}
		}
		this.renderText(str.toString());
		return null;
	}
	
	/**
	 * 数据选择控件/显示
	 * @return
	 * @throws Exception
	 */
	public String showDataSelection() throws Exception {
		if(version==0){
			version=1;
		}
		formView=formViewManager.getFormViewByCodeAndVersion(ContextUtils.getCompanyId(),code,version);
		properties=formViewManager.getDataProperties(formView.getHtml(), formControlId);
		Map<String, String[]> parameterMap=Struts2Utils.getRequest().getParameterMap();
		if(datas.getPageSize()>1){
			datas = formViewManager.getDataExcutionSql(datas,formView.getHtml(), formControlId,parameterMap,properties);
			StringBuilder json = new StringBuilder();
			json.append("{\"page\":\"");
			json.append(datas.getPageNo());
			json.append("\",\"total\":");
			json.append(datas.getTotalPages());
			json.append(",\"records\":\"");
			json.append(datas.getTotalCount());
			json.append("\",\"rows\":");
			json.append("[");
			for(Object obj:datas.getResult()){
				StringBuilder sb=new StringBuilder();
				sb.append("{");
				int i=0;
				Object val=null;
				for(String[] strs:properties){
					if(properties.size()==1){
						val=obj;
					}else{
						val=((Object[])obj)[i];
					}
					
					if(val==null){
						val="&nbsp;";
					}
					sb.append("\"")
					 .append(strs[0])
					 .append("\"")
					 .append(":")
					 .append("\"");
					 if(val!=null){
						 sb.append(val.toString().replaceAll("\"", "_@_#"));
					 }else{
						 sb.append(val);
					 }
					 
					sb.append("\"")
					 .append(",");
					i++;
				}
				//去掉最后一个逗号
				if(sb.charAt(sb.length()-1)==','){
					sb.delete(sb.length()-1, sb.length());
				}
				sb.append("}");
				json.append(sb);
				json.append(",");
			}
			//去掉最后一个逗号
			if(json.charAt(json.length()-1)==','){
				json.delete(json.length()-1, json.length());
			}
			json.append("]");
			json.append("}");
			this.renderText(PageUtils.disposeSpecialCharacter(json.toString()));
			return null;
		}
		StringBuilder colNames=new StringBuilder();//数据选择控件中列表jqgrid中的colNames
		StringBuilder colModel=new StringBuilder();//数据选择控件中列表jqgrid中的colModel
		colNames.append("[");
		colModel.append("[");
		for(String[] strs:properties){
			colModel.append("{name:'").append(strs[0]).append("',")
			.append("index:'").append(strs[0]).append("'}").append(",");
			colNames.append("'").append(strs[1]).append("'").append(",");
		}
		//去掉最后一个逗号
		if(colModel.charAt(colModel.length()-1)==','){
			colModel.append("{name:'act',index:'act',width:30,align:'center',formatter:function addAct(){return \"<a href='#' class='small-button-bg' onclick='addValue(this);'><span class='ui-icon ui-icon-plusthick'></span></a>\"}}");
		}
		//去掉最后一个逗号
		if(colNames.charAt(colNames.length()-1)==','){
			colNames.append("'操作'");
		}
		colModel.append("]");
		colNames.append("]");
		DataTable dataTable=formViewManager.getDataSource(formView.getHtml(), formControlId);
		if(StringUtils.isNotEmpty(dataTable.getEntityName())){
			existTable=true;
		}else{
			existTable=false;
		}
		String resourceCtx=PropUtils.getProp("host.resources");
		String ctx=PropUtils.getProp("host.app");
		Map<String, Object> root=new HashMap<String, Object>();
		root.put("colNames", colNames);
		root.put("colModel", colModel);
		root.put("resourcesCtx", resourceCtx);
		root.put("ctx", ctx);
		root.put("pageName", "datas");
		root.put("code", code);
		root.put("version", version);
		root.put("formControlId", formControlId);
		root.put("properties", properties);
		root.put("theme", ContextUtils.getTheme());
		if(parameterMap!=null){
			StringBuilder urlParam=new StringBuilder();
			String[] value = null;
			for(String[] strs:properties){
				value=parameterMap.get(strs[0]);
 				if(value!=null&&value.length>0&&StringUtils.isNotEmpty(value[0])){
					urlParam.append("&").append(strs[0]).append("=").append(value[0]);
				}
			}
 			root.put("urlParam", urlParam);
		}
		String html = TagUtil.getContent(root, "show-data-selection-tag.ftl");
		//将信息内容输出到JSP页面
		HttpServletResponse response = Struts2Utils.getResponse();
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		writer.print(html);
		return null;
	}
	
	
	/**
	 * 数据选择控件/显示
	 * @return
	 * @throws Exception
	 */
	public String getData() throws Exception {
		formView=formViewManager.getFormViewByCodeAndVersion(ContextUtils.getCompanyId(),code,version);
		this.renderText(formViewManager.getDataAcquisitionResult(formView.getHtml(), formControlId, referenceControlValue));
		return null;
	}
	
	/**
	 * （人员部门控件）显示树
	 * @return
	 * @throws Exception
	 */
	public String createTree() throws Exception {
		return "create-tree";
	}
	
	public void prepareChoiceColumn() throws Exception {
		table = dataTableManager.getDataTable(dataTableId);
	}
	/**
	 * 转向选择数据列的页面
	 * @return
	 * @throws Exception
	 */
	public String choiceColumn() throws Exception {
		return "choiceColumn";
	}
	/**
	 * 显示表单管理树
	 * @return
	 * @throws Exception
	 */
	public String formTree() throws Exception{
		List<Menu> menus = menuManager.getRootMenuByCompany();
		java.util.Collections.sort(menus);
		StringBuilder tree = new StringBuilder("[ ");
		for(Menu menu :menus){
				tree.append(JsTreeUtils.generateJsTreeNode(menu.getId().toString(), "root", menu.getName())).append(",");
		}
		JsTreeUtils.removeLastComma(tree);
		tree.append(" ]");
		renderText(tree.toString());
		return null;
	}
	public String childDefaultForm(){
		StringBuilder tree = new StringBuilder();
		List<DataTable> tables=dataTableManager.getDefaultDataTables();
		for(DataTable dt:tables){
			tree.append(JsTreeUtils.generateJsTreeNode(dt.getId().toString(), "leaf", dataTableManager.getInternation(dt.getAlias()))).append(",");	
		}
		return tree.toString();
	}
	@Override
	protected void prepareModel() throws Exception {
		if(formId==null){
			formView = new FormView();
		}else{
			formView = formViewManager.getFormView(formId);
		}
		if(dataTableId!=null){
			table = dataTableManager.getDataTable(dataTableId);
			formView.setDataTable(table);
		}
	}
	
	public String copy() throws Exception{
		formView = formViewManager.getFormView(formId);
		return "copy";
	}
	public void prepareSavecopy() throws Exception{
		formView = new FormView();
	}
	@Action("form-view-savecopy")
	public String savecopy() throws Exception{
		formViewManager.savecopy(formId, menuId, formView);
		return "list-data";
	}
	/**
	 * 改变表单的状态(草稿->启用;启用->禁用;禁用->启用)
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("form-view-state")
	public String changeFormState()throws Exception{
		String mssge = formViewManager.changeFormState(formViewIds, menuId);
		addSuccessMessage(mssge);
		return list();
	}
	
	public FormView getModel() {
		return formView;
	}
	
	public void prepareNext() throws Exception {
		this.prepareModel();
	}
	public String next() throws Exception{
		//dataTables = dataTableManager.getEnabledDataTables();
		if(formId!=null&&formId!=0){
			formView = formViewManager.getFormView(formId);
		}
		return "editor";
	}
	
	public void preparePreview() throws Exception {
		this.prepareModel();
	}
	public String preview() throws Exception {
		validateSetting = formViewManager.getValidateSetting(formView);
		if(formView!=null)formHtml=formViewManager.getFormHtml(formView,formView.getHtml());
		return "preview";
	}
	/**
	 * 验证编号的唯一
	 * @return
	 * @throws Exception
	 */
	public String validateFormCode() throws Exception {
		this.renderText(formViewManager.isFormCodeExist(soleCode,null).toString());
		return null;
	}
	
	/**
	 * 导出表单
	 * @return
	 * @throws Exception
	 */
	@Action("export-form-view")
	public String exportFormView() throws Exception{
		HttpServletResponse response = Struts2Utils.getResponse();
		response.reset();
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/x-download");
		response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode("export-form.zip","utf-8"));
		String name="form-view";
		String path="basic-data";
		File folder = new File(path);
		if(!folder.exists()){
			folder.mkdirs();
		}
		File file = new File(path+"/"+name+".xls");
		OutputStream out=new FileOutputStream(file);
		dataHandle.exportFormView(out,formViewIds,menuId);
		OutputStream fileOut=response.getOutputStream();
		ZipUtils.zipFolder(path, fileOut);
		if(fileOut!=null)fileOut.close();
		FileUtils.deleteDirectory(new File(path));//删除文件夹
		return null;
	}
	@Action("show-import-form-view")
	public String showImportDataTable() throws Exception{
		return "show-import-form-view";
	}
	/**
	 * 导入数据表及字段信息
	 * @return
	 * @throws Exception
	 */
	@Action("import-form-view")
	public String importDataTable() throws Exception{
		String result = "";
		if(fileName==null || !fileName.endsWith(".zip")){
			result="请选择zip文件格式";
		}
		boolean success = true;
		try {
			String importRootPath="basic-data-temp";
			ZipFile zipFile = new ZipFile(file);
			ZipUtils.unZipFileByOpache(zipFile, importRootPath); 
			importFormView(importRootPath);
			FileUtils.deleteDirectory(new File(importRootPath));
		} catch (Exception e) {
			success = false;
		}
		if(success){
			result="导入成功";
		}else{
			result="导入失败，请检查zip文件格式";
		}
		
		renderText(result);
		return null;
	}
	
	private void importFormView(String importRootPath) {
		File f=new File(importRootPath+"/form-view.xls");
		if(f.exists()){
			dataHandle.importFormView(f, ContextUtils.getCompanyId());
		}
		//读取表单内容
		File dir=new File(importRootPath+"/formview");
		if(dir.exists()){
			File[]files=dir.listFiles();
			for(int i=0;i<files.length;i++){
				File filei=files[i];
				String fileName=filei.getName().split("\\.")[0];
				String formCode=fileName.substring(0,fileName.lastIndexOf("#"));
				String formVersion=fileName.substring(fileName.lastIndexOf("#")+1);
				FormView formview=formViewManager.getCurrentFormViewByCodeAndVersion(formCode, Integer.parseInt(formVersion));
				try {
					String html=FileUtils.readFileToString(filei, "UTF-8");
					if(StringUtils.isNotEmpty(html.toString())){
						formview.setHtml(html.toString());
					}
					formViewManager.save(formview);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	/**
	 * 显示图片
	 * @return
	 * @throws Exception 
	 */
	@Action("form-view-showPic")
	public String showPic() throws Exception{
		if(signId!=null){
			Signature signature=signatureManager.getSignatureById(signId);
			String uploadPath = PropUtils.getProp("application.properties","upload.file.path");
			if(StringUtils.isEmpty(uploadPath)){
				uploadPath = PropUtils.getProp("applicationContent.properties","upload.file.path");
			}
			String path=uploadPath+"/"+"Signature"+signature.getPictureSrc();
			File file=new File(path);
			if(file.exists())PropUtils.showPic(file);
		}
		return null;
	}

	
	public Long getFormId() {
		return formId;
	}
	public void setFormId(Long formId) {
		this.formId = formId;
	}
	public Page<FormView> getPage() {
		return page;
	}
	public void setPage(Page<FormView> page) {
		this.page = page;
	}
	public List<DataTable> getDataTables() {
		return dataTables;
	}
	public void setDataTableId(Long dataTableId) {
		this.dataTableId = dataTableId;
	}
	public void setFormViewIds(List<Long> formViewIds) {
		this.formViewIds = formViewIds;
	}
	public Long getDataTableId() {
		return dataTableId;
	}
	public String getEditorId() {
		return editorId;
	}
	public void setEditorId(String editorId) {
		this.editorId = editorId;
	}
	public TableColumn getTableColumn() {
		return tableColumn;
	}
	public Long getTableColumnId() {
		return tableColumnId;
	}
	public void setTableColumnId(Long tableColumnId) {
		this.tableColumnId = tableColumnId;
	}
	public FormControl getFormControl() {
		return formControl;
	}
	public void setFormControl(FormControl formControl) {
		this.formControl = formControl;
	}
	public DataTable getTable() {
		return table;
	}
	public String getValidateSetting() {
		return validateSetting;
	}
	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}
	public void setOccasion(String occasion) {
		this.occasion = occasion;
	}
	public String[][] getSelectList() {
		return selectList;
	}
	public String getFormHtml() {
		return formHtml;
	}
	public List<DataTable> getDataTableList() {
		return dataTableList;
	}
	public void setFormControlType(String formControlType) {
		this.formControlType = formControlType;
	}
	public void setFormControlId(String formControlId) {
		this.formControlId = formControlId;
	}
	public String getFormControlId() {
		return formControlId;
	}
	public List<String[]> getProperties() {
		return properties;
	}
	
	public Page<Object> getDatas() {
		return datas;
	}
	public boolean isExistTable() {
		return existTable;
	}
	public List<String[]> getDataSelectFields() {
		return dataSelectFields;
	}
	public void setReferenceControlValue(String referenceControlValue) {
		this.referenceControlValue = referenceControlValue;
	}
	public List<String[]> getUrgencyList() {
		return urgencyList;
	}
	public String getTreeType() {
		return treeType;
	}
	public void setTreeType(String treeType) {
		this.treeType = treeType;
	}
	public String getMultiple() {
		return multiple;
	}
	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}
	public String getResultId() {
		return resultId;
	}
	public void setResultId(String resultId) {
		this.resultId = resultId;
	}
	public String getHiddenResultId() {
		return hiddenResultId;
	}
	public void setHiddenResultId(String hiddenResultId) {
		this.hiddenResultId = hiddenResultId;
	}
	public String getInputType() {
		return inputType;
	}
	public void setInputType(String inputType) {
		this.inputType = inputType;
	}
	public String getFormTypeId() {
		return formTypeId;
	}
	public void setFormTypeId(String formTypeId) {
		this.formTypeId = formTypeId;
	}
	public List<DataTable> getDefaultsTables() {
		return defaultsTables;
	}
	public void setDefaultsTables(List<DataTable> defaultsTables) {
		this.defaultsTables = defaultsTables;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	public FormView getFormView() {
		return formView;
	}
	public void setFormView(FormView formView) {
		this.formView = formView;
	}
	public String getStates() {
		return states;
	}
	public void setStates(String states) {
		this.states = states;
	}
	public boolean isStandard() {
		return standard;
	}
	public void setStandard(boolean standard) {
		this.standard = standard;
	}
	public String getSoleCode() {
		return soleCode;
	}
	public void setSoleCode(String soleCode) {
		this.soleCode = soleCode;
	}
	public List<Long> getFormViewDeleteIds() {
		return formViewDeleteIds;
	}
	public void setFormViewDeleteIds(List<Long> formViewDeleteIds) {
		this.formViewDeleteIds = formViewDeleteIds;
	}
	public Long getListViewId() {
		return listViewId;
	}
	public void setListViewId(Long listViewId) {
		this.listViewId = listViewId;
	}
	public List<ListView> getListViews() {
		return listViews;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public void setFileFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public List<TableColumn> getColumns() {
		return columns;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public void setHtmlResult(String htmlResult) {
		this.htmlResult = htmlResult;
	}
	
	public String getListViewCode() {
		return listViewCode;
	}
	public void setListViewCode(String listViewCode) {
		this.listViewCode = listViewCode;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Long getSignId() {
		return signId;
	}
	public void setSignId(Long signId) {
		this.signId = signId;
	}
	public String getDataBase() {
		return dataBase;
	}
	public void setDataBase(String dataBase) {
		this.dataBase = dataBase;
	}
	
}
