package com.norteksoft.product.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.Workgroup;
import com.norteksoft.acs.web.authorization.JsTreeUtil1;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.util.tree.TreeAttr;
import com.norteksoft.product.util.tree.TreeNode;
import com.norteksoft.tags.tree.DepartmentDisplayType;

public class TreeUtils{
	private static String DEPARTMENT="department";
	private static String WORKGROUP="workGroup";
	private static String NOTINDEPARTMENT="notInDepartment";
	//"_"--->"~~"
	private static String SPLIT_ONE="~~";
	//"="--->"=="
	private static String SPLIT_TWO="==";
	//"-"--->"*#"
	private static String SPLIT_THREE="*#";
	//"|"--->"|#"
	private static String SPLIT_FOUR="|#";
	//"+"--->"+#"
	private static String SPLIT_FIVE="+#";
	//"~"--->"~#"
	private static String SPLIT_SIX="~#";
	//"*"--->"**"
	private static String SPLIT_SEVEN="**";
	//","--->"=#"
	private static String SPLIT_EIGHT="=#";
	/**
	 * 部门和工作组人员树
	 * @param onlineVisible 
	 */
	public static String getCreateManDepartmentGroupTree(Long companyId,String currentId, boolean onlineVisible,DepartmentDisplayType departmentDisplayType,boolean userWithoutDeptVisible) {
		StringBuilder tree = new StringBuilder();
		List<Department> departments = ApiFactory.getAcsService().getDepartments();
		String[] str = currentId.split(SPLIT_ONE);
		if (currentId.equals("0")) {
			tree.append(defaultTreeTwo(departments,companyId,onlineVisible,departmentDisplayType,userWithoutDeptVisible));
		}else if(str[0].equals("department")) {
			tree.append(departmentTreeChange(Long.parseLong(str[1].substring(0,str[1].indexOf("="))),onlineVisible));
		}else if(str[0].equals("workGroup")){
			tree.append(workGroupTree(Long.parseLong(str[1].substring(0,str[1].indexOf("="))),onlineVisible));
		}
		return tree.toString();
	}
	private static String defaultTreeTwo(List<Department> departments,Long companyId,boolean onlineVisible,DepartmentDisplayType departmentDisplayType,boolean userWithoutDeptVisible){
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		
		TreeNode root = new TreeNode(
		new TreeAttr("company"+SPLIT_ONE+"company"+SPLIT_TWO+"全公司"+SPLIT_THREE+"全公司","company"), 
		"open", 
		ContextUtils.getCompanyName());
		
		List<TreeNode> headNode = new ArrayList<TreeNode>();
		
		//封装部门树节点
		TreeNode headDepartmentTreeContent = null;
		if(departments.size()>0){
			headDepartmentTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"open",
			"部门");
		}else{
			headDepartmentTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"",
			"部门");
		}
		//封装部门子节点
		
		//如果显示无部门人员，则封装子节点
		if(userWithoutDeptVisible){
			//封装子节点
			List<TreeNode> childrenTreeNode = new ArrayList<TreeNode>();
			childrenTreeNode.addAll(departmentsTree(departments,departmentDisplayType));
			//封装无部门人员节点
			TreeNode noDepartmentUserTreeContent = new TreeNode(
					new TreeAttr("NODEPARTMENT"+SPLIT_ONE+"0"+SPLIT_TWO,"folder"), 	
					"",
			"无部门人员");
			noDepartmentUserTreeContent.setChildren(usersNotInDepartment());
			childrenTreeNode.add(noDepartmentUserTreeContent);
			headDepartmentTreeContent.setChildren(childrenTreeNode);
		}else{
			headDepartmentTreeContent.setChildren(departmentsTree(departments,departmentDisplayType));
		}
		headNode.add(headDepartmentTreeContent);
		
		//封装工作组树节点
		TreeNode headWorkGroupTreeContent = null;
		List<Workgroup> workGroups = ApiFactory.getAcsService().getWorkgroups();
		if(workGroups.size()>0){
			headWorkGroupTreeContent = new TreeNode(
			new TreeAttr("allWorkGroup"+SPLIT_ONE+"allWorkGroup"+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组","folder"), 	
			"open",
			"工作组");
			
		}else{
			headWorkGroupTreeContent = new TreeNode(
			new TreeAttr("allWorkGroup"+SPLIT_ONE+"allWorkGroup"+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组","folder"), 	
			"",
			"工作组");
		}
		//封装工作组子节点
		headWorkGroupTreeContent.setChildren(workGroupsTree(workGroups, departments,onlineVisible));
		headNode.add(headWorkGroupTreeContent);
		root.setChildren(headNode);
		treeNodes.add(root);
		
		return JsonParser.object2Json(treeNodes);
	}
	
	/**
	 * 部门人员树
	 * @param onlineVisible 
	 */
	
	public static String getCreateManDepartmentTree(Long companyId,String currentId, boolean onlineVisible,DepartmentDisplayType departmentDisplayType,boolean userWithoutDeptVisible) {
		StringBuilder tree = new StringBuilder();
		List<Department> departments = ApiFactory.getAcsService().getDepartments();
		String[] str = currentId.split(SPLIT_ONE);
		if (currentId.equals("0")) {
			tree.append(defaultTreeThree(departments,companyId,departmentDisplayType,userWithoutDeptVisible));
		}else if(str[0].equals("department")) {
			tree.append(departmentTreeChange(Long.parseLong(str[1].substring(0,str[1].indexOf("="))),onlineVisible));
		}else if(currentId.equals("INITIALIZED_USERS")) {
			tree.append(defaultTreeThreeIncludeDeleted(departments,companyId,departmentDisplayType));
		}
		return tree.toString();
	}
	public static String getCreateManDepartmentTreeIncludeDeleted(Long companyId,String currentId,boolean onlineVisible,DepartmentDisplayType departmentDisplayType,boolean userWithoutDeptVisible) {
		StringBuilder tree = new StringBuilder();
		List<Department> departments = ApiFactory.getAcsService().getDepartments();
		String[] str = currentId.split(SPLIT_ONE);
		if (currentId.equals("0")) {
			tree.append(defaultTreeThree(departments,companyId,departmentDisplayType,userWithoutDeptVisible));
		}else if(str[0].equals("department")) {
			tree.append(departmentTreeChange(Long.parseLong(str[1].substring(0,str[1].indexOf("="))),onlineVisible));
		}
		return tree.toString();
	}
	private static String departmentTreeChange(Long departmentId,boolean onlineVisible){
		List<Department> childer = ApiFactory.getAcsService().getSubDepartmentList(departmentId);
		List<User> users = ApiFactory.getAcsService().getUsersByDepartmentId(departmentId);
		Department department=ApiFactory.getAcsService().getDepartmentById(departmentId);
	    List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		//加载此部门下用户
	    List<TreeNode> userTreeNode = usersTree(users,department.getId().toString(),department.getName(),DEPARTMENT,onlineVisible);
	    //加载此部门下的子部门
	    List<TreeNode> childTreeNode = childerTreeChange(childer,onlineVisible);
		
	    treeNodes.addAll(userTreeNode);
	    treeNodes.addAll(childTreeNode);
		return JsonParser.object2Json(treeNodes);
	}
	private static List<TreeNode> childerTreeChange(List<Department> childer,boolean onlineVisible){
		List<TreeNode> childenTreeNodes = new ArrayList<TreeNode>();
		TreeNode childTreeContent = null;
		
		for (Department department : childer) {
			List<User> users1 = ApiFactory.getAcsService().getUsersByDepartmentId(department.getId());
			if (users1 != null && users1.size() > 0) {
				List<Department> subDepts =ApiFactory.getAcsService().getSubDepartmentList(department.getId());
					childTreeContent = new TreeNode(
					new TreeAttr("department"+SPLIT_ONE+ department.getId()+SPLIT_TWO+department.getName()+SPLIT_THREE+department.getName(),"folder"), 	
					"closed",
					department.getName());
					
					//封装子节点
					List<TreeNode> childenSubTreeNodes = new ArrayList<TreeNode>();
					
//					 childenSubTreeNodes.addAll(usersTree(users1,department.getId().toString(),department.getName(),DEPARTMENT,onlineVisible));
					 //递归
					 childenSubTreeNodes.addAll(childerTreeChange(subDepts,onlineVisible));
					 
					childTreeContent.setChildren(childenSubTreeNodes);
					childenTreeNodes.add(childTreeContent);	
					
			}else {
				// 如果子部门下没有人员，则不显示(可以将下面代码注释)
				List<Department> subDepts =ApiFactory.getAcsService().getSubDepartmentList(department.getId());
					childTreeContent = new TreeNode(
					new TreeAttr("department"+SPLIT_ONE+ department.getId()+SPLIT_TWO+department.getName()+SPLIT_THREE+department.getName(),"folder"), 	
					"",
					department.getName());
					
					//封装子节点
					List<TreeNode> childenSubTreeNodes = new ArrayList<TreeNode>();
					//递归
					childenSubTreeNodes.addAll(childerTreeChange(subDepts,onlineVisible));
					 
					childTreeContent.setChildren(childenSubTreeNodes);
					childenTreeNodes.add(childTreeContent);	
			}
		}
		return childenTreeNodes;
	}
	


	
	private static String defaultTreeThree(List<Department> departments,Long companyId,DepartmentDisplayType departmentDisplayType,boolean userWithoutDeptVisible){
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		TreeNode root = new TreeNode(
		new TreeAttr("company"+SPLIT_ONE+"company"+SPLIT_TWO+"全公司"+SPLIT_THREE+"全公司","company"), 
		"open", 
		ContextUtils.getCompanyName());
		
		List<TreeNode> headNode = new ArrayList<TreeNode>();
		TreeNode headTreeContent = null;
		if(departments.size()>0){
			
			headTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"open",
			"部门");
			
		}else{
			
			headTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"",
			"部门");
			
		}
		//如果显示无部门人员，则封装子节点
		if(userWithoutDeptVisible){
			//封装子节点
			List<TreeNode> childrenTreeNode = new ArrayList<TreeNode>();
			childrenTreeNode.addAll(departmentsTree(departments,departmentDisplayType));
			//封装无部门人员节点
			TreeNode noDepartmentUserTreeContent = new TreeNode(
					new TreeAttr("NODEPARTMENT"+SPLIT_ONE+"0"+SPLIT_TWO,"folder"), 	
					"",
			"无部门人员");
			noDepartmentUserTreeContent.setChildren(usersNotInDepartment());
			childrenTreeNode.add(noDepartmentUserTreeContent);
			headTreeContent.setChildren(childrenTreeNode);
		}else{
			headTreeContent.setChildren(departmentsTree(departments,departmentDisplayType));
		}
		headNode.add(headTreeContent);
		root.setChildren(headNode);
		treeNodes.add(root);
		return JsonParser.object2Json(treeNodes);
	}
	private static String defaultTreeThreeIncludeDeleted(List<Department> departments,Long companyId,DepartmentDisplayType departmentDisplayType){
		
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		
		TreeNode root = new TreeNode(
		new TreeAttr("company"+SPLIT_ONE+"company"+SPLIT_TWO+"全公司"+SPLIT_THREE+"全公司","company"), 
		"open", 
		ContextUtils.getCompanyName());
		
		List<TreeNode> headNode = new ArrayList<TreeNode>();
		TreeNode headTreeContent = null;
		if(departments.size()>0){
		    
		    headTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"open",
			"部门");
		}else{
			headTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"",
			"部门");
		}
		//封装子节点
		List<TreeNode> childrenTreeNode = new ArrayList<TreeNode>();
		childrenTreeNode.addAll(departmentsTree(departments,departmentDisplayType));
		//封装无部门人员节点
		TreeNode noDepartmentUserTreeContent = new TreeNode(
		new TreeAttr("NODEPARTMENT"+SPLIT_ONE+"0"+SPLIT_TWO,""), 	
		"",
		"无部门人员");
		noDepartmentUserTreeContent.setChildren(usersNotInDepartment());
		childrenTreeNode.add(noDepartmentUserTreeContent);
		
		headTreeContent.setChildren(childrenTreeNode);
		headNode.add(headTreeContent);
		root.setChildren(headNode);
		treeNodes.add(root);
		return JsonParser.object2Json(treeNodes);
	}
	
	public static String generateJsTreeNodeNewUser(String id, String state, String data,String rel,Long companyId){
		StringBuilder node = new StringBuilder();
		List<User> userInfoList = ApiFactory.getAcsService().getUsersWithoutDepartment();
		node.append("{ \"attr\": {").append(JsTreeUtil1.treeAttrBefore).append(id).append(JsTreeUtil1.treeAttrMiddle).append(rel).append(JsTreeUtil1.treeAttrAfter).append("}");
		if(state != null && !"".equals(state.trim())){
			node.append(",\"state\" : \"").append(state).append("\"");
		}
		node.append(", \"data\": \"").append(data).append("\" ,\"children\":[");
		node.append(delComma(usersTree(userInfoList)));
		node.append("]},");
		return node.toString();
	}
	
	private static String usersTree(List<User> usersList){
		StringBuilder tree = new StringBuilder();
		for (User user : usersList) {
			tree.append(JsTreeUtil1.generateJsTreeNodeNew("user"+SPLIT_ONE+user.getId()+SPLIT_TWO+user.getName()+SPLIT_THREE+user.getLoginName()+SPLIT_FOUR+"nondepartment"+SPLIT_FIVE+"nondepartment"+SPLIT_SIX+"0"+SPLIT_EIGHT+user.getEmail()+SPLIT_SEVEN+user.getHonorificName()+SPLIT_SEVEN+user.getWeight(), "", user.getName(),"user")).append(",");
		}
		return tree.toString();
	}
	
	/**
	 * 工作组人员树
	 * @param onlineVisible 
	 */
	
	public static String getCreateManGroupTree(Long companyId,String currentId, boolean onlineVisible) {
		StringBuilder tree = new StringBuilder();
		
		List<Department> departments = ApiFactory.getAcsService().getDepartments();
		String[] str = currentId.split(SPLIT_ONE);
		if (currentId.equals("0")) {
			tree.append(defaultTreeFour(departments,companyId,onlineVisible));
		}else if(str[0].equals("workGroup")){
			tree.append(workGroupTree(Long.parseLong(str[1].substring(0,str[1].indexOf("="))),onlineVisible));
		}
		return tree.toString();
	}
	private static String defaultTreeFour(List<Department> departments,Long companyId,boolean onlineVisible){
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		
		TreeNode root = new TreeNode(
		new TreeAttr("company"+SPLIT_ONE+"company"+SPLIT_TWO+"全公司"+SPLIT_THREE+"全公司","company"), 
		"open", 
		ContextUtils.getCompanyName());
		
		List<Workgroup> workGroups = ApiFactory.getAcsService().getWorkgroups();
		List<TreeNode> headNode = new ArrayList<TreeNode>();
		TreeNode headTreeContent = null;
		if(workGroups.size()>0){
			headTreeContent = new TreeNode(
			new TreeAttr("allWorkGroup"+SPLIT_ONE+"allWorkGroup"+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组","folder"), 	
			"open",
			"工作组");
		}else{
			headTreeContent = new TreeNode(
			new TreeAttr("allWorkGroup"+SPLIT_ONE+"allWorkGroup"+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组","folder"), 	
			"",
			"工作组");
			
		}
		headTreeContent.setChildren(workGroupsTree(workGroups, departments,onlineVisible));
		headNode.add(headTreeContent);
		root.setChildren(headNode);
		treeNodes.add(root);
		return JsonParser.object2Json(treeNodes);
	}
	
	/**
	 * 部门树
	 */
	public static String getCreateDepartmentTree(Long companyId,String currentId,DepartmentDisplayType departmentDisplayType) {
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		TreeNode root = new TreeNode(
		new TreeAttr("company"+SPLIT_ONE+"company"+SPLIT_TWO+"全公司"+SPLIT_THREE+"全公司","company"), 
		"open", 
		ContextUtils.getCompanyName());
		
		List<Department> departments = ApiFactory.getAcsService().getDepartments();
		if (currentId.equals("0")) {
			root.setChildren(defaultTreeFive(departments,companyId,departmentDisplayType));
			treeNodes.add(root);
		}
		return JsonParser.object2Json(treeNodes);
	}
	
	private static List<TreeNode> defaultTreeFive(List<Department> departments,Long companyId,DepartmentDisplayType departmentDisplayType){
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		
		TreeNode headTreeContent = null;
		if(departments.size()>0){
		    headTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"open",
			"部门");
		}else{
			headTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"",
			"部门");
		}
		headTreeContent.setChildren(departmentsOnlyTree(departments,departmentDisplayType));
		treeNodes.add(headTreeContent);
		return treeNodes;
	}
	public static String getCreateDepartmentTreeIncludeDeleted(Long companyId,String currentId,DepartmentDisplayType departmentDisplayType) {
		StringBuilder tree = new StringBuilder();
		tree.append("[");
		tree.append("{\"attr\":{").append(JsTreeUtil1.treeAttrBefore).append("company"+SPLIT_ONE+"company"+SPLIT_TWO+"全公司"+SPLIT_THREE+"全公司").append(JsTreeUtil1.treeAttrMiddle).append("company").append(JsTreeUtil1.treeAttrAfter).append("},\"state\":\"open\",\"data\":\""+ContextUtils.getCompanyName() + "\",\"children\":");
		List<Department> departments = ApiFactory.getAcsService().getDepartments();
		if(currentId.equals("INITIALIZED_USERS")) {
			tree.append(defaultTreeFiveIncludeDeleted(departments,companyId));
		}else if (currentId.equals("INITIALIZED")) {
			tree.append(defaultTreeFive(departments,companyId,departmentDisplayType));
		}
		tree.append("}");
		tree.append("]");
		return tree.toString();
	}
//wj
	private static String defaultTreeFiveIncludeDeleted(List<Department> departments,Long companyId){
		StringBuilder tree = new StringBuilder();
		tree.append("[");
		if(departments.size()>0){
		tree.append("{\"attr\":{").append(JsTreeUtil1.treeAttrBefore).append("allDepartment"+SPLIT_ONE+companyId+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门").append(JsTreeUtil1.treeAttrMiddle).append("folder").append(JsTreeUtil1.treeAttrAfter).append("},\"state\":\"open\",\"data\":\""+ "部门" + "\",\"children\":[");
		}else{
			tree.append("{\"attr\":{").append(JsTreeUtil1.treeAttrBefore).append("allDepartment"+SPLIT_ONE+companyId+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门").append(JsTreeUtil1.treeAttrMiddle).append("folder").append(JsTreeUtil1.treeAttrAfter).append("},\"state\":\"\",\"data\":\""+ "部门" + "\",\"children\":[");
		}
		//tree.append(delComma(departmentsOnlyTree(departments)));
		tree.append("]},");
		tree.append(JsTreeUtil1.generateJsTreeNodeNew("NODEPARTMENT"+SPLIT_ONE+"0"+SPLIT_TWO,"","无部门人员","")).append(",");
		tree.append(JsTreeUtil1.generateJsTreeNodeNew("DELETED"+SPLIT_ONE+"0"+SPLIT_TWO,"","已删除用户",""));
		tree.append("]");
		return tree.toString();
	}
	
	private static List<TreeNode> departmentsOnlyTree(List<Department> departments,DepartmentDisplayType departmentDisplayType){
		List<TreeNode> departmentNode = new ArrayList<TreeNode>();
		for (Department department : departments) {
			departmentNode.add(generatSubDeptNode(department,departmentDisplayType));
		}
		return departmentNode;
	}

	private static TreeNode generatSubDeptNode(Department department,DepartmentDisplayType departmentDisplayType){
		if(departmentDisplayType == null) departmentDisplayType = DepartmentDisplayType.NAME;
		String deptDisplayInfor = "";
		switch (departmentDisplayType) {
		case CODE:
			deptDisplayInfor = department.getCode();
			break;
		case NAME:
			deptDisplayInfor = department.getName();
			break;
		case SHORTTITLE:
			deptDisplayInfor = department.getShortTitle();
			break;
		case SUMMARY:
			deptDisplayInfor = department.getSummary();
			break;
		default:
			deptDisplayInfor = department.getName();
			break;
		}
		TreeNode departmentTreeContent = null;
		List<Department> subDepts = ApiFactory.getAcsService().getSubDepartmentList(department.getId());
		if(subDepts.isEmpty()){
				departmentTreeContent = new TreeNode(
				new TreeAttr("department"+SPLIT_ONE+department.getId()+SPLIT_TWO+deptDisplayInfor+SPLIT_THREE+department.getName(),"folder"), 	
				"open",
				deptDisplayInfor);
		}else{
				List<TreeNode> subDepartmentNode = new ArrayList<TreeNode>();
				for(Department subDept : subDepts){
					subDepartmentNode.add(generatSubDeptNode(subDept,departmentDisplayType));
				}
				departmentTreeContent = new TreeNode(
				new TreeAttr("department"+SPLIT_ONE+department.getId()+SPLIT_TWO+deptDisplayInfor+SPLIT_THREE+department.getName(),"folder"), 	
				"open",
				deptDisplayInfor);
				departmentTreeContent.setChildren(subDepartmentNode);
		}
		return departmentTreeContent;
	}
	
	/**
	 * 部门工作组树
	 */
	public static String getCreateDepartmentWorkgroupTree(Long companyId,String currentId,DepartmentDisplayType departmentDisplayType) {
		StringBuilder tree = new StringBuilder();
		List<Department> departments = ApiFactory.getAcsService().getDepartments();
		String[] str = currentId.split(SPLIT_ONE);
		if (currentId.equals("0")) {
			tree.append(defaultTreeSeven(departments,companyId,departmentDisplayType));
		}else if(str[0].equals("department")) {
			tree.append(departmentTreeSeven(Long.parseLong(str[1].substring(0,str[1].indexOf("=")))));
		}
		return tree.toString();
	}
	
	private static String defaultTreeSeven(List<Department> departments,Long companyId,DepartmentDisplayType departmentDisplayType){
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		
		TreeNode root = new TreeNode(
		new TreeAttr("company"+SPLIT_ONE+"company"+SPLIT_TWO+"全公司"+SPLIT_THREE+"全公司","company"), 
		"open", 
		ContextUtils.getCompanyName());
		
		List<TreeNode> headNode = new ArrayList<TreeNode>();
		//封装部门节点
		TreeNode headDepartmentTreeContent = null;
		if(departments.size()>0){
		    headDepartmentTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"open",
			"部门");
		}else{
		    headDepartmentTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"",
			"部门");
		}
		headDepartmentTreeContent.setChildren(departmentsOnlyTree(departments,departmentDisplayType));
		headNode.add(headDepartmentTreeContent);
		
		//封装工作组节点
		List<Workgroup> workGroups = ApiFactory.getAcsService().getWorkgroups();
		
		TreeNode headWorkGroupTreeContent = null;
		if(workGroups.size()>0){
			headWorkGroupTreeContent = new TreeNode(
			new TreeAttr("allWorkGroup"+SPLIT_ONE+"allWorkGroup"+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组","folder"), 	
			"open",
			"工作组");
			headWorkGroupTreeContent.setChildren(workGroupsTreeSeven(workGroups, departments));
			headNode.add(headWorkGroupTreeContent);
		}else{
			headWorkGroupTreeContent = new TreeNode(
			new TreeAttr("allWorkGroup"+SPLIT_ONE+"allWorkGroup"+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组","folder"), 	
			"",
			"工作组");
			headNode.add(headWorkGroupTreeContent);
		}
		root.setChildren(headNode);
		treeNodes.add(root);
		return JsonParser.object2Json(treeNodes);
	}
	
	private static List<TreeNode> workGroupsTreeSeven(List<Workgroup> workGroups,List<Department> departments){
		List<TreeNode> workGroupTreeNodes = new ArrayList<TreeNode>();
		
		TreeNode workGroupTreeContent = null;
		for (Workgroup workGroup : workGroups) {
			workGroupTreeContent = new TreeNode(
			new TreeAttr("workGroup"+SPLIT_ONE+ workGroup.getId()+SPLIT_TWO+workGroup.getName()+SPLIT_THREE+workGroup.getName(),"folder"), 	
			"",
			workGroup.getName());
			workGroupTreeNodes.add(workGroupTreeContent);
		}
		return workGroupTreeNodes;
	}
	
	private static String departmentTreeSeven(Long departmentId){
		List<Department> childer = ApiFactory.getAcsService().getSubDepartmentList(departmentId);
		return JsonParser.object2Json(childerTreeSeven(childer));
	}
	
	private static List<TreeNode> childerTreeSeven(List<Department> childer){
		List<TreeNode> departmentTreeNode = new ArrayList<TreeNode>();
		TreeNode departmentTreeContent = null;
		for (Department department : childer) {
			departmentTreeContent = new TreeNode(
			new TreeAttr("department"+SPLIT_ONE+ department.getId()+SPLIT_TWO+department.getName()+SPLIT_THREE+department.getName(),"folder"), 	
			"closed",
			department.getName());
			departmentTreeNode.add(departmentTreeContent);
		}
		return departmentTreeNode;
	}
	
	
	/**
	 * 工作组树
	 */	
	public static String getCreateGroupTree(Long companyId,String currentId) {
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		List<Workgroup> workGroups = ApiFactory.getAcsService().getWorkgroups();
		TreeNode root = null;
		if(currentId.equals("INITIALIZED")){
				root = new TreeNode(
				new TreeAttr("company"+SPLIT_ONE+"company"+SPLIT_TWO+"全公司"+SPLIT_THREE+"全公司","company"), 
				"open", 
				ContextUtils.getCompanyName());
				//封装子工作组
				root.setChildren(defaultTreeSix(workGroups));
				treeNodes.add(root);
		}else{
			if(workGroups.size()>0){
				root = new TreeNode(
				new TreeAttr("company"+SPLIT_ONE+"company"+SPLIT_TWO+"全公司"+SPLIT_THREE+"全公司","company"), 
				"open", 
				ContextUtils.getCompanyName());
				if (currentId.equals("0")) {
					root.setChildren(defaultTreeSix(workGroups));
				}
				treeNodes.add(root);
			}else{
				root = new TreeNode(
				new TreeAttr("company"+SPLIT_ONE+"company"+SPLIT_TWO+"全公司"+SPLIT_THREE+"全公司","company"), 
				"", 
				ContextUtils.getCompanyName());
				treeNodes.add(root);
			}
		}
		
		return JsonParser.object2Json(treeNodes);
	}
	
	
	
	private static List<TreeNode> defaultTreeSix(List<Workgroup> workGroups){
		List<TreeNode> workGroupTreeNodes = new ArrayList<TreeNode>();
		TreeNode headTreeContent = null;
		if(workGroups.size()>0){
			headTreeContent = new TreeNode(
			new TreeAttr("allWorkGroup"+SPLIT_ONE+"allWorkGroup"+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组","folder"), 	
			"open",
			"工作组");
			headTreeContent.setChildren(workGroupsOnlyTree(workGroups));
		}else{
			headTreeContent = new TreeNode(
			new TreeAttr("allWorkGroup"+SPLIT_ONE+"allWorkGroup"+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组","folder"), 	
			"",
			"工作组");
		}
		workGroupTreeNodes.add(headTreeContent);
		return workGroupTreeNodes;
	}
	
	private static List<TreeNode> workGroupsOnlyTree(List<Workgroup> workGroups){
		List<TreeNode> workGroupsTreeNodes = new ArrayList<TreeNode>();
		TreeNode workGroupsTreeContent = null;
		for (Workgroup workGroup : workGroups) {
			workGroupsTreeContent = new TreeNode(
			new TreeAttr("workGroup"+SPLIT_ONE+ workGroup.getId()+SPLIT_TWO+workGroup.getName()+SPLIT_THREE+workGroup.getName(),"folder"), 	
			"",
			workGroup.getName());
			workGroupsTreeNodes.add(workGroupsTreeContent);
		}
		return workGroupsTreeNodes;
	}
	
	
	
	
	
	/**
	 * 公司人员树
	 * @param onlineVisible 
	 */
	public static String getCreateManCompanyTree(Long companyId,String companyName,String currentId, boolean onlineVisible,DepartmentDisplayType departmentDisplayType,boolean userWithoutDeptVisible) {
		StringBuilder tree = new StringBuilder();
		List<Department> departments = ApiFactory.getAcsService().getDepartments();
		List<User> usersList = ApiFactory.getAcsService().getUsersWithoutDepartment();
		String[] str = currentId.split(SPLIT_ONE);
		if (currentId.equals("0")) {
			tree.append(defaultTree(companyName,departments,usersList,companyId,onlineVisible,departmentDisplayType,userWithoutDeptVisible));
		}else if(str[0].equals("department")) {
			tree.append(departmentTreeChange(Long.parseLong(str[1].substring(0,str[1].indexOf("="))),onlineVisible));
		}else if(str[0].equals("workGroup")){
			tree.append(workGroupTree(Long.parseLong(str[1].substring(0,str[1].indexOf("="))),onlineVisible));
		}
		return tree.toString();
	}
	
	
	/**
	 * 只查部门，工作组和没有部门的用户
	 * @param departments
	 * @param usersList
	 * @return
	 */

	private static String defaultTree(String companyName,List<Department> departments,List<User> usersList,Long companyId,boolean onlineVisible,DepartmentDisplayType departmentDisplayType,boolean userWithoutDeptVisible){
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		TreeNode root = new TreeNode(
		new TreeAttr("company"+SPLIT_ONE+"company"+SPLIT_TWO+"全公司"+SPLIT_THREE+"全公司","company"), 
		"open", 
		ContextUtils.getCompanyName());
		
		//封装部门子节点
		List<TreeNode> headDepartmentNode = new ArrayList<TreeNode>();
		TreeNode headDepartmentTreeContent = null;
		if(departments.size()>0){
			headDepartmentTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"open",
			"部门");
		}else{
			headDepartmentTreeContent = new TreeNode(
			new TreeAttr("allDepartment"+SPLIT_ONE+"allDepartment"+SPLIT_TWO+"所有部门"+SPLIT_THREE+"部门","folder"), 	
			"",
			"部门");
		}
		
		//如果显示无部门人员，则封装子节点
		if(userWithoutDeptVisible){
			//封装子节点
			List<TreeNode> childrenTreeNode = new ArrayList<TreeNode>();
			childrenTreeNode.addAll(departmentsTree(departments,departmentDisplayType));
			//封装无部门人员节点
			TreeNode noDepartmentUserTreeContent = new TreeNode(
					new TreeAttr("NODEPARTMENT"+SPLIT_ONE+"0"+SPLIT_TWO,"folder"), 	
					"",
			"无部门人员");
			noDepartmentUserTreeContent.setChildren(usersNotInDepartment());
			childrenTreeNode.add(noDepartmentUserTreeContent);
			headDepartmentTreeContent.setChildren(childrenTreeNode);
		}else{
			headDepartmentTreeContent.setChildren(departmentsTree(departments,departmentDisplayType));
		}
		
		headDepartmentNode.add(headDepartmentTreeContent);
		
		//封装工作组子节点
		List<Workgroup> workGroups = ApiFactory.getAcsService().getWorkgroups();
		List<TreeNode> headWorkGroupNode = new ArrayList<TreeNode>();
		TreeNode headWorkGroupTreeContent = null;
		if(workGroups.size()>0){
			headWorkGroupTreeContent = new TreeNode(
			new TreeAttr("allWorkGroup"+SPLIT_ONE+"allWorkGroup"+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组","folder"), 	
			"open",
			"工作组");
		}else{
			headWorkGroupTreeContent = new TreeNode(
			new TreeAttr("allWorkGroup"+SPLIT_ONE+"allWorkGroup"+SPLIT_TWO+"工作组"+SPLIT_THREE+"工作组","folder"), 	
			"",
			"工作组");
		}
		headWorkGroupTreeContent.setChildren(workGroupsTree(workGroups, departments,onlineVisible));
		headWorkGroupNode.add(headWorkGroupTreeContent);
		
		List<TreeNode> togetherTreeNode = new ArrayList<TreeNode>();
		togetherTreeNode.addAll(headDepartmentNode);
		togetherTreeNode.addAll(headWorkGroupNode);
		root.setChildren(togetherTreeNode);
		treeNodes.add(root);
		return JsonParser.object2Json(treeNodes);
	}
	
	
	
	/**
	 * 只查部门
	 * @param departments
	 * @param usersList
	 * @return
	 */
	private static List<TreeNode> departmentsTree(List<Department> departments,DepartmentDisplayType departmentDisplayType){
		List<TreeNode> departmentsTreeNode = new ArrayList<TreeNode>();
		TreeNode childTreeContent = null;
		String deptDisplayInfor="";
		if(departmentDisplayType == null) departmentDisplayType = DepartmentDisplayType.NAME;
		for (Department department : departments) {
			List<Department> childer = ApiFactory.getAcsService().getSubDepartmentList(department.getId());
			List<User> users = ApiFactory.getAcsService().getUsersByDepartmentId(department.getId());
			switch (departmentDisplayType) {
			case CODE:
				deptDisplayInfor = department.getCode();
				break;
			case NAME:
				deptDisplayInfor = department.getName();
				break;
			case SHORTTITLE:
				deptDisplayInfor = department.getShortTitle();
				break;
			case SUMMARY:
				deptDisplayInfor = department.getSummary();
				break;
			default:
				deptDisplayInfor = department.getName();
				break;
			}
			if ((childer != null && childer.size() > 0|| users != null && users.size() > 0)) {
				childTreeContent = new TreeNode(
				new TreeAttr("department"+SPLIT_ONE+ department.getId()+SPLIT_TWO+deptDisplayInfor+SPLIT_THREE+department.getName(),"folder"), 	
				"closed",
				deptDisplayInfor);
			}else{
				childTreeContent = new TreeNode(
				new TreeAttr("department"+SPLIT_ONE+ department.getId()+SPLIT_TWO+deptDisplayInfor+SPLIT_THREE+department.getName(),"folder"), 	
				"",
				deptDisplayInfor);
			}
			departmentsTreeNode.add(childTreeContent);
		}
		return departmentsTreeNode;
	}
	
	/**
	 * 只查工作组
	 * @param departments
	 * @param usersList
	 * @return
	 */
	
	private static List<TreeNode> workGroupsTree(List<Workgroup> workGroups,List<Department> departments,boolean onlineVisible){
		List<TreeNode> workGroupsChildNode = new ArrayList<TreeNode>();
		TreeNode workGroupsChildContent = null;
		for (Workgroup workGroup : workGroups) {
			List<User> users = ApiFactory.getAcsService().getUsersByWorkgroupId(workGroup.getId());
			if (workGroups != null && workGroups.size() > 0&&users != null && users.size() > 0) {
				workGroupsChildContent = new TreeNode(
				new TreeAttr("workGroup"+SPLIT_ONE+workGroup.getId()+SPLIT_TWO+workGroup.getName()+SPLIT_THREE+workGroup.getName(),"folder"), 	
				"closed",
				workGroup.getName());
				//加载工作组下面人员
				//workGroupsChildContent.setChildren(usersTree(users,workGroup.getId().toString(),workGroup.getName(),WORKGROUP,onlineVisible));
			}else{
				workGroupsChildContent = new TreeNode(
				new TreeAttr("workGroup"+SPLIT_ONE+workGroup.getId()+SPLIT_TWO+workGroup.getName()+SPLIT_THREE+workGroup.getName(),"folder"), 	
				"",
				workGroup.getName());
			}
			workGroupsChildNode.add(workGroupsChildContent);
		}
		return workGroupsChildNode;
	}
	
		
//	/**
//	 * 不属于任何部门的用户
//	 * @param departments
//	 * @param usersList
//	 * @return
//	 */
//	private static String usersNotInDeptTree(List<User> usersList){
//		StringBuilder tree = new StringBuilder();
//		tree.append(JsTreeUtil.generateJsTreeNodeNew("usersNotIndept_usersNotIndept=无部门人员", "closed", "无部门人员",generateUsersNotInDept(usersList),"folder")).append(",");
//		
//		return tree.toString();
//	}
	
//	private static String generateUsersNotInDept(List<User> usersList){
//		StringBuilder tree = new StringBuilder();
//		for (User user : usersList) {
//			tree.append(JsTreeUtil.generateJsTreeNodeNew("user_" +user.getId() + "="+ user.getName()+"-"+user.getLoginName(), "", user.getName(),"user")).append(",");
//		}
//		return tree.toString();
//	}	
	

	/**
	 * 2查部门及下面的用户和子部门
	 * @param departments
	 * @param usersList
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String departmentTree(Long departmentId,boolean onlineVisible){
		List<Department> childer = ApiFactory.getAcsService().getSubDepartmentList(departmentId);
		List<User> users = ApiFactory.getAcsService().getUsersByDepartmentId(departmentId);
		Department department=ApiFactory.getAcsService().getDepartmentById(departmentId);
		StringBuilder tree = new StringBuilder();
		tree.append("[");
		tree.append(delComma(usersTree(users,department.getId().toString(),department.getName(),DEPARTMENT,onlineVisible)+childerTree(childer)));
		tree.append("]");
		return tree.toString();
	}
	
	private static List<TreeNode> usersTree(List<User> usersList,String departId,String name,String type,boolean onlineVisible){
		List<TreeNode> usersTreeNodes = new ArrayList<TreeNode>();
		TreeNode userTreeContent = null;
		List<Long> onlineUserIds = ApiFactory.getAcsService().getOnlineUserIds();
		for (User user : usersList) {
			if(onlineVisible){
				if(onlineUserIds.contains(user.getId())){
					userTreeContent = new TreeNode(
							new TreeAttr("user"+SPLIT_ONE+user.getId()+SPLIT_TWO+ user.getName()+SPLIT_THREE+user.getLoginName()+SPLIT_FOUR+type+SPLIT_FIVE+name+SPLIT_SIX+departId+SPLIT_EIGHT+user.getEmail()+SPLIT_SEVEN+user.getHonorificName()+SPLIT_SEVEN+user.getWeight(),"onlineUser"), 	
							"",
							user.getName());
							usersTreeNodes.add(userTreeContent);
				}else{
					userTreeContent = new TreeNode(
							new TreeAttr("user"+SPLIT_ONE+user.getId()+SPLIT_TWO+ user.getName()+SPLIT_THREE+user.getLoginName()+SPLIT_FOUR+type+SPLIT_FIVE+name+SPLIT_SIX+departId+SPLIT_EIGHT+user.getEmail()+SPLIT_SEVEN+user.getHonorificName()+SPLIT_SEVEN+user.getWeight(),"user"), 	
							"",
							user.getName());
							usersTreeNodes.add(userTreeContent);
				}
			}else{
				userTreeContent = new TreeNode(
						new TreeAttr("user"+SPLIT_ONE+user.getId()+SPLIT_TWO+ user.getName()+SPLIT_THREE+user.getLoginName()+SPLIT_FOUR+type+SPLIT_FIVE+name+SPLIT_SIX+departId+SPLIT_EIGHT+user.getEmail()+SPLIT_SEVEN+user.getHonorificName()+SPLIT_SEVEN+user.getWeight(),"user"), 	
						"",
						user.getName());
						usersTreeNodes.add(userTreeContent);
			}
		}
		return usersTreeNodes;
	}
	
    //无部门用户
	private static List<TreeNode> usersNotInDepartment(){
		List<User> usersList = ApiFactory.getAcsService().getUsersWithoutDepartment();
		List<TreeNode> usersTreeNodes = new ArrayList<TreeNode>();
		TreeNode userTreeContent = null;
		for (User user : usersList) {
			userTreeContent = new TreeNode(
			new TreeAttr("user"+SPLIT_ONE+user.getId()+SPLIT_TWO+ user.getName()+SPLIT_THREE+user.getLoginName()+SPLIT_FOUR+NOTINDEPARTMENT+SPLIT_FIVE+NOTINDEPARTMENT+SPLIT_SIX+NOTINDEPARTMENT+SPLIT_EIGHT+user.getEmail()+SPLIT_SEVEN+user.getHonorificName()+SPLIT_SEVEN+user.getWeight(),"user"), 	
			"",
			user.getName());
			usersTreeNodes.add(userTreeContent);
		}
		return usersTreeNodes;
	}
	private static String childerTree(List<Department> childer){
		StringBuilder tree = new StringBuilder();
		for (Department department : childer) {
			List<User> users1 = ApiFactory.getAcsService().getUsersByDepartmentId(department.getId());
			if (users1 != null && users1.size() > 0) {
				tree.append(JsTreeUtil1.generateJsTreeNodeNew("department"+SPLIT_ONE+ department.getId()+SPLIT_TWO+department.getName()+SPLIT_THREE+department.getName(), "closed", department.getName(),"folder")).append(",");
			}else {
				// 如果子部门下没有人员，则不显示(可以将下面代码注释)
				tree.append(JsTreeUtil1.generateJsTreeNodeNew("department"+SPLIT_ONE+ department.getId()+SPLIT_TWO+department.getName()+SPLIT_THREE+department.getName(), "", department.getName(),"folder")).append(",");
			}
		}
		return tree.toString();
	}
		
	
	/**
	 * 3查工作组及下面的用户
	 * @param departments
	 * @param usersList
	 * @return
	 */
	private static String workGroupTree(Long workGroupId,boolean onlineVisible){
		List<User> users = ApiFactory.getAcsService().getUsersByWorkgroupId(workGroupId);
		Workgroup group=ApiFactory.getAcsService().getWorkgroupById(workGroupId);
		return JsonParser.object2Json(usersTree(users,group.getId().toString(),group.getName(),WORKGROUP,onlineVisible));
	}
	
	
	/**
	 * 去逗号
	 * @param str
	 * @return
	 */
	private static String delComma(String str){
		if(StringUtils.endsWith(str, ","))str= str.substring(0,str.length() - 1);
		return str;
	}	
}
