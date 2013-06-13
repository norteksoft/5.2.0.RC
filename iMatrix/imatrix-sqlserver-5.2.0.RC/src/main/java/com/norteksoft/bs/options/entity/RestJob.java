package com.norteksoft.bs.options.entity;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.norteksoft.product.api.entity.BusinessSystem;
import com.norteksoft.bs.options.enumeration.ApplyType;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.SystemUrls;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class RestJob implements Job{

	private static Log log = LogFactory.getLog(RestJob.class);
	private static final String MEDIA_TYPE = "text/html;charset=UTF-8";
	private Timer info;
	
	public RestJob() {
		super();
	}
	
	public RestJob(Timer info) {
		this();
		this.info = info;
	}

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		String url = "";
		//如果请求为绝对路径
		if(!StringUtils.isEmpty(info.getJobInfo().getUrl())&&info.getJobInfo().getUrl().startsWith("http")){
			url = info.getJobInfo().getUrl();
		}else{
			//根据系统编号获得系统
			BusinessSystem currentSystem = ApiFactory.getAcsService().getSystemByCode(info.getJobInfo().getSystemCode());
			if(!StringUtils.isEmpty(currentSystem.getParentCode())) {//表示该系统是子系统
				BusinessSystem parentSystem = ApiFactory.getAcsService().getSystemByCode(currentSystem.getParentCode());
				url = SystemUrls.getSystemUrl(parentSystem.getCode());
			}else{
				url = SystemUrls.getSystemUrl(info.getJobInfo().getSystemCode());
			}
			url = url + info.getJobInfo().getUrl();
		}
		//restful请求
		if(info.getJobInfo().getApplyType()==ApplyType.RESTFUL_APPLY){
			ClientConfig config = new DefaultClientConfig();
			Client client = Client.create(config);
			client.setReadTimeout(info.getJobInfo().getTimeout()*1000);
			WebResource service = client.resource(url);
			ClientResponse cr = service
			.entity("runAsUser="+info.getJobInfo().getRunAsUser(), MEDIA_TYPE)
			.accept(MEDIA_TYPE)
			.post(ClientResponse.class);
 			if(cr != null) log.info(" =========== job execute result : ["+cr.getEntity(String.class)+"] =========== ");
		}else{//http请求
			url = url+"?runAsUser="+info.getJobInfo().getRunAsUser();
			HttpGet httpget = new HttpGet(url);
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
	}

	public void setInfo(Timer info) {
		this.info = info;
	}

	public Timer getInfo() {
		return info;
	}

}
