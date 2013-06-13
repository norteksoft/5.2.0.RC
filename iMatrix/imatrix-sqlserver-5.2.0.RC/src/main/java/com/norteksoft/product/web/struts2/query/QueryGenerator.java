package com.norteksoft.product.web.struts2.query;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * private QueryBean getQuery(HttpServletRequest request, String sortQuery) {
		
		QueryGenerator queryGenerator = new QueryGenerator("from Person p ");
//		如果构造函数中传入的字符串中有where则不需要调用了
		queryGenerator.where();
//		调用一次and后对后面的字段都生效，说明这些字段都是以and连接
//		如果想改变则调用or()方法或者resetLogic()方法 
//		如果之前没有调用where(),则必须在添加一个比较条件后才能调用and()或者or()
		queryGenerator.and();
		
//		会根据value是否为空自动判断是否在查询语句中添加这个条件
//		不需要再写类似于value != null && "".equals(value)这样的代码以及字符串拼接
		queryGenerator.eq("p.belongDep.id", FieldType.LONG, request.getParameter("departmentId"));
		queryGenerator.like("p.name", request.getParameter("name"), MatchMode.ANYWHERE);
		queryGenerator.eq("p.sex", FieldType.STRING, request.getParameter("sex"));
//		FieldType.SQLDATE用于model的属性类型是java.sql.Date,FieldType.UTILDATE用于java.util.Date
		queryGenerator.ge("p.birthday", FieldType.UTILDATE, request.getParameter("birthdayStart"));
		queryGenerator.le("p.birthday", FieldType.UTILDATE, request.getParameter("birthdayStart"));
		
		
		这段代码演示了如何使用子查询就是添加括号
		 
		QueryGenerator subQuery = new QueryGenerator();
		//闭包的调用方式 使代码更简洁
		//p.a,p.b都是示意用的字段名
		subQuery.eq("p.a", FieldType.INTEGER, "1")
				.or()
				.le("p.b", FieldType.FLOAT, "1.2");
		queryGenerator.subQueryGenerator(subQuery);
		queryGenerator.le("p.c", FieldType.DOUBLE, "5.0");
		
		
//		追加order by语句
		queryGenerator.append(addTableAlias2OrderBy(sortQuery, "p"));
		return new QueryBean(queryGenerator.getQuery(), queryGenerator.getParametersAsArray());
	}
 * @author huhongchun
 *
 */
@SuppressWarnings({ "unchecked" })
public class QueryGenerator {
	private StringBuffer preparedQuery = new StringBuffer(); // 预处理的hql查询条件

	private StringBuffer orderBy = new StringBuffer(); // 排序条件

	private ArrayList parameters = new ArrayList(); // 属性值集合

	private boolean additionalCondition = true; // 是否需要增加其他查询条件，默认增加

	private String logicOperation=null;
	
	private String logic_or = " or "; // 保留前后的空格，注意字符长度的处理

	private String logic_and = " and ";

	private String eq = "=";

	private String ge = ">=";

	private String gt = ">";

	private String le = "<=";

	private String lt = "<";

	private String ne = "<>";

	private String space = " ";

	private String comma = ",";

	private String interrogation = " ? ";

	private String space_bracket = " (";

	private String bracket_space = ") ";

	private String order_by = " order by ";

	public QueryGenerator() {
	}

	public QueryGenerator(String initalHql) {
		// TODO Auto-generated constructor stub
		preparedQuery.append(initalHql);
	}

	/**
	 * 等于条件("=")
	 * 
	 * @param fieldName
	 *            String
	 * @param type
	 *            String
	 * @param value
	 *            String
	 * @return QueryGenerator
	 */
	public QueryGenerator eq(String fieldName, String type, String value) {
		return compareHelp(fieldName, type, eq, value);
	}

	/**
	 * 大于等于条件(">=")
	 * 
	 * @param fieldName
	 *            String
	 * @param type
	 *            String
	 * @param value
	 *            String
	 * @return QueryGenerator
	 */
	public QueryGenerator ge(String fieldName, String type, String value) {
		return compareHelp(fieldName, type, ge, value);
	}

	/**
	 * 大于条件(">")
	 * 
	 * @param fieldName
	 *            String
	 * @param type
	 *            String
	 * @param value
	 *            String
	 * @return QueryGenerator
	 */
	public QueryGenerator gt(String fieldName, String type, String value) {
		return compareHelp(fieldName, type, gt, value);
	}

	/**
	 * 小于等于条件("<=")
	 * 
	 * @param fieldName
	 *            String
	 * @param type
	 *            String
	 * @param value
	 *            String
	 * @return QueryGenerator
	 */
	public QueryGenerator le(String fieldName, String type, String value) {
		return compareHelp(fieldName, type, le, value);
	}

	/**
	 * 小于条件("<")
	 * 
	 * @param fieldName
	 *            String
	 * @param type
	 *            String
	 * @param value
	 *            String
	 * @return QueryGenerator
	 */
	public QueryGenerator lt(String fieldName, String type, String value) {
		return compareHelp(fieldName, type, lt, value);
	}

	/**
	 * 不等于条件("<>")
	 * 
	 * @param fieldName
	 *            String
	 * @param type
	 *            String
	 * @param value
	 *            String
	 * @return QueryGenerator
	 */
	public QueryGenerator ne(String fieldName, String type, String value) {
		return compareHelp(fieldName, type, ne, value);
	}

	/**
	 * 比较运算查询条件
	 * 
	 * @param fieldName
	 *            String
	 * @param type
	 *            String
	 * @param compare
	 *            String
	 * @param value
	 *            String
	 * @return QueryGenerator
	 */
	private QueryGenerator compareHelp(String fieldName, String type, String compare,
			String value) {
		if (additionalCondition && fieldName != null && value != null
				&& type != null && compare != null && !"".equals(fieldName)
				&& !"".equals(value) && !"".equals(type) && !"".equals(compare)) {
			//根据操作标志来决定是否添加and或者or来分割不同的条件
			logicHelp(logicOperation);
			
			preparedQuery.append(fieldName).append(compare).append(
					interrogation);
			preparedQuery.append(space);
			
			this.parameters.add(QueryUtil.getObjectByRealType(type, value));
			//this.arrangeProps(fieldName, type, HelpUtil.filter(value));
		}
		return this;
	}

	
	/**
	 * 空值条件
	 * 
	 * @param fieldName
	 *            String
	 * @return QueryGenerator
	 */
	public QueryGenerator isNull(String fieldName) {
		return nullHelp(fieldName, true);
	}

	/**
	 * 非空值条件
	 * 
	 * @param fieldName
	 *            String
	 * @return QueryGenerator
	 */
	public QueryGenerator isNotNull(String fieldName) {
		return nullHelp(fieldName, false);
	}

	/**
	 * 空值查询条件
	 * 
	 * @param fieldName
	 *            String
	 * @param isNull
	 *            boolean
	 * @return 
	 */
	private QueryGenerator nullHelp(String fieldName, boolean isNull) {
		if (additionalCondition && fieldName != null && !"".equals(fieldName)) {
//			根据操作标志来决定是否添加and或者or来分割不同的条件
			logicHelp(logicOperation);
			
			preparedQuery.append(fieldName);
			if (isNull) {
				preparedQuery.append(" is null ");
			} else {
				preparedQuery.append(" is not null ");
			}
		}
		return this;
	}

	/**
	 * 范围条件("between ? and ?")
	 * 
	 * @param fieldName
	 *            String
	 * @param type
	 *            String
	 * @param lo
	 *            String
	 * @param hi
	 *            String
	 * @return QueryGenerator
	 */
	public QueryGenerator between(String fieldName, String type, String lo, String hi) {
		return betweenHelp(fieldName, type, false, lo, hi);
	}

	/**
	 * 范围条件("not between ? and ?")
	 * 
	 * @param fieldName
	 *            String
	 * @param type
	 *            String
	 * @param lo
	 *            String
	 * @param hi
	 *            String
	 * @return QueryGenerator
	 */
	public QueryGenerator notBetween(String fieldName, String type, String lo, String hi) {
		return betweenHelp(fieldName, type, true, lo, hi);
	}

	/**
	 * 范围查询条件between和not between
	 * 
	 * @param fieldName
	 *            String
	 * @param type
	 *            String
	 * @param isNot
	 *            boolean
	 * @param lo
	 *            String
	 * @param hi
	 *            String
	 * @return QueryGenerator
	 */
	private QueryGenerator betweenHelp(String fieldName, String type, boolean isNot,
			String lo, String hi) {
		if (additionalCondition && fieldName != null && type != null
				&& lo != null && hi != null && !"".equals(fieldName)
				&& !"".equals(type) && !"".equals(lo) && !"".equals(hi)) {
			
//			根据操作标志来决定是否添加and或者or来分割不同的条件
			logicHelp(logicOperation);
			
			String keyWord = ""; // between的类型
			
			if (isNot) {
				keyWord = " not between ";
			} else {
				keyWord = " between ";
			}

			preparedQuery.append(fieldName).append(space).append(keyWord)
					.append(interrogation).append(logic_and).append(
							interrogation).append(space);
			this.parameters.add(QueryUtil.getObjectByRealType(type, lo));
			this.parameters.add(QueryUtil.getObjectByRealType(type, hi));
			//this.arrangeProps(fieldName, type, lo);
			//this.arrangeProps(fieldName, type, hi);
		}
		return this;
	}

	/**
	 * 模糊查询条件("like")
	 * 
	 * @param fieldName
	 *            String
	 * @param value
	 *            String
	 * @param matchMode
	 *            MatchMode
	 * @return QueryGenerator
	 */
	public QueryGenerator like(String fieldName, String value, MatchMode matchMode) {
		return likeHelp(fieldName, value, matchMode);
	}

	/**
	 * 模糊查询条件like
	 * 
	 * @param fieldName
	 *            String
	 * @param value
	 *            String
	 * @param matchMode
	 *            MatchMode
	 * @return 
	 */
	private QueryGenerator likeHelp(String fieldName, String value, MatchMode matchMode) {
		if (additionalCondition && fieldName != null && value != null
				&& !"".equals(fieldName) && !"".equals(value)) {
//			根据操作标志来决定是否添加and或者or来分割不同的条件
			logicHelp(logicOperation);
			
			preparedQuery.append(fieldName);
			preparedQuery.append(space);
			preparedQuery.append("like");
			preparedQuery.append(interrogation);

			
			this.parameters.add(matchMode.toMatchString(value));
			//this.arrangeProps(fieldName, "string", HelpUtil.filter(matchMode
			//		.toMatchString(value)));
		}
		return this;
	}

	/**
	 * 范围查询条件("in(?,?,?,...,?)")
	 * 
	 * @param fieldName
	 *            String
	 * @param type
	 *            String
	 * @param obj
	 *            Object
	 * @return QueryGenerator
	 */
	public QueryGenerator in(String fieldName, String type, Object obj) {
		return inHelp(fieldName, type, true, obj);
	}

	/**
	 * 范围查询条件("not in(?,?,?,...,?)")
	 * 
	 * @param fieldName
	 *            String
	 * @param obj
	 *            Object
	 * @param type
	 *            String
	 * @return QueryGenerator
	 */
	public QueryGenerator notIn(String fieldName, Object obj, String type) {
		return inHelp(fieldName, type, false, obj);
	}

	/**
	 * 范围查询条件in
	 * 
	 * @param fieldName
	 *            String
	 * @param type
	 *            String
	 * @param in
	 *            boolean
	 * @param obj
	 *            Object
	 * @return QueryGenerator
	 */
	private QueryGenerator inHelp(String fieldName, String type, boolean in, Object obj) {
		if (additionalCondition && fieldName != null && type != null
				&& !"".equals(fieldName) && !"".equals(type) && obj != null) {
//			根据操作标志来决定是否添加and或者or来分割不同的条件
			logicHelp(logicOperation);
			
			String keyWord = "";
			if (in) {
				keyWord = " in ";
			} else {
				keyWord = " not in ";
			}
			preparedQuery.append(fieldName);
			preparedQuery.append(keyWord);
			preparedQuery.append(space_bracket);
			preparedQuery.append(StringUtil.formInPrepared(obj));
			preparedQuery.append(bracket_space);

			Iterator it = QueryUtil.obj2Iterator(obj);
			while (it.hasNext()) {
				this.parameters.add(QueryUtil.getObjectByRealType(type, (String) it.next()));
				//this.arrangeProps(fieldName, type, HelpUtil.filter((String) it
						//.next()));
			}
		}
		return this;
	}
/**
 * in ? or not in ? 不形成in (?,?....?)的形式
 */
//	private void inHelp(String fieldName, String type, boolean in, Object[] obj) {
//		if (additionalCondition && fieldName != null && type != null
//				&& !"".equals(fieldName) && !"".equals(type) && obj != null) {
//			String keyWord = "";
//			if (in) {
//				keyWord = " in ";
//			} else {
//				keyWord = " not in ";
//			}
//			preparedQuery.append(fieldName);
//			preparedQuery.append(keyWord);
//			preparedQuery.append(interrogation);
//
//			this.parameters.add(obj);
//			//arrangeProps(fieldName, type, obj);
//		}
//	}
	/**
	 * 记录将要增加and条件,但是否真正添加得看后面的条件字段是否正确
	 * 
	 * @param filter
	 *            PreparedFilter
	 * @return PreparedFilter
	 */
	public QueryGenerator and() {
		logicOperation=logic_and;
		return this;
		//return logicHelp(logic_and);
	}

	/**
	 * 记录将要增加or条件,但是否真正添加得看后面的条件字段是否正确
	 * 
	 * @param queryGenerator
	 *            QueryGenerator
	 * @return QueryGenerator
	 */
	public QueryGenerator or() {
		logicOperation=logic_or;
		return this;
	}

	/**
	 * 增加逻辑条件
	 * 
	 * @param logic
	 *            String
	 * @param queryGenerator
	 *            QueryGenerator
	 * @return QueryGenerator
	 */
	private QueryGenerator logicHelp(String logic) {
		if (additionalCondition && logicOperation!=null) {
			preparedQuery.append(logic);
		}
		return this;
	}
/**
 * 清空逻辑操作标志 查询语句中的字段之间不会自动添加and或or关键字了
 * @return
 */
	public QueryGenerator resetLogic(){
		logicOperation=null;
		return this;
	}
	
	/**
	 * 添加where关键字
	 * @return
	 */
	public QueryGenerator where() {
		// if not exists where clause
		if (!StringUtil.findString(preparedQuery.toString(), "where", true)) {
			preparedQuery.append(" where 1=1 ");
		} else {
			preparedQuery.append(" and 1=1 ");
		}
		return this;
	}

	/**
	 * 增加子条件,相当于插入条件" and (查询条件)", 也就是增加优先级
	 * 
	 * @param queryGenerator
	 *            SqlqueryGenerator
	 * @return QueryGenerator
	 */
	public QueryGenerator subQueryGenerator(QueryGenerator queryGenerator) {
		if (additionalCondition && queryGenerator != null
				&& !"".equals(queryGenerator.getQuery())) {
//			根据操作标志来决定是否添加and或者or来分割不同的条件
			logicHelp(logicOperation);
			
			preparedQuery.append(space_bracket);

			preparedQuery.append(queryGenerator.getQuery()); 
			
			preparedQuery.append(bracket_space);
			
			this.copyParameters(queryGenerator); // 复制子查询的参数到主查询
			
		}
		return this;
	}

	private void copyParameters(QueryGenerator queryGenerator) {
		// TODO Auto-generated method stub
		Iterator it=queryGenerator.getParameters().iterator();
		while(it.hasNext()){
			this.parameters.add(it.next());
		}
	}

	/**
	 * 增加排序条件(order by)
	 * 
	 * @param fieldName
	 *            String
	 */
	public void orderBy(String fieldName) {
		if (orderBy.length() > 0) {
			orderBy.append(comma);
		}
		orderBy.append(fieldName);
	}

	/**
	 * 获取排序条件
	 * 
	 * @return String
	 */
	public String getOrderByStr() {
		return orderBy.toString();
	}

	/**
	 * 查询条件
	 * 
	 * @return String
	 */
	public String getQuery() {
		return preparedQuery.toString();
	}

	/**
	 * 排序条件
	 * 
	 * @return String
	 */
	public String getOrderByWithKey() {
		return order_by + orderBy.toString();
	}



	public Object[] getParametersAsArray() {
		return parameters.toArray();
	}
	/**
	 * 对象属性值
	 * 
	 * @return ArrayList
	 */
	public ArrayList getParameters() {
		return parameters;
	}

	/**
	 * 设置初始查询条件
	 * 
	 * @param condition
	 *            String
	 */
	public void setInitalQuery(String condition) {
		preparedQuery.append(condition);
	}

	/**
	 * 设置排序条件
	 * 
	 * @param str
	 *            String
	 */
	public void setOrderByStr(String str) {
		orderBy = new StringBuffer(str);
	}


	/**
	 * 设置属性值集合
	 * 
	 * @param parameters
	 *            ArrayList
	 */
	public void setParameters(ArrayList parameters) {
		this.parameters = parameters;
	}

	public boolean isAdditionalCondition() {
		return additionalCondition;
	}

	public void setAdditionalCondition(boolean additionalCondition) {
		this.additionalCondition = additionalCondition;
	}

	/**
	 * 将过滤器中的查询条件添加到给定的SQL语句
	 * 
	 * @param sql
	 *            String
	 * @return String
	 */
	public String concatQueryGenerator(String sql) {
		StringBuffer sqlBf = new StringBuffer();
		sqlBf.append(sql);

		if (!StringUtil.findString(sql, "where", true)) { // if not exists
															// where clause
			sqlBf.append(" where 1=1 ");
		}

		if (!"".equals(this.getQuery())) {
			sqlBf.append(this.getQuery()); // add query condition
		}
		if (!"".equals(this.getOrderByStr())) {
			sqlBf.append(this.getOrderByWithKey()); // add order by clause
		}
		return sqlBf.toString();
	}
	
	/**
	 * 在已有查询语句的结尾追加查询语句
	 * @param query
	 * @return
	 */
	public  QueryGenerator append(String query){
		this.preparedQuery.append(query);
		return this;
	}
}
