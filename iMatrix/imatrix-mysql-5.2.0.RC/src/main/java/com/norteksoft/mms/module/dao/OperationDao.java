package com.norteksoft.mms.module.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.norteksoft.mms.module.entity.Operation;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
@Repository
public class OperationDao extends HibernateDao<Operation, Long>{

	public void getOperations(Page<Operation> pages,Long systemId){
		this.searchPageByHql(pages, "from Operation o where o.systemId=? and o.parent!=null ",systemId);
	}
	public List<Operation> getAllParentOperations(Long systemId){
		return this.find("from Operation o where o.systemId=? and o.parent is null ",systemId);
	}
	
	public Operation getOperationByCode(String code,Long systemId){
		String hql="from Operation t where t.code=? and t.systemId=?";
		List<Operation> operations=this.find(hql, code,systemId);
		if(operations.size()>0)return operations.get(0);
		return null;
	}
	
	public void getOperationChildren(Page<Operation> page,Long operationId){
		String hql="from Operation t where t.parent.id=? order by t.id desc";
		this.searchPageByHql(page, hql,operationId);
	}
	public List<Operation> getOperationChildrenList(Long operationId){
		String hql="from Operation t where t.parent.id=? order by t.id desc";
		return this.find( hql,operationId);
	}
	
	public List<Operation> getOperations(String systemIds,Long companyId){
		StringBuilder hql=new StringBuilder("from Operation m where m.companyId=? ");
		if(StringUtils.isNotEmpty(systemIds)&&systemIds.charAt(systemIds.length()-1)==',')systemIds=systemIds.substring(0,systemIds.length()-1);
		Object[] values=new Object[1];
		if(StringUtils.isNotEmpty(systemIds)){
			hql.append(" and ");
			values=new Object[1+systemIds.split(",").length];
		}
		values[0]=companyId;
		if(StringUtils.isNotEmpty(systemIds)){
			String[] sysIds=systemIds.split(",");
			for(int i=0;i<sysIds.length;i++){
				if(StringUtils.isNotEmpty(sysIds[i])){
					if(i==0)hql.append("(");
					hql.append(" m.systemId=? ");
					if(i<sysIds.length-1){
						hql.append(" or ");
					}
					if(i==sysIds.length-1)hql.append(")");
					values[1+i]=Long.parseLong(sysIds[i]);
				}
			}
		}
		return find(hql.toString(), values);
	}
}
