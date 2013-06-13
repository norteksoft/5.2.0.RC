package com.norteksoft.portal.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.portal.entity.Webpage;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class WebpageDao extends HibernateDao<Webpage, Long>{
  
	/**
	 * 根据用户id取页签
	 * @return
	 */
	public List<Webpage> getWebpageByUserId(){
		String hql = "FROM Webpage w WHERE w.userId=? AND w.companyId=? ORDER BY w.displayOrder";
		return this.find(hql,ContextUtils.getUserId(),ContextUtils.getCompanyId());
	}
	
	public Webpage getWebpageByCode(String code){
		List<Webpage> pages=this.find("from Webpage w where w.code=?", code);
		if(pages.size()>0)return pages.get(0);
		return null;
	}
	
	public Webpage getWebpage(){
		List<Webpage> pages=this.find("from Webpage w where w.userId is null and w.acquiescent=?", true);
		if(pages.size()>0)return pages.get(0);
		return null;
	}
	public Webpage getWebpage(Long companyId){
		List<Webpage> pages=this.find("from Webpage w where w.userId is null and w.acquiescent=? and w.companyId=?", true,companyId);
		if(pages.size()>0)return pages.get(0);
		return null;
	}
	
	public int getMaxPageOrderNumber(){
		return Integer.parseInt(this.findUnique("select max(w.displayOrder) from Webpage w where w.userId=? AND w.companyId=? ", 
				ContextUtils.getUserId(), 
				ContextUtils.getCompanyId()).toString());
	}
	
}
