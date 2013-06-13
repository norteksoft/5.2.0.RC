package com.norteksoft.task.web;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.portal.service.IndexManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.task.base.enumeration.TaskCategory;
import com.norteksoft.task.base.enumeration.TaskType;
import com.norteksoft.task.entity.Task;
import com.norteksoft.task.entity.TaskMark;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.task.service.TaskManager;
import com.norteksoft.task.service.WorkflowTaskManager;
import com.norteksoft.task.webservice.TaskWebserviceImpl;
import com.norteksoft.wf.engine.core.DefinitionXmlParse;

@Namespace("/task")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "task", type = "redirectAction")})
public class TaskAction extends CrudActionSupport<Task>{

	private Log log = LogFactory.getLog(TaskAction.class);
	private static final long serialVersionUID = 4658506181455886084L;
	private Task task;
	private Page<Task> tasks = new Page<Task>(0, true);
	private WorkflowTaskManager workflowTaskManager;
	private TaskManager taskManager;
	private UserManager userManager;
	private String transactor;
	private Long id;
	private String ids;
	
	private String loginName;
	private Long companyId;
	private String username;//swing登录名
	private String auto;//swing是否自动登录
	private String password;//swing密码,md5加密后的
	
	private String searchString;
	private String finish;
	private String typeName;
//	private boolean completed = false;
//	private boolean canceled = false;
	private String taskCategory;//任务状态，其值为：active,complete,cancel
	@Autowired
	private AcsUtils acsUtils;
	
	@Autowired
	private IndexManager indexManager;
	
	private TaskMark taskMarks;//标识的颜色
	
	private String taskType;//任务类别：默认类别，流程名称，流程自定义类别
	
	private String currentNodeId;//当前节点id
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private String notificationType;
	private String notificationTheme;
	private String notificationContent;
	private String processId;
	private String transitionName;
	
	private Integer rows = 5;
	
	public void setRows(Integer rows) {
		this.rows = rows;
	}
	
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	private TaskWebserviceImpl taskWebserviceImpl;
	@Required
	public void setTaskWebserviceImpl(TaskWebserviceImpl taskWebserviceImpl) {
		this.taskWebserviceImpl = taskWebserviceImpl;
	}
	
	/**
	 * portal远程链接的action
	 * @return
	 * @throws Exception
	 */
	public String personalTasks() throws Exception{
		List<Object[]> typeInfos=taskManager.getTypeInfos( companyId,loginName);
		Integer taskNum=taskManager.getAllTaskNumByUser(companyId,loginName);
		if(taskNum==null)taskNum=0;
		StringBuilder bl = new StringBuilder();
		bl.append("<div class='div-tb'>");
		bl.append("<a href='"+SystemUrls.getSystemUrl("task")+"'>");
		bl.append("所有待办事宜");
		bl.append("("+taskNum+")");
		bl.append("</a>");
		bl.append("</div>");
		for (Object[] type : typeInfos) {
			bl.append("<div class='div-tb'>");
			bl.append("<a href='"+SystemUrls.getSystemUrl("task")+"'>");
			bl.append(type[0]);
			bl.append("("+type[1]+")");
			bl.append("</a>");
			bl.append("</div>");
		}
		//待办事宜的分类及数量
//		renderText(bl.toString()+taskWebserviceImpl.personalTasks(loginName, companyId, rows));
		String order = Struts2Utils.getParameter("order");
		renderText(taskWebserviceImpl.personalTasks(loginName, companyId, rows,order));
		return null;
	}
	
	public String taskMsg() throws Exception{
		tasks.setPageSize(5);
		taskManager.getAllTasksByUser(companyId, loginName, tasks);
		StringBuilder msg = new StringBuilder("{");
		msg.append("'type':'task'");
		msg.append(",'counts':").append(tasks.getTotalCount());
		if(!tasks.getResult().isEmpty()){
			msg.append(",'id':").append(tasks.getResult().get(0).getId())
			.append(",'title':'").append(tasks.getResult().get(0).getTitle()).append("'");
		}
		msg.append("}");
		renderText(msg.toString());
		return null;
	}

	@Autowired
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	
	@Required
	public void setTaskManager(TaskManager taskManager) {
		this.taskManager = taskManager;
	}
	
	/**
	 * 新任务列表
	 */
	@Override
	@Action("task")
	public String list() throws Exception {
		if("default_type".equals(typeName)||"custom_type".equals(typeName)||"workflow_name".equals(typeName)){
			typeName="";
		}
		if(TaskType.WORKFLOW_NAME.toString().equalsIgnoreCase(taskType)){
			if(tasks.getPageSize()>1){
				taskManager.getAllTasksByGroupName(getCompanyId(), getLoginName(), tasks,typeName);
				ApiFactory.getBussinessLogService().log("待办事宜", 
						"待办事宜列表", 
						ContextUtils.getSystemId("task"));
				this.renderText(PageUtils.pageToJson(tasks));
				return null;
			}
		}else if(TaskType.CUSTOM_TYPE.toString().equalsIgnoreCase(taskType)){
			if(tasks.getPageSize()>1){
				taskManager.getAllTasksByCustomType(getCompanyId(), getLoginName(), tasks,typeName);
				ApiFactory.getBussinessLogService().log("待办事宜", 
						"待办事宜列表", 
						ContextUtils.getSystemId("task"));
				this.renderText(PageUtils.pageToJson(tasks));
				return null;
			}
		}else{
			if(tasks.getPageSize()>1){
				taskManager.getAllTasksByUserType(getCompanyId(), getLoginName(), tasks,typeName);
				ApiFactory.getBussinessLogService().log("待办事宜", 
						"待办事宜列表", 
						ContextUtils.getSystemId("task"));
				this.renderText(PageUtils.pageToJson(tasks));
				return null;
			}
		}
		return "task";
	}
	
	
	/**
	 * 标记任务
	 * @return
	 * @throws Exception
	 */
	@Action("task-mark")
	public String mark() throws Exception{
		String[] idStr=ids.split(",");
		for(int i=0;i<idStr.length;i++){
			taskManager.changeTaskMark(Long.parseLong(idStr[i]),taskMarks);
		}
		ApiFactory.getBussinessLogService().log("待办事宜", 
				"标记任务", 
				ContextUtils.getSystemId("task"));
		if(TaskCategory.COMPLETE.equals(taskCategory)){
			return completedTasks();
		}else if(TaskCategory.CANCEL.equals(taskCategory)){
			return canceledTasks();
		}else{
			return list();
		}
	}

	public void prepareMark() throws Exception {
		//task = workflowTaskManager.getTask(id);
	}
	
	/**
	 * 已完成任务列表
	 * @return
	 * @throws Exception
	 */
	@Action("task-completed-list")
	public String completedTasks() throws Exception{
		taskCategory="complete";
		if("default_type".equals(typeName)||"custom_type".equals(typeName)||"workflow_name".equals(typeName)){
			typeName="";
		}
		if(TaskType.WORKFLOW_NAME.toString().equalsIgnoreCase(taskType)){
			if(tasks.getPageSize()>1){
				taskManager.getCompletedTasksByGroupName(getCompanyId(), getLoginName(), tasks,typeName);
				ApiFactory.getBussinessLogService().log("待办事宜", 
						"已完成任务列表", 
						ContextUtils.getSystemId("task"));
				this.renderText(PageUtils.pageToJson(tasks));
				return null;
			}
		}else if(TaskType.CUSTOM_TYPE.toString().equalsIgnoreCase(taskType)){
			if(tasks.getPageSize()>1){
				taskManager.getCompletedTasksByCustomType(getCompanyId(), getLoginName(), tasks,typeName);
				ApiFactory.getBussinessLogService().log("待办事宜", 
						"已完成任务列表", 
						ContextUtils.getSystemId("task"));
				this.renderText(PageUtils.pageToJson(tasks));
				return null;
			}
		}else{
			if(tasks.getPageSize()>1){
				taskManager.getCompletedTasksByUserType(getCompanyId(), getLoginName(), tasks,typeName);
				ApiFactory.getBussinessLogService().log("待办事宜", 
						"已完成任务列表", 
						ContextUtils.getSystemId("task"));
				this.renderText(PageUtils.pageToJson(tasks));
				return null;
			}
		}
		return "task-completed-list";
	}
	
	/**
	 * 已完成任务列表
	 * @return
	 * @throws Exception
	 */
	@Action("task-canceled-list")
	public String canceledTasks() throws Exception{
		if("default_type".equals(typeName)||"custom_type".equals(typeName)||"workflow_name".equals(typeName)){
			typeName="";
		}
		if(TaskType.WORKFLOW_NAME.toString().equalsIgnoreCase(taskType)){
			if(tasks.getPageSize()>1){
				taskManager.getCancelTasksByGroupName(getCompanyId(), getLoginName(), tasks,typeName);
				ApiFactory.getBussinessLogService().log("待办事宜", 
						"已取消任务列表", 
						ContextUtils.getSystemId("task"));
				this.renderText(PageUtils.pageToJson(tasks));
				return null;
			}
		}else if(TaskType.CUSTOM_TYPE.toString().equalsIgnoreCase(taskType)){
			if(tasks.getPageSize()>1){
				taskManager.getCancelTasksByCustomType(getCompanyId(), getLoginName(), tasks,typeName);
				ApiFactory.getBussinessLogService().log("待办事宜", 
						"已取消任务列表", 
						ContextUtils.getSystemId("task"));
				this.renderText(PageUtils.pageToJson(tasks));
				return null;
			}
		}else{
			if(tasks.getPageSize()>1){
				taskManager.getCanceledTasksByUserType(getCompanyId(), getLoginName(), tasks,typeName);
				ApiFactory.getBussinessLogService().log("待办事宜", 
						"已取消任务列表", 
						ContextUtils.getSystemId("task"));
				this.renderText(PageUtils.pageToJson(tasks));
				return null;
			}
		}
		return "task-canceled-list";
	}
	/**
	 * 任务类型树
	 * @return
	 * @throws Exception
	 */
	@Action("task-type-tree")
	public String typeTree() throws Exception{
		StringBuilder tree=new StringBuilder();
		List<Object[]> typeInfos=null;
		tree.append("[");
		if(TaskType.WORKFLOW_NAME.toString().equalsIgnoreCase(taskType)){
			typeInfos=workflowTaskManager.getGroupNames(taskCategory);
			tree.append(JsTreeUtils.generateJsTreeNodeNew("workflow_name", "open", "流程名称",typeTree(typeInfos),"")).append(",");
		}else if(TaskType.CUSTOM_TYPE.toString().equalsIgnoreCase(taskType)){
			typeInfos=workflowTaskManager.getCustomTypes(taskCategory);
			tree.append(JsTreeUtils.generateJsTreeNodeNew("custom_type", "open", "流程自定义类别",typeTree(typeInfos),"")).append(",");
		}else{
			typeInfos=taskManager.getTypeInfos(taskCategory);
			tree.append(JsTreeUtils.generateJsTreeNodeNew("default_type", "open", "默认类别",typeTree(typeInfos),"")).append(",");
		}
		JsTreeUtils.removeLastComma(tree);
		tree.append("]");
		this.renderText(tree.toString());
		return null;
	}
	
	private String typeTree(List<Object[]> typeInfos){
		Integer taskNum=taskManager.getAllTaskNumByUser(taskCategory);
		StringBuilder tree=new StringBuilder();
		tree.append("[");
		if(TaskCategory.COMPLETE.equals(taskCategory)){
			tree.append(JsTreeUtils.generateJsTreeNodeNew("complete_task", "", "所有事宜","")).append(",");
		}else if(TaskCategory.CANCEL.equals(taskCategory)){
			tree.append(JsTreeUtils.generateJsTreeNodeNew("cancel_task", "", "所有事宜","")).append(",");
		}else{
			tree.append(JsTreeUtils.generateJsTreeNodeNew("active_task", "", "所有事宜("+taskNum+")","")).append(",");
		}
		for(Object[] objs:typeInfos){
			String typeName=(String)objs[0];
			Long countTask=(Long)objs[1];
			if(StringUtils.isNotEmpty(typeName)){
				if(TaskCategory.COMPLETE.equals(taskCategory)||TaskCategory.CANCEL.equals(taskCategory)){
					tree.append(JsTreeUtils.generateJsTreeNodeNew(typeName, "", typeName,"")).append(",");
				}else{
					tree.append(JsTreeUtils.generateJsTreeNodeNew(typeName, "", typeName+"("+countTask+")","")).append(",");
				}
			}
		}
		JsTreeUtils.removeLastComma(tree);
		tree.append("]");
		return tree.toString();
	}
	/**
	 * 领取任务
	 * @return
	 * @throws Exception
	 */
	@Action("task-receive")
	public String receive() throws Exception{
		log.debug("*** receive 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
				.append("id:").append(id)
				.append("]").toString());
    	
		String msg = workflowTaskManager.receive(ids);
		ApiFactory.getBussinessLogService().log("待办事宜", 
				"领取任务", 
				ContextUtils.getSystemId("task"));
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+this.getText(msg)+MESSAGE_RIGHT);
		log.debug("*** receive 方法结束");
		return list();
	}
	
	/**
	 * 指派任务
	 * @return
	 * @throws Exception
	 */
	public String assign() throws Exception{
		return "assign";
	}
	
	/**
	 * 完成指派
	 * @return
	 * @throws Exception
	 */
	public String assignTo() throws Exception{
		log.debug("*** assignTo 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
				.append("id:").append(id)
				.append(", transactor:").append(transactor)
				.append("]").toString());
		
		workflowTaskManager.assign(id, transactor);
		ApiFactory.getBussinessLogService().log("待办事宜", 
				"指派任务", 
				ContextUtils.getSystemId("task"));
		log.debug("*** receive 方法结束");
		return null;
	}
	
	@Override
	public String delete() throws Exception {
		return null;
	}

	@Override
	public String input() throws Exception {
		if(!task.getRead()){
		  task.setRead(true);
		  taskManager.saveTask(task);
		}
//		String url="http://192.168.1.98:8088/imatrix/"+task.getUrl()+task.getId();
		task.setUrl(getTaskUrl(task));
		return INPUT;
	}
	
	private String getTaskUrl(Task task){
		String url=task.getUrl();
		if(!task.getUrl().contains("http://")&&task.getUrl().contains("?")){
			url=SystemUrls.getSystemUrl(StringUtils.substringBefore(task.getUrl(), "/"))+StringUtils.substring(task.getUrl(), task.getUrl().indexOf('/'))+task.getId();
		}else if(!task.getUrl().contains("http://")){
			url=SystemUrls.getSystemUrl(StringUtils.substringBefore(task.getUrl(), "/"))+StringUtils.substring(task.getUrl(), task.getUrl().indexOf('/'))+"?taskId="+task.getId();
		}
		//重新加载页面样式
		if(!url.contains("_r=1")){
			if(url.contains("?")){
				url=url+"&_r=1";
			}else{
				url=url+"?_r=1";
			}
		}
		return url;
	}
	
	/**
	 * 超时任务，portal使用
	 */
	public String overtimeTask() throws Exception {
		String companyId=ServletActionContext.getRequest().getParameter("companyId");
		String userId=ServletActionContext.getRequest().getParameter("userId");
		List<WorkflowTask> tasks = workflowTaskManager.getNeedReminderTasks(userManager.getUserById(Long.valueOf(userId)).getLoginName(),Long.valueOf(companyId));
		StringBuilder result = new StringBuilder("[");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar  cal = Calendar.getInstance();
		for(WorkflowTask task:tasks){
			if(neetReminder(task)){
				if(result.length()!=1) result.append(",");
				result.append(wrapTask(task,sdf,cal));
			}
		}
		result.append("]");
		this.renderText(result.toString());
		return null;
	}
	/*
	 * {productCode:xx;url:xx;entityId:xx;companyId:xx;userId:xx;username:xx;endDate：xx;overtimeDay:xx}
	 */
	private String wrapTask(Task task,SimpleDateFormat sdf,Calendar  cal)throws Exception{
		cal.setTime(task.getCreatedTime());
		cal.add(Calendar.DAY_OF_MONTH, new Long(task.getDuedate()).intValue());
		StringBuilder result = new StringBuilder("{productCode:'");
		result.append(StringUtils.substringBefore(task.getUrl(), "/"))
				.append("',url:'").append(StringUtils.substringAfter(task.getUrl(), "/"))
				.append("',entityId:").append(task.getId())
				.append(",endDate:'").append(sdf.format(cal.getTime()))
				.append("',overtimeDay:").append(getDateMinus(cal.getTime(),new Date()))
				.append(",initiateName:'").append(task.getCreatorName())
				.append("',taskName:'").append(task.getTitle())
				.append("'}");
		return result.toString();
	}
	
	private  long getDateMinus(Date beginTime,Date endTime)throws Exception{
			long time = (endTime.getTime()-beginTime.getTime())/1000/60/60/24;
			return time;
	}
	
	private boolean neetReminder(WorkflowTask task){
		long milliSecond = 1000*60*60*24;
		return (task.getLastReminderTime()== null && (System.currentTimeMillis()-task.getCreatedTime().getTime())>task.getDuedate()*milliSecond) ||
		(task.getLastReminderTime()!= null && (System.currentTimeMillis()-task.getLastReminderTime().getTime())>task.getRepeat()*milliSecond);
	}
	
	/**
	 * swing任务信息
	 * @return
	 */
	@Action("task-info")
	public String taskInfo(){
		StringBuilder bu= new StringBuilder();
		String imatrixUrl=SystemUrls.getSystemUrl("imatrix");
		String url="";
		String message="";
		if("false".equals(auto)){//非自动登录
			boolean validateAccess=acsUtils.validateUserAccess(username, password);
			if(validateAccess){//验证用户名或密码是否正确
				User user=acsUtils.getUserByLoginName(username);
				url=imatrixUrl+"/task/task/task.htm?type=auto&name="+username+"&pwd="+user.getPassword();
			}else{//用户名或密码错误
				message="用户名或密码错误";
				url="";
			}
		}else{//自动登录
			url=imatrixUrl+"/task/task/task.htm";
		}
		if(StringUtils.isNotEmpty(url)){//当用户名密码正确时
			Integer taskNum=workflowTaskManager.getTasksNumByTransactor(companyId, username);
			if(taskNum==null||taskNum==0){//无待办事宜
				message="无待办事宜";
				url="";
			}else{//待办事宜信息
				message="待办任务"+taskNum+"条";
			}
		}
		bu.append("{ ");
		bu.append("message:'"+message+"',");
		bu.append("url:'"+url+"'");
		bu.append(" }");
		this.renderText(bu.toString());
		return null;
	}
	/**
	 * 消息是否失效处理
	 * @return
	 */
	@Action("message-task")
	public String messageTask() throws Exception{
		HttpServletResponse response = Struts2Utils.getResponse();
		if(id==null){
			response.sendRedirect(SystemUrls.getSystemUrl("imatrix")+"/portal/my-message-error.action?errorInfo="+URLEncoder.encode("该任务不存在","UTF-8") );
		}else{
			WorkflowTask mytask=workflowTaskManager.getTaskById(id);
			if(mytask==null){
				response.sendRedirect(SystemUrls.getSystemUrl("imatrix")+"/portal/my-message-error.action?errorInfo="+URLEncoder.encode("该任务不存在","UTF-8") );
			}else{
				if(mytask.getPaused()){
					response.sendRedirect(SystemUrls.getSystemUrl("imatrix")+"/portal/my-message-error.action?errorInfo="+URLEncoder.encode("该任务对应的实例已暂停","UTF-8") );
				}else{
					if(mytask.getActive().equals(2)){
						response.sendRedirect(SystemUrls.getSystemUrl("imatrix")+"/portal/my-message-error.action?errorInfo="+URLEncoder.encode("该任务已完成","UTF-8") );
						return null;
					}if(mytask.getActive().equals(3)){
						response.sendRedirect(SystemUrls.getSystemUrl("imatrix")+"/portal/my-message-error.action?errorInfo="+URLEncoder.encode("该任务已取消","UTF-8") );
						return null;
					}else if(mytask.getActive().equals(5)){
						response.sendRedirect(SystemUrls.getSystemUrl("imatrix")+"/portal/my-message-error.action?errorInfo="+URLEncoder.encode("该任务已被指派","UTF-8") );
						return null;
					}else if(mytask.getActive().equals(7)){
						response.sendRedirect(SystemUrls.getSystemUrl("imatrix")+"/portal/my-message-error.action?errorInfo="+URLEncoder.encode("该任务已被他人领取","UTF-8") );
						return null;
					}else{
						response.sendRedirect(SystemUrls.getSystemUrl("task")+"/task/task!input.htm?id="+mytask.getId() );
						return null;
					}
				}
			}
		}
		return null;
	}
	
	@Action("workflow-notification")
	public String workflowNotification() throws Exception{
		if("process".equals(notificationType)){
			notificationTheme=DefinitionXmlParse.getProcessInformSubject(processId);
			notificationContent=DefinitionXmlParse.getProcessInformContent(processId);
		}else if("transition".equals(notificationType)){
			notificationTheme=DefinitionXmlParse.getNeedInformSubject(processId, transitionName);
			notificationContent=DefinitionXmlParse.getNeedInformContent(processId, transitionName);
		}
		return "workflow-notification";
	}
	
	public void prepareCommonTaskInput()throws Exception {
		prepareModel();
	}
	@Action("common-task-input")
	public String commonTaskInput() throws Exception {
		return "common-task-input";
	}
	public void prepareCompleteCommonTask()throws Exception {
		prepareModel();
	}
	@Action("complete-common-task")
	public String completeCommonTask()throws Exception {
		taskManager.completeCommonTask(task);
		return "common-task-input";
	}
	@Action("create-task")
	public String createTask()throws Exception {
		taskManager.createTask("任务"+new Date().getTime(),"测试普通任务"+new Date().getTime(), "普通任务测试", "liudongxia");
		return "task-list";
	}
	@Action("task-type-portal")
	public String taskTypeWindow()throws Exception {
		String companyId=Struts2Utils.getParameter("companyId");
		String userId=Struts2Utils.getParameter("userId");
		HashMap<String, Object> dataModel=new HashMap<String, Object>();
		dataModel.put("taskCtx",SystemUrls.getSystemUrl("task") );//SystemUrls.getSystemUrl("task")
		dataModel.put("companyId", companyId);
		dataModel.put("userId", userId);
		renderText(TagUtil.getContent(dataModel, "task-type.ftl"));
		return null;
	}
	@Action("task-detail-portal")
	public String taskDetailWindow()throws Exception {
		String companyId=Struts2Utils.getParameter("companyId");
		String userId=Struts2Utils.getParameter("userId");
		String loginName=Struts2Utils.getParameter("loginName");
		HashMap<String, Object> dataModel=new HashMap<String, Object>();
		dataModel.put("taskCtx",SystemUrls.getSystemUrl("task") );//SystemUrls.getSystemUrl("task")
		dataModel.put("companyId", companyId);
		dataModel.put("userId", userId);
		ThreadParameters parameter = new ThreadParameters(Long.parseLong(companyId), Long.parseLong(userId));
		ParameterUtils.setParameters(parameter);
		String resourceCtx=PropUtils.getProp("host.resources");
		dataModel.put("resourceCtx", resourceCtx);
		String theme=indexManager.getThemeByUser(Long.parseLong(userId), Long.parseLong(companyId));
		dataModel.put("theme", StringUtils.isEmpty(theme)?"black":theme);
		List<Object[]> taskTypes=taskManager.getTypeInfos(Long.parseLong(companyId),loginName);
		if(taskTypes!=null&&taskTypes.size()>0){
			dataModel.put("haveTask", "yes");
		}else{
			dataModel.put("haveTask", "no");
		}
		dataModel.put("taskTypes", taskTypes);
		List<String> taskContents=new ArrayList<String>();
		String order = Struts2Utils.getParameter("order");
		String taskContent="";
		for(Object[] taskType:taskTypes){
			taskContent=taskWebserviceImpl.detailTasks(loginName, Long.parseLong(companyId), rows,order,String.valueOf(taskType[0]));
			taskContents.add(taskContent);
		}
		dataModel.put("taskContents", taskContents);
		dataModel.put("contentAmount", taskContents.size());
		renderText(TagUtil.getContent(dataModel, "task-detail.ftl"));
		return null;
	}
	@Action("task-type-tree-portal")
	public String portalTaskTypeTree() throws Exception{
		String companyId=Struts2Utils.getParameter("companyId");
		String userId=Struts2Utils.getParameter("userId");
		if(StringUtils.isNotEmpty(companyId)&&StringUtils.isNotEmpty(userId)){
			ThreadParameters parameter=new ThreadParameters();
			parameter.setCompanyId(Long.parseLong(companyId));
			parameter.setUserId(Long.parseLong(userId));
			ParameterUtils.setParameters(parameter);
			portalTypeTree();
		}
		return null;
	}
	
	/**
	 * 任务类型树
	 * @return
	 * @throws Exception
	 */
	private String portalTypeTree(){
		StringBuilder tree=new StringBuilder();
		List<Object[]> typeInfos=null;
		tree.append("[");
		if(TaskType.WORKFLOW_NAME.toString().equalsIgnoreCase(taskType)){
			typeInfos=workflowTaskManager.getGroupNames(TaskCategory.ACTIVE);
			tree.append(JsTreeUtils.generateJsTreeNodeNew("workflow_name", "open", "流程名称",portalTypeTree(typeInfos),"")).append(",");
		}else if(TaskType.DEFAULT_TYPE.toString().equalsIgnoreCase(taskType)){
			typeInfos=taskManager.getTypeInfos(TaskCategory.ACTIVE);
			tree.append(JsTreeUtils.generateJsTreeNodeNew("default_type", "open", "流程类别",portalTypeTree(typeInfos),"")).append(",");
		}else{
			typeInfos=taskManager.getTypeInfos(TaskCategory.ACTIVE);
			tree.append(JsTreeUtils.generateJsTreeNodeNew("default_type", "open", "流程类别",portalTypeTree(typeInfos),"")).append(",");
			typeInfos=workflowTaskManager.getGroupNames(TaskCategory.ACTIVE);
			tree.append(JsTreeUtils.generateJsTreeNodeNew("workflow_name", "close", "流程名称",portalTypeTree(typeInfos),"")).append(",");
		}
		JsTreeUtils.removeLastComma(tree);
		tree.append("]");
		this.renderText(tree.toString());
		return null;
	}
	
	private String portalTypeTree(List<Object[]> typeInfos){
		Integer taskNum=taskManager.getAllTaskNumByUser(TaskCategory.ACTIVE);
		StringBuilder tree=new StringBuilder();
		tree.append("[");
		tree.append(JsTreeUtils.generateJsTreeNodeNew("active_task", "", "所有事宜("+taskNum+")","")).append(",");
		for(Object[] objs:typeInfos){
			String typeName=(String)objs[0];
			Long countTask=(Long)objs[1];
			tree.append(JsTreeUtils.generateJsTreeNodeNew(typeName, "", typeName+"("+countTask+")","")).append(",");
		}
		JsTreeUtils.removeLastComma(tree);
		tree.append("]");
		return tree.toString();
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			task = new Task();
		}else{
			task = taskManager.getTaskById(id);
		}
	}

	@Override
	public String save() throws Exception {
		return null;
	}

	public Task getModel() {
		return task;
	}

	public void setTasks(Page<Task> tasks) {
		this.tasks = tasks;
	}
	public Page<Task> getTasks() {
		return tasks;
	}

	@Required
	public void setWorkflowTaskManager(WorkflowTaskManager workflowTaskManager) {
		this.workflowTaskManager = workflowTaskManager;
	}
	
	public Long getCompanyId(){
		return ContextUtils.getCompanyId();
	}
	
	public String getLoginName(){
		return ContextUtils.getLoginName();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setTransactor(String transactor) {
		this.transactor = transactor;
	}
	
	
	public String getSearchString() {
		return searchString;
	}
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	
	public void setFinish(String finish) {
		this.finish = finish;
	}


	public void setUsername(String username) {
		this.username = username;
	}

	public void setAuto(String auto) {
		this.auto = auto;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public TaskMark getTaskMarks() {
		return taskMarks;
	}

	public void setTaskMarks(TaskMark taskMarks) {
		this.taskMarks = taskMarks;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getCurrentNodeId() {
		return currentNodeId;
	}

	public void setCurrentNodeId(String currentNodeId) {
		this.currentNodeId = currentNodeId;
	}
	
	public void setTaskCategory(String taskCategory) {
		this.taskCategory = taskCategory;
	}
	public String getTaskCategory() {
		return taskCategory;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public String getNotificationTheme() {
		return notificationTheme;
	}

	public void setNotificationTheme(String notificationTheme) {
		this.notificationTheme = notificationTheme;
	}

	public String getNotificationContent() {
		return notificationContent;
	}

	public void setNotificationContent(String notificationContent) {
		this.notificationContent = notificationContent;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getTransitionName() {
		return transitionName;
	}

	public void setTransitionName(String transitionName) {
		this.transitionName = transitionName;
	}

	
}
