package com.norteksoft.portal.web.index;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

import org.hibernate.SessionFactory;


import com.norteksoft.portal.service.IndexManager;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;

public class WidgetThread implements Runnable,Serializable{
	private static final long serialVersionUID = 1L;
	
	
	private Long companyId;
	private Long userId;
	
	private String loginName;
	
	private String widgetIds;//多个小窗体id以逗号隔开
	private Long webpageId;
	private Integer pageNo;
	private final CountDownLatch doneSignal;
	IndexManager indexManager;
	SessionFactory sessionFactory;


	public WidgetThread(CountDownLatch doneSignal,Long companyId,Long userId,String loginName,String widgetIds,Long webpageId,Integer pageNo,IndexManager indexManager,SessionFactory sessionFactory) {
		this.doneSignal=doneSignal;
		this.companyId=companyId;
		this.userId=userId;
		this.loginName=loginName;
		this.widgetIds=widgetIds;
		this.webpageId=webpageId;
		this.pageNo=pageNo;
		this.indexManager=indexManager;
		this.sessionFactory=sessionFactory;
	}

	public void run() {
		ThreadParameters parameters=new ThreadParameters();
		parameters.setLoginName(loginName);
		parameters.setCompanyId(companyId);
		parameters.setUserId(userId);
		ParameterUtils.setParameters(parameters);
		try {
//			indexManager.getWidgetsHtml(widgetIds, webpageId, pageNo,sessionFactory);
			doneSignal.countDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public void setWidgetIds(String widgetIds) {
		this.widgetIds = widgetIds;
	}

	public void setWebpageId(Long webpageId) {
		this.webpageId = webpageId;
	}


	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
