package com.norteksoft.product.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.norteksoft.mms.form.dao.GeneralDao;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.service.FormHtmlParser;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.product.web.struts2.Struts2Utils;

public class JsonParser {
	
	private static Log logger = LogFactory.getLog(JsonParser.class);
	private static final String DATA_FORMART = "yyyy-MM-dd";
	private static final String TIME_FORMART = "yyyy-MM-dd HH:mm";
	/**
	 * 表单页面中有子表表格时，保存主表单时获得子表格中所有字段值并保存，此处解析字段值
	 * 只有一个子表时
	 * 解析字符串例如:[{"id":"52521","useType":"短途","deptCheckResult":"","officeCheckResult":"王滨","leaderCheckResult":"","isDirectAssign":"否","applicationTheme":"汤祁中的用车申请"},{"id":"76991","useType":"短途","deptCheckResult":"","officeCheckResult":"王滨","leaderCheckResult":"","isDirectAssign":"否","applicationTheme":"王滨的用车申请"},{"id":"76835","useType":"短途","deptCheckResult":"张君正","officeCheckResult":"王滨","leaderCheckResult":"","isDirectAssign":"否","applicationTheme":"何庆的用车申请"},{"id":"75200","useType":"短途","deptCheckResult":"","officeCheckResult":"王滨","leaderCheckResult":"","isDirectAssign":"否","applicationTheme":"王滨的用车申请"},{"id":"76235","useType":"短途","deptCheckResult":"","officeCheckResult":"王滨","leaderCheckResult":"","isDirectAssign":"否","applicationTheme":"王滨的用车申请"},{"id":"91716","useType":"短途","deptCheckResult":"","officeCheckResult":"","leaderCheckResult":"","isDirectAssign":"否","applicationTheme":"王滨的用车申请"},{"id":"108380","useType":"短途","deptCheckResult":"","officeCheckResult":"","leaderCheckResult":"","isDirectAssign":"是","applicationTheme":"王滨的用车申请"}];aa=[{"a":"1","b":"2"}]
	 * @return List<Map<String,Object>>,List存放实体对象的集合,Map<String,Object>的key为字段名称,value为字段值，
	 */
	@SuppressWarnings("unchecked")
	public static List<Object> getFormTableDatas(Class classObj){
		String value=Struts2Utils.getParameter("subTableVals");
//		String value="carUseApplication=[{\"id\":\"76235\",\"useType\":{\"value\":\"短途\",\"datatype\":\"TEXT\"},\"deptCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"officeCheckResult\":{\"value\":\"王滨\",\"datatype\":\"TEXT\"},\"leaderCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"isDirectAssign\":{\"value\":\"false\",\"datatype\":\"BOOLEAN\"},\"applicationTheme\":{\"value\":\"王滨的用车申请\",\"datatype\":\"TEXT\"}},{\"id\":\"91716\",\"useType\":{\"value\":\"短途\",\"datatype\":\"TEXT\"},\"deptCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"officeCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"leaderCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"isDirectAssign\":{\"value\":\"false\",\"datatype\":\"BOOLEAN\"},\"applicationTheme\":{\"value\":\"王滨的用车申请\",\"datatype\":\"TEXT\"}},{\"id\":\"108380\",\"useType\":{\"value\":\"短途\",\"datatype\":\"TEXT\"},\"deptCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"officeCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"leaderCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"isDirectAssign\":{\"value\":\"false\",\"datatype\":\"BOOLEAN\"},\"applicationTheme\":{\"value\":\"王滨的用车申请\",\"datatype\":\"TEXT\"}},{\"id\":\"52521\",\"useType\":{\"value\":\"短途\",\"datatype\":\"TEXT\"},\"deptCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"officeCheckResult\":{\"value\":\"王滨\",\"datatype\":\"TEXT\"},\"leaderCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"isDirectAssign\":{\"value\":\"false\",\"datatype\":\"BOOLEAN\"},\"applicationTheme\":{\"value\":\"汤祁中的用车申请\",\"datatype\":\"TEXT\"}},{\"id\":\"76991\",\"useType\":{\"value\":\"短途\",\"datatype\":\"TEXT\"},\"deptCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"officeCheckResult\":{\"value\":\"王滨\",\"datatype\":\"TEXT\"},\"leaderCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"isDirectAssign\":{\"value\":\"false\",\"datatype\":\"BOOLEAN\"},\"applicationTheme\":{\"value\":\"王滨的用车申请\",\"datatype\":\"TEXT\"}},{\"id\":\"112366\",\"useType\":{\"value\":\"短途\",\"datatype\":\"TEXT\"},\"deptCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"officeCheckResult\":{\"value\":\"王滨\",\"datatype\":\"TEXT\"},\"leaderCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"isDirectAssign\":{\"value\":\"false\",\"datatype\":\"BOOLEAN\"},\"applicationTheme\":{\"value\":\"王滨的用车申请\",\"datatype\":\"TEXT\"}},{\"id\":\"76835\",\"useType\":{\"value\":\"短途\",\"datatype\":\"TEXT\"},\"deptCheckResult\":{\"value\":\"张君正\",\"datatype\":\"TEXT\"},\"officeCheckResult\":{\"value\":\"王滨\",\"datatype\":\"TEXT\"},\"leaderCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"isDirectAssign\":{\"value\":\"false\",\"datatype\":\"BOOLEAN\"},\"applicationTheme\":{\"value\":\"何庆的用车申请\",\"datatype\":\"TEXT\"}},{\"id\":\"75200\",\"useType\":{\"value\":\"短途\",\"datatype\":\"TEXT\"},\"deptCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"officeCheckResult\":{\"value\":\"王滨\",\"datatype\":\"TEXT\"},\"leaderCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"isDirectAssign\":{\"value\":\"false\",\"datatype\":\"BOOLEAN\"},\"applicationTheme\":{\"value\":\"王滨的用车申请\",\"datatype\":\"TEXT\"}}]";
		List<Object> list=new ArrayList<Object>();
		String indexname=value.substring(0, value.indexOf("=")).split(":")[1];
		String jsonString=value.substring(value.indexOf("=")+1,value.length());
		jsonString=PageUtils.disposeSpecialCharacter(jsonString);
		if(jsonString!=null&&StringUtils.isNotEmpty(jsonString.toString())){
			list= getValue(jsonString,classObj,indexname);
		}
		return list;
	}
	/**
	 * 表单页面中有子表表格时，保存主表单时获得子表格中所有字段值并保存，此处解析字段值
	 * 考虑到一个表单页面可能有多个子表，所以有以下解法
	 * 解析字符串例如:carUseApplication=[{"id":"52521","useType":{"value":"短途","datatype":"STRING"},"deptCheckResult":"","officeCheckResult":"王滨","leaderCheckResult":"","isDirectAssign":"否","applicationTheme":"汤祁中的用车申请"},{"id":"76991","useType":"短途","deptCheckResult":"","officeCheckResult":"王滨","leaderCheckResult":"","isDirectAssign":"否","applicationTheme":"王滨的用车申请"},{"id":"76835","useType":"短途","deptCheckResult":"张君正","officeCheckResult":"王滨","leaderCheckResult":"","isDirectAssign":"否","applicationTheme":"何庆的用车申请"},{"id":"75200","useType":"短途","deptCheckResult":"","officeCheckResult":"王滨","leaderCheckResult":"","isDirectAssign":"否","applicationTheme":"王滨的用车申请"},{"id":"76235","useType":"短途","deptCheckResult":"","officeCheckResult":"王滨","leaderCheckResult":"","isDirectAssign":"否","applicationTheme":"王滨的用车申请"},{"id":"91716","useType":"短途","deptCheckResult":"","officeCheckResult":"","leaderCheckResult":"","isDirectAssign":"否","applicationTheme":"王滨的用车申请"},{"id":"108380","useType":"短途","deptCheckResult":"","officeCheckResult":"","leaderCheckResult":"","isDirectAssign":"是","applicationTheme":"王滨的用车申请"}];aa=[{"a":"1","b":"2"}]
	 * @return Map<String,List<Map<String,Object>>> ：key 为集合字段名;value为List<Map<String,Object>>,List存放实体对象的集合,Map<String,Object>的key为字段名称,value为字段值，
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,List<Object>> getAllFormTableDatas(Class classObj){
		String values=Struts2Utils.getParameter("subTableVals");
//		String values="carUseApplication=[{\"id\":\"76235\",\"useType\":{\"value\":\"短途\",\"datatype\":\"TEXT\"},\"deptCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"officeCheckResult\":{\"value\":\"王滨\",\"datatype\":\"TEXT\"},\"leaderCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"isDirectAssign\":{\"value\":\"false\",\"datatype\":\"BOOLEAN\"},\"applicationTheme\":{\"value\":\"王滨的用车申请\",\"datatype\":\"TEXT\"}},{\"id\":\"91716\",\"useType\":{\"value\":\"短途\",\"datatype\":\"TEXT\"},\"deptCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"officeCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"leaderCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"isDirectAssign\":{\"value\":\"false\",\"datatype\":\"BOOLEAN\"},\"applicationTheme\":{\"value\":\"王滨的用车申请\",\"datatype\":\"TEXT\"}},{\"id\":\"108380\",\"useType\":{\"value\":\"短途\",\"datatype\":\"TEXT\"},\"deptCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"officeCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"leaderCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"isDirectAssign\":{\"value\":\"false\",\"datatype\":\"BOOLEAN\"},\"applicationTheme\":{\"value\":\"王滨的用车申请\",\"datatype\":\"TEXT\"}},{\"id\":\"52521\",\"useType\":{\"value\":\"短途\",\"datatype\":\"TEXT\"},\"deptCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"officeCheckResult\":{\"value\":\"王滨\",\"datatype\":\"TEXT\"},\"leaderCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"isDirectAssign\":{\"value\":\"false\",\"datatype\":\"BOOLEAN\"},\"applicationTheme\":{\"value\":\"汤祁中的用车申请\",\"datatype\":\"TEXT\"}},{\"id\":\"76991\",\"useType\":{\"value\":\"短途\",\"datatype\":\"TEXT\"},\"deptCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"officeCheckResult\":{\"value\":\"王滨\",\"datatype\":\"TEXT\"},\"leaderCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"isDirectAssign\":{\"value\":\"false\",\"datatype\":\"BOOLEAN\"},\"applicationTheme\":{\"value\":\"王滨的用车申请\",\"datatype\":\"TEXT\"}},{\"id\":\"112366\",\"useType\":{\"value\":\"短途\",\"datatype\":\"TEXT\"},\"deptCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"officeCheckResult\":{\"value\":\"王滨\",\"datatype\":\"TEXT\"},\"leaderCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"isDirectAssign\":{\"value\":\"false\",\"datatype\":\"BOOLEAN\"},\"applicationTheme\":{\"value\":\"王滨的用车申请\",\"datatype\":\"TEXT\"}},{\"id\":\"76835\",\"useType\":{\"value\":\"短途\",\"datatype\":\"TEXT\"},\"deptCheckResult\":{\"value\":\"张君正\",\"datatype\":\"TEXT\"},\"officeCheckResult\":{\"value\":\"王滨\",\"datatype\":\"TEXT\"},\"leaderCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"isDirectAssign\":{\"value\":\"false\",\"datatype\":\"BOOLEAN\"},\"applicationTheme\":{\"value\":\"何庆的用车申请\",\"datatype\":\"TEXT\"}},{\"id\":\"75200\",\"useType\":{\"value\":\"短途\",\"datatype\":\"TEXT\"},\"deptCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"officeCheckResult\":{\"value\":\"王滨\",\"datatype\":\"TEXT\"},\"leaderCheckResult\":{\"value\":\"\",\"datatype\":\"TEXT\"},\"isDirectAssign\":{\"value\":\"false\",\"datatype\":\"BOOLEAN\"},\"applicationTheme\":{\"value\":\"王滨的用车申请\",\"datatype\":\"TEXT\"}}]";
		Map<String,List<Object>> result=new HashMap<String,List<Object>>();
		String[] vals=values.split(";");
		for(String valStr:vals){
			String[] arr=valStr.split("=");
			String field=arr[0].split(":")[0];
			String indexname=arr[0].split(":")[1];
			String val=arr[1];
			if(val!=null&&StringUtils.isNotEmpty(val.toString())){
				List<Object> list=getValue(val,classObj,indexname);
				result.put(field, list);
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private static List<Object> getValue(String jsonString,Class classObj,String indexname){
		List<Object> list=new ArrayList<Object>();
		GeneralDao generalDao = (GeneralDao) ContextUtils.getBean("generalDao");
		try{
			MapType mt = TypeFactory.defaultInstance().constructMapType(
					HashMap.class, String.class, ColunmModule.class);
			CollectionType ct = TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, mt);
			List<Map<String, ColunmModule>> objs = json2Object(ct, jsonString);
			int index = 0;
			Object entity=null;
			for(Map<String, ColunmModule> map : objs){
				index++;
				Object idObj=map.get("id").getValue();
				if(idObj==null||StringUtils.isEmpty(idObj.toString())){
					entity=classObj.newInstance();
				}else{
					entity=generalDao.getObject(classObj.getName(), Long.parseLong(idObj.toString()));
				}
				
				Set<String> fields=map.keySet();
				for(String field : fields){
					ColunmModule mod = map.get(field);
					if(!"id".equals(field)){
						if(shouldSaveValue(map,field)){
							Object valObj=getObjectValue(mod.getValue(), mod.getDatatype(), mod.getClassname());
							if(valObj!=null){
								if("".equals(valObj)){
									PropertyUtils.setProperty(entity, field, null);
								}else{
									BeanUtils.copyProperty(entity, field, valObj);
								}
							}else{
								if(DataType.DATE.toString().equals(mod.getDatatype())||DataType.TIME.toString().equals(mod.getDatatype())){//当是Date类型时copyProperty对应空值null的保存有异常，所以用以下方法
									PropertyUtils.setProperty(entity, field, valObj);
								}else{
									BeanUtils.copyProperty(entity, field, valObj);
								}
							}
						}
					}
				}
				//设置显示顺序
				if(!indexname.equals("false")){
					BeanUtils.copyProperty(entity, indexname, index);
				}
				list.add(entity);
			}
			
		}catch (Exception e) {
		}
		return list;
	}
	
	/**
	 * 是否需要保存值
	 * @return true为需要，false为不需要，只有当是“引用类型"的"某个字段"时不需要保存
	 */
	private static boolean shouldSaveValue(Map<String, ColunmModule> map,String fieldName){
		if(fieldName.contains(".")){
			String field=fieldName.substring(0, fieldName.lastIndexOf("."));//aa.bb.cc.dd,获得aa.bb.cc(该字段需要在字段信息列表中设为引用)
			Set<String> referenceNames=getReferenceNames(map);
			if(referenceNames.contains(field)){
				return false;
			}
		}
		return true;
		
	}
	private static Set<String> getReferenceNames(Map<String, ColunmModule> map){
		Set<String> referenceNames=new HashSet<String>();
		Set<String> fields=map.keySet();
		for(String field : fields){
			ColunmModule mod = map.get(field);
			if(DataType.REFERENCE.toString().equals(mod.getDatatype())){
				referenceNames.add(field);
			}
		}
		return referenceNames;
	}
	/**
	 * 获得colModel对应的值
	 * @param entity
	 * @return
	 */
	public static String getRowValue(Object entity){
		String listCode=Struts2Utils.getParameter("_list_code");
		ListViewManager listViewManager = (ListViewManager) ContextUtils.getBean("listViewManager");
		ListView listView=listViewManager.getListViewByCode(listCode);
		List<ListColumn> columns=listView.getColumns();
		FormHtmlParser formHtmlParser = (FormHtmlParser) ContextUtils.getBean("formHtmlParser");
		return formHtmlParser.getScriptObject(entity,columns);
		
	}
	
	public static Object getObjectValue(String value,String datatype,String className){
		try {
			Object valObj=value;
			if("DATE".equals(datatype)||"TIME".equals(datatype)){
				if(StringUtils.isEmpty(value))return null;
				valObj=getDate(value,datatype);
			}else if("REFERENCE".equals(datatype)){
				if("_temporary".equals(value)){//列表管理/字段信息/对应字段列为“占位符”时的保存
					valObj=null;
				}else{
					valObj=getReferenceObject(value, className);
				}
			}else if("ENUM".equals(datatype)){
				valObj=getEnum(value, className);
			}
			return valObj;
		} catch (Exception e) {
		}
		return null;
	}
	
	private static Date getDate(String value,String datatype){
		try {
			if(value!=null){
				if(datatype.equals("DATE")){
					SimpleDateFormat df = new SimpleDateFormat(DATA_FORMART);    
					return df.parse(value);
				}else if(datatype.equals("TIME")){
					SimpleDateFormat df = new SimpleDateFormat(TIME_FORMART);    
					return df.parse(value);
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static Object getEnum(String value,String className){
		try {
			if(StringUtils.isNotEmpty(className)){
				Object[] objs = Class.forName(className).getEnumConstants();
				for(Object obj : objs){
					if(obj.toString().equals(value.toString())){
						return obj;
					}
				}
			}
		}catch (Exception e) {
		}
		return null;
		
	}
	/**
	 * 获得映射的实体
	 * @param value
	 * @param className
	 * @return
	 */
	private static Object getReferenceObject(String value,String className){
		try {
			if(StringUtils.isNotEmpty(className)){
				GeneralDao generalDao = (GeneralDao) ContextUtils.getBean("generalDao");
				Object entity=generalDao.getObject(className,Long.parseLong(value));
				return entity;
			}
		}catch (Exception e) {
		}
		return null;
	}
	
	
	 protected final static ObjectMapper defaultMapper;//= new ObjectMapper();
		static{
			defaultMapper = new ObjectMapper();
			defaultMapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
			defaultMapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		}
	
	/**
	 * 将对象转换为JSON串
	 * @param obj
	 * @return
	 */
	public static String object2Json(Object obj){
		defaultMapper.setDateFormat(new SimpleDateFormat(DATA_FORMART));
		return write(obj);
	}
	
	public static String object2Json(Object obj, String dataFormart){
		defaultMapper.setDateFormat(new SimpleDateFormat(dataFormart));
		return write(obj);
	}
	
	/**
	 * 将JSON串转换为Map
	 * @param keyType
	 * @param valueType
	 * @param json
	 * @return
	 */
	public static <K,V> Map<K,V> json2Map(Class<K> keyType, Class<V> valueType, String json){
		MapType mt = TypeFactory.defaultInstance().constructMapType(
				HashMap.class, keyType, valueType);
		try {
			return defaultMapper.readValue(json, mt);
		} catch (JsonParseException e) {
			logger.error(e, e);
			throw new RuntimeException("JsonParseException", e);
		} catch (JsonMappingException e) {
			logger.error(e, e);
			throw new RuntimeException("JsonMappingException", e);
		} catch (IOException e) {
			logger.error(e, e);
			throw new RuntimeException("IOException", e);
		}
	}
	
	/**
	 * 将JSON串转换为List
	 * @param elementType List元素类型
	 * @param json        需转换的JSON
	 * @return
	 */
	public static <T> List<T> json2List(Class<T> elementType, String json){
		CollectionType ct = TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, elementType);
		return json2Object(ct, json);
	}
	
	public static <T> T json2Object(JavaType valueType, String json){
		defaultMapper.setDateFormat(new SimpleDateFormat(DATA_FORMART));
		try {
			return (T)defaultMapper.readValue(json, valueType);
		} catch (JsonParseException e) {
			logger.error(e, e);
			throw new RuntimeException("JsonParseException", e);
		} catch (JsonMappingException e) {
			logger.error(e, e);
			throw new RuntimeException("JsonMappingException", e);
		} catch (IOException e) {
			logger.error(e, e);
			throw new RuntimeException("IOException", e);
		}
	}
	
	public static <T> T json2Object(Class<T> valueType, String json){
		defaultMapper.setDateFormat(new SimpleDateFormat(DATA_FORMART));
		try {
			return defaultMapper.readValue(json, valueType);
		} catch (JsonParseException e) {
			logger.error(e, e);
			throw new RuntimeException("JsonParseException", e);
		} catch (JsonMappingException e) {
			logger.error(e, e);
			throw new RuntimeException("JsonMappingException", e);
		} catch (IOException e) {
			logger.error(e, e);
			throw new RuntimeException("IOException", e);
		}
	}
	
	private static String write(Object obj){
		try {
			return defaultMapper.writeValueAsString(obj);
		} catch (JsonGenerationException e) {
			logger.error(e, e);
			throw new RuntimeException("JsonGenerationException", e);
		} catch (JsonMappingException e) {
			logger.error(e, e);
			throw new RuntimeException("JsonMappingException", e);
		} catch (IOException e) {
			logger.error(e, e);
			throw new RuntimeException("IOException", e);
		}
	}
}

class ColunmModule{
	private String value;
	private String datatype;
	private String classname;
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	@Override
	public String toString() {
		return "ColunmModule [classname=" + classname + ", datatype="
				+ datatype + ", value=" + value + "]";
	}
}