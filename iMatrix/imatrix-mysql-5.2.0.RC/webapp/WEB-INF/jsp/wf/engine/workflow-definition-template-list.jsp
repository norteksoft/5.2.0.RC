<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>正文模板</title>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
	
	<!-- 上传组件js start-->
	<link href="${imatrixCtx}/widgets/swfupload/default.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/swfupload.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/handlers.js"></script>
	
	
	<!-- formValidator -->
	<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
	<script src="${resourcesCtx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${resourcesCtx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>
	
	<script type="text/javascript" src="${wfCtx }/js/workflowDefinitionTemplate.js"></script>
</head>
<body>
<div class="ui-layout-center">
<div class="opt-body">
	<form id="defaultForm" action="" name="defaultForm" method="post">
		<input id="wf_type" value="${typeId }" name="typeId" type="hidden"/>
	</form>
	<aa:zone name="wf_template">
	<form id="updateForm" action="" name="updateForm" method="post"></form>
		<div class="opt-btn">
			<button class='btn' onclick="iMatrix.showSearchDIV(this);" hidefocus="true"><span><span>查询</span></span></button>
			<security:authorize ifAnyGranted="wf_engine_workflow-definition-template-input">
				<button  class='btn' onclick="createWorkflowDefinitionTemplate('${wfCtx}/engine/workflow-definition-template-input.htm');" hidefocus="true"><span><span>增加</span></span></button>
				<button class='btn' onclick="updateWorkflowDefinitionTemplate('${wfCtx}/engine/workflow-definition-template-input.htm');" hidefocus="true"><span><span>修改</span></span></button>
			</security:authorize>
			<security:authorize ifAnyGranted="wf_engine_workflow-definition-template-delete ">
				<button class='btn' onclick="deleteWorkflowDefinitionTemplate('${wfCtx}/engine/workflow-definition-template-delete.htm');" hidefocus="true"><span><span>删除</span></span></button>
			</security:authorize>
		</div>
		<div id="message" class="onSuccess" style="display: none;"><s:actionmessage theme="mytheme" /></div>
		
			<div id="opt-content" >
				<form id="templateForm" name="templateForm" method="post">
					<view:jqGrid url="${wfCtx}/engine/workflow-definition-template-list.htm?typeId=${typeId }" pageName="page" code="WF_DEFINITION_TEMPLATE" gridId="main_table"></view:jqGrid>
				</form>
			</div>
		
	</aa:zone>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
