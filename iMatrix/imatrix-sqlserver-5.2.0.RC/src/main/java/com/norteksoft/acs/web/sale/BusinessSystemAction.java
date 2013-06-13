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
import com.norteksoft.acs.entity.authorization.FunctionGroup;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.service.ServiceException;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.FunctionGroupManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.mms.base.data.DataHandle;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.SystemUrls;

/**
 * 业务系统Action
 */
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "/sale/business-system.action", type = "redirect") })
public class BusinessSystemAction extends CRUDActionSupport<BusinessSystem> {

	private static final long serialVersionUID = 4814560124772644966L;

	private BusinessSystemManager businessSystemManager;
	private FunctionGroupManager functionGroupManager;
	private Page<BusinessSystem> page = new Page<BusinessSystem>(15, true);
	private Page<FunctionGroup> pageFunctionGroup = new Page<FunctionGroup>(10, true);
	private BusinessSystem businessSystem;
	private Long id;
	private List<BusinessSystem> allBusinessSystem;
	private String businessName;
	private List<Long> functionGroupIds;
	private Long businessSystemId;
	private List<Long> checkedFunctionGroupIds;
	private String html;
	private boolean isCreate = true;
	private String code;
	private String systemIds;
	private DataHandle dataHandle;
	private CompanyManager companyManager;
	
	private File file;
	private String fileName;
	
	private String imatrixIp;//应用平台部署ip地址
	private String imatrixPort;//应用平台部署的端口号

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	
	public String updateUrlCache() throws Exception{
		SystemUrls.updateUrls();
		return null;
	}

	@Override
	public String delete() throws Exception {
		try {
			businessSystemManager.deleteBusiness(id);
			addActionMessage(getText("common.deleted"));
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
			addActionMessage(e.getMessage());
		}
		return RELOAD;
	}

	@Override
	public String list() throws Exception {
		page = businessSystemManager.getAllSystem(page);
		return SUCCESS;
	}

	/**
	 * 按条件查询业务系
	 */
	
	public void prepareSearch() throws Exception {
		prepareModel();
	}
	public String search() throws Exception {

		page = businessSystemManager.getSearchBusiness(page, businessSystem, false);
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
	 * 业务系统编码校验
	 * 
	 * @return
	 * @throws Exception
	 */
	public String validateSystemCode(){
		if(!code.equals("")){
		BusinessSystem bs=businessSystemManager.getSystemBySystemCode(code);
		if(bs!=null){
			this.renderText("false");
		}else{
			this.renderText("true");
		}
		}
		return null;
	}
	
	
	
	/**
	 * 业务系统添加资源跳转页面
	 * 
	 * @return
	 * @throws Exception
	 */
	
	public void prepareInputFunctionGroup() throws Exception {
		businessSystem = businessSystemManager.getBusiness(businessSystemId);
	}
	
	public String inputFunctionGroup() throws Exception {
		return "function-group";
	}
	
	
	/**
	 * 导出系统
	 * @return
	 * @throws Exception
	 */
	public String exportSystem() throws Exception{
		HttpServletResponse response = ServletActionContext.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode("acs-bussiness-info.xls","UTF-8"));
		dataHandle.exportSystem(response.getOutputStream(),systemIds);
		return null;
	}
	public String showImportSystem() throws Exception{
		return "show-import-system";
	}
	/**
	 * 导入系统
	 * @return
	 * @throws Exception
	 */
	public String importSystem() throws Exception{
		if(fileName==null || !fileName.endsWith(".xls")){
			this.addActionMessage("请选择excel文件格式");
			return "show-import-system";
		}
		boolean success = true;
		try {
			dataHandle.importSystem(file,imatrixIp,imatrixPort,null);
		} catch (Exception e) {
			success = false;
		}
		if(success){
			this.addActionMessage("导入成功");
		}else{
			this.addActionMessage("导入失败，请检查excel文件格式");
		}
		return "show-import-system";
	}
	public String showImportMms() throws Exception{
		return "show-import-mms";
	}
	/**
	 * 是否已经创建了公司
	 * @return
	 * @throws Exception
	 */
	public String validateCompany() throws Exception{
		List<Company> companys=companyManager.getCompanys();
		if(companys.size()<=0){
			this.renderText("false");
		}else{
			this.renderText("true");
		}
		return null;
	}
	
	public String updateFunctionCache() throws Exception{
		businessSystemManager.updateFunctionCache();
		return null;
	}

	public String systemAddFunctionGroup() throws Exception {
		return RELOAD;
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
	public void setDataHandle(DataHandle dataHandle) {
		this.dataHandle = dataHandle;
	}
	@Required
	public void setBusinessSystemManager(
			BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}
	@Required
	public void setCompanyManager(CompanyManager companyManager) {
		this.companyManager = companyManager;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setFileFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setSystemIds(String systemIds) {
		this.systemIds = systemIds;
	}

	public void setImatrixIp(String imatrixIp) {
		this.imatrixIp = imatrixIp;
	}

	public void setImatrixPort(String imatrixPort) {
		this.imatrixPort = imatrixPort;
	}
	
	
}
