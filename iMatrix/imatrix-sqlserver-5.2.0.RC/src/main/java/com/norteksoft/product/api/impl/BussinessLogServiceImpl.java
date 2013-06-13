package com.norteksoft.product.api.impl;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.norteksoft.acs.base.enumeration.OperatorType;
import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.product.api.entity.BusinessSystem;
import com.norteksoft.acs.entity.log.Log;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.BussinessLogService;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Service
@Transactional
public class BussinessLogServiceImpl implements BussinessLogService {
	
	private SimpleHibernateTemplate<Log, Long> logDao;

	public void log(String operationType, String message) {
		debugSaveOrUpdateLOG(operationType, message, null);
	}

	public void log(String operationType, String message, Long systemId) {
		debugSaveOrUpdateLOG(operationType, message, systemId);
	}

	public void log(String operator, String operationType, String message) {
		Log log = createLog(message, null);
		log.setOperator(operator);
		log.setOperationType(operationType);
		logDao.save(log);
	}
	
	private void debugSaveOrUpdateLOG(String optType, String message, Long systemId){
		Log log = createLog(message, systemId);
		log.setOperationType(optType);
		logDao.save(log);
	}

	private Log createLog(String message, Long systemId){
		Log log = new Log();
		HttpServletRequest request= Struts2Utils.getRequest();
		if(request!=null){
			log.setIpAddress(request.getRemoteHost());
			log.setOperator(ContextUtils.getUserName());
			log.setOperatorType(getOperatorType());
		}else{
			log.setIpAddress("0.0.0.0");
			log.setOperator("系统日志");
			log.setOperatorType(OperatorType.SYSTEM_ADMIN);
		}
		log.setAdminLog(false);
		log.setCreatedTime(new Date());
        log.setMessage(message);
        Assert.notNull(ContextUtils.getCompanyId(), "company不能为null");
        Assert.notNull(ContextUtils.getCompanyName(), "companyName不能为null");
        Assert.notNull(ContextUtils.getSystemId(), "systemId不能为null");
	    log.setCompanyId(ContextUtils.getCompanyId());
		log.setCompanyName(ContextUtils.getCompanyName());
		if(systemId != null){
			log.setSystemId(systemId);
			BusinessSystem system=ApiFactory.getAcsService().getSystemById(systemId);
			if(system!=null)log.setSystemName(system.getName());
		}else{
			log.setSystemName(ContextUtils.getSystemName());
			log.setSystemId(ContextUtils.getSystemId());
		}
		return log;
	}
	
	private OperatorType getOperatorType(){
		if(ContextUtils.isSystemAdmin()){
			return OperatorType.SYSTEM_ADMIN;
		}else if(ContextUtils.isSecurityAdmin()){
			return OperatorType.SECURITY_ADMIN;
		}else if(ContextUtils.isAuditAdmin()){
			return OperatorType.AUDIT_ADMIN;
		}
		return OperatorType.COMMON_USER;
	}
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		logDao = new SimpleHibernateTemplate<Log, Long>(sessionFactory, Log.class);
	}
	public void log(Long operatorId, String operationType, String message) {
		User user = ApiFactory.getAcsService().getUserById(operatorId);
		if(user == null) throw new RuntimeException("记录日志时，没有找到给定的用户，用户ID:["+operatorId+"]. ");
		Log log = new Log();
		log.setOperationType(operationType);
		log.setIpAddress("0.0.0.0");
		log.setOperator(user.getName());
		log.setOperatorType(getOperatorType2(user));
		log.setAdminLog(false);
		log.setCreatedTime(new Date());
        log.setMessage(message);
        log.setCompanyId(user.getCompanyId());
		log.setCompanyName(getCompanyNameById(user.getCompanyId()));
		log.setSystemName(ContextUtils.getSystemName());
		log.setSystemId(ContextUtils.getSystemId());
		logDao.save(log);
	}
	
	private String getCompanyNameById(Long id){
		CompanyManager cm = (CompanyManager) ContextUtils.getBean("companyManager");
		Company c = cm.getCompany(id);
		if(c != null) c.getName();
		return null;
	}
	
	private OperatorType getOperatorType2(User user){
		String codes = getRoleCodesStartComma(user);
		if(codes != null && codes.contains(",acsSystemAdmin,")){
			return OperatorType.SYSTEM_ADMIN;
		}else if(codes != null && codes.contains(",acsSecurityAdmin,")){
			return OperatorType.SECURITY_ADMIN;
		}else if(codes != null && codes.contains(",acsAuditAdmin,")){
			return OperatorType.AUDIT_ADMIN;
		}
		return OperatorType.COMMON_USER;
	}
	
	private String getRoleCodesStartComma(User user){
		String roleCode = ApiFactory.getAcsService().getRolesExcludeTrustedRole(user);
		if(!roleCode.startsWith(",")) roleCode=","+roleCode+",";
		return roleCode;
	}
}
