package com.norteksoft.acs.service.organization;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.base.utils.log.LogUtilDao;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleWorkgroup;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.DepartmentUser;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.entity.organization.WorkgroupUser;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;

@SuppressWarnings("deprecation")
@Service
@Transactional
public class WorkGroupManager {
    
	private SimpleHibernateTemplate<Workgroup, Long> workGroupDao;
	private SimpleHibernateTemplate<User,Long> userDao;
	private SimpleHibernateTemplate<UserInfo,Long> userInfoDao;
	private SimpleHibernateTemplate<WorkgroupUser,Long> workGroupToUserDao;
	private SimpleHibernateTemplate<Role,Long> roleDao;
	private SimpleHibernateTemplate<RoleWorkgroup,Long> role_wDao;
	private LogUtilDao logUtilDao;
	private static String hql = "from Workgroup w where w.company.id=? and w.deleted=? order by w.weight desc";
	private static String DELETED = "deleted";
	private static String COMPANYID = "companyId";
	private static String WORKGROUPID = "workgroup.id";
	private static String ACS = "acs";
	private static final String HQL = "from UserInfo u where u.user.id = ? ";

	@Autowired
	private AcsUtils acsUtils;

	public Long getSystemIdByCode(String code) {
	   return acsUtils.getSystemsByCode(code).getId();
	}
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		workGroupDao = new SimpleHibernateTemplate<Workgroup, Long>(sessionFactory, Workgroup.class);
		userDao = new SimpleHibernateTemplate<User, Long>(sessionFactory, User.class);
		userInfoDao = new SimpleHibernateTemplate<UserInfo, Long>(sessionFactory, UserInfo.class);
		workGroupToUserDao = new SimpleHibernateTemplate<WorkgroupUser, Long>(sessionFactory, WorkgroupUser.class);
		roleDao = new SimpleHibernateTemplate<Role, Long>(sessionFactory, Role.class);
		role_wDao = new SimpleHibernateTemplate<RoleWorkgroup, Long>(sessionFactory, RoleWorkgroup.class);
		logUtilDao = new LogUtilDao(sessionFactory);
	}   

	/**
	 * 验证工作组名称唯一性
	 */
	public boolean checkWorkName(String workGroupName,Long id){
		String hql = "FROM Workgroup d WHERE d.name=? AND d.id<>? AND d.company.id=? AND d.deleted=0";
		Object obj = workGroupDao.findUnique(hql, workGroupName,id,ContextUtils.getCompanyId());
		if(obj == null){
			return false;
		}
		return true;
	}
	
	/**
	 * 验证工作组名称唯一性
	 */
	public boolean checkWorkName(String workGroupName){
		String hql = "FROM Workgroup d WHERE d.name=? AND d.company.id=? AND d.deleted=0";
		Object obj = workGroupDao.findUnique(hql, workGroupName,ContextUtils.getCompanyId());
		if(obj == null){
			return false;
		}
		return true;
	}
	
	/**
	 * 验证工作组编号唯一性
	 * liudongxia
	 */
	public boolean checkWorkCode(String workGroupCode,Long id){
		String hql = "FROM Workgroup d WHERE d.code=? AND d.id<>? AND d.company.id=? AND d.deleted=0";
		Object obj = workGroupDao.findUnique(hql, workGroupCode,id,ContextUtils.getCompanyId());
		if(obj == null){
			return false;
		}
		return true;
	}
	
	/**
	 * 验证工作组编号唯一性
	 * liudongxia
	 */
	public boolean checkWorkCode(String workGroupCode){
		String hql = "FROM Workgroup d WHERE d.code=? AND d.company.id=? AND d.deleted=0";
		Object obj = workGroupDao.findUnique(hql, workGroupCode,ContextUtils.getCompanyId());
		if(obj == null){
			return false;
		}
		return true;
	}
	
	public LogUtilDao getLogUtilDao() {
		return logUtilDao;
	}

	public void setLogUtilDao(LogUtilDao logUtilDao) {
		this.logUtilDao = logUtilDao;
	}


	private Long companyId;
	
	public Long getCompanyId() {
		if(companyId == null){
			return ContextUtils.getCompanyId();
		}else 
			return companyId;
	}
    
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	
	/**
  	 * 查询所有工作组信息
  	 */
	@Transactional(readOnly = true)
	public List<Workgroup> getAllWorkGroup(){
		return workGroupDao.findByCriteria(Restrictions.eq("company.id", getCompanyId()),Restrictions.eq(DELETED,false));
	}
	
	/**
	 * 获取单条工作组信息
	 */
	@Transactional(readOnly = true)
	public Workgroup getWorkGroup(Long id) {
		return workGroupDao.get(id);
	}
	
	  /**
	   * 工作组添加用户时查询所有用户
	   */
	public Page<User> workGroupToUsers(Page<User> userPage, Long workgroupId){
		userDao.searchPageByHql(userPage, "select ui.user from UserInfo ui where ui.companyId=? and ui.deleted=false and (ui.user.id not in " +
				"(select u.id from User u inner join u.workgroupUsers wu where u.deleted=false and wu.deleted=false and wu.workgroup.id=?))", 
				getCompanyId(), workgroupId);
	    return userPage;
	}
	   
    /**
     * 公司添加工作组(保存公司与工作组的关系)
     */
	public List<Workgroup> saveWorkGroup(List<Long> workGroupIds){
		return workGroupDao.findByCriteria(Restrictions.in("id", workGroupIds));
	}
	
	public Page<User> workGroupToUsers(Page<User> userPage){
	
	return userDao.findAll(userPage);
    }
   
	/**
	 * 
	 * 查询工作组己添加的用户
	 */
   public List<Long> getUserIds(Long workGroupId) throws Exception {
	List<Long> userIds = new ArrayList<Long>();
	List<WorkgroupUser> workGroupToUsers = workGroupToUserDao.findByCriteria(Restrictions.eq(WORKGROUPID, workGroupId),
		                                        	                           Restrictions.eq("companyId", ContextUtils.getCompanyId()),
                                                                               Restrictions.eq(DELETED,false)
	                                                                            );
	for (WorkgroupUser workGroupToUser : workGroupToUsers) {
		userIds.add(workGroupToUser.getUser().getUserInfo().getId());
	}
	return userIds;
  }
          
	/**
	 * 查询工作组要移除的用户
	 */
	 public Page<User> workGroupToRomoveUserList(Page<User> page,User user,Long workGroupId){
		 
		  String hql = "select userInfo.user from UserInfo userInfo join userInfo.user.workgroupUsers wu where wu.workgroup.id=? and wu.companyId=? and userInfo.deleted=? and wu.deleted=?";
		  if(user!=null){ 
	      	  
			  String userName = user.getLoginName();
	    	  if(userName!=null&&!"".equals(userName)){
	    		  StringBuilder hqL = new StringBuilder(hql);
	    		  hqL.append(" and userInfo.user.loginName like ? ");
	    		  return userDao.searchPageByHql(page, hqL.toString(), workGroupId,getCompanyId(),false,false,"%"+userName+"%");
                                             
	    	 }
	    	
	     }
	    
		  return userDao.searchPageByHql(page, hql, workGroupId,getCompanyId(),false,false);
	  }

	  /**
	     * 工作组添加人员
	     */
		public void workGroupToUser(Long workGroupId, List<Long> userIds,Integer isAdd) {
			if(userIds == null){
				return;
			}
			
			//根据工作组ID查询工作组信息
			Workgroup workGroup = workGroupDao.get(workGroupId);
			StringBuilder userName = new StringBuilder();
	        /**
	         * 添加人员
	         */
			if (isAdd == 0) {
				WorkgroupUser workGroupToUser;
				 //User user = null;
				 UserInfo userInfo =null; 
				for (Long userId  : userIds) {
					workGroupToUser = new WorkgroupUser();
					userInfo = userInfoDao.get(userId);
					workGroupToUser.setUser(userInfo.getUser());
					workGroupToUser.setWorkgroup(workGroup);
					workGroupToUser.setCompanyId(getCompanyId());
					workGroupToUserDao.save(workGroupToUser);
					userName.append(workGroupToUser.getUser().getLoginName());
					userName.append(",");
					 
				}
				userName.deleteCharAt(userName.length()-1);
			}	
				/**
				 *移除人员
				 */
			if(isAdd==1){
				 List<UserInfo> uif = userInfoDao.findByCriteria(Restrictions.in("id", userIds));
				 List<Long> ids = new ArrayList<Long>();
				 for (UserInfo userInfo : uif) {
					 ids.add(userInfo.getUser().getId());
				}
				List<WorkgroupUser> list = workGroupToUserDao.findByCriteria(Restrictions.in("user.id", ids),
						                                                         Restrictions.eq(WORKGROUPID, workGroupId),
						                                                         Restrictions.eq(COMPANYID, getCompanyId()),
						                                                         Restrictions.eq(DELETED, false)
				                                                                 );
				for (WorkgroupUser workGroupToUser : list) {
					workGroupToUser.setDeleted(true);
					workGroupToUserDao.save(workGroupToUser);
					userName.append(workGroupToUser.getUser().getLoginName());
					userName.append(",");
				}
				userName.deleteCharAt(userName.length()-1);
			}

		}	
	/**
	 * 分页查询所有工作组信息
	 */
	@Transactional(readOnly = true)
	public Page<Workgroup> getAllWorkGroup(Page<Workgroup> page) {
		workGroupDao.findPage(page, hql, getCompanyId(), false);
		return page;

	}
	
	/**
	  * 保存工作组信息
	  */	
	public void saveWorkGroup(Workgroup workGroup){
	    
		workGroupDao.save(workGroup);	
	}
	
	/**
	 * 删除工作组信息
	 */
	public void deleteWorkGroup(Long id) {
		Workgroup workGroup = workGroupDao.get(id);
		workGroup.setDeleted(true);
		workGroupDao.save(workGroup);
	}		
	
	/**
	 * 按条件检索工作组
	 */
	@Transactional(readOnly = true)
	public Page<Workgroup> getSearchWorkGroup(Page<Workgroup> page,Workgroup workGroup,boolean deleted) {
		StringBuilder workGroupHql = new StringBuilder(hql);
		
		 if (workGroup != null) {
			
			String workGroupCode = workGroup.getCode();
			String workGroupName = workGroup.getName();
			
			if (!"".equals(workGroupCode) && !"".equals(workGroupName)) {
				workGroupHql.append(" and w.code like ?");
				workGroupHql.append(" and w.name like ?");
				return workGroupDao.find(page, workGroupHql.toString(),
						                getCompanyId(),
						                false,
						                "%" + workGroupCode+ "%", "%" + workGroupName + "%");
			}
			
			if (!"".equals(workGroupCode)) {
				workGroupHql.append(" and w.code like ?");
				return workGroupDao.find(page, workGroupHql.toString(),
						                getCompanyId(),
						                false,
						                "%" + workGroupCode+ "%");
			}
			
			if (!"".equals(workGroupName)) {
				workGroupHql.append(" and w.name like ?");
				return workGroupDao.find(page, workGroupHql.toString(),
						                getCompanyId(), 
						                false,
						                "%" + workGroupName + "%");
			}
		}
		return workGroupDao.find(page, hql, getCompanyId(),false);
	}

	 /**
	  * 工作组添加角色
	  */
	  public Page<Role> workGroupToRoleList(Page<Role> page,Role entity){
		  if(entity!=null){
	    	  String roleName = entity.getName();
	    	 
	    	 if(roleName!=null&&!"".equals(roleName)){
	    		 return roleDao.findByCriteria(page, 
                                               Restrictions.like("name", "%" + roleName + "%"),
                                               Restrictions.eq(DELETED, false));
	    	 }
	    	
	     }
		  return roleDao.findByCriteria(page ,Restrictions.eq(DELETED, false));
				 
	  }
	  /**
	  * 工作组移除角色
	  */
	  public Page<Role> workGroupRomoveRoleList(Page<Role> page,Role entity,Long workId){
		  String hql = "select role from Role role join role.role_WorkGroup r_w where r_w.workGroup.id=? and r_w.companyId=? and role.deleted=? and r_w.deleted=? ";
		  if(entity!=null){
	    	 
			  String roleName = entity.getName();
	    	  if(roleName!=null&&!"".equals(roleName)){
	    		  StringBuilder hqL = new StringBuilder(hql);
	    		  hqL.append(" and role.name like ?");
	    		  return roleDao.find(page,hqL.toString(),workId,getCompanyId(),false,false ,"%" + roleName + "%");
                                            
	    	 }
	    	
	     }
		  return roleDao.find(page ,hql,workId,getCompanyId(),false,false);
				                     
	  }
	  
	  /**
	   * 工作组移除角色
	   * @param workGroupId
	   * @return
	   */
	  public List<BusinessSystem> workGroupRomoveRoleList(Long workId){
		/*//租户购买的订单的有效期大于当前日期
			String hql = "select p.systemId from Product p join p.subscibers s join s.tenant t" +
					" where t.id=? and s.validDate > ? and s.deleted = ?";
			List<Long> idList = businessDao.find(hql, getCompanyId(), new Date(), false);
			
			List<BusinessSystem> list = businessDao.findByCriteria(Restrictions.in("id", idList), Restrictions.eq(DELETED, false));
			hql = "select from BusinessSystem sys join sys.role join Role_WorkGroup  work_r  " +
				  "where work_r.workGroup.id=? and work_r.deleted=? and sys.deleted=? and work_r.companyId=?";*/
			
		//	return businessDao.find(hql,workId,false,false,getCompanyId());
			return null;
		
	  }
	  public List<Long> getRoleIds(Long workGroupId){
		  List<Long> roleIds = new ArrayList<Long>();
		  List<RoleWorkgroup> role_WorkGroups = role_wDao.findByCriteria(Restrictions.eq(WORKGROUPID, workGroupId),
				                                                          Restrictions.eq(COMPANYID, getCompanyId()),
				                                                          Restrictions.eq(DELETED, false));
		  for (RoleWorkgroup role_WorkGroup : role_WorkGroups) {
			  roleIds.add(role_WorkGroup.getRole().getId());
		}
		  return roleIds;
	  }
	  
	  public List<Role> getRole(Long workGroupId){
		  List<Role> roleIds = new ArrayList<Role>();
		  List<RoleWorkgroup> role_WorkGroups = role_wDao.findByCriteria(Restrictions.eq(WORKGROUPID, workGroupId),
				                                                          Restrictions.eq("companyId", getCompanyId()),
				                                                          Restrictions.eq(DELETED, false));
		  for (RoleWorkgroup role_WorkGroup : role_WorkGroups) {
			  roleIds.add(role_WorkGroup.getRole());
		}
		  return roleIds;
	  }
	  
	  public void workGroupAddRole(Long workGroupId, List<Long> roleIds,Integer isAdd){
		   Workgroup workGroup = getWorkGroup(workGroupId);
		   StringBuilder roleName = new StringBuilder();
		   if(isAdd==0){
			  RoleWorkgroup role_WorkGroup;
			  Role role =null;
			  
			  for (Long roId : roleIds) {
				  role_WorkGroup = new RoleWorkgroup();
				  role = roleDao.get(roId);
				  role_WorkGroup.setRole(roleDao.get(roId));
				  role_WorkGroup.setWorkgroup(workGroup);
				  role_WorkGroup.setCompanyId(getCompanyId());
				  role_wDao.save(role_WorkGroup);
				  roleName.append(role.getName());
				  roleName.append(",");
			  }
			  roleName.deleteCharAt(roleName.length()-1);
		  }
		  
		  if(isAdd==1){
			
			 List<RoleWorkgroup>  list = role_wDao.findByCriteria(Restrictions.in("role.id", roleIds),
												                   Restrictions.eq(WORKGROUPID, workGroupId),
												                   Restrictions.eq(COMPANYID, getCompanyId()),
												                   Restrictions.eq(DELETED, false));
			 for (RoleWorkgroup role_work : list) {
				
				 role_work.setDeleted(true);
				 role_wDao.save(role_work);
				 roleName.append(role_work.getRole().getName());
				 roleName.append(",");
			}
			 roleName.deleteCharAt(roleName.length()-1);
		  }
		  
	  }
	  
	  public List<Workgroup> queryWorkGroupByCompany(Long companyId){
		  return workGroupDao.findList(
				  "from Workgroup w where w.company.id=? and w.deleted=? order by w.weight desc", 
				  companyId, false);
	  }

	@SuppressWarnings("unchecked")
	public List<Workgroup> getWorkGroupsByUser(Long companyId, String loginName){
		StringBuilder hql = new StringBuilder();
		hql.append("select wg from Workgroup wg join wg.workgroupUsers wgu join wgu.user u ");
		hql.append("where u.companyId=? and u.loginName=? and u.deleted=? and wgu.deleted=? and wg.deleted=?");
		return workGroupDao.find(hql.toString(), companyId, loginName, false, false, false);
	}
	  
	public SimpleHibernateTemplate<Workgroup, Long> getWorkGroupDao() {
		return workGroupDao;
	}

	public SimpleHibernateTemplate<Role, Long> getRoleDao() {
		return roleDao;
	}

	public SimpleHibernateTemplate<RoleWorkgroup, Long> getRole_wDao() {
		return role_wDao;
	}

	public SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.User, Long> getUserDao() {
		return userDao;
	}

	public SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.WorkgroupUser, Long> getWorkGroupToUserDao() {
		return workGroupToUserDao;
	}


	public void setUserInfoDao(
			SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.UserInfo, Long> userInfoDao) {
		this.userInfoDao = userInfoDao;
	}


	public SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.UserInfo, Long> getUserInfoDao() {
		return userInfoDao;
	}
	 /**
	 * 保存工作组添加用户
	 * userIds:当是全公司时其值为[0],否则是人员id集合
	 * @return
	 * @throws Exception
	 */
	public void workgroupAddUser(Long workGroupId, List<Long> userIds, int isAdd) {
		if(userIds==null){
			return;
		}
		Workgroup workgroup = workGroupDao.get(workGroupId);
		/**
		 * 添加人员
		 */
		if (isAdd == 0) {
			for (Long userId : userIds) {
				if(userId.equals(0L)){//全公司时
					List<com.norteksoft.product.api.entity.User> users = ApiFactory.getAcsService().getAllUsersByCompany(ContextUtils.getCompanyId());
					for(com.norteksoft.product.api.entity.User u:users){
						workgroupAddSingleUser(u.getId(),workgroup);
					}
				}else{
					workgroupAddSingleUser(userId,workgroup);
				}
			}
		}
		/**
		 *移除人员
		 */
		if (isAdd == 1) {
			List<User> uif = userDao.findByCriteria(Restrictions.in(
					"id", userIds));
			List<Long> ids = new ArrayList<Long>();
			for (User user : uif) {
				ids.add(user.getId());
			}
			List<WorkgroupUser> list = workGroupToUserDao.findByCriteria(
					Restrictions.in("user.id", ids), Restrictions.eq(
							WORKGROUPID, workGroupId), Restrictions.eq(
									COMPANYID, getCompanyId()), Restrictions.eq(
									DELETED, false));

			for (WorkgroupUser workgroupUser : list) {
				 workgroupUser.setDeleted(true);
				 workGroupToUserDao.save(workgroupUser);
			}
		}

	}
	
	private void workgroupAddSingleUser(Long userId,Workgroup workgroup){
		WorkgroupUser workgroupUser;
		User user = null;
		List<WorkgroupUser> wu=getWorkgroupUserByuserId(userId,workgroup.getId());
		if(wu.size()==0){
			workgroupUser = new WorkgroupUser();
			user = userDao.get(userId);
			workgroupUser.setUser(user);
			workgroupUser.setWorkgroup(workgroup);
			workgroupUser.setCompanyId(getCompanyId());
			workGroupToUserDao.save(workgroupUser);
		}else{
			WorkgroupUser w=wu.get(0);
			w.setDeleted(false);
			workGroupToUserDao.save(w);
		}
	}
	
	/**
	 *根据userId得到WorkgroupUser
	 */
	@SuppressWarnings("unchecked")
	public List<WorkgroupUser> getWorkgroupUserByuserId(Long userId,Long workgroupId){
		String hql="from WorkgroupUser d where d.user.id=? and d.workgroup.id=?";
		return workGroupToUserDao.find(hql, userId,workgroupId);
	}
}