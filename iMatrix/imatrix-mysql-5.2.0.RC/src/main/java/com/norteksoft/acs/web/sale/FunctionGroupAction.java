package com.norteksoft.acs.web.sale;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.FunctionGroup;
import com.norteksoft.acs.service.ServiceException;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.FunctionGroupManager;
import com.norteksoft.acs.service.authorization.FunctionManager;
import com.norteksoft.mms.base.data.DataHandle;
import com.norteksoft.product.orm.Page;

/**
 * 功能组管理Action
 */
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "/sale/function-group!getFuncGroupsBySystem.action?systemId=${systemId}", type = "redirect") })
public class FunctionGroupAction extends CRUDActionSupport<FunctionGroup> {

	private static final long serialVersionUID = 4814560124772644966L;
	private FunctionGroupManager functionGroupManager;
	private BusinessSystemManager businessSystemManager;
	private FunctionManager functionManager;
	private Page<FunctionGroup> page = new Page<FunctionGroup>(20, true);
	private Page<Function> pageFunction = new Page<Function>(30, true);
	private FunctionGroup functionGroup;
	private Long id;
	private List<FunctionGroup> allFunctionGroup;
	private String functionGroupName;
	private String functionGroupId;
	private Long paternId;
	private List<Long> functionIds;
	private String funcGroupIds;
	private Long systemId;
	private Integer addOrRemove;
	
	private DataHandle dataHandle;
	
	private File file;
	private String fileName;

	public String getFunctionGroupName() {
		return functionGroupName;
	}

	public void setFunctionGroupName(String functionGroupName) {
		this.functionGroupName = functionGroupName;
	}

	public String getFunctionGroupId() {
		return functionGroupId;
	}

	public void setFunctionGroupId(String functionGroupId) {
		this.functionGroupId = functionGroupId;
	}

	@Override
	public String delete() throws Exception {
		try {
			FunctionGroup fg = functionGroupManager.getFunctionGroup(id);
			setSystemId(fg.getBusinessSystem().getId());
			functionGroupManager.deleteFunGroup(id);
			addActionMessage("删除功能组成功");
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
			addActionMessage(e.getMessage());
		}
		return RELOAD;
	}

	@Override
	public String list() throws Exception {
		page = functionGroupManager.getSearchFunctionGroup(page, functionGroup,
				false);
		return SUCCESS;
	}

	@Override
	public String input() throws Exception {
		return INPUT;
	}

	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			functionGroup = functionGroupManager.getFunctionGroup(id);
		} else {
			functionGroup = new FunctionGroup();
			if (systemId != null) {
				BusinessSystem businessSystem = businessSystemManager
						.getBusiness(systemId);
				functionGroup.setBusinessSystem(businessSystem);
			}
		}
	}

	@Override
	public String save() throws Exception {
		functionGroupManager.saveFunGroup(functionGroup);
		addActionMessage("保存用户成功");
		return RELOAD;
	}

	/**
	 * 功能组添加功能跳转页
	 */
	public String inputFunction() throws Exception {
		addOrRemove = 0;
		pageFunction = functionManager.getFunctionsBySystem(pageFunction, systemId);
		return "function-list";
	}

	public String removeFunction() throws Exception {
		addOrRemove = 1;
		pageFunction = functionManager.getFunctionsCanRemoveFromFunctionGroup(pageFunction, paternId);
		return "function-list";
	}
	
	/**
	 * 保存功能组和功能的关系
	 */
	public String saveFunction() throws Exception {
		functionGroup = functionGroupManager.getFunctionGroup(paternId);
		systemId = functionGroup.getBusinessSystem().getId();
		functionGroupManager.saveFunction(paternId, functionIds, addOrRemove);
		return RELOAD;
	}
	
	/**
	 * 导出系统
	 * @return
	 * @throws Exception
	 */
	public String exportFuncGroup() throws Exception{
		HttpServletResponse response = ServletActionContext.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode("acs-func-group.xls","UTF-8"));
		dataHandle.exportFunGroup(response.getOutputStream(),systemId, funcGroupIds);
		return null;
	}
	public String showImportFuncGroup() throws Exception{
		return "show-import";
	}
	/**
	 * 导入系统
	 * @return
	 * @throws Exception
	 */
	public String importFuncGroup() throws Exception{
		if(fileName==null || !fileName.endsWith(".xls")){
			this.addActionMessage("请选择excel文件格式");
			return "show-import";
		}
		boolean success = true;
		try {
			dataHandle.importFunGroup(file,systemId);
		} catch (Exception e) {
			success = false;
		}
		if(success){
			this.addActionMessage("导入成功");
		}else{
			this.addActionMessage("导入失败，请检查excel文件格式");
		}
		return "show-import";
	}

	public FunctionGroup getModel() {

		return functionGroup;
	}

	public Page<FunctionGroup> getPage() {
		return page;
	}

	public void setPage(Page<FunctionGroup> page) {
		this.page = page;
	}

	@Required
	public void setFunctionGroupManager(
			FunctionGroupManager functionGroupManager) {
		this.functionGroupManager = functionGroupManager;
	}

	public List<FunctionGroup> getAllFunGroup() {
		return allFunctionGroup;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 按条件查�?
	 */
	public void prepareSearch() throws Exception {
		prepareModel();
	}

	public String search() throws Exception {

		page = functionGroupManager.getSearchFunctionGroup(page, functionGroup,
				false);
		return SUCCESS;
	}

	public Page<Function> getPageFunction() {
		return pageFunction;
	}

	public void setPageFunction(Page<Function> pageFunction) {
		this.pageFunction = pageFunction;
	}

	public FunctionManager getFunctionManager() {
		return functionManager;
	}

	@Required
	public void setBusinessSystemManager(
			BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}

	@Required
	public void setFunctionManager(FunctionManager functionManager) {
		this.functionManager = functionManager;
	}

	public Long getPaternId() {
		return paternId;
	}

	public void setPaternId(Long paternId) {
		this.paternId = paternId;
	}

	public List<Long> getFunctionIds() {
		return functionIds;
	}

	public void setFunctionIds(List<Long> functionIds) {
		this.functionIds = functionIds;
	}

	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}

	public void setFuncGroupIds(String funcGroupIds) {
		this.funcGroupIds = funcGroupIds;
	}

	public Integer getAddOrRemove() {
		return addOrRemove;
	}

	public void setAddOrRemove(Integer addOrRemove) {
		this.addOrRemove = addOrRemove;
	}

	public String getFuncGroupsBySystem() {
		if (systemId != null) {
			page = functionGroupManager.getFuncGroupsBySystem(page, systemId);
		}
		return SUCCESS;
	}
	@Required
	public void setDataHandle(DataHandle dataHandle) {
		this.dataHandle = dataHandle;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setFileFileName(String fileName) {
		this.fileName = fileName;
	}
}
