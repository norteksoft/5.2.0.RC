<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
<%@ include file="/common/wf-iframe-meta.jsp"%>
<title>流程管理</title>
<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
	<script src="${resourcesCtx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${resourcesCtx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>
	
	<link href="${imatrixCtx}/widgets/workflow-swfupload/default.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflow-swfupload/swfupload.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflow-swfupload/handlers.js"></script>
	
	
	<script src="${wfCtx }/js/workflow-instance.js" type="text/javascript"></script>
	<script src="${wfCtx }/js/util.js" type="text/javascript"></script>
	<script src="${wfCtx }/js/task.js" type="text/javascript"></script>
	<script src="${resourcesCtx}/js/form.js" type="text/javascript"></script>
	
	<script src="${wfCtx }/js/workflow.js"></script>
	<link href="${wfCtx}/css/workflow.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflow-swfupload/workflow-attachment-handlers.js"></script>
	<!-- flex右键 -->
	<script src="${imatrixCtx}/widgets/workflowEditor/rightClick.js" type="text/javascript"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflowEditor/swfobject.js"></script>
	
	<!-- 该主页对应js文件 -->
	<script type="text/javascript" src="${wfCtx }/js/workflow-collection.js"></script>
	<script src="${wfCtx }/js/opinion.js"></script>
	<script>
	$().ready(function(){
		addSWf("view");
		
		$( "#tabs" ).tabs({select:function(event,ui){
		}});
	});
	</script>
</head>
<body>
<div class="ui-layout-center">
<div class="opt-body">
	<aa:zone name="wf_definition">
		<input id="companyId" name="companyId" type="hidden" value="${companyId}"/>		
		<input type="hidden" name="wfdId" id="wfdId" value="${wfdId}"/>
		<input id="systemId" name="systemId" type="hidden" value="${systemId }"/>
		<div class="opt-btn">
			<button id="createInst" class="btn" onclick="selectProcess('${wfDefinitionId}');"><span><span >发起</span></span></button>
			<button id="backbutton" class="btn" onclick="back_main();"><span><span >返回</span></span></button>
		</div>
		<div id="opt-content" >
			<div id="tabs">
				<ul>
					<li ><a href="#tabs-1" onclick="changeDefTab('a1', '${wfdId}')">定义</a></li>
					<li ><a href="#tabs-1" onclick="changeDefTab('b1', '${wfdId}')">表单</a></li>
				</ul>
				<div id="tabs-1">
					<div id="flashcontent" style="width:100%;height: 600px;margin-top:5px;display: block;">
					</div>
					<div id="htmlcontent" style="display: none;">
						${formHtml}	
					</div>
				</div>
			</div>
		</div>
	</aa:zone>
</div>
</div> 
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
