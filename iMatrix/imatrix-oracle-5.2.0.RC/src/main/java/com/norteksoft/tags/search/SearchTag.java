package com.norteksoft.tags.search;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.bs.options.entity.Option;
import com.norteksoft.bs.options.entity.OptionGroup;
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

public class SearchTag extends TagSupport {
	
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(SearchTag.class);
	private String tableId;
	private String listTableCode;
	private String submitForm;
	private String url;
	@SuppressWarnings("unused")
	private Integer fixedSearchSign;//如果有固定查询先显示固定查询的标记
	private ListColumnManager listColumnManager;
	private ListViewManager listViewManager;
	private FormHtmlParser formHtmlParser;
	@Override
	public int doStartTag() throws JspException {
		try {
			((HttpServletRequest)this.pageContext.getRequest()).setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		JspWriter out=pageContext.getOut();
		try {
			log.debug("read templet begin");
			out.print(readScriptTemplet());
			log.debug("read templet over");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Tag.EVAL_PAGE;
	}
	
	@Override
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}
	
	private String readScriptTemplet() throws Exception{
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
		root.put("webRoot", this.pageContext.getServletContext().getContextPath());
		root.put("tableId", tableId);
		root.put("submitForm", submitForm);
		root.put("url", url);
		root.put("containerId", containerId);
		root.put("advancedSearch", advancedSearch);
		//root.put("rushZoon", rushZoon);
		root.put("fieldList", jsonStr);
		root.put("fixedField", fixedFields);
		root.put("fixedSearchSign", fixedFields.size());
		root.put("textProvider", textProvider);
		log.debug("templet parameter set begin");
//		String result = FtlUtil.renderFile(root, "search.ftl");
		String result = TagUtil.getContent(root, "search/search.ftl");
		log.debug("templet parameter set over");
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
					field.setKeyValue(part[1].trim().replace("$", ":").replace("#", ","));
				}else if("beanName".equals(part[0].trim())){
					field.setBeanName(part[1].trim().replace("$", ":").replace("#", ","));
				}else if("optionGroup".equals(part[0].trim())){
					field.setOptionGroup(part[1].trim());
				}
			}
		   if(field.getPropertyType() == PropertyType.BOOLEAN){
				List<Option> options = new ArrayList<Option>();
				Option option = new Option();
				option.setName(textProvider.getText("common.yes"));
				option.setValue("true");
				options.add(option);
				option = new Option();
				option.setName(textProvider.getText("common.no"));
				option.setValue("false");
				options.add(option);
				field.setDefaultValues(options);
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
					log.debug("Enum error:", e);
				}
			}else if(field.getPropertyType() == PropertyType.STRING){
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
		            log.debug("Action context not initialized");
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
			//值设置
			String valueSet = listColumn.getValueSet();
		    String enName = listColumn.getTableColumn().getName();
			//自定义表单在字段名前面加dt_
			if(isStandard==false){enName="dt_"+enName;}
			String chName = formHtmlParser.getInternation(listColumn.getHeaderName());
			String propertyType = listColumn.getTableColumn().getDataType().toString();
			if("TEXT".equals(propertyType)){
				propertyType = "STRING";
			}else if("CLOB".equals(propertyType)||"BLOB".equals(propertyType)||"COLLECTION".equals(propertyType)){
				continue;
			}
			if(StringUtils.isNotEmpty(listColumn.getQuerySettingValue())&&listColumn.getQuerySettingValue().contains("FIXED")){//固定查询
				String fixedField = "true";
				fieldString = fillFieldString(fieldString,propertyType,enName,chName,fixedField,valueSet,listColumn);
			}else if(StringUtils.isNotEmpty(listColumn.getQuerySettingValue())&&listColumn.getQuerySettingValue().contains("CUSTOM")){//自定义查询
				String fixedField = "false";
				fieldString = fillFieldString(fieldString,propertyType,enName,chName,fixedField,valueSet,listColumn);
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
			option = new Option();
			String[] part = p.split(":");
				options.add(option);
				option.setName(part[1].trim());
				option.setValue(part[0].trim());
			}
		return options;
	}
	private String fillFieldString(String fieldString,String propertyType,String enName,String chName,String fixedField,String valueSet,ListColumn listColumn){
		if("ENUM".equals(propertyType)){//枚举的值设置
			fieldString += "{enName:"+enName+",chName:"+chName+",propertyType:"+propertyType+",fixedField:"+fixedField+",enumName:"+valueSet+"},";
		}else if("STRING".equals(propertyType) && valueSet != null && !"".equals(valueSet.trim())){
			if(valueSet.contains("beanname")){//接口
				String bneannameValSet =  getOPtionByListColumnAndValueSet(listColumn, valueSet);
				bneannameValSet = bneannameValSet.replace(",", "#").replace("\'", "");
				bneannameValSet = bneannameValSet.replace(":", "$");
				fieldString += "{enName:"+enName+",chName:"+chName+",propertyType:"+propertyType+",fixedField:"+fixedField+",beanName:beanname"+bneannameValSet+",optionsCode:exist"+"},";
			}else if(valueSet.contains(":")){//键值对
				valueSet = valueSet.replace(",", "#");
				valueSet = valueSet.replace(":", "$");
				valueSet = valueSet.replace("'", "");
				fieldString += "{enName:"+enName+",chName:"+chName+",propertyType:"+propertyType+",fixedField:"+fixedField+",keyValue:"+valueSet+",optionsCode:exist"+"},";
			}else{//选项组
				//valueSet = valueSet.replace(":", "$");
				fieldString += "{enName:"+enName+",chName:"+chName+",propertyType:"+propertyType+",fixedField:"+fixedField+",optionGroup:"+valueSet+",optionsCode:exist"+"},";
			}
		}else{
			fieldString += "{enName:"+enName+",chName:"+chName+",propertyType:"+propertyType+",fixedField:"+fixedField+"},";
		}
		return fieldString;
	}
	public void setTableId(String tableId) {
		this.tableId = tableId;
	}
	
	public String getListTableCode() {
		return listTableCode;
	}

	public void setListTableCode(String listTableCode) {
		this.listTableCode = listTableCode;
	}

	public void setSubmitForm(String submitForm) {
		this.submitForm = submitForm;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public void setFixedSearchSign(Integer fixedSearchSign) {
		this.fixedSearchSign = fixedSearchSign;
	}

}
