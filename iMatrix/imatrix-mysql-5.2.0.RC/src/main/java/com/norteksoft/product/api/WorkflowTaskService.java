package com.norteksoft.product.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.norteksoft.product.api.entity.WorkflowInstance;
import com.norteksoft.product.api.entity.WorkflowTask;
import com.norteksoft.product.orm.Page;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.wf.base.enumeration.CompleteTaskTipType;
import com.norteksoft.wf.engine.client.FormFlowable;

/**
 * 公开提供给用户使用的工作流任务api
 * @author wurong
 */
public interface WorkflowTaskService {

	/**
	 * 根据任务id查询任务
	 * @param taskId
	 * @return 工作流任务
	 */
	public WorkflowTask getTask(Long taskId);
	
	
	 /**
     * 根据TaskId完成任务，没有办理意见
     * @param taskId 任务id
     * @param result :TaskTransact 同意/不同意...交办...
     * @param allOriginalUsers 下一环节原办理人
     * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
     */
    public CompleteTaskTipType completeWorkflowTask(Long taskId, TaskProcessingResult result,String allOriginalUsers);
    /**
     * 根据TaskId完成任务，没有办理意见
     * @param taskId 任务id
     * @param result :TaskTransact 同意/不同意...交办...
     * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
     */
    public CompleteTaskTipType completeWorkflowTask(Long taskId, TaskProcessingResult result);
    
    /**
     * 完成任务 不需要特事特办
     * @param task 任务
     * @param result :TaskTransact 同意/不同意...交办...
     * @param allOriginalUsers 下一环节原办理人
     * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
     */
	public CompleteTaskTipType completeWorkflowTask(WorkflowTask task, TaskProcessingResult result,String allOriginalUsers);
	/**
     * 完成任务 不需要特事特办
     * @param task 任务
     * @param result :TaskTransact 同意/不同意...交办...
     * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
     */
	public CompleteTaskTipType completeWorkflowTask(WorkflowTask task, TaskProcessingResult result);
	
	
	/**
	 * 完成交互的任务
	 * @param taskId 任务id
	 * @param allOriginalUsers 下一环节原办理人
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
	 */
	public CompleteTaskTipType completeInteractiveWorkflowTask(Long taskId ,String allOriginalUsers);
	/**
	 * 完成交互的任务
	 * @param taskId
	 * @param transcators 下一环节办理人
	 * @param allOriginalUsers 下一环节原办理人
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
	 */
	public CompleteTaskTipType completeInteractiveWorkflowTask(Long taskId,Collection<String> transcators,String allOriginalUsers);
	
	
	/**
	 * 完成交互的任务
	 * @param taskId
	 * @param allOriginalUsers 下一环节原办理人
	 * @param transcators 下一环节办理人
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
	 */
	public CompleteTaskTipType completeInteractiveWorkflowTask(Long taskId,String allOriginalUsers,String... transcators);
	
	/**
	 * 完成交互的任务
	 * @param task
	 * @param allOriginalUsers 下一环节原办理人
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
	 */
	public CompleteTaskTipType completeInteractiveWorkflowTask(WorkflowTask task,String allOriginalUsers);
	
	@Deprecated
	public CompleteTaskTipType completeInteractiveWorkflowTask(com.norteksoft.task.entity.WorkflowTask task,String allOriginalUsers);
	/**
	 * 完成交互的任务 
	 * @param task
	 * @param transcators 下一环节办理人
	 * @param allOriginalUsers 下一环节原办理人
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
	 */
	public CompleteTaskTipType completeInteractiveWorkflowTask(WorkflowTask task,  Collection<String> transcators,String allOriginalUsers);
	
	@Deprecated
	public CompleteTaskTipType completeInteractiveWorkflowTask(com.norteksoft.task.entity.WorkflowTask task,  Collection<String> transcators,String allOriginalUsers);
	
	/**
	 * 完成交互的任务
	 * @param task
	 * @param allOriginalUsers 下一环节原办理人
	 * @param transcators 下一环节办理人
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
	 */
	public CompleteTaskTipType completeInteractiveWorkflowTask(WorkflowTask task,String allOriginalUsers,String... transcators);
	
	@Deprecated
	public CompleteTaskTipType completeInteractiveWorkflowTask(com.norteksoft.task.entity.WorkflowTask task,String allOriginalUsers,String... transcators);
	/**
     * 查询下环节任务的办理人
     * @param taskId 任务id
     * @return Map<String[办理模式，任务名]  ,List<String[用户名称，用户登录名称] >>
     */
    public Map<String[], List<String[]>> getNextTasksCandidates(Long taskId);
    
    
    @Deprecated
    public Long getFormIdByTask(com.norteksoft.task.entity.WorkflowTask task);
    
    public Long getFormIdByTask(WorkflowTask task);
    
    @Deprecated
    public Long getDataIdByTask(com.norteksoft.task.entity.WorkflowTask task);
    
    
    public Long getDataIdByTask(WorkflowTask task);
    /**
     * 加签
     * @param taskId 任务id
     * @param users 需要加签的人
     * @deprecated 替换为<code>addSigner(Long taskId, Collection<String> users);</code>
     */
    @Deprecated
    public void additional(Long taskId, Collection<String> users);
    
    /**
     * 加签
     * @param taskId 任务id
     * @param users 需要加签的人
     * @deprecated 替换为<code>addSigner(Long taskId, Collection<String> users);</code>
     */
    public void addSign(Long taskId, Collection<String> users);
    
    /**
     * 加签
     * @param taskId 任务id
     * @param users 需要加签的人
     */
    public void addSigner(Long taskId, Collection<String> users);
    /**
    * 加签
    * @param taskId 任务id
    * @param users 需要加签的人
    * @deprecated 替换为<code>addSigner(Long taskId,  String... users);</code>
    */
    @Deprecated
    public void additional(Long taskId, String... users);
    
    /**
     * 加签
     * @param taskId 任务id
     * @param users 需要加签的人
     * @deprecated 替换为<code>addSigner(Long taskId,  String... users);</code>
     */
    public void addSign(Long taskId,  String... users);
    
    /**
     * 加签
     * @param taskId 任务id
     * @param users 需要加签的人
     * 
     */
    public void addSigner(Long taskId,  String... users);
    
    /**
     * 减签
     * @param taskId 任务id
     * @param users 需要减签的人
     * @deprecated 替换为<code>removeSigner(Long taskId,  Collection<String> users);</code>
     */
    public void reduceHandlers(Long taskId,Collection<String> users);
    
    /**
     * 减签
     * @param taskId 任务id
     * @param users 需要减签的人
     */
    public void removeSigner(Long taskId,Collection<String> users);
    
    /**
     * 减签
     * @param taskId 任务id
     * @param users 需要减签的人
     * @deprecated 替换为<code>removeSigner(Long taskId,String... users);</code>
     */
    public void reduceHandlers(Long taskId,String... users);
    
    /**
     * 减签
     * @param taskId 任务id
     * @param users 需要减签的人
     */
    public void removeSigner(Long taskId,String... users);
    
    /**
     * 获得该会签环节的会签办理人
     * @param taskId 任务id
     * @param handlingState 该办理人对任务的完成状态  {@link com.norteksoft.task.entity.Task#COMPLETED}}、 {@link com.norteksoft.task.entity.Task#WAIT_TRANSACT}}
     * @return 办理人登录名的集合
     * @deprecated 替换为<code>getCountersignTransactors(Long taskId,Integer handlingState);</code>
     */
    public List<String> getCountersignHandlers(Long taskId,Integer handlingState);
    
    /**
     * 获得该会签环节的会签办理人
     * @param taskId 任务id
     * @param handlingState 该办理人对任务的完成状态  {@link com.norteksoft.task.entity.Task#COMPLETED}}、 {@link com.norteksoft.task.entity.Task#WAIT_TRANSACT}}
     * @return 办理人登录名的集合
     */
    public List<String> getCountersignTransactors(Long taskId,Integer handlingState);
    
    /**
	 * 取回任务。当任务刚办理完下一环节办理人还没有开始办理任务的时候，可以取回任务。
	 * @param taskId 任务id
	 * @return 取回任务的提示信息。
	 */
	public String retrieve(Long taskId);
	
	/**
	 * 领取
	 * @param taskId 任务id
	 * @return 当前任务不是“待领取”状态时，会返回不需领取；当前任务领取成功时，返回领取成功
	 */
	public String drawTask(Long taskId);
	/**
	 * 放弃领取
	 * @param taskId
	 * @return
	 */
	public String abandonReceive(Long taskId);
	
	/**
	 * 交办 或 指派 <br/>
     * 将任务交办或指派给指定的人员
     * @param taskId 需交办或指派的任务
     * @param assignee 受理人
     */
	public void assign(Long taskId, String assignee);
	
	/**
	 * 流程实例能退回到的环节名称
	 * @param taskId 任务ID
	 * @return List<String> size等于0时表示当前不能退回到任何环节
	 * @deprecated  替换为 <code>getReturnableTaskNames(Long taskId)</code>
	 */
	@Deprecated
	public List<String> canBackNames(Long taskId);
	
	/**
	 * 流程实例能退回到的环节名称
	 * @param taskId 任务ID
	 * @return List<String> size等于0时表示当前不能退回到任何环节
	 * @deprecated  替换为 <code>getReturnableTaskNames(Long taskId)</code>
	 */
	public List<String> backToTaskNames(Long taskId);
	
	/**
	 * 流程实例能退回到的环节名称
	 * @param taskId 任务ID
	 * @return List<String> size等于0时表示当前不能退回到任何环节
	 */
	public List<String> getReturnableTaskNames(Long taskId);
	
	/**
	 * 退回到某环节
	 * @param taskId 任务ID
	 * @param backTo 退回到的环节名称
	 * @deprecated  替换为 <code>returnTaskTo(Long taskId, String taskName)</code>
	 */
	public void goBack(Long taskId, String backTo);
	
	/**
	 * 退回到某环节
	 * @param taskId 任务ID
	 * @param taskName 退回到的环节名称
	 */
	public void returnTaskTo(Long taskId, String taskName);
	
	/**
	 * 查询办理人的当前任务
	 * @param entity 走流程的表单
	 * @param loginname 办理人的登录名
	 * @return 如果流程已经启动 ，返回当前任务；否则，返回null
	 * @deprecated  替换为 <code>getActiveTaskByLoginName(FormFlowable entity,String loginName)</code>
	 */
	public WorkflowTask getMyTask(FormFlowable entity,String loginName);
	
	/**
	 * 查询办理人的当前任务
	 * @param entity 走流程的表单
	 * @param loginName 办理人的登录名
	 * @return 如果流程已经启动 ，返回当前任务；否则，返回null
	 */
	public WorkflowTask getActiveTaskByLoginName(FormFlowable entity,String loginName);
	
	/**
	 * 完成分发任务
	 * @param taskId 
	 * @param receivers 分发到的用户登录名列表
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
	 */
	public CompleteTaskTipType completeDistributeTask(Long taskId, List<String> receivers);
	
	/**
	 * 完成分发任务
	 * @param taskId 
	 * @param receivers 分发到的用户登录名列表
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
	 */
	public CompleteTaskTipType completeDistributeTask(Long taskId, String... receivers);
	
	/**
	 * 返回查看会签结果的权限
	 */
	@Deprecated
	public boolean viewMeetingResultRight(Long taskId );
	
	
	
	/**
	 * 返回查看投票结果的权限
	 */
	@Deprecated
	public boolean viewVoteResultRight(Long taskId );
	
	/**
	 * 完成选择环节的任务
	 * @param taskId 当前任务的id
	 * @param transitionName 用户选择的流向的名字
	 * @return 返回完成任务后的提示类型
	 * @deprecated 替换为<code>selectActivity(Long taskId, String transitionName)</code>
	 */
	public CompleteTaskTipType completeTacheChoice(Long taskId, String transitionName);
	
	/**
	 * 完成选择环节的任务
	 * @param taskId 当前任务的id
	 * @param transitionName 用户选择的流向的名字
	 * @return 返回完成任务后的提示类型
	 */
	public CompleteTaskTipType selectActivity(Long taskId, String transitionName);
	
	/**
	 * 查询流程定义中某个办理人的任务
	 * @param definitionName 流程定义名
	 * @param loginName 办理人登录名
	 * @return 任务列表
	 */
	public List<WorkflowTask> getWorkflowTasksByDefinitonName(String definitionName,String loginName);
	/**
	 * 生成抄送任务
	 * @param taskId 当前任务的id
	 * @param transactors 将该任务抄送给谁
	 * @deprecated 替换为<code>createCopyTasks(Long taskId, List<String> transactors,String title,String url)</code>
	 */
	@Deprecated
	public void createCopyTaches(Long taskId,List<String> transactors );
	
	/**
	 * 生成抄送任务
	 * 参数title为null或空字符串时，生成的该抄送任务的标题为“(抄送)原任务标题”;参数url为null或空字符串时，生成的抄送任务的办理页面为该url
	 * @param taskId 当前任务的id
	 * @param transactors 将该任务抄送给谁
	 * @deprecated 替换为<code>createCopyTasks(Long taskId, List<String> transactors,String title,String url)</code>
	 */
	public void createCopyTaches(Long taskId, List<String> transactors,String title,String url) ;
	
	/**
	 * 生成抄送任务
	 * 参数title为null或空字符串时，生成的该抄送任务的标题为“(抄送)原任务标题”;参数url为null或空字符串时，生成的抄送任务的办理页面为该url
	 * @param taskId 当前任务的id
	 * @param transactors 将该任务抄送给谁
	 */
	public void createCopyTasks(Long taskId, List<String> transactors,String title,String url) ;
	
	/**
	 * 根据用户查询未完成任务总数
	 * @param companyId 公司id
	 * @param loginName 当前用户登录名
	 * @return 未完成任务总数
	 * @deprecated 替换为<code>getActiveTaskCountByTransactor(String loginName)</code>
	 */
	@Deprecated
	public Integer getTasksNumByTransactor(Long companyId, String loginName);
	
	/**
	 * 根据用户查询未完成任务总数
	 * @param loginName 用户登录名
	 * @return 未完成任务总数
	 * @deprecated 替换为<code>getActiveTaskCountByTransactor(String loginName)</code>
	 */
	public Integer getTasksNumByTransactor(String loginName);
	
	/**
	 * 根据用户查询未完成任务总数
	 * @param loginName 用户登录名
	 * @return 未完成任务总数
	 */
	public Integer getActiveTaskCountByTransactor(String loginName);
	
	/**
	 * 查找公司中所有的超期任务
	 * @param companyId
	 * @return
	 * @deprecated 替换为<code>getActiveOverdueTasks()</code>
	 */
	@Deprecated
	public List<WorkflowTask> getOverdueTasks(Long companyId) ;
	
	/**
	 * 查找公司中所有的超期任务
	 * @return 过期的任务集合
	 * @deprecated 替换为<code>getActiveOverdueTasks()</code>
	 */
	public List<WorkflowTask> getOverdueTasks() ;
	
	/**
	 * 查找公司中所有的超期任务
	 * @return 过期的任务集合
	 */
	public List<WorkflowTask> getActiveOverdueTasks();
	
	/**
	 * 查找当前办理人所有的超期任务的总数
	 * @param companyId
	 * @return map :key为办理人登录名，value为超期次数
	 * @deprecated
	 * 替换为<code>getOverdueTasksNumByTransactor()</code>
	 */
	@Deprecated
	public Map<String,Integer> getOverdueTasksNumByTransactor(Long companyId) ;
	/**
	 * 查找当前办理人所有的超期任务的总数
	 * @param companyId
	 * @return map :key为办理人登录名，value为超期次数
	 */
	public Map<String,Integer> getOverdueTasksNumByTransactor() ;
	/**
	 * 查找公司中所有的超期任务,包括已完成的任务
	 * @param companyId
	 * @return
	 * @deprecated 替换为<code>getAllOverdueTasks()</code>
	 */
	@Deprecated
	public List<WorkflowTask> getTotalOverdueTasks(Long companyId) ;
	
	/**
	 * 查找公司中所有的超期任务,包括已完成的任务
	 * @return 所有超期任务的集合
	 * @deprecated 替换为<code>getAllOverdueTasks()</code>
	 */
	public List<WorkflowTask> getTotalOverdueTasks() ;
	
	/**
	 * 查找公司中所有的超期任务,包括已完成的任务
	 * @return 所有超期任务的集合
	 */
	public List<WorkflowTask> getAllOverdueTasks() ;
	
	/**
	 * 查询每个人的超期任务数,包括已完成的任务
	 * @param companyId
	 * @return
	 * @deprecated
	 * 替换为<code>getOverdueTaskCountGroupByTransactor()</code>
	 */
	public Map<String,Integer> getTotalOverdueTasksNumByTransactor(Long companyId);
	
	/**
	 * 查询每个人的超期任务数,包括已完成的任务
	 * @return 返回值为HashMap,key为用户登录名,value为超期任务总数
	 * @deprecated
	 * 替换为<code>getOverdueTaskCountGroupByTransactor()</code>
	 */
	public Map<String,Integer> getTotalOverdueTasksNumByTransactor();
	
	/**
	 * 查询每个人的超期任务数,包括已完成的任务
	 * @return 返回值为HashMap,key为用户登录名,value为超期任务总数
	 */
	public Map<String,Integer> getOverdueTaskCountGroupByTransactor();
	
	/**
	 * 分页查询用户所有未完成任务
	 * @param page
	 * @deprecated 
	 * 替换为 <code>getUnDoneTasksByUser( String loginName, Page<Task> page)</code>
	 */
	@Deprecated
	public void getAllTasksByUser(Long companyId,String loginName, Page<com.norteksoft.task.entity.WorkflowTask> page);
	
	/**
	 * 查询用户所有未完成任务(不是分页)
	 * @param page
	 * @deprecated   替换为 <code>getUnDoneTasksByUser((String loginName))</code>
	 */
	@Deprecated
	public List<com.norteksoft.task.entity.WorkflowTask> getAllTasksByUser( Long companyId,String loginName);
	/**
	 * 分页查询用户所有未完成任务
	 * @param loginName 用户登录名
	 * @param page
	 */
	@Deprecated
	public void getUnDoneTasksByUser( String loginName, Page<com.norteksoft.task.entity.WorkflowTask> page);
	
	/**
	 * 查询用户所有未完成任务(不是分页)
	 * @param loginName 用户登录名
	 * @return 用户所有未完成任务集合
	 * @deprecated   替换为 <code>getActiveTasksByLoginName(String loginName)</code>
	 */
	public List<WorkflowTask> getUnDoneTasksByUser(String loginName);
	
	/**
	 * 查询用户所有未完成任务(不是分页)
	 * @param loginName 用户登录名
	 * @return 用户所有未完成任务集合
	 */
	public List<WorkflowTask> getActiveTasksByLoginName(String loginName);
	
	/**
	 * 是否是第一环节
	 * @param taskId
	 * @return true表示是第一环节，反之不是
	 */
	public boolean isFirstTask(Long taskId);
	
	/**
	 * 获得所有办理人除当前任务名称的办理人
	 * @param taskId
	 * @return
	 * @deprecated   替换为 <code>getTransactorsExcludeGivenTask(Long taskId)</code>
	 */
	public List<String> getTransactorsExceptTask(Long taskId);
	
	/**
	 * 获得所有办理人除当前任务名称的办理人
	 * @param taskId
	 * @return
	 */
	public List<String> getTransactorsExcludeGivenTask(Long taskId);
	
	/**
	 * 指派任务
	 * @param taskId
	 * @param transcators
	 * @return
	 * @deprecated   替换为 <code>assign(Long taskId, String transactor)</code>
	 */
	public CompleteTaskTipType assignTask(Long taskId, Collection<String> transcators);
	/**
	 * 指派任务
	 * @param taskId
	 * @param transcator
	 * @return
	 * @deprecated   替换为 <code>assign(Long taskId, String transactor)</code>
	 */
	public CompleteTaskTipType assignTask(Long taskId, String transcator);
	
	/**
	 * 设置任务为已读
	 * @param taskId
	 */
	public void setTaskRead(Long taskId);
	/**
	 * 环节跳转功能
	 * @param workflowId 实例id
	 * @param backTo   要跳转到的环节名称
	 * @param transactors
	 * @return
	 */
	public CompleteTaskTipType taskJump(String workflowId, String backTo,Long companyId);
	
	/**
	 * 查询当前任务是否需要选择环节
	 * @param task 当前任务
	 * @deprecated 替换为 <code>getOptionalTasks(WorkflowTask task)</code>
	 */
	@Deprecated
	public CompleteTaskTipType isNeedChoiceTache(com.norteksoft.task.entity.WorkflowTask task);
	
	public CompleteTaskTipType isNeedChoiceTache(WorkflowTask task);
	
	/**
	 * 查询当前任务是否需要选择环节
	 * @param task 当前任务
	 * @return 如果需要选择环节，将返回枚举TACHE_CHOICE_URL；该枚举实例的getCanChoiceTaches()方法将返回可供选择的环节
	 */
	@Deprecated
	public CompleteTaskTipType getOptionalTasks(com.norteksoft.task.entity.WorkflowTask task);
	
	public CompleteTaskTipType getOptionalTasks(WorkflowTask task);
	/**
	 * 查询当前待办理的任务的办理人
	 * @param entity  实体
	 * @return
	 */
	public List<String[]> getActivityTaskTransactors( FormFlowable entity);
	
	/**
	 * 获得委托人集合[
	 * @param entity 实体
	 * @return
	 */
	public List<String> getActivityTaskPrincipals(FormFlowable entity);
	/**
	 * 获得委托人集合[loginName,name]
	 * @param entity 实体
	 * @return
	 */
	public List<String[]> getActivityTaskPrincipalsDetail(FormFlowable entity);
	
	
	/**
	 * 获得所有已办理人
	 * @param entity
	 * @return
	 * @deprecated 替换为 <code>getHandledTransactors(FormFlowable entity)</code>
	 */
	public Set<String> getHandledTransactors(FormFlowable entity);
	
	/**
	 * 获得所有已办理人
	 * @param entity
	 * @return
	 */
	public Set<String> getCompletedTaskTransactor(FormFlowable entity);
	
	/**
	 * 获得所有已办理人
	 * @param entity
	 * @return
	 * @deprecated 替换为 <code>getCompletedTaskTransactor(Long taskId)</code>
	 */
	public Set<String> getHandledTransactors(Long taskId);
	
	/**
	 * 获得所有已办理人
	 * @param entity
	 * @return
	 */
	public Set<String> getCompletedTaskTransactor(Long taskId);
	/**
	 * 保存任务
	 * @param task
	 */
	public void saveTask(WorkflowTask task);
	
	@Deprecated
	public void saveTask(com.norteksoft.task.entity.WorkflowTask task);
	/**
	*获得环节扩展属性
	*/
	public Map<String,String> getExtendFields(FormFlowable entity);
	/**
	*获得环节扩展属性
	*/
	public Map<String,String> getExtendFields(Long taskId);
	
	/**
	 * 查询当前待办理的任务的办理人
	 * @param entity  实体
	 * @return
	 */
	public List<String[]> getActivityTaskTransactors(String workflowId);
	/**
	 * 获得受托人集合
	 * @param workflowId
	 * @return
	 */
	public List<String> getActivityTaskPrincipals(String workflowId);
	/**
	 * 获得受托人集合[loginName,name]
	 * @param workflowId
	 * @return
	 */
	public List<String[]> getActivityTaskPrincipalsDetail(String workflowId);
	
	public Map getDataByTaskId(Long taskId);
	
	/**
	 * 查询当前待办理的任务的集合（包括特事特办的任务）
	 * @param entity  实体
	 * @return
	 */
	public List<WorkflowTask> getActivityTasks( FormFlowable entity);
	
	/**
	 * 任务退回,退回到上一环节
	 * @param entity
	 */
	public String returnTask(Long taskId);
	
	
	@Deprecated
	public void endInstance(com.norteksoft.wf.engine.entity.WorkflowInstance workflow);
	
	public void endInstance(WorkflowInstance workflow);
	/**
	 * 根据流程实例查询当前待办理的任务的集合（包括特事特办的任务），一般用于自定义表单时
	 * @param workflowId  流程实例id
	 * @return
	 */
	public List<WorkflowTask> getActivityTasks( String workflowId);
	/**
	 * 查询办理人的当前任务，一般用于自定义表单时
	 * @param entity 走流程的表单
	 * @param loginName 办理人的登录名
	 * @return 如果流程已经启动 ，返回当前任务；否则，返回null
	 */
	public WorkflowTask getActiveTaskByLoginName(String workflowId,String loginName);
	
}
