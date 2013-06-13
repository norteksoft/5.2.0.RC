package com.norteksoft.acs.service.authorization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.xwork.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.FunctionGroup;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.service.security.SecurityResourceCache;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.AuthFunction;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.MemCachedUtils;

/**
 * 系统管理 李洪超 2009-3-2上午11:39:38
 */
@Service
@Transactional
public class BusinessSystemManager {

	private static final String hql = "from BusinessSystem b where b.deleted=?";
	private SimpleHibernateTemplate<BusinessSystem, Long> businessDao;
	private SimpleHibernateTemplate<Role, Long> roleDao;
	private SimpleHibernateTemplate<FunctionGroup, Long> functionGroupDao;
	private SimpleHibernateTemplate<Function, Long> functionDao;
	private String deleted = "deleted";

	private Long companyId;

	public Long getCompanyId() {
		if (companyId == null) {
			return ContextUtils.getCompanyId();
		} else
			return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		businessDao = new SimpleHibernateTemplate<BusinessSystem, Long>(
				sessionFactory, BusinessSystem.class);
		functionGroupDao = new SimpleHibernateTemplate<FunctionGroup, Long>(
				sessionFactory, FunctionGroup.class);
		functionDao = new SimpleHibernateTemplate<Function, Long>(
				sessionFactory, Function.class);
		roleDao = new SimpleHibernateTemplate<Role, Long>(
				sessionFactory, Role.class);
	}

	/**
	 * 查询所有业务系统信息
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<BusinessSystem> getAllBusiness() {
		String hql = "select si.product.systemId from SubscriberItem si join si.subsciber s where s.tenantId=? and si.invalidDate>=?";
		List<Long> idList = businessDao.find(hql, getCompanyId(), new Date());
		if(idList.isEmpty()){
			return new ArrayList<BusinessSystem>();
		}
		return businessDao.findByCriteria(Restrictions.in("id",idList),Restrictions.eq("deleted",false));

	}

	/**
	 * 获取单条业务系统信息
	 */
	@Transactional(readOnly = true)
	public BusinessSystem getBusiness(Long id) {
		return businessDao.get(id);
	}

	/**
	 * 分页查询所有业务系统信息
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Page<BusinessSystem> getAllBusiness(Page<BusinessSystem> page) {
		String hql = "select p.systemId from Product p join p.subscibers s join s.tenant t"
				+ " where t.id=? and s.validDate > ? and s.deleted = ?";
		List<Long> idList = businessDao.find(hql, getCompanyId(), new Date(), false);
		if (idList.size() <= 0)
			idList.add(-1L);
		return businessDao.findByCriteria(page, Restrictions.in("id", idList),
				Restrictions.eq(deleted, false));

	}

	/**
	 * 保存业务系统信息，如果是新建业务系统，需要为系统建立三个标准角色
	 */
	public void saveBusiness(BusinessSystem businessSystem, boolean isCreate) {
		businessDao.save(businessSystem);
		//为业务系统添加三个管理员角色(标准角色)
//		if(isCreate){
//			Role systemAdmin = new Role((new StringBuffer(
//					businessSystem.getCode()).append("SystemAdmin")).toString(),"系统管理员");
//			Role securityAdmin = new Role((new StringBuffer(
//					businessSystem.getCode()).append("SecurityAdmin")).toString(),"安全管理员");
//			Role auditAdmin = new Role((new StringBuffer(
//					businessSystem.getCode()).append("AuditAdmin")).toString(),"审计管理员");
//			systemAdmin.setBusinessSystem(businessSystem);
//			securityAdmin.setBusinessSystem(businessSystem);
//			auditAdmin.setBusinessSystem(businessSystem);
//			roleDao.save(systemAdmin);
//			roleDao.save(securityAdmin);
//			roleDao.save(auditAdmin);
//		}
	}

	/**
	 * 删除业务系统信息
	 */
	public void deleteBusiness(Long id) {
		BusinessSystem businessSystem = businessDao.get(id);
		businessSystem.setDeleted(true);
		businessDao.save(businessSystem);
	}

	/**
	 * 公司添加业务系统(保存公司与业务系统的关系)
	 */
	public List<BusinessSystem> saveBusiness(List<Long> businessIds) {
		return businessDao.findByCriteria(Restrictions.in("id", businessIds));
	}

	
	
	/**
	 * 按条件检索部门
	 */
	@Transactional(readOnly = true)
	public Page<BusinessSystem> getSearchBusiness(Page<BusinessSystem> page,
			BusinessSystem businessSystem, boolean deleted) {
		StringBuilder businessHql = new StringBuilder(hql);
		if (businessSystem != null) {
			String code = businessSystem.getCode().trim();
			String businessName = businessSystem.getName().trim();
			String path = businessSystem.getPath().trim();

			if (!StringUtils.isEmpty(code)&&!StringUtils.isEmpty(businessName)&&!StringUtils.isEmpty(path)) {
				businessHql.append(" and b.code like ?");
				businessHql.append(" and b.name like ?");
				businessHql.append(" and b.path like ?");
				return businessDao.find(page, businessHql.toString(), false,
						"%" + code + "%", "%" + businessName + "%","%" + path + "%");
			}
			if (!StringUtils.isEmpty(code)&&!StringUtils.isEmpty(businessName)) {
				businessHql.append(" and b.code like ?");
				businessHql.append(" and b.name like ?");
				return businessDao.find(page, businessHql.toString(), false,
						"%" + code + "%", "%" + businessName + "%");
			}
			if (!StringUtils.isEmpty(businessName)&&!StringUtils.isEmpty(path)) {
				businessHql.append(" and b.name like ?");
				businessHql.append(" and b.path like ?");
				return businessDao.find(page, businessHql.toString(), false,
						"%" + businessName + "%","%" + path + "%");
			}
			if (!StringUtils.isEmpty(code)&&!StringUtils.isEmpty(path)) {
				businessHql.append(" and b.code like ?");
				businessHql.append(" and b.path like ?");
				return businessDao.find(page, businessHql.toString(), false,
						"%" + code + "%", "%" + path + "%");
			}

			if (!StringUtils.isEmpty(code)) {
				businessHql.append(" and b.code like ?");
				return businessDao.find(page, businessHql.toString(), false,
						"%" + code + "%");
			}

			if (!StringUtils.isEmpty(businessName)) {
				businessHql.append(" and b.name like ?");
				return businessDao.find(page, businessHql.toString(), false,
						"%" + businessName + "%");
			}
			if (!StringUtils.isEmpty(path)) {
				businessHql.append(" and b.path like ?");
				return businessDao.find(page, businessHql.toString(), false,
						"%" + path + "%");
			}
		}
		return businessDao.find(page, hql, false);
	}

	public void systemAddFunctionGroup(Long businessSystemId,
			FunctionGroup entity) {
		BusinessSystem businessSystem = businessDao.get(businessSystemId);
		entity.setBusinessSystem(businessSystem);
		functionGroupDao.save(entity);
	}

	public SimpleHibernateTemplate<BusinessSystem, Long> getBusinessDao() {
		return businessDao;
	}

	public SimpleHibernateTemplate<Role, Long> getRoleDao() {
		return roleDao;
	}

	public SimpleHibernateTemplate<FunctionGroup, Long> getFunctionGroupDao() {
		return functionGroupDao;
	}

	/**
	 * 专供销售系统使用：查询所有业务系统信息
	 */
	@Transactional(readOnly = true)
	public List<BusinessSystem> getAllSystem() {
		return businessDao.findByCriteria(Restrictions.eq(deleted, false));

	}

	/**
	 *  专供销售系统使用：分页查询所有业务系统信息
	 */
	@Transactional(readOnly = true)
	public Page<BusinessSystem> getAllSystem(Page<BusinessSystem> page) {
		return businessDao.findByCriteria(page, Restrictions.eq(deleted, false));

	}
	

	/**
	 * 根据系统编码获取业务系统
	 */
	@Transactional(readOnly = true)
	public BusinessSystem getSystemBySystemCode(String code){
		BusinessSystem bs = (BusinessSystem) businessDao.findUnique(
				"from BusinessSystem bs where bs.code=? and bs.deleted=?", code, false);
		return bs;
	}
	
	/**
	 * 查询所有业务系统信息
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<BusinessSystem> getAllSystems() {
		return businessDao.find("from BusinessSystem bs where bs.deleted=? order by id", false);
	}
	/**
	 * sales中更新资源缓存功能
	 */
	public void updateFunctionCache(){
		List<Function> functions = functionDao.findByCriteria(Restrictions.eq("deleted", false));
		String pathHashCode = "";
		for(Function function: functions){
			AuthFunction authFun=new AuthFunction();
			authFun.setFunctionPath(function.getPath());
			authFun.setFunctionId(function.getCode());
			String funPath=function.getPath();
			if(StringUtils.isNotEmpty(funPath)){
				//底层系统应用地址
				if(function.getBusinessSystem()!=null){
					if(StringUtils.isNotEmpty(function.getBusinessSystem().getParentCode())){//表示是子系统，则在资源路径前加系统编码
						pathHashCode = String.valueOf(("/"+function.getBusinessSystem().getCode()+function.getPath()).hashCode());
						MemCachedUtils.add(pathHashCode, authFun);
					}else{
						pathHashCode = String.valueOf(function.getPath().hashCode());
						MemCachedUtils.add(pathHashCode, authFun);
					}
				}
			}
		}
		List<BusinessSystem> systems=getAllParentSystems();
//		boolean ifImatrixCache=false;
		for(BusinessSystem system:systems){
			String url=system.getPath();
			if(StringUtils.isNotEmpty(url)){
//				//底层系统应用地址
//				String imatrixCode=PropUtils.getProp("host.imatrix");
//				imatrixCode=imatrixCode.substring(imatrixCode.lastIndexOf("/")+1);
//				if(StringUtils.isNotEmpty(url)&&url.contains(imatrixCode)){//表示是imatrix底层应用
//					if(!ifImatrixCache){
//						url=PropUtils.getProp("host.imatrix")+"/portal/autoAuth.action";
//						ifImatrixCache=true;
//						//更新不受保护的资源缓存
//						getHttpConnection(url);
//					}
//				}else{
					url=url+"/portal/autoAuth.action?systemCode="+system.getCode();
					//更新不受保护的资源缓存
					getHttpConnection(url);
//				}
			}
		}
	}
	
	private void getHttpConnection(String url){
		HttpGet httpget = new HttpGet(url);
		HttpClient httpclient = new DefaultHttpClient();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		try {
			httpclient.execute(httpget, responseHandler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		httpclient.getConnectionManager().shutdown();
	}
	
	/**
	 * 根据父系统编码查询系统信息
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Long> getSystemIdsByParentCode(String parentCode) {
		return businessDao.find("select bs.id from BusinessSystem bs where bs.parentCode=? and bs.deleted=? order by id", parentCode,false);
	}
	/**
	 * 查询所有父系统信息，即父系统编码字段为null的系统信息
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<BusinessSystem> getAllParentSystems() {
		return businessDao.find("from BusinessSystem bs where (bs.parentCode=null or bs.parentCode='') and bs.deleted=? order by id", false);
	}
	/**
	 * 获得平台系统
	 * @return
	 */
	public List<BusinessSystem> getParentSystem(){
		String hql="from BusinessSystem bs where (bs.parentCode is null or bs.parentCode=?) and bs.deleted=? order by id";
		List<BusinessSystem> imatrixSystems= businessDao.find(hql,"",false);
		return imatrixSystems;
	}
	public boolean isParentCodeEmpty(Long systemId){
		BusinessSystem system=getBusiness(systemId);
		if(StringUtils.isEmpty(system.getParentCode())){
			return true;
		}
		return false;
	}
}
