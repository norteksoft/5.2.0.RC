package com.norteksoft.mms.base.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ibm.icu.text.SimpleDateFormat;
import com.norteksoft.acs.base.web.struts2.Struts2Utils;
import com.norteksoft.mms.base.utils.view.ExportData;

/**
 *  已经被 com.norteksoft.product.util.ExcelExporter 替代，请更换
 */
@Deprecated
public class ExcelExporter {
	//web上导出
	public static void export(ExportData exportData,String excelName,ExcelExportEnum excelEdition) throws Exception {
		if(excelEdition==ExcelExportEnum.EXCEL2007){
			XSSFWorkbook wb = (XSSFWorkbook) createReport(exportData,excelName,ExcelExportEnum.EXCEL2007);
			String filedName = setExcelName(excelName,ExcelExportEnum.EXCEL2007,"web");
			produceExcelForWeb(wb,filedName);
		}else{
			export(exportData,excelName); 
		}
	}
	
	public static void export(ExportData exportData,String excelName) throws Exception {
		Workbook wb = createReport(exportData,excelName,ExcelExportEnum.EXCEL2003);
		String filedName = setExcelName(excelName,ExcelExportEnum.EXCEL2003,"web");
		produceExcelForWeb(wb,filedName);
	}
	
	//向服务器导出
	public static void exportToServer(ExportData exportData,String excelName,ExcelExportEnum excelEdition) throws Exception {
		if(excelEdition==ExcelExportEnum.EXCEL2007){
			XSSFWorkbook wb = (XSSFWorkbook) createReport(exportData,excelName,ExcelExportEnum.EXCEL2007);
			String filedName = setExcelName(excelName,ExcelExportEnum.EXCEL2007,"servers");
			produceExcelToServers(wb,filedName);
		}else{
			exportToServer(exportData,excelName); 
		}
	}
	
	public static void exportToServer(ExportData exportData,String excelName) throws Exception {
		Workbook wb = createReport(exportData,excelName,ExcelExportEnum.EXCEL2003);
		String filedName = setExcelName(excelName,ExcelExportEnum.EXCEL2003,"servers");
		produceExcelToServers(wb,filedName);
	}

   private static Workbook createReport(ExportData exportData,String excelName,ExcelExportEnum excelEdition) throws IOException, IllegalArgumentException, IllegalAccessException, ParseException, InvocationTargetException, NoSuchMethodException{
	   Workbook wb = excelEdition==ExcelExportEnum.EXCEL2007 ? new XSSFWorkbook() : new HSSFWorkbook();
		 CellStyle style = setExcelStyle(wb);
		 //sheet
		 Sheet sheet = wb.createSheet("导出");
		 //row
		 Row row = sheet.createRow(0);
		 //cell
		 Cell cell = null;
		 int colIndex = 0;
		 //表头
		 List<Object> headsData = exportData.getHead();
		 //列的数据类型
		 String[] dataTypes = getDataTypesOrFormatting(headsData.size(), exportData.getDataType().length,exportData.getDataType());
		 //列的格式设置
		 String[] formatting = getDataTypesOrFormatting(headsData.size(), exportData.getFormat().length,exportData.getFormat());
		 for(Object head: headsData){
			 sheet.setColumnWidth(colIndex, 30*256); // 列宽
			 cell = row.createCell(colIndex++);
			 cell.setCellValue(head.toString());
			 cell.setCellStyle(style);
		 }
		 // 表体
		 int index = 1;
		 row = sheet.createRow(index);
		 colIndex = 0;
		 List<List<Object>> bodyDatas = exportData.getBodyData();
		 for(int i=0;i<bodyDatas.size();i++){
			 List<Object> bodyDataOneList = bodyDatas.get(i);
			 for(int j=1; j<bodyDataOneList.size();j++){
				 Object bodyData = bodyDataOneList.get(j);
				 cell = row.createCell(colIndex++);
				 //要转变的数据类型
				 String dataType = dataTypes[colIndex-1];
				 String formatData = dealWithFormat(wb,formatting[colIndex-1],dataType,bodyData,cell);
				 if("INTEGER".equals(dataType)&&!"&nbsp;".equals(formatData)){
					 cell.setCellValue(Integer.parseInt(formatData));
    			 }else if("DOUBLE".equals(dataType)&&!"&nbsp;".equals(formatData)){
    				 cell.setCellValue(Double.parseDouble(formatData));
    			 }else if("FLOAT".equals(dataType)&&!"&nbsp;".equals(formatData)){
    				 cell.setCellValue(Float.parseFloat(formatData));
    			 }else if("LONG".equals(dataType)&&!"&nbsp;".equals(formatData)){
    				 cell.setCellValue(Long.parseLong(formatData));
    			 }else if("DATE".equals(dataType)||"TIME".equals(dataType)){
    				 dealWithDateAndTime(wb,formatting[colIndex-1],dataType, bodyData,cell);
    			 }else{
    				 if(bodyData==null||"&nbsp;".equals(bodyData)){bodyData="";}
    				 cell.setCellValue(bodyData.toString());
    			 }
			 }
		     colIndex=0;
			 index++;
			 row = sheet.createRow(index);
		}
		
		return wb;
   } 
   
   private static String dealWithFormat(Workbook wb,String formatting,String dataType,Object bodyData,Cell cell){
	   String date="";
	   if(bodyData==null||bodyData==""){
		   date="";
	   }else if(dataType==""){
		   date=bodyData.toString();
	   }else{
		   CellStyle dateStyle = wb.createCellStyle();
		   DataFormat format=  wb.createDataFormat();
		   dateStyle.setDataFormat(format.getFormat(formatting));
		   cell.setCellStyle(dateStyle);  
		   date=bodyData.toString();  
	   }
	   return date;
   }
   private static void dealWithDateAndTime(Workbook wb,String formatting,String dataType,Object bodyData,Cell cell) throws IllegalArgumentException, IllegalAccessException, ParseException{
	   dealWithFormat(wb,formatting,dataType,bodyData,cell);
	   Date dateValue = getBodyDataOfTime(dataType,bodyData);
	   cell.setCellValue(dateValue);  
   }
   private static Date getBodyDataOfTime(String dataType,Object obj) throws IllegalArgumentException, IllegalAccessException, ParseException{
	   SimpleDateFormat dft = null; 
	   if("DATE".equals(dataType)){
		   dft = new SimpleDateFormat("yyyy-MM-dd");
	   }else if("TIME".equals(dataType)){
		   dft = new SimpleDateFormat("yyyy-MM-dd hh:mm");
	   }
	   java.util.Date cDate = dft.parse(obj.toString());   
	   return new java.sql.Date(cDate.getTime());
   }
  
	/**
	 * 读取properties文件
	 * @return
	 * @throws Exception
	 */
	private static String readProperties(String key)throws Exception{
		Properties propert = new Properties();
		propert.load(ExcelExporter.class.getClassLoader().getResourceAsStream("application.properties"));
		return propert.getProperty(key);
	}

	//设置excel名称
	private static String setExcelName(String excelName,ExcelExportEnum excelEdition,String exportType) throws UnsupportedEncodingException{
    	if(excelName==null || "".equals(excelName)){
    		if(excelEdition.equals(ExcelExportEnum.EXCEL2007)){
    			excelName = "default.xlsx";
    		}else{
    			excelName = "default.xls";
    		}
    	}else{
    		if(excelEdition.equals(ExcelExportEnum.EXCEL2007)){
    			excelName = excelName+".xlsx";
    		}else{
    			excelName = excelName+".xls";
    		}
    		if("web".equals(exportType)){
	    		byte[] byname = excelName.getBytes("gbk");
	    		excelName = new String(byname,"8859_1");
    		}
    	}
    	return excelName;
	}
	//导出excel网页2003
	private static void produceExcelForWeb(Workbook wb,String excelName) throws IOException{
		HttpServletResponse response = responseForweb(excelName);
        wb.write(response.getOutputStream());
	}
	//导出excel上传到服务器2003
	private static void produceExcelToServers(Workbook wb,String excelName) throws Exception{
		 FileOutputStream out = outputStreamToServers(excelName);
		 wb.write(out);
		 out.close();
	}
	//excel设置样式2003
	private static CellStyle setExcelStyle(Workbook wb){
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont(); 
		return produceExcelStyle(font,style);
	}
	//导出excel网页2007
	private static void produceExcelForWeb(XSSFWorkbook wb,String excelName) throws IOException{
		HttpServletResponse response = responseForweb(excelName);
		wb.write(response.getOutputStream());
	}
	//导出excel上传到服务器2007
	private static void produceExcelToServers(XSSFWorkbook wb,String excelName) throws Exception{
		 FileOutputStream out = outputStreamToServers(excelName);
		 wb.write(out);
		 out.close();
	}
	private static CellStyle produceExcelStyle(Font font, CellStyle style){
		// 字体
		font.setFontHeightInPoints((short)10); // 字号
		font.setColor(IndexedColors.RED.getIndex()); // 颜色
		font.setBoldweight(Font.BOLDWEIGHT_BOLD); // 加粗显示
		style.setFont(font);
	   	 
	   	 // 单元格
        style.setAlignment(CellStyle.ALIGN_CENTER);// 居中
        style.setVerticalAlignment(CellStyle.VERTICAL_TOP); // 靠上
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex()); // 背景色
        style.setFillPattern(CellStyle.SOLID_FOREGROUND); // 填充方式
       
        style.setBorderTop(CellStyle.BORDER_THIN); // 上边框填充
        style.setTopBorderColor(IndexedColors.BLUE.getIndex()); // 上边框样式
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLUE.getIndex());
       return style;
	}
	private static HttpServletResponse responseForweb(String excelName){
		HttpServletResponse response = Struts2Utils.getResponse();
    	response.reset();
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + excelName + "\"");
        return response;
	}
	private static FileOutputStream outputStreamToServers(String excelName) throws Exception{
		 String path=readProperties("excel.export.file.path");
		 FileOutputStream out = new FileOutputStream(new File(path+excelName));
        return out;
	}
	private static String[] getDataTypesOrFormatting(int headsDataSize,int targetSize,String[] targetDatas ){
		String[] datas;
		 if(headsDataSize>targetSize){
			 datas = new String[headsDataSize];
			 int size = headsDataSize - targetSize;
			 for(int j=0; j<targetSize; j++){
				 datas[j]=targetDatas[j];
			 }
			 for(int i=0; i<size; i++){
				 datas[targetSize+i]="";
			 }
		 }else{
			datas = targetDatas;
		 }
		 return datas;
	}
}
