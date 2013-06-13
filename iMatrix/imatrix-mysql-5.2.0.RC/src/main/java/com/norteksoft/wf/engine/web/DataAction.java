package com.norteksoft.wf.engine.web;

import java.util.Map;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;
import com.norteksoft.wf.engine.service.WorkflowRightsManager;

/**
 * 处理自定义表单数据的保存，和提交
 * <p>该action中没有任何属性的set方法，是为了避免和数据表单中字段冲突导致不可预知的类型转换异常</p>
 * @author wurong
 *
 */

@Namespace("/engine")
@ParentPackage("default")
@Results( {	@Result(name = "workflowassign", location = "taskassign", type = "chain")})
public class DataAction  extends CrudActionSupport<Object> {
	private static final long serialVersionUID = 1L;

	private String formHtml;
	private Boolean submit = false;
	private String fieldPermission;
	private Long taskId;
	private WorkflowRightsManager workflowRightsManager;

	@SuppressWarnings("unchecked")
	@Override
	public String save() throws Exception {
		WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
		TaskService taskService =  (TaskService)ContextUtils.getBean("taskService");
		Map<String,String> resultMap  = workflowInstanceManager.save(Struts2Utils.getRequest().getParameterMap());
		String workflowInstanceId = resultMap.get(WorkflowInstanceManager.INSTANCEID);
		WorkflowTask task = taskService.getTask(Long.valueOf(resultMap.get(WorkflowInstanceManager.TASKID)));
		taskId = task.getId();
		WorkflowInstance workflowInstance = workflowInstanceManager.getWorkflowInstance(workflowInstanceId);
		StringBuilder builder = new StringBuilder();
		builder.append( "<input type=\"hidden\" name=\"dataId\" value=\"").append(resultMap.get(WorkflowInstanceManager.DATAID)).append("\"/>");
		builder.append("<input type=\"hidden\" name=\"formId\" value=\"").append(workflowInstance.getFormId()).append("\"/>");
		formHtml = workflowInstanceManager.getHtml(workflowInstance, task);
		formHtml = builder.toString() + formHtml;
		fieldPermission = workflowRightsManager.getFieldPermission(task);		
		return "inputForm";
	}
	
	@SuppressWarnings("unchecked")
	public String submit() throws Exception {
		WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
		TaskService taskService =  (TaskService)ContextUtils.getBean("taskService");
		String url = "http://" + Struts2Utils.getRequest().getHeader("Host") +
		Struts2Utils.getRequest().getContextPath() +
		"/engine/task!input.htm";
		Map<String, String[]> parameterMap =  Struts2Utils.getRequest().getParameterMap();
		Map<String,Object> resultMap  = workflowInstanceManager.submit(parameterMap,url);
		String workflowInstanceId = resultMap.get(WorkflowInstanceManager.INSTANCEID).toString();
		WorkflowInstance workflowInstance = workflowInstanceManager.getWorkflowInstance(workflowInstanceId);
		WorkflowTask task = taskService.getTask(Long.valueOf(resultMap.get(WorkflowInstanceManager.TASKID).toString()));
		StringBuilder builder = new StringBuilder();
		builder.append( "<input type=\"hidden\" name=\"dataId\" value=\"").append(resultMap.get(WorkflowInstanceManager.DATAID)).append("\"/>");
		formHtml = workflowInstanceManager.getHtml(workflowInstance, task);
		formHtml = builder.toString() + formHtml;
		String to = null;
		Object result = resultMap.get(WorkflowInstanceManager.RESULT);
//		if(result.equalsIgnoreCase("true")){
//			addActionMessage("任务已完成");
//			submit = true;
//			to = "inputForm";
//		}else if(result.equalsIgnoreCase("false")){//如果需要指定办理人
//			to = "workflowassign";
//		}else{
//			to = null;
//			addActionMessage(result);
//		}
		return to;
	}

	@Override
	public String delete() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String input() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String list() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public Object getModel() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getFieldPermission() {
		return fieldPermission;
	}

	public Boolean getSubmit() {
		return submit;
	}

	public String getFormHtml() {
		return formHtml;
	}

	public Long getTaskId() {
		return taskId;
	}

	@Autowired
	public void setWorkflowRightsManager(WorkflowRightsManager workflowRightsManager) {
		this.workflowRightsManager = workflowRightsManager;
	}
}
