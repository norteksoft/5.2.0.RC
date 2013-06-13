package com.norteksoft.portal.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.norteksoft.portal.entity.Widget;
import com.norteksoft.portal.entity.WidgetRole;
import com.norteksoft.product.orm.hibernate.HibernateDao;

@Repository
public class WidgetRoleDao extends HibernateDao<WidgetRole, Long> {
	@Autowired
	private WidgetDao widgetDao;

	// 根据角色id得到小窗体
	public List<WidgetRole> getWidgetsByRoleId(Long roleId) {
		return this.find("FROM WidgetRole wr WHERE wr.roleId=?", roleId);
	}

	public List<WidgetRole> getWidgetRoles() {
		return this.find("FROM WidgetRole wr");
	}

	public List<WidgetRole> getWidgetRoles(Long companyId) {
		return this.find("FROM WidgetRole wr where wr.companyId=?", companyId);
	}

	// 根据角色id得到小窗体
	public WidgetRole getWidgetRole(Long roleId, Long widgetId) {
		List<WidgetRole> wrs = this.find("FROM WidgetRole wr WHERE wr.roleId=? and wr.widgetId=?",roleId, widgetId);
		if (wrs.size() > 0)
			return wrs.get(0);
		return null;
	}

	// 根据小窗体获得窗体角色关系
	public List<WidgetRole> getWidgetRoleByWidgetId(Long widgetId) {
		return this.find("from WidgetRole wr where wr.widgetId=? ", widgetId);
	}

	// 根据系统获得窗体角色关系
	public List<WidgetRole> getWidgetRoleBySystem(String systemIds,Long companyId) {
		List<Widget> widgets = widgetDao.getWidgetsBySystem(systemIds,
				companyId);
		StringBuilder hql = new StringBuilder("from WidgetRole wr where wr.companyId=?");
		Object[] values = new Object[1];
		if (widgets.size() > 0) {
			hql.append(" and ");
			values = new Object[1 + widgets.size()];
		}
		values[0] = companyId;
		for (int i = 0; i < widgets.size(); i++) {
			if (i == 0)
				hql.append("(");
			hql.append(" wr.widgetId=? ");
			if (i < widgets.size() - 1) {
				hql.append(" or ");
			}
			if (i == widgets.size() - 1)
				hql.append(")");
			values[1 + i] = widgets.get(i).getId();
		}
		return this.find(hql.toString(), values);
	}

	public void deleteWidgetRoleByWidgetId(Long widgetId) {
		this.createQuery("delete  from WidgetRole t where  t.widgetId = ? ",
				widgetId).executeUpdate();
	}
}
