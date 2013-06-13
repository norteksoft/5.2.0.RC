package com.norteksoft.mms.authority.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.mms.authority.entity.Condition;
import com.norteksoft.mms.authority.entity.DataRule;
import com.norteksoft.mms.authority.entity.RuleType;
import com.norteksoft.mms.authority.service.ConditionManager;
import com.norteksoft.mms.authority.service.DataRuleManager;
import com.norteksoft.mms.authority.service.RuleTypeManager;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Namespace("/authority")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "data-rule", type = "redirectAction") })
public class DataRuleAction extends CrudActionSupport<DataRule>{
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String ids;
	private DataRule dataRule;
	private Page<DataRule> page=new Page<DataRule>(0,true);
	private Page<DataTable> dataRulePage=new Page<DataTable>(0,true);
	private Page<TableColumn> tableColumnPage=new Page<TableColumn>(0,true);
	private Page<Condition> conditionPage=new Page<Condition>(0,true);
	private Long dataTableMenuId;
	private Long tableId;
	private String currentInputId;
	private Long ruletypeId;
	private Long dataRuleId;
	private String dataValue;
	private List<String[]> values = new ArrayList<String[]>();
	
	@Autowired
	private DataRuleManager dataRuleManager;
	@Autowired
	private MenuManager menuManager;
	@Autowired
	private ConditionManager conditionManager;
	@Autowired
	private RuleTypeManager ruleTypeManager;
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	
	protected void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}

	@Override
	@Action("data-rule-delete")
	public String delete() throws Exception {
		dataRuleManager.deleteDataRule(ids);
		this.renderText("ok");
		return null;
	}
	
	@Action("data-rule-validateDelete")
	public String validateDelete() throws Exception {
		String result=dataRuleManager.validateDelete(ids);
		if(StringUtils.isNotEmpty(result)){
			this.renderText(result);
		}else{
			this.renderText("ok");
		}
		return null;
	}

	@Override
	@Action("data-rule-input")
	public String input() throws Exception {
		if(dataRuleId==null && ruletypeId != null){
			RuleType ruleType=ruleTypeManager.getRuleType(ruletypeId);
			dataRule.setRuleTypeId(ruletypeId);
			dataRule.setRuleTypeName(ruleType.getName());
		}
		return "data-rule-input";
	}

	@Override
	@Action("data-rule")
	public String list() throws Exception {
		if(page.getPageSize()>1){
			dataRuleManager.getDataRulesByRuleType(page,ruletypeId);
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return "data-rule";
	}

	protected void prepareModel() throws Exception {
		if(dataRuleId==null){
			dataRule=new DataRule();
		}else{
			dataRule=dataRuleManager.getDataRule(dataRuleId);
		}
	}

	@Override
	@Action("data-rule-save")
	public String save() throws Exception {
		if(dataTableMenuId != null){
			Menu menu=menuManager.getMenu(dataTableMenuId);
			if(menu != null)
				dataRule.setSystemId(menu.getSystemId());
		}
		dataRuleManager.saveDataRule(dataRule);
		dataRuleId=dataRule.getId();
		addSuccessMessage("保存成功");
		return "data-rule-input";
	}
	
	public void prepareValidateOnlyCode() throws Exception {
		prepareModel();
	}
	
	/**
	 * 验证数据规则的编码是否唯一
	 * @return
	 */
	@Action("validate-only-code")
	public String validateOnlyCode() {
		boolean sign=true;
		DataRule original;
		if(dataRuleId==null){
			original=dataRuleManager.getDataRuleByCode(dataRule.getCode());
			if(original!=null){
				sign=false;
			}
		}else{
			original=dataRuleManager.getDataRuleByCode(dataRule.getCode(),dataRuleId);
			if(original!=null){
				sign=false;
			}
		}
		if(sign){
			this.renderText("ok");
		}else{
			this.renderText("no");
		}
		return null;
	}
	
	/**
	 * 选择数据表
	 * @return
	 */
	@Action("data-rule-selectDataTable")
	public String selectDataTable() {
		if(dataRulePage.getPageSize()>1){
			dataRuleManager.findAllEnabledDataTable(dataRulePage);
			this.renderText(PageUtils.pageToJson(dataRulePage));
			return null;
		}
		return "data-rule-selectDataTable";
	}
	
	/**
	 * 选择数据表字段
	 * @return
	 */
	@Action("data-rule-selectColumn")
	public String selectColumn() {
		if(tableColumnPage.getPageSize()>1){
			dataRuleManager.getTableColumnByDataTableId(tableColumnPage,tableId);
			this.renderText(PageUtils.pageToJson(tableColumnPage));
			return null;
		}
		return "data-rule-selectColumn";
	}
	/**
	 * 设置布尔型数据的条件值
	 * @return
	 */
	@Action("data-rule-setValue")
	public String setValue() {
		String[] stringArray = dataValue.split(",");
		for (String s : stringArray) {
			String[] str = new String[2];
			str[0] = s.split(":")[0];
			str[1] = s.split(":")[1].replace("'", "");
			values.add(str);
		}
		return "data-rule-setValue";
	}
	
	
	
	/**
	 * 删除数据表规则条件
	 * @return
	 */
	@Action("data-rule-deleteCondition")
	public String deleteCondition() {
		conditionManager.delete(id);
		String callback=Struts2Utils.getParameter("callback");
		this.renderText(callback+"({msg:'删除成功'})");
		return null;
	}
	
	/**
	 * 根据规则id获得数据表规则条件
	 * @return
	 */
	@Action("data-rule-condition-list")
	public String conditionList() {
		if(conditionPage.getPageSize()>1){
			if(id!=null){
				conditionManager.getConditionPage(conditionPage, id);
				this.renderText(PageUtils.pageToJson(conditionPage));
			}
		}
		return null;
	}
	
	/**
	 * 规则类别树
	 * @return
	 * @throws Exception
	 */
	@Action("data-rule-type-tree")
	public String dataRuleTypeTree() throws Exception {
		StringBuilder tree=new StringBuilder();
		List<RuleType> ruleTypes=ruleTypeManager.getRootRuleTypeByCompany();
		if(ruleTypes.size()<=0){
			tree.append(JsTreeUtils.generateJsTreeNodeNew("root", "", "规则类别", "root"));
		}else{
			tree.append(JsTreeUtils.generateJsTreeNodeNew("root", "open", "规则类别",ruleTypeChildren(ruleTypes) ,"root"));
		}
		this.renderText(tree.toString());
		return null;
	}
	private String ruleTypeChildren(List<RuleType> ruleTypes){
		StringBuilder tree=new StringBuilder();
		for(RuleType type :ruleTypes){
			List<RuleType> children = ruleTypeManager.getTypsByParentId(type.getId());
			if(children==null||children.size()==0){
				tree.append(JsTreeUtils.generateJsTreeNodeNew("ruleType_"+type.getId().toString(), "close",  type.getName(), "ruleType")).append(",");
			}else{
				tree.append(JsTreeUtils.generateJsTreeNodeNew("ruleType_"+type.getId().toString(), "open",  type.getName(), ruleTypeChildren(children),"ruleType")).append(",");
			}
		}
		JsTreeUtils.removeLastComma(tree);
		return tree.toString();
	}

	public DataRule getModel() {
		return dataRule;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public DataRule getDataRule() {
		return dataRule;
	}

	public void setDataRule(DataRule dataRule) {
		this.dataRule = dataRule;
	}

	public Page<DataRule> getPage() {
		return page;
	}

	public void setPage(Page<DataRule> page) {
		this.page = page;
	}

	public Page<DataTable> getDataRulePage() {
		return dataRulePage;
	}

	public void setDataRulePage(Page<DataTable> dataRulePage) {
		this.dataRulePage = dataRulePage;
	}

	public void setDataTableMenuId(Long dataTableMenuId) {
		this.dataTableMenuId = dataTableMenuId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public Long getTableId() {
		return tableId;
	}

	public Page<TableColumn> getTableColumnPage() {
		return tableColumnPage;
	}

	public void setTableColumnPage(Page<TableColumn> tableColumnPage) {
		this.tableColumnPage = tableColumnPage;
	}

	public String getCurrentInputId() {
		return currentInputId;
	}

	public void setCurrentInputId(String currentInputId) {
		this.currentInputId = currentInputId;
	}

	public Page<Condition> getConditionPage() {
		return conditionPage;
	}

	public void setConditionPage(Page<Condition> conditionPage) {
		this.conditionPage = conditionPage;
	}

	public Long getRuletypeId() {
		return ruletypeId;
	}

	public void setRuletypeId(Long ruletypeId) {
		this.ruletypeId = ruletypeId;
	}

	public Long getDataRuleId() {
		return dataRuleId;
	}

	public void setDataRuleId(Long dataRuleId) {
		this.dataRuleId = dataRuleId;
	}
	public String getDataValue() {
		return dataValue;
	}
	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}
	public List<String[]> getValues() {
		return values;
	}
	public void setValues(List<String[]> values) {
		this.values = values;
	}
	

}
