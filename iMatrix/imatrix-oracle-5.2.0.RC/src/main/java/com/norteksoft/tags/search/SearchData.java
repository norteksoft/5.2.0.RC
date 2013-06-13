package com.norteksoft.tags.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.xwork.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.options.entity.Option;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.service.FormHtmlParser;
import com.norteksoft.mms.form.service.ListColumnManager;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;

@Service
@Transactional
public class SearchData {
//	private String tableId;
//	private String listTableCode;
//	private String submitForm;
//	private String url;
	@SuppressWarnings("unused")
	private Integer fixedSearchSign;//如果有固定查询先显示固定查询的标记
	private ListColumnManager listColumnManager;
	private ListViewManager listViewManager;
	private FormHtmlParser formHtmlParser;
	
	@Transactional(readOnly=true)
	public String getContent(String listTableCode,String url,String tableId,String submitForm) throws Exception{
		List<ObjectField> fieldList = new ArrayList<ObjectField>();
		String fieldString = getFieldListByCode(listTableCode);
		//判断是否高级查询从mms中取值
		String advancedSearch = getSearchPropertyByCode(listTableCode,"isAdvancedQuery");
		//判断是否弹框从mms中取值
		String containerId = getSearchPropertyByCode(listTableCode,"isContainerIdQuery");
		dealWithFieldString(fieldString, fieldList);
		List<ObjectField> fixedFields = new ArrayList<ObjectField>();
		for (ObjectField field : fieldList) {
			if(field.getFixedField()){
				fixedFields.add(field);
			}
		}
		String jsonStr = JsonParser.object2Json(fieldList);
		
		Map<String, Object> root=new HashMap<String, Object>();
		if(submitForm==null){
			submitForm="";
		}
		root.put("tableId", tableId);
		root.put("submitForm", submitForm);
		root.put("url", url);
		root.put("containerId", containerId);
		root.put("advancedSearch", advancedSearch);
		root.put("fieldList", jsonStr);
		root.put("fixedField", fixedFields);
		root.put("fixedSearchSign", fixedFields.size());
		root.put("textProvider", textProvider);
		String result = TagUtil.getContent(root, "search/search.ftl");
		return result;
	}
	//[{enName:groupName,chName:工作流名称,propertyType:STRING,fixedField:false},{enName:title,chName:任务名称,propertyType:STRING,fixedField:true},{enName:name,chName:环节名称,propertyType:STRING,fixedField:false},{enName:createDate,chName:生成日期,propertyType:DATE,fixedField:true},{enName:creatorName,chName:流程发起人,propertyType:STRING,fixedField:true}]
	private void dealWithFieldString(String fieldString, List<ObjectField> fieldList) {
		int endIndex = 0;
		while((endIndex = fieldString.indexOf("}"))>=0){
			int beginIndex = fieldString.indexOf("{");
			String partStr = fieldString.substring(beginIndex+1, endIndex);
			//partStr = enName:groupName,chName:工作流名称,propertyType:STRING,fixedField:false,optionsCode:***
			ObjectField field = new ObjectField();
			String[] elements = partStr.split(",");
			for (String element : elements) {
				//element = enName:groupName
				String[] part = element.split(":");
				if("enName".equals(part[0].trim())){
					field.setEnName(part[1].trim());
				}else if("chName".equals(part[0].trim())){
					field.setChName(part[1].trim());
				}else if("propertyType".equals(part[0].trim())){
 					field.setPropertyType(PropertyType.valueOf(part[1].trim()));
				}else if("fixedField".equals(part[0].trim())){
					field.setFixedField(Boolean.valueOf(part[1].trim()));
				}else if("optionsCode".equals(part[0].trim())){
					field.setOptionsCode(part[1].trim());
				}else if("enumName".equals(part[0].trim())){
					field.setEnumName(part[2].trim());
				}else if("keyValue".equals(part[0].trim())){
					if(part.length>1)field.setKeyValue(part[1].trim().replace("$", ":").replace("#", ","));
				}else if("beanName".equals(part[0].trim())){
					field.setBeanName(part[1].trim().replace("$", ":").replace("#", ","));
				}else if("optionGroup".equals(part[0].trim())){
					field.setOptionGroup(part[1].trim());
				}else if("eventType".equals(part[0].trim())){
					field.setEventType(part[1].trim());
				}else if("dbName".equals(part[0].trim())){
					field.setDbName(part[1].trim());
				}
			}
		   if(field.getPropertyType() == PropertyType.BOOLEAN){
//				List<Option> options = new ArrayList<Option>();
//				Option option = new Option();
//				option.setName(textProvider.getText("common.yes"));
//				option.setValue("true");
//				options.add(option);
//				option = new Option();
//				option.setName(textProvider.getText("common.no"));
//				option.setValue("false");
//				options.add(option);
//				field.setDefaultValues(options);
			   String keyValue = field.getKeyValue();
			   if(keyValue != null && !"".equals(keyValue.trim())){
				   String[] property =	keyValue.split(",");
				   field.setDefaultValues(createOption(property));
			   }
			}else if(field.getPropertyType() == PropertyType.ENUM){
				try {
					Object[] objs = Class.forName(field.getEnumName()).getEnumConstants();
					List<Option> options = new ArrayList<Option>();
					Option option = null;
					for(Object obj : objs){
						option = new Option();
						options.add(option);
						option.setName(textProvider.getText(BeanUtils.getProperty(obj, "code")));
						option.setValue(obj.toString());
					}
					field.setDefaultValues(options);
				} catch (Exception e) {
				}
			}else if(field.getPropertyType() == PropertyType.STRING||field.getPropertyType() == PropertyType.INTEGER){
				String keyValue = field.getKeyValue();
				String beanName = field.getBeanName();
				String optionGroup = field.getOptionGroup();
				if(beanName != null && !"".equals(beanName.trim())  && beanName.contains("beanname")){//接口	
					beanName = beanName.substring(8, beanName.length());
					String[] property =	beanName.split(",");
					field.setDefaultValues(createOption(property));			
				}else if(keyValue != null && !"".equals(keyValue.trim())){//键值对
					String[] property =	keyValue.split(",");
					field.setDefaultValues(createOption(property));			
				}else if(optionGroup != null && !"".equals(optionGroup.trim())){//选项组
					String[] property =	getOptionGroupByCode(optionGroup).split(",");
					field.setDefaultValues(createOption(property));	
				}
			}
			fieldList.add(field);
			fieldString = fieldString.replace("{" + partStr + "}", "");
			//[,,,,]
		}
	}

	private final transient TextProvider textProvider = 
		new TextProviderFactory().createInstance(getClass(), new LocaleProvider(){
			public Locale getLocale() {
		        ActionContext ctx = ActionContext.getContext();
		        if (ctx != null) {
		            return ctx.getLocale();
		        } else {
		            return null;
		        }
		    }});
	
	//通过列表编号获得想要查询的字段信息
	private String getFieldListByCode(String ListTableCode){
		String fieldString="[";
		listColumnManager = (ListColumnManager)ContextUtils.getBean("listColumnManager");
		List<ListColumn> ListColumns = listColumnManager.getQueryColumnsByCode(ListTableCode);
		listViewManager = (ListViewManager)ContextUtils.getBean("listViewManager");
		formHtmlParser = (FormHtmlParser)ContextUtils.getBean("formHtmlParser");
		ListView listView = listViewManager.getListViewByCode(ListTableCode);
		Boolean isStandard = listView.getStandard();
		for(ListColumn listColumn : ListColumns){
			if(listColumn.getTableColumn()!=null){
				//值设置
				String valueSet = listColumn.getValueSet();
			    String enName = listColumn.getTableColumn().getName();
			    String dbName = listColumn.getTableColumn().getDbColumnName();
			    if(StringUtils.isEmpty(dbName)){
			    	dbName=null;
			    }
				//自定义表单在字段名前面加dt_
				if(isStandard==false){
					if(!FormHtmlParser.isDefaultField(enName)){
						enName="dt_"+enName;
					}
				}
				String chName = formHtmlParser.getInternation(listColumn.getHeaderName());
				String propertyType = listColumn.getTableColumn().getDataType().toString();
				if("TEXT".equals(propertyType)){
					propertyType = "STRING";
				}else if("CLOB".equals(propertyType)||"BLOB".equals(propertyType)||"COLLECTION".equals(propertyType)){
					continue;
				}
				//控件类型为“自定义”时，查询字段的控件类型为普通的输入框
				if("CUSTOM".equals(listColumn.getControlValue())){
					valueSet=null;
					propertyType = "STRING";
				}
				if(StringUtils.isNotEmpty(listColumn.getQuerySettingValue())&&listColumn.getQuerySettingValue().contains("FIXED")){//普通查询
					String fixedField = "true";
					fieldString = fillFieldString(fieldString,propertyType,enName,chName,fixedField,valueSet,listColumn,dbName);
				}else if(StringUtils.isNotEmpty(listColumn.getQuerySettingValue())&&listColumn.getQuerySettingValue().contains("CUSTOM")){//高级查询
					String fixedField = "false";
					fieldString = fillFieldString(fieldString,propertyType,enName,chName,fixedField,valueSet,listColumn,dbName);
				}
			}
		}
		if(fieldString.length()>1){
			fieldString = fieldString.substring(0,fieldString.length()-1);
			fieldString += "]";
		}else{
			fieldString = "";
		}
		return fieldString;
	}
	//通过列表编号判断属性
	private String getSearchPropertyByCode(String ListTableCode,String searchType){
		String sign = "";
		listViewManager = (ListViewManager)ContextUtils.getBean("listViewManager");
		ListView listView = listViewManager.getListViewByCode(ListTableCode);
		Boolean propertyBoolean = null;
		if(searchType=="isAdvancedQuery"){
			propertyBoolean = listView.getAdvancedQuery();
		}else if(searchType=="isContainerIdQuery"){
			if(listView.getPopUp()==null||listView.getPopUp()){//数据库中没值或者是弹出式查询时
				propertyBoolean = false;
			}else{
				propertyBoolean = true;
			}
		}
		if(propertyBoolean){
			sign = "true";
		}else{
			sign = "false";
		}
		return sign;
	}
	//通过值设置获得选项组
	private String getOptionGroupByCode(String valueSet){
		StringBuilder opitions=new StringBuilder();
		com.norteksoft.product.api.entity.OptionGroup group = ApiFactory.getSettingService().getOptionGroupByCode(valueSet);
			if(group!=null){
				int i=0;
				List<com.norteksoft.product.api.entity.Option> ops=ApiFactory.getSettingService().getOptionsByGroup(group.getId());
				for(com.norteksoft.product.api.entity.Option op:ops){
					i++;
					opitions.append(op.getValue())
					.append(":")
					.append(op.getName());
					if(i<ops.size()){
						opitions.append(",");
					}
				}
			}
			return opitions.toString();
	}
	//通过值设置和列获得接口
	private String getOPtionByListColumnAndValueSet(ListColumn listColumn,String valueSet){
		formHtmlParser = (FormHtmlParser)ContextUtils.getBean("formHtmlParser");
		String colName = listColumn.getTableColumn().getName();
		String getOptions = formHtmlParser.getOptionsByBeanName(valueSet, colName, null);
		return getOptions;
	}
	//生成option
	private List<Option> createOption(String[] property){
		List<Option> options = new ArrayList<Option>();
		Option option = null;
		for (String p : property) {
			if(StringUtils.isNotEmpty(p)){
				option = new Option();
				String[] part = p.split(":");
				options.add(option);
				option.setName(part[1].trim());
				option.setValue(part[0].trim());
			}
		}
		return options;
	}
	private String fillFieldString(String fieldString,String propertyType,String enName,String chName,String fixedField,String valueSet,ListColumn listColumn,String dbName){
		if("BOOLEAN".equals(propertyType)){
			if(StringUtils.isNotEmpty(valueSet)){
				valueSet = valueSet.replace(",", "#");
				valueSet = valueSet.replace(":", "$");
				valueSet = valueSet.replace("'", "");
			}else{
				valueSet="";
			}
			fieldString += "{enName:"+enName+",chName:"+chName+",propertyType:"+propertyType+",fixedField:"+fixedField+",keyValue:"+valueSet+",optionsCode:exist"+getEventType(listColumn)+",dbName:"+dbName+"},";
		}else if("ENUM".equals(propertyType)){//枚举的值设置
			String enumname = listColumn.getTableColumn().getObjectPath();
			if(StringUtils.isNotEmpty(enumname)){
				enumname = "enumname:"+enumname;//兼容之前的列表字段设置中的值设置
				fieldString += "{enName:"+enName+",chName:"+chName+",propertyType:"+propertyType+",fixedField:"+fixedField+",enumName:"+enumname+getEventType(listColumn)+",dbName:"+dbName+"},";
			}else{
				fieldString += "{enName:"+enName+",chName:"+chName+",propertyType:"+propertyType+",fixedField:"+fixedField+",enumName:"+valueSet+getEventType(listColumn)+",dbName:"+dbName+"},";
			}
		}else if(("STRING".equals(propertyType)||"INTEGER".equals(propertyType)) && valueSet != null && !"".equals(valueSet.trim())){
			if(valueSet.contains("beanname")){//接口
				String bneannameValSet =  getOPtionByListColumnAndValueSet(listColumn, valueSet);
				bneannameValSet = bneannameValSet.replace(",", "#").replace("\'", "");
				bneannameValSet = bneannameValSet.replace(":", "$");
				fieldString += "{enName:"+enName+",chName:"+chName+",propertyType:"+propertyType+",fixedField:"+fixedField+",beanName:beanname"+bneannameValSet+",optionsCode:exist"+getEventType(listColumn)+",dbName:"+dbName+"},";
			}else if(valueSet.contains(":")){//键值对
				valueSet = valueSet.replace(",", "#");
				valueSet = valueSet.replace(":", "$");
				valueSet = valueSet.replace("'", "");
				fieldString += "{enName:"+enName+",chName:"+chName+",propertyType:"+propertyType+",fixedField:"+fixedField+",keyValue:"+valueSet+",optionsCode:exist"+getEventType(listColumn)+",dbName:"+dbName+"},";
			}else{//选项组
				//valueSet = valueSet.replace(":", "$");
				fieldString += "{enName:"+enName+",chName:"+chName+",propertyType:"+propertyType+",fixedField:"+fixedField+",optionGroup:"+valueSet+",optionsCode:exist"+getEventType(listColumn)+",dbName:"+dbName+"},";
			}
		}else{
			fieldString += "{enName:"+enName+",chName:"+chName+",propertyType:"+propertyType+",fixedField:"+fixedField+getEventType(listColumn)+",dbName:"+dbName+"},";
		}
		return fieldString;
	}
//	public void setTableId(String tableId) {
//		this.tableId = tableId;
//	}
//	
//	public String getListTableCode() {
//		return listTableCode;
//	}
//
//	public void setListTableCode(String listTableCode) {
//		this.listTableCode = listTableCode;
//	}
//
//	public void setSubmitForm(String submitForm) {
//		this.submitForm = submitForm;
//	}
//	
//	public void setUrl(String url) {
//		this.url = url;
//	}


	/**
	 * 获得查询的事件类型
	 */
	private String getEventType(ListColumn listColumn) {
		String querySetting="";
		String querySettingValue=listColumn.getQuerySettingValue();
		String controlValue=listColumn.getControlValue();
		if(StringUtils.isNotEmpty(querySettingValue)){
			String[] arr=querySettingValue.split(",");
			//查询时，当控件类型为“人员部门树”时，查询事件优先于控件类型；
			if(arr.length>1){
				querySetting=",eventType:"+arr[1];
			}else if(StringUtils.isNotEmpty(controlValue)&&controlValue.contains("SELECT_TREE")){//控件类型是人员部门树
				String[] val=controlValue.split(",");
				querySetting=",eventType:"+val[2]+"/"+val[3];
			}
		}
		return querySetting;
	}
	public void setFixedSearchSign(Integer fixedSearchSign) {
		this.fixedSearchSign = fixedSearchSign;
	}
}
