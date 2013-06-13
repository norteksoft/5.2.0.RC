package com.norteksoft.mms.authority.web;


import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.mms.authority.entity.RuleType;
import com.norteksoft.mms.authority.service.RuleTypeManager;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;

@Namespace("/authority")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "rule-type", type = "redirectAction") })
public class RuleTypeAction extends CrudActionSupport<RuleType> {

	private static final long serialVersionUID = 1L;
	private Long id;
	private RuleType ruleType;
	private Long parentRuleTypeId;
	private String code;
	
	@Autowired
	private RuleTypeManager ruleTypeManager;
	
	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			ruleType=new RuleType();
			if(parentRuleTypeId!=null){
				ruleType.setParent(ruleTypeManager.getRuleType(parentRuleTypeId));
			}
		}else {
			ruleType=ruleTypeManager.getRuleType(id);
		}
	}
	
	@Action("rule-type-input")
	@Override
	public String input() throws Exception {
		return "rule-type-input";
	}
	
	@Action("rule-type-save")
	@Override
	public String save() throws Exception {
		ruleTypeManager.saveRuleType(ruleType);
		if(parentRuleTypeId!=null){
			ruleType.setParent(ruleTypeManager.getRuleType(parentRuleTypeId));
		}
		renderText(ruleType.getId().toString()+":"+ruleType.getName());
		return null;
	}
	
	@Action("rule-type-validateCode")
	public String validateCode(){
		String result = ruleTypeManager.validateCode(code,id);
		renderText(result);
		return null;
	}
	
	@Action("rule-type-delete")
	@Override
	public String delete() throws Exception {
		this.renderText(ruleTypeManager.deleteRuleType(id));
		return null;
	}

	@Action("list")
	@Override
	public String list() throws Exception {
		return "rule-type";
	}
	
	@Action("rule-type-tree")
	public String ruleTypeTree(){
		List<RuleType> ruleTypes = ruleTypeManager.getRootRuleTypeByCompany();
		StringBuilder tree = new StringBuilder();
		if(ruleTypes.size()<=0){
			tree.append(JsTreeUtils.generateJsTreeNodeNew("root", "", "规则类别", "root"));
		}else{
			tree.append(JsTreeUtils.generateJsTreeNodeNew("root", "open", "规则类别",ruleTypeChildren(ruleTypes) ,"root"));
		}
		renderText(tree.toString());
		return null;
	}
	
	private String ruleTypeChildren(List<RuleType> ruleTypes) {
		StringBuilder tree = new StringBuilder("[ ");
		for(RuleType type :ruleTypes){
			List<RuleType> children = ruleTypeManager.getTypsByParentId(type.getId());
			if(children==null){
				tree.append(JsTreeUtils.generateJsTreeNodeNew(type.getId().toString(), "close",  type.getName(), "")).append(",");
			}else{
				tree.append(JsTreeUtils.generateJsTreeNodeNew(type.getId().toString(), "close",  type.getName(), childRuleType(children),"")).append(",");
			}
		}
		JsTreeUtils.removeLastComma(tree);
		tree.append(" ]");
		return tree.toString();
	}

	/*
	 * 递归规则类型父子关系，形成tree
	 */
	private String childRuleType(List<RuleType> ruleTypes){
		//java.util.Collections.sort(ruleTypes);
		StringBuilder tree = new StringBuilder();
		for(RuleType type :ruleTypes){
			List<RuleType> children = ruleTypeManager.getTypsByParentId(type.getId());
			if(children==null){
				tree.append(JsTreeUtils.generateJsTreeNodeNew(type.getId().toString(), "close",  type.getName(), "")).append(",");
			}else{
				tree.append(JsTreeUtils.generateJsTreeNodeNew(type.getId().toString(), "close",  type.getName(), childRuleType(children),"")).append(",");
			}
		}
		JsTreeUtils.removeLastComma(tree);
		return tree.toString();
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	
	public RuleType getModel() {
		return ruleType;
	}

	public Long getParentRuleTypeId() {
		return parentRuleTypeId;
	}

	public void setParentRuleTypeId(Long parentRuleTypeId) {
		this.parentRuleTypeId = parentRuleTypeId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
}
