package com.norteksoft.acs.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.context.SecurityContextImpl;

import com.norteksoft.acs.entity.sale.SubscriberItem;
import com.norteksoft.acs.entity.security.User;
import com.norteksoft.acs.service.sale.SubscriberItemManager;
import com.norteksoft.acs.web.listener.AcsHttpSessionListener;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.SystemUrls;

/**
 * 
 */
@SuppressWarnings("deprecation")
public class ExceededOnlineUserFilter implements Filter {

	private static Log log = LogFactory.getLog(ExceededOnlineUserFilter.class);
	
	public void destroy() { }

	public void doFilter(ServletRequest req, ServletResponse rep,
			FilterChain chan) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) rep;
		
		Long companyId = ContextUtils.getCompanyId();
		if(companyId == null){ // 用户没有登录时，由权限模块控制权限
			chan.doFilter(req, rep);
			return;
		}
		
		String url=request.getRequestURI();
		
		String sysCode = SystemUrls.getSysCodeFromUri(url);
		if(url.contains("/j_spring_security_logout")){
			removeConcurrencyStorage(request.getSession());
		}
		if(url.contains("exception-handle.action")||url.contains("update-password.action")){
			chan.doFilter(req, rep);
			return;
		}
		String username = ContextUtils.getLoginName();
		if(username == null || "roleAnonymous".equals(username)){
			chan.doFilter(req, rep);
		}else{
	        boolean isFirstAccess = ConcurrencyStorage.isFirstAccess(companyId, sysCode);
	        log.debug(username+" Access [companyId:"+companyId+", systemCode:"+sysCode+", isFirstAccess:"+isFirstAccess+"]");
	        boolean allowed = false;
	        if(isFirstAccess){ // 应用第一次访问时，判断订单 
	        	// 根据系统编号查询系统的订单项
	        	SubscriberItemManager manager = (SubscriberItemManager)ContextUtils.getBean("subscriberItemManager");
	        	List<SubscriberItem> items = manager.queryItems(companyId, sysCode);
	        	// 判断订单是否在有效期内
	        	Calendar cal = Calendar.getInstance();
	    		cal.add(Calendar.DAY_OF_YEAR, -1);
	    		Date current = cal.getTime();
	        	for(SubscriberItem item : items){
	        		log.debug(" Item [id:"+item.getId()+", EffectDate:"+item.getEffectDate()
	        				+", InvalidDate:"+item.getInvalidDate()+", Concurrency:"+item.getConcurrency()+"]");
	        		if(current.after(item.getEffectDate()) && current.before(item.getInvalidDate())){
	        			ConcurrencyStorage.putConcurrency(companyId+sysCode, item.getConcurrency());
	        			if(item.getConcurrency() > 0){
		        			ConcurrencyStorage.put(companyId+sysCode, username);
		        			allowed = true;
	        			}
	    			}
	        	}
	        }else{ // 判断并发数
	        	boolean containUser = ConcurrencyStorage.containsUser(companyId, sysCode, username);
	        	log.debug(" concurrency storage contains "+username+" "+containUser);
	        	if(containUser){
	        		allowed=true;
	        	} else{
	        		if(ConcurrencyStorage.allowe(companyId+sysCode)){
	        			allowed=true;
	        			ConcurrencyStorage.put(companyId+sysCode, username);
	        		}
	        	}
	        }
	        log.debug(" concurrency result "+allowed);
	        if(allowed){
	        	chan.doFilter(req, rep);
	        }else{
	        	// 产品已过期  或  超出并发人数限制
	        	String imatrixUrl=SystemUrls.getSystemUrl("imatrix");
	            String imatrixCode=imatrixUrl.substring(imatrixUrl.lastIndexOf("/")+1);
	            if(imatrixCode.contains(".")){//判断是否是80端口,是80端口时
	            	imatrixCode="";
	            }
	        	response.sendRedirect(imatrixCode+"/portal/exception-handle.action?type=403&exceed=true");
	        }
		}
	}

	public void init(FilterConfig config) throws ServletException {
		String code = config.getServletContext().getInitParameter("systemCode");
		if("imatrix".equals(code)){
			ConcurrencyStorage.getSysCodes().add("portal");
			ConcurrencyStorage.getSysCodes().add("acs");
			ConcurrencyStorage.getSysCodes().add("mms");
			ConcurrencyStorage.getSysCodes().add("bs");
			ConcurrencyStorage.getSysCodes().add("wf");
			ConcurrencyStorage.getSysCodes().add("task");
		}
		sessionInvalidateTimer();
	}
	
	private void sessionInvalidateTimer(){
		Timer timer = new Timer("session timer", true);
		// 定时验证session
		TimerTask task = new TimerTask(){
			public void run() {
				try {
					validateSessionTime();
				} catch (Exception e) {
					log.error(" validate session timeout error: ", e);
				}
			}};
		timer.schedule(task, 1*60000, 1*60000);
		// 定时移除并发信息
		TimerTask clearStorageTask = new TimerTask(){
			public void run() {
				try {
					log.debug(" clean storage ... ");
					ConcurrencyStorage.cleanStorage();
				} catch (Exception e) {
					log.error(" clean storage error: ", e);
				}
			}};
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.add(Calendar.DAY_OF_YEAR, 1);
		cal.setTimeInMillis(cal.getTimeInMillis());
		timer.schedule(clearStorageTask, cal.getTime(), 24*60*60000l);
	}
	
	private void validateSessionTime(){
		//ConcurrencyStorage.printStorage();
		SingleSignOutFilter.SessionStorage mapping = (SingleSignOutFilter.SessionStorage) SingleSignOutFilter.getSessionMappingStorage();
		//System.out.println("======================== "+mapping.values().size()+" ========================");
		Iterator<HttpSession> it = mapping.values().iterator();
		HttpSession session = null;
		while(it.hasNext()){
			session = it.next();
			boolean invalidation = true;
			try{
				invalidation = SessionFailFilter.isSessionFail(session);
				if(invalidation){
					removeConcurrencyStorage(session);
					// 记录用户退出日志
					AcsHttpSessionListener.recordLogout(session);
					//System.out.println("============  log out  ===============");
				}
			}catch(Exception e){
				log.error(e);
			}
		}
	}
	
	public static void removeConcurrencyStorage(HttpSession session){
		if(session == null) return;
		SecurityContextImpl context = (SecurityContextImpl)session.getAttribute("SPRING_SECURITY_CONTEXT");
		if(context != null){
			User user = (User)context.getAuthentication().getPrincipal();
			ConcurrencyStorage.remove(user.getCompanyId(), user.getUsername());
		}
	}
	
	public static class ConcurrencyStorage{
		private ConcurrencyStorage(){}
		// 存储公司各系统的并发人数
		private static Map<String, Integer> concurrencyStorage = new HashMap<String, Integer>();
		// 存储公司各系统的在线用户
		private static Map<String, Set<String>> storage = new HashMap<String, Set<String>>();
		private static List<String> sysCodes = new ArrayList<String>();
		
		static void cleanStorage(){
			concurrencyStorage = new HashMap<String, Integer>();
			storage = new HashMap<String, Set<String>>();
		}
		
		public static void putConcurrency(String key, Integer value){
			concurrencyStorage.put(key, value);
		}
		
		public static Integer getConcurrency(String key){
			return concurrencyStorage.get(key);
		}
		
		public static synchronized boolean isFirstAccess(Long companyId, String sysCode){
			return concurrencyStorage.get(companyId+sysCode) == null;
		}
		
		public static boolean containsUser(Long companyId, String sysCode, String loginName){
			return storage.get(companyId+sysCode) != null && storage.get(companyId+sysCode).contains(loginName);
		}
		
		public static synchronized void remove(Long companyId, String username){
			for(String code : sysCodes){
				if(storage.get(companyId+code) != null){
					log.debug(" remove user : [company: "+companyId+", code: "+code+", name: "+username+"]");
					storage.get(companyId+code).remove(username);
				}
			}
		}
		
		public static synchronized void put(String key, String value){
			if(storage.containsKey(key)){
				storage.get(key).add(value);
			}else{
				Set<String> set = new LinkedHashSet<String>();
				set.add(value);
				storage.put(key, set);
			}
		}
		
		public static synchronized boolean allowe(String key){
			return storage.get(key)!=null && (storage.get(key).size() < concurrencyStorage.get(key));
		}

		static void printStorage(){
			if(storage != null)
				for(Map.Entry<String,Set<String>> kv : storage.entrySet()){
					System.out.println("===================== "+kv.getKey()+" =====================");
					for(String name : kv.getValue()){
						System.out.println("== "+name+" ==");
					}
				}
		}
		
		public static List<String> getSysCodes() {
			return sysCodes;
		} 
	}
}
