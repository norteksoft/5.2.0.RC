package com.norteksoft.product.api;

import java.util.List;

import com.norteksoft.product.orm.Page;

public interface DbService {

	/**
	 * 保存实体
	 * @param obj
	 */
	public void save(Object obj);
	
	/**
	 * 根据类名和数据ID查询数据
	 * @param className
	 * @param dataId
	 * @return
	 */
	public Object getObject(String className, Long dataId);
	
	/**
	 * 根据类名和ID删除数据
	 * @param className
	 * @param id
	 */
	public void delete(String className, Long id);
	
	/**
	 * 根据HQL查询数据
	 * @param hql
	 * @param param
	 * @return
	 */
	public List<Object> getObjects(String hql, Object... param);
	
	/**
	 * 根据HQL分页查询数据
	 * @param page
	 * @param hql
	 * @param conditionSql
	 * @param values
	 * @return
	 */
	public Page<Object> getObject(Page<Object> page,String hql,
			String conditionSql,List<Object> values);
	
	/**
	 * 根据SQL分页查询数据
	 * @param sql
	 * @param page
	 * @param objects
	 * @return
	 */
	public Page<Object> findPageBySql(String sql, Page<Object> page, Object... objects);
	
}
