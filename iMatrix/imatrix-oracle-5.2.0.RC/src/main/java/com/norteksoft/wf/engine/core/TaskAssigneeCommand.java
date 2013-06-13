package com.norteksoft.wf.engine.core;

import org.jbpm.api.ProcessEngine;
import org.jbpm.api.cmd.Command;
import org.jbpm.api.cmd.Environment;
import org.jbpm.api.task.Task;

public class TaskAssigneeCommand implements Command<Task>{
	
	private static final long serialVersionUID = 1L;
	private Task task;
	private String assignee;
	
	public TaskAssigneeCommand(Task task, String assignee){
		this.task = task;
		this.assignee = assignee;
	}
	
	public Task execute(Environment environment) throws Exception {
		ProcessEngine engine = environment.get(ProcessEngine.class);
		task.setAssignee(assignee);
		engine.getTaskService().saveTask(task);
		return task;
	}

}
