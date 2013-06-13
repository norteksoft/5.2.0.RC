package com.norteksoft.portal.dao;

import org.springframework.stereotype.Repository;

import com.norteksoft.portal.entity.UserTheme;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class UserThemeDao extends HibernateDao<UserTheme, Long>{

	
	public UserTheme getTheme(){
		return this.findUnique("FROM UserTheme t WHERE t.userId=?",ContextUtils.getUserId());
	}
	
	public UserTheme getTheme(Long userId, Long companyId){
		return this.findUnique("FROM UserTheme t WHERE t.userId=? and t.companyId=?", userId, companyId);
	}
}
