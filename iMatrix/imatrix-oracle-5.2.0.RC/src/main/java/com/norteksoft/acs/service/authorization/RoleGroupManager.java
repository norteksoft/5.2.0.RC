package com.norteksoft.acs.service.authorization;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleGroup;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;

@Service
@Transactional
public class RoleGroupManager {
	private static String DELETED = "deleted";
	private static String BUSINESSSYSTEM_ID = "businessSystem.id";
	private static String ROLEGROUP_ID = "roleGroup.id";
	private static String ROLEGROUP = "roleGroup";
	private static String COMPANYID = "companyId";
	private static String NAME = "name";
	private SimpleHibernateTemplate<RoleGroup, Long> roleGroupDao;
	private SimpleHibernateTemplate<Role, Long> roleDao;
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
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		roleGroupDao = new SimpleHibernateTemplate<RoleGroup, Long>(sessionFactory,
				RoleGroup.class);
		roleDao = new SimpleHibernateTemplate<Role, Long>(sessionFactory,
				Role.class);
	}
	
	public void saveRoleGroup(RoleGroup roleGroup){
		roleGroup.setCompanyId(getCompanyId());
		roleGroupDao.save(roleGroup);
	}
	
	public void deleteRoleGroup(Long id){
		RoleGroup roleGroup = roleGroupDao.get(id);
		roleGroup.setDeleted(true);
		roleGroupDao.save(roleGroup);
	}
	
	public RoleGroup getRoleGroup(Long id){
		return roleGroupDao.get(id);
	}
	
	public Page<RoleGroup> getAllRoleGroups(Page<RoleGroup> page){
		return roleGroupDao.findByCriteria(page, Restrictions.eq(DELETED, false), Restrictions.eq(COMPANYID, getCompanyId()), Restrictions.eq(BUSINESSSYSTEM_ID, getCompanyId()));
	}
	
	public List<RoleGroup> getAllRoleGroup(){
		return roleGroupDao.findByCriteria(Restrictions.eq(COMPANYID, getCompanyId()));
	}

	public boolean isRoleGroupNameUnique(String groupName, String roleGroupName) {
		return roleGroupDao.isPropertyUnique("name", groupName, roleGroupName);
	}

	public Page<Role> inputRole(Page<Role> page,String roleName,Long systemid){
		
		if(roleName!=null&&!"".equals(roleName)){
			
			return roleDao.findByCriteria(page, 
					                      Restrictions.isNull(ROLEGROUP),
					                      Restrictions.like(NAME, "%"+roleName +"%"),
					                      Restrictions.eq(BUSINESSSYSTEM_ID, systemid),
					                      Restrictions.eq(DELETED, false));
		}
		
		return roleDao.findByCriteria(page,
				                      Restrictions.isNull(ROLEGROUP),
									  Restrictions.eq(BUSINESSSYSTEM_ID, systemid),
									  Restrictions.eq(DELETED, false));
	}
	
	public Page<Role> romoveRole(Page<Role> page,String roleName,Long systemid,Long fungId){
		
		if(roleName!=null&&!"".equals(roleName)){
			
			return roleDao.findByCriteria(page, 
					                      Restrictions.isNotNull(ROLEGROUP),
					                      Restrictions.eq(ROLEGROUP_ID,fungId),
					                      Restrictions.like(NAME, "%"+roleName +"%"),
					                      Restrictions.eq(BUSINESSSYSTEM_ID, systemid),
					                      Restrictions.eq(DELETED, false));
		}
		return roleDao.findByCriteria(page,
				                      Restrictions.isNotNull(ROLEGROUP),
				                      Restrictions.eq(ROLEGROUP_ID,fungId),
									  Restrictions.eq(BUSINESSSYSTEM_ID, systemid),
									  Restrictions.eq(DELETED, false));
	}
	public void saveRole(Long paternId,List<Long> roleIds,int isAdd){
		RoleGroup roleGroup = roleGroupDao.get(paternId);
		roleGroup.setCompanyId(getCompanyId());
		List<Role> list = roleDao.findByCriteria(Restrictions.in("id", roleIds));
		StringBuilder roleName = new StringBuilder();
		
		if(isAdd==0){
			for (Role role : list) {
				 role.setRoleGroup(roleGroup);
				 roleDao.save(role);
				 roleName.append(role.getName());
				 roleName.append(",");
			}
			 roleName.deleteCharAt(roleName.length()-1);
		}
		if(isAdd==1){
			for (Role role : list) {
				 role.setRoleGroup(null);
				 roleDao.save(role);
				roleName.append(role.getName());
				roleName.append(",");
			}
			roleName.deleteCharAt(roleName.length()-1);
		}
		
		 
	}

	
	public Page<RoleGroup> getRoleGroupsBySystem(Page<RoleGroup> page,	Long systemId) {
		return roleGroupDao.findByCriteria(page, Restrictions.eq(BUSINESSSYSTEM_ID, systemId), 
				Restrictions.eq(DELETED, false),
				Restrictions.eq(COMPANYID, getCompanyId()));
		
	}

	public SimpleHibernateTemplate<RoleGroup, Long> getRoleGroupDao() {
		return roleGroupDao;
	}

	public SimpleHibernateTemplate<Role, Long> getRoleDao() {
		return roleDao;
	}
	
}
