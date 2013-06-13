package com.norteksoft.product.api;


/**
 * 公开提供给用户使用的普通任务api
 * @author liudongxia
 */
public interface CommonTaskService {
	/**
	 * 保存普通任务
	 * @param task
	 */
	public void saveTask(Long taskId);
	/**
	 * 创建普通任务
	 * @param url
	 * @param name
	 * @param title
	 * @param category
	 * @param transactor
	 */
	public void createTask(String url,String name,String title,String category,String transactor);
	/**
	 * 创建普通任务
	 * @param name
	 * @param title
	 * @param category
	 * @param transactor
	 */
	public void createTask(String name,String title,String category,String transactor);
	/**
	 * 完成普通任务
	 * @param task
	 */
	public void completeTask(Long taskId);
}
