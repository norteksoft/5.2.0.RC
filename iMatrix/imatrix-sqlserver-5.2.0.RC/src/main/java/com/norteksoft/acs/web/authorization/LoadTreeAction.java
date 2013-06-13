package com.norteksoft.acs.web.authorization;

import java.util.List;

import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.FunctionGroup;
import com.norteksoft.acs.entity.authorization.RoleGroup;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.product.util.ContextUtils;

@ParentPackage("default")
public class LoadTreeAction extends CRUDActionSupport<Company> {
	private static final long serialVersionUID = 1L;
	private BusinessSystemManager businessSystemManager;
	private String currentId;
	private String treeType;
	
	public String loadSystemTree(){
		StringBuilder tree = new StringBuilder("[ ");
		//初始化时显示公司根节点和工作站根节点
		if("INITIALIZED".equals(currentId)){
			List<BusinessSystem> businessSystems = businessSystemManager.getAllBusiness();
			for(BusinessSystem bs : businessSystems){
				tree.append(generateJsTreeNode("BUSINESSSYSTEM,"+bs.getId(), "open", bs.getName(), ""/*getDefaultNodes(bs.getId())*/));
				tree.append(",");
			}
			if(tree.lastIndexOf(",") != -1 && tree.lastIndexOf(",") == tree.length()-1){
				tree.replace(tree.length()-1, tree.length(), "");
			}
		}
//		else if(currentId.startsWith("ROLEGROUPS")){
//			tree.append(getRoleGroupNodes(Long.valueOf(currentId.substring(currentId.indexOf(',')+1, currentId.length()))));
//		}else if(currentId.startsWith("FUNCTIONGROUPS")){
//			tree.append(getFuncGroupNodes(Long.valueOf(currentId.substring(currentId.indexOf(',')+1, currentId.length()))));
//		}
		tree.append(" ]") ;
		this.renderText(tree.toString());
		return null;
	}
	
	/**
	 * 角色组列表
	 */
	private String getRoleGroupNodes(Long id){
		BusinessSystem businessSystem = businessSystemManager.getBusiness(id);
		StringBuilder nodes = new StringBuilder();
		for(RoleGroup rg : businessSystem.getRoleGroups()){
			if(rg.isDeleted())
				if(rg.getCompanyId().equals(ContextUtils.getCompanyId())){
					nodes.append(generateJsTreeNode("ROLESBYROLEGROUP,"+rg.getId(), "", rg.getName(), ""));
					nodes.append(",");
				}
		}
		if(nodes.lastIndexOf(",") != -1 && nodes.lastIndexOf(",") == nodes.length()-1){
			nodes.replace(nodes.length()-1, nodes.length(), "");
		}
		return nodes.toString();
	}
	
	/**
	 * 资源组列表
	 */
	private String getFuncGroupNodes(Long id){
		BusinessSystem businessSystem = businessSystemManager.getBusiness(id);
		StringBuilder nodes = new StringBuilder();
		for(FunctionGroup fg :businessSystem.getFunctionGroups()){
			if(fg.isDeleted()){
				nodes.append(generateJsTreeNode("FUNCTIONS,"+fg.getId(), "", fg.getName(), ""));
				nodes.append(",");
			}
		}
		if(nodes.lastIndexOf(",") != -1 && nodes.lastIndexOf(",") == nodes.length()-1){
			nodes.replace(nodes.length()-1, nodes.length(), "");
		}
		return nodes.toString();
	}
	
	private String getDefaultNodes(Long id){
		StringBuilder nodes = new StringBuilder();
		nodes.append(generateJsTreeNode("ROLEGROUPS,"+id.toString(), "closed", getText("roleGroup.roleGroup"), ""));
		nodes.append(",").append(generateJsTreeNode("STANDARDROLESBYSYS,"+id.toString(), "", getText("role.standardRole"), ""));
		nodes.append(",").append(generateJsTreeNode("CUSTOMROLESBYSYS,"+id.toString(), "", getText("role.customRole"), ""));
		nodes.append(",").append(generateJsTreeNode("FUNCTIONGROUPS,"+id.toString(), "closed", getText("functionGroup.functionGroup"), ""));
		nodes.append(",").append(generateJsTreeNode("FUNCTIONSBYSYS,"+id.toString(), "", getText("function.functionList"), ""));
		return nodes.toString();
	}
	

	
	/**
	 * 生成JSon格式的树节点
	 * @param id      id
	 * @param state   open or closed
	 * @param data    显示数据
	 * @param children 子节点
	 * @return
	 */
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
	
	
	
	public String getTreeType() {
		return treeType;
	}

	public void setTreeType(String treeType) {
		this.treeType = treeType;
	}

	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
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

	public Company getModel() {
		return null;
	}
}
