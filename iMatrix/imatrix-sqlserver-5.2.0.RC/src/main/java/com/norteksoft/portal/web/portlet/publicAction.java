package com.norteksoft.portal.web.portlet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.Struts2Utils;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.portal.entity.BaseSetting;
import com.norteksoft.portal.entity.Countdown;
import com.norteksoft.portal.entity.Message;
import com.norteksoft.portal.entity.StickyNote;
import com.norteksoft.portal.entity.Widget;
import com.norteksoft.portal.service.BaseSettingManager;
import com.norteksoft.portal.service.MessageInfoManager;
import com.norteksoft.portal.service.PublicManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.MemCachedUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.web.struts2.CrudActionSupport;

@Namespace("/public")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "public?webpageId=${webpageId}", type = "redirectAction")})
public class publicAction extends CrudActionSupport<Widget> {
	private static final long serialVersionUID = 1L;
	
	private Page<User> page = new Page<User>(20, true);
	private PublicManager publicManager;
	private Widget widget;
	private String userName;
	private String userDepart;
	private String userSex;
	private String noteValue;
	private Date countdownDate;
	private String countdownName;
	private String countdownTime;
	private Long widgetId;  //小窗口ID
	private String skipWindwo;
	
	private Long companyId;
	private String username;//swing登录名
	private String auto;//swing是否自动登录
	private String password;//swing密码,md5加密后的
	
	@Autowired
	private AcsUtils acsUtils;
	
	@Autowired
	private MessageInfoManager messageManager;
	@Autowired
	private BaseSettingManager baseSettingManager;
	
	@Required
	public void setPublicManager(PublicManager publicManager) {
		this.publicManager = publicManager;
	}
	
	public Widget getModel() {
		return widget;
	}
	
	@Override
	public String list() throws Exception {
		return null;
	}
	
	@Override
	protected void prepareModel() throws Exception {
	}

	@Override
	public String save() throws Exception {
		return null;
	}
	
	@Override
	public String input() throws Exception {
		return null;
	}
	
	@Override
	public String delete() throws Exception {
		return null;
	}

	/**
	 * 生日提醒
	 */
	public String getBirthdayAwoke(){
		String birthdaysAwoke= publicManager.getBirthdayNotice(widgetId);
		renderText(birthdaysAwoke);
		return null;
	}
	
	/**
	 * 获取员工查询结果
	 * @return
	 * @throws Exception
	 */
	public String getQueryResult()throws Exception{
		publicManager.getQueryResult(userName,userDepart,userSex,page);
		return "queryResult";
	}
	
	/**
	 * 便签
	 * @return
	 * @throws Exception
	 */
	public String getStickyNoteVal() throws Exception{
		if(publicManager.getStickyNoteById()!=null && publicManager.getStickyNoteById().getContent() != null){
			noteValue = publicManager.getStickyNoteById().getContent().length()<1?"":publicManager.getStickyNoteById().getContent();
		}else{
			noteValue = "";
		}
		return "note";
	}
	
	/**
	 * 保存便签
	 * @return
	 * @throws Exception
	 */
	public String saveStickyNote()throws Exception{
		StickyNote stickyNote;
		if(publicManager.getStickyNoteById()!=null){
			stickyNote = publicManager.getStickyNoteById();
			stickyNote.setContent(noteValue);
		}else{
			stickyNote = new StickyNote();
			stickyNote.setCompanyId(ContextUtils.getCompanyId());
			stickyNote.setUserId(ContextUtils.getUserId());
			stickyNote.setContent(noteValue);
		}
		publicManager.saveStickyNote(stickyNote);
		ApiFactory.getBussinessLogService().log("portal管理", "保存便签", ContextUtils.getSystemId("portal"));
		return null;
	}
	
	/**
	 * Iframe跳转到倒计时牌
	 * @return
	 * @throws Exception
	 */
	public String toCountDown() throws Exception{
		countdownName = publicManager.getCountDown().getTitle();
		Long amountSecond = publicManager.getRemainTime();
		if(amountSecond<1){
			countdownTime = "0";
		}else{
			countdownTime = amountSecond.toString();
		}
		return "countDown";
	}
	
	/**
	 * 弹出创建倒计时牌
	 * @return
	 * @throws Exception
	 */
	public String openCountDownWindow() throws Exception{
		if(publicManager.getCountDown()!=null){
			countdownDate = publicManager.getCountDown().getTargetDate();
			countdownName = publicManager.getCountDown().getTitle();
		}
		return "countDownWindow";
	}
	
	/**
	 * 保存倒计时牌
	 * @return
	 * @throws Exception
	 */
	public String saveCountDown() throws Exception{
		Countdown countDown;
		if(publicManager.getCountDown()!=null){
			countDown = publicManager.getCountDown();
			countDown.setTargetDate(countdownDate);
			countDown.setTitle(countdownName);
		}else{
			countDown = new Countdown();
			countDown.setTargetDate(countdownDate);
			countDown.setTitle(countdownName);
			countDown.setCompanyId(ContextUtils.getCompanyId());
			countDown.setUserId(ContextUtils.getUserId());
			skipWindwo="notCountDown";
		}
		publicManager.saveCountDown(countDown);
		renderText("23");
		return null;
	}
	/**
	 * 取消倒计时牌设置
	 * @return
	 * @throws Exception
	 */
	public String cancelCountDown(){
		publicManager.cancelCountDown();
		renderText("23");
		return null;
	}
	
	@Action("public-auto")
	public String auto() throws Exception {
		String username=Struts2Utils.getRequest().getParameter("username");
		User user=acsUtils.getUserByLoginName(username);
		Long companyId=user.getCompanyId();
		StringBuilder bu= new StringBuilder();
		String imatrixUrl=SystemUrls.getSystemUrl("imatrix");
//		String imatrixUrl="http://192.168.1.97:8083/imatrix";
		String url=imatrixUrl+"/portal/public/public-info.htm";
		
		
		
		
		
		ThreadParameters parameters=new ThreadParameters(null,null);
		parameters.setCompanyId(companyId);
		parameters.setUserId(user.getId());
		parameters.setLoginName(user.getLoginName());
		parameters.setCompanyId(companyId);
		ParameterUtils.setParameters(parameters);
		List<com.norteksoft.product.api.entity.Menu> menus=ApiFactory.getMmsService().getTopMenus();
		StringBuilder menuBl = new StringBuilder();
		for (com.norteksoft.product.api.entity.Menu menu : menus) {
			String m=menu.getUrl();
			if(!PropUtils.isBasicSystem(m)){
				menuBl.append(m+",");
			}
		}
				
		bu.append("{ ");
			bu.append("\"username\":\""+username+"\",");
			bu.append("\"companyId\":\""+companyId+"\",");
			bu.append("\"refreshPeriod\":\""+getRrefeshPeriod(username, companyId)+"\",");
			bu.append("\"workRegister\":{").append(getWorkRegisterInfo(username, companyId)).append("},");
			bu.append("\"rootPath\":\""+StringUtils.removeEnd(menuBl.toString(),",")+"\",");
			bu.append("\"cas\":\""+PropUtils.getProp("host.sso")+"\",");
			bu.append("\"urls\":[\""+url+"\"]");
		bu.append(" }");
		this.renderText(bu.toString());
		return null;
	}
	
	private String getWorkRegisterInfo(String username, Long companyId){
		ThreadParameters tp = new ThreadParameters();
		tp.setCompanyId(companyId);
		ParameterUtils.setParameters(tp);
		
		// test
//		username="lianghedong";
//		companyId=2L;
//		String oaUrl = "http://192.168.1.59/oa";
		
		String oaUrl = SystemUrls.getSystemUrl("oa");
		String regUrl = oaUrl+"/work/register-check-swing.htm"+"?username="+username+"&companyId="+companyId;
		StringBuilder sb = new StringBuilder();
		try {
			String regInfo = getHttpClientConnection(regUrl);
			LOG.debug("=========== work register info =========="+regInfo);
			if(StringUtils.isNotEmpty(regInfo)){
				Map<String, String> info = JsonParser.json2Map(String.class, String.class, regInfo);
				sb.append("\"rootUrl\":\"").append(oaUrl).append("\"").append(",");
				sb.append("\"start\":\"").append(info.get("predictStartHour")).append("\"").append(",");
				sb.append("\"end\":\"").append(info.get("predictEndHour")).append("\"").append(",");
				sb.append("\"onWork\":\"").append(info.get("amStartHour")).append("\"").append(",");
				sb.append("\"offWork\":\"").append(info.get("pmEndHour")).append("\"");
			}
		} catch (Exception e) {
			LOG.error("Swing get work register info error. url["+regUrl+"]", e);
		}
		return sb.toString().replaceAll("null", "");
	}

	public String getHttpClientConnection(String url) throws Exception{
		HttpGet httpget = new HttpGet(url);
		HttpClient httpclient = new DefaultHttpClient();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		LOG.debug(" *** IP: [" + Struts2Utils.getRequest().getRemoteHost() + "] httpget URI : "+httpget.getURI());
		String responseBody = httpclient.execute(httpget, responseHandler);
		httpclient.getConnectionManager().shutdown();
	    return responseBody;//接收html
	}
	
	private Integer getRrefeshPeriod(String username, Long companyId){
		BaseSetting setting = baseSettingManager.getBaseSettingByLonginName(username, companyId);
		Integer refeshPeriod = 300;
		if(setting != null && setting.getRefreshTime() != null){
			refeshPeriod = setting.getRefreshTime();
		}
		return refeshPeriod;
	}
	
	/**
	 * swing任务信息
	 * @return
	 */
	@Action("public-info")
	public String taskInfo(){
		List<Message> messages=messageManager.getMessages(username,companyId,true);;
		if(messages!=null){
			List<Message> mess=new ArrayList<Message>();
			for (Message messages2 : messages) {
				String weapp=messages2.getUrl();
					if(StringUtils.isNotEmpty(messages2.getSystemCode())){
						Menu menu=(Menu) MemCachedUtils.get(messages2.getSystemCode());
						if(menu!=null){
							String url=menu.getUrl();
							if(StringUtils.contains(weapp, "?")){
								weapp=url+weapp+"&messageId="+messages2.getId();//先用用户名
							}else{
								weapp=url+weapp+"?messageId="+messages2.getId();
							}
						}
					}
				messages2.setUrl(weapp);
				mess.add(messages2);
			}
			
			this.renderText(JsonParser.object2Json(mess,"yyyy-MM-dd HH:mm")); 
			return null;
		}
		return null;
	}
	
	public Widget getWidget() {
		return widget;
	}

	public void setWidget(Widget widget) {
		this.widget = widget;
	}

	public Page<User> getPage() {
		return page;
	}

	public void setPage(Page<User> page) {
		this.page = page;
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

	public PublicManager getPublicManager() {
		return publicManager;
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

	public Long getWidgetId() {
		return widgetId;
	}

	public void setWidgetId(Long widgetId) {
		this.widgetId = widgetId;
	}

	public String getSkipWindwo() {
		return skipWindwo;
	}

	public void setSkipWindwo(String skipWindwo) {
		this.skipWindwo = skipWindwo;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAuto() {
		return auto;
	}

	public void setAuto(String auto) {
		this.auto = auto;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	
}
