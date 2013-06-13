package com.norteksoft.product.api;

import com.norteksoft.product.util.ContextUtils;

public class ApiFactory {
	
	/**
	 * 通用数据操作API入口
	 * @return
	 */
	public static DbService getDbService(){
		return (DbService)ContextUtils.getBean("dbServiceImpl");
	}
	
	/**
	 * 业务系统日志API入口
	 * @return
	 */
	public static BussinessLogService getBussinessLogService(){
		return (BussinessLogService)ContextUtils.getBean("bussinessLogServiceImpl");
	}
	
	/**
	 * 基础设置API
	 * @return
	 */
	public static SettingService getSettingService(){
		return (SettingService)ContextUtils.getBean("settingServiceImpl");
	}
	
	/**
	 * 获得portal的api入口
	 * @return
	 */
	public static PortalService getPortalService(){
		return (PortalService)ContextUtils.getBean("portalServiceImpl");
	}
	
	/**
	 * 获得acs的api入口
	 * @return
	 */
	public static AcsService getAcsService(){
		return (AcsService)ContextUtils.getBean("acsServiceImpl");
	}
	
	/**
	 * 获得工作流api入口
	 * @return
	 * @deprecated 不再使用
	 */
	public static WorkflowEngine getWorkflowEngine(){
		return (WorkflowEngine)ContextUtils.getBean("workflowEngine");
	}
	
	/**
	 * 获得工作流实例api
	 */
	public static WorkflowInstanceService getInstanceService(){
		return (WorkflowInstanceService) ContextUtils.getBean("workflowClientManager");
	}
	
	/**
	 * 获得工作流定义api
	 */
	public static WorkflowDefinitionService getDefinitionService(){
		return (WorkflowDefinitionService)ContextUtils.getBean("workflowClientManager");
	}
	
	/**
	 * 获得任务api
	 */
	public static WorkflowTaskService getTaskService(){
		return (WorkflowTaskService)ContextUtils.getBean("workflowClientManager");
	}
	
	/**
	 * 获得表单api
	 */
	public static WorkflowFormService getFormService(){
		return (WorkflowFormService)ContextUtils.getBean("workflowClientManager");
	}
	
	/**
	 * 获得流转历史api
	 */
	public static WorkflowHistoryService getHistoryService(){
		return (WorkflowHistoryService)ContextUtils.getBean("workflowClientManager");
	}
	
	/**
	 * 获得附件api
	 */
	public static WorkflowAttachmentService getAttachmentService(){
		return (WorkflowAttachmentService)ContextUtils.getBean("workflowClientManager");	
	}
	
	/**
	 * 获得正文api
	 */
	public static WorkflowDocumentService getDocumentService(){
		return (WorkflowDocumentService)ContextUtils.getBean("workflowClientManager");
	}
	/**
	 * 获得文件api
	 */
	public static FileService getFileService(){
		return (FileService)ContextUtils.getBean("fileService");
	}
	
	/**
	 * 获得意见api
	 */
	public static WorkflowOpinionService getOpinionService(){
		return (WorkflowOpinionService)ContextUtils.getBean("workflowClientManager");
	}
	
	/**
	 * 获取数据字典api
	 * @return
	 */
	public static WorkflowDataDictService getDataDictService(){
		return (WorkflowDataDictService)ContextUtils.getBean("workflowClientManager");
	}
	
	/**
	 * 获取权限api
	 * @return
	 */
	@Deprecated
	public static WorkflowRightService getRightService(){
		return (WorkflowRightService)ContextUtils.getBean("workflowClientManager");
	}
	
	/**
	 * 获取数据元处理管理api
	 * @return
	 */
	public static MmsService getMmsService(){
		return (MmsService)ContextUtils.getBean("mmsServiceImpl");
	}
	
	/**
	 * 获取环节权限api
	 * @return
	 */
	public static WorkflowPermissionService getPermissionService(){
		return (WorkflowPermissionService)ContextUtils.getBean("workflowClientManager");
	}
	
	/**
	 * 通用导入api
	 */
	public static DataImporterService getDataImporterService(){
		return (DataImporterService)ContextUtils.getBean("dataImporterServiceImpl");
	}
	
}
