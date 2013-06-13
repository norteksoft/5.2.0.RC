package com.norteksoft.portal.web.index;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.StandardRoleManager;
import com.norteksoft.product.api.entity.OptionGroup;
import com.norteksoft.portal.base.enumeration.StaticVariable;
import com.norteksoft.portal.entity.BaseSetting;
import com.norteksoft.portal.entity.Theme;
import com.norteksoft.portal.entity.Webpage;
import com.norteksoft.portal.entity.Widget;
import com.norteksoft.portal.entity.WidgetParameter;
import com.norteksoft.portal.entity.WidgetRole;
import com.norteksoft.portal.service.BaseSettingManager;
import com.norteksoft.portal.service.IndexManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.FtlUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.product.util.tree.TreeAttr;
import com.norteksoft.product.util.tree.TreeNode;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;


@Namespace("/index")
@ParentPackage("default")
@Results( {
	@Result(name = CrudActionSupport.RELOAD, location = "index?webpageId=${webpageId}", type = "redirectAction"),
	@Result(name = "toNewPage", location = "${url}", type = "redirectAction")
})
public class IndexAction extends CrudActionSupport<Widget>{
	private static final long serialVersionUID = 1L;
	
	private Page<User> page = new Page<User>(20, true);
	private Page<Widget> widgetPage=new Page<Widget>(0,true);
	private Page<Theme> themePage=new Page<Theme>(0,true);
	private Long webpageId;   //当前页签ID
	private Long widgetId;  //小窗口ID
	private String webpageName;//页签名称
	private int columnSize; //页签栏数
	private int position;  //窗体位置
	private String themeName;
	private Widget widget;
	private Webpage webpage;
	private List<Webpage> webPages ;
	private List<Widget> widgets;
	private List<Long> widgetIds;
	private String widgetIdStrs;
	private String htm;
	private String widgetCode;
	private String positions;
	private String wpId;
	private String userName;
	private String userDepart;
	private String userSex;
	private User user;
	private String userId;
	private String noteValue;
	private Date countdownDate;
	private String countdownName;
	private String countdownTime;
	private String skipWindwo;
	private String widgetPositions;
	private List<BusinessSystem> businessSystems;
	private Long systemId;
	private String registerWidgetIds;
	private String roleIds;
	private String roleNames;
	private Long parameterId;//窗口参数id
	private Integer pageNo;
	private String pageCode;
	private Theme theme;
	private Long id;//主题id
	private String code;//主题编号
	private String name;//主题名称
	private DataState dataState;//主题状态
	private String ids;//主题ids
	private BaseSetting baseSetting;//主题ids
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	
	@Autowired
	private IndexManager indexManager;
	@Autowired
	private BusinessSystemManager businessSystemManager;
	@Autowired
	private StandardRoleManager standardRoleManager;
	@Autowired
	private BaseSettingManager baseSettingManager;
	
	
	protected void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	/**
	 * 动态加载页签
	 */
	public String includeWebPage() throws Exception{
		webPages = indexManager.getWebpagesByUser();
		StringBuilder webpageStr = new StringBuilder();
		for(int i=0; i<webPages.size(); i++){
			webpageStr.append(webPages.get(i).getId());
			webpageStr.append("*-");
			webpageStr.append(webPages.get(i).getName());
			webpageStr.append("*-");
			if(i==webPages.size()-1){
				webpageStr.append(webPages.get(i).getUrl());
			}else{
				webpageStr.append(webPages.get(i).getUrl());
				webpageStr.append("-=");
			}
		}
		renderText(webpageStr.toString());
		return null;
	}
	
	/**
	 * 更换主题
	 */
	public String openTheme() throws Exception{
		return "theme";
	}
	
	/**
	 * 保存主题
	 */
	public String saveTheme() throws Exception{
		indexManager.saveTheme(themeName);
		ContextUtils.setTheme(themeName);
		if(StringUtils.isNotEmpty(url)){
			if(url.endsWith("#")) url = url.replace("#", "");
			if(!url.contains("_r=1")){
				if(url.contains("?")){
					url = url + "&_r=1";
				}else{
					url = url + "?_r=1";
				}
			}
		}
		Struts2Utils.getResponse().sendRedirect(url);
		ApiFactory.getBussinessLogService().log("portal管理", "更换主题", ContextUtils.getSystemId("portal"));
		return null;
	}
	
	/**
	 * 添加小窗体
	 */
	public String addWidget() throws Exception{
		widgets = indexManager.getAllWidgets(webpageId);
		webpage = indexManager.getWebpageById(webpageId);
		return "widget";
	}
	
	/**
	 * 添加页签
	 */
	public String addWebpage() throws Exception{
		if(webpageId != null)
			webpage = indexManager.getWebpageById(webpageId);
		return "webpage";
	}
	
	/**
	 * 保存页签
	 */
	public String savewebpage() throws Exception{
		webpage = indexManager.saveWebpage(webpageName, columnSize, webpageId);
		renderText(webpage.getId()+"");
		ApiFactory.getBussinessLogService().log("portal管理", "保存页签", ContextUtils.getSystemId("portal"));
		return null;
	}
	
	/**
	 * 删除页签
	 * @return
	 * @throws Exception
	 */
	public String deleteWebpage() throws Exception{
		indexManager.deleteWebpage(webpageId);
		webpageId = null;
		ApiFactory.getBussinessLogService().log("portal管理", "删除页签", ContextUtils.getSystemId("portal"));
		return RELOAD;
	}
	
	/**
	 * 保存参数设置
	 */
	@Override
	public String save() throws Exception {
		indexManager.saveParameterValues();
		renderText(widgetId+"");
		ApiFactory.getBussinessLogService().log("portal管理", "保存参数设置", ContextUtils.getSystemId("portal"));
		return null;
	}

	/**
	 * 进入参数设置
	 */
	public String parameterSet() throws Exception{
		List<WidgetParameter> widgetParameterList = indexManager.getParameters(widgetId);//执行所有参数的初始化
		if(widgetParameterList.size()>0){
			//初始化备选值
			indexManager.initializeWidgetParameter(widgetParameterList);
			String webPath=ServletActionContext.getServletContext().getRealPath("/");
			String webapp=ServletActionContext.getServletContext().getContextPath();
			String html=FileUtils.readFileToString(new File(webPath+"templet\\widget_parameter_set_templet.ftl"),"utf-8");
			Map<String, Object> root = new HashMap<String, Object>();
			root.put("widgetParameterList", widgetParameterList);
			root.put("ctx", webapp);
			root.put("widgetId", widgetId);
			root.put("webpageId", webpageId);
			root.put("userId", ContextUtils.getUserId());
			String result = FtlUtils.renderFile(root, html);
			renderText(result);
		}else{
			renderText("false");
		}
		return null;
	}
	
	/**
	 * 小窗体最大化
	 * @return
	 * @throws Exception
	 */
	public String max() throws Exception{
		return "max";
	}
	
	/**
	 * 得到小窗体的位置
	 * @return
	 * @throws Exception
	 */
	public String getWidgetPosition(){
		renderText(indexManager.getWidgetPosition(wpId));
		return null;
	}
	
	/**
	 * 关闭页面小窗体
	 */
	@Override
	public String delete() throws Exception {
		Webpage page = indexManager.getWebpageById(webpageId);
		page.setWidgetPosition(positions);
		indexManager.deleteWidget(page, widgetId);
		renderText("");
		return null;
	}

	/**
	 * 向页面添加小窗体
	 */
	public String saveWidgetToPortal() throws Exception{
		Webpage page = indexManager.getWebpageById(webpageId);
		//left=|center=|right=
		String[] posStrs = positions.split("\\|");
		String newPos = positions;
		if(posStrs.length==1){//1栏
			newPos = newPos+"|widget-place-center=|widget-place-right=";
		}else if(posStrs.length==2){//2栏
			newPos = posStrs[0]+"|widget-place-center=|"+posStrs[1];
		}
		page.setWidgetPosition(newPos);
		indexManager.addWidgets(page, widgetCode, position);
		return RELOAD;
	}
	
	/**
	 * 小窗体移动后，保存小窗体的位置
	 */
	public String savePositions() throws Exception{
		Webpage page = indexManager.getWebpageById(webpageId);
		String[] posStrs = positions.split("\\|");
		String newPos = positions;
		if(posStrs.length==1){//1栏
			newPos = newPos+"|widget-place-center=|widget-place-right=";
		}else if(posStrs.length==2){//2栏
			newPos = posStrs[0]+"|widget-place-center=|"+posStrs[1];
		}
		page.setWidgetPosition(newPos);
		indexManager.saveWebpage(page);
		indexManager.refreshWidgetPosition(webpageId);
		renderText("");
		return null;
	}
	
	
	
	/**
	 * 获取小窗口的HTML
	 */
	@Override
	public String input() throws Exception {
		if(webpageId == null){
			webPages = indexManager.getWebpagesByUser();
			webpageId = webPages.get(0).getId();
		}
		String html = "";
	   if(StringUtils.isNotEmpty(widgetCode)){
			widget = indexManager.getWidgetByCode(widgetCode);
			html = indexManager.getWidgetHtml(widget.getId(), webpageId,pageNo);
			html = html+StaticVariable.PAGE_SIGN+widget.getId();
		}else{
				if(pageNo==null) pageNo = 1;
				if(StringUtils.isEmpty(widgetIdStrs)){//当切换小窗体时会走判断内部
					widgetIdStrs = indexManager.getWidgetIdsByWebpage(webpageId);
				}
				html=indexManager.getWidgetHtml(widgetIdStrs, webpageId,pageNo);
		}
		renderText(html);
		return null;
	}
	
	/**
	 * 获取通知的HTML
	 * @return
	 * @throws Exception
	 */
	public String getActiveNoticeHtml() throws Exception{
		String html = indexManager.getActiveNoticeHtml();
		renderText(html);
		return null;
	}
	
	/**
	 * 显示默认页签所有窗体
	 */
	@Override
	public String list() throws Exception {
		baseSetting = baseSettingManager.getBaseSettingByLonginName();
		if(pageCode == null){
			webPages = indexManager.getWebpagesByUser();
			if(webpageId != null){
				webpage = indexManager.getCurrentWebpage(webpageId);
			}else{
				if(webPages.size()>0){
					webpage = indexManager.getCurrentWebpage(webPages.get(0).getId());
				}
			}
		}else{
			webpage = indexManager.getWebpageByCode(pageCode);
		}
		if(webpage!=null &&webpage.getColumns() == 2){
			return "two";
		}else if(webpage!=null &&webpage.getColumns() == 1){
			return "one";
		}else{
			return SUCCESS;
		}
	}

	/**
	 * 显示OA直通车
	 * @return
	 * @throws Exception
	 */
	public String showOADirectTrain() throws Exception{
		if(ContextUtils.getUserId()==null){
			htm = StaticVariable.NO_LOGIN;
		}else{
			Widget widget = indexManager.getWidgetByName(StaticVariable.OA_DIRECT_TRAIN);
			if(widget!=null){
				htm = indexManager.getWidgetHtml(widget.getId(), webpageId,pageNo);
			}
		}
		return "oa";
	}
	
	/**
	 * 显示个人事务
	 * @return
	 * @throws Exception
	 */
	public String showPersonalWork() throws Exception{
		if(ContextUtils.getUserId()==null){
			htm = StaticVariable.NO_LOGIN;
		}else{
			Widget widget = indexManager.getWidgetByName(StaticVariable.PERSONAL_WORK);
			if(widget!=null){
				htm = indexManager.getWidgetHtml(widget.getId(), webpageId,pageNo);
			}
		}
		return "oa";
	}
	
	/***********************注册小窗体*************************************
	/**
	 * 显示注册小窗体页面
	 * @return
	 * @throws Exception
	 */
	@Action("show-register-widget")
	public String showRegisterWidget() throws Exception{
		businessSystems= businessSystemManager.getAllSystems();
		if(businessSystems.size()>0)systemId=businessSystems.get(0).getId();
		return "index-register-widget";
	}
	
	/**
	 * 显示系统小窗体列表
	 * @return
	 * @throws Exception
	 */
	@Action("show-system-widget")
	public String showSystemWidget() throws Exception{
		if(systemId==null){
			businessSystems= businessSystemManager.getAllSystems();
			if(businessSystems.size()>0)systemId=businessSystems.get(0).getId();
		}
		if(widgetPage.getPageSize()>1){
			indexManager.getWidgetsBySystemCode(widgetPage,businessSystemManager.getBusiness(systemId).getCode());
			this.renderText(PageUtils.pageToJson(widgetPage));
			ApiFactory.getBussinessLogService().log("注册小窗体", "查看列表", ContextUtils.getSystemId("portal"));
			return null;
		}
		return "index-register-widget";
	}
	
	public void prepareRegisterWidgetInput(){
		if(widgetId==null){
			widget=new Widget();
		}else{
			widget=indexManager.getWdigetById(widgetId);
		}
		widget.setCompanyId(ContextUtils.getCompanyId());
	}
	
	/**
	 * 注册小窗体表单页面
	 * @return
	 * @throws Exception
	 */
	@Action("register-widget-input")
	public String registerWidgetInput() throws Exception{
		if(widgetId!=null){
			List<WidgetRole> wrs= indexManager.getWidgetRoleByWidgetId(widgetId);
			if(wrs!=null){
				for(WidgetRole wr:wrs){
					Role role=standardRoleManager.getStandardRole(wr.getRoleId());
					if(StringUtils.isEmpty(roleIds)){
						roleIds=wr.getRoleId()+"";
						roleNames=role.getName();
					}else{
						if(!roleIds.contains(wr.getRoleId()+"")){
							roleIds=roleIds+","+wr.getRoleId();
						}
						if(StringUtils.isEmpty(roleNames)){
							roleNames=role.getName();
						}else{
							roleNames=roleNames+","+role.getName();
						}
					}
				}
			}
		}
		return "register-widget-input";
	}
	
	@Action("validate-widget")
	public String validateWidget() throws Exception{
		boolean isExist=indexManager.isWidgetExist(widgetCode,widgetId);
		if(isExist){//存在
			this.renderText("true");
		}else{
			this.renderText("false");
		}
		return null;
	}
	
	public void prepareSaveWidget(){
		prepareRegisterWidgetInput();
		BusinessSystem system=businessSystemManager.getBusiness(systemId);
		widget.setSystemCode(system.getCode());
	}
	
	/**
	 * 保存注册的小窗体
	 * @return
	 * @throws Exception
	 */
	@Action("save-widget")
	public String saveWidget()  throws Exception{
		indexManager.saveWidget(widget,roleIds);
		widgetId=widget.getId();
		this.addSuccessMessage("保存成功");
		ApiFactory.getBussinessLogService().log("注册小窗体", "保存注册小窗体", ContextUtils.getSystemId("portal"));
		return registerWidgetInput();
	}
	
	/**
	 * 选择角色
	 * @return
	 * @throws Exception
	 */
	@Action("select-role")
	public String selectRoles() throws Exception{
		return "select-role";
	}
	
	/**
	 * 角色树
	 * @return
	 * @throws Exception
	 */
	@Action("role-tree")
	public String roleTree() throws Exception{
		String tree="";
		List<Role> roles=standardRoleManager.getAllStandardRole(systemId);
		if(roles.size()<=0){
			TreeNode root = new TreeNode(
					new TreeAttr("_role",""), 
					"", 
					"角色");
			tree=JsonParser.object2Json(root);
		}else{
			TreeNode root = new TreeNode(
					new TreeAttr("_role",""), 
					"open", 
					"角色");
			List<TreeNode> roleNodes = new ArrayList<TreeNode>();
			roleNodes= roles(roles);
			root.setChildren(roleNodes);
			tree=JsonParser.object2Json(root);
		}
		this.renderText(tree);
		return null;
	}
	
	private List<TreeNode> roles(List<Role> roles){
		List<TreeNode> roleNodes = new ArrayList<TreeNode>();
		for(Role role:roles){
			TreeNode roleNode = new TreeNode(
					new TreeAttr("role-"+role.getId()+"-"+role.getName(),""), 
					"", 
					role.getName());
			roleNodes.add(roleNode);
		}
		return roleNodes;
	}
	/**
	 * 选项组树
	 * @return
	 * @throws Exception
	 */
	@Action("option-group-tree")
	public String optionGroupTree() throws Exception{
		StringBuilder tree=new StringBuilder();
		
		List<OptionGroup> optionGroups = ApiFactory.getSettingService().getOptionGroups();
		if(optionGroups.size()<=0){
			tree.append(JsTreeUtils.generateJsTreeNodeNew("_option_group", "", "选项组", ""));
		}else{
			tree.append(JsTreeUtils.generateJsTreeNodeNew("_option_group", "open", "选项组",optionGroupNode(optionGroups) ,""));
		}
		this.renderText(tree.toString());
		return null;
	}
	
	private String optionGroupNode(List<OptionGroup> optionGroups){
		StringBuilder tree=new StringBuilder();
		for(OptionGroup optionGroup:optionGroups){
			tree.append(JsTreeUtils.generateJsTreeNodeNew("option-"+optionGroup.getId()+"-"+optionGroup.getName(), "",optionGroup.getName(),"")).append(",");
		}
		JsTreeUtils.removeLastComma(tree);
		return tree.toString();
	}
	/**
	 * 验证删除的小窗体是否被其他实体引用
	 * @return
	 * @throws Exception
	 */
	@Action("validate-delete-widget")
	public String validateDeleteWidget()  throws Exception{
		this.renderText(indexManager.validateDeleteWidget(registerWidgetIds));
		return null;
	}
	
	/**
	 * 删除小窗体
	 * @return
	 * @throws Exception
	 */
	@Action("delete-widget")
	public String deleteWidget()  throws Exception{
		indexManager.deleteWidget(registerWidgetIds);
		this.addSuccessMessage("删除成功");
		ApiFactory.getBussinessLogService().log("注册小窗体", "删除小窗体", ContextUtils.getSystemId("portal"));
		return "index-register-widget";
	}
	
	/**
	 * 删除窗口参数
	 * @return
	 * @throws Exception
	 */
	@Action("delete-parameter")
	public String deleteParameter() throws Exception{
		indexManager.deleteParameter(parameterId);
		String callback=Struts2Utils.getParameter("callback");
		this.renderText(callback+"({msg:'删除成功'})");
		return null;
	}
	
	/***********************添加主题*************************************/
	/**
	 * 显示添加主题页面
	 * @return
	 * @throws Exception
	 */
	@Action("add-theme")
	public String addTheme() throws Exception{
		
		if(themePage.getPageSize()>1){
			indexManager.getThemePage(themePage);
			this.renderText(PageUtils.pageToJson(themePage));
			return null;
		}
		return "index-add-theme";
	}
	
	public void prepareAddThemeSave() throws Exception {
		if(id==null){
			theme=new Theme();
		}else{
			theme=indexManager.getTheme(id);
		}
	}
	
	/**
	 * 保存主题
	 * @return
	 * @throws Exception
	 */
	@Action("add-theme-save")
	public String addThemeSave() throws Exception {
		theme.setCode(code);
		theme.setName(name);
		if(dataState==null){
			theme.setDataState(DataState.DRAFT);
		}else{
			theme.setDataState(dataState);
		}
		
		indexManager.saveTheme(theme);
		this.renderText(JsonParser.getRowValue(theme));
		return null;
	}
	
	/**
	 * 删除主题
	 * @return
	 * @throws Exception
	 */
	@Action("add-theme-delete")
	public String addThemeDelete() throws Exception {
		String deleteIds=Struts2Utils.getParameter("deleteIds");
		String[] ids=deleteIds.split(",");
		for(String deleteId:ids){
			indexManager.deleteTheme(Long.valueOf(deleteId));
		}
		return null;
	}
	
	/**
	 * 获得启用的主题
	 * @return
	 * @throws Exception
	 */
	@Action("start-using-theme")
	public String getStartUsingTheme() throws Exception {
		List<Theme> themes=indexManager.getStartUsingTheme();
		StringBuilder styles=new StringBuilder();
		String callback=Struts2Utils.getParameter("callback");
		for(Theme theme:themes){
			if(StringUtils.isNotEmpty(styles.toString())){
				styles.append(",");
			}
			styles.append(theme.getCode());
			styles.append(",");
			styles.append(theme.getName());
		}
		this.renderText(callback+"({msg:\""+styles.toString()+"\"})");
		return null;
	}
	
	/**
	 * 改变主题的状态
	 * @return
	 * @throws Exception
	 */
	@Action("change-theme-state")
	public String changeThemeState() throws Exception {
		this.renderText(indexManager.changeThemeState(ids));
		return null;
	}
	
	@Override
	public void prepareModel() throws Exception {
		
	}

	public Widget getModel() {
		return widget;
	}
	
	
	public List<Webpage> getWebPages() {
		return webPages;
	}
	public Long getWebpageId() {
		return webpageId;
	}
	public void setWebpageId(Long webpageId) {
		this.webpageId = webpageId;
	}
	public List<Widget> getWidgets() {
		return widgets;
	}
	public void setWebpageName(String webpageName) {
		this.webpageName = webpageName;
	}
	public void setColumnSize(int columnSize) {
		this.columnSize = columnSize;
	}
	public Webpage getWebpage() {
		return webpage;
	}
	public void setWebpage(Webpage webpage) {
		this.webpage = webpage;
	}
	public Long getWidgetId() {
		return widgetId;
	}
	public void setWidgetId(Long widgetId) {
		this.widgetId = widgetId;
	}
	public Widget getWidget() {
		return widget;
	}
	public void setWidget(Widget widget) {
		this.widget = widget;
	}
	public String getThemeName() {
		return themeName;
	}
	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}
	public List<Long> getWidgetIds() {
		return widgetIds;
	}
	public void setWidgetIds(List<Long> widgetIds) {
		this.widgetIds = widgetIds;
	}
	public String getHtm() {
		return htm;
	}
	public void setWidgetCode(String widgetCode) {
		this.widgetCode = widgetCode;
	}
	public String getPositions() {
		return positions;
	}
	public void setPositions(String positions) {
		this.positions = positions;
	}
	public String getWpId() {
		return wpId;
	}
	public void setWpId(String wpId) {
		this.wpId = wpId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserDepart() {
		return userDepart;
	}
	public void setUserDepart(String userDepart) {
		this.userDepart = userDepart;
	}
	public String getUserSex() {
		return userSex;
	}
	public void setUserSex(String userSex) {
		this.userSex = userSex;
	}
	public Page<User> getPage() {
		return page;
	}
	public void setPage(Page<User> page) {
		this.page = page;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getNoteValue() {
		return noteValue;
	}
	public void setNoteValue(String noteValue) {
		this.noteValue = noteValue;
	}
	public Date getCountdownDate() {
		return countdownDate;
	}
	public void setCountdownDate(Date countdownDate) {
		this.countdownDate = countdownDate;
	}
	public String getCountdownName() {
		return countdownName;
	}
	public void setCountdownName(String countdownName) {
		this.countdownName = countdownName;
	}
	public String getCountdownTime() {
		return countdownTime;
	}
	public void setCountdownTime(String countdownTime) {
		this.countdownTime = countdownTime;
	}
	public String getSkipWindwo() {
		return skipWindwo;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public void setSkipWindwo(String skipWindwo) {
		this.skipWindwo = skipWindwo;
	}
	public String getWidgetPositions() {
		return widgetPositions;
	}
	public void setWidgetPositions(String widgetPositions) {
		this.widgetPositions = widgetPositions;
	}
	private String url;
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrl() {
		return url;
	}
	public List<BusinessSystem> getBusinessSystems() {
		return businessSystems;
	}

	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}

	public Page<Widget> getWidgetPage() {
		return widgetPage;
	}

	public void setWidgetPage(Page<Widget> widgetPage) {
		this.widgetPage = widgetPage;
	}
	public void setRegisterWidgetIds(String registerWidgetIds) {
		this.registerWidgetIds = registerWidgetIds;
	}
	public void setRoleIds(String roleIds) {
		this.roleIds = roleIds;
	}
	public String getRoleIds() {
		return roleIds;
	}
	public String getRoleNames() {
		return roleNames;
	}
	public void setParameterId(Long parameterId) {
		this.parameterId = parameterId;
	}
	public Integer getPageNo() {
		return pageNo;
	}
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	public void setPageCode(String pageCode) {
		this.pageCode = pageCode;
	}
	public Page<Theme> getThemePage() {
		return themePage;
	}
	public void setThemePage(Page<Theme> themePage) {
		this.themePage = themePage;
	}
	public Theme getTheme() {
		return theme;
	}
	public void setTheme(Theme theme) {
		this.theme = theme;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public DataState getDataState() {
		return dataState;
	}
	public void setDataState(DataState dataState) {
		this.dataState = dataState;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public BaseSetting getBaseSetting() {
		return baseSetting;
	}
	public void setBaseSetting(BaseSetting baseSetting) {
		this.baseSetting = baseSetting;
	}
	public void setWidgetIdStrs(String widgetIdStrs) {
		this.widgetIdStrs = widgetIdStrs;
	}

}
