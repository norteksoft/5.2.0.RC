package com.norteksoft.product.api.entity;

import java.io.Serializable;
import java.util.List;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.JqGridProperty;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.enumeration.OrderType;
import com.norteksoft.mms.form.enumeration.StartQuery;
import com.norteksoft.mms.form.enumeration.TotalType;
public class ListView implements Serializable{

	private static final long serialVersionUID = 1L;
	//entity
	private Long id;
	private boolean deleted;
	private Long companyId;
	//ListView
	private List<ListColumn> columns;//列设置
	private Boolean rowNumbers;//是否显示序号
	private Boolean pagination;//是否分页
	private Boolean totalable;//是否显示列表总条数
	private Boolean searchTotalable;//查询时是否显示列表总条数
	private Integer rowNum;//默认行数
	private String rowList;//可选行数
	private Boolean defaultListView;//是否默认
	private Boolean editable;//是否需要操作:增改
	private Integer actWidth;//操作列宽设置
	private String editUrl;//表格中编辑时保存url
	private Boolean advancedQuery;//是否高级查询
	private StartQuery startQuery;//是否启用查询
	private Boolean popUp;//是否是弹出式查询(true为弹出，false为嵌入)
	private String deleteUrl;//删除的url
	private String orderFieldName;//行顺序字段名称(如果该字段为空表示不能行拖到)
	private String dragRowUrl;//行拖动后保存顺序的url
	private Boolean multiSelect;//是否可以多选
	private String defaultSortField;//默认排序字段
	private OrderType orderType;//默认排序方式
	private Integer frozenColumn;//冻结列数
	private TotalType totalType;//合计方式
	private Boolean multiboxSelectOnly;//仅点击复选框时选中
	private List<JqGridProperty> jqGridPropertys;//属性自由扩展列表
	private Boolean searchFaint;//是否启用模糊查询
	//view
    private String code;//编码
	private String name;//名称
	private DataTable dataTable;//数据表
	private String remark;//备注
	private Boolean standard;//是否是标准的视图
	private Long menuId;//菜单列表
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public List<ListColumn> getColumns() {
		return columns;
	}
	public void setColumns(List<ListColumn> columns) {
		this.columns = columns;
	}
	public Boolean getRowNumbers() {
		return rowNumbers;
	}
	public void setRowNumbers(Boolean rowNumbers) {
		this.rowNumbers = rowNumbers;
	}
	public Boolean getPagination() {
		return pagination;
	}
	public void setPagination(Boolean pagination) {
		this.pagination = pagination;
	}
	public Boolean getTotalable() {
		return totalable;
	}
	public void setTotalable(Boolean totalable) {
		this.totalable = totalable;
	}
	public Boolean getSearchTotalable() {
		return searchTotalable;
	}
	public void setSearchTotalable(Boolean searchTotalable) {
		this.searchTotalable = searchTotalable;
	}
	public Integer getRowNum() {
		return rowNum;
	}
	public void setRowNum(Integer rowNum) {
		this.rowNum = rowNum;
	}
	public String getRowList() {
		return rowList;
	}
	public void setRowList(String rowList) {
		this.rowList = rowList;
	}
	public Boolean getDefaultListView() {
		return defaultListView;
	}
	public void setDefaultListView(Boolean defaultListView) {
		this.defaultListView = defaultListView;
	}
	public Boolean getEditable() {
		return editable;
	}
	public void setEditable(Boolean editable) {
		this.editable = editable;
	}
	public Integer getActWidth() {
		return actWidth;
	}
	public void setActWidth(Integer actWidth) {
		this.actWidth = actWidth;
	}
	public String getEditUrl() {
		return editUrl;
	}
	public void setEditUrl(String editUrl) {
		this.editUrl = editUrl;
	}
	public Boolean getAdvancedQuery() {
		return advancedQuery;
	}
	public void setAdvancedQuery(Boolean advancedQuery) {
		this.advancedQuery = advancedQuery;
	}
	public StartQuery getStartQuery() {
		return startQuery;
	}
	public void setStartQuery(StartQuery startQuery) {
		this.startQuery = startQuery;
	}
	public Boolean getPopUp() {
		return popUp;
	}
	public void setPopUp(Boolean popUp) {
		this.popUp = popUp;
	}
	public String getDeleteUrl() {
		return deleteUrl;
	}
	public void setDeleteUrl(String deleteUrl) {
		this.deleteUrl = deleteUrl;
	}
	public String getOrderFieldName() {
		return orderFieldName;
	}
	public void setOrderFieldName(String orderFieldName) {
		this.orderFieldName = orderFieldName;
	}
	public String getDragRowUrl() {
		return dragRowUrl;
	}
	public void setDragRowUrl(String dragRowUrl) {
		this.dragRowUrl = dragRowUrl;
	}
	public Boolean getMultiSelect() {
		return multiSelect;
	}
	public void setMultiSelect(Boolean multiSelect) {
		this.multiSelect = multiSelect;
	}
	public String getDefaultSortField() {
		return defaultSortField;
	}
	public void setDefaultSortField(String defaultSortField) {
		this.defaultSortField = defaultSortField;
	}
	public OrderType getOrderType() {
		return orderType;
	}
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}
	public Integer getFrozenColumn() {
		return frozenColumn;
	}
	public void setFrozenColumn(Integer frozenColumn) {
		this.frozenColumn = frozenColumn;
	}
	public TotalType getTotalType() {
		return totalType;
	}
	public void setTotalType(TotalType totalType) {
		this.totalType = totalType;
	}
	public Boolean getMultiboxSelectOnly() {
		return multiboxSelectOnly;
	}
	public void setMultiboxSelectOnly(Boolean multiboxSelectOnly) {
		this.multiboxSelectOnly = multiboxSelectOnly;
	}
	public List<JqGridProperty> getJqGridPropertys() {
		return jqGridPropertys;
	}
	public void setJqGridPropertys(List<JqGridProperty> jqGridPropertys) {
		this.jqGridPropertys = jqGridPropertys;
	}
	public Boolean getSearchFaint() {
		return searchFaint;
	}
	public void setSearchFaint(Boolean searchFaint) {
		this.searchFaint = searchFaint;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public DataTable getDataTable() {
		return dataTable;
	}
	public void setDataTable(DataTable dataTable) {
		this.dataTable = dataTable;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Boolean getStandard() {
		return standard;
	}
	public void setStandard(Boolean standard) {
		this.standard = standard;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
}
