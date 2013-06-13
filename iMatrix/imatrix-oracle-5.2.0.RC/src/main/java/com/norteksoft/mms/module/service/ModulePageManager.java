package com.norteksoft.mms.module.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.mms.form.dao.FormViewDao;
import com.norteksoft.mms.form.dao.ListViewDao;
import com.norteksoft.mms.module.dao.ButtonDao;
import com.norteksoft.mms.module.dao.ModulePageDao;
import com.norteksoft.mms.module.entity.Button;
import com.norteksoft.mms.module.entity.ModulePage;
import com.norteksoft.mms.module.enumeration.ViewType;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;

@Service
@Transactional(readOnly=true)
public class ModulePageManager {

	
	private ButtonDao buttonDao;
	private ModulePageDao modulePageDao;
	
	private FormViewDao formViewDao;
	private ListViewDao listViewDao;
	
	@Autowired
	public void setButtonDao(ButtonDao buttonDao) {
		this.buttonDao = buttonDao;
	}
	
	@Autowired
	public void setModulePageDao(ModulePageDao modulePageDao) {
		this.modulePageDao = modulePageDao;
	}
	
	@Autowired
	public void setFormViewDao(FormViewDao formViewDao) {
		this.formViewDao = formViewDao;
	}
	@Autowired
	public void setListViewDao(ListViewDao listViewDao) {
		this.listViewDao = listViewDao;
	}

	private Long getCompanyId(){
		return ContextUtils.getCompanyId();
	}
	/**
	 * 保存页面
	 */
	@Transactional(readOnly=false)
	public void saveModulePage(ModulePage modulePage){
		if(modulePage.getView()!=null){
			if(modulePage.getViewType().equals(ViewType.FORM_VIEW)){
				modulePage.setView(formViewDao.get(modulePage.getView().getId()));
			}else{
				modulePage.setView(listViewDao.get(modulePage.getView().getId()));
			}
		}
		modulePage.setCompanyId(getCompanyId());
		modulePage.setCreatedTime(new Date());
		modulePageDao.saveModulePage(modulePage);
	}

	/**
	 * 获得页面
	 */
	public ModulePage getModulePage(Long pageId){
		return modulePageDao.getModulePage(pageId);
	}
	
	/**
	 * 获得页面
	 */
	public ModulePage getModulePageByPageId(Long pageId){
		return modulePageDao.getModulePageByPageId(pageId);
	}
	/**
	 * 重载上述方法
	 * @param code
	 * @param companyId
	 * @return
	 */
	public ModulePage getModulePage(String code) {
		return modulePageDao.getModulePage(code);
	}
	/**
	 * 删除页面
	 */
	@Transactional(readOnly=false)
	public void deleteModulePage(ModulePage modulePage) {
		modulePageDao.delete(modulePage);
	}
	@Transactional(readOnly=false)
	public String deleteModulePages(String pageIds){
		Integer canNum=0;
		if(pageIds!=null){
			String[] ids=pageIds.split(",");
			Integer[] nums=new Integer[ids.length];
			deleteModulePage(ids, canNum,nums);
			for(Integer num:nums){
				if(num!=null){
					canNum=num;
				}
			}
			String successStr=canNum!=0?canNum+"个删除成功;":"";
			String errorStr=(ids.length-canNum)!=0?(ids.length-canNum)+"个删除失败,某按钮的转向页面引用了该页面或已启用过;":"";
			return successStr+errorStr;
		}
		return null;
	}
	
	//删除页面，处理关联关系
	private Integer deleteModulePage(String[] ids, Integer canNum,Integer[] nums){
		Long id = null;
		boolean result = false;
		List<Long> enabelPageIds=modulePageDao.getEnabelModulePage();
		for(int i = 0; i < ids.length; i++){
			if(ids[i] ==  null || enabelPageIds.contains(Long.parseLong(ids[i])))  continue;
			id = Long.parseLong(ids[i]);
			List<Button> btns=buttonDao.getButtonsByToPageId(id);
			if(btns.size()==0){
				modulePageDao.delete(id);
				ids[i] = null;
				result = true;
				canNum = canNum + 1;
				nums[canNum-1]=canNum;
			}
		}
		if(result && ids.length != canNum) deleteModulePage(ids, canNum,nums);
		return  canNum;
	}
	
	/**
	 * 根据菜单id查询菜单下的视图
	 * @param menuId 菜单id
	 * @return 视图列表
	 */
	public void getModulePagesByMenuId(Page<ModulePage> page, Long menuId) {
		modulePageDao.getModulePagesByMenuId(page ,menuId);
	}
	/**
	 * 根据菜单id查询菜单下的视图
	 * @param menuId 菜单id
	 * @return 视图列表
	 */
	public List<ModulePage> getModulePagesByMenuId(Long menuId) {
		return modulePageDao.getModulePagesByMenuId(menuId);
	}
	@Transactional(readOnly=false)
	public Boolean defaultDisplaySet(Long pageId,Long menuId){
		ModulePage modulePage=modulePageDao.get(pageId);
		ModulePage page=modulePageDao.getDefaultDisplayPageByMenuId(menuId);
		Boolean isHasDefaultDisplay;
		if(page!=null){
			isHasDefaultDisplay=false;
			if(modulePage.getDefaultDisplay()){
				modulePage.setDefaultDisplay(false);
				isHasDefaultDisplay=true;
			}
		}else{
			if(modulePage.getDefaultDisplay()){
				modulePage.setDefaultDisplay(false);
			}else{
				modulePage.setDefaultDisplay(true);
				modulePage.setEnableState(DataState.ENABLE);
			}
			isHasDefaultDisplay=true;
			modulePageDao.save(modulePage);
		}
		return isHasDefaultDisplay;
	}
	
	@Transactional(readOnly=false)
	public String enableSet(String pageIds){
		int enabelCount=0;
		int disabelCount=0;
		int draftCount=0;
		if(pageIds!=null && !pageIds.equals("")){
			String[] ids=pageIds.split(",");
			for(String idStr:ids){
				ModulePage modulePage=modulePageDao.get(Long.parseLong(idStr));
				if(modulePage.getEnableState().equals(DataState.ENABLE)){
					enabelCount++;
					modulePage.setDefaultDisplay(false);
					modulePage.setEnableState(DataState.DISABLE);
				}else if(modulePage.getEnableState().equals(DataState.DISABLE)){
					disabelCount++;
					modulePage.setEnableState(DataState.ENABLE);
				}else{
					draftCount++;
					modulePage.setEnableState(DataState.ENABLE);
				}
				modulePageDao.save(modulePage);
			}
		}
		String enabelStr=enabelCount!=0?enabelCount+"个启用-->禁用;":"";
		String disabelStr=disabelCount!=0?disabelCount+"个禁用-->启用;":"";
		String draftStr=draftCount!=0?draftCount+"个草稿-->启用;":"";
		return enabelStr+disabelStr+draftStr;
	}
	
	public List<ModulePage> getModulePagesByViewId(Long viewId){
		return modulePageDao.getModulePagesByViewId(viewId);
	}
	
	public Boolean isCodeExist(String code,Long pageId){
		List<ModulePage> pages=modulePageDao.getModulePagesByCode(code,pageId);
		return pages.size()>0?true:false;
	}
	
	/**
	 * 根据菜单id查询菜单下的启用的页面
	 * @param menuId 菜单id
	 * @return 页面列表
	 */
	public List<ModulePage> getEnableModulePagesByMenuId(Long menuId) {
		return modulePageDao.getEnableModulePagesByMenuId(menuId);
	}
}
