package com.norteksoft.tags.grid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.norteksoft.mms.base.utils.view.DynamicColumnDefinition;
import com.norteksoft.mms.form.entity.GroupHeader;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.enumeration.OrderType;
import com.norteksoft.mms.form.enumeration.StartQuery;
import com.norteksoft.mms.form.service.FormHtmlParser;
import com.norteksoft.mms.form.service.GroupHeaderManager;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.tags.search.SearchData;
public class GridTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	
	private String gridId;//表单的ID
	private String code;//表单编号
	private String url;//表单URL
	private String pageName;//数据名称
	private String subGrid;//是否有子表
	private List<DynamicColumnDefinition> dynamicColumn;//动态字段
	private String submitForm;//查询需要的FormID
	
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
		String loginName=ContextUtils.getLoginName();
		String userName=ContextUtils.getTrueName();
		ListViewManager listViewManager = (ListViewManager) ContextUtils.getBean("listViewManager");
		ListView listView=listViewManager.getListViewByCode(code);
		Assert.notNull(listView,"listView不能为空");
		List<ListColumn> columns=listView.getColumns();
		Map<String, Object> root=new HashMap<String, Object>();
		root.put("gridId", gridId);
		root.put("url", url);
		root.put("ctx", webapp);
		root.put("_list_code", code);
		FormHtmlParser formHtmlParser = (FormHtmlParser) ContextUtils.getBean("formHtmlParser");
		Boolean total=false;//是否合计
		Boolean mergerCell=false;//是否合并单元格
		String export="false";//是否导出
		if(columns!=null&&columns.size()>0){
			for(ListColumn lc:columns){
				lc.setInternationName(formHtmlParser.getInternation(lc.getHeaderName()));
				String vs=formHtmlParser.getValueSet(lc,null,null);
				lc.setOptionSet(vs);
				if(lc.getTotal()){
					total=true;
				}
				if(lc.getExportable()!=null&&lc.getExportable()){
					export="true";
				}
				if(lc.getTableColumn()!=null){
					lc.setColumnName(formHtmlParser.getColModelName(lc.getTableColumn().getName()));
					mergerCell=true;
				}
			}
			root.put("columns", columns);
		}
		root.put("loginName", loginName);
		root.put("userName", userName);
		if(pageName!=null){
			root.put("pageName", pageName);
		}
		if(StringUtils.isNotEmpty(listView.getEditUrl())){
			root.put("editurl", listView.getEditUrl());
		}else{
			root.put("editurl", "");
		}
		if(StringUtils.isNotEmpty(listView.getDragRowUrl())){
			root.put("dragRowUrl", listView.getDragRowUrl());
		}else{
			root.put("dragRowUrl", "");
		}
		if(StringUtils.isNotEmpty(listView.getDeleteUrl())){
			root.put("deleteUrl", listView.getDeleteUrl());
		}else{
			root.put("deleteUrl", "");
		}
		if(listView.getRowNumbers()){
			root.put("rowNumbers", true);
		}else{
			root.put("rowNumbers", false);
		}
		
		if(StringUtils.isNotEmpty(listView.getCustomProperty())){
			root.put("customProperty", listView.getCustomProperty());
		}
		if(subGrid!=null){
			root.put("subGrid", subGrid);
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
		
		root.put("frozenColumn", listView.getFrozenColumn()==null?0:listView.getFrozenColumn());
		GroupHeaderManager groupHeaderManager = (GroupHeaderManager) ContextUtils.getBean("groupHeaderManager");
		List<GroupHeader> groupHeaders=groupHeaderManager.getGroupHeadersByViewId(listView.getId());
		if(groupHeaders !=null && groupHeaders.size()>0){
			root.put("groupHeaderSign", "true");
			root.put("groupHeader", groupHeaders);
		}
		
		if(dynamicColumn!=null&&dynamicColumn.size()>0){
			for(DynamicColumnDefinition dfo:dynamicColumn){
				formatterEnum(dfo);
				if(!total){
					if(dfo.getIsTotal()!=null&&dfo.getIsTotal()){
						total=true;
					}
				}
			}
			root.put("dynamicColumn", dynamicColumn);
			root.put("dynamicColumns", getDynamicColumns(dynamicColumn));
			String colName=dynamicColumn.get(0).getName();
			root.put("dynamicColumnName", colName.subSequence(0, colName.length()-1));
		}
		root.put("total", total);
		root.put("mergerCell", mergerCell);
		root.put("export", export);
		String result = TagUtil.getContent(root, "grid/gridTag.ftl");
		
		if(!StartQuery.NO_QUERY.equals(listView.getStartQuery())){
			SearchData searchData = (SearchData) ContextUtils.getBean("searchData");
			String searchResult = searchData.getContent(code,url,gridId,submitForm);
			searchResult = "<textarea id=\"searchArea\" style=\"display: none;\" rows=\"\" cols=\"\">"+searchResult.replace("<", "&lt;").replace(">", "&gt;")+"</textarea>";
			result+=searchResult;
			if(StartQuery.CUSTOM_QUERY.equals(listView.getStartQuery())){//启用自定义查询
				result+=getCustomSearchData(listView);
			}
		}
		result+="<input id='totalable_page_id' type='hidden' value='"+listView.getTotalable()+"'/><input id='searchTotalable_page_id' type='hidden' value='"+listView.getSearchTotalable()+"'/>";
		return result;
	}
	
	private String getCustomSearchData(ListView listView){
		StringBuilder searchResult = new StringBuilder();
		searchResult.append("<textarea style=\"display:none;\" id=\"custom_field_list\" >");
		searchResult.append(getCustomSearchFields(listView));
		searchResult.append("</textarea>");
		searchResult.append("<input id=\"custom_search_grid_id\" value=\""+gridId+"\" type=\"hidden\" /> ");
		return searchResult.toString();
		
	}
	
	private String getCustomSearchFields(ListView listView){
		Assert.notNull(listView,"listView不能为空");
		List<ListColumn> columns=listView.getColumns();
		StringBuilder temp=new StringBuilder();
		for(ListColumn column:columns){
			if(StringUtils.isNotEmpty(column.getQuerySettingValue()) && !"NONE".equals(column.getQuerySettingValue())&& column.getTableColumn()!=null){
				if(StringUtils.isNotEmpty(temp.toString())){
					temp.append(",");
				}
				temp.append("{");
				temp.append("\"enName\":");
				temp.append("\""+column.getTableColumn().getName()+"\",");
				temp.append("\"keyValue\":");
				if(StringUtils.isNotEmpty(column.getValueSet())){
					temp.append("\""+column.getOptionSet()+"\",");
				}else{
					temp.append("\"\",");
				}
				temp.append("\"propertyType\":");
				temp.append("\""+column.getTableColumn().getDataType()+"\",");
				temp.append("\"enumName\":");
				if(column.getTableColumn().getDataType().equals(DataType.ENUM)){
					temp.append("\""+column.getValueSet().replaceFirst("enumname:", "")+"\"");
				}else{
					temp.append("\"\"");
				}
				
				temp.append("}");
				
			}
		}
		return "["+temp.toString()+"]";
	}
	
	/**
	 * 获得动态列,形式为{key:value,key:value,...},key表示列体name,value表示列头名称
	 * @param dynamicColumnDefinition2
	 * @return
	 */
	private String getDynamicColumns(List<DynamicColumnDefinition> dynamicColumnDefinitions) {
		StringBuilder dynamicColumns=new StringBuilder();
		dynamicColumns.append("{");
		for(DynamicColumnDefinition dynamicColumnDefinition:dynamicColumnDefinitions){
			dynamicColumns.append("\"");
			dynamicColumns.append(dynamicColumnDefinition.getName());
			dynamicColumns.append("\":");
			dynamicColumns.append(JsonParser.object2Json(dynamicColumnDefinition));
			dynamicColumns.append(",");
		}
		if(dynamicColumns.charAt(dynamicColumns.length()-1)==','){
			dynamicColumns.deleteCharAt(dynamicColumns.length()-1);
		}
		dynamicColumns.append("}");
		return dynamicColumns.toString();
	}

	/**
	 * 若该字段editoptions不为null或空字符串，
	 * 并且该字段的值是枚举类的全名，
	 * 则把该字段封装为'key':'value','key':'value','key':'value' ...形式的字符串。
	 * @param dfo
	 */
	private void formatterEnum(DynamicColumnDefinition dfo){
		String editoptions=dfo.getEditoptions();
		if(StringUtils.isNotEmpty(editoptions)&&DataType.ENUM.equals(dfo.getType())){
			StringBuilder opitions=new StringBuilder(); 
			try {
				Object[] objs = Class.forName(editoptions).getEnumConstants();
				int i=0;
				for(Object obj : objs){
					i++;
					opitions.append("'").append(obj.toString())
					.append("':'")
//					.append(textProvider.getText(BeanUtils.getProperty(obj, "code")))
					.append("'");
					if(i<objs.length){
						opitions.append(",");
					}
				}
			} catch (Exception e) {
			}
			dfo.setEditoptions(opitions.toString());
		}
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

	public String getSubGrid() {
		return subGrid;
	}

	public void setSubGrid(String subGrid) {
		this.subGrid = subGrid;
	}

	public List<DynamicColumnDefinition> getDynamicColumn() {
		return dynamicColumn;
	}

	public void setDynamicColumn(List<DynamicColumnDefinition> dynamicColumn) {
		this.dynamicColumn = dynamicColumn;
	}

	public String getSubmitForm() {
		return submitForm;
	}

	public void setSubmitForm(String submitForm) {
		this.submitForm = submitForm;
	}

}
