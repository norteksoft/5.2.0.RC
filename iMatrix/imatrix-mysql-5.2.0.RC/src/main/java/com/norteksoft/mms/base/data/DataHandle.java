package com.norteksoft.mms.base.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.FunctionGroup;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleFunction;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.entity.sale.PricePolicy;
import com.norteksoft.acs.entity.sale.Product;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.FunctionGroupManager;
import com.norteksoft.acs.service.authorization.FunctionManager;
import com.norteksoft.acs.service.authorization.StandardRoleManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.acs.service.organization.WorkGroupManager;
import com.norteksoft.acs.service.sale.PricePolicyManager;
import com.norteksoft.acs.service.sale.ProductManager;
import com.norteksoft.bs.options.entity.Internation;
import com.norteksoft.bs.options.entity.InternationOption;
import com.norteksoft.bs.options.entity.Option;
import com.norteksoft.bs.options.entity.OptionGroup;
import com.norteksoft.bs.options.entity.TimedTask;
import com.norteksoft.bs.options.entity.Timer;
import com.norteksoft.bs.options.enumeration.TimingType;
import com.norteksoft.bs.options.service.InternationManager;
import com.norteksoft.bs.options.service.JobInfoManager;
import com.norteksoft.bs.options.service.OptionGroupManager;
import com.norteksoft.bs.rank.entity.Subordinate;
import com.norteksoft.bs.rank.entity.Superior;
import com.norteksoft.bs.rank.enumeration.SubordinateType;
import com.norteksoft.bs.rank.service.RankManager;
import com.norteksoft.bs.rank.service.RankUserManager;
import com.norteksoft.mms.form.dao.JqGridPropertyDao;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.GenerateSetting;
import com.norteksoft.mms.form.entity.GroupHeader;
import com.norteksoft.mms.form.entity.JqGridProperty;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.service.DataTableManager;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.form.service.GroupHeaderManager;
import com.norteksoft.mms.form.service.ListColumnManager;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.mms.form.service.TableColumnManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.entity.Operation;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.mms.module.service.OperationManager;
import com.norteksoft.portal.dao.WebpageDao;
import com.norteksoft.portal.dao.WidgetDao;
import com.norteksoft.portal.dao.WidgetParameterDao;
import com.norteksoft.portal.dao.WidgetRoleDao;
import com.norteksoft.portal.entity.Webpage;
import com.norteksoft.portal.entity.Widget;
import com.norteksoft.portal.entity.WidgetParameter;
import com.norteksoft.portal.entity.WidgetRole;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.util.ZipUtils;
import com.norteksoft.product.util.zip.ZipFile;
import com.norteksoft.wf.base.enumeration.DataDictUserType;
import com.norteksoft.wf.engine.entity.DataDictionary;
import com.norteksoft.wf.engine.entity.DataDictionaryProcess;
import com.norteksoft.wf.engine.entity.DataDictionaryType;
import com.norteksoft.wf.engine.entity.DataDictionaryUser;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.entity.WorkflowDefinitionFile;
import com.norteksoft.wf.engine.entity.WorkflowType;
import com.norteksoft.wf.engine.service.DataDictionaryManager;
import com.norteksoft.wf.engine.service.DataDictionaryTypeManager;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;
import com.norteksoft.wf.engine.service.WorkflowTypeManager;
/**
 * mms中数据的导入导出
 * @author liudongxia
 *
 */
@Service
@Transactional
public class DataHandle{
	private Log log = LogFactory.getLog(DataHandle.class);
	private static final String COLUMN_FIELD_NAEM="fieldName";
	private static final String COLUMN_TITLE="title";
	private static final String COLUMN_DATA_TYPE="dataType";
	private static final String COLUMN_ENUM_NAME="enumName";
	private static final String COLUMN_DEFAULT_VALUE="defaultValue";//新增一列时，默认值设置
	private static final String COLUMN_IGNORE="ignore";//导入导出均不读该列
	private static final String IDENTIFIER="identifier";//标识数据是否存在
	
	private static final String FILE_DATA="data";
	private static final String FILE_EXPORT_PATH="exportPath";
	private static final String FILE_IMPORT_ORDER="importOrder";
	private static final String FILE_IMPORT_PATH="importPath";
	private static final String FILE_CATEGORY="category";
	private static final String FILE_BEANNAME="beanname";
	private static final String FILE_NAME="filename";
	private static final String FILE_NAME_STARTWITH="filenameStartwith";
	private static final String FILE_TITLE="title";
	//mms
	private static final String MENU_CODE="menuCode";//菜单编码
	private static final String TABLE_NAME="tableName";//数据表名称
	private static final String LIST_VIEW_CODE="listViewCode";//列表编码
	private static final String TABLE_COLUMN_NAME="tableColumnName";//数据表字段
	private static final String JQ_GRID_PROPERTYS="jqGridPropertys";//属性自由扩展列表
	//acs
	private static final String SYSTEM_CODE="systemCode";//系统编码
	private static final String FUNCTION_GROUP_CODE="functionGroupCode";//资源组编码
	private static final String PARENT_ROLE_CODE="parentRoleCode";//父角色编码
	private static final String FUNCTION_PATH="functionPath";//资源路径
	private static final String ROLE_CODE="roleCode";//角色编码
	
	private static final String FORM_VIEW_PATH="formview";//表单的html片段
	private static final String FLOW_CHAR_PATH="flowChar";//流程图xml格式文件
	@Autowired
	private MenuManager menuManager;
	@Autowired
	private DataTableManager dataTableManager;
	@Autowired
	private TableColumnManager tableColumnManager;
	@Autowired
	private ListColumnManager listColumnManager;
	@Autowired
	private ListViewManager listViewManager;
	@Autowired
	private JqGridPropertyDao jqGridPropertyDao;
	@Autowired
	private FormViewManager formViewManager;
	@Autowired
	private BusinessSystemManager businessSystemManager;
	@Autowired
	private FunctionManager functionManager;
	@Autowired
	private FunctionGroupManager functionGroupManager;
	@Autowired
	private StandardRoleManager standardRoleManager;
	@Autowired
	private WidgetDao widgetDao;
	@Autowired
	private WebpageDao webpageDao;
	@Autowired
	private WidgetRoleDao widgetRoleDao;
	@Autowired
	private CompanyManager companyManager;
	@Autowired
	private ProductManager productManager;
	@Autowired
	private PricePolicyManager pricePolicyManager;
	@Autowired
	private WidgetParameterDao widgetParameterDao;
	@Autowired
	private WorkflowTypeManager workflowTypeManager;
	@Autowired
	private RankManager rankManager;
	@Autowired
	private UserManager userManager;
	@Autowired
	private RankUserManager rankUserManager;
	@Autowired
	private DepartmentManager departmentManager;
	@Autowired
	private WorkGroupManager workGroupManager;
	@Autowired
	private OptionGroupManager optionGroupManager;
	@Autowired
	private WorkflowDefinitionManager workflowDefinitionManager;
	@Autowired
	private DataDictionaryManager dataDictionaryManager;
	@Autowired
	private DataDictionaryTypeManager dataDictionaryTypeManager;
	@Autowired
	private AcsUtils acsUtils;
	@Autowired
	private JobInfoManager jobInfoManager;
	@Autowired
	private GroupHeaderManager groupHeaderManager;
	@Autowired
	private InternationManager internationManager;
	@Autowired
	private OperationManager operationManager;
	public void getFileHead(HSSFWorkbook wb,HSSFRow row,List<DataSheetConfig> confs){
		HSSFFont boldFont = wb.createFont();
        boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		HSSFCellStyle boldStyle = wb.createCellStyle();
        boldStyle.setFont(boldFont);
		for(int i=0;i<confs.size();i++){
			DataSheetConfig conf=confs.get(i);
			if(!conf.isIgnore()){
				HSSFCell cell0 = row.createCell(i);
				cell0.setCellValue(confs.get(i).getTitle());
				cell0.setCellStyle(boldStyle);
			}
		}
	}
	@SuppressWarnings("unchecked")
	public List<DataSheetConfig> getConfigInfo(String nodes){
       List<DataSheetConfig> confs=new ArrayList<DataSheetConfig>();
       InputStreamReader isreader=null;
       try {
	    	SAXReader reader = new SAXReader();
	    	isreader=new InputStreamReader(DataHandle.class.getClassLoader().getResourceAsStream("dataSheetConfig.xml"),"UTF-8");
	    	Document document=reader.read(isreader);
			List<Element> tableList = document.selectNodes(nodes);
			Iterator it = tableList.iterator();
			while(it.hasNext()){//只会循环一次
				Element menuEle = (Element)it.next();
				List<Element> columnList = menuEle.selectNodes("column");
				Iterator itc = columnList.iterator();
				while(itc.hasNext()){
					Element column = (Element)itc.next();
					//得到column的属性
					   List<Attribute> columnAttributes = column.attributes();
					   DataSheetConfig conf=new DataSheetConfig();
					   for(int i=0;i<columnAttributes.size();i++){
						    String attributeName = columnAttributes.get(i).getName();
						    if(COLUMN_FIELD_NAEM.equals(attributeName)){
						    	String fieldname = columnAttributes.get(i).getValue();
						    	conf.setFieldName(fieldname);
						    }else if(COLUMN_TITLE.equals(attributeName)){
						    	String title = columnAttributes.get(i).getValue();
						    	conf.setTitle(title);
						    }else if(COLUMN_ENUM_NAME.equals(attributeName)){
						    	String enumname = columnAttributes.get(i).getValue();
						    	conf.setEnumName(enumname);
						    }else if(COLUMN_DATA_TYPE.equals(attributeName)){
						    	String dataType = columnAttributes.get(i).getValue();
						    	conf.setDataType(dataType);
						    }else if(COLUMN_DEFAULT_VALUE.equals(attributeName)){
						    	String defaultValue = columnAttributes.get(i).getValue();
						    	conf.setDefaultValue(defaultValue);
						    }else if(COLUMN_IGNORE.equals(attributeName)){
						    	String ignore = columnAttributes.get(i).getValue();
						    	conf.setIgnore("true".equals(StringUtils.lowerCase(ignore))?true:false);
						    }else if(IDENTIFIER.equals(attributeName)){
						    	String identifier=columnAttributes.get(i).getValue();
						    	conf.setIdentifier("true".equals(StringUtils.lowerCase(identifier))?true:false);
						    }
					   }
					   if(!conf.isIgnore()){
						   confs.add(conf);
					   }
				}
			}
	    }catch (Exception e) {
	    	log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			try {
				if(isreader!=null)isreader.close();
			} catch (IOException e) {
				log.debug(PropUtils.getExceptionInfo(e));
			}
		}
	    return confs;
	}
	/**
	 * 获得所有标识列
	 * @param confs
	 * @return Map<String,Integer>,key:字段名,value:第几列
	 */
	public Map<String,Integer> getIdentifier(List<DataSheetConfig> confs){
		Map<String,Integer> map=new HashMap<String,Integer>();
		for(int i=0;i<confs.size();i++){
			DataSheetConfig conf=confs.get(i);
			if(!conf.isIgnore()&&conf.isIdentifier()){
				map.put(conf.getFieldName(), i);
			}
		}
		return map;
	}
	/**
	 * 根据数据表管理中录入的日期格式格式化日期
	 * @param dataType
	 * @param attributeValue
	 * @return
	 */
	private String formatValue(String dataType,Object attributeValue){
		try {
			if(DataType.DATE.toString().equals(dataType)){
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				attributeValue = simpleDateFormat.format(attributeValue);
			 }else if(DataType.TIME.toString().equals(dataType)){
				 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				 attributeValue = simpleDateFormat.format(attributeValue);
			 }
		} catch (Exception e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}
		if(attributeValue!=null)return attributeValue.toString();
		else return "";
	}
	
	private Date getDate(String dataType,String value){
		try {
			if(DataType.DATE.toString().equals(dataType)){
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				return simpleDateFormat.parse(value);
			 }else if(DataType.TIME.toString().equals(dataType)){
				 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				 return simpleDateFormat.parse(value);
			 }
		}catch (Exception e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}
		return null;
	}
	
	public void setValue(Object entity,String fieldName,String dataType,String value,String enumname){
		try{
			if(DataType.DATE.toString().equals(dataType)||DataType.TIME.toString().equals(dataType)){
				BeanUtils.copyProperty(entity, fieldName,getDate(dataType,value));
			}else if(DataType.ENUM.toString().equals(dataType)){
				BeanUtils.copyProperty(entity, fieldName, JsonParser.getEnum(value, enumname));
			}else if(DataType.BOOLEAN.toString().equals(dataType)){
				String val=value;
				if(StringUtils.isEmpty(val)){
					val="false";
				}
				BeanUtils.copyProperty(entity, fieldName, val);
			}else{
				BeanUtils.copyProperty(entity, fieldName, value);
			}
	    }catch (Exception e) {
	    	log.debug(PropUtils.getExceptionInfo(e));
		}
		
	}
	
	public void setFieldValue(DataSheetConfig conf,int i,HSSFRow rowi,Object obj){
		if(obj!=null){
			HSSFCell cell = rowi.createCell(i);
			String fieldName=conf.getFieldName();
			String atrtName = "get" + fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1, fieldName.length());
			Method m;
			Object value=null;
			try {
				if(DataType.BOOLEAN.toString().equals(conf.getDataType())){
					value=BeanUtils.getProperty(obj, fieldName);
				}else{
					m = obj.getClass().getMethod(atrtName);
					value= m.invoke(obj);
				}
				cell.setCellValue(formatValue(conf.getDataType(),value));
			} catch (SecurityException e) {
				log.debug(PropUtils.getExceptionInfo(e));
			} catch (NoSuchMethodException e) {
				log.debug(PropUtils.getExceptionInfo(e));
			}catch (IllegalArgumentException e) {
				log.debug(PropUtils.getExceptionInfo(e));
			} catch (IllegalAccessException e) {
				log.debug(PropUtils.getExceptionInfo(e));
			} catch (InvocationTargetException e) {
				log.debug(PropUtils.getExceptionInfo(e));
			}
		}
	}
	
	public void exportExecute(OutputStream fileOut){
		
	}
	/**
	 * 导出菜单
	 */
	public void exportMenu(OutputStream fileOut){
		HSSFWorkbook wb;
    	List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_MENU']");
		wb = new HSSFWorkbook();
		HSSFSheet sheet=wb.createSheet("MMS_MENU");
        HSSFRow row = sheet.createRow(0);
        
        getFileHead(wb,row,confs);
        //导出菜单信息
		List<Menu> menus=menuManager.getAllMenus();
        menuInfo(menus,sheet,confs);
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	
	public void exportMenuBySystem(OutputStream fileOut,String systemIds,Long companyId){
		HSSFWorkbook wb;
    	List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_MENU']");
		wb = new HSSFWorkbook();
		HSSFSheet sheet=wb.createSheet("MMS_MENU");
        HSSFRow row = sheet.createRow(0);
        
        getFileHead(wb,row,confs);
        
        List<Menu> menus=menuManager.getMenuBySystem(systemIds,companyId);
        menuInfo(menus,sheet,confs);
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	
	private void menuInfo(List<Menu> menus,HSSFSheet sheet,List<DataSheetConfig> confs){
		
		 for(Menu menu:menus){
	        	HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
	        	for(int i=0;i<confs.size();i++){
	        		DataSheetConfig conf=confs.get(i);
	        		if(!conf.isIgnore()){
	        			if(MENU_CODE.equals(conf.getFieldName())){
	        				HSSFCell cell = rowi.createCell(i);
	        				if(menu.getParent()!=null){
	        					cell.setCellValue(menu.getParent().getCode());
	        				}else{
	        					cell.setCellValue("");
	        				}
	        			}else if(SYSTEM_CODE.equals(conf.getFieldName())){
	        				BusinessSystem system=businessSystemManager.getBusiness(menu.getSystemId());
	        				HSSFCell cell = rowi.createCell(i);
	        				if(system!=null){
	        					cell.setCellValue(system.getCode());
	        				}else{
	        					cell.setCellValue("");
	        				}
	        			}else{
	        				setFieldValue(conf,i,rowi,menu);
	        			}
	        		}
	        	}
	        }
	}
	/**
	 * 导入菜单
	 */
	public void importMenu(File file,Long companyId){
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_MENU']");
		//注意menu的systemId，companyId
		FileInputStream fis=null;
 		InputStreamReader fr=null;
 		BufferedReader br=null;
 		HSSFWorkbook wb;
 			try {
				fis=new FileInputStream(file);
				wb = new HSSFWorkbook(fis);
				Map<String,Integer> map=getIdentifier(confs);
				//导入菜单信息
				if(ContextUtils.getCompanyId()==null){
					if(companyId==null){
						List<Company> companys=companyManager.getCompanys();
						for(Company company:companys){
							ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
							ParameterUtils.setParameters(parameters);
							addMenu(wb,confs,map);
						}
					}else{
						ThreadParameters parameters=new ThreadParameters(companyId,null);
						ParameterUtils.setParameters(parameters);
						addMenu(wb,confs,map);
					}
					clearCompanyId();
				}else{
					addMenu(wb,confs,map);
				}
			} catch (FileNotFoundException e) {
				log.debug(PropUtils.getExceptionInfo(e));
			}catch (IOException e){
				log.debug(PropUtils.getExceptionInfo(e));
			}finally{
	 			try{
		 			if(br!=null)br.close();
		 			if(fr!=null)fr.close();
		 			if(fis!=null)fis.close();
	 			}catch(IOException ep){
	 				log.debug(PropUtils.getExceptionInfo(ep));
	 			}
	 		}
	}
	
	private void addMenu(HSSFWorkbook wb,List<DataSheetConfig> confs,Map<String,Integer> map){
		HSSFSheet sheet=wb.getSheet("MMS_MENU");
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			HSSFRow row =sheet.getRow(i);
			if(sheet.getRow(i)!=null){
				Integer index=map.get("code");//获得标识字段的位置
				String code=row.getCell(index).getStringCellValue();//获得code
				Menu menu=menuManager.getMenuByCode(code);//根据code查询菜单是否已存在
				if(menu==null){
					menu=new Menu();
				}
				menu.setCompanyId(ContextUtils.getCompanyId());
				menu.setCreatedTime(new Date());
				for(int j=0;j<confs.size();j++){
					DataSheetConfig conf=confs.get(j);
					if(!conf.isIgnore()){
						String fieldName=conf.getFieldName();
						String value=null;
						if(row.getCell(j)!=null){
							value=row.getCell(j).getStringCellValue();
						}
						if(MENU_CODE.equals(fieldName)){
							Menu parentMenu=menuManager.getMenuByCode(value);
							if(parentMenu!=null){
								menu.setParent(parentMenu);
								Menu topMenu=menuManager.getRootMenu(parentMenu.getId());
								if(topMenu!=null)menu.setSystemId(topMenu.getSystemId());//设置系统id
							}
						}else if("url".equals(fieldName)){
							if(menu.getLayer()!=null){
								if(menu.getLayer()!=null&&menu.getLayer()==1){//一级菜单
									if(StringUtils.isEmpty(menu.getUrl())||"#this".equals(StringUtils.trim(menu.getUrl()))){//一级菜单的url地址是null或是#this时才设值
										menu.setUrl(value);
									}
								}else{
									menu.setUrl(value);
								}
							}
						}else if(SYSTEM_CODE.equals(fieldName)){
							BusinessSystem system=businessSystemManager.getSystemBySystemCode(value);
							if(system!=null){
								menu.setSystemId(system.getId());
							}
						}else{
							if(StringUtils.isNotEmpty(value)){//导入数据
								setValue(menu,fieldName,conf.getDataType(),value,conf.getEnumName());
							}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
								setValue(menu,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
							}
						}
					}
				}
				if(menu.getLayer()!=null){
					if(menu.getLayer()!=null&&menu.getLayer()==1){//一级菜单
						BusinessSystem system=businessSystemManager.getSystemBySystemCode(menu.getCode());
						//一级菜单地址需要和sales中配置的系统路径一致
						if(system!=null){
							menu.setUrl(system.getPath());
						}
					}
				}
				menuManager.saveMenu(menu);
			}
		}
	}
	/**
	 * 导出数据表
	 */
	public void exportDataTable(OutputStream fileOut,List<Long> tableIds,Long menuId){
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_DATA_TABLE']");
		List<DataSheetConfig> colConfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_TABLE_COLUMN']");
		List<DataSheetConfig> generateConfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_GENERATE_SETTING']");
		wb = new HSSFWorkbook();
		//数据表excel信息
    	HSSFSheet sheet=wb.createSheet("MMS_DATA_TABLE");
        HSSFRow row = sheet.createRow(0);
        getFileHead(wb,row,confs);
        //数据表字段excel信息
        HSSFSheet colSheet=wb.createSheet("MMS_TABLE_COLUMN");
        HSSFRow colRow = colSheet.createRow(0);
        getFileHead(wb,colRow,colConfs);
        
        //生成代码配置excel信息
        HSSFSheet generateSheet=wb.createSheet("MMS_GENERATE_SETTING");
        HSSFRow generateRow = generateSheet.createRow(0);
        getFileHead(wb,generateRow,generateConfs);
        List<DataTable> dataTables=null;
        if(tableIds==null||tableIds.size()<=0){//导出选中系统中的所有数据表
        	if(menuId!=null){
        		dataTables=dataTableManager.getUnCompanyAllDataTablesByMenu(menuId);
        		for(DataTable table:dataTables){
        			dataTableInfo(table,sheet,colSheet,generateSheet,confs,colConfs,generateConfs);
        		}
        	}
        }else{//导出选中系统中的选中的数据表
        	for(Long tableId:tableIds){
        		DataTable table=dataTableManager.getDataTable(tableId);
        		dataTableInfo(table,sheet,colSheet,generateSheet,confs,colConfs,generateConfs);
        	}
        }
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	//"标识（表或字段）","表名","别名/显示顺序","实体名/字段名","数据表的状态/别名","对应菜单编码/列名","备注/数据类型","(字段)默认值","(字段)最大长度","(字段)是否已删除"
	private void dataTableInfo(DataTable dataTable,HSSFSheet sheet,HSSFSheet colSheet,HSSFSheet generateSheet,List<DataSheetConfig> confs,List<DataSheetConfig> colConfs,List<DataSheetConfig> generateConfs){
		if(dataTable!=null){
			//表的信息
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
        		DataSheetConfig conf=confs.get(i);
        		if(!conf.isIgnore()){
        			if(MENU_CODE.equals(conf.getFieldName())){
        				HSSFCell cell = rowi.createCell(i);
        				if(dataTable.getMenuId()!=null){
        					Menu menu = menuManager.getMenu(dataTable.getMenuId());
        					cell.setCellValue(menu.getCode());
        				}else{
        					cell.setCellValue("");
        				}
        			}else{
        				setFieldValue(conf,i,rowi,dataTable);
        			}
        		}
        	}
			//字段的信息
			tableColumnInfo(dataTable,colSheet,colConfs);
			//代码生成设置
			generateSettingInfo(dataTable,generateSheet,generateConfs);
		}
	}
	
	private void tableColumnInfo(DataTable dataTable,HSSFSheet colSheet,List<DataSheetConfig> colConfs){
			List<TableColumn> columns=tableColumnManager.getUnCompanyAllTableColumnByDataTableId(dataTable.getId());
			for(TableColumn col:columns){
				HSSFRow colrowi = colSheet.createRow(colSheet.getLastRowNum()+1);
				for(int i=0;i<colConfs.size();i++){
	        		DataSheetConfig conf=colConfs.get(i);
	        		if(!conf.isIgnore()){
	        			if(TABLE_NAME.equals(conf.getFieldName())){
	        				HSSFCell cell = colrowi.createCell(i);
	    					cell.setCellValue(dataTable.getName());
	        			}else{
	        				setFieldValue(conf,i,colrowi,col);
	        			}
	        		}
	        	}
			}
	}
	private void generateSettingInfo(DataTable dataTable,HSSFSheet generateSheet,List<DataSheetConfig> generateConfs){
		GenerateSetting generateSetting = dataTableManager.getGenerateSettingByTable(dataTable.getId());
		if(generateSetting!=null){
			//生成代码配置信息
			HSSFRow rowi = generateSheet.createRow(generateSheet.getLastRowNum()+1);
			for(int i=0;i<generateConfs.size();i++){
				DataSheetConfig conf=generateConfs.get(i);
				if(!conf.isIgnore()){
					if(TABLE_NAME.equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						cell.setCellValue(dataTable.getName());
					}else{
						setFieldValue(conf,i,rowi,generateSetting);
					}
				}
			}
			
		}
	}
	public void clearCompanyId(){
		ThreadParameters parameters=new ThreadParameters(null,null);
		ParameterUtils.setParameters(parameters);
	}
	/**
	 * 导入数据表
	 */
	public void importDataTable(File file,Long companyId){
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_DATA_TABLE']");
		List<DataSheetConfig> colConfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_TABLE_COLUMN']");
		List<DataSheetConfig> generateConfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_GENERATE_SETTING']");
		Map<String,Integer> map=getIdentifier(confs);
		Map<String,Integer> colMap=getIdentifier(colConfs);
		Map<String,Integer> generateMap=getIdentifier(generateConfs);
		//创建时间,创建人姓名,创建人id,公司id
		FileInputStream fis=null;
 		InputStreamReader fr=null;
 		BufferedReader br=null;
 		try{
 			fis=new FileInputStream(file);
 			HSSFWorkbook wb=new HSSFWorkbook(fis);
 			HSSFSheet sheet=wb.getSheet("MMS_DATA_TABLE");
 			if(ContextUtils.getCompanyId()==null){
 				if(companyId==null){
 					List<Company> companys=companyManager.getCompanys();
 					for(Company company:companys){
 						ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
 						ParameterUtils.setParameters(parameters);
 						importTable(sheet,confs,map);
 					}
 				}else{
 					ThreadParameters parameters=new ThreadParameters(companyId,null);
						ParameterUtils.setParameters(parameters);
						importTable(sheet,confs,map);
 				}
 				clearCompanyId();
 			}else{
 				importTable(sheet,confs,map);
 			}
 			HSSFSheet colSheet=wb.getSheet("MMS_TABLE_COLUMN");
 			if(ContextUtils.getCompanyId()==null){
 				List<Company> companys=companyManager.getCompanys();
 				for(Company company:companys){
 					ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
 					ParameterUtils.setParameters(parameters);
 					importTableColumn(colSheet,colConfs,colMap);
 				}
 				clearCompanyId();
 			}else{
 				importTableColumn(colSheet,colConfs,colMap);
 			}
 			HSSFSheet generateSheet=wb.getSheet("MMS_GENERATE_SETTING");
 			if(ContextUtils.getCompanyId()==null){
 				List<Company> companys=companyManager.getCompanys();
 				for(Company company:companys){
 					ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
 					ParameterUtils.setParameters(parameters);
 					importGenerateSetting(generateSheet,generateConfs,generateMap);
 				}
 				clearCompanyId();
 			}else{
 				importGenerateSetting(generateSheet,generateConfs,generateMap);
 			}
 		} catch (FileNotFoundException e) {
 			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
 			try{
	 			if(br!=null)br.close();
	 			if(fr!=null)fr.close();
	 			if(fis!=null)fis.close();
 			}catch(IOException ep){
 				log.debug(PropUtils.getExceptionInfo(ep));
 			}
 		}
	}
	private void importTable(HSSFSheet sheet,List<DataSheetConfig> confs,Map<String,Integer> map){
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			HSSFRow row =sheet.getRow(i);
			if(sheet.getRow(i)!=null){
			addDataTable(confs,row,map);
			}
		}
	}
	private void importTableColumn(HSSFSheet colSheet,List<DataSheetConfig> colConfs,Map<String,Integer> colMap){
		int colFirstRowNum = colSheet.getFirstRowNum();
			int colRowNum=colSheet.getLastRowNum();
			for(int i=colFirstRowNum+1;i<=colRowNum;i++){
				HSSFRow row =colSheet.getRow(i);
				if(colSheet.getRow(i)!=null){
					addTableColumns(colConfs,row,colMap);
				}
			}
	}
	private void importGenerateSetting(HSSFSheet generateSheet,List<DataSheetConfig> generateConfs,Map<String,Integer> generateMap){
		int firstRowNum = generateSheet.getFirstRowNum();
		int rowNum=generateSheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			HSSFRow row =generateSheet.getRow(i);
			if(generateSheet.getRow(i)!=null){
				addGenerateSetting(generateConfs,row,generateMap);
			}
		}
	}
	private void addDataTable(List<DataSheetConfig> confs,HSSFRow row,Map<String,Integer> map ){
		//创建时间,创建人姓名,创建人id,公司id
		Integer index=map.get("name");
		String tableName=row.getCell(index).getStringCellValue();//数据表名称
		
		DataTable table=dataTableManager.getDataTableByTableName(tableName);
		if(table==null){
			table=new DataTable();
		}
		table.setName(tableName);
		for(int j=0;j<confs.size();j++){
			DataSheetConfig conf=confs.get(j);
			if(!conf.isIgnore()){
				String fieldName=conf.getFieldName();
				String value=null;
				if(row.getCell(j)!=null){
					value=row.getCell(j).getStringCellValue();
				}
				if(MENU_CODE.equals(fieldName)){
					Menu menu=menuManager.getMenuByCode(value);
					table.setMenuId(menu.getId());
				}else{
					if(StringUtils.isNotEmpty(value)){//导入数据
						setValue(table,fieldName,conf.getDataType(),value,conf.getEnumName());
					}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
						setValue(table,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
					}
				}
			}
		}
		table.setCreatedTime(new Date());
		table.setCreator(ContextUtils.getLoginName());
		table.setCreatorName(ContextUtils.getUserName());
		table.setCompanyId(ContextUtils.getCompanyId());
		dataTableManager.saveDataTable(table);
	}
	private void addTableColumns(List<DataSheetConfig> confs,HSSFRow row,Map<String,Integer> map){
		//公司id
		String tableName=row.getCell(map.get(TABLE_NAME)).getStringCellValue();//数据表名称
		String columnName=row.getCell(map.get("name")).getStringCellValue();//字段名称
		String deleted=row.getCell(map.get("deleted")).getStringCellValue();//是否删除
		
		DataTable table=dataTableManager.getDataTableByTableName(tableName);
		TableColumn column=null;
		if("true".equals(StringUtils.lowerCase(deleted))){//如是删除的字段则新建
			column=new TableColumn();
		}else{//如不是删除的字段
			column=tableColumnManager.getTableColumnByColName(table.getId(),columnName);
			if(column==null){//该字段不存在，则新建
				column=new TableColumn();
			}
		}
		if(column!=null){
			for(int j=0;j<confs.size();j++){
				DataSheetConfig conf=confs.get(j);
				if(!conf.isIgnore()){
					String fieldName=conf.getFieldName();
					String value=null;
					if(row.getCell(j)!=null){
						value=row.getCell(j).getStringCellValue();
					}
					if(TABLE_NAME.equals(conf.getFieldName())){
						column.setDataTableId(table.getId());
        			}else{
        				if(StringUtils.isNotEmpty(value)){//导入数据
        					setValue(column,fieldName,conf.getDataType(),value,conf.getEnumName());
        				}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
        					setValue(column,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
        				}
        			}
				}
			}
			column.setCompanyId(ContextUtils.getCompanyId());
			tableColumnManager.saveColumn(column,false);
		}
	}
	private void addGenerateSetting(List<DataSheetConfig> generateConfs,HSSFRow generateRow,Map<String,Integer> generateMap ){
		//创建时间,创建人姓名,创建人id,公司id
		Integer index=generateMap.get(TABLE_NAME);
		String tableName=generateRow.getCell(index).getStringCellValue();//数据表名称
		
		DataTable table=dataTableManager.getDataTableByTableName(tableName);
		if(table!=null){
			GenerateSetting generateSetting = dataTableManager.getGenerateSettingByTable(table.getId());
			if(generateSetting==null){
				generateSetting = new GenerateSetting();
			}
			for(int j=0;j<generateConfs.size();j++){
				DataSheetConfig conf=generateConfs.get(j);
				if(!conf.isIgnore()){
					String fieldName=conf.getFieldName();
					String value=null;
					if(generateRow.getCell(j)!=null){
						value=generateRow.getCell(j).getStringCellValue();
					}
					if(TABLE_NAME.equals(conf.getFieldName())){
						generateSetting.setTableId(table.getId());
					}else{
						if(StringUtils.isNotEmpty(value)){//导入数据
							setValue(generateSetting,fieldName,conf.getDataType(),value,conf.getEnumName());
						}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
							setValue(generateSetting,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
						}
					}
				}
			}
			generateSetting.setCreatedTime(new Date());
			generateSetting.setCreator(ContextUtils.getLoginName());
			generateSetting.setCreatorName(ContextUtils.getUserName());
			generateSetting.setCompanyId(ContextUtils.getCompanyId());
			dataTableManager.saveGenerateSetting(generateSetting);
		}
	}
	/**
	 * 导出列表
	 */
	public void exportListView(OutputStream fileOut,String listViewIds,Long menuId){
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_LIST_VIEW']");
		List<DataSheetConfig> colConfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_LIST_COLUMN']");
		List<DataSheetConfig> ghConfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_GROUP_HEADER']");
		wb = new HSSFWorkbook();
		//列表excel信息
		HSSFSheet sheet=wb.createSheet("MMS_LIST_VIEW");
        HSSFRow row = sheet.createRow(0);
        getFileHead(wb,row,confs);
        //字段excel信息
        HSSFSheet colSheet=wb.createSheet("MMS_LIST_COLUMN");
        HSSFRow colRow = colSheet.createRow(0);
        getFileHead(wb,colRow,colConfs);
        //字段excel信息
        HSSFSheet ghSheet=wb.createSheet("MMS_GROUP_HEADER");
        HSSFRow ghRow = ghSheet.createRow(0);
        getFileHead(wb,ghRow,ghConfs);
        List<ListView> listViews=null;
        if(StringUtils.isEmpty(listViewIds)){
        	if(menuId!=null){
        		listViews=listViewManager.getUnCompanyListViewsBySystem(menuId);
        		for(ListView listview:listViews){
        			listViewInfo(listview,sheet,colSheet,ghSheet,confs,colConfs,ghConfs);
        		}
        	}
        }else{
        	String[] idStr=listViewIds.split(",");
        	for(String strid:idStr){
        		if(StringUtils.isNotEmpty(strid)){
        			ListView listview=listViewManager.getView(Long.parseLong(strid));
        			listViewInfo(listview,sheet,colSheet,ghSheet,confs,colConfs,ghConfs);
        		}
        	}
        }
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	/**
	 * 
	 * @param listview
	 * @param sheet
	 * 
	 */
	private void listViewInfo(ListView listview,HSSFSheet sheet,HSSFSheet colSheet,HSSFSheet ghSheet,List<DataSheetConfig> confs,List<DataSheetConfig> colConfs,List<DataSheetConfig> ghConfs){
		if(listview!=null){
			//列表的信息
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
				DataSheetConfig conf=confs.get(i);
				if(!conf.isIgnore()){
					if(TABLE_NAME.equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						if(listview.getDataTable()!=null){
							cell.setCellValue(listview.getDataTable().getName());
						}else{
							cell.setCellValue("");
						}
					}else if(MENU_CODE.equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						if(listview.getMenuId()!=null){
							Menu menu=menuManager.getMenu(listview.getMenuId());
							cell.setCellValue(menu.getCode());
						}else{
							cell.setCellValue("");
						}
					}else if(JQ_GRID_PROPERTYS.equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						List<JqGridProperty> pros=listview.getJqGridPropertys();
						StringBuilder jqp=new StringBuilder();
						for(JqGridProperty pro:pros){
							if(StringUtils.isNotEmpty(pro.getName())){
								jqp.append(pro.getName())
								.append(":").append(pro.getValue()).append(",");
							}
						}
						cell.setCellValue(jqp.toString());
					}else{
        				setFieldValue(conf,i,rowi,listview);
        			}
				}
			}
			//字段的信息
			listColumnInfo(listview,colSheet,colConfs);
			//组合头信息
			groupHeaderInfo(listview,ghSheet,ghConfs);
		}
	}
	
	private void listColumnInfo(ListView listview,HSSFSheet colSheet,List<DataSheetConfig> colConfs){
		List<ListColumn> columns=listColumnManager.getUnCompanyColumns(listview.getId());
		for(ListColumn col:columns){
			HSSFRow colrowi = colSheet.createRow(colSheet.getLastRowNum()+1);
			for(int i=0;i<colConfs.size();i++){
				DataSheetConfig conf=colConfs.get(i);
				if(!conf.isIgnore()){
					if(LIST_VIEW_CODE.equals(conf.getFieldName())){
						HSSFCell cell = colrowi.createCell(i);
						cell.setCellValue(listview.getCode());
					}else if(TABLE_COLUMN_NAME.equals(conf.getFieldName())){
						HSSFCell cell = colrowi.createCell(i);
						if(col.getTableColumn()!=null){
							cell.setCellValue(col.getTableColumn().getName());
						}else{//当是占位符时没有对应字段
							cell.setCellValue("");
						}
					}else if("mainKey".equals(conf.getFieldName())){
						HSSFCell cell = colrowi.createCell(i);
						if(col.getMainKey()!=null){
							cell.setCellValue(col.getMainKey().getName());
						}else{//当是占位符时没有对应字段
							cell.setCellValue("");
						}
					}else{
        				setFieldValue(conf,i,colrowi,col);
        			}
				}
			}
		}
	}
	
	private void groupHeaderInfo(ListView listview,HSSFSheet colSheet,List<DataSheetConfig> colConfs){
		List<GroupHeader> groupHeaders=groupHeaderManager.getGroupHeadersByViewId(listview.getId());
		for(GroupHeader header:groupHeaders){
			HSSFRow colrowi = colSheet.createRow(colSheet.getLastRowNum()+1);
			for(int i=0;i<colConfs.size();i++){
				DataSheetConfig conf=colConfs.get(i);
				if(!conf.isIgnore()){
					if(LIST_VIEW_CODE.equals(conf.getFieldName())){
						HSSFCell cell = colrowi.createCell(i);
						cell.setCellValue(listview.getCode());
					}else{
        				setFieldValue(conf,i,colrowi,header);
        			}
				}
			}
		}
	}
	/**
	 * 导入列表
	 * 
	 */
	public void importListView(File file,Long companyId){
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_LIST_VIEW']");
		List<DataSheetConfig> colConfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_LIST_COLUMN']");
		List<DataSheetConfig> ghConfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_GROUP_HEADER']");
		Map<String,Integer> map=getIdentifier(confs);
		Map<String,Integer> colMap=getIdentifier(colConfs);
		Map<String,Integer> ghMap=getIdentifier(ghConfs);
		//创建者姓名,创建者id,创建时间,公司id
		FileInputStream fis=null;
 		InputStreamReader fr=null;
 		BufferedReader br=null;
 		try{
 			fis=new FileInputStream(file);
 			HSSFWorkbook wb=new HSSFWorkbook(fis);
 			HSSFSheet sheet=wb.getSheet("MMS_LIST_VIEW");
 			if(ContextUtils.getCompanyId()==null){
 				if(companyId==null){
 					List<Company> companys=companyManager.getCompanys();
 					for(Company company:companys){
 						ThreadParameters parameters=new ThreadParameters(company.getCompanyId());
 						ParameterUtils.setParameters(parameters);
 						importListView(sheet,confs,map);
 					}
 				}else{
 					ThreadParameters parameters=new ThreadParameters(companyId);
 					ParameterUtils.setParameters(parameters);
						importListView(sheet,confs,map);
 				}
				clearCompanyId();
			}else{
				importListView(sheet,confs,map);
			}
 			
 			
 			HSSFSheet colSheet=wb.getSheet("MMS_LIST_COLUMN");
 			if(ContextUtils.getCompanyId()==null){
				List<Company> companys=companyManager.getCompanys();
				for(Company company:companys){
					ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
					ParameterUtils.setParameters(parameters);
					importListColumn(colSheet,colConfs,colMap);
				}
				clearCompanyId();
			}else{
				importListColumn(colSheet,colConfs,colMap);
			}
 			
 			HSSFSheet ghSheet=wb.getSheet("MMS_GROUP_HEADER");
 			if(ContextUtils.getCompanyId()==null){
				List<Company> companys=companyManager.getCompanys();
				for(Company company:companys){
					ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
					ParameterUtils.setParameters(parameters);
					importGroupHeader(ghSheet,ghConfs,ghMap);
				}
				clearCompanyId();
			}else{
				importGroupHeader(ghSheet,ghConfs,ghMap);
			}
 		} catch (FileNotFoundException e) {
 			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
 			try{
	 			if(br!=null)br.close();
	 			if(fr!=null)fr.close();
	 			if(fis!=null)fis.close();
 			}catch(IOException ep){
 				log.debug(PropUtils.getExceptionInfo(ep));
 			}
 		}
	}
	
	private void importListView(HSSFSheet sheet,List<DataSheetConfig> confs,Map<String,Integer> map){
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			HSSFRow row =sheet.getRow(i);
			if(sheet.getRow(i)!=null){
			addListView(confs,row,map);
			}
		}
	}
	private void importListColumn(HSSFSheet colSheet,List<DataSheetConfig> colConfs,Map<String,Integer> colMap){
		int colFirstRowNum = colSheet.getFirstRowNum();
		int colRowNum=colSheet.getLastRowNum();
		for(int i=colFirstRowNum+1;i<=colRowNum;i++){
			HSSFRow row =colSheet.getRow(i);
			if(colSheet.getRow(i)!=null){
				addListColumn(colConfs,row,colMap);
			}
		}
	}
	private void importGroupHeader(HSSFSheet colSheet,List<DataSheetConfig> colConfs,Map<String,Integer> colMap){
		int colFirstRowNum = colSheet.getFirstRowNum();
		int colRowNum=colSheet.getLastRowNum();
		for(int i=colFirstRowNum+1;i<=colRowNum;i++){
			HSSFRow row =colSheet.getRow(i);
			if(colSheet.getRow(i)!=null){
				addGroupHeader(colConfs,row,colMap);
			}
		}
	}
	
	private void addListView(List<DataSheetConfig> confs,HSSFRow row,Map<String,Integer> map){
		String listViewCode=row.getCell(map.get("code")).getStringCellValue();
		ListView listview=listViewManager.getListViewByCode(listViewCode);
		if(listview!=null){
			//彻底删除列表对应的所有的字段信息
			listColumnManager.deleteAllColumns(listview.getId());
		}
		if(listview==null){
			listview=new ListView();
		}
		//创建者姓名,创建者id,创建时间,公司id
		listview.setCreator(ContextUtils.getLoginName());
		listview.setCreatorName(ContextUtils.getUserName());
		listview.setCreatedTime(new Date());
		listview.setCompanyId(ContextUtils.getCompanyId());
		listViewManager.saveListView(listview);
		for(int j=0;j<confs.size();j++){
			DataSheetConfig conf=confs.get(j);
			if(!conf.isIgnore()){
				String fieldName=conf.getFieldName();
				String value="";
				if(row.getCell(j)!=null){
					value=row.getCell(j).getStringCellValue();
				}
				if(TABLE_NAME.equals(conf.getFieldName())){
					DataTable datatable=dataTableManager.getDataTableByTableName(value);
					if(datatable!=null)listview.setDataTable(datatable);
				}else if(MENU_CODE.equals(conf.getFieldName())){
					Menu menu=menuManager.getMenuByCode(value);
					if(menu!=null)listview.setMenuId(menu.getId());
				}else if(JQ_GRID_PROPERTYS.equals(conf.getFieldName())){
					String[] jgpStrs=value.split(",");
					List<JqGridProperty> jgps=new ArrayList<JqGridProperty>();
					for(String str:jgpStrs){
						if(StringUtils.isNotEmpty(str)){
							JqGridProperty jgp=new JqGridProperty();
							String[] prs=str.split(":");
							jgp.setName(prs.length>=1?prs[0]:"");
							jgp.setValue(prs.length>=2?prs[1]:"");
							jgp.setCompanyId(ContextUtils.getCompanyId());
							jgp.setListView(listview);
							jqGridPropertyDao.save(jgp);
							jgps.add(jgp);
						}
					}
					listview.setJqGridPropertys(jgps);
				}else{
					if(StringUtils.isNotEmpty(value)){//导入数据
						setValue(listview,fieldName,conf.getDataType(),value,conf.getEnumName());
					}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
						setValue(listview,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
					}
				}
			}
		}
		listViewManager.saveListView(listview);
	}
	
	private void addListColumn(List<DataSheetConfig> colConfs,HSSFRow row,Map<String,Integer> map){
		String listViewCode=row.getCell(map.get(LIST_VIEW_CODE)).getStringCellValue();//列表编码
		String tbColName=row.getCell(map.get(TABLE_COLUMN_NAME)).getStringCellValue();//数据表字段名称
		
		ListView listview=listViewManager.getListViewByCode(listViewCode);
		ListColumn col=new ListColumn();
		col.setCompanyId(ContextUtils.getCompanyId());
		for(int j=0;j<colConfs.size();j++){
			DataSheetConfig conf=colConfs.get(j);
			if(!conf.isIgnore()){
				String fieldName=conf.getFieldName();
				String value=null;
				if(row.getCell(j)!=null){
					value=row.getCell(j).getStringCellValue();
				}
				if(LIST_VIEW_CODE.equals(conf.getFieldName())){
					col.setListView(listview);
				}else if(TABLE_COLUMN_NAME.equals(conf.getFieldName())){
					TableColumn tbCol=null;
					if(listview!=null&&listview.getDataTable()!=null)tbCol=tableColumnManager.getTableColumnByColName(listview.getDataTable().getId(), tbColName);
					col.setTableColumn(tbCol);
				}else if("mainKey".equals(conf.getFieldName())){
					TableColumn tbCol=null;
					if(listview!=null&&listview.getDataTable()!=null)tbCol=tableColumnManager.getTableColumnByColName(listview.getDataTable().getId(), value);
					col.setMainKey(tbCol);
				}else{
					if(StringUtils.isNotEmpty(value)){//导入数据
						setValue(col,fieldName,conf.getDataType(),value,conf.getEnumName());
					}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
						setValue(col,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
					}
				}
			}
		}
		listColumnManager.saveColumn(col);
	}
	
	private void addGroupHeader(List<DataSheetConfig> colConfs,HSSFRow row,Map<String,Integer> map){
		String listViewCode=row.getCell(map.get(LIST_VIEW_CODE)).getStringCellValue();//列表编码
		String startColumnName=row.getCell(map.get("startColumnName")).getStringCellValue();//开始列名
		String numberOfColumns=row.getCell(map.get("numberOfColumns")).getStringCellValue();//合并列数
		String titleText=row.getCell(map.get("titleText")).getStringCellValue();//新列名称
		
		ListView listview=listViewManager.getListViewByCode(listViewCode);
		if(listview!=null){
			GroupHeader header=groupHeaderManager.getGroupHeaderByInfo(listview.getId(), startColumnName, Integer.parseInt(numberOfColumns), titleText);
			if(header==null){
				header=new GroupHeader();
			}
			header.setListViewId(listview.getId());
			header.setStartColumnName(startColumnName);
			header.setNumberOfColumns( Integer.parseInt(numberOfColumns));
			header.setTitleText(titleText);
			groupHeaderManager.save(header);
		}
	}
	
	/**
	 * 导出表单
	 */
	public void exportFormView(OutputStream fileOut,List<Long> formViewIds,Long menuId){
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_FORM_VIEW']");
		wb = new HSSFWorkbook();
		HSSFSheet sheet=wb.createSheet("MMS_FORM_VIEW");
		
		//获得导出的根节点
		String[] rootPaths=getRootPath();
		String exportRootPath=rootPaths[0];
		
		//创建导出文件夹，导出的流程定义文件暂存的位置
		File folder = new File(exportRootPath+"/"+FORM_VIEW_PATH);
		if(!folder.exists()){
			folder.mkdirs();
		}
		
        HSSFRow row = sheet.createRow(0);
        getFileHead(wb,row,confs);
        List<FormView> formViews=null;
        if(formViewIds==null||formViewIds.size()<=0){//导出选中系统中的所有数据表
        	if(menuId!=null){
        		formViews=formViewManager.getUnCompanyFormViewsBySystem(menuId);
        		for(FormView formview:formViews){
        			formViewInfo(formview,sheet,confs,exportRootPath);
        		}
        	}
        }else{//导出选中系统中的选中的数据表
        	for(Long formId:formViewIds){
        		FormView formview=formViewManager.getFormView(formId);
        		formViewInfo(formview,sheet,confs,exportRootPath);
        	}
        }
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	private void formViewInfo(FormView formview,HSSFSheet sheet,List<DataSheetConfig> confs,String exportRootPath){
		if(formview!=null){
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
				DataSheetConfig conf=confs.get(i);
				if(!conf.isIgnore()){
					String fieldName=conf.getFieldName();
					if(TABLE_NAME.equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						if(formview.getDataTable()!=null){
							cell.setCellValue(formview.getDataTable().getName());
						}else{
							cell.setCellValue("");
						}
					}else if(MENU_CODE.equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						if(formview.getMenuId()!=null){
							Menu menu=menuManager.getMenu(formview.getMenuId());
							cell.setCellValue(menu.getCode());
						}else{
							cell.setCellValue("");
						}
					}else if("html".equals(conf.getFieldName())){
						if(StringUtils.isNotEmpty(formview.getHtml())){
							File file = new File(exportRootPath+"/"+FORM_VIEW_PATH+"/"+formview.getCode()+"#"+formview.getVersion()+".txt");
							try {
								FileUtils.writeStringToFile(file, formview.getHtml(), "utf-8");
							}catch (Exception e) {
								log.debug(PropUtils.getExceptionInfo(e));
							}
						}
					}else{
        				setFieldValue(conf,i,rowi,formview);
        			}
				}
			}
		}
	}
	/**
	 * 导入表单
	 */
	public void importFormView(File file,Long companyId){
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_FORM_VIEW']");
		Map<String,Integer> map=getIdentifier(confs);
		//创建者姓名,创建者id,创建时间,公司id
		FileInputStream fis=null;
 		InputStreamReader fr=null;
 		BufferedReader br=null;
 		try{
 			fis=new FileInputStream(file);
 			HSSFWorkbook wb=new HSSFWorkbook(fis);
 			HSSFSheet sheet=wb.getSheetAt(0);
 			if(ContextUtils.getCompanyId()==null){
 				if(companyId==null){
 					List<Company> companys=companyManager.getCompanys();
 					for(Company company:companys){
 						ThreadParameters parameters=new ThreadParameters(company.getCompanyId());
 						ParameterUtils.setParameters(parameters);
 						importFormView(sheet,confs,map);
 					}
 				}else{
 					ThreadParameters parameters=new ThreadParameters(companyId);
						ParameterUtils.setParameters(parameters);
						importFormView(sheet,confs,map);
 				}
 				clearCompanyId();
 			}else{
 				importFormView(sheet,confs,map);
 			}
 			
 		} catch (FileNotFoundException e) {
 			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
 			try{
	 			if(br!=null)br.close();
	 			if(fr!=null)fr.close();
	 			if(fis!=null)fis.close();
 			}catch(IOException ep){
 				log.debug(PropUtils.getExceptionInfo(ep));
 			}
 		}
	}
	private void importFormView(HSSFSheet sheet,List<DataSheetConfig> confs,Map<String,Integer> map){
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			if(sheet.getRow(i)!=null){
				HSSFRow row =sheet.getRow(i);
				addFormView(confs,row,map);
			}
		}
	}
	private void addFormView(List<DataSheetConfig> confs,HSSFRow row,Map<String,Integer> map){
		String versionStr=row.getCell(map.get("version")).getStringCellValue();//表单版本
		if(StringUtils.isNotEmpty(versionStr)&&StringUtils.isNotEmpty(versionStr)){
			String code=row.getCell(map.get("code")).getStringCellValue();//表单编码
			FormView formview=formViewManager.getCurrentFormViewByCodeAndVersion(code, Integer.parseInt(versionStr));
			if(formview==null){
				formview=new FormView();
			}
			//创建者姓名,创建者id,创建时间,公司id
			formview.setCreator(ContextUtils.getLoginName());
			formview.setCreatorName(ContextUtils.getUserName());
			formview.setCreatedTime(new Date());
			formview.setCompanyId(ContextUtils.getCompanyId());
			for(int i=0;i<confs.size();i++){
				DataSheetConfig conf=confs.get(i);
				if(!conf.isIgnore()){
					String fieldName=conf.getFieldName();
					String value=null;
					if(row.getCell(i)!=null){
						value=row.getCell(i).getStringCellValue();
					}
					if(TABLE_NAME.equals(fieldName)){
						DataTable datatable=dataTableManager.getDataTableByTableName(value);
						if(datatable!=null)formview.setDataTable(datatable);
					}else if(MENU_CODE.equals(conf.getFieldName())){
						Menu menu=menuManager.getMenuByCode(value);
						if(menu!=null)formview.setMenuId(menu.getId());
					}else{
						if(StringUtils.isNotEmpty(value)){//导入数据
							setValue(formview,fieldName,conf.getDataType(),value,conf.getEnumName());
						}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
							setValue(formview,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
						}
					}
				}
			}
			formViewManager.save(formview);
		}
	}
	
	private void setCompanyId(){
		//将公司id放入线程变量
		if(ContextUtils.getCompanyId()==null){
			List<Company> companys=companyManager.getCompanys();
			Long companyId=null;
			if(companys.size()>0)companyId=companys.get(0).getId();
			ThreadParameters parameters=new ThreadParameters(companyId,null);
			ParameterUtils.setParameters(parameters);
		}
	}
	/**
	 * 导出系统
	 * @param fileOut
	 * @param systemIds
	 */
	public void exportSystem(OutputStream fileOut,String systemIds){
		//将公司id放入线程变量
		setCompanyId();
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_BUSINESS_SYSTEM']");
		try {
			wb = new HSSFWorkbook();
			//系统表
			HSSFSheet sheet=wb.createSheet("ACS_BUSINESS_SYSTEM");
	        HSSFRow row = sheet.createRow(0);
	        getFileHead(wb,row,confs);
	        
	        if(StringUtils.isEmpty(systemIds)){//导出所有系统
	        	List<BusinessSystem> systems=businessSystemManager.getAllSystems();
	    		for(BusinessSystem system:systems){
	    			systemInfo(system,sheet,confs,wb,ContextUtils.getCompanyId());
	    		}
	        }else{//导出选中系统
	        	String[] idStrs=systemIds.split(",");
	        	for(String idStr:idStrs){
	        		if(StringUtils.isNotEmpty(idStr)){
	        			BusinessSystem system=businessSystemManager.getBusiness(Long.parseLong(idStr));
	        			systemInfo(system,sheet,confs,wb,ContextUtils.getCompanyId());
	        		}
	        	}
	        }
			wb.write(fileOut);
		}catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	
	private void systemInfo(BusinessSystem system,HSSFSheet sheet,List<DataSheetConfig> confs,HSSFWorkbook wb,Long companyId){
		if(system!=null){
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
				DataSheetConfig conf=confs.get(i);
				if(!conf.isIgnore()){
    				setFieldValue(conf,i,rowi,system);
				}
			}
		}
	}
	private void webpageInfo(HSSFWorkbook wb,Long companyId){
		List<DataSheetConfig> pageConfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='PORTAL_WEBPAGE']");
		//portal系统的webpage
		HSSFSheet sheet=wb.createSheet("PORTAL_WEBPAGE");
		HSSFRow row = sheet.createRow(0);
		getFileHead(wb,row,pageConfs);
		Webpage webpage=webpageDao.getWebpage(companyId);
		HSSFRow prow = sheet.createRow(sheet.getLastRowNum()+1);
		for(int i=0;i<pageConfs.size();i++){
			DataSheetConfig conf=pageConfs.get(i);
			if(!conf.isIgnore()){
				setFieldValue(conf,i,prow,webpage);
			}
		}
	}
	private void widgetInfo(HSSFWorkbook wb,String systemIds,Long companyId){
		List<DataSheetConfig> widgetConfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='PORTAL_WIDGET']");
		//portal系统的WIDGET
		HSSFSheet widgetSheet=wb.createSheet("PORTAL_WIDGET");
		HSSFRow widgetRow = widgetSheet.createRow(0);
		getFileHead(wb,widgetRow,widgetConfs);
		List<Widget> widgets=null;
		if(StringUtils.isEmpty(systemIds)){
			widgets=widgetDao.getDefaultWidgets(companyId);
		}else{
			widgets=widgetDao.getWidgetsBySystem(systemIds,companyId);
		}
		for(Widget widget:widgets){
			HSSFRow rowi = widgetSheet.createRow(widgetSheet.getLastRowNum()+1);
			for(int i=0;i<widgetConfs.size();i++){
				DataSheetConfig conf=widgetConfs.get(i);
				if(!conf.isIgnore()){
					if(SYSTEM_CODE.equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						if(StringUtils.isNotEmpty(widget.getSystemCode())){
								cell.setCellValue(widget.getSystemCode());
						}else{
							cell.setCellValue("");
						}
					}else{
						setFieldValue(conf,i,rowi,widget);
					}
				}
			}
		}
	}
	private void widgetRoleInfo(HSSFWorkbook wb,String systemIds,Long companyId){
		List<DataSheetConfig> widgetRoleConfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='PORTAL_WIDGET_ROLE']");
		//portal系统的WIDGET_ROLE
		HSSFSheet widgetRoleSheet=wb.createSheet("PORTAL_WIDGET_ROLE");
		HSSFRow widgetRolerow = widgetRoleSheet.createRow(0);
		getFileHead(wb,widgetRolerow,widgetRoleConfs);
		List<WidgetRole> wrs=null;
		if(StringUtils.isEmpty(systemIds)){
			wrs=widgetRoleDao.getWidgetRoles(companyId);
		}else{
			wrs=widgetRoleDao.getWidgetRoleBySystem(systemIds,companyId);
		}
		for(WidgetRole wr:wrs){
			if(wr.getWidgetId()!=null){
				Widget wg=widgetDao.get(wr.getWidgetId());
				if(wg!=null){
					HSSFRow rowi = widgetRoleSheet.createRow(widgetRoleSheet.getLastRowNum()+1);
					for(int i=0;i<widgetRoleConfs.size();i++){
							DataSheetConfig conf=widgetRoleConfs.get(i);
							if(!conf.isIgnore()){
								if(SYSTEM_CODE.equals(conf.getFieldName())){
									HSSFCell cell = rowi.createCell(i);
									if(StringUtils.isNotEmpty(wg.getSystemCode())){
										cell.setCellValue(wg.getSystemCode());
									}else{
										cell.setCellValue("");
									}
								} else if("widgetCode".equals(conf.getFieldName())){
									HSSFCell cell = rowi.createCell(i);
									if(wg!=null){
										cell.setCellValue(wg.getCode());
									}else{
										cell.setCellValue("");
									}
								}else if(ROLE_CODE.equals(conf.getFieldName())){
									HSSFCell cell = rowi.createCell(i);
									if(wr.getRoleId()!=null){
										Role role=standardRoleManager.getStandardRole(wr.getRoleId());
										if(role!=null){
											cell.setCellValue(role.getCode());
										}else{
											cell.setCellValue("");
										}
									}else{
										cell.setCellValue("");
									}
								}
							}
					}
				}
			}
		}
	}
	
	private void widgetParameterInfo(HSSFWorkbook wb,String systemIds,Long companyId){
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='PORTAL_WIDGET_PARAMETER']");
		//portal系统的WIDGET
		HSSFSheet sheet=wb.createSheet("PORTAL_WIDGET_PARAMETER");
		HSSFRow row = sheet.createRow(0);
		getFileHead(wb,row,confs);
		List<WidgetParameter> wps=null;
		if(StringUtils.isEmpty(systemIds)){
			wps=widgetParameterDao.getAllDefaultWidgetParameters(companyId);
		}else{
			wps=widgetParameterDao.getWidgetParameterBySystem(systemIds,companyId);
		}
		for(WidgetParameter wp:wps){
				HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
				for(int i=0;i<confs.size();i++){
						DataSheetConfig conf=confs.get(i);
						if(!conf.isIgnore()){
							if("widgetCode".equals(conf.getFieldName())){
								HSSFCell cell = rowi.createCell(i);
								if(wp!=null){
									cell.setCellValue(wp.getWidget().getCode());
								}else{
									cell.setCellValue("");
								}
							}else if("optionCode".equals(conf.getFieldName())){
								HSSFCell cell = rowi.createCell(i);
								if(wp!=null&&wp.getOptionGroupId()!=null&&wp.getOptionGroupId()!=0){
									OptionGroup group=optionGroupManager.getOptionGroup(wp.getOptionGroupId());
									cell.setCellValue(group.getCode());
								}else{
									cell.setCellValue("");
								}
							}else{
								setFieldValue(conf,i,rowi,wp);
							}
						}
				}
		}
	}
	
	private void portalInfo(HSSFWorkbook wb,String systemIds,Long companyId){
		//portal系统的webpage
		webpageInfo(wb,companyId);
		//portal系统的WIDGET
		widgetInfo(wb,systemIds,companyId);
		//portal系统的WIDGET_ROLE
		widgetRoleInfo(wb,systemIds,companyId);
		//portal系统的WIDGET_PARAMETER
		widgetParameterInfo(wb,systemIds,companyId);
	}
	/**
	 * 导出平台门户信息
	 * @param fileOut
	 * @param systemIds
	 */
	public void exportPortal(OutputStream fileOut,String systemIds,Long companyId){
		//将公司id放入线程变量
		setCompanyId();
		HSSFWorkbook wb;
		wb = new HSSFWorkbook();
		//导出portal
		portalInfo(wb,systemIds,companyId);
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	/**
	 * 导入系统
	 */
	public void importSystem(File file,String imatrixIp,String imatrixPort,String imatrixName){
		//将公司id放入线程，即使此时没有公司，否则parameterUtil会报空指针异常
		setCompanyId();
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_BUSINESS_SYSTEM']");
		Map<String,Integer> map=getIdentifier(confs);
		//创建者姓名,创建者id,创建时间,公司id
		FileInputStream fis=null;
 		InputStreamReader fr=null;
 		BufferedReader br=null;
 		try{
 			fis=new FileInputStream(file);
 			HSSFWorkbook wb=new HSSFWorkbook(fis);
 			HSSFSheet sheet=wb.getSheet("ACS_BUSINESS_SYSTEM");
 			int firstRowNum = sheet.getFirstRowNum();
 			int rowNum=sheet.getLastRowNum();
 			for(int i=firstRowNum+1;i<=rowNum;i++){
 				if(sheet.getRow(i)!=null){
 					HSSFRow row =sheet.getRow(i);
 					addSystem(confs,row,map,wb,imatrixIp,imatrixPort,imatrixName);
 				}
 			}
 		}catch (FileNotFoundException e) {
 			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
 			try{
	 			if(br!=null)br.close();
	 			if(fr!=null)fr.close();
	 			if(fis!=null)fis.close();
 			}catch(IOException ep){
 				log.debug(PropUtils.getExceptionInfo(ep));
 			}
 		}
	}
	
	private void addSystem(List<DataSheetConfig> confs,HSSFRow row,Map<String,Integer> map,HSSFWorkbook wb,String imatrixIp,String imatrixPort,String imatrixName){
		String systemCode=row.getCell(map.get("code")).getStringCellValue();//系统编码
		BusinessSystem system=businessSystemManager.getSystemBySystemCode(systemCode);
		if(system==null){
			system=new BusinessSystem();
		}
		system.setTs(new Timestamp(new Date().getTime()));
		for(int i=0;i<confs.size();i++){
			DataSheetConfig conf=confs.get(i);
			if(!conf.isIgnore()){
				String fieldName=conf.getFieldName();
				String value=row.getCell(i).getStringCellValue();
				if(row.getCell(i)!=null){
					value=row.getCell(i).getStringCellValue();
				}
				if(StringUtils.isNotEmpty(value)){//导入数据
					setValue(system,fieldName,conf.getDataType(),value,conf.getEnumName());
				}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
					setValue(system,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
				}
			}
		}
		if(system.getImatrixable()!=null&&system.getImatrixable()){//如果是平台系统
			if(StringUtils.isEmpty(imatrixName)){
				system.setPath("http://"+StringUtils.trim(imatrixIp)+":"+StringUtils.trim(imatrixPort)+"/imatrix");
			}else{
				system.setPath("http://"+StringUtils.trim(imatrixIp)+":"+StringUtils.trim(imatrixPort)+"/"+imatrixName);
			}
		}else{
			if(StringUtils.isNotEmpty(system.getParentCode())){//如果父系统编码存在
				if(PropUtils.isBasicSystem("/"+system.getCode())){//如果是底层平台系统的子系统
					if(StringUtils.isEmpty(imatrixName)){
						system.setPath("http://"+StringUtils.trim(imatrixIp)+":"+StringUtils.trim(imatrixPort)+"/imatrix"+"/"+system.getCode());
					}else{
						system.setPath("http://"+StringUtils.trim(imatrixIp)+":"+StringUtils.trim(imatrixPort)+"/"+imatrixName+"/"+system.getCode());
					}
				}else{//如果不是底层平台的子系统，即用户自己创建的主子系统
					system.setPath("http://"+StringUtils.trim(imatrixIp)+":"+StringUtils.trim(imatrixPort)+"/"+system.getParentCode()+"/"+system.getCode());
				}
			}else{//如果父系统编码不存在，如ems
				system.setPath("http://"+StringUtils.trim(imatrixIp)+":"+StringUtils.trim(imatrixPort)+"/"+system.getCode());
			}
		}
		businessSystemManager.saveBusiness(system, false);
	}
	
	public void importPortal(File file,Long companyId){
		FileInputStream fis=null;
		InputStreamReader fr=null;
		BufferedReader br=null;
		try{
			fis=new FileInputStream(file);
			HSSFWorkbook wb=new HSSFWorkbook(fis);
			List<DataSheetConfig> pconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='PORTAL_WEBPAGE']");
			List<DataSheetConfig> wconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='PORTAL_WIDGET']");
			Map<String,Integer> wmap=getIdentifier(wconfs);
			List<DataSheetConfig> wrconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='PORTAL_WIDGET_ROLE']");
			Map<String,Integer> wrmap=getIdentifier(wrconfs);
			List<DataSheetConfig> wpconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='PORTAL_WIDGET_PARAMETER']");
			Map<String,Integer> wpmap=getIdentifier(wpconfs);
			if(companyId==null){
				List<Company> companys=companyManager.getCompanys();
				for(Company company:companys){
					addPortalInfo(wb,company.getCompanyId(),pconfs,wconfs, wmap,
							wrconfs, wrmap,wpconfs, wpmap);
				}
			}else{
				addPortalInfo(wb,companyId,pconfs,wconfs, wmap,
						wrconfs, wrmap,wpconfs, wpmap);
			}
			clearCompanyId();
		}catch (FileNotFoundException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			try{
				if(br!=null)br.close();
				if(fr!=null)fr.close();
				if(fis!=null)fis.close();
			}catch(IOException ep){
				log.debug(PropUtils.getExceptionInfo(ep));
			}
		}
	}
	
	private void addPortalInfo(HSSFWorkbook wb,Long companyId,List<DataSheetConfig> pconfs,List<DataSheetConfig> wconfs,Map<String,Integer> wmap,
			List<DataSheetConfig> wrconfs,Map<String,Integer> wrmap,List<DataSheetConfig> wpconfs,Map<String,Integer> wpmap){
		ThreadParameters parameters=new ThreadParameters(companyId,null);
		ParameterUtils.setParameters(parameters);
		//导入portal信息
		addWebpage(wb,pconfs);
		addWiget(wb,wconfs,wmap);
		addWidgetRole(wb,wrconfs,wrmap);
		addWidgetParameter(wb,wpconfs,wpmap);
	}
	
	
	private void addWebpage(HSSFWorkbook wb,List<DataSheetConfig> pconfs){
		HSSFSheet sheet=wb.getSheet("PORTAL_WEBPAGE");
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			if(sheet.getRow(i)!=null){
				HSSFRow prow =sheet.getRow(i);
				Webpage webpage=webpageDao.getWebpage();
				if(webpage==null) webpage=new Webpage();
				for(int j=0;j<pconfs.size();j++){
					DataSheetConfig conf=pconfs.get(j);
					if(!conf.isIgnore()){
						String fieldName=conf.getFieldName();
						String value=null;
						if(prow.getCell(j)!=null){
							value=prow.getCell(j).getStringCellValue();
						}
						if(StringUtils.isNotEmpty(value)){//导入数据
							setValue(webpage,fieldName,conf.getDataType(),value,conf.getEnumName());
						}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
							setValue(webpage,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
						}
					}
				}
				webpageDao.save(webpage);
			}
		}
		
	}
	
	private void addWiget(HSSFWorkbook wb,List<DataSheetConfig> pconfs,Map<String,Integer> map){
		HSSFSheet sheet=wb.getSheet("PORTAL_WIDGET");
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			if(sheet.getRow(i)!=null){
				HSSFRow prow =sheet.getRow(i);
				String widgetCode=prow.getCell(map.get("code")).getStringCellValue();
				Widget widget=widgetDao.getWidgetByCode(widgetCode);
				if(widget==null)widget=new Widget();
				for(int j=0;j<pconfs.size();j++){
					DataSheetConfig conf=pconfs.get(j);
					if(!conf.isIgnore()){
						String fieldName=conf.getFieldName();
						String value=null;
						if(prow.getCell(j)!=null){
							value=prow.getCell(j).getStringCellValue();
						}
						if(SYSTEM_CODE.equals(fieldName)){
							BusinessSystem bs=businessSystemManager.getSystemBySystemCode(value);
							if(bs!=null){
								widget.setSystemCode(bs.getCode());
							}
						}else{
							if(StringUtils.isNotEmpty(value)){//导入数据
								setValue(widget,fieldName,conf.getDataType(),value,conf.getEnumName());
							}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
								setValue(widget,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
							}
						}
					}
				}
				widgetDao.save(widget);
				
			}
		}
	}
	
	private void addWidgetRole(HSSFWorkbook wb,List<DataSheetConfig> pconfs,Map<String,Integer> map){
		HSSFSheet sheet=wb.getSheet("PORTAL_WIDGET_ROLE");
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			if(sheet.getRow(i)!=null){
				HSSFRow prow =sheet.getRow(i);
				String sysCode=prow.getCell(map.get(SYSTEM_CODE)).getStringCellValue();
				String widgetCode=prow.getCell(map.get("widgetCode")).getStringCellValue();
				Widget w=null;Role role=null;
				BusinessSystem bs= businessSystemManager.getSystemBySystemCode(sysCode);
				if(bs!=null){
					if(StringUtils.isNotEmpty(widgetCode)){
						w=widgetDao.getWidgetByCode(widgetCode);
						String roleCode=prow.getCell(map.get("roleCode")).getStringCellValue();
						role=standardRoleManager.getStandarRoleByCode(roleCode, acsUtils.getSystemsByCode(w.getSystemCode()).getId());
					}
				}
				if(role!=null&&w!=null){
					WidgetRole wr=null;
					wr=widgetRoleDao.getWidgetRole(role.getId(), w.getId());
					if(wr==null){
						wr=new WidgetRole();
					}
					wr.setRoleId(role.getId());
					wr.setWidgetId(w.getId());
					widgetRoleDao.save(wr);
				}
			}
		}
	}
	
	private void addWidgetParameter(HSSFWorkbook wb,List<DataSheetConfig> pconfs,Map<String,Integer> map){
		HSSFSheet sheet=wb.getSheet("PORTAL_WIDGET_PARAMETER");
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			if(sheet.getRow(i)!=null){
				HSSFRow prow =sheet.getRow(i);
				String code="";
				String widgetCode="";
				if(prow.getCell(map.get("code"))!=null){
					code=prow.getCell(map.get("code")).getStringCellValue();
				}
				if(prow.getCell(map.get("widgetCode"))!=null){
					widgetCode=prow.getCell(map.get("widgetCode")).getStringCellValue();
				}
				Widget widget=widgetDao.getWidgetByCode(widgetCode);
				if(widget!=null){
					WidgetParameter param=widgetParameterDao.getWidgetParameterByCode(code,widget.getId());
					if(param==null)param=new WidgetParameter();
					for(int j=0;j<pconfs.size();j++){
						DataSheetConfig conf=pconfs.get(j);
						if(!conf.isIgnore()){
							String fieldName=conf.getFieldName();
							String value=null;
							if(prow.getCell(j)!=null){
								value=prow.getCell(j).getStringCellValue();
							}
							if("widgetCode".equals(fieldName)){
								param.setWidget(widget);
							}else if("optionCode".equals(conf.getFieldName())){
								OptionGroup group=optionGroupManager.getOptionGroupByCode(value);
								if(group!=null){
									param.setOptionGroupId(group.getId());
								}
							}else{
								if(StringUtils.isNotEmpty(value)){//导入数据
									setValue(param,fieldName,conf.getDataType(),value,conf.getEnumName());
								}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
									setValue(param,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
								}
							}
						}
					}
					widgetParameterDao.save(param);
				}
			}
		}
	}
	
	/**
	 * 导出资源组及资源
	 * @param fileOut
	 * @param systemIds
	 */
	public void exportFunGroup(OutputStream fileOut,Long systemId,String funGroupIds){
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_FUNCTION_GROUP']");
		List<DataSheetConfig> funConfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_FUNCTION']");
		wb = new HSSFWorkbook();
		//系统表
		HSSFSheet sheet=wb.createSheet("ACS_FUNCTION_GROUP");
        HSSFRow row = sheet.createRow(0);
        getFileHead(wb,row,confs);
        //资源组表
        HSSFSheet funSheet=wb.createSheet("ACS_FUNCTION");
        HSSFRow funRow = funSheet.createRow(0);
        getFileHead(wb,funRow,funConfs);
        
        if(StringUtils.isEmpty(funGroupIds)){//导出该系统中所有资源组及资源
        	List<FunctionGroup> funGroups=functionGroupManager.getFunsGroupsBySystem(systemId);
    		for(FunctionGroup funGroup:funGroups){
    			funcGroupInfo(funGroup,sheet,funSheet,confs,funConfs);
    		}
    		//导出没有资源组的资源
    		List<Function> funs=functionManager.getUnGroupFunctions(systemId);
    		functionInfo(funs,funSheet,funConfs);
        }else{//导出选中的资源组及资源
        	String[] ids=funGroupIds.split(",");
        	for(String id:ids){
        		if(StringUtils.isNotEmpty(id)){
        			FunctionGroup funGroup=functionGroupManager.getFunctionGroup(Long.parseLong(id));
        			funcGroupInfo(funGroup,sheet,funSheet,confs,funConfs);
        		}
        	}
        }
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	
	private void funcGroupInfo(FunctionGroup funGroup,HSSFSheet sheet,HSSFSheet funSheet,List<DataSheetConfig> confs,List<DataSheetConfig> funConfs){
		if(funGroup!=null){
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
				DataSheetConfig conf=confs.get(i);
				if(!conf.isIgnore()){
					String fieldName=conf.getFieldName();
					if(SYSTEM_CODE.equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						if(funGroup.getBusinessSystem()!=null){
							cell.setCellValue(funGroup.getBusinessSystem().getCode());
						}else{
							cell.setCellValue("");
						}
					}else{
        				setFieldValue(conf,i,rowi,funGroup);
        			}
				}
			}
			List<Function> funs=functionManager.getFunctionsByGroup(funGroup.getId());
			functionInfo(funs,funSheet,funConfs);
		}
	}
	
	private void functionInfo(List<Function> funs,HSSFSheet funSheet,List<DataSheetConfig> funConfs){
		for(Function fun:funs){
			HSSFRow rowi = funSheet.createRow(funSheet.getLastRowNum()+1);
			for(int i=0;i<funConfs.size();i++){
				DataSheetConfig conf=funConfs.get(i);
				if(!conf.isIgnore()){
					String fieldName=conf.getFieldName();
					if(SYSTEM_CODE.equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						if(fun.getBusinessSystem()!=null){
							cell.setCellValue(fun.getBusinessSystem().getCode());
						}else{
							cell.setCellValue("");
						}
					}else if(FUNCTION_GROUP_CODE.equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						if(fun.getFunctionGroup()!=null){
							cell.setCellValue(fun.getFunctionGroup().getCode());
						}else{
							cell.setCellValue("");
						}
					}else{
	    				setFieldValue(conf,i,rowi,fun);
	    			}
				}
			}
		}
	}
	
	/**
	 * 导入资源组及资源
	 */
	public void importFunGroup(File file,Long systemId){
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_FUNCTION_GROUP']");
		List<DataSheetConfig> funConfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_FUNCTION']");
		Map<String,Integer> map=getIdentifier(confs);
		Map<String,Integer> funMap=getIdentifier(funConfs);
		//创建者姓名,创建者id,创建时间,公司id
		FileInputStream fis=null;
 		InputStreamReader fr=null;
 		BufferedReader br=null;
 		try{
 			fis=new FileInputStream(file);
 			HSSFWorkbook wb=new HSSFWorkbook(fis);
 			HSSFSheet sheet=wb.getSheet("ACS_FUNCTION_GROUP");
 			int firstRowNum = sheet.getFirstRowNum();
 			int rowNum=sheet.getLastRowNum();
 			for(int i=firstRowNum+1;i<=rowNum;i++){
 				if(sheet.getRow(i)!=null){
 					HSSFRow row =sheet.getRow(i);
 					addFunGroup(confs,row,map,systemId);
 				}
 			}
 			HSSFSheet funSheet=wb.getSheet("ACS_FUNCTION");
 			int funFirstRowNum = funSheet.getFirstRowNum();
 			int funRowNum=funSheet.getLastRowNum();
 			for(int i=funFirstRowNum+1;i<=funRowNum;i++){
 				if(funSheet.getRow(i)!=null){
 					HSSFRow row =funSheet.getRow(i);
 					addFunction(funConfs,row,funMap,systemId);
 				}
 			}
 		} catch (FileNotFoundException e) {
 			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
 			try{
	 			if(br!=null)br.close();
	 			if(fr!=null)fr.close();
	 			if(fis!=null)fis.close();
 			}catch(IOException ep){
 				log.debug(PropUtils.getExceptionInfo(ep));
 			}
 		}
	}
	
	private void addFunGroup(List<DataSheetConfig> confs,HSSFRow row,Map<String,Integer> map,Long systemId){
		String funGroupCode=row.getCell(map.get("code")).getStringCellValue();//资源组编码
		FunctionGroup funGroup=functionGroupManager.getFuncGroupByCode(funGroupCode,systemId);
		if(funGroup==null)funGroup=new FunctionGroup();
		funGroup.setTs(new Timestamp(new Date().getTime()));
		for(int i=0;i<confs.size();i++){
			DataSheetConfig conf=confs.get(i);
			if(!conf.isIgnore()){
				String fieldName=conf.getFieldName();
				String value=null;
				if(row.getCell(i)!=null){
					value=row.getCell(i).getStringCellValue();
				}
				if(SYSTEM_CODE.equals(fieldName)){
					BusinessSystem system=businessSystemManager.getSystemBySystemCode(value);
					funGroup.setBusinessSystem(system);
				}else{
					if(StringUtils.isNotEmpty(value)){//导入数据
						setValue(funGroup,fieldName,conf.getDataType(),value,conf.getEnumName());
					}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
						setValue(funGroup,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
					}
				}
			}
		}
		functionGroupManager.saveFunGroup(funGroup);
	}
	private void addFunction(List<DataSheetConfig> confs,HSSFRow row,Map<String,Integer> map,Long systemId){
		String funPath=row.getCell(map.get("path")).getStringCellValue();//资源路径
		String funId=row.getCell(map.get("code")).getStringCellValue();//资源编码
		Function fun=functionManager.getFunctionByPath(funPath,systemId,funId);
		if(fun==null)fun=new Function();
		fun.setTs(new Timestamp(new Date().getTime()));
		for(int i=0;i<confs.size();i++){
			DataSheetConfig conf=confs.get(i);
			if(!conf.isIgnore()){
				String fieldName=conf.getFieldName();
				String value=null;
				if(row.getCell(i)!=null){
					value=row.getCell(i).getStringCellValue();
				}
				if(SYSTEM_CODE.equals(fieldName)){
					BusinessSystem system=businessSystemManager.getSystemBySystemCode(value);
					fun.setBusinessSystem(system);
				}else if(FUNCTION_GROUP_CODE.equals(fieldName)){
					FunctionGroup funGroup=functionGroupManager.getFuncGroupByCode(value,systemId);
					fun.setFunctionGroup(funGroup);
				}else{
					if(StringUtils.isNotEmpty(value)){//导入数据
						setValue(fun,fieldName,conf.getDataType(),value,conf.getEnumName());
					}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
						setValue(fun,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
					}
				}
			}
		}
		functionManager.saveFunction(fun);
	}
	
	/**
	 * 导出资源组及资源
	 * @param fileOut
	 * @param systemIds
	 */
	public void exportRole(OutputStream fileOut,Long systemId,String roleIds,Long companyId){
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_ROLE']");
		List<DataSheetConfig> roleFunConfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_ROLE_FUNCTION']");
		wb = new HSSFWorkbook();
		//系统表
		HSSFSheet sheet=wb.createSheet("ACS_ROLE");
        HSSFRow row = sheet.createRow(0);
        getFileHead(wb,row,confs);
        //资源组表
        HSSFSheet roleFunSheet=wb.createSheet("ACS_ROLE_FUNCTION");
        HSSFRow roleFunRow = roleFunSheet.createRow(0);
        getFileHead(wb,roleFunRow,roleFunConfs);
        
        if(StringUtils.isEmpty(roleIds)){//导出该系统中所有角色
        	List<Role> roles=standardRoleManager.getAllStandardRoleByCompany(systemId,null);
        	for(Role role:roles){
        		roleInfo(role,sheet,roleFunSheet,confs,roleFunConfs);
        	}
        }else{//导出选中的资源组及资源
        	String[] ids=roleIds.split(",");
        	for(String id:ids){
        		if(StringUtils.isNotEmpty(id)){
        			Role role=standardRoleManager.getStandardRole(Long.parseLong(id));
        			roleInfo(role,sheet,roleFunSheet,confs,roleFunConfs);
        		}
        	}
        }
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	/**
	 * 导出资源组及资源
	 * @param fileOut
	 * @param systemIds
	 */
	public void exportCompanyRole(OutputStream fileOut,Long systemId,String roleIds,Long companyId){
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_ROLE']");
		List<DataSheetConfig> roleFunConfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_ROLE_FUNCTION']");
		wb = new HSSFWorkbook();
		//系统表
		HSSFSheet sheet=wb.createSheet("ACS_ROLE");
		HSSFRow row = sheet.createRow(0);
		getFileHead(wb,row,confs);
		//资源组表
		HSSFSheet roleFunSheet=wb.createSheet("ACS_ROLE_FUNCTION");
		HSSFRow roleFunRow = roleFunSheet.createRow(0);
		getFileHead(wb,roleFunRow,roleFunConfs);
		
		List<Role> roles=standardRoleManager.getAllStandardRoleByCompany(systemId,companyId);
		for(Role role:roles){
			roleInfo(role,sheet,roleFunSheet,confs,roleFunConfs);
		}
		try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	
	private void roleInfo(Role role,HSSFSheet sheet,HSSFSheet roleFunSheet,List<DataSheetConfig> confs,List<DataSheetConfig> roleFunConfs){
		if(role!=null){
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
				DataSheetConfig conf=confs.get(i);
				if(!conf.isIgnore()){
					String fieldName=conf.getFieldName();
					if(SYSTEM_CODE.equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						if(role.getBusinessSystem()!=null){
							cell.setCellValue(role.getBusinessSystem().getCode());
						}else{
							cell.setCellValue("");
						}
					}else if(PARENT_ROLE_CODE.equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						if(role.getParentRole()!=null){
							cell.setCellValue(role.getParentRole().getCode());
						}else{
							cell.setCellValue("");
						}
					}else if("roleType".equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						if(role.getCompanyId()!=null){
							cell.setCellValue("CUSTOM");
						}else{
							cell.setCellValue("STANDARD");
						}
					}else{
        				setFieldValue(conf,i,rowi,role);
        			}
				}
			}
			roleFunctionInfo(role,roleFunSheet,roleFunConfs);
		}
	}
	
	private void roleFunctionInfo(Role role,HSSFSheet roleFunSheet,List<DataSheetConfig> roleFunConfs){
		List<Function> funs=standardRoleManager.getFunctionsByRole(role);
		for(Function fun:funs){
			HSSFRow rowi = roleFunSheet.createRow(roleFunSheet.getLastRowNum()+1);
			for(int i=0;i<roleFunConfs.size();i++){
				DataSheetConfig conf=roleFunConfs.get(i);
				if(!conf.isIgnore()){
					String fieldName=conf.getFieldName();
					if(ROLE_CODE.equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						cell.setCellValue(role.getCode());
					} else if(FUNCTION_PATH.equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						cell.setCellValue(fun.getPath());
					}else if("functionCode".equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						cell.setCellValue(fun.getCode());
					}else{
        				setFieldValue(conf,i,rowi,role);
        			}
				}
			}
		}
	}
	
	public void importRole(File file,Long systemId,Long companyId){
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_ROLE']");
		List<DataSheetConfig> roleFunConfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_ROLE_FUNCTION']");
		Map<String,Integer> map=getIdentifier(confs);
		Map<String,Integer> roleFunMap=getIdentifier(roleFunConfs);
		//创建者姓名,创建者id,创建时间,公司id
		FileInputStream fis=null;
 		InputStreamReader fr=null;
 		BufferedReader br=null;
 		try{
 			fis=new FileInputStream(file);
 			HSSFWorkbook wb=new HSSFWorkbook(fis);
 			HSSFSheet sheet=wb.getSheet("ACS_ROLE");
 			
 			HSSFSheet roleFunSheet=wb.getSheet("ACS_ROLE_FUNCTION");
 			//添加角色及角色资源关系
			if(companyId==null){
				List<Company> companys=companyManager.getCompanys();
				if(companys.size()<=0){
					addRoleInfo(sheet,confs,map,systemId,null);
					addRoleFunInfo(roleFunSheet,roleFunConfs,roleFunMap,systemId,null);
				}else{
					for(Company company:companys){
						addRoleInfo(sheet,confs,map,systemId,company.getCompanyId());
						addRoleFunInfo(roleFunSheet,roleFunConfs,roleFunMap,systemId,company.getCompanyId());
					}
					
				}
			}else{
				addRoleInfo(sheet,confs,map,systemId,companyId);
				addRoleFunInfo(roleFunSheet,roleFunConfs,roleFunMap,systemId,companyId);
			}
 		} catch (FileNotFoundException e) {
 			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
 			try{
	 			if(br!=null)br.close();
	 			if(fr!=null)fr.close();
	 			if(fis!=null)fis.close();
 			}catch(IOException ep){
 				log.debug(PropUtils.getExceptionInfo(ep));
 			}
 		}
	}
 		
 		private void addRoleInfo(HSSFSheet sheet,List<DataSheetConfig> confs,Map<String,Integer> map,Long systemId,Long companyId){
 			//添加角色
 			int firstRowNum = sheet.getFirstRowNum();
 			int rowNum=sheet.getLastRowNum();
 			for(int i=firstRowNum+1;i<=rowNum;i++){
 				if(sheet.getRow(i)!=null){
 					HSSFRow row =sheet.getRow(i);
 					addRole(confs,row,map,systemId,companyId);
 				}
 			}
 		}
 		
 		private void addRoleFunInfo(HSSFSheet roleFunSheet,List<DataSheetConfig> roleFunConfs,Map<String,Integer> roleFunMap,Long systemId,Long companyId){
 			//角色资源关系
 			int roleFunFirstRowNum = roleFunSheet.getFirstRowNum();
 			int roleFunRowNum=roleFunSheet.getLastRowNum();
 			for(int i=roleFunFirstRowNum+1;i<=roleFunRowNum;i++){
	 				if(roleFunSheet.getRow(i)!=null){
	 					HSSFRow row =roleFunSheet.getRow(i);
	 					addRoleFunction(roleFunConfs,row,roleFunMap,systemId,companyId);
	 				}
	 			}
 		}
	
	private void addRole(List<DataSheetConfig> confs,HSSFRow row,Map<String,Integer> map,Long systemId,Long companyId){
		String roleCode=row.getCell(map.get("code")).getStringCellValue();//角色编码
		Role role=null;
		//为了预防一个系统中有多个相同的角色
		if(companyId!=null){//导入在权限系统中创建的角色时
			role=standardRoleManager.getStandarRoleByCode(roleCode, systemId,companyId);
		}
		if(role==null){//导入在sales中创建的角色时
			role=standardRoleManager.getStandarRoleByCode(roleCode, systemId);
		}
		
		
		if(role==null)role=new Role();
		role.setTs(new Timestamp(new Date().getTime()));
		for(int i=0;i<confs.size();i++){
			DataSheetConfig conf=confs.get(i);
			if(!conf.isIgnore()){
				String fieldName=conf.getFieldName();
				String value=null;
				if(row.getCell(i)!=null){
					value=row.getCell(i).getStringCellValue();
				}
				if(SYSTEM_CODE.equals(fieldName)){
					BusinessSystem system=businessSystemManager.getSystemBySystemCode(value);
					role.setBusinessSystem(system);
				}else if(PARENT_ROLE_CODE.equals(fieldName)){
					Role parentRole=standardRoleManager.getStandarRoleByCode(value, systemId);
					role.setParentRole(parentRole);
				}else if("roleType".equals(fieldName)){
					if("CUSTOM".equals(value)){
						role.setCompanyId(companyId);
					}
				}else{
					if(StringUtils.isNotEmpty(value)){//导入数据
						setValue(role,fieldName,conf.getDataType(),value,conf.getEnumName());
					}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
						setValue(role,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
					}
				}
			}
		}
		standardRoleManager.saveStandardRole(role);
	}
	
	private void addRoleFunction(List<DataSheetConfig> confs,HSSFRow row,Map<String,Integer> map,Long systemId,Long companyId){
		String roleCode=row.getCell(map.get(ROLE_CODE)).getStringCellValue();//角色编码
		String functionPath=row.getCell(map.get(FUNCTION_PATH)).getStringCellValue();//资源路径
		String funId=row.getCell(map.get("functionCode")).getStringCellValue();//资源编码
		Role role=null;
		//为了预防一个系统中有多个相同的角色
		if(companyId!=null){//导入在权限系统中创建的角色时
			role=standardRoleManager.getStandarRoleByCode(roleCode, systemId,companyId);
		}
		if(role==null){//导入在sales中创建的角色时
			role=standardRoleManager.getStandarRoleByCode(roleCode, systemId);
		}
		Function fun=functionManager.getFunctionByPath(functionPath,systemId,funId);
		if(role!=null&&fun!=null){
			RoleFunction roleFun=standardRoleManager.getRoleFunction(roleCode, functionPath,funId);
			if(roleFun==null)roleFun=new RoleFunction();
			roleFun.setTs(new Timestamp(new Date().getTime()));
			roleFun.setRole(role);
			roleFun.setFunction(fun);
			for(int i=0;i<confs.size();i++){
				DataSheetConfig conf=confs.get(i);
				if(!conf.isIgnore()){
					String fieldName=conf.getFieldName();
					String value=null;
					if(row.getCell(i)!=null){
						value=row.getCell(i).getStringCellValue();
					}
					if(!ROLE_CODE.equals(fieldName)&&!FUNCTION_PATH.equals(fieldName)){
						if(StringUtils.isNotEmpty(value)){//导入数据
							setValue(roleFun,fieldName,conf.getDataType(),value,conf.getEnumName());
						}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
							setValue(roleFun,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
						}
					}
				}
			}
			roleFun.setCompanyId(role.getCompanyId());
			standardRoleManager.saveRoleFunction(roleFun);
		}
	}
	/**
	 * 导出产品和价格策略
	 * @param fileOut
	 * @param productIds
	 */
	public void exportProduct(OutputStream fileOut,String productIds){
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_PRODUCTS']");
		List<DataSheetConfig> priceConfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_PRICE_POLICYS']");
		wb = new HSSFWorkbook();
		//产品表
		HSSFSheet sheet=wb.createSheet("ACS_PRODUCTS");
        HSSFRow row = sheet.createRow(0);
        getFileHead(wb,row,confs);
        //价格策略表
        HSSFSheet priceSheet=wb.createSheet("ACS_PRICE_POLICYS");
        HSSFRow priceRow = priceSheet.createRow(0);
        getFileHead(wb,priceRow,priceConfs);
        
        if(StringUtils.isEmpty(productIds)){//导出所有的产品及其所有价格策略
        	List<Product> products=productManager.getAllProduct();
        	for(Product product:products){
        		productInfo(product,sheet,priceSheet,confs,priceConfs);
    		}
        }else{//导出选择的产品及其所有价格策略
        	String[] ids=productIds.split(",");
        	for(String id:ids){
        		if(StringUtils.isNotEmpty(id)){
        			Product product=productManager.getProduct(Long.parseLong(id));
        			productInfo(product,sheet,priceSheet,confs,priceConfs);
        		}
        	}
        }
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	
	public void exportProductBySystem(OutputStream fileOut,String systemIds){
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_PRODUCTS']");
		List<DataSheetConfig> priceConfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_PRICE_POLICYS']");
		wb = new HSSFWorkbook();
		//产品表
		HSSFSheet sheet=wb.createSheet("ACS_PRODUCTS");
        HSSFRow row = sheet.createRow(0);
        getFileHead(wb,row,confs);
        //价格策略表
        HSSFSheet priceSheet=wb.createSheet("ACS_PRICE_POLICYS");
        HSSFRow priceRow = priceSheet.createRow(0);
        getFileHead(wb,priceRow,priceConfs);
        
        if(StringUtils.isEmpty(systemIds)){
        	List<Product> products=productManager.getAllProduct();
        	for(Product product:products){
        		productInfo(product,sheet,priceSheet,confs,priceConfs);
    		}
        	
        }else{
        	//导出选中系统的产品及其所有价格策略
        	String[] sysIds=systemIds.split(",");
        	for(String systemId:sysIds){
        		List<Product> products=productManager.getProductBySystem(Long.parseLong(systemId));
        		for(Product product:products){
        			productInfo(product,sheet,priceSheet,confs,priceConfs);
        		}
        	}
        }
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	
	private void productInfo(Product product,HSSFSheet productSheet,HSSFSheet priceSheet,List<DataSheetConfig> productConfs,List<DataSheetConfig> priceConfs){
		if(product!=null){
			HSSFRow rowi = productSheet.createRow(productSheet.getLastRowNum()+1);
			for(int i=0;i<productConfs.size();i++){
				DataSheetConfig conf=productConfs.get(i);
				if(!conf.isIgnore()){
					String fieldName=conf.getFieldName();
					if(SYSTEM_CODE.equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						if(product.getSystemId()!=null){
							BusinessSystem system=businessSystemManager.getBusiness(product.getSystemId());
							cell.setCellValue(system.getCode());
						}else{
							cell.setCellValue("");
						}
					}else{
        				setFieldValue(conf,i,rowi,product);
        			}
				}
			}
			pricePolicyInfo(product,priceSheet,priceConfs);
		}
	}
	private void pricePolicyInfo(Product product,HSSFSheet priceSheet,List<DataSheetConfig> priceConfs){
		List<PricePolicy> pps=pricePolicyManager.getPricePolicyByProduct(product.getId());
		for(PricePolicy pp:pps){
			HSSFRow rowi = priceSheet.createRow(priceSheet.getLastRowNum()+1);
			for(int i=0;i<priceConfs.size();i++){
				DataSheetConfig conf=priceConfs.get(i);
				if(!conf.isIgnore()){
					String fieldName=conf.getFieldName();
					if(SYSTEM_CODE.equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						if(pp.getProduct()!=null&&pp.getProduct().getSystemId()!=null){
							BusinessSystem system=businessSystemManager.getBusiness(pp.getProduct().getSystemId());
							cell.setCellValue(system.getCode());
						}else{
							cell.setCellValue("");
						}
					}else if("productName".equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						if(pp.getProduct()!=null){
							cell.setCellValue(pp.getProduct().getProductName());
						}else{
							cell.setCellValue("");
						}
					}else if("productVersion".equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						if(pp.getProduct()!=null){
							cell.setCellValue(pp.getProduct().getVersion());
						}else{
							cell.setCellValue("");
						}
					}else{
        				setFieldValue(conf,i,rowi,pp);
        			}
				}
			}
		}
	}
	/**
	 * 导入产品和价格策略
	 * @param file
	 */
	public void importProduct(File file){
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_PRODUCTS']");
		List<DataSheetConfig> priceConfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_PRICE_POLICYS']");
		Map<String,Integer> map=getIdentifier(confs);
		Map<String,Integer> priceMap=getIdentifier(priceConfs);
		FileInputStream fis=null;
 		InputStreamReader fr=null;
 		BufferedReader br=null;
 		try{
 			fis=new FileInputStream(file);
 			HSSFWorkbook wb=new HSSFWorkbook(fis);
 			HSSFSheet sheet=wb.getSheet("ACS_PRODUCTS");
 			//添加产品及产品策略
 			int firstRowNum = sheet.getFirstRowNum();
 			int rowNum=sheet.getLastRowNum();
 			for(int i=firstRowNum+1;i<=rowNum;i++){
	 				if(sheet.getRow(i)!=null){
	 					HSSFRow row =sheet.getRow(i);
	 					addProduct(confs,row,map);
	 				}
	 			}
 			
 			HSSFSheet priceSheet=wb.getSheet("ACS_PRICE_POLICYS");
 			int priceFirstRowNum = priceSheet.getFirstRowNum();
 			int priceRowNum=priceSheet.getLastRowNum();
 			
 				for(int i=priceFirstRowNum+1;i<=priceRowNum;i++){
 	 				if(priceSheet.getRow(i)!=null){
 	 					HSSFRow row =priceSheet.getRow(i);
 	 					addPrice(priceConfs,row,priceMap);
 	 				}
 	 			}
 		} catch (FileNotFoundException e) {
 			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
 			try{
	 			if(br!=null)br.close();
	 			if(fr!=null)fr.close();
	 			if(fis!=null)fis.close();
 			}catch(IOException ep){
 				log.debug(PropUtils.getExceptionInfo(ep));
 			}
 		}
	}
	
	private void addProduct(List<DataSheetConfig> confs,HSSFRow row,Map<String,Integer> map){
		String productName=row.getCell(map.get("productName")).getStringCellValue();//产品名称
		String version=row.getCell(map.get("version")).getStringCellValue();//产品版本
		String systemCode=row.getCell(map.get(SYSTEM_CODE)).getStringCellValue();//系统编码
		BusinessSystem system=businessSystemManager.getSystemBySystemCode(systemCode);
		Product product=null;
		if(system!=null){
			product=productManager.getProduct(productName, version, system.getId());
		}
		if(product==null){
			product=new Product();
		}
		product.setTs(new Timestamp(new Date().getTime()));
		for(int i=0;i<confs.size();i++){
			DataSheetConfig conf=confs.get(i);
			if(!conf.isIgnore()){
				String fieldName=conf.getFieldName();
				String value=null;
				if(row.getCell(i)!=null){
					value=row.getCell(i).getStringCellValue();
				}
				if(SYSTEM_CODE.equals(fieldName)){
					BusinessSystem bsystem=businessSystemManager.getSystemBySystemCode(value);
					if(bsystem!=null)product.setSystemId(bsystem.getId());
				}else{
					if(StringUtils.isNotEmpty(value)){//导入数据
						setValue(product,fieldName,conf.getDataType(),value,conf.getEnumName());
					}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
						setValue(product,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
					}
				}
			}
		}
		if(product.getSystemId()!=null)productManager.saveProduct(product);
	}
	private void addPrice(List<DataSheetConfig> confs,HSSFRow row,Map<String,Integer> map){
		String productName=row.getCell(map.get("productName")).getStringCellValue();//产品名称
		String version=row.getCell(map.get("productVersion")).getStringCellValue();//产品版本
		String systemCode=row.getCell(map.get(SYSTEM_CODE)).getStringCellValue();//系统编码
		BusinessSystem system=businessSystemManager.getSystemBySystemCode(systemCode);
		Product product=null;
		if(system!=null){
			product=productManager.getProduct(productName, version, system.getId());
		}
		if(product!=null){
			PricePolicy pp=new PricePolicy();
			pp.setProduct(product);
			pp.setTs(new Timestamp(new Date().getTime()));
			for(int i=0;i<confs.size();i++){
				DataSheetConfig conf=confs.get(i);
				if(!conf.isIgnore()){
					String fieldName=conf.getFieldName();
					String value=null;
					if(row.getCell(i)!=null){
						value=row.getCell(i).getStringCellValue();
					}
					if(StringUtils.isNotEmpty(value)){//导入数据
						setValue(pp,fieldName,conf.getDataType(),value,conf.getEnumName());
					}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
						setValue(pp,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
					}
				}
			}
			pricePolicyManager.savePricePolicy(pp);
		}
	}
	/**
	 * 导出流程类型、流程定义、流程文件xml
	 */
	public void exportDefinitionType(OutputStream fileOut,Long companyId){
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='WF_TYPE']");
		wb = new HSSFWorkbook();
    	HSSFSheet sheet=wb.createSheet("WF_TYPE");
        HSSFRow row = sheet.createRow(0);        
        getFileHead(wb,row,confs);
        
        List<WorkflowType> types=workflowTypeManager.getAllWorkflowType(companyId);
        for(WorkflowType type:types){
        	definitionTypeInfo(type,sheet,confs);
        }
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	private void definitionTypeInfo(WorkflowType type,HSSFSheet sheet,List<DataSheetConfig> confs){
		if(type!=null){
			//表的信息
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
        		DataSheetConfig conf=confs.get(i);
				setFieldValue(conf,i,rowi,type);
    		}
		}
	}
	
	/**
	 * 导出流程定义、流程文件xml
	 */
	public void exportDefinition(OutputStream fileOut,Long companyId,String systemIds){
		HSSFWorkbook wb;
		List<DataSheetConfig> defconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='WF_DEFINITION']");
		List<DataSheetConfig> defileconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='WF_DEFINITION_FILE']");
		wb = new HSSFWorkbook();
        HSSFSheet defsheet=wb.createSheet("WF_DEFINITION");
        HSSFRow defrow = defsheet.createRow(0);        
        getFileHead(wb,defrow,defconfs);
        
        HSSFSheet defilesheet=wb.createSheet("WF_DEFINITION_FILE");
        HSSFRow defilerow = defilesheet.createRow(0);        
        getFileHead(wb,defilerow,defileconfs);
        List<WorkflowDefinition> defs=workflowDefinitionManager.getWfDefinitions(companyId, systemIds);
    		//获得导出的根节点
    		String[] rootPaths=getRootPath();
    		String exportRootPath=rootPaths[0];
    		
		//创建导出文件夹，导出的流程定义文件暂存的位置
		File folder = new File(exportRootPath+"/"+FLOW_CHAR_PATH);
		if(!folder.exists()){
			folder.mkdirs();
		}
        
        int n=1;
		for(int i=0;i<defs.size();i++){
			if(i!=0){
				if(defs.get(i-1).getCode().equals(defs.get(i).getCode())){
					n++;
				}else{
					n=1;
				}
			}
			definitionInfo(defs.get(i),defsheet,defconfs,defilesheet,defileconfs,n+"",exportRootPath);
		}
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	private void definitionInfo(WorkflowDefinition definition,HSSFSheet sheet,List<DataSheetConfig> confs,HSSFSheet defilesheet,List<DataSheetConfig> defileconfs,String version, String exportRootPath){
		if(definition!=null){
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
				DataSheetConfig conf=confs.get(i);
				if(!conf.isIgnore()){
					String fieldName=conf.getFieldName();
					if(SYSTEM_CODE.equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						BusinessSystem system=businessSystemManager.getBusiness(definition.getSystemId());
						if(system!=null){
							cell.setCellValue(system.getCode());
						}else{
							cell.setCellValue("");
						}
					}else if("typeCode".equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						WorkflowType type=workflowTypeManager.getWorkflowTypeById(definition.getTypeId(),definition.getCompanyId());
						if(type!=null)cell.setCellValue(type.getCode());
					}else if("version".equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						cell.setCellValue(version);
					}else{
        				setFieldValue(conf,i,rowi,definition);
        			}
				}
			}
			WorkflowDefinitionFile file=workflowDefinitionManager.getWfDefinitionFileByWfdId(definition.getId(),definition.getCompanyId());
			definitionFileInfo(file,defilesheet,defileconfs,version, exportRootPath);
		}
	}
	private void definitionFileInfo(WorkflowDefinitionFile definitionFile,HSSFSheet sheet,List<DataSheetConfig> confs,String version, String exportRootPath){
		if(definitionFile!=null){
			WorkflowDefinition definition=workflowDefinitionManager.getWfDefinition(definitionFile.getWfDefinitionId());
			if(definition!=null){
				BusinessSystem system=businessSystemManager.getBusiness(definition.getSystemId());
				HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
				for(int i=0;i<confs.size();i++){
					DataSheetConfig conf=confs.get(i);
					if(!conf.isIgnore()){
						String fieldName=conf.getFieldName();
						if("defCode".equals(fieldName)){
							HSSFCell cell = rowi.createCell(i);
							cell.setCellValue(definition.getCode());
						}else if("defVersion".equals(fieldName)){
							HSSFCell cell = rowi.createCell(i);
							cell.setCellValue(version);
						}else if(SYSTEM_CODE.equals(fieldName)){
							HSSFCell cell = rowi.createCell(i);
							cell.setCellValue(system.getCode());
						}else if("document".equals(fieldName)){
							if(StringUtils.isNotEmpty(definitionFile.getDocument())){
								File file = new File(exportRootPath+"/"+FLOW_CHAR_PATH+"/"+definition.getCode()+"#"+version+"#"+system.getCode()+".xml");
								try {
									FileUtils.writeStringToFile(file, definitionFile.getDocument(), "utf-8");
								}catch (Exception e) {
									log.debug(PropUtils.getExceptionInfo(e));
								}
							}
						}else{
							setFieldValue(conf,i,rowi,definitionFile);
						}
					}
				}
			}
		}
	}
	/**
	 * 用户上下级关系
	 */
	public void exportRank(OutputStream fileOut,Long companyId){
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_SUPERIOR']");
		List<DataSheetConfig> bruconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_SUBORDINATE']");
		wb = new HSSFWorkbook();
    	HSSFSheet sheet=wb.createSheet("BS_SUPERIOR");
        HSSFRow row = sheet.createRow(0);
        
        getFileHead(wb,row,confs);
        
        HSSFSheet bruSheet=wb.createSheet("BS_SUBORDINATE");
        HSSFRow bruRow = bruSheet.createRow(0);
        
        getFileHead(wb,bruRow,bruconfs);
        List<Superior> ranks=rankManager.getRanks(companyId);;
        for(Superior rank:ranks){
        	rankInfo(rank,sheet,bruSheet,confs,bruconfs);
        }
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	private void rankInfo(Superior rank,HSSFSheet sheet,HSSFSheet bruSheet,List<DataSheetConfig> confs,List<DataSheetConfig> bruconfs){
		if(rank!=null){
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
				DataSheetConfig conf=confs.get(i);
				if(!conf.isIgnore()){
					String fieldName=conf.getFieldName();
					if(SYSTEM_CODE.equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						BusinessSystem system=businessSystemManager.getBusiness(rank.getSystemId());
						if(system!=null){
							cell.setCellValue(system.getCode());
						}else{
							cell.setCellValue("");
						}
					}else{
        				setFieldValue(conf,i,rowi,rank);
        			}
				}
			}
			List<Subordinate> rankUsers=rankUserManager.getDataDictRankUsersByRank(rank.getId());
			for(Subordinate rankUser:rankUsers){
				rankUserInfo(rankUser,bruSheet,bruconfs);
			}
		}
	}
	private void rankUserInfo(Subordinate rankUser,HSSFSheet bruSheet,List<DataSheetConfig> bruconfs){
		if(rankUser!=null){
			HSSFRow rowi = bruSheet.createRow(bruSheet.getLastRowNum()+1);
			for(int i=0;i<bruconfs.size();i++){
				DataSheetConfig conf=bruconfs.get(i);
				if(!conf.isIgnore()){
					String fieldName=conf.getFieldName();
					if(SYSTEM_CODE.equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						BusinessSystem system=businessSystemManager.getBusiness(rankUser.getSystemId());
						if(system!=null){
							cell.setCellValue(system.getCode());
						}else{
							cell.setCellValue("");
						}
					}else if("infoIdenty".equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						String value="";
						if(rankUser.getSubordinateType()==SubordinateType.DEPARTMENT){
							Department dept=departmentManager.getDepartment(rankUser.getTargetId());
							value=dept.getCode();
						}else if(rankUser.getSubordinateType()==SubordinateType.WORKGROUP){
							Workgroup group=workGroupManager.getWorkGroup(rankUser.getTargetId());
							value=group.getCode();
						}
						cell.setCellValue(value);
					}else if("rankTitle".equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						cell.setCellValue(rankUser.getDataDictionaryRank().getTitle());
					}else{
        				setFieldValue(conf,i,rowi,rankUser);
        			}
				}
			}
		}
	}
	/**
	 * 导出选项组
	 */
	public void exportOption(OutputStream fileOut,String systemIds,Long companyId){
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_OPTION_GROUP']");
		List<DataSheetConfig> bruconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_OPTION']");
		wb = new HSSFWorkbook();
    	HSSFSheet sheet=wb.createSheet("BS_OPTION_GROUP");
        HSSFRow row = sheet.createRow(0);
        
        getFileHead(wb,row,confs);
        
        HSSFSheet bruSheet=wb.createSheet("BS_OPTION");
        HSSFRow bruRow = bruSheet.createRow(0);
        
        getFileHead(wb,bruRow,bruconfs);
        List<OptionGroup> groups=optionGroupManager.getOptionGroups(companyId,systemIds);
        for(OptionGroup group:groups){
        	optionGroupInfo(group,sheet,bruSheet,confs,bruconfs);
        }
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	
	private void optionGroupInfo(OptionGroup group,HSSFSheet sheet,HSSFSheet bruSheet,List<DataSheetConfig> confs,List<DataSheetConfig> bruconfs){
		if(group!=null){
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
				DataSheetConfig conf=confs.get(i);
				if(!conf.isIgnore()){
					String fieldName=conf.getFieldName();
					if(SYSTEM_CODE.equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						if(group.getSystemId()!=null){
							BusinessSystem system=businessSystemManager.getBusiness(group.getSystemId());
							if(system!=null){
								cell.setCellValue(system.getCode());
							}else{
								cell.setCellValue("");
							}
						}else{
							cell.setCellValue("");
						}
					}else{
        				setFieldValue(conf,i,rowi,group);
        			}
				}
			}
			List<Option> options=optionGroupManager.getOptionsByGroup(group.getId(),group.getCompanyId());
			for(Option option:options){
				optionInfo(option,bruSheet,bruconfs);
			}
		}
	}
	private void optionInfo(Option option,HSSFSheet bruSheet,List<DataSheetConfig> bruconfs){
		if(option!=null){
			HSSFRow rowi = bruSheet.createRow(bruSheet.getLastRowNum()+1);
			for(int i=0;i<bruconfs.size();i++){
				DataSheetConfig conf=bruconfs.get(i);
				if(!conf.isIgnore()){
					String fieldName=conf.getFieldName();
					if("groupNo".equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						cell.setCellValue(option.getOptionGroup().getCode());
					}else{
						setFieldValue(conf,i,rowi,option);
					}
				}
			}
		}
	}
	/**
	 * 导出数据字典及类型
	 */
	public void exportDataDictionary(OutputStream fileOut,Long companyId){
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='WF_DATA_DICTIONARY_TYPE']");
		List<DataSheetConfig> ddconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='WF_DATA_DICTIONARYS']");
		List<DataSheetConfig> ddpconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='WF_DATA_DICTIONARY_PROCESS']");
		List<DataSheetConfig> dduconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='WF_DATA_DICTIONARY_USERS']");
		wb = new HSSFWorkbook();
    	HSSFSheet sheet=wb.createSheet("WF_DATA_DICTIONARY_TYPE");
        HSSFRow row = sheet.createRow(0);        
        getFileHead(wb,row,confs);
        
        HSSFSheet ddSheet=wb.createSheet("WF_DATA_DICTIONARYS");
        HSSFRow ddRow = ddSheet.createRow(0);        
        getFileHead(wb,ddRow,ddconfs);
        
        HSSFSheet ddpSheet=wb.createSheet("WF_DATA_DICTIONARY_PROCESS");
        HSSFRow ddpRow = ddpSheet.createRow(0);        
        getFileHead(wb,ddpRow,ddpconfs);
        
        HSSFSheet dduSheet=wb.createSheet("WF_DATA_DICTIONARY_USERS");
        HSSFRow dduRow = dduSheet.createRow(0);        
        getFileHead(wb,dduRow,dduconfs);
        List<DataDictionaryType> types=dataDictionaryTypeManager.getAllDictTypesByCompany(companyId);
        for(DataDictionaryType type:types){
        	dictTypeInfo(type,sheet,confs,ddSheet,ddconfs,ddpSheet,ddpconfs,dduSheet,dduconfs);
        }
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	private void dictTypeInfo(DataDictionaryType dictType, HSSFSheet sheet,List<DataSheetConfig> confs,HSSFSheet ddsheet,List<DataSheetConfig> ddconfs,HSSFSheet ddpsheet,List<DataSheetConfig> ddpconfs,HSSFSheet ddusheet,List<DataSheetConfig> dduconfs){
		if(dictType!=null){
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
				DataSheetConfig conf=confs.get(i);
				if(!conf.isIgnore()){
					String fieldName=conf.getFieldName();
					if(SYSTEM_CODE.equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						if(dictType.getSystemId()!=null){
							BusinessSystem system=businessSystemManager.getBusiness(dictType.getSystemId());
							if(system!=null){
								cell.setCellValue(system.getCode());
							}else{
								cell.setCellValue("");
							}
						}else{
							cell.setCellValue("");
						}
					}else if("typeCodes".equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						if(StringUtils.isNotEmpty(dictType.getTypeIds())){
							List<String> typeCodes=dataDictionaryTypeManager.getDictTypeCodesByIds(dictType.getTypeIds(), dictType.getCompanyId());
							String codes=typeCodes.toString();
							codes=codes.replace("[", "").replace("]", "").replaceAll(" ", "");
							cell.setCellValue(codes);
						}else{
							cell.setCellValue("");
						}
					}else{
        				setFieldValue(conf,i,rowi,dictType);
        			}
				}
			}
			List<DataDictionary> dicts=dataDictionaryManager.getDataDictsByTypeId(dictType.getId(),dictType.getCompanyId());
			for(DataDictionary dict:dicts){
				dictInfo(dict,ddsheet,ddconfs,ddpsheet,ddpconfs,ddusheet,dduconfs);
			}
		}
	}
	private void dictInfo(DataDictionary dict, HSSFSheet sheet,List<DataSheetConfig> confs,HSSFSheet ddpsheet,List<DataSheetConfig> ddpconfs,HSSFSheet ddusheet,List<DataSheetConfig> dduconfs){
		if(dict!=null){
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
				DataSheetConfig conf=confs.get(i);
				if(!conf.isIgnore()){
    				setFieldValue(conf,i,rowi,dict);
				}
			}
			List<DataDictionaryProcess> dps=dataDictionaryManager.getAllDictProcessesByDictId(dict.getId(), dict.getCompanyId());
			for(DataDictionaryProcess ddp:dps){
				dictProcessInfo(ddp, ddpsheet, ddpconfs);
			}
			List<DataDictionaryUser> ddus=dataDictionaryManager.getDDUs(dict.getId(), dict.getCompanyId());
			for(DataDictionaryUser ddu:ddus){
				dictUserInfo(ddu,ddusheet,dduconfs);
			}
		}
	}
	private void dictProcessInfo(DataDictionaryProcess dictProcess, HSSFSheet sheet,List<DataSheetConfig> confs){
		if(dictProcess!=null){
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
				DataSheetConfig conf=confs.get(i);
				if(!conf.isIgnore()){
					WorkflowDefinition def=workflowDefinitionManager.getWfDefinition(dictProcess.getProcessDefinitionId());
					String fieldName=conf.getFieldName();
					if(SYSTEM_CODE.equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						BusinessSystem system=businessSystemManager.getBusiness(def.getSystemId());
						if(system!=null){
							cell.setCellValue(system.getCode());
						}else{
							cell.setCellValue("");
						}
					} else if("definitionCode".equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						cell.setCellValue(def.getCode());
					}else if("definitionVersion".equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						cell.setCellValue(def.getVersion());
					}else if("dictInfo".equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						cell.setCellValue(dictProcess.getDataDictionary().getInfo());
					}else{
        				setFieldValue(conf,i,rowi,dictProcess);
        			}
				}
			}
		}
	}
	private void dictUserInfo(DataDictionaryUser dictUser, HSSFSheet sheet,List<DataSheetConfig> confs){
		if(dictUser!=null){
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
				DataSheetConfig conf=confs.get(i);
				if(!conf.isIgnore()){
					String fieldName=conf.getFieldName();
					if("dictInfo".equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						DataDictionary dict=dataDictionaryManager.getDataDict(dictUser.getDictId());
						cell.setCellValue(dict.getInfo());
					}else if("infoIdenty".equals(fieldName)){
						HSSFCell cell = rowi.createCell(i);
						String value="";
						if(dictUser.getType()==DataDictUserType.DEPARTMENT){
							Department dept=departmentManager.getDepartment(dictUser.getInfoId());
							value=dept.getCode();
						}else if(dictUser.getType()==DataDictUserType.WORKGROUP){
							Workgroup group=workGroupManager.getWorkGroup(dictUser.getInfoId());
							value=group.getCode();
						}else if(dictUser.getType()==DataDictUserType.RANK){
							Superior rank=rankManager.getDataDictRankById(dictUser.getInfoId());
							value=rank.getTitle();
						}
						cell.setCellValue(value);
					}else{
        				setFieldValue(conf,i,rowi,dictUser);
        			}
				}
			}
		}
	}
	/**
	 * 导入流程类型
	 * @param file
	 * @param companyId
	 */
	public void importDefinitionType(File file,Long companyId){
		FileInputStream fis=null;
		InputStreamReader fr=null;
		BufferedReader br=null;
		try{
			fis=new FileInputStream(file);
			HSSFWorkbook wb=new HSSFWorkbook(fis);
			List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='WF_TYPE']");
			Map<String,Integer> map=getIdentifier(confs);
			if(companyId==null){
				List<Company> companys=companyManager.getCompanys();
				for(Company company:companys){
					ThreadParameters parameters=new ThreadParameters(company.getCompanyId());
					ParameterUtils.setParameters(parameters);
					addDefType(wb,confs,map);
				}
			}else{
				ThreadParameters parameters=new ThreadParameters(companyId);
				ParameterUtils.setParameters(parameters);
				addDefType(wb,confs,map);
			}
			clearCompanyId();
		}catch (FileNotFoundException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			try{
				if(br!=null)br.close();
				if(fr!=null)fr.close();
				if(fis!=null)fis.close();
			}catch(IOException ep){
				log.debug(PropUtils.getExceptionInfo(ep));
			}
		}
	}
	private void addDefType(HSSFWorkbook wb,List<DataSheetConfig> confs,Map<String,Integer> map){
		HSSFSheet sheet=wb.getSheet("WF_TYPE");
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			if(sheet.getRow(i)!=null){
				HSSFRow prow =sheet.getRow(i);
				String typeCode=prow.getCell(map.get("code")).getStringCellValue();
				WorkflowType type=workflowTypeManager.getWorkflowType(typeCode);
				if(type==null) type=new WorkflowType();
				for(int j=0;j<confs.size();j++){
					DataSheetConfig conf=confs.get(j);
					if(!conf.isIgnore()){
						String fieldName=conf.getFieldName();
						String value=null;
						if(prow.getCell(j)!=null){
							value=prow.getCell(j).getStringCellValue();
						}
						if(StringUtils.isNotEmpty(value)){//导入数据
							setValue(type,fieldName,conf.getDataType(),value,conf.getEnumName());
						}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
							setValue(type,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
						}
					}
				}
				workflowTypeManager.saveWorkflowType(type);
			}
		}
	}
	/**
	 * 导入流程定义及流程定义文件
	 * @param file
	 * @param companyId
	 */
	public void importDefinition(File file,Long companyId){
		FileInputStream fis=null;
		InputStreamReader fr=null;
		BufferedReader br=null;
		try{
			fis=new FileInputStream(file);
			HSSFWorkbook wb=new HSSFWorkbook(fis);
			List<DataSheetConfig> defconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='WF_DEFINITION']");
			List<DataSheetConfig> defileconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='WF_DEFINITION_FILE']");
			Map<String,Integer> defmap=getIdentifier(defconfs);
			Map<String,Integer> dfilemap=getIdentifier(defileconfs);
			if(companyId==null){
				List<Company> companys=companyManager.getCompanys();
				for(Company company:companys){
					ThreadParameters parameters=new ThreadParameters(company.getCompanyId());
					ParameterUtils.setParameters(parameters);
					//流程定义
					addDefinition(wb,defconfs,defmap);
					//流程定义文件
					addDefinitionFile(wb,defileconfs,dfilemap);
				}
			}else{
				ThreadParameters parameters=new ThreadParameters(companyId);
				ParameterUtils.setParameters(parameters);
				//流程定义
				addDefinition(wb,defconfs,defmap);
				//流程定义文件
				addDefinitionFile(wb,defileconfs,dfilemap);
			}
			clearCompanyId();
		}catch (FileNotFoundException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			try{
				if(br!=null)br.close();
				if(fr!=null)fr.close();
				if(fis!=null)fis.close();
			}catch(IOException ep){
				log.debug(PropUtils.getExceptionInfo(ep));
			}
		}
	}
	private void addDefinition(HSSFWorkbook wb,List<DataSheetConfig> confs,Map<String,Integer> map){
		HSSFSheet sheet=wb.getSheet("WF_DEFINITION");
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			if(sheet.getRow(i)!=null){
				HSSFRow prow =sheet.getRow(i);
				String code=prow.getCell(map.get("code")).getStringCellValue();
				String version=prow.getCell(map.get("version")).getStringCellValue();
				String systemCode=prow.getCell(map.get("systemCode")).getStringCellValue();
				BusinessSystem system=businessSystemManager.getSystemBySystemCode(systemCode);
				if(system!=null){
					WorkflowDefinition definition=workflowDefinitionManager.getWorkflowDefinitionByCodeAndVersion(code, Integer.parseInt(version), ContextUtils.getCompanyId(), system.getId());
					if(definition==null)definition=new WorkflowDefinition();
					for(int j=0;j<confs.size();j++){
						DataSheetConfig conf=confs.get(j);
						if(!conf.isIgnore()){
							String fieldName=conf.getFieldName();
							String value=null;
							if(prow.getCell(j)!=null){
								value=prow.getCell(j).getStringCellValue();
							}
							if("typeCode".equals(fieldName)){
								WorkflowType type=workflowTypeManager.getWorkflowType(value);
								if(type!=null)definition.setTypeId(type.getId());
							}else if(SYSTEM_CODE.equals(fieldName)){
								definition.setSystemId(system.getId());
							}else{
								if(StringUtils.isNotEmpty(value)){//导入数据
									setValue(definition,fieldName,conf.getDataType(),value,conf.getEnumName());
								}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
									setValue(definition,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
								}
							}
						}
					}
					//设置流程管理员为“系统管理员”
					BusinessSystem acsSys=businessSystemManager.getSystemBySystemCode("acs");
					if(acsSys!=null){
						com.norteksoft.acs.entity.organization.User user=acsUtils.getUserByLikeLoginName("systemAdmin",ContextUtils.getCompanyId());
						if(user!=null){
							definition.setAdminLoginName(user.getLoginName());
							definition.setAdminName(user.getName());
						}
					}
					workflowDefinitionManager.saveWorkflowDefinition(definition);
				}
			}
		}
	}
	private void addDefinitionFile(HSSFWorkbook wb,List<DataSheetConfig> confs,Map<String,Integer> map){
		HSSFSheet sheet=wb.getSheet("WF_DEFINITION_FILE");
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			if(sheet.getRow(i)!=null){
				HSSFRow prow =sheet.getRow(i);
				String code=prow.getCell(map.get("defCode")).getStringCellValue();
				String version=prow.getCell(map.get("defVersion")).getStringCellValue();
				String systemCode=prow.getCell(map.get("systemCode")).getStringCellValue();
				BusinessSystem system=businessSystemManager.getSystemBySystemCode(systemCode);
				if(system!=null){
					WorkflowDefinition definition=workflowDefinitionManager.getWorkflowDefinitionByCodeAndVersion(code, Integer.parseInt(StringUtils.trim(version)), ContextUtils.getCompanyId(), system.getId());
					if(definition!=null){
						WorkflowDefinitionFile defFile=workflowDefinitionManager.getWfDefinitionFileByWfdId(definition.getId(),ContextUtils.getCompanyId());
						if(defFile==null)defFile=new WorkflowDefinitionFile();
						for(int j=0;j<confs.size();j++){
							DataSheetConfig conf=confs.get(j);
							if(!conf.isIgnore()){
								String fieldName=conf.getFieldName();
								String value=null;
								if(prow.getCell(j)!=null){
									value=prow.getCell(j).getStringCellValue();
								}
								if("defCode".equals(fieldName)){
								}else if("defVersion".equals(fieldName)){
								}else if(SYSTEM_CODE.equals(fieldName)){
								}else {
									if(StringUtils.isNotEmpty(value)){//导入数据
										setValue(definition,fieldName,conf.getDataType(),value,conf.getEnumName());
									}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
										setValue(definition,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
									}
								}
							}
						}
						defFile.setWfDefinitionId(definition.getId());
						workflowDefinitionManager.saveWorkflowDefinitionFile(defFile);
					}
				}
			}
		}
	}
	public void importDataDict(File file,Long companyId){
		FileInputStream fis=null;
		InputStreamReader fr=null;
		BufferedReader br=null;
		try{
			fis=new FileInputStream(file);
			HSSFWorkbook wb=new HSSFWorkbook(fis);
			List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='WF_DATA_DICTIONARY_TYPE']");
			List<DataSheetConfig> ddconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='WF_DATA_DICTIONARYS']");
			List<DataSheetConfig> ddpconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='WF_DATA_DICTIONARY_PROCESS']");
			List<DataSheetConfig> dduconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='WF_DATA_DICTIONARY_USERS']");
			Map<String,Integer> map=getIdentifier(confs);
			Map<String,Integer> ddmap=getIdentifier(ddconfs);
			Map<String,Integer> ddpmap=getIdentifier(ddpconfs);
			Map<String,Integer> ddumap=getIdentifier(dduconfs);
			if(companyId==null){
				List<Company> companys=companyManager.getCompanys();
				for(Company company:companys){
					ThreadParameters parameters=new ThreadParameters(company.getCompanyId());
					ParameterUtils.setParameters(parameters);
					//数据字典类型
					addDictType(wb,confs,map);
					//数据字典
					addDict(wb,ddconfs,ddmap);
					//数据字典对应的流程
					addDictProcess(wb,ddpconfs,ddpmap);
					//数据字典对应的人员
					addDictUser(wb,dduconfs,ddumap);
				}
			}else{
				ThreadParameters parameters=new ThreadParameters(companyId);
				ParameterUtils.setParameters(parameters);
				//数据字典类型
				addDictType(wb,confs,map);
				//数据字典
				addDict(wb,ddconfs,ddmap);
				//数据字典对应的流程
				addDictProcess(wb,ddpconfs,ddpmap);
				//数据字典对应的人员
				addDictUser(wb,dduconfs,ddumap);
			}
			clearCompanyId();
		}catch (FileNotFoundException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			try{
				if(br!=null)br.close();
				if(fr!=null)fr.close();
				if(fis!=null)fis.close();
			}catch(IOException ep){
				log.debug(PropUtils.getExceptionInfo(ep));
			}
		}
	}
	private void addDictType(HSSFWorkbook wb,List<DataSheetConfig> confs,Map<String,Integer> map){
		HSSFSheet sheet=wb.getSheet("WF_DATA_DICTIONARY_TYPE");
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			if(sheet.getRow(i)!=null){
				HSSFRow prow =sheet.getRow(i);
				String typeCode=prow.getCell(map.get("no")).getStringCellValue();
				DataDictionaryType type=dataDictionaryTypeManager.getDictTypeByNo(typeCode);
				if(type==null) type=new DataDictionaryType();
				for(int j=0;j<confs.size();j++){
					DataSheetConfig conf=confs.get(j);
					if(!conf.isIgnore()){
						String fieldName=conf.getFieldName();
						String value=null;
						if(prow.getCell(j)!=null){
							value=prow.getCell(j).getStringCellValue();
						}
						if(SYSTEM_CODE.equals(fieldName)){
							BusinessSystem system=businessSystemManager.getSystemBySystemCode(value);
							if(system!=null)type.setSystemId(system.getId());
						}else if("typeCodes".equals(fieldName)){
							List<String> ids=dataDictionaryTypeManager.getDictTypeIdsByCodes(value);
							String typeIds=ids.toString();
							typeIds=typeIds.replace("[", "").replace("]", "").replaceAll(" ", "");
							type.setTypeIds(typeIds);
						}else{
							if(StringUtils.isNotEmpty(value)){//导入数据
								setValue(type,fieldName,conf.getDataType(),value,conf.getEnumName());
							}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
								setValue(type,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
							}
						}
					}
				}
				dataDictionaryTypeManager.saveDictType(type);
			}
		}
	}
	private void addDict(HSSFWorkbook wb,List<DataSheetConfig> confs,Map<String,Integer> map){
		HSSFSheet sheet=wb.getSheet("WF_DATA_DICTIONARYS");
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			if(sheet.getRow(i)!=null){
				HSSFRow prow =sheet.getRow(i);
				String title=prow.getCell(map.get("info")).getStringCellValue();
				DataDictionary dict=dataDictionaryManager.getDataDictByTitle(title);
				if(dict==null) dict=new DataDictionary();
				for(int j=0;j<confs.size();j++){
					DataSheetConfig conf=confs.get(j);
					if(!conf.isIgnore()){
						String fieldName=conf.getFieldName();
						String value=null;
						if(prow.getCell(j)!=null){
							value=prow.getCell(j).getStringCellValue();
						}
						if("typeNo".equals(fieldName)){
							dict.setTypeNo(value);
							DataDictionaryType type=dataDictionaryTypeManager.getDictTypeByNo(value);
							if(type!=null)dict.setTypeId(type.getId());
						}else{
							if(StringUtils.isNotEmpty(value)){//导入数据
								setValue(dict,fieldName,conf.getDataType(),value,conf.getEnumName());
							}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
								setValue(dict,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
							}
						}
					}
				}
				dataDictionaryManager.saveDict(dict);
			}
		}
	}
	private void addDictProcess(HSSFWorkbook wb,List<DataSheetConfig> confs,Map<String,Integer> map){
		HSSFSheet sheet=wb.getSheet("WF_DATA_DICTIONARY_PROCESS");
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			if(sheet.getRow(i)!=null){
				HSSFRow prow =sheet.getRow(i);
				String dictTitle=prow.getCell(map.get("dictInfo")).getStringCellValue();
				DataDictionary dict=dataDictionaryManager.getDataDictByTitle(dictTitle);
				if(dict!=null){
					String systemCode=prow.getCell(map.get(SYSTEM_CODE)).getStringCellValue();
					BusinessSystem system=businessSystemManager.getSystemBySystemCode(systemCode);
					if(system!=null){
						String definitionCode=prow.getCell(map.get("definitionCode")).getStringCellValue();
						String definitionVersion=prow.getCell(map.get("definitionVersion")).getStringCellValue();
						WorkflowDefinition def=workflowDefinitionManager.getWorkflowDefinitionByCodeAndVersion(definitionCode, Integer.parseInt(StringUtils.trim(definitionVersion)), ContextUtils.getCompanyId(), system.getId());
						if(def!=null){
							String tacheName=prow.getCell(map.get("tacheName")).getStringCellValue();
							DataDictionaryProcess process=dataDictionaryManager.getDictProcessByDef(def.getId(), dict.getId(), tacheName);
							if(process==null)process=new DataDictionaryProcess();
							for(int j=0;j<confs.size();j++){
								DataSheetConfig conf=confs.get(j);
								if(!conf.isIgnore()){
									String fieldName=conf.getFieldName();
									String value=null;
									if(prow.getCell(j)!=null){
										value=prow.getCell(j).getStringCellValue();
									}
									if(!conf.isIdentifier()){
										if(StringUtils.isNotEmpty(value)){//导入数据
											setValue(process,fieldName,conf.getDataType(),value,conf.getEnumName());
										}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
											setValue(process,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
										}
									}
								}
							}
							process.setDataDictionary(dict);
							process.setProcessDefinitionId(def.getId());
							process.setTacheName(tacheName);
							dataDictionaryManager.saveDictProcess(process);
						}
					}
				}
			}
		}
	}
	private void addDictUser(HSSFWorkbook wb,List<DataSheetConfig> confs,Map<String,Integer> map){
		HSSFSheet sheet=wb.getSheet("WF_DATA_DICTIONARY_USERS");
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			if(sheet.getRow(i)!=null){
				HSSFRow prow =sheet.getRow(i);
				String dictTitle=prow.getCell(map.get("dictInfo")).getStringCellValue();
				DataDictionary dict=dataDictionaryManager.getDataDictByTitle(dictTitle);
				if(dict!=null){
					String type=prow.getCell(map.get("type")).getStringCellValue();
					Department dept=null;
					Workgroup group=null;
					Superior rank=null;
					DataDictionaryUser dictUser=null;
					if(DataDictUserType.USER.toString().equals(type)){
						String loginName=prow.getCell(map.get("loginName")).getStringCellValue();
						dictUser=dataDictionaryManager.getDictUserByType(dict.getId(),DataDictUserType.USER,loginName,null);
					}else if(DataDictUserType.DEPARTMENT.toString().equals(type)){
						String deptCode=prow.getCell(map.get("infoIdenty")).getStringCellValue();
						 dept=acsUtils.getDepartmentByCode(deptCode, ContextUtils.getCompanyId());
						 if(dept!=null)
						dictUser=dataDictionaryManager.getDictUserByType(dict.getId(),DataDictUserType.DEPARTMENT,null,dept.getId());
					}else if(DataDictUserType.WORKGROUP.toString().equals(type)){
						String wgCode=prow.getCell(map.get("infoIdenty")).getStringCellValue();
						group=acsUtils.getWorkGroupByCode(wgCode, ContextUtils.getCompanyId());
						if(group!=null)
						dictUser=dataDictionaryManager.getDictUserByType(dict.getId(),DataDictUserType.WORKGROUP,null,group.getId());
					}else if(DataDictUserType.RANK.toString().equals(type)){
						String rankTitle=prow.getCell(map.get("infoIdenty")).getStringCellValue();
						rank=rankManager.getRankByTitle(rankTitle);
						if(rank!=null)
						dictUser=dataDictionaryManager.getDictUserByType(dict.getId(),DataDictUserType.RANK,null,rank.getId());
					}
					if(dictUser==null)dictUser=new DataDictionaryUser();
					for(int j=0;j<confs.size();j++){
						DataSheetConfig conf=confs.get(j);
						if(!conf.isIgnore()){
							String fieldName=conf.getFieldName();
							String value=null;
							if(prow.getCell(j)!=null){
								value=prow.getCell(j).getStringCellValue();
							}
							if("dictInfo".equals(fieldName)){
								dictUser.setDictId(dict.getId());
							}else if("infoIdenty".equals(fieldName)){
								if(DataDictUserType.USER.toString().equals(type)){
									String loginName=prow.getCell(map.get("loginName")).getStringCellValue();
									User user=ApiFactory.getAcsService().getUserByLoginName(loginName);
									if(user!=null){
										dictUser.setLoginName(loginName);
									}else{
										break;
									}
								}else if(DataDictUserType.DEPARTMENT.toString().equals(type)){
									if(dept!=null)dictUser.setInfoId(dept.getId());
								}else if(DataDictUserType.WORKGROUP.toString().equals(type)){
									if(group!=null)dictUser.setInfoId(group.getId());
								}else if(DataDictUserType.RANK.toString().equals(type)){
									if(rank!=null)dictUser.setInfoId(rank.getId());
								}
							}else if("loginName".equals(fieldName)){
								if(StringUtils.isNotEmpty(value)){
									User user=ApiFactory.getAcsService().getUserByLoginName(value);
									if(user!=null){
										dictUser.setLoginName(value);
									}else{
										break;
									}
								}
							}else if("infoName".equals(fieldName)){
								if(DataDictUserType.USER.toString().equals(type)){
									if(StringUtils.isNotEmpty(dictUser.getLoginName())){
										dictUser.setInfoName(value);
									}
								}else if(DataDictUserType.DEPARTMENT.toString().equals(type)||
										DataDictUserType.WORKGROUP.toString().equals(type)||
										DataDictUserType.RANK.toString().equals(type)){
									if(dictUser.getInfoId()!=null){
										dictUser.setInfoName(value);
									}
								}
							}else{
								if(StringUtils.isNotEmpty(value)){//导入数据
									setValue(dictUser,fieldName,conf.getDataType(),value,conf.getEnumName());
								}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
									setValue(dictUser,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
								}
							}
						}
					}
					if(DataDictUserType.USER.toString().equals(type)&&StringUtils.isNotEmpty(dictUser.getLoginName())){//当是人员时，登录名不能为空
						dataDictionaryManager.saveDictUser(dictUser);
					}else if((DataDictUserType.DEPARTMENT.toString().equals(type)||
							DataDictUserType.WORKGROUP.toString().equals(type)||
							DataDictUserType.RANK.toString().equals(type))&&
							dictUser.getInfoId()!=null){//当是部门、工作组、上下级关系时，对应的id不能为空
						dataDictionaryManager.saveDictUser(dictUser);
					}
				}
			}
		}
	}
	/**
	 * 导入选项组和选项
	 * @param file
	 * @param companyId
	 */
	public void importOption(File file,Long companyId){
		FileInputStream fis=null;
		InputStreamReader fr=null;
		BufferedReader br=null;
		try{
			fis=new FileInputStream(file);
			HSSFWorkbook wb=new HSSFWorkbook(fis);
			List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_OPTION_GROUP']");
			List<DataSheetConfig> opconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_OPTION']");
			Map<String,Integer> map=getIdentifier(confs);
			Map<String,Integer> opmap=getIdentifier(opconfs);
			if(companyId==null){
				List<Company> companys=companyManager.getCompanys();
				for(Company company:companys){
					ThreadParameters parameters=new ThreadParameters(company.getCompanyId());
					ParameterUtils.setParameters(parameters);
					//添加选项组
					addOptionGroup(wb,confs,map);
					//添加选项
					addOption(wb,opconfs,opmap);
				}
			}else{
				ThreadParameters parameters=new ThreadParameters(companyId);
				ParameterUtils.setParameters(parameters);
				//添加选项组
				addOptionGroup(wb,confs,map);
				//添加选项
				addOption(wb,opconfs,opmap);
			}
			clearCompanyId();
		}catch (FileNotFoundException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			try{
				if(br!=null)br.close();
				if(fr!=null)fr.close();
				if(fis!=null)fis.close();
			}catch(IOException ep){
				log.debug(PropUtils.getExceptionInfo(ep));
			}
		}
	}
	private void addOptionGroup(HSSFWorkbook wb,List<DataSheetConfig> confs,Map<String,Integer> map){
		HSSFSheet sheet=wb.getSheet("BS_OPTION_GROUP");
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			if(sheet.getRow(i)!=null){
				HSSFRow prow =sheet.getRow(i);
				String optionGroupNo=prow.getCell(map.get("code")).getStringCellValue();
				OptionGroup group=optionGroupManager.getOptionGroupByCode(optionGroupNo);
				if(group==null) group=new OptionGroup();
				for(int j=0;j<confs.size();j++){
					DataSheetConfig conf=confs.get(j);
					if(!conf.isIgnore()){
						String fieldName=conf.getFieldName();
						String value=null;
						if(prow.getCell(j)!=null){
							value=prow.getCell(j).getStringCellValue();
						}
						if(SYSTEM_CODE.equals(fieldName)){
							BusinessSystem system=businessSystemManager.getSystemBySystemCode(value);
							if(system!=null)group.setSystemId(system.getId());
						}else{
							if(StringUtils.isNotEmpty(value)){//导入数据
								setValue(group,fieldName,conf.getDataType(),value,conf.getEnumName());
							}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
								setValue(group,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
							}
						}
					}
				}
				optionGroupManager.saveOptionGroup(group);
			}
		}
	}
	private void addOption(HSSFWorkbook wb,List<DataSheetConfig> confs,Map<String,Integer> map){
		HSSFSheet sheet=wb.getSheet("BS_OPTION");
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			if(sheet.getRow(i)!=null){
				HSSFRow prow =sheet.getRow(i);
				String groupNo=prow.getCell(map.get("groupNo")).getStringCellValue();
				OptionGroup group=optionGroupManager.getOptionGroupByCode(groupNo);
				if(group!=null){
					String name=prow.getCell(map.get("name")).getStringCellValue();
					String opvalue=prow.getCell(map.get("value")).getStringCellValue();
					Option option=optionGroupManager.getOptionByInfo(opvalue, name, groupNo);
					if(option==null)option=new Option();
					for(int j=0;j<confs.size();j++){
						DataSheetConfig conf=confs.get(j);
						if(!conf.isIgnore()){
							String fieldName=conf.getFieldName();
							String value=null;
							if(prow.getCell(j)!=null){
								value=prow.getCell(j).getStringCellValue();
							}
							if("groupNo".equals(fieldName)){
								option.setOptionGroup(group);
							}else{
								if(StringUtils.isNotEmpty(value)){//导入数据
									setValue(group,fieldName,conf.getDataType(),value,conf.getEnumName());
								}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
									setValue(group,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
								}
							}
						}
					}
					option.setName(name);
					option.setValue(opvalue);
					optionGroupManager.saveOption(option);
				}
			}
		}
	}
	/**
	 * 导入用户上下级关系
	 * @param file
	 * @param companyId
	 */
	public void importRank(File file,Long companyId){
		FileInputStream fis=null;
		InputStreamReader fr=null;
		BufferedReader br=null;
		try{
			fis=new FileInputStream(file);
			HSSFWorkbook wb=new HSSFWorkbook(fis);
			List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_SUPERIOR']");
			List<DataSheetConfig> ruconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_SUBORDINATE']");
			Map<String,Integer> map=getIdentifier(confs);
			Map<String,Integer> rumap=getIdentifier(ruconfs);
			if(companyId==null){
				List<Company> companys=companyManager.getCompanys();
				for(Company company:companys){
					ThreadParameters parameters=new ThreadParameters(company.getCompanyId());
					ParameterUtils.setParameters(parameters);
					//添加用户上下级关系
					addRank(wb,confs,map);
					//添加用户
					addRankUser(wb,ruconfs,rumap);
				}
			}else{
				ThreadParameters parameters=new ThreadParameters(companyId);
				ParameterUtils.setParameters(parameters);
				//添加用户上下级关系
				addRank(wb,confs,map);
				//添加用户
				addRankUser(wb,ruconfs,rumap);
			}
			clearCompanyId();
		}catch (FileNotFoundException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			try{
				if(br!=null)br.close();
				if(fr!=null)fr.close();
				if(fis!=null)fis.close();
			}catch(IOException ep){
				log.debug(PropUtils.getExceptionInfo(ep));
			}
		}
	}
	private void addRank(HSSFWorkbook wb,List<DataSheetConfig> confs,Map<String,Integer> map){
		HSSFSheet sheet=wb.getSheet("BS_SUPERIOR");
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			if(sheet.getRow(i)!=null){
				HSSFRow prow =sheet.getRow(i);
				String title=prow.getCell(map.get("title")).getStringCellValue();
				Superior rank=rankManager.getRankByTitle(title);
				if(rank==null)rank=new Superior();
				for(int j=0;j<confs.size();j++){
					DataSheetConfig conf=confs.get(j);
					if(!conf.isIgnore()){
						String fieldName=conf.getFieldName();
						String value=null;
						if(prow.getCell(j)!=null){
							value=prow.getCell(j).getStringCellValue();
						}
						if("loginName".equals(fieldName)){
							rank.setLoginName(value);
							User user=ApiFactory.getAcsService().getUserByLoginName(value);
							if(user!=null){
								rank.setUserId(user.getId());
								rank.setName(user.getName());
							}else{//当用户不存在时不再做任何操作
								break;
							}
						}else if(SYSTEM_CODE.equals(fieldName)){
							BusinessSystem system=businessSystemManager.getSystemBySystemCode(value);
							if(system!=null)rank.setSystemId(system.getId());
						}else{
							if(StringUtils.isNotEmpty(value)){//导入数据
								setValue(rank,fieldName,conf.getDataType(),value,conf.getEnumName());
							}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
								setValue(rank,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
							}
						}
					}
				}
				if(rank.getUserId()!=null){//当用户存在时才保存该上下级关系
					rankManager.saveDataDictRank(rank);
				}
			}
		}
	}
	private void addRankUser(HSSFWorkbook wb,List<DataSheetConfig> confs,Map<String,Integer> map){
		HSSFSheet sheet=wb.getSheet("BS_SUBORDINATE");
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			if(sheet.getRow(i)!=null){
				HSSFRow prow =sheet.getRow(i);
				String rankTitle=prow.getCell(map.get("rankTitle")).getStringCellValue();
				Superior rank=rankManager.getRankByTitle(rankTitle);
				if(rank!=null){
					String type=prow.getCell(map.get("subordinateType")).getStringCellValue();
					Department dept=null;
					Workgroup group=null;
					Subordinate rankUser=null;
					if(SubordinateType.USER.toString().equals(type)){
						String loginName=prow.getCell(map.get("loginName")).getStringCellValue();
						rankUser=rankUserManager.getRankUserByInfo(rank.getId(), SubordinateType.USER, loginName, null);
					}else if(SubordinateType.DEPARTMENT.toString().equals(type)){
						String deptCode=prow.getCell(map.get("infoIdenty")).getStringCellValue();
						 dept=acsUtils.getDepartmentByCode(deptCode, ContextUtils.getCompanyId());
						 if(dept!=null)
						 rankUser=rankUserManager.getRankUserByInfo(rank.getId(), SubordinateType.DEPARTMENT, null, dept.getId());
					}else if(SubordinateType.WORKGROUP.toString().equals(type)){
						String wgCode=prow.getCell(map.get("infoIdenty")).getStringCellValue();
						group=acsUtils.getWorkGroupByCode(wgCode, ContextUtils.getCompanyId());
						if(group!=null)
						rankUser=rankUserManager.getRankUserByInfo(rank.getId(), SubordinateType.WORKGROUP, null, group.getId());
					}
					if(rankUser==null)rankUser=new Subordinate();
					for(int j=0;j<confs.size();j++){
						DataSheetConfig conf=confs.get(j);
						if(!conf.isIgnore()){
							String fieldName=conf.getFieldName();
							String value=null;
							if(prow.getCell(j)!=null){
								value=prow.getCell(j).getStringCellValue();
							}
							if("rankTitle".equals(fieldName)){
								rankUser.setDataDictionaryRank(rank);
							}else if(SYSTEM_CODE.equals(fieldName)){
								BusinessSystem system=businessSystemManager.getSystemBySystemCode(value);
								if(system!=null)rankUser.setSystemId(system.getId());
							}else if("infoIdenty".equals(fieldName)){
								if(SubordinateType.USER.toString().equals(type)){
									String loginName=prow.getCell(map.get("loginName")).getStringCellValue();
									User user=ApiFactory.getAcsService().getUserByLoginName(loginName);
									if(user!=null){
										rankUser.setLoginName(loginName);
									}else{//当用户不存在时不保存下级用户信息
										break;
									}
								}else if(SubordinateType.DEPARTMENT.toString().equals(type)){
									if(dept!=null)rankUser.setTargetId(dept.getId());
								}else if(SubordinateType.WORKGROUP.toString().equals(type)){
									if(group!=null)rankUser.setTargetId(group.getId());
								}
							}else{
								if(StringUtils.isNotEmpty(value)){//导入数据
									setValue(rankUser,fieldName,conf.getDataType(),value,conf.getEnumName());
								}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
									setValue(rankUser,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
								}
							}
						}
					}
					if(SubordinateType.USER.toString().equals(type)&&StringUtils.isNotEmpty(rankUser.getLoginName())){//当用户不存在时不保存下级用户信息
						rankUserManager.saveRankUser(rankUser);
					}else if((SubordinateType.DEPARTMENT.toString().equals(type)||
							SubordinateType.WORKGROUP.toString().equals(type))&&
							rankUser.getTargetId()!=null){//当用户不存在时不保存下级用户信息
						rankUserManager.saveRankUser(rankUser);
					}
				}
			}
		}
	}
	/**
	 * 导出定时信息及定时时间信息
	 * @param fileOut
	 * @param companyId
	 * @param systemIds
	 */
	public void exportJobInfo(OutputStream fileOut,Long companyId,String systemIds){
		HSSFWorkbook wb;
		List<DataSheetConfig> jobconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_TIMED_TASK']");
		List<DataSheetConfig> cornjobconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_TIMER']");
		wb = new HSSFWorkbook();
        HSSFSheet defsheet=wb.createSheet("BS_TIMED_TASK");
        HSSFRow defrow = defsheet.createRow(0);        
        getFileHead(wb,defrow,jobconfs);
        
        HSSFSheet defilesheet=wb.createSheet("BS_TIMER");
        HSSFRow defilerow = defilesheet.createRow(0);        
        getFileHead(wb,defilerow,cornjobconfs);
        
        List<TimedTask> jobInfos=jobInfoManager.getJobInfoBySystem(companyId, systemIds);
        jobInfo(jobInfos,defsheet,jobconfs,defilesheet,cornjobconfs);
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	
	private void jobInfo(List<TimedTask> jobInfos,HSSFSheet sheet,List<DataSheetConfig> jobconfs,HSSFSheet cornSheet,List<DataSheetConfig> cornconfs){
		for(TimedTask jobInfo:jobInfos){
        	HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
        	for(int i=0;i<jobconfs.size();i++){
        		DataSheetConfig conf=jobconfs.get(i);
        		if(!conf.isIgnore()){
    				setFieldValue(conf,i,rowi,jobInfo);
        		}
        	}
        	cornInfo(jobInfo,cornSheet,cornconfs);
        }
	}
	
	private void cornInfo(TimedTask jobInfo,HSSFSheet sheet,List<DataSheetConfig> cornconfs){
		if(jobInfo!=null){
			List<Timer> cornInfos=jobInfoManager.getCornInfoByJob(jobInfo.getId());
			for(Timer cornInfo:cornInfos){
				HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
				for(int i=0;i<cornconfs.size();i++){
					DataSheetConfig conf=cornconfs.get(i);
					if(!conf.isIgnore()){
						if("jobCode".equals(conf.getFieldName())){
							HSSFCell cell = rowi.createCell(i);
	        				if(jobInfo!=null){
	        					cell.setCellValue(jobInfo.getCode());
	        				}else{
	        					cell.setCellValue("");
	        				}
						}else if(SYSTEM_CODE.equals(conf.getFieldName())){
							HSSFCell cell = rowi.createCell(i);
	        				if(jobInfo!=null){
	        					cell.setCellValue(jobInfo.getSystemCode());
	        				}else{
	        					cell.setCellValue("");
	        				}
						}else{
							setFieldValue(conf,i,rowi,cornInfo);
						}
					}
				}
			}
		}
	}
	/**
	 * 导入定时信息及定时时间信息
	 * @param file
	 * @param companyId
	 */
	public void importJobInfo(File file,Long companyId){
		List<DataSheetConfig> jobconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_TIMED_TASK']");
		List<DataSheetConfig> cornjobconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_TIMER']");
		Map<String,Integer> map=getIdentifier(jobconfs);
		Map<String,Integer> colMap=getIdentifier(cornjobconfs);
		//创建时间,创建人姓名,创建人id,公司id
		FileInputStream fis=null;
 		InputStreamReader fr=null;
 		BufferedReader br=null;
 		try{
 			fis=new FileInputStream(file);
 			HSSFWorkbook wb=new HSSFWorkbook(fis);
 			HSSFSheet sheet=wb.getSheet("BS_TIMED_TASK");
 			if(ContextUtils.getCompanyId()==null){
 				if(companyId==null){
 					List<Company> companys=companyManager.getCompanys();
 					for(Company company:companys){
 						ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
 						ParameterUtils.setParameters(parameters);
 						addJobInfo(sheet,jobconfs,map);
 					}
 				}else{
 					ThreadParameters parameters=new ThreadParameters(companyId,null);
						ParameterUtils.setParameters(parameters);
						addJobInfo(sheet,jobconfs,map);
 				}
 				clearCompanyId();
 			}else{
 				addJobInfo(sheet,jobconfs,map);
 			}
 			HSSFSheet colSheet=wb.getSheet("BS_TIMER");
 			if(ContextUtils.getCompanyId()==null){
 				List<Company> companys=companyManager.getCompanys();
 				for(Company company:companys){
 					ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
 					ParameterUtils.setParameters(parameters);
 					addCornInfo(colSheet,cornjobconfs,colMap);
 				}
 				clearCompanyId();
 			}else{
 				addCornInfo(colSheet,cornjobconfs,colMap);
 			}
 		} catch (FileNotFoundException e) {
 			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
 			try{
	 			if(br!=null)br.close();
	 			if(fr!=null)fr.close();
	 			if(fis!=null)fis.close();
 			}catch(IOException ep){
 				log.debug(PropUtils.getExceptionInfo(ep));
 			}
 		}
	}
	
	private void addJobInfo(HSSFSheet sheet,List<DataSheetConfig> confs,Map<String,Integer> map){
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			if(sheet.getRow(i)!=null){
				HSSFRow prow =sheet.getRow(i);
				String code=prow.getCell(map.get("code")).getStringCellValue();
				String systemCode=prow.getCell(map.get("systemCode")).getStringCellValue();
				TimedTask jobInfo=jobInfoManager.getJobInfoByCode(code, systemCode);
				if(jobInfo==null) jobInfo=new TimedTask();
				for(int j=0;j<confs.size();j++){
					DataSheetConfig conf=confs.get(j);
					if(!conf.isIgnore()){
						String fieldName=conf.getFieldName();
						String value=null;
						if(prow.getCell(j)!=null){
							value=prow.getCell(j).getStringCellValue();
						}
						if(SYSTEM_CODE.equals(fieldName)){
							jobInfo.setSystemCode(value);
							BusinessSystem system=businessSystemManager.getSystemBySystemCode(value);
							if(system!=null){
								jobInfo.setSystemId(system.getId());
							}
						}else{
							if(StringUtils.isNotEmpty(value)){//导入数据
								setValue(jobInfo,fieldName,conf.getDataType(),value,conf.getEnumName());
							}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
								setValue(jobInfo,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
							}
						}
					}
				}
				com.norteksoft.acs.entity.organization.User user=userManager.getUserByLoginName(jobInfo.getRunAsUser());
				if(user==null){
					user=acsUtils.getUserByLikeLoginName("systemAdmin",ContextUtils.getCompanyId());
					if(user!=null){
						jobInfo.setRunAsUser(user.getLoginName());
						jobInfo.setRunAsUserName(user.getName());
					}
				}
				jobInfoManager.saveJobInfo(jobInfo);
			}
		}
	}
	private void addCornInfo(HSSFSheet sheet,List<DataSheetConfig> cornconfs,Map<String,Integer> cornmap){
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			if(sheet.getRow(i)!=null){
				HSSFRow prow =sheet.getRow(i);
				String typeEnum=null;
				String dateTime=null;
				String weekTime=null;
				String corn=null;
				String appointTime=null;
				String appointSet=null;
				String jobCode=null;
				String systemCode=null;
				HSSFCell cell=prow.getCell(cornmap.get("timingType"));
				if(cell!=null){
					typeEnum=cell.getStringCellValue();
				}
				cell=prow.getCell(cornmap.get("dateTime"));
				if(cell!=null){
					dateTime=cell.getStringCellValue();
				}
				cell=prow.getCell(cornmap.get("weekTime"));
				if(cell!=null){
					weekTime=cell.getStringCellValue();
				}
				cell=prow.getCell(cornmap.get("corn"));
				if(cell!=null){
					corn=cell.getStringCellValue();
				}
				cell=prow.getCell(cornmap.get("appointTime"));
				if(cell!=null){
					appointTime=cell.getStringCellValue();
				}
				cell=prow.getCell(cornmap.get("appointSet"));
				if(cell!=null){
					appointSet=cell.getStringCellValue();
				}
				cell=prow.getCell(cornmap.get("jobCode"));
				if(cell!=null){
					jobCode=cell.getStringCellValue();
				}
				cell=prow.getCell(cornmap.get("systemCode"));
				if(cell!=null){
					systemCode=cell.getStringCellValue();
				}
				TimedTask jobInfo=jobInfoManager.getJobInfoByCode(jobCode, systemCode);
				if(jobInfo!=null){
					Timer cornInfo=jobInfoManager.getCornInfo(typeEnum, corn, dateTime, weekTime, appointTime, appointSet, jobInfo.getId());
					if(cornInfo==null)cornInfo=new Timer();
					cornInfo.setJobId(jobInfo.getId());
					if(StringUtils.isNotEmpty(appointSet))cornInfo.setAppointSet(appointSet);
					if(StringUtils.isNotEmpty(appointTime))cornInfo.setAppointTime(appointTime);
					if(StringUtils.isNotEmpty(corn))cornInfo.setCorn(corn);
					if(StringUtils.isNotEmpty(dateTime))cornInfo.setDateTime(dateTime);
					cornInfo.setTimingType(getTypeEnum(typeEnum));
					if(StringUtils.isNotEmpty(weekTime))cornInfo.setWeekTime(weekTime);
					jobInfoManager.saveCornInfo(cornInfo);
				}
			}
		}
	}
	
	private TimingType getTypeEnum(String typeEnum){
		if(TimingType.everyDate.toString().equals(typeEnum)){
			return TimingType.everyDate;
		}else if(TimingType.everyMonth.toString().equals(typeEnum)){
			return TimingType.everyMonth;
		}else if(TimingType.everyWeek.toString().equals(typeEnum)){
			return TimingType.everyWeek;
		}else if(TimingType.appointSet.toString().equals(typeEnum)){
			return TimingType.appointSet;
		}else if(TimingType.appointTime.toString().equals(typeEnum)){
			return TimingType.appointTime;
		}
		return null;
	}
	
	/**
	 * 导出国际化配置
	 */
	public void exportInternation(OutputStream fileOut,Long companyId){
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_INTERNATION']");
		List<DataSheetConfig> colConfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_INTERNATION_OPTION']");
		wb = new HSSFWorkbook();
		//数据表excel信息
    	HSSFSheet sheet=wb.createSheet("BS_INTERNATION");
        HSSFRow row = sheet.createRow(0);
        
        getFileHead(wb,row,confs);
        //数据表字段excel信息
        HSSFSheet colSheet=wb.createSheet("BS_INTERNATION_OPTION");
        HSSFRow colRow = colSheet.createRow(0);
        getFileHead(wb,colRow,colConfs);
        List<Internation> internations=null;
		internations=internationManager.getInternations();
		for(Internation inter:internations){
			internationInfo(inter,sheet,colSheet,confs,colConfs);
		}
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	
	private void internationInfo(Internation internation,HSSFSheet sheet,HSSFSheet colSheet,List<DataSheetConfig> confs,List<DataSheetConfig> colConfs){
		if(internation!=null){
			//表的信息
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
        		DataSheetConfig conf=confs.get(i);
        		if(!conf.isIgnore()){
    				setFieldValue(conf,i,rowi,internation);
        		}
        	}
			//字段的信息
			internationOptionInfo(internation,colSheet,colConfs);
		}
	}
	
	private void internationOptionInfo(Internation internation,HSSFSheet colSheet,List<DataSheetConfig> colConfs){
		List<InternationOption> interOpts=internationManager.getInternationOptions(internation.getId());
		com.norteksoft.product.api.entity.OptionGroup optGroup=ApiFactory.getSettingService().getOptionGroupByCode("internation");
		if(optGroup!=null){
			for(InternationOption opt:interOpts){
				Option option=optionGroupManager.getOptionById(opt.getCategory());
				if(option!=null){
					HSSFRow colrowi = colSheet.createRow(colSheet.getLastRowNum()+1);
					for(int i=0;i<colConfs.size();i++){
						DataSheetConfig conf=colConfs.get(i);
						if(!conf.isIgnore()){
							if("categoryOptionCode".equals(conf.getFieldName())){
								HSSFCell cell = colrowi.createCell(i);
								cell.setCellValue(option.getValue());
							}else if("categoryOptionName".equals(conf.getFieldName())){
								HSSFCell cell = colrowi.createCell(i);
								cell.setCellValue(option.getName());
							}else if("internationCode".equals(conf.getFieldName())){
								if(opt.getInternation()!=null){
									HSSFCell cell = colrowi.createCell(i);
									cell.setCellValue(opt.getInternation().getCode());
								}
							}else{
								setFieldValue(conf,i,colrowi,opt);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 导入国际化配置
	 * @param file
	 * @param companyId
	 */
	public void importInternation(File file,Long companyId){
		FileInputStream fis=null;
		InputStreamReader fr=null;
		BufferedReader br=null;
		try{
			fis=new FileInputStream(file);
			HSSFWorkbook wb=new HSSFWorkbook(fis);
			List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_INTERNATION']");
			List<DataSheetConfig> opconfs=getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_INTERNATION_OPTION']");
			Map<String,Integer> map=getIdentifier(confs);
			Map<String,Integer> opmap=getIdentifier(opconfs);
			if(companyId==null){
				List<Company> companys=companyManager.getCompanys();
				for(Company company:companys){
					ThreadParameters parameters=new ThreadParameters(company.getCompanyId());
					ParameterUtils.setParameters(parameters);
					//添加国际化设置
					addInternation(wb,confs,map);
					//添加国际化语言设置
					addInternationOption(wb,opconfs,opmap);
				}
			}else{
				ThreadParameters parameters=new ThreadParameters(companyId);
				ParameterUtils.setParameters(parameters);
				//添加国际化设置
				addInternation(wb,confs,map);
				//添加国际化语言设置
				addInternationOption(wb,opconfs,opmap);
			}
			clearCompanyId();
		}catch (FileNotFoundException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			try{
				if(br!=null)br.close();
				if(fr!=null)fr.close();
				if(fis!=null)fis.close();
			}catch(IOException ep){
				log.debug(PropUtils.getExceptionInfo(ep));
			}
		}
	}
	private void addInternation(HSSFWorkbook wb,List<DataSheetConfig> confs,Map<String,Integer> map){
		HSSFSheet sheet=wb.getSheet("BS_INTERNATION");
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			if(sheet.getRow(i)!=null){
				HSSFRow prow =sheet.getRow(i);
				String code=prow.getCell(map.get("code")).getStringCellValue();
				Internation internation=internationManager.getInternationByCode(code);
				if(internation==null) internation=new Internation();
				for(int j=0;j<confs.size();j++){
					DataSheetConfig conf=confs.get(j);
					if(!conf.isIgnore()){
						String fieldName=conf.getFieldName();
						String value=null;
						if(prow.getCell(j)!=null){
							value=prow.getCell(j).getStringCellValue();
						}
						if(StringUtils.isNotEmpty(value)){//导入数据
							setValue(internation,fieldName,conf.getDataType(),value,conf.getEnumName());
						}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
							setValue(internation,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
						}
					}
				}
				internationManager.save(internation);
			}
		}
	}
	private void addInternationOption(HSSFWorkbook wb,List<DataSheetConfig> confs,Map<String,Integer> map){
		HSSFSheet sheet=wb.getSheet("BS_INTERNATION_OPTION");
		com.norteksoft.product.api.entity.OptionGroup optGroup=ApiFactory.getSettingService().getOptionGroupByCode("internation");
		if(optGroup!=null){
			int firstRowNum = sheet.getFirstRowNum();
			int rowNum=sheet.getLastRowNum();
			for(int i=firstRowNum+1;i<=rowNum;i++){
				if(sheet.getRow(i)!=null){
					HSSFRow prow =sheet.getRow(i);
					String internationCode=prow.getCell(map.get("internationCode")).getStringCellValue();
					Internation internation=internationManager.getInternationByCode(internationCode);
					if(internation!=null){
						String categoryValue=prow.getCell(map.get("categoryOptionCode")).getStringCellValue();
						String name=prow.getCell(map.get("categoryOptionName")).getStringCellValue();
						String categoryName=prow.getCell(map.get("categoryName")).getStringCellValue();
						String internationOptValue=prow.getCell(map.get("value")).getStringCellValue();
						Option option=optionGroupManager.getOptionByInfo(categoryValue, name, "internation");
						if(option!=null){
							InternationOption interOpt=internationManager.getInternationOptionByInfo(option.getId(), categoryName,internationOptValue,internation.getCode());
							if(interOpt==null)interOpt=new InternationOption();
							interOpt.setCategory(option.getId());
							interOpt.setCategoryName(categoryName);
							interOpt.setValue(internationOptValue);
							interOpt.setInternation(internation);
							internationManager.saveInternationOption(interOpt);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 导出通用类型
	 */
	public void exportOperation(OutputStream fileOut,String systemIds,Long companyId){
		HSSFWorkbook wb;
    	List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_OPERATION']");
		wb = new HSSFWorkbook();
		HSSFSheet sheet=wb.createSheet("MMS_OPERATION");
        HSSFRow row = sheet.createRow(0);
        
        getFileHead(wb,row,confs);
        //导出通用类型
		List<Operation> operations=operationManager.getOperations(systemIds,companyId);
		operationInfo(operations,sheet,confs);
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	
	private void operationInfo(List<Operation> operations,HSSFSheet sheet,List<DataSheetConfig> confs){
		
		 for(Operation opera:operations){
	        	HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
	        	for(int i=0;i<confs.size();i++){
	        		DataSheetConfig conf=confs.get(i);
	        		if(!conf.isIgnore()){
	        			if("parentCode".equals(conf.getFieldName())){
	        				HSSFCell cell = rowi.createCell(i);
	        				if(opera.getParent()!=null){
	        					cell.setCellValue(opera.getParent().getCode());
	        				}else{
	        					cell.setCellValue("");
	        				}
	        			}else if(SYSTEM_CODE.equals(conf.getFieldName())){
	        				BusinessSystem system=businessSystemManager.getBusiness(opera.getSystemId());
	        				HSSFCell cell = rowi.createCell(i);
	        				if(system!=null){
	        					cell.setCellValue(system.getCode());
	        				}else{
	        					cell.setCellValue("");
	        				}
	        			}else{
	        				setFieldValue(conf,i,rowi,opera);
	        			}
	        		}
	        	}
	        }
	}
	
	/**
	 * 导入通用类型
	 */
	public void importOperation(File file,Long companyId){
		List<DataSheetConfig> confs=getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_OPERATION']");
		//注意menu的systemId，companyId
		FileInputStream fis=null;
 		InputStreamReader fr=null;
 		BufferedReader br=null;
 		HSSFWorkbook wb;
 			try {
				fis=new FileInputStream(file);
				wb = new HSSFWorkbook(fis);
				Map<String,Integer> map=getIdentifier(confs);
				//导入通用类型
				if(companyId==null){
					List<Company> companys=companyManager.getCompanys();
					for(Company company:companys){
						ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
						ParameterUtils.setParameters(parameters);
						addOperation(wb,confs,map);
					}
				}else{
					ThreadParameters parameters=new ThreadParameters(companyId,null);
					ParameterUtils.setParameters(parameters);
					addOperation(wb,confs,map);
				}
				clearCompanyId();
			} catch (FileNotFoundException e) {
				log.debug(PropUtils.getExceptionInfo(e));
			}catch (IOException e){
				log.debug(PropUtils.getExceptionInfo(e));
			}finally{
	 			try{
		 			if(br!=null)br.close();
		 			if(fr!=null)fr.close();
		 			if(fis!=null)fis.close();
	 			}catch(IOException ep){
	 				log.debug(PropUtils.getExceptionInfo(ep));
	 			}
	 		}
	}
	
	private void addOperation(HSSFWorkbook wb,List<DataSheetConfig> confs,Map<String,Integer> map){
		HSSFSheet sheet=wb.getSheet("MMS_OPERATION");
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			HSSFRow row =sheet.getRow(i);
			if(sheet.getRow(i)!=null){
				Integer index=map.get("code");//获得标识字段的位置
				String code=row.getCell(index).getStringCellValue();//获得code
				index=map.get(SYSTEM_CODE);//获得标识字段的位置
				String systemCode=row.getCell(index).getStringCellValue();//获得code
				BusinessSystem system=businessSystemManager.getSystemBySystemCode(systemCode);
				if(system!=null){
					Operation opera=operationManager.getOperationByCode(code,system.getId());//根据code查询菜单是否已存在
					if(opera==null){
						opera=new Operation();
					}
					opera.setCompanyId(ContextUtils.getCompanyId());
					opera.setCreatedTime(new Date());
					for(int j=0;j<confs.size();j++){
						DataSheetConfig conf=confs.get(j);
						if(!conf.isIgnore()){
							String fieldName=conf.getFieldName();
							String value=null;
							if(row.getCell(j)!=null){
								value=row.getCell(j).getStringCellValue();
							}
							if("parentCode".equals(fieldName)){
								Operation parentOpera=operationManager.getOperationByCode(code,system.getId());
								if(parentOpera!=null){
									opera.setParent(parentOpera);
								}
							}else if(SYSTEM_CODE.equals(fieldName)){
								opera.setSystemId(system.getId());
							}else{
								if(StringUtils.isNotEmpty(value)){//导入数据
									setValue(opera,fieldName,conf.getDataType(),value,conf.getEnumName());
								}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
									setValue(opera,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
								}
							}
						}
					}
					operationManager.save(opera);
				}
			}
		}
	}
	
	/**
	 * 根据导入位置获得文件配置
	 * @param importPosition
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<FileConfigModel> getFileConfigByCategory(String category){
		List<FileConfigModel> fileConfigs=new ArrayList<FileConfigModel>();
		InputStreamReader isreader=null;
		 try {
		    	SAXReader reader = new SAXReader();
		    	isreader=new InputStreamReader(DataHandle.class.getClassLoader().getResourceAsStream("dataSheetConfig.xml"),"UTF-8");
		    	Document document=reader.read(isreader);
		    	//获得导出的根节点
		    	String[] rootPaths=getRootPath();
		    	String exportRootPath=rootPaths[0];
		    	String importRootPath=rootPaths[1];
		    	
				List<Element> tableList = document.selectNodes("data-sheets/file-configs/file-config[@category='"+category+"']");
				Iterator it = tableList.iterator();
				while(it.hasNext()){//只会循环一次
					Element menuEle = (Element)it.next();
					fileConfigs.add(getConfig(menuEle,exportRootPath,importRootPath));
				}
		    }catch (Exception e) {
		    	log.debug(PropUtils.getExceptionInfo(e));
			}finally{
				try {
					if(isreader!=null)isreader.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
			}
		    
		    //按importOrder升序排序
		    Collections.sort(fileConfigs, new Comparator<FileConfigModel>() {
				public int compare(FileConfigModel tc1, FileConfigModel tc2) {
					return tc1.getImportOrder()-tc2.getImportOrder();
				}
			});
		    return fileConfigs;
	}
	/**
	 * 根据数据类型获得文件配置
	 * @param dataCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public FileConfigModel getFileConfigByData(String dataCode){
		InputStreamReader isreader=null;
		 try {
		    	SAXReader reader = new SAXReader();
		    	isreader=new InputStreamReader(DataHandle.class.getClassLoader().getResourceAsStream("dataSheetConfig.xml"),"UTF-8");
		    	Document document=reader.read(isreader);
		    	//获得导出的根节点
		    	String[] rootPaths=getRootPath();
		    	String exportRootPath=rootPaths[0];
		    	String importRootPath=rootPaths[1];
		    	
		    	List<Element> tableList = document.selectNodes("data-sheets/file-configs/file-config[@data='"+dataCode+"']");
		    	Iterator it = tableList.iterator();
				while(it.hasNext()){//只会循环一次
					Element menuEle = (Element)it.next();
					return getConfig(menuEle,exportRootPath,importRootPath);
				}
		    }catch (Exception e) {
		    	log.debug(PropUtils.getExceptionInfo(e));
			}finally{
				try {
					if(isreader!=null)isreader.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
			}
		    return null;
	}
	@SuppressWarnings("unchecked")
	private  FileConfigModel getConfig(Element menuEle,String exportRootPath,String importRootPath){
		//得到fileConfig的属性
		   List<Attribute> columnAttributes = menuEle.attributes();
		   FileConfigModel conf=new FileConfigModel();
		   if(StringUtils.isNotEmpty(exportRootPath)){
			   conf.setExportRootPath(exportRootPath);
		   }
		   if(StringUtils.isNotEmpty(importRootPath)){
			   conf.setImportRootPath(importRootPath);
		   }
		   for(int i=0;i<columnAttributes.size();i++){
			   String attributeName = columnAttributes.get(i).getName();
			   if(FILE_BEANNAME.equals(attributeName)){
				   String beanname = columnAttributes.get(i).getValue();
				   conf.setBeanname(beanname);
			   }else if(FILE_TITLE.equals(attributeName)){
				   String title = columnAttributes.get(i).getValue();
				   conf.setTitle(title);
			   }else if(FILE_EXPORT_PATH.equals(attributeName)){
				   String exportPath = columnAttributes.get(i).getValue();
				   conf.setExportPath(exportPath);
			   }else if(FILE_IMPORT_ORDER.equals(attributeName)){
				   String importOrder = columnAttributes.get(i).getValue();
				   if(StringUtils.isNotEmpty(importOrder)){
					   conf.setImportOrder(Integer.parseInt(importOrder));
				   }
			   }else if(FILE_IMPORT_PATH.equals(attributeName)){
				   String importPath = columnAttributes.get(i).getValue();
				   conf.setImportPath(importPath);
			   }else if(FILE_NAME.equals(attributeName)){
				   String filename = columnAttributes.get(i).getValue();
				   conf.setFilename(filename);
			   }else if(FILE_NAME_STARTWITH.equals(attributeName)){
				   String filenameStartwith = columnAttributes.get(i).getValue();
				   conf.setFilenameStartwith(filenameStartwith);
			   }else if(FILE_CATEGORY.equals(attributeName)){
				   String category = columnAttributes.get(i).getValue();
				   conf.setCategory(category);
			   }else if(FILE_DATA.equals(attributeName)){
				   String data = columnAttributes.get(i).getValue();
				   conf.setData(data);
			   }
		   }
		   return conf;
	}
	/**
	 * 获得导入导出根路径
	 * @param document
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String[] getRootPath(){
		InputStreamReader isreader=null;
		String[] rootPaths=new String[2];
		 try {
		    	SAXReader reader = new SAXReader();
		    	isreader=new InputStreamReader(DataHandle.class.getClassLoader().getResourceAsStream("dataSheetConfig.xml"),"UTF-8");
		    	Document document=reader.read(isreader);
		    	List<Element> tableList = document.selectNodes("data-sheets/file-configs");
		    	String exportRootPath=null;
		    	String importRootPath=null;
		    	Iterator it = tableList.iterator();
		    	while(it.hasNext()){//只会循环一次
		    		Element menuEle = (Element)it.next();
		    		//得到fileConfig的属性
		    		List<Attribute> columnAttributes = menuEle.attributes();
		    		for(int i=0;i<columnAttributes.size();i++){
		    			String attributeName = columnAttributes.get(i).getName();
		    			if(FILE_EXPORT_PATH.equals(attributeName)){
		    				exportRootPath=columnAttributes.get(i).getValue();
		    			}else if(FILE_IMPORT_PATH.equals(attributeName)){
		    				importRootPath=columnAttributes.get(i).getValue();
		    			}
		    		}
		    	}
		    	rootPaths[0]=StringUtils.isEmpty(exportRootPath)?"basic-data":exportRootPath;
		    	rootPaths[1]=StringUtils.isEmpty(importRootPath)?"basic-data-temp":importRootPath;
		    	return rootPaths;
		 }catch (Exception e) {
		}finally{
			try {
				if(isreader!=null)isreader.close();
			} catch (IOException e) {
				log.debug(PropUtils.getExceptionInfo(e));
			}
		}
		 return rootPaths;
	}
	/**
	 * 导出数据处理
	 * @param fileOut
	 * @param systemIds
	 * @param companyId
	 * @param dataCodes
	 */
	public void exportExecute(OutputStream fileOut,String systemIds,Long companyId,String dataCodes){
		//将数据导出到文件夹中
		if(StringUtils.isEmpty(dataCodes)){
			List<FileConfigModel> result=new ArrayList<FileConfigModel>();
			List<FileConfigModel> acsFileConfigs=getFileConfigByCategory("basicData");
			List<FileConfigModel> initFileConfigs=getFileConfigByCategory("initData");
			result.addAll(acsFileConfigs);
			result.addAll(initFileConfigs);
			for(FileConfigModel config:result){
				if(StringUtils.isNotEmpty(config.getBeanname())){
					//创建导出文件夹，导出的文件暂存的位置
					File folder = new File(config.getExportRootPath()+"/"+config.getExportPath());
					if(!folder.exists()){
						folder.mkdirs();
					}
					
					DataTransfer bean=(DataTransfer)ContextUtils.getBean(config.getBeanname());
					bean.backup(systemIds, companyId, config);
				}
			}
		}else{
			String[] codes=dataCodes.split(",");
			for(String dataCode:codes){
				FileConfigModel config=getFileConfigByData(dataCode);
				if(StringUtils.isNotEmpty(config.getBeanname())){
					//创建导出文件夹，导出的文件暂存的位置
					File folder = new File(config.getExportRootPath()+"/"+config.getExportPath());
					if(!folder.exists()){
						folder.mkdirs();
					}
					
					DataTransfer bean=(DataTransfer)ContextUtils.getBean(config.getBeanname());
					bean.backup(systemIds, companyId, config);
				}
			}
		}
		
    	//获得导出的根节点
    	String[] rootPaths=getRootPath();
    	String exportRootPath=rootPaths[0];
    	
		//将生成的文件夹打成zip包且删除暂时文件夹
		 try {
			 ZipUtils.zipFolder(exportRootPath, fileOut);
		} catch (Exception e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			try {
				if(fileOut!=null)fileOut.close();
				
				FileUtils.deleteDirectory(new File(exportRootPath));
			}catch (Exception e) {
				log.debug(PropUtils.getExceptionInfo(e));
			}
		}
	}
	/**
	 * 导入数据处理
	 * @param file
	 * @param companyId
	 * @param importPosition
	 * @param imatrixInfo
	 */
	public void importExecute(File file,Long companyId,String category,String... imatrixInfo){
		try {
	    	//获得导出的根节点
	    	String[] rootPaths=getRootPath();
	    	String importRootPath=rootPaths[1];
	    	
			ZipFile zipFile = new ZipFile(file);
			ZipUtils.unZipFileByOpache(zipFile, importRootPath); 
			
			List<FileConfigModel>fileConfigs=getFileConfigByCategory(category);
			for(FileConfigModel config:fileConfigs){
				if(StringUtils.isNotEmpty(config.getBeanname())){
					DataTransfer bean=(DataTransfer)ContextUtils.getBean(config.getBeanname());
					bean.restore(companyId, config,imatrixInfo);
				}
			}
			FileUtils.deleteDirectory(new File(importRootPath));
		}catch (Exception e) {
			log.debug(PropUtils.getExceptionInfo(e));
		} 
	}
	/**
	 * 获得数据类型列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<FileConfigModel> getBasicDataTypes(){
		List<FileConfigModel> fileConfigs=new ArrayList<FileConfigModel>();
		InputStreamReader isreader=null;
		 try {
		    	SAXReader reader = new SAXReader();
		    	isreader=new InputStreamReader(DataHandle.class.getClassLoader().getResourceAsStream("dataSheetConfig.xml"),"UTF-8");
		    	Document document=reader.read(isreader);
		    	
				List<Element> tableList = document.selectNodes("data-sheets/file-configs/file-config");
				Iterator it = tableList.iterator();
				while(it.hasNext()){//只会循环一次
					Element menuEle = (Element)it.next();
					//得到fileConfig的属性
				   List<Attribute> columnAttributes = menuEle.attributes();
				   FileConfigModel conf=new FileConfigModel();
				   Attribute attr=menuEle.attribute("visible");
				   String visible="true";
				   if(attr!=null){
					   visible=attr.getValue();
				   }
				   if("true".equals(visible)){//是否是基础数据类型，true表示是，false不是，默认是true
					   for(int i=0;i<columnAttributes.size();i++){
						   String attributeName = columnAttributes.get(i).getName();
						   if(FILE_DATA.equals(attributeName)){
							   String data = columnAttributes.get(i).getValue();
							   conf.setData(data);
						   }else if(FILE_TITLE.equals(attributeName)){
							   String title = columnAttributes.get(i).getValue();
							   conf.setTitle(title);
						   }
					   }
					   fileConfigs.add(conf);
				   }
				}
		    }catch (Exception e) {
		    	log.debug(PropUtils.getExceptionInfo(e));
			}finally{
				try {
					if(isreader!=null)isreader.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
			}
		    return fileConfigs;
	}
	
}

