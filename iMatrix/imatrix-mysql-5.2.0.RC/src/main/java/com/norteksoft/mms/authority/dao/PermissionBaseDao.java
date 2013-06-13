package com.norteksoft.mms.authority.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.mms.authority.entity.Condition;
import com.norteksoft.mms.authority.entity.DataRule;
import com.norteksoft.mms.authority.entity.Permission;
import com.norteksoft.mms.authority.enumeration.FieldOperator;
import com.norteksoft.mms.authority.enumeration.PermissionAuthorize;
import com.norteksoft.mms.base.utils.PermissionUtils;
import com.norteksoft.mms.base.utils.PermissionUtils.UserInfo;
import com.norteksoft.mms.form.dao.DataTableDao;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.SearchUtils;
import com.norteksoft.wf.base.enumeration.LogicOperator;

public class PermissionBaseDao<T, PK extends Serializable> extends HibernateDao<T, PK>{

	@Autowired
	private DataTableDao dataTableDao;
	@Autowired
	private PermissionDao permissionDao;
	
	public Page<T> findPage(Page<T> page, String hql, Object... values) {
		AuthorityResult ar = getAuthorityDataRule(PermissionAuthorize.SEARCH);
		if(!ar.result) return super.findPage(page, hql, values);
		if(ar.dataRule == null) return page;
		ConditionResult cr = getPermissionHqlPamateters(hql, ar.dataRule.getConditions(), values);
		return super.findPage(page, cr.getHql(), cr.getPrameters());
	}
	
	/**
	 * 根据操作查询有权限的规则
	 * @param authority 查询, 查询, 修改, 删除
	 * @return
	 */
	public AuthorityResult getAuthorityDataRule(PermissionAuthorize authority){
		DataTable table = dataTableDao.getDataTableByEntity(entityClass.getName());
		List<Permission> ps = permissionDao.getPermissionsByDataTableId(table.getId());
		DataRule rule = null;
		UserInfo user = new UserInfo(ContextUtils.getLoginName());
		for(Permission p : ps){
			if((p.getAuthority() & authority.getCode()) == 0) continue;
			boolean haPermission = PermissionUtils.hasPermission(p.getItems(), user);
			if(haPermission){
				rule = p.getDataRule();
				break;
			}
		}
		if(ps.isEmpty() && rule == null) return new AuthorityResult(rule, false);
		return new AuthorityResult(rule, true);
	}

	public Page<T> searchPageByHql(Page<T> page, String hql, Object... values) {
		AuthorityResult ar = getAuthorityDataRule(PermissionAuthorize.SEARCH);
		if(!ar.result) return super.searchPageByHql(page, hql, values);
		if(ar.dataRule == null) return page;
		ConditionResult cr = getPermissionHqlPamateters(hql, ar.dataRule.getConditions(), values);
		Map<String, Object> result = SearchUtils.processSearchParameters( cr.getHql(), true, cr.getPrameters());
		return super.findPage(page, 
				result.get(SearchUtils.SQL_OR_HQL).toString(), 
				(Object[])result.get(SearchUtils.PARAMETERS));
	}

	public boolean deleteByPermission(PK id) {
		return this.deleteByPermission(get(id));
	}

	public boolean deleteByPermission(T entity) {
		if(deletePermission(entity)){
			super.delete(entity);
			return true;
		}
		return false;
	}

	public boolean saveByPermission(T entity) {
		if(savePermission(entity)){
			super.save(entity);
			return true;
		}
		return false;
	}
	
	protected boolean deletePermission(T entity){
		AuthorityResult ar = getAuthorityDataRule(PermissionAuthorize.DELETE);
		if(!ar.result) return true;
		if(ar.dataRule == null) return false;
		return PermissionUtils.entityPermission(entity, ar.dataRule.getConditions());
	}
	
	/**
	 * 实体保存权限
	 * @param entity
	 */
	protected boolean savePermission(T entity){
		try {
			AuthorityResult ar;
			if(BeanUtils.getProperty(entity, "id")==null){
				ar = getAuthorityDataRule(PermissionAuthorize.ADD);
			}else{
				ar = getAuthorityDataRule(PermissionAuthorize.UPDATE);
			}
			if(!ar.result) return true;
			if(ar.dataRule == null) return false;
			return PermissionUtils.entityPermission(entity, ar.dataRule.getConditions());
		} catch (Exception e) {
			logger.error("Get save permission error. ", e);
		}
		return false;
	}
	

	public boolean updatePermission(T entity){
		return savePermission(entity);
	}
	
	/**
	 * 根据HQL语句和条件集合拼接HQL，并重新组装条件
	 * @param hql  HQL 如： select x form XX x where x.p=? order by x.op
	 * @param conditions  集合
	 * @param prmts  HQL参数列表
	 * @return 
	 */
	public ConditionResult getPermissionHqlPamateters(String hql, List<Condition> conditions, Object... prmts){
		String alias = getAlias(hql); //HQL实体别名 
		StringBuilder newhql=new StringBuilder();
		List<Object> prameters=new ArrayList<Object>();
		for(Object o:prmts){
			prameters.add(o);
		}
		int i=0;
		for(Condition c:conditions){
			newhql.append(alias).append(".");
			newhql.append(c.getField());
			newhql.append(c.getOperator().sign);
			if(c.getDataType()==DataType.ENUM && needPlaceholder(c.getOperator())){//数据类型为枚举类型，并且不是包含和是否为空的关系
				newhql.append("? ");
				prameters.add(PermissionUtils.getValueByType(c.getConditionValue(),c.getEnumPath()));
			}else if(needPlaceholder(c.getOperator())){//条件为非包含 不是是否为空的条件时
				newhql.append("? ");
				prameters.add(PermissionUtils.getValueByType(c.getDataType(), c.getConditionValue()));
			}else if(containtCondition(c.getOperator())){//条件为包含关系
				newhql.append("? ");
				prameters.add(PermissionUtils.getValueByType(c.getDataType(), "%"+c.getConditionValue()+"%"));
			}
			if(i<conditions.size()-1){
				newhql.append(analysisLogicOperator(c.getLgicOperator()));
			}
			i++;
		}
		ConditionResult cr=new ConditionResult();
		if(StringUtils.isNotEmpty(newhql.toString())){
			String condition=" and ("+newhql.toString()+")";
			String where = " where ";
			String order_by = " order by ";
			StringBuilder hqlResult=new StringBuilder();
			if(hql.contains(where) && hql.contains(order_by)){
				String[] arr=hql.split(order_by);
				hqlResult.append(arr[0]);
				hqlResult.append(condition);
				hqlResult.append(order_by);
				hqlResult.append(arr[1]);
			}else if(hql.contains(where)){
				hqlResult.append(hql);
				hqlResult.append(condition);
			}else if(hql.contains(order_by)){
				String[] arr=hql.split(order_by);
				hqlResult.append(arr[0]);
				hqlResult.append(where);
				hqlResult.append(newhql.toString());
				hqlResult.append(order_by);
				hqlResult.append(arr[1]);
			}else{
				hqlResult.append(hql);
				hqlResult.append(where);
				hqlResult.append(newhql.toString());
			}
			cr.setHql(hqlResult.toString());
		}else{
			cr.setHql(hql);
		}
		cr.setPrameters(prameters.toArray());
		return cr;
	}
	
	private static boolean needPlaceholder(FieldOperator fo){
		return !(FieldOperator.CONTAIN==fo || FieldOperator.NOT_CONTAIN==fo 
				|| FieldOperator.IS_NULL==fo || FieldOperator.NOT_NULL==fo);
	}
	private static boolean containtCondition(FieldOperator fo){
		return (FieldOperator.CONTAIN==fo || FieldOperator.NOT_CONTAIN==fo );
	}
	
	private static String analysisLogicOperator(LogicOperator o){
		if(LogicOperator.AND.equals(o)){
			return " and ";
		}else {
			return " or ";
		}
	}
	
	static class AuthorityResult{
		DataRule dataRule;
		boolean result;
		public AuthorityResult(DataRule dataRule, boolean result) {
			this.dataRule = dataRule;
			this.result = result;
		}
	}
	
	public static class ConditionResult{
		private String hql;
		private Object[] prameters;
		public String getHql() {
			return hql;
		}
		public void setHql(String hql) {
			this.hql = hql;
		}
		public Object[] getPrameters() {
			return prameters;
		}
		public void setPrameters(Object[] prameters) {
			this.prameters = prameters;
		}
	}
	
}
