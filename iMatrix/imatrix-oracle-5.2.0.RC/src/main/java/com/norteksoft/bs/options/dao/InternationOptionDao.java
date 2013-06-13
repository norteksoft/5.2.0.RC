package com.norteksoft.bs.options.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.bs.options.entity.InternationOption;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
@Repository
public class InternationOptionDao extends HibernateDao<InternationOption,Long>{
	public void getInternationOptions(Page<InternationOption> page,Long interId){
		String hql="from InternationOption t where t.internation.id=? order by t.id desc";
		this.searchPageByHql(page, hql,interId);
	}
	
	public List<InternationOption> getInternationOptions(Long interId){
		String hql="from InternationOption t where t.internation.id=? order by t.id desc";
		return this.find(hql, interId);
	}
	
	public InternationOption getInternationOptionByInfo(Long category,String categoryName,String value,String internationCode){
		String hql="from InternationOption t where t.category=? and t.categoryName=? and t.value=? and t.internation.code=?  order by t.id desc";
		List<InternationOption> opts=this.find(hql, category,categoryName,value,internationCode);
		if(opts.size()>0)return opts.get(0);
		return null;
	}
}
