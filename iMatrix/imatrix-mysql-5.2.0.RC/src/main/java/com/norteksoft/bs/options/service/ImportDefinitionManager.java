package com.norteksoft.bs.options.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.bs.options.dao.ImportColumnDao;
import com.norteksoft.bs.options.dao.ImportDefinitionDao;
import com.norteksoft.bs.options.entity.ImportColumn;
import com.norteksoft.bs.options.entity.ImportDefinition;
import com.norteksoft.bs.options.enumeration.BusinessType;
import com.norteksoft.bs.options.enumeration.ImportType;
import com.norteksoft.bs.options.enumeration.ImportWay;
import com.norteksoft.mms.base.data.DataHandle;
import com.norteksoft.mms.base.data.DataSheetConfig;
import com.norteksoft.mms.base.data.DataTransfer;
import com.norteksoft.mms.base.data.FileConfigModel;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.jdbc.JdbcSupport;
import com.norteksoft.product.api.impl.DataImporterServiceImpl;
import com.norteksoft.product.api.impl.DefaultDataImporterCallBack;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.util.ZipUtils;
/**
 * 导入定义
 * @author Administrator
 *
 */
@Service
@Transactional
public class ImportDefinitionManager implements DataTransfer {
	private final static String SEPARATOR = "\\\\";
	public static final SimpleDateFormat SIMPLEDATEFORMAT1 = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat SIMPLEDATEFORMAT2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private JdbcSupport jdbcDao;
	@Autowired
	private ImportDefinitionDao importDefinitionDao;
	@Autowired
	private ImportColumnDao importColumnDao;
	@Autowired
	private CompanyManager companyManager;
	@Autowired
	private DataHandle dataHandle;
	
	/**
	 * 根据ID获得导入定义
	 * @param id
	 * @return
	 */
	@Transactional(readOnly=true)
	public ImportDefinition getImportDefinition(Long id) {
		return importDefinitionDao.get(id);
	}
	
	/**
	 * 根据编号获得导入定义
	 * @param code
	 * @return
	 */
	@Transactional(readOnly=true)
	public ImportDefinition getImportDefinitionByCode(String code) {
		return importDefinitionDao.getImportDefinitionByCode(code);
	}

	/**
	 * 获得所有的导入定义
	 * @param page
	 */
	public void getImportDefinitionPage(Page<ImportDefinition> page) {
		importDefinitionDao.getImportDefinitionPage(page);
	}

	/**
	 * 根据导入定义的id获得导入列
	 * @param id
	 * @return
	 */
	public List<ImportColumn> getImportColumnByImportId(Long importId) {
		return importColumnDao.getImportColumnByImportId(importId);
	}

	/**
	 * 保存导入基本信息
	 * @param importDefinition
	 */
	public void saveImportDefinition(ImportDefinition importDefinition) {
		importDefinitionDao.save(importDefinition);
	}

	/**
	 * 删除导入定义
	 * @param ids
	 */
	public void delete(String ids) {
		for(String id:ids.split(",")){
			importDefinitionDao.delete(Long.valueOf(id));
		}
	}

	/**
	 * 保存导入列
	 * @param id
	 */
	public void saveImportColumn(Long importId) {
		ImportDefinition importDefinition=importDefinitionDao.get(importId);
		List<Object> list=JsonParser.getFormTableDatas(ImportColumn.class);
		for(Object obj:list){
			ImportColumn importColumn=(ImportColumn)obj;
			importColumn.setCompanyId(ContextUtils.getCompanyId());
			importColumn.setImportDefinition(importDefinition);
			importColumnDao.save(importColumn);
		}
	}

	/**
	 * 删除导入列
	 * @param id
	 */
	public void importColumnDelete(Long id) {
		importColumnDao.delete(id);
	}
	
	
	
	
	/**
	 * 导入
	 * @param file
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public String importFile(File file,String fileName, ImportDefinition importDefinition) throws Exception {
		if(ContextUtils.getCompanyId()==null){
			 return "没有取到公司id！";
		 }
		String result = "";
		Workbook workBook = null;
		if(importDefinition.getImportColumns() !=null && importDefinition.getImportColumns().size()>0&&StringUtils.isNotEmpty(importDefinition.getName())){
			if(fileName.endsWith(".xls")){
				workBook = new HSSFWorkbook(new FileInputStream(file));
				result = importExcelData(importDefinition,workBook);
			}else if(fileName.endsWith(".xlsx")){
				workBook = new XSSFWorkbook(new FileInputStream(file));
				result = importExcelData(importDefinition,workBook);
			}else{
				result = importTextData(file,importDefinition);
			}
		}else{
			result = "请填写数据表名和录入导入列！";
		}
		if(StringUtils.isEmpty(result)){
			result="导入成功！";
		}
		return result;
	}
	
	private String importTextData(File file, ImportDefinition importDefinition) throws Exception{
		String result = "";
		if(ImportType.TXT_DIVIDE.equals(importDefinition.getImportType())){
			if(StringUtils.isEmpty(importDefinition.getDivide())){
				result = "字段信息中的 分隔符没有填写！\n";
			}else{
				result = importTxtDivideData(file,importDefinition);
			}
		}else if(ImportType.TXT.equals(importDefinition.getImportType())){
			String validateResult=DataImporterServiceImpl.validateImportTxtData(importDefinition);
			if(StringUtils.isEmpty(validateResult)){
				result = importTxtData(file,importDefinition);
			}else{
				result = validateResult;
			}
		}else{
			result = "编号为 "+importDefinition.getCode()+" 的导入定义中没有选择 导入类型 ！";
		}
		return result;
	}
	
	/**
	 * 固定长度文本导入
	 * @param file
	 * @param importDefinition
	 * @return
	 * @throws Exception
	 */
	private String importTxtData(File file, ImportDefinition importDefinition) throws Exception{
		FileInputStream fis= new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis,ZipUtils.prexEncoding(file.getPath()));
		BufferedReader br = new BufferedReader(isr);
		String line="";
		List<String> fileContent = new ArrayList<String>();
		int i=0;
		while ((line=br.readLine())!=null){
			if(i>0)
				fileContent.add(line);
			i++;
		}
		br.close();
		isr.close();
		fis.close();
		
		String result = "";
		if(ImportWay.SUCCESS.equals(importDefinition.getImportWay())){//所有数据正确后导入
			result = validateImport(fileContent,importDefinition);
			if(StringUtils.isEmpty(result)){
				insertIntoTxtData(fileContent,importDefinition);
				result="导入成功！";
			}else{
				return result;
			}
		}else if(ImportWay.ONLY_SUCCESS.equals(importDefinition.getImportWay())){//只导入正确数据
			result=onlyImportRightData(fileContent,importDefinition);
		}else{//有错误数据就不导入
			result=haveErrorNotImport(fileContent,importDefinition);
		}
		return result;
	}
	
	/**
	 * 有错误数据就不导入
	 * @param fileContent
	 * @param importDefinition
	 * @param callBack
	 */
	private String haveErrorNotImport(List<String> fileContent,ImportDefinition importDefinition) {
		String result="";
		//导入定义中固定长度的总和
		Integer rowWidth=totalWidth(importDefinition.getId());
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
							return validateResult;
					}
				}else{
					return "第"+rowNum+"行数据长度为"+rowContent.length()+"不等于导入定义中固定长度的总和"+rowWidth+"！";
				}
				rowNum++;
			}
		}else{
			return "导入的文件中没有数据！";
		}
		insertIntoTxtData(fileContent,importDefinition);
		return result;
	}
	
	/**
	 * 导入验证
	 */
	private String validateImport(List<String> fileContent,ImportDefinition importDefinition) {
		//导入定义中固定长度的总和
		Integer rowWidth=totalWidth(importDefinition.getId());
		StringBuilder result=new StringBuilder();
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
							result.append(validateResult);
					}
				}else{
					result.append("第"+rowNum+"行数据长度为"+rowContent.length()+"不等于导入定义中固定长度的总和"+rowWidth+"！\n");
				}
				
				rowNum++;
			}
		}else{
			result.append("导入的文件中没有数据！");
		}
		return result.toString();
	}
	
	
	/**
	 * 只导入正确数据
	 * @param fileContent
	 * @param importDefinition
	 * @param callBack
	 */
	private String onlyImportRightData(List<String> fileContent,ImportDefinition importDefinition) {
		//导入定义中固定长度的总和
		Integer rowWidth=totalWidth(importDefinition.getId());
		StringBuilder result1=new StringBuilder();
		int rowNum=1;//文件行数
		if(fileContent != null && fileContent.size()>0){
			for (String row : fileContent) {
				String rowContent=row;
				StringBuilder result=new StringBuilder();
				if(rowWidth==rowContent.length()){
					for(ImportColumn importColumn:importDefinition.getImportColumns()){
						String columnContent=rowContent.substring(0,importColumn.getWidth());
						rowContent=rowContent.substring(importColumn.getWidth(), rowContent.length());
						String validateResult=validateColumnDataType(importColumn,columnContent.trim(),rowNum);
						if(StringUtils.isNotEmpty(validateResult))
							result.append(validateResult);
					}
				}else{
					result.append("第"+rowNum+"行数据长度为"+rowContent.length()+"不等于导入定义中固定长度的总和"+rowWidth+"！\n");
				}
				
				if(StringUtils.isEmpty(result.toString())){//该行没有错误信息
					importTxtRowData(row,importDefinition);
				}else{//该行有错误信息
					result1.append(result.toString());
				}
				rowNum++;
			}
		}else{
			result1.append("导入的文件中没有数据！\n");
		}
		if(StringUtils.isEmpty(result1.toString())){
			result1.append("导入文件成功！\n");
		}
		return result1.toString();
	}
	
	private void importTxtRowData(String rowContent,ImportDefinition importDefinition){
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
		saveSingleRowData(rowValue, importDefinition);
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

	//INSERT INTO BS_IMPORT_DEFINITION(company_id,creator,created_time) VALUES(8,'test1','2012-09-26 16:00:01');
	private void insertIntoTxtData(List<String> fileContent,ImportDefinition importDefinition) {
		for (String row : fileContent) {
			importTxtRowData(row,importDefinition);
		}
	}
	
	/**
	 * 分隔符分隔的文本导入
	 * @param file
	 * @param importDefinition
	 * @return
	 * @throws Exception
	 */
	
	private String importTxtDivideData(File file,ImportDefinition importDefinition) throws Exception{
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
			result = validateSeparatorsImport(fileContent,importDefinition);
			if(StringUtils.isEmpty(result)){
				insertIntoSeparatorsData(fileContent,importDefinition);
			}else{
				return result;
			}
		}else if(ImportWay.ONLY_SUCCESS.equals(importDefinition.getImportWay())){//只导入正确数据
			result=onlyImportTxtDivideRightData(fileContent, importDefinition);
		}else{
			result=haveErrorNotImportTxtDivide(fileContent, importDefinition);
		}
		return result;
	}
	
	/**
	 * 有错误数据就不导入
	 * @param fileContent
	 * @param importDefinition
	 */
	private String haveErrorNotImportTxtDivide(List<String[]> fileContent,ImportDefinition importDefinition) {
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
						return validateResult;
					i++;
				}
				rowNum++;
			}
		}else{
			return "导入的文件中没有数据！";
		}
		insertIntoSeparatorsData(fileContent,importDefinition);
		return result;
	}
	
	/**
	 * 只导入正确数据
	 * @param fileContent
	 * @param importDefinition
	 * @param callBack
	 */
	private String onlyImportTxtDivideRightData(List<String[]> fileContent,ImportDefinition importDefinition) {
		StringBuilder result1=new StringBuilder();
		int rowNum=1;//文件行数
		String relevance =validateRelevance(importDefinition);
		if(StringUtils.isNotEmpty(relevance)){
			return relevance;
		}
		if(fileContent != null && fileContent.size()>0){
			for (String[] row : fileContent) {
				StringBuilder result=new StringBuilder();
				int i=0;
				for(ImportColumn importColumn:importDefinition.getImportColumns()){
					String validateResult=validateColumnDataType(importColumn,row[i].trim(),rowNum);
					if(StringUtils.isNotEmpty(validateResult))
						result.append(validateResult);
					i++;
				}
				if(StringUtils.isEmpty(result.toString())){//该行没有错误信息
					insertIntoSeparatorsRowData(row, importDefinition);
				}else{//该行有错误信息
					result1.append(result.toString());
				}
				rowNum++;
			}
		}else{
			result1.append("导入的文件中没有数据！\n");
		}
		if(StringUtils.isEmpty(result1.toString())){
			result1.append("导入文件成功！\n");
		}
		return result1.toString();
	}
	
	/**
	 * 分隔符分隔文本插入一行数据
	 * 返回值不为空说明没有给要导入的数据表名，为空说明插入成功
	 */
	private void insertIntoSeparatorsRowData(String[] rowContent,ImportDefinition importDefinition) {
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
		saveSingleRowData(rowValue, importDefinition);
	}
	
	private void insertIntoSeparatorsData(List<String[]> fileContent,ImportDefinition importDefinition) {
		for (String[] row : fileContent) {
			insertIntoSeparatorsRowData(row, importDefinition);
		}
	}
	private String validateSeparatorsImport(List<String[]> fileContent,ImportDefinition importDefinition) {
		StringBuilder result=new StringBuilder();
		int rowNum=1;//文件行数
		String relevance =validateRelevance(importDefinition);
		if(StringUtils.isNotEmpty(relevance)){
			result.append(relevance);
		}
		if(fileContent != null && fileContent.size()>0){
			for (String row[] : fileContent) {
				int i=0;
				for(ImportColumn importColumn:importDefinition.getImportColumns()){
					String validateResult=validateColumnDataType(importColumn,row[i].trim(),rowNum);
					if(StringUtils.isNotEmpty(validateResult))
						result.append(validateResult);
					i++;
				}
				rowNum++;
			}
		}else{
			result.append("导入的文件中没有数据！\n");
		}
		return result.toString();
	}
	
	/**
	 * Excel导入
	 * @param importDefinition
	 * @param workBook
	 * @return
	 * @throws Exception
	 */
	private String importExcelData(ImportDefinition importDefinition, Workbook workBook) throws Exception{
		String result = "";
		if(workBook!=null){
			int numberOfSheets = workBook.getNumberOfSheets();
			for(int i=0;i<numberOfSheets;i++){
				Sheet sheet = workBook.getSheetAt(i);
				result = resolvingExcelData(sheet,importDefinition);
			}
		}else{
			result="导入文件中没有内容！";
		}
		return result;
	}
	
	/**
	 * 解析数据
	 * @param sheet
	 * @param importDefinition
	 */
	private String resolvingExcelData(Sheet sheet,ImportDefinition importDefinition) {
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
					columns.add(DataImporterServiceImpl.getCellValue(cell)==null?"":DataImporterServiceImpl.getCellValue(cell).toString());
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
			result=resolvingImportData(list,importDefinition);
		}else{
			result="编号为 "+importDefinition.getCode()+" 的导入文件中没有内容！\n";
		}
		return result;
	}
	
	private String resolvingImportData(List<String[]> list,ImportDefinition importDefinition){
		String result="";
		if(ImportWay.SUCCESS.equals(importDefinition.getImportWay())){//所有数据正确后导入
			result = validateExcelImport(list,importDefinition);
			if(StringUtils.isEmpty(result)){
				insertIntoExcelData(list,importDefinition);
			}else{
				return result;
			}
		}else if(ImportWay.ONLY_SUCCESS.equals(importDefinition.getImportWay())){//只导入正确数据
			result = onlyImportExcelRightData(list, importDefinition);
		}else{
			result = haveErrorNotImportExcel(list, importDefinition);
		}
		return result;
	}
	
	/**
	 * 有错误数据就不导入
	 * @param fileContent
	 * @param importDefinition
	 * @param callBack
	 */
	private String haveErrorNotImportExcel(List<String[]> fileContent,ImportDefinition importDefinition) {
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
						return validateResult;
					i++;
				}
				rowNum++;
			}
		}else{
			return "导入的文件中没有数据！";
		}
		insertIntoExcelData(fileContent,importDefinition);
		return result;
	}
	
	private void insertIntoExcelData(List<String[]> fileContent,ImportDefinition importDefinition) {
		for (String[] row : fileContent) {
			insertIntoSeparatorsRowData(row,importDefinition);
		}
	}
	
	/**
	 * 只导入正确数据
	 * @param fileContent
	 * @param importDefinition
	 * @param callBack
	 */
	private String onlyImportExcelRightData(List<String[]> fileContent,ImportDefinition importDefinition) {
		StringBuilder result1 = new StringBuilder();
		int rowNum=1;//文件行数
		String relevance =validateRelevance(importDefinition);
		if(StringUtils.isNotEmpty(relevance)){
			return relevance+"！";
		}
		if(fileContent != null && fileContent.size()>0){
			for (String[] row : fileContent) {
				StringBuilder result = new StringBuilder();
				int i=0;
				for(ImportColumn importColumn:importDefinition.getImportColumns()){
					String validateResult=validateColumnDataType(importColumn,row[i].trim(),rowNum);
					if(StringUtils.isNotEmpty(validateResult))
						result.append(validateResult);
					i++;
				}
				if(StringUtils.isEmpty(result.toString())){//该行没有错误信息
					insertIntoSeparatorsRowData(row,importDefinition);
				}else{//该行有错误信息
					result1.append(result.toString());
				}
				rowNum++;
			}
		}else{
			result1.append("导入的文件中没有数据！\n");
		}
		if(StringUtils.isEmpty(result1.toString())){
			result1.append("导入文件成功！\n");
		}
		return result1.toString();
	}
	
	private void saveSingleRowData(String[] rowValue,ImportDefinition importDefinition) {
		List<Object[]> relevanceField=new ArrayList<Object[]>();
		List<Object[]> businessField=new ArrayList<Object[]>();
		List<Object[]> field=new ArrayList<Object[]>();
		int i=0;
		for(ImportColumn importColumn:importDefinition.getImportColumns()){
			packagingField(relevanceField,businessField,field,importColumn,rowValue[i]);
			i++;
		}
		if(relevanceField.size()>0){//导入子表
			relevanceData(relevanceField,field,businessField,importDefinition);
		}else{//导入主表
			importData(businessField, field, null, null,importDefinition.getName());
		}
	}
	
	private void packagingField( List<Object[]> relevanceField,List<Object[]> businessField,List<Object[]> field,ImportColumn importColumn,String value){
		if(BusinessType.RELEVANCE_FIELD.equals(importColumn.getBusinessType())){//是关联字段
			relevanceField.add(getFieldMessage(importColumn, value));
		}else if(BusinessType.BUSINESS_FIELD.equals(importColumn.getBusinessType())){//是业务字段
			businessField.add(getFieldMessage(importColumn, value));
		}else{
			field.add(getFieldMessage(importColumn, value));
		}
	}
	private Object[] getFieldMessage(ImportColumn importColumn,String value){
		Object[] obj={importColumn.getName(),value,importColumn.getDataType()};
		return obj;
	}
	
	private void importData(List<Object[]> businessField,List<Object[]> field,Long fkValue,String fkName,String tableName){
		if(businessField.size()>0){
			 businessData(businessField,field,fkValue,fkName,tableName);
		 }else{
			 insertIntoData(field,fkValue,fkName,tableName);
		 }
	}
	
	private void relevanceData(List<Object[]> relevanceField,List<Object[]> field,List<Object[]> businessField,ImportDefinition importDefinition) {
		String condition="";
		for(Object[] obj:relevanceField){
			if(StringUtils.isNotEmpty(condition)){
				condition+=" and ";
			}
			condition+="o."+obj[0]+"=";
			DataType dataType=DataType.valueOf(obj[2].toString());
			if(DefaultDataImporterCallBack.fieldType(dataType, obj[1])){
				condition+=obj[1];
			}else {
				condition+="'"+obj[1]+"'";
			}
		}
		List fkId=jdbcDao.excutionSql("select o.id from "+importDefinition.getRelevanceName()+" o where "+condition);
		if(fkId!=null&&fkId.size()>0){//找到需要主表
			Long id=Long.valueOf(((Map)fkId.get(0)).get("id").toString());
			importData(businessField, field, id,importDefinition.getForeignKey(),importDefinition.getName());
		}else{//没有找到需要主表
			importData(businessField, field, null, null,importDefinition.getName());
		}
	}
	
	private void businessData(List<Object[]> businessField,List<Object[]> field,Long fkValue,String fkName,String tableName) {
		String condition="";
		for(Object[] obj:businessField){
			if(StringUtils.isNotEmpty(condition)){
				condition+=" and ";
			}
			condition+="o."+obj[0]+"=";
			DataType dataType=DataType.valueOf(obj[2].toString());
			if(DefaultDataImporterCallBack.fieldType(dataType, obj[1])){
				condition+=obj[1];
			}else {
				condition+="'"+obj[1]+"'";
			}
		}
		List fkId=jdbcDao.excutionSql("select o.id from "+tableName+" o where "+condition);
		if(fkId!=null && fkId.size()>0){
			for(int i=0;i<fkId.size();i++){
				Long id=Long.valueOf(((Map)fkId.get(i)).get("id").toString());
				updateData(field,id,tableName);
			}
		}else{
			List<Object[]> newField=field;
			for(Object[] obj:businessField){
				newField.add(obj);
			}
			insertIntoData(newField,fkValue,fkName,tableName);
		}
	}
	
	/**
	 * 更新数据
	 * @param field
	 */
	private void updateData(List<Object[]> field,Long id,String tableName) {
		StringBuilder sql=new StringBuilder("UPDATE ");
		sql.append(tableName);
		sql.append(" SET ");
		StringBuilder condition=new StringBuilder();
		List<Object> values=new ArrayList<Object>();
		for(Object[] obj:field){
			if(StringUtils.isEmpty(obj[1].toString()))continue;
			if(StringUtils.isNotEmpty(condition.toString())){
				condition.append(",");
			}
			condition.append(obj[0]).append("=?");
			DataType dataType=DataType.valueOf(obj[2].toString());
			values.add(DefaultDataImporterCallBack.getValueByType(dataType,obj[1]));
		}
		sql.append(condition.toString());
		sql.append(" WHERE id=?");
		values.add(id);
		jdbcDao.updateTable(sql.toString(),values.toArray());
	}

	/**
	 * 插入数据
	 * @param field
	 */
	private void insertIntoData(List<Object[]> field,Long fkValue,String fkName,String tableName) {
		StringBuilder sql=new StringBuilder("INSERT INTO ");
		StringBuilder name=new StringBuilder();
		StringBuilder value=new StringBuilder();
		List<Object> values=new ArrayList<Object>();
		if("oracle".equals(PropUtils.getDataBase())){
			name.append("id");
			value.append("?");
			values.add(jdbcDao.getSequenceValue("HIBERNATE_SEQUENCE"));
		}
		sql.append(tableName).append("(");
		for(Object[] obj:field){
			if(StringUtils.isEmpty(obj[1].toString()))continue;
			if(StringUtils.isNotEmpty(name.toString())){
				name.append(",");
				value.append(",");
			}
			name.append(obj[0]);
			value.append("?");
			DataType dataType=DataType.valueOf(obj[2].toString());
			values.add(DefaultDataImporterCallBack.getValueByType(dataType,obj[1]));
		}
		if(fkValue!=null){
			name.append(",").append(fkName);
			value.append(",?");
			values.add(fkValue);
		}
		if(!name.toString().contains("company_id")){
			name.append(",company_id");
			value.append(",?");
			values.add(ContextUtils.getCompanyId());
		}
		sql.append(name.toString());
		sql.append(") VALUES(");
		sql.append(value.toString());
		sql.append(")");
		jdbcDao.updateTable(sql.toString(),values.toArray());
	}
	
	private String validateRelevance(ImportDefinition importDefinition){
		for(ImportColumn importColumn:importDefinition.getImportColumns()){
			if(BusinessType.RELEVANCE_FIELD.equals(importColumn.getBusinessType()) && (StringUtils.isEmpty(importDefinition.getRelevanceName()) || StringUtils.isEmpty(importDefinition.getForeignKey()))){
				return "基本信息中的关联表名或外键没有填写！\n";
			}
		}
		return "";
	}
	private String validateExcelImport(List<String[]> fileContent,ImportDefinition importDefinition) {
		StringBuilder result = new StringBuilder();
		int rowNum=1;//文件行数
		String relevance =validateRelevance(importDefinition);
		if(StringUtils.isNotEmpty(relevance)){
			result.append(relevance);
		}
		if(fileContent != null && fileContent.size()>0){
			for (String[] row : fileContent) {
				int i=0;
				for(ImportColumn importColumn:importDefinition.getImportColumns()){
					String validateResult=validateColumnDataType(importColumn,row[i].trim(),rowNum);
					if(StringUtils.isNotEmpty(validateResult))
						result.append(validateResult);
					i++;
				}
				rowNum++;
			}
		}else{
			result.append("导入的文件中没有数据！\n");
		}
		return result.toString();
	}
	
	private String validateColumnDataType(ImportColumn importColumn,String columnContent,int rowNum){
		String validateResult="";
		if(importColumn.getNotNull()){
			if(StringUtils.isEmpty(importColumn.getDefaultValue())){//该字段不能为空，并且没有默认值
				if(StringUtils.isEmpty(columnContent)){
					validateResult="第"+rowNum+"行"+importColumn.getAlias()+"为空！\n";
				}else{
					validateResult=DataImporterServiceImpl.validateDataType(importColumn.getDataType(),columnContent,rowNum,importColumn.getAlias(),false);
				}
			}else if(StringUtils.isNotEmpty(importColumn.getDefaultValue())){//该字段不能为空，并且有默认值
				//验证默认值的数据类型是否正确
				validateResult=DataImporterServiceImpl.validateDataType(importColumn.getDataType(),importColumn.getDefaultValue().trim(),0,importColumn.getAlias(),true);
			}
		}else{
			validateResult=DataImporterServiceImpl.validateDataType(importColumn.getDataType(),columnContent,rowNum,importColumn.getAlias(),false);
		}
		return validateResult;
	}
	
	/**
	 * 根据编码和ID获得编码相同且ID不同的导入定义
	 * @param code
	 * @param id
	 * @return
	 */
	@Transactional(readOnly=true)
	public ImportDefinition getImportDefinitionByCode(String code, Long id) {
		return importDefinitionDao.getImportDefinitionByCode(code, id);
	}

	public void backup(String systemIds, Long companyId,FileConfigModel fileConfig) {
		try {
			File file = new File(fileConfig.getExportRootPath()+"/"+fileConfig.getExportPath()+"/"+fileConfig.getFilename()+".xls");
			OutputStream out=null;
			out=new FileOutputStream(file);
			exportImportDefinition(out);
		}catch (Exception e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}
	}
	
	private void exportImportDefinition(OutputStream fileOut){
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_IMPORT_DEFINITION']");
		List<DataSheetConfig> colConfs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_IMPORT_COLUMN']");
		wb = new HSSFWorkbook();
		//导入定义excel信息
    	HSSFSheet sheet=wb.createSheet("BS_IMPORT_DEFINITION");
        HSSFRow row = sheet.createRow(0);
        
        dataHandle.getFileHead(wb,row,confs);
        //导入列excel信息
        HSSFSheet colSheet=wb.createSheet("BS_IMPORT_COLUMN");
        HSSFRow colRow = colSheet.createRow(0);
        dataHandle.getFileHead(wb,colRow,colConfs);
        List<ImportDefinition> importDefinitions=importDefinitionDao.getAllImportDefinition();
		for(ImportDefinition importDefinition:importDefinitions){
			importDefinitionInfo(importDefinition,sheet,colSheet,confs,colConfs);
		}
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}

	private void importDefinitionInfo(ImportDefinition importDefinition,
			HSSFSheet sheet, HSSFSheet colSheet, List<DataSheetConfig> confs,
			List<DataSheetConfig> colConfs) {
		if(importDefinition!=null){
			//导入定义的信息
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
        		DataSheetConfig conf=confs.get(i);
        		if(!conf.isIgnore()){
        			dataHandle.setFieldValue(conf,i,rowi,importDefinition);
        		}
        	}
			//导入列的信息
			importColumnInfo(importDefinition,colSheet,colConfs);
		}
	}

	private void importColumnInfo(ImportDefinition importDefinition,
			HSSFSheet colSheet, List<DataSheetConfig> colConfs) {
		List<ImportColumn> columns=importColumnDao.getImportColumnByImportId(importDefinition.getId());
		for(ImportColumn col:columns){
			HSSFRow colrowi = colSheet.createRow(colSheet.getLastRowNum()+1);
			for(int i=0;i<colConfs.size();i++){
        		DataSheetConfig conf=colConfs.get(i);
        		if(!conf.isIgnore()){
        			if("importCode".equals(conf.getFieldName())){
        				HSSFCell cell = colrowi.createCell(i);
    					cell.setCellValue(importDefinition.getCode());
        			}else{
        				dataHandle.setFieldValue(conf,i,colrowi,col);
        			}
        		}
        	}
		}
	}

	public void restore(Long companyId, FileConfigModel fileConfig,String... imatrixInfo) {
		File file =null;
		if(StringUtils.isNotEmpty(fileConfig.getFilename())){
			file=new File(fileConfig.getImportRootPath()+"/"+fileConfig.getImportPath()+"/"+fileConfig.getFilename()+".xls");
			if(file.exists()){
				importDefinitionDatas(file, companyId);
			}
		}
	}
	
	private void importDefinitionDatas(File file,Long companyId){
		List<DataSheetConfig> confs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_IMPORT_DEFINITION']");
		List<DataSheetConfig> colConfs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_IMPORT_COLUMN']");
		Map<String,Integer> map=dataHandle.getIdentifier(confs);
		Map<String,Integer> colMap=dataHandle.getIdentifier(colConfs);
		//创建时间,创建人姓名,创建人id,公司id
		FileInputStream fis=null;
 		InputStreamReader fr=null;
 		BufferedReader br=null;
 		try{
 			fis=new FileInputStream(file);
 			HSSFWorkbook wb=new HSSFWorkbook(fis);
 			HSSFSheet sheet=wb.getSheet("BS_IMPORT_DEFINITION");
 			if(ContextUtils.getCompanyId()==null){
 				if(companyId==null){
 					List<Company> companys=companyManager.getCompanys();
 					for(Company company:companys){
 						ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
 						ParameterUtils.setParameters(parameters);
 						importDefinitionData(sheet,confs,map);
 					}
 				}else{
 					ThreadParameters parameters=new ThreadParameters(companyId,null);
						ParameterUtils.setParameters(parameters);
						importDefinitionData(sheet,confs,map);
 				}
 				dataHandle.clearCompanyId();
 			}else{
 				importDefinitionData(sheet,confs,map);
 			}
 			HSSFSheet colSheet=wb.getSheet("BS_IMPORT_COLUMN");
 			if(ContextUtils.getCompanyId()==null){
 				List<Company> companys=companyManager.getCompanys();
 				for(Company company:companys){
 					ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
 					ParameterUtils.setParameters(parameters);
 					importColumnData(colSheet,colConfs,colMap);
 				}
 				dataHandle.clearCompanyId();
 			}else{
 				importColumnData(colSheet,colConfs,colMap);
 			}
 		} catch (FileNotFoundException e) {
 			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
 			try{
	 			if(br!=null)br.close();
	 			if(fr!=null)fr.close();
	 			if(fis!=null)fis.close();
 			}catch(IOException ep){
 				log.debug(PropUtils.getExceptionInfo(ep));
 			}
 		}
	}
	private void importDefinitionData(HSSFSheet sheet,List<DataSheetConfig> confs,Map<String,Integer> map){
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			HSSFRow row =sheet.getRow(i);
			if(sheet.getRow(i)!=null){
				addImportDefinition(confs,row,map);
			}
		}
	}
	private void addImportDefinition(List<DataSheetConfig> confs,HSSFRow row,Map<String,Integer> map ){
		Integer index=map.get("code");
		String code=row.getCell(index).getStringCellValue();//导入定义编号
		
		ImportDefinition importDefinition=importDefinitionDao.getImportDefinitionByCode(code);
		if(importDefinition==null){
			importDefinition=new ImportDefinition();
		}
		importDefinition.setCode(code);
		for(int j=0;j<confs.size();j++){
			DataSheetConfig conf=confs.get(j);
			if(!conf.isIgnore()){
				String fieldName=conf.getFieldName();
				String value=null;
				if(row.getCell(j)!=null){
					value=row.getCell(j).getStringCellValue();
				}
				if(StringUtils.isNotEmpty(value)){//导入数据
					dataHandle.setValue(importDefinition,fieldName,conf.getDataType(),value,conf.getEnumName());
				}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
					dataHandle.setValue(importDefinition,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
				}
			}
		}
		importDefinition.setCreatedTime(new Date());
		importDefinition.setCreator(ContextUtils.getLoginName());
		importDefinition.setCreatorName(ContextUtils.getUserName());
		importDefinition.setCompanyId(ContextUtils.getCompanyId());
		importDefinitionDao.save(importDefinition);
	}
	
	private void importColumnData(HSSFSheet colSheet,List<DataSheetConfig> colConfs,Map<String,Integer> colMap){
		int colFirstRowNum = colSheet.getFirstRowNum();
			int colRowNum=colSheet.getLastRowNum();
			for(int i=colFirstRowNum+1;i<=colRowNum;i++){
				HSSFRow row =colSheet.getRow(i);
				if(colSheet.getRow(i)!=null){
					addImportColumns(colConfs,row,colMap);
				}
			}
	}
	
	private void addImportColumns(List<DataSheetConfig> confs,HSSFRow row,Map<String,Integer> map){
		String code=row.getCell(map.get("importCode")).getStringCellValue();//导入定义编号
		String columnName=row.getCell(map.get("name")).getStringCellValue();//字段名称
		
		ImportDefinition importDefinition=importDefinitionDao.getImportDefinitionByCode(code);
		ImportColumn column=importColumnDao.getImportColumn(importDefinition.getId(),columnName);
		if(column==null){//该字段不存在，则新建
			column=new ImportColumn();
		}
		if(column!=null){
			for(int j=0;j<confs.size();j++){
				DataSheetConfig conf=confs.get(j);
				if(!conf.isIgnore()){
					String fieldName=conf.getFieldName();
					String value=null;
					if(row.getCell(j)!=null){
						value=row.getCell(j).getStringCellValue();
					}
					if("importCode".equals(conf.getFieldName())){
						column.setImportDefinition(importDefinition);
        			}else{
        				if(StringUtils.isNotEmpty(value)){//导入数据
        					dataHandle.setValue(column,fieldName,conf.getDataType(),value,conf.getEnumName());
        				}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
        					dataHandle.setValue(column,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
        				}
        			}
				}
			}
			column.setCompanyId(ContextUtils.getCompanyId());
			column.setCreatedTime(new Date());
			column.setCreator(ContextUtils.getLoginName());
			column.setCreatorName(ContextUtils.getUserName());
			importColumnDao.save(column);
		}
	}

	/**
	 * 根据导入定义的id获得导入列中固定长度的总和
	 * @param importDefinitionId
	 * @return
	 */
	public Integer totalWidth(Long importDefinitionId) {
		List<Integer> widthList =  importColumnDao.getColumnWidth(importDefinitionId);
		Integer totalWidth = 0;
		for(Integer itemAmount : widthList){
			totalWidth+=itemAmount;
		}
		return totalWidth;
	}

}
