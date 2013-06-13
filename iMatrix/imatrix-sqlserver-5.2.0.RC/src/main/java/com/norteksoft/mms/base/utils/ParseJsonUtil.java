package com.norteksoft.mms.base.utils;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.norteksoft.mms.form.dao.GeneralDao;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.product.util.WebContextUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;

/**
 *  已经被 com.norteksoft.product.util.JsonParser 替代，请更换
 */
@Deprecated
public class ParseJsonUtil {
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
		String[] arr=value.split("=");
		String indexname=arr[0].split(":")[1];
		String jsonString=arr[1];
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
		GeneralDao generalDao = (GeneralDao) WebContextUtils.getBean("generalDao");
		try{
			ObjectMapper mapper = new ObjectMapper();
			MapType mt = TypeFactory.defaultInstance().constructMapType(
					HashMap.class, String.class, ColunmModule.class);
			CollectionType ct = TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, mt);
			List<Map<String, ColunmModule>> objs = mapper.readValue(jsonString, ct);
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
						if(!field.contains(".")){
							Object valObj=getObjectValue(mod.getValue(), mod.getDatatype(), mod.getClassname());
							BeanUtils.copyProperty(entity, field, valObj);
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
	 * 获得colModel对应的值
	 * @param entity
	 * @return
	 */
	public static String getRowValue(Object entity){
		String listCode=Struts2Utils.getParameter("_list_code");
		ListViewManager listViewManager = (ListViewManager) WebContextUtils.getBean("listViewManager");
		ListView listView=listViewManager.getListViewByCode(listCode);
		List<ListColumn> columns=listView.getColumns();
		StringBuilder json = new StringBuilder();
		json.append("{");
		try {
			String entityId=BeanUtils.getProperty(entity, "id");
			json.append("\"id\":\"");
			json.append(entityId);
			json.append("\"");
			for(ListColumn lc:columns){
				String attributeName=lc.getTableColumn().getName();
				DataType dataType=lc.getTableColumn().getDataType();
				json.append(",\"");
				json.append(attributeName);
				json.append("\":\"");
				String atrtName = "get" + attributeName.substring(0, 1).toUpperCase()+attributeName.substring(1, attributeName.length());
				Method m = entity.getClass().getMethod(atrtName);
				Object o = m.invoke(entity);
				String attributeValue=formatDate(dataType,o,lc);
				json.append(attributeValue);
				json.append("\"");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		json.append("}");
		return json.toString();
		
	}
	
	/**
	 * 根据数据表管理中录入的日期格式格式化日期
	 * @param dataType
	 * @param attributeValue
	 * @return
	 */
	private static String formatDate(DataType dataType,Object attributeValue,ListColumn lc){
		try {
			if(dataType==DataType.DATE){
				String formatedate=packagingFormate(lc.getFormat());
				if(StringUtils.isEmpty(formatedate)){
					formatedate="yyyy-MM-dd";
				}
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatedate);
				attributeValue = simpleDateFormat.format(attributeValue);
			 }else if(dataType==DataType.TIME){
				 String formatedate=packagingFormate(lc.getFormat());
				if(StringUtils.isEmpty(formatedate)){
					formatedate="yyyy-MM-dd HH:mm:ss";
				}
				 SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatedate);
				 attributeValue = simpleDateFormat.format(attributeValue);
			 }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(attributeValue!=null)return attributeValue.toString();
		else return "";
	}
	
	private static String packagingFormate(String formatSetting) {
		if("yyyy-m-d".equals(formatSetting)){
			return "yyyy-MM-dd";
		}else if("yyyy-m-d hh:mm:ss".equals(formatSetting)){
			return "yyyy-MM-dd HH:mm:ss";
		}else if("yyyy-m".equals(formatSetting)){
			return "yyyy-MM";
		}else if("m-d".equals(formatSetting)){
			return "MM-dd";
		}else if("yyyy年m月d日".equals(formatSetting)){
			return "yyyy年MM月dd日";
		}else if("yyyy年m月d日hh时mm分ss秒".equals(formatSetting)){
			return "yyyy年MM月dd日HH时mm分ss秒";
		}else if("yyyy年m月".equals(formatSetting)){
			return "yyyy年MM月";
		}else if("m月d日".equals(formatSetting)){
			return "MM月dd日";
		}
		return "";
	}
	public static Object getObjectValue(String value,String datatype,String className){
		try {
			Object valObj=value;
			if("DATE".equals(datatype)||"TIME".equals(datatype)){
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
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");    
					return df.parse(value);
				}else if(datatype.equals("TIME")){
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");    
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
				GeneralDao generalDao = (GeneralDao) WebContextUtils.getBean("generalDao");
				Object entity=generalDao.getObject(className,Long.parseLong(value));
				return entity;
			}
		}catch (Exception e) {
		}
		return null;
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