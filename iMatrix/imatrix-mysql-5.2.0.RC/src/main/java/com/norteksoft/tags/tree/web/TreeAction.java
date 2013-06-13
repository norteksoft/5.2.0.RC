package com.norteksoft.tags.tree.web;



import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.Workgroup;
import com.norteksoft.product.api.impl.AcsServiceImpl;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.TreeUtils;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.tags.tree.DepartmentDisplayType;
import com.norteksoft.tags.tree.LeafPage;
import com.norteksoft.tags.tree.TreeType;



@Namespace("/tree")
@ParentPackage("default")
public class TreeAction extends CRUDActionSupport<Company> {
	private static final long serialVersionUID = 1L;
	private Log log=LogFactory.getLog(TreeAction.class);
	private String currentId;
	private String searchValue;
	
	//tree的参数
	private TreeType treeType;//树的类型
    private boolean multiple=false;//是否是多选树
    private String hiddenInputId;//页面上隐藏的input框
    private String showInputId;//页面上显示的input框
    private String loginNameId;//登录名的input框
    private String treeTypeJson;//多页签内容json
    private boolean leafPage=false;//是否多页签
    private String defaultTreeValue;//无页签树的返回值设置
    private String isAppend;//是否追加
    private String formId;
    private String mode;//模式
    private boolean onlineVisible;//是否显示在线人员（标准树）
    private DepartmentDisplayType departmentDisplayType;//树的显示类型
    private boolean userWithoutDeptVisible;//无部门人员是否显示
    
    //移除
    private String removeStaffJson;//要移除的内容json
    
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
	public String list() throws Exception {
		getTree(onlineVisible);
		return null;
	}


	@Override
	protected void prepareModel() throws Exception {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public String save() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	//删除选择结果
	public String removeOptionTree() throws Exception {
		//List<RemoveStaff> opinions = createRemoveStaffList(removeStaffJson);
		//List<RemoveStaff> opinions = new ArrayList<RemoveStaff>();
		HttpServletResponse response = Struts2Utils.getResponse();
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		writer.print(readRemoveScriptTemplate("removeOptionTree.ftl"));
		return null;
	}
	/*private List<RemoveStaff> createRemoveStaffList(String removeJson) {
		RemoveStaff removeStaff = null;
		JSONArray array = JSONArray.fromObject(removeJson);
		JSONObject obj = null;
		for(int i = 0; i < array.size(); i++){
			obj = array.getJSONObject(i);
			removeStaff= new RemoveStaff();
			removeStaff.setName(obj.getString("name"));
			removeStaff.setId(obj.getString("id"));
			removeStaff.setType(obj.getString("type"));
			list.add(removeStaff);
		}
		return list;
		return JsonParser.json2List(RemoveStaff.class, removeJson);
	}*/
	
	public String readRemoveScriptTemplate(String TemplateName ) throws Exception{
		String webapp = Struts2Utils.getRequest().getContextPath();
		String theme=ContextUtils.getTheme();
		String resourceCtx=PropUtils.getProp("host.resources");
		Map<String, Object> root=new HashMap<String, Object>();
		//root.put("opinions", null);
		root.put("hiddenInputId", hiddenInputId);
		root.put("showInputId", showInputId);
		root.put("loginNameId", loginNameId);
		root.put("treeType", treeType);
		root.put("resourceCtx",StringUtils.isEmpty(resourceCtx)?webapp:resourceCtx);
		root.put("theme",StringUtils.isEmpty(theme)?"black":theme);
		String result = TagUtil.getContent(root, "tree/"+TemplateName);
		return result;
	}

	public Company getModel() {
		// TODO Auto-generated method stub
		return null;
	}
	//弹出树
	public String popTree() throws Exception {
		HttpServletResponse response = Struts2Utils.getResponse();
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		String actionUrl = "";
		String searchUrl = "";
		String acsUrl = PropUtils.getProp("host.app");
		if(departmentDisplayType==null) departmentDisplayType = DepartmentDisplayType.NAME;
		if(leafPage){
		   actionUrl = acsUrl+"/portal/tree.action?onlineVisible="+onlineVisible+"&departmentDisplayType="+departmentDisplayType+"&userWithoutDeptVisible="+userWithoutDeptVisible;
	   	   searchUrl = acsUrl+"/portal/search-tree.action";
		}else{
		   actionUrl = acsUrl+"/portal/tree.action?treeType="+treeType+"&onlineVisible="+onlineVisible+"&departmentDisplayType="+departmentDisplayType+"&userWithoutDeptVisible="+userWithoutDeptVisible;
   		   searchUrl = acsUrl+"/portal/search-tree.action?treeType="+treeType;
		}
   		if(multiple){
   			writer.print(readScriptTemplate("multipleTreeNew.ftl",actionUrl,searchUrl,hiddenInputId));
   		}else{
   			writer.print(readScriptTemplate("singleTreeNew.ftl",actionUrl,searchUrl,hiddenInputId));
   		}
		return null;
	}
	
	public String readScriptTemplate(String TempletName ,String actionUrl,String searchUrl,String hiddenInputId) throws Exception{
		log.debug("TempletName="+TempletName+",actionUrl="+actionUrl);
		String webapp = Struts2Utils.getRequest().getContextPath();
		
		List<LeafPage> leafPageList = new ArrayList<LeafPage>();
		String defaultTreeType = "COMPANY";
		if(leafPage){
		    leafPageList = createLeafPageList(treeTypeJson,leafPageList);
		    if(!leafPageList.isEmpty()){
			    defaultTreeType = leafPageList.get(0).getType();
			    defaultTreeValue = StringUtils.isEmpty(leafPageList.get(0).getValue())?"id":leafPageList.get(0).getValue();
		    }
		}
		String theme=ContextUtils.getTheme();
		String resourceCtx=PropUtils.getProp("host.resources");
		Map<String, Object> root=new HashMap<String, Object>();
		root.put("ctx", webapp);
		root.put("actionUrl", actionUrl);
		root.put("searchUrl", searchUrl);
		root.put("treeId", "treeId");
		root.put("inputId", "");
		root.put("treeType", treeType);
		root.put("hiddenInputId", StringUtils.isEmpty(hiddenInputId)?"defaultId":hiddenInputId);
		root.put("showInputId", showInputId);
		root.put("loginNameId", StringUtils.isEmpty(loginNameId)?"noLoginName":loginNameId);
		root.put("formId", StringUtils.isEmpty(formId)?"noFormId":formId);
		root.put("mode", StringUtils.isEmpty(mode)?"noMode":mode);
		root.put("leafPageList", leafPageList);
		root.put("defaultTreeType", defaultTreeType);
		root.put("defaultTreeValue", defaultTreeValue);
		root.put("leafPage", String.valueOf(leafPage));
		root.put("resourceCtx",StringUtils.isEmpty(resourceCtx)?webapp:resourceCtx);
		root.put("theme",StringUtils.isEmpty(theme)?"black":theme);
		root.put("isAppend",StringUtils.isEmpty(isAppend)?"true":isAppend);
		String result = TagUtil.getContent(root, "tree/"+TempletName);
		return result;
	}
	
	
	private List<LeafPage> createLeafPageList(String treeTypeJson,List<LeafPage> leafPageList) {
	/*	LeafPage leafPagess = null;
		JSONArray array = JSONArray.fromObject(treeTypeJson);
		JSONObject obj = null;
		for(int i = 0; i < array.size(); i++){
			obj = array.getJSONObject(i);
			leafPagess= new LeafPage();
			leafPagess.setName(obj.getString("name"));
			leafPagess.setType(obj.getString("type"));
			leafPagess.setValue(obj.getString("value"));
			leafPageList.add(leafPagess);
		}
		return leafPageList;*/
		return JsonParser.json2List(LeafPage.class, treeTypeJson);
	}

	//标签树
	public String getTree(boolean onlineVisible) throws Exception{
		if(treeType==null){
			treeType = TreeType.valueOf(Struts2Utils.getParameter("treeType"));
		}
		if(departmentDisplayType==null) departmentDisplayType = DepartmentDisplayType.NAME;
		switch(treeType) {
	       case COMPANY:
	    	   log.debug("进入TreeAction,COMPANY");
	    	   renderText(TreeUtils.getCreateManCompanyTree(ContextUtils.getCompanyId(), ContextUtils.getCompanyName(), currentId,onlineVisible,departmentDisplayType,userWithoutDeptVisible));
	    	   break;
	       case MAN_DEPARTMENT_GROUP_TREE:
	    	   log.debug("进入TreeAction,MAN_DEPARTMENT_GROUP_TREE");
	    	   renderText(TreeUtils.getCreateManDepartmentGroupTree(ContextUtils.getCompanyId(),currentId,onlineVisible,departmentDisplayType,userWithoutDeptVisible));
	    	   break;
	       case MAN_DEPARTMENT_TREE:
	    	   log.debug("进入TreeAction,MAN_DEPARTMENT_TREE");
	    	  renderText(TreeUtils.getCreateManDepartmentTree(ContextUtils.getCompanyId(),  currentId,onlineVisible,departmentDisplayType,userWithoutDeptVisible));
	    	  break;
	       case MAN_GROUP_TREE:
	    	   log.debug("进入TreeAction,MAN_GROUP_TREE");
	    	   renderText(TreeUtils.getCreateManGroupTree(ContextUtils.getCompanyId(),  currentId,onlineVisible));
	    	   break;
	       case DEPARTMENT_TREE:
	    	   log.debug("进入TreeAction,DEPARTMENT_TREE");
	    	   renderText(TreeUtils.getCreateDepartmentTree(ContextUtils.getCompanyId(),  currentId,departmentDisplayType));
	    	   break;
	       case GROUP_TREE:
	    	   log.debug("进入TreeAction,GROUP_TREE");
	    	   renderText(TreeUtils.getCreateGroupTree(ContextUtils.getCompanyId(), currentId));
	    	   break;
	       case DEPARTMENT_WORKGROUP_TREE:
	    	   log.debug("进入TreeAction,DEPARTMENT_WORKGROUP_TREE");
	    	   renderText(TreeUtils.getCreateDepartmentWorkgroupTree(ContextUtils.getCompanyId(), currentId,departmentDisplayType));
	    	   break;
	       default:  return renderText(TreeUtils.getCreateManCompanyTree(ContextUtils.getCompanyId(), ContextUtils.getCompanyName(), currentId,onlineVisible,departmentDisplayType,userWithoutDeptVisible));
	       }
		
		return null;
	}
	
	//标签树
	public String searchTree() throws Exception{
		if(treeType==null){
			treeType = TreeType.valueOf(Struts2Utils.getParameter("treeType"));
		}
		switch(treeType) {
	       case COMPANY:
	    	   log.debug("进入TreeAction,COMPANY");
	    	   StringBuilder company = new StringBuilder();
	    	   company.append(delComma(getDept().append(getWorkGroup()).toString()));
	    	   renderText("["+company.toString()+"]");
	    	   break;
	       case MAN_DEPARTMENT_GROUP_TREE:
	    	   log.debug("进入TreeAction,MAN_DEPARTMENT_GROUP_TREE");
	    	  StringBuilder result = new StringBuilder();
	    	  result.append(delComma(getDept().append(getWorkGroup()).toString()));
	    	  renderText("["+result.toString()+"]");
	          break;
	       case MAN_DEPARTMENT_TREE:
	    	   log.debug("进入TreeAction,MAN_DEPARTMENT_TREE");
	    	   StringBuilder dt = new StringBuilder();
	    	   dt.append(delComma(getDept().toString()));
	    	   renderText("["+dt.toString()+"]");
	          break;
	       case MAN_GROUP_TREE:
	    	   log.debug("进入TreeAction,MAN_GROUP_TREE");
	    	   StringBuilder gt = new StringBuilder();
	    	   gt.append(delComma(getWorkGroup().toString()));
	    	   renderText("["+gt.toString()+"]");
	           break;
	       case DEPARTMENT_TREE:
	         break;
	       case GROUP_TREE:
	          break;
	       case DEPARTMENT_WORKGROUP_TREE:
	          break;
	       default:    
	    	   StringBuilder ct = new StringBuilder();
		       ct.append(delComma(getDept().append(getWorkGroup()).toString()));
	    	   renderText("["+ct.toString()+"]");
	       }
		return null;
	}
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
	
	private StringBuilder getDept(){
		StringBuilder result = new StringBuilder();
		AcsServiceImpl acsService = (AcsServiceImpl)ContextUtils.getBean("acsServiceImpl");
		List<Department> depts= acsService.getDepartmentsByUserLike(ContextUtils.getCompanyId(), searchValue);
		List<Long> deptIds = new ArrayList<Long>();
		for(int i=0;i<depts.size();i++){
			Department dept = depts.get(i);
			Long deptId = dept.getId();
			if(!deptIds.contains(deptId)){
				String deptDisplayInfor = getDeptDisplayInfo(dept);
				result.append("\"");
				result.append("department");
				result.append(SPLIT_ONE);
				result.append(dept.getId());
				result.append(SPLIT_TWO);
				result.append(deptDisplayInfor);
				result.append(SPLIT_THREE);
				result.append(deptDisplayInfor);
				result.append(";");
				Department parentDept = ApiFactory.getAcsService().getParentDepartment(dept.getId());
				if(parentDept!=null){
					deptDisplayInfor = getDeptDisplayInfo(parentDept);
					result.append("department");
					result.append(SPLIT_ONE);
					result.append(parentDept.getId());
					result.append(SPLIT_TWO);
					result.append(deptDisplayInfor);
					result.append(SPLIT_THREE);
					result.append(deptDisplayInfor);
				}
				result.append("\"");
				result.append(",");
				deptIds.add(dept.getId());
			}
		}
  	  return result;
	}
	
	private String getDeptDisplayInfo(Department dept){
		String deptDisplayInfor = "";
		if(departmentDisplayType == null)  departmentDisplayType = DepartmentDisplayType.NAME;
		switch (departmentDisplayType) {
		case CODE:
			deptDisplayInfor = dept.getCode();
			break;
		case NAME:
			deptDisplayInfor = dept.getName();
			break;
		case SHORTTITLE:
			deptDisplayInfor = dept.getShortTitle();
			break;
		case SUMMARY:
			deptDisplayInfor = dept.getSummary();
			break;
		default:
			deptDisplayInfor = dept.getName();
			break;
		}
		return deptDisplayInfor;
	}
	
	private StringBuilder getWorkGroup(){
	  StringBuilder result = new StringBuilder();
  	  List<Workgroup> wgs=ApiFactory.getAcsService().getWorkGroupsByUserLike(ContextUtils.getCompanyId(), searchValue);
  	  for(int i=0;i<wgs.size();i++){
  		result.append("\"");
  		  result.append("workGroup");
  		  result.append(SPLIT_ONE);
  		  result.append(wgs.get(i).getId());
  		  result.append(SPLIT_TWO);
  		  result.append(wgs.get(i).getName());
  		  result.append(SPLIT_THREE);
  		  result.append(wgs.get(i).getName());
  		  result.append(";");
  		  result.append("\"");
		  result.append(",");
  	  }
  	  return result;
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

	public void setMultiple(String multiple) {
		if("true".equals(multiple)){
			this.multiple =true;
		}else{
			this.multiple =false;
		}
	}


	public void setLeafPage(String leafPage) {
		if("true".equals(leafPage)){
			this.leafPage = true;
		}else{
			this.leafPage = false;
		}
		
	}
	
	public String customTree() throws Exception{
		String resourceCtx=PropUtils.getProp("host.resources");
		HttpServletRequest request=Struts2Utils.getRequest();
		request.setAttribute("resourceCtx",resourceCtx);
		String theme=ContextUtils.getTheme();
		request.setAttribute("theme",StringUtils.isEmpty(theme)?"black":theme);
		return "success";
	}
	
	

	public TreeType getTreeType() {
		return treeType;
	}

	public void setTreeType(String treeType) {
		this.treeType =TreeType.valueOf(treeType) ;
	}

	public String getHiddenInputId() {
		return hiddenInputId;
	}

	public void setHiddenInputId(String hiddenInputId) {
		this.hiddenInputId = hiddenInputId;
	}

	public String getShowInputId() {
		return showInputId;
	}

	public void setShowInputId(String showInputId) {
		this.showInputId = showInputId;
	}

	public String getTreeTypeJson() {
		return treeTypeJson;
	}

	public void setTreeTypeJson(String treeTypeJson) {
		this.treeTypeJson = treeTypeJson;
	}

	public String getDefaultTreeValue() {
		return defaultTreeValue;
	}

	public void setDefaultTreeValue(String defaultTreeValue) {
		this.defaultTreeValue = defaultTreeValue;
	}

	public String getIsAppend() {
		return isAppend;
	}

	public void setIsAppend(String isAppend) {
		this.isAppend = isAppend;
	}

	public String getRemoveStaffJson() {
		return removeStaffJson;
	}

	public void setRemoveStaffJson(String removeStaffJson) {
		this.removeStaffJson = removeStaffJson;
	}

	public String getLoginNameId() {
		return loginNameId;
	}

	public void setLoginNameId(String loginNameId) {
		this.loginNameId = loginNameId;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public boolean isOnlineVisible() {
		return onlineVisible;
	}

	public void setOnlineVisible(boolean onlineVisible) {
		this.onlineVisible = onlineVisible;
	}

	public DepartmentDisplayType getDepartmentDisplayType() {
		return departmentDisplayType;
	}

	public void setDepartmentDisplayType(DepartmentDisplayType departmentDisplayType) {
		this.departmentDisplayType = departmentDisplayType;
	}

	public void setUserWithoutDeptVisible(boolean userWithoutDeptVisible) {
		this.userWithoutDeptVisible =userWithoutDeptVisible;
	}
	
}
