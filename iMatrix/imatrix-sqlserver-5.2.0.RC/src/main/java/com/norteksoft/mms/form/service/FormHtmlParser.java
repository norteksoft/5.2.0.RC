package com.norteksoft.mms.form.service;


import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.FormField;
import net.htmlparser.jericho.FormFields;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.bs.options.entity.Option;
import com.norteksoft.bs.options.entity.OptionGroup;
import com.norteksoft.mms.base.CommonStaticConstant;
import com.norteksoft.mms.base.utils.view.ComboxValues;
import com.norteksoft.mms.form.entity.AutomaticallyFilledField;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.ControlType;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.enumeration.DefaultValue;
import com.norteksoft.mms.form.format.FormatSetting;
import com.norteksoft.mms.form.format.FormatSettingFactory;
import com.norteksoft.mms.form.jdbc.JdbcSupport;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;
/**
 * 表单html解析类
 * @author wurong
 *
 */	
@Service
public class FormHtmlParser {
	/**
	 * templatePath 模板的路径 templateContent 模板的内容
	 */
	private Source source;
	private OutputDocument outputDocument;
	
	private static final String TYPE="type";
	private static final String DATATYPE = "datatype";
	private static final String NAME = "name";
	private static final String DATEFORMAT = "format";
	private static final String BEANNAME="beanname";
	private static final String CLASSNAME="classname";
	private static final String ENUMNAME="enumname";
	
	public FormHtmlParser() {
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
	/**
	 * 同表单代码构造一个解析对象
	 * @param formHtml
	 */
	public FormHtmlParser(String formHtml) {
		this.source = new Source(formHtml);
		this.outputDocument = new OutputDocument(source);
	}
	 
	/**
	 * 设置要解析的表单代码
	 * @param formHtml
	 */
	public void setFormHtml(String formHtml){
		this.source = new Source(formHtml);
		this.outputDocument = new OutputDocument(source);
	}
	 /**
	  * 验证html 主要验证重名
	  */
	 public String validatHtml(String formHtml){
		 this.setFormHtml(formHtml);
		 final String doubleFieldName = "请检查，有重复的字段名：";
		 final String doubleControlIdName = "请检查，有重复的控件id：";
		 FormFields formFields=source.getFormFields();
		 List<FormControl> controls=getControls(formHtml);
		 for (FormField formField : formFields) {
			 Element element= formField.getFormControl().getElement();
			 if(element.toString().toLowerCase().indexOf("type=\"radio\"")<=-1 && element.toString().toLowerCase().indexOf("type=\"checkbox\"")<=-1 && formField.getFormControls().size()>1)return doubleFieldName+formField.getFormControls().iterator().next().getName();
			 for(FormControl formControl:controls){
				 if(element.getAttributeValue("id")==null ||(formControl.getName()!=null &&formControl.getName().equals(element.getAttributeValue("name"))))continue;
				 if(element.getAttributeValue("id").equals(formControl.getControlId())) return doubleControlIdName+formControl.getControlId();
			 }
		 }
		 return "ok";
	 }
	 
	 //得到所有的字段
	 public  List<FormControl> getControls(String formHtml){
		 this.setFormHtml(formHtml);
		 FormFields formFields=source.getFormFields();
		 List<FormControl> list = new ArrayList<FormControl>();
		 for (FormField formField : formFields) {
			Element element= formField.getFormControl().getElement();
			FormControl control=new FormControl();
			//<input id="column3" title="字段san" name="column3" datatype="TEXT" request="0" format="no" readolny="0" columnid="3" plugintype="TEXT" />
			control.setTitle(StringUtils.trim(element.getAttributeValue("title")));
			control.setName(StringUtils.trim(element.getAttributeValue("name")));
			control.setControlId(StringUtils.trim(element.getAttributeValue("id")));
			control.setFormat(StringUtils.trim(element.getAttributeValue("format")));
			control.setFormatTip(StringUtils.trim(element.getAttributeValue("formatTip")));
			control.setControlValue(StringUtils.trim(element.getAttributeValue("value")));
			control.setFormatType(StringUtils.trim(element.getAttributeValue("formatType")));
			if(StringUtils.isNotEmpty(StringUtils.trim(element.getAttributeValue("readolny"))))control.setReadOlny( new Boolean(StringUtils.trim(element.getAttributeValue("readolny"))));
			if(StringUtils.isNotEmpty(StringUtils.trim(element.getAttributeValue("request"))))control.setRequest(new Boolean(StringUtils.trim(element.getAttributeValue("request"))));
			if(StringUtils.isNotEmpty(StringUtils.trim(element.getAttributeValue("datatype"))))control.setDataType(DataType.valueOf(StringUtils.trim(element.getAttributeValue("datatype"))));
//			if(StringUtils.isNotEmpty(StringUtils.trim(element.getAttributeValue("columnid"))))control.setTableColumnId(Long.valueOf(StringUtils.trim(element.getAttributeValue("columnid")))); 
			if(StringUtils.isNotEmpty(StringUtils.trim(element.getAttributeValue("dbName"))))control.setDbName(StringUtils.trim(element.getAttributeValue("dbName"))); 
			if(element.toString().toLowerCase().indexOf("<textarea")>-1){
				control.setControlType(ControlType.valueOf("TEXTAREA"));
			}else if(element.toString().toLowerCase().indexOf("<select")>-1){
				control.setControlType(ControlType.valueOf("SELECT"));
			}else if(element.toString().toLowerCase().indexOf("type=\"radio\"")>-1){
				control.setControlType(ControlType.valueOf("RADIO"));
			}else if(element.toString().toLowerCase().indexOf("type=\"checkbox\"")>-1){
				control.setControlType(ControlType.valueOf("CHECKBOX"));
			}else if(element.toString().toLowerCase().indexOf("type=\"password\"")>-1){
				control.setControlType(ControlType.valueOf("PASSWORD"));
			}else if(element.toString().toLowerCase().indexOf("type=\"hidden\"")>-1){
				control.setControlType(ControlType.valueOf("HIDDEN"));
			}else if(element.toString().toLowerCase().indexOf("type=\"button\"")>-1){
				control.setControlType(ControlType.valueOf("BUTTON"));
			}else{
				control.setControlType(ControlType.valueOf("TEXT"));
			}
			list.add(control);
		}
		 return list;
	 }
	 
	 /**
	 * 获得所有列表控件的id
	 * @return
	 */
	public List<String> getAllListControlIds(){
		List<String> result=new ArrayList<String>();
		List<Element> lists= source.getAllElements();
		for(Element element:lists){
			if( "ListControl".equals(element.getAttributeValue("pluginType"))){
				result.add(element.getAttributeValue("id"));
			}
		}
		return result;
	}
	
	public String setDefaultVal(List<FormControl> fields,List<TableColumn> columns){
		FormFields formFields=source.getFormFields();
		 for(FormControl control:fields){
			 for(TableColumn col:columns){
				 if(control.getName().equals(col.getName())){
					 formFields.setValue(control.getName(),col.getDefaultValue());
					 break;
				 }
			 }
		 }
		 //（自定义的标签会有乱码）
		outputDocument.replace(formFields); 
		return outputDocument.toString();
	}
		
	//初始化html
	 public String initHtml(List<FormControl> fields,List<AutomaticallyFilledField> filledField,List<TableColumn> columns){
		 FormFields formFields=source.getFormFields();
		 String key,value;
 		for(AutomaticallyFilledField aff :filledField){
 			key = aff.getName();
 			FormField formField = formFields.get(key);
 			if(formField!=null){
 				value = getAutoFilledFieldValue(fields,aff,formFields);
 				formFields.setValue(key,value);
 			}
 		}
		 //（自定义的标签会有乱码）
		outputDocument.replace(formFields); 
		return outputDocument.toString();
	 }
	 
	 private  String getAutoFilledFieldValue(List<FormControl> fields,AutomaticallyFilledField aff,FormFields formFields){
		 FormControl field = getFieldbyName(fields,aff.getName() );
		String value ;
		if(field.getDataType()==DataType.TIME){
			value = getFormatCurrentTime(aff,DataType.TIME);
		}else if(field.getDataType()==DataType.DATE){
			value = getFormatCurrentTime(aff,DataType.DATE);
		}else if(field.getDataType()==DataType.TEXT){
			value = getValue(aff);
			if(aff.getFillType().equals(AutomaticallyFilledField.AUTO_FILLED_FILL_TYPE_ADDITIONAL)){
				value = (formFields.getValues(aff.getName()).size()<=0?"":formFields.getValues(aff.getName()).get(0))+value;
			}else if(aff.getFillType().equals(AutomaticallyFilledField.AUTO_FILLED_FILL_TYPE_ADDED_TO_THE_BEGINNING)){
				value = value + (formFields.getValues(aff.getName()).size()<=0?"":formFields.getValues(aff.getName()).get(0));
			}
		}else{
			value = aff.getValue();
		}
		return value;
	}
	 
	 /*
	 * 从List中取出英文名为enName的Field
	 */
	private FormControl getFieldbyName(List<FormControl> fields , String enName){
		for(FormControl field:fields){
			if(field.getName().equals(enName)) return field;
		}
		return null;
	}
	
	private String getFormatCurrentTime(AutomaticallyFilledField aff,DataType dataType){
		String format ;
		switch(dataType){
		  	case TIME: format = "yyyy-MM-dd HH:mm" ;break;
		  	case DATE: format = "yyyy-MM-dd";break;
		  	default: return "";
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return StringUtils.contains(aff.getValue(), CommonStaticConstant.CURRENTTIME) ? simpleDateFormat.format(new Date()) :"";
	}
	
	 private String getValue(AutomaticallyFilledField aff){
		StringBuilder builder = new StringBuilder();
		String[] strs = null;
		String condition = aff.getValue();
		if(condition.indexOf('+')==-1){
			strs = new String[]{condition};
		}else{
			strs = condition.split("\\+");
		}
		for(int i=0;i<strs.length;i++){
			if(i!=0) builder.append(aff.getSeparate());
			if( CommonStaticConstant.CURRENTTRANSACTOR.equals(strs[i])){
				builder.append(ContextUtils.getLoginName());
			}else if(CommonStaticConstant.CURRENT_TRANSACTOR_NAME.equals(strs[i])){
				builder.append(ContextUtils.getUserName());
			}else if(CommonStaticConstant.CURRENTTIME.equals(strs[i])){
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				builder.append(simpleDateFormat.format(new Date()));
			}else{
				builder.append(strs[i]);
			}
		}
		return builder.toString();
	}
	 
	//往 FORM里插入值 
	 @SuppressWarnings("unchecked")
	 public String setFieldValue(Map<String, Object> map){
		 FormFields formFields=source.getFormFields();
		 Iterator it = formFields.iterator();
		 FormField formField;
		 Element element;
		 String type,dataType,key,valueKey;
		 while(it.hasNext()){
			formField = (FormField)it.next();
			element = formField.getFormControl().getElement();
			type = element.getAttributeValue(TYPE);
			dataType = element.getAttributeValue(DATATYPE);
			key = element.getAttributeValue(NAME);
			if("id".equals(key)||"instance_id".equals(key)){
				valueKey=key;
			}else{
				valueKey=JdbcSupport.FORM_FIELD_PREFIX_STRING+key;
			}
			type = type==null ? "" : type;
			dataType = dataType==null ? "" : dataType;
			
			if(type!=null&&type.equals("radio")){
				String value= map.get(valueKey)==null ?"":map.get(valueKey).toString();
				formFields.get(key).setValue(value);
			}else if(type!=null&&type.equals("checkbox")){
				Collection<String> c  = new ArrayList<String>();
				String value=map.get(valueKey)==null ?"":map.get(valueKey).toString();
				String[] values=value.split(",");
				for(int i=0;i<values.length;i++){
					String myval=values[i].trim();
					if(DataType.BOOLEAN.toString().equals(dataType)){
						myval="true".equals(myval)?"1":"0";
					}
					c.add(myval);
				}
				formFields.get(key).setValues(c);
			}else if(dataType.equals(DataType.TIME.toString())){
				if(map.get(valueKey)!=null){
					Date date = (Date)map.get(valueKey);
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					formFields.get(key).setValue(simpleDateFormat.format(date));
				} 
			}else if(dataType.equals(DataType.DATE.toString())){
				if(map.get(valueKey)!=null){
					Date date = (Date)map.get(valueKey);
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
					formFields.get(key).setValue(simpleDateFormat.format(date));
				} 
			}else{
				if(map.get(valueKey)!=null){
					formFields.get(key).setValue(map.get(valueKey).toString());
				}
			}
			
		 }
		
		 //（自定义的标签会有乱码）
		 outputDocument.replace(formFields);          
		 return outputDocument.toString();
	 }
	 
	//往 FORM里插入值 
	 @SuppressWarnings("unchecked")
	 public String setStandardFieldValue(Object entity){
		 FormFields formFields=source.getFormFields();
		 Iterator it = formFields.iterator();
		 FormField formField;
		 Element element;
		 String type,dataType,key,valueKey;
		 while(it.hasNext()){
			formField = (FormField)it.next();
			element = formField.getFormControl().getElement();
			type = element.getAttributeValue(TYPE);
			dataType = element.getAttributeValue(DATATYPE);
			key = element.getAttributeValue(NAME);
			valueKey=key;
			type = type==null ? "" : type;
			dataType = dataType==null ? "" : dataType;
			try {
				Object value = PropertyUtils.getProperty(entity, valueKey);
				if(value!=null){
					if(dataType.equals(DataType.DATE.toString())){
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
						formFields.get(key).setValue(simpleDateFormat.format(value));
					}else if(dataType.equals(DataType.TIME.toString())){
						DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
						formFields.get(key).setValue(simpleDateFormat.format(value));
					}else if(type!=null&&type.equals("checkbox")){
						Collection<String> c  = new ArrayList<String>();
						String val=value.toString();
						if(val.contains(",")){
							String[] values=value.toString().split(",");
							for(int i=0;i<values.length;i++){
								c.add(values[i].trim());
							}
							formFields.get(key).setValues(c);
						}else{
							formFields.get(key).setValue(val);
						}
					}else{
						if(!dataType.equals(DataType.COLLECTION.toString())){//不是集合类型
							formFields.get(key).setValue(value.toString());
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			} 
			
		 }
		
		 //（自定义的标签会有乱码）
		 outputDocument.replace(formFields);          
		 return outputDocument.toString();
	 }
	 
	 /**
	 * 获得所有列表控件中的子表单的字段
	 * @return
	 */
	public Map<String,Map<String,String>> getChildFormFields() {
		Map<String,Map<String,String>> result=new HashMap<String, Map<String,String>>();
		List<Element> lists= source.getAllElements();
		for(Element element:lists){
			if( "ListControl".equals(element.getAttributeValue("pluginType"))){
				String lvField=element.getAttributeValue("lv_field");
				String dataSrc=element.getAttributeValue("data_source");
				String[] lvFields=lvField.split(",");
				Map<String,String> fields=new HashMap<String,String>();
				for(String field:lvFields){
					String dataType=field.substring(field.indexOf(":")+1,field.length());
					field=field.substring(0,field.indexOf(":"));
					fields.put(field, dataType);
				}
				if(fields.size()>0){
					result.put(dataSrc, fields);
				}
			}
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	public String getFormHtml(FormView form,String formHtml,Map<String,List> queryMap,boolean fieldRight,boolean signatureVisible){
		List<Element> inputs=source.getAllElements("input");
		 StringBuilder jsHtml=new StringBuilder("");
			//如果有计算控件加入以下js代码
			if(isHasAnyTag(ControlType.CALCULATE_COMPONENT.toString(),inputs)){
				jsHtml.append("<script type='text/javascript'>parse();</script>");
			}
			if(queryMap==null || queryMap.size()<=0){
				if(isHasAnyTag(ControlType.LIST_CONTROL.toString(),inputs)){
					jsHtml.append("<script type='text/javascript'>parseListControl(\'"+fieldRight+"\');</script>");
				}
			}else{
				Set keySet=queryMap.keySet();
				Iterator it=keySet.iterator();
				while(it.hasNext()){
					String childSource=(String)it.next();
					List queryResult=queryMap.get(childSource);
					if(queryResult.size()<=0){
						if(isHasAnyTag(ControlType.LIST_CONTROL.toString(),inputs)){
							jsHtml.append("<script type='text/javascript'>parseListControl(\'"+fieldRight+"\');</script>");
						}
					}else{
						jsHtml.append("<script type='text/javascript'>initListControl(").append(childSource).append(");").append("</script>");
						jsHtml.append(parseListControl(childSource,queryResult,fieldRight));
					}
				}
			}
			//如果有下拉菜单控件加入以下js代码
			List<Element> selects=source.getAllElements("select");
			if(isHasAnySelectTag(ControlType.PULLDOWNMENU.toString(),selects)){
				String script=getPullDownMenuScript(selects);
				script="<script type='text/javascript'>var ___ignore=true;"+script+"</script>";
				jsHtml.append(script);
			}
			formHtml=preViewHtmlMacro(formHtml,form,jsHtml,signatureVisible);
			jsHtml.append("<script type='text/javascript'>__signatureVisible="+signatureVisible+";initSignatureControl("+signatureVisible+");").append("</script>");
			jsHtml.append("<script type='text/javascript'>initLabelControl();").append("</script>");
			formHtml=formHtml+jsHtml;
			 return formHtml; 
	 }
	
	/**
	 * 此方法只有在预览表单时候用
	 * @param form
	 * @param formHtml
	 * @return
	 */
	public String getFormHtml(FormView form,String formHtml){
		List<Element> inputs=source.getAllElements("input");
		 StringBuilder jsHtml=new StringBuilder("");
			//如果有计算控件加入以下js代码
			if(isHasAnyTag(ControlType.CALCULATE_COMPONENT.toString(),inputs)){
				jsHtml.append("<script type='text/javascript'>parse();</script>");
			}
			//如果有下拉菜单控件加入以下js代码
			List<Element> selects=source.getAllElements("select");
			if(isHasAnySelectTag(ControlType.PULLDOWNMENU.toString(),selects)){
				String script=getPullDownMenuScript(selects);
				script="<script type='text/javascript'>"+script+"</script>";
				jsHtml.append(script);
			}
			jsHtml.append("<script type='text/javascript'>initLabelControl();").append("</script>");
			formHtml=preViewHtmlMacro(formHtml,form,jsHtml,false)+jsHtml;
			 return formHtml; 
	 }
	
	 /**
	  * 预览html
	  * @return
	  */
	 public String preViewHtmlMacro(String formHtml,FormView form,StringBuilder jsHtml,boolean signatureVisible){
		 FormFields formFields=source.getFormFields();
		 Iterator<FormField> it = formFields.iterator();
		 FormField formField = null;
		 String signatureFields="";
		 while(it.hasNext()){
			 formField = it.next(); 
			 net.htmlparser.jericho.FormControl formcontrol = formField.getFormControl();
			 Element element = formcontrol.getElement();
			 String pluginType = element.getAttributeValue("pluginType");
			 if( "MacroComponent".equals(pluginType)){//如果是宏控件
				 String datafld = element.getAttributeValue("datafld");
				 if(element.toString().indexOf("<input")>-1){
					 formField.setValue(parseMacroInputControl( datafld,form));
				 }else if(element.toString().indexOf("<select")>-1){
					 addMacroSelectOptions(element,
							 parseMacroSelectControl(datafld),
							 outputDocument);
				 }
			 }else if("TEXT".equals(pluginType)){
				 String signaturevisible = element.getAttributeValue("signaturevisible");
				 if(signatureVisible &&"true".equals(signaturevisible)){//签章
					 List<Long> signIds=new ArrayList<Long>();
					 StringBuilder imgs = new StringBuilder();
					 //获得当前文本框的值，并根据该值获得签章路径，该值一般为用户真名
					 List<String> values = formField.getValues();
					 String url = SystemUrls.getSystemUrl("mms");
					 if(values.size()>0){
						 if(StringUtils.isNotEmpty(values.get(0))){
							 String names = values.get(0);
							 String[] nameArr = names.split(",");//多个人名之间以逗号隔开
							 for(String name:nameArr){
								 Long  signId = ApiFactory.getSettingService().getSignIdByUserName(name);
								 if(signId!=null){
									 signIds.add(signId);
									 imgs.append("<img src='"+url+"/form/form-view-showPic.htm?signId="+signId+"' style='width:2cm;height:1cm;'></img>").append(",");
								 }else{
									 imgs.append(name).append(",");
								 }
							 }
						 }
					 }
					 if(signIds.size()>0){//如果签章路径存在
						 if(StringUtils.isEmpty(signatureFields)){
							 signatureFields=element.getAttributeValue("name");
						 }else{
							 signatureFields=signatureFields+","+element.getAttributeValue("name");
						 }
					 }
					 if(signIds.size()>0){//如果多个人都没有签章时，则不做任何操作;当有的人有签章，有的人没签章时，则执行以下操作
						 if(StringUtils.isNotEmpty(imgs.toString())){
							 imgs.replace(imgs.length()-1, imgs.length(), "");
							 outputDocument.replace(element.getEnd(), element.getEnd(), imgs.toString() );
						 }
					 }
				 }
				 //给签章字段集合赋值，用于验证和处理签章控件的隐藏 显示
				 jsHtml.append("<script type='text/javascript'>");
				 if(StringUtils.isNotEmpty(signatureFields)){
					 jsHtml.append("__signatureFields='").append(signatureFields).append("';");
				 }else{
					 jsHtml.append("__signatureFields='';");
				 }
				 jsHtml.append("</script>");
			 }
		 }
		 //（自定义的标签会有乱码）
		 outputDocument.replace(formFields);          
		 return outputDocument.toString(); 
	 }

	 
	 /*
		 * 宏控件
		 * 为select元素添加option选项 
		 * @param element
		 * @param outputDocument
		 */
		private void addMacroSelectOptions(Element element,Map<Long,String> options, OutputDocument outputDocument){
			String opts = getMacroSelectOptions(options);
			outputDocument.replace(element.getBegin(), element.getEnd(), 
					element.toString().replace("><option value=\"\" selected=\"selected\">{宏控件}</option>", opts));
		}
	 
	 /**
	 * 用户角色
	 * @param departmentName
	 * @return
	 */
	private Map<Long, String> getUsersByRole(String departmentName){
		Map<Long, String> map = new HashMap<Long, String>();
//			Set<User> userSet = acsUtils.getUsersByRole(contextService.getSystemId(), contextService.getCompanyId(), CommonStaticConstant.MINISTER);
//			for(User user:userSet){
//				Set<DepartmentToUser> depat = user.getDepartmentToUser();
//				for(DepartmentToUser department:depat){
//					String name = department.getDepartment().getDepartmentName();
//					if(name.equals(departmentName)){
//						map.put(user.getId(), user.getName());
//						break;
//					}
//				}
//			}
		return map;
	}
	
	/*
	 * 宏控件
	 * 根据规则获取select的option选项 
	 * @param fieldName
	 * @return
	 */
	private String getMacroSelectOptions(Map<Long,String> options){
		StringBuilder opts = new StringBuilder(">");
		opts.append("<option value=\"\">--请选择--</option>");
		for(Entry<Long, String> opt : options.entrySet()){
			opts.append("<option value=\"").append(opt.getKey()).append("\">").append(opt.getValue()).append("</option>");
		}
		return opts.toString();
	}
	 
		/**宏控件
		 * 初始化宏控件(Select)
		 */
	@SuppressWarnings("unchecked")
		 private Map<Long,String> parseMacroSelectControl(String datafld){
		AcsUtils acsUtils = (AcsUtils)ContextUtils.getBean("acsUtils");
			 Map map=new HashMap<Long, String>();
			 if("SYS_LIST_ROLE".equals(datafld)){
				 Set<Role> roles=acsUtils.getRolesByUser(ContextUtils.getUserId(),ContextUtils.getCompanyId());
					Iterator ite=roles.iterator();//将hs转换为一个可遍历的对象Iterator
					while(ite.hasNext()){
						Role role=(Role)ite.next();
						map.put(role.getId(), role.getName());
					}
			 }else if("SYS_LIST_MANAGER1".equals(datafld)){
				 List<Department> list=acsUtils.getDepartmentsByUser(ContextUtils.getCompanyId(), ContextUtils.getUserId());
				for(Department dept:list){
					map = getUsersByRole(dept.getName());
				}
			 }else if("SYS_LIST_MANAGER2".equals(datafld)){
				 List<Department> list=acsUtils.getDepartmentsByUser(ContextUtils.getCompanyId(), ContextUtils.getUserId());
					for(Department dept:list){
						if(dept.getParent()!=null){
							map=getUsersByRole(dept.getParent().getName());
						}
					}
				 
			 }else if("SYS_LIST_MANAGER3".equals(datafld)){
				 List<Department> list=acsUtils.getDepartmentsByUser(ContextUtils.getCompanyId(), ContextUtils.getUserId());
					for(Department dept:list){
						if(dept.getParent()!=null){
							map=getUsersByRole(getTopDepts(dept).getParent().getName());
						}
					}
			 }
			 return map;
		 }
		 
		 /**
		  * 获得顶级部门
		  * @param dept
		  * @return
		  */
		 private Department getTopDepts(Department dept){
			 Department department=null;
			 if(dept.getParent()!=null){
				 getTopDepts(dept.getParent());
			 }else{
				 department=dept;
			 }
			 return department;
		 }
	 
	//初始化宏控件(Input)
		private String parseMacroInputControl(String datafld,FormView form){
			String result = "";
//			if("SYS_DATE".equals(datafld)){
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//				result = dateFormat.format(new Date());
//			}else if("SYS_DATE_CN".equals(datafld)){
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
//				result = dateFormat.format(new Date());
//			}else if("SYS_DATE_CN_SHORT3".equals(datafld)){
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年");
//				result = dateFormat.format(new Date());
//			}else if("SYS_DATE_CN_SHORT4".equals(datafld)){
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
//				result = dateFormat.format(new Date());
//			}else if("SYS_DATE_CN_SHORT1".equals(datafld)){
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月");
//				result = dateFormat.format(new Date());
//			}else if("SYS_DATE_CN_SHORT2".equals(datafld)){
//				SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日");
//				result = dateFormat.format(new Date());
//			}else if("SYS_TIME".equals(datafld)){
//				SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
//				result = dateFormat.format(new Date());
//			}else if("SYS_DATETIME".equals(datafld)){
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//				result = dateFormat.format(new Date());
//			}else if("SYS_WEEK".equals(datafld)){
//				 Calendar calendar = Calendar.getInstance();
//				 result =FormUtil.getWeek(calendar);
//			}else if("SYS_USERNAME".equals(datafld)){
//				result = contextService.getUserName();
//			}else if("SYS_DEPTNAME".equals(datafld)){
//				List<Department> list=acsUtils.getDepartmentsByUser(contextService.getCompanyId(), contextService.getUserId());
//				for(Department dept:list){
//					result = result+dept.getDepartmentName()+",";
//				}
//				if(!"".equals(result)){
//					result=result.substring(0, result.length()-1);
//				}
//			}else if("SYS_DEPTNAME_SHORT".equals(datafld)){
//				List<Department> list=ApiFactory.getAcsService().getDepartmentsByUser(contextService.getCompanyId(), contextService.getUserId());
//				for(Department dept:list){
//					if(dept.getShortTitle()!=null){
//						result = result+dept.getShortTitle()+",";
//					}
//				}
//				if(!"".equals(result)){
//					result=result.substring(0, result.length()-1);
//				}
//			}else if("SYS_USERROLE".equals(datafld)){
//				Set<Role> roles=ApiFactory.getAcsService().getRolesByUser(contextService.getCompanyId(), contextService.getLoginName());
//				Iterator ite=roles.iterator();//将hs转换为一个可遍历的对象Iterator
//				while(ite.hasNext()){
//					Role role=(Role)ite.next();
//					result = result+role.getRoleName()+",";
//				}
//				if(!"".equals(result)){
//					result=result.substring(0, result.length()-1);
//				}
//			}else if("SYS_USERNAME_DATE".equals(datafld)){
//				result = contextService.getUserName();
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//				result = result+" "+dateFormat.format(new Date());
//			}else if("SYS_USERNAME_DATETIME".equals(datafld)){
//				result = contextService.getUserName();
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//				result = result+" "+dateFormat.format(new Date());
//			}else if("SYS_FORMNAME".equals(datafld)){
//				result = form.getFormName();
//			}else if("SYS_MANAGER1".equals(datafld)){
//				List<Department> list=acsUtils.getDepartmentsByUser(contextService.getCompanyId(), contextService.getUserId());
//				for(Department dept:list){
//					result = getResultsUsersByRole(dept.getDepartmentName());
//				}
//				if(!"".equals(result)){
//					result=result.substring(0, result.length()-1);
//				}
//			}else if("SYS_MANAGER2".equals(datafld)){
//				List<Department> list=acsUtils.getDepartmentsByUser(contextService.getCompanyId(), contextService.getUserId());
//				for(Department dept:list){
//					if(dept.getParentDepartment()!=null){
//						result=getResultsUsersByRole(dept.getParentDepartment().getDepartmentName());
//					}
//				}
//				if(!"".equals(result)){
//					result=result.substring(0, result.length()-1);
//				}
//			}else if("SYS_MANAGER3".equals(datafld)){
//				List<Department> list=acsUtils.getDepartmentsByUser(contextService.getCompanyId(), contextService.getUserId());
//				for(Department dept:list){
//					if(dept.getParentDepartment()!=null){
//						result=getResultsUsersByRole(getTopDepts(dept).getParentDepartment().getDepartmentName());
//					}
//				}
//				if(!"".equals(result)){
//					result=result.substring(0, result.length()-1);
//				}
//			}
			return result;
		 }
	
	//列表控件的初始化
		@SuppressWarnings("unchecked")
	private String parseListControl(String childSource,List queryResult,boolean fieldRight){
		StringBuilder html=new StringBuilder("");
		 List<Element> lists= source.getAllElements();
			for(Element element:lists){
				if( "LIST_CONTROL".equals(element.getAttributeValue("pluginType")) && childSource.equals(element.getAttributeValue("data_source"))){//如果是宏控件
					String elementId=element.getAttributeValue("id");
					String dataSrc=element.getAttributeValue("data_source");
					String lvTitle=element.getAttributeValue("lv_title");
					String lvSize=element.getAttributeValue("lv_size");
					String lvSum=element.getAttributeValue("lv_sum");
					String lvCal=element.getAttributeValue("lv_cal");
					String lvField=element.getAttributeValue("lv_field");
					String childForm="<input type=hidden name='dataSrc_"+elementId+"' id='dataSrc_"+elementId+"' value='"+dataSrc+"'>";
					html.append(childForm);
					html.append("<table class='Table changeTR' id='tb_").append(elementId).append("'><thead><tr>");
					String[] lvTitles=lvTitle.split(",");
					String[] lvSizes=lvSize.split(",");
					for(int j=0;j<lvTitles.length;j++){
						if(!"".equals(lvTitles[j])){
							html.append("<th style='width:").append(lvSizes[j]).append("px;'>").append(lvTitles[j]).append("</th>");
						}
					}
					html.append("<th style='width:100px;'>操作</th>");
					html.append("</tr></thead><tbody>")
					.append(listControlAddRow(queryResult,"tb_"+elementId,lvSum,lvField,lvCal,fieldRight))
					.append("</tbody></table>");
					if(fieldRight){
						html.append("<input type=button value=\"新增\"  onclick=\"listControlAddRow(\'tb_")
						.append(elementId).append("\',\'").append(lvSum).append("\',\'").append(lvField)
						.append("\',\'").append(lvCal).append("\',\'").append(fieldRight).append("\');\">");	
					}
				}
			}
			return html.toString();
	}
	
	//列表控件的"新增"
		@SuppressWarnings("unchecked")
	private String listControlAddRow(List queryResult,String tbId,String lvSum,String lvField,String lvCal,boolean fieldRight){
		String[] lvFields=lvField.split(",");
//		String[] tr_num=$("#"+tb_id+" tbody tr").length>0?$("#"+tb_id+" tbody tr").length-1:0;
		String[] lvCals=lvCal.split(",");
		StringBuilder html=new StringBuilder("");
		for(int i=0;i<queryResult.size();i++){
			html.append("<tr id='listControl_tr_").append(tbId).append("_").append(i).append("'>");
			for(int j=0;j<lvFields.length;j++){
				if(!"".equals(lvFields[j])){
					String elementId=tbId.substring(tbId.indexOf("_")+1,tbId.length());
					html.append("<td>");
					String dataType=lvFields[j].substring(lvFields[j].indexOf(":")+1,lvFields[j].length());
					String field=lvFields[j].substring(0,lvFields[j].indexOf(":"));
//					onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',el:aa})" //日期控件
					String currentId="listControl_"+tbId+"_"+i+"_"+(j+1);
					String dateOnFocus="onfocus='WdatePicker({dateFmt:\"yyyy-MM-dd\",el:"+currentId+"})'";
					if("DATE".equals(dataType) || "TIME".equals(dataType)){
						if("DATE".equals(dataType)){
							html.append("<input style='width:95%;' name='listControl_").append(elementId).append("_").append(lvFields[j]).append("' ");
							if(fieldRight){
								html.append(dateOnFocus);
							}
							html.append(" id='").append(currentId).append("' readonly");
						}else{
							html.append("<input style='width:95%;' name='listControl_").append(elementId).append("_").append(lvFields[j]).append("' ");
							if(fieldRight){
								html.append(dateOnFocus);
							}
							html.append(" id='").append(currentId).append("' readonly");;
						}
					}else{
						html.append("<input style='width:95%;' name='listControl_").append(elementId).append("_").append(lvFields[j])
						.append("' id='listControl_").append(tbId).append("_").append(i).append("_").append(j+1).append("'");
						if(!"0".equals(lvCals[j]) || !fieldRight){
							html.append(" readonly");
						}
					}
					String value="";
					value=((Map)queryResult.get(i)).get("wf_"+field).toString()==null?"":((Map)queryResult.get(i)).get("wf_"+field).toString();
					html.append(" value='").append(value).append("'></input></td>") ;
				}
			}
			
			html.append("<td>");
			if(fieldRight){
				html.append("<a href=\"#\" onclick=\"listControlDelRow(this,'").append(tbId).append("','").append(lvSum).append("')\">删除</a>");
			}
			html.append("</td>");
			html.append("</tr>");
		}
		if(isNeedSum(lvSum)){
			html.append(listControlAddSumRow(tbId,lvSum));
		}
		return html.toString();
	}
	
	//列表控件增加"合计"行
	private String listControlAddSumRow(String tbId,String lvSum){
		StringBuilder html=new StringBuilder("");
		String[] lvSums=lvSum.split(",");
		html.append("<tr id='listControlSum_").append(tbId).append("'>");
		for(int i=0;i<lvSums.length;i++){
			if("".equals(lvSums[i])){
				html.append("<td><input style='width:95%;' readonly " );
				String elementId=tbId.substring(tbId.indexOf("_")+1,tbId.length());
				if("1".equals(lvSums[i])){
					html.append("id='listControlSum_").append(tbId).append("_td_").append(i+1).append("' name='listControlSum_").append(elementId)
					.append("_").append(i+1).append("' value='0'");
				}
				html.append("></input></td>");
			}
		}
		html.append("<td></td></tr>");
		return html.toString();
	}
	
	
	
	//是否需要合计
	private boolean isNeedSum(String lvSum){
		String[] lvSumS=lvSum.split(",");
		for(int i=0;i<lvSumS.length;i++){
			if(lvSumS[i]!=""){
				if("1".equals(lvSumS[i])){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 获得所有列表控件的id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getPullDownMenuScript(List<Element> lists){
		String script="";
//		List<Element> lists=source.getAllElements("select");
		for (Iterator iterator = lists.iterator(); iterator.hasNext();) {
			Element element = (Element) iterator.next();
			if( ControlType.PULLDOWNMENU.toString().equals(element.getAttributeValue("pluginType"))){
				String txtChild=element.getAttributeValue("child");
				if(txtChild!=null){
					script+=element.getAttributeValue("scriptContent");
				}
			}
		}
		return script;
	}

	//是否含有计算控件
	@SuppressWarnings("unchecked")
	public boolean isHasAnyTag(String type,List<Element> inputs){
		int num=0;
		boolean isHasCalc=false;
		for (Iterator iterator = inputs.iterator(); iterator.hasNext();) {
			Element input = (Element) iterator.next();
			if(type.equals(input.getAttributeValue("pluginType"))){
				num++;	
				if(num>=1){
					isHasCalc=true;
					break;
				}
			}
		}
		return isHasCalc;
	}
	
	//是否含有select控件
	@SuppressWarnings("unchecked")
	public boolean isHasAnySelectTag(String type,List<Element> inputs){
		int num=0;
		boolean isHasCalc=false;
		for (Iterator iterator = inputs.iterator(); iterator.hasNext();) {
			Element select = (Element) iterator.next();
			if(type.equals(select.getAttributeValue("pluginType"))){
				num++;	
				if(num>=1){
					isHasCalc=true;
					break;
				}
			}
		}
		return isHasCalc;
	}
	
	/*
	 * 数据选择控件/获取数据来源
	 * @return
	 */
	public String getDataSource(String controlId){
		 String dataSrc=null;
		 List<Element> lists= source.getAllElements();
		for(Element element:lists){
			if( ControlType.DATA_SELECTION.toString().equals(element.getAttributeValue("pluginType")) || ControlType.DATA_ACQUISITION.toString().equals(element.getAttributeValue("pluginType"))){//如果是宏控件
				 if(controlId!=null && controlId.equals(element.getAttributeValue("id"))){
					 dataSrc= element.getAttributeValue("data_table");
					 break;
				 }
			 }
		}
		return dataSrc;
	}
	
	/*
	 * 数据选择控件/获取字段集合
	 * @return
	 */
	public List<String[]> getDataProperties(String controlId,DataTable dataTable){
		 List<String[]> myProperties=new ArrayList<String[]>();
		 List<Element> lists= source.getAllElements();
		for(Element element:lists){
			if(ControlType.DATA_SELECTION.toString().equals(element.getAttributeValue("pluginType"))){//如果是数据选择控件
				 if(controlId!=null && controlId.equals(element.getAttributeValue("id"))){
					 String propertyNameStr=element.getAttributeValue("data_fld_name");
					 String propertyStr=element.getAttributeValue("data_field");
					 String queryProStr=element.getAttributeValue("data_query");
					 String[] queryPros=queryProStr.split(",");
					 String[] properties=propertyStr.split(",");
					 String[] propertyNames=propertyNameStr.split(",");
					 for(int i=0;i<properties.length;i++){
						 if(!"".equals(properties[i])){
//							 if(StringUtils.isNotEmpty(dataTable.getEntityName())){
//								 myProperties.add(new String[]{properties[i],propertyNames[i],queryPros[i]});
//							 }else{
//								 myProperties.add(new String[]{JdbcSupport.FORM_FIELD_PREFIX_STRING+properties[i],propertyNames[i],queryPros[i]});
//							 }
							 myProperties.add(new String[]{properties[i],propertyNames[i],queryPros[i]});
						 }
					 }
					 break;
				 }
			 }else if(ControlType.DATA_ACQUISITION.toString().equals(element.getAttributeValue("pluginType"))){
				 String propertyNameStr=element.getAttributeValue("data_fld_name");
				 String propertyStr=element.getAttributeValue("data_field");
				 String[] properties=propertyStr.split(",");
				 String[] propertyNames=propertyNameStr.split(",");
				 for(int i=0;i<properties.length;i++){
					 if(!"".equals(properties[i])){
						 if(StringUtils.isNotEmpty(dataTable.getEntityName())){
							 myProperties.add(new String[]{properties[i],propertyNames[i]});
						 }else{
							 myProperties.add(new String[]{JdbcSupport.FORM_FIELD_PREFIX_STRING+properties[i],propertyNames[i]});
						 }
					 }
				 }
			 }
		}
		return myProperties;
	}
	
	/*
	 * 数据选择控件/获取查询语句
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getDataSqlCondition(String controlId,DataTable dataTable){
		 StringBuilder result=new StringBuilder("");;
		 List<Element> lists= source.getAllElements("input");
		 for (Iterator iterator = lists.iterator(); iterator.hasNext();) {
				Element element = (Element) iterator.next();
			if( ControlType.DATA_SELECTION.toString().equals(element.getAttributeValue("pluginType"))){//如果是宏控件
				 if(controlId!=null && controlId.equals(element.getAttributeValue("id"))){
					 String queryProStr=element.getAttributeValue("data_query");
					 String propertyStr=element.getAttributeValue("data_field");
					 String[] queryPros=queryProStr.split(",");
					 String[] properties=propertyStr.split(",");
					 if(StringUtils.isNotEmpty(dataTable.getEntityName())){
						 result.append("select ");
						 for(int i=0;i<properties.length;i++){
							 if(!"".equals(properties[i])){
								 result.append("f.").append(properties[i]).append(",");
							 }
						 }
						 if(result.indexOf(",")>=0){
							 result=new StringBuilder(result.substring(0,result.length()-1));
						 }
						 result.append(" from ").append(dataTable.getName()).append(" f");
					 }else{
						 result.append("select ");
						 for(int i=0;i<properties.length;i++){
							 if(!"".equals(queryPros[i])){
								 result.append(JdbcSupport.FORM_FIELD_PREFIX_STRING).append(properties[i]).append(",");
							 }
						 }
						 if(result.indexOf(",")>=0){
							 result=new StringBuilder(result.substring(0,result.length()-1));
						 }
						 result.append(" from ").append(dataTable.getName());
					 }
					 break;
				 }
			 }
		}
		return result.toString();
	}
	
	/*
	 * 数据获取控件/获取查询语句
	 * @return
	 */
	public String getDataAcqSqlCondition(String controlId,DataTable dataTable,String referenceControlValue,List<TableColumn> tableColumns){
		 StringBuilder result=new StringBuilder("");
		 List<Element> lists= source.getAllElements("input");
		for(Element element:lists){
 			if( ControlType.DATA_ACQUISITION.toString() .equals(element.getAttributeValue("pluginType"))){//如果是数据获取控件
				 if(controlId!=null && controlId.equals(element.getAttributeValue("id"))){
					 String propertyStr=element.getAttributeValue("data_field");
					 String[] properties=propertyStr.split(",");
					 if(StringUtils.isNotEmpty(dataTable.getEntityName())){//是标准表单时
						 result.append("select ");
						 for(int i=0;i<properties.length;i++){
							 if(!"".equals(properties[i])){
								 result.append("f.").append(properties[i]).append(",");
							 }
						 }
						 if(result.indexOf(",")>=0){
							 result=new StringBuilder(result.substring(0,result.length()-1));
						 }
						 result.append(" from ").append(dataTable.getEntityName()).append(" f");
						 String query=element.getAttributeValue("query_property");
						 if(!"".equals(query)){
							 for(TableColumn field :tableColumns){
								 if(field.getName().equals(query)){
									 result.append(" where ").append("f.").append(query).append("=?");
									 break;
								 }
							 }
						 }
						 
					 }else{ //是自定义表单时
						 result.append("select ");
						 for(int i=0;i<properties.length;i++){
							 if(!"".equals(properties[i])){
								 if(properties[i].indexOf(JdbcSupport.FORM_FIELD_PREFIX_STRING)==0){
									 result.append(properties[i]).append(",");
								 }else{
									 result.append(JdbcSupport.FORM_FIELD_PREFIX_STRING+properties[i]).append(",");
								 }
								 
							 }
						 }
						 if(result.indexOf(",")>=0){
							 result=new StringBuilder(result.substring(0,result.length()-1));
						 }
						 result.append(" from ").append(dataTable.getName());
						 String query=element.getAttributeValue("query_property");
						 if(!"".equals(query)){
							 for(TableColumn field :tableColumns){
								 if(field.getDbColumnName().equals(query)){
									 if(DataType.TEXT.equals(field.getDataType())  ||field.getDataType().equals(DataType. CLOB)){
										 result.append(" where ").append(query).append(" like '%").append(referenceControlValue).append("%'");
									 }else{
										 result.append(" where ").append(query).append("=").append(referenceControlValue);
									 }
									 
									 break;
								 }
							 }
						 }
					 }
					 break;
				 }
			 }
		}
		return result.toString();
	}
	
	/**
	 * 获得标准字段值
	 * @param entity
	 * @return
	 */
	 @SuppressWarnings("unchecked")
	 public Map<String,Collection> getStandardFieldValue(Object entity){
		 String type,dataType,key,valueKey;
		 Map<String,Collection> resultMap=new HashMap<String, Collection>();
		 List<Element> lists= source.getAllElements("input");
			for(Element element:lists){
				if( ControlType.STANDARD_LIST_CONTROL.toString() .equals(element.getAttributeValue("pluginType"))){
					type = element.getAttributeValue(TYPE);
					dataType = element.getAttributeValue(DATATYPE);
					key = element.getAttributeValue(NAME);
					valueKey=key;
					type = type==null ? "" : type;
					dataType = dataType==null ? "" : dataType;
					resultMap=getEntityCollection(valueKey,entity);
				}
			}
		 return resultMap;
	 }
	 @SuppressWarnings("unchecked")
	 public Map<String,Collection> getEntityCollection(String valueKey,Object entity){
		 Map<String,Collection> resultMap=new HashMap<String, Collection>();
		 try {
		 String start=StringUtils.upperCase(valueKey.substring(0,1));
			String other=valueKey.substring(1,valueKey.length());
			Object value = entity.getClass().getMethod("get"+start+other).invoke(entity);
			if(value!=null){
				if(value instanceof Collection){
					resultMap.put(valueKey, (Collection)value);
				}
			}
		 } catch (Exception e) {
				throw new RuntimeException(e);
		} 
		 return resultMap;
	 }
	 /**
	  * 获得标准表单html
	  * @param form
	  * @param formHtml
	  * @return
	  */
	 @SuppressWarnings("unchecked")
	 public String getStandardFormHtml(FormView form,String formHtml,Object entity,Collection collection,boolean signatureVisible){
		 Map<String,Collection> resultMap=new HashMap<String, Collection>();
		 if(entity!=null&&(collection==null||collection.size()<=0)){
			 resultMap=getStandardFieldValue(entity);
		 }
		this.setFormHtml(formHtml);
		List<Element> inputs=source.getAllElements("input");
		StringBuilder jsHtml=new StringBuilder("");
		//如果有计算控件加入以下js代码
		if(isHasAnyTag(ControlType.CALCULATE_COMPONENT.toString(),inputs)){
			jsHtml.append("<script type='text/javascript'>parse();</script>");
		}
		if(collection!=null&&collection.size()>0){
			jsHtml.append("<script type='text/javascript'>0();").append("</script>");
			jsHtml.append(parseStandardListControl(collection,entity));
		}else if(resultMap==null || resultMap.size()<=0){
			if(isHasAnyTag(ControlType.STANDARD_LIST_CONTROL.toString(),inputs)){
				jsHtml.append("<script type='text/javascript'>initStandardListControl();</script>");
				jsHtml.append(parseStandardListControl(new ArrayList(),entity));
			}
		}else{
			Set keySet=resultMap.keySet();
			Iterator it=keySet.iterator();
			while(it.hasNext()){
				String childSource=(String)it.next();
				Collection queryResult=resultMap.get(childSource);
				if(queryResult.size()<=0){
					if(isHasAnyTag(ControlType.STANDARD_LIST_CONTROL.toString(),inputs)){
						jsHtml.append("<script type='text/javascript'>initStandardListControl();</script>");
						jsHtml.append(parseStandardListControl(queryResult,entity));
					}
				}else{
					jsHtml.append("<script type='text/javascript'>initStandardListControl();").append("</script>");
					jsHtml.append(parseStandardListControl(queryResult,entity));
				}
			}
		}
		//如果有下拉菜单控件加入以下js代码
		 List<Element> selects=source.getAllElements("select");
		if(isHasAnySelectTag(ControlType.PULLDOWNMENU.toString(),selects)){
			String script=getPullDownMenuScript(selects);
			script="<script type='text/javascript'>var ___ignore=true;"+script+"</script>";
			jsHtml.append(script);
		}
		//如果有签章加入js代码
		formHtml=preViewHtmlMacro(formHtml,form,jsHtml,signatureVisible);
		jsHtml.append("<script type='text/javascript'>__signatureVisible="+signatureVisible+";initSignatureControl("+signatureVisible+");").append("</script>");
		jsHtml.append("<script type='text/javascript'>initLabelControl();").append("</script>");
		formHtml=formHtml+jsHtml;
		 return formHtml; 
	 }
	 
	 
	//标准列表控件的初始化
	 @SuppressWarnings("unchecked")
	 private String parseStandardListControl(Collection queryResult,Object entity){
		 StringBuilder html=new StringBuilder("");
		 List<Element> lists= source.getAllElements("input");
	 	for(Element element:lists){
	 		if( "STANDARD_LIST_CONTROL".equals(element.getAttributeValue("pluginType")) ){//如果是宏控件
//	 			String listViewId=element.getAttributeValue("listViewId");
	 			String listViewCode=element.getAttributeValue("listViewCode");
	 			String elementId=element.getAttributeValue("id");
	 			html.append("<table id='tb_")
	 			.append(elementId).append("' newId='1'></table>");
	 			html.append(getJqGridScript("tb_"+elementId,listViewCode,queryResult,entity));
	 		}
	 	}
	 	return html.toString();
	 }
	 /**
	  * 获得子表列表的script脚本
	  * @return
	  */
	 @SuppressWarnings("unchecked")
	 private String getJqGridScript(String tableId,String listViewCode,Collection queryResult,Object entity){
		 ListViewManager listViewManager = (ListViewManager)ContextUtils.getBean("listViewManager");
		 StringBuilder script=new StringBuilder("");
		 ListView listView=listViewManager.getListViewByCode(listViewCode);
		 List<ListColumn> columns=listView.getColumns();
		 String footerDatas=getFooterDatas(queryResult,columns,listView.getEditable());
		 String[] colResult=getColNames(columns,listView,entity,null,null);
		 String data=getJsonData(queryResult,columns);
		 script.append("<script type='text/javascript'>")
		 .append("$(document).ready(function(){");
		 String deleteUrl = listView.getDeleteUrl();
		 //判断是否是子系统，编辑时删除行时用到
		 String webCtx = SystemUrls.getSystemUrl(ContextUtils.getSystemCode());
		 boolean isSubSystem = false;
		 if(StringUtils.isNotEmpty(deleteUrl)){
				//  /acs/organization/user!delete.action，当是这种主子系统的情况，则第一个/后的字符串应为子系统编码
				String systemCode = deleteUrl.split("/")[1];
				BusinessSystemManager businessSystemManager = (BusinessSystemManager)ContextUtils.getBean("businessSystemManager");
				BusinessSystem business = businessSystemManager.getSystemBySystemCode(systemCode);
				if(business!=null){
					if(StringUtils.isNotEmpty(business.getParentCode())){
						isSubSystem= true;
						webCtx = SystemUrls.getSystemUrl(business.getParentCode());
					}
				}
			}
		 if(StringUtils.isNotEmpty(deleteUrl)){
			 script.append("deleteUrl='").append(listView.getDeleteUrl()).append("';");
		 }else{
			 script.append("deleteUrl=\"\";");
		 }
		 script.append("jQuery('#").append(tableId).append("').jqGrid({")
		 .append("datatype:'local',")
		 .append("mtype:'POST',")
		 .append("rowNum:200,")
		 .append("colNames:").append(colResult[0]).append(",")
		 .append("colModel:").append(colResult[1]).append(",");
		if(listView.getMultiSelect()!=null&&listView.getMultiSelect()){
			script.append("multiselect: true").append(",");
			script.append("multiboxonly:").append(listView.getMultiboxSelectOnly()).append(",");
		}else {
			script.append("multiselect: false").append(",");
		}
		script.append(getOnSelectRowScript(tableId)).append(",");
		script.append("editurl:'clientArray'").append(",");
		script.append("data:").append(data).append(",");
		 script.append("pager: '#").append(tableId).append("pager'");
		 if(listView.getRowNumbers()){//序号
			 script.append(",rownumbers:true");
		 }
		 if(StringUtils.isNotEmpty(footerDatas)){//合计
			 script.append(",");
			 script.append("footerrow : true,")
			 .append("userDataOnFooter : true,")
			 .append("altRows : true");
		 }
		 if(listView.getEditable()!=null&&listView.getEditable()){//操作
			 script.append(",");
			 script.append(getGridCompleteScript(tableId,columns,isSubSystem,webCtx));
		 }
		 if(StringUtils.isNotEmpty(listView.getCustomProperty())){//自由扩展属性
			 script.append(",");
			 script.append(listView.getCustomProperty());
		 }
		 if(StringUtils.isNotEmpty(listView.getOrderFieldName())){//行拖到
			 script.append(",indexname:'").append(listView.getOrderFieldName()).append("'");
		 }else{
			 script.append(",indexname:'false'");
		 }
		 script.append(",onCellSelect:$onCellClick");
		 script.append(",ondblClickRow: function(id){$ondblClick(id);}");
		 script.append("});");
		 if(StringUtils.isNotEmpty(listView.getOrderFieldName())){//行拖到
			 script.append("var _rowId;")
			 .append("var sortableOptions = {")
			 .append("items : '.jqgrow:not(.unsortable)',")
			 .append("start : function(event, ui) {")
			 .append("_rowId = ui.item.attr('id');")
			 .append("var originalIndex = jQuery(\"#").append(tableId).append("\").jqGrid(\"getInd\", _rowId);")
			 .append("$sortableRowsStart(_rowId,originalIndex,'").append(tableId).append("');")
			 .append("},")
			 .append("stop : function(event, ui) {")
			 .append("var newIndex = jQuery(\"#").append(tableId).append("\"").append(").jqGrid(\"getInd\", _rowId);")
			 .append("$sortableRowsStop(_rowId,newIndex,'").append(tableId).append("');")
			 .append("}")
			 .append("};");
			 script.append("jQuery('#").append(tableId).append("').jqGrid('sortableRows',sortableOptions);");
		 }
		 script.append("var mydata=").append(data).append(";")
		 .append("for(var i=0;i<mydata.length;i++){")
		 .append("if(mydata[i].id=='new_0'){")
		 .append("jQuery('#").append(tableId).append("').jqGrid('editRow',mydata[i].id,true,function(){editFun(mydata[i].id);},function(){},'',{},function(){hasEdit=false;$editRowSave(mydata[i].id,'").append(tableId).append("');lastsel=0;},function(){},function(){hasEdit=false;$editRowRestore(mydata[i].id,'").append(tableId).append("');lastsel=0;if(mydata[i].id.indexOf('new_')>=0){jQuery('#").append(tableId).append("').jqGrid('delRowData',mydata[i].id); _add_row('").append(tableId).append("'); }}")
		 .append(");}")
		 .append("}");
		 if(StringUtils.isNotEmpty(footerDatas)){
			 script.append("jQuery('#").append(tableId).append("').jqGrid('footerData','set',").append(footerDatas).append(");");
		 }
		 script.append("});");
		 
		 script.append(editClick(tableId));
		 
		 script.append("function editFun(id){");
		 script.append("lastsel=id;hasEdit=true;");
		 for(ListColumn col: columns){
			 if(col.getTableColumn()!=null){
				
				 if(StringUtils.isNotEmpty(col.getControlValue())&& !col.getControlValue().equals("CUSTOM")){//如果编辑时控件是”自定义“的，则不要加事件
					 String tag=getHtmlTag(col);//jquery中不支持$("#ddd.sss"),id中含有点的，只有$(input[id=ddd.sss])才可，所以要得到jsp页面中的标签
					 if(col.getControlValue().equals("MULTISELECT")){
						 if(col.getTableColumn()!=null){
							 script.append("jQuery('").append(tag).append("[id='+").append("id").append("+'_").append(col.getTableColumn().getName()).append("']).multiselect({checkAllText:'全选',uncheckAllText:'清除',noneSelectedText:'请选择',selectedList:4});");
						 }
					 }
					 String columName=getColModelName(col.getTableColumn().getName());//当数据表对应的字段带点时将点去掉，点后的第一个字母大写,如dd.cc-->ddCC
					 if(col.getTableColumn().getDataType()==DataType.DATE){
						 script.append("jQuery('").append(tag).append("[id='+").append("id").append("+'_").append(col.getTableColumn().getName()).append("')").append(".attr('readonly','readonly');");
						 script.append("jQuery('").append(tag).append("[id='+").append("id").append("+'_").append(col.getTableColumn().getName()).append("')").append(".datepicker({")
						 .append("'dateFormat':'yy-mm-dd',")
						 .append("changeMonth:true,")
						 .append("changeYear:true,")
						 .append("showButtonPanel:'true',")
						 .append("onSelect:function(dateText, inst){$dateOnSelect({rowid:").append("id").append(",currentInputId:").append("id+'_").append(col.getTableColumn().getName()).append("',dateText:dateText});},")
						.append("onChangeMonthYear:function(){$dateOnChangeMonthYear({rowid:").append("id").append(",currentInputId:").append("id+'_").append(col.getTableColumn().getName()).append("'});},")
						.append("onClose:function(){$dateOnClose({rowid:").append("id").append(",currentInputId:").append("id+'_").append(col.getTableColumn().getName()).append("'});}")
						 .append("});");
					 }else if(col.getTableColumn().getDataType()==DataType.TIME){
						 script.append("jQuery('").append(tag).append("[id='+").append("id").append("+'_").append(col.getTableColumn().getName()).append("')").append(".attr('readonly','readonly');");
						 script.append("jQuery('").append(tag).append("[id='+").append("id").append("+'_").append(col.getTableColumn().getName()).append("')").append(".datetimepicker({")
						 .append("'dateFormat':'yy-mm-dd',")
						 .append("changeMonth:true,")
						 .append("changeYear:true,")
						 .append(" showSecond: false,")
						 .append("showMillisec: false,")
						 .append("'timeFormat': 'hh:mm',")
						 .append("onSelect:function(dateText, inst){$dateOnSelect({rowid:").append("id").append(",currentInputId:").append("id+'_").append(col.getTableColumn().getName()).append("',dateText:dateText});},")
						.append("onChangeMonthYear:function(){$dateOnChangeMonthYear({rowid:").append("id").append(",currentInputId:").append("id+'_").append(col.getTableColumn().getName()).append("'});},")
						.append("onClose:function(){$dateOnClose({rowid:").append("id").append(",currentInputId:").append("id+'_").append(col.getTableColumn().getName()).append("'});}")
						 .append("});");
					 }else if(StringUtils.isNotEmpty(col.getEventType())){
						 String[] events=col.getEventType().split(",");
						 for(String event:events){
							 if("ONCLICK".equals(event)){
								 script.append("jQuery('").append(tag).append("[id='+").append("id").append("+'_").append(col.getTableColumn().getName()).append("')").append(".attr('readonly','readonly');");
								 script.append("jQuery('").append(tag).append("[id='+").append("id").append("+'_").append(col.getTableColumn().getName()).append("')").append(".click(function(){").append(columName).append("Click({rowid:").append("id").append(",currentInputId:").append("id+'_").append(col.getTableColumn().getName()).append("'});});");
							 }else if("ONCHANGE".equals(event)){
								 script.append("jQuery('").append(tag).append("[id='+").append("id").append("+'_").append(col.getTableColumn().getName()).append("')").append(".change(function(){").append(columName).append("Change({rowid:").append("id").append(",currentInputId:").append("id+'_").append(col.getTableColumn().getName()).append("'});});");
							 }else if("ONDBLCLICK".equals(event)){
								 script.append("jQuery('").append(tag).append("[id='+").append("id").append("+'_").append(col.getTableColumn().getName()).append("')").append(".dblclick(function(){").append(columName).append("Dblclick({rowid:").append("id").append(",currentInputId:").append("id+'_").append(col.getTableColumn().getName()).append("'});});");
							 }else if("BLUR".equals(event)){
								 script.append("jQuery('").append(tag).append("[id='+").append("id").append("+'_").append(col.getTableColumn().getName()).append("')").append(".blur(function(){").append(columName).append("Blur({rowid:").append("id").append(",currentInputId:").append("id+'_").append(col.getTableColumn().getName()).append("'});});");
							 }
						 }
					 }
				 }
			 }
		 }
		 script.append("}");
		 script.append("</script>");
		 return script.toString();
	 }
	 
	 private String editClick(String tableId){
		 StringBuilder script=new StringBuilder();
		 script.append("function editClick(id){")
		 .append("restoreOtherTable('").append(tableId).append("');")
		 .append("saveRowWhenAdd(lastsel,'").append(tableId).append("');")
		 .append("if(!hasEdit){")
		 .append("if(id && id!=lastsel){")
		 .append("jQuery(\"#").append(tableId).append("\")").append(".jqGrid('restoreRow',lastsel);")
		 .append("jQuery(\"#").append(tableId).append("\")").append(".jqGrid('editRow',id,true,editFun,function(){},'',{},function(){hasEdit=false;")
		 .append("$editRowSave(id,'").append(tableId).append("');")
		 .append("lastsel=0;")
		 .append("},function(){},")
		 .append("function(){")
		 .append("hasEdit=false;")
		 .append("$editRowRestore(id,'").append(tableId).append("');")
		 .append("lastsel=0;")
		 .append("if(id.indexOf(\"new_\")>=0){")
		 .append("jQuery(\"#").append(tableId).append("\")").append(".jqGrid('delRowData',id);")
		 .append("_add_row('").append(tableId).append("');")
		 .append("}")
		 .append("}")
		 .append(");")
		 .append("lastsel=id;")
		 .append("$editClickCallback(id,'").append(tableId).append("');")
		 .append("}")
		 .append("}")
		 .append("}");
		 return script.toString();
	 }
	 
	 private String getHtmlTag(ListColumn col){
		 if(col.getControlValue().equals("CHECKBOX")){
			 return "checkbox";
		 }else if(col.getControlValue().equals("MULTISELECT")||col.getControlValue().equals("SELECT")){
			 return "select";
		 }else if(col.getControlValue().equals("TEXTAREA")){
			 return "textarea";
		 }else {
			 return "input";
		 }
	 }
	 
	 private boolean getEditable(String editable,boolean viewEditable){
		 if(!"true".equals(editable)&&!"false".equals(editable)&&viewEditable)return true;
		 if("true".equals(editable))return true;
		 return false;
	 }
	 /**
	  * 获得colNames,colModel
	  * @param columns
	  * @param isCustomGrid 是否是【自定义列表标签】
	  * @return
	  * colNames:['Inv No','Date', 'Client', 'Amount','Tax','Total','Notes']
	  * colModel:[
   		{name:'id',index:'id', width:60, sorttype:"int"},
   		{name:'invdate',index:'invdate', width:90, sorttype:"date"},
   		{name:'name',index:'name', width:100},
   		{name:'amount',index:'amount', width:80, align:"right",sorttype:"float"},
   		{name:'tax',index:'tax', width:80, align:"right",sorttype:"float"},		
   		{name:'total',index:'total', width:80,align:"right",sorttype:"float"},		
   		{name:'note',index:'note', width:150, sortable:false}		
   	]
	  */
	 public String[] getColNames(List<ListColumn> columns,ListView listView,Object entity,String dataTableName,String editable){
		 String[] result=new String[2];
		 StringBuilder cols=new StringBuilder();//列名对象
		 StringBuilder colModel=new StringBuilder();//列模式对象
		 cols.append("[");
		 colModel.append("[");
		 boolean viewEditable=getEditable(editable,listView.getEditable()==null?false:listView.getEditable());
		 if(viewEditable){//formGrid标签中editable属性("是否可以操作"的值)不为true和false时，以列表设的为准；否则以editable属性设的为准
			 cols.append("'")
			 .append(getLocal("grid.act"))
			 .append("',");
			 colModel.append("{name:'act',index:'act',width:");
			 if(listView.getActWidth()==null){
				 colModel.append("90");
			 }else{
				 colModel.append(listView.getActWidth());
			 }
			 colModel.append(",sortable:false,align:'center',frozen:true},");
		 }
		 cols.append("'")
		 .append("id")
		 .append("',");
		 if(viewEditable){
			 colModel.append("{name:'id',index:'id',sortable:false,editable:true,hidden:true,frozen:true},");
		 }else{
			 colModel.append("{name:'id',index:'id',sortable:false,editable:false,hidden:true,frozen:true},");
		 }
		 for(ListColumn col: columns){
			 TableColumn column=col.getTableColumn();
			 boolean colEditable=getEditable(editable,col.getEditable()==null?false:col.getEditable());
			 String colName=column.getName();
			 cols.append("'")
			 .append(getInternation(StringUtils.isNotEmpty(col.getHeaderName())?col.getHeaderName():""))
			 .append("',");
			 colModel.append("{")
			 .append("name:'")
			 .append(colName)
			 .append("',")
			 .append("index:'")
			 .append(colName)
			 .append("',");
			 if(colEditable){
				 colModel.append("editable:true,");
				 if(col.getDefaultValue()!=null){
					 if(col.getDefaultValue()==DefaultValue.CURRENT_DATE){
						 colModel.append("unformat:unFormatCurrentDate,");
					 }else if(col.getDefaultValue()==DefaultValue.CURRENT_TIME){
						 colModel.append("unformat:unFormatCurrentTime,");
					 }else if(col.getDefaultValue()==DefaultValue.CURRENT_USER_NAME){
						 colModel.append("unformat:unFormatUserName,");
					 }else if(col.getDefaultValue()==DefaultValue.CURRENT_LOGIN_NAME){
						 colModel.append("unformat:unFormatLoginName,");
					 }
				 }
			 }
			 if(StringUtils.isNotEmpty(col.getHeadStyle())){
				colModel.append("width:");
				colModel.append(col.getHeadStyle()).append(",");
			}
			 StringBuilder editrules=new StringBuilder();
			 if(StringUtils.isNotEmpty(col.getEditRules())){
				 editrules.append(col.getEditRules());
			 }
			 if(editrules.toString().contains("required")){//当该字段必填时，再加相应的验证
				 if(column.getDataType()==DataType.NUMBER||column.getDataType()==DataType.AMOUNT
						 ||column.getDataType()==DataType.DOUBLE||column.getDataType()==DataType.FLOAT){
					 if(StringUtils.isNotEmpty(editrules.toString())){
						 editrules.append(",");
					 }
					 editrules.append("number:true");
				 }else if(column.getDataType()==DataType.INTEGER||column.getDataType()==DataType.LONG){
					 if(StringUtils.isNotEmpty(editrules.toString())){
						 editrules.append(",");
					 }
					 editrules.append("integer:true");
				 }else if(column.getDataType()==DataType.DATE){
					 if(StringUtils.isNotEmpty(editrules.toString())){
						 editrules.append(",");
					 }
					 editrules.append("date:true");
				 }else if(column.getDataType()==DataType.TIME){
					 if(StringUtils.isNotEmpty(editrules.toString())){
						 editrules.append(",");
					 }
					 editrules.append("time:true");
				 }
			 }
			 if(StringUtils.isNotEmpty(editrules.toString())){
				 colModel.append("editrules:{").append(editrules).append("},");
			 }
			 String valueSet=getValueSet(col,entity,dataTableName);
			 if(StringUtils.isNotEmpty(valueSet)){
				 if(column.getDataType()==DataType.BOOLEAN&&(col.getControlValue()!=null&& !col.getControlValue().equals("SELECT"))){//该字段是布尔型，且编辑时控件类型不是下拉框时编辑时均已复选框形式编辑
					 colModel.append("edittype:'").append("checkbox").append("',");
					 String op=getCheckboxOption(valueSet);
					 if(StringUtils.isNotEmpty(op)){
						 colModel.append("editoptions: {value:'").append(op).append("'},");
					 }
				 }else{
					 colModel.append("edittype:'").append("select").append("',");
					 colModel.append("formatter:'select',");
					 if(StringUtils.isNotEmpty(valueSet)){
						 if("MULTISELECT".equals(col.getControlValue())){
							 colModel.append("editoptions: {value:{").append(valueSet).append("},multiple:true},");
						 }else{
							 colModel.append("editoptions: {value:{").append(valueSet).append("}},");
						 }
					 }
				 }
			 }else if(col.getControlValue()!=null&&col.getControlValue().equals("CUSTOM")){
				 String colModelName=getColModelName(colName);
				 colModel.append("edittype:'").append("custom").append("',");
				 colModel.append("editoptions:{custom_element: "+colModelName+"Element, custom_value:"+colModelName+"Value},");
			 }else if(col.getControlValue()!=null&&col.getControlValue().equals("TEXTAREA")){
				 colModel.append("edittype:'").append("textarea").append("',");
			 }else{
				 colModel.append(formatSetting(col));
			 }
			 String tempValueset=col.getValueSet();
			 if(StringUtils.isNotEmpty(tempValueset)){
				 if(column.getDataType()==DataType.REFERENCE){//beanname:"",classname:""（classname为实体类名，不是bean的名称）
					 if(StringUtils.isNotEmpty(column.getObjectPath())){
						 colModel.append("classname:'").append(column.getObjectPath()).append("',");
					 }else{
						 if(tempValueset.contains(CLASSNAME)){
							 String[] vals=tempValueset.split(",");
							 for(String val:vals){
								 if(val.contains(CLASSNAME)){
									 String classname=val.split(":")[1];
									 colModel.append("classname:'").append(classname).append("',");
								 }
							 }
						 }
					 }
				 }else{
					 String enumname=null;
					 if(column.getDataType()==DataType.ENUM){
						enumname=column.getObjectPath();
					 }
					 if(StringUtils.isEmpty(enumname)){
						 if(tempValueset.contains(ENUMNAME)){
							 String[] vals=tempValueset.split(",");
							 for(String val:vals){
								 if(val.contains(ENUMNAME)){
									 enumname=val.split(":")[1];
								 }
							 }
						 }
					 }
					 colModel.append("classname:'").append(enumname).append("',");
				 }
			 }
			 if(StringUtils.isNotEmpty(valueSet)){
				 colModel.append("valueset:{").append(valueSet).append("},");
			 }
			colModel.append("mydatatype:\"").append(column.getDataType()).append("\",");
			if(column.getDataType()==DataType.BOOLEAN&&col.getControlValue()!=null&& !col.getControlValue().equals("SELECT")){//值设置为空，或该字段是布尔型，且编辑时控件类型不是下拉框时，格式化列表数据
				colModel.append("formatter:formatFun,");
			}
			 if(col.getVisible()!=null&&!col.getVisible()){
				 colModel.append("hidden:true,");
			 }
			 if(col.getSortable()!=null&&col.getSortable()){
				 colModel.append("sortable:true,");
				 colModel.append("sorttype:\"")
				 .append(getSortType(column.getDataType()))
				 .append("\"},");
			 }else{
				 colModel.append("sortable:false");
				 colModel.append("},");
			 }
		 }
		 cols=cols.replace(cols.length()-1,cols.length(), "");
		 colModel=colModel.replace(colModel.length()-1,colModel.length(), "");
		 cols.append("]");
		 colModel.append("]");
		 result[0]=cols.toString();
		 result[1]=colModel.toString();
		 return result;
	 }
	 /**
	  * 表单中的列表标签的“格式设置”处理
	  * @param col
	  * @return
	  */
	 private String formatSetting(ListColumn col){
		 String format=col.getFormat();
		 StringBuilder colModel=new StringBuilder();
		 if(StringUtils.isNotEmpty(format)){
			FormatSetting formatSetting= FormatSettingFactory.getFormatSetting(format);
			String fs=formatSetting.format(format);
			if(StringUtils.isNotEmpty(fs))colModel.append(fs).append(",");
		 }
		 return colModel.toString();
	 }
	 
	 public String getColModelName(String columnName){
		 if(columnName.contains(".")){//如果数据表字段是"ddd.fff",则columnName改为“dddFff”,将"."后的第一个字母大写
			 StringBuilder fnNameStart=new StringBuilder();
			 String[] vals=columnName.split("\\.");
			 int i=0;
			 for(String val:vals){
				 if(i==0){
					 fnNameStart.append(val);
				 }
				 i++;
				 if(i!=1){
					 String start=StringUtils.upperCase(val.substring(0, 1));
					 String other=val.substring(1,val.length());
					 fnNameStart.append(start).append(other); 
				 }
			 }
			 columnName=fnNameStart.toString();
		 }
		 return columnName;
	 }
	 /**
	  * 获得值设置
	  * @param col
	  * @return
	  */
	 public String getValueSet(ListColumn col,Object entity,String dataTableName ){
		 DataTableManager dataTableManager = (DataTableManager)ContextUtils.getBean("dataTableManager");
		 TableColumnManager tableColumnManager = (TableColumnManager)ContextUtils.getBean("tableColumnManager");
		 String valueSet=col.getValueSet();
		 TableColumn column = col.getTableColumn();
		 StringBuilder opitions=new StringBuilder();
		 if(column!=null &&column.getDataType()!=DataType.BOOLEAN && col.getControlValue()!=null && !col.getControlValue().equals("MULTISELECT")){
			 opitions.append("'':'")
			 .append("请选择")
			 .append("'").append(",");
		 }
		 if(StringUtils.isNotEmpty(valueSet)){
			   if(valueSet.contains(ENUMNAME)){
				   String[] vals=valueSet.split(",");
				   for(String val:vals){
					 if(val.contains(ENUMNAME)){
						 String enumname=val.split(":")[1];
						 String enumOptions = getOptionsByEnum(enumname);
						 if(StringUtils.isNotEmpty(enumOptions)){
							 opitions.append(enumOptions);
							 return opitions.toString();
						 }
					 }
				 }
			   }else if(column.getDataType()==DataType.REFERENCE){
		 		if(StringUtils.isNotEmpty(dataTableName)){//customGrid标签用到
		 			StringBuilder result=new StringBuilder();
		 			result.append("'':'")
	 				 .append("请选择")
	 				 .append("'").append(",");
		 			DataTable table=dataTableManager.getDataTableByTableName(dataTableName);
		 			List<TableColumn> columns=tableColumnManager.getTableColumnByDataTableId(table.getId());
		 			for(TableColumn mycolumn:columns){
		 				result.append("'").append(mycolumn.getId()).append("':")
		 				.append("'").append(getInternation(mycolumn.getAlias())).append("'").append(",");
		 			}
		 			if(result.charAt(result.length()-1)==','){
		 				result.delete(result.length()-1, result.length());
		 			}
		 			if(StringUtils.isNotEmpty(result.toString())){
						return result.toString();
					}
		 		}else{
	 				return getOptionsByBeanName(valueSet,column.getName(),entity);
		 		}
		 	}else{
		 		if(valueSet.contains(BEANNAME)){
		 			return getOptionsByBeanName(valueSet,column.getName(),entity);
		 		}else{
		 			if(valueSet.contains(":")){
		 				String[] vals=valueSet.split(",");
		 				int i=0;
		 				for(String val:vals){
		 					i++;
		 					String[] strArr=val.split(":");
		 					if(strArr.length>1){
		 						if( strArr[0].contains("'")||strArr[0].contains("\"")){
		 							opitions.append(strArr[0]).append(":");
		 						}else{
		 							opitions.append("'").append(strArr[0]).append("':");
		 						}
		 						if(strArr[1].contains("'")||strArr[1].contains("\"")){
		 							opitions.append(strArr[1]);
		 						}else{
		 							opitions.append("'").append(strArr[1]).append("'");
		 						}
		 						if(i<vals.length){
		 							opitions.append(",");
		 						}
		 					}
		 				}
		 				if(opitions.charAt(opitions.length()-1)==','){
							opitions.delete(opitions.length()-1, opitions.length());
						}
		 				return opitions.toString();
		 			}else{
		 				com.norteksoft.product.api.entity.OptionGroup group=ApiFactory.getSettingService().getOptionGroupByCode(valueSet);
		 				if(group!=null){
		 					int i=0;
		 					List<com.norteksoft.product.api.entity.Option> ops=ApiFactory.getSettingService().getOptionsByGroup(group.getId());
		 					for(com.norteksoft.product.api.entity.Option op:ops){
		 						i++;
		 						opitions.append("'").append(op.getValue())
		 						.append("':'")
		 						.append(op.getName())
		 						.append("'");
		 						if(i<ops.size()){
		 							opitions.append(",");
		 						}
		 					}
		 				}
		 				if(opitions.charAt(opitions.length()-1)==','){
							opitions.delete(opitions.length()-1, opitions.length());
						}
		 				return opitions.toString();
		 			}
		 		}
			 }
		 }
		 if(column!=null&&column.getDataType()==DataType.ENUM){//枚举类型时，获得枚举值选项
			   if(StringUtils.isNotEmpty(column.getObjectPath())){
				   String enumOptions = getOptionsByEnum(column.getObjectPath());
				   if(StringUtils.isNotEmpty(enumOptions)){
					   opitions.append(enumOptions);
					   return opitions.toString();
				   }
			   }
		   }
		 return null;
	 }
	 
	 private String getOptionsByEnum(String enumname){
		 StringBuilder opitions=new StringBuilder();
		 try {
				Object[] objs = Class.forName(enumname).getEnumConstants();
				int i=0;
				for(Object obj : objs){
					i++;
					opitions.append("'").append(obj.toString())
					.append("':'")
					 .append(textProvider.getText(BeanUtils.getProperty(obj, "code")))
					 .append("'");
					 if(i<objs.length){
						 opitions.append(",");
					 }
				}
				if(opitions.charAt(opitions.length()-1)==','){
					opitions.delete(opitions.length()-1, opitions.length());
				}
				return opitions.toString();
			} catch (Exception e) {
			}
			return opitions.toString();
	 }
	 
	 public String getOptionsByBeanName(String valueSet,String colName,Object entity){
		 if(valueSet.contains(BEANNAME)){
			 String[] vals=valueSet.split(",");
			 for(String val:vals){
				 if(val.contains(BEANNAME)){
					String beanname= val.split(":")[1];
					Map<String,String> map=null;
					ComboxValues bean=(ComboxValues)ContextUtils.getBean(beanname);
					map=bean.getValues(entity);
					String value=map.get(colName);
					if(StringUtils.isNotEmpty(value)){
						return value;
					}
				 }
			 }
			}
		return "";
	 }
	 /**
	  * 获得checkbox的editOptions值
	  * 是:否
	  * @param valueSet
	  * @return
	  */
	 public String getCheckboxOption(String valueSet){
		 String[] arr=valueSet.split(",");
		 StringBuilder sb=new StringBuilder();
		 int i=0;
		 for(String str:arr){
			 i++;
			 String[] vals=str.split(":");
			 if(vals.length>1){
				 String val=vals[1];
				 if(val.indexOf("'")>=0){
					 val=val.substring(val.indexOf("'")+1, val.lastIndexOf("'"));
				 }
				 sb.append(val);
			 }
			 if(i<arr.length)sb.append(":");
		 }
		 return sb.toString();
	 }
	 /**
	  * 获得集合数据的json格式
	  * @param queryResult
	  * @param columns
	  * @param isCollection 结果集是否是集合
	  * @return
	  * [
		{id:"1",invdate:"2007-10-01",name:"test",note:"note",amount:"200.00",tax:"10.00",total:"210.00"},
		{id:"2",invdate:"2007-10-02",name:"test2",note:"note2",amount:"300.00",tax:"20.00",total:"320.00"},
		{id:"3",invdate:"2007-09-01",name:"test3",note:"note3",amount:"400.00",tax:"30.00",total:"430.00"},
		{id:"4",invdate:"2007-10-04",name:"test",note:"note",amount:"200.00",tax:"10.00",total:"210.00"},
		{id:"5",invdate:"2007-10-05",name:"test2",note:"note2",amount:"300.00",tax:"20.00",total:"320.00"},
		{id:"6",invdate:"2007-09-06",name:"test3",note:"note3",amount:"400.00",tax:"30.00",total:"430.00"},
		{id:"7",invdate:"2007-10-04",name:"test",note:"note",amount:"200.00",tax:"10.00",total:"210.00"},
		{id:"8",invdate:"2007-10-03",name:"test2",note:"note2",amount:"300.00",tax:"20.00",total:"320.00"},
		{id:"9",invdate:"2007-09-01",name:"test3",note:"note3",amount:"400.00",tax:"30.00",total:"430.00"}
		]
	  */
	 @SuppressWarnings("unchecked")
	 public String getJsonData(Collection queryResult,List<ListColumn> columns){
		 StringBuilder jsonData=new StringBuilder();
		 jsonData.append("[");
		 if(queryResult==null||queryResult.size()<=0){
			 jsonData.append("{\"id\":\"new_0\"}"); 
		 }else{
			 for(Object obj:queryResult){
				 String str=obj.getClass().getName();
				 if(str.indexOf("[")==0){
					 jsonData.append(getScriptArray(obj,columns));
				 }else{
					 jsonData.append(getScriptObject(obj,columns));
				 }
				 jsonData.append(",");
			 }
		 }
		 if(jsonData.indexOf(",")>=0)jsonData=jsonData.replace(jsonData.length()-1,jsonData.length(), "");
		 jsonData.append("]");
		 return PageUtils.disposeSpecialCharacter(jsonData.toString());
	 }
	 /**
	  * 获得某条数据的json格式对象（集合为实体的集合时）
	  * @param entity
	  * @param columns
	  * @return
	  * {id:"1",invdate:"2007-10-01",name:"test",note:"note",amount:"200.00",tax:"10.00",total:"210.00"}
	  */
	 public String getScriptObject(Object entity,List<ListColumn> columns){
		 StringBuilder sb=new StringBuilder();
		 try {
			 sb.append("{");
			 if(entity!=null){
				Object id=BeanUtils.getProperty(entity, "id");
				sb.append("\"")
				  .append("id")
				  .append("\"")
				  .append(":")
				  .append("\"")
				  .append(id)
				  .append("\"")
				  .append(",");
			 }
			 for(ListColumn col: columns){
				 TableColumn tc=col.getTableColumn();
				 if(tc!=null&&tc.getDataType()!=DataType.COLLECTION){
					 String colName=tc.getName();
					 Object val=null;
					 if(entity!=null){
						 if(tc.getDataType()==DataType.BOOLEAN){
							 val=BeanUtils.getProperty(entity, colName);
						 }else{
							 if(!colName.contains("$")){
								 if(colName.contains(".")){//toPage.code.n...,实体引用中的某个字段或嵌入式的某个字段
									 String[] refnames=colName.split("\\.");
									 int n=0;
									 Object obj=null;
									 for(String ref:refnames){
										 if(n==0){
											 obj=entity.getClass().getMethod(PageUtils.getMethodName(ref)).invoke(entity);
										 }else{
											 if(obj!=null){
												 obj=obj.getClass().getMethod(PageUtils.getMethodName(ref)).invoke(obj);
											 }
										 }
										 if(obj==null){
											 val=null;
											 break;
										 }else{
											 if(n==refnames.length-1){
												 val=obj;
											 }
										 }
										 n++;
									 }
								 }else{
									 val= entity.getClass().getMethod(PageUtils.getMethodName(colName)).invoke(entity);
								 }
							 }
						 }
					 }
					 if(val!=null){
						 if(tc.getDataType()==DataType.DATE){
							 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
							 Date date = (Date)val;
							 val=simpleDateFormat.format(date);
						 }else if(tc.getDataType()==DataType.TIME){
							 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
							 Date date = (Date)val;
							 val=simpleDateFormat.format(date);
						 }else if(tc.getDataType()==DataType.AMOUNT||tc.getDataType()==DataType.DOUBLE||tc.getDataType()==DataType.FLOAT){
							 NumberFormat nf=new DecimalFormat("#.##");
							 val=nf.format(val);
						 }else if(tc.getDataType()==DataType.REFERENCE){
							 val=BeanUtils.getProperty(entity, colName+".id");
						 }
					 }else{
						 if(tc.getDataType()==DataType.REFERENCE){
							 if("tableColumn".equals(colName)){
								 val="_temporary";
							 }
						 }else{
							 val="&nbsp;";
						 }
					 }
					 sb.append("\"");
					 sb.append(colName);
					 sb.append("\"")
					 .append(":")
					 .append("\"");
					 if(val!=null){
						 sb.append(val.toString().replaceAll("\"", "_@_#"));//把英文的双引号替换成_@_# 做次替换是为了在页面上显示双引号,该替换会在PageUtils中把符号_@_#再替换为英文的双引号
					 }else{
						 sb.append(val);
					 }
					 sb.append("\"")
					 .append(",");
				 }
			 }
			 sb=sb.replace(sb.length()-1,sb.length(), "");
			 sb.append("}");
			} catch (Exception e) {
				throw new RuntimeException(e);
			} 
			return sb.toString();
		
	 }
	 
	 /**
	  * 获得排序类型
	  * @param dataType
	  * @return
	  */
	 private String getSortType(DataType dataType){
		 if(dataType==DataType.DATE){
			 return "date";
		 }else if(dataType==DataType.TIME){
			 return "datetime";
		 }else if(dataType==DataType.AMOUNT||dataType==DataType.DOUBLE||dataType==DataType.FLOAT){
			 return "float";
		 }else if(dataType==DataType.INTEGER||dataType==DataType.NUMBER){
			 return "integer";
		 }else{
			 return "string";
		 }
	 }
	 /**
	  * 获得gridComplete数据
	  * @param tableId
	  * @param columns
	  * @return
	  */
	 private String getGridCompleteScript(String tableId,List<ListColumn> columns,boolean isSubSystem,String webCtx){
		 StringBuilder sb=new StringBuilder();
		 sb.append("gridComplete: function(){")
		 .append("newId='0';lastsel='0';hasEdit=false;")
		 .append("var ids = jQuery('#").append(tableId).append("').jqGrid('getDataIDs');")
		 .append("for(var i=0;i < ids.length;i++){")
		 .append("var cl = ids[i];")
		 .append("ae = \"<a href='#' class='small-button-bg' onclick=\\\"myAddRow('1','\"+cl+\"','").append(tableId).append("');\\\"").append("><span class='ui-icon ui-icon-plusthick'></span></a>\"; ")
		 .append("if(deleteUrl!=''){")
		 .append("de = \"<a href='#' class='small-button-bg' onclick=\\\"deleteFormTableData('").append(tableId).append("','\"+cl+\"','\"+deleteUrl+\"','").append(isSubSystem).append("','").append(webCtx).append("');\\\"").append("><span class='ui-icon  ui-icon-minusthick'></span></a>\"; ")
		 .append("}else{")
		 .append("de = \"<a href='#' class='small-button-bg' onclick=\\\"$deleteFormTableData('").append(tableId).append("','\"+cl+\"'").append(");\\\"").append("><span class='ui-icon  ui-icon-minusthick'></span></a>\"; ")
		 .append("}")
		 .append("edit = \"<a href='#' class='small-button-bg' onclick=\\\"editClick('\"+cl+\"');\\\"").append("><span class='ui-icon  ui-icon-pencil'></span></a>\"; ");
		 sb.append("jQuery('#").append(tableId).append("').jqGrid('setRowData',ids[i],{act:ae+' '+de+' '+edit+_getCustomeButtons('").append(tableId).append("',cl)});");
		 sb.append("}");
		 sb.append("$gridComplete();");
		 sb.append("}");
		 return sb.toString();
	 }
	 /**
	  * 获得onSelectRow数据
	  * @param tableId
	  * @return
	  */
	 private String getOnSelectRowScript(String tableId){
		 StringBuilder sb=new StringBuilder();
		 sb.append("onSelectRow: function(id,status){")
		 .append("if(hasEdit){if(id && id!=lastsel){restoreOtherTable('").append(tableId).append("');saveRowWhenAdd(lastsel,'").append(tableId).append("');lastsel=0;}}")
		 .append("}");
		 return sb.toString();
	 }
	 /**
	  * 获得“合计”对象
	  * {'useType':'0','total':'100','act':'合计'}
	  * @return
	  */
	 @SuppressWarnings("unchecked")
	 public String getFooterDatas(Collection queryResult,List<ListColumn> columns,Boolean isOperate){
		 StringBuilder jsonData=new StringBuilder();
		 try {
			 int i=0;
			 String totalStr=null;
			 int columnIndex=0;
			 for(ListColumn col: columns){
				 if(col.getTotal()){//如果该列需要合计
					 if(StringUtils.isEmpty(jsonData.toString()))jsonData.append("{");
					 if(i==0){
						 totalStr="fail"; 
					 }
					 if(i>0&&StringUtils.isEmpty(totalStr)){
						 totalStr="\""+columns.get(i-1).getTableColumn().getName()+"\":\""+getLocal("grid.total")+"\"";
					 }
					 TableColumn tc=col.getTableColumn();
					 String colName=tc.getName();
					 jsonData.append("\"");
					 jsonData.append(colName);
					 jsonData.append("\":");
					 Long totalInt=0l;//整数时的“和”
					 Double totalFloat=0d;//小数时的"和"
					 if(tc.getDataType()==DataType.AMOUNT||tc.getDataType()==DataType.DOUBLE||tc.getDataType()==DataType.FLOAT||tc.getDataType()==DataType.INTEGER||tc.getDataType()==DataType.NUMBER||tc.getDataType()==DataType.LONG){
						 if(queryResult!=null){
							 for(Object entity:queryResult){
								 Object val=null;
								 if(entity!=null){
									String str=entity.getClass().getName();
									if(str.indexOf("[")==0){
										val=((Object[])entity)[columnIndex];
									}else{
										val=BeanUtils.getProperty(entity, colName);
									}
								 }
								 if(val!=null){
									 if(tc.getDataType()==DataType.AMOUNT||tc.getDataType()==DataType.DOUBLE){
										 totalFloat=totalFloat+Double.parseDouble(val.toString());
									 }else if(tc.getDataType()==DataType.FLOAT){
										 totalFloat=totalFloat+Float.parseFloat(val.toString());
									 }else if(tc.getDataType()==DataType.INTEGER||tc.getDataType()==DataType.NUMBER||tc.getDataType()==DataType.LONG){
										 totalInt=totalInt+Integer.parseInt(val.toString());
									 }
								 }
							 }
						 }
					 }
					 if(tc.getDataType()==DataType.AMOUNT||tc.getDataType()==DataType.DOUBLE||tc.getDataType()==DataType.FLOAT){
						 jsonData.append("\"").append(totalFloat).append("\"");
					 }else{
						 jsonData.append("\"").append(totalInt).append("\"");
					 }
					 jsonData.append(",");
				 }
				 i++;
				 columnIndex++;
			 }
			 if(StringUtils.isNotEmpty(jsonData.toString())){
				 if(isOperate!=null&&isOperate&&"fail".equals(totalStr)){//有操作时，合计显示在操作列
					 totalStr="\"act\":\""+getLocal("grid.total")+"\"";
				 }
				 if(!"fail".equals(totalStr)){
					 jsonData.append(totalStr);
				 }else{
					 jsonData=jsonData.replace(jsonData.length()-1,jsonData.length(), "");
				 }
				 jsonData.append("}");
			 }
		 }catch (Exception e) {
		 }
		 return jsonData.toString();
	 }
	 /**
	  * 获得某条数据的json格式数据（集合为数组的集合时）
	  * @param obj
	  * @param columns
	  * @return
	  */
	 public String getScriptArray(Object obj,List<ListColumn> columns){
			StringBuilder sb=new StringBuilder();
			 try {
				 sb.append("{");
				 int i=0;
				 for(ListColumn col: columns){
					 TableColumn tc=col.getTableColumn();
					 if(tc!=null&&tc.getDataType()!=DataType.COLLECTION){
						 String colName=tc.getName();
						 Object val=((Object[])obj)[i];
						 if(val!=null&&val!=""){
							 if(tc.getDataType()==DataType.DATE){
								 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
								 Date date = simpleDateFormat.parse(val.toString());
								 val=simpleDateFormat.format(date);
							 }else if(tc.getDataType()==DataType.TIME){
								 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
								 Date date = simpleDateFormat.parse(val.toString());
								 val=simpleDateFormat.format(date);
							 }else if(tc.getDataType()==DataType.AMOUNT||tc.getDataType()==DataType.DOUBLE||tc.getDataType()==DataType.FLOAT){
								 NumberFormat nf=new DecimalFormat("#.##");
								 val=nf.format(val);
							 }
						 }else{
							 val="&nbsp;";
						 }
						 sb.append("\"")
						 .append(colName)
						 .append("\"")
						 .append(":")
						 .append("\"");
						 sb.append(val.toString().replaceAll("\"", "_@_#"));//把英文的双引号替换成_@_# 做次替换是为了在页面上显示双引号,该替换会在PageUtils中把符号_@_#再替换为英文的双引号
						 sb.append("\"")
						 .append(",");
						 i++;
					 }
				 }
				 sb=sb.replace(sb.length()-1,sb.length(), "");
				 sb.append("}");
				} catch (Exception e) {
					throw new RuntimeException(e);
				} 
				return sb.toString();
		}
	 
	 public String getLocal(String code){
		return textProvider.getText(code);
	 }
	 
	 public String getInternation(String code){
		 return ApiFactory.getSettingService().getInternationOptionValue(code);
	 }
	 /**
	  * 判断是否是默认字段
	  * @param columnName
	  * @return true:表示是默认字段,false:表示不是默认字段
	  */
	 public static boolean isDefaultField(String columnName){
		return "id".equalsIgnoreCase(columnName)
		||"create_date".equalsIgnoreCase(columnName)||
		"creator_department".equalsIgnoreCase(columnName)||
		"workflow_definition_version".equalsIgnoreCase(columnName)||
		"workflow_definition_code".equalsIgnoreCase(columnName)||
		"workflow_definition_id".equalsIgnoreCase(columnName)||
		"workflow_definition_name".equalsIgnoreCase(columnName)||
		"current_activity_name".equalsIgnoreCase(columnName)||
		"process_state".equalsIgnoreCase(columnName)||
		"creator_name".equalsIgnoreCase(columnName)||
		"creator".equalsIgnoreCase(columnName)||
		"company_id".equalsIgnoreCase(columnName)||
		"instance_id".equalsIgnoreCase(columnName)||
		"first_task_id".equalsIgnoreCase(columnName)||
		"form_id".equalsIgnoreCase(columnName);
	}
	 
	 /**
	  * 打印html
	  * @return
	  */
	 public String getPrintFormHtml(FormView form,String html,Long dataId,boolean fieldRight){
 		 setFormHtml(html);
		 StringBuilder formHtml = new StringBuilder();
		 formHtml.append(html);
		 FormFields formFields=source.getFormFields();
		 Iterator<FormField> it = formFields.iterator();
		 FormField formField = null;
		 while(it.hasNext()){
			 formField = it.next(); 
			 Element element = formField.getFormControl().getElement();
			 StringBuilder divHtml = new StringBuilder();
			 System.out.println(element.toString());
			if("TEXT".equals(element.getAttributeValue("pluginType"))&& !"hidden".equals(element.getAttributeValue("type")) ){
				if("true".equals(element.getAttributeValue("signaturevisible"))){//签章处理
					outputDocument = setSignatureImage(outputDocument,formField);
				 }else{
					 if("radio".equals(element.getAttributeValue("type"))||"checkbox".equals(element.getAttributeValue("type"))){//单选框和复选框
						Collection<net.htmlparser.jericho.FormControl> list =  formField.getFormControls();
						String url = "";
						for(net.htmlparser.jericho.FormControl control:list){
							StringBuilder a = new StringBuilder();
							 Element e = control.getElement();
							 if("checked".equals(e.getAttributeValue("checked"))){
								 if("radio".equals(element.getAttributeValue("type"))){
									 url = PropUtils.getProp("host.resources")+"/images/single-selected.png";
								 }else{
									 url = PropUtils.getProp("host.resources")+"/images/double-selected.png";
								 }
								 a.append("<span>").append("<img src='"+url+"' ></img>").append("</span>");
								 outputDocument.replace(e.getEnd(), e.getEnd(), a.toString() );
							 }else{
								 if("radio".equals(element.getAttributeValue("type"))){
									 url = PropUtils.getProp("host.resources")+"/images/single-unselected.png";
								 }else{
									 url = PropUtils.getProp("host.resources")+"/images/double-unselected.png";
								 }
								 a.append("<span>").append("<img src='"+url+"' ></img>").append("</span>");
								 outputDocument.replace(e.getEnd(), e.getEnd(), a.toString() );
							 }
						}
					 }else{
						 if(formField.getValues().size()>0){
							 divHtml.append("<span>").append(formField.getValues().get(0).toString()).append("</span>");
							 outputDocument.replace(element.getEnd(), element.getEnd(), divHtml.toString() );
						 }
					 }
				 }
			 }else if("TIME".equals(element.getAttributeValue("pluginType"))){//日期类型
				 if(formField.getValues().size()>0){
					 divHtml.append("<span>").append(formField.getValues().get(0).toString()).append("</span>");
				 }
				 outputDocument.replace(element.getEnd(), element.getEnd(), divHtml.toString() );
			 }else if("PULLDOWNMENU".equals(element.getAttributeValue("pluginType"))){//下拉选
				 String value = "";
				 if(formField.getValues().size()>0){
					 List<Element> subElements = element.getAllElements();
					 //解析子元素，取得被选项的值
					 for (Element e : subElements) {
						if("selected".equals((e.getAttributeValue("selected")))&&StringUtils.isNotEmpty(e.getAttributeValue("value"))){
								value = e.getContent().toString();
						}
					}
					 if(value!=null){
						 divHtml.append("<span>").append(value.toString()).append("</span>");
					 }
				 }
				 outputDocument.replace(element.getEnd(), element.getEnd(), divHtml.toString() );
			 }else if("textarea".equals(element.getAttributeValue("pluginType"))){//文本域
				 if(formField.getValues().size()>0){
						 divHtml.append("<div class=\"textArea-print\">").append(formField.getValues().get(0).toString()).append("</div>");
				 }
				 outputDocument.replace(element.getEnd(), element.getEnd(), divHtml.toString() );
			 }else if("CALCULATE_COMPONENT".equals(element.getAttributeValue("pluginType"))){//计算控件
				 if(formField.getValues().size()>0){
					 divHtml.append("<span>").append(formField.getValues().get(0).toString()).append("</span>");
					 outputDocument.replace(element.getEnd(), element.getEnd(), divHtml.toString() );
				 }
			 }
		 }
		 //（自定义的标签会有乱码）
		 outputDocument.replace(formFields);
		 StringBuilder jsHtml=new StringBuilder("");
		 jsHtml.append("<script type='text/javascript'>initPrintLabelControl();").append("</script>");
		 return outputDocument.toString()+jsHtml; 
	 }
	 
	//表单显示签章设置
	private OutputDocument setSignatureImage(OutputDocument outputDocument,
			FormField formField) {
		String signatureFields="";
		 Element element = formField.getFormControl().getElement();
		 StringBuilder imgs = new StringBuilder();
		 //获得当前文本框的值，并根据该值获得签章路径，该值一般为用户真名
		 List<Long> signIds=new ArrayList<Long>();
		 List<String> values = formField.getValues();
		 String url = SystemUrls.getSystemUrl("mms");
		 if(values.size()>0){
			 if(StringUtils.isNotEmpty(values.get(0))){
				 String names = values.get(0);
				 String[] nameArr = names.split(",");//多个人名之间以逗号隔开
				 for(String name:nameArr){
					 Long  signId = ApiFactory.getSettingService().getSignIdByUserName(name);
					 if(signId!=null){
						 signIds.add(signId);
						 imgs.append("<img src='"+url+"/form/form-view-showPic.htm?signId="+signId+"' style='width:2cm;height:1cm;'></img>").append(",");
					 }else{
						 imgs.append(name).append(",");
					 }
				 }
			 }else{
				 imgs.append("<img src='"+PropUtils.getProp("host.resources")+"/images/noSign.gif;' style='width:2cm;height:1cm;'></img>&nbsp;");
			 }
		 }
		 if(signIds.size()>0){//如果签章路径存在
			 if(StringUtils.isEmpty(signatureFields)){
				 signatureFields=element.getAttributeValue("name");
			 }else{
				 signatureFields=signatureFields+","+element.getAttributeValue("name");
			 }
		 }
		 if(StringUtils.isNotEmpty(imgs.toString())){
			 imgs.replace(imgs.length()-1, imgs.length(), "");
			 outputDocument.replace(element.getEnd(), element.getEnd(), imgs.toString() );
		 }
		 return outputDocument;
	}
}
