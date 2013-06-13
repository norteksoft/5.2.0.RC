package com.norteksoft.mms.form.web;

import java.util.List;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.service.ListColumnManager;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
@Namespace("/form")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "list-column", type = "redirectAction")})
public class ListColumnAction extends CrudActionSupport<ListColumn> {
	private static final long serialVersionUID = 1L;
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	protected void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	private List<ListColumn> columns;
	private List<TableColumn> tableColumns;
	private Long viewId;
	private Long dataTableId;
	private Long menuId;
	private Long id;//listColumn的id
	private Long tableColumnId;//数据表列表的的行id
	
	private ListView listView;
	private Integer frozenColumnAmount;
	
	private ListColumnManager listColumnManager;
	private ListViewManager  listViewManager;
	@Autowired
	public void setListColumnManager(ListColumnManager listColumnManager) {
		this.listColumnManager = listColumnManager;
	}
	@Autowired
	public void setListViewManager(ListViewManager listViewManager) {
		this.listViewManager = listViewManager;
	}
	@Override
	public String delete() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String input() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String list() throws Exception {
		listView=listViewManager.getView(viewId);
		frozenColumnAmount=listView.getFrozenColumn()==null?0:listView.getFrozenColumn();
		ApiFactory.getBussinessLogService().log("列表管理", 
				"字段信息列表", 
				ContextUtils.getSystemId("mms"));
		return SUCCESS;
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
	public String saveColumns() throws Exception {
		listColumnManager.save(viewId);
		ApiFactory.getBussinessLogService().log("列表管理", 
				"保存字段信息", 
				ContextUtils.getSystemId("mms"));
		addSuccessMessage("保存成功");
		return list();
	}
	
	public String deleteColumn() throws Exception {
		listColumnManager.deleteColumn(id);
		String callback=Struts2Utils.getParameter("callback");
		ApiFactory.getBussinessLogService().log("列表管理", 
				"删除字段信息", 
				ContextUtils.getSystemId("mms"));
		this.renderText(callback+"({msg:'删除成功'})");
		return null;
	}
	
	/**
	 * 根据数据表字段信息的行id获取列表中的值设置
	 * @return
	 */
	public String getValuesetByTableColumn(){
		String valueSet = listColumnManager.getValuesetByTableColumn(tableColumnId);
		renderText(valueSet);
		return null;
	}

	public ListColumn getModel() {
		// TODO Auto-generated method stub
		return null;
	}
	public Long getViewId() {
		return viewId;
	}
	public void setViewId(Long viewId) {
		this.viewId = viewId;
	}
	public List<TableColumn> getTableColumns() {
		return tableColumns;
	}
	public List<ListColumn> getColumns() {
		return columns;
	}
	public Long getDataTableId() {
		return dataTableId;
	}
	public ListView getListView() {
		return listView;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getFrozenColumnAmount() {
		return frozenColumnAmount;
	}
	public Long getTableColumnId() {
		return tableColumnId;
	}
	public void setTableColumnId(Long tableColumnId) {
		this.tableColumnId = tableColumnId;
	}

}
