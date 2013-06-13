package com.norteksoft.acs.web.sale;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.sale.SalesModule;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.FunctionManager;
import com.norteksoft.acs.service.sale.SalesModuleManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.CollectionUtils;

/**
 * SalesModuleAction.java
 * @author Administrator
 */
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "/sale/sales-module.action", type="redirect") })
public class SalesModuleAction extends CRUDActionSupport<SalesModule>{

	private static final long serialVersionUID = 1L;
	private SalesModuleManager salesModuleManager;
	private Page<SalesModule> page = new Page<SalesModule>();
	private SalesModule entity;
	private FunctionManager functionManager;
	private List<Function> allFunctions;
	private List<Long> checkedFunctionIds;
	private List<Long> functionIds;
	private List<BusinessSystem> allSystems;
	private BusinessSystemManager businessSystemManager;
	private Long systemId;
	private Long id;

	@Override
	public String delete() throws Exception {
		salesModuleManager.deleteSalesModule(id);
		return RELOAD;
	}

	@Override
	public String list() throws Exception {
		page = salesModuleManager.getAllSalesMdule(page);
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void prepareModel() throws Exception {
		if(id != null){
			entity = salesModuleManager.getSalesModule(id);
			checkedFunctionIds = CollectionUtils.fetchPropertyToList(entity.getFunctions(), "id");
			allFunctions = functionManager.getFunctionsBySystem(entity.getSystemId());
			allSystems = new ArrayList<BusinessSystem>();
			BusinessSystem bs = businessSystemManager.getBusiness(entity.getSystemId());
			allSystems.add(bs);
		}else{
			entity = new SalesModule();
			allSystems = businessSystemManager.getAllBusiness();
		}
	}

	@Override
	public String input() throws Exception {
		return INPUT;
	}

	@Override
	public String save() throws Exception {
		CollectionUtils.mergeByCheckedIds(entity.getFunctions(), functionIds, Function.class);
		salesModuleManager.saveSalesModule(entity);
		return RELOAD;
	}

	public SalesModule getModel() {
		return entity;
	}

	@Required
	public void setFunctionManager(FunctionManager functionManager) {
		this.functionManager = functionManager;
	}

	@Required
	public void setSalesModuleManager(SalesModuleManager salesModuleManager) {
		this.salesModuleManager = salesModuleManager;
	}

	public List<Long> getCheckedFunctionIds() {
		return checkedFunctionIds;
	}

	public void setCheckedFunctionIds(List<Long> checkedFunctionIds) {
		this.checkedFunctionIds = checkedFunctionIds;
	}

	public List<Long> getFunctionIds() {
		return functionIds;
	}

	public void setFunctionIds(List<Long> functionIds) {
		this.functionIds = functionIds;
	}

	public List<Function> getAllFunctions() {
		return allFunctions;
	}

	public void setAllFunctions(List<Function> allFunctions) {
		this.allFunctions = allFunctions;
	}

	public Page<SalesModule> getPage() {
		return page;
	}

	public void setPage(Page<SalesModule> page) {
		this.page = page;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<BusinessSystem> getAllSystems() {
		return allSystems;
	}

	public void setAllSystems(List<BusinessSystem> allSystems) {
		this.allSystems = allSystems;
	}

	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}

	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}
	
	public String getFunctions() throws Exception{
		allFunctions = functionManager.getFunctionsBySystem(systemId);
		return INPUT;
	}
	
}
