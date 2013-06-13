package com.norteksoft.wf.engine.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.xwork.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.bs.rank.entity.Superior;
import com.norteksoft.bs.rank.service.RankManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.wf.base.enumeration.DataDictUseType;
import com.norteksoft.wf.engine.entity.DataDictionary;
import com.norteksoft.wf.engine.entity.DataDictionaryProcess;
import com.norteksoft.wf.engine.entity.DataDictionaryType;
import com.norteksoft.wf.engine.entity.DataDictionaryUser;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.service.DataDictionaryManager;
import com.norteksoft.wf.engine.service.DataDictionaryTypeManager;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;

@Namespace("/engine")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "data-dictionary", type = "redirectAction") })
public class DataDictionaryAction  extends CrudActionSupport<DataDictionary>{
	
	private Log log = LogFactory.getLog(DataDictionaryAction.class);
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	
	private static final long serialVersionUID = 1L;
	private Page<DataDictionary> dictPage = new Page<DataDictionary>(0, true);
	private DataDictionaryManager dataDictionaryManager;
	private DataDictionaryTypeManager dataDictionaryTypeManager;
	private WorkflowDefinitionManager workflowDefinitionManager;
	private RankManager rankManager;
	private DataDictionary dataDict;
	private String[] userNames;
	private String[] deptNames;
	private String[] groupNames;
	private String[] rankNames;
	private String[] processes;
	
	private String userNamesView;
	private Long id;
	private int operate;   //添加用户还是删除用户
	private static final int IS_ADD = 0;
	private static final int IS_REMOVE = 1;
	private List<String> operations;
	private List<DataDictionaryUser> dduUsers;
	private List<DataDictionaryUser> depts;
	private List<DataDictionaryUser> workGroups;
	private List<DataDictionaryUser> ranks;
	
	private List<DataDictionaryType> typeList;
	
	private List<DataDictionaryProcess> processTaches;
	private List<DataDictionaryProcess> processPros;
	private List<String[]> processesView;
	
	private String currentId;
	private String dictIds;
	
	private String queryTypeNo;
	private String queryTypeName;
	
	private DataDictUseType[] use;//前台用途需要的enum的集合
	
	private String showPage;
	private String searchValue;
	private String queryName;
	
	public DataDictUseType[] getUse() {
		return use;
	}

	@Override
	public String list() throws Exception {
		if(dictPage.getPageSize()>1){
			use = DataDictUseType.values();
			dataDictionaryManager.getDataDicts(dictPage,queryTypeNo,queryTypeName,queryName);
			ApiFactory.getBussinessLogService().log("数据字典", 
					"数据字典列表", 
					ContextUtils.getSystemId("wf"));
			this.renderText(PageUtils.pageToJson(dictPage));
			return null;
		}
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String input() throws Exception {
		log.debug("***input方法开始");
		typeList=dataDictionaryTypeManager.getAllDictTypes();
		log.debug("***typeList:"+typeList);
		if(id != null){
			Object[] lists=dataDictionaryManager.getDataDictUsers(id);
			dduUsers = ((ArrayList<List<DataDictionaryUser>>)(lists[0])).get(0);
			depts=((ArrayList<List<DataDictionaryUser>>)(lists[0])).get(1);
			workGroups=((ArrayList<List<DataDictionaryUser>>)(lists[0])).get(2);
			ranks=((ArrayList<List<DataDictionaryUser>>)(lists[0])).get(3);
			userNamesView=(String)lists[1];
			Object[] processLists=dataDictionaryManager.getDictProcessesByDictId(id);
			processPros=((ArrayList<List<DataDictionaryProcess>>)(processLists[0])).get(0);
			processTaches=((ArrayList<List<DataDictionaryProcess>>)(processLists[0])).get(1);
			processesView=(List<String[]>)processLists[1];
			log.debug("***dduUsers:"+dduUsers);
			log.debug("***depts:"+depts);
			log.debug("***workGroups:"+workGroups);
			log.debug("***userNamesView:"+userNamesView);
			log.debug("***processPros:"+processPros);
			log.debug("***processTaches:"+processTaches);
			log.debug("***processesView:"+processesView);
		}
		ApiFactory.getBussinessLogService().log("数据字典", 
				"数据字典表单页面", 
				ContextUtils.getSystemId("wf"));
		log.debug("***input方法结束");
		return "input";
	}

	@Override
	public String save() throws Exception {
		log.debug("***save方法开始");
		log.debug("***operations:"+operations);
		if(operations!=null){
			dataDict.setOperation(operations.toString().replace("[", "").replace("]","")+",");
			log.debug("***operation:"+dataDict.getOperation());
		}else{
			dataDict.setOperation(null);
		}
		dataDictionaryManager.saveDataDict(dataDict, userNames,deptNames,groupNames,processes,rankNames);
		id = dataDict.getId();
		ApiFactory.getBussinessLogService().log("数据字典", 
				"保存数据字典", 
				ContextUtils.getSystemId("wf"));
		this.addSuccessMessage("保存成功");
		log.debug("***save方法结束");
		return input();
	}
	private void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	@Override
	public String delete() throws Exception {
		log.debug("***delete方法开始");
		log.debug("***dictIds:"+dictIds);
		dataDictionaryManager.deleteDataDict(dictIds);
		ApiFactory.getBussinessLogService().log("数据字典", 
				"删除数据字典", 
				ContextUtils.getSystemId("wf"));
		log.debug("***delete方法结束");
		return list();
	}
	
	/**
	 * 添加用户
	 * @return
	 * @throws Exception
	 */
	public String addUsers() throws Exception {
		operate = IS_ADD;
		ApiFactory.getBussinessLogService().log("数据字典", 
				"添加用户页面", 
				ContextUtils.getSystemId("wf"));
		return "user";
	}
	
	/**
	 * 添加流程环节
	 * @return
	 * @throws Exception
	 */
	public String addProcesses() throws Exception {
		operate = IS_ADD;
		ApiFactory.getBussinessLogService().log("数据字典", 
				"添加流程环节页面", 
				ContextUtils.getSystemId("wf"));
		return "process";
	}
	
	/**
	 * 移除用户
	 * @return
	 * @throws Exception
	 */
	public String removeUsers() throws Exception{
		operate = IS_REMOVE;
		ApiFactory.getBussinessLogService().log("数据字典", 
				"移除用户页面", 
				ContextUtils.getSystemId("wf"));
		return "remove-user";
	}
	
	
	/**
	 * 保存用户关联
	 * @return
	 * @throws Exception
	 */
	public String saveDictUser() throws Exception{
		dataDictionaryManager.saveDictUsers(id, userNames);
		ApiFactory.getBussinessLogService().log("数据字典", 
				"保存用户关联", 
				ContextUtils.getSystemId("wf"));
		return "user";
	}
	
	/**
	 * 移除用户关联
	 * @return
	 * @throws Exception
	 */
	public String removeDictUser() throws Exception{
		dataDictionaryManager.deleteDictUsers(id, userNames);
		ApiFactory.getBussinessLogService().log("数据字典", 
				"移除用户关联", 
				ContextUtils.getSystemId("wf"));
		return removeUsers();
	}
	
	//流程树
	public String createProcessTree() throws Exception{
		log.debug("***createProcessTree方法开始");
		StringBuilder tree = new StringBuilder();
		String children=getProcessTree();
		tree.append("[");
		if(children.length()>0){
			tree.append(JsTreeUtils.generateJsTreeNodeNew("all_","open", "所有流程",children,"root")).append("");
		}else{
			tree.append(JsTreeUtils.generateJsTreeNodeNew("all_","", "所有流程","root")).append("");
		}
		tree.append("]");
		ApiFactory.getBussinessLogService().log("数据字典", 
				"显示流程树", 
				ContextUtils.getSystemId("wf"));
		renderText(tree.toString());
		log.debug(new StringBuilder("*** Result:[")
		.append("tree:").append(tree.toString())
		.toString());
		log.debug("***createProcessTree方法结束");
		return null;
	}
	
	private String getProcessTree(){
		StringBuilder tree = new StringBuilder();
		boolean isFirstNode = true;
		List<WorkflowDefinition> definitions = workflowDefinitionManager.getActiveDefinition();
		for (WorkflowDefinition workflowDefinition : definitions) {
			List<String> taches =workflowDefinitionManager.getTachesByProcessDefinition(workflowDefinition.getId());
			if(isFirstNode){
				tree.append(JsTreeUtils.generateJsTreeNodeNew("process_"+ workflowDefinition.getId()+";"+workflowDefinition.getName(),"closed", workflowDefinition.getName(),getTachesTree(taches, workflowDefinition.getId(), workflowDefinition.getName()),"process")).append(",");
				isFirstNode=false;
			}else{
				if(taches.size()>0){
					tree.append(JsTreeUtils.generateJsTreeNodeNew("process_"+workflowDefinition.getId()+";"+workflowDefinition.getName(),"closed", workflowDefinition.getName(),getTachesTree(taches, workflowDefinition.getId(), workflowDefinition.getName()),"process")).append(",");
				}else{
					tree.append(JsTreeUtils.generateJsTreeNodeNew("process_"+workflowDefinition.getId()+";"+workflowDefinition.getName(),"", workflowDefinition.getName(),getTachesTree(taches, workflowDefinition.getId(), workflowDefinition.getName()),"process")).append(",");
				}
			}
		}
		JsTreeUtils.removeLastComma(tree);
		return tree.toString();
	}
	
	private String getTachesTree(List<String> taches,Long processId,String processName){
		StringBuilder tree = new StringBuilder();
		for(String tache:taches){
			tree.append(JsTreeUtils.generateJsTreeNodeNew("tache_process_" + processId+";"+processName+"["+tache+"]", "", tache,"tache")).append(",");
		}
		JsTreeUtils.removeLastComma(tree);
		return tree.toString();
	}
	
	//上下级关系树
	public String createRankTree() throws Exception{
		log.debug("***createProcessTree方法开始");
		StringBuilder tree = new StringBuilder();
		tree.append("[");
		String rankTree=getRankTree();
		if(StringUtils.isNotEmpty(rankTree)){
			tree.append(JsTreeUtils.generateJsTreeNodeNew("all_rank_","closed", "上下级关系",rankTree,"root"));
		}else{
			tree.append(JsTreeUtils.generateJsTreeNodeNew("all_rank_","", "上下级关系","root"));
		}
		tree.append("]");
		ApiFactory.getBussinessLogService().log("数据字典", 
				"用户上下级关系树", 
				ContextUtils.getSystemId("wf"));
		renderText(tree.toString());
		log.debug(new StringBuilder("*** Result:[")
		.append("tree:").append(tree.toString())
		.toString());
		log.debug("***createProcessTree方法结束");
		return null;
	}
	
	private String getRankTree(){
		StringBuilder tree = new StringBuilder();
		List<Superior> dataRanks=rankManager.getDataDictRanks(searchValue);
		for (Superior  dataDictRank: dataRanks) {
			tree.append(JsTreeUtils.generateJsTreeNodeNew("rank_"+ dataDictRank.getId()+"-"+dataDictRank.getTitle(),"", dataDictRank.getTitle(),"","rank")).append(",");
		}
		JsTreeUtils.removeLastComma(tree);
		return tree.toString();
	}
	
	public void prepareDeleteProcess(){
		dataDict=dataDictionaryManager.getDataDict(id);
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if(id == null){
			dataDict = new DataDictionary();
		}else{
			dataDict = dataDictionaryManager.getDataDict(id);
		}
	}

	public DataDictionary getModel() {
		return dataDict;
	}
	
	@Required
	public void setDataDictionaryManager(DataDictionaryManager dataDictionaryManager) {
		this.dataDictionaryManager = dataDictionaryManager;
	}

	@Required
	public void setDataDictionaryTypeManager(
			DataDictionaryTypeManager dataDictionaryTypeManager) {
		this.dataDictionaryTypeManager = dataDictionaryTypeManager;
	}
	
	@Autowired
	public void setWorkflowDefinitionManager(
			WorkflowDefinitionManager workflowDefinitionManager) {
		this.workflowDefinitionManager = workflowDefinitionManager;
	}

	@Autowired
	public void setRankManager(RankManager rankManager) {
		this.rankManager = rankManager;
	}
	public Page<DataDictionary> getDictPage() {
		return dictPage;
	}

	public void setDictPage(Page<DataDictionary> dictPage) {
		this.dictPage = dictPage;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getOperate() {
		return operate;
	}

	public void setOperate(int operate) {
		this.operate = operate;
	}

	public void setUserNames(String[] userNames) {
		this.userNames = userNames;
	}

	public void setOperations(List<String> operations) {
		this.operations = operations;
	}

	public void setDduUsers(List<DataDictionaryUser> dduUsers) {
		this.dduUsers = dduUsers;
	}

	public List<DataDictionaryUser> getDduUsers() {
		return dduUsers;
	}

	public List<DataDictionaryUser> getDepts() {
		return depts;
	}

	public List<DataDictionaryUser> getWorkGroups() {
		return workGroups;
	}

	public void setDepts(List<DataDictionaryUser> depts) {
		this.depts = depts;
	}

	public void setWorkGroups(List<DataDictionaryUser> workGroups) {
		this.workGroups = workGroups;
	}

	public List<DataDictionaryType> getTypeList() {
		return typeList;
	}
	
	public String getCurrentId() {
		return currentId;
	}

	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}

	public void setProcesses(String[] processes) {
		this.processes = processes;
	}

	public void setDeptNames(String[] deptNames) {
		this.deptNames = deptNames;
	}

	public void setGroupNames(String[] groupNames) {
		this.groupNames = groupNames;
	}

	public String getUserNamesView() {
		return userNamesView;
	}

	public void setUserNamesView(String userNamesView) {
		this.userNamesView = userNamesView;
	}

	public List<DataDictionaryProcess> getProcessTaches() {
		return processTaches;
	}

	public List<DataDictionaryProcess> getProcessPros() {
		return processPros;
	}

	public List<String[]> getProcessesView() {
		return processesView;
	}

	public String getDictIds() {
		return dictIds;
	}

	public void setDictIds(String dictIds) {
		this.dictIds = dictIds;
	}

	public void setQueryTypeNo(String queryTypeNo) {
		this.queryTypeNo = queryTypeNo;
	}

	public void setQueryTypeName(String queryTypeName) {
		this.queryTypeName = queryTypeName;
	}

	public String getShowPage() {
		return showPage;
	}

	public void setShowPage(String showPage) {
		this.showPage = showPage;
	}

	public String[] getRankNames() {
		return rankNames;
	}

	public void setRankNames(String[] rankNames) {
		this.rankNames = rankNames;
	}

	public List<DataDictionaryUser> getRanks() {
		return ranks;
	}

	public void setRanks(List<DataDictionaryUser> ranks) {
		this.ranks = ranks;
	}
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
}
