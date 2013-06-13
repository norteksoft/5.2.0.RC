package com.norteksoft.bs.options.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.norteksoft.bs.options.entity.TimedTask;
import com.norteksoft.product.orm.hibernate.HibernateDao;

@Repository
public class JobInfoDao extends HibernateDao<TimedTask, Long> {
	public List<TimedTask> getJobInfoBySystem(Long companyId,String systemIds){
		StringBuilder hql=new StringBuilder("from TimedTask m where m.companyId=? ");
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
		return this.find(hql.toString(), values);
	}
	
	public TimedTask getJobInfoByCode(String code,String systemCode){
		String hql="from TimedTask t where t.code=? and t.systemCode=?";
		return this.findUnique(hql, code,systemCode);
	}
}
