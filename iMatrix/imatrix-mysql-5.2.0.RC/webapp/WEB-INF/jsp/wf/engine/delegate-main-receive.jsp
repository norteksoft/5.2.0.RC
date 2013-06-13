<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
	<title>委托管理</title>
	<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
	<script src="${resourcesCtx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${resourcesCtx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>
	
	<script language="javascript" type="text/javascript" src="${imatrixCtx}/widgets/calendar/WdatePicker.js"></script>
	<script src="${wfCtx}/js/workflowTag.js"></script>

	<script language="javascript" type="text/javascript" src="${wfCtx}/js/delegate-main.js"></script>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		<form action="${wfCtx}/engine/delegate-main!receive.htm" name="backForm" id="backForm" method="post"></form>
		<form action="${wfCtx}/engine/delegate-main!receive.htm" name="delegatemain" id="delegatemain" method="post" ></form>
		<aa:zone name="delegatemainlist">
			<div class="opt-btn">
			</div>
			<div id="opt-content" >
				<view:jqGrid url="${wfCtx}/engine/delegate-main!receive.htm" pageName="receivePage" code="DELEGATE_RECEIV" gridId="main_table"></view:jqGrid>
			</div>
		</aa:zone>
	</div>
</div> 
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
