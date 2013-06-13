package com.norteksoft.product.api;


/**
 * 公开提供给用户使用的流转历史的api
 * @author wurong
 */
public interface WorkflowHistoryService {

	
	/**
	 * 返回流转历史的查看权限
	 * @param taskId 任务id
	 */
	@Deprecated
	public boolean historyAuthorization(Long taskId );
	
}
