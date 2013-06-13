package com.norteksoft.acs.web.organization;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.enumeration.TreeType;
import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.acs.service.organization.WorkGroupManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.TreeUtils;
import com.norteksoft.tags.tree.DepartmentDisplayType;


@SuppressWarnings("deprecation")
@ParentPackage("default")
public class LoadTreeAction extends CRUDActionSupport<Company> {
	private static final long serialVersionUID = 1L;
	private CompanyManager companyManager;
	private DepartmentManager departmentManager;
	private WorkGroupManager workGroupManager;
	private String currentId;
	private String treeType;
	
	public String loadWorkgroupTree(){
		StringBuilder sb = new StringBuilder("[ ");
		if(currentId == null || currentId.trim().length() <= 0) return null;
		//初始化时显示公司根节点和工作站根节点
		if("INITIALIZED".equals(currentId)){
			Company company = companyManager.getCompany(ContextUtils.getCompanyId());
			sb.append(JsTreeUtils.generateJsTreeNodeNew("WORKGROUPS-"+company.getId(), "open", company.getName(), getWorkGroupNodes(company.getId()), ""));
		}
		sb.append(" ]") ;
		this.renderText(sb.toString());
		return null;
	}
	
	/**
	 * 以公司为根节点的树
	 * @return
	 */
	public String loadDepartmentTree(){
		StringBuilder sb = new StringBuilder("[ ");
		if(currentId == null || currentId.trim().length() <= 0) return null;
		//初始化时显示公司根节点和工作站根节点
		Company company = companyManager.getCompany(ContextUtils.getCompanyId());
		if("INITIALIZED".equals(currentId)){
			sb.append(JsTreeUtils.generateJsTreeNodeNew("DEPARTMENTS-" + company.getId(), "open", company.getName(), getDepartmentNodes(company.getId()), ""));
		}else if("INITIALIZED_USERS".equals(currentId)){
			StringBuilder strs=new StringBuilder();
			strs.append(getDepartmentNodes(company.getId()));
			if(StringUtils.isNotEmpty(strs.toString())){
				strs.append(",");
			}
			sb.append(JsTreeUtils.generateJsTreeNodeNew("DEPARTMENTS-" + company.getId(), "open", company.getName(), 
					strs.append(JsTreeUtils.generateJsTreeNodeNew("NODEPARTMENT_USER-"+ company.getId(), "", getText("user.noDepartment"), "")+","+JsTreeUtils.generateJsTreeNodeNew("DELETED_USER-" + company.getId(), "", getText("common.userDelete"),"")).toString(),""));
		}
		sb.append(" ]") ;
		this.renderText(sb.toString());
		return null;
	}
	
	/**
	 * 部门树
	 * @return
	 */
	public String loadDepartment(){
		StringBuilder sb = new StringBuilder("[ ");
		if(currentId == null || currentId.trim().length() <= 0) return null;
		//初始化时显示公司根节点和工作站根节点
		Company company = companyManager.getCompany(ContextUtils.getCompanyId());
		if("INITIALIZED".equals(currentId)){
			sb.append(JsTreeUtils.generateJsTreeNodeNew("DEPARTMENTS-" + company.getId()+"="+company.getName(), "open", company.getName(), getDepartmentNodes2(company.getId()),""));
		}else if("INITIALIZED_USERS".equals(currentId)){
			StringBuilder strs=new StringBuilder();
			strs.append(getDepartmentNodes2(company.getId()));
			if(StringUtils.isNotEmpty(strs.toString())){
				strs.append(",");
			}
			sb.append(JsTreeUtils.generateJsTreeNodeNew("DEPARTMENTS-" + company.getId()+"="+company.getName(), "open", company.getName(), 
					strs.append(JsTreeUtils.generateJsTreeNodeNew("DELETED_USER-" + company.getId()+"="+company.getName(), "", getText("common.userDelete"),"")).toString(), ""));
		}
		sb.append(" ]") ;
		this.renderText(sb.toString());
		return null;
	}
	
	
	
	
	
	
	/**
	 * 生成公司的子公司及部门的树
	 * @param companyId
	 */
	public String getDepartmentNodes(Long companyId){
		Company company = companyManager.getCompany(companyId);
		StringBuilder nodes = new StringBuilder();
		for(Company comp : company.getChildren()){
			//nodes.append(generateJsTreeNode("DEPARTMENTS,"+comp.getId().toString()+"="+comp.getName(), "closed", comp.getName()));
			nodes.append(JsTreeUtils.generateJsTreeNodeNew("DEPARTMENTS-"+comp.getId().toString(), "closed", comp.getName(),""));
			nodes.append(",");
		}
		List<Department> departments = departmentManager.getAllDepartment();
		for(Department d : departments){
			String nodeString = getDepartmentsNodes(d, false);
			if(StringUtils.isNotEmpty(nodeString)){
				nodes.append(nodeString).append(",");
			}
		}
		//去掉最后一个逗号
		if(StringUtils.isNotEmpty(nodes.toString())){
			if(nodes.charAt(nodes.length()-1)==','){
				nodes.delete(nodes.length()-1, nodes.length());
			}
		}
		return nodes.toString();
	}
	public String getDepartmentNodes2(Long companyId){
		Company company = companyManager.getCompany(companyId);
		StringBuilder nodes = new StringBuilder();
		for(Company comp : company.getChildren()){
			nodes.append(JsTreeUtils.generateJsTreeNodeNew("DEPARTMENTS-"+comp.getId().toString()+"="+comp.getName(), "closed", comp.getName(), ""));
			nodes.append(",");
		}
		List<Department> departments = departmentManager.getAllDepartment();
		for(Department d : departments){
			String nodeString = getDepartmentsNodes2(d, false);
			if(StringUtils.isNotEmpty(nodeString)){
				nodes.append(nodeString).append(",");
			}
		}
		//去掉最后一个逗号
		if(StringUtils.isNotEmpty(nodes.toString())){
			if(nodes.charAt(nodes.length()-1)==','){
				nodes.delete(nodes.length()-1, nodes.length());
			}
		}
		return nodes.toString();
	}
	
	private String getDepartmentsNodes(Department dept, boolean isSubDept){
		StringBuilder nodes = new StringBuilder();
		if(dept.getParent() != null && !isSubDept) return "";
		List<Department> subDepts = departmentManager.getSubDeptments(dept.getId());
		if(subDepts.size() > 0){
			StringBuilder subNodes = new StringBuilder();
			//子部门树节点列表
			for(Department d : subDepts){
				if(d.isDeleted()) continue;
				subNodes.append(getDepartmentsNodes(d, true));
				subNodes.append(",");
			}
			//去掉最后一个逗号
			if(StringUtils.isNotEmpty(subNodes.toString())){
				if(subNodes.charAt(subNodes.length()-1)==','){
					subNodes.delete(subNodes.length()-1, subNodes.length());
				}
			}
			//部门树节点
			//nodes.append(JsTreeUtil.generateJsTreeNode("USERSBYDEPARTMENT,"+dept.getId()+"="+dept.getDepartmentName(), "closed", dept.getDepartmentName(), subNodes.toString()));
			nodes.append(JsTreeUtils.generateJsTreeNodeNew("USERSBYDEPARTMENT-"+dept.getId(), "closed", dept.getName(), subNodes.toString(),""));
		}else{
			//nodes.append(JsTreeUtil.generateJsTreeNode("USERSBYDEPARTMENT,"+dept.getId()+"="+dept.getDepartmentName(), "", dept.getDepartmentName(), ""));
			nodes.append(JsTreeUtils.generateJsTreeNodeNew("USERSBYDEPARTMENT-"+dept.getId(), "", dept.getName(), ""));
		}
		return nodes.toString();
	}
	
	private String getDepartmentsNodes2(Department dept, boolean isSubDept){
		StringBuilder nodes = new StringBuilder();
		if(dept.getParent() != null && !isSubDept) return "";
		List<Department> subDepts = departmentManager.getSubDeptments(dept.getId());
		if(subDepts.size() > 0){
			StringBuilder subNodes = new StringBuilder();
			//子部门树节点列表
			for(Department d : subDepts){
				if(d.isDeleted()) continue;
				subNodes.append(getDepartmentsNodes2(d, true));
				subNodes.append(",");
			}
			//去掉最后一个逗号
			if(subNodes.charAt(subNodes.length()-1)==','){
				subNodes.delete(subNodes.length()-1, subNodes.length());
			}
			//部门树节点
			nodes.append(JsTreeUtils.generateJsTreeNodeNew("USERSBYDEPARTMENT-"+dept.getId()+"="+dept.getName(), "closed", dept.getName(), subNodes.toString(),""));
		}else{
			nodes.append(JsTreeUtils.generateJsTreeNodeNew("USERSBYDEPARTMENT-"+dept.getId()+"="+dept.getName(), "", dept.getName(), ""));
		}
		return nodes.toString();
	}
	
	@Required
	public void setDepartmentManager(DepartmentManager departmentManager) {
		this.departmentManager = departmentManager;
	}
	
	public String getWorkGroupNodes(Long companyId){
		List<Workgroup> workGroups = workGroupManager.queryWorkGroupByCompany(ContextUtils.getCompanyId());
		StringBuilder nodes = new StringBuilder();
		for(Workgroup wg: workGroups){
			if(wg.isDeleted()) continue;
			nodes.append(JsTreeUtils.generateJsTreeNodeNew("USERSBYWORKGROUP-"+wg.getId().toString(), "", wg.getName(), ""));
			nodes.append(",");
		}
		if(nodes.lastIndexOf(",") != -1 && nodes.lastIndexOf(",") == nodes.length()-1){
			nodes.replace(nodes.length()-1, nodes.length(), "");
		}
		return nodes.toString();
	}
	
	
	
	//公司人员树
	public String createManCompanyTree() throws Exception {
		renderText(TreeUtils.getCreateManCompanyTree(ContextUtils.getCompanyId(), ContextUtils.getCompanyName(), currentId,false,DepartmentDisplayType.NAME,false));
		return null;
	}
	
	
	
	//部门工作组人员树
	public String createManDepartmentGroupTree(){
		renderText(TreeUtils.getCreateManDepartmentGroupTree(ContextUtils.getCompanyId(),  currentId,false,DepartmentDisplayType.NAME,false));
		return null;
	}
	
	
	
	//部门人员树
	public String createManDepartmentTree(){
		renderText(TreeUtils.getCreateManDepartmentTree(ContextUtils.getCompanyId(),  currentId,false,DepartmentDisplayType.NAME,false));
		return null;
	}
	//工作组人员树
	public String createManGroupTree(){
		renderText(TreeUtils.getCreateManGroupTree(ContextUtils.getCompanyId(),  currentId,false));
		return null;
		
	}
	//部门树
	public String createDepartmentTree(){
		renderText(TreeUtils.getCreateDepartmentTree(ContextUtils.getCompanyId(),  currentId,DepartmentDisplayType.NAME));
		return null;
	}
	//工作组树
	public String createGroupTree(){
		renderText(TreeUtils.getCreateGroupTree(ContextUtils.getCompanyId(),  currentId));
		return null;
	}	
	
	/*public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {    
        ((HttpServletResponse)response).setHeader("Pragma","No-cache");     
        ((HttpServletResponse)response).setHeader("Cache-Control","no-cache");     
        ((HttpServletResponse)response).setHeader("Expires","0");    
        chain.doFilter(request, response);    
    }*/
	
	//标签树
	public String getTree(){
	
		 switch(TreeType.valueOf(treeType)) {
	       case COMPANY:
	    	   renderText(TreeUtils.getCreateManCompanyTree(ContextUtils.getCompanyId(), ContextUtils.getCompanyName(), currentId,false,DepartmentDisplayType.NAME,false));
	    	   break;
	       case MAN_DEPARTMENT_GROUP_TREE:
	    	   renderText(TreeUtils.getCreateManDepartmentGroupTree(ContextUtils.getCompanyId(),  currentId,false,DepartmentDisplayType.NAME,false));
	          break;
	       case MAN_DEPARTMENT_TREE:
	    	   renderText(TreeUtils.getCreateManDepartmentTree(ContextUtils.getCompanyId(),  currentId,false,DepartmentDisplayType.NAME,false));
	          break;
	       case MAN_GROUP_TREE:
	    	   renderText(TreeUtils.getCreateManGroupTree(ContextUtils.getCompanyId(),  currentId,false));
	           break;
	       case DEPARTMENT_TREE:
	    	   renderText(TreeUtils.getCreateDepartmentTree(ContextUtils.getCompanyId(),  currentId,DepartmentDisplayType.NAME));
	         break;
	       case GROUP_TREE:
	    	   renderText(TreeUtils.getCreateGroupTree(ContextUtils.getCompanyId(),  currentId));
	          break;
	       
	       default:  return renderText(TreeUtils.getCreateManCompanyTree(ContextUtils.getCompanyId(), ContextUtils.getCompanyName(), currentId,false,DepartmentDisplayType.NAME,false));
	       }
		
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	

	public String getCurrentId() {
		return currentId;
	}

	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}

	@Required
	public void setCompanyManager(CompanyManager companyManager) {
		this.companyManager = companyManager;
	}
  
	@Required
	public void setWorkGroupManager(WorkGroupManager workGroupManager) {
		this.workGroupManager = workGroupManager;
	}
	
// 继承自父类的方法=======================================================================
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

	public String getTreeType() {
		return treeType;
	}

	public void setTreeType(String treeType) {
		this.treeType = treeType;
	}
	
	
}
