package com.norteksoft.acs.web.organization;

import java.util.List;

import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;

@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "company", type = "redirectAction") })
public class CompanyAction extends CRUDActionSupport<Company> {

	private static final long serialVersionUID = 5612318534208727400L;
	private CompanyManager companyManager;
	private Page<Company> page = new Page<Company>(20, true);
	private Page<Department> page1 = new Page<Department>(20, true);
	private Page<Workgroup> pageW = new Page<Workgroup>(20, true);
	private Page<BusinessSystem> pageB = new Page<BusinessSystem>(20, true);
	private List<Company> companys;
	private Company entity;
	private Long parentId;
	private Long id;
	private Long companyId;
	private BusinessSystemManager businessSystemManager;
	private List<Long> departmentIds;
	private List<Long> workGroupIds;
	private List<Long> businessIds;	

	@Override
	public String list() throws Exception {
		page = companyManager.getAllCompanys(page);
		//companys = companyManager.getAllCompanys();
		return SUCCESS;
	}

	@Override
	public String save() throws Exception {
		companyManager.saveCompany(entity);
		return RELOAD;
	}

	@Override
	public String input() throws Exception {
		return INPUT;
	}

	/**
	 * 新加子公司
	 */
	public String inputSub() throws Exception {
		parentId = companyManager.getCompanyId();
		return "sub-input";
	}

	/**
	 * 保存子公司信息
	 */
	public void prepareSaveSub() throws Exception {
		prepareModel();
	}

	/**
	 * 保存子公司信息
	 */
	public String saveSub() throws Exception {
		Company entity = companyManager.getCompany(ContextUtils.getCompanyId());
		this.entity.setParent(entity);
		this.entity.setCompanyId(ContextUtils.getCompanyId());
		companyManager.saveCompany(this.entity);
		addActionMessage(getText("common.saved"));
		return RELOAD;
	}

	@Override
	public String delete() throws Exception {
		companyManager.deleteCompany(id);
		addActionMessage(getText("common.deleted"));
		return RELOAD;
	}

	/**
	 *公司添加业务系统
	 */
	public String addBusiness() throws Exception {
		pageB = businessSystemManager.getAllBusiness(pageB);
		return "business-system";
	}

	public void prepareSaveBusiness() throws Exception {
		prepareModel();
	}

	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			entity = companyManager.getCompany(id);
		} else {
			entity = new Company();
		}
	}
 
	public List<Long> getBusinessIds() {
		return businessIds;
	}

	public void setBusinessIds(List<Long> businessIds) {
		this.businessIds = businessIds;
	}

	public Page<BusinessSystem> getPageB() {
		return pageB;
	}

	public void setPageB(Page<BusinessSystem> pageB) {
		this.pageB = pageB;
	}

	public Page<Workgroup> getPageW() {
		return pageW;
	}

	public void setPageW(Page<Workgroup> pageW) {
		this.pageW = pageW;
	}

	public Page<Department> getPage1() {
		return page1;
	}

	public void setPage1(Page<Department> page1) {
		this.page1 = page1;
	}
	
	@Required
	public void setCompanyManager(CompanyManager companyManager) {
		this.companyManager = companyManager;
	}

	@Required
	public void setBusinessSystemManager(
			BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public List<Company> getCompanys() {
		return companys;
	}

	public void setCompanys(List<Company> companys) {
		this.companys = companys;
	}

	public Page<Company> getPage() {
		return page;
	}

	public void setPage(Page<Company> page) {
		this.page = page;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Company getModel() {
		return entity;
	}

	public List<Long> getDepartmentIds() {
		return departmentIds;
	}

	public void setDepartmentIds(List<Long> departmentIds) {
		this.departmentIds = departmentIds;
	}

	public void setWorkGroupIds(List<Long> workGroupIds) {
		this.workGroupIds = workGroupIds;
	}

	public List<Long> getWorkGroupIds() {
		return workGroupIds;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
}
