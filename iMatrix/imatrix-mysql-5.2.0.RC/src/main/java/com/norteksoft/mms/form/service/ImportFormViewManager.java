package com.norteksoft.mms.form.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.options.entity.ImportDefinition;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.api.impl.DefaultDataImporterCallBack;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.util.ContextUtils;

@Service
@Transactional
public class ImportFormViewManager extends DefaultDataImporterCallBack{
	@Autowired
	private FormViewManager formViewManager;
	@Autowired
	private DataTableManager dataTableManager;
	@Autowired
	private MenuManager menuManager;
	
	public String saveSingleRow(String[] rowValue,ImportDefinition importDefinition) {
		FormView formview=formViewManager.getCurrentFormViewByCodeAndVersion(rowValue[0], Integer.parseInt(rowValue[1]));
		if(formview==null){
			formview=new FormView();
			formview.setCode(rowValue[0]);
			formview.setVersion(Integer.valueOf(rowValue[1]));
		}
		formview.setCreator(ContextUtils.getLoginName());
		formview.setCreatorName(ContextUtils.getUserName());
		formview.setCreatedTime(new Date());
		formview.setCompanyId(ContextUtils.getCompanyId());
		formview.setName(rowValue[2]);
		DataTable datatable=dataTableManager.getDataTableByTableName(rowValue[3]);
		if(datatable!=null)formview.setDataTable(datatable);
		formview.setStandard(Boolean.valueOf(rowValue[4]));
		Menu menu=menuManager.getMenuByCode(rowValue[5]);
		if(menu!=null)formview.setMenuId(menu.getId());
		formview.setHtml(rowValue[6]);
		formview.setFormState(DataState.valueOf(rowValue[7]));
		formViewManager.save(formview);
		return "";
	}
}
