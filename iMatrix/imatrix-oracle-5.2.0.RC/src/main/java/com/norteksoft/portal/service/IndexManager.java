package com.norteksoft.portal.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.service.authorization.AcsApiManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.bs.options.service.OptionGroupManager;
import com.norteksoft.mms.base.data.DataHandle;
import com.norteksoft.mms.base.data.DataSheetConfig;
import com.norteksoft.mms.base.data.DataTransfer;
import com.norteksoft.mms.base.data.FileConfigModel;
import com.norteksoft.portal.base.enumeration.ControlType;
import com.norteksoft.portal.base.enumeration.StaticVariable;
import com.norteksoft.portal.dao.ThemeDao;
import com.norteksoft.portal.dao.UserThemeDao;
import com.norteksoft.portal.dao.WebpageDao;
import com.norteksoft.portal.dao.WidgetConfigDao;
import com.norteksoft.portal.dao.WidgetDao;
import com.norteksoft.portal.dao.WidgetParameterDao;
import com.norteksoft.portal.dao.WidgetParameterValueDao;
import com.norteksoft.portal.dao.WidgetRoleDao;
import com.norteksoft.portal.entity.Theme;
import com.norteksoft.portal.entity.UserTheme;
import com.norteksoft.portal.entity.Webpage;
import com.norteksoft.portal.entity.Widget;
import com.norteksoft.portal.entity.WidgetConfig;
import com.norteksoft.portal.entity.WidgetParameter;
import com.norteksoft.portal.entity.WidgetParameterValue;
import com.norteksoft.portal.entity.WidgetRole;
import com.norteksoft.portal.web.index.WidgetThread;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Role;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.util.ThreadPool;
import com.norteksoft.product.util.freemarker.TagUtil;


@SuppressWarnings("deprecation")
@Service
@Transactional
public class IndexManager implements DataTransfer {
	@Autowired
    private WebpageDao webPageDao;
	
	@Autowired
    private WidgetConfigDao widgetConfigDao;
	
	@Autowired
    private WidgetDao widgetDao;
	
	@Autowired
    private WidgetParameterDao widgetParameterDao;
	
	@Autowired
    private WidgetParameterValueDao widgetParameterValueDao;
	
	@Autowired
    private UserThemeDao userThemeDao;
    
    @Autowired
    private AcsApiManager acsApiManager;
    
    @Autowired
    private WidgetRoleDao widgetRoleDao;
    
    @Autowired
    private ThemeDao themeDao;
    
    @Autowired
    private OptionGroupManager optionGroupManager;
    
    @Autowired
    private DataHandle dataHandle;
    
    @Autowired
    private CompanyManager companyManager;
    
    private Log log = LogFactory.getLog(getClass());
    public static StringBuilder resultHtml;
    
	@Transactional
	public Widget getWdigetById(Long id){
		return widgetDao.get(id);
	}
	
	@Transactional
	public Webpage getWebpageById(Long id){
		Webpage webpage = webPageDao.get(id);
		return webpage;
	}
	
	public Webpage getWebpageByCode(String code){
		Webpage webpage = webPageDao.getWebpageByCode(code);
		List<WidgetConfig> widgetConfigs = widgetConfigDao.getCustomerWidgetConfigs(webpage.getId());
		List<Widget> leftWidgets = new ArrayList<Widget>();
		List<Widget> centerWidgets = new ArrayList<Widget>();
		List<Widget> rightWidgets = new ArrayList<Widget>();
		List<Widget> widgetList = getWidgetsByBuyerAndAuthority();
		for(WidgetConfig config:widgetConfigs){
			Widget widget = widgetDao.get(config.getWidgetId());
				widget.setParameters(getParametersByWidget(widget.getId()));
				widget.setSystemUrl(SystemUrls.getSystemUrl(widget.getSystemCode()));
				switch (config.getPosition()) {
				case 0:
					if(widgetList.contains(widget)){
					leftWidgets.add(widget);
					}
					break;
				case 1:
					if(widgetList.contains(widget)){
					centerWidgets.add(widget);
					}
					break;
				case 2:
					if(widgetList.contains(widget)){
					rightWidgets.add(widget);
					}
					break;
				}
		}
		webpage.setLeftWidgets(leftWidgets);
		webpage.setCenterWidgets(centerWidgets);
		webpage.setRightWidgets(rightWidgets);
		
		return webpage;
	}
	
	public String getWidgetIdsByWebpage(Long webpageId){
		Webpage webpage = getWebpageById(webpageId);
		List<WidgetConfig> widgetConfigs = widgetConfigDao.getCustomerWidgetConfigs(webpage.getId());
		StringBuilder widgetIds = new StringBuilder();
		List<Widget> widgetList = getWidgetsByBuyerAndAuthority();
		for(WidgetConfig config:widgetConfigs){
			Widget widget = widgetDao.get(config.getWidgetId());
				widget.setParameters(getParametersByWidget(widget.getId()));
				widget.setSystemUrl(SystemUrls.getSystemUrl(widget.getSystemCode()));
				switch (config.getPosition()) {
				case 0:
					if(widgetList.contains(widget)){
						widgetIds.append(widget.getId()).append(",");
					}
					break;
				case 1:
					if(widgetList.contains(widget)){
						widgetIds.append(widget.getId()).append(",");
					}
					break;
				case 2:
					if(widgetList.contains(widget)){
						widgetIds.append(widget.getId()).append(",");
					}
					break;
				}
		}
		if(widgetIds.length()>0&&widgetIds.charAt(widgetIds.length()-1)==',')widgetIds.deleteCharAt(widgetIds.length()-1);
		return widgetIds.toString();
	}
	
	/**
	 * 删除页签
	 * @param id
	 */
	public void deleteWebpage(Long id){
		widgetConfigDao.batchExecute("delete WidgetConfig wc where wc.webpageId=?", id);
		webPageDao.delete(id);
	}
	
	/**
	 * 获取用户的所有页签
	 * @param userId
	 * @return
	 */
	public List<Webpage> getWebpagesByUser() throws CloneNotSupportedException{
		List<Webpage> webpages = webPageDao.getWebpageByUserId();
		if(webpages.isEmpty()){
			Webpage model = webPageDao.getWebpage();
			if(model!=null){
				Webpage webPage = new Webpage();
				webPage.setAcquiescent(true);
				webPage.setName(model.getName());
				webPage.setUserId(ContextUtils.getUserId());
				webPage.setColumns(model.getColumns());
				webPage.setCompanyId(ContextUtils.getCompanyId());
				webPage.setCreatedTime(new Date());
				webPage.setDisplayOrder(StaticVariable.DEFAULT_SEQUENCE_VALUE);
				webPage.setUrl(model.getUrl());
				webPage.setWidgetPosition(model.getWidgetPosition());
				webPageDao.save(webPage);
				saveDefaultWidgetByUser(webPage.getId(),model.getColumns());
				webpages.add(webPage);
			}
		}
		return webpages;
	}
	
	/**
	 * 为用户设置默认的小窗体
	 * @param webPageId
	 */
	private void saveDefaultWidgetByUser(Long webPageId,int columnSize){
		List<List<Long>> lists=getWidgetIds(webPageId);
		List<Long> leftIds=lists.get(0);
		List<Long> centerIds=lists.get(1);
		List<Long> rightIds=lists.get(2);
		List<Widget> defaultWidgets = widgetDao.getWidgets();
		for(int i=0;i<defaultWidgets.size();i++){
			if(!isExistentInWidgetConfig(webPageId,defaultWidgets.get(i).getId())){
				WidgetConfig config = new WidgetConfig();
				config.setWebpageId(webPageId);
				config.setWidgetId(defaultWidgets.get(i).getId());
				config.setUserId(ContextUtils.getUserId());
				
				Widget widget = widgetDao.get(config.getWidgetId());
				if(leftIds.contains(widget.getId())){
					config.setPosition(StaticVariable.POSITION_LEFT);
				}
				if(centerIds.contains(widget.getId())){
					config.setPosition(StaticVariable.POSITION_CENTER);
				}
				if(rightIds.contains(widget.getId())){
					config.setPosition(StaticVariable.POSITION_RIGHT);
				}
				if(config.getPosition()==null){
					config.setPosition(StaticVariable.POSITION_LEFT);
				}
//				config.setPosition(StaticVariable.POSITION_LEFT);
//				if(StaticVariable.OA_DIRECT_TRAIN.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_LEFT);
//				}else if(StaticVariable.SCHEDULE_TASK.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_CENTER);
//					if(columnSize==2){
//						config.setPosition(StaticVariable.POSITION_LEFT);
//					}
//				}else if(StaticVariable.SCHEDULE.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_RIGHT);
//				}else if(StaticVariable.MAIL.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_RIGHT);
//				}else if(StaticVariable.NOTICE.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_RIGHT);
//				}else if(StaticVariable.VOTE.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_LEFT);
//				}else if(StaticVariable.NEWS.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_LEFT);
//				}else if(StaticVariable.OVERTIMENOTICE.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_LEFT);
//				}else if(StaticVariable.COUNTDOWNCARD.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_LEFT);
//				}else if(StaticVariable.INNERDISCUSS.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_LEFT);
//				}else if(StaticVariable.MYBORROWRECORD.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_CENTER);
//					if(columnSize==2){
//						config.setPosition(StaticVariable.POSITION_RIGHT);
//					}
//				}else if(StaticVariable.NEWBOOK.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_CENTER);
//					if(columnSize==2){
//						config.setPosition(StaticVariable.POSITION_RIGHT);
//					}
//				}else if(StaticVariable.MYWORKREPORT.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_CENTER);
//					if(columnSize==2){
//						config.setPosition(StaticVariable.POSITION_RIGHT);
//					}
//				}else if(StaticVariable.LASTDOCUMENT.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_CENTER);
//					if(columnSize==2){
//						config.setPosition(StaticVariable.POSITION_RIGHT);
//					}
//				}else if(StaticVariable.ONLINETIMELONG.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_CENTER);
//					if(columnSize==2){
//						config.setPosition(StaticVariable.POSITION_RIGHT);
//					}
//				}else if(StaticVariable.COMMONCOMMUNICATIONBOOK.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_CENTER);
//					if(columnSize==2){
//						config.setPosition(StaticVariable.POSITION_RIGHT);
//					}
//				}else if(StaticVariable.MYRETURNOFFICESUPPLIES.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_RIGHT);
//				}else if(StaticVariable.BIRTHDAYNOTICE.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_RIGHT);
//				}else if(StaticVariable.STICKYNOTE.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_RIGHT);
//				}else if(StaticVariable.FAVORITEURL.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_RIGHT);
//				}else if(StaticVariable.MYFAVORITE.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_RIGHT);
//				}else if(StaticVariable.STAFFSEARCH.equals(defaultWidgets.get(i).getName())){
//					config.setPosition(StaticVariable.POSITION_RIGHT);
//				}
				widgetConfigDao.save(config);
			}
		}
	}
	
	private boolean isExistentInWidgetConfig(Long webPageId,Long widgetId){
		WidgetConfig config = widgetConfigDao
		        .getWidgetConfig( webPageId, widgetId);
		if(config == null){
			return false;
		}
		return true;
	}
	
	/**
	 * 获取所有窗口model
	 * @return
	 */
	public List<Widget> getAllWidgets(Long webpageId){
		List<Widget> wdigets = null;
		List<WidgetConfig> configs = widgetConfigDao.getWidgetConfigs(webpageId);
		List<Widget> model = getWidgetsByBuyerAndAuthority();
		
		//这个页签是否有初始化小窗体
	    if(configs==null||configs.size()<=0){
	    	wdigets = model;
	    }else{
	    	wdigets = new ArrayList<Widget>();
	    	List<Long> ids = new ArrayList<Long>();
	    	for(WidgetConfig  c:configs){
	    		Long id = c.getWidgetId();
	    		ids.add(id);
	    	}
	    	for(Widget w:model){
	    		if(!isExistentInList(w.getId(),ids)){
	    			wdigets.add(w);
	    		}
	    	}
	    }
		return wdigets;
	}

	private boolean isExistentInList(Long id,List<Long> ids){
		boolean existence = false;
		for(Long uid:ids){
			if(uid==id){
				existence = true;
				break;
			}
		}
		return existence;
	}
	
	/**
	 * 保存页签
	 * @param webpageName
	 * @param columnSize
	 */
	public Webpage saveWebpage(String webpageName, int columnSize, Long webpageId){
		Webpage webPage = null;
		if(webpageId == null){
			webPage = new Webpage();
			int maxSize = webPageDao.getMaxPageOrderNumber()+1;
			webPage.setName(webpageName);
			webPage.setUserId(ContextUtils.getUserId());
			webPage.setColumns(columnSize);
			webPage.setCompanyId(ContextUtils.getCompanyId());
			webPage.setCreatedTime(new Date());
			webPage.setDisplayOrder(maxSize);
			webPage.setUrl("/index/index.htm");
			webPage.setWidgetPosition("widget-place-left=|widget-place-center=|widget-place-right=");
		}else{
			webPage = webPageDao.get(webpageId);
			webPage.setName(webpageName);
			//修改小窗体列数时更新小窗体位置
			updateWebpageColumn(webPage,columnSize);
		}
		webPageDao.save(webPage);
		return webPage;
	}
	
	/**
	 * 修改小窗体列数时更新小窗体位置
	 * @param webPage
	 * @param columnSize
	 */
	private void updateWebpageColumn(Webpage webPage,int columnSize){
		if(webPage.getColumns().equals(3)&&columnSize==2){//3栏变2栏,中栏的小窗体挪到左栏中
			String postion = webPage.getWidgetPosition();
			String[] pos = postion.split("\\|");
			String centerPos = pos[1];
			String leftPos = pos[0];
			String rightPos = pos[2];
			String[] centerPosIds = centerPos.split("=");
			if(centerPosIds.length>1){//如果中栏有小窗体
				String centerIdStrs = centerPosIds[1];
				leftPos=leftPos+","+centerIdStrs;
			}
			String newPosition = leftPos+"|widget-place-center=|"+rightPos;
			webPage.setWidgetPosition(newPosition);
			//更新人员小窗体配置信息
			List<WidgetConfig> widgetConfigs = widgetConfigDao.getWidgetConfigs(webPage.getId());
			for(WidgetConfig conf:widgetConfigs){
				if(conf.getPosition().equals(1)){//如果是中栏
					conf.setPosition(0);//改为左栏
					widgetConfigDao.save(conf);
				}
			}
		}else if(webPage.getColumns().equals(3)&&columnSize==1){//3栏变1栏,将中栏和右栏的小窗体挪到左栏中
			String postion = webPage.getWidgetPosition();
			String[] pos = postion.split("\\|");
			String centerPos = pos[1];
			String leftPos = pos[0];
			String rightPos = pos[2];
			String[] centerPosIds = centerPos.split("=");
			if(centerPosIds.length>1){//如果中栏有小窗体，挪到左栏中
				String centerIdStrs = centerPosIds[1];
				leftPos=leftPos+","+centerIdStrs;
			}
			String[] rightPosIds = rightPos.split("=");
			if(rightPosIds.length>1){//如果右栏有小窗体，挪到左栏中
				String rightIdStrs = rightPosIds[1];
				leftPos=leftPos+","+rightIdStrs;
			}
			String newPosition = leftPos+"|widget-place-center=|widget-place-right=";
			webPage.setWidgetPosition(newPosition);
			//更新人员小窗体配置信息
			List<WidgetConfig> widgetConfigs = widgetConfigDao.getWidgetConfigs(webPage.getId());
			for(WidgetConfig conf:widgetConfigs){
				if(conf.getPosition().equals(1)||conf.getPosition().equals(2)){//如果是中栏或右栏
					conf.setPosition(0);//改为左栏
					widgetConfigDao.save(conf);
				}
			}
		}else if(webPage.getColumns().equals(2)&&columnSize==1){//2栏变1栏,将右栏的小窗体挪到左栏中
			String postion = webPage.getWidgetPosition();
			String[] pos = postion.split("\\|");
			String leftPos = pos[0];
			String rightPos = pos[2];
			String[] rightPosIds = rightPos.split("=");
			if(rightPosIds.length>1){//如果右栏有小窗体，挪到左栏中
				String rightIdStrs = rightPosIds[1];
				leftPos=leftPos+","+rightIdStrs;
			}
			String newPosition = leftPos+"|widget-place-center=|widget-place-right=";
			webPage.setWidgetPosition(newPosition);
			//更新人员小窗体配置信息
			List<WidgetConfig> widgetConfigs = widgetConfigDao.getWidgetConfigs(webPage.getId());
			for(WidgetConfig conf:widgetConfigs){
				if(conf.getPosition().equals(2)){//如果是右栏
					conf.setPosition(0);//改为左栏
					widgetConfigDao.save(conf);
				}
			}
		}
		webPage.setColumns(columnSize);
	}
	
	public void saveWebpage(Webpage webPage){
		webPageDao.save(webPage);
	}
	
	/**
	 * 获取页签
	 * @param id
	 * @return
	 */
	public Webpage getCurrentWebpage(Long webpageId){
		StringBuilder leftpostion=new StringBuilder("widget-place-left=");
		StringBuilder centerpostion=new StringBuilder("widget-place-center=");
		StringBuilder rightpostion=new StringBuilder("widget-place-right=");
		Webpage entity = webPageDao.get(webpageId);
		List<WidgetConfig> widgetConfigs = widgetConfigDao.getWidgetConfigs(webpageId);
		List<Widget> leftWidgets = new ArrayList<Widget>();
		List<Widget> centerWidgets = new ArrayList<Widget>();
		List<Widget> rightWidgets = new ArrayList<Widget>();
		List<Widget> widgetList = getWidgetsByBuyerAndAuthority();
		
		List<List<Long>> lists=getWidgetIds(webpageId);
		//通过webpage的widgetPostion获得以下小窗体id
		List<Long> leftIds=lists.get(0);
		List<Long> centerIds=lists.get(1);
		List<Long> rightIds=lists.get(2);
		//用于存放实际存在的小窗体id
		List<Long> reallyleftIds=new ArrayList<Long>();
		List<Long> reallycenterIds=new ArrayList<Long>();
		List<Long> reallyrightIds=new ArrayList<Long>();
		
		for(WidgetConfig config:widgetConfigs){
			Widget widget = widgetDao.get(config.getWidgetId());
			widget.setParameters(getParametersByWidget(widget.getId()));
			widget.setSystemUrl(SystemUrls.getSystemUrl(widget.getSystemCode()));
			switch (config.getPosition()) {
			case 0:
				if(widgetList.contains(widget)){
					reallyleftIds.add(widget.getId());
				leftWidgets.add(widget);
				}
				break;
			case 1:
				if(widgetList.contains(widget)){
					reallycenterIds.add(widget.getId());
				centerWidgets.add(widget);
				}
				break;
			case 2:
				if(widgetList.contains(widget)){
					reallyrightIds.add(widget.getId());
				rightWidgets.add(widget);
				}
				break;
			}
		}
		entity.setLeftWidgets(leftWidgets);
		entity.setCenterWidgets(centerWidgets);
		entity.setRightWidgets(rightWidgets);
		//更新各窗体存放位置
		//左侧小窗体
		if(leftIds.size()<=0){
			//获得webpage的widgetPostion中不存在，但really...中存在的小窗体的id
			for(Long widgetId:reallyleftIds){
				leftpostion.append("identifierwidget-").append(widgetId).append(",");
			}
		}else{
			//获得webpage的widgetPostion中存在，且really...中存在的小窗体的id
			for(Long widgetId:leftIds){
				if(reallyleftIds.contains(widgetId)){
					leftpostion.append("identifierwidget-").append(widgetId).append(",");
				}
			}
			//获得webpage的widgetPostion中不存在，但really...中存在的小窗体的id
			for(Long widgetId:reallyleftIds){
				if(!leftIds.contains(widgetId)){
					leftpostion.append("identifierwidget-").append(widgetId).append(",");
				}
			}
		}
		//中间小窗体
		if(centerIds.size()<=0){
			//获得webpage的widgetPostion中不存在，但really...中存在的小窗体的id
			for(Long widgetId:reallycenterIds){
				centerpostion.append("identifierwidget-").append(widgetId).append(",");
			}
		}else{
			//获得webpage的widgetPostion中存在，且really...中存在的小窗体的id
			for(Long widgetId:centerIds){
				if(reallycenterIds.contains(widgetId)){
					centerpostion.append("identifierwidget-").append(widgetId).append(",");
				}
			}
			//获得webpage的widgetPostion中不存在，但really...中存在的小窗体的id
			for(Long widgetId:reallycenterIds){
				if(!centerIds.contains(widgetId)){
					centerpostion.append("identifierwidget-").append(widgetId).append(",");
				}
			}
		}
		//右侧小窗体
		if(rightIds.size()<=0){
			//获得webpage的widgetPostion中不存在，但really...中存在的小窗体的id
			for(Long widgetId:reallyrightIds){
				rightpostion.append("identifierwidget-").append(widgetId).append(",");
			}
		}else{
			//获得webpage的widgetPostion中存在，且really...中存在的小窗体的id
			for(Long widgetId:rightIds){
				if(reallyrightIds.contains(widgetId)){
					rightpostion.append("identifierwidget-").append(widgetId).append(",");
				}
			}
			//获得webpage的widgetPostion中不存在，但really...中存在的小窗体的id
			for(Long widgetId:reallyrightIds){
				if(!rightIds.contains(widgetId)){
					rightpostion.append("identifierwidget-").append(widgetId).append(",");
				}
			}
		}
		if(leftpostion.length()>0&&leftpostion.charAt(leftpostion.length()-1)==',')leftpostion=leftpostion.deleteCharAt(leftpostion.length()-1);
		if(centerpostion.length()>0&&centerpostion.charAt(centerpostion.length()-1)==',')centerpostion=centerpostion.deleteCharAt(centerpostion.length()-1);
		if(rightpostion.length()>0&&rightpostion.charAt(rightpostion.length()-1)==',')rightpostion=rightpostion.deleteCharAt(rightpostion.length()-1);
		entity.setWidgetPosition(leftpostion.append("|").append(centerpostion).append("|").append(rightpostion).toString());
		
		return entity;
	}
	
	private List<WidgetParameter> getParametersByWidget(Long widgetId){
		List<WidgetParameter> parameters = widgetParameterDao.getWidgetParameters( widgetId);
		for(int i=0;i<parameters.size();i++){
			List<WidgetParameterValue> wpvs = widgetParameterValueDao.getWidgetParameterValues(parameters.get(i).getId());
			parameters.get(i).setParameterValues(wpvs);
		}
		return parameters;
	}
	
	/**
	 * 取出窗口的所有参数&参数值&用户已设置的参数
	 * @param widgetId
	 * @return
	 */
	public List<WidgetParameter> getParameters(Long widgetId){
		List<WidgetParameter> wps = widgetParameterDao.getWidgetParameters(widgetId);
		return wps;
	}
	
	/**
	 * 根据widgetId得打小窗体的名字
	 * @param widgetId
	 * @return
	 */
	public  String getWidgetNameById(Long widgetId){
		Widget widget=widgetDao.getWidgetById(widgetId);
		String widgetName = widget.getName().toString();
		return widgetName;
	}
	
	/**
	 * 保存用户参数设置
	 */
	public void saveParameterValues(){
		String widgetId = ServletActionContext.getRequest().getParameter("widgetId");
		String webpageId = ServletActionContext.getRequest().getParameter("webpageId");
		List<WidgetParameter> parameters = widgetParameterDao.getWidgetParameters(Long.parseLong(widgetId));
		for(WidgetParameter parameter:parameters){
			List<WidgetParameterValue> parameterValues = widgetParameterValueDao.getWidgetParameterValuesByUserIdAndWebpageId(parameter.getId(),Long.parseLong(webpageId));
			String parameterValue = ServletActionContext.getRequest().getParameter(parameter.getCode());
			if(StringUtils.isNotEmpty(parameterValue)){//只有存在值的时候我才去更新或新建一个WidgetParameterValue
				if(parameterValues == null || parameterValues.size() == 0){//新建
					WidgetParameterValue newParameterValue = new WidgetParameterValue();
					newParameterValue.setValue(parameterValue);
					newParameterValue.setUserId(ContextUtils.getUserId());
					newParameterValue.setWidgetParameter(parameter);
					newParameterValue.setWebPageId(Long.parseLong(webpageId));
					widgetParameterValueDao.save(newParameterValue);
				}else{//修改
					WidgetParameterValue olderParameterValue = parameterValues.get(0);
					olderParameterValue.setValue(parameterValue);
					widgetParameterValueDao.save(olderParameterValue);
				}
			}else{//前台穿过来空值
				if(parameterValues.size()>0){
					WidgetParameterValue olderParameterValue = parameterValues.get(0);
					widgetParameterValueDao.delete(olderParameterValue);
				}
			}
		}
	}

    /**
     * 依次获取页面所有的窗体ID
     * @param webpage
     * @return
     */
    public List<Long> getAllWidgetId(Webpage webpage){
    	Map<String, Widget> map = new HashMap<String, Widget>();
    	List<Long> widgetIds = new ArrayList<Long>();
    	List<Widget> widgetList = getWidgetsByBuyerAndAuthority();
    	
		List<Widget> widgets = webpage.getLeftWidgets();
		for(Widget widget : widgets){
			if(widgetList.contains(widget)){
				widgetIds.add(widget.getId());
				map.put("LEFT_"+widget.getId(), widget);
			}
		}
		widgets = webpage.getCenterWidgets();
		for(Widget widget : widgets){
			if(widgetList.contains(widget)){
				widgetIds.add(widget.getId());
				map.put("CENTER_"+widget.getId(), widget);
			}
		}
		widgets = webpage.getRightWidgets();
		for(Widget widget : widgets){
			if(widgetList.contains(widget)){
				widgetIds.add(widget.getId());
				map.put("RIGHT_"+widget.getId(), widget);
			}
		}
		String widgetPositions = webpage.getWidgetPosition();
    	if(!StringUtils.isEmpty(widgetPositions)){
	    	String[] columns=widgetPositions.split("\\|");
	    	for(int i=0;i<columns.length;i++){
	    		String[] widgetsArray = columns[i].split("=");
	    		if(widgetsArray.length==2){
	    			String place=widgetsArray[0];
	    			if(place.equals("col1")){
	    				String[] widgetId=widgetsArray[1].split(",");
	    				webpage.setLeftWidgets(new ArrayList<Widget>());
	    				for(String w:widgetId){
	    					webpage.getLeftWidgets().add(map.get("LEFT_"+w.split("-")[1]));
	    		    	}
	    			}else if(place.equals("col2")){
	    				String[] widgetId=widgetsArray[1].split(",");
	    				webpage.setRightWidgets(new ArrayList<Widget>());
	    				for(String w:widgetId){
	    					webpage.getRightWidgets().add(map.get("RIGHT_"+w.split("-")[1]));
	    		    	}
	    			}else if(place.equals("col3")){
	    				String[] widgetId=widgetsArray[1].split(",");
	    				webpage.setCenterWidgets(new ArrayList<Widget>());
	    				for(String w:widgetId){
	    					webpage.getCenterWidgets().add(map.get("CENTER_"+w.split("-")[1]));
	    		    	}
	    			}
	    		}
	    	}
    	}
    	return widgetIds;
    }
    
    public void addWidgets(Webpage page, String widgetIds, int position){
    	String[] ids = widgetIds.split(",");
    	WidgetConfig config = null;
    	for(int i = 0;i < ids.length; i++){
    		config=widgetConfigDao.getWidgetConfig(page.getId(), Long.valueOf(ids[i]));
    		if(config==null)config = new WidgetConfig();
    		config.setPosition(position);
    		config.setCompanyId(ContextUtils.getCompanyId());
    		config.setUserId(ContextUtils.getUserId());
    		config.setWidgetId(Long.valueOf(ids[i]));
    		config.setWebpageId(page.getId());
    		config.setVisible(true);
    		widgetConfigDao.save(config);
    	}
    	webPageDao.save(page);
    }
	
	/**
	 * 删除小窗体
	 * @param webpageId
	 * @param widgetId
	 */
	public void deleteWidget(Webpage page,Long widgetId){
		WidgetConfig config = widgetConfigDao.getWidgetConfig(page.getId(), widgetId);
		if(config != null){
			config.setVisible(false);
			widgetConfigDao.save(config);
		}
		webPageDao.save(page);
	}
	
	/**
	 * 获取小窗体html
	 * @param widgetId
	 * @return
	 */
	public String getWidgetHtml(String widgetIdStrs, Long webpageId,Integer pageNo) throws Exception{
		StringBuilder result = new StringBuilder();
		result = getWidgetsHtml(widgetIdStrs, webpageId, pageNo);
		return result.toString();
	}
	
	public StringBuilder getWidgetsHtml(String widgetIds,Long webpageId,Integer pageNo) throws Exception{
		StringBuilder result = new StringBuilder();
		try {
			String[] widgetIdArr = widgetIds.split(",");
			for(String widgetId:widgetIdArr){
				if(StringUtils.isNotEmpty(widgetId)){
					result.append("{").append(widgetId).append(":");
					String widgetHtml =getWidgetHtml(Long.parseLong(widgetId),webpageId,pageNo);
					if(!"error".equals(widgetHtml)){//如果出现异常了，则不再拼接
						Widget widget = getWdigetById(Long.parseLong(widgetId));
						if(widget.getPageVisible()!=null && widget.getPageVisible()){
							HashMap<String, Object> dataModel=new HashMap<String, Object>();
							dataModel.put("pageNo", pageNo);
							dataModel.put("totalNo", Integer.parseInt(widgetHtml.split("totalNo")[1]));
							dataModel.put("id", widgetId);
							widgetHtml=widgetHtml.split("totalNo")[0]+TagUtil.getContent(dataModel, "show-pagination.ftl");	
						}else{
							widgetHtml=widgetHtml.split("totalNo")[0];
						}
					}
					result.append(widgetHtml).append("}@#$%");
				}
			}
		}catch (Exception e) {
			e.printStackTrace();

		}finally{
		}
		return result;
	}
	
	/**
	 * 获取小窗体html
	 * @param widgetId
	 * @return
	 */
	public String getWidgetHtml(Long widgetId, Long webpageId,Integer pageNo) throws Exception{
		Widget widget = getWdigetById(widgetId);
		List<String> parameterNames = new ArrayList<String>();
		List<String> parameterValues = new ArrayList<String>();
		
		List<WidgetParameter> parameters = widgetParameterDao.getWidgetParameters(widgetId);
		for(WidgetParameter wp:parameters){
			List<WidgetParameterValue> widgetParameterValues = widgetParameterValueDao.getWidgetParameterValuesByUserId(wp.getId(), webpageId);
			if(!widgetParameterValues.isEmpty()){//设置小窗体参数设置值
				parameterNames.add(wp.getName());
				parameterValues.add(getWidgetParameterValues(widgetParameterValues));	
			}else{//设置小窗体参数默认值
				if(wp.getControlType().equals(ControlType.PT_TEXT)){//文本类型
					if(!wp.getDefaultValue().isEmpty()){
						parameterNames.add(wp.getName());
					    parameterValues.add(wp.getDefaultValue());
					}
				}else{//其他类型
					if(wp.getOptionGroupId()!=null&&!wp.getOptionGroupId().equals(0l)){
						parameterNames.add(wp.getName());
						parameterValues.add(ApiFactory.getSettingService().getOptionGroupDefaultValue(wp.getOptionGroupId()));
					}
				}
				
			}
		}
		parameterNames.add(StaticVariable.USER_ID);
		parameterNames.add(StaticVariable.LOGIN_NAME);
		parameterNames.add(StaticVariable.COMPANY_ID);
		parameterNames.add(StaticVariable.PAGE_NO);
		parameterValues.add(ContextUtils.getUserId().toString());
		parameterValues.add(ContextUtils.getLoginName());
		parameterValues.add(ContextUtils.getCompanyId().toString());
		parameterValues.add(pageNo.toString());
		return getHttpClientConnection(widget.getSystemCode(),widget.getUrl(),parameterNames,parameterValues);
	}
	
	/**
	 * 根据名称获取小窗体
	 * @return
	 * @throws Exception
	 */
	public Widget getWidgetByName(String widgetName) throws Exception{
		Widget widget = widgetDao.getWidgetByNames(widgetName);
		return widget;
	}
	private String getWidgetParameterValues(List<WidgetParameterValue> widgetParameterValues){
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<widgetParameterValues.size();i++){
			if(i>0){
				sb.append(StaticVariable.SYMBOL_SUBTRACTION);
			}
			sb.append(widgetParameterValues.get(i).getValue());
		}
		return sb.toString();
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
			List<String> paramNames,List<String> paramValues){
		
		StringBuilder sb = new StringBuilder(url);
		if(paramNames!=null)
			for(int i=0;i<paramNames.size();i++){
				if(i == 0) sb.append("?");
				else sb.append(StaticVariable.SYMBOL_AND);
				sb.append(paramNames.get(i));
				sb.append(StaticVariable.SYMBOL_EQUAL);
				sb.append(paramValues.get(i));
			}
			try {
				String productUrl = sb.toString();
				if(!url.startsWith("http")){
					productUrl = getSystemUrl(productCode);
					productUrl=productUrl+sb.toString();
				}
				return getHttpClientConnection(productUrl);
			} catch (Exception e) {
				return "error";
			}
	}
	
	/**
	 * 获取通知的HTML
	 * @return
	 * @throws Exception
	 */
	public String getActiveNoticeHtml() throws Exception{
		String url = getProperty(StaticVariable.NOTICE_WEBSERVICE_URL);
		List<String> paramNames = new ArrayList<String>();
		List<String> paramValues = new ArrayList<String>();
		paramNames.add(StaticVariable.USER_ID);
		paramNames.add(StaticVariable.LOGIN_NAME);
		paramNames.add(StaticVariable.COMPANY_ID);
		paramValues.add(ContextUtils.getUserId().toString());
		paramValues.add(ContextUtils.getLoginName());
		paramValues.add(ContextUtils.getCompanyId().toString());
		
		return getHttpClientConnection(
				   StaticVariable.OA_PRODUCT_CODE,url,paramNames,paramValues);
	}

	private String getProperty(String key) throws Exception{
		Properties property = new Properties();
		property.load(IndexManager.class.getClassLoader()
				.getResourceAsStream(StaticVariable.APPLICATION_PROPERTIES));
		return property.getProperty(key);
	}
	
	/**
	 * 保存主题
	 * @param name
	 */
	public void saveTheme(String name){
		UserTheme theme = userThemeDao.getTheme();
		if(theme==null){
			theme = new UserTheme();
			theme.setUserId(ContextUtils.getUserId());
		}
		theme.setThemeCode(name);
		userThemeDao.save(theme);
	}
	
	/**
	 * 获取某用户设置的主题
	 * @return
	 */
	public String getThemeByUser(Long userId, Long companyId){
		UserTheme theme = userThemeDao.getTheme(userId, companyId);
		if(theme==null){
			return "";
		}else{
			return theme.getThemeCode();
		}
	}
	
	private String getSystemUrl(String key) throws Exception{
		String url = "";
		if(StringUtils.isNotEmpty(key)){
			url=SystemUrls.getSystemUrl(key);
			url += StaticVariable.SYMBOL_SLASH;
		}else{
			url="/";
		}
		return url;
	}
	
	
	/**
	 * 得到小窗体位置
	 * @return
	 */
	public String getWidgetPosition(String wpId){
		Webpage webpage=webPageDao.get(Long.parseLong(wpId));
		if(webpage!=null){
		return webpage.getWidgetPosition();
		}
		return null;
	}
	
	/**
	 * 获得当前的信息数
	 * @return
	 * @throws Exception
	 */
	public String getCurrentTotalNoteNum(Long userId) throws Exception {
		return getHttpClientConnection("http://localhost:8080/octopus/student/small-note!getMessageCount.html?currentUserId="+userId);
	}
	
	/**
	 * 重载getHttpClientConnection方法
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private String getHttpClientConnection(String url) throws Exception{
		HttpGet httpget = new HttpGet(url);
		HttpClient httpclient = new DefaultHttpClient();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseBody = httpclient.execute(httpget, responseHandler);
		httpclient.getConnectionManager().shutdown();
	    return responseBody;//接收html
	}
	
	//根据购买的系统、用户权限显示小窗体
	 public List<Widget> getWidgetsByBuyerAndAuthority(){
		 List<BusinessSystem> businessSystemList = acsApiManager.getAllBusiness(ContextUtils.getCompanyId());
		 List<Widget> widgetList = new ArrayList<Widget>();
		 for(BusinessSystem businessSystem : businessSystemList){
			 widgetList.addAll(widgetDao.getWidgetsBySystemCode(businessSystem.getCode()));
		 }
		 Set<Role> roleSet = ApiFactory.getAcsService().getRolesByUser(ContextUtils.getUserId());
		 List<WidgetRole> widgetByRoleList = new ArrayList<WidgetRole>();
		 for(Role role : roleSet ){
			 widgetByRoleList.addAll(widgetRoleDao.getWidgetsByRoleId(role.getId()));
		 }
		 
		 List<Widget> result = new ArrayList<Widget>();
		 for(WidgetRole widgetRole : widgetByRoleList){
			 if(widgetList.contains(widgetDao.get(widgetRole.getWidgetId()))&&!result.contains(widgetDao.get(widgetRole.getWidgetId()))){
				 result.add(widgetDao.get(widgetRole.getWidgetId()));
			 }
		 }
		 java.util.Collections.sort(result);
		 return result;
	 }
	 
	 /**
	  * 根据系统id获得当前公司中所有的小窗体
	  * @param businessId
	  * @return
	  */
	 public void getWidgetsBySystemCode(Page<Widget> widgetPage,String systemCode){
		 widgetDao.getWidgetsBySystemCode(widgetPage,systemCode);
	 }
	 
	 public void saveWidget(Widget widget,String roleIds){
		 //保存Widget
		 widgetDao.save(widget);
		 List<WidgetParameter> parameters=new ArrayList<WidgetParameter>();
		 //保存窗体参数
			List<Object> objects=JsonParser.getFormTableDatas(WidgetParameter.class);
			for(Object obj:objects){
				WidgetParameter parameter=(WidgetParameter)obj;
				if(StringUtils.isNotEmpty(parameter.getName())){
					parameter.setWidget(widget);
					parameter.setCompanyId(ContextUtils.getCompanyId());
					widgetParameterDao.save(parameter);
					parameters.add(parameter);
				}
			}
			if(parameters.size()>0){
				widget.setParameters(parameters);
			}
			//删除原有角色小窗体关系
			widgetRoleDao.deleteWidgetRoleByWidgetId(widget.getId());
			//保存WidgetRole
			 if(StringUtils.isNotEmpty(roleIds)){
				 String[] ids=roleIds.split(",");
				 for(String id:ids){
					 //创建小窗体角色关系
					WidgetRole wr=new WidgetRole();
					wr.setCompanyId(ContextUtils.getCompanyId());
					wr.setRoleId(Long.parseLong(id));
					wr.setWidgetId(widget.getId());
					widgetRoleDao.save(wr);
				 }
			 }
	 }
	 
	 public void deleteWidget(String widgetIds){
		 if(StringUtils.isNotEmpty(widgetIds)){
			 String[] ids=widgetIds.split(",");
			 for(String id:ids){
				 widgetDao.delete(Long.parseLong(id));
				 List<WidgetConfig> configs=widgetConfigDao.getWidgetConfigsByWidgetId(Long.parseLong(id));
				 if(configs!=null){
					 for(WidgetConfig config:configs){
						 widgetConfigDao.delete(config);
					 }
				 }
				 List<WidgetRole> wrs= widgetRoleDao.getWidgetRoleByWidgetId(Long.parseLong(id));
				 if(wrs!=null){
					 for(WidgetRole wr:wrs){
						 widgetRoleDao.delete(wr);
					 }
				 }
			 }
		 }
	 }
	 
	 public String validateDeleteWidget(String widgetIds){
		 StringBuilder sb=new StringBuilder();
		 if(StringUtils.isNotEmpty(widgetIds)){
			 String[] ids=widgetIds.split(",");
			 for(String id:ids){
				 Widget widget=getWdigetById(Long.parseLong(id));
				 List<WidgetConfig> configs=widgetConfigDao.getVisibleWidgetConfigsByWidgetId(Long.parseLong(id));
				 if(configs!=null&&configs.size()>0){
					 sb.append("\"").append(widget.getCode()).append("\"").append(",");
				 }else{
					 List<WidgetRole> wrs= widgetRoleDao.getWidgetRoleByWidgetId(Long.parseLong(id));
					 if(wrs!=null&&wrs.size()>0)sb.append("\"").append(widget.getCode()).append("\"").append(",");
				 }
			 }
		 }
		 if(StringUtils.isNotEmpty(sb.toString())){
			 if(sb.charAt(sb.length()-1)==',')sb.deleteCharAt(sb.length()-1);
		 }
		 return sb.toString();
	 }
	 
	 public List<WidgetRole> getWidgetRoleByWidgetId(Long widgetId){
		 return widgetRoleDao.getWidgetRoleByWidgetId(widgetId);
	 }
	 
	 /**
	 * 根据CODE获取小窗体
	 * @param code
	 * @return
	 */
	 public Widget getWidgetByCode(String code){
		 Widget widget = widgetDao.getWidgetByCode(code);
		return widget;
	 }
	 
	 /**
	  * 验证小窗体是否存在
	  * @param code
	  * @return 存在返回true,反之
	  */
	 public boolean isWidgetExist(String code,Long widgetId){
		 Widget wg=widgetDao.getWidgetByCode(code);
		 if(wg==null){
			 return false;
		 }else{
			 if(widgetId==null)return true;
			 if(wg.getId().equals(widgetId)){
				 return false;
			 }else{
				 return true;
			 }
		 }
	 }
	 
	 /**
	  * 删除窗口参数
	  * @param parameterId
	  */
	 public void deleteParameter(Long parameterId){
		 widgetParameterDao.delete(parameterId);
	 }
 
	//初始化备选值
	public void initializeWidgetParameter(
			List<WidgetParameter> widgetParameterList) {
		for(WidgetParameter widgetParameter:widgetParameterList){
			if(widgetParameter.getControlType().equals(ControlType.PT_SELECT)){
				if(widgetParameter.getOptionGroupId()!=null){
					widgetParameter.setOptions(optionGroupManager.getOptionsByGroup(widgetParameter.getOptionGroupId()));
				}
			}
		}
		
	}
	
	/**
	 * 获得所有主题
	 * @param themePage
	 */
	public void getThemePage(Page<Theme> themePage) {
		themeDao.themePage(themePage);
	}

	/**
	 * 保存主题
	 * @param theme
	 */
	public void saveTheme(Theme theme) {
		themeDao.save(theme);
	}

	/**
	 * 删除主题
	 * @param valueOf
	 */
	public void deleteTheme(Long id) {
		themeDao.delete(id);
	}

	/**
	 * 根据id获得主题
	 * @param id
	 * @return
	 */
	public Theme getTheme(Long id) {
		return themeDao.get(id);
	}

	/**
	 * 获得启用的主题
	 * @return
	 */
	public List<Theme> getStartUsingTheme() {
		return themeDao.getStartUsingTheme();
	}

	/**
	 * 改变主题的状态
	 * @param ids
	 * @return
	 */
	public String changeThemeState(String ids) {
		int draftToEn=0,enToDis=0,disToEn=0;
		StringBuilder sbu=new StringBuilder("");
		for(String id:ids.split(",")){
			Theme theme = themeDao.get(Long.valueOf(id));
			if (theme.getDataState().equals(DataState.DRAFT)) {// 草稿->启用
				theme.setDataState(DataState.ENABLE);
				draftToEn++;
			} else if (theme.getDataState().equals(DataState.ENABLE)) {// 启用->禁用
				theme.setDataState(DataState.DISABLE);
				enToDis++;
			} else if (theme.getDataState().equals(DataState.DISABLE)) {// 禁用->启用
				theme.setDataState(DataState.ENABLE);
				disToEn++;
			}
			themeDao.save(theme);
		}
		sbu.append(draftToEn).append("个草稿->启用,")
		.append(enToDis).append("个启用->禁用,")
		.append(disToEn).append("个禁用->启用");
		return sbu.toString();
	}
	//刷新小窗体位置,在移动小窗体后用到 （liudongxia）
	public void refreshWidgetPosition(Long webpageId){
		//widget-place-left=identifierwidget-3,identifierwidget-5|widget-place-center=identifierwidget-1|widget-place-right=identifierwidget-4,identifierwidget-2
		List<List<Long>> lists=getWidgetIds(webpageId);
		List<Long> leftIds=lists.get(0);
		List<Long> centerIds=lists.get(1);
		List<Long> rightIds=lists.get(2);
		List<WidgetConfig> widgetConfigs = widgetConfigDao.getWidgetConfigs(webpageId);
		for(WidgetConfig config:widgetConfigs){
			Widget widget = widgetDao.get(config.getWidgetId());
			if(leftIds.contains(widget.getId())){
				config.setPosition(StaticVariable.POSITION_LEFT);
				widgetConfigDao.save(config);
				continue;
			}
			if(centerIds.contains(widget.getId())){
				config.setPosition(StaticVariable.POSITION_CENTER);
				widgetConfigDao.save(config);
				continue;
			}
			if(rightIds.contains(widget.getId())){
				config.setPosition(StaticVariable.POSITION_RIGHT);
				widgetConfigDao.save(config);
				continue;
			}
		}
	}
	
	private List<List<Long>> getWidgetIds(Long webpageId){
		List<List<Long>> lists=new ArrayList<List<Long>>();
		//widget-place-left=identifierwidget-3,identifierwidget-5|widget-place-center=identifierwidget-1|widget-place-right=identifierwidget-4,identifierwidget-2
		Webpage page = webPageDao.get(webpageId);
		List<Long> leftIds=new ArrayList<Long>();
		List<Long> centerIds=new ArrayList<Long>();
		List<Long> rightIds=new ArrayList<Long>();
		String widgetPostion=page.getWidgetPosition();
		if(StringUtils.isNotEmpty(widgetPostion)){
			String postion = page.getWidgetPosition();
			String[] postionArr = postion.split("\\|");
			String leftPositon=postionArr[0];
			String centerPositon="";
			String rightPositon="";
			centerPositon=postionArr[1];
			rightPositon=postionArr[2];
			//获得左侧小窗体id
			if(leftPositon.split("=").length>1){
				leftPositon=leftPositon.split("=")[1];
			}else{
				leftPositon="";
			}
			String[] leftWidgetIds=leftPositon.split(",");
			//获得中间小窗体id
			if(centerPositon.split("=").length>1){
				centerPositon=centerPositon.split("=")[1];
			}else{
				centerPositon="";
			}
			String[] centerWidgetIds=centerPositon.split(",");
			
			if(rightPositon.split("=").length>1){
				rightPositon=rightPositon.split("=")[1];
			}else{
				rightPositon="";
			}
			String[] rightWidgetIds=rightPositon.split(",");
			//获得右侧小窗体id
			for (String widgetId : leftWidgetIds) {
				if(widgetId.split("-").length>1)leftIds.add(Long.parseLong(widgetId.split("-")[1]));
			}
			for (String widgetId : centerWidgetIds) {
				if(widgetId.split("-").length>1)centerIds.add(Long.parseLong(widgetId.split("-")[1]));
			}
			for (String widgetId : rightWidgetIds) {
				if(widgetId.split("-").length>1)rightIds.add(Long.parseLong(widgetId.split("-")[1]));
			}
			
		}
		lists.add(leftIds);
		lists.add(centerIds);
		lists.add(rightIds);
		return lists;
		
	}
	
	/**
	 * 导出主题
	 */
	public void backup(String systemIds, Long companyId,FileConfigModel fileConfig) {
		try {
			if(StringUtils.isNotEmpty(fileConfig.getFilename())){
				File file = new File(fileConfig.getExportRootPath()+"/"+fileConfig.getExportPath()+"/"+fileConfig.getFilename()+".xls");
				OutputStream out=null;
				out=new FileOutputStream(file);
				exportTheme(out);
			}
		}catch (Exception e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}
	}
	
	private void exportTheme(OutputStream fileOut){
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='PORTAL_THEME']");
		wb = new HSSFWorkbook();
		HSSFSheet sheet=wb.createSheet("PORTAL_THEME");
		
		//获得导出的根节点
		String[] rootPaths=dataHandle.getRootPath();
		String exportRootPath=rootPaths[0];
		
		//创建导出文件夹，导出的流程定义文件暂存的位置
		File folder = new File(exportRootPath+"/portalTheme");
		if(!folder.exists()){
			folder.mkdirs();
		}
		
        HSSFRow row = sheet.createRow(0);
        dataHandle.getFileHead(wb,row,confs);
        List<Theme> themes=themeDao.getAllTheme();
		for(Theme theme:themes){
			themeInfo(theme,sheet,confs,exportRootPath);
		}
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	
	private void themeInfo(Theme theme,HSSFSheet sheet,List<DataSheetConfig> confs,String exportRootPath){
		if(theme!=null){
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
				DataSheetConfig conf=confs.get(i);
				if(!conf.isIgnore()){
    				dataHandle.setFieldValue(conf,i,rowi,theme);
				}
			}
		}
	}
	
	public void restore(Long companyId, FileConfigModel fileConfig,String... imatrixInfo) {
		File file =null;
		if(StringUtils.isNotEmpty(fileConfig.getFilename())){
			file=new File(fileConfig.getImportRootPath()+"/"+fileConfig.getImportPath()+"/"+fileConfig.getFilename()+".xls");
			if(file.exists()){
				importTheme(file, companyId);
			}
		}
	}
	private void importTheme(File file,Long companyId){
		List<DataSheetConfig> confs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='PORTAL_THEME']");
		Map<String,Integer> map=dataHandle.getIdentifier(confs);
		//创建时间,创建人姓名,创建人id,公司id
		FileInputStream fis=null;
 		InputStreamReader fr=null;
 		BufferedReader br=null;
 		try{
 			fis=new FileInputStream(file);
 			HSSFWorkbook wb=new HSSFWorkbook(fis);
 			HSSFSheet sheet=wb.getSheet("PORTAL_THEME");
 			if(ContextUtils.getCompanyId()==null){
 				if(companyId==null){
 					List<Company> companys=companyManager.getCompanys();
 					for(Company company:companys){
 						ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
 						ParameterUtils.setParameters(parameters);
 						importThemeData(sheet,confs,map);
 					}
 				}else{
 					ThreadParameters parameters=new ThreadParameters(companyId,null);
						ParameterUtils.setParameters(parameters);
						importThemeData(sheet,confs,map);
 				}
 				dataHandle.clearCompanyId();
 			}else{
 				importThemeData(sheet,confs,map);
 			}
 		} catch (FileNotFoundException e) {
 			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
 			try{
	 			if(br!=null)br.close();
	 			if(fr!=null)fr.close();
	 			if(fis!=null)fis.close();
 			}catch(IOException ep){
 				log.debug(PropUtils.getExceptionInfo(ep));
 			}
 		}
	}
	
	private void importThemeData(HSSFSheet sheet,List<DataSheetConfig> confs,Map<String,Integer> map){
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			HSSFRow row =sheet.getRow(i);
			if(sheet.getRow(i)!=null){
				addTheme(confs,row,map);
			}
		}
	}
	private void addTheme(List<DataSheetConfig> confs,HSSFRow row,Map<String,Integer> map ){
		Integer index=map.get("code");
		String code=row.getCell(index).getStringCellValue();//导入定义编号
		Theme theme=themeDao.getTheme(code);
		if(theme==null){
			theme=new Theme();
		}
		theme.setCode(code);
		for(int j=0;j<confs.size();j++){
			DataSheetConfig conf=confs.get(j);
			if(!conf.isIgnore()){
				String fieldName=conf.getFieldName();
				String value=null;
				if(row.getCell(j)!=null){
					value=row.getCell(j).getStringCellValue();
				}
				if(StringUtils.isNotEmpty(value)){//导入数据
					dataHandle.setValue(theme,fieldName,conf.getDataType(),value,conf.getEnumName());
				}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
					dataHandle.setValue(theme,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
				}
			}
		}
		theme.setCreatedTime(new Date());
		theme.setCreator(ContextUtils.getLoginName());
		theme.setCreatorName(ContextUtils.getUserName());
		theme.setCompanyId(ContextUtils.getCompanyId());
		themeDao.save(theme);
	}
}
