package com.norteksoft.acs.base.utils.log;


import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.norteksoft.acs.base.enumeration.OperatorType;
import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.product.api.entity.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.log.Log;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Repository
public class LogUtilDao {
	
    //private static AnalyzeAnnotation analyzeAnnotation;
    private SimpleHibernateTemplate<Log, Long> logDao;
	private SimpleHibernateTemplate<Function, Long> functionDao;
	
	public LogUtilDao(){}
	
	public LogUtilDao(SessionFactory sessionFactory){ 
		setSessionFactory(sessionFactory);
		//analyzeAnnotation = AnalyzeAnnotation.getInstance();
	}
	
	public String  recordOperateLog(HttpServletRequest request){
		List<Function> Functions = functionDao.findByCriteria(Restrictions.eq("deleted", false));
		String url = request.getRequestURI();
		String functionName = null;
		for (Function function : Functions) {
			if(url.endsWith(function.getPath())){
				functionName = function.getName();
				break;
			}
		}
		return functionName;
	}
	
	/**
	 * 对数据库操作调用此方法
	 * message简单的格式为动宾语句格式
	 * 例如1：查看用户：列表
	 * 例如2：新建用户:用户名[admin]
	 * 添加关系
	 * 例如3：部门加入人员:部门名称[开发部];人员名称{用户1,用户2,用户3,用户4,用户5}
	 */
//	public synchronized  void debugLog(Object entity,String message){
//		debugSaveOrUpdateLOG(entity, message);
//	}
	
    /**
     * 
	 * message简单的格式为动宾语句格式
	 * 例如1：查看用户：列表
	 * 例如2：新建用户:用户名[admin]
	 * 添加关系
	 * 例如3：部门加入人员:部门名称[开发部];人员名称{用户1,用户2,用户3,用户4,用户5}
	 */
//    public synchronized void debugLog(String message){
//		debugSaveOrUpdateLOG(message);
//	}
    
    public void debugLog(String optType, String message){
		debugSaveOrUpdateLOG(optType, message, null);
	}
    
    public void debugLog(String optType, String message, Long systemId){
		debugSaveOrUpdateLOG(optType, message, systemId);
	}
    
    /**
     * 日志记录
     * @param optPerson 操作人
     * @param optType   操作类型
     * @param message   日志信息
     */
    public void debugLog(String operatorName, String optType, String message){
    	Log log = createLog(message, null);
		log.setOperator(operatorName);
		log.setOperationType(optType);
		logDao.save(log);
	}

    /**
     * entity 目前设为NULL，为以后扩展所用
	 * message简单的格式为动宾语句格式
	 * 例如：查看:用户列表,新建:用户名是admin；
	 */
//	private void debugSaveOrUpdateLOG(Object entity,String message){
//		Log log = createLog(message);
//		logDao.save(log);
//	}

	private void debugSaveOrUpdateLOG(String optType, String message, Long systemId){
		Log log = createLog(message, systemId);
		log.setOperationType(optType);
		logDao.save(log);
	}
	
	/**
	 * message简单的格式为动宾语句格式
	 * 例如：查看用户列表；
	 * 
	 */
//	private void debugSaveOrUpdateLOG(String message){
//		Log log = createLog(message);
//		logDao.save(log);
//	}
	
	private Log createLog(String message, Long systemId){
		Log log = new Log();
		if(Struts2Utils.getRequest()!=null){
			log.setIpAddress(Struts2Utils.getRequest().getRemoteHost());
			log.setOperator(ContextUtils.getTrueName());
			log.setOperatorType(getOperatorType());
		}else{
			log.setIpAddress("0.0.0.0");
			log.setOperator("系统日志");
			log.setOperatorType(OperatorType.SYSTEM_ADMIN);
		}
		log.setAdminLog(ContextUtils.isAdmin());
		log.setCreatedTime(new Date());
        log.setMessage(getMessage(message));
	    log.setCompanyId(getCompanyId());
		log.setCompanyName(getCompanyName());
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
	
	public boolean isHaveId(Object entity) throws SecurityException, NoSuchMethodException{
		boolean istrue =false;
		Method[] method = entity.getClass().getDeclaredMethods();
		for (Method mm : method) {
			if(mm.getName().equals("getId")){
				istrue = true;
				break;
			}
		}
		return istrue;
	}
	
	/**
	 * 例如1：查看用户：列表
	 * 例如2：新建用户:用户名[admin]
	 * 添加关系
	 * 例如3：部门加入人员:部门名称[开发部];人员名称{用户1,用户2,用户3,用户4,用户5}
	 */
	private String getMessage(String message){
		StringBuilder meg = new StringBuilder(message);
		int index_1 = meg.indexOf(":");
		int index_2 = meg.indexOf("[");
		int index_3 = meg.indexOf("]");
		int index_4 = meg.indexOf("];");
		int index_5 = meg.indexOf("{");
		int index_6 = meg.indexOf("}");
		int temp = 0;
		if(index_1>0){
			meg.setCharAt(index_1, ';');
			
		}
		if(index_2>0){
			meg.setCharAt(index_2, ':');
			
		}
		if(index_4>0){
			meg.deleteCharAt(index_4);
			temp++;
			
		}
		if(index_3>0&&temp==0){
			meg.setCharAt(index_3, ';');
			
		}
		if(index_5>0){
			meg.setCharAt(index_5-temp, ':');
			
		}
		if(index_6>0){
			meg.deleteCharAt(index_6-temp);
			temp++;
			
		}
		return meg.toString();
	}
	
	public Object getObject(Object child,Session session){
		//另外一个方向从代理本身的方向来考虑，实际上经过hibernate代理后的对象实现了org.hibernate.proxy.HibernateProxy 接口，通过该接口可以取得被代理的原始对象，如下：
		Object target = null;
		if(child instanceof HibernateProxy){
	        HibernateProxy proxy = (HibernateProxy)child;
	        target = proxy.getHibernateLazyInitializer().getImplementation();
	        return target;
	     }
		return child;
	}
	

	public Long getCompanyId() {
		return ContextUtils.getCompanyId();
	}

	public String getBusinessName() {
		return ContextUtils.getSystemName();
	}

	public String getCompanyName() {
		return ContextUtils.getCompanyName();
	}

	public Long getSysId() {
		return ContextUtils.getSystemId();
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		logDao = new SimpleHibernateTemplate<Log, Long>(sessionFactory, Log.class);
		functionDao = new SimpleHibernateTemplate<Function, Long>(sessionFactory, Function.class);
	}
}
