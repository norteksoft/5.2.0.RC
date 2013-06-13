package com.norteksoft.product.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.Workgroup;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.util.tree.ZTreeNode;

//ztree json util
public class ZTreeUtils{
	//拼节点id时为了防止节点id重复,前面加的前缀
	private static String COMPANY_="company_";
	private static String ALLDEPARTMENT_="allDepartment_";
	private static String DEPARTMENT_="department_";
	private static String ALLWORKGROUP_="allWorkgroup_";
	private static String WORKGROUP_="workgroup_";
	private static String USER_="user_";
	private static String USERHASNOTDEPARTMENT_="userHasNotDepartment_";
	
	//节点类型
	private static String COMPANY="company";//公司
	private static String ALLDEPARTMENT="allDepartment";//所有部门
	private static String DEPARTMENT="department";//部门
	private static String ALLWORKGROUP="allWorkgroup";//所有工作组
	private static String WORKGROUP="workgroup";//工作组
	private static String USER="user";//员工
	private static String USERHASNOTDEPARTMENT="userHasNotDepartment";//无部门节点
	
	//可以考虑在配置文件里配
	private static String DEFAULTTREENODEDATA="name,loginName,email,weight,code,description,shortTitle,summary";
	
	//设置树节点显示内容
	public static String treeNodeShowContent;//线程不安全
	public static void setTreeNodeShowContent(String treeNodeShowContent) {
		ZTreeUtils.treeNodeShowContent = treeNodeShowContent;
	}
	
	//是否显示无部门人员
	private static boolean userWithoutDeptVisible;
	public static void setUserWithoutDeptVisible(boolean userWithoutDeptVisible) {
		ZTreeUtils.userWithoutDeptVisible = userWithoutDeptVisible;
	}
	
	//标准tree的参数
    private static String treeNodeData;//设置树节点data
	public static void setTreeNodeData(String treeNodeData) {
		ZTreeUtils.treeNodeData = treeNodeData;
	}
	
	//是否显示在线人员
	private static boolean onlineVisible;
	public static void setOnlineVisible(boolean onlineVisible) {
		ZTreeUtils.onlineVisible = onlineVisible;
	}
	
	//显示设定部门
	private static String departmentShow;
	public static void setDepartmentShow(String departmentShow) {
		ZTreeUtils.departmentShow = departmentShow;
	}

	/**
	 * ****公司人员树*******************************************************************************************************************
	 */	
	/**
	 * 公司人员树(异步)
	 * COMPANY
	 */
	public static String createCompanyTree(Long companyId,String companyName,String currentId) {
		StringBuilder tree = new StringBuilder();
	
		String[] str = currentId.split("_");
		if (currentId.equals("0")) {
			tree.append(getInitialCompanyTree(companyId,companyName));
		}else if(str[0].equals("department")) {
			tree.append(getNodesOnExpandOneDepartment(Long.parseLong(str[1])));
		}else if(str[0].equals("workgroup")){
			tree.append(getNodesOnExpandOneWorkgroup(Long.parseLong(str[1])));
		}
		
		return tree.toString();
	}

	/**
	 * 只查部门，工作组和没有部门的用户
	 * @param departments
	 * @param usersList
	 * @return
	 */
	private static Object getInitialCompanyTree(Long companyId, String companyName) {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		List<Department> departments = getSettingDepartment(departmentShow);//得到所有一级部门
		List<User> usersList = ApiFactory.getAcsService().getUsersWithoutDepartment();//得到所有无部门人员
		List<Workgroup> workGroups = ApiFactory.getAcsService().getWorkgroups();//得到所有一级工作组
		
		//公司节点
		createCompanyNode(companyId,companyName,treeNodes);
		
		//所有部门节点
		addAllDepartmentNode(companyId.toString(),treeNodes,departments);
		
		//封装无部门人员节点
		addUserHasNotDepartment(companyId.toString(),treeNodes,usersList);
		
		//封装所有工作组节点
		addUserAllWorkgroup(companyId.toString(),treeNodes,workGroups);
		return JsonParser.object2Json(treeNodes);
	}

	//根据部门名字字符串得到部门list 
	private static List<Department> getDepartmentByNameStr(String departmentShow) {
		String[] arr = departmentShow.split(",");
		List<Department> list = new ArrayList<Department>();
		for(String departmentName : arr){
			Department department=ApiFactory.getAcsService().getDepartmentByName(departmentName.trim());
			if(department!=null)
			list.add(department);
		}
		return list;
	}

	//所有部门节点
	private static void addAllDepartmentNode(String companyId , List<ZTreeNode> treeNodes,List<Department> departments) {
		
		//"部门"节点
		createAllDepartmentNode(companyId,treeNodes,departments);
		
		for(Department d : departments){
			List<Department> subDepartments = ApiFactory.getAcsService().getSubDepartmentList(d.getId());//得到子部门 
			List<User> users = ApiFactory.getAcsService().getUsersByDepartmentId(d.getId());//得到部门下的人
			if((subDepartments != null && subDepartments.size() > 0|| users != null && users.size() > 0)){
				createDepartmentNodeOpen(d,ALLDEPARTMENT_+companyId,treeNodes);
			}else{
				createDepartmentNodeClose(d,ALLDEPARTMENT_+companyId,treeNodes);
			}
		}
	}

	//封装无部门人员节点
	private static void addUserHasNotDepartment(String companyId,List<ZTreeNode> treeNodes, List<User> usersList) {
		
		if(userWithoutDeptVisible){
		
		//封装"无部门人员"节点
		createUserHasNotDepartmentNode(companyId,treeNodes,usersList);
		
		//封装人员节点
		createUserNode(usersList,USERHASNOTDEPARTMENT_+companyId,treeNodes);
		}
		
	}
	

	//封装所有工作组节点
	private static void addUserAllWorkgroup(String companyId,List<ZTreeNode> treeNodes, List<Workgroup> workGroups) {
		 
		//封装"工作组"节点
		createAllWorkgroupNode(companyId.toString(),treeNodes,workGroups);
		
		for(Workgroup w : workGroups){
			List<User> users = ApiFactory.getAcsService().getUsersByWorkgroupId(w.getId());
			if(workGroups != null && workGroups.size() > 0&&users != null && users.size() > 0){
				createWorkgroupNodeOpen(w,ALLWORKGROUP_+companyId,treeNodes);
			}else{
				createWorkgroupNodeClose(w,ALLWORKGROUP_+companyId,treeNodes);
			}
		}
		
	}
	
	
	/**
	 * 只查某一部门下的子部门和员工
	 * @param 
	 * @param 
	 * @return
	 */	
	
	private static Object getNodesOnExpandOneDepartment(Long departmentId) {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		
		List<Department> subDepartments = ApiFactory.getAcsService().getSubDepartmentList(departmentId);
		List<User> users = ApiFactory.getAcsService().getUsersByDepartmentId(departmentId);
		
		//封装此部门下员工
		createUserNode(users,DEPARTMENT_+departmentId,treeNodes);
		
		//封装此部门下的子部门
		addSubDepartmentsInTheDepartment(departmentId.toString(),treeNodes,subDepartments);
		
		return JsonParser.object2Json(treeNodes);
	}

	
	//封装此部门下的子部门
	private static void addSubDepartmentsInTheDepartment(String departmentId,List<ZTreeNode> treeNodes, List<Department> subDepartments) {
		for (Department d : subDepartments) {
			List<User> users = ApiFactory.getAcsService().getUsersByDepartmentId(d.getId());
			List<Department> subDepts =ApiFactory.getAcsService().getSubDepartmentList(d.getId());
			if ((users != null && users.size() > 0)||(subDepts != null && subDepts.size() > 0)) {
				
				createDepartmentNodeOpen(d,DEPARTMENT_+departmentId,treeNodes);
				
				//封装员工
				createUserNode(users,DEPARTMENT_+d.getId(),treeNodes);
				
				//递归封装子部门
				addSubDepartmentsInTheDepartment(d.getId().toString(),treeNodes,subDepts);
				
			}else{
				
				createDepartmentNodeClose(d,DEPARTMENT_+departmentId,treeNodes);
				
			}
		}
	}
	
	
	/**
	 * 只查某一工作组下的员工(无子工作组)
	 * @param 
	 * @param 
	 * @return
	 */	
	private static Object getNodesOnExpandOneWorkgroup(long workgroupId) {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		
		List<User> users = ApiFactory.getAcsService().getUsersByWorkgroupId(workgroupId);
		
		//封装员工
		createUserNode(users,WORKGROUP_+workgroupId,treeNodes);
		
		return JsonParser.object2Json(treeNodes);
	}
	/**
	 * ***部门人员树********************************************************************************************************************
	 */	
	
	/**
	 * 部门人员树(异步)
	 * MAN_DEPARTMENT_TREE
	 */
	
	public static String createDepartmentUserTree(Long companyId,String companyName,String currentId) {
		StringBuilder tree = new StringBuilder();
		String[] str = currentId.split("_");
		if (currentId.equals("0")) {
			tree.append(getInitialDepartmentUserTree(companyId,companyName));
		}else if(str[0].equals("department")) {
			tree.append(getNodesOnExpandOneDepartment(Long.parseLong(str[1])));
		}
		return tree.toString();
	}
	
	/**
	 * 只查部门和没有部门的用户
	 * @param onlineVisible 
	 */
	private static Object getInitialDepartmentUserTree(Long companyId,String companyName) {
        List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		
		List<Department> departments = getSettingDepartment(departmentShow);//得到所有一级部门
		
		List<User> usersList = ApiFactory.getAcsService().getUsersWithoutDepartment();//得到所有无部门人员
		//公司节点
		createCompanyNode(companyId,companyName,treeNodes);
		
		//所有部门节点
		addAllDepartmentNode(companyId.toString(),treeNodes,departments);
		
		//封装无部门人员节点
		addUserHasNotDepartment(companyId.toString(),treeNodes,usersList);
		
		return JsonParser.object2Json(treeNodes);
	}
	
	
	/**
	 * ****工作组人员树*******************************************************************************************************************
	 */	
	
	/**
	 * 工作组人员树(异步)
	 * MAN_DEPARTMENT_TREE
	 */
	public static String createWorkgroupUserTree(Long companyId,String companyName, String currentId) {
		StringBuilder tree = new StringBuilder();
		
		String[] str = currentId.split("_");
		if (currentId.equals("0")) {
			tree.append(getInitialWorkgroupUserTree(companyId,companyName));
		}else if(str[0].equals("workgroup")){
			tree.append(getNodesOnExpandOneWorkgroup(Long.parseLong(str[1])));
		}
		
		return tree.toString();
	}
	
	/**
	 * 只查工作组和没有部门的用户
	 * @param onlineVisible 
	 */
	private static Object getInitialWorkgroupUserTree(Long companyId,String companyName) {
        List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		
		List<User> usersList = ApiFactory.getAcsService().getUsersWithoutDepartment();//得到所有无部门人员
		List<Workgroup> workGroups = ApiFactory.getAcsService().getWorkgroups();//得到所有一级工作组
		//公司节点
		createCompanyNode(companyId,companyName,treeNodes);
		
		//封装所有工作组节点
		addUserAllWorkgroup(companyId.toString(),treeNodes,workGroups);
		
		//封装无部门人员节点
		addUserHasNotDepartment(companyId.toString(),treeNodes,usersList);
		
		return JsonParser.object2Json(treeNodes);
	}
	
	/**
	 * ****部门树*******************************************************************************************************************
	 */	
	

	/**
	 * 部门树(一下全部加载)
	 * MAN_DEPARTMENT_TREE
	 */
	public static String createDepartmentsTree(Long companyId,String companyName, String currentId) {
		    List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
			
			//公司节点
		    createCompanyNode(companyId,companyName,treeNodes);
			
			//封装"部门"节点
			List<Department> departments = getSettingDepartment(departmentShow);//得到所有一级部门
			
			createAllDepartmentNode(String.valueOf(companyId),treeNodes,departments);
			
			
			//封装第一层部门节点
			addAllDepartmentAndSubDepartmentNode(companyId.toString(),treeNodes,departments);
			
			
			return JsonParser.object2Json(treeNodes);
	}

	//所有部门及其子部门节点
	private static void addAllDepartmentAndSubDepartmentNode(String companyId,List<ZTreeNode> treeNodes, List<Department> departments) {
		
		//封装第一层部门节点
		for(Department d : departments){
			List<Department> subDepartments = ApiFactory.getAcsService().getSubDepartmentList(d.getId());;//得到子部门 
			if((subDepartments != null && subDepartments.size() > 0)){
				
				createDepartmentNodeOpen(d,ALLDEPARTMENT_+companyId,treeNodes);
				
				//封装子部门
				addSubDepartmentNode(d.getId().toString(),treeNodes,subDepartments);
			
			}else{
				createDepartmentNodeClose(d,ALLDEPARTMENT_+companyId,treeNodes);
			}
		}
		
	}
	
	//递归封装子部门
	private static void addSubDepartmentNode(String departmentId,List<ZTreeNode> treeNodes, List<Department> departments) {
		for(Department d : departments){
			List<Department> subDepartments = ApiFactory.getAcsService().getSubDepartmentList(d.getId());;//得到子部门 
			if((subDepartments != null && subDepartments.size() > 0)){
			   
				createDepartmentNodeOpen(d,DEPARTMENT_+departmentId,treeNodes);
			
				//封装子部门
				addSubDepartmentNode(d.getId().toString(),treeNodes,subDepartments);
			
			}else{
				createDepartmentNodeClose(d,DEPARTMENT_+departmentId,treeNodes);
			}
		}
		
	}
	
	/**
	 * ***工作组树********************************************************************************************************************
	 */	
	
	/**
	 * 工作组树(一下全部加载)
	 * GROUP_TREE
	 */

	public static String createWorkgroupsTree(Long companyId,String companyName, String currentId) {
		    List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
			
			//公司节点
		    createCompanyNode(companyId,companyName,treeNodes);
			
			//封装"工作组节点"
			List<Workgroup> workGroups = ApiFactory.getAcsService().getWorkgroups();
			createAllWorkgroupNode(companyId.toString(),treeNodes,workGroups);
			
			
			//封装第一层工作组节点(只有一层)
			addWorkgroupExceptUser(ALLWORKGROUP_+companyId,treeNodes,workGroups);
			
			return JsonParser.object2Json(treeNodes);
	}

	//封装第一层工作组节点(只有一层)
	private static void addWorkgroupExceptUser(String allWorkgroupId,List<ZTreeNode> treeNodes, List<Workgroup> workGroups) {
		for(Workgroup w : workGroups){
			createWorkgroupNodeClose(w,allWorkgroupId,treeNodes);
		}
		
	}
	/**
	 * ***部门和工作组树********************************************************************************************************************
	 */
	
	
	/**
	 * 部门和工作组树(一下全部加载)
	 * DEPARTMENT_WORKGROUP_TREE
	 */
	public static String createDepartmentsAndWorkgroupsTree(Long companyId,String companyName, String currentId) {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		//公司节点
		createCompanyNode(companyId,companyName,treeNodes);
		
		//封装"部门"节点
		List<Department> departments = getSettingDepartment(departmentShow);//得到所有一级部门
		createAllDepartmentNode(companyId.toString(),treeNodes,departments);
		
		//封装第一层部门节点
		addAllDepartmentAndSubDepartmentNode(companyId.toString(),treeNodes,departments);
		
		//封装"工作组节点"
		List<Workgroup> workGroups = ApiFactory.getAcsService().getWorkgroups();
		createAllWorkgroupNode(companyId.toString(),treeNodes,workGroups);
		
		
		//封装第一层工作组节点(只有一层)
		addWorkgroupExceptUser(ALLWORKGROUP_+companyId,treeNodes,workGroups);
		
		return JsonParser.object2Json(treeNodes);
	}
	
	/**
	 * ***工具/得到各种节点********************************************************************************************************************
	 *
	 */
	
	/**
	 * 得到公司节点
	 * 
	 */
	public static void createCompanyNode(Long companyId,String companyName,List<ZTreeNode> treeNodes) {
		ZTreeNode company = 
			new ZTreeNode(COMPANY_+companyId.toString(), "0", getCompanyNodeName(companyName),"true","true",COMPANY,"{\"name\" : \""+companyName+"\" }","root");
		treeNodes.add(company);
	}
	
	/**
	 * 得到公司节点的显示名称
	 * 
	 */
	private static String getCompanyNodeName(String companyName) {
		JSONArray array = JSONArray.fromObject((treeNodeShowContent.equals("null")||StringUtils.isEmpty(treeNodeShowContent))?"[{'company':'"+companyName+"'}]":treeNodeShowContent);
		JSONObject jsonObj = array.getJSONObject(0);
		if(jsonObj.containsKey(COMPANY)){
			if(StringUtils.isEmpty(jsonObj.getString(COMPANY))){
				return companyName;
			}else{
				return jsonObj.getString(COMPANY);
			}
			
		}
		return null;
	}

	/**
	 * 得到"部门"节点
	 * 
	 */
	public static void createAllDepartmentNode(String companyId,List<ZTreeNode> treeNodes,List<Department> departments) {
		ZTreeNode allDepartmentNode = null;
		if(departments.size()>0){
			allDepartmentNode = 
			new ZTreeNode(ALLDEPARTMENT_+companyId, "company_"+companyId, "部门","true","true",ALLDEPARTMENT,String.valueOf(departments.size()),"department");
		}else{
			allDepartmentNode = 
			new ZTreeNode(ALLDEPARTMENT_+companyId, "company_"+companyId, "部门","false","false",ALLDEPARTMENT,"","department");
		}
		treeNodes.add(allDepartmentNode);
	}
	
	
	/**
	 * 得到"工作组"节点
	 * 
	 */
	public static void createAllWorkgroupNode(String companyId,List<ZTreeNode> treeNodes,List<Workgroup> workGroups) {
		ZTreeNode allWorkgroupNode = null;
		if(workGroups.size()>0){
			allWorkgroupNode = 
			new ZTreeNode(ALLWORKGROUP_+companyId, "company_"+companyId, "工作组","true","true",ALLWORKGROUP,String.valueOf(workGroups.size()),"department");
		}else{
			allWorkgroupNode = 
			new ZTreeNode(ALLWORKGROUP_+companyId, "company_"+companyId, "工作组","false","false",ALLWORKGROUP,"","department");
		}
		treeNodes.add(allWorkgroupNode);
	}
	
	
	/**
	 * 得到"无部门人员"节点
	 * 
	 */
	private static void createUserHasNotDepartmentNode(String companyId,List<ZTreeNode> treeNodes,List<User> users) {
		ZTreeNode userHasNotDepartmentNode = null;
		if(users.size()>0){
			userHasNotDepartmentNode = 
			new ZTreeNode(USERHASNOTDEPARTMENT_+companyId, "company_"+companyId.toString(), "无部门人员","true","true",USERHASNOTDEPARTMENT,String.valueOf(users.size()),"department");
		}
		treeNodes.add(userHasNotDepartmentNode);
	}
	
	
	
	/**
	 * 得到open部门节点
	 * 
	 */
	public static void createDepartmentNodeOpen(Department d,String parentId ,List<ZTreeNode> treeNodes) {
		ZTreeNode department = 
		new ZTreeNode(DEPARTMENT_+d.getId().toString(), parentId, getNodeShowName(d),"true","true",DEPARTMENT,getNodeData(d),"department");
		treeNodes.add(department);
	}
	
	/**
	 * 得到close部门节点
	 * 
	 */
	private static void createDepartmentNodeClose(Department d, String parentId,List<ZTreeNode> treeNodes) {
		ZTreeNode department = 
		new ZTreeNode(DEPARTMENT_+d.getId().toString(), parentId, getNodeShowName(d),"false","false",DEPARTMENT,getNodeData(d),"department");
		treeNodes.add(department);
	}
	
	
	/**
	 * 得到open工作组节点
	 * 
	 */
	public static void createWorkgroupNodeOpen(Workgroup w,String parentId ,List<ZTreeNode> treeNodes) {
		ZTreeNode workgroup = 
		new ZTreeNode(WORKGROUP_+w.getId().toString(), parentId, getNodeShowName(w),"true","true",WORKGROUP,getNodeData(w),"department");
		treeNodes.add(workgroup);
	}
	
	/**
	 * 得到close工作组节点
	 * 
	 */
	private static void createWorkgroupNodeClose(Workgroup w, String parentId,List<ZTreeNode> treeNodes) {
		ZTreeNode workgroup = 
		new ZTreeNode(WORKGROUP_+w.getId().toString(), parentId, getNodeShowName(w),"false","false",WORKGROUP,getNodeData(w),"department");	
		treeNodes.add(workgroup);
	}
	
	/**
	 * 得到员工节点
	 * 
	 */
	private static void createUserNode(List<User> users, String parentId,List<ZTreeNode> treeNodes) {
		ZTreeNode userNode = null;
		
		//得到在线人员
		List<Long> onlineUserIds = new ArrayList<Long>();
		if(onlineVisible)
				onlineUserIds = ApiFactory.getAcsService().getOnlineUserIds();
		
		for(User u : users){
			if(onlineVisible&&onlineUserIds.contains(u.getId())){//显示在线人员
				userNode = 
				new ZTreeNode(USER_+u.getId().toString(), parentId, getNodeShowName(u),"false","false",USER,getNodeData(u),"userOnline");
				treeNodes.add(userNode);
			}else{
				userNode = 
				new ZTreeNode(USER_+u.getId().toString(), parentId, getNodeShowName(u),"false","false",USER,getNodeData(u),"user");
				treeNodes.add(userNode);
			}
		}
	}
	
	/**
	 * 得到节点显示数据
	 * 
	 */
	public static String getNodeShowName(Object obj) {
		String fieldName = "";
		if(!treeNodeShowContent.equals("null")&&!StringUtils.isEmpty(treeNodeShowContent)){
			JSONArray array = JSONArray.fromObject(treeNodeShowContent);
			JSONObject jsonObj = array.getJSONObject(0);
			
			
			if(jsonObj.containsKey(USER)){
				 fieldName = jsonObj.getString(USER);
			}else if(jsonObj.containsKey(DEPARTMENT)){
				 fieldName = jsonObj.getString(DEPARTMENT);
			}else if(jsonObj.containsKey(WORKGROUP)){
				 fieldName = jsonObj.getString(WORKGROUP);
			}
		}
		
		try {
		  if(containTheField(obj,fieldName)){
		      return BeanUtils.getFieldValue(obj,StringUtils.isEmpty(fieldName)?"name":fieldName).toString();
		  }else{
			  return BeanUtils.getFieldValue(obj,"name").toString();
		  }
		   
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	/**
	 * 得到节点数据
	 * 
	 */
	public static String getNodeData(Object obj) {
		StringBuilder json = new StringBuilder("{");
	    String[] str = null;
	    
	    if(StringUtils.isEmpty(treeNodeData)){
		    str = DEFAULTTREENODEDATA.split(",");
	    }else{
		    str = treeNodeData.split(",");
	    }
		
		for(int i =0;i<str.length;i++){
			try {
				if(containTheField(obj,str[i])){
				 json.append("\""+str[i]+"\" : \""+BeanUtils.getFieldValue(obj, str[i])+"\" ,");
				}
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return json.substring(0, json.length()-1)+"}";
	}
	/**
	 * 判断某个类是否含有此属性
	 * 
	 */
	private static boolean containTheField(Object obj,String fieldName) {
		Field[] fields = obj.getClass().getDeclaredFields();
		for(Field field : fields){
			if(fieldName.equals(field.getName())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 得到树的部门
	 * 
	 */
	private static List<Department> getSettingDepartment(String departmentShow) {
		List<Department> departments = null;
		if(!StringUtils.isEmpty(departmentShow)&&!departmentShow.equals("undefined")){
			departments = getDepartmentByNameStr(departmentShow);
		}else{
			departments = ApiFactory.getAcsService().getDepartments();//得到所有一级部门
		}
		return departments;
	}
	
	
}
