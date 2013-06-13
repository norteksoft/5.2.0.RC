package com.norteksoft.portal.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.portal.entity.Theme;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class ThemeDao extends HibernateDao<Theme, Long>{

	/**
	 * 获得所有主题
	 * @param themePage
	 */
	public void themePage(Page<Theme> themePage) {
		this.searchPageByHql(themePage, "from Theme t where t.companyId=? ",ContextUtils.getCompanyId());
	}

	/**
	 * 获得启用的主题
	 * @return
	 */
	public List<Theme> getStartUsingTheme() {
		return this.find("from Theme t where t.companyId=? and t.dataState=? ",ContextUtils.getCompanyId(),DataState.ENABLE);
	}
	
	/**
	 * 获得所有的主题
	 * @return
	 */
	public List<Theme> getAllTheme() {
		return this.find("from Theme t where t.companyId=? ",ContextUtils.getCompanyId());
	}

	/**
	 * 根据编号获得主题
	 * @param code
	 * @return
	 */
	public Theme getTheme(String code) {
		return this.findUnique("from Theme t where t.companyId=? and t.code=? ",ContextUtils.getCompanyId(),code);
	}

}
