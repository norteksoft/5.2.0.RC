package com.norteksoft.mms.module.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.norteksoft.mms.form.enumeration.MenuType;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.product.api.impl.WorkflowClientManager;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class MenuDao extends HibernateDao<Menu, Long> {
	private Log log = LogFactory.getLog(WorkflowClientManager.class);
	/**
	 * 保存菜单，如果菜单为第一次新建，该方法自动设置公司id、系统id和创建时间。
	 * 如果此菜单为子菜单，还设置该菜单的层次为父菜单层次+1
	 * @param menu
	 */
	public void saveMenu(Menu menu) {
		this.save(menu);
	}
	
	public Menu getMenu(Long menuId) {
		List<Menu> menus=this.find("from Menu m where m.id=?", menuId);
		if(menus.size()>0){
			return menus.get(0);
		}
		return null;
	}

	public List<Menu> getRootMenuByCompany() {
		return this.find("from Menu m where m.layer=? and m.companyId=? order by m.displayOrder",  1,ContextUtils.getCompanyId());
	}
	
	public List<Menu> getEnabledStandardRootMenuByCompany() {
		return this.find("from Menu m where m.layer=? and m.companyId=? and m.enableState=? and m.type=? order by m.displayOrder", 1,ContextUtils.getCompanyId(),DataState.ENABLE,MenuType.STANDARD);
	}
	
	public List<Menu> getEnabledCustomRootMenuByCompany() {
		return this.find("from Menu m where m.layer=? and m.companyId=? and m.enableState=? and m.type=? order by m.displayOrder", 1,ContextUtils.getCompanyId(),DataState.ENABLE,MenuType.CUSTOM);
	}
	
	public List<Menu> getEnabledRootMenuByCompany() {
		return this.find("from Menu m where m.layer=? and m.companyId=? and m.enableState=? order by m.displayOrder", 1,ContextUtils.getCompanyId(),DataState.ENABLE);
	}
	
	public List<Menu> getMenuByLayer(Integer layer,Long parentId) {
		return this.find("from Menu m where m.layer=? and m.companyId=? and m.parent.id=? order by m.displayOrder", layer,ContextUtils.getCompanyId(),parentId);
	}
	
	public List<Menu> getEnableMenuByLayer(Integer layer,Long parentId) {
		return this.find("from Menu m where m.layer=? and m.companyId=? and m.parent.id=? and m.enableState=? order by m.displayOrder", layer,ContextUtils.getCompanyId(),parentId,DataState.ENABLE);
	}
	
	public List<Menu> getDefaultMenuByLayer(Integer Layer, String code, Long companyId) {
		return this.find("from Menu m where m.layer=? and m.code=? and m.companyId=? and m.type=? order by m.displayOrder", Layer, code, companyId, MenuType.STANDARD);
	}
	
	public Menu getGoldMenuByCode(String code, Long systemId, Long companyId) {
		return this.findUnique("from Menu m where m.code=? and m.systemId=? and m.companyId=?", code, systemId, companyId);
	}
	
	public List<Menu> getChildrenEnabledMenus(Long menuId){
		return this.find("from Menu m where m.parent.id=? and m.enableState=? order by m.displayOrder", menuId, DataState.ENABLE);
	}
	
	public Menu getSysMenu(Long systemId){
		String hql="from Menu m where m.systemId=? and m.type=? and m.companyId=? and m.layer=?";
		return this.findUnique(hql,systemId,MenuType.STANDARD,ContextUtils.getCompanyId(),1);
	}
	
	public Menu getRootMenuByCode(String code, Long companyId) {
		List<Menu> menus=this.find("from Menu m where m.code=? and m.layer=? and m.companyId=?", code, 1, companyId);
		if(menus.size()>0){
			return menus.get(0);
		}
		return null;
	}
	
	public List<Menu> getAllMenus(){
		return this.find("from Menu m where m.companyId=? and m.type=? order by m.layer",ContextUtils.getCompanyId(),MenuType.STANDARD);
	}
	public List<Menu> getMenus(){
		return this.find("from Menu m where m.companyId=? order by m.layer, m.type",ContextUtils.getCompanyId());
	}
	public Menu getMenuByCode(String code){
		if(ContextUtils.getCompanyId()==null){
			log.debug("companyId不能为null");
			throw new RuntimeException("companyId不能为null");
		}
		List<Menu> menus= this.find("from Menu m where m.code=? and m.companyId=?",code,ContextUtils.getCompanyId());
		if(menus.size()>0)return menus.get(0);
		return null;
	}
	public Menu getMenuByCode(String code,Long companyId){
		List<Menu> menus= this.find("from Menu m where m.code=? and m.companyId=? ",code,companyId);
		if(menus.size()>0)return menus.get(0);
		return null;
	}
	public Menu getUnCompanyMenuByCode(String code){
		List<Menu> menus= this.findNoCompanyCondition("from Menu m where m.code=?",code);
		if(menus.size()>0)return menus.get(0);
		return null;
	}
	
	public List<Menu> getMenuBySystem(String systemIds,Long companyId){
		StringBuilder hql=new StringBuilder("from Menu m where m.companyId=? and m.type=?");
		if(StringUtils.isNotEmpty(systemIds)&&systemIds.charAt(systemIds.length()-1)==',')systemIds=systemIds.substring(0,systemIds.length()-1);
		Object[] values=new Object[2];
		if(StringUtils.isNotEmpty(systemIds)){
			hql.append(" and ");
			values=new Object[2+systemIds.split(",").length];
		}
		values[0]=companyId;
		values[1]=MenuType.STANDARD;
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
					values[2+i]=Long.parseLong(sysIds[i]);
				}
			}
		}
		return find(hql.toString(), values);
	}
	/**
	 * 获得该系统中第一个叶子菜单
	 * @param systemId
	 * @return
	 */
	public Menu getLeafMenuBySystem(Long systemId){
		String hql="from Menu m where m.systemId=? and m.type=? and m.companyId=? and m.layer=? order by displayOrder asc";
		List<Menu> menus = this.find(hql,systemId,MenuType.STANDARD,ContextUtils.getCompanyId(),2);
		Menu secondMenu = null;
		if(menus.size()>0)secondMenu = menus.get(0);
		if(secondMenu!=null){
			Menu thirdMenu=null;
			hql="from Menu m where m.systemId=? and m.type=? and m.companyId=? and m.layer=? and m.parent.id=? order by displayOrder asc";
			menus = this.find(hql,systemId,MenuType.STANDARD,ContextUtils.getCompanyId(),3,secondMenu.getId());
			if(menus.size()>0)thirdMenu = menus.get(0);
			if(thirdMenu==null){
				return secondMenu;
			}
			if(thirdMenu!=null){
				Menu fourMenu = null;
				hql="from Menu m where m.systemId=? and m.type=? and m.companyId=? and m.layer=? and m.parent.id=? order by displayOrder asc";
				menus = this.find(hql,systemId,MenuType.STANDARD,ContextUtils.getCompanyId(),4,thirdMenu.getId());
				if(menus.size()>0)fourMenu = menus.get(0);
				if(fourMenu==null){
					return thirdMenu;
				}else{
					return fourMenu;
				}
			}
			
		}
		return null;
		
	}
	
}
