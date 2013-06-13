package com.norteksoft.mms.form.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class FormViewDao extends HibernateDao<FormView, Long> {

	public void getFormViewPage(Page<FormView> page, DataTable dataTable) {
		String hql = " from FormView fv where fv.dataTable.id=?";
		this.findPage(page, hql, dataTable.getId());
	}
	public void getFormViewPage(Page<FormView> page, Long dataTableId) {
		String hql = " from FormView fv where fv.dataTable.id=?";
		this.findPage(page, hql, dataTableId);
	}
	public void getFormViewPageByMenu(Page<FormView> page, Long id) {
	   Long companyId= ContextUtils.getCompanyId();
		String hql = " from FormView fv where fv.companyId="+companyId +
				" and fv.menuId=?";
		this.searchPageByHql(page, hql, id);
	}
	public List<FormView> getViewsByCodeOrderByVersion(String code) {
		String hql = " from FormView fv where fv.code=? and fv.companyId=? and fv.version is not null order by fv.version desc";
		return this.find(hql, code, ContextUtils.getCompanyId());
	}
	public List<FormView> getFormViewsByCompany(){
		String hql = " from FormView fv where fv.companyId=? order by fv.code";
		return this.find(hql, ContextUtils.getCompanyId());
	}

	public FormView getFormViewByCodeAndVersion(String code, Integer version) {
		if(ContextUtils.getCompanyId()==null){throw new RuntimeException("根据编码和版本查询表单时，公司id不能为null");}
		return getFormViewByCodeAndVersion(code,version,ContextUtils.getCompanyId());
	}
	public FormView getUnCompanyFormViewByCodeAndVersion(String code, Integer version) {
		String hql = " from FormView fv where fv.code=? and fv.version=? order by fv.code";
		List<FormView> views= this.findNoCompanyCondition(hql, code,version);
		if(views.size()>0)return views.get(0);
		return null;
	}

	public FormView getFormViewByCodeAndVersion(String code, Integer version,
			Long companyId) {
		String hql = " from FormView fv where fv.code=? and fv.version=? and fv.companyId=? order by fv.code";
		return this.findUnique(hql, code,version,companyId);
	}
	
	public List<FormView> getFormViewsByCompany(Long companyId){
		String hql = " from FormView fv where fv.companyId=? and fv.formState=? order by fv.code";
		return this.find(hql, companyId,DataState.ENABLE);
	}
	
	public List<FormView> getFormViewByCode(String code, Long formId){
		if(formId != null){
			String hql =" from FormView fv where fv.code=? and fv.companyId=? and fv.id<>?";
			return this.find(hql, code,ContextUtils.getCompanyId(),formId);
		}else{
			String hql =" from FormView fv where fv.code=? and fv.companyId=? ";
			return this.find(hql, code,ContextUtils.getCompanyId());
		}
	}
	
	public List<FormView> getFormViewsByMenu(Long menuId){
		String hql = " from FormView fv where fv.companyId=? and fv.menuId=? and fv.formState=? order by fv.code";
		return this.find(hql, ContextUtils.getCompanyId(), menuId,DataState.ENABLE);
	}
	public List<FormView> getUnCompanyFormViewsBySystem(Long menuId){
		String hql = " from FormView fv where fv.menuId=? order by fv.code";
		return this.findNoCompanyCondition(hql,  menuId);
	}
	
	public FormView getHighViewByCode(String code) {
		String hql = " from FormView fv where fv.code=? and fv.companyId=? and fv.formState=? and fv.version is not null order by fv.version desc";
		List<FormView> list=this.find(hql, code, ContextUtils.getCompanyId(),DataState.ENABLE);
		if(list.size()>0)return list.get(0);
		return null;
	}
	//根据表单名称查询表单，bkyoa更新数据时用到
	public FormView getFormViewByName(String formName){
		String hql = " from FormView fv where fv.name=? and fv.companyId=? and fv.formState=? and fv.version is not null order by fv.version desc";
		List<FormView> list=this.find(hql, formName, ContextUtils.getCompanyId(),DataState.ENABLE);
		if(list.size()>0)return list.get(0);
		return null;
	}
	/**
	 * 取出所有标准并启用的表单
	 * @return
	 */
	public List<FormView> getAllStandardFormView() {
		String hql = " from FormView fv where fv.standard=? and fv.formState=? order by fv.code";
		return this.findNoCompanyCondition(hql, true,DataState.ENABLE);
	}
}
