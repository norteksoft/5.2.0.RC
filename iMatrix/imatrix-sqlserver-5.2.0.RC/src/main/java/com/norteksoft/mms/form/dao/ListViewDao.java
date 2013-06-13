package com.norteksoft.mms.form.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.product.api.impl.WorkflowClientManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

import edu.emory.mathcs.backport.java.util.Arrays;

@Repository
public class ListViewDao extends HibernateDao<ListView, Long> {
	private Log log = LogFactory.getLog(WorkflowClientManager.class);
	public void getViewPage(Page<ListView> page, Long dataTableId) {
		String hql = " from ListView v where v.dataTable.id=?";
		this.findPage(page, hql, dataTableId);
	}
	
	public List<ListView> getListViewsByCompany(){
		String hql = " from ListView lv where lv.companyId=? order by lv.code";
		return this.find(hql, ContextUtils.getCompanyId());
	}
	public List<ListView> getListViewByCode(String code,Long viewId){
		if(viewId!=null){
			return find("from ListView lv where lv.code=? and lv.companyId=? and lv.id<>?",code,ContextUtils.getCompanyId(),viewId);
		}else{
			return find("from ListView lv where lv.code=? and lv.companyId=? ",code,ContextUtils.getCompanyId());
		}
	}
	public ListView getDefaultDisplayListViewByTabelId(Long dataTableId){
		String hql = " from ListView v where v.dataTable.id=? and v.defaultListView=? and v.companyId=?";
		List<ListView> views= this.find(hql, dataTableId,true,ContextUtils.getCompanyId());
		if(views.size()==0)return null;
		return views.get(0);
	}
	public void getListViewPageByMenu(Page<ListView> page, Long id) {
		   Long companyId= ContextUtils.getCompanyId();
			String hql = " from ListView lv where lv.companyId="+companyId +
					" and lv.menuId=?";
			this.searchPageByHql(page, hql, id);
		}
	
	public ListView getListViewByCode(String code) {
		String hql = "from ListView lv where lv.companyId=? and lv.code=?";
		if(ContextUtils.getCompanyId()==null){
			log.debug("companyId不能为null");
			throw new RuntimeException("companyId不能为null");
		}
		List<ListView> views = this.find(hql, ContextUtils.getCompanyId(), code);
		if(views.size() == 1) return views.get(0);
		return null;
	}
	public List<ListView> getListViewsBySystem(Long menuId){
		String hql = " from ListView lv where lv.companyId=? and lv.menuId=? order by lv.code";
		return this.find(hql, ContextUtils.getCompanyId(), menuId);
	}
	public List<ListView> getUnCompanyListViewsBySystem(Long menuId){
		String hql = " from ListView lv where lv.menuId=? order by lv.code";
		return this.findNoCompanyCondition(hql,  menuId);
	}
	public List<ListView> getFormViewByCodeAndMenuId(String code, Long mId){
		String hql = " from ListView lv where lv.companyId=? and lv.menuId=? and lv.code=? order by lv.code";
		return this.find(hql,  ContextUtils.getCompanyId(), mId, code);
	}
	
	public List<ListView> getListViewsBySystem(String... sysCodes){
		List<String> codes=Arrays.asList(sysCodes);
		StringBuilder hql = new StringBuilder();
		int len=0;
		if(codes!=null)len=codes.size();
		Object[] vals=new Object[len+1];
		hql.append(" from ListView lv where lv.companyId=?");// order by lv.code
		if(ContextUtils.getCompanyId()==null){
			log.debug("companyId不能为null");
			throw new RuntimeException("companyId不能为null");
		}
		vals[0]=ContextUtils.getCompanyId();
		if(codes!=null){
			int i=1;
			for(String code:codes){
				hql.append(" (lv.menu.code=?");
				hql.append(" or ");
				vals[i++]=code;
			}
			if(hql.toString().contains("or")){
				hql.replace(hql.lastIndexOf("or"), hql.length(), "");
				hql.append(")");
			}
		}
		hql.append(" order by lv.code");
		return this.find(hql.toString(), vals);
	}
	
	public List<ListView> getListViewByTabelId(Long dataTableId){
		String hql = " from ListView v where v.dataTable.id=? and v.companyId=?";
		return this.find(hql, dataTableId,ContextUtils.getCompanyId());
	}
}
