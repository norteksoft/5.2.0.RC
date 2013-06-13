package com.norteksoft.task.webservice;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.norteksoft.product.orm.Page;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.task.entity.Task;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.engine.client.FormFlowable;

//@WebService(name="WorkflowTaskService")
public interface WorkflowTaskService {

	/**
	 * 保存任务
	 * @param task
	 */
	public void saveTask(WorkflowTask workflowTask);
	
	public void saveTasks(List<WorkflowTask> workflowTasks);

	/**
	 * 查询代理的任务
	 * @param companyId
	 * @param loginName
	 * @param page
	 * @return
	 */
	Page<WorkflowTask> getDelegateTasks(Long companyId, String loginName, Page<WorkflowTask> page);
	
	Page<WorkflowTask> getDelegateTasksByActive(Long companyId, String loginName, Page<WorkflowTask> page, boolean isEnd);
	
	Page<WorkflowTask> getTaskAsTrustee(Long companyId, String loginName, Page<WorkflowTask> page, boolean isEnd);
	
	Integer getDelegateTasksNum(Long companyId, String loginName);
	
	Integer getDelegateTasksNumByActive(Long companyId, String loginName, Boolean isCompleted);
	
	Integer getTrusteeTasksNum(Long companyId, String loginName, Boolean isCompleted);
	
	List<WorkflowTask> getAllTasksByInstance(Long companyId, String instanceId);
	
	void getAllTasksByUser(Long companyId, String loginName, Page<WorkflowTask> page);
	List<WorkflowTask> getAllTasksByUser(Long companyId, String loginName);
	
	List<WorkflowTask> getTasksByActivity(Long companyId, String executionId, String taskName);
	
	List<WorkflowTask> getTasksByName(Long companyId, String instanceId, String taskName);
	
	List<WorkflowTask> getNoAssignTasksByName(Long companyId, String instanceId, String taskName,Integer groupNum);
	
	/**
	 * 查询流程执行过程中所有有效的参与人员
	 * @param companyId
	 * @param instanceId
	 * @return
	 */
	List<String> getParticipantsTransactor(Long companyId, String instanceId);
	
	void deleteTasksByName(Long companyId, String instanceId, String[] taskName);
	
	void deleteTask(WorkflowTask task);
	
	/**
	 * 根据任务ID查询任务
	 * @param id
	 * @return
	 */
	public WorkflowTask getTask(Long id);

	/**
	 * 根据流程实例ID查询任务
	 * @param instanceId
	 * @return
	 */
	public WorkflowTask getFirstTaskByInstance(Long companyId, String instanceId, String transactor);
	
	
	/**
	 * 根据流程名字和实例id查询workflowTask
	 * @param instanceId
	 * @param taskName
	 * @return
	 */
	public List<WorkflowTask> getWorkflowTasks(String instanceId, String taskName);
	
	/**
	 * 根据实例查询所有的任务名称
	 * @param companyId
	 * @param instanceId
	 * @return
	 */
	public List<String> getTaskNamesByInstance(Long companyId, String instanceId);
	
	/**
	 * 根据流程实例ID删除任务
	 * @param processId
	 * @param companyId
	 */
	public void deleteTaskByProcessId(String processId,Long companyId);
	
	/**
     * 流程被手动结束时，强制结束流程实例的当前任务
     */
    public void endTasks(String instanceId,Long companyId);
    
    /**
     * 流程被强制结束时，强制结束流程实例的当前任务
     */
    public void compelEndTasks(String instanceId,Long companyId);
    
    /**
     * 活动该流程实例的当前任务
     */
    public List<WorkflowTask> getActivityTasks(String instanceId,Long companyId);
    
    /**
     * 活动该流程实例的当前任务
     */
    public List<WorkflowTask> getActivitySignTasks(String instanceId,Long companyId);
    
    /**
     * 查询办理人的当前环节
     */
    public WorkflowTask getMyTask(String instanceId,Long companyId,String loginName);
    /**
	 * 返回对应办理模式的所有环节
	 * @param processInstanceId
	 * @param processingMode
	 * @return
	 */
	public List<String> getCountersignByProcessInstanceId(String processInstanceId,TaskProcessingMode processingMode);
	
	public List<String> getSignByProcessInstanceId(String processInstanceId,TaskProcessingMode processingMode);
	
	/**
	 * 根据办理结果查询环节
	 */
	public List<WorkflowTask> getCountersignByProcessInstanceIdResult(String processInstanceId,String taskName,TaskProcessingResult result);
	/**
	 * 获得审批任务组数
	 * @param processInstanceId
	 * @param taskName
	 * @param result
	 * @return
	 */
	public List<Integer> getGroupNumByTaskName(String processInstanceId,String taskName);
	
	/**
	 * 获得与该任务是同一环节的所有未办理任务
	 */
	public List<WorkflowTask> getCountersigns(Long id);
	
	/**
	 * 获得与该任务是同一环节的所有未办理任务
	 */
	public List<WorkflowTask> getProcessCountersigns(Long id);
	
	/**
	 * 获得同一会签环节下的所有办理人
	 * @param processInstanceId
	 * @param taskName
	 * @return
	 */
	public List<String> getCountersignsHandler(Long id,Integer handlingState);
	
	/**
	 * 删除同一会签环节下的办理人
	 * @param taskId
	 * @param users 需要删除的办理人
	 */
	public void deleteCountersignHandler(Long taskId, Collection<String> users);
	
	/**
	 * 删除任务
	 */
	public void deleteWorkflowTask(List<Long> ids);
	
	/**
	 * 领取任务
	 * @param taskId
	 * @return
	 */
	public String receive(Long taskId);
	
	/**
	 * 放弃领取的任务
	 * @param taskId
	 * @return
	 */
	public String abandonReceive(Long taskId);
	
	/**
	 * 获得已办理人
	 */
	public Set<String> getHandledTransactors(String workflowId);
	
	/**
	 * 获得所有办理人
	 */
	public Set<String> getAllHandleTransactors(String workflowId);
	
	/**
	 * 获得所有未办理任务
	 */
	public List<WorkflowTask> getNeedReminderTasks();
	
	/**
	 * 获得 已完成的任务
	 */
	public List<WorkflowTask> getCompletedTasks(String workflowId,Long companyId);
	
	/**
	 * 查询实例下的任务
	 * @param definitionName 定义名称
	 * @param loginName 登录名
	 * @return 任务集合
	 */
	public List<WorkflowTask> getTasksOrderByWdfName(String definitionName,String loginName);
	/**
	 * 根据已完成的任务名，获得 已完成的任务
	 */
	public List<WorkflowTask> getCompletedTasksByTaskName(String workflowId,
			Long companyId,String taskName);
	/**
	 * 根据当前用户查询未完成任务总数
	 * @param companyId 公司id
	 * @param loginName 当前用户登录名
	 * @return 未完成任务总数
	 */
	public Integer getTasksNumByTransactor(Long companyId, String loginName);
	/**
	 * 查找公司中所有的超期任务
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getOverdueTasks(Long companyId) ;
	
	/**
	 * 查找当前办理人所有的超期任务的总数
	 * @param companyId
	 * @param transactorName
	 * @return map :key为办理人登录名，value为超期次数
	 */
	public Map<String,Integer> getOverdueTasksNumByTransactor(Long companyId) ;
	/**
	 * 查找公司中所有的超期任务,包括已完成的任务
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getTotalOverdueTasks(Long companyId) ;
	/**
	 * 查找当前办理人所有的超期任务的总数,包括已完成的任务
	 * @param companyId
	 * @param transactorName
	 * @return
	 */
	public Map<String,Integer> getTotalOverdueTasksNumByTransactor(Long companyId);
	/**
	 * 获得所有办理人除当前任务名称的办理人
	 * @param task
	 * @return 办理人列表集合
	 */
	public List<String> getTransactorsExceptTask(Long taskId);
	/**
	 * 根据“任务组”查询任务列表
	 * @param companyId
	 * @param instanceId
	 * @param taskName
	 * @return
	 */
	public List<WorkflowTask> getTaskOrderByGroupNum(Long companyId,String instanceId,String taskName);
	/**
     * 活动该流程实例的已taskName为名称的当前任务
     */
	public List<WorkflowTask> getActivityTasksByName(String instanceId,Long companyId,String taskName);
	/**
	 * 获得当前实例中所有当前办理人
	 * @param instanceId
	 * @param companyId
	 * @return 办理人列表
	 */
	public List<String[]> getActivityTaskTransactors(String instanceId,Long companyId);
	/**
	 * 获得当前实例中所有当前委托人
	 * @param instanceId
	 * @param companyId
	 * @return 办理人列表
	 */
	public List<String> getActivityTaskPrincipals(String instanceId,Long companyId);
	public List<String> getCompletedTaskNames(String workflowId,
			Long companyId);
	/**
     * 流程被暂停时，强制暂停流程实例的当前任务
     */
    public void pauseTasks(String instanceId,Long companyId);
    /**
     * 继续被暂停的任务
     */
    public void continueTasks(String instanceId,Long companyId);
    /**
	 * 批量移除任务中根据办理人查询当前任务列表
	 * @param tasks
	 * @param transactorName
	 * @param typeId
	 * @param defCode
	 * @param wfdId
	 */
	
	public void getActivityTasksByTransactorName(Page<WorkflowTask> tasks,Long typeId, String defCode,Long wfdId);
	/**
	 * 根据实例获得任务
	 * @param instanceIds
	 * @param taskName
	 * @param recieveUser
	 * @param consignor
	 * @param companyId
	 * @return
	 */
	 public List<WorkflowTask> getTasksByInstance(List<String> instanceIds,String taskName,String recieveUser,String consignor,Long companyId);
	 /**
	  * 获得当前任务名称，除特事特办任务
	  * @param instanceId
	  * @return
	  */
	 public List<String> getActiveTaskNameWithoutSpecial(String instanceId);
	 /**
	  * 指派
	  * @param taskId
	  * @param transactor
	  */
	 public void assign(Long taskId, String transactor);
	 
	 public String getTaskUrl(Task task);
	 
	 public WorkflowTask getLastCompletedTaskByTaskName(String workflowId,
				Long companyId,String taskName);

	 	/**
		 * 获得委托人集合[loginName,name]
		 * @param workflowId
		 * @return
		 */
		public List<String[]> getActivityTaskPrincipalsDetail(String instanceId,Long companyId);
		/**
		 * 查询当前环节其它的待办理的任务集合，除当前传过来的任务
		 * @param workflowId 实例id
		 * @param taskId 任务id
		 * @param taskName 任务名称
		 * @return 任务列表
		 */
		 public List<WorkflowTask> getActivityTasksByNameWithout(String workflowId,Long taskId,String taskName);
		 /**
		 * 根据办理人查找待办理的委托任务
		 * @param workflowId
		 * @param transactor
		 * @return
		 */
		 public List<WorkflowTask> getActivityTrustorTasksByTransactor(String workflowId,String transactor,Long taskId);
}
