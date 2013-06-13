package com.norteksoft.task.web;

import java.util.List;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.DepartmentUser;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.web.struts2.CrudActionSupport;

@SuppressWarnings("unchecked")
@Namespace("/task")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "tree", type = "redirectAction")})
public class TreeAction extends CrudActionSupport {
	private static final long serialVersionUID = 1L;

	private DepartmentManager departmentManager;
	private String currentId;
	private String tree;
	
	@Override
	public String list() throws Exception {
		return SUCCESS;
	}
	
	public String load() throws Exception{
		StringBuilder tree = new StringBuilder("[ ");
		if("INITIALIZED".equals(currentId)){
			//公司里的部门节点
			StringBuilder subNodes = new StringBuilder();
			List<Department> departments = departmentManager.getAllDepartment();
			for(Department d : departments){
				String nodeString = getDdeptNodes(d);
				if(nodeString.length() > 0)
					subNodes.append(nodeString).append(",");
			}
			subNodes.append(generateJsTreeNode("NODEPARTMENTUS," + ContextUtils.getCompanyId(), 
					"closed", getText("user.noDepartment"), ""));
			if(subNodes.lastIndexOf(",") != -1 && subNodes.lastIndexOf(",") == subNodes.length()-1){
				subNodes.replace(subNodes.length()-1, subNodes.length(), "");
			}
			//公司节点
			tree.append(generateJsTreeNode("", "open", ContextUtils.getCompanyName(), subNodes.toString()));
		}else if(currentId.startsWith("DEPARTMENT")){
			tree.append(getUserNodes(Long.valueOf(currentId.substring(currentId.indexOf(',')+1, currentId.length()))));
		}else if(currentId.startsWith("NODEPARTMENTUS")){
			tree.append(getNoDepartmentUserNodes(Long.valueOf(currentId.substring(currentId.indexOf(',')+1, currentId.length()))));
		}
		tree.append(" ]");
		renderText(tree.toString());
		return null;
	}
	
	/**
	 * 部门节点 
	 */
	private String getDdeptNodes(Department dept){
		StringBuilder nodes = new StringBuilder();
		if(dept.getParent() == null){
			//部门树节点
			nodes.append(generateJsTreeNode("DEPARTMENT," + dept.getId(), "closed", dept.getName(), ""));
		}
		return nodes.toString();
	}
	
	/**
	 * 用户节点 
	 */
	public String getUserNodes(Long deptId) throws Exception{
		StringBuilder nodes = new StringBuilder();
		Department dept = departmentManager.getDepartment(deptId);
		for(Department d : dept.getChildren()){
			nodes.append(getDdeptNodes(d)).append(",");
		}
		for(DepartmentUser du : dept.getDepartmentUsers()){
			if(du.isDeleted()) continue;
			User user = du.getUser();
			if(user.isDeleted()) continue;
			nodes.append(generateJsTreeNode("USER," + user.getId() + "," + user.getLoginName(), "", 
					user.getName(), "")).append(",");
		}
		if(nodes.lastIndexOf(",") != -1 && nodes.lastIndexOf(",") == nodes.length()-1){
			nodes.replace(nodes.length()-1, nodes.length(), "");
		}
		return nodes.toString();
	}
	
	/**
	 * 没有部门的用户的树节点
	 * @param companyId
	 * @return
	 */
	public String getNoDepartmentUserNodes(Long companyId){
		StringBuilder nodes = new StringBuilder();
		ThreadParameters parameters=new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
		List<com.norteksoft.product.api.entity.User> users = ApiFactory.getAcsService().getUsersWithoutDepartment();
		for(com.norteksoft.product.api.entity.User user : users){
			if(user.isDeleted()) continue;
			nodes.append(generateJsTreeNode("USER," + user.getId() + "," + user.getLoginName(), "", 
					user.getName(), "")).append(",");
		}
		if(nodes.lastIndexOf(",") != -1 && nodes.lastIndexOf(",") == nodes.length()-1){
			nodes.replace(nodes.length()-1, nodes.length(), "");
		}
		return nodes.toString();
	}
	
	/**
	 *  生成树的一个NODE
	 * @param id        NODE的id
	 * @param state     NODE的状态   open || closed || ""
	 * @param data      NODE的显示数据
	 * @param children  NODE的子NODE 
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
	
	@Required
	public void setDepartmentManager(DepartmentManager departmentManager) {
		this.departmentManager = departmentManager;
	}

	public String getTree() {
		return tree;
	}

	public void setTree(String tree) {
		this.tree = tree;
	}

	public String getCurrentId() {
		return currentId;
	}

	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}

	@Override
	public String delete() throws Exception {
		return null;
	}

	@Override
	public String input() throws Exception {
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		
	}

	@Override
	public String save() throws Exception {
		return null;
	}

	public Object getModel() {
		return null;
	}

}
