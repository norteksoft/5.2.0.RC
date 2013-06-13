package com.norteksoft.wf.engine.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.product.orm.Page;
import com.norteksoft.wf.engine.dao.WorkflowTypeDao;
import com.norteksoft.wf.engine.entity.WorkflowType;

@Service
@Transactional
public class WorkflowTypeManager {
	
	private Log log=LogFactory.getLog(WorkflowTypeManager.class);
	private static String LOGMESSAGE_METHOD_OVER = " method over. ";
	
	private WorkflowTypeDao workflowTypeDao;
    
	@Autowired
	public void setWorkflowTypeDao(WorkflowTypeDao workflowTypeDao) {
		this.workflowTypeDao = workflowTypeDao;
	}
	
	/**
	 * 保存WorkflowType
	 */
	@Transactional(readOnly=false)
	public void saveWorkflowType(WorkflowType basicType){
		try {
			workflowTypeDao.saveWorkflowType(basicType);
			log.debug("excute save success.");
		} catch (Exception e) {
			log.debug("excute save failed.");
			new RuntimeException( e);
		}finally{
			log.debug(LOGMESSAGE_METHOD_OVER);
		}
		
	}
	
	/**
	 * 获得对应id的WorkflowType
	 */
	public WorkflowType getWorkflowType(Long id){
		WorkflowType basicType = workflowTypeDao.get(id);
		return basicType;
	}
	
	public WorkflowType getWorkflowTypeById(Long typeId,Long companyId){
		return workflowTypeDao.getWorkflowTypeById(typeId, companyId);
	}
	
	/**
	 * 查询workflow
	 * 封装为Page
	 */
	public void getWorkflowTypePage(Page<WorkflowType> page){
		workflowTypeDao.getWorkflowType(page);
	}
	
	/**
	 * 查询companyId公司的所有workflow
	 */
	public List<WorkflowType> getAllWorkflowType(){
		List<WorkflowType> basicTypes = workflowTypeDao.getAllWorkflowType();
		return basicTypes;
		
	}
	
	/**
	 * 查询companyId公司的所有workflow
	 * liudongxia
	 */
	public List<WorkflowType> getAllWorkflowType(Long companyId,Long systemId){
		List<WorkflowType> basicTypes = workflowTypeDao.getAllWorkflowType();
		return basicTypes;
		
	}
	
	/**
	 * 删除对应id的WorkflowType
	 */
	@Transactional(readOnly=false)
	public void deleteWorkflowType(Long id){
		try {
			workflowTypeDao.delete(id);
			log.debug("excute delete success.");
		} catch (Exception e) {
			log.debug("excute delete failed.");
			new RuntimeException( e);
		}finally{
			log.debug(LOGMESSAGE_METHOD_OVER);
		}
	}
	
	/**
	 * 删除WorkflowType
	 */
	@Transactional(readOnly=false)
	public void deleteWorkflowType(WorkflowType basicType){
		try {
			workflowTypeDao.delete(basicType);
			log.debug("excute delete success.");
		} catch (Exception e) {
			log.debug("excute delete failed.");
			new RuntimeException( e);
		}finally{
			log.debug(LOGMESSAGE_METHOD_OVER);
		}
	}

	public WorkflowType getWorkflowType(String typeCode) {
		if(StringUtils.isEmpty(typeCode)) throw new RuntimeException("没有给定查询流程类型的查询条件：流程定义编号");
		return workflowTypeDao.getWorkflowType(typeCode);
	}
	
	public List<WorkflowType> getWorkflowTypes(String typeCode) {
		List<WorkflowType> types = workflowTypeDao.getWorkflowTypes(typeCode);
		return types;
	}
	
	/**
	 * 查询companyId公司的所有workflow(flex使用)
	 */
	public List<WorkflowType> getAllWorkflowType(Long companyId){
		List<WorkflowType> workflowTypes = workflowTypeDao.getAllWorkflowType(companyId);
		return workflowTypes;
		
	}
}
