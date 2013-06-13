package com.norteksoft.task.webservice;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.product.orm.Page;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.task.entity.Task;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.task.service.WorkflowTaskManager;

//@WebService(endpointInterface = "com.norteksoft.task.webservice.WorkflowTaskService")
//@Transactional
public class WorkflowTaskServiceImpl implements WorkflowTaskService{

	private WorkflowTaskManager taskManager;
	
	@Autowired
	public void setWorkflowTaskManager(WorkflowTaskManager workflowTaskManager) {
		taskManager = workflowTaskManager;
	}
	
	public void saveTask(WorkflowTask workflowTask) {
		taskManager.saveTask(workflowTask);
	}

	public List<String> getTaskNamesByInstance(Long companyId, String instanceId) {
		return taskManager.getTaskNamesByInstance(companyId, instanceId);
	}
	
	public Page<WorkflowTask> getDelegateTasks(
			Long companyId, String loginName, Page<WorkflowTask> page){
		return taskManager.getDelegateTasks(companyId, loginName, page);
	}
	
	public Page<WorkflowTask> getDelegateTasksByActive(Long companyId, String loginName, Page<WorkflowTask> page, boolean isEnd){
		return taskManager.getDelegateTasksByActive(companyId, loginName, page, isEnd);
	}
	
	public Page<WorkflowTask> getTaskAsTrustee(Long companyId, String loginName, Page<WorkflowTask> page, boolean isEnd){
		return taskManager.getTaskAsTrustee(companyId, loginName, page, isEnd);
	}
	
	public Integer getDelegateTasksNum(Long companyId, String loginName){
		return taskManager.getDelegateTasksNum(companyId, loginName);
	}
	
	public Integer getTrusteeTasksNum(Long companyId, String loginName, Boolean isCompleted){
		return taskManager.getTrusteeTasksNum(companyId, loginName,isCompleted);
	}
	
	public Integer getDelegateTasksNumByActive(Long companyId, String loginName, Boolean isCompleted){
		return taskManager.getDelegateTasksNumByActive(companyId, loginName, isCompleted);
	}

	public List<WorkflowTask> getAllTasksByInstance(Long companyId, String instanceId){
		return taskManager.getAllTasksByInstance(companyId, instanceId);
	}
	public void deleteTask(WorkflowTask task) {
		taskManager.deleteTask(task);
		
	}

	public void deleteTasksByName(Long companyId, String instanceId, String[] taskName) {
		taskManager.deleteTasksByName(companyId, instanceId, taskName);
	}

	public List<WorkflowTask> getTasksByName(Long companyId, String instanceId, String taskName) {
		return taskManager.getTasksByName(companyId, instanceId, taskName);
	}

	public List<WorkflowTask> getNoAssignTasksByName(Long companyId, String instanceId, String taskName,Integer groupNum) {
		return taskManager.getNoAssignTasksByName(companyId, instanceId, taskName,groupNum);
	}

	public void saveTasks(List<WorkflowTask> workflowTasks){
		taskManager.saveTasks(workflowTasks);
	}
	
	public WorkflowTask getFirstTaskByInstance(Long companyId, String instanceId, String transactor) {
		return taskManager.getFirstTaskByInstance(companyId, instanceId, transactor);
	}
	public List<WorkflowTask> getWorkflowTasks(String instanceId, String taskName) {
		return taskManager.getWorkflowTasks(instanceId, taskName);
	}

	public WorkflowTask getTask(Long id) {
		return taskManager.getTask(id);
	}

	public void deleteTaskByProcessId(String processId,Long companyId){
		taskManager.deleteTaskByProcessId(processId,companyId);
	}

	public void endTasks(String instanceId, Long companyId) {
		taskManager.endTasks(instanceId, companyId);
	}
	
	public void compelEndTasks(String instanceId, Long companyId) {
		taskManager.compelEndTasks(instanceId, companyId);
	}

	public List<WorkflowTask> getActivityTasks(String instanceId, Long companyId) {
		return taskManager.getActivityTasks(instanceId, companyId);
	}
	
	public List<WorkflowTask> getActivitySignTasks(String instanceId, Long companyId) {
		return taskManager.getActivitySignTasks(instanceId, companyId);
	}

	public WorkflowTask getMyTask(String instanceId,Long companyId,String loginName){
	    	return taskManager.getMyTask(instanceId, companyId, loginName);
	}
	
	public List<WorkflowTask> getTasksByActivity(Long companyId,
			String executionId, String taskName) {
		return taskManager.getTasksByActivity(companyId, executionId, taskName);
	}
	
	public List<String> getParticipantsTransactor(Long companyId, String instanceId){
		return taskManager.getParticipantsTransactor(companyId, instanceId);
	}

	public List<String> getCountersignByProcessInstanceId(
			String processInstanceId, TaskProcessingMode processingMode) {
		return taskManager.getCountersignByProcessInstanceId(processInstanceId, processingMode);
	}
	
	/**
	 * 自定义流程中取会签环节名称
	 */
	public List<String> getSignByProcessInstanceId(
			String processInstanceId, TaskProcessingMode processingMode) {
		return taskManager.getSignByProcessInstanceId(processInstanceId, processingMode);
	}

	public List<WorkflowTask> getCountersignByProcessInstanceIdResult(
			String processInstanceId, String taskName, TaskProcessingResult result) {
		return taskManager.getCountersignByProcessInstanceIdResult(processInstanceId, taskName, result);
	}

	public void deleteWorkflowTask(List<Long> ids) {
		taskManager.deleteWorkflowTask(ids);		
	}

	public List<WorkflowTask> getCountersigns(Long id) {
		return taskManager.getCountersigns(id);
	}
	public List<String> getCountersignsHandler(Long id,Integer handlingState){
		return taskManager.getCountersignsHandler(id,handlingState);
	}

	public void deleteCountersignHandler(Long taskId, Collection<String> users) {
		taskManager.deleteCountersignHandler(taskId,users);
	}

	public String receive(Long taskId) {
		return taskManager.receive(taskId);
	}
	
	public String abandonReceive(Long taskId) {
		return taskManager.abandonReceive(taskId);
	}

	public Set<String> getHandledTransactors(String workflowId) {
		return taskManager.getHandledTransactors(workflowId);
	}

	/**
	 * 得到所有需要催办的task
	 */
	public List<WorkflowTask> getNeedReminderTasks(){
		return taskManager.getNeedReminderTasks();
	}

	public List<WorkflowTask> getProcessCountersigns(Long id) {
		return taskManager.getProcessCountersigns(id);
	}

	public List<WorkflowTask> getCompletedTasks(String workflowId,
			Long companyId) {
		return taskManager.getCompletedTasks( workflowId,
				 companyId);
	}

	public List<WorkflowTask> getTasksOrderByWdfName(String definitionName,
			String loginName) {
		return taskManager.getTasksOrderByWdfName(definitionName, loginName);
	}

	public List<WorkflowTask> getCompletedTasksByTaskName(String workflowId,
			Long companyId, String taskName) {
		return taskManager.getCompletedTasksByTaskName(workflowId, companyId, taskName);
	}
	/**
	 * 根据当前用户查询未完成任务总数
	 * @param companyId 公司id
	 * @param loginName 当前用户登录名
	 * @return 未完成任务总数
	 */
	public Integer getTasksNumByTransactor(Long companyId, String loginName){
		return taskManager.getTasksNumByTransactor(companyId, loginName);
	}
	/**
	 * 查找公司中所有的超期任务
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getOverdueTasks(Long companyId) {
		return taskManager.getOverdueTasks(companyId);
	}
	
	/**
	 * 查找当前办理人所有的超期任务的总数
	 * @param companyId
	 * @param transactorName
	 * @return map :key为办理人登录名，value为超期次数
	 */
	public Map<String,Integer> getOverdueTasksNumByTransactor(Long companyId) {
		return taskManager.getOverdueTasksNumByTransactor(companyId);
	}
	/**
	 * 查找公司中所有的超期任务,包括已完成的任务
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getTotalOverdueTasks(Long companyId){
		return taskManager.getTotalOverdueTasks(companyId);
	}
	/**
	 * 查找当前办理人所有的超期任务的总数,包括已完成的任务
	 * @param companyId
	 * @param transactorName
	 * @return
	 */
	public Map<String,Integer> getTotalOverdueTasksNumByTransactor(Long companyId){
		return taskManager.getTotalOverdueTasksNumByTransactor(companyId);
	}

	public Set<String> getAllHandleTransactors(String workflowId) {
		return taskManager.getAllHandleTransactors(workflowId);
	}

	public void getAllTasksByUser(Long companyId, String loginName,
			Page<WorkflowTask> page) {
		taskManager.getAllTasksByUser(companyId, loginName, page);
	}

	public List<WorkflowTask> getAllTasksByUser(Long companyId, String loginName) {
		return taskManager.getAllTasksByUser(companyId, loginName);
	}
	public List<String> getTransactorsExceptTask(Long taskId) {
		return taskManager.getTransactorsExceptTask(taskId);
	}
	public List<WorkflowTask> getTaskOrderByGroupNum(Long companyId,
			String instanceId, String taskName) {
		return taskManager.getTaskOrderByGroupNum(companyId, instanceId, taskName);
	}

	public List<WorkflowTask> getActivityTasksByName(String instanceId,
			Long companyId, String taskName) {
		return taskManager.getActivityTasksByName(instanceId, companyId, taskName);
	}

	public List<String[]> getActivityTaskTransactors(String instanceId,
			Long companyId) {
		return taskManager.getActivityTaskTransactors(instanceId,companyId);
	}

	public List<String> getActivityTaskPrincipals(String instanceId,
			Long companyId) {
		return taskManager.getActivityTaskPrincipals(instanceId,companyId);
	}

	public List<String> getCompletedTaskNames(String workflowId, Long companyId) {
		return taskManager.getCompletedTaskNames(workflowId, companyId);
	}

	public void continueTasks(String instanceId, Long companyId) {
		taskManager.continueTasks(instanceId, companyId);
		
	}

	public void pauseTasks(String instanceId, Long companyId) {
		taskManager.pauseTasks(instanceId, companyId);
		
	}

	public void getActivityTasksByTransactorName(Page<WorkflowTask> tasks,
			Long typeId, String defCode, Long wfdId) {
		taskManager.getActivityTasksByTransactorName(tasks,  typeId, defCode, wfdId);
		
	}

	public List<WorkflowTask> getTasksByInstance(List<String> instanceIds,
			String taskName, String recieveUser, String consignor,
			Long companyId) {
		return taskManager.getTasksByInstance(instanceIds, taskName, recieveUser, consignor, companyId);
	}

	public List<String> getActiveTaskNameWithoutSpecial(String instanceId) {
		return taskManager.getActiveTaskNameWithoutSpecial(instanceId);
	}

	public void assign(Long taskId, String transactor) {
		taskManager.assign(taskId, transactor);
	}
	public List<Integer> getGroupNumByTaskName(String processInstanceId,String taskName){
		return taskManager.getGroupNumByTaskName(processInstanceId, taskName);
	}
	

	public String getTaskUrl(Task task) {
		return taskManager.getTaskUrl(task);
	}
	
	public WorkflowTask getLastCompletedTaskByTaskName(String workflowId,
			Long companyId, String taskName) {
		return taskManager.getLastCompletedTaskByTaskName(workflowId, companyId, taskName);
	}

	public List<String[]> getActivityTaskPrincipalsDetail(String instanceId,
			Long companyId) {
		return taskManager.getActivityTaskPrincipalsDetail(instanceId, companyId);
	}

	public List<WorkflowTask> getActivityTasksByNameWithout(String workflowId,
			Long taskId, String taskName) {
		return taskManager.getActivityTasksByNameWithout(workflowId, taskId, taskName);
	}
	public List<WorkflowTask> getActivityTrustorTasksByTransactor(
			String workflowId, String transactor,Long taskId) {
		return taskManager.getActivityTrustorTasksByTransactor(workflowId, transactor,taskId);
	}
}
