package com.norteksoft.mms.form.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Service
@Transactional(readOnly = true)
public class SheetManager {
	private TableColumnManager tableColumnManager;
	@Autowired
	public void setTableColumnManager(TableColumnManager tableColumnManager) {
		this.tableColumnManager = tableColumnManager;
	}
	@Transactional(readOnly=false)
	public List<TableColumn> importIntoData(File file,DataTable dataTable)  throws IOException{
		List<TableColumn> columns=new ArrayList<TableColumn>();
		FileInputStream fileIn = null;

        try{
        	List<TableColumn> exsitColumns=tableColumnManager.getTableColumnByDataTableId(dataTable.getId());
        	for(TableColumn col:exsitColumns){
        		col.setDeleted(true);
        		tableColumnManager.saveColumn(col, false);
        	}
            fileIn = new FileInputStream(file);
            POIFSFileSystem fs = new POIFSFileSystem(fileIn);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row = null;
            TableColumn tableColumn = null;
            String val = null;
            for(int i=1;i<sheet.getLastRowNum()+1;i++){
            	row = sheet.getRow(i);
            	tableColumn = new TableColumn();
        		tableColumn.setName(row.getCell(0).getStringCellValue());
        		tableColumn.setDbColumnName(row.getCell(1).getStringCellValue());
        		tableColumn.setAlias(row.getCell(2).getStringCellValue());
        		tableColumn.setDataType(DataType.valueOf(row.getCell(3).getStringCellValue()));
        		tableColumn.setDefaultValue(row.getCell(4).getStringCellValue());
        		val=row.getCell(5).getStringCellValue();
        		tableColumn.setMaxLength(StringUtils.isEmpty(val)?null:Integer.parseInt(val));
        		tableColumn.setDisplayOrder(Integer.parseInt(getCellValue(row.getCell(6))));
        		tableColumn.setDataTableId(dataTable.getId());
        		tableColumnManager.saveColumn(tableColumn, false);
        		columns.add(tableColumn);
            }
        } finally {
            if (fileIn != null)
                fileIn.close();
        }
        return columns;
	}
	private String getCellValue(HSSFCell cell){
		String result = "";
		if(HSSFCell.CELL_TYPE_NUMERIC == cell.getCellType()){
			result = Double.valueOf(cell.getNumericCellValue()).intValue()+"";
		}else if(HSSFCell.CELL_TYPE_NUMERIC == cell.getCellType()){
			result = cell.getStringCellValue();
		}
		return result;
	}
	public void exportToExcel(OutputStream fileOut) throws IOException{
		HSSFWorkbook wb;
        try
        {
             wb = new HSSFWorkbook();
        	HSSFSheet sheet=wb.createSheet("table-colums");
            
            HSSFFont boldFont = wb.createFont();
            boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            HSSFCellStyle boldStyle = wb.createCellStyle();
            boldStyle.setFont(boldFont);
            
            HSSFRow row = sheet.createRow(0);
            HSSFCell cell0 = row.createCell(0);
            cell0.setCellValue("字段名称");
            cell0.setCellStyle(boldStyle);
            HSSFCell cell1 = row.createCell(1);
            cell1.setCellValue("列名");
            cell1.setCellStyle(boldStyle);
            HSSFCell cell2 = row.createCell(2);
            cell2.setCellValue("字段别名");
            cell2.setCellStyle(boldStyle);
            HSSFCell cell3 = row.createCell(3);
            cell3.setCellValue("字段类型");
            cell3.setCellStyle(boldStyle);
            HSSFCell cell4 = row.createCell(4);
            cell4.setCellValue("默认值");
            cell4.setCellStyle(boldStyle);
            HSSFCell cell5 = row.createCell(5);
            cell5.setCellValue("最大长度");
            cell5.setCellStyle(boldStyle);
            HSSFCell cell6 = row.createCell(6);
            cell6.setCellValue("显示顺序");
            cell6.setCellStyle(boldStyle);
            
            String value=Struts2Utils.getParameter("subTableVals");
    		String[] arr=value.split("=");
    		String jsonString=arr[1];
    		if(jsonString!=null&&StringUtils.isNotEmpty(jsonString.toString())){
    			getSheetContent(jsonString,sheet);
    		}
            wb.write(fileOut);
        }catch(IOException exception){
 		}
	}
	
	private void getSheetContent(String jsonString,HSSFSheet sheet){
		try{
			MapType mt = TypeFactory.defaultInstance().constructMapType(
					HashMap.class, String.class, String.class);
			CollectionType ct = TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, mt);
			List<Map<String,String>> prms = JsonParser.json2Object(ct, jsonString);
			
			for(int i=0;i<prms.size(); i++){
				HSSFRow rowi = sheet.createRow(i);
				Set<Entry<String, String>> set = prms.get(i).entrySet();
				for(Entry<String, String> en : set){
					if(!"id".equals(en.getKey())&&!"displayOrder".equals(en.getKey())){
						if(en.getValue()!=null){
							String value=en.getValue();
							if("name".equals(en.getKey())){
								HSSFCell celli0 = rowi.createCell(0);
								celli0.setCellValue(value);
								continue;
							}
							if("dbColumnName".equals(en.getKey())){
								HSSFCell celli1 = rowi.createCell(1);
			    	        	celli1.setCellValue(value);
			    	        	continue;
							}
			    	        if("alias".equals(en.getKey())){
			    	        	HSSFCell celli2 = rowi.createCell(2);
								celli2.setCellValue(value);
								continue;
			    	        }
			    	        if("dataType".equals(en.getKey())){
			    	        	HSSFCell celli3 = rowi.createCell(3);
				    	        celli3.setCellValue(value);
				    	        continue;
			    	        }
			    	        if("defaultValue".equals(en.getKey())){
			    	        	HSSFCell celli4 = rowi.createCell(4);
				    	        celli4.setCellValue(value);
				    	        continue;
			    	        }
			    	        if("maxLength".equals(en.getKey())){
			    	        	 HSSFCell celli5 = rowi.createCell(5);
				    	        celli5.setCellValue(value);
				    	        continue;
			    	        }
						}
					}
				}
				HSSFCell celli6 = rowi.createCell(6);
    	        celli6.setCellValue(i);
			}
		}catch (Exception e) {
		}
	}
}
