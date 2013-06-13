package com.norteksoft.task.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.task.base.enumeration.TaskCategory;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.task.dao.TaskDao;
import com.norteksoft.task.entity.Task;
import com.norteksoft.task.entity.TaskMark;

@Service
@Transactional
public class TaskManager {
	private Log log = LogFactory.getLog(TaskManager.class);
	private TaskDao taskDao;
	
	@Autowired
	public void setTaskDao(TaskDao taskDao) {
		this.taskDao = taskDao;
	}
	@Transactional(readOnly=false)
	public void saveTask(Task task){
		log.debug("*** saveTask 方法开始");
		log.debug("*** Received parameter:" + task);
		
		taskDao.save(task);
		
		log.debug("*** saveTask 方法结束");
	}
	
	public List<Task> getPersonalTasks(String loginName, Long companyId, Integer size, String order){
		log.debug("*** getPersonalTasks 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("loginName:").append(loginName)
			.append("companyId:").append(companyId)
			.append("]").toString());
		StringBuilder sql = new StringBuilder("from Task task where task.companyId=? and task.transactor=? and (task.active=? or task.active=? or task.active=? or task.active=?) and task.paused=? and task.visible=?");
		if(StringUtils.isNotEmpty(order)){
			sql.append(" order by task.displayOrder asc,task."+order+" desc");
		}else{
			sql.append(" order by task.displayOrder asc,task.createdTime desc");
		}
		Page<Task> tasks = new Page<Task>(size);
		taskDao.findPage(tasks,sql.toString(),companyId, loginName, TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.DRAW_WAIT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false,true);
		log.debug("*** getPersonalTasks 方法结束");
		return tasks.getResult();
	}
	
	public List<Task> getDetailTasksByUserType(Long companyId, String loginName, String typeName, Integer size, String order){
		log.debug("*** getPersonalTasks 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("loginName:").append(loginName)
			.append("companyId:").append(companyId)
			.append("]").toString());
		StringBuilder sql = new StringBuilder("from Task t where t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=? or t.active=?  or t.active=?)  and t.paused=? and t.category=?");
		if(StringUtils.isNotEmpty(order)){
			sql.append(" order by t.displayOrder asc,t."+order+" desc");
		}else{
			sql.append(" order by t.displayOrder asc,t.createdTime desc");
		}
		Page<Task> tasks = new Page<Task>(size);
		taskDao.findPage(tasks,sql.toString(),companyId, loginName,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false,typeName);
		log.debug("*** getPersonalTasks 方法结束");
		return tasks.getResult();
	}
	
	public List<Task> getPersonalTasks(String loginName, Long companyId, Integer size){
		log.debug("*** saveTask 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
				.append("loginName:").append(loginName)
				.append("companyId:").append(companyId)
				.append("size:").append(size)
				.append("]").toString());
		
		Page<Task> tasks = new Page<Task>(size);
		taskDao.findPage(tasks, "from Task task where task.visible=? and task.companyId=? and task.transactor=? and (task.active=? or task.active=? or task.active=?) order by task.createdTime desc", 
				true, companyId, loginName, TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex());
		
		log.debug("*** getPersonalTasks 方法结束");
		return tasks.getResult();
	}
	
	public List<Task> getPersonalCompletedTasks(String loginName, Long companyId){
		log.debug("*** getPersonalCompletedTasks 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("loginName:").append(loginName)
			.append("companyId:").append(companyId)
			.append("]").toString());
		
		List<Task> tasks = taskDao.find("from Task task where task.companyId=? and task.transactor=? and (task.active=? or task.active=?)", 
				companyId, loginName, TaskState.COMPLETED.getIndex(), TaskState.CANCELLED.getIndex());
		
		log.debug("*** getPersonalCompletedTasks 方法结束");
		return tasks;
	}
	
	public Task getTaskById(Long taskId){
		return taskDao.get(taskId);
	}
	//改变任务标识
	public void changeTaskMark(Long taskId,TaskMark taskMark) {
		Task task = getTaskById(taskId);
		switch(taskMark) {
	       case RED:
	    	   task.setTaskMark(TaskMark.RED);
	    	   break;
	       case BLUE:
	    	   task.setTaskMark(TaskMark.BLUE);
	          break;
	       case YELLOW:
	    	   task.setTaskMark(TaskMark.YELLOW);
	          break;
	       case GREEN:
	    	   task.setTaskMark(TaskMark.GREEN);
	           break;
	       case ORANGE:
	    	   task.setTaskMark(TaskMark.ORANGE);
	         break;
	       case PURPLE:
	    	   task.setTaskMark(TaskMark.PURPLE);
	          break;
	       case CANCEL:
	    	   task.setTaskMark(TaskMark.CANCEL);
	          break;
	       default:    
	       }
		taskDao.save(task);
	}
	
	
	/**
	 * 完成普通任务
	 * @param task
	 */
	public void completeCommonTask(Task task){
		task.setActive(TaskState.COMPLETED.getIndex());
		saveTask(task);
	}
	/**
	 * 创建普通任务
	 * @param url
	 * @param name
	 * @param title
	 * @param category
	 * @param transactor
	 */
	public void createTask(String url,String name, String title, String category,String transactor){
		Task task=new Task();
		task.setActive(TaskState.WAIT_TRANSACT.getIndex());
		task.setName(name);
		if(StringUtils.isNotEmpty(url)){
			task.setUrl(ContextUtils.getSystemCode()+url);
		}else{
			task.setUrl("task/task/common-task-input.htm?id=");
		}
		task.setTitle(title);
		task.setCategory(category);
		task.setWorkflowTask(false);
		task.setTransactor(transactor);
		com.norteksoft.product.api.entity.User user=ApiFactory.getAcsService().getUserByLoginName(transactor);
		if(user!=null){
			task.setTransactorName(user.getName());
		}
		saveTask(task);
		try {
			ApiFactory.getPortalService().addMessage("task", ContextUtils.getUserName(), ContextUtils.getLoginName(), task.getTransactor(),category, title, "/task/message-task.htm?id="+task.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createTask(String name,String title, String category,String transactor){
		createTask(null, name,title,category,transactor);
	}
	/**
	 * 分页查询用户所有未完成任务
	 * @param page
	 */
	public void getAllTasksByUser(Long companyId, String loginName, Page<Task> page){
		taskDao.getAllTasksByUser(companyId, loginName, page);
	}
	
	
	/**
	 * 分页查询用户所有未完成任务
	 * @param page
	 */
	public void getAllTasksByUserType(Long companyId, String loginName, Page<Task> page,String typeName){
		taskDao.getAllTasksByUserType(companyId, loginName, page,typeName);
	}
	/**
	 * 分页查询用户已完成任务
	 * @param companyId
	 * @param loginName
	 * @param page
	 */
	public void getCompletedTasksByUserType(Long companyId, String loginName, Page<Task> page,String typeName) {
		taskDao.getCompletedTasksByUserType(companyId, loginName, page,typeName);
	}
	
	/**
	 * 分页查询用户已取消任务
	 * @param companyId
	 * @param loginName
	 * @param page
	 */
	public void getCanceledTasksByUserType(Long companyId, String loginName, Page<Task> page,String typeName) {
		taskDao.getCanceledTasksByUserType(companyId, loginName, page,typeName);
	}
	
	/**
	 * 获得所有任务类型
	 * @param isComplete
	 * @return
	 */
	public List<Object[]> getTypeInfos(String taskCategory){
		if(TaskCategory.COMPLETE.equals(taskCategory)){
			return taskDao.getAllCompleteTaskTypeInfos(ContextUtils.getCompanyId(), ContextUtils.getLoginName());
		}else if(TaskCategory.CANCEL.equals(taskCategory)){
			return taskDao.getAllCancelTaskTypeInfos(ContextUtils.getCompanyId(), ContextUtils.getLoginName());
		}else{
			return taskDao.getAllActiveTaskTypeInfos(ContextUtils.getCompanyId(), ContextUtils.getLoginName());
		}
	}
	
	/**
	 * 根据办理人登录名获得类型信息
	 * @param companyId
	 * @param longinName
	 * @return
	 */
	public List<Object[]> getTypeInfos(Long companyId,String longinName){
		return taskDao.getAllActiveTaskTypeInfos(companyId,longinName);
	}
	
	/**
	 * 根据办理人获得任务数目
	 * @param isComplete
	 * @return
	 */
	public Integer getAllTaskNumByUser(String taskCategory){
		if(TaskCategory.COMPLETE.equals(taskCategory)){
			return taskDao.getAllCompleteTasksNum(ContextUtils.getCompanyId(), ContextUtils.getLoginName());
		}else if(TaskCategory.CANCEL.equals(taskCategory)){
			return taskDao.getAllCancelTasksNum(ContextUtils.getCompanyId(), ContextUtils.getLoginName());
		}else{
			return taskDao.getAllActiveTasksNum(ContextUtils.getCompanyId(), ContextUtils.getLoginName());
		}
	}
	
	/**
	 * 根据办理人登录名获得所有待办事宜数目
	 * @param companyId
	 * @param longinName
	 * @return
	 */
	public Integer getAllTaskNumByUser(Long companyId,String longinName){
		return taskDao.getAllActiveTasksNum(companyId,longinName);
	}
	/**
	 * 分页查询用户所有未完成任务
	 * @param page
	 */
	public void getAllTasksByGroupName(Long companyId, String loginName, Page<Task> page,String typeName){
		taskDao.getAllTasksByGroupName(companyId, loginName, page,typeName);
	}
	/**
	 * 分页查询用户所有未完成任务
	 * @param page
	 */
	public void getAllTasksByCustomType(Long companyId, String loginName, Page<Task> page,String typeName){
		taskDao.getAllTasksByCustomType(companyId, loginName, page,typeName);
	}
	/**
	 * 根据自定义类型分页查询用户所有已完成任务
	 * @param page
	 */
	public void getCompletedTasksByCustomType(Long companyId, String loginName, Page<Task> page,String typeName){
		taskDao.getCompletedTasksByCustomType(companyId, loginName, page,typeName);
	}
	/**
	 * 根据流程名称分页查询用户所有已完成任务
	 * @param page
	 */
	public void getCompletedTasksByGroupName(Long companyId, String loginName, Page<Task> page,String typeName){
		taskDao.getCompletedTasksByGroupName(companyId, loginName, page,typeName);
	}
	/**
	 * 根据自定义类型分页查询用户所有已取消任务
	 * @param page
	 */
	public void getCancelTasksByCustomType(Long companyId, String loginName, Page<Task> page,String typeName){
		taskDao.getCancelTasksByCustomType(companyId, loginName, page,typeName);
	}
	/**
	 * 根据流程名称分页查询用户所有已取消任务
	 * @param page
	 */
	public void getCancelTasksByGroupName(Long companyId, String loginName, Page<Task> page,String typeName){
		taskDao.getCancelTasksByGroupName(companyId, loginName, page,typeName);
	}
}
