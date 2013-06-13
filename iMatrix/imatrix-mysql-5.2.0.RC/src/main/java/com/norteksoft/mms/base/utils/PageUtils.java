package com.norteksoft.mms.base.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.norteksoft.mms.base.DynamicColumnValues;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.service.FormHtmlParser;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.WebContextUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;

/**
 *  已经被 com.norteksoft.product.util.PageUtils 替代，请更换
 */
@Deprecated
public class PageUtils {
	
	public static String pageToJson(Page<?> page,String listCode){
		StringBuilder json = new StringBuilder();
		json.append("{\"page\":\"");
		json.append(page.getPageNo());
		json.append("\",\"total\":");
		json.append(page.getTotalPages());
		json.append(",\"records\":\"");
		json.append(page.getTotalCount());
		json.append("\",\"rows\":");
		
		ListViewManager listViewManager = (ListViewManager) WebContextUtils.getBean("listViewManager");
		ListView listView=listViewManager.getListViewByCode(listCode);
		List<ListColumn> columns=listView.getColumns();
		
		FormHtmlParser formHtmlParser = (FormHtmlParser) WebContextUtils.getBean("formHtmlParser");
		json.append("[");
		
		
		for(Object obj:page.getResult()){
			String str=obj.getClass().getName();
			if(str.indexOf("[")==0){
				json.append(formHtmlParser.getScriptArray(obj,columns));
			}else{
				json.append(formHtmlParser.getScriptObject(obj,columns));
			}
			json.append(",");
		}
		
		if(page.getResult().size()>0){
			json=json.replace(json.length()-1,json.length(), "");
		}
		json.append("]");
		json.append("}");
		return json.toString();
	}
	
	public static String pageToJson(Page<?> page){
		String listCode=Struts2Utils.getParameter("_list_code");
		return pageToJson(page, listCode);
	}
	
	public static String dynamicPageToJson(Page<?> page,DynamicColumnValues dynamicColumnValues){
		String listCode=Struts2Utils.getParameter("_list_code");
		StringBuilder json = new StringBuilder();
		json.append("{\"page\":\"");
		json.append(page.getPageNo());
		json.append("\",\"total\":");
		json.append(page.getTotalPages());
		json.append(",\"records\":\"");
		json.append(page.getTotalCount());
		json.append("\",\"rows\":");
		
		ListViewManager listViewManager = (ListViewManager) WebContextUtils.getBean("listViewManager");
		ListView listView=listViewManager.getListViewByCode(listCode);
		List<ListColumn> columns=listView.getColumns();
		List<Map<String,Object>> result=new ArrayList<Map<String,Object>>();
		try{
			for(Object obj:page.getResult()){
				result.add(getRowData(obj,columns));
			}
			dynamicColumnValues.addValuesTo(result);
			json.append(JsonParser.object2Json(result));
			json.append("}");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	/**
	 * 根据mms列表管理中录入的列值获得行数据
	 * @param object
	 * @param columns
	 * @return
	 */
	private static Map<String,Object> getRowData(Object object,List<ListColumn> columns){
		Map<String,Object> entityMap=new HashMap<String, Object>();
		String str=object.getClass().getName();
		if(str.indexOf("[")==0){//数组
			entityMap=packagingColumn("array",object,columns);
		}else{//实体
			entityMap=packagingColumn("entity",object,columns);
		}
		return entityMap;
		
	}
	
	private static Map<String,Object> packagingColumn(String type,Object object,List<ListColumn> columns){
		Map<String,Object> entityMap=new HashMap<String, Object>();
		 try {
			 if("entity".equals(type)){
				Object id=BeanUtils.getProperty(object, "id");
				entityMap.put("id", id);
			 }
			 int i=0;
			 for(ListColumn col: columns){
				 TableColumn tc=col.getTableColumn();
				 if(tc.getDataType()!=DataType.COLLECTION){
					 String colName=tc.getName();
					 Object val=null;
					 if("entity".equals(type)){
						 if(!colName.contains("$")){
							 if(colName.contains(".")){
								 String refname=colName.split("\\.")[0];//映射实体名称
								 Object obj=BeanUtils.getProperty(object, refname);
								 if(obj!=null){
									 val=BeanUtils.getProperty(object, colName);
								 }
							 }else{
								 val=BeanUtils.getProperty(object, colName);
							 }
						 }
					 }
					 if("array".equals(type)){
						 val=((Object[])object)[i];
						 i++;
					 }
					 if(val!=null){
						 if(tc.getDataType()==DataType.DATE){
							 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
							 Date date = simpleDateFormat.parse(val.toString());
							 val=simpleDateFormat.format(date);
						 }else if(tc.getDataType()==DataType.TIME){
							 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
							 Date date = simpleDateFormat.parse(val.toString());
							 val=simpleDateFormat.format(date);
						 }else if(tc.getDataType()==DataType.AMOUNT||tc.getDataType()==DataType.DOUBLE){
							 NumberFormat nf=new DecimalFormat("#.##");
							 Double d=Double.valueOf(val.toString());
							 val=nf.format(d);
						 }else if(tc.getDataType()==DataType.FLOAT){
							 NumberFormat nf=new DecimalFormat("#.##");
							 Float d=Float.valueOf(val.toString());
							 val=nf.format(d);
						 }else if("entity".equals(type)&&tc.getDataType()==DataType.REFERENCE){
							 val=BeanUtils.getProperty(object, colName+".id");
						 }
					 }
					 entityMap.put(colName, val);
				 }
			 }
			} catch (Exception e) {
				throw new RuntimeException(e);
			} 
			return entityMap;
	}
	
}
