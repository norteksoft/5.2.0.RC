package com.norteksoft.acs.web.authorization;

import java.util.List;

import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.FunctionGroup;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.FunctionGroupManager;
import com.norteksoft.product.orm.Page;

/**
 *  业务系统Action
 */
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "business-system", type = "redirectAction") })
public class BusinessSystemAction extends CRUDActionSupport<BusinessSystem> {

	private static final long serialVersionUID = 4814560124772644966L;
	private BusinessSystemManager businessSystemManager;
	private FunctionGroupManager functionGroupManager;
	private Page<BusinessSystem> page = new Page<BusinessSystem>(20, true);
	private Page<FunctionGroup> pageFunctionGroup = new Page<FunctionGroup>(20, true);
	private BusinessSystem businessSystem;
	private Long id;
	private List<BusinessSystem> allBusinessSystem;
	private String businessName;
	private List<Long> functionGroupIds;
	private Long businessSystemId;
	private List<Long> checkedFunctionGroupIds;
	private String html;
	private FunctionGroup functionGroup;
	private boolean isCreate = true;;

	@Override
	public String delete() throws Exception {
			businessSystemManager.deleteBusiness(id);
			addActionMessage(getText("common.deleted"));
		return RELOAD;
	}

	/**
	 * 按照公司获取公司所购买的系统
	 * @return
	 */
	public String getBusinessSystemByCompany() {
		page = businessSystemManager.getAllBusiness(page);
		return SUCCESS;
	}

	@Override
	public String list() throws Exception {
		page = businessSystemManager.getAllBusiness(page);
		return SUCCESS;
	}

	/**
	 * 按条件查询业务系统
	 */
	public void prepareSearch() throws Exception {
		prepareModel();
	}

	public String search() throws Exception {
		page = businessSystemManager.getSearchBusiness(page, businessSystem,
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
			businessSystem = businessSystemManager.getBusiness(id);
			isCreate = false;
		} else {
			businessSystem = new BusinessSystem();
		}
	}

	@Override
	public String save() throws Exception {
		businessSystemManager.saveBusiness(businessSystem, isCreate);
		addActionMessage(getText("common.saved"));
		return RELOAD;
	}

	/**
	 * 业务系统添加资源跳转页面
	 */
	public void prepareInputFunctionGroup() throws Exception {
		businessSystem = businessSystemManager.getBusiness(businessSystemId);
	}

	public String inputFunctionGroup() throws Exception {
		return "function-group";
	}

	public String systemAddFunctionGroup() throws Exception {
		businessSystemManager.systemAddFunctionGroup(businessSystemId,
				functionGroup);
		return RELOAD;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	
	public BusinessSystem getModel() {

		return businessSystem;
	}

	public Page<BusinessSystem> getPage() {
		return page;
	}

	public void setPage(Page<BusinessSystem> page) {
		this.page = page;
	}

	public String temp() throws Exception {
		return SUCCESS;
	}

	@Required
	public void setBusinessSystemManager(
			BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}

	public List<BusinessSystem> getAllBusinessSystem() {
		return allBusinessSystem;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBusinessSystemId() {
		return businessSystemId;
	}

	public void setBusinessSystemId(Long businessSystemId) {
		this.businessSystemId = businessSystemId;
	}

	public Page<FunctionGroup> getPageFunctionGroup() {
		return pageFunctionGroup;
	}

	public void setPageFunctionGroup(Page<FunctionGroup> pageFunctionGroup) {
		this.pageFunctionGroup = pageFunctionGroup;
	}

	public FunctionGroupManager getFunctionGroupManager() {
		return functionGroupManager;
	}

	public List<Long> getFunctionGroupIds() {
		return functionGroupIds;
	}

	public void setFunctionGroupIds(List<Long> functionGroupIds) {
		this.functionGroupIds = functionGroupIds;
	}

	@Required
	public void setFunctionGroupManager(
			FunctionGroupManager functionGroupManager) {
		this.functionGroupManager = functionGroupManager;
	}

	public List<Long> getCheckedFunctionGroupIds() {
		return checkedFunctionGroupIds;
	}

	public void setCheckedFunctionGroupIds(List<Long> checkedFunctionGroupIds) {
		this.checkedFunctionGroupIds = checkedFunctionGroupIds;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public FunctionGroup getFunctionGroup() {
		return functionGroup;
	}

	public void setFunctionGroup(FunctionGroup functionGroup) {
		this.functionGroup = functionGroup;
	}

}
