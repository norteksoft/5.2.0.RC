package com.norteksoft.product.orm.hibernate;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.util.Assert;

import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.SearchUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;

/**
 * 封装SpringSide扩展功能的Hibernat DAO泛型基类.
 * 
 * 扩展功能包括分页查询,按属性过滤条件列表查询.
 * 可在Service层直接使用,也可以扩展泛型DAO子类使用,见两个构造函数的注释.
 * 
 * @param <T> DAO操作的对象类型
 * @param <PK> 主键类型
 * 
 */
public abstract class HibernateDao<T, PK extends Serializable> extends SimpleHibernateDao<T, PK> {
	
	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * 按HQL分页查询.
	 * 
	 * @param page 分页参数.
	 * @param hql hql语句.
	 * @param values 数量可变的查询参数,按顺序绑定.
	 * 
	 * @return 分页查询结果, 附带结果列表及所有查询时的参数.
	 */
	public Page<T> findPage(final Page<T> page, final String hql, final Object... values) {
		String newHql = addCompanyCondition(hql);
		return findPageNoCompanyCondition(page, newHql, values);
	}
	
	@SuppressWarnings("unchecked")
	public Page<T> findPageNoCompanyCondition(final Page<T> page, final String hql, final Object... values) {
		Assert.notNull(page, "page不能为空");
		String newHql = createHqlAddOrderBy(hql, page);
		
		Query q = createQuery(newHql, values);

		if (page.isAutoCount()) {
			int pageNo=page.getPageNo();
			int pageSize=page.getPageSize();
			long totalCount=getTotalCount(newHql,values);
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

	private long getTotalCount(String newHql,final Object... values){
		String listCode=Struts2Utils.getParameter("_list_code");
		if("export_data".equals(Struts2Utils.getParameter("exportParameters"))){//导出查询
			return 100000l;
		}else if(StringUtils.isNotEmpty(listCode)){
			ListViewManager listViewManager = (ListViewManager) ContextUtils.getBean("listViewManager");
			if(listViewManager==null){
				log.debug(" ListViewManager 为空");
				return  countHqlResult(newHql, values);
			}else{
				ListView listView=listViewManager.getListViewByCode(listCode);
				if(listView==null){
					log.debug(" ListView 为空");
					return  countHqlResult(newHql, values);
				}else{
					if(listView.getPagination()){
						if(StringUtils.isEmpty(Struts2Utils.getParameter("searchParameters"))){
							if(listView.getTotalable()){
								return  countHqlResult(newHql, values);
							}else{
								return  100000l;
							}
						}else{
							if(listView.getSearchTotalable()){
								return  countHqlResult(newHql, values);
							}else{
								return  100000l;
							}
						}
					}else{
						if(listView.getRowNum()==null){
							return  20l;
						}else{
							return  listView.getRowNum();
						}
					}
				}
			}
		}else{
			return countHqlResult(newHql, values);
		}
	}
	

	/**
	 * 执行count查询获得本次Hql查询所能获得的对象总数.
	 * 
	 * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
	 */
	public long countHqlResult(final String hql, final Object... values) {
		Long count = 0L;
		String fromHql = hql;
		//select子句与order by子句会影响count查询,进行简单的排除.
		fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
		fromHql = StringUtils.substringBefore(fromHql, "order by");
		
		// String countHql = "select count(*) " + fromHql;
		String countHql = "select count("+getAlias(fromHql)+".id) " + fromHql;
		if(hql.contains(" distinct ")){//判断用户的语句中是否有distinct，如果有则count时也要加上distinct，但会影响速度
			countHql = "select count(distinct "+getAlias(fromHql)+".id) " + fromHql;
		}

		try {
			count = findUnique(countHql, values);
		} catch (Exception e) {
			throw new RuntimeException("hql can't be auto count, hql is:" + countHql, e);
		}
		return count;
	}
	
	/**
	 * 原生SQL分页查询
	 * @param page
	 * @param sql
	 * @param values
	 * @return Page
	 */
	@SuppressWarnings("unchecked")
	public Page<Object> findPageBySql(Page<Object> page, String sql, Object... values){
		String newSql = sql;//addCompanyCondition(sql);
		String sqlOrderBy = createSqlAddOrderBy(newSql, page);
		log.debug(" newest query sql :[" + sqlOrderBy + "]");
		SQLQuery sqlQuery = getSession().createSQLQuery(sqlOrderBy);
		if(values != null){
			for(int i = 0; i < values.length; i++){
				sqlQuery.setParameter(i, values[i]);
			}
		}
		page.setTotalCount(getTotalCountSql(newSql, values));
		List<Object> results = sqlQuery.setFirstResult(page.getFirst()-1).setMaxResults(page.getPageSize()).list();
		page.setResult(results);
		return page;
	}
	
	private long getTotalCountSql(String sql,final Object... values){
		String listCode=Struts2Utils.getParameter("_list_code");
		if("export_data".equals(Struts2Utils.getParameter("exportParameters"))){//导出查询
			return 100000l;
		}else if(StringUtils.isNotEmpty(listCode)){
			ListViewManager listViewManager = (ListViewManager) ContextUtils.getBean("listViewManager");
			if(listViewManager==null){
				log.debug(" ListViewManager 为空");
				return  countSql(sql, values);
			}else{
				ListView listView=listViewManager.getListViewByCode(listCode);
				if(listView==null){
					log.debug(" ListView 为空");
					return  countSql(sql, values);
				}else{
					if(listView.getPagination()){
						if(StringUtils.isEmpty(Struts2Utils.getParameter("searchParameters"))){
							if(listView.getTotalable()){
								return  countSql(sql, values);
							}else{
								return  100000l;
							}
						}else{
							if(listView.getSearchTotalable()){
								return  countSql(sql, values);
							}else{
								return  100000l;
							}
						}
					}else{
						if(listView.getRowNum()==null){
							return  20l;
						}else{
							return  listView.getRowNum();
						}
					}
				}
			}
		}else{
			return countSql(sql, values);
		}
	}
	
	public int countSql(String sql, Object... values){
		String countQueryString = null;
		if(sql.contains(" distinct ")){ // sql语句中包含有 distinct
			countQueryString = "select count(*) from (" + removeOrders(sql) +") _default_table";
		}else{
			countQueryString = " select count(*) " + removeSelect(removeOrders(sql));
		}
		log.debug(" query count sql :[" + countQueryString + "]");
		SQLQuery query = getSession().createSQLQuery(countQueryString);
		if(values != null){
			for(int i = 0; i < values.length; i++){
				query.setParameter(i, values[i]);
			}
		}
		Number count = (Number) query.uniqueResult();
		//BigDecimal count = (BigDecimal)query.uniqueResult();
		return count.intValue();
	}
	
	/**
	 * 原生SQL查询
	 * @param sql
	 * @param values
	 * @return List
	 */
	@SuppressWarnings("unchecked")
	public List<Object> findBySql(String sql, Object... values){
		SQLQuery sqlQuery = getSession().createSQLQuery(sql);
		if(values != null){
			for(int i = 0; i < values.length; i++){
				sqlQuery.setParameter(i, values[i]);
			}
		}
		return sqlQuery.list();
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
	
	protected String createSqlAddOrderBy(final String sql, final Page<Object> page) {
		String newSql = sql;
		if(page.isOrderBySetted()) {
			String[] orderByArray = StringUtils.split(page.getOrderBy(), ',');
			String[] orderArray = StringUtils.split(page.getOrder(), ',');
			
			Assert.isTrue(orderByArray.length == orderArray.length, "分页多重排序参数中,排序字段与排序方向的个数不相等");
			
			StringBuilder orderByStr = new StringBuilder();
			if(StringUtils.contains(newSql, "order by")){
				for (int i = 0; i < orderByArray.length; i++) {
					if(i > 0)orderByStr.append(",");
					orderByStr.append(orderByArray[i].trim()).append(" ").append(orderArray[i].trim());
				}
				//FIXME 破坏了原有的排序序列
				newSql = StringUtils.substringBefore(newSql, "order by") + "order by " + orderByStr.toString();
			}else{
				orderByStr.append(" order by ");
				for (int i = 0; i < orderByArray.length; i++) {
					if(i > 0)orderByStr.append(",");
					orderByStr.append(orderByArray[i].trim()).append(" ").append(orderArray[i].trim());
				}
				newSql += orderByStr.toString();
			}
		}	
		log.debug("add order by sql :[" + newSql + "]");
		return newSql;
	}
	
	/**
	 * 设置分页参数到Query对象,辅助函数.
	 */
	protected Query setPageParameter(final Query q, final Page<T> page) {
		q.setFirstResult(page.getFirst() - 1);
		q.setMaxResults(page.getPageSize());
		return q;
	}
	
	/**
	 * 去除hql的orderby 子句，用于pagedQuery.
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
	 * 去除hql的select 子句，未考虑union的情况,用于pagedQuery.
	 */
	private static String removeSelect(String hql) {
		int beginPos = hql.toLowerCase().indexOf("from");
		return hql.substring(beginPos);
	}
	
	/**
	 * 搜索，直接从request中获取参数，
	 * 参数名字必须为：searchParameters
	 * @param page
	 * @param hql
	 * @param values
	 * @return
	 */
	public Page<T> searchPageByHql(final Page<T> page, final String hql, final Object... values) {
		log.debug("old search Hql:[" + hql + "]");
		Map<String, Object> result = SearchUtils.processSearchParameters(hql, true, values);
		log.debug("new search Hql:[" + result.get(SearchUtils.SQL_OR_HQL).toString() + "]");
		return this.findPage(page, 
				result.get(SearchUtils.SQL_OR_HQL).toString(), 
				(Object[])result.get(SearchUtils.PARAMETERS));
	}
	
	/**
	 * 搜索，直接从request中获取参数，
	 * 参数名字必须为：searchParameters
	 * @param page
	 * @param sql
	 * @param values
	 * @return
	 */
	public Page<Object> searchPageBySql(Page<Object> page, String sql, Object... values){
		log.debug("old search Sql:[" + sql + "]");
		Map<String, Object> result = SearchUtils.processSearchParameters(sql, false, values);
		log.debug("new search Sql:[" + result.get(SearchUtils.SQL_OR_HQL).toString() + "]");
		return this.findPageBySql(page, 
				result.get(SearchUtils.SQL_OR_HQL).toString(), 
				(Object[])result.get(SearchUtils.PARAMETERS));
	}
	
	/**
	 * 搜索，直接从request中获取参数，
	 * 参数名字必须为：searchParameters
	 * @param page
	 * @param hql
	 * @param values
	 * @return
	 */
	public Page<T> searchPageSubByHql(final Page<T> page, final String hql, final Object... values) {
		log.debug("old search Hql:[" + hql + "]");
		Map<String, Object> result = SearchUtils.processSearchSubParameters(hql, true, values);
		log.debug("new search Hql:[" + result.get(SearchUtils.SQL_OR_HQL).toString() + "]");
		return this.findPage(page, 
				result.get(SearchUtils.SQL_OR_HQL).toString(), 
				(Object[])result.get(SearchUtils.PARAMETERS));
	}
	
	/**
	 * 搜索，直接从request中获取参数，
	 * 参数名字必须为：searchParameters
	 * @param page
	 * @param sql
	 * @param values
	 * @return
	 */
	public Page<Object> searchPageSubBySql(Page<Object> page, String sql, Object... values){
		log.debug("old search Sql:[" + sql + "]");
		Map<String, Object> result = SearchUtils.processSearchSubParameters(sql, false, values);
		log.debug("new search Sql:[" + result.get(SearchUtils.SQL_OR_HQL).toString() + "]");
		return this.findPageBySql(page, 
				result.get(SearchUtils.SQL_OR_HQL).toString(), 
				(Object[])result.get(SearchUtils.PARAMETERS));
	}

	/**
	 * 用于Dao层子类使用的构造函数.
	 * 通过子类的泛型定义取得对象类型Class.
	 * eg.
	 * public class UserDao extends HibernateDao<User, Long>{
	 * }
	 */
	public HibernateDao() {
		super();
	}

	/**
	 * 用于省略Dao层, Service层直接使用通用HibernateDao的构造函数.
	 * 在构造函数中定义对象类型Class.
	 * eg.
	 * HibernateDao<User, Long> userDao = new HibernateDao<User, Long>(sessionFactory, User.class);
	 */
	public HibernateDao(final SessionFactory sessionFactory, final Class<T> entityClass) {
		super(sessionFactory, entityClass);
	}
}
