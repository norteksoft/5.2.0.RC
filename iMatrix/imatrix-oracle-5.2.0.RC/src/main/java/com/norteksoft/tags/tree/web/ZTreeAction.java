package com.norteksoft.tags.tree.web;





import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.ZTreeUtils;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.tags.tree.TreeType;
import com.norteksoft.tags.tree.ZtreeLeafPage;



@Namespace("/ztree")
@ParentPackage("default")
public class ZTreeAction extends CRUDActionSupport<Company> {
	private static final long serialVersionUID = 1L;
	private Log log=LogFactory.getLog(ZTreeAction.class);
	private String currentId;
	private String searchValue;//搜索
	
	//标签tree的参数
	private TreeType treeType;//树的类型
    private String chkStyle;//树的选择类型
    private String treeNodeShowContent;//设置树节点显示内容
    private boolean userWithoutDeptVisible=false;//是否显示无部门人员
    
    //标准tree的参数(包括标签树的参数)
    private String leafEnable="fasle";//是否需要页签
    private String multiLeafJson;//多页签内容json
    private String append="false";//显示框的内容是否追加--
    private boolean onlineVisible=false;//是否显示在线人员
    private String mode;//模式??--
    private String treeNodeData;//设置树节点data
    private String chkboxType;//设置checkbox时父子节点勾选关系{"Y" : "ps", "N" : "ps" }
    
    private String feedbackEnable="false";//是否启用自动赋值
    private String showInput;//显示input框id
    private String showThing;//显示input内容设置
    private String hiddenInput;//隐藏input框id
    private String hiddenThing;//隐藏input内容设置
    
    private String departmentShow;//显示的部门
    


	@Override
	public String delete() throws Exception {
		return null;
	}


	@Override
	public String list() throws Exception {
		getTree();
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

	public Company getModel() {
		// TODO Auto-generated method stub
		return null;
	}
	

	//封装树的数据
	public String getTree() throws Exception{
		if(treeType==null){
			treeType = TreeType.valueOf(Struts2Utils.getParameter("treeType"));
		}
		switch(treeType) {
	       case COMPANY:
	    	   log.debug("进入TreeAction,COMPANY");
	    	   renderText(ZTreeUtils.createCompanyTree(ContextUtils.getCompanyId(), ContextUtils.getCompanyName(),currentId));
	    	   break;
	       case MAN_DEPARTMENT_TREE:
	    	   log.debug("进入TreeAction,MAN_DEPARTMENT_TREE");
	    	   renderText(ZTreeUtils.createDepartmentUserTree(ContextUtils.getCompanyId(), ContextUtils.getCompanyName(),currentId));
	    	  break;
	       case MAN_GROUP_TREE:
	    	   log.debug("进入TreeAction,MAN_GROUP_TREE");
	    	   renderText(ZTreeUtils.createWorkgroupUserTree(ContextUtils.getCompanyId(), ContextUtils.getCompanyName(),currentId));
	    	   break;
	       case DEPARTMENT_TREE:
	    	   log.debug("进入TreeAction,DEPARTMENT_TREE");
	    	   renderText(ZTreeUtils.createDepartmentsTree(ContextUtils.getCompanyId(), ContextUtils.getCompanyName(),currentId));
	    	   break;
	       case GROUP_TREE:
	    	   log.debug("进入TreeAction,GROUP_TREE");
	    	   renderText(ZTreeUtils.createWorkgroupsTree(ContextUtils.getCompanyId(), ContextUtils.getCompanyName(),currentId));
	    	   break;
	       case DEPARTMENT_WORKGROUP_TREE:
	    	   log.debug("进入TreeAction,DEPARTMENT_WORKGROUP_TREE");
	       renderText(ZTreeUtils.createDepartmentsAndWorkgroupsTree(ContextUtils.getCompanyId(), ContextUtils.getCompanyName(),currentId));
	    	   break;
	       default:  return renderText(ZTreeUtils.createCompanyTree(ContextUtils.getCompanyId(), ContextUtils.getCompanyName(),currentId));
	       }
		
		return null;
	}
	
   //树的搜索	
   public String searchZtree(){
	   if(treeType==null){
			treeType = TreeType.valueOf(Struts2Utils.getParameter("treeType"));
		}
		switch(treeType) {
	       case COMPANY:
	    	   renderText(delComma(getParentDepartment().append(getParentWorkgroup()).toString()));
	    	   break;
	       case MAN_DEPARTMENT_TREE:
	    	   renderText(ZTreeUtils.createDepartmentUserTree(ContextUtils.getCompanyId(), ContextUtils.getCompanyName(),currentId));
	    	  break;
	       case MAN_GROUP_TREE:
	    	   renderText(ZTreeUtils.createWorkgroupUserTree(ContextUtils.getCompanyId(), ContextUtils.getCompanyName(),currentId));
	    	   break;
	       case DEPARTMENT_TREE:
	    	   break;
	       case GROUP_TREE:
	    	   break;
	       case DEPARTMENT_WORKGROUP_TREE:
	    	   break;
	       default:  return renderText(ZTreeUtils.createCompanyTree(ContextUtils.getCompanyId(), ContextUtils.getCompanyName(),currentId));
	       }
		
		return null;
   }	
	
   private StringBuilder getParentDepartment(){
		StringBuilder result = new StringBuilder();
		List<Department> depts= ApiFactory.getAcsService().getUpstageDepartmentsByUserLike(ContextUtils.getCompanyId(), searchValue);
	    for(Department d : depts){
	    	result.append("department_"+d.getId()).append(",");
	    }
 	  return result;
	}
	
   private StringBuilder getParentWorkgroup(){
		StringBuilder result = new StringBuilder();
		 List<Workgroup> wgs=ApiFactory.getAcsService().getWorkGroupsByUserLike(ContextUtils.getCompanyId(), searchValue);
	    for(Workgroup w : wgs){
	    	result.append("workgroup_"+w.getId()).append(",");
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
	
	//弹出标准树
	public String popZtree() throws IOException{
		HttpServletResponse response = Struts2Utils.getResponse();
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		
		
		String webapp = PropUtils.getProp("host.app");
		String resourceCtx=PropUtils.getProp("host.resources");
		
		
		//得到树的数据请求地址
		String actionUrl = "";
		if("true".equals(leafEnable)){
			actionUrl = webapp+"/portal/ztree.action";
		}else{
			actionUrl = webapp+"/portal/ztree.action?treeType="+treeType;
		}
		
		String searchUrl = webapp+"/portal/search-ztree.action?treeType="+treeType;
		
		String theme=ContextUtils.getTheme();
		
		
		Map<String, Object> root=new HashMap<String, Object>();
		root.put("ctx", webapp);
		root.put("actionUrl", actionUrl);
		root.put("searchUrl", searchUrl);
		root.put("treeType", treeType);
		root.put("chkStyle", (chkStyle==null||chkStyle.equals("undefined"))?"":chkStyle);
		root.put("chkboxType", (chkboxType==null||chkboxType.equals("undefined"))?"":chkboxType);
		root.put("theme",StringUtils.isEmpty(theme)?"black":theme);
		root.put("leafEnable",leafEnable);
		root.put("multiLeafList",createZtreeLeafList(multiLeafJson,treeType));
		root.put("feedbackEnable",feedbackEnable);
		root.put("showInput",(StringUtils.isEmpty(showInput)||showInput.equals("undefined"))?"":showInput);
		root.put("showThing",(StringUtils.isEmpty(showThing)||showThing.equals("undefined"))?"":showThing);
		root.put("hiddenInput",(StringUtils.isEmpty(hiddenInput)||hiddenInput.equals("undefined"))?"":hiddenInput);
		root.put("hiddenThing",(StringUtils.isEmpty(hiddenThing)||hiddenThing.equals("undefined"))?"":hiddenThing);
		root.put("append",append);
		root.put("resourcesCtx",StringUtils.isEmpty(resourceCtx)?webapp:resourceCtx);
		
		try {
			writer.print(TagUtil.getContent(root, "tree/ztree-pop.ftl"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	//把multiLeafJson转换成list
	private List<ZtreeLeafPage> createZtreeLeafList(String multiLeafJson,TreeType treeType) {
		if(StringUtils.isEmpty(multiLeafJson)||multiLeafJson.equals("undefined")){
			//给multiLeafList默认值
		    return JsonParser.json2List(ZtreeLeafPage.class, getDefaultMultiLeafJson(treeType));
		}else{
			return JsonParser.json2List(ZtreeLeafPage.class, multiLeafJson);
		}
	}
	
	//"[{'name':'公司树','type':'COMPANY','hiddenValue':'','showValue':''}]"
	//得到默认的多页签设置
	private String getDefaultMultiLeafJson(TreeType treeType) {
		String  result="";
		switch(treeType) {
	       case COMPANY:
	    	   result = "[{'name':'公司树','type':'COMPANY','hiddenValue':'{\"user\":\"id\"}','showValue':'{\"user\":\"name\"}'}]";
	    	   break;
	       case MAN_DEPARTMENT_TREE:
	    	   result = "[{'name':'部门人员树','type':'MAN_DEPARTMENT_TREE','hiddenValue':'{\"user\":\"id\"}','showValue':'{\"user\":\"name\"}'}]";
	    	  break;
	       case MAN_GROUP_TREE:
	    	   result = "[{'name':'工作组人员树','type':'MAN_GROUP_TREE','hiddenValue':'{\"user\":\"id\"}','showValue':'{\"user\":\"name\"}'}]";
	    	   break;
	       case DEPARTMENT_TREE:
	    	   result = "[{'name':'部门树','type':'DEPARTMENT_TREE','hiddenValue':'{\"department\":\"id\"}','showValue':'{\"department\":\"name\"}'}]";
	    	   break;
	       case GROUP_TREE:
	    	   result = "[{'name':'工作组树','type':'GROUP_TREE','hiddenValue':'{\"workgroup\":\"id\"}','showValue':'{\"workgroup\":\"name\"}'}]";
	    	   break;
	       case DEPARTMENT_WORKGROUP_TREE:
	    	   result = "[{'name':'部门工作组树','type':'DEPARTMENT_WORKGROUP_TREE','hiddenValue':'{\"department\":\"id\",\"workgroup\":\"id\"}','showValue':'{\"department\":\"name\",\"workgroup\":\"name\"}'}]";
	    	   break;
	       default:  return "[{'name':'公司树','type':'COMPANY','hiddenValue':'{\"user\":\"name\"}','showValue':'{\"user\":\"name\"}'}]";
	       }
		return result;
	}

	//删除树的内容
	public String removeZtree() throws Exception{
		HttpServletResponse response = Struts2Utils.getResponse();
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		String webapp = Struts2Utils.getRequest().getContextPath();
		String theme=ContextUtils.getTheme();
		String resourceCtx=PropUtils.getProp("host.resources");
		
		//封装参数
		Map<String, Object> root=new HashMap<String, Object>();
		root.put("resourceCtx",StringUtils.isEmpty(resourceCtx)?webapp:resourceCtx);
		root.put("theme",StringUtils.isEmpty(theme)?"black":theme);
		root.put("showInput",(StringUtils.isEmpty(showInput)||showInput.equals("undefined"))?"":showInput);
		root.put("hiddenInput",(StringUtils.isEmpty(hiddenInput)||hiddenInput.equals("undefined"))?"":hiddenInput);
		
		
		writer.print(TagUtil.getContent(root, "tree/ztree-remove.ftl"));
		return null;
	}
	public String getCurrentId() {
		return currentId;
	}

	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}

	public TreeType getTreeType() {
		return treeType;
	}

	public void setTreeType(String treeType) {
		this.treeType =TreeType.valueOf(treeType) ;
	}
	
	public String getTreeNodeShowContent() {
		return treeNodeShowContent;
	}

	public void setTreeNodeShowContent(String treeNodeShowContent) {
		ZTreeUtils.setTreeNodeShowContent(treeNodeShowContent);
		this.treeNodeShowContent = treeNodeShowContent;
	}

	public void setUserWithoutDeptVisible(boolean userWithoutDeptVisible) {
		ZTreeUtils.setUserWithoutDeptVisible(userWithoutDeptVisible);
		this.userWithoutDeptVisible =userWithoutDeptVisible;
	}
	public String getSearchValue() {
		return searchValue;
	}
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public void setTreeNodeData(String treeNodeData) {
		ZTreeUtils.setTreeNodeData(treeNodeData);
		this.treeNodeData = treeNodeData;
	}

	public String getLeafEnable() {
		return leafEnable;
	}

	public void setLeafEnable(String leafEnable) {
		this.leafEnable = leafEnable;
	}

	public void setMultiLeafJson(String multiLeafJson) {
		this.multiLeafJson = multiLeafJson;
	}
	public void setAppend(String append) {
		this.append = append;
	}

	public void setOnlineVisible(boolean onlineVisible) {
		ZTreeUtils.setOnlineVisible(onlineVisible);
		this.onlineVisible = onlineVisible;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
	public void setChkStyle(String chkStyle) {
		this.chkStyle = chkStyle;
	}

	public void setChkboxType(String chkboxType) {
		this.chkboxType = chkboxType;
	}

	public String getFeedbackEnable() {
		return feedbackEnable;
	}

	public void setFeedbackEnable(String feedbackEnable) {
		this.feedbackEnable = feedbackEnable;
	}


	public String getShowInput() {
		return showInput;
	}

	public void setShowInput(String showInput) {
		this.showInput = showInput;
	}

	public String getShowThing() {
		return showThing;
	}

	public void setShowThing(String showThing) {
		this.showThing = showThing;
	}

	public String getHiddenInput() {
		return hiddenInput;
	}
	public void setHiddenInput(String hiddenInput) {
		this.hiddenInput = hiddenInput;
	}
	public String getHiddenThing() {
		return hiddenThing;
	}
	public void setHiddenThing(String hiddenThing) {
		this.hiddenThing = hiddenThing;
	}
	public String getDepartmentShow() {
		return departmentShow;
	}
	public void setDepartmentShow(String departmentShow) {
		ZTreeUtils.setDepartmentShow(departmentShow);
		this.departmentShow = departmentShow;
	}
}
