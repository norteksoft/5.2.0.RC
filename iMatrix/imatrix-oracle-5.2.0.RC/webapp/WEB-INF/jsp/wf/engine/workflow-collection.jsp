<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">

<%@page import="com.norteksoft.product.util.WebContextUtils"%><html>
<head>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
	<title>流程汇编</title>
	
	
	<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
	<script src="${resourcesCtx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${resourcesCtx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>
	
	<link href="${imatrixCtx}/widgets/workflow-swfupload/default.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflow-swfupload/swfupload.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflow-swfupload/handlers.js"></script>
	
	<script language="javascript" type="text/javascript" src="${imatrixCtx}/widgets/calendar/WdatePicker.js"></script>
	
	<script src="${wfCtx }/js/workflow-instance.js" type="text/javascript"></script>
	<script src="${wfCtx }/js/util.js" type="text/javascript"></script>
	<script src="${wfCtx }/js/task.js" type="text/javascript"></script>
	<script src="${resourcesCtx}/js/form.js" type="text/javascript"></script>
	
	<script src="${wfCtx }/js/workflow.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflow-swfupload/workflow-attachment-handlers.js"></script>
	<!-- flex右键 -->
	<script src="${imatrixCtx}/widgets/workflowEditor/rightClick.js" type="text/javascript"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflowEditor/swfobject.js"></script>
	
	<!-- 该主页对应js文件 -->
	<script type="text/javascript" src="${wfCtx }/js/workflow-collection.js"></script>
	<script src="${wfCtx }/js/opinion.js"></script>
	
	<script type="text/javascript">
	function back(){
		goBack("defaultForm", webRoot + "/engine/workflow!collection.htm", "myprocess","wfdPage");
	}
	
	</script>
</head>
<body>
<div class="ui-layout-center">
<form id="defaultForm" action="" name="defaultForm" method="post">
	<input type="hidden" id="wf_type"   name="type"  value="${type }"/>
	<input type="hidden" id="wf_defId"   name="wfdId" />
	<input type="hidden" id="processId"  name="processId" />
	<input type="hidden" id="formHtml"   name="formHtml" />
	<input type="hidden" id="systemId" name="systemId"   value="<%=WebContextUtils.getSystemId()%>"/>
	<input type="hidden" id="localeLang"  name="localeLang"  value="<%= request.getLocale().getLanguage() %>" />
</form>
<div class="opt-body">
	<aa:zone name="myprocess">
		<div class="opt-btn">
			<security:authorize ifAnyGranted="wf_engine_start_process">
				<button id="createInst" class="btn" onclick="createInstance();"><span><span >发起</span></span></button>
				<div id="backMsg" style="margin-left: 12px;color: red;"><s:actionmessage theme="mytheme"/></div>
			</security:authorize>
		</div>
		<div id="opt-content" >
			<view:jqGrid url="${wfCtx}/engine/workflow!collection.htm?type=${type }" pageName="wfdPage" code="WF_COLLECTION" gridId="main_table"></view:jqGrid>
		</div>
	</aa:zone>
</div> 
</div>  
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>