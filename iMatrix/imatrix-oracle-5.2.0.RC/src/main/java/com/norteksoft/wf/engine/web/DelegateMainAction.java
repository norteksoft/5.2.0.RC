package com.norteksoft.wf.engine.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.product.api.entity.Role;
import com.norteksoft.product.api.entity.Department;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.acs.web.authorization.JsTreeUtil1;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.base.enumeration.TrustRecordState;
import com.norteksoft.wf.engine.entity.TrustRecord;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.service.DelegateMainManager;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;



@Namespace("/engine")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "delegate-main", type = "redirectAction")})
public class DelegateMainAction extends CrudActionSupport<TrustRecord>{
	private static final long serialVersionUID = 1L;
	

	private Long id;
	private List<Long> ids;
	private String currentId;
	private TrustRecord delegateMain;
	private TaskService taskService;
	private Page<TrustRecord> page = new Page<TrustRecord>(0,true);
	private Page<TrustRecord> receivePage = new Page<TrustRecord>(0,true);
	private Page<WorkflowTask> tasks = new Page<WorkflowTask>(0,true);
	private DelegateMainManager delegateMainManager;
	private WorkflowDefinitionManager workflowDefinitionManager;
	private List<WorkflowDefinition> workflowDefinitions;    //流程
 	private List<String> taskNames;            //环节
	private String processDefinitionId;
	private UserManager userManager;
	private String processId;
	private String task;
	private java.sql.Date bTime;
	private java.sql.Date eTime;
	private Integer flag;
	private WorkflowTask delegate;
	private List<Long> rolesIds;
	private short style;
	private String addOrEdit;
	private boolean needStart = false;
	private String deleteIds;
	
	@Override
	public String delete() throws Exception {
		int[] result = delegateMainManager.deleteDelegateMains(deleteIds);
		StringBuilder message = new StringBuilder();
		if(result[0]!=0){
			message.append(result[0]).append("个已删除");
		}
		if(result[0]!=0&&result[1]!=0)message.append(",");
		if(result[1]!=0){
			message.append(result[1]).append("个为启用中不能删除");
		}
		ApiFactory.getBussinessLogService().log("委托", 
				"删除委托", 
				ContextUtils.getSystemId("wf"));
		this.addSuccessMessage(message.toString());
		return list();
	}

	@Override
	public String input() throws Exception {
		ApiFactory.getBussinessLogService().log("委托", 
				"委托表单页面", 
				ContextUtils.getSystemId("wf"));
		if(id==null||delegateMain.getState()==TrustRecordState.NEW_CREATING||needStart){
			processDefinitionId=delegateMain.getProcessId();
			workflowDefinitions=workflowDefinitionManager.getAllActiveDefinition();
			if(StringUtils.isNotEmpty(delegateMain.getProcessId())&&!"0".equals(delegateMain.getProcessId())){
				taskNames=workflowDefinitionManager.getTaskNames(delegateMain.getProcessId());
			}
			return INPUT;
		}else{
			this.addErrorMessage("启用后的委托不能修改");
			return list();
		}
	}
	
	public void prepareView() throws Exception {
		if(id==null){
			delegateMain = new TrustRecord();
		}else{
			delegateMain = delegateMainManager.getDelegateMain(id);
		}
		
	}
	//查看页面
	public String view() throws Exception {
		delegateMain.setTrustorName(userManager.getUserByLoginName(delegateMain.getTrustor()).getName());
		ApiFactory.getBussinessLogService().log("委托", 
				"查看委托", 
				ContextUtils.getSystemId("wf"));
		return "view";
	}
	
	
	public void prepareViewReceive() throws Exception {
		if(id==null){
			delegateMain = new TrustRecord();
		}else{
			delegateMain = delegateMainManager.getDelegateMain(id);
		}
		
	}
	//查看页面
	public String viewReceive() throws Exception {
		delegateMain.setTrustorName(userManager.getUserByLoginName(delegateMain.getTrustor()).getName());
		ApiFactory.getBussinessLogService().log("委托", 
				"查看受托", 
				ContextUtils.getSystemId("wf"));
		return "viewReceive";
	}

	@Override
	@Action("delegate-main")
	public String list() throws Exception {
		if(page.getPageSize()>1){
			delegateMainManager.getPageDelegateMain(page);
			ApiFactory.getBussinessLogService().log("委托", 
					"委托列表", 
					ContextUtils.getSystemId("wf"));
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return SUCCESS;
	}
	
	/**
	 * 我接受的委托
	 * @return
	 * @throws Exception
	 */
	public String receive() throws Exception{
		if(receivePage.getPageSize()>1){
			delegateMainManager.getReceiveDelegate(receivePage);
			List<TrustRecord> result=new ArrayList<TrustRecord>();
			List<TrustRecord> list=receivePage.getResult();
			for(TrustRecord delegateMain:list){
				delegateMain.setTrustorName(userManager.getUserByLoginName(delegateMain.getTrustor()).getName());
				result.add(delegateMain);
			}
			receivePage.setResult(result);
			ApiFactory.getBussinessLogService().log("委托", 
					"受托列表", 
					ContextUtils.getSystemId("wf"));
			this.renderText(PageUtils.pageToJson(receivePage));
			return null;
		}
		return "receive";
	}
	
	static int MY_DELEGATE = 0;
	static int REVEIVE_DELEGATE = 1;

	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			delegateMain = new TrustRecord();
			delegateMain.setState(TrustRecordState.NEW_CREATING);
		}else{
			delegateMain = delegateMainManager.getDelegateMain(id);
		}
		
	}
	
	/**
	 * 获得当前日期，精确到日
	 * @return
	 */
	public  Date getDate(Date date){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	
	private int validateDelegateDate(TrustRecord delegateMain){
		Date currentDate = getDate(new Date());
		Date beginDate = getDate(delegateMain.getBeginTime());
		if(beginDate.getTime()<currentDate.getTime()){
			return 1;
		}else if(delegateMain.getEndTime().getTime()<delegateMain.getBeginTime().getTime()){
			return 2;
		}
		return 0;
	}

	@Override
	public String save() throws Exception {
		
		if(validateDelegateDate(delegateMain)==0){
			if(!ContextUtils.getLoginName().equals(delegateMain.getTrustee())){
				delegateMain.setCompanyId(ContextUtils.getCompanyId());
				delegateMain.setCreatedTime(new Date());
				delegateMain.setTrustor(ContextUtils.getLoginName());
				switch(delegateMain.getStyle()){
				case 1: 
					delegateMain.setName(workflowDefinitionManager.getWorkflowDefinitionByProcessId(delegateMain.getProcessId()).getName());
					break;
				case 2: 
					delegateMain.setName("全权委托");
					break;
				case 3:
					delegateMain.setName("权限委托");
					StringBuilder roleNames = new StringBuilder();
					StringBuilder roleIds = new StringBuilder();
					if(rolesIds!=null){
						for(int i=0;i<rolesIds.size();i++){
							Role role=ApiFactory.getAcsService().getRoleById(rolesIds.get(i));
							roleNames.append(role.getName()).append("(").append(role.getBusinessSystem().getName()).append(")");
							roleIds.append(role.getId());
							if(i<rolesIds.size()-1){
								roleIds.append(",");
								roleNames.append(",");
							}
						}
					}else{
						this.addErrorMessage("请选择一个角色后再保存");
						ApiFactory.getBussinessLogService().log("委托", 
								"保存委托", 
								ContextUtils.getSystemId("wf"));
						//不能执行保存方法，直接返回
						return input();
					}
					delegateMain.setSelectedRoleNames(roleNames.toString());
					delegateMain.setRoleIds(roleIds.toString());
					break;
				}
				delegateMainManager.saveDelegateMain(delegateMain);
				this.addSuccessMessage("保存成功");
				if(needStart){
					this.clearMessages();
					this.addActionMessage(delegateMainManager.startDelegateMain(delegateMain.getId()));
				}
			}else{
				this.addErrorMessage("不能委托给自己");
			}
		}else{
			if(validateDelegateDate(delegateMain)==2){
			    this.addErrorMessage("委托截止日期不能早于生效日期");
			}else if(validateDelegateDate(delegateMain)==1){
				this.addErrorMessage("委托生效时间须大于等于当前时间");
			}
		}
		ApiFactory.getBussinessLogService().log("委托", 
				"保存委托", 
				ContextUtils.getSystemId("wf"));
		return input();
	}
	public void prepareStart() throws Exception{
	}
	public String start() throws Exception {
		if(ids.size()==1){
			this.addActionMessage(delegateMainManager.startDelegateMain(ids.get(0)));
		}else if(ids.size()==0){
			this.addErrorMessage("请选择一个委托");
		}else{
			this.addErrorMessage("只能选择一个委托");
		}
		ApiFactory.getBussinessLogService().log("委托", 
				"启用委托", 
				ContextUtils.getSystemId("wf"));
		return list();
	}
	
	public void prepareEnd() throws Exception{
	}
	public String end() throws Exception {
		if(ids.size()==1){
			delegateMain = delegateMainManager.getDelegateMain(ids.get(0));
			this.addActionMessage(delegateMainManager.endDelegateMain(ids.get(0)));
		}else if(ids.size()==0){
			this.addErrorMessage("请选择一个委托");
		}else{
			this.addErrorMessage("只能选择一个委托");
		}
		ApiFactory.getBussinessLogService().log("委托", 
				"取消委托", 
				ContextUtils.getSystemId("wf"));
		return list();
	}
	
	/**
	 * 判断某日前是否在两个日期之间
	 * @param date1
	 * @param date2
	 * @return
	 */
	
	public void prepareGetLink() throws Exception {
		prepareModel();
	}
	//得到环节
	public String getLink() throws Exception{
		workflowDefinitions=workflowDefinitionManager.getAllActiveDefinition();
		taskNames=workflowDefinitionManager.getTaskNames(processDefinitionId);
		StringBuilder tree = new StringBuilder();
		tree.append("[");
		for (String taskName : taskNames) {
			tree.append(JsTreeUtil1.generateJsTreeNodeNew(taskName , "", taskName,"folder")).append(",");
		}
		renderText(tree.toString().substring(0, tree.length()-1)+"]");
		return null;
	}
	
	//人员树的页面
	public String tree()throws Exception {
		return "tree";
	}

	//人员树
	public String createManTree()throws Exception {
		StringBuilder tree = null;
		List<com.norteksoft.product.api.entity.Workgroup> workGroups = ApiFactory.getAcsService().getWorkgroups();
		List<Department> departments = ApiFactory.getAcsService().getDepartments();
		List<User> usersList = ApiFactory.getAcsService().getUsersWithoutDepartment();
		if (currentId.equals("0")) {
			tree = new StringBuilder();
			tree.append("[");
			tree.append("{attributes:{id:\"company\"},state:\"open\",data:\""+ContextUtils.getCompanyName()+ "\",children:[");
			if (workGroups != null && workGroups.size() > 0) {
				for (int i = 0; i < workGroups.size(); i++) {
					com.norteksoft.product.api.entity.Workgroup workGroup = workGroups.get(i);
					List<User> users = ApiFactory.getAcsService()
							.getUsersByWorkgroupId(workGroup.getId());
					if (departments != null && departments.size() > 0) {
						if (users != null && users.size() > 0) {
							tree.append("{attributes:{id:\"workGroup_"
									+ workGroup.getId()
									+ "\"},state:\"closed\",data:\""
									+ workGroup.getName() + "\"},");
						} else {
							tree.append("{attributes:{id:\"workGroup_"
									+ workGroup.getId() + "\"},data:\""
									+ workGroup.getName() + "\"},");
						}
					} else {
						if (i == workGroups.size() - 1) {
							if (users != null && users.size() > 0) {
								tree.append("{attributes:{id:\"workGroup_"
										+ workGroup.getId()
										+ "\"},state:\"closed\",data:\""
										+ workGroup.getName() + "\"}");
							} else {
								tree.append("{attributes:{id:\"workGroup_"
										+ workGroup.getId() + "\"},data:\""
										+ workGroup.getName() + "\"}");
							}
						} else {
							if (users != null && users.size() > 0) {
								tree
										.append("{attributes:{id:\"workGroup_"
												+ workGroup.getId()
												+ "\"},state:\"closed\",data:\""
												+ workGroup.getName()
												+ "\"},");
							} else {
								tree
										.append("{attributes:{id:\"workGroup_"
												+ workGroup.getId()
												+ "\"},data:\""
												+ workGroup.getName()
												+ "\"},");
							}
						}
					}
				}
			}
			if(departments!=null&&departments.size()>0){
				for (int i = 0; i < departments.size(); i++) {
					Department department = departments.get(i);
					List<Department> childer = ApiFactory.getAcsService().getSubDepartmentList(department.getId());
					List<User> users = ApiFactory.getAcsService()
							.getUsersByDepartmentId(department.getId());
					if (usersList != null && usersList.size() > 0) {
						if (childer != null && childer.size() > 0
								|| users != null && users.size() > 0) {
							tree.append("{attributes:{id:\"department_"
									+ department.getId()
									+ "\"},state:\"closed\",data:\""
									+ department.getName() + "\"},");
						} else {
							tree.append("{attributes:{id:\"department_"
									+ department.getId() + "\"},data:\""
									+ department.getName() + "\"},");
						}
					} else {
						if (i == departments.size() - 1) {
							if (childer != null && childer.size() > 0
									|| users != null && users.size() > 0) {
								tree.append("{attributes:{id:\"department_"
										+ department.getId()
										+ "\"},state:\"closed\",data:\""
										+ department.getName()
										+ "\"}");
							} else {
								tree.append("{attributes:{id:\"department_"
										+ department.getId() + "\"},data:\""
										+ department.getName()
										+ "\"}");
							}
						} else {
							if (childer != null && childer.size() > 0
									|| users != null && users.size() > 0) {
								tree.append("{attributes:{id:\"department_"
										+ department.getId()
										+ "\"},state:\"closed\",data:\""
										+ department.getName()
										+ "\"},");
							} else {
								tree.append("{attributes:{id:\"department_"
										+ department.getId() + "\"},data:\""
										+ department.getName()
										+ "\"},");
							}
						}
					}
				}
			}
			if(usersList!=null&&usersList.size()>0){
				for (int i = 0; i < usersList.size(); i++) {
					User user = usersList.get(i);
					if (i == usersList.size() - 1) {
						tree.append("{attributes:{id:\"" + user.getId()+"="+user.getName()
								+ "\"},data:\"" + user.getName() + "\"}");
					} else {
						tree.append("{attributes:{id:\"" + user.getId()
								+ "\"},data:\"" + user.getName() + "\"},");
					}
				}
			}
			tree.append("]}");
			tree.append("]");
		} else {
			tree = new StringBuilder();
			String[] str = currentId.split("_");
			if (str[0].equals("workGroup")) {
				List<User> users = ApiFactory.getAcsService().getUsersByWorkgroupId(Long
						.parseLong(str[1]));
				tree.append("[");
				if (users != null && users.size() > 0) {
					for (int i = 0; i < users.size(); i++) {
						User user = users.get(i);
						if (i == users.size() - 1) {
							tree.append("{attributes:{id:\"" + user.getId()+"="+user.getName()
									+ "\"},data:\"" + user.getName()
									+ "\"}");
						} else {
							tree.append("{attributes:{id:\"" + user.getId()+"="+user.getName()
									+ "\"},data:\"" + user.getName()
									+ "\"},");
						}
					}
				}
				tree.append("]");
			}
			if (str[0].equals("department")) {
				tree.append("[");
				List<Department> childer = ApiFactory.getAcsService()
						.getSubDepartmentList(Long.parseLong(str[1]));
				List<User> users = ApiFactory.getAcsService().getUsersByDepartmentId(Long
						.parseLong(str[1]));
				if (users != null && users.size() > 0) {
					for (int i = 0; i < users.size(); i++) {
						User user = users.get(i);
						if (i == users.size() - 1) {
							if (childer != null && childer.size() > 0) {
								tree.append("{attributes:{id:\"" + user.getId()+"="+user.getName()
										+ "\"},data:\"" + user.getName()
										+ "\"},");
							} else {
								tree.append("{attributes:{id:\"" + user.getId()+"="+user.getName()
										+ "\"},data:\"" + user.getName()
										+ "\"}");
							}
						} else {
							tree.append("{attributes:{id:\"" + user.getId()+"="+user.getName()
									+ "\"},data:\"" + user.getName()
									+ "\"},");
						}
					}
				}
				if (childer != null && childer.size() > 0) {
					for (int i = 0; i < childer.size(); i++) {
						Department department = childer.get(i);
						List<User> users1 = ApiFactory.getAcsService()
								.getUsersByDepartmentId(department.getId());
						if (i == childer.size() - 1) {
							if (users1 != null && users1.size() > 0) {
								tree.append("{attributes:{id:\"department_"
										+ department.getId()
										+ "\"},state:\"closed\",data:\""
										+ department.getName()
										+ "\"}");
							}
							// 如果子部门下没有人员，则不显示(可以将下面代码注释)
							else {
								tree.append("{attributes:{id:\"department_"
										+ department.getId() + "\"},data:\""
										+ department.getName()
										+ "\"}");
							}
						} else {
							if (users1 != null && users1.size() > 0) {
								tree.append("{attributes:{id:\"department_"
										+ department.getId()
										+ "\"},state:\"closed\",data:\""
										+ department.getName()
										+ "\"},");
							}
							// 如果子部门下没有人员，则不显示(可以将下面代码注释)
							else {
								tree.append("{attributes:{id:\"department_"
										+ department.getId() + "\"},data:\""
										+ department.getName()
										+ "\"},");
							}
						}
					}
				}

				tree.append("]");
			}
		}
		renderText(tree.toString());
		return null;
	}
	
	private String isEnd;
	
	public void setIsEnd(String isEnd) {
		this.isEnd = isEnd;
	}
	
	private Boolean isDone=false;

	public void setIsDone(Boolean isDone) {
		this.isDone = isDone;
	}

	public Boolean getIsDone() {
		return isDone;
	}

	//我的委托
	public String myDelegate()throws Exception{
		if(tasks.getPageSize()>1){
			if(isEnd == null || isEnd.length() == 0){
				tasks = taskService.getDelegateTasksByActive(ContextUtils.getCompanyId(), tasks, ContextUtils.getLoginName(),false);
			}else{
				tasks = taskService.getDelegateTasksByActive(
						ContextUtils.getCompanyId(), tasks, ContextUtils.getLoginName(), Boolean.valueOf(isEnd));
			}
			ApiFactory.getBussinessLogService().log("委托", 
					"任务委托监控列表", 
					ContextUtils.getSystemId("wf"));
			this.renderText(PageUtils.pageToJson(tasks));
			return null;
		}
		return "delegate";
	}
	
	//我的受托
	public String superviseAsTrustee()throws Exception{
		if(tasks.getPageSize()>1){
			tasks = taskService.getTaskAsTrustee(
					ContextUtils.getCompanyId(), tasks, ContextUtils.getLoginName(), isDone);
			ApiFactory.getBussinessLogService().log("委托", 
					"任务受托监控列表", 
					ContextUtils.getSystemId("wf"));
			this.renderText(PageUtils.pageToJson(tasks));
			return null;
		}
		return "trusteeview";
	}
	
	public void prepareMyDelegateInput() throws Exception{
		delegate=taskService.getWorkflowTask(id);
		delegate.setExpands(userManager.getUserByLoginName(delegate.getTransactor()).getName());
	}

	//我的委托查看
	public String myDelegateInput()throws Exception{
		return "delegate-input";
	}
	/**
	 * 获取某人的所有角色
	 * @return
	 * @throws Exception
	 */
	public String getRolesByUser() throws Exception{
		StringBuilder htm = new StringBuilder();
		List<Role> roles = ApiFactory.getAcsService().getRolesExcludeTrustedRole(ContextUtils.getUserId());
		htm.append("<ul>");
		for(Role role : roles){
			BusinessSystem businessSystem = role.getBusinessSystem();
			StringBuilder roleName = new StringBuilder(role.getName());
			if(businessSystem!=null){
				roleName.append("(");
				roleName.append(businessSystem.getName());
				roleName.append(")");
			}
			htm.append("<li>");
			if(id==null){
				htm.append("<input type=\"checkbox\"  name=\"rolesIds\" value=\""+role.getId()+"\"/>");
			}else{
				delegateMain = delegateMainManager.getDelegateMain(id);
				if(checkItemIsExsit(role.getId().toString(),delegateMain.getRoleIds())){
					htm.append("<input type=\"checkbox\"  name=\"rolesIds\"  checked=\"checked\" value=\""+role.getId()+"\"/>");
				}else{
					htm.append("<input type=\"checkbox\"  name=\"rolesIds\"  value=\""+role.getId()+"\"/>");
				}
			}
			htm.append(roleName);
			htm.append("</li>");
		}
		htm.append("</ul>");
		renderText(htm.toString());
		return null;
	}
	

	
	/**
	 * 检测某个元素是否在数组中
	 * @param roleId
	 * @param ids
	 * @return
	 */
	private boolean checkItemIsExsit(String roleId,String rolesIds){
		if(StringUtils.isNotEmpty(rolesIds)){
			String[] rolesIdArray = rolesIds.split(",");
			for(String id:rolesIdArray){
				if(roleId.equals(id))return true;
			}
			return false;
		}else{
			return false;
		}
	}
	public TrustRecord getModel() {
		return delegateMain;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Page<TrustRecord> getPage() {
		return page;
	}
	
	public List<WorkflowDefinition> getWorkflowDefinitions() {
		return workflowDefinitions;
	}

	public void setWorkflowDefinitions(List<WorkflowDefinition> workflowDefinitions) {
		this.workflowDefinitions = workflowDefinitions;
	}

	public String getCurrentId() {
		return currentId;
	}

	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public List<String> getTaskNames() {
		return taskNames;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public void setTaskNames(List<String> taskNames) {
		this.taskNames = taskNames;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public java.sql.Date getBTime() {
		return bTime;
	}

	public void setBTime(java.sql.Date time) {
		bTime = time;
	}

	public java.sql.Date getETime() {
		return eTime;
	}

	public void setETime(java.sql.Date time) {
		eTime = time;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	
	public void setTasks(Page<WorkflowTask> tasks) {
		this.tasks = tasks;
	}
	
	public Page<WorkflowTask> getTasks() {
		return tasks;
	}
	public WorkflowTask getDelegate() {
		return delegate;
	}

	public void setDelegate(WorkflowTask delegate) {
		this.delegate = delegate;
	}

	public Page<TrustRecord> getReceivePage() {
		return receivePage;
	}

	public void setReceivePage(Page<TrustRecord> receivePage) {
		this.receivePage = receivePage;
	}


	@Required
	public void setDelegateMainManager(DelegateMainManager delegateMainManager) {
		this.delegateMainManager = delegateMainManager;
	}
	
	@Required
	public void setWorkflowDefinitionManager(
			WorkflowDefinitionManager workflowDefinitionManager) {
		this.workflowDefinitionManager = workflowDefinitionManager;
	}
	
	@Required
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	
	@Required
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	public void setRolesIds(List<Long> rolesIds) {
		this.rolesIds = rolesIds;
	}

	public short getStyle() {
		return style;
	}

	public void setStyle(short style) {
		this.style = style;
	}

	public String getAddOrEdit() {
		return addOrEdit;
	}

	public void setAddOrEdit(String addOrEdit) {
		this.addOrEdit = addOrEdit;
	}

	public String getIsEnd() {
		return isEnd;
	}
	public void setNeedStart(boolean needStart) {
		this.needStart = needStart;
	}

	public String getDeleteIds() {
		return deleteIds;
	}

	public void setDeleteIds(String deleteIds) {
		this.deleteIds = deleteIds;
	}

	private void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	private void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
}
