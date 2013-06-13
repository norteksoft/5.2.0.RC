package com.norteksoft.task.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.base.enumeration.ProcessState;

@Repository
public class WorkflowTaskDao extends HibernateDao<WorkflowTask, Long>{
	
	public WorkflowTask getTask(Long taskId){
		return findUniqueNoCompanyCondition("from WorkflowTask t where t.id=?", taskId);
	}
	
	public List<WorkflowTask> getWorkflowTasks(String instanceId, String taskName) {
		return find("from WorkflowTask t where t.processInstanceId = ? and t.name = ? and t.paused=?", instanceId, taskName,false);
	}

	public WorkflowTask getFirstTaskByInstance(Long companyId, String instanceId, String transactor) {
		return findUnique("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ? and t.transactor = ?", 
				companyId, instanceId, transactor);
	}
	
	public void deleteTaskByProcessId(String processInstanceId, Long companyId) {
		this.createQuery("delete  from WorkflowTask t where  t.processInstanceId = ? and t.companyId = ? ", processInstanceId,companyId).executeUpdate();
	}
	
	public void deleteTasksByName(Long companyId, String instanceId, String[] names){
		for(String name : names){
			createQuery("delete from WorkflowTask t where t.companyId=? and t.processInstanceId=? and t.name=?", 
					companyId, instanceId,StringUtils.trim(name)).executeUpdate();
		}
	}
	
	public List<WorkflowTask> getTasksByName(Long companyId, String instanceId, String name){
		return  this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and t.name=? and t.effective=? and t.paused=?", 
				companyId, instanceId,name,true,false);
	}
	
	public List<WorkflowTask> getNotCompleteTasksByName(Long companyId, String instanceId, String name){
		return  this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and t.name=? and t.effective=? and (t.active<>? and t.active<>?)", 
				companyId, instanceId,name,true,TaskState.COMPLETED.getIndex(), TaskState.CANCELLED.getIndex());
	}

	public List<WorkflowTask> getNoAssignTasksByName(Long companyId, String instanceId, String taskName,Integer groupNum) {
		return this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ? and t.name = ? and t.active <> ? and  t.effective=? and t.groupNum=?", 
				companyId, instanceId, taskName, TaskState.ASSIGNED.getIndex(), true,groupNum);
	}
	
	public Page<WorkflowTask> getDelegateTasks(Long companyId, String loginName, Page<WorkflowTask> page){
		Page<WorkflowTask> result = new Page<WorkflowTask>(page.getPageSize(), true);
		result.setPageNo(page.getPageNo());
		//result.setOrder(page.getOrder());
		//result.setOrderBy(page.getOrderBy());
		this.findPage(result,"from WorkflowTask t where t.companyId=? and t.trustor=? and t.paused=?",companyId, loginName,false);
		page = new Page<WorkflowTask>();
		page.setResult(result.getResult());
		//page.setOrder(result.getOrder());
		//page.setOrderBy(result.getOrderBy());
		page.setPageNo(result.getPageNo());
		page.setPageSize(result.getPageSize());
		page.setTotalCount(result.getTotalCount());
		return page;
	}
	
	public Integer getDelegateTasksNum(Long companyId, String loginName){
		Object o = createQuery("select count(t) from WorkflowTask t where t.companyId=? and t.trustor=?  and t.paused=?", companyId, loginName,false).uniqueResult();
		return Integer.valueOf(o.toString());
	}
	
	public Integer getDelegateTasksNum(Long companyId, String loginName, Boolean isCompleted){
		String hql = "select count(t) from WorkflowTask t where t.companyId=? and t.effective = true and t.trustor=? and (t.active=? or t.active=?  or t.active=?  or t.active=?) and t.paused=?";
		Object o = 0;
		if(isCompleted){
			o = createQuery(hql, companyId, loginName, TaskState.COMPLETED.getIndex(), TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false).uniqueResult();
		}else{
			hql = "select count(t) from WorkflowTask t where t.companyId=? and t.effective = true and t.trustor=? and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=? ";
			o = createQuery(hql, companyId, loginName, TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.DRAW_WAIT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false).uniqueResult();
		}
		return Integer.valueOf(o.toString());
	}
	
	public Integer getTrusteeTasksNum(Long companyId, String loginName, Boolean isCompleted){
		String hql = "select count(t) from WorkflowTask t where t.companyId=?  and t.effective = true and t.transactor=? and (t.active=? or t.active=?  or t.active=?  or t.active=?) and t.paused=? and t.trustor is not null";
		Object o = 0;
		if(isCompleted){
			o = createQuery(hql, companyId, loginName, TaskState.COMPLETED.getIndex(), TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false).uniqueResult();
		}else{
			hql = "select count(t) from WorkflowTask t where t.companyId=?  and t.effective = true and t.transactor=? and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=? and t.trustor is not null";
			o = createQuery(hql, companyId, loginName, TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.DRAW_WAIT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false).uniqueResult();
		}
		return Integer.valueOf(o.toString());
	}
	
	public Page<WorkflowTask> getDelegateTasks(Long companyId, String loginName, Page<WorkflowTask> page, boolean isEnd){
		Page<WorkflowTask> result = new Page<WorkflowTask>(page.getPageSize(), true);
		result.setPageNo(page.getPageNo());
		//result.setOrder(page.getOrder());
		//result.setOrderBy(page.getOrderBy());
		String hql = "from WorkflowTask t where t.companyId=? and t.trustor=?  and t.effective = true and (t.active=? or t.active=? or t.active=?  or t.active=?) and t.paused=?";
		if(isEnd){
			this.findPage(result,hql,companyId, loginName, TaskState.COMPLETED.getIndex(), TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false);			
		}else{
			hql = "from WorkflowTask t where t.companyId=? and t.trustor=?  and t.effective = true and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=? ";
			this.findPage(result,hql,companyId, loginName, TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.DRAW_WAIT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false);
		}
		page = new Page<WorkflowTask>();
		page.setResult(result.getResult());
		//page.setOrder(result.getOrder());
		//page.setOrderBy(result.getOrderBy());
		page.setPageNo(result.getPageNo());
		page.setPageSize(result.getPageSize());
		page.setTotalCount(result.getTotalCount());
		return page;
	}
	
	public Page<WorkflowTask> getTaskAsTrustee(Long companyId, String loginName, Page<WorkflowTask> page, boolean isEnd){
		Page<WorkflowTask> result = new Page<WorkflowTask>(page.getPageSize(), true);
		result.setPageNo(page.getPageNo());
		String hql = "from WorkflowTask t where t.companyId=? and t.transactor=? and t.visible = true and t.effective = true and (t.active=? or t.active=? or t.active=?  or t.active=?) and t.paused=? and t.trustor is not null";
		if(isEnd){
			this.findPage(result,hql,companyId, loginName, TaskState.COMPLETED.getIndex(), TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false);			
		}else{
			hql = "from WorkflowTask t where t.companyId=? and  t.transactor=?  and t.visible = true and t.effective = true and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=? and t.trustor is not null";
			this.findPage(result,hql,companyId, loginName, TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.DRAW_WAIT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false);
		}
		page = new Page<WorkflowTask>();
		page.setResult(result.getResult());
		page.setPageNo(result.getPageNo());
		page.setPageSize(result.getPageSize());
		page.setTotalCount(result.getTotalCount());
		return page;
	}

	public List<WorkflowTask> getAllTasksByInstance(Long companyId, String instanceId){
		return this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ? and t.paused=?", 
				companyId, instanceId,false);
	}
	
	/**
	 * 活动该流程实例当前任务,当前任务为待领取或待办理且该任务不是分发给的任务也不是特事特办任务，且任务是有效的
	 * @param instanceId 实例id
	 * @param companyId 公司id
	 * @return 任务列表
	 */
	public List<WorkflowTask> getActivityTasks(String instanceId,Long companyId) {
		if(companyId==null){
			return this.find("from WorkflowTask t where t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6 or t.active=1) and t.distributable=? and t.effective=?  and t.paused=? order by t.specialTask DESC", 
					 instanceId,false,true,false);
		}else{
			return this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6 or t.active=1) and t.distributable=? and t.effective=?  and t.paused=? order by t.specialTask DESC", 
					companyId, instanceId,false,true,false);
		}
	}
	
	/**
	 * 加签或者减签时候取活动任务
	 * @param instanceId
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getActivitySignTasks(String instanceId,Long companyId) {
		if(companyId==null){
			return this.find("from WorkflowTask t where t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6 or t.active=1) and t.distributable=? and t.effective=?  and t.paused=? and t.processingMode!=? order by t.specialTask DESC", 
					 instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}else{
			return this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6 or t.active=1) and t.distributable=? and t.effective=?  and t.paused=? and t.processingMode!=? order by t.specialTask DESC", 
					companyId, instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}
	}
	
	/**
	 * 查询办理人的当前任务
	 * @param instanceId
	 * @param companyId
	 * @return
	 */
	public WorkflowTask getMyTask(String instanceId,Long companyId,String loginName) {
		List<WorkflowTask> tasks=this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ? and t.transactor=? and (t.active=0 or t.active=1) and t.paused=?", 
				companyId, instanceId,loginName,false);
		if(tasks.size()>0){
			return tasks.get(0);
		}
		return null;
	}
	
	public List<String> getCountersignByProcessInstanceId(String processInstanceId,TaskProcessingMode processingMode){
		return find( "select distinct t.name from WorkflowTask t where t.processInstanceId=? and t.processingMode=? and t.paused=? ", processInstanceId,processingMode,false);
	}
	
	/**
	 * 自定义流程中取会签环节名称
	 * @param processInstanceId
	 * @param processingMode
	 * @return
	 */
	public List<String> getSignByProcessInstanceId(String processInstanceId,TaskProcessingMode processingMode){
		return find( "select t.name from WorkflowTask t where t.companyId = ? and t.processInstanceId=? and t.processingMode=? and t.paused=? group by t.name ",ContextUtils.getCompanyId(), processInstanceId,processingMode,false);
	}
	
	public List<WorkflowTask> getCountersignByProcessInstanceIdResult(String processInstanceId,String taskName,TaskProcessingResult result){
		return find( "from WorkflowTask t where t.processInstanceId=?  and t.name=? and t.taskProcessingResult=?  and t.paused=? ", processInstanceId,taskName,result,false);
	}
	/**
	 * 获得审批任务组数
	 * @param processInstanceId
	 * @param taskName
	 * @param result
	 * @return
	 */
	public List<Integer> getGroupNumByTaskName(String processInstanceId,String taskName){
		return find( "select t.groupNum from WorkflowTask t where t.processInstanceId=?  and t.name=?   and t.paused=? and t.companyId = ? group by t.groupNum", processInstanceId,taskName,false,ContextUtils.getCompanyId());
	}
	/**
	 * 流程有效的办理人
	 * @param companyId
	 * @param instanceId
	 * @return
	 */
	public List<String> getParticipantsTransactor(Long companyId,
			String instanceId) {
		return find("select t.transactor from WorkflowTask t where t.companyId = ? and t.processInstanceId = ? and t.active=? and t.effective=? and t.paused=?", 
				companyId, instanceId, TaskState.COMPLETED.getIndex(), true,false);
	}
	
	public List<WorkflowTask> getCountersigns(String instanceId,String taskName){
		return find( "from WorkflowTask t where t.processInstanceId=?  and t.name=? and t.active=0  and t.paused=?", instanceId,taskName,false);
	}
	
	public List<WorkflowTask> getCountersigns(Long taskId,String instanceId,String taskName){
		return find( "from WorkflowTask t where t.processInstanceId=?  and t.name=? and t.active=0 and t.id!=? and t.paused=?", instanceId,taskName,taskId,false);
	}
	
	public List<String> getCountersignsHandler(String instanceId,String taskName,Integer activie){
		return find( " select t.transactor from WorkflowTask t where t.processInstanceId=?  and t.name=? and t.active=? and t.paused=?", instanceId,taskName,activie,false);
	}
	
	public void deleteCountersignHandler(String instanceId,String taskName,Collection<String> users){
		String hql = "delete WorkflowTask t where t.processInstanceId=?  and t.name=?  and (t.transactor=? or t.trustor=?)  and t.paused=?  and t.processingMode!=? ";
		for(String user:users){
			this.batchExecute(hql, instanceId,taskName,user,user,false,TaskProcessingMode.TYPE_READ);
		}
	}

	public List<String> getHandledTransactors(String workflowId) {
		String hql = "select t.transactor from WorkflowTask t where t.processInstanceId=? and t.active=? and t.effective=?  and t.paused=?";
		return find(hql, workflowId,TaskState.COMPLETED.getIndex(),true,false);
	}
	//获得流程所有办理人
	public List<String> getAllHandleTransactors(String workflowId) {
		String hql = "select t.transactor from WorkflowTask t where t.processInstanceId=? and t.effective=? and t.paused=?";
		return find(hql, workflowId,true,false);
	}

	public List<WorkflowTask> getNeedReminderTasks() {
		String hql = "from Task t where (t.active=? or t.active=? or t.active=?) and t.duedate<>0  and t.paused=?";
		return find(hql, TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_TRANSACT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false);
	}

	public List<WorkflowTask> getCompletedTasks(String workflowId,
			Long companyId) {
		 String hql = "from WorkflowTask t where t.processInstanceId=? and t.companyId=? and t.active=?  and t.paused=? order by t.id";
		return find(hql, workflowId,companyId,TaskState.COMPLETED.getIndex(),false);
	}

	public List<WorkflowTask> getNeedReminderTasks(String loginName,
			Long companyId) {
		String hql = "from Task t where (t.active=? or t.active=? or t.active=?) and t.duedate<>0 and t.reminderStyle is not null and t.transactor=? and t.companyId=?  and t.paused=?";
		return find(hql, TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_TRANSACT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),loginName,companyId,false);
	}

	public List<WorkflowTask> getTasksOrderByWdfName(String definitionName,
			String loginName) {
		Assert.notNull(ContextUtils.getCompanyId(),"查询流程定义中某个办理人的任务时，公司id不能为null");
		String hql = " from Task t where t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=? or t.active=?  or t.active=?) and t.groupName=?  and t.paused=? order by t.createdTime desc";
		return find(hql,ContextUtils.getCompanyId(), loginName,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),definitionName,false);
	}
	public List<WorkflowTask> getCompletedTasksByTaskName(String workflowId,
			Long companyId,String taskName) {
		 String hql = "from WorkflowTask t where t.processInstanceId=? and t.companyId=? and t.active=? and t.name=?  and t.paused=? order by t.id";
		return find(hql, workflowId,companyId,TaskState.COMPLETED.getIndex(),taskName,false);
	}
	public Integer getNotCompleteTasksNumByTransactor(Long companyId, String loginName){
		return Integer.parseInt(createQuery(
				"select count(t) from WorkflowTask t where t.companyId = ? and t.visible=true and  t.transactor=? and (t.active=? or t.active=? or t.active=?  or t.active=?)  and t.paused=?", 
				companyId, loginName, TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false).uniqueResult().toString());
	}
	/**
	 * 查找公司中所有的超期任务
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getOverdueTasks(Long companyId) {
		 String hql = "from WorkflowTask t where t.companyId=? and (t.active=? or t.active=? or t.active=?  or t.active=?) and t.lastReminderTime is not null  and t.paused=?  order by t.createdTime desc";
		return find(hql, companyId,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false);
	}
	
	/**
	 * 查找当前办理人所有的超期任务的总数
	 * @param companyId
	 * @param transactorName
	 * @return
	 */
	public Integer getOverdueTasksNumByTransactor(Long companyId,String transactorName) {
		return Integer.parseInt(createQuery(
				"select count(t) from WorkflowTask t where t.companyId=? and (t.active=? or t.active=? or t.active=?  or t.active=?) and t.transactor=? and t.lastReminderTime is not null  and t.paused=?", 
				companyId, TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),transactorName,false).uniqueResult().toString());
	}
	/**
	 * 查找公司中所有的超期任务,包括已完成的任务
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getTotalOverdueTasks(Long companyId) {
		 String hql = "from WorkflowTask t where t.companyId=? and  t.lastReminderTime is not null  and t.paused=? order by t.createdTime desc";
		return find(hql, companyId,false);
	}
	/**
	 * 查找当前办理人所有的超期任务的总数,包括已完成的任务
	 * @param companyId
	 * @param transactorName
	 * @return
	 */
	public Integer getTotalOverdueTasksNumByTransactor(Long companyId,String transactorName) {
		return Integer.parseInt(createQuery(
				"select count(t) from WorkflowTask t where t.companyId=? and t.transactor=? and t.lastReminderTime is not null  and t.paused=?", 
				companyId, transactorName,false).uniqueResult().toString());
	}
	
	/**
	 * 获得“他人已领取”状态的任务
	 * @param companyId   公司id
	 * @param instanceId  流程实例id
	 * @param name        任务名称
	 * @return
	 */
	public List<WorkflowTask> getHasDrawOtherTasks(Long companyId, String instanceId, String name) {
		 String hql = "from WorkflowTask t where t.processInstanceId=? and t.companyId=? and t.name=? and t.active=?  and t.paused=? order by t.id";
		return find(hql, instanceId,companyId,name,TaskState.HAS_DRAW_OTHER.getIndex(),false);
	}
	
	/**
	 * 分页查询用户所有未完成任务
	 * @param page
	 */
	public void getAllTasksByUser(Long companyId, String loginName, Page<WorkflowTask> page){
			String hql="from WorkflowTask t where t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=? or t.active=?  or t.active=?)  and t.paused=? order by t.createdTime desc";
			this.searchPageByHql(page, hql.toString(),companyId, loginName,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false);
	}
	
	
	/**
	 * 分页查询用户已完成任务
	 * @param companyId
	 * @param loginName
	 * @param page
	 */
	public void getCompletedTasksByUser(Long companyId, String loginName, Page<WorkflowTask> page) {
		String hql="from WorkflowTask t where t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=? or t.active=? or t.active=?)  and t.paused=? order by t.transactDate desc";
		this.searchPageByHql(page, hql.toString(),companyId, loginName, TaskState.COMPLETED.getIndex(), TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false);
	}
	
	
	/**
	 * 分页查询用户已完成任务
	 * @param companyId
	 * @param loginName
	 * @param page
	 */
	public void getReadTasksByUser(Long companyId, String loginName, Page<WorkflowTask> page) {
		findPage(page, "from WorkflowTask t where t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=?) and t.read=true  and t.paused=?", 
				companyId, loginName, TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),false);
	}
	
	/**
	 * 获得所有办理人除当前任务名称的办理人
	 * @param task
	 * @return
	 */
	public List<String> getTransactorsExceptTask(WorkflowTask task){
		String hql="select distinct t.transactor from WorkflowTask t where t.name!=? and t.companyId=? and t.processInstanceId=? and t.active=?  and t.paused=?";
		return this.find(hql, task.getName(),task.getCompanyId(),task.getProcessInstanceId(),TaskState.COMPLETED.getIndex(),false);
	}
	/**
	 * 根据“任务组”查询任务列表
	 * @param companyId
	 * @param instanceId
	 * @param taskName
	 * @return
	 */
	public List<WorkflowTask> getTaskOrderByGroupNum(Long companyId,String instanceId,String taskName){
		String hql="from WorkflowTask t where t.name=? and t.companyId=? and t.processInstanceId=? and t.groupNum!=null  and t.paused=? order by t.groupNum desc";
		return this.find(hql,taskName,companyId,instanceId,false);
	}
	
	/**
	 * 活动该流程实例当前任务,当前任务为待领取或待办理且该任务不是分发给的任务也不是特事特办任务，且任务是有效的
	 * @param instanceId 实例id
	 * @param companyId 公司id
	 * @return 任务列表
	 */
	public List<WorkflowTask> getActivityTasksByName(String instanceId,Long companyId,String taskName) {
		if(companyId==null){
			return this.find("from WorkflowTask t where t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.name=?  and t.paused=? order by t.specialTask DESC", 
					 instanceId,false,true,taskName,false);
		}else{
			return this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.name=?  and t.paused=? order by t.specialTask DESC", 
					companyId, instanceId,false,true,taskName,false);
		}
	}
	
	/**
	 * 活动该流程实例当前任务的所有办理人,当前任务不是委托任务
	 * @param instanceId 实例id
	 * @param companyId 公司id
	 * @return 办理人列表
	 */
	public List<String[]> getActivityTaskTransactors(String instanceId,Long companyId) {
		if(companyId==null){
			return this.find("select t.transactor,t.transactorName,t.name from WorkflowTask t where t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.trustor=null  and t.paused=? and t.processingMode!=?  order by t.specialTask DESC", 
					 instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}else{
			return this.find("select t.transactor,t.transactorName,t.name from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.trustor=null  and t.paused=? and t.processingMode!=?  order by t.specialTask DESC", 
					companyId, instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}
	}
	
	/**
	 * 活动该流程实例当前任务的所有委托人,当前任务是委托任务
	 * @param instanceId 实例id
	 * @param companyId 公司id
	 * @return 办理人列表
	 */
	public List<String> getActivityTaskPrincipals(String instanceId,Long companyId) {
		if(companyId==null){
			return this.find("select t.trustor from WorkflowTask t where t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.trustor!=null  and t.paused=?  and t.processingMode!=?  order by t.specialTask DESC", 
					 instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}else{
			return this.find("select t.trustor from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.trustor!=null  and t.paused=?  and t.processingMode!=?    order by t.specialTask DESC", 
					companyId, instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}
	}
	/**
	 * 活动该流程实例当前任务的所有委托人,当前任务是委托任务
	 * @param instanceId 实例id
	 * @param companyId 公司id
	 * @return 办理人列表[loginName,name]
	 */
	public List<String[]> getActivityTaskPrincipalsDetail(String instanceId,Long companyId) {
		if(companyId==null){
			return this.find("select t.trustor,t.trustorName from WorkflowTask t where t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.trustor!=null  and t.paused=?  and t.processingMode!=?  order by t.specialTask DESC", 
					instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}else{
			return this.find("select t.trustor,t.trustorName from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.trustor!=null  and t.paused=?  and t.processingMode!=?    order by t.specialTask DESC", 
					companyId, instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}
	}
	
	public List<String> getCompletedTaskNames(String workflowId,
			Long companyId) {
		 String hql = "select t.name from WorkflowTask t where t.processInstanceId=? and t.companyId=? and t.active=?  and t.paused=? order by t.id";
		return find(hql, workflowId,companyId,TaskState.COMPLETED.getIndex(),false);
	}
	/**
	 * 获得实例中暂停的任务
	 * @param workflowId
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getPauseTasksByInstance(String workflowId,
			Long companyId){
		 String hql = "from WorkflowTask t where t.processInstanceId=? and t.companyId=? and t.paused=?";
			return find(hql, workflowId,companyId,true);
	}
	
	/**
	 * 批量移除任务中根据办理人查询当前任务列表
	 * @param tasks
	 * @param transactorName
	 * @param typeId
	 * @param defCode
	 * @param wfdId
	 */
	
	public void getActivityTasksByTransactorName(Page<WorkflowTask> tasks,Long typeId, String defCode,Long wfdId){
		StringBuilder hql = new StringBuilder("select wt ");
		hql.append(" from ").append("  WorkflowTask wt,WorkflowInstance wi  ").append("where wi.processInstanceId=wt.processInstanceId and wi.processInstanceId=wt.executionId and wi.processState<>? and wt.companyId = ? and ( wt.active=0 or wt.active=4 ) and wt.distributable=? and wt.effective=? ");
		List<Object> objs = new ArrayList<Object>();
		objs.add(ProcessState.UNSUBMIT);
		objs.add(ContextUtils.getCompanyId());
		objs.add(false);
		objs.add(true);
		if(wfdId!=null && wfdId.intValue() != 0){
			hql.append("and wi.workflowDefinitionId=? ");
			objs.add(wfdId);
		}
		if(typeId!=null && typeId.intValue() != 0){
			hql.append("and wi.typeId = ? ");
			objs.add(typeId);
		}
		if(StringUtils.isNotEmpty(defCode)){
			hql.append("and wi.processCode=? ");
			objs.add(defCode);
		}
		hql.append(" order by wt.transactDate desc ");
		this.searchPageByHql(tasks, hql.toString(),objs.toArray());
	}
	
	/**
	 * 根据实例集合活动未办理的任务
	 * @param instanceIds 实例id的集合
	 * @param taskName 任务名称
	 * @param recieveUser 委托的受托人登录名
	 * @param consignor 委托的委托人登录名
	 * @return
	 */
	public List<WorkflowTask> getTasksByInstance(List<String> instanceIds,String taskName,String recieveUser,String consignor,Long companyId){
		StringBuilder hql=new StringBuilder("from WorkflowTask t where t.companyId = ? and ( t.active=0 or t.active=4 or t.active=1) and t.distributable=? and t.effective=? and t.paused=? and t.transactor = ? and t.trustor=? ");
		Object[] objs=new Object[6+instanceIds.size()] ;
		if(StringUtils.isNotEmpty(taskName)&&!"0".equals(taskName)){
			objs=new Object[7+instanceIds.size()];
		}
		int i=0;
		objs[0]=companyId;
		objs[1]=false;
		objs[2]=true;
		objs[3]=false;
		objs[4]=recieveUser;
		objs[5]=consignor;
		i=6;
		if(StringUtils.isNotEmpty(taskName)&&!"0".equals(taskName)){
			hql.append("and t.name=? ");
			objs[6]=taskName;
			i=7;
		}
		if(instanceIds.size()>0){
			hql.append("and (");
		}
		int j=0;
		for(String instanceId:instanceIds){
			hql.append(" t.processInstanceId=? ");
			if(j<instanceIds.size()-1)hql.append("or ");
			objs[i++]=instanceId;
			j++;
		}
		if(instanceIds.size()>0){
			hql.append(")");
		}
		return this.find(hql.toString(), objs);
	}
	
	
	public List<String> getActiveTaskNameWithoutSpecial(String instanceId){
			return this.find("select distinct(t.name) from WorkflowTask t where t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6 or t.active=1) and t.distributable=? and t.effective=?  and t.paused=? and t.specialTask=?", 
					 instanceId,false,true,false,false);
	}
	/**
	 * 根据用户获得自己所有已完成的流程名称
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Object[]> getAllCompleteTaskGroupNames(Long companyId,String loginName){
		String hql="select t.groupName,count(t.groupName) from WorkflowTask t where t.companyId = ? and t.transactor = ? and t.visible = true and t.active=? and t.paused=? and t.groupName!=null group by t.groupName";
		return find(hql, companyId, loginName,TaskState.COMPLETED.getIndex(),false);
	}
	/**
	 * 根据用户获得自己所有已完成的流程名称
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Object[]> getAllCancelTaskGroupNames(Long companyId,String loginName){
		String hql="select t.groupName,count(t.groupName) from WorkflowTask t where t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=?) and t.paused=? and t.groupName!=null group by t.groupName";
		return find(hql, companyId, loginName,TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),false);
	}
	
	/**
	 * 根据用户获得自己所有流程名称
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Object[]> getAllActiveTaskGroupNames(Long companyId,String loginName){
		String hql="select t.groupName,count(t.groupName) from WorkflowTask t where  t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=? and t.groupName!=null group by t.groupName";
		return find(hql, companyId, loginName,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false);
	}
	/**
	 * 根据用户获得自己所有已完成的流程名称
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Object[]> getAllCompleteTaskCustomTypes(Long companyId,String loginName){
		String hql="select t.customType,count(t.customType) from WorkflowTask t where t.companyId = ? and t.transactor = ? and t.visible = true and t.active=? and t.paused=? and t.customType!=null group by t.customType";
		return find(hql, companyId, loginName,TaskState.COMPLETED.getIndex(),false);
	}
	/**
	 * 根据用户获得自己所有已完成的流程名称
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Object[]> getAllCancelTaskCustomTypes(Long companyId,String loginName){
		String hql="select t.customType,count(t.customType) from WorkflowTask t where t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=?) and t.paused=? and t.customType!=null group by t.customType";
		return find(hql, companyId, loginName, TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),false);
	}
	
	/**
	 * 根据用户获得自己所有流程名称
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Object[]> getAllActiveTaskCustomTypes(Long companyId,String loginName){
		String hql="select t.customType,count(t.customType) from WorkflowTask t where  t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=? and t.customType!=null group by t.customType";
		return find(hql, companyId, loginName,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false);
	}
	/**
	 * 退回功能中需要得到环节名称taskName的最新完成的任务
	 * @param workflowId
	 * @param companyId
	 * @param taskName
	 * @return
	 */
	public WorkflowTask getLastCompletedTaskByTaskName(String workflowId,
			Long companyId,String taskName) {
		 String hql = "from WorkflowTask t where t.processInstanceId=? and t.companyId=? and t.active=? and t.name=?  and t.paused=?  and t.specialTask=? order by t.id desc ";
		List<WorkflowTask> tasks= find(hql, workflowId,companyId,TaskState.COMPLETED.getIndex(),taskName,false,false);
		if(tasks.size()>0) return tasks.get(0);
		return null;
	}
	
	/**
	 * 查询当前环节其它的待办理的任务集合，除当前传过来的任务
	 * @param workflowId 实例id
	 * @param taskId 任务id
	 * @param taskName 任务名称
	 * @return 任务列表
	 */
	public List<WorkflowTask> getActivityTasksByNameWithout(String workflowId,Long taskId,String taskName) {
		return this.find("from WorkflowTask t where  t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6 or t.active=1) and t.distributable=? and t.effective=?  and t.paused=? and t.id<>? and t.name=? order by t.createdTime DESC", 
				workflowId,false,true,false,taskId,taskName);
	}
	/**
	 * 根据办理人查找待办理的委托任务
	 * @param workflowId
	 * @param transactor
	 * @return
	 */
	public List<WorkflowTask> getActivityTrustorTasksByTransactor(String workflowId,String transactor,Long taskId) {
		return this.find("from WorkflowTask t where  t.processInstanceId = ?  and t.active=? and t.visible=? and t.transactor=? and t.trustor is not null and t.id<>? and t.distributable=? and t.effective=?  and t.paused=? and t.specialTask=? order by t.createdTime DESC", 
				workflowId,TaskState.WAIT_TRANSACT.getIndex(),false,transactor,taskId,false,true,false,false);
	}
}
