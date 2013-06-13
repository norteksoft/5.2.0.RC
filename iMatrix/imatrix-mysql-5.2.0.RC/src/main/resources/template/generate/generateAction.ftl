package ${packageName};
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;
<#if containWorkflow?if_exists>
import edu.emory.mathcs.backport.java.util.Arrays;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.web.wf.WorkflowActionSupport;
import java.util.ArrayList;
import java.util.List;
import com.norteksoft.wf.base.enumeration.CompleteTaskTipType;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.acs.web.authorization.JsTreeUtil1;
import com.norteksoft.product.api.entity.User;
<#else>
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.util.JsonParser;
</#if>
<#list imports?if_exists as item>
import ${item};
</#list>

@Namespace("/${namespace}")
@ParentPackage("default")
<#if containWorkflow?if_exists>
public class ${entityName}Action  extends WorkflowActionSupport<${entityName}> {
<#else>
public class ${entityName}Action  extends CrudActionSupport<${entityName}> {
</#if>

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String ids;
	private ${entityName} ${entityAttribute};
	private Page<${entityName}> page;
<#if containWorkflow?if_exists>
	private String addSignPerson;//加签人员
	private String removeSignPerson;//减签人员
	private String copyPerson;//抄送人员
	private List<String[]> handerList = new ArrayList<String[]>();//减签环节办理人list
	private String assignee; //指派人
	private String submitResult;//任务提交结果
</#if>
	
	@Autowired
	private ${entityName}Manager ${entityAttribute}Manager;
	
<#if containWorkflow?if_exists>

	public void prepareTask() throws Exception {
		prepareModel();
	}

	/**
	 * 办理任务页面
	 * @return
	 */
	@Action("${entityAttribute}-task")
	public String task() throws Exception {
		getRight(taskId,"${workflowCode}");
		//办理前自动填写域设值
		if(taskId!=null){
			ApiFactory.getFormService().fillEntityByTask(${entityAttribute}, taskId);
		}
		return SUCCESS;
	}
	
	/**
	 * 抄送
	 * @return
	 */
	@Action("${entityAttribute}-copyTask")
	public String copyTasks(){
		List<String> loginNames=new ArrayList<String>();
		if("all_user".equals(copyPerson)){
			List<User> users=ApiFactory.getAcsService().getUsersByCompany(ContextUtils.getCompanyId());
			for(User u:users){
				loginNames.add(u.getLoginName());
			}
		}else{
			loginNames=Arrays.asList(copyPerson.split(","));
		}
		${entityAttribute}Manager.createCopyTasks(taskId, loginNames, null, null);
		renderText("已抄送");
		return null;
	}
	
	/**
	 * 退回
	 * @return
	 */
	@Action("${entityAttribute}-goback")
	public String goback(){
		String msg=${entityAttribute}Manager.goback(taskId);
		task=${entityAttribute}Manager.getWorkflowTask(taskId);
		${entityAttribute}=${entityAttribute}Manager.get${entityName}ByTaskId(taskId);
		renderText(msg);
		return null;
	}
	
	/**
	 * 放弃领取任务
	 */
	@Override
	@Action("${entityAttribute}-abandonReceive")
	public String abandonReceive() {
		${entityAttribute}Manager.abandonReceive(taskId);
		task=${entityAttribute}Manager.getWorkflowTask(taskId);
		return "${entityAttribute}-task";
	}
	
	/**
	 * 加签
	 */
	@Override
	@Action("${entityAttribute}-addSigner")
	public String addSigner() {
		String[] strs = addSignPerson.split(",");
		List<String> lists = new ArrayList<String>();
		if("all_user".equals(addSignPerson)){
			List<User> users=ApiFactory.getAcsService().getUsersByCompany(ContextUtils.getCompanyId());
			for(User u:users){
				lists.add(u.getLoginName());
			}
		}else{
			for (String str : strs) {
				lists.add(str);
			}
		}
		${entityAttribute}Manager.addSigner(taskId, lists);
		renderText("加签成功！");
		return null;
	}
	
	/**
	 * 完成交互任务：用于选人、选环节、填意见
	 */
	@Override
	@Action("${entityAttribute}-completeInteractiveTask")
	public String completeInteractiveTask() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 完成任务
	 */
	@Override
	@Action("${entityAttribute}-completeTask")
	public String completeTask() {
		CompleteTaskTipType completeTaskTipType=null;
		try{
			completeTaskTipType =  ${entityAttribute}Manager.completeTask(${entityAttribute}, taskId, taskTransact);
		}catch(RuntimeException e){
			e.printStackTrace();
		}
		renderText(${entityAttribute}Manager.getCompleteTaskTipType(completeTaskTipType,${entityAttribute}));
		return null;
	}
	
	/**
	 * 绑定完成任务
	 */
	
	public void prepareCompleteTask() throws Exception{
		prepareModel();
	}
	/**
	 * 领取任务
	 */
	@Override
	@Action("${entityAttribute}-drawTask")
	public String drawTask() {
		${entityAttribute}Manager.drawTask(taskId);
		task=${entityAttribute}Manager.getWorkflowTask(taskId);
		return "${entityAttribute}-task";
	}
	
	/**
	 * 填写意见
	 */
	@Override
	@Action("${entityAttribute}-fillOpinion")
	public String fillOpinion() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 流程监控中应急处理功能
	 */
	@Override
	@Action("${entityAttribute}-processEmergency")
	public String processEmergency() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 减签
	 * @return
	 */
	@Override
	@Action("${entityAttribute}-removeSigner")
	public String removeSigner() {
		String[] strs = removeSignPerson.split(",");
		List<String> lists = new ArrayList<String>();
		for (String str : strs) {
			lists.add(str);
		}
		${entityAttribute}Manager.removeSigner(taskId, lists);
		renderText("减签成功！");
		return null;
	}
	
	/**
	 * 选择减签人员
	 * @return
	 * @throws Exception 
	 */
	@Action("${entityAttribute}-cutsignTree")
	public String cutsignTree() throws Exception{
		prepareModel();
		handerList = ${entityAttribute}Manager.getTaskHander(${entityAttribute});
		String userLoginName = ContextUtils.getLoginName();
		StringBuilder tree = new StringBuilder();
		tree.append("[");
		for (Object[] arr : handerList) {
			if(userLoginName.equals((String)arr[0])) continue;
			tree.append(JsTreeUtil1.generateJsTreeNodeNew((String)arr[0] , "", (String) arr[1],"folder")).append(",");
		}
		renderText(tree.toString().substring(0, tree.length()-1)+"]");
		return null;
	}
	
	/**
	 * 指派
	 * @return
	 */
	@Action("${entityAttribute}-assign")
	public String assign(){
		${entityAttribute}Manager.assign(taskId, assignee);
		renderText("指派完成");
		return null;
	}
	
	/**
	 * 取回任务
	 */
	@Override
	@Action("${entityAttribute}-retrieveTask")
	public String retrieveTask() {
		String msg=${entityAttribute}Manager.retrieve(taskId);
		task=${entityAttribute}Manager.getWorkflowTask(taskId);
		${entityAttribute}=${entityAttribute}Manager.get${entityName}ByTaskId(taskId);
		renderText(msg);
		return null;
	}
	
	/**
	 * 显示流转历史
	 */
	@Override
	@Action("${entityAttribute}-showHistory")
	public String showHistory() {
		${entityAttribute}=${entityAttribute}Manager.get${entityName}(id);
		return "${entityAttribute}-history";
	}
	
	/**
	 * 绑定流转历史
	 */
	public void prepareShowHistory() throws Exception {
		prepareModel();
	}
	
	/**
	 * 绑定提交流程
	 */
	public void prepareSubmitProcess() throws Exception {
		prepareModel();
	}
	
	/**
	 * 启动并提交流程
	 */
	@Override
	@Action("${entityAttribute}-submitProcess")
	public String submitProcess() {
		CompleteTaskTipType completeTaskTipType=null;
		try{
			completeTaskTipType =  ${entityAttribute}Manager.submitProcess(${entityAttribute},"发起","${workflowCode}");
		}catch(RuntimeException de){
			de.printStackTrace();
		}
		submitResult=${entityAttribute}Manager.getCompleteTaskTipType(completeTaskTipType,${entityAttribute});
		return "${entityAttribute}-input";
	}
	
	/**
	 * 删除
	 */
	@Override
	@Action("${entityAttribute}-delete")
	public String delete() throws Exception {
		addActionMessage("<font class=\"onSuccess\"><nobr>"+${entityAttribute}Manager.delete${entityName}(ids)+"</nobr></font>");
		return "${entityAttribute}-list";
	}
	
	/**
	 * 新建页面
	 */
	@Override
	@Action("${entityAttribute}-input")
	public String input() throws Exception {
		getRight(taskId,"${workflowCode}");
		return SUCCESS;
	}
	
	/**
	 * 列表页面
	 */
	@Override
	@Action("${entityAttribute}-list")
	public String list() throws Exception {
		return SUCCESS;
	}
	/**
	 * 可编辑列表页面
	 */
	@Action("${entityAttribute}-listEditable")
	public String listEditable() throws Exception {
		return SUCCESS;
	}
	
	/**
	 * 列表数据
	 */
	@Action("${entityAttribute}-listDatas")
	public String getListDatas() throws Exception {
		page = ${entityAttribute}Manager.search(page);
		renderText(PageUtils.pageToJson(page));
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		if(taskId!=null){
	    	${entityAttribute} = ${entityAttribute}Manager.get${entityName}ByTaskId(taskId);
	    	task = ${entityAttribute}Manager.getWorkflowTask(taskId);
	    	ApiFactory.getFormService().fillEntityByTask(${entityAttribute}, taskId);
	    }else if(id!=null){
	    	${entityAttribute}=${entityAttribute}Manager.get${entityName}(id);
			task = ${entityAttribute}Manager.getMyTask(${entityAttribute},ContextUtils.getLoginName());
			if(task!=null)taskId = task.getId();
			if(task==null) taskId = ${entityAttribute}.getWorkflowInfo().getFirstTaskId();
	    }else if(id==null){
			${entityAttribute}=new ${entityName}();
			ApiFactory.getFormService().fillEntityByDefinition(${entityAttribute}, "${workflowCode}");
		}
	}
	
	/**
	 * 保存
	 */
	@Override
	@Action("${entityAttribute}-save")
	public String save() throws Exception {
		getRight(taskId,"${workflowCode}");
		${entityAttribute}Manager.saveInstance("${workflowCode}",${entityAttribute});
		renderText(${entityAttribute}.getId().toString());
		return null;
	}
	
	/**
	 * 获取权限
	 */
	public void getRight(Long taskId,String defCode) {
		if(taskId==null){
			fieldPermission = ${entityAttribute}Manager.getFieldPermission(defCode);//禁止或必填字段
			taskPermission = ${entityAttribute}Manager.getActivityPermission(defCode);
		}else{
			fieldPermission = ${entityAttribute}Manager.getFieldPermissionByTaskId(taskId);//禁止或必填字段
			taskPermission = ${entityAttribute}Manager.getActivityPermission(taskId);
		}
	}

	@Override
	public ${entityName} getModel() {
		return ${entityAttribute};
	}
<#else>
	/**
	 * 删除
	 */
	@Override
	@Action("${entityAttribute}-delete")
	public String delete() throws Exception {
		addActionMessage("<font class=\"onSuccess\"><nobr>"+${entityAttribute}Manager.delete${entityName}(ids)+"</nobr></font>");
		return "${entityAttribute}-list";
	}

	/**
	 * 新建页面
	 */
	@Override
	@Action("${entityAttribute}-input")
	public String input() throws Exception {
		return SUCCESS;
	}

	/**
	 * 列表页面
	 */
	@Override
	@Action("${entityAttribute}-list")
	public String list() throws Exception {
		return SUCCESS;
	}
	
	/**
	 * 列表数据
	 */
	@Action("${entityAttribute}-listDatas")
	public String getListDatas() throws Exception {
		page = ${entityAttribute}Manager.search(page);
		renderText(PageUtils.pageToJson(page));
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			${entityAttribute}=new ${entityName}();
		}else{
			${entityAttribute}=${entityAttribute}Manager.get${entityName}(id);
		}
	}

	/**
	 * 保存
	 */
	@Override
	@Action("${entityAttribute}-save")
	public String save() throws Exception {
		${entityAttribute}Manager.save${entityName}(${entityAttribute});
		this.renderText(${entityAttribute}.getId().toString());
		return null;
	}

	@Override
	public ${entityName} getModel() {
		return ${entityAttribute};
	}
	
	public void prepareEditSave() throws Exception{
		prepareModel();
	}
	
	/**
	 * 编辑-保存
	 */
	@Action("${entityAttribute}-editSave")
	public String editSave() throws Exception {
		${entityAttribute}Manager.save${entityName}(${entityAttribute});
			this.renderText(JsonParser.getRowValue(${entityAttribute}));
		return null;
	}
	
	/**
	 * 编辑-删除
	 */
	@Action("${entityAttribute}-editDelete")
	public String editDelete() throws Exception {
		ids=Struts2Utils.getParameter("deleteIds");
		String[] deleteIds=ids.split(",");
		for(String deleteId:deleteIds){
			${entityAttribute}Manager.delete${entityName}(Long.valueOf(deleteId));
		}
		return null;
	}
</#if>

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public ${entityName} get${entityName}() {
		return ${entityAttribute};
	}

	public void set${entityName}(${entityName} ${entityAttribute}) {
		this.${entityAttribute} = ${entityAttribute};
	}

	public Page<${entityName}> getPage() {
		return page;
	}

	public void setPage(Page<${entityName}> page) {
		this.page = page;
	}
	
<#if containWorkflow?if_exists>
	public String getAddSignPerson() {
		return addSignPerson;
	}

	public void setAddSignPerson(String addSignPerson) {
		this.addSignPerson = addSignPerson;
	}
	
	public String getRemoveSignPerson() {
		return removeSignPerson;
	}

	public void setRemoveSignPerson(String removeSignPerson) {
		this.removeSignPerson = removeSignPerson;
	}
	public String getCopyPerson() {
		return copyPerson;
	}
	public void setCopyPerson(String copyPerson) {
		this.copyPerson = copyPerson;
	}
		public List<String[]> getHanderList() {
		return handerList;
	}

	public void setHanderList(List<String[]> handerList) {
		this.handerList = handerList;
	}
	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public String getSubmitResult() {
		return submitResult;
	}
</#if>
}

