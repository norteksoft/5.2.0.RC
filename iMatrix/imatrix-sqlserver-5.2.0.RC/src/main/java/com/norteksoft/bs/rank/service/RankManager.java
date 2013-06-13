package com.norteksoft.bs.rank.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.bs.rank.dao.RankDao;
import com.norteksoft.bs.rank.dao.RankUserDao;
import com.norteksoft.bs.rank.entity.Superior;
import com.norteksoft.bs.rank.entity.Subordinate;
import com.norteksoft.bs.rank.enumeration.SubordinateType;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;

@Service
@Transactional
public class RankManager {
	private Log log = LogFactory.getLog(RankManager.class);
	private RankDao dataDictionaryRankDao;
	private RankUserDao dataDictionaryRankuserDao;
	private UserManager userManager;
	
	@Autowired
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	
	@Autowired
	public void setDataDictionaryRankDao(RankDao dataDictionaryRankDao) {
		this.dataDictionaryRankDao = dataDictionaryRankDao;
	}
	@Autowired
	public void setDataDictionaryRankuserDao(
			RankUserDao dataDictionaryRankuserDao) {
		this.dataDictionaryRankuserDao = dataDictionaryRankuserDao;
	}

	public Long getSystemId(){
    	return ContextUtils.getSystemId();
    }
	
	public Long getCompanyId(){
		return ContextUtils.getCompanyId();
	}
	public String getLoginName(){
		return ContextUtils.getLoginName();
	}
	
	public void getDataDictRanksPage(Page<Superior> dictRankPage){
		dataDictionaryRankDao.getDataDictRanksPage(dictRankPage,getCompanyId());
	}
	
	public Superior getDataDictRankById(Long id){
		return dataDictionaryRankDao.get(id);
	}
	@Transactional(readOnly=false)
	public void saveDataDictRank(Superior dataDictionaryRank,List<String> userInfos){
		dataDictionaryRank.setCompanyId(getCompanyId());
		dataDictionaryRank.setSystemId(getSystemId());
		dataDictionaryRank.setCreator(getLoginName());
		dataDictionaryRankDao.save(dataDictionaryRank);
		List<Subordinate> ddrus=dataDictionaryRankuserDao.getDataDictRankUsersByRankId(dataDictionaryRank.getId());
		for(Subordinate ddru:ddrus){
			dataDictionaryRankuserDao.delete(ddru);
		}
		for(String info:userInfos){
			String[] infos=info.split(";");
			String infoName=infos[0];
			Long infoId=null;
			if(!"".equals(infos[1])){
				infoId=Long.parseLong(infos[1]);
			}
			String loginName=null;
			if(infos.length==4){
				loginName=infos[3];
			}
			Subordinate dataDictRankUser=new Subordinate();
			dataDictRankUser.setCompanyId(getCompanyId());
			dataDictRankUser.setSystemId(getSystemId());
			dataDictRankUser.setName(infoName);
			dataDictRankUser.setTargetId(infoId);
			dataDictRankUser.setLoginName(loginName);
		    Integer type=Integer.parseInt(infos[2]);
			if(type==0){
				dataDictRankUser.setSubordinateType(SubordinateType.USER);
			}else if(type==1){
				dataDictRankUser.setSubordinateType(SubordinateType.DEPARTMENT);
			}else{
				dataDictRankUser.setSubordinateType(SubordinateType.WORKGROUP);
			}
			dataDictRankUser.setDataDictionaryRank(dataDictionaryRank);
			dataDictionaryRankuserDao.save(dataDictRankUser);
		}
	}
	@Transactional(readOnly=false)
	public void deleteDataDictRanks(String dictRankIds){
		List<Long> dids=getList(dictRankIds);;
	    for(int i=0;i<dids.size();i++){
	    	List<Subordinate> ddrus=dataDictionaryRankuserDao.getDataDictRankUsersByRankId(dids.get(i));
			for(Subordinate ddru:ddrus){
				dataDictionaryRankuserDao.delete(ddru);
			}
	    	dataDictionaryRankDao.delete(dids.get(i));
	    }
	}
	public User getDirectLeader(Long userId) {
		return getDirectLeader(userId, getCompanyId());
	}

	
	/**
     * 根据用户ID查询该用户的直属领导
     * @param userId
     * @return
     */
	public User getDirectLeader(Long userId, Long companyId) {
		if(userId == null) throw new RuntimeException("没有给定查询直属领导的查询条件： 人员ID. ");
		if(companyId == null) throw new RuntimeException("没有给定查询直属领导的查询条件： 公司ID. ");
		List<Superior> dicts = dataDictionaryRankDao.getDirectLeader(userId, companyId);
		Long leaderId = null;
		if(!dicts.isEmpty()){
			leaderId = getLeaderIdFromDict(dicts);
		}else{
			ThreadParameters parameters = new ThreadParameters(companyId);
			ParameterUtils.setParameters(parameters);
			List<Department> depts = userManager.getDepartmentsByUserId(userId);
			dicts = dataDictionaryRankDao.getDirectLeader(depts, companyId);
			if(!dicts.isEmpty()){
				leaderId = getLeaderIdFromDict(dicts);
			}
		}
		if(leaderId == null) return null;
		else return userManager.getUserById(leaderId);
	}

	/**
     * 根据用户登录名查询该用户的直属领导列表
     * @param loginName
     * @return
     */
	public List<User> getDirectLeaders(String loginName) {
		return getDirectLeaders(loginName,getCompanyId());
	}
	/**
     * 根据用户登录名查询该用户的直属领导
     * @param loginName
     * @return
     */
	public List<User> getDirectLeaders(String loginName,Long companyId) {
		if(loginName == null) throw new RuntimeException("没有给定查询直属领导集合的查询条件： 人员登录名. ");
		if(companyId == null) throw new RuntimeException("没有给定查询直属领导集合的查询条件：  公司ID. ");
 		List<Superior> dicts = dataDictionaryRankDao.getDirectLeader(loginName,companyId);
		Long leaderId = null;
		List<User> directs=new ArrayList<User>();
		if(!dicts.isEmpty()){
			for(Superior rank:dicts){
				leaderId =rank.getUserId();
				User user=userManager.getUserById(leaderId);
				directs.add(user);
			}
		}else{
			ThreadParameters parameters = new ThreadParameters(companyId);
			ParameterUtils.setParameters(parameters);
			List<Department> depts = userManager.getDepartments(loginName);
			dicts = dataDictionaryRankDao.getDirectLeader(depts, companyId);
			if(!dicts.isEmpty()){
				for(Superior rank:dicts){
					leaderId =rank.getUserId();
					User user=userManager.getUserById(leaderId);
					directs.add(user);
				}
			}
		}
		if(directs.size()<=0) return null;
		else return directs;
	}
	/**
     * 根据用户登录名查询该用户的直属领导
     * @param loginName
     * @return
     */
	public User getDirectLeader(String loginName) {
		return getDirectLeader(loginName,getCompanyId());
	}
	/**
     * 根据用户登录名查询该用户的直属领导
     * @param loginName
     * @return
     */
	public User getDirectLeader(String loginName,Long companyId) {
		if(loginName == null) throw new RuntimeException("没有给定查询直属领导的查询条件： 人员登录名. ");
		if(companyId == null) throw new RuntimeException("没有给定查询直属领导的查询条件： 公司ID. ");
		List<Superior> dicts = dataDictionaryRankDao.getDirectLeader(loginName,companyId);
		Long leaderId = null;
		if(!dicts.isEmpty()){
			leaderId = getLeaderIdFromDict(dicts);
		}else{
			ThreadParameters parameters = new ThreadParameters(companyId);
			ParameterUtils.setParameters(parameters);
			List<Department> depts = userManager.getDepartments(loginName);
			dicts = dataDictionaryRankDao.getDirectLeader(depts, companyId);
			if(!dicts.isEmpty()){
				leaderId = getLeaderIdFromDict(dicts);
			}
		}
		if(leaderId == null) return null;
		else return userManager.getUserById(leaderId);
	}
	
	private Long getLeaderIdFromDict(List<Superior> dicts){
		Long leaderId = null;
		if(dicts.size() == 1){
			leaderId = dicts.get(0).getUserId();
		}else{
			log.debug(" *** query direct leader error. DataDictionaryRank num [" + dicts.size() + "] *** ");
		}
		return leaderId;
	}
	
	public List<Superior> getDataDictRanks(String value){
		 return dataDictionaryRankDao.getDataDictRanks(getCompanyId(),value);
	}
	
	public static List<Long> getList(String ids){
		String[] dids=ids.split(",");
		List<Long> id=new ArrayList<Long>();
		for(int i=0;i<dids.length;i++){
			id.add(Long.parseLong(dids[i]));
		}
		return id;
	}

	public List<Superior> getRanks(Long companyId){
		return dataDictionaryRankDao.getRanks(companyId);
	}
	public Superior getRankByTitle(String rankTitle){
		return dataDictionaryRankDao.getRankByTitle(rankTitle);
	}
	@Transactional(readOnly=false)
	public void saveDataDictRank(Superior rank){
	    	dataDictionaryRankDao.save(rank);
    }
}
