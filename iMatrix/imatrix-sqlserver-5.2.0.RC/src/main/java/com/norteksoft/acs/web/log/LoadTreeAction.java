package com.norteksoft.acs.web.log;

import java.util.List;

import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.log.Log;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.product.util.ContextUtils;

@ParentPackage("default")
public class LoadTreeAction extends CRUDActionSupport<Log> {

	private static final long serialVersionUID = 1L;
	private CompanyManager companyManager;
	private BusinessSystemManager businessSystemManager;
	private String currentId;
	
	public String loadLogTree(){
		StringBuilder tree = new StringBuilder("[ ");
		if("INITIALIZED".equals(currentId)){
			tree.append(getCompanyNodes(ContextUtils.getCompanyId()));
		}else if(currentId.startsWith("COMPANY")){
			Company company = companyManager.getCompany(Long.valueOf(currentId.substring(currentId.indexOf(',')+1, currentId.length())));
			tree.append(getLeafNodes(company));
		}
		tree.append(" ]") ;
		renderText(tree.toString());
		return null;
	}
	
	private String getCompanyNodes(Long companyId){
		Company company = companyManager.getCompany(companyId);
		StringBuilder nodes = new StringBuilder();
		nodes.append(generateJsTreeNode("COMPANY,"+company.getId(), "open", company.getName(), getLeafNodes(company)));
		return nodes.toString();
	}
	
	private String getLeafNodes(Company company){
		StringBuilder nodes = new StringBuilder();
		nodes.append(generateJsTreeNode("USERLOGINLOGS,"+company.getId(), "", getText("log.loginLog"), ""));
		List<BusinessSystem> systems = businessSystemManager.getAllBusiness();
		for(BusinessSystem sys : systems){
			nodes.append(",").append(generateJsTreeNode("SYSLOGS,"+sys.getId(), "", sys.getName(), ""));
		}
		for(Company c : company.getChildren()){
			nodes.append(",").append(generateJsTreeNode("COMPANY,"+c.getId(), "closed", c.getName(), ""));
		}
		return nodes.toString();
	}
	
	protected String generateJsTreeNode(String id, String state, String data, String children){
		StringBuilder node = new StringBuilder();
		node.append("{ attributes: { id : \"").append(id).append("\" }");
		if(state != null && !"".equals(state.trim())){
			node.append(",state : \"").append(state).append("\"");
		}
		node.append(", data: \"").append(data).append("\" ");
		if(children != null && !"".equals(children.trim())){
			node.append(", children : [").append(children).append("]");
		}
		node.append("}");
		return node.toString();
	}

	public String getCurrentId() {
		return currentId;
	}

	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}
	
	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}

	@Required
	public void setCompanyManager(CompanyManager companyManager) {
		this.companyManager = companyManager;
	}
	
	@Override
	public String delete() throws Exception {
		return null;
	}

	@Override
	public String list() throws Exception {
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		
	}

	@Override
	public String save() throws Exception {
		return null;
	}

	public Log getModel() {
		return null;
	}

}
