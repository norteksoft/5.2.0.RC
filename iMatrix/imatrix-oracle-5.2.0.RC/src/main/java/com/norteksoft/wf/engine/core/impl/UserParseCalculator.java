package com.norteksoft.wf.engine.core.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import com.norteksoft.product.api.entity.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.bs.rank.service.RankManager;
import com.norteksoft.mms.form.dao.GeneralDao;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.jdbc.JdbcSupport;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.form.service.TableColumnManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.base.enumeration.CommonStrings;
import com.norteksoft.wf.base.enumeration.LogicOperator;
import com.norteksoft.wf.base.enumeration.TextOperator;
import com.norteksoft.wf.base.utils.UserUtil;
import com.norteksoft.wf.engine.core.Computable;

/**
 * 对用户解析的运算器
 */
public class UserParseCalculator implements Computable {
	
	private String documentCreator;//文档创建人
	private String currentTransactor;//当前办理人（如果有委托为受托人）
	private String previousTransactor;//上一环节办理人（如果有委托为受托人）
	private String processAdmin;//流程管理员
	private Collection<String> handledTransactors;//已办理人员
	private Collection<String> allHandleTransactors;//已办理人员
	private FormView formView;
	private Long dataId;
	private String approvalResult;//审批结果
	
	private static final String SQUARE_BRACKETS_LEFT = "[";
	private static final String SQUARE_BRACKETS_RIGHT = "]";

	public Boolean execute(String atomicExpress) {
		 RankManager rankManager=(RankManager)ContextUtils.getBean("rankManager");
		if(StringUtils.isEmpty(atomicExpress)) return false;
		boolean result = false;
		atomicExpress = atomicExpress.trim();
		
		if(StringUtils.startsWith(atomicExpress, CommonStrings.DOCUMENT_CREATOR_NAME)){
			//表达式左边为文档创建人姓名
			result = parseUser(atomicExpress,documentCreator);
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.DOCUMENT_CREATOR_ROLE)){
			//表达式左边为文档创建人角色
			result = parseRole(atomicExpress,documentCreator);
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.DOCUMENT_CREATOR_DEPARTMENT)){
			//表达式左边为文档创建人部门
			result = parseDepartment(atomicExpress,documentCreator);
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.DOCUMENT_SUPERIOR_DEPARTMENT)){
			//表达式左边为文档创建人上级部门 
			List<Department> departments2 = ApiFactory.getAcsService().getParentDepartmentsByUser(documentCreator);
			result = parseDepartment(atomicExpress,departments2);
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.DOCUMENT_CREATOR_UPSTAGE_DEPARTMENT)){
			//表达式左边为文档创建人顶级部门
			List<Department> departments2 = ApiFactory.getAcsService().getTopDepartmentsByUser(documentCreator);
			result = parseDepartment(atomicExpress,departments2);	
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.DOCUMENT_CREATOR_WORKGROUP)){
			//表达式左边为文档创建人工作组
			result = parseWorkGroup(atomicExpress,documentCreator);
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.DOCUMENT_CREATOR_DIRECT_SUPERIOR_NAME)){
			//表达式左边为文档创建人直属上级名称
			User user=rankManager.getDirectLeader(documentCreator);
			result = user==null?false:parseUser(atomicExpress,user.getLoginName());
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.DOCUMENT_CREATOR_DIRECT_SUPERIOR_DEPARTMENT)){
			//表达式左边为文档创建人直属上级部门
			User user=rankManager.getDirectLeader(documentCreator);
			result = user==null?false:parseDepartment(atomicExpress,user.getLoginName());
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.DOCUMENT_CREATOR_DIRECT_SUPERIOR_ROLE)){
			//表达式左边为文档创建人直属上级角色
			User user=rankManager.getDirectLeader(documentCreator);
			result = user==null?false:parseRole(atomicExpress,user.getLoginName());
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.DOCUMENT_CREATOR_DIRECT_SUPERIOR_WORKGROUP)){
			//表达式左边为文档创建人直属上级工作组
			User user=rankManager.getDirectLeader(documentCreator);
			result = user==null?false:parseWorkGroup(atomicExpress,user.getLoginName());
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_NAME)){
			//当前办理人姓名
			result = parseUser(atomicExpress,currentTransactor);
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_ROLE)){
			result = parseRole(atomicExpress,currentTransactor);
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_DEPARTMENT)){
			result = parseDepartment(atomicExpress,currentTransactor);
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_SUPERIOR_DEPARTMENT)){
			List<Department> departments2 = ApiFactory.getAcsService().getParentDepartmentsByUser(currentTransactor);
			result = parseDepartment(atomicExpress,departments2);
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_UPSTAGE_DEPARTMENT)){
			List<Department> departments2 = ApiFactory.getAcsService().getTopDepartmentsByUser(currentTransactor);
			result = parseDepartment(atomicExpress,departments2);
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_WORKGROUP)){
			result = parseWorkGroup(atomicExpress,currentTransactor);
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_DIRECT_SUPERIOR_NAME)){
			User user=rankManager.getDirectLeader(currentTransactor);
			result = user==null?false:parseUser(atomicExpress,user.getLoginName());
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_DIRECT_SUPERIOR_DEPARTMENT)){
			User user=rankManager.getDirectLeader(currentTransactor);
			result = user==null?false:parseDepartment(atomicExpress,user.getLoginName());
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_DIRECT_SUPERIOR_ROLE)){
			User user=rankManager.getDirectLeader(currentTransactor);
			result = user==null?false:parseRole(atomicExpress,user.getLoginName());
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_DIRECT_SUPERIOR_WORKGROUP)){
			User user=rankManager.getDirectLeader(currentTransactor);
			result = user==null?false:parseWorkGroup(atomicExpress,user.getLoginName());
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_NAME)){
			result = parseUser(atomicExpress,previousTransactor);
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_ROLE)){
			result = parseRole(atomicExpress,previousTransactor);
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_DEPARTMENT)){
			result = parseDepartment(atomicExpress,previousTransactor);
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_WORKGROUP)){
			result = parseWorkGroup(atomicExpress,previousTransactor);
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_NAME)){
			User user=rankManager.getDirectLeader(previousTransactor);
			result = user==null?false:parseUser(atomicExpress,user.getLoginName());
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_DEPARTMENT)){
			User user=rankManager.getDirectLeader(previousTransactor);
			result = user==null?false:parseDepartment(atomicExpress,user.getLoginName());
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_ROLE)){
			User user=rankManager.getDirectLeader(previousTransactor);
			result = user==null?false:parseRole(atomicExpress,user.getLoginName());
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_WORKGROUP)){
			User user=rankManager.getDirectLeader(previousTransactor);
			result = user==null?false:parseWorkGroup(atomicExpress,user.getLoginName());
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_WORKGROUP)){
			result = parseWorkGroup(atomicExpress,previousTransactor);
		}else if(StringUtils.startsWith(atomicExpress, CommonStrings.APPROVAL_RESULT)){
			parseApprovalResult(atomicExpress);
		}
		return result;
	}
	/*
	 * 解析表达式 返回部门列表
	 */
	private List<Department> getDepartmentByExpress(String valueExpress){
		List<Department> result = new ArrayList<Department>();
		if(StringUtils.isEmpty(valueExpress)) return result;
		if(valueExpress.trim().startsWith("${field[")){
			int start = valueExpress.lastIndexOf(SQUARE_BRACKETS_LEFT);
			int end = valueExpress.indexOf(SQUARE_BRACKETS_RIGHT);
			String fieldName = valueExpress.substring(start + 1, end);
			String departmentName = getValue(fieldName);
			Department tempDepartment = ApiFactory.getAcsService().getDepartmentByName(departmentName);
			if(tempDepartment!=null)result.add(tempDepartment);
			return result;
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_DEPARTMENT)){
			//创建人部门
			return ApiFactory.getAcsService().getDepartments(documentCreator);
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_SUPERIOR_DEPARTMENT)){
			//创建人上级部门
			return ApiFactory.getAcsService().getParentDepartmentsByUser( documentCreator);
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_UPSTAGE_DEPARTMENT)){
			//创建人顶级部门
			return ApiFactory.getAcsService().getTopDepartmentsByUser(documentCreator);
		}else if(valueExpress.trim().equals(CommonStrings.UPSTAGE_DEPARTMENT)){
			//顶级部门(当前办理人所在部门为顶级部门)
			return ApiFactory.getAcsService().getDepartments();
		}else if(valueExpress.trim().equals(CommonStrings.CURRENT_TRANSACTOR_SUPERIOR_DEPARTMENT)){
			//当前办理人的上级部门
			return ApiFactory.getAcsService().getParentDepartmentsByUser(currentTransactor);
		}else if(valueExpress.trim().equals(CommonStrings.CURRENT_TRANSACTOR_UPSTAGE_DEPARTMENT)){
			//当前办理人的顶级部门
			return ApiFactory.getAcsService().getTopDepartmentsByUser(currentTransactor);
		}else{
			result.add(ApiFactory.getAcsService().getDepartmentByName(valueExpress));
			return result;
		}
	}
	/*
	 * 判断两个部门集合是不是有交集
	 */
	private boolean haveIntersectionDepartment(List<Department> departments1,List<Department> departments2) {
		if(departments1.size()==0||departments2.size()==0) return false;
		if(departments1.size()<departments2.size()){
			for(Department department :departments1){
				if(departments2.contains(department)) return true;
			}
		}else{
			for(Department department :departments2){
				if(departments1.contains(department)) return true;
			}
		}
		return false;
	}
	/*
	 * 解析表单式和部门的关系
	 */
	private boolean parseDepartment(String atomicExpress,List<Department> departments2) {
		List<Department> department1 = getDepartmentByExpress(StringUtils.substringBetween(atomicExpress, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
		if(StringUtils.contains(atomicExpress, TextOperator.NET.getCode())) {
			return !haveIntersectionDepartment(department1,departments2);
		}else if(StringUtils.contains(atomicExpress, TextOperator.ET.getCode())){
			return haveIntersectionDepartment(department1,departments2);
		}
		return false;
	}

	private boolean parseApprovalResult(String atomicExpress){
		 if(StringUtils.contains(atomicExpress, TextOperator.NET.getCode())) {
				return !approvalResult.equals(StringUtils.substringBetween(atomicExpress, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			}else if(StringUtils.contains(atomicExpress, TextOperator.ET.getCode())){
				return approvalResult.equals(StringUtils.substringBetween(atomicExpress, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			}
			return false;
	}
	private static final String SINGLE_QUOTATION_MARK = "'";
	/**
	 * 解析用户和工作组的关系
	 * ${documentCreatorWorkGroup} operator.text.et '${field[姓名[name]]}'
	 * ${documentCreatorWorkGroup} operator.text.et 'SBU工作组'
	 * ${currentTransactorWorkGroup} operator.text.et '${documentCreatorWorkGroup}'
	 */
	private   boolean parseWorkGroup(String atomicExpress,String loginName){
		 if(StringUtils.contains(atomicExpress, TextOperator.NET.getCode())) {
			return !userInWorkGroup(StringUtils.substringBetween(atomicExpress, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK), loginName );
		}else if(StringUtils.contains(atomicExpress, TextOperator.ET.getCode())){
			return userInWorkGroup(StringUtils.substringBetween(atomicExpress, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK), loginName );
		}
		return false;
	}
	private  boolean userInWorkGroup(String valueExpress,String loginName){
		if(StringUtils.isEmpty(valueExpress)) return false;
		if(valueExpress.trim().startsWith("${field[")){
			int start = valueExpress.lastIndexOf(SQUARE_BRACKETS_LEFT);
			int end = valueExpress.indexOf(SQUARE_BRACKETS_RIGHT);
			String fieldName = valueExpress.substring(start + 1, end);
			String workGroupName = getValue(fieldName);
			return UserUtil.userInWorkGroup(ContextUtils.getCompanyId(), loginName, workGroupName);
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_WORKGROUP)){
			List<com.norteksoft.product.api.entity.Workgroup> workGroups = ApiFactory.getAcsService().getWorkgroupsByUser(documentCreator);
			for (com.norteksoft.product.api.entity.Workgroup workGroup : workGroups) {
				if(UserUtil.userInWorkGroup(ContextUtils.getCompanyId(), loginName, workGroup.getName())){
					return true;
				}
			}
			return false;
		}else{
			return UserUtil.userInWorkGroup(ContextUtils.getCompanyId(), loginName, valueExpress);
		}
	}
	
	
	/**
	 * 解析用户和部门的关系
	 * ${documentCreatorDepartment} operator.text.et '${upstageDepartment}'
	 * ${documentCreatorName} operator.text.et '${field[姓名[name]]}' 
	 * ${documentCreatorDepartment} operator.text.et '财务部' 
	 * ${currentTransactorDepartment} operator.text.et '${documentCreatorDepartment}' 
	 * ${currentTransactorDepartment} operator.text.et '${superiorDepartment}'
	 */
	private  boolean parseDepartment(String atomicExpress,String loginName){
		if(StringUtils.contains(atomicExpress, TextOperator.NET.getCode())) {
			
			return !userInDepartment(StringUtils.substringBetween(atomicExpress, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK),loginName);
		}else if(StringUtils.contains(atomicExpress, TextOperator.ET.getCode())){
			return userInDepartment(StringUtils.substringBetween(atomicExpress, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK),loginName);
		}
		return false;
	}
	
	private  boolean userInDepartment(String valueExpress,String loginName){
		if(StringUtils.isEmpty(valueExpress)) return false;
		if(valueExpress.trim().startsWith("${field[")){
			int start = valueExpress.lastIndexOf(SQUARE_BRACKETS_LEFT);
			int end = valueExpress.indexOf(SQUARE_BRACKETS_RIGHT);
			String fieldName = valueExpress.substring(start + 1, end);
			String departmentName = getValue(fieldName);
			return UserUtil.userInDepartment(ContextUtils.getCompanyId(), loginName, departmentName);
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_DEPARTMENT) 
				|| valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_DIRECT_SUPERIOR_DEPARTMENT)){
			//创建人部门
			List<Department> departments = ApiFactory.getAcsService().getDepartments(loginName);
			for (Department department : departments) {
				if(UserUtil.userInDepartment(ContextUtils.getCompanyId(), loginName, department.getName())){
					return true;
				}
			}
			return false;
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_SUPERIOR_DEPARTMENT)){
			//创建人上级部门
			List<Department> departments = ApiFactory.getAcsService().getParentDepartmentsByUser( documentCreator);
			for (Department department : departments) {
				if(UserUtil.userInDepartment(ContextUtils.getCompanyId(), loginName, department.getName())){
					return true;
				}
			}
			
			return false;
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_UPSTAGE_DEPARTMENT)){
			//创建人顶级部门
			List<Department> departments = ApiFactory.getAcsService().getTopDepartmentsByUser(documentCreator);
			for (Department department : departments) {
				if(UserUtil.userInDepartment(ContextUtils.getCompanyId(), loginName, department.getName())){
					return true;
				}
			}
			
			return false;
		}else if(valueExpress.trim().equals(CommonStrings.UPSTAGE_DEPARTMENT)){
			//顶级部门(当前办理人所在部门为顶级部门)
			List<Department> departments = ApiFactory.getAcsService().getDepartments(loginName);
			for (Department department : departments) {
				Department parentDept = ApiFactory.getAcsService().getParentDepartment(department.getId());
				if(parentDept==null){
					return true;
				}
			}
			return false;
		}else{
			return UserUtil.userInDepartment(ContextUtils.getCompanyId(), loginName, valueExpress);
		}
	}
	
	
	/**
	 * 解析用户是否拥有某角色 角色来源：字段中或组织结果中
	 * ${currentTransactorRole} operator.text.et '${field[姓名[name]]}' 
	 * ${currentTransactorRole} operator.text.et '安全管理员'
	 */
	private  boolean parseRole(String atomicExpress,String loginName){
		if(StringUtils.contains(atomicExpress, TextOperator.NET.getCode())) {
			String roleName = getRoleName(StringUtils.substringBetween(atomicExpress, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			return !UserUtil.userHaveRole(ContextUtils.getCompanyId(), loginName, roleName);
		}else if(StringUtils.contains(atomicExpress, TextOperator.ET.getCode())){
			String roleName = getRoleName(StringUtils.substringBetween(atomicExpress, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			return UserUtil.userHaveRole(ContextUtils.getCompanyId(), loginName, roleName);
		}
		return false;
	}
	
	private  String getRoleName(String valueExpress){
		if(StringUtils.isEmpty(valueExpress)) return "";
		if(valueExpress.trim().startsWith("${field[")){
			int start = valueExpress.lastIndexOf(SQUARE_BRACKETS_LEFT);
			int end = valueExpress.indexOf(SQUARE_BRACKETS_RIGHT);
			String fieldName = valueExpress.substring(start + 1, end);
			String roleName = getValue(fieldName);
			return roleName;
		}else{
			return valueExpress;
		}
	}
	
	/**
	 * 解析用户是否满足条件 ，判断条件的值有3中来源，分别为组织结构中、表单字段中和标准值
	 * ${currentTransactorName} operator.text.et '吴荣[wurong]' 
	 *  ${currentTransactorName} operator.text.et '${documentCreatorName}'
	 * ${currentTransactorName} operator.text.et '${field[姓名[name]]}
	 */
	private  boolean parseUser(String atomicExpress,String loginName){
		if(StringUtils.contains(atomicExpress, TextOperator.NET.getCode())) {
			String userLoginName = getUserLoginName(StringUtils.substringBetween(atomicExpress, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			return !loginName.equals(userLoginName);
		}else if(StringUtils.contains(atomicExpress, TextOperator.ET.getCode())){
			String userLoginName = getUserLoginName(StringUtils.substringBetween(atomicExpress, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			return loginName.equals(userLoginName);
		}
		return false;
	}
	
	private  String getUserLoginName(String valueExpress){
		 RankManager rankManager=(RankManager)ContextUtils.getBean("rankManager");
		if(StringUtils.isEmpty(valueExpress)) return "";
		if(valueExpress.trim().startsWith("${field[")){
			int start = valueExpress.lastIndexOf(SQUARE_BRACKETS_LEFT);
			int end = valueExpress.indexOf(SQUARE_BRACKETS_RIGHT);
			String fieldName = valueExpress.substring(start + 1, end);
			String loginName = getValue(fieldName);
			return loginName;
		}else if(valueExpress.trim().endsWith("${documentCreatorName}")){
			return documentCreator;
		}else if(valueExpress.trim().endsWith(CommonStrings.DOCUMENT_CREATOR_DIRECT_SUPERIOR_NAME)){
			User user=rankManager.getDirectLeader(documentCreator);
			return user==null?"":user.getLoginName();
		}else{
			return StringUtils.substringBetween(valueExpress, "[", "]");
		}
	}

	@SuppressWarnings("unchecked")
	private String getValue(String fieldName){
		 GeneralDao generalDao  =  (GeneralDao)ContextUtils.getBean("generalDao");
		 FormViewManager formManager = (FormViewManager)ContextUtils.getBean("formViewManager");
		 TableColumnManager tableColumnManager = (TableColumnManager)ContextUtils.getBean("tableColumnManager");
		String value = "";
		boolean isSql = true;
		Object entity = null;
		Map dataMap = null;
		//标准表单
		if(!formView.isStandardForm()){
			//自定义表单
			dataMap = formManager.getDataMap(formView.getDataTable().getName(), dataId);
		}else if(formView.isStandardForm()){
			try{
				Class.forName(formView.getDataTable().getEntityName());//判断是否存在该类型
				entity = generalDao.getObject(formView.getDataTable().getEntityName(),dataId);
				isSql=false;
			}catch(ClassNotFoundException e){
				dataMap = formManager.getDataMap(formView.getDataTable().getName(), dataId);
			}
		}
		if(isSql){
			Object obj = null;
			if(!formView.isStandardForm()){
				//自定义表单
				obj = dataMap.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+fieldName);
			}else{
				//标准表单
				String dbName = fieldName;
				TableColumn column=tableColumnManager.getTableColumnByColName(formView.getDataTable().getId(), fieldName);
				if(column!=null){
					dbName=column.getDbColumnName();
				}
				if(StringUtils.isNotEmpty(fieldName))obj = dataMap.get(dbName);
			}
			if(obj==null){
				value = "";
			}else{
				value = obj.toString();
			}
		}else{
			//标准表单
			try {
				Object object = BeanUtils.getProperty(entity, fieldName);
				if(object==null){
					value = "";
				}else{
					value = object.toString();
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
		
		return value;
	}
	
	
	//--------------获得用户
	
	
	/**
	 * 从条件中取得用户
	 * 算法：
	 * A OR B AND ((C OR D) OR E AND F) AND G OR (H OR I) AND J
	 * 截取	从右向左找第一 '('  从'('的位置向右找第一个 ） 将它中间内容存为 x1，并将它们替换为 x1 x1= H OR I
	 *  得 A OR B AND ((C OR D) OR E AND F) AND G OR x1 AND J
	 *  截取	从右向左找第一 （  从左向右找第一个 ） 将它中间内容存为 x2，并将它们替换为 x2 = C OR D
	 *  得 A OR B AND (x2 OR E AND F) AND G OR x1 AND J
	 
	 *  截取	从右向左找第一 （  从左向右找第一个 ） 将它中间内容存为 x3，并将它们替换为 x3 = x2 OR E AND F
	 *  得 A OR B AND x3 AND G OR x1 AND J
	 * 
	 * 再以 or 分割表达式 得 y1 = A ；y2 = B AND x3 AND G ; y3 = x1 AND J
	 
	 *  再以 and 分割表达式 的 y2{z1 = B ; z2 = x3; z3 = G;} y3{x1 , J}
	 * 
	 * @param userCondition
	 * @return 满足条件的用户
	 */
	public Set<String> getUsers(String userCondition,Long systemId,Long companyId){
		return parseBrackets(userCondition,systemId,companyId);
	}
	private Map<String,String> userMap = new HashMap<String,String>();
	private static char LEFT_BRACKET = '(';
	private static char RIGHT_BRACKET = ')';
	private static String VARIABLE_PRE = "var";
	private Set<String> parseBrackets(String userCondition,Long systemId,Long companyId){
		int left_Bracket_index = -1;
		int right__Bracket_index = -1;
		String subString = null;
		while(true){
			left_Bracket_index = userCondition.lastIndexOf(LEFT_BRACKET);
			if(left_Bracket_index==-1) break;
			right__Bracket_index = userCondition.indexOf(RIGHT_BRACKET,left_Bracket_index);
			subString = userCondition.substring(left_Bracket_index+1,right__Bracket_index);
			userCondition = StringUtils.replace(userCondition, userCondition.substring(left_Bracket_index,right__Bracket_index+1), VARIABLE_PRE+subString.hashCode());
			userMap.put(VARIABLE_PRE+subString.hashCode(), subString);
		}
		return parseOr(userCondition,systemId,companyId);
	}
	private Set<String> parseOr(String condition,Long systemId,Long companyId){
		String[] conds = condition.split(LogicOperator.OR.getCode());
		Set<String> userLoginNames = new HashSet<String>();
		for(String cond :conds){
			userLoginNames.addAll(parseAnd(cond,systemId,companyId));
		}
		return userLoginNames;
	}
	private Set<String> parseAnd(String condition,Long systemId,Long companyId){
		String[] conds = condition.split(LogicOperator.AND.getCode());
		Map<Integer,Set<String>> map = new HashMap<Integer,Set<String>>();
		int minSize = 100000;//默认为十万。如果一个公司人数超过十万，这个的初始值可能会出问题
		Set<String> temp = null;
		int minI = 0;
		for(int i=0;i<conds.length;i++){
			if(userMap.get(conds[i].trim())==null){
				temp = parseAtomCondition(conds[i].trim(),systemId,companyId);
				
			}else{
				temp = parseOr(userMap.get(conds[i].trim()),systemId,companyId);
			}
			if(temp.size()==0) return temp;//如果在and条件中有一个条件没有选出人，则整个and条件也没有人
			if(temp.size()<minSize){
				 minSize = temp.size();
				 minI = i;//人数最少的条件的key
			}
			map.put(i, temp);
		}
		Set<String> result = new HashSet<String>();
		List<String> minSet = new ArrayList<String>(map.get(minI));
		
		for(int j=0;j<minSet.size();j++ ){
			boolean isSelect = true;
			for(int i=0;i<conds.length;i++){
				if(!map.get(i).contains(minSet.get(j))){
					isSelect = false;
					break;
				} 
			}
			if(isSelect)result.add(minSet.get(j));
		}
		return result;
	}
	private Set<String> parseAtomCondition(String atomCondition,Long systemId,Long companyId){
		Set<String> userSet =  new HashSet<String>();
		if(StringUtils.isEmpty(atomCondition))return userSet;
		if(atomCondition.trim().startsWith(CommonStrings.SYS_VAR_USER)){
			userSet.addAll(parseUser(atomCondition));
		}else if(atomCondition.trim().startsWith(CommonStrings.SYS_VAR_ROLE)){
			userSet.addAll(parseRole(atomCondition,systemId,companyId));
		}else if(atomCondition.trim().startsWith(CommonStrings.SYS_VAR_DEPARTMENT)){
			userSet.addAll(parseDepartment(atomCondition));
		}else if(atomCondition.trim().startsWith(CommonStrings.SYS_VAR_WORKGROUP)){
			userSet.addAll(parseWorkGroup(atomCondition));
		}else if(atomCondition.trim().equals(CommonStrings.PROCESS_ADMIN)){
			userSet.add(processAdmin);
		}else if(atomCondition.trim().equals(CommonStrings.CURRENTTRANSACTOR)){
			userSet.add(currentTransactor);
		}else if(atomCondition.trim().equals(CommonStrings.DOCUMENT_CREATOR)){
			userSet.add(documentCreator);
		}else if(atomCondition.trim().equals(CommonStrings.PARTICIPANTS_TRANSACTOR)){
			userSet.addAll(handledTransactors);
		}else if(atomCondition.trim().equals(CommonStrings.PARTICIPANTS_ALL_TRANSACTOR)){
			userSet.addAll(allHandleTransactors);
		}
		return userSet;
	}
	
	private Set<String> parseWorkGroup(String condition){
		Set<String> userSet =  new HashSet<String>();
		if(StringUtils.contains(condition, TextOperator.NET.getCode())) {
			Set<Workgroup> workgroupSet = getWorkGroup(StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			userSet.addAll(UserUtil.getUsersNotInWorkGroup(workgroupSet));
		}else if(StringUtils.contains(condition, TextOperator.ET.getCode())){
			Set<Workgroup> workgroupSet = getWorkGroup(StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			userSet.addAll(UserUtil.getUsersByWorkGroup(workgroupSet));
		}
		return userSet;
	}
	
	private Set<com.norteksoft.acs.entity.organization.Workgroup> getWorkGroup(String valueExpress){
		 RankManager rankManager=(RankManager)ContextUtils.getBean("rankManager");
		Set<Workgroup> workGroupSet = new HashSet<Workgroup>();
		if(StringUtils.isEmpty(valueExpress)) return workGroupSet;
		if(valueExpress.trim().startsWith("${field[")){
			int start = valueExpress.lastIndexOf(SQUARE_BRACKETS_LEFT);
			int end = valueExpress.indexOf(SQUARE_BRACKETS_RIGHT);
			String fieldName = valueExpress.substring(start + 1, end);
			String workGroupName = getValue(fieldName);
			workGroupSet.add(UserUtil.getWorkGroupByName(workGroupName));
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_WORKGROUP)){
			List<Workgroup> workGroups = UserUtil.getWorkGroupsByUser( documentCreator);
			workGroupSet.addAll(workGroups);
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_DIRECT_SUPERIOR_WORKGROUP)){
			User user=rankManager.getDirectLeader(documentCreator);
			if(user!=null){
				List<Workgroup> workGroups = UserUtil.getWorkGroupsByUser( user.getLoginName());
				workGroupSet.addAll(workGroups);
			}
		}else if(valueExpress.trim().equals(CommonStrings.PREVIOUS_TRANSACTOR_WORKGROUP)){
			List<Workgroup> workGroups = UserUtil.getWorkGroupsByUser(previousTransactor);
			workGroupSet.addAll(workGroups);
		}else if(valueExpress.trim().equals(CommonStrings.PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_WORKGROUP)){
			User user=rankManager.getDirectLeader(previousTransactor);
			if(user!=null){
				List<Workgroup> workGroups = UserUtil.getWorkGroupsByUser(user.getLoginName());
				workGroupSet.addAll(workGroups);
			}
		}else{
			workGroupSet.add(UserUtil.getWorkGroupByName(valueExpress));
		}
		return workGroupSet;
	}
	
	
	private Set<String> parseDepartment(String condition){
		Set<String> userSet =  new HashSet<String>();
		if(StringUtils.contains(condition, TextOperator.NET.getCode())) {
			Set<Department> departmentSet = getDepartment(StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			userSet.addAll(UserUtil.getUsersNotInDepartment(departmentSet));
		}else if(StringUtils.contains(condition, TextOperator.ET.getCode())){
			Set<Department> departmentSet = getDepartment(StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			if(departmentSet!=null)userSet.addAll(UserUtil.getUsersByDepartment(departmentSet));
		}
		return userSet;
	}
	
	private Set<Department> getDepartment(String valueExpress){
		 RankManager rankManager=(RankManager)ContextUtils.getBean("rankManager");
		Set<Department> departmentSet = new HashSet<Department>();
		if(StringUtils.isEmpty(valueExpress)) return departmentSet;
		if(valueExpress.trim().startsWith("${field[")){
			//值来自表单字段中
			int start = valueExpress.lastIndexOf(SQUARE_BRACKETS_LEFT);
			int end = valueExpress.indexOf(SQUARE_BRACKETS_RIGHT);
			String fieldName = valueExpress.substring(start + 1, end);
			String departmentName = getValue(fieldName);
			Department department = UserUtil.getDepartmentByName(departmentName);
			if(department!=null)departmentSet.add(department);
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_DEPARTMENT)){
			//文档创建人部门
			departmentSet.addAll(UserUtil.getDepartmentsByUser( documentCreator));
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_SUPERIOR_DEPARTMENT)){
			//文档创建人上级部门
			departmentSet.addAll(ApiFactory.getAcsService().getParentDepartmentsByUser(documentCreator));
			
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_UPSTAGE_DEPARTMENT)){
			//文档创建人顶级部门
			departmentSet.addAll(ApiFactory.getAcsService().getTopDepartmentsByUser(documentCreator));
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_DIRECT_SUPERIOR_DEPARTMENT)){
			//文档创建人直属上级部门
			User user=rankManager.getDirectLeader(documentCreator);
			if(user!=null){
				departmentSet.addAll(UserUtil.getDepartmentsByUser( user.getLoginName()));
			}
		}else if(valueExpress.trim().equals(CommonStrings.PREVIOUS_TRANSACTOR_DEPARTMENT)){
			//上环节办理人部门
			departmentSet.addAll(UserUtil.getDepartmentsByUser( previousTransactor));
		}else if(valueExpress.trim().equals(CommonStrings.PREVIOUS_TRANSACTOR_SUPERIOR_DEPARTMENT)){
			//上环节办理人上级部门
			departmentSet.addAll(ApiFactory.getAcsService().getParentDepartmentsByUser(previousTransactor));
		}else if(valueExpress.trim().equals(CommonStrings.PREVIOUS_TRANSACTOR_UPSTAGE_DEPARTMENT)){
			//上环节办理人顶级部门
			departmentSet.addAll(ApiFactory.getAcsService().getTopDepartmentsByUser(previousTransactor));
		}else if(valueExpress.trim().equals(CommonStrings.UPSTAGE_DEPARTMENT)){
			//顶级部门
			departmentSet.addAll(ApiFactory.getAcsService().getDepartments());
		}else if(valueExpress.trim().equals(CommonStrings.PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_DEPARTMENT)){
			//上一环节办理人直属上级的部门
			User user=rankManager.getDirectLeader(previousTransactor);
			if(user!=null){
				departmentSet.addAll(UserUtil.getDepartmentsByUser( user.getLoginName()));
			}
		}else{
			//值来自组织结构中
			departmentSet.add(UserUtil.getDepartmentByName(valueExpress));
		}
		return departmentSet;
	}
	
	private Set<String> parseRole(String condition,Long systemId,Long companyId){
		Set<String> userSet =  new HashSet<String>();
		if(StringUtils.contains(condition, TextOperator.NET.getCode())) {
			String roleName = getRoleName(StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			userSet.addAll(UserUtil.getUsersExceptRoleName(systemId,roleName));
		}else if(StringUtils.contains(condition, TextOperator.ET.getCode())){
			String roleName = getRoleName(StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			userSet.addAll(UserUtil.getUsersByRoleName(roleName,systemId,companyId));
		}
		return userSet;
	}
	
	private Set<String> parseUser(String condition){
		Set<String> userSet =  new HashSet<String>();
		if(StringUtils.contains(condition, TextOperator.NET.getCode())) {
			String loginName = getUserLoginName(StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			userSet.addAll(UserUtil.getUserExceptLoginName(loginName));
		}else if(StringUtils.contains(condition, TextOperator.ET.getCode())){
			String loginName = getUserLoginName(StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			if(StringUtils.isNotEmpty(loginName)){
				userSet.add(loginName);
			}
		}
		return userSet;
	}
	
	public void setDocumentCreator(String documentCreator) {
		this.documentCreator = documentCreator;
	}

	public void setCurrentTransactor(String currentTransactor) {
		this.currentTransactor = currentTransactor;
	}

	public void setPreviousTransactor(String previousTransactor) {
		this.previousTransactor = previousTransactor;
	}

	public void setFormView(FormView form) {
		this.formView = form;
	}
	
	public FormView getFormView() {
		return formView;
	}

	public void setDataId(Long dataId) {
		this.dataId = dataId;
	}
	public Long getDataId() {
		return dataId;
	}
	public String getApprovalResult() {
		return approvalResult;
	}
	public void setApprovalResult(String approvalResult) {
		this.approvalResult = approvalResult;
	}
	public void setProcessAdmin(String processAdmin) {
		this.processAdmin = processAdmin;
	}
	public void setHandledTransactors(Collection<String> handledTransactors) {
		this.handledTransactors = handledTransactors;
	}
	public void setAllHandleTransactors(Collection<String> allHandleTransactors) {
		this.allHandleTransactors = allHandleTransactors;
	}
	public String getDocumentCreator() {
		return documentCreator;
	}
	public String getCurrentTransactor() {
		return currentTransactor;
	}
	public String getPreviousTransactor() {
		return previousTransactor;
	}
	public String getProcessAdmin() {
		return processAdmin;
	}
	public Collection<String> getHandledTransactors() {
		return handledTransactors;
	}
	public Collection<String> getAllHandleTransactors() {
		return allHandleTransactors;
	}
}
