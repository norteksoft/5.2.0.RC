package com.norteksoft.bs.options.web;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.bs.options.entity.ImportDefinition;
import com.norteksoft.bs.options.service.ImportDefinitionManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;

/**
 * 导入定义
 * @author Administrator
 *
 */
@Namespace("/options")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "import-definition", type = "redirectAction")})
public class ImportDefinitionAction extends CrudActionSupport<ImportDefinition> {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private ImportDefinition importDefinition;
	private Page<ImportDefinition> page=new Page<ImportDefinition>(0,true);
	private String ids;
	private Long importDefinitionId;
	
	private File file;
	private String fileName;
	
	@Autowired
	private ImportDefinitionManager importDefinitionManager;
	
	@Override
	@Action("import-definition-delete")
	public String delete() throws Exception {
		importDefinitionManager.delete(ids);
		return null;
	}

	@Override
	@Action("import-definition-input")
	public String input() throws Exception {
		return "import-definition-input";
	}

	@Override
	@Action("import-definition")
	public String list() throws Exception {
		if(page.getPageSize()>1){
			importDefinitionManager.getImportDefinitionPage(page);
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return "import-definition";
	}

	@Override
	protected void prepareModel() throws Exception {
		if(importDefinitionId==null){
			importDefinition=new ImportDefinition();
		}else{
			importDefinition=importDefinitionManager.getImportDefinition(importDefinitionId);
		}
	}

	@Override
	@Action("import-definition-save")
	public String save() throws Exception {
		if(validateOnlyCode()){
			importDefinitionManager.saveImportDefinition(importDefinition);
			this.renderText("ok"+importDefinition.getId().toString());
		}else{
			this.renderText("no");
		}
		return null;
	}
	
	/**
	 * 验证导入定义的编码是否唯一
	 * @return
	 */
	private boolean validateOnlyCode() {
		boolean sign=true;
		ImportDefinition original;
		if(importDefinitionId==null){
			original=importDefinitionManager.getImportDefinitionByCode(importDefinition.getCode());
			if(original!=null){
				sign=false;
			}
		}else{
			original=importDefinitionManager.getImportDefinitionByCode(importDefinition.getCode(),importDefinitionId);
			if(original!=null){
				sign=false;
			}
		}
		return sign;
	}

	/**
	 * 字段设置页面
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("import-definition-column")
	public String importDefinitionColumn() throws Exception {
		importDefinition=importDefinitionManager.getImportDefinition(importDefinitionId);
		return "import-definition-column";
	}
	
	/**
	 * 保存导入列
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("import-definition-column-save")
	public String importDefinitionColumnSave() throws Exception {
		importDefinitionManager.saveImportColumn(importDefinitionId);
		return importDefinitionColumn();
	}
	
	/**
	 * 删除导入列
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("import-definition-column-delete")
	public String importDefinitionColumnDelete() throws Exception {
		importDefinitionManager.importColumnDelete(id);
		String callback=Struts2Utils.getParameter("callback");
		this.renderText(callback+"({msg:'删除成功'})");
		return null;
	}
	
	/**
	 * 导入页面
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("import-definition-import")
	public String importDefinitionImport() throws Exception {
		return "import-definition-import";
	}
	
	public void prepareImportDefinitionShift() throws Exception {
		prepareModel();
	}
	
	/**
	 * 导入
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("import-definition-shift")
	public String importDefinitionShift() throws Exception {
		String result = "";
		try {
			result = importDefinitionManager.importFile(file,fileName, importDefinition);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		renderText(result);
		return null;
	}

	public ImportDefinition getModel() {
		return importDefinition;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ImportDefinition getImportDefinition() {
		return importDefinition;
	}

	public void setImportDefinition(ImportDefinition importDefinition) {
		this.importDefinition = importDefinition;
	}

	public Page<ImportDefinition> getPage() {
		return page;
	}

	public void setPage(Page<ImportDefinition> page) {
		this.page = page;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
	
	public Long getImportDefinitionId() {
		return importDefinitionId;
	}

	public void setImportDefinitionId(Long importDefinitionId) {
		this.importDefinitionId = importDefinitionId;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	public void setFileFileName(String fileName) {
		this.fileName = fileName;
	}
}
