package com.norteksoft.acs.service.organization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.enumeration.MailboxDeploy;
import com.norteksoft.acs.base.enumeration.SecretGrade;
import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.base.utils.log.LogUtilDao;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleDepartment;
import com.norteksoft.acs.entity.authorization.RoleUser;
import com.norteksoft.acs.entity.authorization.RoleWorkgroup;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.DepartmentUser;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.entity.organization.WorkgroupUser;
import com.norteksoft.acs.entity.sale.Subsciber;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;

@Service
@Transactional
public class UserManager {
	private Log log = LogFactory.getLog(UserManager.class);
	private SimpleHibernateTemplate<User, Long> userDao;
	private SimpleHibernateTemplate<UserInfo, Long> userInfoDao;
	private SimpleHibernateTemplate<RoleUser, Long> roleUserDao;
	private SimpleHibernateTemplate<DepartmentUser, Long> departmentToUserDao;
	private SimpleHibernateTemplate<WorkgroupUser, Long> workGroupToUserDao;
	private SimpleHibernateTemplate<Department, Long> departmentDao;
	private SimpleHibernateTemplate<Workgroup, Long> workGroupDao;
	private SimpleHibernateTemplate<Company, Long> companyDao;
	private SimpleHibernateTemplate<Subsciber, Long> subsciberDao;
	private Long companyId;
	private static String USERID = "user.id";
	private static String DELETED = "deleted";
	private static String COMPANYID = "companyId";
	private LogUtilDao logUtilDao;
	private static String ACS = "acs";

	@Autowired
	private AcsUtils acsUtils;
	@Autowired
	private UserInfoManager userInfoManager;

	public Long getSystemIdByCode(String code) {
	   return acsUtils.getSystemsByCode(code).getId();
	}
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		userDao = new SimpleHibernateTemplate<User, Long>(sessionFactory, User.class);
		roleUserDao = new SimpleHibernateTemplate<RoleUser, Long>(sessionFactory, RoleUser.class);
		workGroupToUserDao = new SimpleHibernateTemplate<WorkgroupUser, Long>(sessionFactory, WorkgroupUser.class);
		departmentToUserDao = new SimpleHibernateTemplate<DepartmentUser, Long>(sessionFactory, DepartmentUser.class);
		departmentDao = new SimpleHibernateTemplate<Department, Long>(sessionFactory, Department.class);
		workGroupDao = new SimpleHibernateTemplate<Workgroup, Long>(sessionFactory, Workgroup.class);
		userInfoDao = new SimpleHibernateTemplate<UserInfo, Long>(sessionFactory, UserInfo.class);
		companyDao=new SimpleHibernateTemplate<Company, Long>(sessionFactory, Company.class);
		subsciberDao=new SimpleHibernateTemplate<Subsciber, Long>(sessionFactory, Subsciber.class);
		logUtilDao = new LogUtilDao(sessionFactory);
	}
	@SuppressWarnings("unchecked")
	public Integer getAllowedNumbByCompany(Long companyId){
		List<Subsciber> subscibers = subsciberDao.find("select ss from Subsciber ss where ss.tenantId=? and deleted=?", companyId, false);
		Integer num = 0;
		for(Subsciber sb : subscibers){
			Date now = new Date();
			if(now.after(sb.getBeginDate()) && now.before(sb.getValidDate())){
				if(num < sb.getUseNumber()){
					num = sb.getUseNumber();
				}
			}
		}
		return num;
	}
	@SuppressWarnings("unchecked")
	public Department getManDepartment(String loginName){
		List<Department> depts = departmentDao.find("select d from Department d,User u where d.id=u.mainDepartmentId and u.companyId=? and u.loginName=?", getCompanyId(), loginName);
		if(depts.size() == 1){
			return depts.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Department getManDepartment(Long userId){
		List<Department> depts = departmentDao.find("select d from Department d,User u where d.id=u.mainDepartmentId and u.companyId=? and u.id=?", getCompanyId(), userId);
		if(depts.size() == 1){
			return depts.get(0);
		}
		return null;
	}
	
	public void saveUser(User user){
		userDao.save(user);
	}
	
	@Transactional(readOnly = true)
	public List<User> getAllUser(){
		return userDao.findAll();
	}
	
	public User getUserById(Long id){
		return userDao.get(id);
	}
	
	public User getUserByLoginName(String name){
		log.debug("*** getUserByLoginName 开始");
		log.debug("*** Received parameter: loginName:" + name);
		
		User user = (User) userDao.findUnique("select user from User user where user.deleted=false and user.loginName=?", name);
		
		log.debug("*** Return:" + user);
		log.debug("*** getUserByLoginName 结束");
		return user;
	}
	@SuppressWarnings("unchecked")
	public List<User> getUsersByLoginName(String name){
		log.debug("*** getUsersByLoginName 开始");
		log.debug("*** Received parameter: loginName:" + name);
		List<User> users = userDao.find("from User user where user.loginName=?", name);
		log.debug("*** getUserByLoginName 结束");
		return users;
	}
	public User getCompanyUserByLoginName(String name){
		log.debug("*** getUserByLoginName 开始");
		log.debug("*** Received parameter: loginName:" + name);
		
		User user = (User) userDao.findUnique("select user from User user where user.deleted=false and user.loginName=? and user.companyId=?", name,ContextUtils.getCompanyId());
		
		log.debug("*** Return:" + user);
		log.debug("*** getUserByLoginName 结束");
		return user;
	}
	@SuppressWarnings("unchecked")
	public Integer getUserNumberByCompanyId(long companyId){
		List<UserInfo> userList =userInfoDao.find("from UserInfo userInfo where userInfo.companyId=? and userInfo.dr=?  order by userInfo.user.weight ,userInfo.user.loginName desc", getCompanyId(),0);
		return new Integer(userList.size());
		
	}
	public User getDelUserByLoginName(String name){
		log.debug("*** getUserByLoginName 开始");
		log.debug("*** Received parameter: loginName:" + name);
		
		User user = (User) userDao.findUnique("select user from User user where user.deleted=true and user.loginName=?", name);
		
		log.debug("*** Return:" + user);
		log.debug("*** getUserByLoginName 结束");
		return user;
	}
	/**
	 * 设置用户为禁用
	*/
	public void closeUser(List<User> users){}

	/**
	* 设置用户为启用
	*/
	public void openUser(List<User> users){}

	/**
	* 设置用户为解锁
	*/
	public void unlock(List<User> users){}
	/**
	*查询已删除用户
	*/
	public List<User> queryDeletedUser(){
		
		return userDao.findByCriteria(Restrictions.eq("user.deleted", true));
	}
	
/**
     * 选中用户（己删除）添加部门
     */	
	public void addDepartmentToUserDel(Long userInfoId, List<Long> departmentIds,Integer isAdd) {

		User user = userInfoDao.get(userInfoId).getUser();
		List<Department> departments = departmentDao.findByCriteria(Restrictions.in("id", departmentIds));
		DepartmentUser departmentToUsers = null;
		StringBuilder departmentName = new StringBuilder();
        
       /*  添加部门*/
     
		if (isAdd == 0) {
			for (Department department : departments) {
				departmentToUsers = new DepartmentUser();
				departmentToUsers.setDepartment(department);
				departmentToUsers.setUser(user);
				departmentToUsers.setCompanyId(getCompanyId());
				departmentToUserDao.save(departmentToUsers);	
				departmentName.append(department.getName());
				departmentName.append(",");	
			}
			if(departmentName.indexOf(",")>=0){
				departmentName.deleteCharAt(departmentName.length()-1);
			}
			
			/* 移除部门*/
			
		} else {
			List<DepartmentUser> departmentToUser = departmentToUserDao.findByCriteria(Restrictions.in("department.id", departmentIds),Restrictions.eq(USERID, user.getId()));
			for (DepartmentUser departmentToUser2 : departmentToUser) {
				departmentToUserDao.delete(departmentToUser2);
			}
		}

	}
	/**
	 * 删除部门和用户关系
	 */
	public void deleteDepartmemtToUser(List<Long> departmentIds,Long userId){
			List<DepartmentUser> departmentToUser = departmentToUserDao.findByCriteria(Restrictions.in("department.id", departmentIds),Restrictions.eq(USERID, userId));
			for (DepartmentUser departmentToUser2 : departmentToUser) {
				departmentToUserDao.delete(departmentToUser2);
			}
		
	}
	/**
	 * 获得部门和用户关系
	 */
	public List<DepartmentUser> getDepartmemtToUser(Long departmentId,Long userId){
		List<DepartmentUser> departmentToUser = departmentToUserDao.findByCriteria(Restrictions.eq("department.id", departmentId),Restrictions.eq(USERID, userId));
		return departmentToUser;
	}
	
	/**
	 * 还原用户
	 */
	public void rebackUser(Long id){
		UserInfo userif = userInfoDao.get(id);
		userif.setDr(0);
        userInfoDao.save(userif);
	}

	public List<Long> getCheckedRoleIdsByUser(Long userId){
		List<RoleUser> roleUsers = roleUserDao.findByCriteria(
				Restrictions.eq(USERID, userId), 
				Restrictions.eq(DELETED, false),
				Restrictions.eq(COMPANYID, ContextUtils.getCompanyId()));
		List<Long> checkedRoleIds = new ArrayList<Long>();
		for(RoleUser ru : roleUsers){
			checkedRoleIds.add(ru.getRole().getId());
		}
		return checkedRoleIds;
	}
	
 /**
  * 选中用户添加角色
  */
	public void addRolesToUser(Long userId, List<Long> roleIds, Integer isAdd){
		User user = userDao.get(userId);
		if(isAdd == 0){
			RoleUser roleUser = null;
			Role role = null;
			for(Long id: roleIds){
				role = new Role();
				role.setId(id);
				roleUser = new RoleUser();
				roleUser.setRole(role);
				roleUser.setUser(user);
				roleUser.setCompanyId(ContextUtils.getCompanyId());
				roleUserDao.save(roleUser);
			}
		}else if(isAdd == 1){
			List<RoleUser> roleUsers = roleUserDao.findByCriteria(
					Restrictions.eq(USERID, userId),
					Restrictions.in("role.id", roleIds),
					Restrictions.eq(DELETED, false),
					Restrictions.eq(COMPANYID, ContextUtils.getCompanyId()));
			for(RoleUser ru : roleUsers){
				ru.setDeleted(true);
				roleUserDao.save(ru);
			}
		}
	}

	/**
	 * 查询用户要移除的部门
	 */
	 public Page<Department> userToRomoveDepartmentList(Page<Department> page,Department department,Long userId){
		 
		 String hql = "select department from Department department join department.departmentUsers ud where ud.user.userInfo.id=? and ud.companyId=? and department.deleted=? and ud.deleted=? ";
		  if(department!=null){
	    	  
			  String departmentName = department.getName();
	    	  if(departmentName!=null&&!"".equals(departmentName)){
	    		  StringBuilder hqL = new StringBuilder(hql);
	    		  hqL.append(" and department.name like ? ");
	    		  return departmentDao.find(page, hqL.toString(), userId,getCompanyId(),false,false,"%"+departmentName+"%");
                                              
	    	 }
	    	
	     }
	    
		  return departmentDao.find(page, hql, userId,getCompanyId(),false,false);
	  }
	 
		/**
		 * 查询用户要移除的工作组
		 */
		 public Page<Workgroup> userToRomoveWorkGroupList(Page<Workgroup> page,Workgroup workGroup,Long userId){
			 
			  String hql = "select workgroup from Workgroup workGroup join workGroup.workgroupUsers uw where uw.user.userInfo.id=? and uw.companyId=? and workGroup.deleted=? and uw.deleted=? ";
			  if(workGroup!=null){
		    	  
				  String workGroupName = workGroup.getName();
		    	  if(workGroupName!=null&&!"".equals(workGroupName)){
		    		  StringBuilder hqL = new StringBuilder(hql);
		    		  hqL.append(" and workGroup.name like ? ");
		    		  return workGroupDao.find(page, hqL.toString(), userId,getCompanyId(),false,false,"%"+workGroupName+"%");
	                                              
		    	 }
		    	
		     }
		    
			  return workGroupDao.find(page, hql, userId,getCompanyId(),false,false);
		  }
		 
	
	/**
     * 选中人员添加部门
     */
	public void addDepartmentToUser(Long userInfoId, List<Long> departmentIds,Integer isAdd) {

		User user = userInfoDao.get(userInfoId).getUser();
		StringBuilder departmentName = new StringBuilder();
	
        /**
         * 添加部门
         */
		if (isAdd == 0) {
			DepartmentUser departmentToUser;
			Department department = null;
			for (Long departmentId : departmentIds) {
				departmentToUser = new DepartmentUser();
				department = departmentDao.get(departmentId);
				departmentToUser.setUser(user);
				departmentToUser.setDepartment(department);
				departmentToUser.setCompanyId(getCompanyId());
				departmentToUserDao.save(departmentToUser);
				departmentName.append(departmentToUser.getDepartment().getName());
				departmentName.append(",");	
			}
		
		departmentName.deleteCharAt(departmentName.length()-1);
		}
			/**
			 *移除部门
			 */
		 if(isAdd==1) {
			List<DepartmentUser> list_d = departmentToUserDao.findByCriteria(Restrictions.in("department.id", departmentIds),
					                                                           Restrictions.eq(USERID, user.getId()),
					                                                           Restrictions.eq(COMPANYID, getCompanyId()),
					                                                           Restrictions.eq(DELETED, false)
			                                                                   );
			for (DepartmentUser departmentToUser : list_d) {
				departmentToUser.setDeleted(true);
				departmentToUserDao.save(departmentToUser);
				departmentName.append(departmentToUser.getDepartment().getName());
				departmentName.append(",");				
			}
			departmentName.deleteCharAt(departmentName.length()-1);
		}

	}	
	
	/**
     * 选中人员添加工作组
     */
	public void addWorkGroupToUser(Long userInfoId, List<Long> workGroupIds,Integer isAdd) {
		User user = userInfoDao.get(userInfoId).getUser();
		StringBuilder workGroupName = new StringBuilder();
        //添加工作组
		if (isAdd == 0) {
			WorkgroupUser workGroupToUser;
			Workgroup workGroup = null;
			for (Long workGroupId : workGroupIds) {
				workGroupToUser = new WorkgroupUser();
				workGroup = workGroupDao.get(workGroupId);
				workGroupToUser.setUser(user);
				workGroupToUser.setWorkgroup(workGroup);
				workGroupToUser.setCompanyId(getCompanyId());
				workGroupToUserDao.save(workGroupToUser);
				workGroupName.append(workGroupToUser.getWorkgroup().getName());
				workGroupName.append(",");	
			}
			workGroupName.deleteCharAt(workGroupName.length()-1);
		}
		 //移除工作组
		 if(isAdd==1) {
			List<WorkgroupUser> list_d = workGroupToUserDao.findByCriteria(Restrictions.in("workgroup.id", workGroupIds),
					                                                           Restrictions.eq(USERID, user.getId()),
					                                                           Restrictions.eq(COMPANYID, getCompanyId()),
					                                                           Restrictions.eq(DELETED, false)
			                                                                   );
			for (WorkgroupUser workGroupToUser : list_d) {
				workGroupToUser.setDeleted(true);
				workGroupToUserDao.save(workGroupToUser);
				workGroupName.append(workGroupToUser.getUser().getLoginName());
				workGroupName.append(",");				
			}
			workGroupName.deleteCharAt(workGroupName.length()-1);
		}

	}	
	/**
	 *  查询人员己分配的部门
	 */
    public Page<Department> getDepartmentList(Page<Department> page){
    	return departmentDao.find(page, "from Department d where d.company.id=? and d.deleted=?", ContextUtils.getCompanyId(), false);
	}
    
    public List<Long> getCheckedDepartmentIds(Long userInfoId){
    	List<Long> departmentIds = new ArrayList<Long>();
    	User user = userInfoDao.get(userInfoId).getUser();
    	List<DepartmentUser> d_u= departmentToUserDao.findByCriteria(Restrictions.eq(USERID, user.getId()),Restrictions.eq(COMPANYID, getCompanyId())
                ,Restrictions.eq(DELETED,false));
    	for (DepartmentUser departmentToUser : d_u) {
    		departmentIds.add(departmentToUser.getDepartment().getId());
		}
		return departmentIds;
	}
    public List<Department> getDepartmentsByUser(Long userId){
    	List<Department> departments = new ArrayList<Department>();
    	User user = userDao.get(userId);
    	List<DepartmentUser> d_u= departmentToUserDao.findByCriteria(Restrictions.eq(USERID, user.getId()),Restrictions.eq(COMPANYID, getCompanyId())
                ,Restrictions.eq(DELETED,false));
    	for (DepartmentUser departmentToUser : d_u) {
    		departments.add(departmentToUser.getDepartment());
		}
		return departments;
	}
	/**
	 *  查询查询人员己分配的工作组
	 */
    public Page<Workgroup> getWorkGroupList(Page<Workgroup> page){
		return workGroupDao.findByCriteria(page, Restrictions.eq("company.id", ContextUtils.getCompanyId()) ,Restrictions.eq(DELETED,false));
	}
    public List<Long> getCheckedWorkGroupIds(Long userInfoId){
    	List<Long> workGroupIds = new ArrayList<Long>();
    	User user = userInfoDao.get(userInfoId).getUser();
    	List<WorkgroupUser> w_u= workGroupToUserDao.findByCriteria(Restrictions.eq(USERID, user.getId()),
    			                                                     Restrictions.eq(DELETED,false),
    			                                                     Restrictions.eq(COMPANYID,getCompanyId())); 
    	for (WorkgroupUser workGroupToUser : w_u) {
    		workGroupIds.add(workGroupToUser.getWorkgroup().getId());
		}
		return workGroupIds;
    }
    
	public SimpleHibernateTemplate<UserInfo, Long> getUserInfoDao() {
		return userInfoDao;
	}

	public SimpleHibernateTemplate<User, Long> getUserDao() {
		return userDao;
	}

	public SimpleHibernateTemplate<DepartmentUser, Long> getDepartmentToUserDao() {
		return departmentToUserDao;
	}

	public SimpleHibernateTemplate<WorkgroupUser, Long> getWorkGroupToUserDao() {
		return workGroupToUserDao;
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
	 * 获取所有公司的用户
	 * @return List<User>
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<User> getAllUsers(){
		String hql = "from User ui where ui.deleted=0 ";
		return userDao.find(hql);
	}
	
	@SuppressWarnings("unchecked")
	public List<User> getUsersByDeptId(Long deptId){
		return userDao.find("select u from User u join u.departmentUsers du where u.deleted=? and du.deleted=? and du.department.id=? order by u.weight desc", 
				false, false, deptId);
	}
	
 	@SuppressWarnings("unchecked")
	public boolean getLoginName(String loginName,Long deptId){
 		List<User> users=userDao.find("select u from User u join u.departmentUsers du where u.deleted=? and du.deleted=? and du.department.id=? order by u.weight desc", 
				false, false, deptId);
 		for(User user:users){
 			String ln=user.getLoginName();
 			if(loginName.equals(ln)){
 				return true;
 			}else{
 				return false;
 			}
 		}
 		return false;
 	}
 	@SuppressWarnings("unchecked")
	public String importUser(File file){
 		FileInputStream fis=null;
 		InputStreamReader fr=null;
 		BufferedReader br=null;
 		Integer currentUserNumber=null;
 		Integer companyUserLimit=getAllowedNumbByCompany(getCompanyId());
 		Integer importCount = 0;
 		try{
 			fis=new FileInputStream(file);
 			fr=new InputStreamReader(fis,"utf-8");
 			br=new BufferedReader(fr);
 			String content=null;
 			content=br.readLine();//读出文件第一行
 			currentUserNumber = getUserNumberByCompanyId(getCompanyId());
 			while((content=br.readLine())!=null){
 				String[] values=content.split(",");
 				if(StringUtils.isNotEmpty(content)){//部门不为空
					//####部门
 					if(StringUtils.isNotEmpty(values[0])){
	 					String[] depts=values[0].split("/");
	 					for(int i=0;i<depts.length;i++){
	 						Department department=null;
	 						if(isDepartmentExist(depts[i],getCompanyId())){//部门存在
	 							department=getDepartmentByName(depts[i]);
	 						}else{
								department=new Department();
	 						}
	 						Company company = companyDao.get(getCompanyId());
	 						department.setCompany(company);
	 						department.setCode(depts[i]);
	 						department.setName(depts[i]);
	 						if(i>0){
	 							Department parentDept=getDepartmentByName(depts[i-1]);
	 							department.setParent(parentDept);
	 						}
	 						//如果是最后一个部门,则添加人。如：办公室/后勤/车队,周宏1,zhouhong1,68963158,男,zhouhong@bky.com,50,10,如果是“车队”则添加人员
	 						if(depts.length-1==i){
	 							//#####用户
	 							if(StringUtils.isNotEmpty(values[2])){//用户登录名不为空,添加用户
	 								if(currentUserNumber+importCount+1>companyUserLimit)return "已导入"+importCount+"条,超出系统允许注册人数";
	 								departmentDao.save(department);
	 								UserInfo userInfo=importUserSaveUser(values,department);
	 								//新建用户时默认给用户portal普通用户权限
	 								userInfoManager.giveNewUserPortalCommonRole(userInfo.getUser());
	 								//####部门人员
	 								DepartmentUser departmentToUser;
	 								List<DepartmentUser> dtu=departmentToUserDao.find("from DepartmentUser d where d.user.id=? and d.department.id=?", userInfo.getUser().getId(),department.getId());
	 								if(dtu.size()==0){
	 									departmentToUser = new DepartmentUser();
	 									userInfo = userInfoDao.get(userInfo.getId());
	 									departmentToUser.setUser(userInfo.getUser());
	 									departmentToUser.setDepartment(department);
	 									departmentToUser.setCompanyId(getCompanyId());
	 									departmentToUserDao.save(departmentToUser);
	 									//记录公司用户数量
		 								importCount++;
	 								}else{
	 									DepartmentUser d=dtu.get(0);
	 									d.setDeleted(false);
	 									departmentToUserDao.save(d);
	 								}
	 							}
	 						}
	 					}
	 				}else{//部门为空，即无部门人员导入
	 					if(StringUtils.isNotEmpty(values[2])){
	 						if(currentUserNumber+importCount+1>companyUserLimit)return "已导入"+importCount+"条,超出系统允许注册人数";
	 						importUserSaveUser(values,null);
	 						User user = getUserByLoginName(StringUtils.trim(values[2]));
	 						//新建用户时默认给用户portal普通用户权限
	 						if(user!=null)userInfoManager.giveNewUserPortalCommonRole(user);
	 						if(user==null){
	 						//记录公司用户数量
	 						importCount++;
	 						}
	 					}
	 				}
 				}
 			}
 			
 		}catch(IOException exception){
 			log.debug(exception.getStackTrace());
 		}finally{
 			try{
	 			if(br!=null)br.close();
	 			if(fr!=null)fr.close();
	 			if(fis!=null)fis.close();
 			}catch(IOException ep){
 				log.debug(ep.getStackTrace());
 			}
 		}
 		return null;
 	}
 	@SuppressWarnings("unchecked")
	public Department getDepartmentByName(String name){
		if(name == null) throw new RuntimeException("没有给定查询部门时的查询条件：部门名称");
		List<Department> depts = departmentDao.find("from Department d where d.company.id=? and d.name=? and d.deleted=?", getCompanyId(), name, false);
		if(depts.size() == 1){
			return depts.get(0);
		}
		return null;
	}
 	@SuppressWarnings("unchecked")
	public Date getUserTs(Long companyId){
		List<Date> dates = userDao.find("select max(l.loginTime) from LoginLog l where l.companyId=?", companyId);
		if(dates.isEmpty()){
			dates = userDao.find("select max(u.ts) from User u where u.companyId=?", companyId);
		}
		if(!dates.isEmpty()){
			return dates.get(0);
		}
		return new Date();
	}
	
 	public UserInfo importUserSaveUser(String[] values,Department dept){
 		//#####用户
		User user = getCompanyUserByLoginName(values[2]);
		UserInfo userInfo =null;
		List<UserInfo> userInfos=new ArrayList<UserInfo>();
		if(user==null){
			user=new User();
			userInfo = new UserInfo();
		}else{
			userInfo=user.getUserInfo();
		}
		if(dept!=null)user.setMainDepartmentId(dept.getId());
		user.setName(StringUtils.trim(values[1]));
		user.setPassword("123");
		user.setLoginName(StringUtils.trim(values[2]));
		user.setCompanyId(getCompanyId());
		
		userInfo.setCompanyId(getCompanyId());
		userInfo.setPasswordUpdatedTime(getNewDate());
		for(int i=4;i<=values.length;i++){
			switch (i) {
			case 4: if(StringUtils.isNotEmpty(values[3]))userInfo.setTelephone(StringUtils.trim(values[3])); break;
			case 5: user.setSex("男".equals(values[4])); break;
			case 6: if(StringUtils.isNotEmpty(values[5]))user.setEmail(StringUtils.trim(values[5])); break;
			case 7: if(StringUtils.isNotEmpty(values[6]))user.setWeight(Integer.parseInt(StringUtils.trim(values[6]))); break;
			case 8: if(StringUtils.isNotEmpty(values[7]))user.setMailSize(Float.parseFloat(StringUtils.trim(values[7]))); break;
			case 9: if(StringUtils.isNotEmpty(values[8])){
				if("一般".equals(StringUtils.trim(values[8]))){
					user.setSecretGrade(SecretGrade.COMMON);
				}else if("重要".equals(StringUtils.trim(values[8]))){
					user.setSecretGrade(SecretGrade.MAJOR);
				}else if("核心".equals(StringUtils.trim(values[8]))){
					user.setSecretGrade(SecretGrade.CENTRE);
				}
			}
			break;
			case 10: if(StringUtils.isNotEmpty(values[9])){
				if("内网".equals(StringUtils.trim(values[9]))){
					user.setMailboxDeploy(MailboxDeploy.INSIDE);
				}else if("外网".equals(StringUtils.trim(values[9]))){
					user.setMailboxDeploy(MailboxDeploy.EXTERIOR);
				}
			}
			break;
			}
		}
		userInfos.add(userInfo);
		user.setUserInfos(userInfos);
		userDao.save(user);
		userInfo.setUser(user);
		userInfoDao.save(userInfo);
		return userInfo;
 	}
 	
 	/**
	 * 验证部门是否存在
	 * @param name
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public  boolean isDepartmentExist(String name, Long companyId){
		List<Department> depts = departmentDao.find("from Department d where d.company.id=? and d.name=? and d.deleted=?", companyId, name, false);
		if(depts.size() >= 1){
			return true;
		}
		return false;
	}
	
	private Date newDate;
	private Date getNewDate() {
		if(newDate==null){
			Calendar cal=Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DATE);
			cal.clear();
			cal.set(year, month, day);
			newDate = cal.getTime();
		}
		return newDate;
	}
	/**
	 * 解锁用户
	 */
	public String unlockUser(String userIds){
		StringBuilder result=new StringBuilder();
		int successNum=0;
		int failNum=0;
		if(StringUtils.isNotEmpty(userIds)){
			String[] ids=userIds.split(",");
			for(String id:ids){
				if(StringUtils.isNotEmpty(id)){
					Long userId=Long.parseLong(id);
					User user=getUserById(userId);
					if(user.getAccountLocked()){//true为锁定，false为未锁
						user.setAccountLocked(false);
						saveUser(user);
						successNum++;
					}
				}
			}
			failNum=ids.length-successNum;
		}
		result.append("解锁").append(successNum).append("个;")
		.append(failNum).append("个").append("不需解锁");
		return result.toString();
	}
	
	/**
	 * 批量更换用户的主职部门
	 */
	public void batchChangeMainDepartment(String ids, Long departmentId) {
		String[] idArr = ids.split(",");
		DepartmentUser departmentToUsers ;
		Department department = departmentDao.findUniqueByProperty("id", departmentId);
		for(int i=0;i<idArr.length;i++){
			User user= this.getUserById(Long.valueOf(idArr[i]));
			//更换的正职部门与原正职部门相同就不用重新设置正职部门
			if(departmentId.equals(user.getMainDepartmentId()))continue;
			
			//建立新主职部门和用户的关系
			List<DepartmentUser> departmentToUser = departmentToUserDao
			.findByCriteria(Restrictions.eq("department.id", departmentId),Restrictions.eq(USERID, user.getId()));
			if(departmentToUser.isEmpty()){
				departmentToUsers = new DepartmentUser();
				departmentToUsers.setDepartment(department);
				departmentToUsers.setUser(user);
				departmentToUsers.setCompanyId(getCompanyId());
				departmentToUserDao.save(departmentToUsers);	
			}
			
			//删除原来主职部门和用户的关系
			Long oldMainDepartmentId = user.getMainDepartmentId();
			if(oldMainDepartmentId!=null){
				List<DepartmentUser> oldDepartmentToUser = departmentToUserDao
				.findByCriteria(Restrictions.eq("department.id", oldMainDepartmentId),Restrictions.eq(USERID, user.getId()));
				if(!oldDepartmentToUser.isEmpty()){
					departmentToUserDao.delete(oldDepartmentToUser.get(0));
				}
			}
			
			//重新设置主职部门
			user.setMainDepartmentId(departmentId);
			userDao.save(user);
		}
		
	}
	@SuppressWarnings("unchecked")
	public List<User> getUsersByLoginNames(Set<String> loginNames){
		List<User> users = new ArrayList<User>();
		if(loginNames.size()>0){
			Object[] objs =new Object[loginNames.size()+1];
			StringBuilder hql = new StringBuilder("from User u where u.deleted=? and (");
			objs[0] = false;
			int i = 0;
			for(String loginName:loginNames){
				i++;
				if(i==loginNames.size()){
					hql.append(" u.loginName=?)");
				}else{
					hql.append(" u.loginName=? or ");
				}
				objs[i] = loginName;
			}
			users = userDao.find(hql.toString(), objs);
		}
		return users;
	}
	
	public String getRolesExcludeTrustedRole(User user){
		if(user == null) return "";
		Set<com.norteksoft.acs.entity.authorization.Role> roles = new HashSet<com.norteksoft.acs.entity.authorization.Role>();
		// 用户具有的角色
		Set<RoleUser> roleUsers = user.getRoleUsers();
		for(RoleUser ru : roleUsers){
			if(ru.isDeleted()) continue;
			if(ru.getConsigner()!= null) continue;
			com.norteksoft.acs.entity.authorization.Role role = ru.getRole();
			if(!role.isDeleted()) roles.add(role);
		}
		// 用户具有的部门拥有的角色
		Set<DepartmentUser> departmentUsers =  user.getDepartmentUsers();
		for(DepartmentUser du : departmentUsers){
			if(du.isDeleted() || du.getDepartment().isDeleted()) continue;
			for(RoleDepartment rd : du.getDepartment().getRoleDepartments()){
				if(!rd.isDeleted() && !rd.getRole().isDeleted()) roles.add(rd.getRole());
			}
		}
		// 用户具有的工作组拥有的角色
		Set<WorkgroupUser> workgroupUsers = user.getWorkgroupUsers();
		for(WorkgroupUser wu : workgroupUsers){
			if(wu.isDeleted() || wu.getWorkgroup().isDeleted()) continue;
			for(RoleWorkgroup rw : wu.getWorkgroup().getRoleWorkgroups()){
				if(!rw.isDeleted() && !rw.getRole().isDeleted()) roles.add(rw.getRole());
			}
		}
		List< com.norteksoft.acs.entity.authorization.Role> roleList = new ArrayList<com.norteksoft.acs.entity.authorization.Role>();
		roleList.addAll(roles);
		//角色按权重排序
		sortRole(roleList);
		// 生成字符串形式
		StringBuilder roleStrings = new StringBuilder();
		for(com.norteksoft.acs.entity.authorization.Role role : roleList){
			roleStrings.append(role.getCode()).append(",");
		}
		// 去掉最后一个逗号
		if(roleStrings.lastIndexOf(",") != -1 && roleStrings.lastIndexOf(",") == roleStrings.length()-1){
			roleStrings.replace(roleStrings.length()-1, roleStrings.length(), "");
		}
		return roleStrings.toString();
	}
	
	//角色按权重排序
	private void sortRole(List<Role> roles){
		Collections.sort(roles, new Comparator<Role>() {
			public int compare(Role role1, Role role2) {
				if(role1.getWeight()==null&&role2.getWeight()!=null)return 1;
				if(role1.getWeight()!=null&&role2.getWeight()==null)return 0;
				if(role1.getWeight()==null&&role2.getWeight()==null)return 0;
				if(role1.getWeight()<role2.getWeight()){
					return 1;
				}
				return 0;
			}
		});
	}
	@SuppressWarnings("unchecked")
	public List<Department> getDepartmentsByUserId(Long userId){
		if(userId == null) throw new RuntimeException("没有给定查询用户所在部门列表的查询条件：用户ID");
		StringBuilder hql = new StringBuilder();
		hql.append("select d from Department d join d.departmentUsers du join du.user u ");
		hql.append("where u.companyId=? and u.id=? and u.deleted=? and du.deleted=? and d.deleted=? order by d.weight desc");
		return departmentDao.find(hql.toString(), getCompanyId(), userId, false, false, false);
	}
	@SuppressWarnings("unchecked")
	public List<Department> getDepartments(String loginName){
		if(StringUtils.isEmpty(loginName)) throw new RuntimeException("没有给定用户所在部门列表的查询条件：用户登录名");
		StringBuilder hql = new StringBuilder();
		hql.append("select d from Department d join d.departmentUsers du join du.user u ");
		hql.append("where u.companyId=? and u.loginName =? and u.deleted=? and du.deleted=? and d.deleted=?");
		return departmentDao.find(hql.toString(), getCompanyId(), loginName, false, false, false);
	}
	/**
	 * 根据公司ID查询该公司所有的工作组
	 * 
	 * @param companyId 公司ID
	 * @return List<WorkGroup>
	 */
	@Transactional(readOnly = true)
	public List<Workgroup> getWorkgroups() {
		List<com.norteksoft.acs.entity.organization.Workgroup> workGroupList = workGroupDao.findByCriteria(
				Restrictions.eq("company.id", getCompanyId()), Restrictions.eq(
						DELETED, false));
		return workGroupList;
	}
	@SuppressWarnings("unchecked")
	public Workgroup getWorkgroupByName(String name){
		if(name == null) throw new RuntimeException("没有给定查询工作组时的查询条件：工作组名称");
		List<Workgroup> workGroups = workGroupDao.find("from Workgroup wg where wg.company.id=? and wg.name=? ", getCompanyId(), name);
		if(workGroups.size() == 1){
			return workGroups.get(0);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<Workgroup> getWorkgroupsByUser(String loginName){
		if(StringUtils.isEmpty(loginName)) throw new RuntimeException("没有给出查询用户所在工作组列表的查询条件：用户登录名");
		StringBuilder hql = new StringBuilder();
		hql.append("select wg from Workgroup wg join wg.workgroupUsers wgu join wgu.user u ");
		hql.append("where u.companyId=? and u.loginName=? and u.deleted=? and wgu.deleted=? and wg.deleted=?");
		return workGroupDao.find(hql.toString(), getCompanyId(), loginName, false, false, false);
	}
}