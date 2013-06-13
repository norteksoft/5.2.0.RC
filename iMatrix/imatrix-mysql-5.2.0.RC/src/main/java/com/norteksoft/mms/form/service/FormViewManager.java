package com.norteksoft.mms.form.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.htmlparser.jericho.FormField;
import net.htmlparser.jericho.FormFields;
import net.htmlparser.jericho.Source;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.norteksoft.mms.base.CommonStaticConstant;
import com.norteksoft.mms.form.dao.FormViewDao;
import com.norteksoft.mms.form.dao.GeneralDao;
import com.norteksoft.mms.form.dao.ListViewDao;
import com.norteksoft.mms.form.entity.AutomaticallyFilledField;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.jdbc.JdbcSupport;
import com.norteksoft.mms.module.dao.ButtonDao;
import com.norteksoft.mms.module.dao.MenuDao;
import com.norteksoft.mms.module.dao.ModulePageDao;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.entity.ModulePage;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.MemCachedUtils;
import com.norteksoft.product.util.PropUtils;

@Service
@Transactional(readOnly=true)
public class FormViewManager {
	private FormViewDao formViewDao;
	private ListViewDao listViewDao;
	private JdbcSupport jdbcDao;
	private GeneralDao generalDao;
	private Log log = LogFactory.getLog(FormViewManager.class);
	private MenuDao menuDao;
	private DataTableManager dataTableManager;
	private ListViewManager listViewManager;
	private TableColumnManager tableColumnManager;
	private MenuManager menuManager;
	@Autowired
	private ButtonDao buttonDao;
	@Autowired
	private ModulePageDao modulePageDao;
	@Autowired
	public void setTableColumnManager(TableColumnManager tableColumnManager) {
		this.tableColumnManager = tableColumnManager;
	}
	@Autowired
	public void setListViewManager(ListViewManager listViewManager) {
		this.listViewManager = listViewManager;
	}
	@Autowired
	public void setDataTableManager(DataTableManager dataTableManager) {
		this.dataTableManager = dataTableManager;
	}
	@Autowired
	public void setFormViewDao(FormViewDao formViewDao) {
		this.formViewDao = formViewDao;
	}
	@Autowired
	public void setJdbcDao(JdbcSupport jdbcDao) {
		this.jdbcDao = jdbcDao;
	}
	@Autowired
	public void setMenuDao(MenuDao menuDao) {
		this.menuDao = menuDao;
	}
	@Autowired
	public void setGeneralDao(GeneralDao generalDao) {
		this.generalDao = generalDao;
	}
	@Autowired
	public void setListViewDao(ListViewDao listViewDao) {
		this.listViewDao = listViewDao;
	}
	@Autowired
	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}
	/**
	 * 保存表单视图
	 * @param formView
	 */
	@Transactional(readOnly=false)
	public void save(FormView formView){
		formViewDao.save(formView);
	}
	
	private FormHtmlParser getFormHtmlParser(){
		return new FormHtmlParser();
	}
	/**
	 * 保存表单视图
	 * @param formView
	 */
	@Transactional(readOnly=false)
	public void saveFormView(FormView srcFormView, Long menuId,String operation,String htmlResult){
		if("addVersion".equals(operation)||srcFormView.getId()==null){
			FormView formView=null;
			if("addVersion".equals(operation)&&srcFormView.getId()!=null){//增加版本
				formView=new FormView();
				formView=srcFormView.clone();
				formView.setId(null);
			}else{//新建时
				formView=srcFormView;
			}
			formView.setHtml(htmlResult);
			if(formView.getFormState()==null||"".equals(formView.getFormState())){
				formView.setFormState(DataState.DRAFT);
			}else if(formView.getFormState().equals(DataState.DISABLE)){
				formView.setFormState(DataState.DRAFT);
			}
			formView.setMenuId(menuId);
			formView.setCompanyId(ContextUtils.getCompanyId());
			formView.setCreatedTime(new Date());
			formView.setCreator(ContextUtils.getLoginName());
			formView.setCreatorName(ContextUtils.getUserName());
			if(formView.getStandard()!=true){
				formView.setStandard(false);
			}
			formView.setVersion(getVersion(formView.getCode()));
			if("addVersion".equals(operation)&&srcFormView.getId()!=null){//增加版本时，判断是否增加字段
				if(!formView.getStandard()){//如果是自定义表单
					changeUpdateFormControl(formView, menuId);
				}
			}
			formViewDao.save(formView);
		}else {//更新当前版本
			if(srcFormView.getFormState()==null||"".equals(srcFormView.getFormState())){
				srcFormView.setFormState(DataState.DRAFT);
				if(srcFormView.getVersion()==0||srcFormView.getVersion()==null){
					srcFormView.setVersion(1);
				}
			}
			srcFormView.setHtml(htmlResult);
			if(!srcFormView.getStandard()){//如果是自定义表单
				changeUpdateFormControl(srcFormView, menuId);
			}else{
				//标准表单时，更新当前版本时更新签章字段
				if(PropUtils.getProp("project.model")==null||PropUtils.getProp("project.model").equals("product.model")){//默认是产品模式，或配置的是产品模式时
					MemCachedUtils.add(srcFormView.getCode()+"~"+srcFormView.getVersion()+"~"+srcFormView.getCompanyId(),getSignatureField(srcFormView));
				}
			}
			formViewDao.save(srcFormView);
		}
	}
	/**
	 * 获得该编号下视图的新版本号
	 * @param code
	 * @return 新的版本号
	 */
	protected int getVersion(String code){
		List<FormView> views = formViewDao.getViewsByCodeOrderByVersion(code);
		return views.size()==0?1:views.get(0).getVersion()+1;
	}
	/**
	 * 查询表单视图
	 * @param id 数据id
	 * @return 表单视图
	 */
	public FormView getFormView(Long id){
		if(id==null){throw new RuntimeException("查询表单时，表单id不能为null");}
		return formViewDao.get(id);
	}
	
	/**
	 * 删除表单视图
	 * @param id 表单视图id
	 */
	@Transactional(readOnly=false)
	public void deleteFormView(Long id){
		FormView view = formViewDao.get(id);
		if(DataState.DRAFT!=view.getFormState()){
			// 删除页面
			List<ModulePage> pages=modulePageDao.getModulePagesByViewId(id);
			for(ModulePage mp : pages){
				buttonDao.deleteButtonByModulePage(mp.getId());
				modulePageDao.delete(mp);
			}
		}
		formViewDao.delete(id);
	}
	
	public List<FormView> getFormViewByDataTable(Long dtId){
		return formViewDao.find("from FormView f where f.dataTable.id=?", dtId);
	}
	/**
	 * 查询某个数据表下的表单视图
	 * @param page 
	 * @param dataTable
	 */
	public void getFormViewPage(Page<FormView> page,DataTable dataTable){
		if(dataTable==null){
			return;
		}
		formViewDao.getFormViewPage(page,dataTable);
	}
	/**
	 * 获得某个数据表下的表单视图
	 * @param dataTableId 数据表id
	 */
	public void getFormViewPage(Page<FormView> page,Long dataTableId){
		if(dataTableId==null){
			return;
		}
		formViewDao.getFormViewPage(page,dataTableId);
	}
	/**
	 * 获得标准或自定义的表单视图
	 * @param existedTable 是否存在数据表
	 */
	public void getFormViewPageByMenu(Page<FormView> page,Long menuId){
		formViewDao.getFormViewPageByMenu(page,menuId);
	}
	/**
	 * 获得某个公司的表单视图
	 * 
	 */
	public List<FormView> getFormViewsByCompany(){
		return formViewDao.getFormViewsByCompany();
	}
	/**
	 * 获得某个公司的表单视图
	 * (flex有调用)
	 */
	public List<FormView> getFormViewsByCompany(Long companyId){
		log.debug("flex getFormViewsByCompany begin:");
		log.debug("param["+companyId+"]");
		List<FormView> list=formViewDao.getFormViewsByCompany(companyId);
		log.debug("result["+list+"]");
		log.debug("flex getFormViewsByCompany end:");
		return list;
	}
	/**
	 * 根据表单code和版本查询表单视图 
	 * @param code
	 * @param version
	 */
	public FormView getCurrentFormViewByCodeAndVersion(String code,Integer version){
		return formViewDao.getFormViewByCodeAndVersion(code,version);
	}
	
	/**
	 * 查询最新版本的启用的表单
	 * @param code
	 * @return
	 */
	public FormView getHighFormViewByCode(String code){
		return formViewDao.getHighViewByCode(code);
	}
	
	/**
	 * 根据表单code和版本查询表单视图 
	 * @param code
	 * @param version
	 */
	public FormView getFormViewByCodeAndVersion(Long companyId,String code,Integer version){
		return formViewDao.getFormViewByCodeAndVersion(code,version,companyId);
	}
	
	/**
	 * 根据表单code和版本查询控件列表（按控件在html代码中出现先后排序）
	 * (flex有调用)
	 */
	public List<FormControl> getControlsByCodeAndVersion(Long companyId,String code,Integer version) {
		FormView formView = this.getFormViewByCodeAndVersion(companyId,code, version);
		return getFormHtmlParser().getControls(formView.getHtml());
	}
	
	/**
	 * 根据表单code和版本查询控件列表（按控件在html代码中出现先后排序）
	 * (flex有调用)
	 */
	public List<FormControl> getControlsByCodeAndVersion(String code,Integer version) {
		FormView formView = this.getCurrentFormViewByCodeAndVersion(code, version);
		return getFormHtmlParser().getControls(formView.getHtml());
	}

	
	/**
	 * 获得该表单的控件列表（按控件在html代码中出现先后排序）
	 */
	public List<FormControl> getControls(FormView formView) {
		Assert.notNull(formView, "FormView实体不能为null");
		List<FormControl> list = getFormHtmlParser().getControls(formView.getHtml());
		return list;
	}
	/**
	 * 获得该表单的控件列表（按控件在html代码中出现先后排序）
	 */
	public List<FormControl> getControls(Long formViewId) {
		Assert.notNull(formViewId,"获得该表单的控件列表时,FormView表单id不能为null");
		return getControls(getFormView(formViewId));
	}
	/**
	 * 校验form表单中是否有重名的控件
	 * @param formHtml 表单html
	 */
	public String validatHtml(String formHtml){
		return getFormHtmlParser().validatHtml(formHtml);
	}
	/**
	 * 获得表单视图的校验设置 
	 * 返回格式如下：
	 * 	{"required_fields":[{id:"oneint"},{id:"onttext",type:"TEXT"},{id:"onedouble",type:"AMOUNT"}],
	 * 		"forbidden_fields":["ontnottext"]}';
	 * @param formView 表单视图
	 */
	public String getValidateSetting(FormView formView) {
		StringBuilder validateSetting = new StringBuilder("[");
		List<FormControl> controls = this.getControls(formView);
		for(FormControl control :controls){
			validateSetting.append("{");
			//<input id="column3" title="字段san" name="column3" datatype="TEXT" request="0" format="no" readolny="0" columnid="3" plugintype="TEXT" />
			validateSetting.append("request:\"").append(control.getRequest()).append("\",");
			validateSetting.append("readonly:\"").append(control.getReadOlny()).append("\",");
			validateSetting.append("controlType:\"").append(control.getControlType().getEnumName()).append("\",");
			validateSetting.append("format:\"").append(control.getFormat()).append("\",");
			validateSetting.append("datatype:\"").append(control.getDataType().getEnumName()).append("\",");
			validateSetting.append("title:\"").append(control.getTitle()).append("\",");
			validateSetting.append("name:\"").append(control.getName()).append("\",");
			validateSetting.append("id:\"").append(control.getControlId()).append("\",");
			validateSetting.append("formatType:\"").append(control.getFormatType()).append("\",");
			validateSetting.append("formatTip:\"").append(control.getFormatTip()).append("\"");
			validateSetting.append("},");
		}
		if(controls!=null && controls.size()>0){
			validateSetting.delete(validateSetting.length()-1, validateSetting.length());
		}
		validateSetting.append("]");
		return validateSetting.toString();
	}
	
	/**
	 * 根据表名和记录ID查询对应数据，返回Map
	 */
	@SuppressWarnings("unchecked")
	public Map getDataMap(String tableName, Long id) {
		Map map = jdbcDao.getDataMap(tableName, id);
		return map;
	}
	
	/**
	 * 根据请求中获得的参数map保存或修改表单内容数据 id为空时保存否则修改
	 * @param parameterMap  parameterMap中key为metaFormId的值时元表单的ID
	 * @throws ParseException 
	 */
	@Transactional(readOnly=false)
	public Long saveFormContentToTable(Map<String,String[]> parameterMap,Long formId,Long dataId) {
		Long id=saveParentFormContentToTable(parameterMap,formId,dataId);
		saveChildFormToTable(parameterMap,formId,id);
		return id;
	}
	
	@Transactional(readOnly=false)
	private Long saveParentFormContentToTable(Map<String,String[]> parameterMap,Long formId,Long dataId){
		Long id=null;
		FormView form =formViewDao.get(formId);
		List<FormControl> fields = this.getControls(formId);
		if(dataId!=null){
			id = jdbcDao.autoUpdateTable(parameterMap,form,fields,dataId);
		}else{
			id = jdbcDao.insertTable(parameterMap,form,fields);
		}
		return id;
	}
	/**
	 * 保存自定义表单中子表单的值
	 * @param parameterMap
	 * @param formId
	 */
	@Transactional(readOnly=false)
	private void saveChildFormToTable(Map<String,String[]> parameterMap,Long formId,Long parentRowId){
		FormView parentForm =formViewDao.get(formId);
		List<FormControl> parentFields = this.getControls(formId);
		List<Map<String,Object>> result=parseChildFormTable(parameterMap,parentForm.getHtml());
		for(Map<String,Object> map:result){
			FormView childForm=formViewDao.get(Long.parseLong((String)(map.get(CommonStaticConstant.DATA_SOURCE))));
			jdbcDao.insertChildTable(map,parentForm,parentFields,childForm,parentRowId);
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Map<String,Object>> parseChildFormTable(Map<String,String[]> parameterMap,String html){
		FormHtmlParser formHtmlParser=getFormHtmlParser();
		formHtmlParser.setFormHtml(html);
		List<Map<String,Object>> result=new ArrayList<Map<String,Object>>();
		Set<String> keys=parameterMap.keySet();
		Iterator<String> it=keys.iterator();
		List<String> listControlIds=formHtmlParser.getAllListControlIds();
		for(String id:listControlIds){
			Map<String,Object> map=new HashMap<String, Object>();
			Map fields=new HashMap();
			Map fieldValues=new HashMap();
			while(it.hasNext()){
				String key=it.next();
				if(key.startsWith("dataSrc_")){
					String source=null;
					if(key.substring(key.indexOf("_")+1,key.length()).equals(id)){
						source=parameterMap.get(key)[0];
						map.put(CommonStaticConstant.DATA_SOURCE, source);
					}
				}else if(key.startsWith("listControl_")){
					String currentId=key.substring(key.indexOf("_")+1,key.lastIndexOf("_"));
					if(currentId.equals(id)){
						String fieldName=key.substring(key.lastIndexOf("_")+1,key.indexOf(":"));
						String fieldType=key.substring(key.indexOf(":")+1,key.length());
						fields.put(fieldName, fieldType);
						fieldValues.put(fieldName, parameterMap.get(key));
						map.put(CommonStaticConstant.DATA_SOURCE_FIELD, fields);
						map.put(CommonStaticConstant.DATA_SOURCE_FIELD_VALUE, fieldValues);
					}
				}
			}
			if(map.size()>0){
				result.add(map);
			}
		}
		return result;
	}
	
	
	/**
	 * 给html加初始值
	 * @param form
	 * @return
	 */
	@Transactional(readOnly=false)
	public String initHtml(FormView form,List<AutomaticallyFilledField> filledField,String html){
		FormHtmlParser formHtmlParser=getFormHtmlParser();
		List<FormControl> controls = this.getControls(form.getId());
		formHtmlParser.setFormHtml(html);
		List<TableColumn> columns=tableColumnManager.getTableColumnByDataTableId(form.getDataTable().getId());
		return formHtmlParser.initHtml(controls,filledField,columns);
	}
	
	public String setDefaultVal(FormView form,String html){
		FormHtmlParser formHtmlParser=getFormHtmlParser();
		List<FormControl> controls = this.getControls(form.getId());
		formHtmlParser.setFormHtml(html);
		List<TableColumn> columns=tableColumnManager.getTableColumnByDataTableId(form.getDataTable().getId());
		return formHtmlParser.setDefaultVal(controls,columns);
	}
	
	@SuppressWarnings("unchecked")
	public String getFormHtml(FormView form,String  formHtml,Long dataId,boolean fieldRight,boolean signatureVisible){
		FormHtmlParser formHtmlParser=getFormHtmlParser();
		formHtmlParser.setFormHtml(formHtml);
		Map<String,List> queryMap=new HashMap<String, List>();
		//自定义列表控件
		Map<String,Map<String,String>> fields = formHtmlParser.getChildFormFields();
		Set keySet=fields.keySet();
		Iterator it=keySet.iterator();
		while(it.hasNext()){
			String source=(String)it.next();
			FormView childForm=formViewDao.get(Long.parseLong(source));
			Map<String,String> childFields=(Map<String,String>)fields.get(source);
			Set fieldSet=childFields.keySet();
			Iterator fieldIt=fieldSet.iterator();
			if(childFields.size()>0 && dataId!=null){
				List queryResult=new ArrayList();
				StringBuilder sql=new StringBuilder("select ");
				while(fieldIt.hasNext()){
					String field=(String)fieldIt.next();
					String dataType=childFields.get(field);
					if(dataType.equals(DataType.DATE.toString())) {
						sql.append("to_char(").append(JdbcSupport.FORM_FIELD_PREFIX_STRING).append(field).append(",").append("'yyyy-mm-dd') ").append(JdbcSupport.FORM_FIELD_PREFIX_STRING).append(field).append(",");
					}else if( dataType.equals(DataType.TIME.toString())){
						sql.append("to_char(").append(JdbcSupport.FORM_FIELD_PREFIX_STRING).append(field).append(",").append("'yyyy-mm-dd hh24:mi') ").append(JdbcSupport.FORM_FIELD_PREFIX_STRING).append(field).append(",");
					}else{
						sql.append("t.").append(JdbcSupport.FORM_FIELD_PREFIX_STRING).append(field).append(",");
					}
				}
				sql.replace(sql.length()-1, sql.length(), " ");
				sql.append("from ").append(childForm.getDataTable().getName()).append(" t")
				.append(" where ").append("t.").append("fk_").append(form.getDataTable().getName()).append("=").append(dataId)
				.append(" order by ").append("t.id");
				queryResult=jdbcDao.excutionSql(sql.toString());
				queryMap.put(source, queryResult);
			}
		}
		return formHtmlParser.getFormHtml(form,formHtml,queryMap,fieldRight,signatureVisible);
	}
	
	public String getFormHtml(FormView form,String  formHtml){
		FormHtmlParser formHtmlParser=getFormHtmlParser();
		formHtmlParser.setFormHtml(formHtml);
		return formHtmlParser.getFormHtml(form,formHtml);
	}
	 
	/**
	 * 根据公司ID，FORMI，DATAID，FIELDNAME得到字段值
	 * @param companyId
	 * @param formId
	 * @param dataId
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getFieldValue(Long formId,Long dataId,String fieldName){
		Object value = null;
		FormView form =getFormView(formId);
		if(form==null){log.debug("getFieldValue中，表单不能为null");throw new RuntimeException("getFieldValue中，表单不能为null");}
		if(form.isStandardForm()){
			//标准表单的处理
			//根据表单id获得对应的类
			if(form.getDataTable()==null){log.debug("getFieldValue中，表单对应的数据表不能为null");throw new RuntimeException("getFieldValue中，表单对应的数据表不能为null");}
			String className = form.getDataTable().getEntityName();
			//根据表名和id获得实体
			if(className==null){log.debug("getFieldValue中，表单对应的数据表的实体类名不能为null");throw new RuntimeException("getFieldValue中，表单对应的数据表的实体类名不能为null");}
			Object entity = generalDao.getObject(className, dataId);
			try {
				value = BeanUtils.getProperty(entity, fieldName);
				if(value==null) throw new RuntimeException("Field:"+fieldName+" no value.");
			} catch (Exception e) {
				throw new RuntimeException(e);
			} 
		}else if(!form.isStandardForm()){
			fieldName = new StringBuilder(JdbcSupport.FORM_FIELD_PREFIX_STRING).append(fieldName).toString().toUpperCase();
			Map map = this.getDataMap(form.getDataTable().getName(), dataId);
			value = map.get(fieldName);
			
		}
		return value==null?null:value.toString();
	}
	
	/**
	 * 数据选择控件/获取字段集合
	 * @return
	 */
	public List<String[]> getDataProperties(String html,String controlId){
		FormHtmlParser formHtmlParser=getFormHtmlParser();
		formHtmlParser.setFormHtml(html);
		DataTable dataTable=dataTableManager.getDataTableByTableName(formHtmlParser.getDataSource(controlId));
		return formHtmlParser.getDataProperties(controlId,dataTable);
	}
	
	/**
	 * 数据选择控件/查询所有数据
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List getDataExcutionSql(String html,String controlId){
		FormHtmlParser formHtmlParser=getFormHtmlParser();
		formHtmlParser.setFormHtml(html);
		DataTable dataTable=dataTableManager.getDataTableByTableName(formHtmlParser.getDataSource(controlId));
		String sql=formHtmlParser.getDataSqlCondition(controlId, dataTable);
		return jdbcDao.excutionSql(sql);
	}
	
	public Page<Object> getDataExcutionSql(Page<Object> page,String html,String controlId,Map<String, String[]> parameterMap,List<String[]> properties){
		FormHtmlParser formHtmlParser=getFormHtmlParser();
		formHtmlParser.setFormHtml(html);
		DataTable dataTable=dataTableManager.getDataTableByTableName(formHtmlParser.getDataSource(controlId));
		Object[] conditions=getConditionSql(dataTable,parameterMap);
		String conditionSql =(String)conditions[0];
		if(StringUtils.isNotEmpty(conditionSql.trim())){
			generalDao.findPageBySql(getExecutionSql(dataTable,conditionSql,properties),page, conditions[1]);
		}else{
			generalDao.findPageBySql(getExecutionSql(dataTable,conditionSql,properties),page);
		}
//		page = jdbcDao.excutionSql(page,result,conditionSql);
		return page;
	}
	
	private String getExecutionSql(DataTable dataTable,String conditionSql,List<String[]> properties){
		StringBuilder sql = new StringBuilder(" ");
		sql.append("select ");
		for(String[] strs:properties){
			sql.append(strs[0]).append(",");
		}
		if(sql.toString().contains(",")){
			sql.replace(sql.toString().length()-1, sql.toString().length(), "");
		}
		sql.append(" from ").append(dataTable.getName()).append(" ");
		if(StringUtils.isNotEmpty(conditionSql.trim())){
			sql.append("where ").append(conditionSql);
		}
		return sql.toString();
	}
	
	/**
	 * 获得查询条件
	 * @param form
	 * @param parameterMap
	 * @return
	 */
	private Object[] getConditionSql(DataTable dataTable,Map<String, String[]> parameterMap){
		List<TableColumn> tableColumns=tableColumnManager.getTableColumnByDataTableId(dataTable.getId());
		StringBuilder conditionSql = new StringBuilder(" ");
		String key;
		String[] value = null;
		Object[] objs=new Object[tableColumns.size()];
		
		Object[] result=new Object[2];
		try{
			for(int i=0;i<tableColumns.size()-1;i++){
				TableColumn field=tableColumns.get(i);
				if(StringUtils.isEmpty(dataTable.getEntityName())){
					key = JdbcSupport.FORM_FIELD_PREFIX_STRING+field.getName();
				}else{
					key = field.getDbColumnName();
				}
				value = parameterMap.get(key);
				
				if(value!=null&&value.length>0&&StringUtils.isNotEmpty(value[0])){
					if(!conditionSql.toString().equals(" ")) conditionSql.append(" and ");				
					if(field.getDataType().equals(DataType.TEXT) ||field.getDataType().equals(DataType. CLOB) ){
						objs[i]="%"+value[0]+"%";
						conditionSql.append(key).append(" like ").append("? ");
					}else{
						if(field.getDataType().equals(DataType.DATE)){
							SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
							objs[i]=df.parse(value[0]);
						}else if(field.getDataType().equals(DataType.TIME)){
							SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							objs[i]=df.parse(value[0]);
						}else if(field.getDataType().equals(DataType.DOUBLE)||field.getDataType().equals(DataType.AMOUNT)){
							objs[i]=Double.parseDouble(value[0]);
						}else if(field.getDataType().equals(DataType.FLOAT)){
							objs[i]=Float.parseFloat(value[0]);
						}else if(field.getDataType().equals(DataType.INTEGER)||field.getDataType().equals(DataType.NUMBER)){
							objs[i]=Integer.parseInt(value[0]);
						}else if(field.getDataType().equals(DataType.LONG)){
							objs[i]=Long.parseLong(value[0]);
						}else if(field.getDataType().equals(DataType.ENUM)){
							//TODO 数据表字段没有地方设枚举类名
//							ParseJsonUtil.getEnum(value[0], field.getClassName());
						}else if(field.getDataType().equals(DataType.BOOLEAN)){
							if(value[0].equals("1")){
								objs[i]=true;
							}else if(value[0].equals("0")){
								objs[i]=false;
							}
						}
						conditionSql.append(key).append("=?").append(" ");
					}
				}
			}
		}catch (Exception e) {
		}
		result[0]=conditionSql.toString();
		result[1]=objs;
		return result;
	}
	
	/**
	 * 数据选择控件/查询所有数据
	 * @return
	 */
	public DataTable getDataSource(String html,String controlId){
		FormHtmlParser formHtmlParser=getFormHtmlParser();
		formHtmlParser.setFormHtml(html);
		return dataTableManager.getDataTableByTableName(formHtmlParser.getDataSource(controlId));
	}
	
	/**
	 * 数据获取控件
	 * 查询指定数据结果，并将结果封装为json数据格式
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getDataAcquisitionResult(String html,String controlId,String referenceControlValue){
		FormHtmlParser formHtmlParser=getFormHtmlParser();
		formHtmlParser.setFormHtml(html);
		DataTable dataTable=dataTableManager.getDataTableByTableName(formHtmlParser.getDataSource(controlId));
		List<TableColumn> tableColumns=tableColumnManager.getTableColumnByDataTableId(dataTable.getId());
		//获得查询语句
		String result=formHtmlParser.getDataAcqSqlCondition(controlId, dataTable, referenceControlValue, tableColumns);
		String json = "";
		if(StringUtils.isNotEmpty(dataTable.getEntityName())){
			List list = generalDao.getObject(result, referenceControlValue);//referenceControlValue);
			if(list.size()>0){
				StringBuilder jsonHql=new StringBuilder("{");
				List<String[]> pros=formHtmlParser.getDataProperties(controlId,dataTable);
				Object obj=list.get(0);
				Object[] objs=null;
				if(obj instanceof Object[]){
					objs=(Object[])obj;
				}
				if(objs!=null){
					for(int i=0;i<objs.length;i++){
						jsonHql.append(pros.get(i)[0]).append(":").append('"').append(objs[i]==null?"":objs[i].toString()).append('"').append(',');
					}
				}else{
					jsonHql.append(pros.get(0)[0]).append(":").append('"').append(obj==null?"":obj.toString()).append('"').append(',');
				}
				if(jsonHql.indexOf(",")>=0){
					jsonHql=new StringBuilder(jsonHql.substring(0, jsonHql.lastIndexOf(",")));
				}
				jsonHql.append("}");
				json=jsonHql.toString();
			}
		}else{
			List list = jdbcDao.excutionSql(result);
			if(list.size()>0){
				json = JsonParser.object2Json(list.get(0));
			}
		}
		return json;
	}
	
	public ListView getListViewByFormId(Long formId){
		FormView formView=formViewDao.get(formId);
		return listViewDao.getDefaultDisplayListViewByTabelId(formView.getDataTable().getId());
	}
	//复制表单
	@Transactional(readOnly=false)
	public void savecopy(Long formId,Long menuId,FormView fv){
		FormView getFormView = formViewDao.get(formId);
		DataTable dt=null;
		if(getFormView.getDataTable()!=null){
			dt=dataTableManager.getDataTable(getFormView.getDataTable().getId());
		}
		fv.setId(null);
		fv.setMenuId(menuId);
		fv.setVersion(1);
		fv.setCompanyId(getFormView.getCompanyId());
		fv.setCreatedTime(new Date());
		fv.setCreator(ContextUtils.getLoginName());
		fv.setCreatorName(ContextUtils.getUserName());
		fv.setRemark(getFormView.getRemark());
		fv.setDataTable(dt);
		fv.setHtml(getFormView.getHtml());
		fv.setStandard(getFormView.getStandard());
		fv.setFormState(DataState.DRAFT);
		formViewDao.save(fv);
	}
	//改变form表单状态
	@Transactional(readOnly = false)
	public String changeFormState(List<Long> formIds, Long menuId){
		int draftToEn=0,enToDis=0,disToEn=0;
		StringBuilder sbu=new StringBuilder("");
		for(Long formId:formIds){
			FormView formView = getFormView(formId);
			if (formView.getFormState().equals(DataState.DRAFT)) {// 草稿->启用
				List<FormView> formVies = formViewDao.getViewsByCodeOrderByVersion(formView.getCode());
				Boolean sign = true;
				for(FormView formVie : formVies){
					if(DataState.DISABLE.equals(formVie.getFormState())||DataState.ENABLE.equals(formVie.getFormState())){
						sign = false;
					}
				}
				if(!formView.getStandard() && sign){//非标准表单   并且   其他版本都为草稿的生成数据表
					dataTableManager.createTable(formView);
				}
				log.debug("form state has change to " + DataState.ENABLE.toString());
				formView.setFormState(DataState.ENABLE);
				draftToEn++;
				log.debug("begin to create defaultView");
				if(!formView.getStandard()){
					createDefaultView(formView, menuId);
				}else{
					//标准表单时，启用表单时更新签章字段
					if(PropUtils.getProp("project.model")==null||PropUtils.getProp("project.model").equals("product.model")){//默认是产品模式，或配置的是产品模式时
						MemCachedUtils.add(formView.getCode()+"~"+formView.getVersion()+"~"+formView.getCompanyId(),getSignatureField(formView));
					}
				}
			} else if (formView.getFormState().equals(DataState.ENABLE)) {// 启用->禁用
				log.debug("form state has change to " + DataState.DISABLE.toString());
				formView.setFormState(DataState.DISABLE);
				enToDis++;
			} else if (formView.getFormState().equals(DataState.DISABLE)) {// 禁用->启用
				log.debug("form state has change to " + DataState.ENABLE.toString());
				formView.setFormState(DataState.ENABLE);
				disToEn++;
			}
			saveFormView(formView, menuId,null,formView.getHtml());
		}
		sbu.append(draftToEn).append("个草稿->启用,")
		.append(enToDis).append("个启用->禁用,")
		.append(disToEn).append("个禁用->启用");
		return sbu.toString();
	}

	/**
	 * 生成默认视图
	 * 
	 * @param formView
	 */
	@Transactional(readOnly = false)
	public void createDefaultView(FormView formView, Long menuId) {
		String name = formView.getName();
		String code = formView.getCode();
		String remark = formView.getRemark();
		DataTable dataTable = dataTableManager.getDataTableByTableName("mms_"+code);
		if(dataTable==null){
			//创建默认的数据表视图以及字段
			dataTable = createDefaultTableView(formView, menuId);
		}
		//创建默认的列表视图
		int versionForViewCode;
		if(formView.getVersion().intValue()==0){
			versionForViewCode = 1;
		}else{
			versionForViewCode = formView.getVersion();
		}
		ListView view = listViewManager.getListViewByCode(code+"_"+versionForViewCode);
		if(view==null){
			listViewManager.createDefaultListView(dataTable,code+"_"+versionForViewCode,name,remark,menuId, formView.getStandard());
		}
	}
	/**
	 * 更新表单控件
	 * 
	 * @param formView
	 */
	@Transactional(readOnly=false)
	public void changeUpdateFormControl(FormView formView,Long menuId){
		FormHtmlParser formHtmlParser=getFormHtmlParser();
		//更新控件和更新新增控件
		List<FormControl> controls=formHtmlParser.getControls(formView.getHtml());
		DataTable dataTable = formView.getDataTable();
		Menu menu = menuDao.get(menuId);
		if(dataTable!=null){
			List<TableColumn> tableColumns=tableColumnManager.getTableColumnByDataTableId(dataTable.getId());
			int i=0;
			for(FormControl control : controls){
				Boolean sign = true;
				for(TableColumn tableColumn : tableColumns){
					if(control.getName().equals(tableColumn.getName())){
						sign = false;
						++i;
						tableColumn.setName(control.getName());
						tableColumn.setAlias(control.getTitle());
						tableColumn.setDataType(control.getDataType());
						tableColumn.setDefaultValue(control.getControlValue());
						tableColumn.setMaxLength(control.getMaxLength());
						tableColumn.setDisplayOrder(i);
						tableColumn.setDataTableId(dataTable.getId());
						tableColumn.setCompanyId(menu.getCompanyId());
						tableColumn.setDeleted(false);
						tableColumnManager.saveColumn(tableColumn, true);
					}
				}
				if(sign){
					++i;
					TableColumn tableCo = new TableColumn();
					tableCo.setName(control.getName());
					tableCo.setAlias(control.getTitle());
					String tableListName = JdbcSupport.FORM_FIELD_PREFIX_STRING + control.getName();
					tableCo.setDbColumnName(tableListName);
					tableCo.setDataType(control.getDataType());
					tableCo.setDefaultValue(control.getControlValue());
					tableCo.setMaxLength(control.getMaxLength());
					tableCo.setDisplayOrder(i);
					tableCo.setDataTableId(dataTable.getId());
					tableCo.setCompanyId(menu.getCompanyId());
					tableCo.setDeleted(false);
					tableColumnManager.saveColumn(tableCo, true);
					tableColumns.add(tableCo);
					String tableName ="mms_"+formView.getCode();
					String columnName = control.getName();
					List<TableColumn> columns = tableColumnManager.getDeleteColumnByColumnName(columnName,dataTable.getId());
					if(formView.getFormState()!=DataState.DRAFT && columns.size()<1){
						jdbcDao.addDataBaseColumn(tableName, columnName, tableCo);
					}
				}
			}
			for(TableColumn tableColumn : tableColumns){
				if(!FormHtmlParser.isDefaultField(tableColumn.getName())){//是默认生成的字段不设置删除标识
					Boolean sign = true;
					for(FormControl control : controls){
						if(tableColumn.getName().equals(control.getName())||tableColumn.getDisplayOrder()==0){
							sign = false;
						}
					}
					if(sign){
						tableColumn.setDeleted(true);
					}
				}
			}
			dataTableManager.saveDataTable(dataTable);
			formView.setDataTable(dataTable);
		}
	}
	
	/**
	 * 验证表单编号的唯一
	 * 
	 * @param 
	 */
	public Boolean isFormCodeExist(String code, Long formId){
		String finalCode = code.toLowerCase().trim();
		List<FormView> formViews = formViewDao.getFormViewByCode(finalCode, formId);
		return formViews.size()>0?true:false;
	}
	/**
	 *  通过系统id获得表单视图
	 * @param 
	 */
	public List<FormView> getFormViewsBySystem(Long menuId){
		Menu menu = menuManager.getRootMenu(menuId);
		return formViewDao.getFormViewsByMenu(menu.getId());
	}
	/**
	 *  通过系统id获得表单视图
	 * @param 
	 */
	public List<FormView> getFormViewsByMenu(Long menuId){
		return formViewDao.getFormViewsByMenu(menuId);
	}
	
	public List<FormView> getUnCompanyFormViewsBySystem(Long menuId){
		Menu menu = menuManager.getRootMenu(menuId);
		return formViewDao.getUnCompanyFormViewsBySystem(menu.getId());
	}
	/**
	 *  创建默认的数据表视图
	 * @param 
	 */
	@Transactional(readOnly = false)
	public DataTable createDefaultTableView(FormView formView, Long menuId){
		String name = formView.getName();
		String code = formView.getCode();
		Menu menu = menuDao.get(menuId);
		//创建默认的数据表视图
		DataTable dataTable = new DataTable();
		dataTable.setName("mms_"+code);
		dataTable.setAlias(name);
		dataTable.setTableState(DataState.ENABLE);
		dataTable.setMenuId(menuId);
		dataTableManager.saveDataTable(dataTable);
		//创建默认数据表视图的字段
		FormHtmlParser formHtmlParser=getFormHtmlParser();
		List<FormControl> controls=formHtmlParser.getControls(formView.getHtml());
		int j=0;
		TableColumn tableColumn;
		List<TableColumn> columns = new ArrayList<TableColumn>();
		for(FormControl control : controls){
			++j;
			tableColumn = new TableColumn();
			tableColumn.setName(control.getName());
//			if(columns.contains(tableColumn)){//如果包含该数据表字段则不再保存。考虑到复选框和单选框问题
//				continue;
//			}
			tableColumn.setAlias(control.getTitle());
			String tableListName = JdbcSupport.FORM_FIELD_PREFIX_STRING + control.getName();
			tableColumn.setDbColumnName(tableListName);
			tableColumn.setDataType(control.getDataType());
			tableColumn.setDefaultValue(control.getControlValue());
			tableColumn.setMaxLength(control.getMaxLength());
			tableColumn.setDisplayOrder(j);
			tableColumn.setCompanyId(menu.getCompanyId());
			tableColumn.setDeleted(false);
			tableColumn.setDataTableId(dataTable.getId());
			tableColumnManager.saveColumn(tableColumn, false);
			columns.add(tableColumn);
		}
		//id
		saveDefaultTableColumn("id",DataType.LONG,dataTable.getId(),menu.getCompanyId(),"实体id",++j);
		//生成默认instance_id
		saveDefaultTableColumn("instance_id",DataType.TEXT,dataTable.getId(),menu.getCompanyId(),"实例id",++j);
		//生成默认first_task_id
		saveDefaultTableColumn("first_task_id",DataType.LONG,dataTable.getId(),menu.getCompanyId(),"第一环节任务id",++j);
		//生成默认form_id
		saveDefaultTableColumn("form_id",DataType.LONG,dataTable.getId(),menu.getCompanyId(),"表单id",++j);
		//生成默认create_date
		saveDefaultTableColumn("create_date",DataType.DATE,dataTable.getId(),menu.getCompanyId(),"创建时间",++j);
		//生成默认creator_department
		saveDefaultTableColumn("creator_department",DataType.TEXT,dataTable.getId(),menu.getCompanyId(),"创建人部门",++j);
		formView.setDataTable(dataTable);
		return dataTable;
	}
	
	private void saveDefaultTableColumn(String name,DataType dataType,Long dataTableId,Long companyId,String alias,Integer displayOrder){
		TableColumn tableColumn = new TableColumn();
		tableColumn.setName(name);
		tableColumn.setDataTableId(dataTableId);
		tableColumn.setDataType(dataType);
		tableColumn.setAlias(alias);
		tableColumn.setCompanyId(companyId);
		tableColumn.setDeleted(false);
		tableColumn.setDisplayOrder(displayOrder);
		tableColumn.setDbColumnName(name);
		tableColumnManager.saveColumn(tableColumn, false);
	}
	/**
	 * 给form表单赋值:主表单和子表同时赋
	 * @param form
	 * @param html
	 * @param dataId
	 * @param fieldRight
	 * @return 返回赋了值之后的html
	 */
	@SuppressWarnings("unchecked")
	public String getHtml(FormView form,String html,Long dataId,boolean fieldRight,boolean signatureVisible){
		FormHtmlParser formHtmlParser=getFormHtmlParser();
		formHtmlParser.setFormHtml(html);
		if(dataId!=null){
			Map map=jdbcDao.getDataMap(form.getDataTable().getName(), dataId);
			//主表单赋值
			html = formHtmlParser.setFieldValue(map);
		}
		//子表赋值
		html=getFormHtml(form, html, dataId, fieldRight,signatureVisible);
		return html;
	}
	
	/**
	 * 给form表单赋值:主表单和子表同时赋
	 * @param form
	 * @param html
	 * @param dataId
	 * @param fieldRight
	 * @param signatureVisible 
	 * @return 返回赋了值之后的html
	 */
	@SuppressWarnings("unchecked")
	public String getHtml(FormView form,String html,Long dataId,boolean fieldRight,Object entity,Collection collection, boolean signatureVisible){
		FormHtmlParser formHtmlParser=getFormHtmlParser();
		formHtmlParser.setFormHtml(html);
		boolean hasFill=false;
		//处理自定义表单中填写前自动填写字段
		if(entity!=null){
			if(!form.getStandard()){
				if(entity instanceof Map){
					html = formHtmlParser.setFieldValue((Map) entity);
					hasFill=true;
				}
			}
		}
		
		if(!hasFill){
			if(dataId!=null){
				//主表单赋值
				if(form.getStandard()){
					html = formHtmlParser.setStandardFieldValue(entity);
				}else{
					Map map=jdbcDao.getDataMap(form.getDataTable().getName(), dataId);
					html = formHtmlParser.setFieldValue(map);
				}
			}
		}
		//子表赋值
		if(form.getStandard()){
			html=formHtmlParser.getStandardFormHtml(form, html,entity,collection,signatureVisible);
		}else{
			html=getFormHtml(form, html, dataId, fieldRight,signatureVisible);
		}
		String validateSetting = getValidateSetting(form);
		html+="<script>$().ready(function(){addFormValidate('"+validateSetting+"');});</script>";
		
		return html;
	}
	
	public FormView getUnCompanyFormViewByCodeAndVersion(String code, Integer version) {
		return formViewDao.getUnCompanyFormViewByCodeAndVersion(code, version);
	}
	
	/**
	 * 将所有标准表单的签章字段存入缓存
	 */
	public void getAllSignatureFields(){
		if(PropUtils.getProp("project.model")==null||PropUtils.getProp("project.model").equals("product.model")){//默认是产品模式，或配置的是产品模式时
			int i=0;
			List<FormView> views=formViewDao.getAllStandardFormView();
			for (FormView formView : views) {
				System.out.println(i+"=正在加载表单【"+formView.getName()+":"+formView.getVersion()+":"+formView.getCompanyId()+"】......");
				MemCachedUtils.add(formView.getCode()+"~"+formView.getVersion()+"~"+formView.getCompanyId(),getSignatureField(formView));
				i++;
			}
		}
	}
	
	/**
	 * 根据表单编号和版本号取得此表单中签章id的list
	 * @param code
	 * @param version
	 * @return
	 */
	public List<String> getSignatureField(FormView view){
		List<String> signatureFields = new ArrayList<String>();
		if(view!=null){
			Source source = new Source(view.getHtml());
			FormFields formFields=source.getFormFields();
			Iterator<FormField> it = formFields.iterator();
			FormField formField = null;
			while(it.hasNext()){
				formField = it.next(); 
				if("TEXT".equals(formField.getFormControl().getElement().getAttributeValue("pluginType"))){
					if("true".equals(formField.getFormControl().getElement().getAttributeValue("signaturevisible"))){//签章
						signatureFields.add(formField.getFormControl().getElement().getAttributeValue("id")) ;
					}
				}
			}
		}
		return signatureFields;
	}
	
	
	/**
	 * 给form的打印表单赋值
	 * @param form
	 * @param html
	 * @param dataId
	 * @param fieldRight
	 * @return 返回赋了值之后的html
	 */
	@SuppressWarnings("unchecked")
	public String getPrintHtml(FormView form,String html,Long dataId,boolean fieldRight,Object entity){
		FormHtmlParser formHtmlParser=getFormHtmlParser();
		formHtmlParser.setFormHtml(html);
		boolean hasFill=false;
		//处理自定义表单中填写前自动填写字段
		if(entity!=null){
			if(!form.getStandard()){
				if(entity instanceof Map){
					html = formHtmlParser.setFieldValue((Map) entity);
					hasFill=true;
				}
			}
		}
		
		if(!hasFill){
			if(dataId!=null){
				//主表单赋值
				if(form.getStandard()){
					html = formHtmlParser.setStandardFieldValue(entity);
				}else{
					Map map=jdbcDao.getDataMap(form.getDataTable().getName(), dataId);
					html = formHtmlParser.setFieldValue(map);
				}
			}
		}
		
		html=formHtmlParser.getPrintFormHtml(form, html, dataId, fieldRight);
			//添加脚本隐藏控件
			html+="<script type='text/javascript'>formPrintHideControl();</script>";
		return html;
	}
}
