package com.norteksoft.tags.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.xwork.StringUtils;

import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.service.FormHtmlParser;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.freemarker.TagUtil;
public class CustomGridTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	
	private String gridId;//表格的ID
	private String tableName;//数据表表名
	private String listCode;//对应的列表编号
	private String headListCode;//表头的列表编号
	//标签开始时调用的出来方法
	@Override
	public int doStartTag() throws JspException {
		try {
			if(StringUtils.isEmpty(headListCode)){
				headListCode="MMS_LIST_COLUMN";
			}
			String html =readTemplate();
			//将信息内容输出到JSP页面
			pageContext.getOut().print(html);
		} catch (Exception e) {
			throw new JspException(e);
		}
		return Tag.EVAL_PAGE;
		//跳过标签体的执行
//		return SKIP_BODY;
	}
	@SuppressWarnings("unchecked")
	private String readTemplate() throws Exception {
		String webapp=((HttpServletRequest)pageContext.getRequest()).getContextPath();
		
		ListViewManager listViewManager = (ListViewManager) ContextUtils.getBean("listViewManager");
		FormHtmlParser formHtmlParser = (FormHtmlParser) ContextUtils.getBean("formHtmlParser");
		ListView listView=listViewManager.getListViewByCode(headListCode);//列表头设置
		ListView listVeiwEntity=listViewManager.getListViewByCode(listCode);//列表对应的实体
		String[] colResult=formHtmlParser.getColNames(listView.getColumns(),listView,listVeiwEntity,tableName,null);
		String imatrix=SystemUrls.getSystemUrl("imatrix");
		String data=null;
		if(listVeiwEntity!=null){
			data=formHtmlParser.getJsonData(listVeiwEntity.getColumns(),listView.getColumns());
		}else{
			data=formHtmlParser.getJsonData(new ArrayList(),listView.getColumns());
		}
		Map<String, Object> root=new HashMap<String, Object>();
		root.put("tableId", gridId);
		root.put("ctx", webapp);
		root.put("listView", listView);
		root.put("colNames", colResult[0]);
		root.put("colModel", colResult[1]);
		root.put("jsonData", data);
		root.put("webCtx", imatrix);
		root.put("isSubSystem", "true");//是否是子系统
		root.put("loginName", ContextUtils.getLoginName());
		root.put("userName", ContextUtils.getUserName());
		String result = TagUtil.getContent(root, "grid/customGridTag.ftl");
		return result;
	}
	
	//标签结束时调用的处理方法
	public int doEndTag(){
		//继续执行后续的JSP页面的内容
		return Tag.EVAL_PAGE;
	}

	public String getGridId() {
		return gridId;
	}
	public void setGridId(String gridId) {
		this.gridId = gridId;
	}
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getListCode() {
		return listCode;
	}

	public void setListCode(String listCode) {
		this.listCode = listCode;
	}
	public String getHeadListCode() {
		return headListCode;
	}
	public void setHeadListCode(String headListCode) {
		this.headListCode = headListCode;
	}

}
