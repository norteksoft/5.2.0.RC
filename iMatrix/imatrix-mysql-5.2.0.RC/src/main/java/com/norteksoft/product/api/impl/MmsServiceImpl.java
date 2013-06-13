package com.norteksoft.product.api.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.mms.base.ExportDynamicColumnValues;
import com.norteksoft.mms.base.MmsUtil;
import com.norteksoft.mms.base.utils.view.DynamicColumnDefinition;
import com.norteksoft.mms.base.utils.view.ExportData;
import com.norteksoft.mms.base.utils.view.GridColumnInfo;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.service.DataTableManager;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.api.MmsService;
import com.norteksoft.product.api.entity.FormView;
import com.norteksoft.product.api.entity.ListView;
import com.norteksoft.product.api.entity.Menu;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.base.utils.WorkflowMemcachedUtil;
@Service
@Transactional
public class MmsServiceImpl implements MmsService {

	@Autowired
	private ListViewManager listViewManager;
	@Autowired
	private FormViewManager formViewManager;
	@Autowired
	private DataTableManager dataTableManager;
	
	@Autowired
	private MenuManager menuManager;
	@Autowired
	private MmsUtil mmsUtil;
	
	public void deleteCustomListView(String code) {
		com.norteksoft.mms.form.entity.ListView view=listViewManager.getListViewByCode(code);
		if(view!=null)listViewManager.delete(view.getId()+"");
	}

	public List<Menu> getTopMenus(){
		return BeanUtil.turnToModelMenuList(menuManager.getRootMenuByCompany());
	}
	
	public Menu getTopMenu(String code){
		return BeanUtil.turnToModelMenu(menuManager.getMenuByCode(code));
	}

	public ExportData getDynamicColumnExportData(Page<?> page,
			ExportDynamicColumnValues exportDynamicColumnValues) {
		return mmsUtil.getDynamicColumnExportData(page, exportDynamicColumnValues);
	}

	public Map<String, DynamicColumnDefinition> getDynamicColumnName() {
		return mmsUtil.getDynamicColumnName();
	}

	public ExportData getExportData(Page<?> page, String listCode) {
		return mmsUtil.getExportData(page, listCode);
	}

	public GridColumnInfo getGridColumnInfo(String code) {
		return mmsUtil.getGridColumnInfo(code);
	}

	public ListView getListViewByCode(String code) {
		return BeanUtil.turnToModelListView(mmsUtil.getListViewByCode(code));
	}

	public List<ListView> getListViews(String... systemCodes) {
		return BeanUtil.turnToModelListViewList(mmsUtil.getListViews(systemCodes));
	}

	public void saveColums(String code, String name, String tableName) {
		mmsUtil.saveColums(code, name, tableName);
	}
	
	public String getColumnsByCode(String code){
		return mmsUtil.getColumnsByCode(code);
	}
	
	public String getExportColumnsByCode(String code){
		return mmsUtil.getExportColumnsByCode(code);
	}
	
	@Deprecated
	public void saveView(com.norteksoft.mms.form.entity.ListView view) {
		listViewManager.saveListView(view);
		
	}

	public void saveView(ListView view) {
		listViewManager.saveListView(BeanUtil.turnToListView(view));
		
	}
	
	public String[] getDynamicColumnNames(){
		return mmsUtil.getDynamicColumnNames();
	}
	
	public FormView getFormViewByCode(String code,Integer version) {
		return BeanUtil.turnToModelFormView(formViewManager.getCurrentFormViewByCodeAndVersion(code, version));
	}

	public ListView getDefaultListViewByDataTable(String tableName) {
		DataTable table = dataTableManager.getDataTableByTableName(tableName);
		com.norteksoft.mms.form.entity.ListView view = listViewManager.getDefaultDisplay(table.getId());
		List<com.norteksoft.mms.form.entity.ListView> views = listViewManager.getListViewByTabelId(table.getId());
		if(view==null){
			if(views.size()>0)view = views.get(0);
		}
		return BeanUtil.turnToModelListView(view);
	}

	public List<String> getSignatureFieldByFormViewCode(String code) {
		com.norteksoft.mms.form.entity.FormView view = formViewManager.getHighFormViewByCode(code);
    	return getSignatureFields(view);
	}
	
	@Deprecated
	@SuppressWarnings("unchecked")
	private List<String> getSignatureFields(com.norteksoft.mms.form.entity.FormView view){
		List<String> result = (List<String>)WorkflowMemcachedUtil.get(view.getCode()+"~"+view.getVersion()+"~"+view.getCompanyId());
    	if(result == null) result = formViewManager.getSignatureField(view);
    	return result;
	}
	
	@SuppressWarnings("unchecked")
	private List<String> getSignatureFields(FormView view){
		List<String> result = (List<String>)WorkflowMemcachedUtil.get(view.getCode()+"~"+view.getVersion()+"~"+view.getCompanyId());
    	if(result == null) result = formViewManager.getSignatureField(BeanUtil.turnToFormView(view));
    	return result;
	}

	public List<String> getSignatureFieldByFormViewCode(String code,
			Integer version) {
		com.norteksoft.mms.form.entity.FormView view = formViewManager.getFormViewByCodeAndVersion(ContextUtils.getCompanyId(), code, version);
		return getSignatureFields(view);
	}
}
