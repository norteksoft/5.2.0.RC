package com.norteksoft.product.web.wf;

import java.util.ArrayList;
import java.util.List;

import com.norteksoft.product.api.entity.Opinion;
import com.norteksoft.product.api.entity.TaskPermission;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.wf.impl.WorkflowAction;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.product.api.entity.WorkflowTask;
import com.norteksoft.wf.engine.client.FormFlowable;
/**
 * 工作流Action基类
 * @author qiao
 * @param <T>
 */
public abstract class WorkflowActionSupport<T extends FormFlowable> extends CrudActionSupport<T> implements WorkflowAction<T> {
	
	protected static final long serialVersionUID = 1L;
	// 工作流任务id
	protected Long taskId;
	// 点击的按钮
	protected TaskProcessingResult taskTransact;
	// 字段权限
	protected String fieldPermission;
	//任务
	protected WorkflowTask task;
	//意见列表
	protected List<Opinion> opinions=new ArrayList<Opinion>();
	//工作流上传正文和附件相关权限
	protected TaskPermission taskPermission;
	

	/**
	 * 启动并提交流程
	 * @return
	 * @throws Exception
	 */
	public abstract String submitProcess();
	
	/**
	 * 完成任务
	 * @return
	 * @throws Exception
	 */
	public abstract String completeTask();

	/**
	 * 完成交互任务：用于选人、选环节、填意见
	 * @return
	 */
	public abstract String completeInteractiveTask();
	
	/**
	 * 取回任务
	 * @return
	 * @throws Exception
	 */
	public abstract String retrieveTask();
	
	/**
	 * 减签
	 * @return
	 */
	public abstract String removeSigner();
	
	/**
	 * 加签
	 * @return
	 */
	public abstract String addSigner();
	
	/**
	 * 显示流转历史
	 * @return
	 */
	public abstract  String showHistory();
	
	/**
	 * 填写意见
	 * @return
	 */
	public abstract String fillOpinion();
	
	/**
	 * 流程监控中应急处理功能
	 */
	public abstract String processEmergency();
	
	/**
	 * 领取任务
	 * @return
	 */
	public abstract String drawTask();
	
	public abstract String abandonReceive();

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public TaskProcessingResult getTaskTransact() {
		return taskTransact;
	}

	public void setTaskTransact(TaskProcessingResult taskTransact) {
		this.taskTransact = taskTransact;
	}

	public String getFieldPermission() {
		return fieldPermission;
	}

	public void setFieldPermission(String fieldPermission) {
		this.fieldPermission = fieldPermission;
	}

	public WorkflowTask getTask() {
		return task;
	}

	public void setTask(WorkflowTask task) {
		this.task = task;
	}

	public List<Opinion> getOpinions() {
		return opinions;
	}

	public void setOpinions(List<Opinion> opinions) {
		this.opinions = opinions;
	}

	public TaskPermission getTaskPermission() {
		return taskPermission;
	}

	public void setTaskPermission(TaskPermission taskPermission) {
		this.taskPermission = taskPermission;
	}
	
	
	
}
