package com.norteksoft.wf.engine.service;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.mms.form.dao.GeneralDao;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.jdbc.JdbcSupport;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.MemCachedUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.wf.base.enumeration.ProcessProperties;
import com.norteksoft.wf.base.enumeration.ProcessState;
import com.norteksoft.wf.base.enumeration.ProcessType;
import com.norteksoft.wf.base.utils.DocumentParameterUtils;
import com.norteksoft.wf.base.utils.DocumentThreadParameters;
import com.norteksoft.wf.base.utils.Dom4jUtils;
import com.norteksoft.wf.engine.core.DefinitionXmlParse;
import com.norteksoft.wf.engine.dao.WorkflowDefinitionDao;
import com.norteksoft.wf.engine.dao.WorkflowDefinitionFileDao;
import com.norteksoft.wf.engine.dao.WorkflowDefinitionTemplateDao;
import com.norteksoft.wf.engine.dao.WorkflowDefinitionTemplateFileDao;
import com.norteksoft.wf.engine.dao.WorkflowInstanceDao;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.entity.WorkflowDefinitionFile;
import com.norteksoft.wf.engine.entity.WorkflowDefinitionTemplate;
import com.norteksoft.wf.engine.entity.WorkflowDefinitionTemplateFile;
import com.norteksoft.wf.engine.entity.WorkflowType;

@Service
@Transactional
public class WorkflowDefinitionManager {
	
	private Log log = LogFactory.getLog(WorkflowDefinitionManager.class);
	private static final String TIME_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";
    private static final String JBPM_DEFINITION_FILE_EXTENSION = ".jpdl.xml";
    private static final String KEY_OF_PROCESS_NAME_IN_XML = "wf_name";
    private static final String ENCODING_UTF_8 = "UTF-8";
    private static final String EMPTY_STRING = "";
    private static final Integer TIME_STRING_LENGTH = 19;
	private static final String DELTA_START_REGEXP = "\\[";
	private static final String DELTA_END = "]";
	private static final String WF_MANAGER_ROLE_CODE="workflowManager";//工作流管理员角色编码
	private static final String DEF_MANAGER_ROLE_CODE="defManager";//流程定义管理员编码
	
    private ProcessEngine processEngine;
    private WorkflowDefinitionDao workflowDefinitionDao;
    private WorkflowDefinitionFileDao workflowDefinitionFileDao;
    private WorkflowDefinitionTemplateDao workflowDefinitionTemplateDao;
    private GeneralDao generalDao;
    private FormViewManager formViewManager;
    private UserManager userManager;
    private WorkflowInstanceDao workflowInstanceDao;
    private WorkflowTypeManager workflowTypeManager;
    private BusinessSystemManager businessSystemManager;
    @Autowired
    private WorkflowDefinitionTemplateFileDao workflowDefinitionTemplateFileDao;
    @Autowired
    private AcsUtils acsUtils;
    
    @Autowired
    public void setWorkflowInstanceDao(WorkflowInstanceDao workflowInstanceDao) {
		this.workflowInstanceDao = workflowInstanceDao;
	}
	@Autowired
    public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
    
    @Autowired
    public void setFormViewManager(FormViewManager formManager) {
		this.formViewManager = formManager;
	}
    @Autowired
    public void setGeneralDao(GeneralDao generalDao) {
		this.generalDao = generalDao;
	}
    
    @Autowired
	public void setAcsUtils(AcsUtils acsUtils) {
		this.acsUtils = acsUtils;
	}
    @Autowired
    public void setWorkflowTypeManager(WorkflowTypeManager workflowTypeManager) {
		this.workflowTypeManager = workflowTypeManager;
	}
    @Autowired
    public void setBusinessSystemManager(
			BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}
    public WorkflowDefinitionFile getWorkflowDefinitionFile(Long wfdId){
    	return workflowDefinitionFileDao.getWfDefinitionFileByWfdId(wfdId);
    }
    
    public Long getSystemId(){
    	return ContextUtils.getSystemId();
    }
    
    public Long getCompanyId(){
    	return ContextUtils.getCompanyId();
    }
    
    public String getLoginName(){
    	return ContextUtils.getLoginName();
    }
    
    /**
     * 查询模版列表
     * @param typeId
     * @param companyId
     * @return
     */
    public List<WorkflowDefinitionTemplate> getWorkflowDefinitionTemplates(Long typeId) {
		return workflowDefinitionTemplateDao.getWorkflowDefinitionTemplates(typeId,this.getCompanyId());
	}
    /**
     * 根据类型Id得到流程
     * @param typeId
     * @param companyId
     * @return
     */
    public List<WorkflowDefinition> getWorkflowDefinitionByTypeId(Long typeId,Long companyId) {
		return workflowDefinitionDao.getWorkflowDefinition(typeId,this.getCompanyId());
	} 
    
    
    
	/**
	 * 查询模版的xml(flex 用)
	 * @param templateId
	 * @return
	 */
	public String getTemplateXml(Long templateId){
		WorkflowDefinitionTemplateFile file=workflowDefinitionTemplateFileDao.getWorkflowDefinitionTemplateFileByTemplateId(templateId);
		if(file!=null) return file.getXml();
		return null;
	}
    
    /**
     * 分页查询流程定义
     * @param page
     */
    public void getWfDefinitions(Page<WorkflowDefinition> page){
    	workflowDefinitionDao.getWfDefinitions(page, getCompanyId());
    }
    /**
     * 是否是流程定义管理员
     * @return true表示是流程定义管理员，否则则是工作流管理员。工作流管理员可以看见所有的流程
     */
    public boolean isSuperWf(){
    	Set<User> wfManager=acsUtils.getUsersByRole(ContextUtils.getSystemId(), ContextUtils.getCompanyId(), WF_MANAGER_ROLE_CODE);//工作流管理员列表
		boolean isSuperWf=false;
		for(User user:wfManager){
			if(user.getLoginName().equals(ContextUtils.getLoginName())){//如果当前用户既是流程管理员，又是流程定义管理员，则取工作流管理员权限
				return false;
			}
		}
		Set<User> defManagers=acsUtils.getUsersByRole(ContextUtils.getSystemId(), ContextUtils.getCompanyId(), DEF_MANAGER_ROLE_CODE);//流程定义管理员
		for(User user:defManagers){
			if(user.getLoginName().equals(ContextUtils.getLoginName())){
				isSuperWf=true;
				break;
			}
		}
		return isSuperWf;
    }
    public void getWfDefinitions(Page<WorkflowDefinition> page,String vertionType,String adminCode){
    	boolean isSuperWf=isSuperWf();
    	workflowDefinitionDao.getWfDefinitions(page, getCompanyId(),vertionType,adminCode,isSuperWf);
    }
    
    public void getWfDefinitions(Page<WorkflowDefinition> page, Long type) {
    	workflowDefinitionDao.getWfDefinitions(page, getCompanyId(),type);
	}
    
    public void getWfDefinitions(Page<WorkflowDefinition> page, Long type,String vertionType,String adminCode) {
    	boolean isSuperWf=isSuperWf();
    	workflowDefinitionDao.getWfDefinitions(page, getCompanyId(),type,vertionType,adminCode,isSuperWf);
	}
    
    public void getWfDefinitionsBySystemId(Page<WorkflowDefinition> page, Long systemId) {
    	workflowDefinitionDao.getWfDefinitionsBySystemId(page, getCompanyId(),systemId);
	}
    
    public void getWfDefinitionsBySystemId(Page<WorkflowDefinition> page, Long systemId,String vertionType,String adminCode) {
    	boolean isSuperWf=isSuperWf();
    	workflowDefinitionDao.getWfDefinitionsBySystemId(page, getCompanyId(),systemId,vertionType,adminCode,isSuperWf);
	}
    public void getEnableWfDefinitions(Page<WorkflowDefinition> page){
    	workflowDefinitionDao.getEnableWfDefinitions(page, getCompanyId());
    }
    
    public void getEnableWfDefinitions(Page<WorkflowDefinition> page, Long type){
    	workflowDefinitionDao.getEnableWfDefinitions(page, getCompanyId(), type);
    }
    
    @Transactional(readOnly=false)
    public void saveWorkflowDefinition(WorkflowDefinition workflowDefinition){
    	workflowDefinitionDao.save(workflowDefinition);
    }
    @Transactional(readOnly=false)
    public void saveWorkflowDefinitionFile(WorkflowDefinitionFile workflowDefinitionFile){
    	workflowDefinitionFileDao.save(workflowDefinitionFile);
    }
    
    /**
     * 根据流程定义ID查询流程定义(不包含流程定义文件)
     * @param id
     * @return
     */
    public WorkflowDefinition getWfDefinition(Long id){
    	Assert.notNull(id, "流程定义id不能为null");
    	return workflowDefinitionDao.get(id);
    }
    
    /**
     * 通过流程定义ID查询所有的环节名称
     * @param jbpmDefinitionId 
     * @return 环节名封装的集合
     */
    public List<String> getTachesByprocessId(String processId){
		return DefinitionXmlParse.getTaskNames(processId);
    }
    
    /**
     * 通过流程定义ID查询所有的环节名称
     * @param workflowDefinitionId 定义id
     * @return 环节名称封装成的集合
     */
    public List<String> getTachesByProcessDefinition(Long workflowDefinitionId){
    	WorkflowDefinition wfd = getWfDefinition(workflowDefinitionId);
    	return getTachesByprocessId(wfd.getProcessId());
    }
    
    /**
     * 查询类型为type所有活动的流程
     * @param page
     */
    public void getActiveDefinition(Page<WorkflowDefinition> page,Long type){
    	workflowDefinitionDao.getActiveDefinition(page, 
    			getCompanyId(), DataState.ENABLE,type, getSystemId());
    }
    
    /**
     * 查询所有活动的流程
     * @param page
     */
    public void getActiveDefinition(Page<WorkflowDefinition> page){
    	workflowDefinitionDao.getActiveDefinition(page, 
    			getCompanyId(), DataState.ENABLE, getSystemId());
    }
    
    
    /**
     * 根据系统查询所有活动的流程(返回List)
     */
    public List<WorkflowDefinition> getActiveDefinition(){
    	return 	workflowDefinitionDao.getActiveDefinition(
    			getCompanyId(), DataState.ENABLE);
    }
    
    /**
     * 查询所有活动的流程(返回List)
     */
    public List<WorkflowDefinition> getAllActiveDefinition(){
    	return 	workflowDefinitionDao.getActiveDefinition(
    			getCompanyId(), DataState.ENABLE);
    }
    
    /**
     * 查询所有活动的流程(返回List)（flex用）
     * 公司Id
     */
    public List<WorkflowDefinition> getActiveDefinition(Long companyId,String systemCode){
    	BusinessSystem system=businessSystemManager.getSystemBySystemCode(systemCode);
    	if(system!=null)  	return workflowDefinitionDao.getActiveDefinition(companyId, DataState.ENABLE, system.getId());
    	return new ArrayList<WorkflowDefinition>();
    }
    
    /**
     * 查询所有活动的流程(返回List)
     */
    public List<WorkflowDefinition> getActiveDefinitionsByForm(String formName){
    	return workflowDefinitionDao.getActiveDefinitionsByFrom(
    			getCompanyId(), DataState.ENABLE, formName, getSystemId());
    }
    
    public List<WorkflowDefinition> getAllDefinitionsByName(String name,Long companyId){
    	return workflowDefinitionDao.getAllDefinitionsByName(name,companyId, getSystemId());
    }
    
    /**
     * 根据流程定义ID查询流程定义(包含流程定义文件)
     * @param wfdId
     * @return
     */
    public WorkflowDefinition getWfdAndXmlFile(Long wfdId){
    	WorkflowDefinition wfd = workflowDefinitionDao.get(wfdId);
    	wfd.setWorkflowDefinitionFile(
    			workflowDefinitionFileDao.getWfDefinitionFileByWfdId(wfdId));
    	return wfd;
    }
    
    /**
     * 删除流程定义.启用过的流程定义不会被删除。
     * @param wdfIds
     * @return 删除的记录数
     */
    @Transactional(readOnly=false)
    public int deleteWfDefinitions(List<Long> wdfIds){
    	int deleteNum = 0;
    	WorkflowDefinition definition = null;
    	Integer   wfInstanceNum = 0;
    	for(int i=0;i<wdfIds.size();i++){
    		definition = this.getWfDefinition(wdfIds.get(i));
    		wfInstanceNum=workflowInstanceDao.getInstancesNumByDefId(wdfIds.get(i), getCompanyId(), definition.getSystemId());
    		if(definition.getEnable()==DataState.DRAFT
    			||(definition.getEnable()==DataState.DISABLE&&wfInstanceNum==0)){
    			workflowDefinitionDao.delete(definition);
    			workflowDefinitionFileDao.deleteDefinitionFileByWfdId(definition.getId());	
    			deleteNum++;
    		}
    	}
    	return deleteNum;
    } 
    
    
    /**
     *  根据流程定义ID查询流程定义文件（flex用）
     */
    public String getXmlByDefinitionId(Long wfdId, Long companyId){
    	return workflowDefinitionFileDao.getWfDefinitionFileByWfdId(wfdId, companyId).getDocument();
    }
    
    /**
     * 创建流程定义（flex用）
     */
    @Transactional(readOnly=false)
    public Long createWfDefinition(Long companyId, String xmlFile,String typeCode,String systemCode){
    	ThreadParameters params=new ThreadParameters(companyId);
    	ParameterUtils.setParameters(params);
    	WorkflowType type=workflowTypeManager.getWorkflowType(typeCode);
    	BusinessSystem system=businessSystemManager.getSystemBySystemCode(systemCode);
    	if(type==null) throw new RuntimeException("创建流程定义时，流程类型不能为null");
    	if(system==null) throw new RuntimeException("创建流程定义时，系统不能为null");
    	return saveWorkflowDefinition(null, companyId, xmlFile,type.getId(),system.getId());
    }
    
    /**
     * 修改流程定义（flex用）
     * @param id
     * @param xmlFile
     */
    @Transactional(readOnly=false)
    public Long updateWfDefinition(Long id, Long companyId, String xmlFile,String typeCode,String systemCode){
    	ThreadParameters params=new ThreadParameters(companyId);
    	ParameterUtils.setParameters(params);
    	WorkflowType type=workflowTypeManager.getWorkflowType(typeCode);
    	BusinessSystem system=businessSystemManager.getSystemBySystemCode(systemCode);
    	if(type==null) throw new RuntimeException("更新流程定义时，流程类型不能为null");
    	if(system==null) throw new RuntimeException("更新流程定义时，系统不能为null");
    	return saveWorkflowDefinition(id, companyId, xmlFile,type.getId(),system.getId());
    }
    
    /**
     * 修改流程定义,且及时生效
     * @param id
     * @param xmlFile
     */
    @Transactional(readOnly=false)
    public Long updateWfDefVersion(Long id, Long companyId, String xmlFile,Long type,Long systemId){ 
    	ThreadParameters params=new ThreadParameters(companyId);
    	ParameterUtils.setParameters(params);
    	log.debug("*** updatewfDefVersion 方法开始");
    	Map<String, String> props = DefinitionXmlParse.getProcessBaseInfo(xmlFile);
    	WorkflowDefinition wfd = workflowDefinitionDao.get(id);
		Integer version=wfd.getVersion();
		DataState enable=wfd.getEnable();
		setWfDefinitionBaseInfo(wfd, props);
		//还原版本和状态
		wfd.setVersion(version);
		wfd.setEnable(enable);
		workflowDefinitionDao.save(wfd);
		WorkflowDefinitionFile file = workflowDefinitionFileDao.getWfDefinitionFileByWfdId(id,companyId);
		file.setDocument(xmlFile);
		workflowDefinitionFileDao.save(file);
		if(wfd.getEnable()!=DataState.DRAFT){
			MemCachedUtils.add(wfd.getProcessId(),file.getDocument());
			
			DocumentThreadParameters parameters = new DocumentThreadParameters();
	    	DocumentParameterUtils.setParameters(parameters);
			long a = System.currentTimeMillis();
			DefinitionXmlParse.getDefinitionParseInfo(wfd.getProcessId());
			DocumentParameterUtils.clearParameter();
			long b = System.currentTimeMillis();
			System.out.println((b-a)+"---------------updateWfDefVersion");
			
		}
		log.debug("*** updatewfDefVersion 方法结束");
		return wfd.getId();
    }
    
    
    /**
     * 修改流程定义
     * @param id
     * @param xmlFile
     */
    @Transactional(readOnly=false)
    public Long updateWfDefinition(Long id, Long companyId, String xmlFile){
    	return saveWorkflowDefinition(id, companyId, xmlFile,null,getSystemId());
    }
    
    
    /**
     * 保存流程定义
     * @param id
     * @param xmlFile
     */
    @Transactional(readOnly=false)
    public Long saveWorkflowDefinition(Long id, Long companyId, String xmlFile,Long type,Long systemId){
		log.debug("*** saveWorkflowDefinition 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("id:").append(id)
			.append(", companyId:").append(companyId)
			.append(", type:").append(type)
			.append(", systemId:").append(systemId)
			.append("]").toString());
		
    	if(id == null){
    		id = createWorkflowDefinition(companyId, xmlFile, type, systemId);
    	}else{
    		id = updateWorkflowDefinition(id, companyId, xmlFile, type, systemId);
    	}
    	
    	log.debug("*** saveWorkflowDefinition 方法结束");
    	return id;
    }
    
    /* 
     * 创建一个新的流程定义
     */
    @Transactional(readOnly=false)
    private Long createWorkflowDefinition(Long companyId, String xmlFile,Long type,Long systemId){
    	log.debug("*** createWorkflowDefinition 方法开始");
    	
    	Map<String, String> props = DefinitionXmlParse.getProcessBaseInfo(xmlFile);
    	//保存流程定义信息
    	WorkflowDefinition wfd = createWorkflowDefinition(companyId, props, type, systemId);
		workflowDefinitionDao.save(wfd);
		//保存流程定义文件
		createWorkflowDefinitionFile(xmlFile, wfd.getId(), companyId);
		log.debug("*** createWorkflowDefinition 方法结束");
		return wfd.getId();
		
    }
    
    @Transactional(readOnly=false)
    private WorkflowDefinition createWorkflowDefinition(Long companyId, Map<String, String> props,Long type,Long systemId){
    	WorkflowDefinition wfd = new WorkflowDefinition();
    	setWfDefinitionBaseInfo(wfd, props);
    	wfd.setCompanyId(companyId);
    	wfd.setTypeId(type);
    	if(StringUtils.isNotEmpty(props.get(DefinitionXmlParse.SYSTEM_CODE))){
    		String systemCode=props.get(DefinitionXmlParse.SYSTEM_CODE);
    		BusinessSystem system=businessSystemManager.getSystemBySystemCode(systemCode);
    		if(system!=null)wfd.setSystemId(system.getId());
    	}
    	wfd.setCustomType(props.get(DefinitionXmlParse.CUSTOME_TYPE));
    	wfd.setProcessType(ProcessType.PREDEFINED_PROCESS);
    	wfd.setVersion(workflowDefinitionDao.generateWorkflowDefinitionVersion(
    			props.get(ProcessProperties.WF_CODE.toString()),companyId, systemId));
    	return wfd;
    }
    
    /*
     * 修改流程定义
     * 如果流程已经启用过，则为流程增加新版本
     */
    @Transactional(readOnly=false)
    private Long updateWorkflowDefinition(Long id, Long companyId, String xmlFile,Long type,Long systemId){
    	log.debug("*** updateWorkflowDefinition 方法开始");
    	Map<String, String> props = DefinitionXmlParse.getProcessBaseInfo(xmlFile);
    	WorkflowDefinition wfd = workflowDefinitionDao.get(id);
		//2. ID不为空，流程为草稿状态，直接保存
		if(wfd.getEnable()==DataState.DRAFT){
			log.debug("*** 保存为草稿");
			if(StringUtils.isNotEmpty(props.get(DefinitionXmlParse.SYSTEM_CODE))){
	    		String systemCode=props.get(DefinitionXmlParse.SYSTEM_CODE);
	    		BusinessSystem system=businessSystemManager.getSystemBySystemCode(systemCode);
	    		if(system!=null)wfd.setSystemId(system.getId());
	    	}
			wfd.setCustomType(props.get(DefinitionXmlParse.CUSTOME_TYPE));
			log.debug("*** setWfDefinitionBaseInfo开始**");
			log.debug("*** wfd="+wfd+"**props="+props);
			setWfDefinitionBaseInfo(wfd, props);
			log.debug("*** setWfDefinitionBaseInfo结束**");
			log.debug("*** workflowDefinitionDao.save开始**");
    		workflowDefinitionDao.save(wfd);
    		log.debug("*** workflowDefinitionDao.save结束**");
    		log.debug("*** workflowDefinitionFileDao.getWfDefinitionFileByWfdId***wfdId="+id);
    		WorkflowDefinitionFile file = workflowDefinitionFileDao.getWfDefinitionFileByWfdId(id,companyId);
    		log.debug("*** file***companyId="+file.getCompanyId());
    		log.debug("*** file.setDocument开始");
    		file.setDocument(xmlFile);
    		log.debug("*** file.setDocument结束");
    		log.debug("*** workflowDefinitionFileDao.save开始***file="+file);
    		workflowDefinitionFileDao.save(file);
    		log.debug("*** workflowDefinitionFileDao.save结束***file="+file);
		}else {
			//2. ID不为空，流程已经启用过，复制保存
			log.debug("*** 流程已经启用过, 增加新版本");
        	wfd = createWorkflowDefinition(companyId, props, type, systemId);
        	
    		workflowDefinitionDao.save(wfd);
    		createWorkflowDefinitionFile(xmlFile, wfd.getId(), companyId);
		}
		
		log.debug("*** updateWorkflowDefinition 方法结束");
		return wfd.getId();
    }
    
    /*
     * 创建流程定义文件
     */
    @Transactional(readOnly=false)
    private void createWorkflowDefinitionFile(String xmlFile, Long workflowDefinitionId, Long companyId){
    	log.debug("*** createWorkflowDefinitionFile 方法开始");
    	
		WorkflowDefinitionFile file = new WorkflowDefinitionFile();
		log.debug("*** setXmlFile开始***");
		file.setDocument(xmlFile);
		log.debug("*** setXmlFile结束***");
		log.debug("*** setWfDefinitionId开始***");
		file.setWfDefinitionId(workflowDefinitionId);
		log.debug("*** setWfDefinitionId结束***workflowDefinitionId="+workflowDefinitionId);
		log.debug("*** setCompanyId开始***");
		file.setCompanyId(companyId);
		log.debug("*** setCompanyId结束***companyId="+companyId);
		log.debug("*** save开始***");
		workflowDefinitionFileDao.save(file);
		log.debug("*** save结束***");
		log.debug("*** createWorkflowDefinitionFile 方法结束");
    }
    
    /*
     * 设置流程定义的基本属性
     */
    @Transactional(readOnly=false)
    private void setWfDefinitionBaseInfo(WorkflowDefinition wfd, Map<String, String> props){
    	if(StringUtils.isNotEmpty(props.get(ProcessProperties.WF_TYPE.toString()))){
    		wfd.setTypeId(Long.valueOf(props.get(ProcessProperties.WF_TYPE.toString())));
    	}
    	if(StringUtils.isNotEmpty(props.get(ProcessProperties.WF_TYPE_CODE.toString()))){
    		String typeCode=props.get(ProcessProperties.WF_TYPE_CODE.toString());
    		WorkflowType type=workflowTypeManager.getWorkflowType(typeCode);
    		if(type!=null)wfd.setTypeId(type.getId());
    	}
    	wfd.setName(props.get(KEY_OF_PROCESS_NAME_IN_XML));
    	wfd.setCode(props.get(ProcessProperties.WF_CODE.toString()));
    	wfd.setCreator(props.get(ProcessProperties.WF_CREATOR.toString()));
    	if(userManager.getUserByLoginName(wfd.getCreator())!=null){
    		wfd.setCreatorName(userManager.getUserByLoginName(wfd.getCreator()).getName());
    	}
    	wfd.setFormName(props.get(ProcessProperties.WF_FORM.toString()));
    	wfd.setFormCode(props.get(ProcessProperties.WF_FORM_CODE.toString()));
    	String formVersion = props.get(ProcessProperties.WF_FORM_VERSION.toString());
    	wfd.setFromVersion(StringUtils.isNotEmpty(formVersion)
    			? Integer.parseInt(formVersion) : null);
    	String admin = props.get(ProcessProperties.WF_ADMIN.toString());
    	if(admin != null){
    		String[] names = admin.split(DELTA_START_REGEXP);
    		wfd.setAdminName(names[0]);
        	wfd.setAdminLoginName(names[1].replace(DELTA_END, EMPTY_STRING));
    	}
    	wfd.setEnable(DataState.DRAFT);
    	String createdTime = props.get(ProcessProperties.WF_CREATED_TIME.toString());
    	if(createdTime != null && createdTime.length() == TIME_STRING_LENGTH){
    		wfd.setCreatedTime(stringToDate(createdTime));
    	}
    	if(StringUtils.isNotEmpty(props.get(DefinitionXmlParse.SYSTEM_CODE))){
    		String systemCode=props.get(DefinitionXmlParse.SYSTEM_CODE);
    		BusinessSystem system=businessSystemManager.getSystemBySystemCode(systemCode);
    		if(system!=null)wfd.setSystemId(system.getId());
    	}
    	wfd.setCustomType(props.get(DefinitionXmlParse.CUSTOME_TYPE));
    }
    
    /**
     * 流程启用与禁用
     * @param standardXml
     * @param extendXml
     * @return
     * @throws UnsupportedEncodingException 
     */
    @Transactional(readOnly=false)
    public String deployProcess(Long wfdId) throws UnsupportedEncodingException{
    	log.debug("*** deployProcess 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("wfdId:").append(wfdId)
			.append("]").toString());
    	String message = "";
    	WorkflowDefinition wfd = workflowDefinitionDao.get(wfdId);
    	//如果流程为启用状态，设置为禁用 ,如果是禁用状态，设置为启用
    	if(wfd.getEnable()==DataState.ENABLE){
    		log.debug("*** 启用 -> 禁用");
    		wfd.setEnable(DataState.DISABLE);
    		message = "启用 -> 禁用";
    	}else if(wfd.getEnable()==DataState.DISABLE){
    		log.debug("*** 禁用 -> 启用");
    		//disableOtherProcess(wfd);
    		wfd.setEnable(DataState.ENABLE);
    		message = "禁用 -> 启用";
    	}else{
    		if(ProcessType.PREDEFINED_PROCESS.equals(wfd.getProcessType())){
    			log.debug("*** 草稿 -> 启用");
    			//如果是草稿状态，部署流程并设置为启用
    			WorkflowDefinitionFile file = workflowDefinitionFileDao.getWfDefinitionFileByWfdId(wfdId);
    			log.debug("file:"+file);
				try {
					String processKey  = processEngine.getRepositoryService().createDeployment()
					.addResourceFromInputStream(wfd.getName() + JBPM_DEFINITION_FILE_EXTENSION, 
							new ByteArrayInputStream(DefinitionXmlParse.getStandardXml4Jbpm(file.getDocument(),wfd.getId()).getBytes(ENCODING_UTF_8)))
							.deploy();
					ProcessDefinition pd = processEngine.getRepositoryService()
					.createProcessDefinitionQuery().deploymentId(processKey).uniqueResult();
					//将流程定义文件放入cache
					MemCachedUtils.add(pd.getId(), file.getDocument());
					wfd.setProcessId(pd.getId());
					DocumentThreadParameters parameters = new DocumentThreadParameters();
			    	DocumentParameterUtils.setParameters(parameters);
//					MemCachedUtils.add("document="+wfd.getProcessId(), Dom4jUtils.getDocument(file.getDocument()));
					DefinitionXmlParse.getDefinitionParseInfo(wfd.getProcessId());
					DocumentParameterUtils.clearParameter();
				} catch (Exception e) {
					log.debug(e);
					log.debug("jbpm部署异常",e);
				}
    		}else{
    			wfd.setProcessId("workflow_"+wfd.getId()+"_1");
    		}
    		//disableOtherProcess(wfd);
			wfd.setEnable(DataState.ENABLE);
			saveWorkflowDefinition(wfd);
			message = "草稿 -> 启用";
    	}
    	
    	log.debug("*** deployProcess 方法结束");
    	return message;
	}
    
    /**
     * 获得最新版本的流程定义
     * @param name
     * @param companyId
     * @return
     */
    public WorkflowDefinition getLatestVersion(String name, Long companyId){
    	return workflowDefinitionDao.getLatestVersion(name, companyId, getSystemId());
    }
    
    /**
     * 获得最新版本的流程定义
     * @param name
     * @param companyId
     * @return
     */
    public WorkflowDefinition getEnabledHighestVersionWorkflowDefinition(String code){
    	if(StringUtils.isEmpty(code)) throw new RuntimeException("没有给定查询最新版本流程定义的查询条件： 流程定义编号. ");
    	Assert.notNull(getSystemId(), "systemId不能为null");
    	Assert.notNull(getCompanyId(), "companyId不能为null");
    	return workflowDefinitionDao.getEnabledHighestVersionWorkflowDefinition(code, getCompanyId(), getSystemId());
    }
    /**
     * 获得最新版本的流程定义
     * @param name
     * @param companyId
     * @return
     */
    public WorkflowDefinition getEnabledHighestVersionWorkflowDefinitionBySystem(String code,Long systemId){
    	if(StringUtils.isEmpty(code)) throw new RuntimeException("没有给定查询最新版本流程定义的查询条件： 流程定义编号. ");
    	Assert.notNull(systemId, "systemId不能为null");
    	Assert.notNull(getCompanyId(), "companyId不能为null");
    	return workflowDefinitionDao.getEnabledHighestVersionWorkflowDefinition(code, getCompanyId(), systemId);
    }
    /**
     * 获得最新版本的流程定义
     * @param name
     * @param companyId
     * @return
     */
    public WorkflowDefinition getEnabledHighestVersionWorkflowDefinition(String code,Long companyId){
    	return workflowDefinitionDao.getEnabledHighestVersionWorkflowDefinition(code, companyId, getSystemId());
    }
    
    /**
     * 初始化系统中的所有流程定义，仅供Listener调用
     */
    @Transactional(readOnly=false)
    public void initWorkflowDefinition(){
    	List<Object[]> wfdAndFile =  workflowDefinitionDao.getAllDefinitionAndFile(getSystemId());
    	for(Object[] objs : wfdAndFile){
    		WorkflowDefinition wfd = (WorkflowDefinition) objs[0];
    		WorkflowDefinitionFile  wfdf = (WorkflowDefinitionFile) objs[1];
    		MemCachedUtils.add(wfd.getProcessId(), wfdf.getDocument());
    	}
    }
    
    /**
     * 初始化所有的流程定义，仅供Listener调用
     */
    @Transactional(readOnly=false)
    public void initAllWorkflowDefinition(){
    	DocumentThreadParameters parameters = new DocumentThreadParameters();
    	DocumentParameterUtils.setParameters(parameters);
    	List<Object[]> wfdAndFile =  workflowDefinitionDao.getAllDefinitionAndFile();
    	if(PropUtils.getProp("project.model")==null||PropUtils.getProp("project.model").equals("product.model")){//默认是产品模式，或配置的是产品模式时
    		int i=0;
    		for(Object[] objs : wfdAndFile){
    			WorkflowDefinition wfd = (WorkflowDefinition) objs[0];
    			WorkflowDefinitionFile  wfdf = (WorkflowDefinitionFile) objs[1];
    			MemCachedUtils.add(wfd.getProcessId(), wfdf.getDocument());
				if(wfd.getEnable()==DataState.ENABLE){
					System.out.println(i+"=正在加载流程【"+wfd.getName()+"】......");
					DefinitionXmlParse.getDefinitionParseInfo(wfd.getProcessId());
					DocumentParameterUtils.clearParameter();
					i++;
				}
    		}
    	}else{//开发模式时
    		for(Object[] objs : wfdAndFile){
    			WorkflowDefinition wfd = (WorkflowDefinition) objs[0];
    			WorkflowDefinitionFile  wfdf = (WorkflowDefinitionFile) objs[1];
    			MemCachedUtils.add(wfd.getProcessId(), wfdf.getDocument());
    		}
    	}
    }
    
    /**
     * 通过流程定义的Key和version查询WorkflowDefinition
     * @param key
     * @param version
     * @return
     */
    public WorkflowDefinition getWorkflowDefinitionByProcessId(String processId){
    	return workflowDefinitionDao.findUniqueNoCompanyCondition("from WorkflowDefinition def where def.processId=? ", processId);
    }
    @Autowired
    public void setProcessEngine(ProcessEngine processEngine) {
		this.processEngine = processEngine;
	}

    @Autowired
	public void setWorkflowDefinitionDao(WorkflowDefinitionDao workflowDefinitionDao) {
		this.workflowDefinitionDao = workflowDefinitionDao;
	}
    
    @Autowired
    public void setWorkflowDefinitionFileDao(
			WorkflowDefinitionFileDao workflowDefinitionFileDao) {
		this.workflowDefinitionFileDao = workflowDefinitionFileDao;
	}
    
    @Autowired
    public void setWorkflowDefinitionTemplateDao(
			WorkflowDefinitionTemplateDao workflowDefinitionTemplateDao) {
		this.workflowDefinitionTemplateDao = workflowDefinitionTemplateDao;
	}

	/**
     * 将字符装换成日期类型
     * @param source "yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static Date stringToDate(String source){
    	SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT_STRING);
    	try {
			return dateFormat.parse(source);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
    }

	public List<String> getTaskNames(String processDefinitionId) {
		if(StringUtils.isEmpty(processDefinitionId)||"0".equals(processDefinitionId))return new ArrayList<String>();
		return DefinitionXmlParse.getTaskNames(processDefinitionId);
	}
	public List<Long> getList(String ids){
		String[] dids=ids.split(",");
		List<Long> id=new ArrayList<Long>();
		for(int i=0;i<dids.length;i++){
			id.add(Long.parseLong(dids[i]));
		}
		return id;
	}

	public WorkflowDefinitionTemplate getWorkflowDefinitionTemplate(
			Long templateId) {
		return workflowDefinitionTemplateDao.get(templateId);
	}

	public Integer generateWorkflowDefinitionVersion(String name) {
		return workflowDefinitionDao.generateWorkflowDefinitionVersion(name, getCompanyId(), getSystemId());
	}

	/**
	 * 流程监控
	 */
	public void monitor(Page<Object> page,WorkflowDefinition definition) {
		searchMonitor(page,definition,null);
	}
	
	/**
	 * 流程监控
	 */
	public void monitorDefinition(Page<Object> page,Long typeId,String defCode) {
		searchManagerMonitor(page,typeId,defCode,null);
	}
	
	public void searchManagerMonitor(Page<Object> page,Long typeId,String defCode,List<ListColumn> fields){
		monitorWorkflowDefinition(page,typeId,defCode,fields);
	}
	
	/**
	 * 流程监控(查询)
	 */
	public void searchMonitor(Page<Object> page,
			WorkflowDefinition definition,
			List<ListColumn> fields){
		FormView form = formViewManager.getCurrentFormViewByCodeAndVersion(definition.getFormCode(), definition.getFromVersion());
//		if(form.isStandardForm()){
//			monitorStandardWorkflowDefinition(page,definition,form,fields);
//		}else{
			monitorDefaultWorkflowDefinition(page,definition,form,fields);
//		}
	}
	
	
	private void monitorDefaultWorkflowDefinition(Page<Object> page,
			WorkflowDefinition definition, FormView form, List<ListColumn> searchField) {
		StringBuilder sql = new StringBuilder(
				);
		List<Object> objs = new ArrayList<Object>();
		sql.append(" from ").append(" WorkflowInstance wi ")
			.append(" where  wi.processState<>? and wi.workflowDefinitionId=?");
		sql.append("  order by wi.submitTime desc ");
		objs.add(ProcessState.UNSUBMIT);
		objs.add(definition.getId());
		generalDao.searchPageByHql(page, sql.toString(),objs.toArray());
	}
	
	@SuppressWarnings("deprecation")
	public void monitorWorkflowDefinition(Page<Object> page,
			Long typeId, String defCode,List<ListColumn> searchField) {
		StringBuilder hql = new StringBuilder("select wi ");
		List<Object> objs = new ArrayList<Object>();
		objs.add(ProcessState.UNSUBMIT);
		boolean isSuperWf=isSuperWf();
		if(isSuperWf){//是否是流程定义管理员
			hql.append(" from ").append(" WorkflowInstance wi , WorkflowDefinition wfd  ").append("where wi.processState<>? and wi.workflowDefinitionId=wfd.id and wfd.adminLoginName=? ");
			objs.add(getLoginName());
		}else{
			hql.append(" from ").append(" WorkflowInstance wi , WorkflowDefinition wfd  ").append("where wi.processState<>? and wi.workflowDefinitionId=wfd.id ");
		}
		if(typeId!=null && typeId.intValue() != 0){
			hql.append("and wi.typeId = ? ");
			objs.add(typeId);
		}
		if(StringUtils.isNotEmpty(defCode)){
			hql.append("and wi.processCode=? ");
			objs.add(defCode);
		}
		hql.append("and wi.companyId = ? ");
		objs.add(getCompanyId());
		if(searchField!=null && !searchField.isEmpty()){
			String temp2 = " and wi.";
			for(ListColumn field:searchField){
				hql.append(temp2).append(field.getTableColumn().getName()).append(" ").append(field.getTableColumn().getOperate()).append(" ?");
				try {
					if(DataType.DATE.toString().equalsIgnoreCase(field.getTableColumn().getDataType().getEnumName())){
						objs.add(JdbcSupport.SIMPLEDATEFORMAT1.parse(field.getTableColumn().getSearchValue()));	
					}else if(DataType.TIME.toString().equalsIgnoreCase(field.getTableColumn().getDataType().getEnumName())){
						objs.add(JdbcSupport.SIMPLEDATEFORMAT2.parse(field.getTableColumn().getSearchValue()));	
					}else if(DataType.AMOUNT.toString().equalsIgnoreCase(field.getTableColumn().getDataType().getEnumName())){
						objs.add(Double.valueOf(field.getTableColumn().getSearchValue()));	
					}else if(DataType.NUMBER.toString().equalsIgnoreCase(field.getTableColumn().getDataType().getEnumName())){
						objs.add(Integer.valueOf(field.getTableColumn().getSearchValue()));	
					}else{
						if(field.getTableColumn().getOperate().equals("like")||field.getTableColumn().getOperate().equals("not like")){
							objs.add("%"+field.getTableColumn().getSearchValue()+"%");
						}else{
							if(field.getTableColumn().getName().equals("processState")){
								objs.add(ProcessState.valueOf((short)Integer.parseInt(field.getTableColumn().getSearchValue())));
							}else{
								objs.add(field.getTableColumn().getSearchValue());
							}
						}
					}
				} catch (NumberFormatException e) {
					log.debug("字段:'"+field.getTableColumn().getAlias()+"'的searchValue不是数字格式。");
					throw new RuntimeException(e);
				} catch (ParseException e) {
					log.debug("字段:'"+field.getTableColumn().getAlias()+"'的searchValue不是日期格式。");
					throw new RuntimeException(e);
				}
			}
		}
		hql.append(" order by wi.submitTime desc ");
		generalDao.searchPageByHql(page, hql.toString(),objs.toArray());
	}
	
	public List<Long> getAllDefinitionIdNotDraft(WorkflowDefinition definition) {
		return workflowDefinitionDao.getAllDefinitionIdNotDraft(definition);
	}
	
	public List<WorkflowDefinition> getAllEnableDefinitionsByformCodeAndVersion(String code, Integer version) {
		return workflowDefinitionDao.getAllEnableDefinitonsByFormCodeAndVersion(code, version);
	}
	
	public List<WorkflowDefinition> getCommonEnableDefinitionsByformCodeAndVersion(String code, Integer version) {
		return workflowDefinitionDao.getCommonEnableDefinitonsByFormCodeAndVersion(code, version);
	}
	
	public WorkflowDefinition getEnabledWorkflowDefinitionByCodeAndVersion(String definitionCode,
			Integer definitionVersion,Long companyId){
		return workflowDefinitionDao.getEnabledWorkflowDefinitionByCodeAndVersion(definitionCode,definitionVersion,companyId);
	}
	
	public WorkflowDefinition getWorkflowDefinitionByCodeAndVersion(String definitionCode,
			Integer definitionVersion,Long companyId,Long systemId){
		if(StringUtils.isEmpty(definitionCode)) throw new RuntimeException("没有给定查询流程定义的查询条件：流程定义编号");
		if(definitionVersion == null) throw new RuntimeException("没有给定查询流程定义的查询条件：流程定义版本号");
		if(companyId == null) throw new RuntimeException("没有给定查询流程定义的查询条件：公司ID");
		if(systemId == null) throw new RuntimeException("没有给定查询流程定义的查询条件：系统ID");
		return workflowDefinitionDao.getWorkflowDefinitionByCodeAndVersion(definitionCode,definitionVersion,companyId,systemId);
	}
	public WorkflowDefinition getWorkflowDefinitionByCodeAndVersion(String definitionCode,
			Integer definitionVersion,Long companyId,boolean isSuperWf){
		return workflowDefinitionDao.getWorkflowDefinitionByCodeAndVersion(definitionCode,definitionVersion,companyId,isSuperWf);
	}
	
	public List<WorkflowDefinition> getWfDefinitionsByType(Long companyId,Long typeId){
		return workflowDefinitionDao.getWfDefinitionsByType(companyId,typeId);
	}
	
	public List<String> getWfDefinitionCodesByType(Long companyId,Long typeId){
		return workflowDefinitionDao.getWfDefinitionCodesByType(companyId,typeId);
	}
	public List<WorkflowDefinition> getWfDefinitionsByCode(String code){
		if(StringUtils.isEmpty(code)) throw new RuntimeException("没有给定查询流程定义的查询条件：流程编号");
		return workflowDefinitionDao.getWfDefinitionsByCode(getCompanyId(), code);
	}
	
	public WorkflowDefinitionFile getWfDefinitionFileByWfdId(Long wfdId, Long companyId){
		return workflowDefinitionFileDao.getWfDefinitionFileByWfdId(wfdId, companyId);
	}
	public List<WorkflowDefinition> getWfDefinitions(Long companyId,String systemIds){
		return workflowDefinitionDao.getWfDefinitions(companyId, systemIds);
	}
	
	public void saveWfBasic(WorkflowDefinition definition){
		workflowDefinitionDao.save(definition);
		try{
			WorkflowDefinitionFile defFile=workflowDefinitionFileDao.getWfDefinitionFileByWfdId(definition.getId());
			if(defFile!=null){
				Document document=Dom4jUtils.getDocument(defFile.getDocument());
				Element root = document.getRootElement();
		        
		        Element basePropElement = Dom4jUtils.getElementByPath(root, "extend:basic-properties");
		        Element propElement = null;
				List<Element> tableList = document.selectNodes("process");
				Iterator it = tableList.iterator();
				while(it.hasNext()){//只会循环一次
					Element element = (Element)it.next();
					Attribute attr=element.attribute("name");
					attr.setValue(definition.getName());
				}
				
				propElement = Dom4jUtils.getSubElementByName(basePropElement, "admin");
				propElement.setText(definition.getAdminName()+"["+definition.getAdminLoginName()+"]");
				
				propElement = Dom4jUtils.getSubElementByName(basePropElement, "process-type-code");
				WorkflowType type=workflowTypeManager.getWorkflowType(definition.getTypeId());
				if(type!=null)propElement.setText(type.getCode());
					
				propElement = Dom4jUtils.getSubElementByName(basePropElement, "process-type-name");
				if(type!=null)propElement.setText(type.getName());
				
				propElement = Dom4jUtils.getSubElementByName(basePropElement, "system-name");
				BusinessSystem system=businessSystemManager.getBusiness(definition.getSystemId());
				if(system!=null)propElement.setText(system.getName());
				
				propElement = Dom4jUtils.getSubElementByName(basePropElement, "system-code");
				if(system!=null)propElement.setText(system.getCode());
				
				propElement = Dom4jUtils.getSubElementByName(basePropElement, "custom-type");
				propElement.setText(definition.getCustomType());
				defFile.setDocument(document.asXML());
				workflowDefinitionFileDao.save(defFile);
			}
		}catch (Exception e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}
		
	}
	
	/**
	 * 根据流程名称模糊查询某类别下的流程
	 * @param companyId
	 * @param typeId
	 * @return
	 */
	public List<WorkflowDefinition> getWfDefinitionsByName(Long companyId, Long typeId,String name){
		return workflowDefinitionDao.getWfDefinitionsByName(companyId, typeId, name);
	}
	
}
