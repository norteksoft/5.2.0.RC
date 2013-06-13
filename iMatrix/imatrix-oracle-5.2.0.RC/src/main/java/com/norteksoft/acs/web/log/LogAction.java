package com.norteksoft.acs.web.log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.excelutils.utils.ExcelUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.base.web.struts2.Struts2Utils;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.log.Log;
import com.norteksoft.acs.entity.log.LoginLog;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.log.LogManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PageUtils;

@ParentPackage("default")
@Results( { @Result(name = "reload", location = "log", type="redirectAction") })
public class LogAction extends CRUDActionSupport<Log>{

	private static final long serialVersionUID = -6636275446940878497L;
	private LogManager logManager;
	private BusinessSystemManager businessSystemManager;
	private Page<Log> page = new Page<Log>(0,true);
	private Page<LoginLog> userLoginPage = new Page<LoginLog>(0,true);
	private Log entity;
	private Long id;
	private String name;
	private Map logMap;
	private Long businessSystemId;
	private Long searchsysId;
	private Long companyId;
	private Long sysId;
	private LoginLog loginUserLog;
	private String systemTree;
	private String loginLogIds;
	private String syIds;
	private String dsysId;
	private String deleteAll;
	private String deleteAllSysLog;
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
	public String delete() throws Exception {
		logManager.deleteLog(id);
		return RELOAD;
	}

	/**
	 *  查询所有日志
	 */
	@Override
	public String list() throws Exception {
		List<BusinessSystem> businessSystems = businessSystemManager.getAllBusiness();
		if(businessSystems.size() > 0){
			if(sysId == null){
				sysId = businessSystems.get(0).getId();
				if(dsysId!=null&&!dsysId.equals("")){
					sysId=Long.parseLong(dsysId);	
				}
			}
		}
		businessSystemId=sysId;
		return SUCCESS;
	}

	@Action("log-data")
	public String listData() throws Exception{
		if(page.getPageSize() > 1){
			page = logManager.getAllLog(page,sysId);
			ApiFactory.getBussinessLogService().log("系统日志管理", 
					"查看系统日志列表",ContextUtils.getSystemId("acs"));
			renderText(PageUtils.pageToJson(page));
			return null;
		}
		return "log-list";
	}
	
	/**
	 * 日志导出
	 */
	public String export() throws Exception{
		page.setAutoCount(false);
		page.setPageSize(65535);
		page = logManager.getAllLog(page,sysId);
		ExcelUtils.addValue("values", page.getResult());
		try {
			Struts2Utils.resetExportExcelProp("日志");
			String config = "/xls/log.xls";
			ExcelUtils.export(ServletActionContext.getServletContext(), config,
					Struts2Utils.getResponse().getOutputStream());
		} catch (Exception e) {
			LOG.error("导出错误", e);
			renderText("导出错误");
		}
		ApiFactory.getBussinessLogService().log("系统日志管理", 
				"导出系统日志",ContextUtils.getSystemId("acs"));
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id != null){
			entity = logManager.getLog(id);
		}else{
			entity = new Log();
		}
	}
	

	
	/**
	 * 删除系统日志
	 */
	
	public String deleteSysLoginLog() throws Exception {
		String s="";
		String result="";
		if("yes".equals(deleteAllSysLog)){
			s=syIds;
			result=logManager.deleteAllSysLog(syIds);
		}else{
			String ss=syIds.substring(0,syIds.indexOf("="));
			s=syIds.substring(syIds.indexOf("=")+1,syIds.length());
			result=logManager.deleteSysLogs(ss);
		}
		dsysId=s;
		if(page.getPageSize() > 1){
			page = logManager.getAllLog(page,sysId);
			renderText(PageUtils.pageToJson(page));
			return null;
		}
		ApiFactory.getBussinessLogService().log("系统日志管理", 
				"删除系统日志",ContextUtils.getSystemId("acs"));
		this.renderText(result);
		return null;
	}

	/**
	 * 保存方法,是不允许用户自己插入日志记录的
	 */
	@Override
	public String save() throws Exception {
		logManager.saveLog(entity);
		return RELOAD;
	}

	@Override
	public String input() throws Exception {
		return INPUT;
	}
	
	
	public void prepareSearch()throws Exception{
		prepareModel();
	}
	public String search(){
		page = logManager.getLogByCondition(page, entity,searchsysId);
		return SUCCESS;
	}
   
	public String lookLog() throws Exception{
		logMap = logManager.lookLog(id);
		return "history";
	}
	
	/**
	 * 查看登陆日志
	 * @return
	 */
	public String lookUserLoginLog()throws Exception{
		if(userLoginPage.getPageSize() >1){
			userLoginPage = logManager.getloginUserLogAllByCompanyId(userLoginPage, getCompanyId());
			this.renderText(PageUtils.pageToJson(userLoginPage));
			ApiFactory.getBussinessLogService().log("系统登陆管理", 
					"查看登陆日志",ContextUtils.getSystemId("acs"));
			return null;
		}
		return "user-login";
	}
	
	/**
	 * 导出登陆日志
	 */
	public String exportLoginLog() throws Exception{
		userLoginPage.setAutoCount(false);
		userLoginPage.setPageSize(65535);
		userLoginPage = logManager.getloginUserLogAllByCompanyId(userLoginPage, getCompanyId());
		ExcelUtils.addValue("values", userLoginPage.getResult());
		try {
			Struts2Utils.resetExportExcelProp("登陆日志");
			String config = "/xls/loginLog.xls";
			ExcelUtils.export(ServletActionContext.getServletContext(), config,
					Struts2Utils.getResponse().getOutputStream());
		} catch (Exception e) {
			LOG.error("导出错误", e);
			renderText("导出错误");
		}
		ApiFactory.getBussinessLogService().log("登陆日志管理", 
				"导出登陆日志",ContextUtils.getSystemId("acs"));
		return null;
	}
	
	
	/**
	 * 删除登陆日志
	 * @return
	 */
	public String deleteUserLoginLog()throws Exception{
		String result="";
		if("yes".equals(deleteAll)){
			result=logManager.deleteAllLoginUserLog();
		}else{
			result=logManager.deleteloginUserLogAllByCompanyId(loginLogIds, getCompanyId());	
		}
		ApiFactory.getBussinessLogService().log("登陆日志管理", 
				"删除登陆日志",ContextUtils.getSystemId("acs"));
		this.renderText(result);
		return null;
	}
	
	
	/**
	 * 搜索登陆日志
	 * @return
	 */
	public String searchUserLoginLog()throws Exception{
		userLoginPage = logManager.getListByLoginUserLog(userLoginPage, loginUserLog, getCompanyId());
		return "user-login";
	}
	
	/**
	 * 在线时长排行榜
	 * @return
	 */
	public String topkOnline()throws Exception{
		String comId=ServletActionContext.getRequest().getParameter("companyId");
		String rows=ServletActionContext.getRequest().getParameter("rows");
		rows=StringUtils.isEmpty(rows)?"20":rows;
		List<Object[]> userIdList=logManager.getTopkOnline(Long.valueOf(comId),Integer.parseInt(rows));
		StringBuffer html=new StringBuffer();
		List<Object[]> listArr=new ArrayList<Object[]>();
		for(Object[] userArr:userIdList){
			List<LoginLog> loginRecordList=logManager.getLoginRecordByUserId(Long.valueOf(userArr[0].toString()));
			long hour=0;
			long minute=0;
			long minutes=0;
			Date recentlyLoginDate=loginRecordList.get(0).getLoginTime();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String d=format.format(recentlyLoginDate);
			for(LoginLog login:loginRecordList){
				long[] hourMinute=getHourMinute(login);
				hour+=hourMinute[0];
				minute+=hourMinute[1];
				minutes=hour*60+minute;
			}
			hour=hour+minute/60;
			minute=minute%60;
			Object[] obj={minutes,userArr[1],hour,minute,d};
			listArr.add(obj);
//			html.append("<span><img src='../images/point.jpg'></s:if>"+userId[1]+"&nbsp;&nbsp;在线时长："+hour+"小时"+minute+"分&nbsp;&nbsp;最近登录时间："+recentlyLoginDate+"</span><br/>");
		}
		Collections.sort(listArr, new Comparator<Object[]>() {
			public int compare(Object[] o1, Object[] o2) {
				return ((Long)o2[0]).compareTo((Long)o1[0]);
			}
		});
		wrapTask(html,listArr);
		renderText(html.toString());
		return null;
	}
	
	private void wrapTask(StringBuffer html,List<Object[]> listArr){
		html.append("<table style=\"margin: 5px 0 5px 0;\" class=\"Table\">");
		html.append("<tr>");
		html.append("<th>名称</th>");
		html.append("<th>在线时长</th>");
		html.append("<th>最近登录时间</th>");
		html.append("</tr>");
		for(Object[] user:listArr){
			html.append("<tr>");
			html.append("<td>"+user[1]+"</td>");
			html.append("<td>"+user[2]+"小时"+user[3]+"分</td>");
			html.append("<td>"+user[4]+"</td>");
			html.append("</tr>");
			
		}
		html.append("</table>");
	}
	
	/**
	 * 获得登录日期与退出日期相差的小时和分钟
	 * @param login
	 * @return
	 * @throws Exception
	 */
	private long[] getHourMinute(LoginLog login)throws Exception{
		long hour=0;
		long minute=0;
		Date nowDate=new Date();
		Date beginDate=new Date(0);
		if(beginDate.compareTo(login.getExitTime())==0){
			hour=getHourMinute(login.getLoginTime(),nowDate)[0];
			minute=getHourMinute(login.getLoginTime(),nowDate)[1];
		}else{
			hour=getHourMinute(login.getLoginTime(),login.getExitTime())[0];
			minute=getHourMinute(login.getLoginTime(),login.getExitTime())[1];
		}
		long[] hourMinute={hour,minute};
		return hourMinute;
	}
	
	/**
	 * 获得两个日期相差的小时和分钟
	 * @param longinTime
	 * @param exitTime
	 * @return
	 * @throws Exception
	 */
	private long[] getHourMinute(Date longinTime,Date exitTime)throws Exception{
		Date longinDate=getYearMonthDay(longinTime);
		Date exitDate=getYearMonthDay(exitTime);
		int longinHour=getHour(longinTime);
		int longinMinute=getMinute(longinTime);
		int exitHour=getHour(exitTime);
		int exitMinute=getMinute(exitTime);
		long day=getDateMinus(longinDate,exitDate);
		long hour=0;
		long minute=0;
		if(day>0){
			if(longinHour>0 || longinMinute>0){
				day=day-1;
				int[] hourMinute=timeValue(24,0,longinHour,longinMinute);
				hour=hourMinute[0]+day*24+exitHour;
				minute=hourMinute[1]+exitMinute;
			}else if(longinHour==0 && longinMinute==0){
				hour=day*24+exitHour;
				minute=exitMinute;
			}
		}else if(day==0){
			int[] hourMinute=timeValue(exitHour,exitMinute,longinHour,longinMinute);
			hour=hourMinute[0];
			minute=hourMinute[1];
		}
		long[] hourMinute={hour,minute};
		return hourMinute;
	}
	
	/**
	 * 获得两个时间的差值
	 * @param firstHour减数（小时）
	 * @param firstMinute减数（分钟）
	 * @param secondHour被减数（小时）
	 * @param secondMinute被减数（分钟）
	 * @return
	 */
	private int[] timeValue(int firstHour,int firstMinute,int secondHour,int secondMinute){
		if(firstMinute<secondMinute){
			if(firstMinute==0){
				firstMinute=60;
				firstHour=firstHour-1;
			}else{
				firstMinute=firstMinute+60;
				firstHour=firstHour-1;
			}
		}
		int hour=firstHour-secondHour;
		int minute=firstMinute-secondMinute;
		int[] hourMinute={hour,minute};
		return hourMinute; 
	}
	
	/**
	 * 计算2个日期之间的天数
	 * @param beginTime
	 * @param endTime
	 * @return 相差的天数
	 */
	private long getDateMinus(Date beginTime,Date endTime)throws Exception{
			long time = (endTime.getTime()-beginTime.getTime())/1000/60/60/24;
			return time;
	}
	
	/**
	 * 获得小时
	 */
	private int getHour(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}
	
	/**
	 * 获得分钟
	 */
	private int getMinute(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MINUTE);
	}
	
	/**
	 * 获得年月日
	 */
	private Date getYearMonthDay(Date date){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}
	
	public Page<Log> getPage() {
		return page;
	}

	public void setPage(Page<Log> page) {
		this.page = page;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Required
	public void setLogManager(LogManager logManager) {
		this.logManager = logManager;
	}

	public BusinessSystemManager getBusinessSystemManager() {
		return businessSystemManager;
	}
	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}


	public Log getModel() {
		return entity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map getLogMap() {
		return logMap;
	}

	public void setLogMap(Map logMap) {
		this.logMap = logMap;
	}

	public Long getCompanyId() {
		if(companyId==null){
			companyId=ContextUtils.getCompanyId();
		}
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getSysId() {
		return sysId;
	}

	public void setSysId(Long sysId) {
		this.sysId = sysId;
	}

	public Page<LoginLog> getUserLoginPage() {
		return userLoginPage;
	}

	public void setUserLoginPage(Page<LoginLog> userLoginPage) {
		this.userLoginPage = userLoginPage;
	}

	public LoginLog getLoginUserLog() {
		return loginUserLog;
	}

	public void setLoginUserLog(LoginLog loginUserLog) {
		this.loginUserLog = loginUserLog;
	}

	public String getSystemTree() {
		return systemTree;
	}

	public void setSystemTree(String systemTree) {
		this.systemTree = systemTree;
	}

	public Long getBusinessSystemId() {
		return businessSystemId;
	}

	public void setBusinessSystemId(Long businessSystemId) {
		this.businessSystemId = businessSystemId;
	}

	public Long getSearchsysId() {
		return searchsysId;
	}

	public void setSearchsysId(Long searchsysId) {
		this.searchsysId = searchsysId;
	}

	public String getLoginLogIds() {
		return loginLogIds;
	}

	public void setLoginLogIds(String loginLogIds) {
		this.loginLogIds = loginLogIds;
	}

	public String getSyIds() {
		return syIds;
	}

	public void setSyIds(String syIds) {
		this.syIds = syIds;
	}

	public String getDsysId() {
		return dsysId;
	}

	public void setDsysId(String dsysId) {
		this.dsysId = dsysId;
	}

	public String getDeleteAll() {
		return deleteAll;
	}

	public void setDeleteAll(String deleteAll) {
		this.deleteAll = deleteAll;
	}

	public String getDeleteAllSysLog() {
		return deleteAllSysLog;
	}

	public void setDeleteAllSysLog(String deleteAllSysLog) {
		this.deleteAllSysLog = deleteAllSysLog;
	}
}
