package com.norteksoft.acs.base.orm.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;
import org.springframework.util.Assert;

import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.BeanUtils;
import com.norteksoft.product.util.SearchUtils;

/**
 * Hibernate的范型基类.
 * 
 * 可以在service类中直接创建使用.也可以继承出DAO子类,在多个Service类中共享DAO操作.
 * 参考Spring2.5自带的Petlinc例子,取消了HibernateTemplate.
 * 通过Hibernate的sessionFactory.getCurrentSession()获得session,直接使用Hibernate原生API.
 *
 * @param <T> DAO操作的对象类型
 * @param <PK> 主键类型
 * 
 * @author huhongchun
 */
@SuppressWarnings("unchecked")
public class SimpleHibernateTemplate<T, PK extends Serializable> {

//	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected Log log = LogFactory.getLog(getClass());

	protected SessionFactory sessionFactory;

	protected Class<T> entityClass;

	public SimpleHibernateTemplate(SessionFactory sessionFactory, Class<T> entityClass) {
		this.sessionFactory = sessionFactory;
		this.entityClass = entityClass;
	}

	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void save(T entity) {
		Assert.notNull(entity);
		getSession().saveOrUpdate(entity);
		log.debug("save entity: " + entity);
	}

	public void delete(T entity) {
		Assert.notNull(entity);
		getSession().delete(entity);
		log.debug("delete entity: " + entity);
	}

	public void delete(PK id) {
		Assert.notNull(id);
		delete(get(id));
	}

	public List<T> findAll() {
		return findByCriteria();
	}

	public Page<T> findAll(Page<T> page) {
		return findByCriteria(page);
	}

	/**
	 * 按id获取对象.
	 */
	public T get(final PK id) {
		return (T) getSession().load(entityClass, id);
	}

	/**
	 * 按HQL查询对象列表.
	 * 
	 * @param hql hql语句
	 * @param values 数量可变的参数
	 */
	public List find(String hql, Object... values) {
		return createQuery(hql, values).list();
	}
	
	public List<T> findList(String hql, Object... values){
		return createQuery(hql, values).list();
	}

	/**
	 * 按HQL分页查询.
	 * 暂不支持自动获取总结果数,需用户另行执行查询.
	 * 
	 * @param page 分页参数.包括pageSize 和firstResult.
	 * @param hql hql语句.
	 * @param values 数量可变的参数.
	 * 
	 * @return 分页查询结果,附带结果列表及所有查询时的参数.
	 */
	public Page<T> find(Page<T> page, String hql, Object... values) {
		Assert.notNull(page);
		
		String newHql = createHqlAddOrderBy(hql, page);
		
		Query q = createQuery(newHql, values);

		if (page.isAutoCount()) {
			int pageNo=page.getPageNo();
			int pageSize=page.getPageSize();
			long totalCount = countHqlResult(newHql, values);
			long z=totalCount/pageSize;
			long y=totalCount%pageSize;
			long c=pageNo-z;
			page.setTotalCount(totalCount);
			if(y==0 && c==1){//该页没有数据转到上一页
				page.setPageNo(Integer.valueOf(pageNo-1));
			}
		}

		setPageParameter(q, page);
		List result = q.list();
		page.setResult(result);
		return page;
	}

	/**
	 * 按HQL查询唯一对象.
	 */
	public Object findUnique(String hql, Object... values) {
		return createQuery(hql, values).uniqueResult();
	}

	/**
	 * 按HQL查询Intger类形结果. 
	 */
	public Integer findInt(String hql, Object... values) {
		return (Integer) findUnique(hql, values);
	}

	/**
	 * 按HQL查询Long类型结果. 
	 */
	public Long findLong(String hql, Object... values) {
		return (Long) findUnique(hql, values);
	}

	/**
	 * 按Criterion查询对象列表.
	 * @param criterion 数量可变的Criterion.
	 */
	public List<T> findByCriteria(Criterion... criterion) {
		return createCriteria(criterion).list();
	}

	/**
	 * 按Criterion分页查询.
	 * @param page 分页参数.包括pageSize、firstResult、orderBy、asc、autoCount.
	 *             其中firstResult可直接指定,也可以指定pageNo.
	 *             autoCount指定是否动态获取总结果数.
	 *             
	 * @param criterion 数量可变的Criterion.
	 * @return 分页查询结果.附带结果列表及所有查询时的参数.
	 */
	public Page<T> findByCriteria(Page page, Criterion... criterion) {
		Assert.notNull(page);

		Criteria c = createCriteria(criterion);

		if (page.isAutoCount()) {
			page.setTotalCount(countQueryResult(page, c));
		}
		
		if (page.isOrderBySetted()) {
			if (page.getOrder().endsWith("asc")) {
				c.addOrder(Order.asc(page.getOrderBy()));
			} else {
				c.addOrder(Order.desc(page.getOrderBy()));
			}
		}
		//hibernate的firstResult的序号从0开始
		c.setFirstResult(page.getFirst()-1);
		c.setMaxResults(page.getPageSize());
		page.setResult(c.list());
		return page;
	}

	/**
	 * 按属性查找对象列表.
	 */
	public List<T> findByProperty(String propertyName, Object value) {
		Assert.hasText(propertyName);
		return createCriteria(Restrictions.eq(propertyName, value)).list();
	}

	/**
	 * 按属性查找唯一对象.
	 */
	public T findUniqueByProperty(String propertyName, Object value) {
		Assert.hasText(propertyName);
		return (T) createCriteria(Restrictions.eq(propertyName, value)).uniqueResult();
	}

	/**
	 * 根据查询函数与参数列表创建Query对象,后续可进行更多处理,辅助函数.
	 */
	public Query createQuery(String queryString, Object... values) {
		Assert.hasText(queryString);
		Query queryObject = getSession().createQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				queryObject.setParameter(i, values[i]);
			}
		}
		return queryObject;
	}

	/**
	 * 根据Criterion条件创建Criteria,后续可进行更多处理,辅助函数.
	 */
	public Criteria createCriteria(Criterion... criterions) {
		Criteria criteria = getSession().createCriteria(entityClass);
		for (Criterion c : criterions) {
			criteria.add(c);
		}
		return criteria;
	}

	/**
	 * 判断对象的属性值在数据库内是否唯一.
	 * 
	 * 在修改对象的情景下,如果属性新修改的值(value)等于属性原值(orgValue)则不作比较.
	 * 传回orgValue的设计侧重于从页面上发出Ajax判断请求的场景.
	 * 否则需要SS2里那种以对象ID作为第3个参数的isUnique函数.
	 */
	public boolean isPropertyUnique(String propertyName, Object newValue, Object orgValue) {
		if (newValue == null || newValue.equals(orgValue))
			return true;

		Object object = findUniqueByProperty(propertyName, newValue);
		return (object == null);
	}

	/**
	 * 通过count查询获得本次查询所能获得的对象总数.
	 * @return page对象中的totalCount属性将赋值.
	 */
	protected int countQueryResult(Page<T> page, Criteria c) {
		CriteriaImpl impl = (CriteriaImpl) c;

		// 先把Projection、ResultTransformer、OrderBy取出来,清空三者后再执行Count操作
		Projection projection = impl.getProjection();
		ResultTransformer transformer = impl.getResultTransformer();

		List<CriteriaImpl.OrderEntry> orderEntries = null;
		try {
			orderEntries = (List) BeanUtils.getFieldValue(impl, "orderEntries");
			BeanUtils.setFieldValue(impl, "orderEntries", new ArrayList());
		} catch (Exception e) {
			log.error("不可能抛出的异常", e);
		}

		// 执行Count查询
		int totalCount = (Integer) c.setProjection(Projections.rowCount()).uniqueResult();
		if (totalCount < 1)
			return -1;

		// 将之前的Projection和OrderBy条件重新设回去
		c.setProjection(projection);

		if (projection == null) {
			c.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		}
		if (transformer != null) {
			c.setResultTransformer(transformer);
		}

		try {
			BeanUtils.setFieldValue(impl, "orderEntries", orderEntries);
		} catch (Exception e) {
			log.error("不可能抛出的异常", e);
		}

		return totalCount;
	}
	
	
	/**
	 * 去除hql的select 子句，未考虑union的情况,用于pagedQuery.
	 *
	 */
	private static String removeSelect(String hql) {
		int beginPos = hql.toLowerCase().indexOf("from");
		return hql.substring(beginPos);
	}

	/**
	 * 去除hql的orderby 子句，用于pagedQuery.
	 *
	 */
	private static String removeOrders(String hql) {
		Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(hql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	/**
	 * 原生SQL查询, 没有完善，慎用。。。
	 * @return List<T>
	 */
	public List<T> findByJdbc(String sql, Object... values){
		SQLQuery sqlQuery = getSession().createSQLQuery(sql);
		if(values != null){
			for(int i = 0; i < values.length; i++){
				sqlQuery.setString(i, values[i].toString());
			}
		}
		List<T> results = sqlQuery.addEntity(entityClass).list();
		return results;
	}
	
	/**
	 * 原生SQL分页查询
	 */
	public void findPageByJdbc(Page<T> page, String sql, Object... values){
		SQLQuery sqlQuery = getSession().createSQLQuery(sql);
		if(values != null){
			for(int i = 0; i < values.length; i++){
				sqlQuery.setString(i, values[i].toString());
			}
		}
		
		page.setTotalCount(countSql(sql, values));
		
		List<T> results = sqlQuery.addEntity(entityClass).setFirstResult(page.getFirst()-1).setMaxResults(page.getPageSize()).list();
		page.setResult(results);
	}
	
	public int countSql(String sql, Object... values){
		String countQueryString = " select count(*) " + removeSelect(removeOrders(sql));
		SQLQuery query = getSession().createSQLQuery(countQueryString);
		if(values != null){
			for(int i = 0; i < values.length; i++){
				query.setString(i, values[i].toString());
			}
		}
		Number count = (Number) query.uniqueResult();
		//BigDecimal count = (BigDecimal)query.uniqueResult();
		return count.intValue();
	}
	
	/**
	 * HQL批量修改
	 */
	public int executeUpdate(String hql, Object... values){
		Query query = this.createQuery(hql, values);
		return query.executeUpdate();
	}	
	
	/**
	 * Hibernate 原生SQL，不支持有null参数的sql
	 * @param sql
	 * @param values
	 */
	public int executeSqlUpdate(String sql, Object...values){
		SQLQuery query = getSession().createSQLQuery(sql);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		return query.executeUpdate();
	}
	
	/**
	 * 直接执行原生SQL
	 * @param sql
	 */
	public int executeSqlUpdate(String sql){
		SQLQuery query = getSession().createSQLQuery(sql);
		return query.executeUpdate();
	}
	
	
	/**
	 * 向hql中设置orderBy条件(可以处理以有order by的hql和没有order by的hql)
	 * @param hql hql语句
	 * @param page 分页和排序参数
	 * @return
	 */
	protected String createHqlAddOrderBy(final String hql, final Page<T> page) {
		String newHql = hql;
		if(page.isOrderBySetted()) {
			String[] orderByArray = StringUtils.split(page.getOrderBy(), ',');
			String[] orderArray = StringUtils.split(page.getOrder(), ',');
			
			Assert.isTrue(orderByArray.length == orderArray.length, "分页多重排序参数中,排序字段与排序方向的个数不相等");
			
			String orderByStr = "";
			if(StringUtils.contains(newHql, "order by")){
				for (int i = 0; i < orderByArray.length; i++) {
					if((i + 1) == orderByArray.length) {
						orderByStr += getAlias(hql)+ "." + orderByArray[i].trim() + " " + orderArray[i].trim();
					} else {
						orderByStr += getAlias(hql)+ "." + orderByArray[i].trim() + " " + orderArray[i].trim() + ", ";
					}
				}
				newHql = StringUtils.substringBefore(newHql, "order by") + "order by " + orderByStr;
			}else{
				orderByStr = " order by ";
				for (int i = 0; i < orderByArray.length; i++) {
					if((i + 1) == orderByArray.length) {
						orderByStr += getAlias(hql)+ "." + orderByArray[i].trim() + " " + orderArray[i].trim();
					} else {
						orderByStr += getAlias(hql)+ "." + orderByArray[i].trim() + " " + orderArray[i].trim() + ", ";
					}
				}
				newHql += orderByStr;
			}
		}	
		log.debug("newHql =" + newHql);
		return newHql;
	}
	
	/*
	 * 解析 HQL from子句中的entity的别名 
	 */
	public String getAlias(String hql){
		String fromHql = StringUtils.substringAfter(hql, "from");
		fromHql = StringUtils.substringBefore(fromHql, "where");
		if(fromHql.indexOf(",")>=0){//TaskReport tr, WorkRepoet wr, ViewReport vr
			String[] fromHql1 = fromHql.split(",");
			return alias(fromHql1[0], fromHql);
		}else{//TaskReport tr与TaskReport tr inner join tr.workReport wr left outer join wr.viewReport vr
			if(fromHql.contains("join")){
				String[] fromParts = fromHql.trim().split("join");
				String hostTable = fromParts[0].trim();
				String[] tableAlias = null;
				if(hostTable.contains("inner")){
					tableAlias = hostTable.split("inner");
					return alias(tableAlias[0].trim(), fromHql);
				}else if(hostTable.contains("left outer")){
					tableAlias = hostTable.split("left outer");
					return alias(tableAlias[0].trim(), fromHql);
				}else if(hostTable.contains("right outer")){
					tableAlias = hostTable.split("right outer");
					return alias(tableAlias[0].trim(), fromHql);
				}else{
					return alias(hostTable, fromHql);
				}
			}else{
				return alias(fromHql.trim(), fromHql);
			}
		}
	}
	
	private String alias(String str, String fromHql){
		String[] strs = str.split(" ");
		for(int i = strs.length-1; i >= 0; i--){
			if(StringUtils.isNotBlank(strs[i])){
				log.debug(" *** entity alias ["+strs[i]+"] hql : [" + fromHql + "]");
				return strs[i].trim();
			}
		}
		return "";
	}
	
	/**
	 * 执行count查询获得本次Hql查询所能获得的对象总数.
	 * 
	 * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
	 */
	public int countHqlResult(final String hql, final Object... values) {
		int count = 0;
		String fromHql = hql;
		//select子句与order by子句会影响count查询,进行简单的排除.
		fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
		fromHql = StringUtils.substringBefore(fromHql, "order by");
//
//		String countHql = "select count(*) " + fromHql;
		
		String countHql = "select count(distinct "+getAlias(fromHql)+".id) " + fromHql;

		try {
			Object obj = findUnique(countHql, values);
			count = Integer.parseInt(obj.toString());
		} catch (Exception e) {
			throw new RuntimeException("hql can't be auto count, hql is:" + countHql, e);
		}
		return count;
	}	
	
	/**
	 * 设置分页参数到Query对象,辅助函数.
	 */
	protected Query setPageParameter(final Query q, final Page<T> page) {
		//hibernate的firstResult的序号从0开始
		q.setFirstResult(page.getFirst()-1);
		q.setMaxResults(page.getPageSize());
		return q;
	}
	
	public Page<T> searchPageByHql(final Page<T> page, final String hql, final Object... values) {
		log.debug("old search Hql:[" + hql + "]");
		Map<String, Object> result = SearchUtils.processSearchParameters(hql, true, values);
		log.debug("new search Hql:[" + result.get(SearchUtils.SQL_OR_HQL).toString() + "]");
		return findPage(page, 
				result.get(SearchUtils.SQL_OR_HQL).toString(), 
				(Object[])result.get(SearchUtils.PARAMETERS));
	}
	
	public Page<T> findPage(final Page<T> page, final String hql, final Object... values) {
		Assert.notNull(page, "page不能为空");

		String newHql = createHqlAddOrderBy(hql, page);
		
		Query q = createQuery(newHql, values);

		if (page.isAutoCount()) {
			int pageNo=page.getPageNo();
			int pageSize=page.getPageSize();
			long totalCount = countHqlResult(newHql, values);
			long z=totalCount/pageSize;
			long y=totalCount%pageSize;
			long c=pageNo-z;
			page.setTotalCount(totalCount);
			if(y==0 && c==1){//该页没有数据转到上一页
				page.setPageNo(Integer.valueOf(pageNo-1));
			}
		}

		setPageParameter(q, page);
		List result = q.list();
		page.setResult(result);
		return page;
	}
	
	public <X> List<X> findBySql(String sql, Object... values){
		SQLQuery sqlQuery = getSession().createSQLQuery(sql);
		if(values != null){
			for(int i = 0; i < values.length; i++){
				sqlQuery.setParameter(i, values[i]);
			}
		}
		return sqlQuery.list();
	}
}