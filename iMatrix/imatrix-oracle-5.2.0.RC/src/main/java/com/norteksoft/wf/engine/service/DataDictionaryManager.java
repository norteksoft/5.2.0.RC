package com.norteksoft.wf.engine.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.rank.dao.RankUserDao;
import com.norteksoft.bs.rank.entity.Subordinate;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.base.enumeration.DataDictAllUsers;
import com.norteksoft.wf.base.enumeration.DataDictUseType;
import com.norteksoft.wf.base.enumeration.DataDictUserType;
import com.norteksoft.wf.engine.dao.DataDictionaryDao;
import com.norteksoft.wf.engine.dao.DataDictionaryProcessDao;
import com.norteksoft.wf.engine.dao.DataDictionaryTypeDao;
import com.norteksoft.wf.engine.dao.DataDictionaryUserDao;
import com.norteksoft.wf.engine.entity.DataDictionary;
import com.norteksoft.wf.engine.entity.DataDictionaryProcess;
import com.norteksoft.wf.engine.entity.DataDictionaryType;
import com.norteksoft.wf.engine.entity.DataDictionaryUser;

@Service
@Transactional
public class DataDictionaryManager {

	private Log log = LogFactory.getLog(DataDictionaryManager.class);
	private DataDictionaryDao dataDictionaryDao;
	private DataDictionaryTypeDao dataDictionaryTypeDao;
	private DataDictionaryUserDao dataDictionaryUserDao;
	private DataDictionaryProcessDao dataDictionaryProcessDao;
	private RankUserDao rankUserDao;
	
	@Autowired
	public void setDataDictionaryUserDao(DataDictionaryUserDao dataDictionaryUserDao) {
		this.dataDictionaryUserDao = dataDictionaryUserDao;
	}
	
	@Autowired
	public void setDataDictionaryDao(DataDictionaryDao dataDictionaryDao) {
		this.dataDictionaryDao = dataDictionaryDao;
	}
	
	@Autowired
	public void setDataDictionaryProcessDao(
			DataDictionaryProcessDao dataDictionaryProcessDao) {
		this.dataDictionaryProcessDao = dataDictionaryProcessDao;
	}
	
	@Autowired
	public void setRankUserDao(RankUserDao rankUserDao) {
		this.rankUserDao = rankUserDao;
	}
	
	@Autowired
	public void setDataDictionaryTypeDao(DataDictionaryTypeDao dataDictionaryTypeDao) {
		this.dataDictionaryTypeDao = dataDictionaryTypeDao;
	}

	@Transactional(readOnly=false)
	public void saveDataDict(DataDictionary dict, String[] users,String[] deptNames,String[] groupNames,String[] processes,String[] rankNames){
		log.debug("***saveDataDict方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("dict:").append(dict)
		.append(", users:").append(users)
		.append(", deptNames:").append(deptNames)
		.append(", groupNames:").append(groupNames)
		.append(", processes:").append(processes)
		.append("]").toString());
		dict.setCompanyId(getCompanyId());
		dict.setSystemId(ContextUtils.getSystemId("wf"));
		dataDictionaryDao.save(dict);
		List<DataDictionaryUser> ddus = dataDictionaryUserDao.getDDUs(dict.getId(), getCompanyId());
		for(DataDictionaryUser ddu : ddus){
			dataDictionaryUserDao.delete(ddu);
		}
		List<DataDictionaryProcess> ddps = dataDictionaryProcessDao.getAllDictProcessesByDictId(dict.getId());
		for(DataDictionaryProcess ddp : ddps){
			dataDictionaryProcessDao.delete(ddp);
		}
		saveAllDictUser(dict.getId(),users,deptNames,groupNames,rankNames);
		if(dict.getProcessType()==1){
			if(processes!=null && processes.length>0){
				saveProcesses(dict,processes);
			}
		}
		log.debug("*** saveDataDict 方法结束");
	}
	
	@Transactional(readOnly=false)
	private void saveAllDictUser(Long dictId,String[] users,String[] deptNames,String[] groupNames,String[] rankNames){
		log.debug("***saveAllDictUser方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("dictId:").append(dictId)
		.append("]").toString());
		if(users!=null && users.length>0){
			saveDictUsers(dictId,users);
		}
		if(deptNames!=null && deptNames.length>0){
			saveDictUsers(dictId,deptNames);
		}
		if(groupNames!=null && groupNames.length>0){
			saveDictUsers(dictId,groupNames);
		}
		if(rankNames!=null && rankNames.length>0){
			saveDictUsers(dictId,rankNames);
		}
		log.debug("***saveAllDictUser方法结束");
	}
	
	@Transactional(readOnly=false)
	public void deleteDataDict(Long id){
		log.debug("***deleteDataDict方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("dictId:").append(id)
		.append("]").toString());
		List<DataDictionaryUser> ddus = dataDictionaryUserDao.getDDUs(id,getCompanyId());
		for(DataDictionaryUser ddu:ddus){
			dataDictionaryUserDao.delete(ddu);
		}
		List<DataDictionaryProcess> ddps = dataDictionaryProcessDao.getAllDictProcessesByDictId(id);
		for(DataDictionaryProcess ddp : ddps){
			dataDictionaryProcessDao.delete(ddp);
		}
		dataDictionaryDao.delete(id);
		log.debug("***deleteDataDict方法结束");
	}
	
	@Transactional(readOnly=false)
	public void deleteDataDict(String ids){
	    List<Long> dids=getList(ids);
	    for(int i=0;i<dids.size();i++){
	    	List<DataDictionaryUser> ddus = dataDictionaryUserDao.getDDUs(dids.get(i),getCompanyId());
			for(DataDictionaryUser ddu:ddus){
				dataDictionaryUserDao.delete(ddu);
			}
			List<DataDictionaryProcess> ddps = dataDictionaryProcessDao.getAllDictProcessesByDictId(dids.get(i));
			for(DataDictionaryProcess ddp : ddps){
				dataDictionaryProcessDao.delete(ddp);
			}
			dataDictionaryDao.delete(dids.get(i));	
	    }
	}	
	
	public static List<Long> getList(String ids){
		String[] dids=ids.split(",");
		List<Long> id=new ArrayList<Long>();
		for(int i=0;i<dids.length;i++){
			id.add(Long.parseLong(dids[i]));
		}
		return id;
	}
	
	@Transactional(readOnly=false)
	public void deleteDataDict(DataDictionary dict){
		dataDictionaryDao.delete(dict);
	}
	
	public DataDictionary getDataDict(Long id){
		if(id == null) throw new RuntimeException("没有给定查询数据字的查询条件： 数据字典的ID. ");
		return dataDictionaryDao.get(id);
	}
	
	public Long getCompanyId(){
		return ContextUtils.getCompanyId();
	}
	
	public Long getSystemId(){
    	return ContextUtils.getSystemId();
    }
	
	public void getDataDicts(Page<DataDictionary> dictPage,String typeNo,String typeName,String queryName){
		log.debug("***getDataDicts方法开始");
		log.debug(new StringBuilder("***")
		.append("companyId:").append(getCompanyId()).toString());
		if(StringUtils.isEmpty(typeNo) && StringUtils.isEmpty(typeName) && StringUtils.isEmpty(queryName)){
			dataDictionaryDao.getDataDicts(dictPage,getCompanyId());
		}else if(StringUtils.isEmpty(typeNo) && StringUtils.isNotEmpty(typeName)&& StringUtils.isEmpty(queryName)){
			dataDictionaryDao.getDataDictsByTypeName(dictPage,getCompanyId(),typeName);
		}else if(StringUtils.isNotEmpty(typeNo) && StringUtils.isEmpty(typeName)&& StringUtils.isEmpty(queryName) ){
			dataDictionaryDao.getDataDictsByTypeNo(dictPage,getCompanyId(),typeNo);
		}else if(StringUtils.isEmpty(typeNo) && StringUtils.isEmpty(typeName)&& StringUtils.isNotEmpty(queryName) ){
			dataDictionaryDao.getDataDictsByInfo(dictPage,getCompanyId(),queryName);
		}else if(!StringUtils.isEmpty(typeNo) && !StringUtils.isEmpty(typeName)&& StringUtils.isEmpty(queryName) ){
			dataDictionaryDao.getDataDictsByTypeNoAndName(dictPage,getCompanyId(),typeNo,typeName);
		}else if(StringUtils.isEmpty(typeNo) && StringUtils.isNotEmpty(typeName)&& StringUtils.isNotEmpty(queryName) ){
			dataDictionaryDao.getDataDictsByInfoAndTypeName(dictPage,getCompanyId(),typeName,queryName);
		}else if(StringUtils.isNotEmpty(typeNo) && StringUtils.isEmpty(typeName)&& StringUtils.isNotEmpty(queryName) ){
			dataDictionaryDao.getDataDictsByInfoAndTypeNo(dictPage,getCompanyId(),typeNo,queryName);
		}else{
			dataDictionaryDao.getDataDictsByInfoAndTypeNoAndName(dictPage,getCompanyId(),typeNo,typeName,queryName);
		}
		log.debug("***getDataDicts方法结束");
	}
	
	
	public Object[] getDictProcessesByDictId(Long dictId){
		log.debug("***getDictProcessesByDictId方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("dictId:").append(dictId)
		.append("]").toString());
		Object[] result=new Object[2];
		List<List<DataDictionaryProcess>> processResult=new ArrayList<List<DataDictionaryProcess>>();
		List<DataDictionaryProcess> list = dataDictionaryProcessDao.getAllDictProcessesByDictId(dictId);
		List<DataDictionaryProcess> processes=new ArrayList<DataDictionaryProcess>();
		List<DataDictionaryProcess> processTaches=new ArrayList<DataDictionaryProcess>();
		List<String[]> viewResult=new ArrayList<String[]>();
		for(DataDictionaryProcess ddp:list){
			String[] str=new String[2];
			if(ddp.getTacheName()==null || "".equals(ddp.getTacheName())){
				processes.add(ddp);
				str[0]=ddp.getProcessDefinitionName();
				str[1]=ddp.getProcessDefinitionId().toString();
			}else{
				processTaches.add(ddp);
				str[0]=ddp.getProcessDefinitionName()+"["+ddp.getTacheName()+"]";
				str[1]=ddp.getProcessDefinitionId().toString();
			}
			viewResult.add(str);
		}
		processResult.add(processes);
		processResult.add(processTaches);
		result[0]=processResult;
		result[1]=viewResult;
		log.debug(new StringBuilder("*** Result:[")
		.append("processes:").append(processes)
		.append("]").toString());
		log.debug("***getDictProcessesByDictId方法结束");
		return result;
	}
	
	/**
	 * 增加人员关联
	 * @param dictId
	 * @param users:userType;userName[loginName]/userType;deptName[deptId]/userType;workGroupName[workGroupId]
	 */
	@Transactional(readOnly=false)
	public void saveDictUsers(Long dictId, String[] users){
		log.debug("***saveDictUsers方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("dictId:").append(dictId)
		.append("users:").append(users)
		.append("]").toString());
		DataDictionaryUser ddu = null;
		String name="";
		String info="";//loginName/deptId,groupId
		for(String user : users){
			if(DataDictAllUsers.ALL_USERS.toString().equals(user)){
				log.debug(new StringBuilder("*** “所有人员”保存")
				.toString());
				saveDictAllUsers(user,dictId);
			}else{
				log.debug(new StringBuilder("*** 非所有人员保存")
				.toString());
				ddu = new DataDictionaryUser();
				ddu.setDictId(dictId);
				Integer type=Integer.parseInt(user.substring(0,user.indexOf(";")));
				name=user.substring(user.indexOf(";")+1,user.indexOf("["));
				info=user.substring(user.indexOf("[")+1,user.indexOf("]"));
				ddu.setType(getUserType(type));
				if(type==0){//人员时
					ddu.setLoginName(info);
				}else{
					ddu.setInfoId(Long.parseLong(info));
				}
				ddu.setInfoName(name);
				ddu.setCompanyId(getCompanyId());
				dataDictionaryUserDao.save(ddu);
				log.debug(ddu);
			}
		}
		log.debug("***saveDictUsers方法结束");
	}
	
	private DataDictUserType getUserType(Integer type){
		if(type==0){
			return DataDictUserType.USER;
		}else if(type==1){
			return DataDictUserType.DEPARTMENT;
		}else if(type==2){
			return DataDictUserType.WORKGROUP;
		}else{
			return DataDictUserType.RANK;
		}
	}
	
	/**
	 * 当是“所有人员”时的保存操作
	 * */
	@Transactional(readOnly=false)
	private void saveDictAllUsers(String user,Long dictId){
		log.debug("***saveDictAllUsers方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("dictId:").append(dictId)
		.append("user:").append(user)
		.append("]").toString());
		DataDictionaryUser ddu = null;
		ddu = new DataDictionaryUser();
		ddu.setDictId(dictId);
		ddu.setType(DataDictUserType.USER);
		ddu.setLoginName(DataDictAllUsers.ALL_USERS.toString());
		ddu.setCompanyId(getCompanyId());
		dataDictionaryUserDao.save(ddu);
		log.debug(ddu);
		log.debug("***saveDictAllUsers方法结束");
	}
	
	/**
	 * processes:processId;processName[tachName],....
	 * */
	@Transactional(readOnly=false)
	private void saveProcesses(DataDictionary dict,String[] processes){
		log.debug("***saveProcesses方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("dict:").append(dict)
		.append("processes:").append(processes)
		.append("]").toString());
		DataDictionaryProcess ddp = null;
		String processName="";
		String tacheName="";
		for(String processTache : processes){
			ddp = new DataDictionaryProcess();
			ddp.setDataDictionary(dict);
			ddp.setProcessDefinitionId(Long.parseLong(processTache.substring(0, processTache.indexOf(";"))));
			if(processTache.indexOf("[")<=0){
				processName=processTache.substring(processTache.indexOf(";")+1,processTache.length());
				ddp.setTacheName("");
			}else{
				processName=processTache.substring(processTache.indexOf(";")+1,processTache.indexOf("["));
				tacheName=processTache.substring(processTache.indexOf("[")+1,processTache.indexOf("]"));
				ddp.setTacheName(tacheName);
			}
			ddp.setProcessDefinitionName(processName);
			dataDictionaryProcessDao.save(ddp);
			log.debug(ddp);
		}
		log.debug("***saveProcesses方法结束");
	}
	
	/**
	 * 删除人员关联
	 * @param dictId
	 * @param users
	 */
	@Transactional(readOnly=false)
	public void deleteDictUsers(Long dictId, String[] users){
		for(String user : users){
			dataDictionaryUserDao.deleteDdu(dictId, user, getCompanyId());
		}
	}
	
	/**
	 * 根据类型查询所有关联的办理人信息:类型为:人员，部门，工作组
	 * @param dictId
	 * @param type:0：人员;1:部门；2:工作组
	 */
	public Object[] getDataDictUsers(Long dictId){
		log.debug("***getDataDictUsersByType方法");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("dictId:").append(dictId)
		.append("]").toString());
		Object[] result = new Object[2];
		List<List<DataDictionaryUser>> listResult=new ArrayList<List<DataDictionaryUser>>();
		List<DataDictionaryUser> dduUsers=new ArrayList<DataDictionaryUser>();
		List<DataDictionaryUser> depts=new ArrayList<DataDictionaryUser>();
		List<DataDictionaryUser> workGroups=new ArrayList<DataDictionaryUser>();
		List<DataDictionaryUser> dataRanks=new ArrayList<DataDictionaryUser>();
		List<DataDictionaryUser> lists = dataDictionaryUserDao.getDDUs(dictId,getCompanyId());
		StringBuffer sb=new StringBuffer();
		String strResult="";
		for(DataDictionaryUser ddu:lists){
			if(ddu.getType()==DataDictUserType.USER){
				dduUsers.add(ddu);
			}else if(ddu.getType()==DataDictUserType.DEPARTMENT){
				depts.add(ddu);
			}else if(ddu.getType()==DataDictUserType.WORKGROUP){
				workGroups.add(ddu);
			}else{
				dataRanks.add(ddu);
			}
			if(DataDictAllUsers.ALL_USERS.toString().equals(ddu.getLoginName())){
				sb.append("所有人员");
			}else{
				sb.append(ddu.getInfoName());
			}
			sb.append(",");
		}
		
		if(lists!=null && lists.size()>0){
			log.debug(new StringBuilder("*** Result:[")
			.append("result:").append(sb.substring(0,sb.lastIndexOf(",")))
			.append("]").toString());
			log.debug("***getViewInfoByDictId方法结束");
			strResult=sb.substring(0,sb.lastIndexOf(","));
		}
		listResult.add(dduUsers);
		listResult.add(depts);
		listResult.add(workGroups);
		listResult.add(dataRanks);
		result[0]=listResult;
		result[1]=strResult;
		return result;
	}
	
	/**
	 * 根据类别查询数据字典
	 * */
	public List<DataDictionary> getDataDictsByTypeId(Long typeId,Long companyId){
		return dataDictionaryDao.getDataDictsByTypeId(typeId,companyId);
	}
	

	/**
	 * 根据给定的条件查询数据字典
	 * @param condition
	 * @return
	 */
	public List<DataDictionary> queryDataDict(String condition, List<Object> values){
		StringBuilder hql = new StringBuilder();
		if(StringUtils.isEmpty(condition)){
			hql.append("from DataDictionary dict where dict.companyId=? ");
		}else{
			hql.append("select dict from DataDictionary dict left join dict.dataDictionaryProcess process where dict.companyId=? ");
			hql.append(condition);
		}
		Object[] objs = new Object[values.size()+1];
		objs[0] = getCompanyId();
		for(int i = 0; i < values.size(); i++){
			objs[i+1] = values.get(i);
		}
		hql.append(" order by dict.displayIndex desc");
		return dataDictionaryDao.find(hql.toString(), objs);
	}
	
	/**
	 * 根据数据字典查询人员
	 * @param dictIds
	 * @return
	 */
	public Set<String[]> getCandidate(List<Long> dictIds){
		if(dictIds == null) throw new RuntimeException("没有给定查询数据字典办理人的查询条件： 数据字典的ID集合. ");
		Set<String[]> loginNames = new HashSet<String[]>();
		for(Long id : dictIds){
			getCandidate(id, loginNames);
		}
		return loginNames;
	}
	
	public Set<String[]> getCandidate(Long dictId, Set<String[]> loginNames){
		if(dictId == null) throw new RuntimeException("没有给定查询数据字典办理人的查询条件： 数据字典的ID. ");
		List<DataDictionaryUser> users = dataDictionaryUserDao.find("from DataDictionaryUser u where u.companyId=? and u.dictId=?", getCompanyId(), dictId);
		for(DataDictionaryUser user : users){
			getCandidate(user, loginNames);
		}
		return loginNames;
	}
	
	private Set<String[]> getCandidate(DataDictionaryUser user, Set<String[]> loginNames){
		switch (user.getType()){
			//case PERSON : ;
			case USER : loginNames.add(new String[]{user.getLoginName(), user.getInfoName()}); break;
			//case DEPARTMENT : ;
			case DEPARTMENT : getLoginNamesByDept(user.getInfoId(), loginNames); break;
			//case WORKGROUP : ;
			case WORKGROUP : getLoginNamesByWorkgroup(user.getInfoId(), loginNames); break;
			//case RANK : ;
			case RANK : getLoginNamesByRank(user.getInfoId(), loginNames); break;	
		}
		return loginNames;
	}
	
	private void getLoginNamesByDept(Long deptId, Set<String[]> loginNames){
		List<User> users = ApiFactory.getAcsService().getUsersByDepartmentId(deptId);
		for(User user : users){
			loginNames.add(new String[]{user.getLoginName(), user.getName()});
		}
	}
	
	private void getLoginNamesByWorkgroup(Long wgId, Set<String[]> loginNames){
		List<User> users = ApiFactory.getAcsService().getUsersByWorkgroupId(wgId);
		for(User user : users){
			loginNames.add(new String[]{user.getLoginName(), user.getName()});
		}
	}
	
	private void getLoginNamesByRank(Long rankId, Set<String[]> loginNames){
		List<Subordinate> ddrs = rankUserDao.getDataDictRankUsersByRankId(rankId);
		for(Subordinate ddr : ddrs){
			User user=ApiFactory.getAcsService().getUserById(ddr.getTargetId());
			loginNames.add(new String[]{user.getLoginName(), user.getName()});
		}
	}
	
	public List<DataDictionary> queryDataDict(String typeNo,DataDictUseType dataDictUseType){
		if(StringUtils.isEmpty(typeNo)) throw new RuntimeException("没有给定查询数据字典集合的查询条件： 数据字典类型编号. ");
		if(dataDictUseType ==null ) throw new RuntimeException("没有给定查询数据字典集合的查询条件： 用途[DataDictUseType]. ");
		StringBuilder hql = new StringBuilder();
		DataDictionaryType dictType=dataDictionaryTypeDao.getDictTypeByNo(typeNo, getCompanyId());
		hql.append("from DataDictionary dict where dict.companyId=? ");
		hql.append(" and (dict.typeNo=? ");
		Object[] objs =null;
		if(dictType.getTypeIds()!=null){
			String[] ids=dictType.getTypeIds().split(",");
			objs = new Object[ids.length+3];
			for(int i=0;i<ids.length;i++){
				dictType=dataDictionaryTypeDao.get(Long.parseLong(ids[i].trim()));
				hql.append(" or dict.typeNo=? ");
				objs[i+2] =dictType.getNo();
			}
		}else{
			objs = new Object[3];
		}
		objs[0] = getCompanyId();
		objs[1]=typeNo;
		hql.append(") and dict.type=? ");
		objs[objs.length-1] =dataDictUseType.getCode();
		hql.append(" order by dict.typeNo, dict.displayIndex desc");
		return dataDictionaryDao.find(hql.toString(), objs);
	}
	
	public List<String> getCandidate(String title){
		if(StringUtils.isEmpty(title)) throw new RuntimeException("没有给定查询数据字典办理人登录名的查询条件： 数据字典标题. ");
		//去掉重复人员
		Set<String> sr=new HashSet<String>();
		List<DataDictionaryUser> ddus=dataDictionaryDao.getCandidate(title);
		for(DataDictionaryUser ddu:ddus){
			if(ddu.getType().equals(0)){//人员时
				sr.add(ddu.getLoginName());
			}else if(ddu.getType().equals(1)){
				List<User> list=ApiFactory.getAcsService().getUsersByDepartmentId(ddu.getInfoId());
				for(User user:list){
					sr.add(user.getLoginName());
				}
			}else{
				List<User> list=ApiFactory.getAcsService().getUsersByWorkgroupId(ddu.getInfoId());
				for(User user:list){
					sr.add(user.getLoginName());
				}
			}
		}
		//根据权重排序
		List<String> users=new ArrayList<String>();
		users.addAll(sr);
		//得到有序的登录名集合
		List<String> resultUsers=new ArrayList<String>();
		if(users.size()>0){
			List<User> results=ApiFactory.getAcsService().getUsersByLoginNames(getCompanyId(),users);
			Collections.sort(results, new Comparator<User>() {
				public int compare(User user1, User user2) {
					if(user1.getWeight()<user2.getWeight()){
						return 1;
					}
					return 0;
				}
			});
			for(User u:results){
				resultUsers.add(u.getLoginName());
			}
		}
		return resultUsers;
	}
	
	/**
	 * 根据数据字典查询人员和附加信息
	 * @param dictIds
	 * @return
	 */
	public List<String> getCandidateAddition(List<Long> dictIds) {
		if(dictIds == null) throw new RuntimeException("没有给定查询数据字典及其备注的查询条件： 数据字典ID集合. ");
		List<String> loginNames=new ArrayList<String>();
		for(Long dictId:dictIds){
			getCandidateAddition(dictId,loginNames);
		}
		return loginNames;
	}
	public List<String> getCandidateAddition(Long dictId, List<String> loginNames){
		if(dictId == null) throw new RuntimeException("没有给定查询数据字典及其备注的查询条件： 数据字典ID. ");
		if(loginNames == null) loginNames=new ArrayList<String>();
		DataDictionary  dd=dataDictionaryDao.get(dictId);
		List<DataDictionaryUser> users = dataDictionaryUserDao.find("from DataDictionaryUser u where u.companyId=? and u.dictId=?", getCompanyId(), dictId);
		for(DataDictionaryUser user : users){
			getCandidateAddition(user, loginNames,dd.getRemark());
		}
		return loginNames;
	}
	
	private List<String> getCandidateAddition(DataDictionaryUser user, List<String> loginNames,String remark){
		switch (user.getType()){
		case USER : loginNames.add(user.getLoginName()+":"+remark); break;
		case DEPARTMENT : getLoginNameAdditionsByDept(user.getInfoId(), loginNames,remark); break;
		case WORKGROUP : getLoginNameAdditionsByWorkgroup(user.getInfoId(), loginNames,remark); break;
			
		}
		return loginNames;
	}
	
	private void getLoginNameAdditionsByDept(Long deptId,List<String> loginNames,String remark){
		List<User> users = ApiFactory.getAcsService().getUsersByDepartmentId(deptId);
		for(User user : users){
			loginNames.add(user.getLoginName()+":"+remark);
		}
	}
	
	private void getLoginNameAdditionsByWorkgroup(Long wgId,List<String> loginNames,String remark){
		List<User> users = ApiFactory.getAcsService().getUsersByWorkgroupId(wgId);
		for(User user : users){
			loginNames.add(user.getLoginName()+":"+remark);
		}
	}
	
	/**
	 * 根据数据字典查询人员
	 * @param dictIds
	 * @return HashMap
	 */
	public HashMap<String,String> getUserNames(List<Long> dictIds){
		if(dictIds == null) throw new RuntimeException("没有给定查询数据字典办理人登陆名和办理人的查询条件： 数据字典的ID集合. ");
		HashMap<String,String> loginNames = new HashMap<String,String>();
		for(Long id : dictIds){
			getUser(id, loginNames);
		}
		return loginNames;
	}
	
	public HashMap<String,String> getUser(Long dictId, HashMap<String,String> loginNames){
		if(dictId == null) throw new RuntimeException("没有给定查询数据字典办理人登陆名和办理人的查询条件： 数据字典的ID. ");
		if(loginNames == null) loginNames = new HashMap<String, String>();
		List<DataDictionaryUser> users = dataDictionaryUserDao.find("from DataDictionaryUser u where u.companyId=? and u.dictId=?", getCompanyId(), dictId);
		for(DataDictionaryUser user : users){
			getUser(user, loginNames);
		}
		return loginNames;
	}
	
	private HashMap<String,String> getUser(DataDictionaryUser user, HashMap<String,String> loginNames){
		switch (user.getType()){
			//case PERSON : ;
			case USER : loginNames.put(user.getLoginName(), user.getInfoName()); break;
			//case DEPARTMENT : ;
			case DEPARTMENT : getUserLoginNamesByDept(user.getInfoId(), loginNames); break;
			//case WORKGROUP : ;
			case WORKGROUP : getUserLoginNamesByWorkgroup(user.getInfoId(), loginNames); break;
			//case RANK : ;
			case RANK : getUserLoginNamesByRank(user.getInfoId(), loginNames); break;	
		}
		return loginNames;
	}
	
	private void getUserLoginNamesByDept(Long deptId, HashMap<String,String> loginNames){
		List<User> users = ApiFactory.getAcsService().getUsersByDepartmentId(deptId);
		for(User user : users){
			loginNames.put(user.getLoginName(), user.getName());
		}
	}
	
	private void getUserLoginNamesByWorkgroup(Long wgId, HashMap<String,String> loginNames){
		List<User> users = ApiFactory.getAcsService().getUsersByWorkgroupId(wgId);
		for(User user : users){
			loginNames.put(user.getLoginName(), user.getName());
		}
	}
	
	private void getUserLoginNamesByRank(Long rankId, HashMap<String,String> loginNames){
		List<Subordinate> ddrs = rankUserDao.getDataDictRankUsersByRankId(rankId);
		for(Subordinate ddr : ddrs){
			User user=ApiFactory.getAcsService().getUserById(ddr.getTargetId());
			loginNames.put(user.getLoginName(), user.getName());
		}
	}
	
	public List<DataDictionary> queryDataDicts(String loginName){
		if(StringUtils.isEmpty(loginName)) throw new RuntimeException("没有给定查询数据字典的查询条件： 办理人登录名. ");
		return dataDictionaryDao.getDataDicts(loginName, getCompanyId());
	}
	public List<DataDictionaryProcess> getAllDictProcessesByDictId(Long dictId,Long companyId){
		return dataDictionaryProcessDao.getAllDictProcessesByDictId(dictId, companyId);
	}
	public List<DataDictionaryUser> getDDUs(Long dictId, Long companyId){
		return dataDictionaryUserDao.getDDUs(dictId, companyId);
	}
	public DataDictionary getDataDictByTitle(String title){
		return dataDictionaryDao.getDataDictByTitle(title);
	}
	@Transactional(readOnly=false)
	public void saveDict(DataDictionary dict){
		dataDictionaryDao.save(dict);
	}
	public DataDictionaryProcess getDictProcessByDef(Long defId,Long dictId,String tachName){
		return dataDictionaryProcessDao.getDictProcessByDef(defId, dictId, tachName);
	}
	@Transactional(readOnly=false)
	public void saveDictProcess(DataDictionaryProcess process){
		dataDictionaryProcessDao.save(process);
	}
	public DataDictionaryUser getDictUserByType(Long dictId,DataDictUserType type,String loginName,Long infoId){
		return dataDictionaryUserDao.getDictUserByType(dictId, type, loginName, infoId);
	}
	@Transactional(readOnly=false)
	public void saveDictUser(DataDictionaryUser dictUser){
		dataDictionaryUserDao.save(dictUser);
	}
}
