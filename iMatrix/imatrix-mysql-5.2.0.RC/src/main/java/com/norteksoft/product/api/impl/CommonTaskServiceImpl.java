package com.norteksoft.product.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.norteksoft.product.api.CommonTaskService;
import com.norteksoft.task.entity.Task;
import com.norteksoft.task.service.TaskManager;

public class CommonTaskServiceImpl implements CommonTaskService {
	private Log log = LogFactory.getLog(CommonTaskServiceImpl.class);
	@Autowired
	private TaskManager taskManager;

	public void completeTask(Long taskId) {
		Assert.notNull(taskId, "taskId不能为null");
		Task task=taskManager.getTaskById(taskId);
		if(task==null){
			log.debug("任务不能为null");
			throw new RuntimeException("任务不能为null");
		}
		taskManager.completeCommonTask(task);
	}

	public void createTask(String url, String name,String title, String category,String transactor) {
		taskManager.createTask(url, name,title, category,transactor);
	}

	public void createTask(String name,String title, String category,String transactor) {
		taskManager.createTask(name, title, category,transactor);
	}

	public void saveTask(Long taskId) {
		Assert.notNull(taskId, "taskId不能为null");
		Task task=taskManager.getTaskById(taskId);
		if(task==null){
			log.debug("任务不能为null");
			throw new RuntimeException("任务不能为null");
		}
		taskManager.saveTask(task);
	}

}
