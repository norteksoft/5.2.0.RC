package com.norteksoft.product.api.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.options.entity.ImportColumn;
import com.norteksoft.bs.options.entity.ImportDefinition;
import com.norteksoft.bs.options.enumeration.BusinessType;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.jdbc.JdbcSupport;
import com.norteksoft.product.api.DataImporterCallBack;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;

@Service
@Transactional
public class DefaultDataImporterCallBack implements DataImporterCallBack{
	public static final SimpleDateFormat SIMPLEDATEFORMAT1 = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat SIMPLEDATEFORMAT2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String tableName;
	
	public DefaultDataImporterCallBack() {
		super();
	}
	public DefaultDataImporterCallBack(String tableName) {
		super();
		this.tableName = tableName;
	}

	public String afterValidate(List<String> results) {
		String str="";
		for(String result:results){
			str+=result+"！\n";
		}
		return str;
	}
	
	public boolean beforeSaveSingleRow(String[] rowValue,ImportDefinition importDefinition) {
		return true;
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
	
	public String saveSingleRow(String[] rowValue,ImportDefinition importDefinition) {
		if(ContextUtils.getCompanyId()==null){
			 return "no_company_id";
		 }
		 if(StringUtils.isEmpty(tableName)&&StringUtils.isEmpty(importDefinition.getName())){
			return "no_table_name";
		 }else if(StringUtils.isEmpty(tableName)&&StringUtils.isNotEmpty(importDefinition.getName())){
			 tableName=importDefinition.getName();
		 }
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
			 importData(businessField, field, null, null);
		 }
		return "";
	}
	
	private void importData(List<Object[]> businessField,List<Object[]> field,Long fkValue,String fkName){
		if(businessField.size()>0){
			 businessData(businessField,field,fkValue,fkName);
		 }else{
			 insertIntoData(field,fkValue,fkName);
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
			if(fieldType(dataType,obj[1])){
				condition+=obj[1];
			}else {
				condition+="'"+obj[1]+"'";
			}
		}
		if(((JdbcSupport)ContextUtils.getBean("jdbcDao")) == null) throw new RuntimeException("JdbcSupport为null");
		List fkId=((JdbcSupport)ContextUtils.getBean("jdbcDao")).excutionSql("select o.id from "+importDefinition.getRelevanceName()+" o where "+condition);
		if(fkId!=null&&fkId.size()>0){//找到需要主表
			Long id=Long.valueOf(((Map)fkId.get(0)).get("id").toString());
			importData(businessField, field, id,importDefinition.getForeignKey());
		}else{//没有找到需要主表
			importData(businessField, field, null, null);
		}
	}
	
	public static boolean fieldType(DataType dataType,Object columnContent){
		if(DataType.BOOLEAN.equals(dataType)||DataType.DOUBLE.equals(dataType)||DataType.FLOAT.equals(dataType)||DataType.LONG.equals(dataType)||DataType.AMOUNT.equals(dataType)||DataType.NUMBER.equals(dataType)||DataType.INTEGER.equals(dataType)){
			return true;
		}else if(DataType.ENUM.equals(dataType)&&columnContent.toString().matches("^-?\\d+$")){
			return true;
		}else{
			return false;
		}
	}
	
	public static Object getValueByType(DataType dataType,Object value){
		try {
			if(DataType.DATE.equals(dataType)){
				return SIMPLEDATEFORMAT1.parse(value.toString());
			}else if(DataType.TIME.equals(dataType)){
				return SIMPLEDATEFORMAT1.parse(value.toString());
			}else if(DataType.BOOLEAN.equals(dataType)){
				if("0".equals(value.toString())||"false".equals(value.toString())){
					return false;
				}else if("1".equals(value.toString())||"true".equals(value.toString())){
					return true;
				}
			}else if(DataType.DOUBLE.equals(dataType)||DataType.AMOUNT.equals(dataType)){
				return Double.valueOf(value.toString());
			}else if(DataType.FLOAT.equals(dataType)){
				return Float.valueOf(value.toString());
			}else if(DataType.LONG.equals(dataType)||DataType.NUMBER.equals(dataType)||DataType.INTEGER.equals(dataType)){
				return Integer.valueOf(value.toString());
			}else if(DataType.ENUM.equals(dataType)){
				if(value.toString().matches("^-?\\d+$")){
					return Integer.valueOf(value.toString());
				}else{
					return String.valueOf(value);
				}
			}else{
				return String.valueOf(value);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void businessData(List<Object[]> businessField,List<Object[]> field,Long fkValue,String fkName) {
		String condition="";
		for(Object[] obj:businessField){
			if(StringUtils.isNotEmpty(condition)){
				condition+=" and ";
			}
			condition+="o."+obj[0]+"=";
			DataType dataType=DataType.valueOf(obj[2].toString());
			if(fieldType(dataType,obj[1])){
				condition+=obj[1];
			}else {
				condition+="'"+obj[1]+"'";
			}
		}
		if(((JdbcSupport)ContextUtils.getBean("jdbcDao")) == null) throw new RuntimeException("JdbcSupport为null");
		List fkId=((JdbcSupport)ContextUtils.getBean("jdbcDao")).excutionSql("select o.id from "+tableName+" o where "+condition);
		if(fkId!=null && fkId.size()>0){
			for(int i=0;i<fkId.size();i++){
				Long id=Long.valueOf(((Map)fkId.get(i)).get("id").toString());
				updateData(field,id);
			}
		}else{
			List<Object[]> newField=field;
			for(Object[] obj:businessField){
				newField.add(obj);
			}
			insertIntoData(newField,fkValue,fkName);
		}
	}
	
	/**
	 * 更新数据
	 * @param field
	 */
	private void updateData(List<Object[]> field,Long id) {
		StringBuilder sql=new StringBuilder("UPDATE ");
		sql.append(tableName);
		sql.append(" t set ");
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
		if(((JdbcSupport)ContextUtils.getBean("jdbcDao")) == null) throw new RuntimeException("JdbcSupport为null");
		((JdbcSupport)ContextUtils.getBean("jdbcDao")).updateTable(sql.toString(),values.toArray());
	}

	/**
	 * 插入数据
	 * @param field
	 */
	private void insertIntoData(List<Object[]> field,Long fkValue,String fkName) {
		StringBuilder sql=new StringBuilder("INSERT INTO ");
		StringBuilder name=new StringBuilder();
		StringBuilder value=new StringBuilder();
		List<Object> values=new ArrayList<Object>();
		if("oracle".equals(PropUtils.getDataBase())){
			name.append("id");
			value.append("?");
			if(((JdbcSupport)ContextUtils.getBean("jdbcDao")) == null) throw new RuntimeException("JdbcSupport为null");
			values.add(((JdbcSupport)ContextUtils.getBean("jdbcDao")).getSequenceValue("HIBERNATE_SEQUENCE"));
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
			values.add(getValueByType(dataType,obj[1]));
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
		if(((JdbcSupport)ContextUtils.getBean("jdbcDao")) == null) throw new RuntimeException("JdbcSupport为null");
		((JdbcSupport)ContextUtils.getBean("jdbcDao")).updateTable(sql.toString(),values.toArray());
	}
	
	public void afterSaveSingleRow(String[] rowValue,ImportDefinition importDefinition) {
		
	}
	
	public void afterSaveAllRows() {
		
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
