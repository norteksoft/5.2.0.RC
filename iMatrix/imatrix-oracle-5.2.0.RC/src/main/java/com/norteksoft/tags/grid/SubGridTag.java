package com.norteksoft.tags.grid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.enumeration.OrderType;
import com.norteksoft.mms.form.service.FormHtmlParser;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.freemarker.TagUtil;
public class SubGridTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	
	private String gridId;//子表的ID
	private String code;//对应的子表编号
	private String url;//子表URL
	private String pageName;//数据名称
	
	//标签开始时调用的出来方法
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
		//跳过标签体的执行
//		return SKIP_BODY;
	}
	
	private String readTemplate() throws Exception {
		String webapp=((HttpServletRequest)pageContext.getRequest()).getContextPath();
		
		ListViewManager listViewManager = (ListViewManager) ContextUtils.getBean("listViewManager");
		ListView listView=listViewManager.getListViewByCode(code);
		List<ListColumn> columns=listView.getColumns();
		Map<String, Object> root=new HashMap<String, Object>();
		FormHtmlParser formHtmlParser = (FormHtmlParser) ContextUtils.getBean("formHtmlParser");
		
		root.put("gridId", gridId);
		if(url.contains("?")){
			root.put("url", url.substring(0,url.indexOf("?")));
			root.put("urlParameter", url.substring(url.indexOf("?")+1,url.length()));
		}else{
			root.put("url", url);
		}
		root.put("ctx", webapp);
		root.put("_list_code", code);
		Boolean total=false;//是否合计
		if(columns!=null&&columns.size()>0){
			for(ListColumn lc:columns){
				lc.setInternationName(formHtmlParser.getInternation(lc.getHeaderName()));
				String vs=formHtmlParser.getValueSet(lc,null,null);
				lc.setOptionSet(vs);
				if(lc.getTotal()){
					total=true;
				}
			}
			root.put("columns", columns);
		}
		if(pageName!=null){
			root.put("pageName", pageName);
		}
		if(listView.getRowNumbers()){
			root.put("rowNumbers", true);
		}else{
			root.put("rowNumbers", false);
		}
		if(StringUtils.isNotEmpty(listView.getEditUrl())){
			root.put("editurl", listView.getEditUrl());
		}
		if(listView.getRowNum()!=null){
			root.put("rowNum", listView.getRowNum().toString());
		}
		if(listView.getRowList()!=null){
			root.put("rowList", listView.getRowList());
		}
		if(listView.getMultiSelect()!=null&&listView.getMultiSelect()){
			root.put("multiselect", "true");
		}else {
			root.put("multiselect", "false");
		}
		if(listView.getMultiboxSelectOnly()!=null&&listView.getMultiboxSelectOnly()){
			root.put("multiboxSelectOnly", "true");
		}else{
			root.put("multiboxSelectOnly", "false");
		}
		if(StringUtils.isNotEmpty(listView.getDefaultSortField())){
			root.put("sortname", listView.getDefaultSortField());
		}else{
			root.put("sortname", "");
		}
		if(OrderType.DESC.equals(listView.getOrderType())){
			root.put("sortorder", "desc");
		}else{
			root.put("sortorder", "asc");
		}
		if(listView.getPagination()){
			root.put("pagination", "true");
		}
		root.put("renmibi", "￥");
		root.put("_year", "年");
		root.put("_month", "月");
		root.put("_day", "日");
		root.put("_hour", "时");
		root.put("_minute", "分");
		root.put("_second", "秒");
		root.put("total", total);
		String result = TagUtil.getContent(root, "grid/subGridTag.ftl");
		return result;
	}

	public String getGridId() {
		return gridId;
	}

	public void setGridId(String gridId) {
		this.gridId = gridId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
}
