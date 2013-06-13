package com.norteksoft.mms.form.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.mms.form.dao.GroupHeaderDao;
import com.norteksoft.mms.form.dao.JqGridPropertyDao;
import com.norteksoft.mms.form.dao.ListColumnDao;
import com.norteksoft.mms.form.dao.ListViewDao;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.GroupHeader;
import com.norteksoft.mms.form.entity.JqGridProperty;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.module.dao.ButtonDao;
import com.norteksoft.mms.module.dao.ModulePageDao;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.entity.ModulePage;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.api.impl.WorkflowClientManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;

@Service
@Transactional(readOnly=true)
public class ListViewManager {
	private Log log = LogFactory.getLog(WorkflowClientManager.class);
	private ListViewDao viewDao;
	private ModulePageDao modulePageDao;
	private ListColumnDao listColumnDao;
	private MenuManager menuManager;
	@Autowired
	private ButtonDao buttonDao;
	@Autowired
	private DataTableManager dataTableManager;
	@Autowired
	private JqGridPropertyDao jqGridPropertyDao;
	@Autowired
	private GroupHeaderDao groupHeaderDao;
	
	@Autowired
	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}
	@Autowired
	public void setViewDao(ListViewDao viewDao) {
		this.viewDao = viewDao;
	}
	@Autowired
	public void setModulePageDao(ModulePageDao modulePageDao) {
		this.modulePageDao = modulePageDao;
	}
	@Autowired
	public void setListColumnDao(ListColumnDao listColumnDao) {
		this.listColumnDao = listColumnDao;
	}
	
	public void getViewPage(Page<ListView> page, Long dataTableId) {
		viewDao.getViewPage(page, dataTableId);
	}
	
	public ListView getView(Long viewId) {
		return viewDao.get(viewId);
	}
	
	@Transactional(readOnly=false)
	public void saveView(ListView view,Long menuId){
		List<Object> jqGridPropertys=JsonParser.getFormTableDatas(JqGridProperty.class);
		view.setMenuId(menuId);
		view.setCompanyId(ContextUtils.getCompanyId());
		view.setCreator(ContextUtils.getLoginName());
		view.setCreatorName(ContextUtils.getUserName());
		view.setCreatedTime(new Date());
		viewDao.save(view);
		saveJqGridProperty(view,jqGridPropertys);
	}
	@Transactional(readOnly=false)
	public void saveListView(ListView view){
		viewDao.save(view);
	}
	@Transactional(readOnly=false)
	public void saveDefaultListView(ListView view,Long menuId){
		view.setMenuId(menuId);
		view.setCompanyId(ContextUtils.getCompanyId());
		view.setCreator(ContextUtils.getLoginName());
		view.setCreatorName(ContextUtils.getUserName());
		view.setCreatedTime(new Date());
		saveListView(view);
	}
	
	
	@Transactional(readOnly=false)
	private void saveJqGridProperty(ListView view,List<Object> jqGridPropertys){
		for(Object obj:jqGridPropertys){
			JqGridProperty jqGridProperty=(JqGridProperty)obj;
			if(StringUtils.isNotEmpty(jqGridProperty.getName())){
				jqGridProperty.setCompanyId(ContextUtils.getCompanyId());
				jqGridProperty.setListView(view);
				jqGridPropertyDao.save(jqGridProperty);
			}
		}
	}
	
	public List<ListView> getListViewsByCompany(){
		return viewDao.getListViewsByCompany();
	}
	@Transactional(readOnly=false)
	public String  delete(String viewIds){
		Integer canNum=0;
		if(viewIds!=null){
			String[] ids=viewIds.split(",");
			Integer[] nums=new Integer[ids.length];
			deleteListView(ids, canNum,nums);
			for(Integer num:nums){
				if(num!=null){
					canNum=num;
				}
			}
			String successStr=canNum!=0?canNum+"个删除成功;":"";
			String errorStr=(ids.length-canNum)!=0?(ids.length-canNum)+"个删除失败,数据表对应的列表只有一个或某页面视图引用了该视图":"";
			return successStr+errorStr;
		}
		return null;
	}
	
	@Transactional(readOnly=false)
	public String  deleteEnable(String viewIds){
		if(viewIds!=null){
			String[] ids=viewIds.split(",");
			for(String id : ids){
				deleteEnable(Long.valueOf(id));
			}
		}
		return null;
	}
	@Transactional(readOnly=false)
	public void  deleteEnable(Long id){
		ListView view = viewDao.get(id);
		List<ModulePage> pages=modulePageDao.getModulePagesByViewId(id);
		for(ModulePage mp : pages){
			buttonDao.deleteButtonByModulePage(mp.getId());
			modulePageDao.delete(mp);
		}
		List<ListColumn> listColumns=view.getColumns();
		for(ListColumn listColumn:listColumns){
			listColumnDao.delete(listColumn);
		}
		viewDao.delete(id);
	}
	
	public List<ListView> getListViewByDataTable(Long dtId){
		return viewDao.find("from ListView l where l.dataTable.id=?", dtId);
	}
	//删除列表视图，处理关联关系
	@Transactional(readOnly=false)
	private Integer deleteListView(String[] ids, Integer canNum,Integer[] nums){
		Long id = null;
		boolean result = false;
		for(int i = 0; i < ids.length; i++){
			if(ids[i] ==  null)  continue;
			id = Long.parseLong(ids[i]);
			List<ModulePage> pages=modulePageDao.getModulePagesByViewId(id);
			if(pages.size()==0){
				ListView view=viewDao.get(id);
				if(view==null){
					log.debug("ListView实体不能为null");
					throw new RuntimeException("ListView实体不能为null");
				}
				List<ListView> views=viewDao.getListViewByTabelId(view.getDataTable().getId());
				if(views.size()>1){
					//删除列表对应的字段
					List<ListColumn> listColumns=view.getColumns();
					for(ListColumn listColumn:listColumns){
						listColumnDao.delete(listColumn);
					}
					//删除列表
					viewDao.delete(id);
					ids[i] = null;
					result = true;
					canNum = canNum + 1;
					nums[canNum-1]=canNum;
				}
			}
		}
		if(result && ids.length != canNum) deleteListView(ids, canNum,nums);
		return  canNum;
	}
	
	public Boolean isCodeExist(String code,Long viewId){
		List<ListView> listViews=viewDao.getListViewByCode(code,viewId);
		return listViews.size()>0?true:false;
	}
	
	@Transactional(readOnly=false)
	public boolean defaultDisplaySet(Long viewId,Long dataTableId){
		ListView listView=viewDao.get(viewId);
		ListView defaultListView=viewDao.getDefaultDisplayListViewByTabelId(dataTableId);
		Boolean isHasDefaultDisplay;
		if(defaultListView!=null){
			isHasDefaultDisplay=false;
			if(listView.getDefaultListView()){
				listView.setDefaultListView(false);
				isHasDefaultDisplay=true;
			}
		}else{
			if(listView.getDefaultListView()){
				listView.setDefaultListView(false);
			}else{
				listView.setDefaultListView(true);
			}
			isHasDefaultDisplay=true;
			viewDao.save(listView);
		}
		return isHasDefaultDisplay;
	}
	
	public ListView getDefaultDisplay(Long dataTableId){
		return viewDao.getDefaultDisplayListViewByTabelId(dataTableId);
	}
	
	public ListView getListViewByCode(String code){
		return viewDao.getListViewByCode(code);
	}
	
	/**
	 * 获得标准或自定义的列表视图
	 * @param 
	 */
	
	public void getListViewPageByMenu(Page<ListView> page,Long menuId){
		viewDao.getListViewPageByMenu(page,menuId);
	}
	/**
	 * 通过系统Id得到列表视图
	 * @param 
	 */
	
	public List<ListView> getListViewsBySystem(Long menuId){
		Menu menu = menuManager.getRootMenu(menuId);
		return viewDao.getListViewsBySystem(menu.getId());
	}
	
	public List<ListView> getUnCompanyListViewsBySystem(Long menuId){
		Menu menu = menuManager.getRootMenu(menuId);
		return viewDao.getUnCompanyListViewsBySystem(menu.getId());
	}
	/**
	 * 复制列表
	 * @param 
	 */
	@Transactional(readOnly=false)
	public void savecopy(Long viewId, ListView view){
		ListView lv = getView(viewId);
		ListView copyLv=lv.clone();
		copyLv.setId(null);
		copyLv.setColumns(null);
		copyLv.setJqGridPropertys(null);
		copyLv.setCode(view.getCode());
		copyLv.setName(view.getName());
		copyLv.setCreatedTime(new Date());
		copyLv.setCreator(ContextUtils.getLoginName());
		copyLv.setCreatorName(ContextUtils.getUserName());
		viewDao.save(copyLv);
		List<ListColumn> listColumns = lv.getColumns();
		List<ListColumn> myColumns=new ArrayList<ListColumn>();
		for(ListColumn listC : listColumns){
			ListColumn col=listC.clone();
			col.setId(null);
			col.setListView(copyLv);
			listColumnDao.save(col);
			myColumns.add(col);
		}
		copyLv.setColumns(myColumns);
		List<JqGridProperty> props = lv.getJqGridPropertys();
		List<JqGridProperty> myProps=new ArrayList<JqGridProperty>();
		for(JqGridProperty prop : props){
			JqGridProperty col=prop.clone();
			col.setId(null);
			col.setListView(copyLv);
			jqGridPropertyDao.save(col);
			myProps.add(col);
		}
		copyLv.setJqGridPropertys(myProps);
		List<GroupHeader> groupHeaders = groupHeaderDao.getGroupHeadersByViewId(lv.getId());;
		for(GroupHeader header : groupHeaders){
			GroupHeader col=header.clone();
			col.setId(null);
			col.setListViewId(copyLv.getId());
			groupHeaderDao.save(col);
		}
		viewDao.save(copyLv);
	}
	/**
	 * 验证表单编号的唯一
	 * 
	 * @param 
	 */
	
	public Boolean isListCodeExist(String code, Long menuId){
		Menu menu = menuManager.getRootMenu(menuId);
		Long mId = menu.getId();
		String finalCode = code.trim(); 
		List<ListView> listViews = viewDao.getFormViewByCodeAndMenuId(finalCode, mId);
		return listViews.size()>0?true:false;
	}
	
	/**
	 * 删除属性自由扩展
	 * @param propertyId
	 */
	@Transactional(readOnly=false)
	public void deleteJqGridProperty(Long propertyId) {
		jqGridPropertyDao.delete(propertyId);
	}
	@Transactional(readOnly=false)
	public void createDefaultListView(DataTable dataTable,String code,String name,String remark,Long menuId){
		createDefaultListView(dataTable, code, name, remark, menuId, true);
	}
	
	@Transactional(readOnly=false)
	public void createDefaultListView(DataTable dataTable,String code,String name,String remark,Long menuId, boolean standard){
		ListView listView = new ListView();
		listView.setDataTable(dataTable);
		listView.setCode(code);
		listView.setName(name);
		listView.setRemark(remark);
		listView.setStandard(standard);
		listView.setMenuId(menuId);
		saveDefaultListView(listView, menuId);
		List<ListColumn> listColumns = new ArrayList<ListColumn>();
		List<TableColumn> columns = dataTableManager.getAllUnDeleteColumns(dataTable);
		for (TableColumn tableColumn : columns) {
			if((tableColumn.getDataType().equals(DataType.BLOB)) || (tableColumn.getDataType().equals(DataType.CLOB))){
				continue;
			}
			ListColumn listColumn = new ListColumn();
			listColumn.setCompanyId(tableColumn.getCompanyId());
			listColumn.setListView(listView);
			listColumn.setTableColumn(tableColumn);
			listColumn.setHeaderName(tableColumn.getAlias());
			listColumn.setDisplayOrder(tableColumn.getDisplayOrder());
			listColumn.setExportable(false);
			listColumn.setVisible(true);
			//以下几个字段设为不显示
			if("instance_id".equals(tableColumn.getDbColumnName())||"first_task_id".equals(tableColumn.getDbColumnName())
					||"id".equals(tableColumn.getDbColumnName())||"form_id".equals(tableColumn.getDbColumnName())){
				listColumn.setVisible(false);
			}
			listColumn.setEditable(false);
			listColumn.setTotal(false);
			listColumn.setQuerySettingName("不查询");
			listColumn.setQuerySettingValue("NONE");
			listColumn.setSortable(true);
			listColumn.setControlName("文本框");
			listColumn.setControlValue("TEXT");
			listColumnDao.save(listColumn);
			listColumns.add(listColumn);
		}
		listView.setColumns(listColumns);
		saveDefaultListView(listView, menuId);
	}
	
	public List<ListView> getListViewByTabelId(Long dataTableId){
		return viewDao.getListViewByTabelId(dataTableId);
	}
}
