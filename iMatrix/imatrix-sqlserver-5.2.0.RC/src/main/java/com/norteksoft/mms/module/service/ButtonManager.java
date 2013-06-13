package com.norteksoft.mms.module.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.mms.module.dao.ButtonDao;
import com.norteksoft.mms.module.dao.ModulePageDao;
import com.norteksoft.mms.module.entity.Button;
import com.norteksoft.mms.module.entity.ModulePage;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;

@Service
@Transactional(readOnly=true)
public class ButtonManager {
	private static final String COLUMN_ID="columnId";
	private static final String CODE="code";
	private static final String NAME="name";
	private static final String DISPLAY_ORDER="displayOrder";
	private static final String TO_PAGE_ID="toPageId";
	private static final String EVENT="event";
	
	private ButtonDao buttonDao;
	private ModulePageDao modulePageDao;
	@Autowired
	public void setButtonDao(ButtonDao buttonDao) {
		this.buttonDao = buttonDao;
	}
	
	@Autowired
	public void setModulePageDao(ModulePageDao modulePageDao) {
		this.modulePageDao = modulePageDao;
	}

	public void getButtonPages(Page<Button> page){
		buttonDao.getButtonPages(page);
	}
	@Transactional(readOnly=false)
	public void save(Long pageId){
		List<Object> list=JsonParser.getFormTableDatas(Button.class);
		for(Object obj:list){
			Button button=(Button)obj;
			button.setCompanyId(ContextUtils.getCompanyId());
			button.setModulePage(modulePageDao.get(pageId));
			buttonDao.save(button);
		}
	}
	
	
	@Transactional(readOnly=false)
	public void save(Map<String,String[]> parameterMap,Long pageId){
		String[] ids=parameterMap.get(COLUMN_ID);
		String[] codes=parameterMap.get(CODE);
		String[] names=parameterMap.get(NAME);
		String[] orders=parameterMap.get(DISPLAY_ORDER);
		String[] toPageIds=parameterMap.get(TO_PAGE_ID);
		String[] events=parameterMap.get(EVENT);
		List<Long> list=buttonDao.getButtonIdsByPageId(pageId);
		if(codes!=null){
			for(int i=0;i<codes.length;i++){
				Button button=null;
				if(ids[i]==null || "".equals(ids[i])){
					button=new Button();
					button.setCompanyId(ContextUtils.getCompanyId());
					button.setCode(codes[i]);
					button.setName(names[i]);
					button.setDisplayOrder(Integer.parseInt(orders[i]));
					if(toPageIds[i]!=null && !"".equals(toPageIds[i]))button.setToPage(modulePageDao.get(Long.parseLong(toPageIds[i])));
					button.setEvent(events[i]);
					button.setModulePage(modulePageDao.get(pageId));
				}else{
					for(int j=0;j<list.size();j++){
						if(list.get(j).equals(Long.parseLong(ids[i]))){
							list.remove(j);
							break;
						}
					}
					button=buttonDao.get(Long.parseLong(ids[i]));
					button.setCode(codes[i]);
					button.setName(names[i]);
					button.setDisplayOrder(Integer.parseInt(orders[i]));
					if(toPageIds[i]!=null&& !"".equals(toPageIds[i]))button.setToPage(modulePageDao.get(Long.parseLong(toPageIds[i])));
					button.setEvent(events[i]);
				}
				buttonDao.save(button);
			}
		}
		for(Long id:list){
			buttonDao.delete(id);
		}
	}
	
	public Button getButton(Long id){
		return buttonDao.get(id);
	}
	/**
	 * 编号是否存在,存在返回true，否则返回false
	 * @param button
	 * @return
	 */
	public Boolean isCodeExist(String code,Long pageId){
		List<Button> buttons=buttonDao.getButtonsByCode(code,pageId);
		if(buttons.size()>0){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 按钮是否存在,存在返回false，否则返回true
	 * @param button
	 * @return
	 */
	public Boolean isButtonExist(Long pageId){
		List<Button> buttons=buttonDao.getButtonsByPageId(pageId);
		if(buttons.size()>0){
			return false;
		}else{
			return true;
		}
	}
	
	public List<Button> getButtons(Long pageId){
		return buttonDao.getButtonsByPageId(pageId);
	}
	
	public void getButtonsPage(Page<Button> buttonsPage , Long pageId){
		buttonDao.getButtonsPageByPageId(buttonsPage,pageId);
	}
	@Transactional(readOnly=false)
	public void deleteButton(Long buttonId){
		buttonDao.delete(buttonId);
	}
	@Transactional(readOnly=false)
	public void saveCustom(Button button,Long viewId,Long toViewId){
		if(toViewId!=null)
			button.setToPage(modulePageDao.get(toViewId));
		button.setModulePage(modulePageDao.get(viewId));
		button.setCompanyId(ContextUtils.getCompanyId());
		buttonDao.save(button);
	}
	/**
	 * 创建默认的列表按钮
	 */
	@Transactional(readOnly=false)
	public List<Button> createDefaultListButton(Long pageId){
		List<Button> buttons = new ArrayList<Button>();
		ModulePage modulePage = modulePageDao.get(pageId);
		Button button = null;
		button = new Button();
		button.setCode("query");
		button.setCompanyId(ContextUtils.getCompanyId());
		//button.setDisplayOrder(displayOrder);
		button.setEvent("execute: toQuery");
		button.setModulePage(modulePage);
		button.setName("查询");
		buttons.add(button);
	    button = new Button();
		button.setCode("create");
		button.setCompanyId(ContextUtils.getCompanyId());
		//button.setDisplayOrder(displayOrder);
		button.setEvent("execute: toCreateFrom");
		button.setModulePage(modulePage);
		button.setName("新增");
		buttons.add(button);
		button = new Button();
		button.setCode("update");
		button.setCompanyId(ContextUtils.getCompanyId());
		//button.setDisplayOrder(displayOrder);
		button.setEvent("execute: toUpdateForm");
		button.setModulePage(modulePage);
		button.setName("修改");
		buttons.add(button);
		button = new Button();
		button.setCode("delete");
		button.setCompanyId(ContextUtils.getCompanyId());
		//button.setDisplayOrder(displayOrder);
		button.setEvent("execute: deleteList");
		button.setModulePage(modulePage);
		button.setName("删除");
		buttons.add(button);
		return buttons;
	}
	/**
	 * 创建默认的表单按钮
	 */
	@Transactional(readOnly=false)
	public List<Button> createDefaultFormButton(Long pageId){
		List<Button> buttons = new ArrayList<Button>();
		ModulePage modulePage = modulePageDao.get(pageId);
		Button button = null;
		button = new Button();
		button.setCode("save");
		button.setCompanyId(ContextUtils.getCompanyId());
		//button.setDisplayOrder(displayOrder);
		button.setEvent("execute: saveForm");
		button.setModulePage(modulePage);
		button.setName("保存");
		buttons.add(button);
	    button = new Button();
		button.setCode("back");
		button.setCompanyId(ContextUtils.getCompanyId());
		//button.setDisplayOrder(displayOrder);
		button.setEvent("execute: toListPage");
		button.setModulePage(modulePage);
		button.setName("返回");
		buttons.add(button);
		return buttons;
	}
	
	/**
	 * 保存默认的列表按钮
	 */
	@Transactional(readOnly=false)
	public void saveDefaultListButton(Long pageId){
		ModulePage modulePage = modulePageDao.get(pageId);
		Button button = null;
		button = new Button();
		button.setCode("query");
		button.setCompanyId(ContextUtils.getCompanyId());
		button.setDisplayOrder(1);
		button.setEvent("execute: toQuery");
		button.setModulePage(modulePage);
		button.setName("查询");
		buttonDao.save(button);
		
	    button = new Button();
		button.setCode("create");
		button.setCompanyId(ContextUtils.getCompanyId());
		button.setDisplayOrder(2);
		button.setEvent("execute: toCreateFrom");
		button.setModulePage(modulePage);
		button.setName("新增");
		buttonDao.save(button);
		
		button = new Button();
		button.setCode("update");
		button.setCompanyId(ContextUtils.getCompanyId());
		button.setDisplayOrder(3);
		button.setEvent("execute: toUpdateForm");
		button.setModulePage(modulePage);
		button.setName("修改");
		buttonDao.save(button);
		
		button = new Button();
		button.setCode("delete");
		button.setCompanyId(ContextUtils.getCompanyId());
		button.setDisplayOrder(4);
		button.setEvent("execute: deleteList");
		button.setModulePage(modulePage);
		button.setName("删除");
		buttonDao.save(button);
	}
	/**
	 * 保存默认的表单按钮
	 */
	@Transactional(readOnly=false)
	public void saveDefaultFormButton(Long pageId){
		ModulePage modulePage = modulePageDao.get(pageId);
		Button button = null;
		button = new Button();
		button.setCode("save");
		button.setCompanyId(ContextUtils.getCompanyId());
		button.setDisplayOrder(1);
		button.setEvent("execute: saveForm");
		button.setModulePage(modulePage);
		button.setName("保存");
		buttonDao.save(button);
		
	    button = new Button();
		button.setCode("back");
		button.setCompanyId(ContextUtils.getCompanyId());
		button.setDisplayOrder(2);
		button.setEvent("execute: toListPage");
		button.setModulePage(modulePage);
		button.setName("返回");
		buttonDao.save(button);
	}
}
