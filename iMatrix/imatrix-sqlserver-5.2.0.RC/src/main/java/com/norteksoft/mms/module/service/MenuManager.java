package com.norteksoft.mms.module.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.enumeration.MenuType;
import com.norteksoft.mms.form.service.DataTableManager;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.module.dao.MenuDao;
import com.norteksoft.mms.module.dao.ModulePageDao;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.entity.ModulePage;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.util.ContextUtils;

@Service
@Transactional(readOnly=true)
public class MenuManager {

	private MenuDao menuDao;
	private ModulePageDao modulePageDao;
	private AcsUtils acsUtils;
	@Autowired
	private DataTableManager dataTableManager;
	@Autowired
	private FormViewManager formViewManager;
	
	@Autowired
	public void setMenuDao(MenuDao menuDao) {
		this.menuDao = menuDao;
	}
	@Autowired
	public void setModulePageDao(ModulePageDao modulePageDao) {
		this.modulePageDao = modulePageDao;
	}
	@Autowired
	public void setAcsUtils(AcsUtils acsUtils) {
		this.acsUtils = acsUtils;
	}
	/**
	 * 保存菜单
	 */
	@Transactional(readOnly=false)
	public void saveMenu(Menu menu){
		if(menu.getId()==null){
			if(menu.getSystemId()==null){
				if(menu.getParent()!=null){
					//二级菜单系统id
					menu.setSystemId(menu.getParent().getSystemId());
				}else{
					//自定义一级菜单
					menu.setType(MenuType.CUSTOM);
					menu.setSystemId(ContextUtils.getSystemId("mms"));
					if(!(StringUtils.isNotBlank(menu.getUrl())&&menu.getUrl().startsWith("http:"))){
						String sysUrl=getSysUrl(ContextUtils.getSystemId("mms"));
						menu.setUrl(sysUrl+"/common/list.htm");
					}
				}
				//自定义非一级菜单的url的设置
				if(menu.getParent()!=null){
					if("#this".equals(menu.getUrl())||StringUtils.isEmpty(menu.getUrl())){
						//标准或自定义系统中的子菜单没设url则为“定义菜单”
						menu.setType(MenuType.CUSTOM);
						//三级和三级以下菜单
						if(menu.getParent().getParent()!=null){
							menu.setUrl("/mms/common/list.htm");
						}else{//二级菜单
							menu.setUrl("/mms/common/list.htm");
						}
					}else{
						//子菜单设置了url
						if(menu.getParent().getType()==MenuType.STANDARD){
							//标准系统已有子菜单设置为“标准菜单”
							menu.setType(MenuType.STANDARD);
						}else{
							//自定义系统中子菜单无论是否设置了url，永远为“自定义菜单”
							menu.setType(MenuType.CUSTOM);
							menu.setUrl("/mms/common/list.htm");
						}
					}
				}
			}
			menu.setCompanyId(ContextUtils.getCompanyId());
			menu.setCreatedTime(new Date());
			if(menu.getParent()!=null){
				menu.setLayer(menu.getParent().getLayer()+1);
			}
		}
		if(!menu.getLayer().equals(1)){//如果不是一级菜单
			if("/mms/common/list.htm".equals(menu.getUrl())||"/mms/common/list.htm?".equals(menu.getUrl())){
				//当不是一级菜单时，修改了路径为/mms/common/list.htm或/mms/common/list.htm?时，该菜单的类型改为自定义
				menu.setType(MenuType.CUSTOM);
			}else{
				//当不是一级菜单时，修改了路径不为/mms/common/list.htm或/mms/common/list.htm?时，该菜单的类型改为标准
				menu.setType(MenuType.STANDARD);
			}
		}
		menuDao.saveMenu(menu);
	}
	
	/**
	 * 获取菜单
	 */
	public Menu getMenu(Long menuId){
		return menuDao.getMenu(menuId);
	}

	/**
	 * 得到公司所有的一级菜单
	 */
	public List<Menu> getRootMenuByCompany() {
		return menuDao.getRootMenuByCompany();
	}
	
	/**
	 * 得到公司所有启用的一级菜单
	 */
	public List<Menu> getEnabledRootMenuByCompany() {
		return menuDao.getEnabledRootMenuByCompany();
	}
	/**
	 * 删除菜单
	 */
	@Transactional(readOnly=false)
	public String deleteMenu(Menu menu) {
		List<DataTable> tables=dataTableManager.getAllDataTablesByMenu(menu.getId());
		if(tables.size()>0){
			return "该菜单已被使用,无法删除";
		}
		List<FormView> formviews=formViewManager.getFormViewsByMenu(menu.getId());
		if(formviews.size()>0){
			return "该菜单已被使用,无法删除";
		}
		menuDao.delete(menu);
		return "success";
	}
	public Menu getRootMenu(Long menuId){
		Menu menu=menuDao.get(menuId);
		if(menu.getLayer()==1){
			return menu;
		}
		if(menu.getLayer()==2){
			return menu.getParent();
		}
		if(menu.getLayer()==3){
			return menu.getParent().getParent();
		}
		if(menu.getLayer()==4){
			return menu.getParent().getParent().getParent();
		}
		return null;
	}
	
	public List<Menu> getMenuByLayer(Integer layer,Long parentId) {
		return menuDao.getMenuByLayer(layer,parentId);
	}
	public List<Menu> getEnableMenuByLayer(Integer layer,Long parentId) {
		return menuDao.getEnableMenuByLayer(layer,parentId);
	}
	
	public Menu getDefaultModulePageBySystem(String code, Long companyId) {
		List<Menu> menus=menuDao.getDefaultMenuByLayer(1, code, companyId);
		Menu firstMenu =null;
		if(menus.size()>0){
			firstMenu = menus.get(0);
		}
		Menu secondMenu = (firstMenu==null?null:firstMenu.getFirstChildren());
		return secondMenu==null?null:secondMenu.getFirstChildren();
	}
	/**
	 * 获得最底层菜单
	 * @param systemId
	 * @param companyId
	 * @return
	 */
	@Transactional(readOnly=true)
	public Menu getLastMenu(Long menuId) {
		Menu lastMenu=null;
		Menu menu=getMenu(menuId);
		if(menu!=null){
			lastMenu=menu;
			List<Menu> secMenus=menuDao.getChildrenEnabledMenus(lastMenu.getId());
			Menu secondMenu = (secMenus.size()<=0?null:secMenus.get(0));
			if(secondMenu!=null){
				lastMenu=secondMenu;
				List<Menu> thirdMenus=menuDao.getChildrenEnabledMenus(lastMenu.getId());
				Menu thirdMenu = (thirdMenus.size()<=0?null:thirdMenus.get(0));
				if(thirdMenu!=null){
					lastMenu=thirdMenu;
					List<Menu> fourMenus=menuDao.getChildrenEnabledMenus(lastMenu.getId());
					Menu fourMenu = (fourMenus.size()<=0?null:fourMenus.get(0));
					if(fourMenu!=null){
						lastMenu=fourMenu;
					}
				}
			}
		}
		return lastMenu;
	}
	
	public Menu getGoldMenuByCode(String code, Long systemId, Long companyId) {
		return menuDao.getGoldMenuByCode(code, systemId, companyId);
	}
	
	public ModulePage getDefaultModulePageByMenu(Long menuId) {
		Menu menu=menuDao.get(menuId);
		ModulePage defaultPage=null;
		ModulePage page=modulePageDao.getDefaultDisplayPageByMenuId(menuId);
		if(page==null){
			List<ModulePage> pages=modulePageDao.getEnableModulePagesByMenuId(menuId);
			if(pages.size()>0)defaultPage=pages.get(0);
			if(defaultPage==null){
				List<Menu> menus=menuDao.getChildrenEnabledMenus(menu.getId());
				menu= (menus.size()<=0?null:menus.get(0));
				if(menu!=null){
					defaultPage=getDefaultModulePageByMenu(menu.getId());
					if(defaultPage!=null){
						return defaultPage;
					}
				}
			}
		}else{
			defaultPage=page;
		}
		return defaultPage;
	}
	
	public Menu getMenuByLastMenu(Integer layer,Long menuId){
		Menu result=null;
		Menu menu=getMenu(menuId);
		if(menu.getLayer()>=layer){
			if(menu.getLayer().equals(layer))result=menu;
			if(result==null){
				menu=menu.getParent();
				if(menu!=null){
					if(menu.getLayer().equals(layer))result=menu;
					if(result==null){
						menu=menu.getParent();
						if(menu!=null){
							if(menu.getLayer().equals(layer))result=menu;
						}
					}
				}
			}
		}
		return result;
	}
	
	//得到子菜单及其所有父菜单
	public void getMenuParents(List<Menu> menus,Menu menu){
		if(menu!=null){
			menus.add(menu);
			getMenuParents(menus,menu.getParent());
		}
	}
	
	public List<Menu> getChildrenEnabledMenus(Long menuId){
		return menuDao.getChildrenEnabledMenus(menuId);
	}
	/**
	 * 获得启用的标准菜单一级菜单集合
	 * @return
	 */
	public List<Menu> getEnabledStandardRootMenuByCompany() {
		return menuDao.getEnabledStandardRootMenuByCompany();
	}
	
	/**
	 * 获得启用的自定义菜单一级菜单集合
	 * @return
	 */
	public List<Menu> getEnabledCustomRootMenuByCompany() {
		return menuDao.getEnabledCustomRootMenuByCompany();
	}
	
	public String getSysUrl(Long systemId){
		Menu menu=menuDao.getSysMenu(systemId);
		if(menu!=null){
			String url=menu.getUrl();
			if(url.lastIndexOf("/")==url.length()-1){
				return url.substring(0,url.length()-1);
			}else{
				return url;
			}
		}
		return "";
	}
	
	/**
	 * 初始化一级菜单
	 */
	@Transactional(readOnly=false)
	public void initAllMenus(){
		List<BusinessSystem> bses=acsUtils.getAllBusiness(ContextUtils.getCompanyId());
		List<BusinessSystem> imatrixBs=acsUtils.getParentSystem();
		if(imatrixBs!=null){
			for(BusinessSystem sys:imatrixBs){
				if(!bses.contains(sys)){//底层系统中是否已经包括底层平台系统
					bses.add(sys);
				}
			}
		}
		List<Menu> menus=menuDao.getRootMenuByCompany();
		for(BusinessSystem bs:bses){
			boolean isHasMenu=false;
			Menu mn=null;
			for(Menu menu:menus){
				if(menu.getCode().equals(bs.getCode())){
					isHasMenu=true;
					mn=getMenu(menu.getId());
					break;
				}
			}
			if(!isHasMenu){
				mn=new Menu();
				mn.setType(MenuType.STANDARD);
				mn.setCompanyId(ContextUtils.getCompanyId());
				mn.setSystemId(bs.getId());
				mn.setLayer(1);
				mn.setCode(bs.getCode());
				mn.setName(bs.getName());
				mn.setEnableState(DataState.ENABLE);
				mn.setUrl(bs.getPath());
				menuDao.save(mn);
			}
		}
	}
	/**
	 * 根据系统id获得一级菜单
	 * @return
	 */
	public Menu getDefaultMenuByLayer(String code){
		List<Menu> menus=menuDao.getDefaultMenuByLayer(1, code, ContextUtils.getCompanyId());
		if(menus.size()>0)return menus.get(0);
		return null;
	}
	/**
	 * 获得所有标准菜单
	 * @return
	 */
	public List<Menu> getAllMenus(){
		return menuDao.getAllMenus();
	}
	
	/**
	 * 获得所有菜单(标准和自定义)
	 * @return
	 */
	public List<Menu> getMenus(){
		return menuDao.getMenus();
	}
	
	public Menu getMenuByCode(String code){
		return menuDao.getMenuByCode(code);
	}
	public Menu getMenuByCode(String code,Long companyId){
		return menuDao.getMenuByCode(code,companyId);
	}
	public Menu getUnCompanyMenuByCode(String code){
		return menuDao.getUnCompanyMenuByCode(code);
	}
	
	public List<Menu> getMenuBySystem(String systemIds,Long companyId){
		return menuDao.getMenuBySystem(systemIds,companyId);
	}
	/**
	 * 获得该系统中第一个叶子菜单
	 * @param systemId
	 * @return
	 */
	public Menu getLeafMenuBySystem(Long systemId){
		return menuDao.getLeafMenuBySystem(systemId);
	}
	
}
