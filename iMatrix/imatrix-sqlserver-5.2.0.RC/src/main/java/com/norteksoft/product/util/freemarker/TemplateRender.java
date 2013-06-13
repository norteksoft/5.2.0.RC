package com.norteksoft.product.util.freemarker;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.norteksoft.product.util.ZipUtils;


import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;

public class TemplateRender{
	private static final String TEMPLATE_DIR="template/";
	public static final String GENERATE_DIR="generateDir/";
	private static Configuration config = null;

	public static TemplateRender getInstance() {
		return new TemplateRender();
	}

	public TemplateRender() {
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
		String ret = null;
		BufferedReader reader=null;
		StringWriter stringWriter=null;
		BufferedWriter writer=null;
		try { 
			reader=new BufferedReader(new InputStreamReader(TemplateRender.class.getClassLoader().getResourceAsStream(TemplateRender.TEMPLATE_DIR+fileName),"UTF-8"));
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
	@SuppressWarnings("unchecked")
	public void generateFile(Map dataModel,String dirPath,String fileName,String templateName){
		BufferedReader reader=null;
		try {
			reader=new BufferedReader(new InputStreamReader(TemplateRender.class.getClassLoader().getResourceAsStream(TemplateRender.TEMPLATE_DIR+"generate/"+templateName),"UTF-8"));
			Template template = new Template(null, reader, config, "UTF-8");
			File afile = new File(GENERATE_DIR+dirPath);
			if(!afile.exists()){
				afile.mkdirs();
			}
			File file = new File(GENERATE_DIR+dirPath+"/"+fileName);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			template.process(dataModel, writer);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
