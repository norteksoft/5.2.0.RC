package com.norteksoft.acs.base.utils.log;


import java.util.Iterator;

import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
@SuppressWarnings("unchecked")
public class JavaXML {
	
	private static final String ROOT = "root";
	
	private static JavaXML xml;
	
	private JavaXML(){
		init();
	}
	
	private void init(){
		
	}
	
	public JavaXML getInstance(){
		if(xml==null){
			xml = new JavaXML();
		}
		return xml;
	} 
	public static String getXML(Map paraMap){  
		if(paraMap!=null&&!paraMap.isEmpty()){
			Document document = DocumentHelper.createDocument();  
		     //生成一个接点  
	        Element root = document.addElement(ROOT);  
	        //生成root的一子接点  
	        for(Iterator it = paraMap.entrySet().iterator(); it.hasNext();) {
				Map.Entry e = (Map.Entry) it.next();
				Element element = root.addElement(e.getKey().toString());  
				element.addText(e.getValue().toString()); 
				
			}
		   return document.asXML();  
		}
		return null;
		      
   }  
	
	
/**
   * 主方法用于测试
   * @param args
   */
public static void main(String[] args) {
  
//    JavaXML javaXML = new JavaXML();
//    Map map = new LinkedHashMap();
//    map.put("姓名", "张三");
//    map.put("年龄", "22");
//    map.put("性别", "男");
//    String xml = javaXML.getXML(map);
   // System.out.println(xml.trim());
}
}  

