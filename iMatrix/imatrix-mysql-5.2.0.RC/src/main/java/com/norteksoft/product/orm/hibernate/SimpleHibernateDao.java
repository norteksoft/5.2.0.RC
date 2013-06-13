package com.norteksoft.product.orm.hibernate;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ReflectionUtils;

/**
 * 封装Hibernate原生API的DAO泛型基类.
 * 
 * 可在Service层直接使用,也可以扩展泛型DAO子类使用.
 * 参考Spring2.5自带的Petlinc例子,取消了HibernateTemplate,直接使用Hibernate原生API.
 * 
 * @param <T> DAO操作的对象类型
 * @param <PK> 主键类型
 * 
 */
@SuppressWarnings({ "unchecked"})
public class SimpleHibernateDao<T, PK extends Serializable> {

	public static final String COMPANY_ID = "companyId";
	protected Log logger = LogFactory.getLog(getClass());

	protected SessionFactory sessionFactory;

	protected Class<T> entityClass;

	/**
	 * 用于Dao层子类使用的构造函数.
	 * 通过子类的泛型定义取得对象类型Class.
	 * eg.
	 * public class UserDao extends SimpleHibernateDao<User, Long>
	 */
	public SimpleHibernateDao() {
		this.entityClass = ReflectionUtils.getSuperClassGenricType(getClass());
	}

	/**
	 * 用于用于省略Dao层, 在Service层直接使用通用SimpleHibernateDao的构造函数.
	 * 在构造函数中定义对象类型Class.
	 * eg.
	 * SimpleHibernateDao<User, Long> userDao = new SimpleHibernateDao<User, Long>(sessionFactory, User.class);
	 */
	public SimpleHibernateDao(final SessionFactory sessionFactory, final Class<T> entityClass) {
		this.sessionFactory = sessionFactory;
		this.entityClass = entityClass;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * 采用@Autowired按类型注入SessionFactory,当有多个SesionFactory的时候Override本函数.
	 */
	@Autowired
	public void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * 取得当前Session.
	 */
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * 保存新增或修改的对象.
	 */
	public void save(final T entity) {
		Assert.notNull(entity, "entity不能为空");
		setEntityCompanyId(entity);
		getSession().saveOrUpdate(entity);
		logger.debug("save entity: {"+entity+"}");
	}
	/**
	 * 保存新增或修改的对象.
	 */
	public void saveNoCompany(final T entity) {
		Assert.notNull(entity, "entity不能为空");
		getSession().saveOrUpdate(entity);
		logger.debug("save entity: {"+entity+"}");
	}

	/**
	 * 删除对象.
	 * 
	 * @param entity 对象必须是session中的对象或含id属性的transient对象.
	 */
	public void delete(final T entity) {
		Assert.notNull(entity, "entity不能为空");
		getSession().delete(entity);
		logger.debug("delete entity: {"+entity+"}");
	}

	/**
	 * 按id删除对象.
	 */
	public void delete(final PK id) {
		Assert.notNull(id, "id不能为空");
		delete(get(id));
		logger.debug("delete entity: {"+entityClass.getSimpleName()+"},id is {"+id+"}");
	}

	/**
	 * 按id获取对象.
	 */
	public T get(final PK id) {
		Assert.notNull(id, "id不能为空");
		return (T) getSession().load(entityClass, id);
	}

	
	/**
	 *	获取全部对象.
	 */
	public List<T> getAll() {
		return find();
	}
	
	/**
	 * 按Criteria查询对象列表.
	 * 
	 * @param criterions 数量可变的Criterion.
	 */
	public List<T> find(final Criterion... criterions) {
		return createCriteria(criterions).list();
	}

	/**
	 * 按Criteria查询唯一对象.
	 * 
	 * @param criterions 数量可变的Criterion.
	 */
	public T findUnique(final Criterion... criterions) {
		return (T) createCriteria(criterions).uniqueResult();
	}

	/**
	 * 按属性查找唯一对象,匹配方式为相等.
	 */
	public T findUniqueBy(final String propertyName, final Object value) {
		Assert.hasText(propertyName, "propertyName不能为空");
		Criterion criterion = Restrictions.eq(propertyName, value);
		return (T) createCriteria(criterion).uniqueResult();
	}

	/**
	 * 根据Criterion条件创建Criteria.
	 * 
	 * 本类封装的find()函数全部默认返回对象类型为T,当不为T时使用本函数.
	 * 
	 * @param criterions 数量可变的Criterion.
	 */
	public Criteria createCriteria(final Criterion... criterions) {
		Criteria criteria = getSession().createCriteria(entityClass);
		for (Criterion c : criterions) {
			criteria.add(c);
		}
		criteria.add(Restrictions.eq(COMPANY_ID, getCompanyId()));
		return criteria;
	}

	/**
	 * 按HQL查询对象列表.
	 * 
	 * @param values 数量可变的参数,按顺序绑定.
	 */
	public <X> List<X> find(final String hql, final Object... values) {
		String newHql = addCompanyCondition(hql);
		return createQuery(newHql, values).list();
	}
	
	/**
	 * 按HQL查询对象列表.  不区分公司
	 */
	public <X> List<X> findNoCompanyCondition(final String hql, final Object... values) {
		return createQuery(hql, values).list();
	}

	public <X> List<X> list(final String hql, int topSize, final Object... values) {
		String newHql = addCompanyCondition(hql);
		return createQuery(newHql, values).setFirstResult(0).setMaxResults(topSize).list();
	}

	/**
	 * 按HQL查询唯一对象.
	 * 
	 * @param values 数量可变的参数,按顺序绑定.
	 */
	public <X> X findUnique(final String hql, final Object... values) {
		String newHql = addCompanyCondition(hql);
		return (X) createQuery(newHql, values).uniqueResult();
	}
	
	/**
	 * 按HQL查询唯一对象. 不区分公司
	 */
	public <X> X findUniqueNoCompanyCondition(final String hql, final Object... values) {
		return (X) createQuery(hql, values).uniqueResult();
	}

	/**
	 * 执行HQL进行批量修改/删除操作.
	 */
	public int batchExecute(final String hql, final Object... values) {
		String newHql = addCompanyCondition(hql);
		return createQuery(newHql, values).executeUpdate();
	}

	/**
	 * 根据查询HQL与参数列表创建Query对象.
	 * 
	 * 本类封装的find()函数全部默认返回对象类型为T,当不为T时使用本函数.
	 * 
	 * @param values 数量可变的参数,按顺序绑定.
	 */
	public Query createQuery(final String queryString, final Object... values) {
		Assert.hasText(queryString, "queryString不能为空");
		Query query = getSession().createQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		return query;
	}

	/**
	 * 初始化对象.
	 * 使用load()方法得到的仅是对象Proxy, 在传到View层前需要进行初始化.
	 * 只初始化entity的直接属性,但不会初始化延迟加载的关联集合和属性.
	 * 如需初始化关联属性,可实现新的函数,执行:
	 * Hibernate.initialize(user.getRoles())，初始化User的直接属性和关联集合.
	 * Hibernate.initialize(user.getDescription())，初始化User的直接属性和延迟加载的Description属性.
	 */
	public void initEntity(T entity) {
		Hibernate.initialize(entity);
	}

	/**
	 * @see #initEntity(Object)
	 */
	public void initEntity(List<T> entityList) {
		for (T entity : entityList) {
			Hibernate.initialize(entity);
		}
	}

	/**
	 * 为Query添加distinct transformer.
	 */
	public Query distinct(Query query) {
		query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return query;
	}

	/**
	 * 为Criteria添加distinct transformer.
	 */
	public Criteria distinct(Criteria criteria) {
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return criteria;
	}

	/**
	 * 通过Set将不唯一的对象列表唯一化.
	 * 主要用于HQL/Criteria预加载关联集合形成重复记录,又不方便使用distinct查询语句时.
	 */
	public <X> List<X> distinct(List list) {
		Set<X> set = new LinkedHashSet<X>(list);
		return new ArrayList<X>(set);
	}

	/**
	 * 取得对象的主键名.
	 */
	public String getIdName() {
		ClassMetadata meta = getSessionFactory().getClassMetadata(entityClass);
		return meta.getIdentifierPropertyName();
	}
	
	/**
	 * 设置公司ID
	 * @param t
	 */
	private void setEntityCompanyId(T t){
		try {
			Method method = t.getClass().getMethod("getCompanyId");
			Object obj = method.invoke(t);
			if(obj == null){
				method = t.getClass().getMethod("setCompanyId", Long.class);
				method.invoke(t, getCompanyId());
			}
		} catch (Exception e) {
			logger.error("为["+t.getClass()+"]设置公司ID错误 ... ", e);
			throw new RuntimeException(e);
		}
	}
	
	public  Long getCompanyId(){
		Long companyId=ContextUtils.getCompanyId();
		return companyId;
	}
	
	/*
	 * 解析 HQL from子句中的entity的别名 
	 */
	public String getAlias(String hql){
		String order_by = "order by";
		String where = "where";
		String from = "from";
		if(!hql.contains(from) && hql.contains("FROM")) from = "FROM";
		if(!hql.contains(where) && hql.contains("WHERE")) where = "WHERE";
		if(!hql.contains(order_by) && hql.contains("ORDER BY")) order_by = "ORDER BY";
		
		String fromHql = StringUtils.substringAfter(hql, from);
		fromHql = StringUtils.substringBefore(fromHql, order_by);
		fromHql = StringUtils.substringBefore(fromHql, where);
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
				logger.debug(" *** entity alias ["+strs[i]+"] hql : [" + fromHql + "]");
				return strs[i].trim();
			}
		}
		return "";
	}
	
	protected String addCompanyCondition(final String hql){
		logger.debug("**before add company id condition :["+hql+"]**");
		if(isUpdateHql(hql)){
			return addCompanyConditionForUpdate(hql);
		}
		String order_by = "order by";
		String where = "where";
		String from = "from";
		if(!hql.contains(from) && hql.contains("FROM")) from = "FROM";
		if(!hql.contains(where) && hql.contains("WHERE")) where = "WHERE";
		if(!hql.contains(order_by) && hql.contains("ORDER BY")) order_by = "ORDER BY";
		
		StringBuilder newHql = new StringBuilder();
		// 已经包含  companyId
		if(StringUtils.substringAfter(hql, from).contains(COMPANY_ID)) return hql;
		// 包含 order by
		String orderBy = "";
		if(hql.contains(order_by)){
			orderBy = " order by " + StringUtils.substringAfter(hql, order_by);
		}
		// 包含 where 子句
		if(hql.contains(where)){
			newHql.append(StringUtils.substringBefore(hql, where));
			String whereCondition = StringUtils.substringBefore(hql, order_by);
			whereCondition = StringUtils.substringAfter(whereCondition, where);
			newHql.append(" where (").append(whereCondition);
			newHql.append(")").append(" and ");
			newHql.append(getAlias(hql)).append(".").append(COMPANY_ID).append("=");
			newHql.append(getCompanyId());
			newHql.append(orderBy);
		}else{
			// 不包含 where , 有 order by
			if(StringUtils.isNotBlank(orderBy)){
				newHql.append(StringUtils.substringBefore(hql, order_by));
				newHql.append(" where ");
				newHql.append(getAlias(hql)).append(".").append(COMPANY_ID).append("=");
				newHql.append(getCompanyId());
				newHql.append(orderBy);
			}else{// 不包含 where , 没有 order by
				newHql.append(hql).append(" where ");
				newHql.append(getAlias(hql)).append(".").append(COMPANY_ID).append("=");
				newHql.append(getCompanyId());
			}
		}
		logger.debug("**after add company id condition :["+newHql+"]**");
		return newHql.toString();
	}
	
	private String addCompanyConditionForUpdate(String hql) {
		logger.debug("**before add company id condition for update:["+hql+"]**");
		String where = "where";
		if(!hql.contains(where) && hql.contains("WHERE")) where = "WHERE";
		
		StringBuilder newHql = new StringBuilder();
		if(hql.contains(where)){
			newHql.append(StringUtils.substringBefore(hql, where));
			String whereCondition = StringUtils.substringAfter(hql, where);
			newHql.append(" where (").append(whereCondition);
			newHql.append(")").append(" and ");
			newHql.append(COMPANY_ID).append("=");
			newHql.append(getCompanyId());
		}else{
			newHql.append(hql).append(" where ");
			newHql.append(COMPANY_ID).append("=");
			newHql.append(getCompanyId());
		}
		logger.debug("**after add company id condition for update:["+newHql+"]**");
		return newHql.toString();
	}

	private boolean isUpdateHql(String hql){
		return hql.contains("update ") || hql.contains("UPDATE ") ||
				hql.contains("delete ") || hql.contains("DELETE ");
	}
	
}