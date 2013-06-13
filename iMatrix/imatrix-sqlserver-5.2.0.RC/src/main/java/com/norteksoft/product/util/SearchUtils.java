package com.norteksoft.product.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.product.enumeration.QueryConditionProperty;
import com.norteksoft.product.web.struts2.Struts2Utils;

public class SearchUtils {
	
	private static SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final String SEARCH_STRING_NAME = "searchParameters";
	public static final String SQL_OR_HQL = "_sql_or_hql";
	public static final String PARAMETERS = "_parameters";
	private static Log log = LogFactory.getLog(SearchUtils.class);

	/**
	 * 处理查询参数
	 * @param sqlOrHql 查询语句
	 * @param isHql 是否原生sql
	 * @param values 原有条件
	 * @return
	 */
	public static Map<String, Object> processSearchParameters(String sqlOrHql, boolean isHql, Object... values){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(SQL_OR_HQL, sqlOrHql);
		result.put(PARAMETERS, values);
		String searchParameters = Struts2Utils.getParameter(SEARCH_STRING_NAME);
		log.debug(" *** search parameters: /*" + searchParameters + "*/ ***");
		if(StringUtils.isNotBlank(searchParameters)){
			result = processQuerySentence(sqlOrHql, isHql, searchParameters, values);
		}
		return result;
	}
	/**
	 * 处理查询参数
	 * @param sqlOrHql 查询语句
	 * @param isHql 是否原生sql
	 * @param values 原有条件
	 * @return
	 */
	public static Map<String, Object> processSearchSubParameters(String sqlOrHql, boolean isHql, Object... values){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(SQL_OR_HQL, sqlOrHql);
		result.put(PARAMETERS, values);
		String searchSubParameters = getSearchSubParameters(Struts2Utils.getParameter(SEARCH_STRING_NAME),isHql);
		log.debug(" *** search sub parameters: /*" + searchSubParameters + "*/ ***");
		if(StringUtils.isNotBlank(searchSubParameters)){
			result = processQuerySentence(sqlOrHql, isHql, searchSubParameters, values);
		}
		return result;
	}
	
	private static String getSearchSubParameters(String searchParameters, boolean isHql){
		String parameter="";
		if(StringUtils.isNotEmpty(searchParameters)&&searchParameters.contains("$")){
			MapType mt = TypeFactory.defaultInstance().constructMapType(
					HashMap.class, QueryConditionProperty.class, String.class);
			CollectionType ct = TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, mt);
			List<Map<QueryConditionProperty,String>> prms = JsonParser.json2Object(ct, searchParameters);
			String propName="";
			String dbName="";
			for(Map<QueryConditionProperty,String> obj:prms){
				propName=obj.get(QueryConditionProperty.propName);
				dbName=obj.get(QueryConditionProperty.dbName);
				if(isHql){
					if(propName.startsWith("$")){
						if(StringUtils.isNotEmpty(parameter)){
							parameter+=",";
						}
						parameter+=packagingParameter(obj);
					}
				}else{
					if(("null".equals(dbName)&&propName.startsWith("$")) || (!"null".equals(dbName)&&dbName.startsWith("$"))){
						if(StringUtils.isNotEmpty(parameter)){
							parameter+=",";
						}
						parameter+=packagingParameter(obj);
					}
				}
			}
			if(StringUtils.isNotEmpty(parameter)){
				parameter="["+parameter+"]";
			}
		}
		return parameter;
	}
	
	private static String packagingParameter(Map<QueryConditionProperty,String> obj){
		String parameter="";
		for(QueryConditionProperty prop : QueryConditionProperty.values()){
			if(StringUtils.isNotEmpty(parameter)){
				parameter+=",";
			}
			String name=prop.name();
			parameter+="\""+name+"\""+":"+"\"";
			String value=obj.get(prop);
			if(StringUtils.isNotEmpty(value)){
				parameter+=value;
			}
			parameter+="\"";
		}
		if(StringUtils.isNotEmpty(parameter)){
			parameter="{"+parameter+"}";
		}
		return parameter;
	}
	
//	public static void main(String[] args) {
		// is null
//		String s = "[{leftBracket:\"\",propName:\"vin.inner_color\",enumName:\"\",optSign:\"is null\",propValue:\"\",rightBracket:\"\",joinSign:\"and\",dataType:\"STRING\"},{leftBracket:\"\",propName:\"vin.dispose_result\",enumName:\"com.norteksoft.cbm.base.enumeration.SpecialCarStateEnum\",optSign:\"=\",propValue:\"NODISPOSAL\",rightBracket:\"\",joinSign:\"and\",dataType:\"ENUM\"}]";
//		String s = "[{leftBracket:\"\",propName:\"vin.dispose_result\",enumName:\"com.norteksoft.cbm.base.enumeration.SpecialCarStateEnum\",optSign:\"=\",propValue:\"NODISPOSAL\",rightBracket:\"\",joinSign:\"and\",dataType:\"ENUM\"},{leftBracket:\"\",propName:\"vin.inner_color\",enumName:\"\",optSign:\"is null\",propValue:\"\",rightBracket:\"\",joinSign:\"and\",dataType:\"STRING\"}]";
		// is not null
//		String s = "[{leftBracket:\"\",propName:\"vin.inner_color\",enumName:\"\",optSign:\"is not null\",propValue:\"\",rightBracket:\"\",joinSign:\"and\",dataType:\"STRING\"},{leftBracket:\"\",propName:\"vin.dispose_result\",enumName:\"com.norteksoft.cbm.base.enumeration.SpecialCarStateEnum\",optSign:\"=\",propValue:\"NODISPOSAL\",rightBracket:\"\",joinSign:\"and\",dataType:\"ENUM\"}]";
//		String s = "[{leftBracket:\"\",propName:\"vin.dispose_result\",enumName:\"com.norteksoft.cbm.base.enumeration.SpecialCarStateEnum\",optSign:\"=\",propValue:\"NODISPOSAL\",rightBracket:\"\",joinSign:\"and\",dataType:\"ENUM\"},{leftBracket:\"\",propName:\"vin.inner_color\",enumName:\"\",optSign:\"is not null\",propValue:\"\",rightBracket:\"\",joinSign:\"and\",dataType:\"STRING\"}]";
//		Map<String, Object> result = processQuerySentence("", false, s);
//		System.out.println(result);
//		String s = "[{leftBracket:\"\",propName:\"vin.inner_color\",enumName:\"\",optSign:\"is null\",propValue:\"\",rightBracket:\"\",joinSign:\"and\",dataType:\"STRING\"}," +
//				"{leftBracket:\"\",propName:\"vin\",enumName:\"\",optSign:\"=\",propValue:\"2011-01-01\",rightBracket:\"\",joinSign:\"and\",dataType:\"DATE\"}," +
//				"{leftBracket:\"\",propName:\"vin.inner_color\",enumName:\"\",optSign:\"is null\",propValue:\"\",rightBracket:\"\",joinSign:\"and\",dataType:\"STRING\"}]";
//		Map<String, Object> result = processQuerySentence("", false, s);
//		System.out.println(result);
//	}
	
	private static Map<String, Object> processQuerySentence(
			String sqlOrHql, boolean isHql, String searchParameters, Object... values){
		Map<String, Object> result = new HashMap<String, Object>();
		List<Object> list = getParameters(sqlOrHql, isHql, searchParameters);
		//重新拼接sql，封装参数
		String newSql = processSentence(sqlOrHql, list.get(0).toString());
		Object[] newValues = processParameter(list, values);
		result.put(SQL_OR_HQL, newSql);
		result.put(PARAMETERS, newValues);
		return result;
	}
	
	// list 0 号元素为sql， 其余为参数
	private static List<Object> getParameters(String sqlOrHql, boolean isHql, String searchParameters){
		List<Object> result = new ArrayList<Object>();
		StringBuilder additionalWhere = new StringBuilder();
		result.add(additionalWhere);
		
		MapType mt = TypeFactory.defaultInstance().constructMapType(
				HashMap.class, QueryConditionProperty.class, String.class);
		CollectionType ct = TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, mt);
		List<Map<QueryConditionProperty,String>> prms = JsonParser.json2Object(ct, searchParameters);
		
		List<Object> value = null;
		String alias = "";
		if(isHql) alias = getAlias(sqlOrHql);
		for(int i = 0; i < prms.size(); i++){
				if(i == prms.size()-1){
					additionalWhere.append(getSql(prms.get(i), alias, false,isHql));
				} else {
					additionalWhere.append(getSql(prms.get(i), alias, true,isHql));
				}
				value = getValue(prms.get(i), isHql);
				if(value != null){
					result.addAll(value);
				}
		}
		//处理创建时间相等的情况
			List<Object> newResult = new ArrayList<Object>();
			int flag = 0;
			for(int i=0;i<result.size();i++){
				if(result.get(i) instanceof StringBuilder){
					StringBuilder dateStr = (StringBuilder)result.get(i);
					if(dateStr.toString().indexOf("t.createDate >= ? and t.createDate <= ?")!=-1){
							flag = 1;
					}else if(dateStr.toString().indexOf("l.logTime >= ? and l.logTime <= ?")!=-1){
						    flag = 2;
					}
				}
				if(flag==1){
					StringBuilder dateSb = (StringBuilder)result.get(i);
					String dateStr = dateSb.toString();
					String beforStr = dateStr.substring(0, dateStr.toString().indexOf("t.createDate >= ? and t.createDate <= ?"));
					String afterStr = dateStr.substring( dateStr.toString().indexOf("t.createDate >= ? and t.createDate <= ?")+39,dateStr.length());
					newResult.add(beforStr+"t.createDate between ? and ?"+afterStr);
					flag = 0;
				}else if(flag==2){
					StringBuilder dateSb = (StringBuilder)result.get(i);
					String dateStr = dateSb.toString();
					String beforStr = dateStr.substring(0, dateStr.toString().indexOf("l.logTime >= ? and l.logTime <= ?"));
					String afterStr = dateStr.substring( dateStr.toString().indexOf("l.logTime >= ? and l.logTime <= ?")+33,dateStr.length());
					newResult.add(beforStr+"l.logTime between ? and ?"+afterStr);
					flag = 0;
				}else{
					newResult.add(result.get(i));
				}
			}
		log.debug(" *** additional where： /*" + result.toString() + "*/ ***");
		
		/**
		String alias = "";
		if(isHql) alias = getAlias(sqlOrHql);
		JSONArray array = JSONArray.fromObject(searchParameters);
		JSONObject obj = null;
		List<Object> value = null;
		for(int i = 0; i < array.size(); i++){
			obj = (JSONObject) array.get(i);
			if(i == array.size()-1){
				additionalWhere.append(getSql(obj, alias, false));
			} else {
				additionalWhere.append(getSql(obj, alias, true));
			}
			value = getValue(obj, isHql);
			if(value != null){
				result.addAll(value);
			}
		} */
			return newResult;
	}
	
	/**
	 * 获取查询条件
	 * @return
	 */
	public static List<Map<QueryConditionProperty,String>> getQueryParameter(){
		String searchParameters = Struts2Utils.getParameter(SEARCH_STRING_NAME);
		if(StringUtils.isNotBlank(searchParameters)){
			MapType mt = TypeFactory.defaultInstance().constructMapType(
					HashMap.class, QueryConditionProperty.class, String.class);
			CollectionType ct = TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, mt);
			List<Map<QueryConditionProperty,String>> prms = JsonParser.json2Object(ct, searchParameters);
			return prms;
		}
		return new ArrayList<Map<QueryConditionProperty,String>>();
	}

	/**
	 * hql语句别名
	 * @param hql
	 * @return
	 */
	public static String getAlias(String hql){
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
	
	private static String alias(String str, String fromHql){
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
	 * @param sql 源sql
	 * @param additionalWhere 需添加的where子句
	 * @return newSql
	 */
	private static String processSentence(String sql, String additionalWhere){
		StringBuilder newSql = new StringBuilder();
		newSql.append(removeOrders(sql));
		if(!sql.contains(" where ")&&!sql.contains(" WHERE ")){
			newSql.append("where ");
			newSql.append(additionalWhere);
		} else {
			newSql.append(" and (");
			newSql.append(additionalWhere).append(")");
		}
		if(sql.contains("order by")) {
			newSql.append(" order by ").append(StringUtils.substringAfter(sql, "order by"));
		}
		return newSql.toString();
	}
	
	private static Object[] processParameter(List<Object> list, Object... values){
		Object[] newValues = null;
		if(values == null){
			newValues = new Object[list.size()-1];
		} else {
			newValues = new Object[values.length + list.size()-1];
			System.arraycopy(values, 0, newValues, 0, values.length);
			int index = newValues.length -1;
			for(int j = list.size() - 1; j > 0;j--){
				newValues[index--] = list.get(j);
			}
		}
		return newValues;
	}
	
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
	
	private static String getSql(Map<QueryConditionProperty, String> obj, String alias, boolean needJoinSign,boolean isHql){
		StringBuilder sql = new StringBuilder();
		String propName = "";
		String tempName = "";
		String value = null;
		boolean neetLeftBracket = false;
		String dataType = obj.get(QueryConditionProperty.dataType);
		for(QueryConditionProperty prop : QueryConditionProperty.values()){
			if(QueryConditionProperty.dataType == prop) break;
			if(QueryConditionProperty.joinSign == prop && !needJoinSign) break;
			// 2011-08-08  
			if(QueryConditionProperty.joinSign == prop && needJoinSign){
				if("is null".equals(obj.get(QueryConditionProperty.optSign)) && 
						"STRING".equals(dataType)){
					sql.append(" or ").append(propName).append("='') ");
					neetLeftBracket = true;
				}
				if("is not null".equals(obj.get(QueryConditionProperty.optSign)) && 
						"STRING".equals(dataType)){
					sql.append(" and ").append(propName).append("!='') ");
					neetLeftBracket = true;
				}
				if("not like".equals(obj.get(QueryConditionProperty.optSign)) && 
						"STRING".equals(dataType)){
					sql.append(" or ").append(propName).append(" is null) ");
					neetLeftBracket = true;
				}
			}
			//2011-10-26  日期等于时的特殊处理
			if(QueryConditionProperty.optSign == prop 
					&& "=".equals(obj.get(QueryConditionProperty.optSign))
					&& "DATE".equals(dataType)){
				continue;
			}
			if(QueryConditionProperty.propValue == prop 
					&& "=".equals(obj.get(QueryConditionProperty.optSign))
					&& "DATE".equals(dataType)){
				sql.append(" between ? and ? "); 
				continue;
			}
			//2011-10-26 00:00:00  时间等于时的特殊处理
			if(QueryConditionProperty.optSign == prop 
					&& "=".equals(obj.get(QueryConditionProperty.optSign))
					&& "TIME".equals(dataType)){
				continue;
			}
			if(QueryConditionProperty.propValue == prop 
					&& "=".equals(obj.get(QueryConditionProperty.optSign))
					&& "TIME".equals(dataType)){
				sql.append(" between ? and ? "); 
				continue;
			}
			//=============
			if(QueryConditionProperty.propValue == prop){
				if(!"is null".equals(obj.get(QueryConditionProperty.optSign)) 
						&& !"is not null".equals(obj.get(QueryConditionProperty.optSign))){
					sql.append("? "); 
				}
				continue;
			}
			value = obj.get(prop);
			if(!isHql&&QueryConditionProperty.propName == prop)tempName=value;
			if(StringUtils.isNotBlank(value)){
				if(isHql&&QueryConditionProperty.dbName != prop){
					if(QueryConditionProperty.propName == prop && value.startsWith("$")){
						propName = value.replaceFirst("\\$", "");
						sql.append(propName).append(" ");
					}else{
						if(QueryConditionProperty.propName == prop && StringUtils.isNotBlank(alias)) {
							propName = alias+"." + value;
							//sql.append(propName);
							sql.append(propName).append(" ");
						}else{
							if(QueryConditionProperty.propName == prop) propName = value;
							sql.append(value).append(" ");
						}
					}
				}else if(!isHql&&QueryConditionProperty.propName != prop){
					if("null".equals(value)){//为了兼容以前的数据dbName为空时就取propName的值
						value=tempName;
					}
					if(QueryConditionProperty.dbName == prop && value.startsWith("$")){
						propName = value.replaceFirst("\\$", "");
						sql.append(propName).append(" ");
					}else{
						if(QueryConditionProperty.dbName == prop) propName = value;
						sql.append(value).append(" ");
					}
				}
			}
		}
		// 2011-08-08  
		if(!needJoinSign && "is null".equals(obj.get(QueryConditionProperty.optSign)) && 
				"STRING".equals(dataType)){
			sql.append(" or ").append(propName).append("='') ");
			neetLeftBracket = true;
		}
		if(!needJoinSign && "is not null".equals(obj.get(QueryConditionProperty.optSign)) && 
				"STRING".equals(dataType)){
			if(!"oracle".equals(PropUtils.getDataBase())){//oracle数据库中不认识name!=''
				sql.append(" and ").append(propName).append("!='') ");
				neetLeftBracket = true;
			}
		}
		if(!needJoinSign && "not like".equals(obj.get(QueryConditionProperty.optSign)) && 
				"STRING".equals(dataType)){
			sql.append(" or ").append(propName).append(" is null) ");
			neetLeftBracket = true;
		}
		if(neetLeftBracket){
			return "(" + sql.toString();
		}
		//=============
		return sql.toString();
	}
	
	private static List<Object> getValue(Map<QueryConditionProperty, String> obj, boolean isHql){
		String optSign = obj.get(QueryConditionProperty.optSign);
		String propValue = obj.get(QueryConditionProperty.propValue);
		String dataType = obj.get(QueryConditionProperty.dataType);
		String enumName = obj.get(QueryConditionProperty.enumName);
		List<Object> result = new ArrayList<Object>();
		if("like".equals(optSign) || "not like".equals(optSign)){
			String listCode=Struts2Utils.getParameter("_list_code");
			ListViewManager listViewManager = (ListViewManager) ContextUtils.getBean("listViewManager");
			ListView listView=listViewManager.getListViewByCode(listCode);
			if(listView.getSearchFaint()){//是否启用模糊查询，默认是启用的
				result.add("%"+propValue+"%");
			}else{
				result.add(propValue+"%");
			}
		}else if("is null".equals(optSign) || "is not null".equals(optSign)){
			result = null;
		}else{
			try {
				Object value = getObjectByType(propValue, dataType, enumName, isHql);
				if(DataType.valueOf(dataType) == DataType.DATE && "=".equals(optSign)){
					result.add(value);
					result.add(new Date(((Date)value).getTime()+(24*3600000-1)));
				}else if(DataType.valueOf(dataType) == DataType.TIME && "=".equals(optSign)){
					result.add(value);
					result.add(new Date(((Date)value).getTime()+(60000)));
				}else{
					result.add(value);
				}
			} catch (ParseException e) {
				log.debug(" *** format parameters error : ", e);
			}
		}
		return result;
	}
	
	private static Object getObjectByType(String value, String type, String enumName, boolean isHql) throws ParseException{
		log.debug(" *** format parameters: /*" + value + " to " + type + "*/ ***");
		switch(DataType.valueOf(type)){
		case AMOUNT: return new BigDecimal(value);
		case STRING: return value;
		case DATE: return dataFormat.parse(value);
		case TIME: return dataFormat.parse(value);
		case INTEGER: return Integer.valueOf(value);
		case LONG: return Long.valueOf(value);
		case BOOLEAN: 
			if("1".equals(value)||"true".equals(value)){
				return true;
			}else if("0".equals(value)||"false".equals(value)){
				return false;
			}
		case DOUBLE: return Double.valueOf(value);
		case FLOAT: return Float.valueOf(value);
		case ENUM: return getEnumObject(enumName, value, isHql);
		default: break;
		}
		log.debug(" *** format parameters end *** ");
		return null;
	}

	@SuppressWarnings("unchecked")
	private static Object getEnumObject(String enumName, String value, boolean isHql) {
		try {
			Class clazz = Class.forName(enumName);
			if(isHql){
				return Enum.valueOf(clazz, value);
			}else{
				return Enum.valueOf(clazz, value).ordinal();
			}
		} catch (ClassNotFoundException e) {
			log.debug(" *** format Enum error: [" + enumName + ":" + value + "]", e);
		}
		return null;
	}
}
enum DataType {
	AMOUNT("金额"),
	STRING("文本"),
	DATE("日期"),
	TIME("时间"),
	INTEGER("整型"),
	LONG("长整型"),
	DOUBLE("双精度浮点数"),
	FLOAT("单精度浮点数"),
	BOOLEAN("布尔型"),
	ENUM("枚举型");
	
	public String code;
	DataType(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
	public String getEnumName(){
		return this.toString();
	}
}

