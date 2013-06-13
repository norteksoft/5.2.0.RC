package com.norteksoft.wf.engine.client;

import org.apache.struts2.convention.annotation.Action;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;

/**
 * 工作流Action基类
 * @author xiao
 * 2011-12-26
 */
@SuppressWarnings("serial")
public abstract class WorkflowBaseAction<T extends FormFlowable> extends CRUDActionSupport<T>{

	/**
	 * 保存实体，同时发起流程
	 */
	@Action("start")
	public String start() throws Exception{
		return null;
	}
	
	/**
	 * 第一环节保存表单,启动流程(如果需要),并提交第一环节任务
	 */
	@Action("submit")
	public String submit() throws Exception{
		return null;
	}
	
	/**
	 * 保存任务,保存表单
	 */
	@Action("save-task")
	public String saveTask() throws Exception{
		return null;
	}
	
	/**
	 * 提交任务
	 */
	@Action("submit-task")
	public String submitTask() throws Exception{
		return null;
	}
	
	/**
	 * 打开任务
	 */
	@Action("task")
	public String task() throws Exception{
		return null;
	}
	
	/**
	 * 查看流转历史
	 */
	@Action("histroy")
	public String histroy() throws Exception{
		return null;
	}
	
	/**
	 * 取回任务
	 */
	@Action("retrieve")
	public String retrieve() throws Exception{
		getWorkflowBaseManager().retrieve(getTaskId());
		return SUCCESS;
	}
	
	public abstract WorkflowBaseManager<T> getWorkflowBaseManager();
	public abstract Long getTaskId();
	
}
