package com.norteksoft.bs.rank.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.bs.rank.entity.Superior;
import com.norteksoft.bs.rank.entity.Subordinate;
import com.norteksoft.bs.rank.enumeration.SubordinateType;
import com.norteksoft.bs.rank.service.RankManager;
import com.norteksoft.bs.rank.service.RankUserManager;

@Namespace("/rank")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "list", type = "redirectAction") })
public class RankAction extends CRUDActionSupport<Superior> {
	private static final long serialVersionUID = 1L;
	private Page<Superior> page = new Page<Superior>(0, true);
	private RankManager rankManager;
	private RankUserManager rankUserManager;
	private Long id;
	private Superior dataDictionaryRank;
	private List<String> userInfos=new ArrayList<String>();//以分号隔开的人员列表信息:infoName;infoId;type;loginName(真名/部门名称/工作组;用户id/部门id/工作组id;类型(人员，部门，工作组);登录名)
	private String userNames="";//逗号隔开的人员名称
	private List<Subordinate> dataDictRankUsers;
	private String dictIds;
	@Required
	public void setRankManager(RankManager rankManager) {
		this.rankManager = rankManager;
	}
	@Required
	public void setRankUserManager(RankUserManager rankUserManager) {
		this.rankUserManager = rankUserManager;
	}

	@Action("input")
	public String input() throws Exception{
		if(id!=null){
			dataDictRankUsers=rankUserManager.getDataDictRankUsersByRank(id);
			userInfos.removeAll(userInfos);
			userNames="";
			for(Subordinate ddru:dataDictRankUsers){
				userNames+=ddru.getName()+",";
				userInfos.add(ddru.getName()+";"+(ddru.getTargetId()==null?"":ddru.getTargetId())+";"+(ddru.getSubordinateType()!=null?ddru.getSubordinateType().getIndex():SubordinateType.USER.getIndex())+";"+(ddru.getLoginName()==null?"":ddru.getLoginName()));
			}
			if(userNames.indexOf(",")>0){
				userNames=userNames.substring(0,userNames.lastIndexOf(","));
			}
		}
		return "input";
	}
	
	@Override
	@Action("delete")
	public String delete() throws Exception {
		rankManager.deleteDataDictRanks(dictIds);
		ApiFactory.getBussinessLogService().log("上下级关系管理", "删除上下级关系",ContextUtils.getSystemId("bs"));
		this.renderText("ok");
		return null;
	}
	
	@Action("list")
	public String list(){
		return SUCCESS;
	}
	
	@Action("list-data")
	public String listData() throws Exception {
		if(page.getPageSize()>1){
			rankManager.getDataDictRanksPage(page);
			renderText(PageUtils.pageToJson(page));
			ApiFactory.getBussinessLogService().log("上下级关系管理", "查看上下级关系列表",ContextUtils.getSystemId("bs"));
			return null;
		}
		return SUCCESS;
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			dataDictionaryRank = new Superior();
		}else{
			dataDictionaryRank = rankManager.getDataDictRankById(id);
		}
		
	}

	@Override
	@Action("save")
	public String save() throws Exception {
		rankManager.saveDataDictRank(dataDictionaryRank,userInfos);
		id = dataDictionaryRank.getId();
		addActionMessage("<font class=\"onSuccess\"><nobr>保存成功！</nobr></font>");
		ApiFactory.getBussinessLogService().log("上下级关系管理", "保存上下级关系",ContextUtils.getSystemId("bs"));
		return input();
	}
	@Action("user-tree")
	public String showUserTree() throws Exception {
		return SUCCESS;
	}
	@Action("dept-tree")
	public String showDeptTree() throws Exception {
		return SUCCESS;
	}
	@Action("group-tree")
	public String showGroupTree() throws Exception {
		return SUCCESS;
	}
	@Action("superior-user-tree")
	public String showSuperiorUserTree() throws Exception {
		return SUCCESS;
		
	}
	
	public Superior getModel() {
		return dataDictionaryRank;
	}

	public Page<Superior> getPage() {
		return page;
	}

	public void setPage(Page<Superior> page) {
		this.page = page;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<String> getUserInfos() {
		return userInfos;
	}

	public void setUserInfos(List<String> userInfos) {
		this.userInfos = userInfos;
	}

	public String getUserNames() {
		return userNames;
	}

	public void setUserNames(String userNames) {
		this.userNames = userNames;
	}

	public List<Subordinate> getDataDictRankUsers() {
		return dataDictRankUsers;
	}

	public String getDictIds() {
		return dictIds;
	}

	public void setDictIds(String dictIds) {
		this.dictIds = dictIds;
	}


}
