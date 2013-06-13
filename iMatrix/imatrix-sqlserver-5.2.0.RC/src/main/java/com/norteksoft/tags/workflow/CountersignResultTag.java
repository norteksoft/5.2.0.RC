package com.norteksoft.tags.workflow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.task.service.WorkflowTaskManager;
import com.norteksoft.wf.engine.entity.Temp;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;
import com.norteksoft.wf.engine.service.WorkflowRightsManager;

public class CountersignResultTag extends SimpleTagSupport {

	private Log log = LogFactory.getLog(CountersignResultTag.class);
	
	private Long taskId;
	
	private String message;//提示信息

	private boolean view = false;//会签结果的查看

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getMessage() {
		return message;
	}
	
	public boolean isView() {
		return view;
	}
	
	@Override
	public void doTag() throws JspException, IOException {
		JspWriter out = getJspContext().getOut();
		List<Temp> temps = new ArrayList<Temp>();
		if(taskId != 0) {
			TaskService taskService = (TaskService)ContextUtils.getBean("taskService");
			WorkflowTaskManager workflowTaskManager = (WorkflowTaskManager)ContextUtils.getBean("workflowTaskManager");
			WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
			WorkflowRightsManager workflowRightsManager = (WorkflowRightsManager)ContextUtils.getBean("workflowRightsManager");
			WorkflowTask task = workflowTaskManager.getTask(taskId);
			WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
			view = workflowRightsManager.viewMeetingResultRight(task);
			if(view) {
				List<String> nameList=taskService.getCountersignByProcessInstanceId(instance.getProcessInstanceId(),TaskProcessingMode.TYPE_COUNTERSIGNATURE);
				if(nameList!=null){
					int yesnum = 0,nonum = 0;
					for (int i=0;i<nameList.size();i++) {
						String name= nameList.get(i);
						List<WorkflowTask> listYes = taskService.getCountersignByProcessInstanceIdResult(instance.getProcessInstanceId(),TaskProcessingMode.TYPE_COUNTERSIGNATURE,name,TaskProcessingResult.APPROVE);
						List<WorkflowTask> listNo= taskService.getCountersignByProcessInstanceIdResult(instance.getProcessInstanceId(),TaskProcessingMode.TYPE_COUNTERSIGNATURE,name,TaskProcessingResult.REFUSE);
						List<WorkflowTask> list = new ArrayList<WorkflowTask>();
						if(listYes!=null){
							list.addAll(listYes);
							yesnum = listYes.size();
						}
						if(listNo!=null){
							list.addAll(listNo);
							nonum = listNo.size();
						}
						Temp temp = new Temp(name,yesnum,nonum,list);
						temps.add(temp);
					}
				}
			} else {
				message = "你没有权限查看会签结果";
			}
		} else {
			message = "没有任务id，无法查看会签结果";
		}
		try {
			out.print(readTemplet(temps));
		} catch (Exception e) {
			log.error(e);
			throw new JspException(e);
		}
	}
	
	private String readTemplet(List<Temp> temps) throws Exception {
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("temps", temps);
		root.put("view", view);
		root.put("message", message);
		String result = TagUtil.getContent(root, "workflow/countersignresult.ftl");
		return result;
	}
}
