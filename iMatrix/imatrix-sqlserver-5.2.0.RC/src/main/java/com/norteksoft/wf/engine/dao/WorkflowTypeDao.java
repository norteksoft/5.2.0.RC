package com.norteksoft.wf.engine.dao;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.engine.entity.WorkflowType;

@Repository
public class WorkflowTypeDao extends HibernateDao<WorkflowType, Long>{
	
	public void getWorkflowType(Page<WorkflowType> page) {// order by createdTime desc
		searchPageByHql(page, "from WorkflowType wt where wt.companyId = ? order by wt.createdTime desc",ContextUtils.getCompanyId());
	}

	public List<WorkflowType>  getAllWorkflowType() {
		return find("from WorkflowType wt where wt.companyId = ? order by wt.createdTime desc",ContextUtils.getCompanyId());
	}

	public WorkflowType getWorkflowType(String typeCode) {
		return findUnique("from WorkflowType wt where wt.code=? and wt.companyId = ? ",typeCode,ContextUtils.getCompanyId());
	}
	
	public List<WorkflowType> getWorkflowTypes(String typeCode) {
		return find("from WorkflowType wt where wt.code=? and wt.companyId = ? order by wt.createdTime desc",typeCode,ContextUtils.getCompanyId());
	}

	/**
	 * 保存工作流类型
	 * @param basicType
	 */
	public void saveWorkflowType(WorkflowType basicType) {
		Assert.notNull(basicType);
		if(basicType.getId()==null){
			basicType.setCompanyId(ContextUtils.getCompanyId());
			basicType.setCreatedTime(new Date(System.currentTimeMillis()));
			basicType.setCreator(ContextUtils.getLoginName());
			basicType.setCreatorName(ContextUtils.getUserName());
		}
		this.save(basicType);
	}

	/**
	 * 查询companyId公司的所有workflow(flex使用)
	 */
	public List<WorkflowType>  getAllWorkflowType(Long companyId) {
		return find("from WorkflowType wt where wt.companyId = ?",companyId);
	}
	
	public WorkflowType getWorkflowTypeById(Long typeId,Long companyId){
		List<WorkflowType>  types=find("from WorkflowType wt where wt.companyId = ? and wt.id=?",companyId,typeId);
		if(types.size()>0)return types.get(0);
		return null;
	}

	/**
	 * 获得是审批系统的流程类型
	 * @return
	 */
	public List<WorkflowType> getApproveSystemWorkflowTypes() {
		return find("from WorkflowType wt where wt.companyId = ? and wt.approveSystem = ? ",ContextUtils.getCompanyId(),true);
	}

}
