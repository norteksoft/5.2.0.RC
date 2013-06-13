package com.norteksoft.mms.base.data;

public class FileConfigModel {
	private String data;
	private String exportRootPath="basic-data";
	private String importRootPath="basic-data-temp";
	private String exportPath="portal-mms";
	private Integer importOrder=0;
	private String importPath="portal-mms";
	private String category="initData";
	private String beanname;
	private String filename;
	private String filenameStartwith;
	private String title;
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getExportPath() {
		return exportPath;
	}
	public void setExportPath(String exportPath) {
		this.exportPath = exportPath;
	}
	public Integer getImportOrder() {
		return importOrder;
	}
	public void setImportOrder(Integer importOrder) {
		this.importOrder = importOrder;
	}
	public String getImportPath() {
		return importPath;
	}
	public void setImportPath(String importPath) {
		this.importPath = importPath;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getBeanname() {
		return beanname;
	}
	public void setBeanname(String beanname) {
		this.beanname = beanname;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFilenameStartwith() {
		return filenameStartwith;
	}
	public void setFilenameStartwith(String filenameStartwith) {
		this.filenameStartwith = filenameStartwith;
	}
	public String getExportRootPath() {
		return exportRootPath;
	}
	public void setExportRootPath(String exportRootPath) {
		this.exportRootPath = exportRootPath;
	}
	public String getImportRootPath() {
		return importRootPath;
	}
	public void setImportRootPath(String importRootPath) {
		this.importRootPath = importRootPath;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public boolean equals(FileConfigModel config) {
		return this.getData().equals(config.getData());
	}
}
