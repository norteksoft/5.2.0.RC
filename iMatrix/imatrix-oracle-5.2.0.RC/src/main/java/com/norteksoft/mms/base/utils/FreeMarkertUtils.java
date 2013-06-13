package com.norteksoft.mms.base.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.FormControl;
import net.htmlparser.jericho.FormField;
import net.htmlparser.jericho.FormFields;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 根据给定的对象及HTML片段给HTML片段赋值
 * HTML片段中所有需要填充的域的name属性必须和对象中的属性名对应；
 * 动态select选项的需要提供 @see SelectOptions 的对应实现类
 * 
 * @author xj
 */
public class FreeMarkertUtils {
	private static String DATA_TYPE_PROP_NAME = "datatype";
	private static String DEFAULT_ENTITY_NAME = "obj_value_prefix";
	private static Configuration config = null;
	
	enum DataType{
		AMOUNT("金额"),
		TEXT("文本"),
		NUMBER("数字"),
		DATE("日期"),
		TIME("时间");
		public String code;
		DataType(String code){
			this.code=code;
		}
		public Short getIndex(){
			return (short)(this.ordinal()+1);
		}
		public String getCode(){
			return this.code;
		}
	}
	
	private FreeMarkertUtils(){}
	
	private static void init() throws TemplateException{
		if (config == null) {
			config = new Configuration();
			config.setTemplateLoader(new StringTemplateLoader());
			config.setClassicCompatible(true);
			config.setLocale(Locale.CHINA);
		}
	}
	
	/**
	 * 根据Map
	 * @param dataModel
	 * @param html
	 * @param addPrefix 是否添加前缀，为实体时设置为true
	 * @return
	 */
	public static String render(Map<?, ?> dataModel, String html, boolean addPrefix) {
		String ret = null;
		BufferedReader reader=null;
		StringWriter stringWriter=null;
		BufferedWriter writer=null;
		try {
			init();
			html = templateProcess(html, addPrefix);
			reader= new BufferedReader(new StringReader(html));
			Template template = new Template(null, reader, config, "UTF-8");
			SimpleHash root = new SimpleHash();
			root.putAll(dataModel);
			stringWriter= new StringWriter();
			writer = new BufferedWriter(stringWriter);
			template.process(root, writer);
			writer.flush();
			ret = stringWriter.toString();
		} catch (Exception ex) {
			ret = null;
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage());
		}finally{
			try {
				if(reader != null) reader.close();
				if(writer != null) writer.close();
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		return ret;
	}
	
	/**
	 * 根据对象为HTML片段赋值(实体)
	 * @param valueObject
	 * @param html
	 * @return
	 */
	public static String render(Object valueObject, String html){
		String resultHtml = html;
		if(valueObject == null){
			resultHtml = selectOptionsProcess(html);
		}else{
			Map<String, Object> test = new HashMap<String, Object>();
			test.put(DEFAULT_ENTITY_NAME, valueObject);
			resultHtml = render(test, html, true);
		}
		return resultHtml;
	}
	
	/*
	 * 将HTML解析为FreemMrker可以出来的模板
	 * @param html 
	 * @return
	 * @throws IOException
	 */
	private static String templateProcess(String html, boolean addPrefix) throws IOException{
		html = selectOptionsProcess(html);
		Source source = getSourceByHtml(html);
		OutputDocument outputDocument = new OutputDocument(source);
		//处理动态select的选项
		FormFields formFields=source.getFormFields();
		for(FormField field : formFields){
			//将表单的域替换为free mark可替换的模板
			formFieldProcess(field, outputDocument, addPrefix);
		}
		html = outputDocument.toString().replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		return html;
	}
	
	/*
	 * 根据Html获取jericho的Source
	 * @param html
	 * @return
	 * @throws IOException
	 */
	private static Source getSourceByHtml(String html) throws IOException{
		BufferedReader reader = null;
		Source source = null;
		try {
			reader = new BufferedReader(new StringReader(html));
			source = new Source(reader);
		} finally{
			reader.close();
		}
		return source;
	}
	
	/*
	 * 处理动态select的选项
	 * @param html
	 * @return
	 */
	private static String selectOptionsProcess(String html){
		Source source;
		try {
			source = getSourceByHtml(html);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		OutputDocument outputDocument = new OutputDocument(source);
		//selectOptionsProcess(source, outputDocument);
		return outputDocument.toString();
	}
	
	/*
	 * 将表单的域替换为free mark可替换的模板
	 * @param field
	 * @param outputDocument
	 */
	private static void formFieldProcess(FormField field, OutputDocument outputDocument, boolean addPrefix){
		Collection<FormControl> controls = field.getFormControls();
		if(controls.size() == 1){
			//类型不为radio、checkbox的input和select、textarea
			singleTagProcess(field.getFormControl(), outputDocument, addPrefix);
		}else{
			//类型为radio、checkbox的input
			sameNameTagsProcess(controls, outputDocument, addPrefix);
		}
	}
	
	/*
	 * 类型不为radio、checkbox的input和select、textarea
	 * @param field
	 * @param outputDocument
	 * @return
	 */
	private static OutputDocument singleTagProcess(FormControl control, 
			OutputDocument outputDocument, boolean addPrefix){
		String valueExpr = "";
		List<Element> subElements = control.getAllElements();
		if(addPrefix) valueExpr = DEFAULT_ENTITY_NAME+"." + subElements.get(0).getAttributeValue("name");
		else valueExpr = subElements.get(0).getAttributeValue("name");
		//简单input和textarea
		if(subElements.size() == 1){
			//日期类型
			if(DataType.DATE.toString().equals(subElements.get(0).getAttributeValue(DATA_TYPE_PROP_NAME))){
				control.setValue(new StringBuilder("<#if ").append(valueExpr).append("?exists>${")
						.append(valueExpr).append("?date}</#if>").toString());
			}else{
				control.setValue("${"+valueExpr+"}");
			}
			outputDocument.replace(control);
		}else{//select
			boolean isFirst = true;
			String newElementString = "";
			for(Element sub : subElements){
				if(isFirst){isFirst = false; continue;}
				newElementString = new StringBuilder(" <#if ").append(valueExpr).append("==\"")
						.append(sub.getAttributeValue("value")).append("\">selected='selected'</#if>>").toString();
				outputDocument.replace(sub.getBegin(), sub.getEnd(), sub.toString().replaceFirst(">", newElementString));
			}
		}
		return outputDocument;
	}
	
	/*
	 * 类型为radio、checkbox的input
	 * @param controls
	 * @param outputDocument
	 * @return
	 */
	private static OutputDocument sameNameTagsProcess(Collection<FormControl> controls, 
			OutputDocument outputDocument, boolean addPrefix){
		Element element = null;
		String newElementString = "";
		String valueExpr = "";
		for(FormControl control : controls){
			element = control.getElement();
			if(addPrefix) valueExpr = DEFAULT_ENTITY_NAME+"." + element.getAttributeValue("name");
			else element.getAttributeValue("name");
			if("radio".equals(element.getAttributeValue("type"))){
				//radio
				newElementString = new StringBuilder(" <#if ").append(valueExpr).append("==\"")
					.append(element.getAttributeValue("value")).append("\">checked='checked'</#if>>").toString();
			}else if("checkbox".equals(element.getAttributeValue("type"))){
				//check-box
				newElementString = new StringBuilder(" <#if (").append(valueExpr)
						.append("?exists && ").append(valueExpr).append("?contains(\"")
						.append(element.getAttributeValue("value")).append("\"))>checked='checked'</#if>>").toString();
			}
			outputDocument.replace(element.getBegin(), element.getEnd(), 
					element.toString().replaceFirst(">", newElementString));
		}
		System.out.println(outputDocument.toString());
		return outputDocument;
	}
}
