package com.norteksoft.product.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.norteksoft.mms.base.utils.view.ExportData;
import com.norteksoft.product.util.excel.CellFormatter;
import com.norteksoft.product.util.excel.CellFormatterFactory;

public class ExcelExporter {
	//此修改是为了解决大文件导出，根据用户配置的路径先把文件下载到用户服务器上，然后再从服务器上读文件给用户下载
	public static String export(ExportData exportData,String excelName,ExcelExportEnum excelEdition) throws Exception {
		if(excelEdition==ExcelExportEnum.EXCEL2007){
			XSSFWorkbook wb = (XSSFWorkbook) createReport(exportData,excelName,ExcelExportEnum.EXCEL2007);
			String filedName = setExcelName(excelName,ExcelExportEnum.EXCEL2007);
			return produceExcelToServers(wb,filedName);
		}else{
			return export(exportData,excelName); 
		}
	}
	
	public static String export(ExportData exportData,String excelName) throws Exception {
		Workbook wb = createReport(exportData,excelName,ExcelExportEnum.EXCEL2003);
		String filedName = setExcelName(excelName,ExcelExportEnum.EXCEL2003);
		return produceExcelToServers(wb,filedName);
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
		 String[] dataTypes = getDataTypesOrFormatting(headsData==null?0:headsData.size(), exportData.getDataType()==null?0:exportData.getDataType().length,exportData.getDataType());
		 //列的格式设置
		 String[] formatting = getDataTypesOrFormatting(headsData==null?0:headsData.size(), exportData.getFormat()==null?0:exportData.getFormat().length,exportData.getFormat());
		 //列的值设置
		 String[] valueSets = getDataTypesOrFormatting(headsData==null?0:headsData.size(), exportData.getValueSet()==null?0:exportData.getValueSet().length,exportData.getValueSet());
		if(headsData!=null){
			for(Object head: headsData){
				sheet.setColumnWidth(colIndex, 30*256); // 列宽
				cell = row.createCell(colIndex++);
				cell.setCellValue(head.toString());
				cell.setCellStyle(style);
			}
		}
		 // 表体
		 int index = 1;
		 row = sheet.createRow(index);
		 colIndex = 0;
		 List<List<Object>> bodyDatas = exportData.getBodyData();
		 if(bodyDatas!=null){
			 for(int i=0;i<bodyDatas.size();i++){
				 List<Object> bodyDataOneList = bodyDatas.get(i);
				 for(int j=1; j<bodyDataOneList.size();j++){
					 Object bodyData = bodyDataOneList.get(j);
					 cell = row.createCell(colIndex++);
					 //根据dataType来设置cell的值并格式化成excel的对应格式
					 CellFormatter cellFormatter=CellFormatterFactory.getCellFormatter(dataTypes[colIndex-1],cell,valueSets[colIndex-1]);
					 cellFormatter.formatValue(bodyData,formatting[colIndex-1]);
					 
				 }
				 colIndex=0;
				 index++;
				 row = sheet.createRow(index);
			 }
		 }
		
		return wb;
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
	private static String setExcelName(String excelName,ExcelExportEnum excelEdition) throws UnsupportedEncodingException{
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
//    		byte[] byname = excelName.getBytes("gbk");
//    		excelName = new String(byname,"8859_1");
    	}
    	return excelName;
	}
	//导出excel上传到服务器2003
	private static String produceExcelToServers(Workbook wb,String excelName) throws Exception{
		String fileName= UUID.randomUUID().toString();
		FileOutputStream out = outputStreamToServers(fileName);
		wb.write(out);
		out.close();
		return excelName+"_"+fileName;
	}
	//excel设置样式2003
	private static CellStyle setExcelStyle(Workbook wb){
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont(); 
		return produceExcelStyle(font,style);
	}
	//导出excel上传到服务器2007
	private static String produceExcelToServers(XSSFWorkbook wb,String excelName) throws Exception{
		String fileName= UUID.randomUUID().toString();
		 FileOutputStream out = outputStreamToServers(fileName);
		 wb.write(out);
		 out.close();
		 return excelName+"_"+fileName;
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
	private static FileOutputStream outputStreamToServers(String excelName) throws Exception{
		 String path=readProperties("excel.export.file.path");
		 path=cretaFolder(path+"/");
		 FileOutputStream out = new FileOutputStream(new File(path+excelName));
        return out;
	}
	/**
	 * 创建文件夹
	 * @param path
	 * @return
	 */
	private static String cretaFolder(String path){
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		return path;
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
