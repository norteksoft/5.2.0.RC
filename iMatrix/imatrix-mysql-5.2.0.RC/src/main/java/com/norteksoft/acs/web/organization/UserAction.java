package com.norteksoft.acs.web.organization;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.utils.ExportUserInfo;
import com.norteksoft.acs.base.utils.Ldaper;
import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.acs.service.organization.ImportUserManager;
import com.norteksoft.acs.service.organization.UserInfoManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.acs.service.sale.SubsciberManager;
import com.norteksoft.acs.web.eunms.AddOrRomoveState;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.CollectionUtils;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.Md5;
import com.norteksoft.product.util.PageUtils;

@SuppressWarnings("deprecation")
@Namespace("/organization")
@ParentPackage("default")
@Results( {
		@Result(name = CRUDActionSupport.RELOAD, location = "user?synchronousLdapMessage=${synchronousLdapMessage}&message=${message}", type = "redirectAction"),
		@Result(name = "deleteList", location = "user!deleteList", type = "redirectAction"),
		@Result(name = "redirect_url", location = "${redirectUrl}", type = "redirect")})
public class UserAction extends CRUDActionSupport<UserInfo> {

	private static final long serialVersionUID = 4814560124772644966L;
	private Page<User> page = new Page<User>(0, true);// 每页5项，自动查询计算总页数.
	private Page<Department> pageUserToDepart = new Page<Department>(20, true);// 每页5项，自动查询计算总页数.
	private Page<Workgroup> pageUserToWork = new Page<Workgroup>(20, true);// 每页5项，自动查询计算总页数.
	private UserInfoManager userInfoManager;
	private UserManager userManager;
	private DepartmentManager departmentManager;
	private List<UserInfo> allUser;
	private List<Department> allDepartment;
	private List<Workgroup> allWorkGroup;
	private List<Long> checkedWorkGroupIds;
	private List<Long> checkedDepartmentIds;
	private Long userInfoIds;
	private User user;
	private UserInfo entity;
	private List<UserInfo> userInfos;
	private Long id;
	private String passWord_CreateTime;
	private String ids;
	private List<Long> workGroupIds;
	private List<Long> departmentIds;
	private String dids;
	private Long userId;
	private List<Role> allRoles;
	private List<Long> roleIds;
	private List<Long> checkedRoleIds;
	private List<Long> passWordOverdueIds;
	private Map<Long,Integer> passwordOverNoticeId;
	private Integer isAddOrRomove;
	private String flag;
	private List<BusinessSystem> systems;
	private BusinessSystemManager businessSystemManager;
	private CompanyManager companyManager;
	private SubsciberManager subsciberManager;
	private String redirectUrl;
	private String password;
	private String states;
	private Long companyId;
	private String historyUserName;
	private String synchronousLdapMessage;
	private String message;
	private String departmentName;
	private String deId;
	private String type;
	private String depIds;
	private String usersId;
	private String mode;
	private String lookId;
	private String look;
	private String looked;
    private String oldDid;
	private String oldType;
	private String olDid;
	private String olType;
	private String passWordChange;
	private String departmId;
	private String departmType;
	private String edit;
	private String edited;;
	
	private String oraginalPassword;
	private Boolean isPasswordChange;
	private String levelpassword;
	
	private File file;
	private String fileName;
	
	private String oneDid;
	private String mainDepartmentName;
	
	private String comy;
	private String fromWorkgroup;
	private String fromChangeMainDepartment;//来自批量更换主职部门
	private Long newMainDepartmentId;
	@Autowired
	private ImportUserManager importUserManager;
	
	
	@Action("list")
	public String toList() throws Exception{
		return SUCCESS;
	}
	
	/**
	 *=================用户管理==================== 用户管理的列表界面 条件是用户信息的dr字段等于0
	 */
	@Override
	@Action("user")
	public String list() throws Exception {
		if(departmId!=null&&departmType!=null){
			if(departmType.equals("USERSBYDEPARTMENT"))
				departmentId=Long.parseLong(departmId);
		}
		if(departmentId != null){
			return getUserByDepartment();
		}else if(workGroupId != null){
			return getUserByWorkGroup();
		}else if(departmType!=null&&(departmType.equals("NODEPARTMENT")||departmType.equals("NODEPARTMENT_USER"))){
			return getNoDepartmentUsers();
		}else if(departmType!=null&&departmType.equals("DELETED")){
			return deleteList();
		}else if(departmType!=null&&departmType.equals("allDepartment")){
			return getUserByCompanyHasLog();
		}else if(departmType!=null&&departmType.equals("company")){
			return getUserByCompanyHasLog();
		}else{
			flag = "true";
			return 	search();
		}
		
	}
	
	public String getUserByCompanyHasLog()throws Exception{
		if(page.getPageSize() <= 1){
			return SUCCESS; 
		}else{
			ApiFactory.getBussinessLogService().log("用户管理","查看用户列表",ContextUtils.getSystemId("acs"));
			page = userInfoManager.queryUsersByCompany(page, companyManager.getCompanyId());
			renderHtml(PageUtils.pageToJson(page));
			return null;
		}
	}

	public void prepareSearch() throws Exception {
		prepareModel();
	}

	public String search() throws Exception {
		if(page.getPageSize() <= 1){
			ApiFactory.getBussinessLogService().log("用户管理","查看用户列表",ContextUtils.getSystemId("acs"));
			return SUCCESS; 
		}else{
			page = userInfoManager.getSearchUser(page, entity, 0, false);
            //passWordOverdueIds = userInfoManager.getPassWordOverdueId(page.getResult());
            //passwordOverNoticeId = userInfoManager.passwordOverNotice(page.getResult());
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
	}
	
	@Override
	public String save() throws Exception {
		boolean logSign=true;//该字段只是为了标识日志信息：true表示新建用户、false表示修改用户
		if(id!=null)logSign=false;
		
		Long oldDeptId = entity.getUser().getMainDepartmentId();
		if(StringUtils.isNotEmpty(oneDid)){//正职部门id
			  entity.getUser().setMainDepartmentId(Long.valueOf(oneDid));
		}
		if((entity!=null&&entity.getId()==null)||"yes".equals(passWordChange)){
			entity.setPasswordUpdatedTime(new Date()); 
		}
		userInfoManager.save(entity);
		id = entity.getId();
		
		//新建用户是默认给用户portal普通用户权限
		userInfoManager.giveNewUserPortalCommonRole(entity.getUser());
		
		//修改ldap密码
		if(Ldaper.isStartedAboutLdap()){
			Company company = companyManager.getCompany(ContextUtils.getCompanyId());
			List<Department> departments = userManager.getDepartmentsByUser(entity.getId());
			message = Ldaper.modifyUser(entity.getUser(), company.getCode(), departments, false, entity.getUser().getLoginName());
		}
		
		
		// 处理部门关系，正职或兼职有修改
		Set<Long> addDeptIds = new HashSet<Long>();
		List<Long> delDeptIds = new ArrayList<Long>();
		//dids是新兼职部门id  //deId为原来的兼职部门id
		if(StringUtils.isNotEmpty(oneDid)){
			addDeptIds.add(Long.valueOf(oneDid));
			if(oldDeptId != null) delDeptIds.add(oldDeptId);
		}else{
			if(oldDeptId != null) delDeptIds.add(oldDeptId);
		}
		if(StringUtils.isEmpty(dids)){// 新兼职部门没有值
			if(StringUtils.isNotEmpty(deId)){ // 而原来的兼职部门有值，删除原来的
				String[] tempDelIds = deId.split("=");
				delDeptIds.addAll(CollectionUtils.changeList(tempDelIds));
			}
		}else{ // 新兼职部门有值
			// 增加的新兼职部门
			String[] tempAddIds = dids.split("=");
			addDeptIds.addAll(CollectionUtils.changeList(tempAddIds));
			if(StringUtils.isNotEmpty(deId)){ // 而原来的兼职部门有值，删除原来的
				String[] tempDelIds = deId.split("=");
				delDeptIds.addAll(CollectionUtils.changeList(tempDelIds));
			}
		}
		if(!delDeptIds.isEmpty()){// 删除的
			userManager.deleteDepartmemtToUser(delDeptIds, entity.getUser().getId());
			deId=null;
		}
		if(!addDeptIds.isEmpty()){ // 增加的
			departmentIds = new ArrayList<Long>();departmentIds.addAll(addDeptIds);
			userManager.addDepartmentToUserDel(entity.getId(), departmentIds, 0);
		}
		
			
		setUserDeptmentInfo(entity.getUser());
		
		if(logSign){
			ApiFactory.getBussinessLogService().log("用户管理", 
					"新建用户："+entity.getUser().getName(),ContextUtils.getSystemId("acs"));
		}else{
			ApiFactory.getBussinessLogService().log("用户管理", 
					"修改用户:"+entity.getUser().getName(),ContextUtils.getSystemId("acs"));
		}
			return INPUT;
	}
	
	/**
	 * 弹选多个部门树
	 */
	public String chooseDepartments(){
		
		return "departmentTree";
	}
	/**
	 * 弹选单个部门树
	 */
	public String chooseOneDepartment(){
		
		return "departmentSingleTree";
	}
	
	
	
	/**
	 * 修改密码方法
	 */
	public void prepareModifyPassWord() throws Exception {
		prepareModel();
	}
	
	public String modifyPassWord()throws Exception {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		passWord_CreateTime = sdf.format(new Date());
		return "modify-password";
	}
	
	/**
	 *各项目中的 修改密码的方法
	 */
	public void prepareUpdateUserPassword() throws Exception {
		userId=ContextUtils.getUserId();
		prepareModel();
	}
	
	public String updateUserPassword()throws Exception {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		passWord_CreateTime = sdf.format(new Date());
		User u=userManager.getUserById(ContextUtils.getUserId());
		oraginalPassword=u.getPassword();
		return "update-user-password";
	}
	
	public void prepareSavePassWord() throws Exception {
		prepareModel();
	}
    public String savePassWord()throws Exception {
    	if(Md5.toMessageDigest(levelpassword).equals(oraginalPassword)){
    		entity.setPasswordUpdatedTime(new Date());
    		userInfoManager.savePassWord(entity);
    		Company company = companyManager.getCompany(ContextUtils.getCompanyId());
    		List<Department> departments = userManager.getDepartmentsByUser(entity.getId());
    		if(Ldaper.isStartedAboutLdap()){
    			message = Ldaper.modifyUser(entity.getUser(), company.getCode(), departments, false, entity.getUser().getName());
    		}
    		isPasswordChange=true;
    	}else{
    		isPasswordChange=false;
    		User myuser = entity.getUser();
    		myuser.setPassword(oraginalPassword);
    		userManager.saveUser(myuser);
    		addActionMessage("原密码错误");
    	}
    	ApiFactory.getBussinessLogService().log("所有系统中", 
				"修改用户密码",ContextUtils.getSystemId("acs"));
		return "update-user-password";
	}
    
	public String input() throws Exception {
		if(id!=null){
			entity = user.getUserInfo();
			setUserDeptmentInfo(user);
			looked=look;
			edited=edit;
			ApiFactory.getBussinessLogService().log("用户管理", 
					"修改用户",ContextUtils.getSystemId("acs"));
		}else{
			ApiFactory.getBussinessLogService().log("用户管理", 
					"新建用户",ContextUtils.getSystemId("acs"));
			if(departmentIds.size()>0&&departmentIds.get(0)!=null&&departmentIds.get(0)!=0){
				Department department=departmentManager.getDepartment(departmentIds.get(0));
			    if(department!=null){
			    	mainDepartmentName = department.getName();
			    }
			}
		}
		return INPUT;
	}
	
	private void setUserDeptmentInfo(User user){
		// 正职部门
		if(user.getMainDepartmentId()!=null){
			Department d = departmentManager.getDepartmentById(user.getMainDepartmentId());
			mainDepartmentName = (d==null?"":d.getName());
		}
		// 兼职部门
		List<Department> departments= userManager.getDepartmentsByUser(user.getId());
		departmentName="";deId="";
		if(departments.size()>0){
			for(Department department:departments){
				if(!department.getName().equals(mainDepartmentName)){
			        departmentName+=department.getName()+",";
			        deId+=department.getId()+"=";
				}
			}
			if(StringUtils.isNotEmpty(deId)) deId=deId.substring(0, deId.length()-1);
			if(StringUtils.isNotEmpty(departmentName)) departmentName=departmentName.substring(0,departmentName.length()-1);
		}
	}
	public void prepareInputLook() throws Exception {
		prepareModel();
	}
	public String inputLook() throws Exception {
		if(id!=null){
			user=userInfoManager.getUserInfoById(id).getUser();
			if(user.getMainDepartmentId()!=null){
				Department d = departmentManager.getDepartment(user.getMainDepartmentId());
				mainDepartmentName = (d==null?"":d.getName());
			}
			List<Department> departments= userManager.getDepartmentsByUser(user.getId());
			departmentName="";
			if(departments.size()>0){
				for(Department department:departments){
					if(!department.getName().equals(mainDepartmentName))
			        departmentName+=department.getName()+",";
				}
				if(departmentName.length() > 0) departmentName = departmentName.substring(0,departmentName.length()-1);
			}
			looked=look;
		}
		return INPUT;
	}

	

  
  public String checkUserRegister()throws Exception{
	  Integer maxUser = subsciberManager.getAllowedNumbByCompany(userInfoManager.getCompanyId());
  	  Integer currentUser = userInfoManager.getCompanyIsUsers();
  	  HttpServletRequest request = ServletActionContext.getRequest();
	  String weburl = request.getParameter("weburl");
  	  if(maxUser.intValue()<(currentUser.intValue()+1)){
  		 renderText("1");
  	  }else{
  		renderText(weburl);
  	  }
  	  return null;
  }
  
  
  
  /**
   * 删除用户
   * @return
   * @throws Exception
   */
	public String falseDelete() throws Exception {
		String logSign="";//该字段只是为了标识日志信息：用户1，用户2，...
		
		String[] arr=ids.split(",");
		for(String userId:arr){
			userInfoManager.falseDelete(Long.valueOf(userId),departmentIds);
			
			user=userManager.getUserById(Long.valueOf(userId));
			if(StringUtils.isNotEmpty(logSign)){
				logSign+=",";
			}
			logSign+=user.getName();
		}
		if(departmentIds.get(0)!=null){
		    departmentId=departmentIds.get(0);
		}else{
			if(StringUtils.isEmpty(departmType)){
				departmType = "NODEPARTMENT";
			}
		}
		ApiFactory.getBussinessLogService().log("用户管理", 
				"删除用户:"+logSign,ContextUtils.getSystemId("acs"));
		return list();
	}
	/**
	 * 判断是否是管理员
	 * @return
	 * @throws Exception
	 */
	public String checkIsAdmin() throws Exception {
		//User user = userManager.getUserById(id);
		String roles = "";
		String result = "";
		String[] arr=ids.split(",");
		for(String userId : arr){
			user = userManager.getUserById(Long.valueOf(userId));
			roles = userManager.getRolesExcludeTrustedRole(user);
			if(roles.indexOf("Admin")>-1){
				result = "yes";
			}
		}
		renderText(result);
		return null;
	}

	/**
	 * 用户禁用
	 */
	public void forbidden() throws Exception {
		userInfoManager.forbidden(id);
		
	}

	/**
	 * 用户启用
	 */
	public void invocation() throws Exception {
		userInfoManager.invocation(id);
		
	}

	/**
	 * 用户解锁
	 */
	public void unblock() throws Exception {
		userInfoManager.unblock(id);
		
	}
	/**
	 * 锁定用户
	 */
	public void lock() throws Exception {
		userInfoManager.lock(id);
	}
	public void prepareUserManger() throws Exception {
		prepareModel();
	}
	
	public String userManger()throws Exception{
		
		return "state";
	}
	
	
	public String saveUserState()throws Exception{
		if(StringUtils.isEmpty(states)){
		
			return RELOAD;
		}else{
			boolean logSign=true;//该字段只是为了标识日志信息：true表示启用、false表示禁用
			entity = userInfoManager.getUserInfoById(id);
			
			String[] stateStr = states.split(",");
			for (int i=0;i<stateStr.length;i++) {
				if(stateStr[i].equals("accountUnLock"))//用户密码过期解锁
					unblock();
				if(stateStr[i].equals("accountLock"))//用户密码过期不解锁
					lock();
				if(stateStr[i].equals("forbidden"))//禁用
					forbidden();
					logSign=false;
				if(stateStr[i].equals("invocation")){//启用
					invocation();
					logSign=true;
				}
			}
			
			if(logSign){
				ApiFactory.getBussinessLogService().log("用户管理", 
						"改变用户状态:启用"+entity.getUser().getName(),ContextUtils.getSystemId("acs"));
			}else{
				ApiFactory.getBussinessLogService().log("用户管理", 
						"改变用户状态:禁用"+entity.getUser().getName(),ContextUtils.getSystemId("acs"));
			}
			
			return RELOAD;
		}
		
	}

	/**
	 * ================已删除用户管理==============
	 * 
	 */
	@Override
	public String delete() throws Exception {
		userInfoManager.delete(ids);
		ApiFactory.getBussinessLogService().log("已删除用户管理", 
				"彻底删除用户",ContextUtils.getSystemId("acs"));
		return deleteList();
	}

	/**
	 * 已删除用户的列表界面
	 */
	public String deleteList() throws Exception {
		if(page.getPageSize() <= 1){
			return "delete"; 
		}else{
			page = userInfoManager.getSearchUser(page, entity, 0, true);
			renderHtml(PageUtils.pageToJson(page));
			ApiFactory.getBussinessLogService().log("已删除用户管理", 
					"已删除用户列表",ContextUtils.getSystemId("acs"));
			return null;
		}
	}
	/**
	 * 无部门人员列表
	 */
	
	public String getNoDepartmentUsers() throws Exception{
		//passWordOverdueIds = userInfoManager.getPassWordOverdueId(page.getResult());
		//passwordOverNoticeId = userInfoManager.passwordOverNotice(page.getResult());
		if(page.getPageSize() <= 1){
			return SUCCESS; 
		}else{
			ApiFactory.getBussinessLogService().log("用户管理", 
					"查看用户列表",ContextUtils.getSystemId("acs"));
			userInfoManager.getNoDepartmentUsers(page);
			renderHtml(PageUtils.pageToJson(page));
			return null;
		}
	}
	/**
	 * 已删除用户管理查询方法
	 */
	public void prepareSearchDelete() throws Exception {
		prepareModel();
	}

	public String searchDelete() throws Exception {
		page = userInfoManager.getSearchUser(page, entity,0, true);
		return "delete";
	}
	
	  /**
     * 跳转到己删除用户添加部门页面
     */
    public void prepareToDepartmentToUsersDel() throws Exception {
    	
    	//entity =userInfoManager.getUserInfoById(userId);
    	userInfos=new ArrayList<UserInfo>();
    	String[] arr=ids.split(",");
    	for(int i=0;i<arr.length;i++){
    		User user=userManager.getUserById(Long.valueOf(arr[i]));
    		userInfos.add(user.getUserInfo());
    	}
	} 
    
    public String toDepartmentToUsersDel()throws Exception{
    	pageUserToDepart = userManager.getDepartmentList(pageUserToDepart);
    	//checkedDepartmentIds = userManager.getCheckedDepartmentIds(userId);
    	 isAddOrRomove=AddOrRomoveState.ADD.code;
    	return "deleted-department-list";
    }

	/**
	 * 给用户(己删除)分配部门
	 */
	public String saveDepartmentToUserDel() throws Exception{
		String[] arr=ids.split(",");
		for(int i=0;i<arr.length;i++){
			User user = userManager.getUserById(Long.valueOf(arr[i]));
			user.getUserInfo().setDr(0);
			user.getUserInfo().setDeleted(false);
			user.setDeleted(false);
			Department department=departmentManager.getDepartment(departmentId);
			if(department != null){
				user.setMainDepartmentId(departmentId);
			}
			userInfoManager.save(user.getUserInfo());
			userManager.saveUser(user);
			List<Long> dIds = new ArrayList<Long>();
			dIds.add(departmentId);
			userManager.addDepartmentToUserDel(user.getUserInfo().getId(), dIds,0);
		}
		return "delete";
	}
	
	/**
	 * 批量更换用户的主职部门
	 */
	public String batchChangeUserMainDepartment() throws Exception{
		userManager.batchChangeMainDepartment(ids,newMainDepartmentId);
		
		String logSign="";//该字段只是为了标识日志信息：用户1，用户2，...
		if(StringUtils.isNotEmpty(ids)){
			String[] userids=ids.split(",");
			Department department = departmentManager.getDepartment(newMainDepartmentId);
			for(String userid:userids){
				user=userManager.getUserById(Long.valueOf(userid));
				if(StringUtils.isNotEmpty(logSign)){
					logSign+=",";
				}
				logSign+=user.getName();
			}
			ApiFactory.getBussinessLogService().log("用户管理", 
					"更改用户"+logSign+"正职部门为"+department.getName(),ContextUtils.getSystemId("acs"));
		}
		return list();
	}

	/**
	 * 给用户分配部门
	 */
	
	public String  changeDepartment(){
		String[] d=depIds.split("=");
		String[] u=usersId.split(",");
		List<Long> departIds=CollectionUtils.changeList(d);
		List<Long> uIds=CollectionUtils.changeList(u);
		for(Long id:uIds){
			List<Department> departments=userManager.getDepartmentsByUser(id);
			if(departments.size()>0){
				List<Long> depaIds=new ArrayList<Long>();
				for(Department department :departments){
					depaIds.add(department.getId());
				}
				userManager.deleteDepartmemtToUser(depaIds,id);
			}
			userManager.addDepartmentToUserDel(id,departIds ,0);
		}
		return RELOAD;
	}

	/**
	 * 用户分配角色
	 */
	public String listRoles() throws Exception {
		isAddOrRomove = 0;
		userId = entity.getUser().getId();
		systems = businessSystemManager.getAllBusiness();
		checkedRoleIds = userManager.getCheckedRoleIdsByUser(userId);
		return "role";
	}
	
	/**
	 * 用户移除角色
	 */
	public String removeRoles() throws Exception{
		isAddOrRomove = 1;
		userId = entity.getUser().getId();
		systems = businessSystemManager.getAllBusiness();
		checkedRoleIds = userManager.getCheckedRoleIdsByUser(userId);
		return "role";
	}

	/**
	 * 给用户分配角色
	 */
	public String addRolesToUser() {
		userManager.addRolesToUser(userId, roleIds, isAddOrRomove);
		addActionMessage(getText("department.addRolesSuccess"));
		return RELOAD;
	}
	
	  /**
     * 跳转到人员添加部门页面
     */
    public void prepareAddDepartmentToUsers() throws Exception {
    	//entity =userInfoManager.getUserInfoById(userId);
	} 
    public String addDepartmentToUsers()throws Exception{
    	
    	pageUserToDepart = userManager.getDepartmentList(pageUserToDepart);
    	//checkedDepartmentIds = userManager.getCheckedDepartmentIds(userId);
    	 isAddOrRomove=AddOrRomoveState.ADD.code;
    	return "department-list";
    	
    }
    
    /**
     * 跳转到人员移除部门页面
     */
    
    public void prepareRemoveDepartmentToUsers() throws Exception {
    	entity = userInfoManager.getUserInfoById(userId);
	} 
    public String removeDepartmentToUsers()throws Exception{
    	
    	pageUserToDepart = userManager.userToRomoveDepartmentList(pageUserToDepart, null, userId);
    	isAddOrRomove=AddOrRomoveState.ROMOVE.code;
    	return "department-list";
    }

	/**
	 * 给用户分配部门
	 */
	public String addDepartmentToUser() {
		userManager.addDepartmentToUser(userId, departmentIds,isAddOrRomove);
		addActionMessage(getText("user.addDepartmentSuccess"));
		return RELOAD;
	}
	  /**
     * 跳转到人员添加工作组页面
     */
    public void prepareAddWorkGroupToUsers() throws Exception {
    	entity = userInfoManager.getUserInfoById(userId);
	} 
    
    public String addWorkGroupToUsers()throws Exception{
    	
    	pageUserToWork = userManager.getWorkGroupList(pageUserToWork);
    	checkedWorkGroupIds = userManager.getCheckedWorkGroupIds(userId);
    	isAddOrRomove=AddOrRomoveState.ADD.code; 
    	return "work-group-list";
    }
    
    /**
     * 跳转人员移除工作组页面
     */
    
    public void prepareRemoveWorkGroupToUsers() throws Exception {
    	entity = userInfoManager.getUserInfoById(userId);
	} 
    public String removeWorkGroupToUsers()throws Exception{   
    	
    	pageUserToWork = userManager.userToRomoveWorkGroupList(pageUserToWork, null, userId);
	    isAddOrRomove=AddOrRomoveState.ROMOVE.code;
    	return "work-group-list";
    }

	/**
	 * 给用户分配工作组
	 */
	public String addWorkGroupToUser() {
		userManager.addWorkGroupToUser(userId, workGroupIds,isAddOrRomove);
		addActionMessage(getText("user.addWorkGroupSuccess"));
		return RELOAD;
	}
	/**
	 * 同步LDAP的用户
	 * @return
	 * @throws Exception
	 */
	public String synchronous() throws Exception{
		synchronousLdapMessage = userInfoManager.synchronize();
		renderText(synchronousLdapMessage);
		ApiFactory.getBussinessLogService().log("用户管理", "同步Ldap",ContextUtils.getSystemId("acs"));
		return null;
	}
	
	public String validateLdapStart() throws Exception{
		boolean falg = userInfoManager.validateLdapStart();
		if(falg){
			renderText("true");
		}else{
			renderText("false");
		}
		return null;
	}

	public String checkLoginPassword() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		String orgPassword = request.getParameter("orgPassword");
		boolean istrue = userInfoManager.checkLoginPassword(orgPassword);
		if(istrue){
			renderText("");
			return null;
		}
		   renderText(getText("user.rulesNotMatch"));
		   return null;
	}
	

	public String checkOldPassword() throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		String oldPassword = request.getParameter("oldPassword");
		Long id = Long.valueOf(request.getParameter("id"));
		User user = userManager.getUserById(id);
		if(oldPassword==null || "".equals(oldPassword.trim())){
			this.renderText("false");
		}else if(oldPassword.equals(user.getPassword())){
		//}else if(PasswordEncoder.encode(oldPassword).equals(user.getPassword())){
			this.renderText("true");
		}else{
			this.renderText("false");
		}
		return null;
	}
	
	public String updatePassword() throws Exception{
		User user = userManager.getUserById(id);
		String oldPassword = Md5.toMessageDigest(oraginalPassword);
		if(StringUtils.isNotBlank(oraginalPassword) && oldPassword.equals(user.getPassword())){
			user.getUserInfo().setPasswordUpdatedTime(new Date());
			user.setPassword(password);
			userManager.saveUser(user);
			renderText("");
		}else{
			renderText("old_pwd_error");
		}
		return null;
	}
	
	public  void overdueUnblock()throws Exception{
		userInfoManager.overdueUnblock(id);
	}
	public  void overdueblock()throws Exception{
		userInfoManager.overdueblock(id);
	}
	
	public String showImportUser() throws Exception{
		return "import-user";
	}
	
	public String importUser() throws Exception{
		String result = "";
		try {
			result = ApiFactory.getDataImporterService().importData(file, fileName,importUserManager);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		renderText(result);
		return null;
	}
	
	public String exportUser() throws Exception{
		HttpServletResponse response = ServletActionContext.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode("用户信息.xls","UTF-8"));
		List<Department> depts = departmentManager.getAllDepartment();
		ExportUserInfo.exportUser(response.getOutputStream(), depts, ContextUtils.getCompanyId());
		ApiFactory.getBussinessLogService().log("用户管理", 
				"导出用户",ContextUtils.getSystemId("acs"));
		return null;
	}
	/**
	 * 用户解锁
	 * @return
	 * @throws Exception
	 */
	public String unlockUser() throws Exception{
		String logSign="";//该字段只是为了标识日志信息：用户1，用户2，...
		if(StringUtils.isNotEmpty(ids)){
			String[] userids=ids.split(",");
			for(String userid:userids){
				user=userManager.getUserById(Long.valueOf(userid));
				if(StringUtils.isNotEmpty(logSign)){
					logSign+=",";
				}
				logSign+=user.getName();
			}
		}
		
		this.renderText(userManager.unlockUser(ids));
		ApiFactory.getBussinessLogService().log("用户管理", 
				"用户解锁:"+logSign,ContextUtils.getSystemId("acs"));
		return null;
	}
	
	
	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			entity = userInfoManager.getUserInfoById(id);
		}else if(userId != null){
			user = userManager.getUserById(userId);
			entity = user.getUserInfo();
			id = entity.getId();
		}else {
			entity = new UserInfo();
			entity.setUser(new User());
		}
	}

	public UserInfo getModel() {
		return entity;
	}
	
	public User getUser() {
		return user;
	}
	
	public Long getUserInfoIds() {
		return userInfoIds;
	}

	public UserInfo getEntity() {
		return entity;
	}

	public List<Long> getCheckedWorkGroupIds() {
		return checkedWorkGroupIds;
	}

	public List<Long> getCheckedDepartmentIds() {
		return checkedDepartmentIds;
	}

	public Integer getIsAddOrRomove() {
		return isAddOrRomove;
	}

	public void setIsAddOrRomove(Integer isAddOrRomove) {
		this.isAddOrRomove = isAddOrRomove;
	}

	@Required
	public void setUserInfoManager(UserInfoManager userInfoManager) {
		this.userInfoManager = userInfoManager;
	}
	
	@Required
	public void setDepartmentManager(DepartmentManager departmentManager) {
		this.departmentManager = departmentManager;
	}
	@Required
	public void setCompanyManager(CompanyManager companyManager) {
		this.companyManager = companyManager;
	}
	public UserManager getUserManager() {
		return userManager;
	}

	@Required
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public void setCheckedWorkGroupIds(List<Long> checkedWorkGroupIds) {
		this.checkedWorkGroupIds = checkedWorkGroupIds;
	}

	public void setCheckedDepartmentIds(List<Long> checkedDepartmentIds) {
		this.checkedDepartmentIds = checkedDepartmentIds;
	}

	public List<UserInfo> getAllUser() {
		return allUser;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public Page<User> getPage() {
		return page;
	}

	public void setPage(Page<User> page) {
		this.page = page;
	}

	
	public Page<Workgroup> getPageUserToWork() {
		return pageUserToWork;
	}

	public void setPageUserToWork(Page<Workgroup> pageUserToWork) {
		this.pageUserToWork = pageUserToWork;
	}


	public List<Department> getAllDepartment() {
		return allDepartment;
	}

	public void setAllDepartment(List<Department> allDepartment) {
		this.allDepartment = allDepartment;
	}

	public List<Long> getDepartmentIds() {
		return departmentIds;
	}

	public void setDepartmentIds(List<Long> departmentIds) {
		this.departmentIds = departmentIds;
	}

	public void setUserInfoIds(Long userInfoIds) {
		this.userInfoIds = userInfoIds;
	}

	public List<Role> getAllRoles() {
		return allRoles;
	}
  
	public List<Workgroup> getAllWorkGroup() {
		return allWorkGroup;
	}

	public void setWorkGroupIds(List<Long> workGroupIds) {
		this.workGroupIds = workGroupIds;
	}

	public void setAllRoles(List<Role> allRoles) {
		this.allRoles = allRoles;
	}

	public List<Long> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}

	public List<Long> getCheckedRoleIds() {
		return checkedRoleIds;
	}

	public void setCheckedRoleIds(List<Long> checkedRoleIds) {
		this.checkedRoleIds = checkedRoleIds;
	}

	public void setEntity(UserInfo entity) {
		this.entity = entity;
	}

	
	public void prepareListRoles() throws Exception {
		entity = userInfoManager.getUserInfoById(userId);
	}
	
	public void prepareRemoveRoles() throws Exception {
		entity = userInfoManager.getUserInfoById(userId);
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public Page<Department> getPageUserToDepart() {
		return pageUserToDepart;
	}

	public void setPageUserToDepart(Page<Department> pageUserToDepart) {
		this.pageUserToDepart = pageUserToDepart;
	}

	public String getUserByDepartment() throws Exception{
		if(departmentId != null){
			//passWordOverdueIds = userInfoManager.getPassWordOverdueId(page.getResult());
			//passwordOverNoticeId = userInfoManager.passwordOverNotice(page.getResult());
			if(page.getPageSize() <= 1){
				return SUCCESS; 
			}else{
				ApiFactory.getBussinessLogService().log("用户管理", 
						"查看用户列表",ContextUtils.getSystemId("acs"));
				page = userInfoManager.queryUsersByDepartment(page, departmentId);
				renderHtml(PageUtils.pageToJson(page));
				return null;
			}
			
		}else{
			search();
		}
		return SUCCESS;
	}
	
	public String getUserByCompany()throws Exception{
		if(page.getPageSize() <= 1){
			return SUCCESS; 
		}else{
			page = userInfoManager.queryUsersByCompany(page, companyManager.getCompanyId());
			renderHtml(PageUtils.pageToJson(page));
			return null;
		}
	}
	
	/**
	 * 检测用户名是否注册
	 * @return
	 * @throws Exception
	 */
	public String checkUserName() throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		String userName = request.getParameter("userName");
		UserInfo ui = userInfoManager.checkUserName(userName);
		if(ui==null){
			this.renderText("true");
		}else{
			this.renderText(userName);
		}
		return null;
	}
	
	public String getUserByWorkGroup() throws Exception{
		look = "look";
		fromWorkgroup = "fromWorkgroup";
		if(page.getPageSize() <= 1){
			return SUCCESS; 
		}else{
			if(workGroupId != null){
				ApiFactory.getBussinessLogService().log("用户管理", 
						"查看用户列表",ContextUtils.getSystemId("acs"));
				page = userInfoManager.queryUsersByWorkGroup(page, workGroupId);
			}
			renderHtml(PageUtils.pageToJson(page));
			return null;
		}
	}
	
	private Long workGroupId;
	private Long departmentId;

	public Long getWorkGroupId() {
		return workGroupId;
	}

	public void setWorkGroupId(Long workGroupId) {
		this.workGroupId = workGroupId;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public String getPassWord_CreateTime() {
		return passWord_CreateTime;
	}

	public void setPassWord_CreateTime(String passWord_CreateTime) {
		this.passWord_CreateTime = passWord_CreateTime;
	}

	public List<Long> getPassWordOverdueIds() {
		return passWordOverdueIds;
	}

	public void setPassWordOverdueIds(List<Long> passWordOverdueIds) {
		this.passWordOverdueIds = passWordOverdueIds;
	}

	public Map<Long, Integer> getPasswordOverNoticeId() {
		return passwordOverNoticeId;
	}

	public void setPasswordOverNoticeId(Map<Long, Integer> passwordOverNoticeId) {
		this.passwordOverNoticeId = passwordOverNoticeId;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	public List<BusinessSystem> getSystems() {
		return systems;
	}
	
	public void setSystems(List<BusinessSystem> systems) {
		this.systems = systems;
	}
	
	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}

	public SubsciberManager getSubsciberManager() {
		return subsciberManager;
	}
	@Required
	public void setSubsciberManager(SubsciberManager subsciberManager) {
		this.subsciberManager = subsciberManager;
	}

	
	public String getStates() {
		return states;
	}

	public void setStates(String states) {
		this.states = states;
	}

	public Long getCompanyId() {
		return ContextUtils.getCompanyId();
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public void setHistoryUserName(String historyUserName) {
		this.historyUserName = historyUserName;
	}

	public String getSynchronousLdapMessage() {
		return synchronousLdapMessage;
	}

	public void setSynchronousLdapMessage(String synchronousLdapMessage) {
		this.synchronousLdapMessage = synchronousLdapMessage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<UserInfo> getUserInfos() {
		return userInfos;
	}

	public void setUserInfos(List<UserInfo> userInfos) {
		this.userInfos = userInfos;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getDids() {
		return dids;
	}

	public void setDids(String dids) {
		this.dids = dids;
	}


	public String getDeId() {
		return deId;
	}

	public void setDeId(String deId) {
		this.deId = deId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDepIds() {
		return depIds;
	}

	public void setDepIds(String depIds) {
		this.depIds = depIds;
	}

	public String getUsersId() {
		return usersId;
	}


	public String getLookId() {
		return lookId;
	}

	public void setLookId(String lookId) {
		this.lookId = lookId;
	}

	public String getLook() {
		return look;
	}

	public void setLook(String look) {
		this.look = look;
	}

	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getLooked() {
		return looked;
	}

	public void setLooked(String looked) {
		this.looked = looked;
	}

	public String getOldDid() {
		return oldDid;
	}

	public void setOldDid(String oldDid) {
		this.oldDid = oldDid;
	}

	public String getOldType() {
		return oldType;
	}

	public void setOldType(String oldType) {
		this.oldType = oldType;
	}

	public String getOlDid() {
		return olDid;
	}

	public void setOlDid(String olDid) {
		this.olDid = olDid;
	}

	public String getOlType() {
		return olType;
	}

	public void setOlType(String olType) {
		this.olType = olType;
	}

	public String getPassWordChange() {
		return passWordChange;
	}

	public void setPassWordChange(String passWordChange) {
		this.passWordChange = passWordChange;
	}

	public String getDepartmId() {
		return departmId;
	}

	public void setDepartmId(String departmId) {
		this.departmId = departmId;
	}

	public String getDepartmType() {
		return departmType;
	}

	public void setDepartmType(String departmType) {
		this.departmType = departmType;
	}

	public String getEdit() {
		return edit;
	}

	public void setEdit(String edit) {
		this.edit = edit;
	}

	public String getEdited() {
		return edited;
	}

	public void setEdited(String edited) {
		this.edited = edited;
	}
	public void setOraginalPassword(String oraginalPassword) {
		this.oraginalPassword = oraginalPassword;
	}
	public String getOraginalPassword() {
		return oraginalPassword;
	}
	public Boolean getIsPasswordChange() {
		return isPasswordChange;
	}
	public void setLevelpassword(String levelpassword) {
		this.levelpassword = levelpassword;
	}
	public void setFile(File file) {
		this.file = file;
	}
	
	public void setFileFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getOneDid() {
		return oneDid;
	}

	public void setOneDid(String oneDid) {
		this.oneDid = oneDid;
	}

	public String getMainDepartmentName() {
		return mainDepartmentName;
	}

	public void setMainDepartmentName(String mainDepartmentName) {
		this.mainDepartmentName = mainDepartmentName;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getComy() {
		return comy;
	}

	public void setComy(String comy) {
		this.comy = comy;
	}

	public String getFromWorkgroup() {
		return fromWorkgroup;
	}

	public void setFromWorkgroup(String fromWorkgroup) {
		this.fromWorkgroup = fromWorkgroup;
	}

	public String getFromChangeMainDepartment() {
		return fromChangeMainDepartment;
	}

	public void setFromChangeMainDepartment(String fromChangeMainDepartment) {
		this.fromChangeMainDepartment = fromChangeMainDepartment;
	}

	public Long getNewMainDepartmentId() {
		return newMainDepartmentId;
	}

	public void setNewMainDepartmentId(Long newMainDepartmentId) {
		this.newMainDepartmentId = newMainDepartmentId;
	}
}
