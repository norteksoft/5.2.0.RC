package com.norteksoft.product.api.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.springframework.util.Assert;

import com.norteksoft.product.api.DbService;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;

public class DbServiceImpl extends HibernateDao<Object, Long> implements DbService{

	public Object getObject(String className, Long dataId) {
		String hql = " from " + className + " o where o.id=?";
		return this.createQuery(hql, dataId).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Object> getObjects(String hql, Object... param) {
		return this.createQuery(hql, param).list();
	}

	public Page<Object> getObject(Page<Object> page,String hql,String conditionSql,List<Object> values) {
		hql=createConditionHql(hql,conditionSql);
		if(values.size()<=0){
			return this.findPage(page, hql);
		}else{
			Object[] objs=new Object[values.size()];
			for(int i=0;i<values.size();i++){
				objs[i]=values.get(i);
			}
			return this.findPage(page, hql,objs);
		}
	}
	
	private String createConditionHql(String hql,String condition){
		if(StringUtils.isEmpty(condition.trim()))return hql;
		if(hql.contains("where")){
			return hql + " and " + condition;
		}else{
			return hql + " where " + condition;
		}
	}
	
	public void delete(String className, Long id) {
		String hql = "delete from " + className + " o where o.id=?";
		this.createQuery(hql, id).executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	public Page<Object> findPageBySql(String sql, Page<Object> page, Object... objects) {
		Assert.hasText(sql, "sql不能为空");
		
		String newSql = createNewSql(sql, page);
		
		SQLQuery q = createSqlQuery(newSql, objects);

		if (page.isAutoCount()) {
			long totalCount = countSqlResult(newSql, objects);
			page.setTotalCount(totalCount);
		}

		q.setFirstResult(page.getFirst() - 1);
		q.setMaxResults(page.getPageSize());
		
		List<Object> result = q.list();
		page.setResult(result);
		return page;
	}
	
	private String createNewSql(String sql, Page<Object> page) {
		String newSql = sql;
		if (page.isOrderBySetted()) {
			String[] orderByArray = StringUtils.split(page.getOrderBy(), ',');
			String[] orderArray = StringUtils.split(page.getOrder(), ',');

			Assert.isTrue(orderByArray.length == orderArray.length, "分页多重排序参数中,排序字段与排序方向的个数不相等");

			String orderByStr = " order by ";
			for (int i = 0; i < orderByArray.length; i++) {
				if((i + 1) == orderByArray.length) {
					orderByStr += orderByArray[i].trim() + " " + orderArray[i].trim();
				} else {
					orderByStr += orderByArray[i].trim() + " " + orderArray[i].trim() + ", ";
				}
			}
			//System.out.println(orderByStr);
			newSql += orderByStr;
		}
		return newSql;
	}

	private SQLQuery createSqlQuery(final String sql, final Object... values) {
		SQLQuery query = getSession().createSQLQuery(sql);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				if(values[i]!=null){
					String str=values[i].getClass().getName();
					if(str.indexOf("[")==0){
						Object[] objs=(Object[])values[i];
						for(Object obj:objs){
							if(obj!=null){
								query.setParameter(i, obj);
							}
						}
					}else{
						query.setParameter(i, values[i]);
					}
					
				}
			}
		}
		return query;
	}
	
	private long countSqlResult(final String sql, final Object... values) {
		Long count = 0L;
		String fromSql = "";
		//select子句与order by子句会影响count查询,进行简单的排除.
		fromSql = "from " + StringUtils.substringAfter(sql, "from");
		fromSql = StringUtils.substringBefore(fromSql, "order by");

		String countSql = "select count(*) " + fromSql;

		try {
			SQLQuery sqlQuery = createSqlQuery(countSql, values);
			count = Long.parseLong(sqlQuery.uniqueResult().toString());
		} catch (Exception e) {
			throw new RuntimeException("sql can't be auto count, sql is:" + countSql, e);
		}
		return count;
	}
}
