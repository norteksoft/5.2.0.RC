package com.norteksoft.acs.service.organization;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.MailDeploy;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.entity.sale.Subsciber;
import com.norteksoft.acs.entity.sale.SubscriberItem;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;

/**
 * 单位管理
 */

@Service
@Transactional
public class CompanyManager {
	private Log log = LogFactory.getLog(CompanyManager.class);
	public DepartmentManager departmentManager;
	public Department department;
	public WorkGroupManager workGroupManager;
	public Workgroup workGroup;

	private Long companyId;
	private SimpleHibernateTemplate<Company, Long> companyDao;
	private SimpleHibernateTemplate<Department, Long> departmentDao;
	private SimpleHibernateTemplate<Workgroup, Long> workGroupDao;
	private SimpleHibernateTemplate<Subsciber, Long> subsciberDao;
	private SimpleHibernateTemplate<MailDeploy, Long> mailDeployDao;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		companyDao = new SimpleHibernateTemplate<Company, Long>(sessionFactory,
				Company.class);
		departmentDao = new SimpleHibernateTemplate<Department, Long>(
				sessionFactory, Department.class);
		workGroupDao = new SimpleHibernateTemplate<Workgroup, Long>(
				sessionFactory, Workgroup.class);
		subsciberDao = new  SimpleHibernateTemplate<Subsciber, Long>(
				sessionFactory, Subsciber.class);
		mailDeployDao = new  SimpleHibernateTemplate<MailDeploy, Long>(
				sessionFactory, MailDeploy.class);
	}
	
	/**
	 * 通过公司名称得到公司ID
	 */
	public Long getSchoolId(String schoolName){
		String hql="select c.id from Company c where c.name=?";
		Long schoolId = companyDao.findLong(hql, schoolName);
		return schoolId;
	}

	/**
	 * 添加子公司
	 */
	public void addSubCompany(Company parentCompany, Company subCompany) {
		subCompany.setParent(parentCompany);
		subCompany.setCompanyId(getCompanyId());
		companyDao.save(subCompany);
	}

	/**
	 * 公司新建部门
	 */
	public void addDepartment(Company company, Department department) {
		department.setCompany(company);
		departmentDao.save(department);
	}

	/**
	 * 公司新建工作组
	 */
	public void addWorkGroup(Company company, Workgroup workGroup) {
		workGroup.setCompany(company);
		workGroupDao.save(workGroup);
	}

	@Transactional(readOnly = true)
	public Company getCompany(Long id) {
		return companyDao.get(id);
	}
	
	/**
	 * 判断给定系统所在的公司账户是否在有效期内 
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public boolean isCompanyValidDate(Long companyId, Long systemId){
		log.debug("*** isCompanyValidDate 开始");
		log.debug("*** Received parameter: [companyId=" + companyId + ", systemId=" + systemId + "]");
		
		String hql = "select si from SubscriberItem si join si.subsciber s join si.product p " +
				"where si.deleted=? and s.deleted=? and p.deleted=? and s.tenantId=? and p.systemId=?";
 		List<SubscriberItem> list =  subsciberDao.find(hql, false, false, false, companyId, systemId);
		Date current = new Date();
		boolean isValid = false;
		for(SubscriberItem si : list){
			if(current.after(si.getEffectDate()) && current.before(si.getInvalidDate())){
				isValid = true;
				break;
			}
		}
		
		log.debug("*** Return:" + isValid);
		log.debug("*** isCompanyValidDate 结束");
		return isValid;
	}
    
	/**
	 * 分页查询所有公司信息
	 */
	@Transactional(readOnly = true)
	public Page<Company> getAllCompanys(Page<Company> page) {
		return companyDao.findByCriteria(page, 
				Restrictions.eq("companyId", getCompanyId()),
				Restrictions.eq("deleted", false));
	}
    
	/**
	 * 查询所有公司信息
	 */
	@Transactional(readOnly = true)
	public List<Company> getAllCompanys() {
		return companyDao.findByCriteria(Restrictions.eq("companyId",getCompanyId()), 
				Restrictions.eq("deleted", false));
	}
	
	/**
	 * 获取所有的集团公司
	 */
	public List<Company> getCompanys(){
		List<Company> companys = companyDao.findByCriteria(Restrictions.eq("deleted", false),
				Restrictions.isNull("parent.id"));
		return companys;
	}
	
	

	public void saveCompany(Company company) {
		companyDao.save(company);
	}

	public void deleteCompany(Long id) {
		Company company = companyDao.get(id);
		company.setDeleted(true);
		companyDao.save(company);
	}
	/**
	 * 根据公司code获取公司ID
	 */
	public Long getCompanyIdByCode(String code){
		return companyDao.findUniqueByProperty("code", code).getId();
	}

	// 测试用DAO
	public SimpleHibernateTemplate<Company, Long> getCompanyDao() {
		return companyDao;
	}

	public SimpleHibernateTemplate<Department, Long> getDepartmentDao() {
		return departmentDao;
	}

	public SimpleHibernateTemplate<Workgroup, Long> getWorkGroupDao() {
		return workGroupDao;
	}

	public Long getCompanyId() {
		if (companyId == null) {
			return ContextUtils.getCompanyId();
		} else
			return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	/**
	 * 根据公司id获得邮件配置
	 * @return
	 */
	public MailDeploy getMailDeployByCompanyId() {
		return mailDeployDao.findUniqueByProperty("companyId", ContextUtils.getCompanyId());
	}

}
