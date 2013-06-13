package com.norteksoft.wf.base.enumeration;

public interface CommonStrings {
	public static final String NEED_GENERATE_TASK = "need_generate_task";//提示办理人需要生成任务
	
	public static final String PARENT_INSTANCE_ID = "parent_instance_id";//提示办理人需要生成任务
	
	public static final String TRANSACTOR_ASSIGNMENT = "_assignment_transactor";
	public static final String TRANSACTOR_SINGLE = "_assignment_transactor_single";
	
	public static final String TRANSACTOR_SINGLE_CANDIDATES = "_transactor_single_candidates";//选择具体办理人
	
	public static final String TRANSITION_NAME = "_transition_name";
	public static final String NEW_TRANSACTOR = "_new_transactor";
//	public static final String CURRENTUSERLONGINNAME = "${currentUserLonginName}";//当前用户登录名
//	public static final String CURRENTUSERNAME = "${currentUserName}";//当前用户的用户名
	
	public static final String CURRENTTRANSACTOR = "${currentTransactor}";//本环节办理人登录名
	public static final String CURRENTTIME ="${currentTime}";//本环节办理时间
	public static final String CURRENTOPERATION = "${currentOperation}";//本环节办理人执行的操作

	public static final String CURRENT_OPERATTION_STRING = "_previous_task_operation"; //上一环节执行的操作
	public static final String PREVIOUS_TASK_TRANSACTOR = "_previous_task_transactor";  //上一环节任务的办理人
	public static final String PREVIOUS_TASK_PRINCI_TRANSACTOR="_previous_task_princi_transactor";//上一环节任务的委托人
	public static final String PREVIOUS_TASK_NAME = "_previous_task_name";  //上一环节任务名称
	public static final String COLLECTIVE_OPERATION = "task_collective_operation";
	
	public static final String ASSIGN_TO = "assign to:";
	
	public static final String PREVIOUS_TRANSACTOR_UPSTAGE_DEPARTMENT = "${previousTransactorUpstageDepartment}";//上一环节办理人的顶级部门
	public static final String PREVIOUS_TRANSACTOR_SUPERIOR_DEPARTMENT ="${previousTransactorSuperiorDepartment}";//上一环节办理人的上级部门
	public static final String DOCUMENT_CREATOR_UPSTAGE_DEPARTMENT = "${documentCreatorUpstageDepartment}";//文档创建人的顶级部门
	public static final String DOCUMENT_SUPERIOR_DEPARTMENT = "${superiorDepartment}";//文档创建人的上级部门
	public static final String UPSTAGE_DEPARTMENT = "${upstageDepartment}";//顶级部门
	public static final String CURRENT_TRANSACTOR_UPSTAGE_DEPARTMENT = "${currentTransactorUpstageDepartment}";//当前办理人的顶级部门
	public static final String CURRENT_TRANSACTOR_SUPERIOR_DEPARTMENT = "${currentTransactorSuperiorDepartment}";//当前办理人的上级部门
	public static final String CURRENT_TRANSACTOR_DIRECT_SUPERIOR_NAME="${currentTransactorDirectSuperiorName}";//当前办理人直属上级名称
	public static final String CURRENT_TRANSACTOR_DIRECT_SUPERIOR_LOGIN_NAME="${currentTransactorDirectSuperior}";//当前办理人直属上级登录名（环节自动填写有用）
	public static final String CURRENT_TRANSACTOR_DIRECT_SUPERIOR_DEPARTMENT="${currentTransactorDirectSuperiorDepartment}";//当前办理人直属上级部门
	public static final String CURRENT_TRANSACTOR_DIRECT_SUPERIOR_MAIN_DEPARTMENT="${currentTransactorDirectSuperiorMainDepartment}";//当前办理人直属上级正职部门(环节自动填写有用)
	public static final String CURRENT_TRANSACTOR_DIRECT_SUPERIOR_ROLE="${currentTransactorDirectSuperiorRole}";//当前办理人直属上级角色
	public static final String CURRENT_TRANSACTOR_DIRECT_SUPERIOR_WORKGROUP="${currentTransactorDirectSuperiorWorkGroup}";//当前办理人直属上级工作组
	public static final String CURRENT_TRANSACTOR_NAME = "${currentTransactorName}"; //本环节办理人姓名
	public static final String CURRENT_TRANSACTOR_ROLE = "${currentTransactorRole}";//本环节办理人角色
	public static final String CURRENT_TRANSACTOR_DEPARTMENT = "${currentTransactorDepartment}";//本环节办理人部门
	public static final String CURRENT_TRANSACTOR_MAIN_DEPARTMENT = "${currentTransactorMainDepartment}";//本环节办理人正职部门(环节自动填写有用)
	public static final String CURRENT_TRANSACTOR_WORKGROUP = "${currentTransactorWorkGroup}";//本环节办理人工作组
	
	public static final String DOCUMENT_CREATOR_NAME = "${documentCreatorName}"; //文档创建人姓名
	public static final String DOCUMENT_CREATOR_ROLE = "${documentCreatorRole}";//文档创建人角色
	public static final String DOCUMENT_CREATOR_DEPARTMENT = "${documentCreatorDepartment}";//文档创建人部门
	public static final String DOCUMENT_CREATOR_WORKGROUP = "${documentCreatorWorkGroup}";//文档创建人工作组	
	public static final String DOCUMENT_CREATOR_DIRECT_SUPERIOR_NAME="${documentCreatorDirectSuperiorName}";//创建人直属上级名称
	public static final String DOCUMENT_CREATOR_DIRECT_SUPERIOR_DEPARTMENT="${documentCreatorDirectSuperiorDepartment}";//创建人直属上级部门
	public static final String DOCUMENT_CREATOR_DIRECT_SUPERIOR_ROLE="${documentCreatorDirectSuperiorRole}";//创建人直属上级角色
	public static final String DOCUMENT_CREATOR_DIRECT_SUPERIOR_WORKGROUP="${documentCreatorDirectSuperiorWorkGroup}";//创建人直属上级工作组
	
	public static final String PREVIOUS_TRANSACTOR = "${previousTransactor}";//上一环节办理人登录
	public static final String PREVIOUS_TRANSACTOR_NAME = "${previousTransactorName}";//上一环节办理人姓名
	public static final String PREVIOUS_TRANSACTOR_ROLE = "${previousTransactorRole}";//上一环节办理人角色
	public static final String PREVIOUS_TRANSACTOR_DEPARTMENT = "${previousTransactorDepartment}"; // 上一环节办理人部门
	public static final String PREVIOUS_TRANSACTOR_WORKGROUP = "${previousTransactorWorkGroup}";//上一环节办理人工作组	
	public static final String PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_NAME="${previousTransactorDirectSuperiorName}";//上一环节办理人直属上级名称
	public static final String PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_DEPARTMENT="${previousTransactorDirectSuperiorDepartment}";//上一环节办理人直属上级部门
	public static final String PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_ROLE="${previousTransactorDirectSuperiorRole}";//上一环节办理人直属上级角色
	public static final String PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_WORKGROUP="${previousTransactorDirectSuperiorWorkGroup}";//上一环节办理人直属上级工作组
	
	public static final String FAVOR_COUNT = "${favorCount}";//赞成票总数
	public static final String AGAINST_COUNT = "${againstCount}";//反对票总数
	public static final String ABSTENTION_COUNT = "${abstentionCount}" ;//弃权票总数
	public static final String COUNTERSIGNATURE_AGREE_PERCENTAGE = "${countersignatureAgreePercentage}";//会签同意人员百分比
	public static final String COUNTERSIGNATURE_DISAGREE_PERCENTAGE = "${countersignatureDisagreePercentage}";//会签不同意人员百分比
	public static final String FAVOR_PERCENTAGE = "${favorPercentage}";//赞成票百分比
	public static final String AGAINST_PERCENTAGE = "${againstPercentage}";//反对票百分比
	public static final String ABSTENTION_PERCENTAGE = "${abstentionPercentage}";//弃权票百分比
	public static final String COUNTERSIGNATURE_AGREE_COUNT = "${countersignatureAgreeCount}";//会签同意人员总数
	public static final String COUNTERSIGNATURE_DISAGREE_COUNT = "${countersignatureDisagreeCount}";//会签不同意人员总数
	public static final String APPROVAL_RESULT = "${approvalResult}" ;//审批结果
	
	
	public static final String DOCUMENT_CREATOR = "${documentCreator}"; //文档创建人
	public static final String PROCESS_ADMIN = "${processAdmin}";        //流程管理员
	public static final String PARTICIPANTS_TRANSACTOR = "${hasHandledTransactor}"; //参与办理人
	public static final String PARTICIPANTS_ALL_TRANSACTOR = "${allHandleTransactors}"; //参与办理人
	
	public static final String TASK_URL_PREFIX = "url_";
	
	public static final String DEFAULT_ENTITY_NAME = "wf_value_prefix";
	
	public static final String ALL_USER = "all_user";
	public static final String ALL_DEPARTMENT = "all_department";
	public static final String ALL_WORKGROUP = "all_workGroup";
	
	public static final String MINISTER="012";//部长角色编号
	
	
	public static final String SYS_VAR_USER = "${user}";
	public static final String SYS_VAR_ROLE = "${role}";
	public static final String SYS_VAR_DEPARTMENT = "${department}";
	public static final String SYS_VAR_WORKGROUP = "${workGroup}";
	
	public static final String RTX_STYLE = "rtx";//RTX方式
	public static final String EMAIL_STYLE = "mail";//email方式
	public static final String SMS_STYLE = "message";//短信方式
	public static final String SWING_STYLE = "swing";//办公助手方式
	
	public static final String DATA_SOURCE="data_sourc";//主表单对应的子表单
	public static final String DATA_SOURCE_FIELD="data_sourc_field";//主表单对应的子表单中的字段名称
	public static final String DATA_SOURCE_FIELD_VALUE="data_sourc_field_value";//主表单对应的子表单中的字段名称/值
	public static final String IS_ORIGINAL_USER="is_original_user";//流向中设置使用目标任务的原办理人登录名
	public static final String ALL_ORIGINAL_USERS="all_original_users";//上次目标任务的所有办理人的登录名
	
	/**
	 * 工作流引擎使用的变量key
	 * 文档创建人的后续者
	 */
	public static final String CREATOR_CANDIDATES = "creatorCandidates"; 
	
	/**
	 * 工作流引擎使用的变量key
	 * 文档创建人
	 */
	public static final String CREATOR = "creator"; 
	
	/**
	 * 工作流引擎使用的变量key
	 * 父流程的workflowId
	 */
	public static final String PARENT_WORKFLOW_ID = "parentWorkflowId"; 
	
	/**
	 * 工作流引擎使用的变量key
	 * 父流程发起子流程的环节名
	 */
	public static final String PARENT_TACHE_NAME = "parentTacheName"; 
	
	/**
	 * 工作流引擎使用的变量key
	 * 流程紧急程度标志
	 */
	public static final String PRIORITY = "priority";
	
	/**
	 * 流程实例id
	 */
	public static final String PROCESS_ID = "processId";

	/**
	 * 父流程的executionId
	 */
	public static final String PARENT_EXECUTION_ID = "parentExecutionId";
	/**
	 * 表单数据id
	 */
	public static final String FORM_DATA_ID="formDataId";
	
	public static final String SUBPROCESS_TASK_ID="subprocess_task_id";
	
	public static final String SUBPROCESS_PARSE="subprocess_parse";
	public static final String COMPANY_ID="companyId";
	public static final String SYSTEM_ID="systemId";
	/**
	 * 流程取消标识:true:取消,false：非取消
	 */
	public static final String CANCEL_FLAG = "cancel";
	/**
	 * 流程强制结束标识:true:强制结束,false：非强制结束
	 */
	public static final String COMPEL_END_FLAG = "compelEnd";
	
	public static final String WORKFLOW_PARAMETER_URL="workflowParameterUrl.properties";
	public static final String PROCESS_TASK_URL="process.task.url";
	public static final String PROCESS_TASK_PARAMTER_NAME="process.task.paramter.name";
	public static final String FORM_VIEW_URL="form.view.url";
	public static final String FORM_VIEW_PARAMTER_NAME="form.view.paramter.name";
	public static final String FORM_URGEN_URL="form.urgen.url";
	public static final String FORM_URGEN_PARAMTER_NAME="form.urgen.paramter.name";
}
