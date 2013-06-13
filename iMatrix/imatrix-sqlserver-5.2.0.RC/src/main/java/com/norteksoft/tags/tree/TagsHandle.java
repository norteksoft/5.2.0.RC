package com.norteksoft.tags.tree;



import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.freemarker.TagUtil;



public class TagsHandle extends TagSupport{
	private Log log=LogFactory.getLog(TagsHandle.class);
	private static final long serialVersionUID = 1L;
    private TreeType treeType;
    private boolean multiple=false;
    private String treeId;
    private boolean defaultable;
    private String inputId;
    private DepartmentDisplayType departmentDisplayType;//树的显示类型
    private boolean userWithoutDeptVisible;//无部门人员是否显示
    
	 public int doStartTag() {  
		 try{
			 String webapp=PropUtils.getProp("host.app");
			 if(departmentDisplayType==null) departmentDisplayType = DepartmentDisplayType.NAME;
			 JspWriter out=pageContext.getOut(); 
			     log.debug("treeType="+treeType+",multiple="+multiple+",treeId="+treeId+",defaultable="+defaultable);
			     if(defaultable==true){
				    switch(treeType) {
				       case COMPANY:
				    	   out.print(getTreeTemplet(multiple,webapp+"/portal/tree.action?treeType="+treeType+"&departmentDisplayType="+departmentDisplayType+"&userWithoutDeptVisible="+userWithoutDeptVisible,webapp+"/portal/search-tree.action?treeType="+treeType+"&departmentDisplayType="+departmentDisplayType+"&userWithoutDeptVisible="+userWithoutDeptVisible));break;
				       case MAN_DEPARTMENT_GROUP_TREE:
				           out.print(getTreeTemplet(multiple,webapp+"/portal/tree.action?treeType="+treeType+"&departmentDisplayType="+departmentDisplayType+"&userWithoutDeptVisible="+userWithoutDeptVisible,webapp+"/portal/search-tree.action?treeType="+treeType+"&departmentDisplayType="+departmentDisplayType+"&userWithoutDeptVisible="+userWithoutDeptVisible));break;
				       case MAN_DEPARTMENT_TREE:
				           out.print(getTreeTemplet(multiple,webapp+"/portal/tree.action?treeType="+treeType+"&departmentDisplayType="+departmentDisplayType+"&userWithoutDeptVisible="+userWithoutDeptVisible,webapp+"/portal/search-tree.action?treeType="+treeType+"&departmentDisplayType="+departmentDisplayType+"&userWithoutDeptVisible="+userWithoutDeptVisible));break;
				       case MAN_GROUP_TREE:
				           out.print(getTreeTemplet(multiple,webapp+"/portal/tree.action?treeType="+treeType+"&departmentDisplayType="+departmentDisplayType+"&userWithoutDeptVisible="+userWithoutDeptVisible,webapp+"/portal/search-tree.action?treeType="+treeType+"&departmentDisplayType="+departmentDisplayType+"&userWithoutDeptVisible="+userWithoutDeptVisible));break;
				       case DEPARTMENT_TREE:
				           out.print(getTreeTemplet(multiple,webapp+"/portal/tree.action?treeType="+treeType+"&departmentDisplayType="+departmentDisplayType+"&userWithoutDeptVisible="+userWithoutDeptVisible,webapp+"/portal/search-tree.action?treeType="+treeType+"&departmentDisplayType="+departmentDisplayType+"&userWithoutDeptVisible="+userWithoutDeptVisible));break;
				       case GROUP_TREE:
				           out.print(getTreeTemplet(multiple,webapp+"/portal/tree.action?treeType="+treeType+"&departmentDisplayType="+departmentDisplayType+"&userWithoutDeptVisible="+userWithoutDeptVisible,webapp+"/portal/search-tree.action?treeType="+treeType+"&departmentDisplayType="+departmentDisplayType+"&userWithoutDeptVisible="+userWithoutDeptVisible));break;
				       case DEPARTMENT_WORKGROUP_TREE:
				           out.print(getTreeTemplet(multiple,webapp+"/portal/tree.action?treeType="+treeType+"&departmentDisplayType="+departmentDisplayType+"&userWithoutDeptVisible="+userWithoutDeptVisible,webapp+"/portal/search-tree.action?treeType="+treeType+"&departmentDisplayType="+departmentDisplayType+"&userWithoutDeptVisible="+userWithoutDeptVisible));break;   
				       default:  return  Tag.SKIP_BODY;
				       }
			     }else{
		    	  switch(treeType) {
				       case COMPANY:
				    	   out.print(getTreeTempletTwo(multiple,webapp+"/portal/tree.action",webapp+"/portal/search-tree.action"));break;
				       case MAN_DEPARTMENT_GROUP_TREE:
				           out.print(getTreeTempletTwo(multiple,webapp+"/portal/tree.action",webapp+"/portal/search-tree.action"));break;
				       case MAN_DEPARTMENT_TREE:
				           out.print(getTreeTempletTwo(multiple,webapp+"/portal/tree.action",webapp+"/portal/search-tree.action"));break;
				       case MAN_GROUP_TREE:
				           out.print(getTreeTempletTwo(multiple,webapp+"/portal/tree.action",webapp+"/portal/search-tree.action"));break;
				       case DEPARTMENT_TREE:
				           out.print(getTreeTempletTwo(multiple,webapp+"/portal/tree.action",webapp+"/portal/search-tree.action"));break;
				       case GROUP_TREE:
				           out.print(getTreeTempletTwo(multiple,webapp+"/portal/tree.action",webapp+"/portal/search-tree.action"));break;
				       case DEPARTMENT_WORKGROUP_TREE:
				           out.print(getTreeTempletTwo(multiple,webapp+"/portal/tree.action",webapp+"/portal/search-tree.action"));break;
				       default:  return  Tag.SKIP_BODY;
				       }
			     }
		 
		 }catch(Exception ee){
			ee.printStackTrace();
		 }
		 return Tag.EVAL_PAGE;
	    }



	public String getTreeTemplet(boolean multiple, String actionUrl,String searcheUrl )throws Exception{
		log.debug("multiple="+multiple+",actionUrl="+actionUrl);
		StringBuilder s=new StringBuilder();
		if(multiple==true){
			s.append(readScriptTemplet("multipleTree.ftl",actionUrl,searcheUrl));	
		}else if(multiple==false){
			s.append(readScriptTemplet("singleTree.ftl",actionUrl,searcheUrl));
		}
		return s.toString();
	}
	public String getTreeTempletTwo(boolean multiple, String actionUrl,String searcheUrl )throws Exception{
		log.debug("multiple="+multiple+",actionUrl="+actionUrl+",searcheUrl="+searcheUrl);
		StringBuilder s=new StringBuilder();
		if(multiple==true){
			s.append(readScriptTemplet("multipleTreeTwo.ftl",actionUrl,searcheUrl));	
		}else if(multiple==false){
			s.append(readScriptTemplet("singleTreeTwo.ftl",actionUrl,searcheUrl));
		}
		return s.toString();
	}

	//读取脚本模板
	public String readScriptTemplet(String TempletName ,String actionUrl,String searchUrl) throws Exception{
		log.debug("TempletName="+TempletName+",actionUrl="+actionUrl);
		String webapp=((HttpServletRequest)pageContext.getRequest()).getContextPath();
		 String resourceCtx=PropUtils.getProp("host.resources");
		Map<String, Object> root=new HashMap<String, Object>();
		root.put("ctx", webapp);
		root.put("actionUrl", actionUrl);
		root.put("searchUrl", searchUrl);
		root.put("treeId", treeId);
		root.put("treeType", treeType);
		root.put("inputId", inputId==null?"":inputId);
		root.put("resourceCtx",StringUtils.isEmpty(resourceCtx)?webapp:resourceCtx);
		String result =TagUtil.getContent(root, "tree/"+TempletName);
		return result;
	}



	public void setTreeType(String treeType) {
		this.treeType =TreeType.valueOf(treeType) ;
	}
	public void setDepartmentDisplayType(String departmentDisplayType) {
		this.departmentDisplayType =DepartmentDisplayType.valueOf(departmentDisplayType) ;
	}

	public void setMultiple(String multiple) {
		if("true".equals(multiple)){
			this.multiple =true;
		}else{
			this.multiple =false;
		}
	}

	public void setTreeId(String treeId) {
		this.treeId = treeId;
	}



	public void setDefaultable(String defaultable) {
		if("true".equals(defaultable)){
			this.defaultable =true;
		}else{
			this.defaultable =false;
		}
	}
	public void setInputId(String inputId) {
		this.inputId = inputId;
	}

	public void setUserWithoutDeptVisible(boolean userWithoutDeptVisible) {
		this.userWithoutDeptVisible =userWithoutDeptVisible;
	}

}
