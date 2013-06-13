package com.norteksoft.acs.service.log;


import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.enumeration.OperatorType;
import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.base.utils.log.LogUtilDao;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.log.Log;
import com.norteksoft.acs.entity.log.LoginLog;
import com.norteksoft.acs.entity.sysSetting.SecuritySetting;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.security.DynamicAuthority;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;


/**
*  日志管理
*/
@SuppressWarnings("deprecation")
@Service
@Transactional
public class LogManager {
	
	private SimpleHibernateTemplate<Log, Long> logDao;
	private SimpleHibernateTemplate<LoginLog, Long> loginUserLogDao;
	private LogUtilDao logUtilDao;
	private static String searchSql = "from Log as log where 1=1 and log.companyId=? and log.systemId=? and log.deleted=? ";
	private static String ACS = "acs";
	private SimpleHibernateTemplate<SecuritySetting, Long> securitySetDao;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory){
		logDao = new SimpleHibernateTemplate<Log, Long>(sessionFactory, Log.class);
		loginUserLogDao = new SimpleHibernateTemplate<LoginLog, Long>(sessionFactory, LoginLog.class);
		securitySetDao = new SimpleHibernateTemplate<SecuritySetting, Long>(sessionFactory, SecuritySetting.class);
		logUtilDao = new LogUtilDao(sessionFactory);
	}
	
	private Long companyId;
	
	public Long getCompanyId() {
		if(companyId == null){
			return ContextUtils.getCompanyId();
		}else 
			return companyId;
	}
	
	public Long getSystemIdByCode(String code) {
		return acsUtils.getSystemsByCode(code).getId();
    }
	
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	
	/**
	 * 保存
	 * @param log
	 */
	public void saveLog(Log log){
		log.setAdminLog(ContextUtils.isAdmin());
		logDao.save(log);
	}
	
	/**
	 * 删除
	 * @param id
	 */
	public void deleteLog(Long id){
		Log log = logDao.get(id);
		log.setDeleted(true);
		logDao.save(log);
	}
	
	/**
	 * 查询
	 * @param id
	 * @return
	 */
	public Log getLog(Long id){
		return logDao.get(id);
	}
	
	@Autowired
	private AcsUtils acsUtils;
	
	/**
	 * 分页查询
	 * @param page
	 * @return
	 */
	public Page<Log> getAllLog(Page<Log> page,Long sysId){
		StringBuilder sql = new StringBuilder("from Log l where l.companyId=? and l.systemId=? and l.deleted=?");
		List<Object> prmts = new ArrayList<Object>();
		prmts.add(ContextUtils.getCompanyId());
		prmts.add(sysId);
		prmts.add(false);
		
		Object[] prmt = getSystemLogSqlPrmts(sql, prmts);
		
		logDao.searchPageByHql(page, sql.toString(), prmt);
		return page;
	}
	
	private Object[] getLogSqlPrmts(Map<OperatorType, List<OperatorType>> authMap, StringBuilder sql, List<Object> prmts){
		List<OperatorType> auths = authMap.get(getOperatorType());
		for(int i=0;i<auths.size();i++){
			if(i==0) sql.append(" and (l.operatorType=?");
			else sql.append(" or l.operatorType=?");
			prmts.add(auths.get(i));
		}
		if(!auths.isEmpty())sql.append(")");
		return prmts.toArray(new Object[prmts.size()]);
	}
	
	private Object[] getSystemLogSqlPrmts(StringBuilder sql, List<Object> prmts){
		Map<OperatorType, List<OperatorType>> authMap = DynamicAuthority.getSystemLogAuthority();
		return getLogSqlPrmts(authMap, sql, prmts);
	}
	
	private Object[] getLoginLogSqlPrmts(StringBuilder sql, List<Object> prmts){
		Map<OperatorType, List<OperatorType>> authMap = DynamicAuthority.getLoginLogAuthority();
		return getLogSqlPrmts(authMap, sql, prmts);
	}
	
	public static OperatorType getOperatorType(){
		if(ContextUtils.isSystemAdmin()){
			return OperatorType.SYSTEM_ADMIN;
		}else if(ContextUtils.isSecurityAdmin()){
			return OperatorType.SECURITY_ADMIN;
		}else if(ContextUtils.isAuditAdmin()){
			return OperatorType.AUDIT_ADMIN;
		}
		return OperatorType.COMMON_USER;
	}
	
	public boolean isAcsSystem(List<BusinessSystem> list,Long sysId){
		for(BusinessSystem bs:list){
			if(bs.getId().equals(sysId)&&bs.getCode().equals("acs"))return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public List<Log> getAllLogs(Long sysId){
		StringBuilder sql = new StringBuilder("from Log l where l.companyId=? and l.systemId=? and l.deleted=?");
		List<Object> prmts = new ArrayList<Object>();
		prmts.add(ContextUtils.getCompanyId());
		prmts.add(sysId);
		prmts.add(false);
		
		Object[] prmt = getSystemLogSqlPrmts(sql, prmts);
		
		return logDao.find(sql.toString(),prmt);
	}
	
	/**
	 * 删除系统日志
	 * @param page
	 * @return
	 */
	public String deleteSysLogs(String sysIds){
		int successNum=0;
		SecuritySetting securitySet=null;
		List<SecuritySetting> list = securitySetDao.findByCriteria(Restrictions.eq(
				"name", "log-set"), Restrictions.eq("companyId",
				getCompanyId()));
		if(!list.isEmpty()){
			securitySet=list.get(0);
		}
		String[] sIds=sysIds.split(",");
		for(int i=0;i<sIds.length;i++){
			Log log=getLog(Long.parseLong(sIds[i]));
			if(securitySet!=null){
				if(shouldDeleteSystemLog(log,securitySet)){//日志创建时间+保留时间<=当前时间
					successNum++;
					deleteLog(Long.parseLong(sIds[i]));
				}
			}else{
				successNum++;
				deleteLog(Long.parseLong(sIds[i]));
			}
		}
		StringBuilder result=new StringBuilder();
		result.append(successNum).append("个删除成功,").append(sIds.length-successNum).append("个在保留时间内未删除。");
		return result.toString();
	}
	
	/**
	 * 删除所有系统日志
	 * @param page
	 * @return
	 */
	public String deleteAllSysLog(String sysId){
		int successNum=0;
		SecuritySetting securitySet=null;
		List<SecuritySetting> list = securitySetDao.findByCriteria(Restrictions.eq(
				"name", "log-set"), Restrictions.eq("companyId",
				getCompanyId()));
		if(!list.isEmpty()){
			securitySet=list.get(0);
		}
		List<Log> sLogs = getAllLogs(Long.parseLong(sysId));
		for(Log log : sLogs){
			if(securitySet!=null){
				if(shouldDeleteSystemLog(log,securitySet)){//日志创建时间+保留时间<=当前时间
					successNum++;
					deleteLog(log.getId());
				}
			}else{
				successNum++;
				deleteLog(log.getId());
			}
		}
		StringBuilder result=new StringBuilder();
		result.append(successNum).append("个删除成功,").append(sLogs.size()-successNum).append("个在保留时间内未删除。");
		return result.toString();
	}
	/**
	 * 是否可以删除日志
	 * @param log
	 * @param securitySet
	 * @return
	 */
	private boolean shouldDeleteSystemLog(Log log,SecuritySetting securitySet){
		long currentTime=new Date().getTime();
		return log.getTs().getTime()+Integer.parseInt(securitySet.getValue())*24*60*60*1000<=currentTime;
	}
	
	/**
	 * 查询所有
	 * @return
	 */
	public List<Log> getAllLog(){
		return logDao.findByCriteria(Restrictions.eq("companyId", getCompanyId()),Restrictions.eq("deleted", false));
	}

	/**
	 * 按条件查询
	 * @param page
	 * @param values
	 * @return
	 */
	public Page<Log> getLogByCondition(Page<Log> page,Log entity,Long sysId){
		StringBuilder hql = new StringBuilder(searchSql);
		if(entity.getOperator()!=null&&!"".equals(entity.getOperator())&&entity.getMessage()!=null&&!"".equals(entity.getMessage())){
			hql.append(" and  log.operator like ?");
			hql.append(" and  log.message like ? order by log.createdTime desc");
			return logDao.find(page, hql.toString(), getCompanyId(),sysId,false,"%"+entity.getOperator()+"%","%"+entity.getMessage()+"%");
		}
		if(entity.getOperator()!=null&&!"".equals(entity.getOperator())){
			hql.append(" and  log.operator like ? order by log.createdTime desc");
			return logDao.find(page, hql.toString(), getCompanyId(),sysId,false,"%"+entity.getOperator()+"%");
		}
		if(entity.getMessage()!=null&&!"".equals(entity.getMessage())){
			hql.append(" and  log.message like ? order by log.createdTime desc");
			return logDao.find(page, hql.toString(), getCompanyId(),sysId,false,"%"+entity.getMessage()+"%");
		}
		if(hql.indexOf("like")==-1){
			hql.append(" order by log.createdTime desc");
		}
		return logDao.find(page, hql.toString(),  getCompanyId(),sysId,false);
		
	}
	
	public Page<LoginLog> getloginUserLogAllByCompanyId(Page<LoginLog> page,Long companyId){
		StringBuilder sql = new StringBuilder("from LoginLog l where l.companyId=? and l.deleted=?");
		List<Object> prmts = new ArrayList<Object>();
		prmts.add(ContextUtils.getCompanyId());
		prmts.add(false);
		Object[] prmt = getLoginLogSqlPrmts(sql, prmts);
		loginUserLogDao.searchPageByHql(page, sql.toString(), prmt);
		return page;
	}
	
	@SuppressWarnings("unchecked")
	public List<LoginLog> getLoginUserLogs(){
		StringBuilder sql = new StringBuilder("from LoginLog l where l.companyId=? and l.deleted=?");
		List<Object> prmts = new ArrayList<Object>();
		prmts.add(ContextUtils.getCompanyId());
		prmts.add(false);
		Object[] prmt = getLoginLogSqlPrmts(sql, prmts);
		return loginUserLogDao.find(sql.toString(), prmt);
	}
	
	public String deleteloginUserLogAllByCompanyId(String loginLogIds, Long companyId){
		int successNum=0;
		SecuritySetting securitySet=null;
		List<SecuritySetting> list = securitySetDao.findByCriteria(Restrictions.eq(
				"name", "log-set"), Restrictions.eq("companyId",
				getCompanyId()));
		if(!list.isEmpty()){
			securitySet=list.get(0);
		}
		String[] ids=loginLogIds.split(",");
		for(int i=0;i<ids.length;i++){
			LoginLog log=loginUserLogDao.get(Long.parseLong(ids[i]));
			if(securitySet!=null){
				if(shouldDeleteLoginLog(log,securitySet)){
					successNum++;
					loginUserLogDao.delete(Long.parseLong(ids[i]));
				}
			}else{
				successNum++;
				loginUserLogDao.delete(Long.parseLong(ids[i]));
			}
		}
		StringBuilder result=new StringBuilder();
		result.append(successNum).append("个删除成功,").append(ids.length-successNum).append("个在保留时间内未删除。");
		return result.toString();
	}
	
	public String deleteAllLoginUserLog(){
		int successNum=0;
		SecuritySetting securitySet=null;
		List<SecuritySetting> list = securitySetDao.findByCriteria(Restrictions.eq(
				"name", "log-set"), Restrictions.eq("companyId",
				getCompanyId()));
		if(!list.isEmpty()){
			securitySet=list.get(0);
		}
		List<LoginLog> luls = this.getLoginUserLogs();
		for(LoginLog lul : luls){
			if(securitySet!=null){
				if(shouldDeleteLoginLog(lul,securitySet)){
					successNum++;
					loginUserLogDao.delete(lul);
				}
			}else{
				successNum++;
				loginUserLogDao.delete(lul);
			}
		}
		StringBuilder result=new StringBuilder();
		result.append(successNum).append("个删除成功,").append(luls.size()-successNum).append("个在保留时间内未删除。");
		return result.toString();
	}
	
	/**
	 * 是否可以删除日志
	 * @param log
	 * @param securitySet
	 * @return
	 */
	private boolean shouldDeleteLoginLog(LoginLog log,SecuritySetting securitySet){
		long currentTime=new Date().getTime();
		return log.getTs().getTime()+Integer.parseInt(securitySet.getValue())*24*60*60*1000<=currentTime;
	}
	
	public Map<String,String> lookLog(Long id) throws DocumentException{
		Log log = logDao.get(id);
		return getLogMapValues(log.getXmlText());
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,String> getLogMapValues(String xmlText) throws DocumentException{
		Map<String,String> map = new LinkedHashMap<String,String>();
		Document document1 = DocumentHelper.parseText(xmlText);
		Element root = document1.getRootElement();
		
	    for( Iterator iterInner = root.elementIterator(); iterInner.hasNext(); ) {
		   Element elementInner = (Element) iterInner.next();
		   map.put(elementInner.getName(), elementInner.getText());
	    }
		return map;
	}
	
	/**
	 * 在线用户查询 
	 */
	 public Page<LoginLog> getListByLoginUserLog(Page<LoginLog> page,LoginLog loginUserLog,Long companyId){
		 String loginUserLogHql = "from LoginLog as loginLog where loginLog.companyId=? and loginLog.deleted=? order by loginLog.loginTime desc";
		 StringBuilder hql = new StringBuilder(loginUserLogHql);
		 return loginUserLogDao.searchPageByHql(page, hql.toString(),companyId,false);
	 }
	 
	 /**
	 * 在线用户查询 
	 */
	 @SuppressWarnings("unchecked")
	public List<Object[]> getTopkOnline(Long companyId,int rows){
		StringBuilder hql = new StringBuilder();
		hql.append("select u.id,u.name from acs_user u join (");
		hql.append("select tt.user_id from (select count(t.longin_time-t.exit_time) time_,t.user_id from acs_login_log t ");
		hql.append("where t.fk_company_id=?  group by t.user_id order by time_ desc) tt where rownum <= ?) ttt on u.id=ttt.user_id  ");
		SQLQuery query = loginUserLogDao.getSession().createSQLQuery(hql.toString());
		query.setParameter(0, companyId);
		query.setParameter(1, rows);
		return query.list();
	 }
	 
	 /**
	 * 通过userId获得登录记录 
	 */
	 public List<LoginLog> getLoginRecordByUserId(Long userId){
		 return loginUserLogDao.findList("from LoginLog t where t.userId=? order by loginTime desc",userId);
	 }
	
}
