package com.norteksoft.bs.options.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.bs.options.entity.Internation;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
@Repository
public class InternationDao extends HibernateDao<Internation,Long>{
	public void getInternations(Page<Internation> page){
		String hql="from Internation t order by t.id desc";
		this.searchPageByHql(page, hql);
	}
	
	public Internation getInternationByCode(String code){
		String hql="from Internation t where t.code=?";
		List<Internation> inters=this.find(hql, code);
		if(inters.size()>0)return inters.get(0);
		return null;
	}
	//监听中用到
	public List<Internation> getAllInternations(){
		return this.findNoCompanyCondition("from Internation t order by t.id desc");
	}
	
	public List<Internation> getInternations(){
		return this.find("from Internation t order by t.id desc");
	}
}
