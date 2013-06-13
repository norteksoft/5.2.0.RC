package com.norteksoft.mms.form.jdbc.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.util.Assert;

import com.norteksoft.product.api.entity.Department;
import com.norteksoft.mms.base.CommonStaticConstant;
import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.jdbc.JdbcSupport;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;

public class MySqlJdbc extends JdbcDaoSupport implements JdbcSupport {
	private Log log=LogFactory.getLog(MySqlJdbc.class);
	
	/**
	 * 创建表
	 * @param tableName
	 * @param fields
	 */
	public void createTable(String tableName,List<TableColumn> columns){
		StringBuilder str = new StringBuilder();
		str.append("CREATE TABLE ").append(tableName).append(" (");
		str.append("id bigint(20) NOT NULL AUTO_INCREMENT ,PRIMARY KEY ('id'),instance_id varchar(255)" );
		if(columns.size()>0){
			str.append(",");
		}else{
			str.append(" )");
		} 
		for(TableColumn tableColumn:columns){
			str.append(FORM_FIELD_PREFIX_STRING).append(tableColumn.getName()).append(getSqlType(tableColumn)).append(",");
		}
		str.replace(str.length()-1, str.length(), ") DEFAULT CHARSET=utf8;");
		this.getJdbcTemplate().execute(str.toString());
	}
	
	/**
	 * 创建表
	 * @param tableName
	 * @param fields
	 */
	public void createDefaultTable(String tableName,List<FormControl> columns){
		StringBuilder str = new StringBuilder();
		str.append("CREATE TABLE ").append(tableName).append(" (");
		str.append("id bigint(20) NOT NULL AUTO_INCREMENT ,PRIMARY KEY (id),instance_id varchar(255),company_id bigint(20),creator varchar(255) DEFAULT NULL,creator_name varchar(255) DEFAULT NULL,first_task_id bigint(20) DEFAULT NULL,process_state int(11) DEFAULT NULL,current_activity_name varchar(255) DEFAULT NULL,workflow_definition_name varchar(255) DEFAULT NULL,workflow_definition_id varchar(255) DEFAULT NULL,workflow_definition_code  varchar(255) DEFAULT NULL,workflow_definition_version int(11) DEFAULT NULL,form_id bigint(20) DEFAULT NULL,create_date datetime  DEFAULT NULL,creator_department varchar(255) DEFAULT NULL " );
		if(columns.size()>0){
			str.append(",");
		}else{
			str.append(" )");
		} 
		for(FormControl tableColumn:columns){
			str.append(FORM_FIELD_PREFIX_STRING).append(tableColumn.getName()).append(getSqlType(tableColumn)).append(",");
		}
		str.replace(str.length()-1, str.length(), ") DEFAULT CHARSET=utf8;");
		this.getJdbcTemplate().execute(str.toString());
	}
	
	private String getSqlType(TableColumn tableColumn){
		DataType type = tableColumn.getDataType();
		Integer length = tableColumn.getMaxLength();
		String dataType = "";
		if(type.equals(DataType.TEXT)){
			if(length != null){
				dataType = " VARCHAR("+ length +") DEFAULT NULL";
			}else{
				dataType = " VARCHAR(255) DEFAULT NULL";
			}
		}else if(type.equals(DataType.INTEGER)){
			dataType = " int(11) DEFAULT NULL";
		}else if(type.equals(DataType.LONG)){
			dataType = " bigint(20) DEFAULT NULL";
		}else if(type.equals(DataType.BOOLEAN)){
			dataType = " bit(1)  DEFAULT NULL";
		}else if(type.equals(DataType.DOUBLE)){
			dataType = " decimal(19,2) DEFAULT NULL";
		}else if(type.equals(DataType.FLOAT)){
			dataType = " float DEFAULT NULL";
		}else if(type.equals(DataType.TIME)){
			dataType = " datetime DEFAULT NULL";
		}else if(type.equals(DataType.DATE)){
			dataType = " datetime DEFAULT NULL";
		}else if(type.equals(DataType.CLOB)){
			dataType = " longtext";
		}else if(type.equals(DataType.AMOUNT)){
			dataType = " decimal(19,2) DEFAULT NULL";
		}else if(type.equals(DataType.NUMBER)){
			dataType = " bigint(20) DEFAULT NULL";
		}
		return dataType;
	}
	
	private String getSqlType(FormControl tableColumn){
		DataType type = tableColumn.getDataType();
		Integer length = tableColumn.getMaxLength();
		String dataType = "";
		if(type.equals(DataType.TEXT)){
			if(length != null){
				dataType = " VARCHAR("+ length +") DEFAULT NULL";
			}else{
				dataType = " VARCHAR(255) DEFAULT NULL";
			}
		}else if(type.equals(DataType.INTEGER)){
			dataType = " int(11) DEFAULT NULL";
		}else if(type.equals(DataType.LONG)){
			dataType = " bigint(20) DEFAULT NULL";
		}else if(type.equals(DataType.BOOLEAN)){
			dataType = " bit(1) DEFAULT NULL";
		}else if(type.equals(DataType.DOUBLE)){
			dataType = " decimal(19,2) DEFAULT NULL";
		}else if(type.equals(DataType.FLOAT)){
			dataType = " float DEFAULT NULL";
		}else if(type.equals(DataType.TIME)){
			dataType = " datetime DEFAULT NULL";
		}else if(type.equals(DataType.DATE)){
			dataType = " datetime DEFAULT NULL";
		}else if(type.equals(DataType.CLOB)){
			dataType = " text";
		}else if(type.equals(DataType.AMOUNT)){
			dataType = " decimal(19,2) DEFAULT NULL";
		}else if(type.equals(DataType.NUMBER)){
			dataType = " bigint(20) DEFAULT NULL";
		}
		return dataType;
	}
	/**
	 * 创建外键
	 * @param sequenceId
	 */
	public void createFK(String majorTableName,String childTableName ){
		StringBuilder sql = new StringBuilder();
		sql.append("alter table ").append(childTableName).append(" add ").append(TABLE_FK_PREFIX_STRING).append(majorTableName).append(" bigint(20) ");
		this.getJdbcTemplate().execute(sql.toString());
		this.getJdbcTemplate().execute("alter table "+childTableName+" add foreign key("+TABLE_FK_PREFIX_STRING+majorTableName+") references "+majorTableName+"(id) on delete cascade");
	}
	
	/**
	 * 追加一列
	 * @param tableName
	 * @param field
	 */
	public void addTableColumn(String tableName,TableColumn column){
		StringBuilder sql = new StringBuilder();
		sql.append("alter table ").append(tableName).append(" add ").append(FORM_FIELD_PREFIX_STRING).append(column.getName()).append(getSqlType(column));
		this.getJdbcTemplate().execute(sql.toString());
	}
	
	/**
	 * 修改一列的名称
	 * @param tableName
	 * @param column
	 */
	public void alterTableColumn(String tableName,TableColumn column, String newName){
		StringBuilder sql = new StringBuilder();
		sql.append("alter table ").append(tableName).append(" change ").append(FORM_FIELD_PREFIX_STRING).append(column.getName()).append(" ").append(FORM_FIELD_PREFIX_STRING).append(newName);
		this.getJdbcTemplate().execute(sql.toString());
	}
	
	/**
	 * 根据表名和记录ID查询对应数据，返回Map
	 */
	public Map getDataMap(String tableName, Long id) {
		String sql = "select * from " + tableName +" where id= ?" ;
		return  this.getJdbcTemplate().queryForMap(sql, new Long[] {id});
	}
	
	public Long updateTable(Map<String,String[]> parameterMap,FormView form,List<FormControl> fields,Long dataId){
		try {
			if(fields!=null && fields.size()>0){
				final List<Object> obj = new ArrayList<Object>();
				final List<String> dataTypes=new ArrayList<String>();
				boolean canUpateTabel=false;
				StringBuilder sql = new StringBuilder("UPDATE ").append(form.getDataTable().getName()).append(" SET ");
				String[] firstTaskIds = parameterMap.get(FIRST_TASK_ID);
				if(firstTaskIds!=null){
					joinStandardSql(Long.parseLong(firstTaskIds[0]),FIRST_TASK_ID,sql,obj);
					dataTypes.add(DataType.LONG.toString());
					canUpateTabel=true;
				}
				String[] currentActivityNames = parameterMap.get(CURRENT_ACTIVITY_NAME);
				if(currentActivityNames!=null){
					joinStandardSql(currentActivityNames[0],CURRENT_ACTIVITY_NAME,sql,obj);
					dataTypes.add(DataType.TEXT.toString());
					canUpateTabel=true;
				}
				String[] workflowDefinitionNames = parameterMap.get(WORKFLOW_DEFINITION_NAME);
				if(workflowDefinitionNames!=null){
					joinStandardSql(workflowDefinitionNames[0],WORKFLOW_DEFINITION_NAME,sql,obj);
					dataTypes.add(DataType.TEXT.toString());
					canUpateTabel=true;
				}
				String[] WorkflowDefinitionIds = parameterMap.get(WORKFLOW_DEFINITION_ID);
				if(WorkflowDefinitionIds!=null){
					joinStandardSql(WorkflowDefinitionIds[0],WORKFLOW_DEFINITION_ID,sql,obj);
					dataTypes.add(DataType.TEXT.toString());
					canUpateTabel=true;
				}
				String[] WorkflowDefinitionCodes = parameterMap.get(WORKFLOW_DEFINITION_CODE);
				if(WorkflowDefinitionCodes!=null){
					joinStandardSql(WorkflowDefinitionCodes[0],WORKFLOW_DEFINITION_CODE,sql,obj);
					dataTypes.add(DataType.TEXT.toString());
					canUpateTabel=true;
				}
				String[] WorkflowDefinitionVersions = parameterMap.get(WORKFLOW_DEFINITION_VERSION);
				if(WorkflowDefinitionVersions!=null){
					joinStandardSql(Integer.parseInt(WorkflowDefinitionVersions[0]),WORKFLOW_DEFINITION_VERSION,sql,obj);
					dataTypes.add(DataType.INTEGER.toString());
					canUpateTabel=true;
				}
				String[] formIds = parameterMap.get(FORM_ID);
				if(formIds!=null){
					joinStandardSql(Long.parseLong(formIds[0]),FORM_ID,sql,obj);
					dataTypes.add(DataType.LONG.toString());
					canUpateTabel=true;
				}
				String[] processStates = parameterMap.get(PROCESS_STATE);
				if(processStates!=null){
					joinStandardSql(Integer.parseInt(processStates[0]),PROCESS_STATE,sql,obj);
					dataTypes.add(DataType.INTEGER.toString());
					canUpateTabel=true;
				}
					for (FormControl field:fields) {
						String dbname=field.getName();
						if(parameterMap.get(dbname)!=null){
							String value ="";
							Object myobj=parameterMap.get(dbname);
							String str=myobj.getClass().getName();
							if(str.indexOf("[")==0){
								if(((String[])myobj).length>1){
									String text = Arrays.toString(((String[])myobj));//获得的值为[value]
									value=text.substring(1, text.length()-1);
								}else{
									value=((String[])myobj)[0];
								}
							}else{
								value=myobj+"";
							}
							if(field.getDataType()==DataType.DATE){
								dataTypes.add(field.getDataType().toString());
								if(StringUtils.isEmpty(value)) { joinSql(null,field.getName(),sql,obj);continue;};
								joinSql(SIMPLEDATEFORMAT1.parse(value),field.getName(),sql,obj);
							}else if(field.getDataType()==DataType.TIME){
								dataTypes.add(field.getDataType().toString());
								if(StringUtils.isEmpty(value)) { joinSql(null,field.getName(),sql,obj);continue;};
								if(!value.contains(":")){//是否包含时和分，如果不包含则添加00:00。数据获取控件用于存放字段值的控件为时间类型时会出现该情况。
									value = value+" 00:00";
								}
								joinSql(SIMPLEDATEFORMAT2.parse(value),field.getName(),sql,obj);
							}else if(field.getDataType()==DataType.BOOLEAN){
								dataTypes.add(field.getDataType().toString());
								if(StringUtils.isEmpty(value)) { joinSql(null,field.getName(),sql,obj);continue;};
								if("false".equals(value)||"0".equals(value)){
									joinSql(0x00,field.getName(),sql,obj);
								}else if("true".equals(value)||"1".equals(value)){
									joinSql(0x01,field.getName(),sql,obj);
								}
							}else if(field.getDataType()==DataType.DOUBLE){
								dataTypes.add(field.getDataType().toString());
								if(StringUtils.isEmpty(value)) { joinSql(null,field.getName(),sql,obj);continue;};
								joinSql(Double.valueOf(value),field.getName(),sql,obj);
							}else if(field.getDataType()==DataType.FLOAT){
								dataTypes.add(field.getDataType().toString());
								if(StringUtils.isEmpty(value)) { joinSql(null,field.getName(),sql,obj);continue;};
								joinSql(Float.valueOf(value),field.getName(),sql,obj);
							}else if(field.getDataType()==DataType.LONG){
								dataTypes.add(field.getDataType().toString());
								if(StringUtils.isEmpty(value)) { joinSql(null,field.getName(),sql,obj);continue;};
								joinSql(Integer.valueOf(value),field.getName(),sql,obj);
							}else if(field.getDataType()==DataType.AMOUNT){
								dataTypes.add(field.getDataType().toString());
								if(StringUtils.isEmpty(value)) { joinSql(null,field.getName(),sql,obj);continue;};
								joinSql(Double.valueOf(value),field.getName(),sql,obj);
							}else if(field.getDataType()==DataType.NUMBER){
								dataTypes.add(field.getDataType().toString());
								if(StringUtils.isEmpty(value)) { joinSql(null,field.getName(),sql,obj);continue;};
								joinSql(Integer.valueOf(value),field.getName(),sql,obj);
							}else if(field.getDataType()==DataType.INTEGER){
								dataTypes.add(field.getDataType().toString());
								if(StringUtils.isEmpty(value)){ joinSql(null,field.getName(),sql,obj);continue;}
								joinSql(Integer.valueOf(value),field.getName(),sql,obj);
							}else{
								String[] values = parameterMap.get(field.getName());
								String text = Arrays.toString(values);//获得的值为[value]
								//text.substring(1, text.length()-1),去掉text的前后[]
								joinSql(text.substring(1, text.length()-1),field.getName(),sql,obj);
								dataTypes.add(field.getDataType().toString());
							}
							canUpateTabel=true;
						}else{
							joinSql(null,field.getName(),sql,obj);
							dataTypes.add(field.getDataType().toString());
							canUpateTabel=true;
						}
				}
				sql.replace(sql.length()-1, sql.length(), " ");
				sql.append(" where id=?");
				obj.add(dataId);
				if(canUpateTabel){
					getJdbcTemplate().update(sql.toString(), new PreparedStatementSetter() {
						
						public void setValues(PreparedStatement ps) throws SQLException {
							for(int i=0;i<obj.size();i++){
								if(obj.get(i)==null){
									ps.setNull(i+1, getSqlTypeByDataType(dataTypes.get(i)));
								}else{
									ps.setObject(i+1, obj.get(i));
								}
							}
							
						}
					});
				}
			}
		} catch (NumberFormatException e) {
			 throw new RuntimeException("numberFormatException",e);
		} catch (DataAccessException e) {
			 throw new RuntimeException("update Exception",e);
		} catch (ParseException e) {
			throw new RuntimeException("dataFormatException",e);
		}
		return dataId;
		
	}
	public Long autoUpdateTable(Map<String,String[]> parameterMap,FormView form,List<FormControl> fields,Long dataId){
		try {
			if(fields!=null && fields.size()>0){
				List<Object> obj = new ArrayList<Object>();
				boolean canUpateTabel=false;
				StringBuilder sql = new StringBuilder("UPDATE ").append(form.getDataTable().getName()).append(" SET ");
				for (FormControl field:fields) {
					if(parameterMap.get(field.getName())!=null){
						if(field.getDataType()==DataType.DATE){
							String value = parameterMap.get(field.getName())[0];
							if(StringUtils.isEmpty(value)) continue;
							joinSql(SIMPLEDATEFORMAT1.parse(value),field.getName(),sql,obj);
						}else if(field.getDataType()==DataType.TIME){
							String value = parameterMap.get(field.getName())[0];
							if(StringUtils.isEmpty(value)) continue;
							joinSql(SIMPLEDATEFORMAT2.parse(value),field.getName(),sql,obj);
						}else if(field.getDataType()==DataType.BOOLEAN){
							String value = parameterMap.get(field.getName())[0];
							if(StringUtils.isEmpty(value)) continue;
							if("false".equals(value)||"0".equals(value)){
								joinSql(0x00,field.getName(),sql,obj);
							}else if("true".equals(value)||"1".equals(value)){
								joinSql(0x01,field.getName(),sql,obj);
							}
						}else if(field.getDataType()==DataType.DOUBLE){
							String value = parameterMap.get(field.getName())[0];
							if(StringUtils.isEmpty(value)) continue;
							joinSql(Double.valueOf(value),field.getName(),sql,obj);
						}else if(field.getDataType()==DataType.FLOAT){
							String value = parameterMap.get(field.getName())[0];
							if(StringUtils.isEmpty(value)) continue;
							joinSql(Float.valueOf(value),field.getName(),sql,obj);
						}else if(field.getDataType()==DataType.LONG){
							String value = parameterMap.get(field.getName())[0];
							if(StringUtils.isEmpty(value)) continue;
							joinSql(Integer.valueOf(value),field.getName(),sql,obj);
						}else if(field.getDataType()==DataType.AMOUNT){
							String value = parameterMap.get(field.getName())[0];
							if(StringUtils.isEmpty(value)) continue;
							joinSql(Double.valueOf(value),field.getName(),sql,obj);
						}else if(field.getDataType()==DataType.NUMBER){
							String value = parameterMap.get(field.getName())[0];
							if(StringUtils.isEmpty(value)) continue;
							joinSql(Integer.valueOf(value),field.getName(),sql,obj);
						}else if(field.getDataType()==DataType.INTEGER){
							String value = parameterMap.get(field.getName())[0];
							if(StringUtils.isEmpty(value)) continue;
							joinSql(Integer.valueOf(value),field.getName(),sql,obj);
						}else{
							String[] values = parameterMap.get(field.getName());
							String text = Arrays.toString(values);//获得的值为[value]
							//text.substring(1, text.length()-1),去掉text的前后[]
							joinSql(text.substring(1, text.length()-1),field.getName(),sql,obj);
						}
						canUpateTabel=true;
					}
				}
				sql.replace(sql.length()-1, sql.length(), " ");
				sql.append(" where id=?");
				obj.add(dataId);
				if(canUpateTabel){
					getJdbcTemplate().update(sql.toString(),obj.toArray());
				}
			}
		} catch (NumberFormatException e) {
			throw new RuntimeException("numberFormatException",e);
		} catch (DataAccessException e) {
			throw new RuntimeException("update Exception",e);
		} catch (ParseException e) {
			throw new RuntimeException("dataFormatException",e);
		}
		return dataId;
		
	}
	
	private  int getSqlTypeByDataType(String dataType){
		if(DataType.DATE.toString().equals(dataType)){
			return java.sql.Types.DATE;
		}else if(DataType.TIME.toString().equals(dataType)){
			return java.sql.Types.TIME;
		}else if(DataType.BOOLEAN.toString().equals(dataType)){
			return java.sql.Types.BIT;
		}else if(DataType.DOUBLE.toString().equals(dataType)){
			return java.sql.Types.DOUBLE;
		}else if(DataType.FLOAT.toString().equals(dataType)){
			return java.sql.Types.FLOAT;
		}else if(DataType.LONG.toString().equals(dataType)){
			return java.sql.Types.BIGINT;
		}else if(DataType.AMOUNT.toString().equals(dataType)){
			return java.sql.Types.FLOAT;
		}else if(DataType.NUMBER.toString().equals(dataType)){
			return java.sql.Types.INTEGER;
		}else if(DataType.INTEGER.toString().equals(dataType)){
			return java.sql.Types.INTEGER;
		}else{
			return java.sql.Types.VARCHAR;
		}
	}
	
	private void joinStandardSql(Object value,String enName,StringBuilder sql,List<Object> obj){
		sql.append( enName+"=? ,");
		obj.add(value);
	}
	private void joinSql(Object value,String enName,StringBuilder sql,List<Object> obj){
		sql.append(FORM_FIELD_PREFIX_STRING + enName+"=? ,");
		obj.add(value);
	}
	
	public Long insertTable(Map<String,String[]> parameterMap,FormView form,List<FormControl> fields){
		StringBuilder sql = new StringBuilder("INSERT INTO ").append(form.getDataTable().getName()).append("(");
		StringBuilder sql_values = new StringBuilder(" VALUES(");
		List<Object> obj = new ArrayList<Object>();
		String[] instanceIds = parameterMap.get(INSTANCE_ID);
		if(instanceIds!=null){
			sql.append(INSTANCE_ID).append(",");
			sql_values.append("?");
			obj.add(instanceIds[0]);
		}
		sql.append(COMPANY_ID).append(",");
		sql_values.append(",?");
		obj.add(ContextUtils.getCompanyId());
		sql.append(CREATOR).append(",");
		sql_values.append(",?");
		obj.add(ContextUtils.getLoginName());
		sql.append(CREATOR_NAME).append(",");
		sql_values.append(",?");
		obj.add(ContextUtils.getUserName());
		//创建时间
		sql.append(CREATE_DATE).append(",");
		sql_values.append(",?");
		obj.add(new Date());
		//创建人部门
		com.norteksoft.product.api.entity.User user = ApiFactory.getAcsService().getUserByLoginName(ContextUtils.getLoginName());
		if(user!=null){
			if(user.getMainDepartmentId()!=null){
				Department dept = ApiFactory.getAcsService().getDepartmentById(user.getMainDepartmentId());
				if(dept!=null){
					sql.append(CREATOR_DEPARTMENT).append(",");
					sql_values.append(",?");
					obj.add(dept.getName());
				}
			}
		}
		try {
			for (FormControl field:fields) {
				if(parameterMap.get(field.getName())==null) continue;
				if(field.getDataType()==DataType.DATE){
					String value = parameterMap.get(field.getName())[0];
					if(StringUtils.isEmpty(value)) continue;
					joinSql(SIMPLEDATEFORMAT1.parse(value),field.getName(),sql,sql_values,obj);
				}else if(field.getDataType()==DataType.TIME){
					String value = parameterMap.get(field.getName())[0];
					if(StringUtils.isEmpty(value)) continue;
					if(!value.contains(":")){//是否包含时和分，如果不包含则添加00:00。数据获取控件用于存放字段值的控件为时间类型时会出现该情况。
						value = value+" 00:00";
					}
					joinSql(SIMPLEDATEFORMAT2.parse(value),field.getName(),sql,sql_values,obj);
				}else if(field.getDataType()==DataType.BOOLEAN){
					String value = parameterMap.get(field.getName())[0];
					if(StringUtils.isEmpty(value)) continue;
					if("0".equals(value)||"false".equals(value)){
						joinSql(0x00,field.getName(),sql,sql_values,obj);
					}else if("1".equals(value)||"true".equals(value)){
						joinSql(0x01,field.getName(),sql,sql_values,obj);
					}
				}else if(field.getDataType()==DataType.DOUBLE){
					String value = parameterMap.get(field.getName())[0];
					if(StringUtils.isEmpty(value)) continue;
					joinSql(Double.valueOf(value),field.getName(),sql,sql_values,obj);
				}else if(field.getDataType()==DataType.FLOAT){
					String value = parameterMap.get(field.getName())[0];
					if(StringUtils.isEmpty(value)) continue;
					joinSql(Float.valueOf(value),field.getName(),sql,sql_values,obj);
				}else if(field.getDataType()==DataType.LONG){
					String value = parameterMap.get(field.getName())[0];
					if(StringUtils.isEmpty(value)) continue;
					joinSql(Integer.valueOf(value),field.getName(),sql,sql_values,obj);
				}else if(field.getDataType()==DataType.AMOUNT){
					String value = parameterMap.get(field.getName())[0];
					if(StringUtils.isEmpty(value)) continue;
					joinSql(Double.valueOf(value),field.getName(),sql,sql_values,obj);
				}else if(field.getDataType()==DataType.NUMBER){
					String value = parameterMap.get(field.getName())[0];
					if(StringUtils.isEmpty(value)) continue;
					joinSql(Integer.valueOf(value),field.getName(),sql,sql_values,obj);
				}else if(field.getDataType()==DataType.INTEGER){
					String value = parameterMap.get(field.getName())[0];
					if(StringUtils.isEmpty(value)) continue;
					joinSql(Integer.valueOf(value),field.getName(),sql,sql_values,obj);
				}else{
					String[] values = parameterMap.get(field.getName());
					String text = Arrays.toString(values);
					joinSql(text.substring(1, text.length()-1),field.getName(),sql,sql_values,obj);
				}
			}
			sql.replace(sql.length()-1, sql.length(), ")");
			sql_values.append(")");
			sql.append(sql_values);
			getJdbcTemplate().update(sql.toString(),obj.toArray());
			Long id=getJdbcTemplate().queryForLong("select max(id) from "+form.getDataTable().getName());
			return id;
		} catch (NumberFormatException e) {
			 log.debug(e);
			 throw new RuntimeException("numberFormatException",e);
		} catch (DataAccessException e) {
			log.debug("excute sql failed .");
			log.debug(e);
			 throw new RuntimeException("update Exception",e);
		} catch (ParseException e) {
			log.debug(e);
			throw new RuntimeException("dataFormatException",e);
		}
	}
	
	private void joinSql(Object value,String enName,StringBuilder sql,StringBuilder sql_values,List<Object> obj){
		sql.append(FORM_FIELD_PREFIX_STRING + enName).append(",");
		sql_values.append(",?");
		obj.add(value);
	}
	
	
	//将表单对应的子表的数据保存到数据库中
	public void insertChildTable(Map<String,Object> result,FormView parentForm,List<FormControl> parentFields,FormView childForm,Long parentRowId){
		deleteData(childForm.getDataTable().getName(),TABLE_FK_PREFIX_STRING+parentForm.getDataTable().getName(),parentRowId);
		Map childFields=(Map)result.get(CommonStaticConstant.DATA_SOURCE_FIELD);
		Map fieldValues=(Map)(result.get(CommonStaticConstant.DATA_SOURCE_FIELD_VALUE));
		if(childFields!=null && fieldValues!=null){
			Set set=childFields.keySet();
			Iterator childFieldIt=set.iterator();
			String firstField=childFields.size()>0?(String)(childFieldIt.next()):"";
			int rows=((String[])fieldValues.get(firstField)).length;
			for(int i=0;i<rows;i++){
				childFieldIt=set.iterator();
				StringBuilder sql = new StringBuilder("INSERT INTO ").append(childForm.getDataTable().getName()).append("(");
				StringBuilder sql_values = new StringBuilder(" VALUES(");
				List<Object> obj = new ArrayList<Object>();
				
				sql.append(TABLE_FK_PREFIX_STRING+parentForm.getDataTable().getName()+",");
				sql_values.append("?");
				obj.add(parentRowId);
				try {
					while(childFieldIt.hasNext()){
						String field=(String)(childFieldIt.next());
						joinSqlByDataType(DataType.valueOf((String)(childFields.get(field))),((String[])(fieldValues.get(field)))[i],field,sql,sql_values,obj);
					}
					sql.replace(sql.length()-1, sql.length(), ")");
					sql_values.append(")");
					sql.append(sql_values);
					log.debug("begin to excute sql.");
					log.debug("sql:" + sql.toString());
					log.debug("value array:" + obj);
					getJdbcTemplate().update(sql.toString(),obj.toArray());
					log.debug("excute sql success .");
				}catch (NumberFormatException e) {
					 log.debug(e);
					 throw new RuntimeException("numberFormatException",e);
				} catch (DataAccessException e) {
					log.debug("excute sql failed .");
					log.debug(e);
					 throw new RuntimeException("update Exception",e);
				} 
			}
		}
	}
	
	private void joinSqlByDataType(DataType dataType,Object initValue,String enName,StringBuilder sql,StringBuilder sql_values,List<Object> obj){
		try {
			if(dataType==DataType.DATE){
				String value = initValue.toString();
				if(StringUtils.isEmpty(value)) return;
				joinSql(SIMPLEDATEFORMAT1.parse(value),enName,sql,sql_values,obj);
			}else if(dataType==DataType.TIME){
				String value = initValue.toString();
				if(StringUtils.isEmpty(value)) return;
				joinSql(SIMPLEDATEFORMAT2.parse(value),enName,sql,sql_values,obj);
			}else if(dataType==DataType.BOOLEAN){
				String value = initValue.toString();
				if(StringUtils.isEmpty(value)) return;
				if("0".equals(value)||"false".equals(value)){
					joinSql(0x00,enName,sql,sql_values,obj);
				}else if("1".equals(value)||"true".equals(value)){
					joinSql(0x01,enName,sql,sql_values,obj);
				}
			}else if(dataType==DataType.DOUBLE){
				String value = initValue.toString();
				if(StringUtils.isEmpty(value)) return;
				joinSql(Double.valueOf(value),enName,sql,sql_values,obj);
			}else if(dataType==DataType.FLOAT){
				String value = initValue.toString();
				if(StringUtils.isEmpty(value)) return;
				joinSql(Float.valueOf(value),enName,sql,sql_values,obj);
			}else if(dataType==DataType.LONG){
				String value =initValue.toString();
				if(StringUtils.isEmpty(value)) return;
				joinSql(Integer.valueOf(value),enName,sql,sql_values,obj);
			}else if(dataType==DataType.AMOUNT){
				String value = initValue.toString();
				if(StringUtils.isEmpty(value)) return;
				joinSql(Double.valueOf(value),enName,sql,sql_values,obj);
			}else if(dataType==DataType.NUMBER){
				String value = initValue.toString();
				if(StringUtils.isEmpty(value)) return;
				joinSql(Integer.valueOf(value),enName,sql,sql_values,obj);
			}else if(dataType==DataType.INTEGER){
				String value = initValue.toString();
				if(StringUtils.isEmpty(value)) return;
				joinSql(Integer.valueOf(value),enName,sql,sql_values,obj);
			}else{
				joinSql(initValue.toString(),enName,sql,sql_values,obj);
			}
		}catch (ParseException e) {
			log.debug(e);
			throw new RuntimeException("dataFormatException",e);
		}
	}
	
	
	//DELETE FROM 表名称 WHERE 列名称 = 值
	public void deleteData(String tableName, String column,Object value) {
		this.getJdbcTemplate().execute("delete from " + tableName + " where "+column+"="+value );
	}
	
	/**
	 * 执行sql
	 * @param sql
	 * @return
	 */
	public List excutionSql(String sql) {
		return this.getJdbcTemplate().queryForList(sql);
	}
	
	/**
	 * 执行sql
	 * @param sql
	 * @return
	 */
	public Page<Object> excutionSql(Page<Object> page,String sql,String conditionSql) {
		sql = createConditionSql(sql,conditionSql);
		if(page.isAutoCount()) page.setTotalCount(countHqlResult(sql));
		sql = createHqlAddOrderBy(sql,page);
		StringBuilder pageSql = new StringBuilder("select * from ( select sql.* ,rownum rownum_  from ( ")
		 						.append(sql).append(") sql where rownum <= ").append(page.getPageNo()*page.getPageSize()).append(" ) where rownum_ > ").append(page.getFirst() - 1);
		List<Object> list = getJdbcTemplate().queryForList(pageSql.toString());
		page.setResult(list);
		return page;
	}
	
	private String createConditionSql(String sql,String condition){
		if(StringUtils.isEmpty(condition))return sql;
		if(StringUtils.isEmpty(condition.trim()))return sql;
		if(sql.contains("where")){
			return sql + " and " + condition;
		}else{
			return sql + " where " + condition;
		}
	}
	
	/**
	 * 向hql中设置orderBy条件
	 * @param hql hql语句
	 * @param page 分页和排序参数
	 * @return
	 */
	protected String createHqlAddOrderBy(final String sql, final Page<Object> page) {
		String newSql = sql;
		if (page.isOrderBySetted()) {
			String[] orderByArray = StringUtils.split(page.getOrderBy(), ',');
			String[] orderArray = StringUtils.split(page.getOrder(), ',');

			Assert.isTrue(orderByArray.length == orderArray.length, "分页多重排序参数中,排序字段与排序方向的个数不相等");

			String orderByStr = " order by ";
			for (int i = 0; i < orderByArray.length; i++) {
				if((i + 1) == orderByArray.length) {
					orderByStr += orderByArray[i].trim() + " " + orderArray[i].trim();
				} else {
					orderByStr += orderByArray[i].trim() + " " + orderArray[i].trim() + ", ";
				}
			}
			newSql += orderByStr;
		}
		return newSql;
	}
	
	protected long countHqlResult(final String sql) {
		String fromSql = sql;
		//select子句与order by子句会影响count查询,进行简单的排除.
		fromSql = "from " + StringUtils.substringAfter(fromSql, "from");
		fromSql = StringUtils.substringBefore(fromSql, "order by");
		String countSql = "select count(*) " + fromSql;
		return this.getJdbcTemplate().queryForLong(countSql);

	}
	
	//DELETE FROM 表名称 WHERE 列名称 = 值
	public void deleteData(String tableName, Long id) {
		this.getJdbcTemplate().execute("delete from " + tableName + " where id="+id );
	}
	
	public void deleteDatas(String tableName, List<Long> ids) {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from ").append(tableName).append(" where");
		boolean isFirst = true;
		for(Long id : ids){
			if(!isFirst) sql.append(" or ");
			sql.append(" id=").append(id);
			isFirst = false;
		}
		this.getJdbcTemplate().execute(sql.toString());
	}
	
	public void updateTable(String sql){
		this.getJdbcTemplate().update(sql);
	}
	
	public void updateTable(String sql,Object[] values){
		this.getJdbcTemplate().update(sql.toString(),values);
	}
	
	/**
	 * 得到sequenceValue
	 * @param sequenceName
	 */
	public Long getSequenceValue(String sequenceName){
		return this.getJdbcTemplate().queryForLong("SELECT "+sequenceName+".nextval FROM DUAL");
	}
	
	/**
	 * 增加数据库表字段
	 * @param tableName
	 * @param fields
	 */
	public void addDataBaseColumn(String tableName, String columnName, TableColumn tableCo){
		StringBuilder str = new StringBuilder();
		str.append("ALTER TABLE ").append(tableName).append(" add ").append(FORM_FIELD_PREFIX_STRING + columnName+" ").append(getSqlType(tableCo));
		this.getJdbcTemplate().execute(str.toString());
	}
}
