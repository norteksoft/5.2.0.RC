package com.norteksoft.mms.form.jdbc.impl;

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
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.util.Assert;

import com.norteksoft.product.api.entity.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.mms.base.CommonStaticConstant;
import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.jdbc.JdbcSupport;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;

public class OracleJdbc extends JdbcDaoSupport implements JdbcSupport {
	private Log log=LogFactory.getLog(OracleJdbc.class);
	
	/**
	 * 创建表
	 * @param tableName
	 * @param fields
	 */
	public void createTable(String tableName,List<TableColumn> columns){
		StringBuilder str = new StringBuilder();
		str.append("CREATE TABLE ").append(tableName).append(" (");
		str.append("id NUMBER(19) primary key ,instance_id varchar(100)" );
		if(columns.size()>0){
			str.append(",");
		}else{
			str.append(" )");
		} 
		for(TableColumn tableColumn:columns){
			str.append(FORM_FIELD_PREFIX_STRING).append(tableColumn.getName()).append(getSqlType(tableColumn)).append(",");
		}
		str.replace(str.length()-1, str.length(), ")");
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
		str.append("id NUMBER(19) primary key ,instance_id varchar(100),company_id NUMBER(19),creator VARCHAR2(255 CHAR),creator_name VARCHAR2(255 CHAR),first_task_id NUMBER(19),process_state NUMBER(10),current_activity_name VARCHAR2(255 CHAR),workflow_definition_name VARCHAR2(255 CHAR),workflow_definition_id VARCHAR2(255 CHAR),workflow_definition_code  VARCHAR2(255 CHAR),workflow_definition_version NUMBER(10),form_id NUMBER(19),create_date DATE,creator_department VARCHAR2(255 CHAR) " );
		if(columns.size()>0){
			str.append(",");
		}else{
			str.append(" )");
		} 
		for(FormControl tableColumn:columns){
			str.append(FORM_FIELD_PREFIX_STRING).append(tableColumn.getName()).append(getSqlType(tableColumn)).append(",");
		}
		str.replace(str.length()-1, str.length(), ")");
		this.getJdbcTemplate().execute(str.toString());
		createSequence(tableName + "_ID");
	}
	
	private String getSqlType(TableColumn tableColumn){
		DataType type = tableColumn.getDataType();
		Integer length = tableColumn.getMaxLength();
		String dataType = "";
		if(type.equals(DataType.TEXT)){
			if(length != null){
				dataType = " VARCHAR2("+ length +" CHAR)";
			}else{
				dataType = " VARCHAR2(255 CHAR)";
			}
		}else if(type.equals(DataType.INTEGER)){
			dataType = " NUMBER(10)";
		}else if(type.equals(DataType.LONG)){
			dataType = " NUMBER(19)";
		}else if(type.equals(DataType.BOOLEAN)){
			dataType = " NUMBER(1)";
		}else if(type.equals(DataType.DOUBLE)){
			dataType = " NUMBER(19,2)";
		}else if(type.equals(DataType.FLOAT)){
			dataType = " FLOAT";
		}else if(type.equals(DataType.TIME)){
			dataType = " TIMESTAMP";
		}else if(type.equals(DataType.DATE)){
			dataType = " DATE";
		}else if(type.equals(DataType.CLOB)){
			dataType = " VARCHAR2(3000 CHAR)";
		}else if(type.equals(DataType.AMOUNT)){
			dataType = " NUMBER(19,2)";
		}else if(type.equals(DataType.NUMBER)){
			dataType = " NUMBER(19)";
		}
		return dataType;
	}
	
	private String getSqlType(FormControl tableColumn){
		DataType type = tableColumn.getDataType();
		Integer length = tableColumn.getMaxLength();
		String dataType = "";
		if(type.equals(DataType.TEXT)){
			if(length != null){
				dataType = " VARCHAR2("+ length +" CHAR)";
			}else{
				dataType = " VARCHAR2(255 CHAR)";
			}
		}else if(type.equals(DataType.INTEGER)){
			dataType = " NUMBER(10)";
		}else if(type.equals(DataType.LONG)){
			dataType = " NUMBER(19)";
		}else if(type.equals(DataType.BOOLEAN)){
			dataType = " NUMBER(1)";
		}else if(type.equals(DataType.DOUBLE)){
			dataType = " NUMBER(19,2)";
		}else if(type.equals(DataType.FLOAT)){
			dataType = " FLOAT";
		}else if(type.equals(DataType.TIME)){
			dataType = " TIMESTAMP";
		}else if(type.equals(DataType.DATE)){
			dataType = " DATE";
		}else if(type.equals(DataType.CLOB)){
			dataType = " CLOB";
		}else if(type.equals(DataType.AMOUNT)){
			dataType = " NUMBER(19,2)";
		}else if(type.equals(DataType.NUMBER)){
			dataType = " NUMBER(19)";
		}
		return dataType;
	}
	
	/**
	 * 创建sequence
	 * @param sequenceId
	 */
	public void createSequence(String sequenceName){
		StringBuilder str = new StringBuilder("create sequence ");
		str.append(sequenceName).append(" minvalue 1 maxvalue 999999999999999999999999999 ");
		str.append("start with 1 increment by 1 cache 20");
		this.getJdbcTemplate().execute(str.toString());
	}
	
	/**
	 * 创建外键
	 * @param sequenceId
	 */
	public void createFK(String majorTableName,String childTableName ){
		StringBuilder sql = new StringBuilder();
		sql.append("alter table ").append(childTableName).append(" add ").append(TABLE_FK_PREFIX_STRING).append(majorTableName).append(" NUMBER(32) ");
		this.getJdbcTemplate().execute(sql.toString());
		this.getJdbcTemplate().execute("alter table "+childTableName+" add constraint FK_"+majorTableName+" foreign key("+TABLE_FK_PREFIX_STRING+majorTableName+") references "+majorTableName+"(id) on delete cascade");
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
	 * 修改一列
	 * @param tableName
	 * @param column
	 */
	public void alterTableColumn(String tableName,TableColumn column, String newName){
		StringBuilder sql = new StringBuilder();
		sql.append("alter table ").append(tableName).append(" rename column ").append(FORM_FIELD_PREFIX_STRING).append(column.getName()).append(" to ").append(FORM_FIELD_PREFIX_STRING).append(newName);
		this.getJdbcTemplate().execute(sql.toString());
	}
	
	/**
	 * 根据表名和记录ID查询对应数据，返回Map
	 */
	public Map getDataMap(String tableName, Long id) {
		String sql = "select * from " + tableName +"  t where t.id= ?" ;
		return  this.getJdbcTemplate().queryForMap(sql, new Long[] {id});
	}
	
	public Long updateTable(Map<String,String[]> parameterMap,FormView form,List<FormControl> fields,Long dataId){
		try {
			if(fields!=null && fields.size()>0){
				final List<Object> obj = new ArrayList<Object>();
				boolean canUpateTabel=false;
				StringBuilder sql = new StringBuilder("UPDATE ").append(form.getDataTable().getName()).append(" SET ");
				String[] firstTaskIds = parameterMap.get(FIRST_TASK_ID);
				if(firstTaskIds!=null){
					joinStandardSql(Long.parseLong(firstTaskIds[0]),FIRST_TASK_ID,sql,obj);
					canUpateTabel=true;
				}
				String[] currentActivityNames = parameterMap.get(CURRENT_ACTIVITY_NAME);
				if(currentActivityNames!=null){
					joinStandardSql(currentActivityNames[0],CURRENT_ACTIVITY_NAME,sql,obj);
					canUpateTabel=true;
				}
				String[] workflowDefinitionNames = parameterMap.get(WORKFLOW_DEFINITION_NAME);
				if(workflowDefinitionNames!=null){
					joinStandardSql(workflowDefinitionNames[0],WORKFLOW_DEFINITION_NAME,sql,obj);
					canUpateTabel=true;
				}
				String[] WorkflowDefinitionIds = parameterMap.get(WORKFLOW_DEFINITION_ID);
				if(WorkflowDefinitionIds!=null){
					joinStandardSql(WorkflowDefinitionIds[0],WORKFLOW_DEFINITION_ID,sql,obj);
					canUpateTabel=true;
				}
				String[] WorkflowDefinitionCodes = parameterMap.get(WORKFLOW_DEFINITION_CODE);
				if(WorkflowDefinitionCodes!=null){
					joinStandardSql(WorkflowDefinitionCodes[0],WORKFLOW_DEFINITION_CODE,sql,obj);
					canUpateTabel=true;
				}
				String[] WorkflowDefinitionVersions = parameterMap.get(WORKFLOW_DEFINITION_VERSION);
				if(WorkflowDefinitionVersions!=null){
					joinStandardSql(Integer.parseInt(WorkflowDefinitionVersions[0]),WORKFLOW_DEFINITION_VERSION,sql,obj);
					canUpateTabel=true;
				}
				String[] formIds = parameterMap.get(FORM_ID);
				if(formIds!=null){
					joinStandardSql(Long.parseLong(formIds[0]),FORM_ID,sql,obj);
					canUpateTabel=true;
				}
				String[] processStates = parameterMap.get(PROCESS_STATE);
				if(processStates!=null){
					joinStandardSql(Integer.parseInt(processStates[0]),PROCESS_STATE,sql,obj);
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
								if(StringUtils.isEmpty(value)) { joinSql(null,field.getName(),sql,obj);continue;};
								joinSql(SIMPLEDATEFORMAT1.parse(value),field.getName(),sql,obj);
							}else if(field.getDataType()==DataType.TIME){
								if(StringUtils.isEmpty(value)) { joinSql(null,field.getName(),sql,obj);continue;};
								if(!value.contains(":")){//是否包含时和分，如果不包含则添加00:00。数据获取控件用于存放字段值的控件为时间类型时会出现该情况。
									value = value+" 00:00";
								}
								joinSql(SIMPLEDATEFORMAT2.parse(value),field.getName(),sql,obj);
							}else if(field.getDataType()==DataType.BOOLEAN){
								if(StringUtils.isEmpty(value)) { joinSql(null,field.getName(),sql,obj);continue;};
								if("false".equals(value)||"0".equals(value)){
									joinSql(0x00,field.getName(),sql,obj);
								}else if("true".equals(value)||"1".equals(value)){
									joinSql(0x01,field.getName(),sql,obj);
								}
							}else if(field.getDataType()==DataType.DOUBLE){
								if(StringUtils.isEmpty(value)) { joinSql(null,field.getName(),sql,obj);continue;};
								joinSql(Double.valueOf(value),field.getName(),sql,obj);
							}else if(field.getDataType()==DataType.FLOAT){
								if(StringUtils.isEmpty(value)) { joinSql(null,field.getName(),sql,obj);continue;};
								joinSql(Float.valueOf(value),field.getName(),sql,obj);
							}else if(field.getDataType()==DataType.LONG){
								if(StringUtils.isEmpty(value)) { joinSql(null,field.getName(),sql,obj);continue;};
								joinSql(Integer.valueOf(value),field.getName(),sql,obj);
							}else if(field.getDataType()==DataType.AMOUNT){
								if(StringUtils.isEmpty(value)) { joinSql(null,field.getName(),sql,obj);continue;};
								joinSql(Double.valueOf(value),field.getName(),sql,obj);
							}else if(field.getDataType()==DataType.NUMBER){
								if(StringUtils.isEmpty(value)) { joinSql(null,field.getName(),sql,obj);continue;};
								joinSql(Integer.valueOf(value),field.getName(),sql,obj);
							}else if(field.getDataType()==DataType.INTEGER){
								if(StringUtils.isEmpty(value)){ joinSql(null,field.getName(),sql,obj);continue;}
								joinSql(Integer.valueOf(value),field.getName(),sql,obj);
							}else{
								String[] values = parameterMap.get(field.getName());
								String text = Arrays.toString(values);//获得的值为[value]
								//text.substring(1, text.length()-1),去掉text的前后[]
								joinSql(text.substring(1, text.length()-1),field.getName(),sql,obj);
							}
							canUpateTabel=true;
						}else{
							joinSql(null,field.getName(),sql,obj);
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
							if(!value.contains(":")){//是否包含时和分，如果不包含则添加00:00。数据获取控件用于存放字段值的控件为时间类型时会出现该情况。
								value = value+" 00:00";
							}
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
		sql.append("id,");
		Long id = getSequenceValue(form.getDataTable().getName()+"_ID");
		sql_values.append("?");
		obj.add(id);
		String[] instanceIds = parameterMap.get(INSTANCE_ID);
		if(instanceIds!=null){
			sql.append(INSTANCE_ID).append(",");
			sql_values.append(",?");
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
		return id;
	}
	
	/**
	 * 得到sequenceValue
	 * @param sequenceName
	 */
	public Long getSequenceValue(String sequenceName){
		return this.getJdbcTemplate().queryForLong("SELECT "+sequenceName+".nextval FROM DUAL");
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
				sql.append("id,");
				Long id = getSequenceValue(childForm.getDataTable().getName()+"_ID");
				log.debug("sequence return id:"+id);
				sql_values.append("?");
				obj.add(id);
				
				sql.append(TABLE_FK_PREFIX_STRING+parentForm.getDataTable().getName()+",");
				sql_values.append(",?");
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
		this.getJdbcTemplate().execute("delete from " + tableName + " t where t."+column+"="+value );
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
		sql.append("delete from ").append(tableName).append(" t where");
		boolean isFirst = true;
		for(Long id : ids){
			if(!isFirst) sql.append(" or ");
			sql.append(" t.id=").append(id);
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
