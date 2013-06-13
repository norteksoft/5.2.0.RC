package com.norteksoft.acs.web.sale;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.sale.Product;
import com.norteksoft.acs.entity.sale.SalesModule;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.sale.ProductManager;
import com.norteksoft.acs.service.sale.SalesModuleManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.CollectionUtils;

/**
 * ProductAction.java
 * @author Administrator
 */
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "/sale/product.action", type="redirect") })
public class ProductAction extends CRUDActionSupport<Product>{

	private static final long serialVersionUID = 1L;
	private ProductManager productManager;
	private Page<Product> page = new Page<Product>();
	private Product entity;
	private Long id;
	private List<Long> salesModuleIds;
	private List<Long> checkedSalesModuleIds;
	private List<SalesModule> allSalesModules;
	private SalesModuleManager salesModuleManager;
	private BusinessSystemManager businessSystemManager;
	private List<BusinessSystem> allSystems;
	private Long systemId;


	@Override
	public String delete() throws Exception {
		productManager.deleteProduct(id);
		return RELOAD;
	}

	@Override
	public String list() throws Exception {
		productManager.getAllProduct(page);
		return SUCCESS;
	}

	@Override
	public String input() throws Exception {
		return INPUT;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void prepareModel() throws Exception {
		if(id == null){
			entity = new Product();
			allSystems = businessSystemManager.getAllBusiness();
		}else{
			entity = productManager.getProduct(id);
			checkedSalesModuleIds = CollectionUtils.fetchPropertyToList(entity.getSalesModuels(), "id");
			allSystems = new ArrayList<BusinessSystem>();
			BusinessSystem bs = businessSystemManager.getBusiness(entity.getSystemId());
			allSystems.add(bs);
		}
	}

	@Override
	public String save() throws Exception {
		CollectionUtils.mergeByCheckedIds(entity.getSalesModuels(), salesModuleIds, SalesModule.class);
		productManager.saveProduct(entity);
		return RELOAD;
	}

	public Product getModel() {
		return entity;
	}

	@Required
	public void setSalesModuleManager(SalesModuleManager salesModuleManager) {
		this.salesModuleManager = salesModuleManager;
	}

	@Required
	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}

	public Page<Product> getPage() {
		return page;
	}

	public void setPage(Page<Product> page) {
		this.page = page;
	}

	public List<Long> getSalesModuleIds() {
		return salesModuleIds;
	}

	public void setSalesModuleIds(List<Long> salesModuleIds) {
		this.salesModuleIds = salesModuleIds;
	}

	public List<Long> getCheckedSalesModuleIds() {
		return checkedSalesModuleIds;
	}

	public void setCheckedSalesModuleIds(List<Long> checkedSalesModuleIds) {
		this.checkedSalesModuleIds = checkedSalesModuleIds;
	}

	public List<SalesModule> getAllSalesModules() {
		return allSalesModules;
	}

	public void setAllSalesModules(List<SalesModule> allSalesModules) {
		this.allSalesModules = allSalesModules;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getSalesModules(){
		allSalesModules = salesModuleManager.getSalesModulesBySystem(systemId);
		return INPUT;
	}

	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
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

}
