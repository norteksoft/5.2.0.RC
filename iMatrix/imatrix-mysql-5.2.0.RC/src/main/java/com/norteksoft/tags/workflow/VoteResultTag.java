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

public class VoteResultTag extends SimpleTagSupport {
	private Log log = LogFactory.getLog(VoteResultTag.class);
	
	private Long taskId;
	
	private String message;
	
	private boolean view = false;

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
		if(taskId != 0){
			TaskService taskService = (TaskService)ContextUtils.getBean("taskService");
			WorkflowTaskManager workflowTaskManager = (WorkflowTaskManager)ContextUtils.getBean("workflowTaskManager");
			WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
			WorkflowRightsManager workflowRightsManager = (WorkflowRightsManager)ContextUtils.getBean("workflowRightsManager");
			WorkflowTask task = workflowTaskManager.getTask(taskId);
			WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
			view = workflowRightsManager.viewVoteResultRight(task);
			if(view) {
				List<String> nameList = taskService.getCountersignByProcessInstanceId(instance.getProcessInstanceId(),TaskProcessingMode.TYPE_VOTE);
				if(nameList!=null){
					int yesnum = 0,nonum = 0,invanum=0;
					for (int i=0;i<nameList.size();i++) {
						String name= nameList.get(i);
						List<WorkflowTask>listYes = taskService.getCountersignByProcessInstanceIdResult(instance.getProcessInstanceId(),TaskProcessingMode.TYPE_VOTE,name,TaskProcessingResult.AGREEMENT);
						List<WorkflowTask>listNo = taskService.getCountersignByProcessInstanceIdResult(instance.getProcessInstanceId(),TaskProcessingMode.TYPE_VOTE,name,TaskProcessingResult.OPPOSE);
						List<WorkflowTask>listInva = taskService.getCountersignByProcessInstanceIdResult(instance.getProcessInstanceId(),TaskProcessingMode.TYPE_VOTE,name,TaskProcessingResult.KIKEN);
						List<WorkflowTask> list = new ArrayList<WorkflowTask>();
						if(listYes != null){
							list.addAll(listYes);
							yesnum = listYes.size();
						}
						if(listNo != null){
							list.addAll(listNo);
							nonum = listNo.size();
						}
						if(listInva != null){
							list.addAll(listInva);
							invanum = listInva.size();
						}
						Temp temp = new Temp(name,yesnum,nonum,invanum,list);
						temps.add(temp);
					}
				}
			} else {
				message = "你没有权限查看投票结果";
			}
		} else {
			message = "没有任务id，无法查看投票结果";
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
		String result =TagUtil.getContent(root, "workflow/voteresult.ftl");
		return result;
	}
}
