package com.norteksoft.wf.engine.web;

import java.sql.Timestamp;
import java.util.List;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.wf.engine.entity.DataDictionaryType;
import com.norteksoft.wf.engine.service.DataDictionaryTypeManager;

@Namespace("/engine")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "data-dictionary-type", type = "redirectAction") })
public class DataDictionaryTypeAction extends CrudActionSupport<DataDictionaryType>{
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	
	private DataDictionaryTypeManager dataDictionaryTypeManager;
	
	private Page<DataDictionaryType> page = new Page<DataDictionaryType>(0, true);
	
	private DataDictionaryType dataDictionaryType;
	
	private Long id;
	
	private String dictIds;
	
	private String no;
	
	private List<DataDictionaryType>  typeList;
	
	private List<Long> typeIdList;
	
	private String types;
	
	@Required
	public void setDataDictionaryTypeManager(
			DataDictionaryTypeManager dataDictionaryTypeManager) {
		this.dataDictionaryTypeManager = dataDictionaryTypeManager;
	}

	private static final long serialVersionUID = 1L;
	
	@Override
	public String delete() throws Exception {
		dataDictionaryTypeManager.deleteDictType(dictIds);
		ApiFactory.getBussinessLogService().log("数据字典类型", 
				"保存数据字典类型", 
				ContextUtils.getSystemId("wf"));
		return list();
	}

	@Override
	public String input() throws Exception {
		if(id==null){
			typeList=dataDictionaryTypeManager.getAllDictTypes();
		}else{
			typeList=dataDictionaryTypeManager.getAllDictTypes(id);
			types=dataDictionaryType.getTypeIds();
		}
		ApiFactory.getBussinessLogService().log("数据字典类型", 
				"数据字典类型表单页面", 
				ContextUtils.getSystemId("wf"));
		return "input";
	}

	@Override
	public String list() throws Exception {
		if(page.getPageSize()>1){
			dataDictionaryTypeManager.getDataDictTypesPage(page);
			ApiFactory.getBussinessLogService().log("数据字典类型", 
					"数据字典类型列表", 
					ContextUtils.getSystemId("wf"));
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return SUCCESS;
		
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			dataDictionaryType = new DataDictionaryType();
		}else{
			dataDictionaryType = dataDictionaryTypeManager.getDictTypeById(id);
		}
	}

	@Override
	public String save() throws Exception {
		dataDictionaryType.setCompanyId(getCompanyId());
		dataDictionaryType.setCreatedTime(new Timestamp(System.currentTimeMillis()));
		dataDictionaryType.setCreator(getLoginName());
		dataDictionaryType.setCreatorName(getUserName());
		if(typeIdList!=null){
			dataDictionaryType.setTypeIds(typeIdList.toString().replace("[", "").replace("]",""));
		}else{
			dataDictionaryType.setTypeIds(null);
		}
		dataDictionaryTypeManager.saveDictType(dataDictionaryType);
		id = dataDictionaryType.getId();
		ApiFactory.getBussinessLogService().log("数据字典类型", 
				"保存数据字典类型", 
				ContextUtils.getSystemId("wf"));
		this.addSuccessMessage("保存成功");
		return input();
	}
	private void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}

	public String validateTypeNo() throws Exception{
		Boolean flag=dataDictionaryTypeManager.isTypeNoExist(no);
		if(flag){  
			this.renderText("true");
		}else{
			this.renderText("false");
		}
		return null;
	}
	
	private String getLoginName(){
		return ContextUtils.getLoginName();
	}
	
	private String getUserName(){
		return ContextUtils.getUserName();
	}
	
	private Long getCompanyId(){
		return ContextUtils.getCompanyId();
	}

	public DataDictionaryType getModel() {
		return dataDictionaryType;
	}
	
	public Page<DataDictionaryType> getPage() {
		return page;
	}

	public void setPage(Page<DataDictionaryType> page) {
		this.page = page;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public void setDictIds(String dictIds) {
		this.dictIds = dictIds;
	}

	public List<DataDictionaryType> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<DataDictionaryType> typeList) {
		this.typeList = typeList;
	}

	public List<Long> getTypeIdList() {
		return typeIdList;
	}

	public void setTypeIdList(List<Long> typeIdList) {
		this.typeIdList = typeIdList;
	}
	public String getTypes() {
		return types;
	}

}
