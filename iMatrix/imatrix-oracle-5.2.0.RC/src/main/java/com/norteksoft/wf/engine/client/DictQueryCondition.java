package com.norteksoft.wf.engine.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.wf.base.enumeration.DataDictProcessType;
import com.norteksoft.wf.base.enumeration.DataDictUseType;

/**
 * 数据字典API查询条件 
 */
public class DictQueryCondition {

	public static final String DICT_HQL_ALIAS = "dict";
	public static final String PROCESS_HQL_ALIAS = "process";
	private StringBuilder condition = new StringBuilder();
	private List<Object> conditionValue = new ArrayList<Object>();
	private String typeCode;        //类型编号typeNo
	private String typeName;        //类型名称
	private String processName; //流程名称
	private String tacheName;    //环节名称
	private DataDictUseType dataDictUseType;     //用途
	
	public DictQueryCondition(){}
	
	public DictQueryCondition(String typeCode, String processName, String tacheName, DataDictUseType dataDictUseType) {
		super();
		this.typeCode = typeCode;
		this.processName = processName;
		this.tacheName = tacheName;
		this.dataDictUseType = dataDictUseType;
	}
	
	/**
	 * 获取查询条件的HQL
	 * @return
	 */
	public String getCondition(){
		addTypeCodeCondition();
		addTypeNameCondition();
		addUseTypeCondition();
		addProcessNameCondition();
		addTacheNameCondition();
		return condition.toString();
	}
	
	/**
	 * 获取查询条件的值
	 * @return
	 */
	public List<Object> getConditionValues(){
		return conditionValue;
	}

	/*
	 * 类型条件
	 *  and dict.typeName=?
	 */
	private void addTypeCodeCondition(){
		if(StringUtils.isNotBlank(typeCode)){
			condition.append(" and ").append("dict.typeNo=?");
			conditionValue.add(typeCode);
		}
	}

	/*
	 * 类型条件
	 *  and dict.typeName=?
	 */
	private void addTypeNameCondition(){
		if(StringUtils.isNotBlank(typeName)){
			condition.append(" and ").append("dict.typeName=?");
			conditionValue.add(typeName);
		}
	}

	/*
	 * 使用类型条件
	 *  and dict.type=?
	 */
	private void addUseTypeCondition() {
		if(dataDictUseType != null){
			condition.append(" and ").append("dict.type=?");
			conditionValue.add(dataDictUseType.ordinal());
		}
	}
	
	/*
	 * 流程名称条件
	 * and (dict.processType=?  or ( dict.processType=? and process.processDefinitionName=? and process.tacheName is null )) 
	 */
	private void addProcessNameCondition(){
		if(StringUtils.isNotBlank(processName) && StringUtils.isBlank(tacheName)){
			condition.append(" and (");
			condition.append("dict.processType=?");
			condition.append(" or ( ").append("dict.processType=?");
			condition.append(" and ").append("process.processDefinitionName=? and ").append("process.tacheName is null ").append(" )");
			condition.append(") ");
			conditionValue.add(DataDictProcessType.COMMON.ordinal());
			conditionValue.add(DataDictProcessType.SELECT.ordinal());
			conditionValue.add(processName);
		}
	}
	
	/*
	 * 环节名称条件
	 * and ( 
	 * dict.processType=?  
	 * or ( dict.processType=? and process.processDefinitionName=? and process.tacheName is null ) 
	 * or ( dict.processType=? and process.processDefinitionName=? and process.tacheName=? ) 
	 */
	private void addTacheNameCondition() {
		if(StringUtils.isNotBlank(tacheName)){
			if(StringUtils.isBlank(processName)){
				throw new IllegalArgumentException("设置了流程环节后，必须设置流程名称");
			}
			condition.append(" and (");
			condition.append("dict.processType=?");
			condition.append(" or (").append("dict.processType=?");
			condition.append(" and ").append("process.processDefinitionName=? and ").append("process.tacheName is null ").append(" )");
			condition.append(" or (").append("dict.processType=?");
			condition.append(" and ").append("process.processDefinitionName=? and ").append("process.tacheName=?").append(" )");
			condition.append(") ");
			conditionValue.add(DataDictProcessType.COMMON.ordinal());
			conditionValue.add(DataDictProcessType.SELECT.ordinal());
			conditionValue.add(processName);
			conditionValue.add(DataDictProcessType.SELECT.ordinal());
			conditionValue.add(processName);
			conditionValue.add(tacheName);
		}
	}

	/**
	 * 添加类型编号参数
	 * @param typeCode 类型编号
	 */
	public void addTypeCode(String typeCode){
		this.typeCode = typeCode;
	}

	/**
	 * 添加类型名称参数
	 * @param typeName 类型名称
	 */
	public void addTypeName(String typeName){
		this.typeName = typeName;
	}
	
	/**
	 * 添加流程名称参数
	 * @param processName 流程名称
	 */
	public void addProcessName(String processName){
		this.processName = processName;
	}
	
	/**
	 * 添加环节名称参数，如果调用了此方法，必须还调用addProcessName方法
	 * @param tacheName 环节名称
	 */
	public void addTacheName(String tacheName){
		this.tacheName = tacheName;
	}
	
	/**
	 * 添加使用类型参数
	 * @param dataDictUseType 使用类型
	 */
	public void addUseType(DataDictUseType useType){
		this.dataDictUseType = useType;
	}
	
}
