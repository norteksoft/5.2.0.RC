package com.norteksoft.acs.service.organization;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.DepartmentUser;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.bs.options.entity.ImportDefinition;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.impl.DefaultDataImporterCallBack;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.util.ContextUtils;

@Service
@Transactional
public class ImportUserManager extends DefaultDataImporterCallBack{
	private SimpleHibernateTemplate<Company, Long> companyDao;
	private SimpleHibernateTemplate<Department, Long> departmentDao;
	private SimpleHibernateTemplate<DepartmentUser, Long> departmentToUserDao;
	private SimpleHibernateTemplate<UserInfo, Long> userInfoDao;
	
	@Autowired
	private UserManager userManager;
	@Autowired
	private UserInfoManager userInfoManager;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		companyDao=new SimpleHibernateTemplate<Company, Long>(sessionFactory, Company.class);
		departmentDao = new SimpleHibernateTemplate<Department, Long>(sessionFactory, Department.class);
		departmentToUserDao = new SimpleHibernateTemplate<DepartmentUser, Long>(sessionFactory, DepartmentUser.class);
		userInfoDao = new SimpleHibernateTemplate<UserInfo, Long>(sessionFactory, UserInfo.class);
	}
	
	private String validateUser(User u){
		if(ContextUtils.getCompanyId().equals(u.getCompanyId())){
			if(u.isDeleted()&&u.getUserInfo().getDr()==0){
				return "登陆名为"+u.getLoginName()+"的用户没有彻底删除";
			}
		}else{
			if(!u.isDeleted()||(u.isDeleted()&&u.getUserInfo().getDr()==0)){
				return "其他租户中已有登陆名为"+u.getLoginName()+"的用户";
			}
		}
		return "";
	}
	
	public String saveSingleRow(String[] rowValue,ImportDefinition importDefinition) {
		List<User> users = userManager.getUsersByLoginName(rowValue[2]);
		String result="";
		if(users != null && users.size()>0){
			for(User u:users){
				result=validateUser(u);
				if(StringUtils.isNotEmpty(result))
					return result;
			}
		}
		
		Integer currentUserNumber = userManager.getUserNumberByCompanyId(ContextUtils.getCompanyId());
		Integer companyUserLimit=userManager.getAllowedNumbByCompany(ContextUtils.getCompanyId());
 		Integer importCount = 0;
		if(StringUtils.isNotEmpty(rowValue[0])){
			String[] depts=rowValue[0].split("/");
			for(int i=0;i<depts.length;i++){
				Department department=null;
				if(userManager.isDepartmentExist(depts[i],ContextUtils.getCompanyId())){//部门存在
					department=getDepartmentByName(depts[i]);
				}else{
					department=new Department();
				}
				Company company = companyDao.get(ContextUtils.getCompanyId());
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
					if(StringUtils.isNotEmpty(rowValue[2])){//用户登录名不为空,添加用户
						if(currentUserNumber+importCount+1>companyUserLimit)return "已导入"+importCount+"条,超出系统允许注册人数";
						departmentDao.save(department);
						UserInfo userInfo=userManager.importUserSaveUser(rowValue,department);
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
							departmentToUser.setCompanyId(ContextUtils.getCompanyId());
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
			if(StringUtils.isNotEmpty(rowValue[2])){
					if(currentUserNumber+importCount+1>companyUserLimit)return "已导入"+importCount+"条,超出系统允许注册人数";
					userManager.importUserSaveUser(rowValue,null);
					User user = userManager.getUserByLoginName(StringUtils.trim(rowValue[2]));
					//新建用户时默认给用户portal普通用户权限
					if(user!=null)userInfoManager.giveNewUserPortalCommonRole(user);
					if(user==null){
					//记录公司用户数量
					importCount++;
					}
				}
		}
		return "";
	}
	
	
	@SuppressWarnings("unchecked")
	public Department getDepartmentByName(String name){
		if(name == null) throw new RuntimeException("没有给定查询部门时的查询条件：部门名称");
		List<Department> depts = departmentDao.find("from Department d where d.company.id=? and d.name=? and d.deleted=?", ContextUtils.getCompanyId(), name, false);
		if(depts.size() == 1){
			return depts.get(0);
		}
		return null;
	}
}
