package com.norteksoft.mms.form.jdbc;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.TableColumn;

public interface JdbcSupport {
	public static final String FORM_FIELD_PREFIX_STRING = "dt_";
	public static final String TABLE_FK_PREFIX_STRING = "FK_";
	
	public static final SimpleDateFormat SIMPLEDATEFORMAT1 = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat SIMPLEDATEFORMAT2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public static final String INSTANCE_ID = "instance_id";
	public static final String FORM_CODE = "form_code";
	public static final String FORM_VERSION = "form_version";
	public static final String COMPANY_ID = "company_id";
	public static final String CREATOR="creator";
	public static final String CREATOR_NAME="creator_Name";
	public static final String CREATE_DATE="create_date";
	public static final String CREATOR_DEPARTMENT="creator_department";
	public static final String FIRST_TASK_ID="first_task_id";
	public static final String PROCESS_STATE="process_state";
	public static final String CURRENT_ACTIVITY_NAME="current_activity_name";
	public static final String WORKFLOW_DEFINITION_NAME="workflow_definition_name";
	public static final String WORKFLOW_DEFINITION_ID="workflow_definition_id";
	public static final String WORKFLOW_DEFINITION_CODE="workflow_definition_code";
	public static final String WORKFLOW_DEFINITION_VERSION="workflow_definition_version";
	public static final String FORM_ID="form_id";
	
	public static final String FILED_NAME_IS_CREATE_SPECIAL_TASK = FORM_FIELD_PREFIX_STRING+"CREATE_SPECIAL_TASK";//是否特事特办
	public static final String FILED_NAME_IS_SPECIAL_TASK_TRANSACTOR = FORM_FIELD_PREFIX_STRING+"SPECIAL_TASK_TRANSACTOR";//选择的特事特办人员
	
	public void createDefaultTable(String tableName,List<FormControl> columns);
	
	public Long insertTable(Map<String,String[]> parameterMap,FormView form,List<FormControl> fields);
	
	public Long autoUpdateTable(Map<String,String[]> parameterMap,FormView form,List<FormControl> fields,Long dataId);
	
	public List excutionSql(String sql);
	
	public Map getDataMap(String tableName, Long id);
	
	public void addDataBaseColumn(String tableName, String columnName, TableColumn tableCo);
	
	public void insertChildTable(Map<String,Object> result,FormView parentForm,List<FormControl> parentFields,FormView childForm,Long parentRowId);
	
	public void updateTable(String sql);
	
	public void updateTable(String sql,Object[] values);
	
	public Long updateTable(Map<String,String[]> parameterMap,FormView form,List<FormControl> fields,Long dataId);
	
	public void deleteDatas(String tableName, List<Long> ids);
	
	public void deleteData(String tableName, Long id);
	
	public Long getSequenceValue(String sequenceName);
}
