package com.norteksoft.acs.service.log;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.log.LoginLog;
import com.norteksoft.acs.service.ContextService;

@Service
@Transactional
public class LoginUserLogManager {
	
	private SimpleHibernateTemplate<LoginLog, Long> loginUserLogDao;
	@Autowired
	private ContextService contextService;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		loginUserLogDao = new SimpleHibernateTemplate<LoginLog, Long>(
				sessionFactory, LoginLog.class);
	}
	
	public void saveLoginUserLog(LoginLog log){
		log.setAdminLog(contextService.isAdmin());
		loginUserLogDao.save(log);
	}
	
	@Transactional(readOnly=true)
	public LoginLog getLoginUserLogByCondition(String loginName){
		List<LoginLog> logs = loginUserLogDao.findByCriteria(Restrictions.eq("userName", loginName), Restrictions.isNull("exitTime"));
		if(logs.size() == 1){
			return logs.get(0);
		}
		return null;
	}
	
	public List<LoginLog> getLoginLogs(Long userId){
		List<LoginLog> logs = loginUserLogDao.findByCriteria(Restrictions.eq("userId", userId), Restrictions.isNull("exitTime"));
		return logs;
	}
	
	public List<LoginLog> getLoginUserLogBySystemId(){
		List<LoginLog> logs = loginUserLogDao.findByCriteria(Restrictions.isNull("exitTime"));
		return logs;
	}
}
