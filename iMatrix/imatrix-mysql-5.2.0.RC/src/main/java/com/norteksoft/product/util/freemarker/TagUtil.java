package com.norteksoft.product.util.freemarker;

import java.io.OutputStream;
import java.util.Map;


public class TagUtil {
	/**
	 * 
	 * @param dataModel 模板中需要用到的变量
	 * @param fileName 模板的文件名，相对于"template/tags/"目录的相对路径，如"gridTag.ftl","menuTag.ftl"等
	 * @return 根据模板文件生成的文本
	 * @throws Exception
	 */
	private static final String TAG_DIR="tags/";
	public static String getContent(Map dataModel, String fileName)
			throws Exception {
		String ret =new TemplateRender().render(dataModel, TAG_DIR+fileName); 
		return ret;
	}
	
	public static void generateFile(Map dataModel,String dirPath,String fileName,String templateName){
		new TemplateRender().generateFile(dataModel,dirPath,fileName,templateName);
	}
	
}
