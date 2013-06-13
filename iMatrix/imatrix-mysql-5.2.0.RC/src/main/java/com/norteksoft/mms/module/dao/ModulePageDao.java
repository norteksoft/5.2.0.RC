package com.norteksoft.mms.module.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.norteksoft.mms.module.entity.ModulePage;
import com.norteksoft.product.api.impl.WorkflowClientManager;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class ModulePageDao extends HibernateDao<ModulePage, Long> {
	private Log log = LogFactory.getLog(WorkflowClientManager.class);
	/**
	 * 保存菜单，如果菜单为第一次新建，该方法自动设置公司id、系统id和创建时间。
	 * 如果此菜单为子菜单，还设置该菜单的层次为父菜单层次+1
	 * @param menu
	 */
	public void saveModulePage(ModulePage page) {
		this.save(page);
	}
	
	public ModulePage getModulePage(Long pageId) {
		 return this.get(pageId);
	}
	
	
	public ModulePage getModulePageByPageId(Long pageId) {
		List<ModulePage> modules = this.find("from ModulePage v where v.enableState=? and v.companyId=? and v.id=?",DataState.ENABLE,ContextUtils.getCompanyId(),pageId);
		if(modules.size()>0){
			return modules.get(0);
		}
		return null;
	}
	
	public ModulePage getModulePage(String code) {
		return this.findUnique("from ModulePage m where m.code=? and m.companyId=?", code, ContextUtils.getCompanyId());
	}
	/**
	 * 根据菜单id查询本公司本系统下的
	 */
	public void getModulePagesByMenuId(Page<ModulePage> page,Long menuId) {
		this.findPage(page,"from ModulePage v where v.menuId=? and v.companyId=? order by v.code", menuId,ContextUtils.getCompanyId());
	}
	
	/**
	 * 根据菜单id查询本公司本系统下的
	 */
	public List<ModulePage> getModulePagesByMenuId(Long menuId) {
		return this.find("from ModulePage v where v.menuId=? and v.companyId=?", menuId,ContextUtils.getCompanyId());
	}
	/**
	 * 查询菜单中的默认页面
	 * @param menuId
	 * @return
	 */
	public ModulePage getDefaultDisplayPageByMenuId(Long menuId){
		List<ModulePage> pages = this.find("from ModulePage v where v.menuId=? and v.companyId=? and v.defaultDisplay=?  and v.enableState=? ", menuId,ContextUtils.getCompanyId(),true,DataState.ENABLE);
		if(pages.isEmpty()) return null;
		else return pages.get(0);
	}
	
	/**
	 * 根据页面视图id查询本公司本系统下的
	 */
	public List<ModulePage> getModulePagesByViewId(Long viewId) {
		if(viewId==null){
			log.debug("页面视图id不能为null");
			throw new RuntimeException("页面视图id不能为null");
		}
		if(ContextUtils.getCompanyId()==null){
			log.debug("companyId不能为null");
			throw new RuntimeException("companyId不能为null");
		}
		return this.find("from ModulePage v where v.view.id=? and v.companyId=?", viewId,ContextUtils.getCompanyId());
	}
	
	/**
	 * 根据页面视图id查询本公司本系统下的
	 */
	public List<ModulePage> getModulePagesByCode(String code,Long pageId) {
		if(pageId!=null){
			return this.find("from ModulePage v where v.code=? and v.companyId=? and v.id<>? ", code,ContextUtils.getCompanyId(),pageId);
		}else{
			return this.find("from ModulePage v where v.code=? and v.companyId=?", code,ContextUtils.getCompanyId());
		}
	}
	
	public List<Long> getEnabelModulePage(){
		return this.find("select v.id from ModulePage v where v.companyId=? and v.enableState=?", ContextUtils.getCompanyId(),DataState.ENABLE);
	}
	
	/**
	 * 根据菜单id查询本公司本系统下的
	 */
	public List<ModulePage> getEnableModulePagesByMenuId(Long menuId) {
		return this.find("from ModulePage v where v.menuId=? and v.enableState=? and v.companyId=? and v.enableState=? order by v.id", menuId,DataState.ENABLE,ContextUtils.getCompanyId(),DataState.ENABLE);
	}
}
