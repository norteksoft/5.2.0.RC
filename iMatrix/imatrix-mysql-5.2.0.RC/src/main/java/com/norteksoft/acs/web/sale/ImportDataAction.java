package com.norteksoft.acs.web.sale;

import java.io.File;

import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.service.sale.ImportDataManager;

/**
 * ImportDataAction.java
 * @author Administrator
 */
@SuppressWarnings("unchecked")
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "/sale/import-data!input.action", type = "redirect") })
public class ImportDataAction extends CRUDActionSupport {

	private static final long serialVersionUID = 1L;
	private ImportDataManager importDataManager;
	private String tableName;
	private File file;
	
	

	@Override
	public String save() throws Exception {
		importDataManager.saveFileData(file, tableName);
		addActionMessage(tableName + "导入完成");
		return RELOAD;
	}

	@Required
	public void setImportDataManager(ImportDataManager importDataManager) {
		this.importDataManager = importDataManager;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public String delete() throws Exception {
		return null;
	}

	@Override
	public String list() throws Exception {
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		
	}

	public Object getModel() {
		return null;
	}

}
