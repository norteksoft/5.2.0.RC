package com.norteksoft.task.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.task.entity.Task;

@Repository
public class TaskDao extends HibernateDao<Task, Long>{

	
	public void findFinishTaskForPage(Page<Task> tasks, List<String> names, List<String> values) {
		StringBuffer hql = new StringBuffer("from Task t where t.companyId = ? and (t.active=? or t.active=? or t.active=? or t.active=? or t.active=?) ");
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(0, ContextUtils.getCompanyId());
		parameters.add(1, TaskState.WAIT_TRANSACT.getIndex());
		parameters.add(2, TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex());
		parameters.add(3, TaskState.COMPLETED.getIndex());
		parameters.add(4, TaskState.CANCELLED.getIndex());
		parameters.add(5, TaskState.ASSIGNED.getIndex());
		for (int i=0; i<names.size(); i++) {
			String parameterName = names.get(i);
			if("groupName".equals(parameterName) || "title".equals(parameterName)){
				hql.append("and t." + parameterName + " like ? ");
				parameters.add("%" + values.get(i) + "%");
			}
			if("creatorName".equals(parameterName)){
				hql.append("and t." + parameterName + " = ? ");
				parameters.add(values.get(i));
			}
			if("transactDate".equals(parameterName)){
				hql.append("and (t." + parameterName + " between ? and ?) ");
				List<Date> twoDays = getDays(values.get(i));
				parameters.add(twoDays.get(0));
				parameters.add(twoDays.get(1));
			}
		}
		hql.append("order by t.id desc");
		findPage(tasks, hql.toString(), parameters);
	}

	private List<Date> getDays(String value){
		List<Date> betweenDay = new ArrayList<Date>(2);
		DateFormat df = DateFormat.getDateInstance();
		Date today = null;
		try {
			today = df.parse(value);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date tomorrow = new Date();
		tomorrow.setTime(today.getTime()+1*24*3600*1000);
		betweenDay.add(0, today);
		betweenDay.add(1, tomorrow);
		return betweenDay;
	}
	
	public void findUNFinishTaskForPage(Page<Task> tasks, List<String> names, List<String> values) {
		StringBuffer hql = new StringBuffer("from Task t where t.companyId = ? and (t.active=? or t.active=? or t.active=? or t.active=?) ");
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(0, ContextUtils.getCompanyId());
		parameters.add(1, TaskState.DRAW_WAIT.getIndex());
		parameters.add(2, TaskState.WAIT_TRANSACT.getIndex());
		parameters.add(3, TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex());
		parameters.add(4, TaskState.WAIT_CHOICE_TACHE.getIndex());
		for (int i=0; i<names.size(); i++) {
			String parameterName = names.get(i);
			if("groupName".equals(parameterName) || "title".equals(parameterName)){
				hql.append("and t." + parameterName + " like ? ");
				parameters.add("%" + values.get(i) + "%");
			}
			if("creatorName".equals(parameterName)){
				hql.append("and t." + parameterName + " = ? ");
				parameters.add(values.get(i));
			}
		}
		hql.append("and t.transactor=?");
		parameters.add(ContextUtils.getLoginName());
		hql.append("order by t.id desc");
		findPage(tasks, hql.toString(), parameters);
	}
	/**
	 * 分页查询用户所有未完成任务
	 * @param page
	 */
	public void getAllTasksByUser(Long companyId, String loginName, Page<Task> page){
			String hql="from Task t where t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=? or t.active=?  or t.active=?)  and t.paused=? order by t.createdTime desc";
			this.searchPageByHql(page, hql.toString(),companyId, loginName,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false);
	}
	
	/**
	 * 分页查询用户所有未完成任务
	 * @param page
	 */
	public void getAllTasksByUserType(Long companyId, String loginName, Page<Task> page,String typeName){
		String hql=null;
		if(StringUtils.isEmpty(typeName)){
			hql="from Task t where t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=? or t.active=?  or t.active=?)  and t.paused=? order by t.createdTime desc";
			this.searchPageByHql(page, hql.toString(),companyId, loginName,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false);
		}else{
			hql="from Task t where t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=? or t.active=?  or t.active=?)  and t.paused=? and t.category=?  order by t.createdTime desc";
			this.searchPageByHql(page, hql.toString(),companyId, loginName,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false,typeName);
		}
	}
	
	/**
	 * 分页查询用户已完成任务
	 * @param companyId
	 * @param loginName
	 * @param page
	 */
	public void getCompletedTasksByUserType(Long companyId, String loginName, Page<Task> page,String typeName) {
		String hql=null;
		if(StringUtils.isEmpty(typeName)){
			hql="from Task t where t.companyId = ? and t.transactor = ? and t.visible = true and t.active=? and t.paused=? and t.transactDate != null  order by t.transactDate desc";
			this.searchPageByHql(page, hql.toString(),companyId, loginName, TaskState.COMPLETED.getIndex(),false);
		}else{
			hql="from Task t where t.companyId = ? and t.transactor = ? and t.visible = true and t.active=? and t.paused=? and t.category=? and t.transactDate != null  order by t.transactDate desc";
			this.searchPageByHql(page, hql.toString(),companyId, loginName, TaskState.COMPLETED.getIndex(),false,typeName);
		}
	}
	
	/**
	 * 分页查询用户已取消任务
	 * @param companyId
	 * @param loginName
	 * @param page
	 */
	public void getCanceledTasksByUserType(Long companyId, String loginName, Page<Task> page,String typeName) {
		String hql=null;
		if(StringUtils.isEmpty(typeName)){
			hql="from Task t where t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=? or t.active=?)  and t.paused=? order by t.transactDate desc";
			this.searchPageByHql(page, hql.toString(),companyId, loginName, TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false);
		}else{
			hql="from Task t where t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=? or t.active=?)  and t.paused=? and t.category=? order by t.transactDate desc";
			this.searchPageByHql(page, hql.toString(),companyId, loginName, TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false,typeName);
		}
	}
	/**
	 * 根据用户获得自己所有已完成的任务类型名称
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Object[]> getAllCompleteTaskTypeInfos(Long companyId,String loginName){
		String hql="select t.category,count(t.category) from Task t where t.companyId = ? and t.transactor = ? and t.visible = true and t.active=?  and t.paused=? and t.category!=null group by t.category";
		return find(hql, companyId, loginName,TaskState.COMPLETED.getIndex(),false);
	}
	/**
	 * 根据用户获得自己所有已完成的任务类型名称
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Object[]> getAllCancelTaskTypeInfos(Long companyId,String loginName){
		String hql="select t.category,count(t.category) from Task t where t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=?) and t.paused=? and t.category!=null group by t.category";
		return find(hql, companyId, loginName,TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),false);
	}
	
	/**
	 * 根据用户获得自己所有类型名称
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Object[]> getAllActiveTaskTypeInfos(Long companyId,String loginName){
		String hql="select t.category,count(t.category) from Task t where  t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=? and t.category!=null group by t.category";
		return find(hql, companyId, loginName,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false);
	}
	
	/**
	 * 根据用户获得自己所有任务个数
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public Integer getAllActiveTasksNum(Long companyId,String loginName){
		String hql="select count(t) from Task t where t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=?";
		Object obj=createQuery(hql, companyId, loginName,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false).uniqueResult();
		if(obj!=null)return Integer.parseInt(obj.toString());
		return 0;
	}
	
	/**
	 * 根据用户获得自己所有已完成的任务类型名称
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public Integer getAllCompleteTasksNum(Long companyId,String loginName){
		String hql="select count(t) from Task t where t.companyId = ? and t.transactor = ? and t.visible = true and t.active=?  and t.paused=?";
		Object obj=createQuery(hql, companyId, loginName,TaskState.COMPLETED.getIndex(),false).uniqueResult();
		if(obj!=null)return Integer.parseInt(obj.toString());
		return 0;
	}
	/**
	 * 根据用户获得自己所有已完成的任务类型名称
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public Integer getAllCancelTasksNum(Long companyId,String loginName){
		String hql="select count(t) from Task t where t.companyId = ? and t.transactor = ? and t.visible = true and ( t.active=? or t.active=?) and t.paused=?";
		Object obj=createQuery(hql, companyId, loginName, TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),false).uniqueResult();
		if(obj!=null)return Integer.parseInt(obj.toString());
		return 0;
	}
	/**
	 * 分页查询用户所有未完成任务
	 * @param page
	 */
	public void getAllTasksByGroupName(Long companyId, String loginName, Page<Task> page,String typeName){
		String hql=null;
		if(StringUtils.isEmpty(typeName)){
			hql=" from Task t where  t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=? or t.active=?  or t.active=?)  and t.paused=? order by t.createdTime desc";
			this.searchPageByHql(page, hql.toString(),companyId, loginName,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false);
		}else{
			hql=" from Task t where   t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=? or t.active=?  or t.active=?)  and t.paused=? and t.groupName=?  order by t.createdTime desc";
			this.searchPageByHql(page, hql.toString(),companyId, loginName,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false,typeName);
		}
	}
	/**
	 * 分页查询用户所有未完成任务
	 * @param page
	 */
	public void getAllTasksByCustomType(Long companyId, String loginName, Page<Task> page,String typeName){
		String hql=null;
		if(StringUtils.isEmpty(typeName)){
			hql="select t from Task t,WorkflowTask wt where t.id=wt.id and t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=? or t.active=?  or t.active=?)  and t.paused=? order by t.createdTime desc";
			this.searchPageByHql(page, hql.toString(),companyId, loginName,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false);
		}else{
			hql="select t from Task t,WorkflowTask wt where  t.id=wt.id and  t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=? or t.active=?  or t.active=?)  and t.paused=? and wt.customType=?  order by t.createdTime desc";
			this.searchPageByHql(page, hql.toString(),companyId, loginName,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false,typeName);
		}
	}
	/**
	 * 根据自定义类型分页查询用户所有已完成任务
	 * @param page
	 */
	public void getCompletedTasksByCustomType(Long companyId, String loginName, Page<Task> page,String typeName){
		String hql=null;
		if(StringUtils.isEmpty(typeName)){
			hql="select t from Task t,WorkflowTask wt where t.id=wt.id and t.companyId = ? and t.transactor = ? and t.visible = true and  t.active=?  and t.paused=? order by t.createdTime desc";
			this.searchPageByHql(page, hql.toString(),companyId, loginName,TaskState.COMPLETED.getIndex(),false);
		}else{
			hql="select t from Task t,WorkflowTask wt where  t.id=wt.id and  t.companyId = ? and t.transactor = ? and t.visible = true and t.active=?  and t.paused=? and wt.customType=?  order by t.createdTime desc";
			this.searchPageByHql(page, hql.toString(),companyId, loginName,TaskState.COMPLETED.getIndex(),false,typeName);
		}
	}
	
	/**
	 * 根据流程名称分页查询用户所有已完成任务
	 * @param page
	 */
	public void getCompletedTasksByGroupName(Long companyId, String loginName, Page<Task> page,String typeName){
		String hql=null;
		if(StringUtils.isEmpty(typeName)){
			hql=" from Task t where  t.companyId = ? and t.transactor = ? and t.visible = true and t.active=?   and t.paused=? order by t.createdTime desc";
			this.searchPageByHql(page, hql.toString(),companyId, loginName,TaskState.COMPLETED.getIndex(),false);
		}else{
			hql=" from Task t where   t.companyId = ? and t.transactor = ? and t.visible = true and t.active=?  and t.paused=? and t.groupName=?  order by t.createdTime desc";
			this.searchPageByHql(page, hql.toString(),companyId, loginName,TaskState.COMPLETED.getIndex(),false,typeName);
		}
	}
	/**
	 * 根据自定义类型分页查询用户所有已完成任务
	 * @param page
	 */
	public void getCancelTasksByCustomType(Long companyId, String loginName, Page<Task> page,String typeName){
		String hql=null;
		if(StringUtils.isEmpty(typeName)){
			hql="select t from Task t,WorkflowTask wt where t.id=wt.id and t.companyId = ? and t.transactor = ? and t.visible = true and  (t.active=? or t.active=? or t.active=?)  and t.paused=? order by t.createdTime desc";
			this.searchPageByHql(page, hql.toString(),companyId, loginName,TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false);
		}else{
			hql="select t from Task t,WorkflowTask wt where  t.id=wt.id and  t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=? or t.active=?)  and t.paused=? and wt.customType=?  order by t.createdTime desc";
			this.searchPageByHql(page, hql.toString(),companyId, loginName,TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false,typeName);
		}
	}
	
	/**
	 * 根据流程名称分页查询用户所有已完成任务
	 * @param page
	 */
	public void getCancelTasksByGroupName(Long companyId, String loginName, Page<Task> page,String typeName){
		String hql=null;
		if(StringUtils.isEmpty(typeName)){
			hql=" from Task t where  t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=? or t.active=?)   and t.paused=? order by t.createdTime desc";
			this.searchPageByHql(page, hql.toString(),companyId, loginName,TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false);
		}else{
			hql=" from Task t where   t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=? or t.active=?)  and t.paused=? and t.groupName=?  order by t.createdTime desc";
			this.searchPageByHql(page, hql.toString(),companyId, loginName,TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false,typeName);
		}
	}
}
