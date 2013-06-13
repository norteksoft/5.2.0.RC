package com.norteksoft.tags.grid;

import java.util.ArrayList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.service.FormHtmlParser;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.freemarker.TagUtil;
public class FormGridTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	
	private Log log = LogFactory.getLog(FormGridTag.class);
	private String gridId;//表格的ID
	private String code;//子列表对应的编码
	private String attributeName;//子列表对应的字段名称
	private Object entity;//对应实体
	private Collection collection;//是集合时（子表只存主表id）
	private String basic;
	private String editable;//是否可以操作

	
	public void setGridId(String gridId) {
		this.gridId = gridId;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public void setEntity(Object entity) {
		this.entity = entity;
	}
	
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public void setBasic(String basic) {
		this.basic = basic;
	}
	public void setEditable(String editable) {
		this.editable = editable;
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			String html =readTemplate();
			//将信息内容输出到JSP页面
			pageContext.getOut().print(html);
		} catch (Exception e) {
			throw new JspException(e);
		}
		return Tag.EVAL_PAGE;
	}
	
	@SuppressWarnings("unchecked")
	private String readTemplate() throws Exception {
		String webapp=((HttpServletRequest)pageContext.getRequest()).getContextPath();
		ListViewManager listViewManager = (ListViewManager) ContextUtils.getBean("listViewManager");
		FormHtmlParser formHtmlParser = (FormHtmlParser) ContextUtils.getBean("formHtmlParser");
		ListView listView=listViewManager.getListViewByCode(code);//列表头设置
		String[] colResult=formHtmlParser.getColNames(listView.getColumns(),listView,entity,null,editable);
		String footerDatas=null;
		String imatrix=SystemUrls.getSystemUrl("imatrix");
		log.debug("******************imatrix=="+imatrix);
		String data=null;
		Collection list=new ArrayList();
		if(collection!=null&&collection.size()>0){
			list=collection;
		}else if(entity!=null){
			if(StringUtils.isNotEmpty(attributeName)){
				Map<String,Collection> map=formHtmlParser.getEntityCollection(attributeName,entity);
				Collection queryResult=map.get(attributeName);
				if(queryResult!=null)list=queryResult;
			}
		}
		footerDatas=formHtmlParser.getFooterDatas(list,listView.getColumns(),listView.getEditable());
		data=formHtmlParser.getJsonData(list,listView.getColumns());
		Map<String, Object> root=new HashMap<String, Object>();
		root.put("tableId", gridId);
		root.put("ctx", webapp);
		root.put("listView", listView);
		root.put("colNames", colResult[0]);
		root.put("colModel", colResult[1]);
		root.put("jsonData", data);
		root.put("footerDatas", footerDatas);
		root.put("attributeName",attributeName);
		root.put("editable",editable==null?"true":editable);
		root.put("loginName", ContextUtils.getLoginName());
		root.put("userName", ContextUtils.getUserName());
		String deleteUrl = listView.getDeleteUrl();
		if(StringUtils.isNotEmpty(deleteUrl)){
			//  /acs/organization/user!delete.action，当是这种主子系统的情况，则第一个/后的字符串应为子系统编码
			String systemCode = deleteUrl.split("/")[1];
			BusinessSystemManager businessSystemManager = (BusinessSystemManager)ContextUtils.getBean("businessSystemManager");
			BusinessSystem business = businessSystemManager.getSystemBySystemCode(systemCode);
			if(business!=null){
				if(StringUtils.isNotEmpty(business.getParentCode())){
					root.put("isSubSystem", "true");//是否是子系统
					root.put("webCtx", SystemUrls.getSystemUrl(business.getParentCode()));
				}else{
					root.put("isSubSystem", "false");
				}
			}
		}
		String result = TagUtil.getContent(root, "grid/customGridTag.ftl");
		return result;
	}
	
	@Override
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}


}
