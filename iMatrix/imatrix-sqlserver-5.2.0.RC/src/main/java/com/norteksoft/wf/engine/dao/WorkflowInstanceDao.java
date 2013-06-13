package com.norteksoft.wf.engine.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.wf.base.enumeration.ProcessState;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.entity.WorkflowInstance;

@Repository
public class WorkflowInstanceDao extends HibernateDao<WorkflowInstance, Long>{
	
	private WorkflowDefinitionDao workflowDefinitionDao;
	
	@Autowired
	public void setWorkflowDefinitionDao(
			WorkflowDefinitionDao workflowDefinitionDao) {
		this.workflowDefinitionDao = workflowDefinitionDao;
	}
	
	public WorkflowInstance getInstanceByJbpmInstanceId(String jbpmInstanceId, Long companyId){
		return findUnique("from WorkflowInstance wfi where wfi.companyId=? and wfi.processInstanceId=?", companyId, jbpmInstanceId);
	}
	//删除正文
	public void deleteText(Long documentId){
		getSession().createSQLQuery("DELETE FROM WF_DOCUMENT t WHERE t.DOCUMENTID=?").setParameter(0, documentId).executeUpdate();
	}
	
	public void listEndWorkflowInstance(Page<WorkflowInstance> workflowInstances,String documentCreator,Long companyId, Long systemId){
		findPage(workflowInstances, "from WorkflowInstance wi where wi.companyId = ? and  wi.creator=?  and wi.systemId=? and (wi.processState=? or wi.processState=?) order by submitTime desc", 
				companyId, documentCreator, systemId,ProcessState.END,ProcessState.MANUAL_END);
	}
	
	public void listEndWorkflowInstance(Page<WorkflowInstance> workflowInstances,Long type,String documentCreator,Long companyId, Long systemId){
		findPage(workflowInstances, "from WorkflowInstance wi where wi.companyId = ? and  wi.creator=?  and wi.typeId=? and wi.systemId=? and (wi.processState=? or wi.processState=?)  order by submitTime  desc", 
				companyId, documentCreator,type, systemId,ProcessState.END,ProcessState.MANUAL_END);
	}
	
	public void listNotEndWorkflowInstance(Page<WorkflowInstance> workflowInstances,String documentCreator,Long companyId, Long systemId){
		findPage(workflowInstances, "from WorkflowInstance wi where wi.companyId = ? and  wi.creator=?  and wi.systemId=? and (wi.processState=? or wi.processState=?)  order by submitTime  desc" , 
				companyId, documentCreator, systemId,ProcessState.UNSUBMIT,ProcessState.SUBMIT);
	}
	
	public void listNotEndWorkflowInstance(Page<WorkflowInstance> workflowInstances,Long type,String documentCreator,Long companyId, Long systemId){
		findPage(workflowInstances, "from WorkflowInstance wi where wi.companyId = ? and  wi.creator=?  and wi.typeId=? and wi.systemId=? and (wi.processState=? or wi.processState=?)  order by submitTime  desc", 
				companyId, documentCreator,type, systemId,ProcessState.UNSUBMIT,ProcessState.SUBMIT);
	}
	
	public void listEndWorkflowInstanceByDefinitionId(Page<WorkflowInstance> workflowInstances,Long workflowDefinitionId,Long companyId, Long systemId,String documentCreator){
		findPage(workflowInstances, "from WorkflowInstance wi where wi.companyId = ? and  wi.workflowDefinitionId=? and wi.systemId=? and  wi.creator=? and (wi.processState=? or wi.processState=?) and wi.typeId=?  and  wi.parentProcessId is null order by submitTime  desc", 
				companyId, workflowDefinitionId,systemId,documentCreator,ProcessState.END,ProcessState.MANUAL_END);
	}
	
	public void listNotEndWorkflowInstanceByDefinitionId(Page<WorkflowInstance> workflowInstances,Long workflowDefinitionId,Long companyId, Long systemId,String documentCreator){
		findPage(workflowInstances, "from WorkflowInstance wi where wi.companyId = ? and  wi.workflowDefinitionId=? and wi.systemId=? and  wi.creator=? and (wi.processState=? or wi.processState=?) and  wi.parentProcessId is null order by submitTime  desc", 
				companyId, workflowDefinitionId,systemId,documentCreator,ProcessState.UNSUBMIT,ProcessState.SUBMIT);
	}
	
	/**
	 * 根据类型查询流程实例个数 (不含子流程)
	 * @param companyId
	 * @param creator 创建人
	 * @param typeId 类型ID
	 * @param isEnd 流程是否结束
	 * @return 
	 */
	public Integer getEndInstanceNumByCreatorAndType(Long companyId, String creator, Long typeId, Long systemId){
		return Integer.parseInt(createQuery(
				"select count(wi) from WorkflowInstance wi where wi.companyId = ? and  wi.creator=? and wi.typeId=? and wi.parentProcessId is null and wi.systemId=? and (wi.processState=? or wi.processState=?) ", 
				companyId, creator, typeId,  systemId,ProcessState.END,ProcessState.MANUAL_END).uniqueResult().toString());
	}
	
	/**
	 * 根据类型查询流程实例个数 (不含子流程)
	 * @param companyId
	 * @param creator 创建人
	 * @param typeId 类型ID
	 * @param isEnd 流程是否结束
	 * @return 
	 */
	public Integer getNotEndInstanceNumByCreatorAndType(Long companyId, String creator, Long typeId, Long systemId){
		return Integer.parseInt(createQuery(
				"select count(wi) from WorkflowInstance wi where wi.companyId = ? and  wi.creator=? and wi.typeId=?  and wi.parentProcessId is null and wi.systemId=? and (wi.processState=? or wi.processState=?)  ", 
				companyId, creator, typeId, systemId,ProcessState.UNSUBMIT,ProcessState.SUBMIT).uniqueResult().toString());
	}
	
	/**
	 * 根据流程是否结束查询流程实例个数
	 * @param companyId
	 * @param creator
	 * @param isEnd
	 * @return
	 */
	public Integer getEndInstanceNumByEnable(Long companyId, String creator,  Long systemId){
		return Integer.parseInt(createQuery(
				"select count(wi) from WorkflowInstance wi where wi.companyId = ? and  wi.creator=? and wi.parentProcessId is null and wi.systemId=? and (wi.processState=? or wi.processState=?)  ", 
				companyId, creator,  systemId,ProcessState.END,ProcessState.MANUAL_END).uniqueResult().toString());
	}
	
	public Integer getNotEndInstanceNumByEnable(Long companyId, String creator,  Long systemId){
		return Integer.parseInt(createQuery(
				"select count(wi) from WorkflowInstance wi where wi.companyId = ? and  wi.creator=? and wi.parentProcessId is null and wi.systemId=? and (wi.processState=? or wi.processState=?) ", 
				companyId, creator,  systemId,ProcessState.UNSUBMIT,ProcessState.SUBMIT).uniqueResult().toString());
	}
	
	public Integer getNotEndInstanceNumByDefinition(Long companyId, String creator, WorkflowDefinition definition, Long systemId){
		StringBuilder hql = new StringBuilder("select count(wi) from WorkflowInstance wi where wi.companyId = ? and  wi.creator=? and  wi.parentProcessId is null and wi.systemId=? and (wi.processState=? or wi.processState=?) and wi.typeId=? and wi.workflowDefinitionId in ( ");
		List<Long> definitionIds = workflowDefinitionDao.getAllDefinitionIdNotDraft(definition);
		for(int i=0;i<definitionIds.size();i++){
			if(i!=0)hql.append(",");
			hql.append(definitionIds.get(i));
		}
		hql.append(")");
		return Integer.parseInt(createQuery(hql.toString(), 
				companyId, creator,systemId,ProcessState.UNSUBMIT,ProcessState.SUBMIT,definition.getTypeId()).uniqueResult().toString());
	}
	
	public Integer getEndInstanceNumByDefinition(Long companyId, String creator, WorkflowDefinition definition, Long systemId){
		StringBuilder hql = new StringBuilder("select count(wi) from WorkflowInstance wi where wi.companyId = ? and  wi.creator=?  and wi.parentProcessId is null and wi.systemId=? and (wi.processState=? or wi.processState=?)  and wi.typeId=? and wi.workflowDefinitionId in ( ");
		List<Long> definitionIds = workflowDefinitionDao.getAllDefinitionIdNotDraft(definition);
		for(int i=0;i<definitionIds.size();i++){
			if(i!=0)hql.append(",");
			hql.append(definitionIds.get(i));
		}
		hql.append(")");
		return Integer.parseInt(createQuery(
				hql.toString(), 
				companyId, creator, systemId,ProcessState.END,ProcessState.MANUAL_END,definition.getTypeId()).uniqueResult().toString());
	}
	public List<WorkflowInstance> getSubWorkflowInstances(String processInstanceId,
			Long companyId, Long systemId) {
		String hql = "from WorkflowInstance wi where wi.companyId = ? and wi.systemId = ? and wi.parentProcessId = ?" ;
		return this.find(hql, companyId,systemId,processInstanceId);
		
	}
	public List<WorkflowInstance> getAllEndWorkflowInstances(
			Long workflowDefinitionId, Long companyId, Long systemId) {
		String hql = "from WorkflowInstance wi where wi.companyId = ? and wi.systemId = ? and wi.workflowDefinitionId = ? and (wi.processState=? or wi.processState=?)";
		return this.find(hql, companyId,systemId,workflowDefinitionId,ProcessState.END,ProcessState.MANUAL_END);
	}
	public List<WorkflowInstance> getAllWorkflowInstances(
			Long workflowDefinitionId, Long companyId, Long systemId) {
		String hql = "from WorkflowInstance wi where wi.companyId = ? and wi.systemId = ? and wi.workflowDefinitionId = ? and wi.processState<>? ";
		return this.find(hql, companyId,systemId,workflowDefinitionId,ProcessState.UNSUBMIT);
	}
	public void getAllWorkflowInstances(Page<WorkflowInstance> page,
			Long workflowDefinitionId, Long companyId, Long systemId) {
		String hql = "from WorkflowInstance wi where wi.companyId = ? and wi.systemId = ? and wi.workflowDefinitionId = ? and wi.processState<>?  and wi.parentProcessId is null  order by submitTime desc";
		this.findPage(page,hql, companyId,systemId,workflowDefinitionId,ProcessState.UNSUBMIT);
	}

	public List<WorkflowInstance> getNeedReminderInstance() {
		String hql = "from WorkflowInstance wi where wi.processState=?  and wi.duedate<>0 and wi.reminderStyle is not null";
		return find(hql, ProcessState.SUBMIT);
	}

	public List<WorkflowInstance> getSubProcessInstance(String parentWorkflowId) {
		String hql = "from WorkflowInstance wi where wi.parentProcessId = ?  order by submitTime desc";
		return this.findNoCompanyCondition(hql, parentWorkflowId);
	}

	public List<WorkflowInstance> getSubProcessInstance(
			String parentWorkflowId, String tacheName) {
		String hql = "from WorkflowInstance wi where wi.parentProcessId = ? and wi.parentProcessTacheName=? order by submitTime desc";
		return this.findNoCompanyCondition(hql, parentWorkflowId,tacheName);
	}

	public List<WorkflowInstance> getActivityWorkflowInstance(
			String parentWorkflowId, String tacheName) {
		String hql = "from WorkflowInstance wi where wi.parentProcessId = ? and wi.parentProcessTacheName=? and (wi.processState=? or wi.processState=?)order by submitTime desc";
		return this.find(hql, parentWorkflowId,tacheName,ProcessState.UNSUBMIT,ProcessState.SUBMIT);
	}
	
	public List<WorkflowInstance> getAllWorkflowInstances() {
		String hql = "from WorkflowInstance wi order by submitTime desc";
		return this.find(hql);
	}
	
	public Integer getInstancesNumByDefId(
			Long workflowDefinitionId, Long companyId, Long systemId) {
		String hql = "select count(wi) from WorkflowInstance wi where wi.companyId = ? and wi.systemId = ? and wi.workflowDefinitionId = ?";
		return Integer.parseInt(createQuery(hql, companyId,systemId,workflowDefinitionId).uniqueResult().toString());
	}
	
	public WorkflowInstance  getInstancesByDataId(Long dataId){
		String hql = "from WorkflowInstance wi where  wi.dataId = ?";
		List<WorkflowInstance> wis=this.find(hql,dataId);
		 if(wis.size()>0)return wis.get(0);
		 return null;
	}
	//根据jbpm流程定义id活动实例id
	public List<String> getInstanceIdByProcessId(String processId,Long companyId,String taskName){
		String hql ="select wi.processInstanceId from WorkflowInstance wi where wi.processDefinitionId=? and wi.companyId=? and wi.currentActivity=?";
			return this.find(hql, processId,companyId,taskName);
			
	}
	/**
	 * 判断当前环节是否是子流程
	 * @param processInstanceId
	 * @return
	 */
	public List<Object> getActivetySubProcess(String processInstanceId) {
		StringBuilder newHql = new StringBuilder();
		newHql.append("SELECT t FROM WorkflowTask t, WorkflowInstance w, Task p ");
		newHql.append(" where w.processInstanceId = t.processInstanceId ");
		newHql.append(" and p.id = t.id ");
		newHql.append(" and w.parentProcessId=? and (p.active=0 or p.active=1 or p.active=4 or p.active=6) ");

		return this.find(newHql.toString(), processInstanceId);
	}
}
