package com.norteksoft.wf.engine.dao;

import java.util.List;

import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.wf.engine.entity.InstanceHistory;
import com.norteksoft.wf.engine.entity.Opinion;

@Repository
public class InstanceHistoryDao extends HibernateDao<InstanceHistory, Long>{
	
	/**
	 * 流程自动环节和人工环节的流转历史
	 * @param companyId
	 * @param instanceId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getHistoryBySpecial(Long companyId, String instanceId, boolean isSpecial){
		
		StringBuilder hql = new StringBuilder();
		hql.append("select {ih.*}, {o.*} ");
		hql.append("from WF_INSTANCE_HISTORY ih left join WF_OPINION o on ih.task_id=o.task_id ");
		hql.append("where ih.company_id=? and ih.instance_id=? and (ih.type = ? or (ih.type = ? and o.id is null)) and ih.special_task=? order by ih.id,ih.task_id");
		
		SQLQuery query = this.getSession().createSQLQuery(hql.toString());
		query.setParameter(0, companyId);
		query.setParameter(1, instanceId);
		query.setParameter(2, InstanceHistory.TYPE_TASK);
		query.setParameter(3, InstanceHistory.TYPE_AUTO);
		query.setParameter(4, isSpecial);
		
		return query.addEntity("ih", InstanceHistory.class).addEntity("o", Opinion.class).list();
		
	}
	
	/**
	 * 文本流转历史
	 * @param companyId
	 * @param workflowId
	 * @return InstanceHistory
	 */	
	@SuppressWarnings("unchecked")
	public List<Object[]> getHistoryByWorkflowId(Long companyId, String workflowId){
		StringBuilder hql = new StringBuilder();
		hql.append("select {ih.*}, {o.*} ");
		hql.append("from WF_INSTANCE_HISTORY ih left join WF_OPINION o on ih.task_id=o.task_id ");
		hql.append("where ih.company_id=? and ih.instance_id=? and (ih.type = ? or (ih.type = ? or ih.type = ? and o.id is null)) order by ih.id,ih.task_id");
		SQLQuery query = this.getSession().createSQLQuery(hql.toString());
		query.setParameter(0, companyId);
		query.setParameter(1, workflowId);
		query.setParameter(2, InstanceHistory.TYPE_TASK);
		query.setParameter(3, InstanceHistory.TYPE_FLOW_START);
		query.setParameter(4, InstanceHistory.TYPE_FLOW_END);
		return query.addEntity("ih", InstanceHistory.class).addEntity("o", Opinion.class).list();
	}
	
	
	/**
	 * 主流程的人工流转历史
	 * @param companyId
	 * @param instanceId
	 * @return
	 */
	public List<InstanceHistory> getMainProcessHistory(Long companyId, String instanceId){
		return find("from InstanceHistory ih where ih.companyId=? and ih.executionId=? and ih.type = ? and ih.effective=? order by ih.id", 
				companyId, instanceId, InstanceHistory.TYPE_TASK, true);
	}
	
	/**
	 * 流程的人工流转历史(排序)
	 * @param companyId
	 * @param instanceId
	 * @return
	 */
	public List<InstanceHistory> getArtificialHistory(Long companyId, String instanceId){
		return find("from InstanceHistory ih where ih.companyId=? and ih.instanceId=? and ih.type = ? and ih.effective=? order by ih.id", 
				companyId, instanceId, InstanceHistory.TYPE_TASK, true);
	}
	
	/**
	 * 查询流程实例所有有效的流转历史
	 * @param companyId
	 * @param instanceId
	 * @return
	 */
	public List<InstanceHistory> getAllHistoryByInstance(Long companyId, String instanceId){
		return find("from InstanceHistory ih where ih.companyId=? and ih.instanceId=? and ih.effective = ?", 
				companyId, instanceId, true);
	}
	
	/**
	 * 流程的进入任务流转历史(排序)
	 * @param companyId
	 * @param instanceId
	 * @return
	 */
	public List<InstanceHistory> getIntoTaskHistory(Long companyId, String instanceId){
		return find("from InstanceHistory ih where ih.companyId=? and ih.instanceId=? and ih.type = ? and ih.effective=? order by ih.id", 
				companyId, instanceId, InstanceHistory.TYPE_FLOW_INTO, true);
	}
	
	/**
	 * 根据任务删除任务的流转历史
	 * @param companyId
	 * @param instanceId
	 * @param taskName
	 * @param taskNames
	 */
	public void deleteHistoryByTask(Long companyId, String instanceId, Long taskId, String[] taskNames){
		createQuery("delete InstanceHistory ih where ih.companyId=? and ih.instanceId=? and ih.taskId=? and (ih.type = ? or ih.type = ? ) and ih.effective=?", 
				companyId, instanceId, taskId, InstanceHistory.TYPE_TASK, InstanceHistory.TYPE_FLOW_LEAVE, true).executeUpdate();
		for(String name : taskNames){
			createQuery("delete InstanceHistory ih where ih.companyId=? and ih.instanceId=? and ih.taskName=? and ih.type = ? and ih.effective=?", 
					companyId, instanceId, name, InstanceHistory.TYPE_FLOW_INTO, true).executeUpdate();
		}
	}
	/**
	 * 删除流程实例的所有流转历史
	 * @param companyId
	 * @param workflowId
	 */
	public void deleteHistoryByworkflowId(String workflowId,Long companyId){
		createQuery("delete InstanceHistory ih where ih.companyId=? and ih.instanceId=? ", 
				companyId, workflowId).executeUpdate();
	}
}
