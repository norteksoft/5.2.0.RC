package com.norteksoft.acs.web.organization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.acs.service.organization.UserInfoManager;
import com.norteksoft.acs.service.organization.WorkGroupManager;
import com.norteksoft.acs.web.eunms.AddOrRomoveState;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PageUtils;

/**
 * author 李洪超 
 * version 创建时间：2009-3-11 上午09:51:10 
 * 工作组管理Action
 */
@SuppressWarnings("deprecation")
@Namespace("/organization")
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "work-group", type = "redirectAction") })
public class WorkGroupAction extends CRUDActionSupport<Workgroup> {

	private static final long serialVersionUID = 4814560124772644966L;
	private WorkGroupManager workGroupManager;
	private CompanyManager companyManager;
	private UserInfoManager userInfoManager;
	private Page<Workgroup> page = new Page<Workgroup>(0, true);// 每页5项，自动查询计算总页数.
	private Page<User> userPage = new Page<User>(0, true);// 每页5项，自动查询计算总页数.
	private Page<Role> rolePage = new Page<Role>(20, true);// 每页5项，自动查询计算总页数.
	private Workgroup workGroup;
	private Long id;
	private Long companyId;
	private Long workGroupId;
	private String workGroupIdStr;
	private Long parentId;
	private List<Workgroup> allWorkGroup;
	private String workGroupName;
	private String workGroupCode;
	private List<Long> userIds;
	private List<Long> checkedRoleIds;
	private List<Long> roleIds;
	private Integer isAddOrRomove;
	private List<BusinessSystem> systems;
	private BusinessSystemManager businessSystemManager;
	private List<Role> roleList;
	private String ids;
	private String prems1;
	private String ides;
	private String wfType;
	private String comeFrom;
	
	/**
	 * 验证工作组名称唯一性
	 */
	public String checkWorkName() throws Exception{
		boolean workGroup = false;
		if(id!=null){
			workGroup = workGroupManager.checkWorkName(workGroupName,id);
		}else{
			workGroup = workGroupManager.checkWorkName(workGroupName);
		}
		if(workGroup == false){
			this.renderText("true");
		}else{
			this.renderText(workGroupName);
		}
		return null;
	}
	
	/**
	 * 验证工作组编号唯一性
	 * liudongxia
	 */
	public String checkWorkCode() throws Exception{
		boolean workGroup = false;
		if(id!=null){
			workGroup = workGroupManager.checkWorkCode(workGroupCode,id);
		}else{
			workGroup = workGroupManager.checkWorkCode(workGroupCode);
		}
		if(workGroup == false){
			this.renderText("true");
		}else{
			this.renderText(workGroupCode);
		}
		return null;
	}
	
	/**
	 * 新建工作组
	 */
	public String inputWorkGroup() throws Exception{
		companyId = companyManager.getCompanyId();
		ApiFactory.getBussinessLogService().log("工作组管理", 
				"新建工作组",ContextUtils.getSystemId("acs"));
		return "input";
	}
	
	/**
	 * 保存新建工作组
	 */
	
	public void prepareSaveWorkGroup() throws Exception {
		prepareModel();
	}
	
	/**
	 * 保存新建工作组信息
	 */
	public String saveWorkGroup() throws Exception{
		boolean logSign=true;//该字段只是为了标识日志信息：true表示新建工作组、false表示修改工作组
		if(id==null){
			Company company = companyManager.getCompany(companyId); 
			workGroup.setCompany(company);
			workGroupManager.saveWorkGroup(workGroup);
		}else{
			workGroupManager.saveWorkGroup(workGroup);
			logSign=false;
		}
		
		if(logSign){
			ApiFactory.getBussinessLogService().log("工作组管理", 
					"新建工作组："+workGroup.getName(),ContextUtils.getSystemId("acs"));
		}else{
			ApiFactory.getBussinessLogService().log("工作组管理", 
					"修改工作组："+workGroup.getName(),ContextUtils.getSystemId("acs"));
		}
		 return RELOAD;
	}
	
	public String checkSubmit()throws Exception{
		boolean nameFlag = workGroupManager.checkWorkName(workGroupName,id);
		boolean codeFlag = workGroupManager.checkWorkCode(workGroupCode,id);
		if(!nameFlag && !codeFlag){
			this.renderText("true");
		}else{
			if(nameFlag){
				this.renderText("name-false");
			}else{
				this.renderText("code-false");
			}
		}
		return null;
	}

	
	@Override
	public String delete() throws Exception {
		String logSign="";//该字段只是为了标识日志信息：工作组名称
		String[] str=ides.split(",");
		for(String sid:str){
			workGroup = workGroupManager.getWorkGroup(Long.valueOf(sid));
			if(StringUtils.isNotEmpty(logSign)){
				logSign+=",";
			}
			logSign+=workGroup.getName();
			workGroupManager.deleteWorkGroup(Long.valueOf(sid));
		}
		ApiFactory.getBussinessLogService().log("工作组管理", 
				"删除工作组:"+logSign,ContextUtils.getSystemId("acs"));
     	return null;
	}

	@Override
	@Action("work-group")
	public String list() throws Exception {
		if(page.getPageSize() <= 1){
			return SUCCESS; 
		}else{
			page = workGroupManager.getAllWorkGroup(page);
			ApiFactory.getBussinessLogService().log("工作组管理", 
					"查看工作组列表",ContextUtils.getSystemId("acs"));
			renderHtml(PageUtils.pageToJson(page));
			return null;
		}
	}

	/**
	 * 按条件查询
	 * 
	 * @return
	 */
	public void prepareSearch() throws Exception {
		prepareModel();
	}
	
	public String search() throws Exception {

		page = workGroupManager.getSearchWorkGroup(page, workGroup, false);
		return SUCCESS;

	}

	public void prepareSaveUser() throws Exception {
		prepareModel();
	}

	public void prepareAddWorkGroupToUsers() throws Exception {
		workGroup = workGroupManager.getWorkGroup(workGroupId);
	}

	 /**
     * 跳转到工作组添加人员页面
     */
	public String addWorkGroupToUsers()throws Exception{
    	isAddOrRomove=AddOrRomoveState.ADD.code;
    	if(userPage.getPageSize() <= 1){
    		return "user-list";
		}else{
		    userPage = workGroupManager.workGroupToUsers(userPage, StringUtils.isEmpty(workGroupIdStr)?workGroupId:Long.parseLong(workGroupIdStr));
			renderHtml(PageUtils.pageToJson(userPage));
			return null;
		}
    }
    
    /**
     * 跳转到工作组移除人员页面
     */
	public void prepareRemoveWorkGroupToUsers() throws Exception {
	}
	
    public String removeWorkGroupToUsers()throws Exception{ 	
    	workGroup = workGroupManager.getWorkGroup(StringUtils.isEmpty(workGroupIdStr)?workGroupId:Long.parseLong(workGroupIdStr));
	    userPage = workGroupManager.workGroupToRomoveUserList(userPage,null,workGroup.getId());
	    isAddOrRomove=AddOrRomoveState.ROMOVE.code;
	    if(userPage.getPageSize() <= 1){
	    	return "user-list";
		}else{
			renderHtml(PageUtils.pageToJson(userPage));
			return null;
		}
    	
    }
    
    /**
     * 工作组添加人员树
     */
    public String addUsersToWorkgroup()throws Exception{
    	return "user-tree";
    }
    
    /**
	 * 保存工作组添加用户
	 * @return
	 * @throws Exception
	 */
    public String workgroupAddUser()throws Exception{
    	workGroupManager.workgroupAddUser(workGroupId, userIds, 0);
    	ApiFactory.getBussinessLogService().log("工作组管理", 
				"保存工作组添加人员",ContextUtils.getSystemId("acs"));
    	return getUserByWorkGroup();
    }
    /**
     * 工作组用户列表
     * @return
     * @throws Exception
     */
    public String getUserByWorkGroup() throws Exception{
		if(userPage.getPageSize() <= 1){
			return "users"; 
		}else{
			if(workGroupId != null){
				userPage = userInfoManager.queryUsersByWorkGroup(userPage, workGroupId);
			}
			renderHtml(PageUtils.pageToJson(userPage));
			return null;
		}
	}
    
    /**
     * 保存工作组移除用户
     * @return
     * @throws Exception
     */
    public String removeWorkgroupToUsers() throws Exception{
    	workGroupManager.workgroupAddUser(workGroupId, userIds, 1);
    	ApiFactory.getBussinessLogService().log("工作组管理", 
				"保存工作组移除人员",ContextUtils.getSystemId("acs"));
    	return getUserByWorkGroup();
    }
    
	/**
	 *工作组添加人员(保存关系)
	 */
	public String workGroupAddUser() throws Exception {
		String[] arr=ids.split(",");
		List<Long> userids=new ArrayList<Long>();
		for(String str:arr){
			userids.add(Long.valueOf(str));
		}
		workGroupManager.workGroupToUser(StringUtils.isEmpty(workGroupIdStr)?workGroupId:Long.parseLong(workGroupIdStr), userids,isAddOrRomove);
		return RELOAD;
	}

	/**
	 * 工作组添加角色
	 */
	public void prepareWorkGroupToRoleList() throws Exception {
		workGroup = workGroupManager.getWorkGroup(workGroupId);
	}
	
	public String workGroupToRoleList() throws Exception {
		systems = businessSystemManager.getAllBusiness();
		checkedRoleIds = workGroupManager.getRoleIds(workGroupId);
		isAddOrRomove=AddOrRomoveState.ADD.code;
		return "role-list";
	}
	
	public void prepareWorkGroupRomoveRoleList() throws Exception {
		workGroup = workGroupManager.getWorkGroup(workGroupId);
	}
	public String workGroupRomoveRoleList() throws Exception {

		systems = businessSystemManager.getAllBusiness();
		roleList = workGroupManager.getRole(workGroupId);
		checkedRoleIds = workGroupManager.getRoleIds(workGroupId);
		isAddOrRomove=AddOrRomoveState.ROMOVE.code;
		return "role-list";
	}
	
	public String workGroupAddRole() throws Exception {
		workGroupManager.workGroupAddRole(workGroupId, roleIds,isAddOrRomove);
		ApiFactory.getBussinessLogService().log("工作组管理", 
				"工作组中添加角色",ContextUtils.getSystemId("acs"));
		return RELOAD;
	}
	@Override
	public String input() throws Exception {
		ApiFactory.getBussinessLogService().log("工作组管理", 
				"修改工作组",ContextUtils.getSystemId("acs"));
		return INPUT;
	}

	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			workGroup = workGroupManager.getWorkGroup(id);
		} else {
			workGroup = new Workgroup();
		}
	}

	@Override
	public String save() throws Exception {
		workGroupManager.saveWorkGroup(workGroup);
		addActionMessage("保存工作组成功");
		ApiFactory.getBussinessLogService().log("工作组管理", 
				"保存工作组信息",ContextUtils.getSystemId("acs"));
		return RELOAD;
	}

	@Required
	public void setCompanyManager(CompanyManager companyManager) {
		this.companyManager = companyManager;
	}

	public Long getCompanyId() {
		return ContextUtils.getCompanyId();
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Page<User> getUserPage() {
		return userPage;
	}

	public void setUserPage(Page<User> userPage) {
		this.userPage = userPage;
	}

	public List<Long> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Long> userIds) {
		this.userIds = userIds;
	}

	public Long getWorkGroupId() {
		return workGroupId;
	}

	public void setWorkGroupId(Long workGroupId) {
		this.workGroupId = workGroupId;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public UserInfoManager getUserInfoManager() {
		return userInfoManager;
	}

	public void setUserInfoManager(UserInfoManager userInfoManager) {
		this.userInfoManager = userInfoManager;
	}

	public void setWorkGroup(Workgroup workGroup) {
		this.workGroup = workGroup;
	}

	public String getWorkGroupName() {
		return workGroupName;
	}

	public void setWorkGroupName(String workGroupName) {
		this.workGroupName = workGroupName;
	}

	public String getWorkGroupCode() {
		return workGroupCode;
	}

	public void setWorkGroupCode(String workGroupCode) {
		this.workGroupCode = workGroupCode;
	}
	
	public Workgroup getModel() {

		return workGroup;
	}

	public Page<Workgroup> getPage() {
		return page;
	}

	public void setPage(Page<Workgroup> page) {
		this.page = page;
	}

	public String temp() throws Exception {
		return SUCCESS;
	}

	@Required
	public void setWorkGroupManager(WorkGroupManager workGroupManager) {
		this.workGroupManager = workGroupManager;
	}

	public List<Workgroup> getAllWorkGroup() {
		return allWorkGroup;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Page<Role> getRolePage() {
		return rolePage;
	}

	public void setRolePage(Page<Role> rolePage) {
		this.rolePage = rolePage;
	}

	public List<Long> getCheckedRoleIds() {
		return checkedRoleIds;
	}

	public void setCheckedRoleIds(List<Long> checkedRoleIds) {
		this.checkedRoleIds = checkedRoleIds;
	}

	public List<Long> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}

	public Integer getIsAddOrRomove() {
		return isAddOrRomove;
	}

	public void setIsAddOrRomove(Integer isAddOrRomove) {
		this.isAddOrRomove = isAddOrRomove;
	}

	public List<BusinessSystem> getSystems() {
		return systems;
	}

	public void setSystems(List<BusinessSystem> systems) {
		this.systems = systems;
	}
	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}

	public List<Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}

	public String getPrems1() {
		return prems1;
	}

	public void setPrems1(String prems1) {
		this.prems1 = prems1;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getIdes() {
		return ides;
	}

	public void setIdes(String ides) {
		this.ides = ides;
	}

	public String getWfType() {
		return wfType;
	}

	public void setWfType(String wfType) {
		this.wfType = wfType;
	}

	public String getComeFrom() {
		return comeFrom;
	}

	public void setComeFrom(String comeFrom) {
		this.comeFrom = comeFrom;
	}
}
