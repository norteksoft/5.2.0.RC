package com.norteksoft.wf.engine.web;

import java.util.List;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.wf.engine.entity.WorkflowType;
import com.norteksoft.wf.engine.service.WorkflowTypeManager;

@Namespace("/engine")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "workflow-type", type = "redirectAction")})
public class WorkflowTypeAction extends CrudActionSupport<WorkflowType>{
	
	private static final long serialVersionUID = 1L;
	
	private WorkflowTypeManager workflowTypeManager;
	private Long id;
	private WorkflowType basicType;
	private Page<WorkflowType> page = new Page<WorkflowType>(0,true);
	private String name;
	private List<Long> typeIds;
	private Boolean approveSystem;
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	@Autowired	
	public void setWorkflowTypeManager(WorkflowTypeManager workflowTypeManager) {
		this.workflowTypeManager = workflowTypeManager;
	}
	@Override
	public String delete() throws Exception {
		for(int i=0;i<typeIds.size();i++){
			workflowTypeManager.deleteWorkflowType(typeIds.get(i));
		}
		workflowTypeManager.getWorkflowTypePage(page);
		ApiFactory.getBussinessLogService().log("流程类型", 
				"删除流程类型", 
				ContextUtils.getSystemId("wf"));
		this.addActionMessage("已删除");
		return SUCCESS;
	}

	@Override
	public String input() throws Exception {
		return "input";
	}

	@Override
	public String list() throws Exception {
		if(page.getPageSize()>1){
			workflowTypeManager.getWorkflowTypePage(page);
			ApiFactory.getBussinessLogService().log("流程类型", 
					"流程类型列表", 
					ContextUtils.getSystemId("wf"));
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return SUCCESS;
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			basicType = new WorkflowType();
		}else{
			basicType = workflowTypeManager.getWorkflowType(id);
		}
	}

	@Override
	public String save() throws Exception {
		if(uniqueValidate(basicType.getName())){
			if(approveSystem==null){
				basicType.setApproveSystem(false);
			}
			workflowTypeManager.saveWorkflowType(basicType);
			ApiFactory.getBussinessLogService().log("流程类型", 
					"保存流程类型", 
					ContextUtils.getSystemId("wf"));
			this.addSuccessMessage("保存成功");
		}else{
			this.addErrorMessage("已有这个类型");
		}
		return input();
	}
	private boolean uniqueValidate(String name){
		List<WorkflowType> lists = workflowTypeManager.getWorkflowTypes(name);
		return lists==null || lists.isEmpty()||lists.size()==1&&lists.get(0).equals(basicType);
	}

	public WorkflowType getModel() {
		return basicType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Page<WorkflowType> getPage() {
		return page;
	}

	public void setTypeIds(List<Long> typeIds) {
		this.typeIds = typeIds;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	
	private void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	private void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	public Boolean getApproveSystem() {
		return approveSystem;
	}
	public void setApproveSystem(Boolean approveSystem) {
		this.approveSystem = approveSystem;
	}

}
