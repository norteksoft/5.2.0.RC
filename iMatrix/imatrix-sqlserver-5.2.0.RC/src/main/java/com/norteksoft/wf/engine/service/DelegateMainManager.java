package com.norteksoft.wf.engine.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.product.api.entity.Role;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.base.enumeration.TrustRecordState;
import com.norteksoft.wf.engine.dao.DelegateMainDao;
import com.norteksoft.wf.engine.entity.TrustRecord;


@Service
@Transactional
public class DelegateMainManager {
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//流转历史常量
	private static final String COMMA = ", ";
	private static final String DELTA_START = "[ ";
	private static final String DELTA_END = " ]";
	private DelegateMainDao delegateMainDao;
	private AcsUtils acsUtils;
	private InstanceHistoryManager instanceHistoryManager;
	
	
	@Autowired
	public void setInstanceHistoryManager(
			InstanceHistoryManager instanceHistoryManager) {
		this.instanceHistoryManager = instanceHistoryManager;
	}
	@Autowired
	public void setAcsUtils(AcsUtils acsUtils) {
		this.acsUtils = acsUtils;
	}
	@Autowired
	private TaskService taskService;
	
	@Autowired
	public void setDelegateMainDao(DelegateMainDao delegateMainDao) {
		this.delegateMainDao = delegateMainDao;
	}
	
	/**
	 * 保存
	 * @param delegateMain
	 */
	@Transactional(readOnly=false)
	public void saveDelegateMain(TrustRecord delegateMain){
		delegateMainDao.save(delegateMain);
	}
	
	/**
	 * 删除
	 * @param delegateMain
	 */
	@Transactional(readOnly=false)
	public void deleteDelegateMain(Long id){
		delegateMainDao.delete(id);
	}
	
	/**
	 * 删除
	 * @param delegateMain
	 */
	@Transactional(readOnly=false)
	public int[] deleteDelegateMains(String deleteIds){
		String[] ids=deleteIds.split(",");
		TrustRecord delegateMain = null;
		int deleteNum=0,notDeleteNum=0;
		for(String id:ids){
			delegateMain = getDelegateMain(Long.valueOf(id));
			
			if(delegateMain.getState()==TrustRecordState.STARTED||delegateMain.getState()==TrustRecordState.EFFICIENT){
				notDeleteNum++;
			}else{
				deleteNum++;
				delegateMainDao.delete(delegateMain);
			}
		}
		return new int[]{deleteNum,notDeleteNum};
	}
	
	@Transactional(readOnly=false)
	public void deleteDelegateMainByFlowId(String processId){
		delegateMainDao.deleteDelegateMainByFlowId(processId);
	}
	
	/**
	 * 得到
	 * @param delegateMain
	 */
	public TrustRecord getDelegateMain(Long id){
		return delegateMainDao.get(id);
	}
	
	/**
	 * 得到所有的委托
	 * @param page
	 * @param companyId
	 * @return
	 */
	public Page<TrustRecord> getPageDelegateMain(Page<TrustRecord> page){
		if(StringUtils.isEmpty(page.getOrderBy())){
			page.setOrderBy("createdTime");
			page.setOrder(Page.DESC);
		}
		return delegateMainDao.findPage(page, "from TrustRecord d where d.companyId=? and d.trustor=?", getCompanyId(),getLonginName());
	}
	
	/**
	 * 查询我接收的委托
	 * @param page
	 */
	public void getReceiveDelegate(Page<TrustRecord> page){
		delegateMainDao.findPage(page, "from TrustRecord d where d.companyId=? and d.trustee=? and d.state=?",
				getCompanyId(),getLonginName(),TrustRecordState.EFFICIENT);
	}
	
	public Long getCompanyId(){
		return ContextUtils.getCompanyId();
	}
	
	public String getLonginName(){
		return ContextUtils.getLoginName();
	}
	
	/**
	 * 通过委托人的登陆名，流程 定义ID，环节名
	 * @param myLoadName
	 * @param flowId
	 * @param linkName
	 */
	public String getDelegateMainName(Long companyId,String myLoadName,String flowId,String linkName){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		
		String loanName = getDelegateMainName(companyId,myLoadName,cal.getTime());
		if(StringUtils.isNotEmpty(loanName))return loanName;
		
		List<TrustRecord> trustRecords=this.delegateMainDao.find("from TrustRecord d where d.companyId=?  and d.state=? and d.trustor=? and d.processId=? and (d.activityName=? or d.activityName like ? or d.activityName like ? or d.activityName like ?) and d.beginTime<=? and d.endTime>=? and d.style=1",
																				companyId,TrustRecordState.EFFICIENT,myLoadName,flowId,linkName,linkName+",%","%,"+linkName+",%","%,"+linkName,cal.getTime(),cal.getTime());
		if(trustRecords.size()>0){
			return trustRecords.get(0).getTrustee();
		}
		return null;
	}

	
	/*
	 * 全权委托
	 */
	public String getDelegateMainName(Long companyId,String myLoadName,Date time){
		TrustRecord main=this.delegateMainDao.findUnique("from TrustRecord d where d.companyId=? and d.state=? and d.trustor=? and d.beginTime<=? and d.endTime>=? and d.style=2", companyId,TrustRecordState.EFFICIENT,myLoadName,time,time);
		if(main!=null){
			return main.getTrustee();
		}
		return null;
	}
	/**
	 * 查询所有已启用的权限委托
	 * @return
	 */
	public List<TrustRecord> getDelegateMainsOnAssign(){
		String hql = "FROM TrustRecord d WHERE d.style=? and ( d.state=? or d.state=? )and d.roleIds is not null";
		return delegateMainDao.find(hql, new Short("3"),TrustRecordState.STARTED,TrustRecordState.EFFICIENT);
	}
	
	public List<TrustRecord> getAllStartWorkflowDelegateMain(){
		String hql = "FROM TrustRecord d WHERE (d.style=? or d.style=?) and ( d.state=? or d.state=? )";
		return delegateMainDao.find(hql, new Short("1"),new Short("2"),TrustRecordState.STARTED,TrustRecordState.EFFICIENT);
	}
	
	/**
	 * 验证
	 * @param companyId
	 * @param myLoadName
	 * @param style
	 * @return
	 */
	public Boolean validDelegate(Long companyId,String myLoadName,Date beginTime,Date endTime){
		TrustRecord main=this.delegateMainDao.findUnique("from TrustRecord d where d.companyId=? and d.trustor=? and (d.beginTime<=? and d.endTime>=? or d.beginTime<=? and d.endTime>=?)", companyId,myLoadName,beginTime,beginTime,endTime,endTime);
		if(main!=null){
			return true;
		}
		return false;
	}
	/**
	 * 验证委托权限
	 * @param companyId
	 * @param myLoadName
	 * @param style
	 * @return
	 */
	public Boolean validTypeThreeDelegate(Long companyId,String myLoadName,String rolesIds,short style,Date beginTime,Date endTime){
		List<TrustRecord> main=this.delegateMainDao.find("from TrustRecord d where d.companyId=? and d.trustor=? and (d.beginTime<=? and d.endTime>=? or d.beginTime<=? and d.endTime>=?) and d.style=?", companyId,myLoadName,beginTime,beginTime,endTime,endTime,style);
		String[] rIds=rolesIds.split(",");
		List<String> roleNames=new ArrayList<String>();
		for(int i=0;i<rIds.length;i++){
			Long rId=Long.parseLong(rIds[i].substring(1,rIds[i].length())); 
			Role role=ApiFactory.getAcsService().getRoleById(rId);
			String rName=role.getName()+"("+role.getBusinessSystem().getName()+")";
			roleNames.add(rName);
		}
		if(main!=null){
		for(int i=0;i<roleNames.size();i++){
			for(int j=0;j<main.size();j++){
				if(main.get(j).getSelectedRoleNames().indexOf(roleNames.get(i))>=0){
					return true;
				}
			}
			}
			
		}
		return false;
	}
	
	public Boolean validDelegate(Long companyId,String myLoadName,Short style,Date beginTime,Date endTime){
		TrustRecord main=this.delegateMainDao.findUnique("from TrustRecord d where d.companyId=? and d.trustor=? and d.style=? and (d.beginTime<=? and d.endTime>=? or d.beginTime<=? and d.endTime>=?)", companyId,myLoadName,style,beginTime,beginTime,endTime,endTime);
		if(main!=null){
			return true;
		}
		return false;
	}
	
	/**
	 * 查看是否存在和当前委托重复的全权委托
	 * 判断条件为：同意公司下，当前委托人存在开始时间或者结束时间位于当前委托的开始时间和结束时间中间的全权委托.
	 * 当然，不包括当前委托在内。
	 * @param delegateMain 当前委托
	 * @return 存在返回true，否则返回false
	 */
	public TrustRecord existentFullDelegate(TrustRecord delegateMain){
		String hql = "from TrustRecord d where d.companyId=? and d.trustor=? and (d.state=? or d.state=?) and d.style=2  and d.id<>? and  ((d.beginTime>=? and d.beginTime<=?)or(d.endTime>=? and d.endTime<=?))";
		TrustRecord result = this.delegateMainDao.findUnique(hql, delegateMain.getCompanyId(),delegateMain.getTrustor(),TrustRecordState.STARTED,TrustRecordState.EFFICIENT
				,delegateMain.getId(),delegateMain.getBeginTime(),delegateMain.getEndTime(),delegateMain.getBeginTime(),delegateMain.getEndTime());
		return result;
	}
	
	/**
	 * 查看是否存在和当前委托重复的流程委托
	 * 判断条件为：同意公司下，当前委托人存在开始时间或者结束时间位于当前委托的开始时间和结束时间中间的流程委托.
	 * 当然，不包括当前委托在内。
	 * @param delegateMain 当前委托
	 * @return 存在返回true，否则返回false
	 */
	public Map<String,Object> existentFlowDelegate(TrustRecord delegateMain){
		//key为trustRecord,value为TrustRecord；key为activityName，value为委托的环节名称
		Map<String,Object> results= new HashMap<String, Object>();
		if(delegateMain.getStyle()==1){
			StringBuilder hql = new StringBuilder("from TrustRecord d where d.companyId=? and d.trustor=? and (d.state=? or d.state=? ) and  d.style=1 and d.processId=? and (d.activityName=? or d.activityName like ? or d.activityName like ? or d.activityName like ?) and d.id<>?")
								.append(" and  ((d.beginTime>=? and d.beginTime<=?)or(d.endTime>=? and d.endTime<=?)or(d.beginTime<=? and d.endTime>=?)or(d.beginTime>=? and d.endTime<=?))");
			String[] activityNames=delegateMain.getActivityName().split(",");
			for(String activityName:activityNames){
				List<TrustRecord> trustRecords = this.delegateMainDao.find(hql.toString(), delegateMain.getCompanyId(),delegateMain.getTrustor(),TrustRecordState.STARTED,TrustRecordState.EFFICIENT,delegateMain.getProcessId(),activityName,activityName+",%","%,"+activityName+",%","%,"+activityName
						,delegateMain.getId(),delegateMain.getBeginTime(),delegateMain.getEndTime()
						,delegateMain.getBeginTime(),delegateMain.getEndTime()
						,delegateMain.getBeginTime(),delegateMain.getEndTime()
						,delegateMain.getBeginTime(),delegateMain.getEndTime());
				if(trustRecords.size()>0){
					results.put("trustRecord", trustRecords.get(0));
					results.put("activityName", activityName);
					break;
				}
			}
		}else if(delegateMain.getStyle()==2){
			TrustRecord result = null;
			StringBuilder hql = new StringBuilder("from TrustRecord d where d.companyId=? and d.trustor=? and (d.state=? or d.state=? ) and  d.style=1 and d.id<>? ")
			.append(" and  ((d.beginTime>=? and d.beginTime<=?)or(d.endTime>=? and d.endTime<=?)or(d.beginTime<=? and d.endTime>=?)or(d.beginTime>=? and d.endTime<=?))");
			result = this.delegateMainDao.findUnique(hql.toString(), delegateMain.getCompanyId(),delegateMain.getTrustor(),TrustRecordState.STARTED,TrustRecordState.EFFICIENT
					,delegateMain.getId(),delegateMain.getBeginTime(),delegateMain.getEndTime()
					,delegateMain.getBeginTime(),delegateMain.getEndTime()
					,delegateMain.getBeginTime(),delegateMain.getEndTime()
					,delegateMain.getBeginTime(),delegateMain.getEndTime());
			results.put("trustRecord", result);
			if(result!=null)
			results.put("activityName", result.getActivityName());
		}
		return results;
	}
	
	/**
	 * 启用委托
	 * @param id
	 * @return
	 */
	@Transactional(readOnly=false)
	public String startDelegateMain(Long id){
		TrustRecord delegateMain = getDelegateMain(id);
		switch(delegateMain.getState()){
			case STARTED:
				return errorMessage("该委托已启用");
			case EFFICIENT:
				return errorMessage("该委托已生效");
			case END:
				return errorMessage("该委托已结束");
			default:
				return startDelegateMain(delegateMain);
		}
	}
	@Transactional(readOnly=false)
	private String startDelegateMain(TrustRecord delegateMain){
		TrustRecord result;
		StringBuilder message = new StringBuilder();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		if(delegateMain.getEndTime().compareTo(cal.getTime())<0) return errorMessage("当前时间已经超过截止日期");
		if(delegateMain.getStyle()==3){
			//权限委托的启用
			//如果生效日期到截至日期包含当前日期，则将角色委托出去
			if(delegateMain.getBeginTime().compareTo(cal.getTime())<=0&&delegateMain.getEndTime().compareTo(cal.getTime())>=0){
				User trustee=ApiFactory.getAcsService().getUserByLoginName(delegateMain.getTrustee());
				User trustor=ApiFactory.getAcsService().getUserByLoginName(delegateMain.getTrustor());
				ApiFactory.getAcsService().assignTrustedRole(trustor.getId(), delegateMain.getRoleIds().split(","),trustee.getId() );
				delegateMain.setState(TrustRecordState.EFFICIENT);
			}else{
				delegateMain.setState(TrustRecordState.STARTED);
			}
			saveDelegateMain(delegateMain);	
		}else {
			//指定环节委托的启用
			//验证全权委托
			result = existentFullDelegate(delegateMain);
			if(result!=null){
				return errorMessage(message.append("在").append(SIMPLEDATEFORMAT.format(result.getBeginTime()))
					.append("到").append(SIMPLEDATEFORMAT.format(result.getEndTime())).append("时间内存在全权委托").toString()); 
			}
			//验证流程委托
			Map<String,Object> results = existentFlowDelegate(delegateMain);
			if(results.size()>0){
				Object obj = results.get("trustRecord");
				if(obj!=null){
					result = (TrustRecord)obj;
					return errorMessage(message.append("在").append(SIMPLEDATEFORMAT.format(result.getBeginTime()))
							.append("到").append(SIMPLEDATEFORMAT.format(result.getEndTime()))
							.append("时间内存在环节(").append(results.get("activityName")).append(")的委托").toString());
				}
			}
			//如果生效日期到截至日期包含当前日期，则将将该委托设为生效
			if(delegateMain.getBeginTime().compareTo(cal.getTime())<=0&&delegateMain.getEndTime().compareTo(cal.getTime())>=0){
				delegateMain.setState(TrustRecordState.EFFICIENT);
			}else{
				delegateMain.setState(TrustRecordState.STARTED);
			}
			saveDelegateMain(delegateMain);	
		}
	
		return successMessage("该委托成功启用");
		
	}
	

	@Transactional(readOnly=false)
	public String endDelegateMain(Long id) {
		TrustRecord delegateMain = getDelegateMain(id);
		switch(delegateMain.getState()){
			case NEW_CREATING:
				return errorMessage("该委托还没有启用");
			case CANCEL:
				return errorMessage("该委托已中止");
			case END:
				return errorMessage("该委托已结束");
			default:
				return endDelegateMain(delegateMain);
		}
	}
	@Transactional(readOnly=false)
	private String endDelegateMain(TrustRecord delegateMain) {
		if(delegateMain.getStyle()==3&&delegateMain.getState()==TrustRecordState.EFFICIENT){
			User trustee=ApiFactory.getAcsService().getUserByLoginName(delegateMain.getTrustee());//受托人
			User trustor=ApiFactory.getAcsService().getUserByLoginName( delegateMain.getTrustor());//委托人
			//权限委托的中止
			ApiFactory.getAcsService().deleteTrustedRole(trustor.getId(), delegateMain.getRoleIds().split(","),trustee.getId() );
		}
		delegateMain.setState(TrustRecordState.CANCEL);
		saveDelegateMain(delegateMain);	
		if(delegateMain.getStyle()==1 || delegateMain.getStyle()==2){
			//委托结束时取回任务
			taskService.recieveDelegateTask(delegateMain);
		}
		return successMessage("该委托成功中止");
	}
	
	/**
	 * 根据受托人获得委托人登录名集合
	 * @return
	 */
	public List<String> getConsignerByTrustee(String trustee,Long companyId,String processId,String activityName){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		
		List<String> names= getAllConsignerByTrustee(trustee,companyId,cal.getTime());
		if(names.size()>0)return names;
		
		List<String> consigners=this.delegateMainDao.find("select d.trustor from TrustRecord d where d.companyId=?  and d.state=? and d.trustee=? and d.processId=? and d.activityName=? and d.beginTime<=? and d.endTime>=? and d.style=1",
																				companyId,TrustRecordState.EFFICIENT,trustee,processId,activityName,cal.getTime(),cal.getTime());
		return consigners;
	}
	/**
	 * 根据受托人获得委托人集合
	 * @return
	 */
	public List<String> getAllConsignerByTrustee(String trustee,Long companyId,Date time){
		List<String> names=this.delegateMainDao.find("select d.trustor from TrustRecord d where d.companyId=? and d.state=? and d.trustee=? and d.beginTime<=? and d.endTime>=? and d.style=2", companyId,TrustRecordState.EFFICIENT,trustee,time,time);
		return names;
	}
	public static final SimpleDateFormat SIMPLEDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");

	
	private String errorMessage(String message){
		return ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT;
	}
	
	private String successMessage(String message){
		return SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT;
	}
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
}
