package com.norteksoft.wf.base.tags;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.product.util.FtlUtils;
import com.norteksoft.product.util.WebContextUtils;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.engine.entity.InstanceHistory;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.service.InstanceHistoryManager;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;
import com.norteksoft.wf.engine.service.WorkflowRightsManager;



@Deprecated
public class WorkflowHistoryTag extends TagSupport{

	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(WorkflowHistoryTag.class);
	
	private String url;
	private String webRoot;
	private Long companyId;
	private String workflowId;
	private String locale;
	private Long taskId;
	private Boolean view =false;
	private WorkflowInstanceManager workflowInstanceManager;
	private TaskService taskService;
	 public int doStartTag() throws JspException{  
		 try {
			 workflowInstanceManager = (WorkflowInstanceManager)WebContextUtils.getBean("workflowInstanceManager");
			 WorkflowRightsManager workflowRightsManager = (WorkflowRightsManager)WebContextUtils.getBean("workflowRightsManager");
			 taskService = (TaskService)WebContextUtils.getBean("taskService");
			 webRoot = ((HttpServletRequest)this.pageContext.getRequest()).getContextPath();
			 ((HttpServletRequest)this.pageContext.getRequest()).setCharacterEncoding("utf-8");
			 locale = this.pageContext.getRequest().getLocale().toString();
			 JspWriter out=pageContext.getOut(); 
			 if(taskId!=0){
				 WorkflowTask task = taskService.getTask(taskId);
				 workflowId = task.getProcessInstanceId();
				 view=workflowRightsManager.viewFlowHistoryRight(task);
				 out.print(readScriptTemplet());
			 }else{
				 out.print("taskId没有值");
			 }
		} catch (Exception e) {
			log.error(e);
			throw new JspException(e);
		}
	     

		 return Tag.EVAL_PAGE;
	 }

	//读取脚本模板
		private String readScriptTemplet() throws Exception{
//			String templetCode =JarFileUtil.readFile(getClass(), "template/tags/workflowHistory.ftl");
			String file = WorkflowHistoryTag.class.getResource("/template/tags/workflowHistory.ftl").getFile();
			String templetCode=FileUtils.readFileToString(new File(file),"utf-8");
			InstanceHistoryManager instanceHistoryManager=(InstanceHistoryManager)WebContextUtils.getBean("instanceHistoryManager");
			List<InstanceHistory> ihs=instanceHistoryManager.getHistorysByWorkflowId(companyId, workflowId);
			for(int i=0;i<ihs.size();i++){
				InstanceHistory ih=ihs.get(i);
				String result=ih.getTransactionResult();
				if(result.contains("[")){
					String temp=result.substring(result.indexOf("[")+1,result.indexOf("]"));
					if(temp.equals("transition.approval.result.agree")){
						result=result.substring(0, result.indexOf("[")) + "[同意]" + result.substring(result.lastIndexOf("]") + 1, result.length());
						ih.setTransactionResult(result);
						ihs.set(i, ih);
					}else if (temp .equals( "transition.approval.result.disagree")){
						result=result.substring(0, result.indexOf("[")) + "[不同意]" + result.substring(result.lastIndexOf("]") + 1, result.length());
						ih.setTransactionResult(result);
						ihs.set(i, ih);
					}else if(temp.contains("_")){
						WorkflowInstance workflowInstance = workflowInstanceManager.getWorkflowInstance(workflowId);
						result=result.substring(0, result.indexOf("[")) + "["+workflowInstance.getProcessName()+"]" + result.substring(result.lastIndexOf("]") + 1, result.length());
						ih.setTransactionResult(result);
						ihs.set(i, ih);
					}
				}
			}
			Map<String, Object> root=new HashMap<String, Object>();
			root.put("ctx", webRoot);
			root.put("url", url);
			root.put("companyId", companyId.toString());
			root.put("workflowId", workflowId);
			root.put("view", view);
			root.put("locale", locale);
			root.put("textContent", "列表视图");
			root.put("flashContent", "图形视图");
			root.put("instanceHistory",ihs);
			root.put("sequence","序号");
			root.put("name","名称");
			root.put("history","流转操作");
			root.put("start","流程开始");
			root.put("end","流程结束");
			root.put("opinion","办理意见");
			String result = FtlUtils.renderFile(root, templetCode);
			return result;
		}
	 public int doEndTag() throws JspException{
		 return Tag.EVAL_PAGE;
	 }

	public void setUrl(String url) {
		this.url = url;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public String getWebRoot() {
		return webRoot;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
}
