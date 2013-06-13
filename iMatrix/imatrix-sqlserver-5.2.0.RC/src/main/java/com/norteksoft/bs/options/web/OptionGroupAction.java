package com.norteksoft.bs.options.web;

import java.util.Date;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.bs.options.entity.Option;
import com.norteksoft.bs.options.entity.OptionGroup;
import com.norteksoft.bs.options.service.OptionGroupManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;

@Namespace("/options")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "option-group", type = "redirectAction")})
public class OptionGroupAction extends CrudActionSupport<OptionGroup> {

	private static final long serialVersionUID = 1L;
	
	private Long optionGroupId;
	
	private OptionGroup optionGroup;
	
	private Page<OptionGroup> groups = new Page<OptionGroup>(0, true);
	
	private OptionGroupManager optionGroupManager;
	
	private String ids;
	
	private String groupName;
	
	private String groupNo;
	
	private List<Option> option;
	
	private Long optionId;
	
	private Long systemId;
	@Autowired
	private BusinessSystemManager businessSystemManager;
	private List<BusinessSystem> businessSystems;
	
	@Override
	public String list() throws Exception {
		if(groups.getPageSize()>1){
			optionGroupManager.getAllOptionGroupForPage(groups,systemId);
			ApiFactory.getBussinessLogService().log("选项组管理", "查看选项组列表",ContextUtils.getSystemId("bs"));
			renderText(PageUtils.pageToJson(groups));
			return null;
		}
		return SUCCESS;
	}
	
	@Action("option-group-input")
	public String input() throws Exception {
		if(optionGroupId != null){
			option = optionGroup.getOptions();
		}
		return SUCCESS;
	}
	
	public String checkGroupName() throws Exception {
		boolean canCreate = optionGroupManager.checkOptionGroupCanCreate(groupName, optionGroupId);
		renderText(String.valueOf(canCreate));
		return null;
	}
	
	public String checkGroupNo() throws Exception{
		boolean isOnly = optionGroupManager.checkOptionGroupForOnly(groupNo);
		renderText(String.valueOf(isOnly));
		return null;
	}
	
	@Action("option-group-save")
	public String save() throws Exception {
		if(optionGroupId == null){
			optionGroup.setCompanyId(ContextUtils.getCompanyId());
			optionGroup.setCreatorName(ContextUtils.getUserName());
			optionGroup.setSystemId(systemId);
			optionGroup.setCreatedTime(new Date());
		}
		option = optionGroupManager.saveOptionGroup(optionGroup, option);
		addActionMessage("<font class=\"onSuccess\"><nobr>保存成功</nobr></font>");
		ApiFactory.getBussinessLogService().log("选项组管理", "保存选项组",ContextUtils.getSystemId("bs"));
		return "option-group-input";
	}
	
	@Action("option-group-delete")
	public String delete() throws Exception {
			String result=optionGroupManager.deleteOptionGroups(ids);
		addActionMessage("<font class=\"onSuccess\"><nobr>"+result+"</nobr></font>");
		ApiFactory.getBussinessLogService().log("选项组管理", "删除选项组",ContextUtils.getSystemId("bs"));
		return null;
	}

	/**
	 * 删除选项
	 * @return
	 * @throws Exception
	 */
	public String deleteOption() throws Exception {
		optionGroupManager.deleteOption(optionId);
		return null;
	}
	
	/**
	 * 得到所有系统树
	 * @return
	 * @throws Exception
	 */
	@Action("system-tree")
	public String systemTree() throws Exception {
		StringBuilder tree = new StringBuilder("[ ");
		tree.append(JsTreeUtils.generateJsTreeNodeNew("all_system", "open", "所有系统",childSystem(),""));
		tree.append(" ]");
		renderText(tree.toString());
		return null;
	}
	
	public String childSystem(){
		StringBuilder tree = new StringBuilder("");
		businessSystems= businessSystemManager.getAllSystems();
		for(BusinessSystem system :businessSystems){
			tree.append(JsTreeUtils.generateJsTreeNodeNew(system.getId().toString(), "root", system.getName(),"")).append(",");
		}
		JsTreeUtils.removeLastComma(tree);
		return tree.toString();
	}

	@Override
	protected void prepareModel() throws Exception {
		if(optionGroupId == null){
			optionGroup = new OptionGroup();
		}else{
			optionGroup = optionGroupManager.getOptionGroup(optionGroupId);
		}
	}

	public OptionGroup getModel() {
		return this.optionGroup;
	}

	public Long getOptionGroupId() {
		return optionGroupId;
	}
	public void setOptionGroupId(Long optionGroupId) {
		this.optionGroupId = optionGroupId;
	}
	
	public Page<OptionGroup> getGroups() {
		return groups;
	}
	public void setGroups(Page<OptionGroup> groups) {
		this.groups = groups;
	}

	@Autowired
	public void setOptionGroupManager(OptionGroupManager optionGroupManager) {
		this.optionGroupManager = optionGroupManager;
	}

	
	public void setIds(String ids) {
		this.ids = ids;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<Option> getOption() {
		return option;
	}
	public void setOption(List<Option> option) {
		this.option = option;
	}

	public Long getOptionId() {
		return optionId;
	}
	public void setOptionId(Long optionId) {
		this.optionId = optionId;
	}

	public void setGroupNo(String groupNo) {
		this.groupNo = groupNo;
	}

	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}
}
