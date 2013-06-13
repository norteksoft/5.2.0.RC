package com.norteksoft.bs.options.web;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.bs.options.entity.TimedTask;
import com.norteksoft.bs.options.entity.Timer;
import com.norteksoft.bs.options.enumeration.TimingType;
import com.norteksoft.bs.options.service.JobInfoManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.Scheduler;
import com.norteksoft.product.web.struts2.CrudActionSupport;
/**
 * 定时任务
 * @author Administrator
 *
 */
@Namespace("/options")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "job-info", type = "redirectAction")})
public class JobInfoAction extends CrudActionSupport<TimedTask> {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String ids;
	
	private TimedTask jobInfo;
	
	private Long systemId;
	
	private List<BusinessSystem> businessSystems;
	
	private Page<TimedTask> pages= new Page<TimedTask>(0,true);
	
	private Page<Timer> cornInfos =new Page<Timer>(0,true);
	
	private TimingType typeEnum;//方式
	
	private String everyMonth;//每月
	
	private String everyWeek;//每周
	
	private String everyDate;//每天
	
	private String appointTime;//指定时间
	
	private String appointSet;//高级设置
	
	private DataState dataState;//状态
	
	@Autowired
	private JobInfoManager jobInfoManager;
	
	@Autowired
	private BusinessSystemManager businessSystemManager;
	
	
	/**
	 * 删除
	 */
	@Action("job-info-delete")
	@Override
	public String delete() throws Exception {
		String str=jobInfoManager.deleteJobInfos(ids);
		if(str.split("=-")[0].equals("0")){
			addActionMessage("<font class='onSuccess'><nobr>成功删除"+str.split("=-")[0]+"条,失败"+str.split("=-")[1]+"条,不能删除已启用的记录.</nobr></font>");	
		}else{
		    addActionMessage("<font class='onSuccess'><nobr>成功删除"+str.split("=-")[0]+"条,失败"+str.split("=-")[1]+"条</nobr></font>");
		}
		ApiFactory.getBussinessLogService().log("定时设置", "删除定时器",ContextUtils.getSystemId("bs"));
		return "job-info";
	}

	/**
	 * 新建
	 */
	@Action("job-info-input")
	@Override
	public String input() throws Exception {
		if(jobInfo.getRunAsUser()==null){
			jobInfo.setRunAsUser(ContextUtils.getLoginName());
			jobInfo.setRunAsUserName(ContextUtils.getUserName());
		}
		return "job-info-input";
	}

	@Override
	public String list() throws Exception {
		if(pages.getPageSize()>1){
			jobInfoManager.getJobInfo(pages,systemId);
			this.renderText(PageUtils.pageToJson(pages));
			return null;
		}else{
			businessSystems= businessSystemManager.getAllSystems();
			if(businessSystems.size()>0&&systemId==null)systemId=businessSystems.get(0).getId();
		}
		ApiFactory.getBussinessLogService().log("定时设置", "查看定时列表",ContextUtils.getSystemId("bs"));
		return SUCCESS;
	}
	
	/**
	 * 子表内容(时间表)
	 * @return
	 */
	@Action("job-info-chiledList")
	public String chiledList(){
		if(cornInfos.getPageSize()>1){
			if(id!=null){
				cornInfos=jobInfoManager.getCornInfos(cornInfos,id);
				this.renderText(PageUtils.pageToJson(cornInfos));
			}
		}
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			jobInfo = new TimedTask();
			jobInfo.setCreatedTime(new Date());
			jobInfo.setCompanyId(ContextUtils.getCompanyId());
		}else{
			jobInfo = jobInfoManager.getJobInfo(id);
		}
	}

	/**
	 * 保存
	 */
	@Action("job-info-save")
	@Override
	public String save() throws Exception {
		if(systemId!=null)
			jobInfo.setSystemCode(businessSystemManager.getBusiness(systemId).getCode());
		jobInfoManager.saveJobInfo(jobInfo);
		
		if(typeEnum!=null||everyMonth!=null||everyWeek!=null||everyDate!=null||appointTime!=null||appointSet!=null){
			Timer cornInfo=new Timer();
			cornInfo.setCompanyId(ContextUtils.getCompanyId());
			cornInfo.setTimingType(typeEnum);//类型
			if(StringUtils.isNotEmpty(everyMonth))
				cornInfo.setDateTime(everyMonth);//按月
			if(StringUtils.isNotEmpty(everyWeek))
				cornInfo.setWeekTime(everyWeek);//按星期
			if(StringUtils.isNotEmpty(everyDate))
				cornInfo.setCorn(everyDate);//每天
			if(StringUtils.isNotEmpty(appointTime))
				cornInfo.setAppointTime(appointTime);//指定时间
			if(StringUtils.isNotEmpty(appointSet))
				cornInfo.setAppointSet(appointSet);//高级
			cornInfo.setJobId(jobInfo.getId());
			cornInfo.setJobInfo(jobInfo);
			jobInfoManager.saveCornInfo(cornInfo);
			if(DataState.ENABLE.equals(jobInfo.getDataState()))
				Scheduler.addJob(cornInfo);
			ApiFactory.getBussinessLogService().log("定时设置", "增加定时",ContextUtils.getSystemId("bs"));
		}
		this.renderText(jobInfo.getId().toString());
		ApiFactory.getBussinessLogService().log("定时设置", "保存定时",ContextUtils.getSystemId("bs"));
		return null;
	}
	
	
	public void prepareView()throws Exception {
		prepareModel();
	}
	
	/**
	 * 查看
	 * @return
	 * @throws Exception
	 */
	@Action("job-info-view")
	public String view()throws Exception{
		return "job-info-view";
	}
	
	/**
	 * 删除CornInfo表
	 * @return
	 * @throws Exception
	 */
	@Action("job-info-deleteCornInfo")
	public String deleteCornInfo() throws Exception {
		jobInfoManager.deleteCornInfos(ids);
		addActionMessage("<font class='onSuccess'><nobr>删除成功</nobr></font>");
		ApiFactory.getBussinessLogService().log("定时设置", "删除定时器中定时任务",ContextUtils.getSystemId("bs"));
		return "job-info";
	}
	
	public void prepareSetState()throws Exception {
		prepareModel();
	}
	
	/**
	 * 设置状态
	 * @return
	 * @throws Exception
	 */
	@Action("job-info-setState")
	public String setState()throws Exception {
		addActionMessage("<font class='onSuccess'><nobr>成功设置"+jobInfoManager.setJobInfos(ids,dataState)+"条</nobr></font>");
		ApiFactory.getBussinessLogService().log("定时设置", "启用或禁用定时器",ContextUtils.getSystemId("bs"));
		return "job-info";
	}
	
	/**
	 * 验证重复
	 * @return
	 */
	@Action("job-info-validateJob")
	public String validateJob(){
		return null;
	}
	
	/**
	 * 得到所有系统树
	 * @return
	 * @throws Exception
	 */
	@Action("job-info-dataTableStandardSysTree")
	public String dataTableStandardSysTree() throws Exception {
		businessSystems= businessSystemManager.getAllSystems();
		StringBuilder tree = new StringBuilder("[ ");
		for(BusinessSystem system :businessSystems){
			tree.append(JsTreeUtils.generateJsTreeNodeNew(system.getId().toString(), "root", system.getName(),"")).append(",");
		}
		JsTreeUtils.removeLastComma(tree);
		tree.append(" ]");
		renderText(tree.toString());
		return null;
	}
	
	public TimedTask getModel() {
		return jobInfo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
	
	public Page<TimedTask> getPages() {
		return pages;
	}
	
	public Page<Timer> getCornInfos() {
		return cornInfos;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public TimedTask getJobInfo() {
		return jobInfo;
	}

	public void setJobInfo(TimedTask jobInfo) {
		this.jobInfo = jobInfo;
	}

	public TimingType getTypeEnum() {
		return typeEnum;
	}

	public void setTypeEnum(TimingType typeEnum) {
		this.typeEnum = typeEnum;
	}

	public String getEveryMonth() {
		return everyMonth;
	}

	public void setEveryMonth(String everyMonth) {
		this.everyMonth = everyMonth;
	}

	public String getEveryWeek() {
		return everyWeek;
	}

	public void setEveryWeek(String everyWeek) {
		this.everyWeek = everyWeek;
	}

	public String getEveryDate() {
		return everyDate;
	}

	public void setEveryDate(String everyDate) {
		this.everyDate = everyDate;
	}

	public String getAppointTime() {
		return appointTime;
	}

	public void setAppointTime(String appointTime) {
		this.appointTime = appointTime;
	}

	public String getAppointSet() {
		return appointSet;
	}

	public void setAppointSet(String appointSet) {
		this.appointSet = appointSet;
	}

	public DataState getDataState() {
		return dataState;
	}

	public void setDataState(DataState dataState) {
		this.dataState = dataState;
	}

}
