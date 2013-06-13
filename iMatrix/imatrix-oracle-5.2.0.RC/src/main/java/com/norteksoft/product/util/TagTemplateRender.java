package com.norteksoft.product.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import com.norteksoft.tags.grid.GridTag;


import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;

public class TagTemplateRender{
	private static final String TEMPLATE_DIR="template/tags/";
	private static Configuration config = null;

	public static TagTemplateRender getInstance() {
		return new TagTemplateRender();
	}

	public TagTemplateRender() {
		if (config == null) {
			config = new Configuration();
			config.setTemplateLoader(new StringTemplateLoader());

			try {
				config.setSetting("datetime_format", "yyyy-MM-dd HH:mm:ss");
				config.setLocale(Locale.CHINA);

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
	}
	
	@SuppressWarnings("unchecked")
	public String render(Map dataModel, String fileName) throws Exception {
		String ret=render(dataModel, fileName,true);
		return ret;
	}

	/**
	 * 
	 * @param dataModel
	 * @param fileName
	 * @param ifTag  是否是标签，如500,403,404,casfailed都不是标签
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String render(Map dataModel, String fileName,boolean ifTag) throws Exception {
		String ret = null;
		BufferedReader reader=null;
		StringWriter stringWriter=null;
		BufferedWriter writer=null;
		try { 
			String name=fileName;
			if(ifTag){
				name=TagTemplateRender.TEMPLATE_DIR+fileName;
			}
			reader=new BufferedReader(new InputStreamReader(GridTag.class.getClassLoader().getResourceAsStream(name),"UTF-8"));
			Template template = new Template(null, reader, config, "UTF-8");
			SimpleHash root = new SimpleHash();
			root.putAll(dataModel);
			stringWriter= new StringWriter();
			writer = new BufferedWriter(stringWriter);
			template.process(root, writer);
			writer.flush();
			ret = stringWriter.toString();

		} catch (Exception ex) {
			ex.printStackTrace();
			ret = null;
		}finally{
			reader.close();
			stringWriter.close();
			writer.close();
		}
		return ret;
	}
	
}
