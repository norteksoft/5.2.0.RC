package com.norteksoft.wf.engine.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.engine.entity.Opinion;

@Repository
public class OpinionDao extends HibernateDao<Opinion, Long>{
	public void deleteAllOpinionsByWorkflowInstanceId(String workflowId,Long companyId){
		createQuery("delete from Opinion o where  o.workflowId = ? and o.companyId = ? ", workflowId,companyId).executeUpdate();
	}
	
	public List<Opinion> getOpinionsByInstanceId(String workflowId , Long companyId) {
		return find("from Opinion o where o.workflowId=? and o.companyId=? order by o.createdTime", workflowId,companyId);
	}
	
	public List<Opinion> getOpinionsByTaskId(Long taskId , Long companyId) {
		return find("from Opinion o where o.taskId=? and o.companyId=?", taskId,companyId);
	}
	
	public Opinion getOpinionsById(Long opinionId) {
		return findUnique("from Opinion o where o.id=?", opinionId);
	}

	public List<Opinion> getOpinions(Long taskId, Long companyId) {
		StringBuilder hql = new StringBuilder("from Opinion o where o.taskId=? and o.companyId=? order by o.createdTime");
		return find(hql.toString(), taskId,companyId);
	}	
	
	public List<Opinion> getOpinions(String workflowId, Long companyId,
			String taskName) {
		StringBuilder hql = new StringBuilder("from Opinion o where o.workflowId=? and o.companyId=?  and o.taskName=?  order by o.createdTime");
		return find(hql.toString(), workflowId,companyId,taskName);
	}

	public List<Opinion> getOpinions(String workflowId, Long companyId,
			TaskProcessingMode taskMode) {
		StringBuilder hql = new StringBuilder("from Opinion o where o.workflowId=? and o.companyId=?  and o.taskMode=?  order by o.createdTime");
		return find(hql.toString(), workflowId,companyId,taskMode);
	}

	public List<Opinion> getOpinionsExceptTaskMode(String workflowId,
			Long companyId, TaskProcessingMode taskMode) {
		StringBuilder hql = new StringBuilder("from Opinion o where o.workflowId=? and o.companyId=?  and o.taskMode<>?  order by o.createdTime");
		return find(hql.toString(), workflowId,companyId,taskMode);
	}

	public List<Opinion> getOpinionsExceptCustomField(String workflowId,
			String customField) {
		String hql = "from Opinion d where d.workflowId=? and d.customField<>? order by d.createdTime desc";
		return this.find(hql, workflowId,customField);
	}

	public List<Opinion> getOpinionsByCustomField(String workflowId,
			String customField) {
		String hql = "from Opinion d where d.workflowId=? and d.customField=? order by d.createdTime desc";
		return this.find(hql, workflowId,customField);
	}
	
	/**
	 * 查询任务和意见
	 * @param companyId
	 * @param task
	 * @return
	 */
	public List<Object[]> getOpinions(WorkflowTask task){
		String hql = "select t, d from Opinion d, WorkflowTask t  where t.id=d.taskId and d.workflowId=? and d.taskName=? and d.companyId=? and d.opinion is not null order by d.taskId desc";
		return this.find(hql, task.getProcessInstanceId(),task.getName(),task.getCompanyId());
	}
	
	public List<Opinion> getOpinionsExceptTaskName(String workflowId,
			Long companyId, String taskName) {
		StringBuilder hql = new StringBuilder("from Opinion o where o.workflowId=? and o.companyId=?  and o.taskName!=?  order by o.createdTime");
		return find(hql.toString(), workflowId,companyId,taskName);
	}
	
	public List<Opinion> getOpinions(String workflowId, Long companyId,
			List<String> taskNames) {
		StringBuilder hql = new StringBuilder("from Opinion o where o.workflowId=? and o.companyId=?");
		Object[] objs = new Object[taskNames.size()+2];
		if(taskNames.size()>0)hql.append(" and (");
		boolean isFirst=true;
		objs[0]=workflowId;
		objs[1]=companyId;
		for(int i=0;i<taskNames.size();i++){
			if(!isFirst) hql.append(" or ");
			hql.append(" o.taskName=?" );
			isFirst=false;
			objs[i+2]=taskNames.get(i);
		}
		if(taskNames.size()>0)hql.append(")");
		hql.append(" order by o.createdTime");
		return find(hql.toString(),objs);
	}
	
	public List<Opinion> getOpinionsExceptTaskName(String workflowId,
			Long companyId, List<String> taskNames) {
		StringBuilder hql = new StringBuilder("from Opinion o where o.workflowId=? and o.companyId=? ");
		Object[] objs = new Object[taskNames.size()+2];
		if(taskNames.size()>0)hql.append(" and (");
		boolean isFirst=true;
		objs[0]=workflowId;
		objs[1]=companyId;
		for(int i=0;i<taskNames.size();i++){
			if(!isFirst) hql.append(" and ");
			hql.append(" o.taskName!=?" );
			isFirst=false;
			objs[i+2]=taskNames.get(i);
		}
		if(taskNames.size()>0)hql.append(")");
		hql.append(" order by o.createdTime");
		return find(hql.toString(),objs);
	}
	
	public List<Opinion> getOpinionsByTacheCode(String workflowId, Long companyId,
			String tacheCode) {
		StringBuilder hql = new StringBuilder("from Opinion o where o.workflowId=? and o.companyId=?  and o.taskCode=?  order by o.createdTime");
		return find(hql.toString(), workflowId,companyId,tacheCode);
	}
	
	public List<Opinion> getOpinionsByTacheCode(String workflowId, Long companyId,
			List<String> tacheCodes) {
		StringBuilder hql = new StringBuilder("from Opinion o where o.workflowId=? and o.companyId=?");
		Object[] objs = new Object[tacheCodes.size()+2];
		objs[0]=workflowId;
		objs[1]=companyId;
		if(tacheCodes.size()>0)hql.append(" and (");
		boolean isFirst=true;
		int i=2;
		for(String tacheCode:tacheCodes){
			if(!isFirst) hql.append(" or ");
			hql.append(" o.taskCode=?" );
			isFirst=false;
			objs[i++]=tacheCode;
		}
		if(tacheCodes.size()>0)hql.append(")");
		hql.append(" order by o.createdTime");
		return find(hql.toString(),objs);
	}
	
	public List<Opinion> getOpinionsByTaskName(String workflowId, Long companyId,
			String  taskName) {
		StringBuilder hql = new StringBuilder("from Opinion o where o.workflowId=? and o.companyId=?  and o.taskName=? and o.taskCode=null order by o.createdTime");
		return find(hql.toString(), workflowId,companyId,taskName);
	}
	
	public List<Opinion> getOpinionsByTaskName(String workflowId, Long companyId,
			List<String> taskNames) {
		StringBuilder hql = new StringBuilder("from Opinion o where o.workflowId=? and o.companyId=?");
		Object[] objs = new Object[taskNames.size()+2];
		objs[0]=workflowId;
		objs[1]=companyId;
		if(taskNames.size()>0)hql.append(" and (");
		boolean isFirst=true;
		int i=2;
		for(String tacheCode:taskNames){
			if(!isFirst) hql.append(" or ");
			hql.append(" o.taskName=?" );
			isFirst=false;
			objs[i++]=tacheCode;
		}
		if(taskNames.size()>0)hql.append(")");
		hql.append(" and o.taskCode=null order by o.createdTime");
		return find(hql.toString(),objs);
	}
	
	/**
	 * 根据任务删除意见
	 * @param companyId
	 * @param instanceId
	 * @param taskName
	 * @param taskNames
	 */
	public void deleteOpinionsByTask(Long companyId, String instanceId, Long taskId){
		createQuery("delete Opinion ih where ih.companyId=? and ih.workflowId=? and ih.taskId=?", 
				companyId, instanceId, taskId).executeUpdate();
	}
}
