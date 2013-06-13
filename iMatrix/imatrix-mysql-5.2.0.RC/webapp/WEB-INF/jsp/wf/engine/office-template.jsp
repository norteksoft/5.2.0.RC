<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>正文模板</title>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
	
	<script type="text/javascript" src="${resourcesCtx}/widgets/tree/_lib/css.js"></script>
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/tree/tree_component.css" />
	<script type="text/javascript" src="${resourcesCtx}/widgets/tree/tree_component.js"></script>
	
	
	<!-- 上传组件js start-->
	<link href="${imatrixCtx}/widgets/swfupload/default.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/swfupload.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/handlers.js"></script>
	<!-- 上传组件js end -->
	<script type="text/javascript" src="${wfCtx }/js/office-template.js"></script>
	
	<!-- formValidator -->
	<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
	<script src="${resourcesCtx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${resourcesCtx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>
	<!--  -->
</head>
<body>
<div class="ui-layout-center">
<div class="opt-body">
		<form id="backForm" action="" name="backForm" method="post">
			<input id="backTypeId" name="backTypeId" value="${backTypeId }" type="hidden"/>
		</form>
	<aa:zone name="wf_template">
		<form id="defaultForm" action="" name="defaultForm" method="post">
			<input name="typeId" id="wf_type" value="${typeId }" type="hidden"/>
			<input name="id" id="templateId" value="" type="hidden"/>
		</form>
		<form id="updateForm" action="" name="updateForm" method="post"></form>
		
		<div class="opt-btn">
			<security:authorize ifAnyGranted="wf_engine_wf_type_input">
				<button  class='btn' onclick="option('add');" hidefocus="true"><span><span>增加</span></span></button>
				<button class='btn' onclick="option('update');" hidefocus="true"><span><span>修改</span></span></button>
			</security:authorize>
			<security:authorize ifAnyGranted="wf_engine_wf_type_delete ">
				<button class='btn' onclick="option('delete');" hidefocus="true"><span><span>删除</span></span></button>
			</security:authorize>
		</div>
		<div id="message" class="onSuccess" style="display: none;"><s:actionmessage theme="mytheme" /></div>
		<form id="templateForm" name="templateForm" method="post">
			<input name="typeId" value="${typeId }" type="hidden"/>
		</form>
		<div id="opt-content" >
			<view:jqGrid url="${wfCtx}/engine/office-template.htm?typeId=${typeId }" pageName="page" code="WF_TEMPLATE_FILE" gridId="main_table"></view:jqGrid>
		</div>
	</aa:zone>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
