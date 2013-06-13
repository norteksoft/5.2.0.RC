package com.norteksoft.product.api.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.options.entity.ImportColumn;
import com.norteksoft.bs.options.entity.ImportDefinition;
import com.norteksoft.bs.options.enumeration.BusinessType;
import com.norteksoft.bs.options.enumeration.ImportType;
import com.norteksoft.bs.options.enumeration.ImportWay;
import com.norteksoft.bs.options.service.ImportDefinitionManager;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.product.api.DataImporterCallBack;
import com.norteksoft.product.api.DataImporterService;
import com.norteksoft.product.util.ZipUtils;
@Service
@Transactional
public class DataImporterServiceImpl implements DataImporterService {
	private final static String SEPARATOR = "\\\\";
	public static final SimpleDateFormat SIMPLEDATEFORMAT1 = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat SIMPLEDATEFORMAT2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	private ImportDefinitionManager importDefinitionManager;
	
	public String importData(File file, String fileName) throws Exception{
		return importData(file, fileName, new DefaultDataImporterCallBack());
	}
	
	public String importData(File file, String fileName,DataImporterCallBack callBack) throws Exception{
		String result = "";
		Workbook workBook = null;
		if(fileName.endsWith(".xls")){
			workBook = new HSSFWorkbook(new FileInputStream(file));
			result = importExcelData(workBook,callBack);
		}else if(fileName.endsWith(".xlsx")){
			workBook = new XSSFWorkbook(new FileInputStream(file));
			result = importExcelData(workBook,callBack);
		}else{
			result = importTextData(file,fileName,callBack);
		}
		if(StringUtils.isEmpty(result)){
			result="导入成功！";
		}
		return result;
	}
	
	private String importTextData(File file, String fileName, DataImporterCallBack callBack) throws Exception{
		String result = "";
		if(fileName.lastIndexOf("_")==-1)
			return "文件名不符合要求，正确格式为：导入定义编号_ 文件名.文件格式！";
		String code=fileName.substring(0, fileName.lastIndexOf("_"));
		ImportDefinition importDefinition=importDefinitionManager.getImportDefinitionByCode(code);
		if(importDefinition != null){
			if(importDefinition.getImportColumns() !=null && importDefinition.getImportColumns().size()>0){
				if(ImportType.TXT_DIVIDE.equals(importDefinition.getImportType())){
					if(StringUtils.isEmpty(importDefinition.getDivide())){
						result = "字段信息中的 分隔符没有填写！\n";
					}else{
						result = importTxtDivideData(file,importDefinition,callBack);
					}
				}else if(ImportType.TXT.equals(importDefinition.getImportType())){
					String validateResult=validateImportTxtData(importDefinition);
					if(StringUtils.isEmpty(validateResult)){
						result = importTxtData(file,importDefinition,callBack);
					}else{
						result = validateResult;
					}
				}else{
					result = "编号为 "+code+" 的导入定义中没有选择 导入类型 ！";
				}
			}else{
				result = "编号为 "+code+" 的导入定义中没有录入导入列！";
			}
		}else{
			result = "导入管理中没有编号为 "+code+" 的导入定义！\n";
		}
		return result;
	}
	
	public static String validateImportTxtData(ImportDefinition importDefinition){
		String result="";
		String relevanceResult=""; 
		for(ImportColumn importColumn:importDefinition.getImportColumns()){
			if(BusinessType.RELEVANCE_FIELD.equals(importColumn.getBusinessType()) && (StringUtils.isEmpty(importDefinition.getRelevanceName()) || StringUtils.isEmpty(importDefinition.getForeignKey()))){
				relevanceResult="基本信息中的关联表名或外键没有填写！\n";
			}
			if(importColumn.getWidth()==null || importColumn.getWidth()==0){
				result+="字段信息中的 "+importColumn.getAlias()+" 字段固定长度没有填写！\n";
			}
		}
		if(StringUtils.isNotEmpty(relevanceResult))
			result+=relevanceResult;
		return result;
	}
	
	/**
	 * 固定长度文本导入
	 * @param file
	 * @param importDefinition
	 * @return
	 * @throws Exception
	 */
	private String importTxtData(File file, ImportDefinition importDefinition, DataImporterCallBack callBack) throws Exception{
		FileInputStream fis= new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis,ZipUtils.prexEncoding(file.getPath()));
		BufferedReader br = new BufferedReader(isr);
		String line="";
		List<String> fileContent = new ArrayList<String>();
		int i=0;
		while ((line=br.readLine())!=null){
			if(i > 0)
				fileContent.add(line);
			i++;
		}
		br.close();
		isr.close();
		fis.close();
		
		String result = "";
		if(ImportWay.SUCCESS.equals(importDefinition.getImportWay())){//所有数据正确后导入
			result = callBack.afterValidate(validateImport(fileContent,importDefinition));
			if(StringUtils.isEmpty(result)){
				result=insertIntoData(fileContent,importDefinition,callBack);
			}else{
				return result;
			}
		}else if(ImportWay.ONLY_SUCCESS.equals(importDefinition.getImportWay())){//只导入正确数据
			result=onlyImportRightData(fileContent,importDefinition,callBack);
		}else{//有错误数据就不导入
			result=haveErrorNotImport(fileContent,importDefinition,callBack);
		}
		return result;
	}
	
	/**
	 * 有错误数据就不导入
	 * @param fileContent
	 * @param importDefinition
	 * @param callBack
	 */
	private String haveErrorNotImport(List<String> fileContent,ImportDefinition importDefinition, DataImporterCallBack callBack) {
		String result="";
		//导入定义中固定长度的总和
		Integer rowWidth=importDefinitionManager.totalWidth(importDefinition.getId());
		int rowNum=1;//文件行数
		if(fileContent != null && fileContent.size()>0){
			for (String row : fileContent) {
				String rowContent=row;
				if(rowWidth==rowContent.length()){
					for(ImportColumn importColumn:importDefinition.getImportColumns()){
						String columnContent=rowContent.substring(0,importColumn.getWidth());
						rowContent=rowContent.substring(importColumn.getWidth(), rowContent.length());
						String validateResult=validateColumnDataType(importColumn,columnContent.trim(),rowNum);
						if(StringUtils.isNotEmpty(validateResult))
							return validateResult+"！" ;
					}
				}else{
					return "第"+rowNum+"行数据长度为"+rowContent.length()+"不等于导入定义中固定长度的总和"+rowWidth+"！";
				}
				rowNum++;
			}
		}else{
			return "导入的文件中没有数据！";
		}
		String message=insertIntoData(fileContent,importDefinition,callBack);
		if(StringUtils.isNotEmpty(message)){
			return message;
		}
		return result;
	}
	
	/**
	 * 固定长度导入验证
	 */
	private List<String> validateImport(List<String> fileContent,ImportDefinition importDefinition) {
		//导入定义中固定长度的总和
		Integer rowWidth=importDefinitionManager.totalWidth(importDefinition.getId());
		List<String> result=new ArrayList<String>();
		int rowNum=1;//文件行数
		if(fileContent != null && fileContent.size()>0){
			for (String row : fileContent) {
				String rowContent=row;
				if(rowWidth==rowContent.length()){
					for(ImportColumn importColumn:importDefinition.getImportColumns()){
						String columnContent=rowContent.substring(0,importColumn.getWidth());
						rowContent=rowContent.substring(importColumn.getWidth(), rowContent.length());
						String validateResult=validateColumnDataType(importColumn,columnContent.trim(),rowNum);
						if(StringUtils.isNotEmpty(validateResult))
							result.add(validateResult);
					}
				}else{
					result.add("第"+rowNum+"行数据长度为"+rowContent.length()+"不等于导入定义中固定长度的总和"+rowWidth);
				}
				
				rowNum++;
			}
		}else{
			result.add("导入的文件中没有数据");
		}
		return result;
	}
	
	/**
	 * 只导入正确数据
	 * @param fileContent
	 * @param importDefinition
	 * @param callBack
	 */
	private String onlyImportRightData(List<String> fileContent,ImportDefinition importDefinition, DataImporterCallBack callBack) {
		//导入定义中固定长度的总和
		Integer rowWidth=importDefinitionManager.totalWidth(importDefinition.getId());
		List<String> result1=new ArrayList<String>();
		int rowNum=1;//文件行数
		if(fileContent != null && fileContent.size()>0){
			for (String row : fileContent) {
				String rowContent=row;
				List<String> result=new ArrayList<String>();
				if(rowWidth==rowContent.length()){
					for(ImportColumn importColumn:importDefinition.getImportColumns()){
						String columnContent=rowContent.substring(0,importColumn.getWidth());
						rowContent=rowContent.substring(importColumn.getWidth(), rowContent.length());
						String validateResult=validateColumnDataType(importColumn,columnContent.trim(),rowNum);
						if(StringUtils.isNotEmpty(validateResult))
							result.add(validateResult);
					}
				}else{
					result.add("第"+rowNum+"行数据长度为"+rowContent.length()+"不等于导入定义中固定长度的总和"+rowWidth);
				}
				if(result.size()==0){//该行没有错误信息
					String message=importTxtRowData(row,importDefinition,callBack);
					if(StringUtils.isNotEmpty(message)){//message不为空说明没有给要导入的数据表名
						if("no_table_name".equals(message)){
							return "没有给数据表名！";
						}else if("no_company_id".equals(message)){
							return "没有取到公司id！";
						}else{
							result1.add(message);
						}
					}
				}else{//该行有错误信息
					for(String str:result){
						result1.add(str);
					}
				}
				rowNum++;
			}
			callBack.afterSaveAllRows();
		}else{
			result1.add("导入的文件中没有数据");
		}
		if(result1.size()==0){
			result1.add("导入文件成功");
		}
		return callBack.afterValidate(result1);
	}

	//INSERT INTO BS_IMPORT_DEFINITION(company_id,creator,created_time) VALUES(8,'test1','2012-09-26 16:00:01');
	private String insertIntoData(List<String> fileContent,ImportDefinition importDefinition, DataImporterCallBack callBack) {
		List<String> result=new ArrayList<String>();
		for (String row : fileContent) {
			String message=importTxtRowData(row,importDefinition,callBack);
			if(StringUtils.isNotEmpty(message)){//message不为空说明没有给要导入的数据表名
				if("no_table_name".equals(message)){
					return "没有给数据表名！";
				}else if("no_company_id".equals(message)){
					return "没有取到公司id！";
				}else{
					result.add(message);
				}
				
			}
		}
		callBack.afterSaveAllRows();
		if(result.size()>0){
			return callBack.afterValidate(result);
		}else{
			return "";
		}
	}
	
	private String importTxtRowData(String rowContent,ImportDefinition importDefinition, DataImporterCallBack callBack){
		List<String> rowValues=new ArrayList<String>();
		Date currentDate=new Date();
		for(ImportColumn importColumn:importDefinition.getImportColumns()){
			String columnValue=(rowContent.substring(0,importColumn.getWidth())).trim();
			if(StringUtils.isEmpty(columnValue) && StringUtils.isNotEmpty(importColumn.getDefaultValue())){
				columnValue=importColumn.getDefaultValue().trim();
			}
			rowValues.add(packagingColumnValue(importColumn,columnValue,currentDate));
			rowContent=rowContent.substring(importColumn.getWidth(), rowContent.length());
		}
		String[] rowValue=rowValues.toArray(new String[rowValues.size()]);
		return saveSingleRowData(rowValue, importDefinition, callBack);
	}
	
	/**
	 * 分隔符分隔的文本导入
	 * @param file
	 * @param importDefinition
	 * @return
	 * @throws Exception
	 */
	
	private String importTxtDivideData(File file,ImportDefinition importDefinition, DataImporterCallBack callBack) throws Exception{
		int columnAmount=importDefinition.getImportColumns().size();
		String separators=importDefinition.getDivide();
		FileInputStream fis= new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis,ZipUtils.prexEncoding(file.getPath()));
		BufferedReader br = new BufferedReader(isr);
		String line="";
		List<String[]> fileContent = new ArrayList<String[]>();
		int i=0;
		while ((line=br.readLine())!=null){
			if(i > 0){
				String[] columnContent ;
				if(separators.equals("\\")){
					columnContent = line.split(SEPARATOR);
				}else{
					columnContent = line.split(separators);
				}
				List<String> columns =Arrays.asList(columnContent);
				List<String> values=new ArrayList<String>();
				values.addAll(columns);
				if(columnContent.length<columnAmount){
					for(int m=0;m<columnAmount-columnContent.length;m++){
						values.add("");
					}
				}
				
				fileContent.add(values.toArray(new String[columnAmount]));
			}
				
			i++;
		}
		br.close();
		isr.close();
		fis.close();
		String result = "";
		if(ImportWay.SUCCESS.equals(importDefinition.getImportWay())){//所有数据正确后导入
			result = callBack.afterValidate(validateSeparatorsImport(fileContent,importDefinition));
			if(StringUtils.isEmpty(result)){
				result=insertIntoSeparatorsData(fileContent,importDefinition,callBack);
			}else{
				return result;
			}
		}else if(ImportWay.ONLY_SUCCESS.equals(importDefinition.getImportWay())){//只导入正确数据
			result=onlyImportTxtDivideRightData(fileContent, importDefinition, callBack);
		}else{
			result=haveErrorNotImportTxtDivide(fileContent, importDefinition, callBack);
		}
		return result;
	}
	
	/**
	 * 有错误数据就不导入
	 * @param fileContent
	 * @param importDefinition
	 * @param callBack
	 */
	private String haveErrorNotImportTxtDivide(List<String[]> fileContent,ImportDefinition importDefinition, DataImporterCallBack callBack) {
		String result="";
		int rowNum=1;//文件行数
		String relevance =validateRelevance(importDefinition);
		if(StringUtils.isNotEmpty(relevance)){
			return relevance+"！";
		}
		if(fileContent != null && fileContent.size()>0){
			for (String[] row : fileContent) {
				int i=0;
				for(ImportColumn importColumn:importDefinition.getImportColumns()){
					String validateResult=validateColumnDataType(importColumn,row[i].trim(),rowNum);
					if(StringUtils.isNotEmpty(validateResult))
						return validateResult+"！";
					i++;
				}
				rowNum++;
			}
		}else{
			return "导入的文件中没有数据！";
		}
		String message=insertIntoSeparatorsData(fileContent,importDefinition,callBack);
		if(StringUtils.isNotEmpty(message)){
			result=message;
		}
		return result;
	}
	
	/**
	 * 只导入正确数据
	 * @param fileContent
	 * @param importDefinition
	 * @param callBack
	 */
	private String onlyImportTxtDivideRightData(List<String[]> fileContent,ImportDefinition importDefinition, DataImporterCallBack callBack) {
		List<String> result1=new ArrayList<String>();
		int rowNum=1;//文件行数
		String relevance =validateRelevance(importDefinition);
		if(StringUtils.isNotEmpty(relevance)){
			return relevance+"！";
		}
		if(fileContent != null && fileContent.size()>0){
			for (String[] row : fileContent) {
				List<String> result=new ArrayList<String>();
				int i=0;
				for(ImportColumn importColumn:importDefinition.getImportColumns()){
					String validateResult=validateColumnDataType(importColumn,row[i].trim(),rowNum);
					if(StringUtils.isNotEmpty(validateResult))
						result.add(validateResult);
					i++;
				}
				if(result.size()==0){//该行没有错误信息
					String message=insertIntoSeparatorsRowData(row,importDefinition,callBack);
					if(StringUtils.isNotEmpty(message)){//message不为空说明没有给要导入的数据表名
						if("no_table_name".equals(message)){
							return "没有给数据表名！";
						}else if("no_company_id".equals(message)){
							return "没有取到公司id！";
						}else{
							result1.add(message);
						}
					}
				}else{//该行有错误信息
					for(String str:result){
						result1.add(str);
					}
				}
				rowNum++;
			}
			callBack.afterSaveAllRows();
		}else{
			result1.add("导入的文件中没有数据");
		}
		if(result1.size()==0){
			result1.add("导入文件成功");
		}
		return callBack.afterValidate(result1);
	}
	
	private String insertIntoSeparatorsData(List<String[]> fileContent,ImportDefinition importDefinition, DataImporterCallBack callBack) {
		List<String> result=new ArrayList<String>();
		for (String row[] : fileContent) {
			String message=insertIntoSeparatorsRowData(row,importDefinition,callBack);
			if(StringUtils.isNotEmpty(message)){//message不为空说明没有给要导入的数据表名
				if("no_table_name".equals(message)){
					return "没有给数据表名！";
				}else if("no_company_id".equals(message)){
					return "没有取到公司id！";
				}else{
					result.add(message);
				}
			}
		}
		callBack.afterSaveAllRows();
		if(result.size()>0){
			return callBack.afterValidate(result);
		}else{
			return "";
		}
		
	}
	
	/**
	 * 分隔符分隔文本插入一行数据
	 * 返回值不为空说明没有给要导入的数据表名，为空说明插入成功
	 */
	private String insertIntoSeparatorsRowData(String[] rowContent,ImportDefinition importDefinition, DataImporterCallBack callBack) {
		int i=0;
		List<String> rowValues=new ArrayList<String>();
		Date currentDate=new Date();
		for(ImportColumn importColumn:importDefinition.getImportColumns()){
			String columnValue=rowContent[i].trim();
			if(StringUtils.isEmpty(columnValue) && StringUtils.isNotEmpty(importColumn.getDefaultValue())){
				columnValue=importColumn.getDefaultValue().trim();
			}
			rowValues.add(packagingColumnValue(importColumn,columnValue,currentDate));
			i++;
		}
		String[] rowValue=rowValues.toArray(new String[rowValues.size()]);
		return  saveSingleRowData(rowValue, importDefinition, callBack);
	}
	
	private String packagingColumnValue(ImportColumn importColumn,String columnValue, Date currentDate){
		String column="";
		if((DataType.LONG.equals(importColumn.getDataType())||DataType.INTEGER.equals(importColumn.getDataType()))&&columnValue.contains(".")){//excel的整形数据会带小数点
			column=columnValue.substring(0,columnValue.indexOf("."));
		}else if(DataType.DATE.equals(importColumn.getDataType()) && "${createdTime}".equals(columnValue)){
			column=SIMPLEDATEFORMAT1.format(currentDate);
		}else if(DataType.TIME.equals(importColumn.getDataType()) && "${createdTime}".equals(columnValue)){
			column=SIMPLEDATEFORMAT2.format(currentDate);
		}else{
			column=columnValue;
		}
		return column;
	}
	
	/**
	 * 验证分隔符分隔的文本
	 * @param fileContent
	 * @param importDefinition
	 * @return
	 */
	private List<String> validateSeparatorsImport(List<String[]> fileContent,ImportDefinition importDefinition) {
		List<String> result=new ArrayList<String>();
		int rowNum=1;//文件行数
		String relevance =validateRelevance(importDefinition);
		if(StringUtils.isNotEmpty(relevance)){
			result.add(relevance);
		}
		if(fileContent != null && fileContent.size()>0){
			for (String row[] : fileContent) {
				int i=0;
				for(ImportColumn importColumn:importDefinition.getImportColumns()){
					String validateResult=validateColumnDataType(importColumn,row[i].trim(),rowNum);
					if(StringUtils.isNotEmpty(validateResult))
						result.add(validateResult);
					i++;
				}
				rowNum++;
			}
		}else{
			result.add("导入的文件中没有数据");
		}
		return result;
	}
	
	/**
	 * Excel导入
	 * @param importDefinition
	 * @param workBook
	 * @return
	 * @throws Exception
	 */
	private String importExcelData(Workbook workBook, DataImporterCallBack callBack) throws Exception{
		String result = "";
		if(workBook!=null){
			int numberOfSheets = workBook.getNumberOfSheets();
			for(int i=0;i<numberOfSheets;i++){
				Sheet sheet = workBook.getSheetAt(i);
				String code=sheet.getSheetName();
				ImportDefinition importDefinition=importDefinitionManager.getImportDefinitionByCode(code);
				if(importDefinition != null){
					if(importDefinition.getImportColumns() !=null && importDefinition.getImportColumns().size()>0){
						String sheetResult = resolvingExcelData(sheet,callBack,importDefinition);
						if("success".equals(sheetResult)){
							result+="Sheet名为 "+code+" 的文件导入成功！\n";
						}else{
							result+=sheetResult;
						}
					}else{
						result += "编号为 "+importDefinition.getCode()+" 的导入定义中没有录入导入列！\n";
					}
				}else{
					result += "导入管理中没有编号为 "+code+" 的导入定义！\n";
				}
			}
		}else{
			result="导入文件中没有内容！";
		}
		return result;
	}
	/**
	 * 解析数据
	 * @param sheet
	 * @param callBack
	 * @param importDefinition
	 */
	private String resolvingExcelData(Sheet sheet, DataImporterCallBack callBack,ImportDefinition importDefinition) {
		String result = "";
		int firstRowNum = sheet.getFirstRowNum();
		int lastRowNum = sheet.getLastRowNum();
		List<String[]> list = new ArrayList<String[]>();
		int columnAmount=importDefinition.getImportColumns().size();
		for(int i = firstRowNum+1; i <= lastRowNum; i++){
			List<String> columns = new ArrayList<String>();
			Row rowData = sheet.getRow(i);
			if(rowData == null) continue;
			for (int j = 0; j <= rowData.getLastCellNum()-1; j++) {
				Cell cell = rowData.getCell(j);
				if(cell==null){
					columns.add("");
				}else{
					columns.add(getCellValue(cell)==null?"":getCellValue(cell).toString());
				}
			}
			if(rowData.getLastCellNum()<columnAmount){
				for(int m=0;m<columnAmount-rowData.getLastCellNum();m++){
					columns.add("");
				}
			}
			list.add(columns.toArray(new String[columnAmount]));
		}
		if(list.size()>0){
			result=resolvingImportData(list,importDefinition,callBack);
		}else{
			result="Sheet名为 "+importDefinition.getCode()+" 的文件中没有内容！\n";
		}
		return result;
	}
	
	private String resolvingImportData(List<String[]> list,ImportDefinition importDefinition,DataImporterCallBack callBack){
		String result="";
		if(ImportWay.SUCCESS.equals(importDefinition.getImportWay())){//所有数据正确后导入
			result = callBack.afterValidate(validateExcelImport(list,importDefinition));
			if(StringUtils.isEmpty(result)){
				String message=insertIntoExcelData(list,importDefinition,callBack);
				if(StringUtils.isNotEmpty(message)){
					result=message;
				}else{
					result="success";
				}
			}else{
				return result;
			}
		}else if(ImportWay.ONLY_SUCCESS.equals(importDefinition.getImportWay())){//只导入正确数据
			result = onlyImportExcelRightData(list, importDefinition, callBack);
		}else{
			result = haveErrorNotImportExcel(list, importDefinition,  callBack);
		}
		return result;
	}

	/**
	 * 有错误数据就不导入
	 * @param fileContent
	 * @param importDefinition
	 * @param callBack
	 */
	private String haveErrorNotImportExcel(List<String[]> fileContent,ImportDefinition importDefinition, DataImporterCallBack callBack) {
		String result = "";
		int rowNum=1;//文件行数
		String relevance =validateRelevance(importDefinition);
		if(StringUtils.isNotEmpty(relevance)){
			return relevance+"！";
		}
		if(fileContent != null && fileContent.size()>0){
			for (String[] row : fileContent) {
				int i=0;
				for(ImportColumn importColumn:importDefinition.getImportColumns()){
					String validateResult=validateColumnDataType(importColumn,row[i].trim(),rowNum);
					if(StringUtils.isNotEmpty(validateResult))
						return validateResult+"！";
					i++;
				}
				rowNum++;
			}
		}else{
			return "导入的文件中没有数据！";
		}
		String message=insertIntoExcelData(fileContent,importDefinition,callBack);
		if(StringUtils.isNotEmpty(message)){
			result=message;
		}
		return result;
	}
	
	/**
	 * 只导入正确数据
	 * @param fileContent
	 * @param importDefinition
	 * @param callBack
	 */
	private String onlyImportExcelRightData(List<String[]> fileContent,ImportDefinition importDefinition, DataImporterCallBack callBack) {
		List<String> result1 = new ArrayList<String>();
		int rowNum=1;//文件行数
		String relevance =validateRelevance(importDefinition);
		if(StringUtils.isNotEmpty(relevance)){
			return relevance+"！";
		}
		if(fileContent != null && fileContent.size()>0){
			for (String[] row : fileContent) {
				List<String> result = new ArrayList<String>();
				int i=0;
				for(ImportColumn importColumn:importDefinition.getImportColumns()){
					String validateResult=validateColumnDataType(importColumn,row[i].trim(),rowNum);
					if(StringUtils.isNotEmpty(validateResult))
						result.add(validateResult);
					i++;
				}
				if(result.size()==0){//该行没有错误信息
					String message=insertIntoSeparatorsRowData(row,importDefinition,callBack);
					if(StringUtils.isNotEmpty(message)){//message不为空说明没有给要导入的数据表名
						if("no_table_name".equals(message)){
							return "没有给数据表名！";
						}else if("no_company_id".equals(message)){
							return "没有取到公司id！";
						}else{
							result1.add(message);
						}
					}
				}else{//该行有错误信息
					for(String str:result){
						result1.add(str);
					}
				}
				rowNum++;
			}
			callBack.afterSaveAllRows();
		}else{
			result1.add("导入的文件中没有数据");
		}
		if(result1.size()==0){
			result1.add("导入文件成功");
		}
		return callBack.afterValidate(result1);
	}
	
	private String saveSingleRowData(String[] rowValue,ImportDefinition importDefinition, DataImporterCallBack callBack) {
		String result="";
		if(callBack.beforeSaveSingleRow(rowValue,importDefinition)){
			result=callBack.saveSingleRow(rowValue,importDefinition);
			callBack.afterSaveSingleRow(rowValue,importDefinition);
		}
		return result;
	}
	private String insertIntoExcelData(List<String[]> fileContent,ImportDefinition importDefinition, DataImporterCallBack callBack) {
		List<String> result=new ArrayList<String>();
		for (String[] row : fileContent) {
			String message=insertIntoSeparatorsRowData(row,importDefinition,callBack);
			if(StringUtils.isNotEmpty(message)){//message不为空说明没有给要导入的数据表名
				if("no_table_name".equals(message)){
					return "没有给数据表名！";
				}else if("no_company_id".equals(message)){
					return "没有取到公司id！";
				}else{
					result.add(message);
				}
			}
		}
		callBack.afterSaveAllRows();
		if(result.size()>0){
			return callBack.afterValidate(result);
		}else{
			return "";
		}
	}
	private String validateRelevance(ImportDefinition importDefinition){
		for(ImportColumn importColumn:importDefinition.getImportColumns()){
			if(BusinessType.RELEVANCE_FIELD.equals(importColumn.getBusinessType()) && (StringUtils.isEmpty(importDefinition.getRelevanceName()) || StringUtils.isEmpty(importDefinition.getForeignKey()))){
				return "基本信息中的关联表名或外键没有填写";
			}
		}
		return "";
	}
	private List<String> validateExcelImport(List<String[]> fileContent,ImportDefinition importDefinition) {
		List<String> result = new ArrayList<String>();
		int rowNum=1;//文件行数
		String relevance =validateRelevance(importDefinition);
		if(StringUtils.isNotEmpty(relevance)){
			result.add(relevance);
		}
		if(fileContent != null && fileContent.size()>0){
			for (String[] row : fileContent) {
				int i=0;
				for(ImportColumn importColumn:importDefinition.getImportColumns()){
					String validateResult=validateColumnDataType(importColumn,row[i].trim(),rowNum);
					if(StringUtils.isNotEmpty(validateResult))
						result.add(validateResult);
					i++;
				}
				rowNum++;
			}
		}else{
			result.add("导入的文件中没有数据");
		}
		return result;
	}
	
	private String validateColumnDataType(ImportColumn importColumn,String columnContent,int rowNum){
		String validateResult="";
		if(importColumn.getNotNull()){
			if(StringUtils.isEmpty(importColumn.getDefaultValue())){//该字段不能为空，并且没有默认值
				if(StringUtils.isEmpty(columnContent)){
					validateResult="第"+rowNum+"行"+importColumn.getAlias()+"为空";
				}else{
					validateResult=validateDataType(importColumn.getDataType(),columnContent,rowNum,importColumn.getAlias(),false);
				}
			}else if(StringUtils.isNotEmpty(importColumn.getDefaultValue())){//该字段不能为空，并且有默认值
				//验证默认值的数据类型是否正确
				validateResult=validateDataType(importColumn.getDataType(),importColumn.getDefaultValue().trim(),0,importColumn.getAlias(),true);
			}
		}else{
			validateResult=validateDataType(importColumn.getDataType(),columnContent,rowNum,importColumn.getAlias(),false);
		}
		return validateResult;
	}
	
	/**
	 * 验证数据类型
	 * @param dataType
	 * @param columnContent
	 * @return
	 */
	public static String validateDataType(DataType dataType, String columnContent,int rowNum,String alias,boolean isDefaultValue) {
		if(StringUtils.isEmpty(columnContent)) return "";
		String result = "";
		if(DataType.DATE.equals(dataType)){
			if(!"${createdTime}".equals(columnContent) && !columnContent.matches("\\d{4}-\\d{2}-d{2}")){
				if(isDefaultValue)
					result=defaultValueMessage(alias);
				else
					result=dataMessage(rowNum,alias);
				result+="不是日期类型的数据";
			}
		}else if(!"${createdTime}".equals(columnContent) && DataType.TIME.equals(dataType)){
			if(!columnContent.matches("\\d{4}-\\d{2}-d{2}")){
				if(isDefaultValue)
					result=defaultValueMessage(alias);
				else
					result=dataMessage(rowNum,alias);
				result+="不是时间类型的数据";
			}
		}else if(DataType.BOOLEAN.equals(dataType)){
			if(!("0".equals(columnContent) || "1".equals(columnContent)  || "true".equals(columnContent)  || "false".equals(columnContent))){
				if(isDefaultValue)
					result=defaultValueMessage(alias);
				else
					result=dataMessage(rowNum,alias);
				result+="不是布尔类型的数据";
			}
		}else if(DataType.DOUBLE.equals(dataType)||DataType.FLOAT.equals(dataType)||DataType.AMOUNT.equals(dataType)||DataType.NUMBER.equals(dataType)){
			if(!(columnContent.matches("^(-?\\d+)(\\.\\d+)?$") || columnContent.matches("^-?\\d+$"))){
				if(isDefaultValue)
					result=defaultValueMessage(alias);
				else
					result=dataMessage(rowNum,alias);
				result+="不是浮点类型的数据";
			}
		}else if(DataType.LONG.equals(dataType)||DataType.INTEGER.equals(dataType)){
			String column="";
			if(columnContent.contains(".")){//excel的整形数据会带小数点
				column=columnContent.substring(0,columnContent.indexOf("."));
			}else{
				column=columnContent;
			}
			if(!column.matches("^-?\\d+$")){
				if(isDefaultValue)
					result=defaultValueMessage(alias);
				else
					result=dataMessage(rowNum,alias);
				result+="不是整数类型的数据";
			}
		}
		return result;
	}
	
	private static String dataMessage(int rowNum,String alias){
		return "第"+rowNum+"行"+alias;
	}
	
	private static String defaultValueMessage(String alias){
		return "字段信息中的 "+alias+" 字段的默认值";
	}
	
	public static Object getCellValue(Cell cell){
		if(cell!=null){
			Object object = null;
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_BOOLEAN:
				object = cell.getBooleanCellValue();
				break;
			case Cell.CELL_TYPE_FORMULA:
				object = cell.getCellFormula();
				break;
			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {       
			        double d = cell.getNumericCellValue();       
			        Date date = DateUtil.getJavaDate(d);
			        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			        object = format.format(date);
			    } else{
			    	object = cell.getNumericCellValue();
			    }
				break;
			case Cell.CELL_TYPE_STRING:
				object = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_ERROR:
				object = cell.getErrorCellValue();
				break;
			}
			return object;
		}else{
			return null;
		}
	}
}
