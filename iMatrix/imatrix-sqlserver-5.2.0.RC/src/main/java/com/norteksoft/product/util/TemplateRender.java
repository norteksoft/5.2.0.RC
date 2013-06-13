package com.norteksoft.product.util;

 public interface TemplateRender {  
	     /** 
	      * 装载模板，用提供的数据渲染模板，返回输出字符串 
	      *  
	      * @param dataModel 数据模型 
	      * @param template 模板 
	      */  
	     public String render(Object dataModel, String template)throws Exception;  
	 }