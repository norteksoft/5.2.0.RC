package com.norteksoft.wf.engine.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.task.webservice.WorkflowTaskService;
import com.norteksoft.wf.engine.dao.InstanceHistoryDao;
import com.norteksoft.wf.engine.entity.InstanceHistory;
import com.norteksoft.wf.engine.entity.Opinion;

@Service
@Transactional
public class InstanceHistoryManager {
	
	private Log log = LogFactory.getLog(InstanceHistoryManager.class);
	private InstanceHistoryDao instanceHistoryDao;
	private WorkflowTaskService workflowTaskService;
	
	@Autowired
	public void setInstanceHistoryDao(InstanceHistoryDao instanceHistoryDao) {
		this.instanceHistoryDao = instanceHistoryDao;
	}
	
	@Autowired
	public void setWorkflowTaskService(WorkflowTaskService workflowTaskService) {
		this.workflowTaskService = workflowTaskService;
	}
	
	/**
	 * 保存流转历史
	 * @param ih
	 */
	@Transactional(readOnly=false)
	public void saveHistory(InstanceHistory ih){
		instanceHistoryDao.save(ih);
	}
	
	@Transactional(readOnly=false)
	public void saveHistories(List<InstanceHistory> ihs){
		for(InstanceHistory ih : ihs){
			instanceHistoryDao.save(ih);
		}
	}
	
	/**
	 * 根据流程实例ID查询历史记录（flex用）
	 * @param companyId
	 * @param instanceId
	 * @return 	String[][]: [环节名称，办理人，操作，办理意见];自动环节没有办理人
	 */
	public String[][] getHistoryByInstanceId(Long companyId, String instanceId){

		List<Object[]> specialHistorieAndOpinions = instanceHistoryDao.getHistoryBySpecial(companyId, instanceId, true);
		List<Object[]> historieAndOpinions = instanceHistoryDao.getHistoryBySpecial(companyId, instanceId, false);

		List<InstanceHistory> specialHistories = getHistoryFromHistoryAndOpinion(specialHistorieAndOpinions);
		List<InstanceHistory> histories = getHistoryFromHistoryAndOpinion(historieAndOpinions);
		
		String[][] result = new String[specialHistories.size()+histories.size()][5];
		
		int num=0;
		for(int i = 0; i < specialHistories.size(); i++){
			result[num++] = new String[]{specialHistories.get(i).getTaskName(), specialHistories.get(i).getTransactor(), 
					specialHistories.get(i).getTransactionResult(), specialHistories.get(i).getTransactorOpinion(),specialHistories.get(i).getSpecialTask()+""};
		}
		for(int i = 0; i < histories.size(); i++){
			result[num++] = new String[]{histories.get(i).getTaskName(), histories.get(i).getTransactor(), 
					histories.get(i).getTransactionResult(), histories.get(i).getTransactorOpinion(),histories.get(i).getSpecialTask()+""};
		}
		return result;
	}
	
	public List<InstanceHistory> getHistorysByWorkflowId(Long companyId, String workflowId){
		List<Object[]> histories=instanceHistoryDao.getHistoryByWorkflowId(companyId, workflowId);
		return getHistoryFromHistoryAndOpinion(histories);
	}
	
	private List<InstanceHistory> getHistoryFromHistoryAndOpinion(List<Object[]> histories){
		List<InstanceHistory> list = new ArrayList<InstanceHistory>();
		InstanceHistory ih = null;
		Opinion opinion = null;
		Long lastTaskId = null;
		for(int i = 0; i < histories.size(); i++){
			opinion = (Opinion) histories.get(i)[1];
			//处理同一条流转历史多条意见
			if(opinion != null && lastTaskId != null && lastTaskId.equals(opinion.getTaskId())){
				if(ih.getTransactorOpinion() != null){
					ih.setTransactorOpinion(ih.getTransactorOpinion()+"；"+opinion.getOpinion());
				}else{
					ih.setTransactorOpinion(opinion.getOpinion());
				}
			}else{
				ih = (InstanceHistory) histories.get(i)[0];
				if(opinion != null) ih.setTransactorOpinion(opinion.getOpinion());
				lastTaskId = ih.getTaskId();
				list.add(ih);
			}
		}
		return list;
	}
	
	/**
	 * 查询流程的人工环节流转历史
	 * @param companyId
	 * @param instanceId
	 * @return
	 */
	public List<InstanceHistory> getArtificialHistory(Long companyId, String instanceId){
		return instanceHistoryDao.getArtificialHistory(companyId, instanceId);
	}
	
	/**
	 * 查询流程实例所有有效的流转历史
	 * @param companyId
	 * @param instanceId
	 * @return
	 */
	public List<InstanceHistory> getAllHistoryByInstance(Long companyId, String instanceId){
		return instanceHistoryDao.getAllHistoryByInstance(companyId, instanceId);
	}
	
	/**
	 * 查询流程进入环节的流转历史
	 * @param companyId
	 * @param instanceId
	 * @return
	 */
	public List<InstanceHistory> getIntoTaskHistory(Long companyId, String instanceId){
		return instanceHistoryDao.getIntoTaskHistory(companyId, instanceId);
	}
	
	/**
	 * 查询主流程的人工环节流转历史
	 * @param companyId
	 * @param instanceId
	 * @return
	 */
	public List<InstanceHistory> getMainProcessHistory(Long companyId, String instanceId){
		return instanceHistoryDao.getMainProcessHistory(companyId, instanceId);
	}
	
	/**
	 * 删除取回后的流转历史
	 * @param companyId
	 * @param instanceId
	 * @param taskName
	 * @param taskNames
	 */
	@Transactional(readOnly=false)
	public void deleteHistoryByTask(Long companyId, String instanceId,Long taskId, String[] taskNames){
		instanceHistoryDao.deleteHistoryByTask(companyId, instanceId, taskId, taskNames);
	}
	
	@Transactional(readOnly=false)
	public void deleteHistoryByworkflowId(String workflowId){
		instanceHistoryDao.deleteHistoryByworkflowId(workflowId,ContextUtils.getCompanyId());
	}
	
	/**
	 * 查询当前环节（flex用）
	 * @param companyId
	 * @param instanceId
	 * @return List 当前环节
	 */
	public List<String[]> getCurrentTasks(Long companyId, String instanceId){
		
		//FIXME 重构：逻辑过于复杂，二十天后自己都难读明白
		
		//存放任务及候选人(任务待领取的)  <任务名称，办理任务的候选人>
		Map<String, StringBuilder> transactorCandidates = new HashMap<String, StringBuilder>();
		//存放任务及办理人（任务已经领取的或不需要领取的）  <任务名称，任务办理人>
		Map<String, StringBuilder> transactors = new HashMap<String, StringBuilder>();
		Map<String, Boolean> specials = new HashMap<String, Boolean>();
		List<WorkflowTask> tasks = workflowTaskService.getActivityTasks(instanceId, companyId);
		processTasks(tasks, transactors, transactorCandidates,specials,companyId);
		List<String[]> result = new ArrayList<String[]>();
		String[] taskTransactor = null;
		String resultTransactors = "";
		
		for(Map.Entry<String, StringBuilder> taskTransactorPair : transactors.entrySet()){
			taskTransactor = new String[3];
			resultTransactors = taskTransactorPair.getValue().toString();
			if(!resultTransactors.endsWith(",")){
				resultTransactors = transactorCandidates.get(taskTransactorPair.getKey()).toString();
			}
			taskTransactor[0] = taskTransactorPair.getKey();
			if(resultTransactors.length()>0){
				taskTransactor[1] = resultTransactors.substring(0, resultTransactors.length()-1);
			}else{
				taskTransactor[1] = "";
			}
			taskTransactor[2] = specials.get(taskTransactorPair.getKey()).toString();
			result.add(taskTransactor);
		}
		
		log.debug("*** getCurrentTasks 方法结束");
		return result;
	}
	
	
	/*
	 * 处理任务及其办理人
	 */
	private void processTasks(List<WorkflowTask> tasks, 
			Map<String, StringBuilder> transactors, Map<String, StringBuilder> transactorCandidates,Map<String,Boolean> specials,Long companyId){
		String userName = "";
		List<String> candiLoginNames=new ArrayList<String>();
		List<String> loginNames=new ArrayList<String>();
		//遍历当前待办的任务
		for(WorkflowTask task : tasks){
			if(transactors.get(task.getName()) == null){
				transactors.put(task.getName(), new StringBuilder());
			}
			if(transactorCandidates.get(task.getName()) == null){
				transactorCandidates.put(task.getName(), new StringBuilder());
			}
			if(TaskState.DRAW_WAIT.getIndex().equals(task.getActive())){//未领取的，
				candiLoginNames.add(task.getTransactor());
			}else{//已领取的任务
				loginNames.add(task.getTransactor());
			}
			if(task.isSpecialTask()){
				specials.put(task.getName(), true);
			}else{
				specials.put(task.getName(), false);
			}
		}
		//未领取任务办理人排序
		List<User> results=null;
		if(candiLoginNames.size()>0){
			results=ApiFactory.getAcsService().getUsersByLoginNames(companyId,candiLoginNames);
			Collections.sort(results, new Comparator<User>() {
				public int compare(User user1, User user2) {
					if(user1.getWeight()<user2.getWeight()){
						return 1;
					}
					return 0;
				}
			});
			for(User u:results){
				for(WorkflowTask task : tasks){
					if(TaskState.DRAW_WAIT.getIndex().equals(task.getActive())){//未领取的，
						if(u.getLoginName().equals(task.getTransactor())){
							userName = getUserNameByLoginName(task.getTransactor());
							if(task.isSpecialTask()){
								transactorCandidates.get(task.getName()).append(userName).append("(特)").append(",");
							}else{
								transactorCandidates.get(task.getName()).append(userName).append(",");
							}
							break;
						}
					}
				}
			}
		}
		//已领取的任务办理人排序
		if(loginNames.size()>0){
			results=ApiFactory.getAcsService().getTacheUsersByLoginNames(companyId,loginNames);
			Collections.sort(results, new Comparator<User>() {
				public int compare(User user1, User user2) {
					if(user1.getWeight()<user2.getWeight()){
						return 1;
					}
					return 0;
				}
			});
			List<WorkflowTask> tempTasks = tasks;
			for(User u:results){
				for(WorkflowTask task : tempTasks){
					if(!TaskState.DRAW_WAIT.getIndex().equals(task.getActive())){//已领取的任务
						if(u!=null&&u.getLoginName().equals(task.getTransactor())){
							userName = getUserNameByLoginName(task.getTransactor());
							if(task.isSpecialTask()){
								transactors.get(task.getName()).append(userName).append("(特)").append(",");
							}else{
								transactors.get(task.getName()).append(userName).append(",");
							}
							tempTasks.remove(task);
							break;
						}
					}
				}
			}
		}
	}
	
	
	/*
	 * 通过登陆名查询用户名 
	 */
	private String getUserNameByLoginName(String loginName){
		UserManager userManager = (UserManager) ContextUtils.getBean("userManager");
		com.norteksoft.acs.entity.organization.User user = userManager.getUserByLoginName(loginName);
		String userName = "";
		if(user != null){
			userName = user.getName(); 
		}
		return userName;
	}
	
}
