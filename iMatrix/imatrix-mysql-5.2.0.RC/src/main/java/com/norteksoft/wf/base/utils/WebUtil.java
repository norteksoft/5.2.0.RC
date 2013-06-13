package com.norteksoft.wf.base.utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.wf.base.enumeration.CommonStrings;
import com.norteksoft.wf.base.enumeration.LogicOperator;
import com.norteksoft.wf.engine.core.DefinitionXmlParse;
import com.norteksoft.wf.engine.core.impl.UserParseCalculator;
import com.norteksoft.wf.engine.service.TaskService;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class WebUtil {
	private static Log log = LogFactory.getLog(WebUtil.class);
	private static final String MEDIA_TYPE = "text/html;charset=UTF-8";
	/**
	 * RESTful请求
	 * @param url
	 * @param companyId
	 */
	public static void restful(String url,Long companyId,Long entityId,String systemCode){
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		String resultUrl = SystemUrls.getSystemUrl(systemCode);
		if(PropUtils.isBasicSystem(resultUrl)){
			resultUrl = SystemUrls.getSystemUrl("imatrix");
		}
		log.info(" == system url : ["+resultUrl+"] == ");
		resultUrl = resultUrl + url;
		log.info(" == restlet url : ["+resultUrl+"] == ");
		WebResource service = client.resource(resultUrl);
		ClientResponse cr = service
		.entity("companyId="+companyId+"&entityId="+entityId, MEDIA_TYPE)
		.accept(MEDIA_TYPE)
		.post(ClientResponse.class);
		if(cr != null) log.info(" =========== RESTful execute result : ["+cr.getEntity(String.class)+"] =========== ");
	}
	/**
	 * 普通的http请求
	 * @param url
	 */
	public static void getHttpConnection(String url,Long companyId,Long entityId,String systemCode){
		String resultUrl=SystemUrls.getBusinessPath(systemCode);
		if(PropUtils.isBasicSystem(resultUrl)){
			resultUrl = SystemUrls.getSystemUrl("imatrix");
		}
		log.info(" == system url : ["+resultUrl+"] == ");
		resultUrl = resultUrl + url+"?companyId="+companyId+"&entityId="+entityId;
		log.info(" == restlet url : ["+resultUrl+"] == ");
		HttpGet httpget = new HttpGet(resultUrl);
		HttpClient httpclient = new DefaultHttpClient();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		try {
			httpclient.execute(httpget, responseHandler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		httpclient.getConnectionManager().shutdown();
	}
	
	public static String getSystemCodeByDef(String processId){
		Map<String, String> defBasicInfo=DefinitionXmlParse.getWorkFlowBaseInfo(processId);
		String systemCode=defBasicInfo.get(DefinitionXmlParse.SYSTEM_CODE);
		String code=ContextUtils.getSystemCode();
		if(StringUtils.isNotEmpty(systemCode)){
			code=systemCode;
		}
		return code;
	}
	
	/*
	 * 解析通知条件获得需要通知用户的邮件地址
	 */
	public static Set<String> getEmailsInformCondition(String userCondition,Long systemId,Long companyId,UserParseCalculator upc){
		Set<String> emails = new HashSet<String>();
		//获得通知用户的登录名
		Set<String> loginNames = getInformUserLoginNames(userCondition,systemId,companyId,upc);
		
		UserManager userManager = (UserManager)ContextUtils.getBean("userManager");
		List<User> users = userManager.getUsersByLoginNames(loginNames);
		for(User user:users){
			if(StringUtils.isEmpty(user.getEmail())){
				log.error(user.getName() + "邮件地址为空。" );
				continue;
			}
			emails.add(user.getEmail());
		}
		return emails;
	}
	
	private static Set<String> getInformUserLoginNames(String userCondition,Long systemId,Long companyId,UserParseCalculator upc){
		Set<String> loginNames = new HashSet<String>();
		if(StringUtils.isNotEmpty(userCondition)){
			String[] conditionArray = userCondition.split(LogicOperator.OR.getCode());
			for(String condition : conditionArray){
				if(CommonStrings.DOCUMENT_CREATOR.equals(condition.trim())){
					//文档创建人
					loginNames.add(upc.getDocumentCreator());
				}else if(CommonStrings.PROCESS_ADMIN.equals(condition.trim())){
					loginNames.add(upc.getProcessAdmin());
				}else if(CommonStrings.PARTICIPANTS_TRANSACTOR.equals(condition.trim())){
					loginNames.addAll(upc.getHandledTransactors());
				}else{
					loginNames.addAll(upc.getUsers(condition,systemId,companyId));
				}
			}
		}
		return loginNames;
	}
	
	/*
	 * 解析通知条件获得需要通知用户的登录名
	 */
	public static String getLoginNameInformCondition(String userCondition,Long systemId,Long companyId,UserParseCalculator upc){
		String result = "";
		//获得通知用户的登录名
		Set<String> loginNames = getInformUserLoginNames(userCondition,systemId,companyId,upc);
		
		UserManager userManager = (UserManager)ContextUtils.getBean("userManager");
		List<User> users = userManager.getUsersByLoginNames(loginNames);
		for(User user:users){
			if(StringUtils.isEmpty(user.getLoginName())){
				log.error(user.getName() + "登录名为空。" );
				continue;
			}
			result=result+user.getLoginName()+",";
		}
		result="".equals(result)?"":result.substring(0,result.length()-1);
		return result;
	}
	
	//获得用户解析需要的信息
	public static UserParseCalculator getUserParseInfor(String instanceId,String creator,String processAdmin){
		TaskService taskService = (TaskService) ContextUtils.getBean("taskService");
		List<String> userList = taskService.getParticipantsTransactor(instanceId);
		UserParseCalculator upc = new UserParseCalculator();
		upc.setDocumentCreator(creator);
		upc.setHandledTransactors(userList);
		upc.setProcessAdmin(processAdmin);
		return upc;
	}
}
