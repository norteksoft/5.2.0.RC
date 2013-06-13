package com.norteksoft.product.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import com.norteksoft.mms.base.DynamicColumnValues;
import com.norteksoft.mms.base.MmsUtil;
import com.norteksoft.mms.base.TotalColumnValues;
import com.norteksoft.mms.base.utils.view.DynamicColumnDefinition;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.enumeration.TotalType;
import com.norteksoft.mms.form.service.FormHtmlParser;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.web.struts2.Struts2Utils;

public class PageUtils {
	public static String pageToJson(Page<?> page,String listCode){
		StringBuilder json = new StringBuilder();
		json.append("{\"page\":\"");
		json.append(page.getPageNo());
		json.append("\",\"total\":");
		json.append(page.getTotalPages());
		json.append(",\"records\":\"");
		json.append(page.getTotalCount());
		
		ListViewManager listViewManager = (ListViewManager) ContextUtils.getBean("listViewManager");
		ListView listView=listViewManager.getListViewByCode(listCode);
		List<ListColumn> columns=listView.getColumns();
		FormHtmlParser formHtmlParser = (FormHtmlParser) ContextUtils.getBean("formHtmlParser");
		String footerDatas="";
		footerDatas=formHtmlParser.getFooterDatas(page.getResult(),columns,false);
		if(StringUtils.isNotEmpty(footerDatas)){
			json.append("\",\"userdata\":");
			json.append(footerDatas);
		}else{
			json.append("\"");
		}
		json.append(",\"rows\":");
		json.append(pageToRowData(page,columns));
		
		json.append("}");
		return json.toString();
	}
	
	public static String pageToJson(Page<?> page){
		String listCode=Struts2Utils.getParameter("_list_code");
		String json="";
		if(StringUtils.isEmpty(listCode)){
			json=customPageToJson(page);
		}else{
			json=pageToJson(page, listCode);
		}
		return disposeSpecialCharacter(json);
	}
	
	public static String disposeSpecialCharacter(String json){
		json=json.replaceAll("\\\\", "\\\\\\\\").replaceAll("\r", "");
		if(json.contains("\r\n")){
			json=json.replaceAll("\r\n", "\\\\n");
		}else if(json.contains("\n")){
			json=json.replaceAll("\n", "\\\\n");
		}
		json=json.replaceAll("\t", "");
		return json.replaceAll("_@_#", "\\\\\"");
	}
	
	private static String customPageToJson(Page<?> page){
		StringBuilder json = new StringBuilder();
		json.append("{\"page\":\"");
		json.append(page.getPageNo());
		json.append("\",\"total\":");
		json.append(page.getTotalPages());
		json.append(",\"records\":\"");
		json.append(page.getTotalCount());
		json.append("\",\"rows\":");
		json.append(JsonParser.object2Json(page.getResult())).append("}");
		return json.toString();
	}
	
	public static String pageToRowData(Page<?> page,List<ListColumn> columns){
		StringBuilder json = new StringBuilder();
		FormHtmlParser formHtmlParser = (FormHtmlParser) ContextUtils.getBean("formHtmlParser");
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
		return json.toString();
	}
	
	/**
	 * 合计所有页
	 * @param page
	 * @param totalColumnValues
	 * @return
	 */
	public static String PageToJson(Page<?> page,TotalColumnValues totalColumnValues){
		String listCode=Struts2Utils.getParameter("_list_code");
		StringBuilder json = new StringBuilder();
		json.append("{\"page\":\"");
		json.append(page.getPageNo());
		json.append("\",\"total\":");
		json.append(page.getTotalPages());
		json.append(",\"records\":\"");
		json.append(page.getTotalCount());
		json.append("\",\"rows\":");
		FormHtmlParser formHtmlParser = (FormHtmlParser) ContextUtils.getBean("formHtmlParser");
		ListViewManager listViewManager = (ListViewManager) ContextUtils.getBean("listViewManager");
		ListView listView=listViewManager.getListViewByCode(listCode);
		List<ListColumn> columns=listView.getColumns();
		json.append(pageToRowData(page,columns));
		
		if(TotalType.ALL_PAGE.equals(listView.getTotalType())){//合计所有页
			List<String> result=new ArrayList<String>();
			StringBuilder footerDatas=new StringBuilder();
			int i=0;
			 String totalStr="";
			 for(ListColumn col: columns){//统计所有的合计列
				 if(col.getTotal()){//如果该列需要合计
					 if(i==0){//第一列需要合计
						 totalStr="first"; 
					 }
					 if(i>0&&StringUtils.isEmpty(totalStr)){
						 totalStr=",\""+columns.get(i-1).getTableColumn().getName()+"\":\""+formHtmlParser.getLocal("grid.total")+"\"";
					 }
					 TableColumn tc=col.getTableColumn();
					 result.add(tc.getName());
				 }
				 i++;
			 }
			if(result!=null && result.size()>0){
				Map<String,Object> totalValues=totalColumnValues.getValues(result);//追加合计列的值
				for(String columnName:result){
					if(StringUtils.isNotEmpty(footerDatas.toString())){
						footerDatas.append(",");
					}
					footerDatas.append("\"");
					footerDatas.append(columnName);
					footerDatas.append("\":");
					footerDatas.append("\"");
					footerDatas.append(totalValues.get(columnName)==null?"0":totalValues.get(columnName));
					footerDatas.append("\"");
				 }
				if(StringUtils.isNotEmpty(totalStr) && !"first".equals(totalStr)){
					footerDatas.append(totalStr);
				}
				if(StringUtils.isNotEmpty(footerDatas.toString())){
					json.append(",\"userdata\":");
					json.append("{");
					json.append(footerDatas);
					json.append("}");
				}
			}
		}else{//合计当前页
			
			String footerDatas="";
			footerDatas=formHtmlParser.getFooterDatas(page.getResult(),columns,false);
			if(StringUtils.isNotEmpty(footerDatas)){
				json.append(",\"userdata\":");
				json.append(footerDatas);
			}
		}
		json.append("}");
		return disposeSpecialCharacter(json.toString());
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
		
		ListViewManager listViewManager = (ListViewManager) ContextUtils.getBean("listViewManager");
		ListView listView=listViewManager.getListViewByCode(listCode);
		List<ListColumn> columns=listView.getColumns();
		List<Map<String,Object>> result=new ArrayList<Map<String,Object>>();
		try{
			if(page!=null && page.getResult()!=null && page.getResult().size()>0){
				for(Object obj:page.getResult()){
					result.add(getRowData(obj,columns));
				}
				dynamicColumnValues.addValuesTo(result);
				//JSONArray jsonarray=JSONArray.fromObject(result);
				//json.append(jsonarray);
				json.append(JsonParser.object2Json(result));
				
				//获得合计行
				FormHtmlParser formHtmlParser = (FormHtmlParser) ContextUtils.getBean("formHtmlParser");
				String footerDatas=formHtmlParser.getFooterDatas(page.getResult(),columns,false);
				StringBuilder footers=new StringBuilder(footerDatas);
				String dynFooterData=getdynamicColumnFooterDatas(result);
				if(StringUtils.isNotEmpty(footers.toString())||StringUtils.isNotEmpty(dynFooterData.toString())){
					//如果合计的值不为空
					json.append(",\"userdata\":");
					if(StringUtils.isNotEmpty(dynFooterData.toString())){
						if(StringUtils.isNotEmpty(footers.toString())){
							if(footers.charAt(footers.length()-1)=='}'){
								//动态列有合计时，删除了footer的一个大括号（后面记得要补上）
								footers.deleteCharAt(footers.length()-1);
							}
							footers.append(",");
						}else{
							footers.append("{");
							String colName=getTotalNameCol(columns);
							if(StringUtils.isNotEmpty(colName)){
								footers.append("\"").append(colName)
								.append("\":\"").append(formHtmlParser.getLocal("grid.total")).append("\",");
							}
						}
					}
					footers.append(dynFooterData);
					json.append(footers);
					//动态列有合计时，需要补上一个大括号
					if(StringUtils.isNotEmpty(dynFooterData.toString())){
						json.append("}");
					}
				}
			}else{
				json.append("[]");
			}
			json.append("}");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return disposeSpecialCharacter(json.toString());
	}
	
	private static String getTotalNameCol(List<ListColumn> columns){
		for(int i=columns.size()-1;i>0;i--){
			ListColumn col=columns.get(i);
			if(col.getVisible()!=null&&col.getVisible()&&col.getTableColumn()!=null&&col.getTableColumn().getDataType()==DataType.TEXT){
				return col.getTableColumn().getName();
			}
		}
		return null;
	}
	
	private static String getdynamicColumnFooterDatas(List<Map<String,Object>> result){
		StringBuilder dynColfooterData=new StringBuilder();
		MmsUtil mmsUtil = (MmsUtil) ContextUtils.getBean("mmsUtil");
		Map<String,DynamicColumnDefinition> dynamicColumns=mmsUtil.getDynamicColumnName();
		Set<String> dynamicColumnNames=dynamicColumns.keySet();
		for(String key:dynamicColumnNames){
			DynamicColumnDefinition dyColDef=dynamicColumns.get(key);
			if((dyColDef.getIsTotal()!=null&&dyColDef.getIsTotal())&&isNumber(dyColDef)){
				Long totalInt=0l;//整数时的“和”
				 Double totalFloat=0d;//小数时的"和"
				for(Map<String,Object> m:result){
					Object val=m.get(key);
					if(val!=null){
						if(isInt(dyColDef)){
							totalInt=totalInt+Integer.parseInt(val.toString());
						}else if(isFloat(dyColDef)){
							totalFloat=totalFloat+Double.parseDouble(val.toString());
						}
					}
				}
				if(isInt(dyColDef)){
					dynColfooterData.append("\"")
					.append(key)
					.append("\":\"")
					.append(totalInt)
					.append("\",");
				}else if(isFloat(dyColDef)){
					dynColfooterData.append("\"")
					.append(key)
					.append("\":\"")
					.append(totalFloat)
					.append("\",");
				}
			}
		}
		if(dynColfooterData.length()>0&&dynColfooterData.charAt(dynColfooterData.length()-1)==','){
			dynColfooterData.deleteCharAt(dynColfooterData.length()-1);
		}
		return dynColfooterData.toString();
	}
	
	private static boolean isNumber(DynamicColumnDefinition dyColDef){
		return dyColDef.getType()!=null&&(dyColDef.getType()==DataType.AMOUNT||dyColDef.getType()==DataType.NUMBER||dyColDef.getType()==DataType.INTEGER||dyColDef.getType()==DataType.LONG||dyColDef.getType()==DataType.DOUBLE||dyColDef.getType()==DataType.FLOAT);
	}
	private static boolean isInt(DynamicColumnDefinition dyColDef){
		return dyColDef.getType()!=null&&(dyColDef.getType()==DataType.NUMBER||dyColDef.getType()==DataType.INTEGER||dyColDef.getType()==DataType.LONG);
	}
	private static boolean isFloat(DynamicColumnDefinition dyColDef){
		return dyColDef.getType()!=null&&(dyColDef.getType()==DataType.AMOUNT||dyColDef.getType()==DataType.DOUBLE||dyColDef.getType()==DataType.FLOAT);
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
							 Float f=Float.valueOf(val.toString());
							 val=nf.format(f);
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
	
//	public static void main(String[] args) {
//		System.out.println(String[].class.getName());
//		System.out.println(Object[].class.getName());
//		
//		System.out.println(com.norteksoft.product.orm.Page.class.getName());
//	}
	
	public static String getMethodName(String valueKey){
		String start=StringUtils.upperCase(valueKey.substring(0,1));
		String other=valueKey.substring(1,valueKey.length());
		return "get"+start+other;
	}
}
