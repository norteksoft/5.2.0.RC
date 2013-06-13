package com.norteksoft.wf.engine.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.base.enumeration.ProcessType;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;

@Repository
public class WorkflowDefinitionDao extends HibernateDao<WorkflowDefinition, Long>{
	
	public void getActiveDefinition(Page<WorkflowDefinition> page, Long companyId, DataState enable,Long type, Long systemId){
		 this.searchPageByHql(page, "from WorkflowDefinition wfd where wfd.companyId = ? and wfd.enable = ? and wfd.typeId=? and wfd.systemId=? order by name,version desc", 
				 companyId, enable,type, systemId);
	}
	/**
	 * 查询某种状态的流程定义
	 * @param page 页面
	 * @param companyId 公司id
	 * @param enable 启用状态
	 * @param systemId 系统id
	 */
	public void getActiveDefinition(Page<WorkflowDefinition> page, Long companyId, DataState enable, Long systemId){
		 this.searchPageByHql(page, "from WorkflowDefinition wfd where wfd.companyId = ? and wfd.enable = ? and wfd.systemId=? order by name,version desc", 
				 companyId, enable, systemId);
	}
	
	public List<Object[]> getAllDefinitionAndFile(Long systemId){
		return find("select wfd, wfdf from WorkflowDefinition wfd, WorkflowDefinitionFile wfdf " +
				"where wfd.id=wfdf.wfDefinitionId and wfd.systemId=? and wfd.enable<>2 order by wfd.version desc", systemId);
	}
	public List<Object[]> getAllDefinitionAndFile(){
		return findNoCompanyCondition("select wfd, wfdf from WorkflowDefinition wfd, WorkflowDefinitionFile wfdf " +
				"where wfd.id=wfdf.wfDefinitionId and wfd.enable<>2 order by wfd.version desc");
	}

	/**
	 * 查询某种状态的流程定义
	 * @param companyId 公司id
	 * @param enable 启用状态
	 * @param systemId 系统id
	 */
	public List<WorkflowDefinition> getActiveDefinition(Long companyId, DataState enable, Long systemId) {
		return this.find("from WorkflowDefinition wfd where wfd.companyId = ? and wfd.enable = ? and wfd.systemId=? and wfd.processType=? order by name,version desc", 
				 companyId, enable, systemId,ProcessType.PREDEFINED_PROCESS);
	}
	
	/**
	 * 查询某种状态的流程定义
	 * @param companyId 公司id
	 * @param enable 启用状态
	 * @param systemId 系统id
	 */
	public List<WorkflowDefinition> getActiveDefinition(Long companyId, DataState enable) {
		return this.find("from WorkflowDefinition wfd where wfd.companyId = ? and wfd.enable = ? and wfd.processType=? order by name,version desc", 
				 companyId, enable, ProcessType.PREDEFINED_PROCESS);
	}
	
	public List<WorkflowDefinition> getActiveDefinitionsByFrom(Long companyId,
			DataState enable, String formName, Long systemId) {
		return this.find("from WorkflowDefinition wfd where wfd.companyId = ? and wfd.formName=? and wfd.enable = ? and wfd.systemId=? order by wfd.version desc", 
				 companyId, formName, enable, systemId);
	}

	public List<WorkflowDefinition> getAllDefinitionsByName(String name, Long companyId, Long systemId) {
		return find("from WorkflowDefinition wfd where wfd.companyId = ? and wfd.name=? and wfd.systemId=? order by wfd.version desc", companyId, name, systemId);
	} 
	
	public List<WorkflowDefinition> getAllEnableDefinitonsByFormCodeAndVersion(String formCode, Integer version) {
		return find("from WorkflowDefinition wfd where wfd.companyId = ? and wfd.systemId = ? and wfd.enable = 0 and wfd.formCode = ? and wfd.fromVersion = ? order by wfd.version desc",
				ContextUtils.getCompanyId(), ContextUtils.getSystemId(), formCode, version);
	}
	
	/**
	 * mms自定义系统中新建表单时根据流程定义编号和版本号取流程实例
	 * @param formCode
	 * @param version
	 * @return
	 */
	public List<WorkflowDefinition> getCommonEnableDefinitonsByFormCodeAndVersion(String formCode, Integer version) {
		return find("from WorkflowDefinition wfd where wfd.companyId = ? and wfd.systemId = ? and wfd.enable = ? and wfd.formCode = ? and wfd.fromVersion = ? order by wfd.version desc",
				ContextUtils.getCompanyId(), ContextUtils.getSystemId("mms"),DataState.ENABLE, formCode, version);
	}
	
	/**
	 * 得到最新版本的表单
	 * @param name 表单名
	 * @param companyId 公司id
	 * @param systemId 系统id
	 * @return 最新版本的流程定义
	 */
	public WorkflowDefinition getLatestVersion(String code, Long companyId, Long systemId){
		 List<WorkflowDefinition> wds = find("from WorkflowDefinition wfd where wfd.companyId = ? and wfd.code=? and wfd.systemId=? order by wfd.version desc", 
				 companyId, code, systemId);
		 if(wds.isEmpty()){
			 return null;
		 }else{
			 return wds.get(0);
		 }
	} 
	/**
	 * 获取启用的最新版本
	 * @param code 流程编号
	 * @param companyId 公司id
	 * @param systemId 系统id
	 * @return 启用的最新版本
	 */
	public WorkflowDefinition getEnabledHighestVersionWorkflowDefinition(String code, Long companyId, Long systemId){
		 List<WorkflowDefinition> wds = find("from WorkflowDefinition wfd where wfd.companyId = ? and wfd.code=? and wfd.systemId=? and wfd.enable=0 order by wfd.version desc", 
				 companyId, code, systemId);
		 if(wds.isEmpty()){
			 return null;
		 }else{
			 return wds.get(0);
		 }
	} 
	
	public List<WorkflowDefinition> getWfDefinitionsByType(Long companyId, Long typeId){
		return find("from WorkflowDefinition wfd where wfd.companyId = ? and wfd.typeId=? and wfd.enable=? order by wfd.version desc", 
				companyId, typeId, DataState.ENABLE);
	}
	
	
	
	public void getWfDefinitions(Page<WorkflowDefinition> page,Long companyId){
		searchPageByHql(page, "from WorkflowDefinition wfd where wfd.companyId = ? order by name,version desc",companyId);
	}
	public void getWfDefinitions(Page<WorkflowDefinition> page,Long companyId,String enableOrDisable,String adminCode,boolean  isSuperWf){
		if(isSuperWf){//是否是流程定义管理员
			if(enableOrDisable.equals("UNABLE")){
				searchPageByHql(page, "from WorkflowDefinition wfd where wfd.companyId = ? and wfd.enable=? and wfd.adminLoginName=? order by name,version desc",companyId,DataState.DISABLE,adminCode);
			}else{
				searchPageByHql(page, "from WorkflowDefinition wfd where wfd.companyId = ? and (wfd.enable=? or wfd.enable=? ) and wfd.adminLoginName=? order by enable asc ,name,version desc",companyId,DataState.ENABLE,DataState.DRAFT,adminCode);
			}
		}else{
			if(enableOrDisable.equals("UNABLE")){
				searchPageByHql(page, "from WorkflowDefinition wfd where wfd.companyId = ? and wfd.enable=? order by name,version desc",companyId,DataState.DISABLE);
			}else{
				searchPageByHql(page, "from WorkflowDefinition wfd where wfd.companyId = ? and (wfd.enable=? or wfd.enable=? ) order by enable asc ,name,version desc",companyId,DataState.ENABLE,DataState.DRAFT);
			}
		}
	}
	
	public void getWfDefinitions(Page<WorkflowDefinition> page,Long companyId,Long type){
		searchPageByHql(page, "from WorkflowDefinition wfd where wfd.companyId = ? and wfd.typeId=?  order by name,version desc" ,companyId,type);
	}
	
	public void getWfDefinitions(Page<WorkflowDefinition> page,Long companyId,Long type,String enableOrDisable,String adminCode,boolean  isSuperWf){
		if(isSuperWf){//是否是流程定义管理员
			if(enableOrDisable.equals("UNABLE")){
				searchPageByHql(page, "from WorkflowDefinition wfd where wfd.companyId = ? and wfd.typeId=?  and wfd.enable=? and wfd.adminLoginName=? order by name,version desc" ,companyId,type,DataState.DISABLE,adminCode);
			}else{
				searchPageByHql(page, "from WorkflowDefinition wfd where wfd.companyId = ? and wfd.typeId=?  and (wfd.enable=? or wfd.enable=? ) and wfd.adminLoginName=? order by enable asc ,name,version desc" ,companyId,type,DataState.ENABLE,DataState.DRAFT,adminCode);
			}
		}else{
			if(enableOrDisable.equals("UNABLE")){
				searchPageByHql(page, "from WorkflowDefinition wfd where wfd.companyId = ? and wfd.typeId=?  and wfd.enable=? order by name,version desc" ,companyId,type,DataState.DISABLE);
			}else{
				searchPageByHql(page, "from WorkflowDefinition wfd where wfd.companyId = ? and wfd.typeId=?  and (wfd.enable=? or wfd.enable=? ) order by enable asc ,name,version desc" ,companyId,type,DataState.ENABLE,DataState.DRAFT);
			}
			
		}
	}
	
	public void getWfDefinitionsBySystemId(Page<WorkflowDefinition> page,Long companyId,Long systemId){
		searchPageByHql(page, "from WorkflowDefinition wfd where wfd.companyId = ? and wfd.systemId=?  order by name,version desc" ,companyId,systemId);
	}

	public void getWfDefinitionsBySystemId(Page<WorkflowDefinition> page,Long companyId,Long systemId,String enableOrDisable,String adminCode,boolean isSuperWf){
		if(isSuperWf){//是否是流程定义管理员
			if(enableOrDisable.equals("UNABLE")){
				searchPageByHql(page, "from WorkflowDefinition wfd where wfd.companyId = ? and wfd.systemId=? and wfd.enable=? and wfd.adminLoginName=? order by name,version desc" ,companyId,systemId,DataState.DISABLE,adminCode);
			}else{
				searchPageByHql(page, "from WorkflowDefinition wfd where wfd.companyId = ? and wfd.systemId=? and (wfd.enable=? or wfd.enable=? ) and wfd.adminLoginName=? order by enable asc , name,version desc" ,companyId,systemId,DataState.ENABLE,DataState.DRAFT,adminCode);
			}
		}else{
			if(enableOrDisable.equals("UNABLE")){
				searchPageByHql(page, "from WorkflowDefinition wfd where wfd.companyId = ? and wfd.systemId=?  and wfd.enable=? order by name,version desc" ,companyId,systemId,DataState.DISABLE);
			}else{
				searchPageByHql(page, "from WorkflowDefinition wfd where wfd.companyId = ? and wfd.systemId=? and (wfd.enable=? or wfd.enable=? ) order by enable asc , name,version desc" ,companyId,systemId,DataState.ENABLE,DataState.DRAFT);
			}
		}
	}
	
	
	public void getEnableWfDefinitions(Page<WorkflowDefinition> page,Long companyId){
		searchPageByHql(page, "from WorkflowDefinition wfd where wfd.companyId = ? and wfd.enable=?  and wfd.systemId=? order by name,version desc",companyId, DataState.ENABLE,ContextUtils.getSystemId());
	}
	
	public void getEnableWfDefinitions(Page<WorkflowDefinition> page,Long companyId,Long type){
		searchPageByHql(page, "from WorkflowDefinition wfd where wfd.companyId = ? and wfd.typeId=? and wfd.enable=? and wfd.systemId=?  order by name,version desc",
				companyId,type, DataState.ENABLE,ContextUtils.getSystemId());
	}
	
	/**
	 * 生成版本号
	 * @param code 流程名
	 * @param companyId 公司id
	 * @param systemId 系统id
	 * @return 新的版本号
	 */
	public int generateWorkflowDefinitionVersion(String code, Long companyId, Long systemId){
		WorkflowDefinition wd = getLatestVersion(code,companyId, systemId);
		if(wd==null){
			return 1;
		}else{
			return wd.getVersion()+1;
		}
	}
	

	public List<WorkflowDefinition> getWorkflowDefinition(Long typeId, Long companyId) {
			return this.find("from WorkflowDefinition ft where ft.companyId = ? and ft.typeId=?",companyId,typeId);
	}
	
	/**
	 * 禁用其他的流程定义
	 * @param wfd 流程定义
	 */
	public void disableOtherProcess(WorkflowDefinition wfd){
		String hql = "update WorkflowDefinition f set f.enable=? where f.name=? and f.enable=? and f.companyId=? and f.systemId=?";
		this.batchExecute(hql, DataState.DISABLE,wfd.getName(),DataState.ENABLE,wfd.getCompanyId(),wfd.getSystemId());
	}

	/**
	 * 查出非草稿状态的所有和给定流程名，表单名相同的定义id
	 * @param definition 流程定义
	 * @return 定义id集合
	 */
	public List<Long> getAllDefinitionIdNotDraft(WorkflowDefinition definition) {
		return this.find("select ft.id from WorkflowDefinition ft where ft.companyId = ? and ft.systemId=? and ft.name=? and ft.formName=? and ft.enable <>?"
				,definition.getCompanyId(),definition.getSystemId(),definition.getName(),definition.getFormName(),DataState.DRAFT);
	}
	
	public WorkflowDefinition getEnabledWorkflowDefinitionByCodeAndVersion(String definitionCode,
			Integer definitionVersion,Long companyId){
		return this.findUnique("from WorkflowDefinition ft where ft.code=? and ft.version=? and ft.companyId = ? and ft.enable=0", definitionCode,definitionVersion,companyId);
	}
	
	public List<String> getWfDefinitionCodesByType(Long companyId,Long typeId){
		String hql = "select distinct t.code from WorkflowDefinition t where t.companyId=? and t.typeId=?";
		return this.find(hql, companyId,typeId);
	}
	/**
	 * 根据编码查询流程定义集合
	 * @param companyId
	 * @param code
	 * @return
	 */
	public List<WorkflowDefinition> getWfDefinitionsByCode(Long companyId, String code){
		return find("from WorkflowDefinition wfd where wfd.companyId = ? and wfd.code=? and wfd.enable=? order by wfd.version desc", 
				companyId, code, DataState.ENABLE);
	}
	
	public WorkflowDefinition getWorkflowDefinitionByCodeAndVersion(String definitionCode,
			Integer definitionVersion,Long companyId,Long systemId){
		return this.findUnique("from WorkflowDefinition ft where ft.code=? and ft.version=? and ft.companyId = ? and ft.systemId=? ", definitionCode,definitionVersion,companyId,systemId);
	}
	public WorkflowDefinition getWorkflowDefinitionByCodeAndVersion(String definitionCode,
			Integer definitionVersion,Long companyId,boolean isSuperWf){
		if(isSuperWf){//是流程定义管理员
			return this.findUnique("from WorkflowDefinition ft where ft.code=? and ft.version=? and ft.companyId = ?  and ft.adminLoginName=?", definitionCode,definitionVersion,companyId,ContextUtils.getLoginName());
		}else{
			return this.findUnique("from WorkflowDefinition ft where ft.code=? and ft.version=? and ft.companyId = ? ", definitionCode,definitionVersion,companyId);
		}
	}
	public List<WorkflowDefinition> getWfDefinitions(Long companyId,String systemIds){
		StringBuilder hql=new StringBuilder("from WorkflowDefinition wfd where wfd.companyId = ? and (wfd.enable=? or wfd.enable=?) ");
		Object[] values=new Object[3];
		values[0]=companyId;
		values[1]=DataState.ENABLE;
		values[2]=DataState.DRAFT;
		if(StringUtils.isNotEmpty(systemIds)){
			String[] ids=systemIds.split(",");
			values=new Object[3+ids.length];
			values[0]=companyId;
			values[1]=DataState.ENABLE;
			values[2]=DataState.DRAFT;
			if(ids.length>0){
				hql.append("and (");
			}
			for(int i=0;i<ids.length;i++){
				hql.append("wfd.systemId=? ");
				if(i<ids.length-1){
					hql.append(" or ");
				}
				if(i==ids.length-1)hql.append(")");
				values[3+i]=Long.parseLong(ids[i]);
			}
		}
		hql.append(" order by wfd.code,wfd.version desc");
		return find(hql.toString(),values);
	}
	
	public WorkflowDefinition getWorkflowDefinitionByProcessId(String processId){
		return this.findUnique("from WorkflowDefinition ft where ft.processId=? ", processId);
	}
	/**
	 * 根据流程名称模糊查询某类别下的流程
	 * @param companyId
	 * @param typeId
	 * @return
	 */
	public List<WorkflowDefinition> getWfDefinitionsByName(Long companyId, Long typeId,String name){
		return find("from WorkflowDefinition wfd where wfd.companyId = ? and wfd.typeId=? and wfd.enable=? and wfd.name like ? order by wfd.version desc", 
				companyId, typeId, DataState.ENABLE,"%"+name+"%");
	}
	
}
