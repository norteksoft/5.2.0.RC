package com.norteksoft.product.api;

import com.norteksoft.product.api.entity.TaskPermission;

/**
 * 任务权限api
 * @author liudongxia
 *
 */
public interface WorkflowPermissionService {
	/**
	 * 获得当前任务的所有权限
	 * @param taskId  当前任务id
	 * @return 任务的权限
	 */
	public TaskPermission getActivityPermission(Long taskId);
	/**
	 * 获得启用最高版本的流程还未发起时第一环节的所有权限
	 * @param definitionCode 流程编码
	 * @return
	 */
	public TaskPermission getActivityPermission(String definitionCode);
	/**
	 * 获得流程还未发起时第一环节的所有权限
	 * @param definitionCode 流程编码
	 * @return
	 */
	public TaskPermission getActivityPermission(String definitionCode,Integer definitionVersion);
	/**
	 * 根据当前任务获得环节办理人的正文权限
	 * @return 环节办理人的正文权限,正文控件规定的形式(-1,0,1,1,0,0,1,1)
	 */
	public String getDocumentPermission(Long taskId);
}
