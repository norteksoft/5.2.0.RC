<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>我的流程</title>
	<%@ include file="/common/wf-iframe-meta.jsp"%>

	<script src="${wfCtx}/js/opinion.js" type="text/javascript"></script>
	
	
	<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
	<script src="${resourcesCtx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${resourcesCtx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>
	
	<link href="${imatrixCtx}/widgets/workflow-swfupload/default.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflow-swfupload/workflow-attachment-handlers.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflow-swfupload/swfupload.js"></script>
	
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflowEditor/swfobject.js"></script>
	
	<script type="text/javascript" src="${wfCtx}/js/util.js"></script>
	<script language="javascript" type="text/javascript" src="${wfCtx}/js/delegate-main.js"></script>
	<script type="text/javascript">
	//创建 委托
	function createForm(url){
		$("#delegatemain").attr("action",url);
		ajaxAnywhere.formName="delegatemain";
		ajaxAnywhere.getZonesToReload = function(){
			return "myprocess";
		};
		ajaxAnywhere.onAfterResponseProcessing = function () {
		 };
		ajaxAnywhere.submitAJAX();
	}


	function back(){
		goBack("defaultForm", "${wfCtx}/engine/delegate-main!myDelegate.htm", "myprocess","tasks");
	}

	function viewTask(ts1,cellval,opts,rwdat,_act){
		var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"openViewTask('"+opts.id+"');\">" + ts1 + "</a>";
		return v;
	}
	function openViewTask(id){
		var win = window.open(webRoot+"/engine/workflow!input.htm?taskId="+id,'win',"top=0,left=0,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=false,resizable=no,width="+screen.availWidth+",height="+screen.availHeight);
	}
	
	</script>
</head>
<body>
<div class="ui-layout-center">
<form id="defaultForm" action="" name="defaultForm" method="post">
	<input id="wf_instanceState" type="hidden" name="end" />
</form>
<div class="opt-body">
	<aa:zone name="myprocess">
		<div class="opt-btn">
		</div>
		<div id="opt-content" >
			<view:jqGrid url="${wfCtx}/engine/delegate-main!superviseAsTrustee.htm?isDone=${isDone}" pageName="tasks" code="WORKFLOW_TASK_TRUSTEE" gridId="main_table"></view:jqGrid>
		</div>
	</aa:zone>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
