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

import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.freemarker.TagUtil;



public class ztreeTag extends TagSupport{
	private Log log=LogFactory.getLog(ztreeTag.class);
	private static final long serialVersionUID = 1L;
    private TreeType treeType;
    private String chkStyle;//多选和单选
    private String treeId;
    private String treeNodeShowContent;//设置树节点显示内容 
    private boolean userWithoutDeptVisible=true;//是否显示无部门人员
    private String chkboxType;//设置checkbox时父子节点勾选关系{"Y" : "ps", "N" : "ps" }
    private String departmentShow;//显示那些部门
    
    
	 public int doStartTag() {  
		 try{
			 
			 JspWriter out=pageContext.getOut(); 
			 out.print(readZtreeTemplet());
			 
		 }catch(Exception ee){
			ee.printStackTrace();
		 }
		 return Tag.EVAL_PAGE;
	    }


    //读取ztree模板
	private String readZtreeTemplet() {
		String webapp=((HttpServletRequest)pageContext.getRequest()).getContextPath();
		String resourceCtx=PropUtils.getProp("host.resources");
		
		
		//得到树的数据请求地址
		String actionUrl = webapp+"/portal/ztree.action?treeType="+treeType
		                   +"&treeNodeShowContent="+treeNodeShowContent
		                   +"&userWithoutDeptVisible="+userWithoutDeptVisible
		                   +"&departmentShow="+departmentShow;
		
		String searchUrl = webapp+"/portal/search-ztree.action?treeType="+treeType;
		
		String theme=ContextUtils.getTheme();
		
		
		Map<String, Object> root=new HashMap<String, Object>();
		root.put("ctx", webapp);
		root.put("actionUrl", actionUrl);
		root.put("searchUrl", searchUrl);
		root.put("treeId", treeId);
		root.put("treeType", treeType);
		root.put("chkStyle", chkStyle==null?"":chkStyle);
		root.put("chkboxType", chkboxType==null?"":chkboxType);
		root.put("theme",StringUtils.isEmpty(theme)?"black":theme);
		root.put("resourcesCtx",StringUtils.isEmpty(resourceCtx)?webapp:resourceCtx);
		
		String result="";
		try {
			result = TagUtil.getContent(root, "tree/ztree-tag.ftl");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	public void setTreeType(String treeType) {
		this.treeType =TreeType.valueOf(treeType) ;
	}

	public String getTreeNodeShowContent() {
		return treeNodeShowContent;
	}

	public void setTreeNodeShowContent(String treeNodeShowContent) {
		this.treeNodeShowContent = treeNodeShowContent;
	}


	public void setChkStyle(String chkStyle) {
		this.chkStyle = chkStyle;
	}


	public void setTreeId(String treeId) {
		this.treeId = treeId;
	}


	public void setUserWithoutDeptVisible(boolean userWithoutDeptVisible) {
		this.userWithoutDeptVisible =userWithoutDeptVisible;
	}

	public String getChkboxType() {
		return chkboxType;
	}

	public void setChkboxType(String chkboxType) {
		this.chkboxType = chkboxType;
	}


	public String getDepartmentShow() {
		return departmentShow;
	}

	public void setDepartmentShow(String departmentShow) {
		this.departmentShow = departmentShow;
	}

	
}
