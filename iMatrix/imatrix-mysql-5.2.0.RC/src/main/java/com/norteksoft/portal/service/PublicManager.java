package com.norteksoft.portal.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.portal.base.enumeration.StaticVariable;
import com.norteksoft.portal.dao.CountDownDao;
import com.norteksoft.portal.dao.StickyNoteDao;
import com.norteksoft.portal.dao.WidgetParameterDao;
import com.norteksoft.portal.dao.WidgetParameterValueDao;
import com.norteksoft.portal.entity.Countdown;
import com.norteksoft.portal.entity.StickyNote;
import com.norteksoft.portal.entity.WidgetParameter;
import com.norteksoft.portal.entity.WidgetParameterValue;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.DateUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Service
@Transactional
public class PublicManager {
	@Autowired
	private CountDownDao countDownDao;
	
	@Autowired
	private StickyNoteDao stickyNoteDao;
	
	@Autowired
	private WidgetParameterDao widgetParameterDao;
	
	@Autowired
	private WidgetParameterValueDao widgetParameterValueDao;
	
	private Log log = LogFactory.getLog(getClass());
	
	/**
	 * 生日提醒
	 * @return
	 * @throws Exception
	 */
	public String getBirthdayNotice(Long widgetId){
		Integer beforeDays=StaticVariable.BIRTHDAY_DAY_NUBMBER;
		List<WidgetParameter> parameters=widgetParameterDao.getWidgetParameters(widgetId);
		for(WidgetParameter parameter:parameters){
			if("beforeDays".equals(parameter.getName())){
				List<WidgetParameterValue> parameterValues = widgetParameterValueDao.getWidgetParameterValuesByUserId(parameter.getId(), null);
				if(parameterValues.size()>0)beforeDays=Integer.valueOf(parameterValues.get(0).getValue());
			}
		}
		Map<Long,String> birthdays= null;//ApiFactory.getAcsService().getUserBirthdayByCompany(ContextUtils.getCompanyId());
		Iterator<Entry<Long, String>> it=birthdays.entrySet().iterator();
		StringBuffer html=new StringBuffer();
		StringBuffer htm=new StringBuffer();
		   Calendar calendarToday=getCalendarDate();
	       Calendar calendarYestoday=getCalendarDate();
	       calendarYestoday.add(Calendar.DATE, beforeDays);//提前7天提醒
	       SimpleDateFormat formatToday=new SimpleDateFormat("yyyy-MM-dd");
		   html.append("<div style=\"text-align: center;\"><span style=\" color:rgb(100000,0,500)\">今日生日("+formatToday.format(new Date())+"):</span><br>");
		while(it.hasNext()){
			Entry<Long, String> entry=it.next();
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			Date birthdayDate;
			try {
				birthdayDate = format.parse(entry.getValue());
				 Calendar calendar=Calendar.getInstance();
			       calendar.setTime(birthdayDate);
			       calendar.set(Calendar.YEAR, calendarToday.get(Calendar.YEAR));
			        if(calendar.getTime().equals(calendarToday.getTime())){
							html.append("<span style=\" color:rgb(100000,0,500)\"><img src=\" "+PropUtils.getProp("host.app")+"/images/birthdayCake.jpg \"></img>"+ApiFactory.getAcsService().getUserById(entry.getKey()).getName()+",生日快乐！</span><br>");
			        }
			        if(calendar.getTime().equals(calendarYestoday.getTime())||(calendar.getTime().before(calendarYestoday.getTime())&&calendar.getTime().after(calendarToday.getTime()))){
			        	htm.append("<span style=\" color:rgb(100000,0,500)\" >"+ApiFactory.getAcsService().getUserById(entry.getKey()).getName()+" 的生日快要到了！</span><br>");
			        }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return html.toString()+htm.toString();
	}
	
	private Calendar getCalendarDate(){
		Calendar calendarToday=Calendar.getInstance(); 
		   calendarToday.set(Calendar.HOUR_OF_DAY, 0);
	       calendarToday.set(Calendar.MINUTE, 0);
	       calendarToday.set(Calendar.SECOND, 0);
	       calendarToday.set(Calendar.MILLISECOND, 0);
	    return    calendarToday;
	}
	
	/**
	 * 员工查询
	 * @return
	 * @throws Exception
	 */
	public String searchUser(){
		StringBuffer html=new StringBuffer();
		html.append("<form name=\"queryForm\" id=\"queryForm\" method=\"post\" action=\"\">");
		html.append("<table style=\"margin: 5px 0 5px 0; border:0;\" class=\"Table\">");
		html.append("<tr style=\"border:0;\"><td style=\"width:25%;text-align: center ;border:0;\">姓名：</td><td style=\"border:0;\"><input maxlength=\"15\" name=\"userName\" id=\"userName\" style=\"width : 90%\"></input></td></tr>");
		html.append("<tr style=\"border:0;\" ><td style=\"width:25%;text-align: center;border:0;\">部门：</td><td style=\"border:0;\"><input maxlength=\"15\" name=\"userDepart\" id=\"userDepart\" style=\"width : 90%\"></input></td></tr>");
		html.append("<tr style=\"border:0;\"><td style=\"width:25%; text-align: center;border:0;\">性别：</td><td style=\"border:0;\"><select id=\"userSex\"><option ></option><option value=\"1\">男</option><option value=\"0\">女</option></select></td></tr>");
		html.append("<tr  style=\"border:0;\"><td style=\"border:0;\" ></td><td style=\"text-align: right;padding-right: : 30px;border:0;\"><p class=\"buttonP\"><a class=\"btnStyle\" href=\"#\" onclick=\"doQuery();\">查询</a></p></td></tr>");
		html.append("</table>");
		html.append("</form>");
		return html.toString();
	}
	
	/**
	 * 获取员工查询结果
	 * @return
	 * @throws Exception
	 */
	public void getQueryResult(String userName ,String userDepart, String userSex,Page<User> page){
//		if("0".equals(userSex)){
//			ApiFactory.getAcsService().userSearch(userName,userDepart,false,ContextUtils.getCompanyId(),page);
//		}else if("1".equals(userSex)){
//			ApiFactory.getAcsService().userSearch(userName,userDepart,true,ContextUtils.getCompanyId(),page);
//		}else{
//			ApiFactory.getAcsService().userSearchAllSex(userName,userDepart,ContextUtils.getCompanyId(),page);
//		}
	}
	
	/**
	 * 便签
	 * @return
	 */
	public String getStickyNote(){
		StringBuffer html=new StringBuffer();
		html.append("<iframe style=\"width:100%;height:210px;border:0px solid;overflow:hidden;\" src=\""+ServletActionContext.getServletContext().getContextPath()+"/public/public!getStickyNoteVal.htm\">");
		html.append("</iframe>");
		return html.toString();
	}
	
	/**
	 * 保存便签
	 * @return
	 */
	public void saveStickyNote(StickyNote stickyNote){
		stickyNoteDao.save(stickyNote);
	}
	
	/**
	 * 得到便签
	 * @return
	 */
	public StickyNote getStickyNoteById(){
		return stickyNoteDao.getStickyNoteById(ContextUtils.getUserId(), ContextUtils.getCompanyId());
	}
	
	/**
	 * 倒计时HTML
	 * @return
	 */
	public String getCountDownHTML(){
		Countdown countDown = countDownDao.getCountDownByUserIdAndCompanyId(ContextUtils.getUserId(), ContextUtils.getCompanyId());
		String html="";
		if(countDown!=null){
			html = haveCountDown();
		}else{
			html = notCountDown();
		}
		return html;
	}
	
	public String notCountDown(){
		StringBuffer html = new StringBuffer();
		html.append("<div style=\"overflow:auto; text-align:center;\">");
		html.append("<span>您还没有创建倒计时牌!</span><br/><br/>");
		html.append("<a href=\"#\" onclick=\"openCountDown('notCountDown');\" style=\"color:red;\">创建</a>");
		html.append("</div>");
		return html.toString();
	}
	
	public String haveCountDown(){
		StringBuffer html = new StringBuffer();
		html.append("<iframe style=\"width:100%;height:180px;border:0px solid;overflow:hidden;\" src=\""+ServletActionContext.getServletContext().getContextPath()+"/public/public!toCountDown.htm\">");
		html.append("</iframe>");
		return html.toString();
	}
	
	/**
	 * 得到倒计时实体
	 * @return
	 */
	public Countdown getCountDown(){
		return countDownDao.getCountDownByUserIdAndCompanyId(ContextUtils.getUserId(), ContextUtils.getCompanyId());
	}
	
	public void saveCountDown(Countdown countDown){
		countDownDao.save(countDown);
	}
	
	/**
	 * 取消倒计时牌设置
	 * @return
	 */
	public void cancelCountDown(){
		Countdown countDown=getCountDown();
		countDownDao.delete(countDown);
	}
	
	/**
	 * 得到剩余的时间
	 * @return
	 */
	public Long getRemainTime() throws Exception{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		Long hour = (long)calendar.get(Calendar.HOUR_OF_DAY);
		Long minute = (long)calendar.get(Calendar.MINUTE);
		Long second = (long)calendar.get(Calendar.SECOND);
		Long pastHour = ((24-hour-1)*60)*60;
		Long pastMinute = (60-minute)*60;
		Long pastSecont = (60-second);
		Long amountDay = DateUtils.getDateMinus(new Date(), getCountDown().getTargetDate());
		return ((((amountDay)*24)*60)*60)+(pastHour+pastMinute+pastSecont);
	}
	
	/**
	 * 获得超时工作任务
	 * @return
	 */
	public String getOvertimeWorkTaskRemind(Long widgetId)throws Exception{
		List<String> paramNames = new ArrayList<String>();
		List<String> paramValues = new ArrayList<String>();
		paramNames.add(StaticVariable.COMPANY_ID);
		paramNames.add(StaticVariable.USER_ID);
		paramValues.add(ContextUtils.getCompanyId().toString());
		paramValues.add(ContextUtils.getUserId().toString());
		String taskPath=PropUtils.getProp("task.urge.url");
		String bookPath=PropUtils.getProp("book.urge.url");
		String workReportPath=PropUtils.getProp("workReport.urge.url");
		String[] taskCodeAndUrl=getProductCodeAndUrl(taskPath);
		String[] bookCodeAndUrl=getProductCodeAndUrl(bookPath);
		String[] workReportCodeAndUrl=getProductCodeAndUrl(workReportPath);
		String task=getHttpClientConnection(taskCodeAndUrl[0],taskCodeAndUrl[1],paramNames,paramValues);
		String book=getHttpClientConnection(bookCodeAndUrl[0],bookCodeAndUrl[1],paramNames,paramValues);
		String workReport=getHttpClientConnection(workReportCodeAndUrl[0],workReportCodeAndUrl[1],paramNames,paramValues);
		List<Map<String,String>> taskList=processJSONString(task,"productCode","url","entityId","endDate","overtimeDay","initiateName","taskName");
		List<Map<String,String>> bookList=processJSONString(book,"productCode","url","entityId","endDate","overtimeDay","initiateName","taskName");
		List<Map<String,String>> workReportList=processJSONString(workReport,"productCode","url","entityId","endDate","overtimeDay","initiateName","taskName");
		StringBuffer html=new StringBuffer();
		html.append("<table style=\"margin: 5px 0 5px 0;\" class=\"Table\">");
		html.append("<tr>");
		html.append("<th>任务名称</th>");
		html.append("<th>发起人</th>");
		html.append("<th>超时天数</th>");
		html.append("</tr>");
		
		int temp=0;
		int rows=0;
		List<WidgetParameter> parameters=widgetParameterDao.getWidgetParameters(widgetId);
		for(WidgetParameter parameter:parameters){
			if("rows".equals(parameter.getName())){
				List<WidgetParameterValue> parameterValues = widgetParameterValueDao.getWidgetParameterValuesByUserId(parameter.getId(), null);
				if(parameterValues.size()>0)rows=Integer.parseInt(parameterValues.get(0).getValue());
			}
		}
		for(Map<String,String> map:taskList){
			temp++;
			if(rows>0&&temp>rows){
				break;
			}else{
				String productUrl = getSystemUrl(map.get("productCode"));
				String address=productUrl+map.get("url")+map.get("entityId");
				wrapTask(html,map,address);
			}
		}
		for(Map<String,String> map:bookList){
			temp++;
			if(rows>0&&temp>rows){
				break;
			}else{
				wrapTask(html,map,"");
			}
		}
		for(Map<String,String> map:workReportList){
			temp++;
			if(rows>0&&temp>rows){
				break;
			}else{
				String productUrl = getSystemUrl(map.get("productCode"));
				String address=productUrl+map.get("url")+map.get("entityId");
				wrapTask(html,map,address);
			}
		}
		html.append("</table>");
		return html.toString();
	}
	
	private void wrapTask(StringBuffer html,Map<String,String> map,String address){
		html.append("<tr>");
		if(StringUtils.isNotEmpty(address)){
			html.append("<td><a href=\"#\" onclick=\"window.open('"+address+"','','');\">"+map.get("taskName")+"</a></td>");
		}else{
			html.append("<td>"+map.get("taskName")+"</td>");
		}
		html.append("<td>"+map.get("initiateName")+"</td>");
		html.append("<td>"+map.get("overtimeDay")+"</td>");
		html.append("</tr>");
	}
	
	private String[] getProductCodeAndUrl(String str)throws Exception{
		String code=str.substring(0, str.indexOf("/"));
		String url=str.substring(str.indexOf("/")+1, str.length());
		String[] productCodeAndUrl={code,url};
		return productCodeAndUrl;
	}
	
	/**
	 * httpClient连接方式，获取数据
	 * @param productCode
	 * @param url
	 * @param methodName
	 * @param parameterNames
	 * @param parameterValues
	 * @return
	 * @throws Exception
	 */
	private String getHttpClientConnection(String productCode,String url,
			List<String> paramNames,List<String> paramValues) throws Exception{
		StringBuilder sb = new StringBuilder(url);
		if(paramNames!=null)
			for(int i=0;i<paramNames.size();i++){
				if(i == 0) sb.append("?");
				else sb.append(StaticVariable.SYMBOL_AND);
				sb.append(paramNames.get(i));
				sb.append(StaticVariable.SYMBOL_EQUAL);
				sb.append(paramValues.get(i));
			}
		String productUrl = getSystemUrl(productCode);
		HttpGet httpget = new HttpGet(productUrl+sb.toString());
		HttpClient httpclient = new DefaultHttpClient();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		log.info(" *** IP: [" + Struts2Utils.getRequest().getRemoteHost() + "] httpget URI : "+httpget.getURI());
		String responseBody = httpclient.execute(httpget, responseHandler);
		httpclient.getConnectionManager().shutdown();
	    return responseBody;//接收html
	}
	
	private String getSystemUrl(String key) throws Exception{
		String url = SystemUrls.getSystemUrl(key);
		url += StaticVariable.SYMBOL_SLASH;
		return url;
	}
	
	private List<Map<String,String>> processJSONString(String json, String... propNames) {
		MapType mt = TypeFactory.defaultInstance().constructMapType(
				HashMap.class, String.class, String.class);
		CollectionType ct = TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, mt);
		return JsonParser.json2Object(ct, json);
	}
}
