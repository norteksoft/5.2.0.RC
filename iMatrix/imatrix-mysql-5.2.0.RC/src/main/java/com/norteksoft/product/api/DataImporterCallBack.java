package com.norteksoft.product.api;

import java.util.List;

import com.norteksoft.bs.options.entity.ImportDefinition;

public interface DataImporterCallBack {
	/**
	 * 导入验证后的回调方法
	 * @param message 导入验证后的出错信息
	 * @return
	 */
	public String afterValidate(List<String> message);
	/**
	 * 保存一行之前的回调方法
	 * @param rowValue导入文件中的一行值
	 * @param importDefinition 导入定义
	 */
	public boolean beforeSaveSingleRow(String[] rowValue,ImportDefinition importDefinition);
	/**
	 * 保存一行的回调方法
	 * @param rowValue导入文件中的一行值
	 * @param importDefinition导入定义
	 */
	public String saveSingleRow(String[] rowValue,ImportDefinition importDefinition) ;
	/**
	 * 保存一行之后的回调方法
	 * @param rowValue导入文件中的一行值
	 * @param importDefinition导入定义
	 */
	public void afterSaveSingleRow(String[] rowValue,ImportDefinition importDefinition);
	/**
	 * 所有行保存之后的回调方法
	 */
	public void afterSaveAllRows();
	
}
