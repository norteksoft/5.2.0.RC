package com.norteksoft.mms.authority.web;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.service.authorization.StandardRoleManager;
import com.norteksoft.mms.authority.entity.DataRule;
import com.norteksoft.mms.authority.entity.Permission;
import com.norteksoft.mms.authority.entity.PermissionItem;
import com.norteksoft.mms.authority.entity.RuleType;
import com.norteksoft.mms.authority.enumeration.PermissionAuthorize;
import com.norteksoft.mms.authority.service.DataRuleManager;
import com.norteksoft.mms.authority.service.PermissionItemManager;
import com.norteksoft.mms.authority.service.PermissionManager;
import com.norteksoft.mms.authority.service.RuleTypeManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Namespace("/authority")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "permission", type = "redirectAction") })
public class PermissionAction extends CrudActionSupport<Permission>{

	private static final long serialVersionUID = 1L;
	private Permission permission;
	private Long permissionId;
	private Long id;//主子表中用到
	private String ids;
	private Page<Permission> page=new Page<Permission>(0, true);
	private Page<PermissionItem> itemPage=new Page<PermissionItem>(0, true);
	private Long dataRuleId;
	private DataRule permissionDataRule;
	private List<PermissionAuthorize> docAuthes;
	private String validateAuths;//验证数据授权用到了，以逗号隔开的数字，如：1,2,4
	private Integer permissionPriority;//验证数据授权用到
	@Autowired
	private PermissionManager permissionManager;
	@Autowired
	private PermissionItemManager permissionItemManager;
	@Autowired
	private StandardRoleManager standardRoleManager;
	@Autowired
	private DataRuleManager dataRuleManager;
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
	/**
	 * 删除数据授权
	 */
	@Override
	@Action("permission-delete")
	public String delete() throws Exception {
		permissionManager.deletePermissions(ids);
		addSuccessMessage("删除成功");
		ApiFactory.getBussinessLogService().log("数据授权", "删除数据授权",ContextUtils.getSystemId("mms"));
		return "permission-data";
	}

	/**
	 * 数据授权表单页面
	 */
	@Override
	@Action("permission-input")
	public String input() throws Exception {
		permissionDataRule=dataRuleManager.getDataRule(dataRuleId);
		ApiFactory.getBussinessLogService().log("数据授权", "数据授权表单",ContextUtils.getSystemId("mms"));
		return "permission-input";
	}

	
	@Override
	public String list() throws Exception {
		return SUCCESS;
	}
	/**
	 * 数据授权列表
	 * @return
	 * @throws Exception
	 */
	@Action("permission-data")
	public String permissionList() throws Exception {
		if(page.getPageSize()>1){
			ApiFactory.getBussinessLogService().log("数据授权", "数据授权列表",ContextUtils.getSystemId("mms"));
			permissionManager.getPermissions(page,dataRuleId);
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return SUCCESS;
	}
	
	/**
	 * 数据授权条件项列表
	 * @return
	 * @throws Exception
	 */
	@Action("permission-item-list")
	public String permissionItemList() throws Exception {
		if(itemPage.getPageSize()>1){
			if(id!=null){
				permissionItemManager.getPermissionItems(itemPage, id);
				this.renderText(PageUtils.pageToJson(itemPage));
			}
		}
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		if(permissionId==null){
			permission=new Permission();
		}else{
			permission=permissionManager.getPermissions(permissionId);
		}
	}

	/**
	 * 保存数据授权
	 */
	@Override
	@Action("permission-save")
	public String save() throws Exception {
		permissionDataRule=dataRuleManager.getDataRule(dataRuleId);
		permission.setDataRule(permissionDataRule);
		permissionManager.savePermission(permission,docAuthes);
		permissionId=permission.getId();
		addSuccessMessage("保存成功");
		ApiFactory.getBussinessLogService().log("数据授权", "保存数据授权",ContextUtils.getSystemId("mms"));
		return "permission-input";
	}
	
	/**
	 * 角色树
	 * @return
	 * @throws Exception
	 */
	@Action("role-tree")
	public String roleTree() throws Exception{
		permissionDataRule=dataRuleManager.getDataRule(dataRuleId);
		StringBuilder tree=new StringBuilder();
		List<Role> roles=standardRoleManager.getAllStandardRole(permissionDataRule.getSystemId());
		if(roles.size()<=0){
			tree.append(JsTreeUtils.generateJsTreeNodeNew("_role", "", "角色", ""));
		}else{
			tree.append(JsTreeUtils.generateJsTreeNodeNew("_role", "open", "角色",roles(roles) ,""));
		}
		this.renderText(tree.toString());
		return null;
	}
	
	private String roles(List<Role> roles){
		StringBuilder tree=new StringBuilder();
		for(Role role:roles){
			tree.append(JsTreeUtils.generateJsTreeNodeNew("role-"+role.getCode()+"-"+role.getName(), "", role.getName(),"")).append(",");
		}
		JsTreeUtils.removeLastComma(tree);
		return tree.toString();
	}
	/**
	 * 删除条件项
	 * @return
	 * @throws Exception
	 */
	@Action("permission-item-delete")
	public String permissionItemDelete() throws Exception {
		permissionItemManager.deletePermissionItem(id);
		String callback=Struts2Utils.getParameter("callback");
		ApiFactory.getBussinessLogService().log("数据授权", "删除数据授权中的条件项",ContextUtils.getSystemId("mms"));
		this.renderText(callback+"({msg:'删除成功'})");
		return null;
	}
	
	/**
	 * 验证该授权的优先级及操作权限
	 * @return
	 * @throws Exception
	 */
	@Action("validate-permission")
	public String validatePermission() throws Exception {
		ApiFactory.getBussinessLogService().log("数据授权", "验证数据授权",ContextUtils.getSystemId("mms"));
		this.renderText(permissionManager.validatePermission(validateAuths,dataRuleId,permissionId,permissionPriority));
		return null;
	}
	/**
	 * 数据规则树
	 * @return
	 * @throws Exception
	 */
	@Action("rule-type-data-tree")
	public String ruleTypeDataTree() throws Exception {
		StringBuilder tree=new StringBuilder();
		List<RuleType> ruleTypes=ruleTypeManager.getRootRuleTypeByCompany();
		if(ruleTypes.size()<=0){
			tree.append(JsTreeUtils.generateJsTreeNodeNew("root", "", "数据规则", "root"));
		}else{
			tree.append(JsTreeUtils.generateJsTreeNodeNew("root", "open", "数据规则",ruleTypeChildren(ruleTypes) ,"root"));
		}
		this.renderText(tree.toString());
		return null;
	}
	
	private String ruleTypeChildren(List<RuleType> ruleTypes){
		StringBuilder tree=new StringBuilder();
		for(RuleType type :ruleTypes){
			List<RuleType> children = ruleTypeManager.getTypsByParentId(type.getId());
			String dataRuleTree=dataRuleTree(type.getId());
			if(StringUtils.isNotEmpty(dataRuleTree)){
				if(children==null||children.size()==0){
					tree.append(JsTreeUtils.generateJsTreeNodeNew("ruleType_"+type.getId().toString(), "open",  type.getName(),dataRuleTree, "ruleType")).append(",");
				}else{
					tree.append(JsTreeUtils.generateJsTreeNodeNew("ruleType_"+type.getId().toString(), "open",  type.getName(),dataRuleTree+","+ ruleTypeChildren(children),"ruleType")).append(",");
				}
			}else{
				if(children==null||children.size()==0){
					tree.append(JsTreeUtils.generateJsTreeNodeNew("ruleType_"+type.getId().toString(), "close",  type.getName(), "ruleType")).append(",");
				}else{
					tree.append(JsTreeUtils.generateJsTreeNodeNew("ruleType_"+type.getId().toString(), "open",  type.getName(), ruleTypeChildren(children),"ruleType")).append(",");
				}
			}
		}
		JsTreeUtils.removeLastComma(tree);
		return tree.toString();
	}
	
	private String dataRuleTree(Long ruleTypeId){
		StringBuilder tree=new StringBuilder();
		List<DataRule> dataRules=dataRuleManager.getDataRulesByRuleType(ruleTypeId);
		for(DataRule dataRule :dataRules){
				tree.append(JsTreeUtils.generateJsTreeNodeNew("dataRule_"+dataRule.getId().toString(), "close",  dataRule.getName(), "dataRule")).append(",");
		}
		JsTreeUtils.removeLastComma(tree);
		return tree.toString();
	}

	public Permission getModel() {
		return permission;
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Page<Permission> getPage() {
		return page;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}


	public DataRule getPermissionDataRule() {
		return permissionDataRule;
	}

	public Long getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(Long permissionId) {
		this.permissionId = permissionId;
	}

	public Page<PermissionItem> getItemPage() {
		return itemPage;
	}

	public void setPage(Page<Permission> page) {
		this.page = page;
	}

	public void setItemPage(Page<PermissionItem> itemPage) {
		this.itemPage = itemPage;
	}

	public List<PermissionAuthorize> getDocAuthes() {
		return docAuthes;
	}

	public void setDocAuthes(List<PermissionAuthorize> docAuthes) {
		this.docAuthes = docAuthes;
	}

	public Long getDataRuleId() {
		return dataRuleId;
	}

	public void setDataRuleId(Long dataRuleId) {
		this.dataRuleId = dataRuleId;
	}
	public void setValidateAuths(String validateAuths) {
		this.validateAuths = validateAuths;
	}
	public void setPermissionPriority(Integer permissionPriority) {
		this.permissionPriority = permissionPriority;
	}

}
