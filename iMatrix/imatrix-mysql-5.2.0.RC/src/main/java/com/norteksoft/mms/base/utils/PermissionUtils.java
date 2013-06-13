package com.norteksoft.mms.base.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.proxy.HibernateProxy;

import com.norteksoft.product.api.entity.Role;
import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.Workgroup;
import com.norteksoft.mms.authority.entity.Condition;
import com.norteksoft.mms.authority.entity.PermissionItem;
import com.norteksoft.mms.authority.enumeration.FieldOperator;
import com.norteksoft.mms.authority.enumeration.UserOperator;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.BeanUtils;
import com.norteksoft.wf.base.enumeration.LogicOperator;
import com.norteksoft.wf.base.utils.BeanShellUtil;

/**
 * 根据 PermissionItem 组合,判断当前用户是否满足条件
 * @author xiao
 * 2012-11-2
 */
public class PermissionUtils {

	protected static Log logger = LogFactory.getLog(PermissionUtils.class);
	
	public static boolean hasPermission(List<PermissionItem> items, UserInfo user){
		//UserInfo user = new UserInfo(ContextUtils.getLoginName());
		StringBuilder result = new StringBuilder();
		for(PermissionItem item : items){
			result.append(" ").append(itemPermission(item, user)).append(joinType(item.getJoinType()));
		}
		String express = result.substring(0, result.length()-2);
		return BeanShellUtil.evel(express);
	}
	
	private static boolean itemPermission(PermissionItem item, UserInfo user){
		switch (item.getItemType()) {
		case USER:
			return permissionDecision(item.getOperator(), user.loginNames, item.getConditionValue());
		case DEPARTMENT:
			if(user.departments == null){ 
				user.departments = getDepartments(user.loginName);
			}
			return permissionDecision(item.getOperator(), user.departments, item.getConditionValue());
		case ROLE: 
			if(user.roles == null){ 
				user.roles = getRoles(user.loginName);
			}
			return permissionDecision(item.getOperator(), user.roles, item.getConditionValue());
		case WORKGROUP: 
			if(user.workgroups == null){
				user.workgroups = getWorkgroups(user.loginName);
			}
			return permissionDecision(item.getOperator(), user.workgroups, item.getConditionValue());
		}
		return false;
	}
	
	private static boolean permissionDecision(UserOperator operator, List<String> src, String value){
		switch (operator) {
		case ET:
			if(src.contains(value)) return true;
			break;
		case NET: 
			if(!src.contains(value)) return true;
			break;
		}
		return false;
	}
	
	private static String joinType(LogicOperator type){
		switch (type) {
		case AND:
			return " &&";
		case OR:
			return " ||";
		}
		return "";
	}
	
	private static List<String> getRoles(String loginName){
		Set<Role> roles = ApiFactory.getAcsService().getRolesByUser(loginName);
		List<String> result = new ArrayList<String>();
		for(Role role : roles){
			result.add(role.getCode());
		}
		return result;
	}
	
	private static List<String> getWorkgroups(String loginName){
		List<Workgroup> groups = ApiFactory.getAcsService().getWorkgroupsByUser(loginName);
		List<String> result = new ArrayList<String>();
		for(Workgroup group : groups){
			result.add(group.getId().toString());
		}
		return result;
	}
	
	private static List<String> getDepartments(String loginName){
		List<Department> depts = ApiFactory.getAcsService().getDepartments(loginName);
		List<String> result = new ArrayList<String>();
		for(Department dept : depts){
			result.add(dept.getId().toString());
		}
		return result;
	}
	
	public static class UserInfo{
		String loginName;
		List<String> loginNames = new ArrayList<String>();
		List<String> roles;
		List<String> departments;
		List<String> workgroups;
		
		public UserInfo(String loginName){
			this.loginName = loginName;
			this.loginNames.add(loginName);
		}
	}
	
	/**
	 * 实体是否满足数据规则
	 * @param entity
	 * @param rule
	 */
	public static boolean entityPermission(Object entity, List<Condition> conditions){
		StringBuilder sb = new StringBuilder();
		Object obj = null;
		Condition con =null;
		boolean result;
		try {
			for(int i=0;i<conditions.size();i++){
				con = conditions.get(i);
				if(entity instanceof HibernateProxy){
					HibernateProxy proxy = (HibernateProxy)entity;
					entity = proxy.getHibernateLazyInitializer().getImplementation();
				}
				obj = BeanUtils.getFieldValue(entity, con.getField());
				result = calculateCondition(obj, con.getOperator(), con.getDataType(), con.getConditionValue(),con.getEnumPath());
				sb.append(result).append(joinType(con.getLgicOperator()));
			}
		} catch (Exception e) {
			logger.error("Compare value error. Field:[" + con.getField() + 
					"], SRC: [" + obj + "], DEST:["+con.getConditionValue()+"]", e);
		}
		String express = sb.substring(0, sb.length()-2);
		return BeanShellUtil.evel(express);
	}
	
	/**
	 * 计算表达式的值
	 * @param obj  原始值
	 * @param fo   比较符
	 * @param dt   数据类型
	 * @param value  比较的值
	 * @return
	 */
	public static boolean calculateCondition(Object obj, FieldOperator fo, DataType dt, String value,String enumPath){
		switch (fo) {
		case IS_NULL: return obj == null;
		case NOT_NULL: return obj != null;
		case CONTAIN: 
			if(dt==DataType.TEXT && obj!=null && value!=null){
				return obj.toString().contains(value);
			}
			return false;
		case NOT_CONTAIN: 
			if(dt==DataType.TEXT && obj!=null && value!=null){
				return !obj.toString().contains(value);
			}
			return false;
		case ET: 
			if(obj!=null && value!=null){
				if(dt==DataType.ENUM){//处理枚举类型
					return obj.equals(getValueByType(value, enumPath));
				}
				if(dt==DataType.DATE){//处理日期类型
					return ((Date)obj).getTime()==((Date)getValueByType(dt, value)).getTime();
				}
				return obj.equals(getValueByType(dt, value));
			}
			return false;
		case NET: 
			if(obj!=null && value!=null){
				return !obj.equals(getValueByType(dt, value));
			}
			return false;
		case GT: 
			if(comparableBigSmall(dt)){
				return CompareUtils.compareGT(dt, obj, getValueByType(dt, value));
			}
			return false;
		case GET: 
			if(comparableBigSmall(dt)){
				return CompareUtils.compareGET(dt, obj, getValueByType(dt, value));
			}
			return false;
		case LT: 
			if(comparableBigSmall(dt)){
				return CompareUtils.compareLT(dt, obj, getValueByType(dt, value));
			}
			return false;
		case LET: 
			if(comparableBigSmall(dt)){
				return CompareUtils.compareLET(dt, obj, getValueByType(dt, value));
			}
			return false;
		}
		return false;
	}
	
	private static boolean comparableBigSmall(DataType dt){
		return DataType.DATE==dt||DataType.TIME==dt||DataType.INTEGER==dt
				||DataType.LONG==dt||DataType.DOUBLE==dt||DataType.FLOAT==dt;
	}
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public static Object getValueByType(DataType dt, String value){
		if(StringUtils.isEmpty(value)) return null;
		try {
			switch (dt) {
			case TEXT: return value;
			case DATE: return DATE_FORMAT.parse(value);
			case TIME: return TIME_FORMAT.parse(value);
			case INTEGER: return Integer.valueOf(value);
			case LONG: return Long.valueOf(value);
			case DOUBLE: return Double.valueOf(value);
			case FLOAT: return Float.valueOf(value);
			case BOOLEAN: return Boolean.valueOf(value);
			case ENUM: break;
			}
		} catch (Exception e) {
			logger.error("Parse string to " + dt + " error. string["+value+"]", e);
		}
		return null;
	}
	
	public static Object getValueByType( String value,String enumPath){
		if(StringUtils.isEmpty(value)) return null;
		try {
			Object[] enumValues = Class.forName(enumPath).getEnumConstants();
			for (Object object : enumValues) {
				if(object.toString().equals(value)){
					return object;
				}
			}
		} catch (Exception e) {
			logger.error("Parse  " + value + " to enum:"+enumPath+"error.", e);
		}
		return null;
	}
	
}
