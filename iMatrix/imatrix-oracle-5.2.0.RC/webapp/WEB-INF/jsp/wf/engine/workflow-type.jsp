<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>流程类型管理</title>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
	
	<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
	<script src="${resourcesCtx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${resourcesCtx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>	
	<script type="text/javascript" src="${resourcesCtx}/widgets/tree/_lib/css.js"></script>
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/tree/tree_component.css" />
	<script type="text/javascript" src="${resourcesCtx}/widgets/tree/tree_component.js"></script>
	
	<script src="${wfCtx }/js/workflow-type.js" type="text/javascript"></script>
</head>
<body>
<div class="ui-layout-center">
<div class="opt-body">
	<form id="defaultForm" action="" name="defaultForm" method="post"></form>
<aa:zone name="wf_type">
		<div class="opt-btn">
			<security:authorize ifAnyGranted="wf_engine_wf_type_input">
				<button  class='btn' onclick="option('add');" hidefocus="true"><span><span>增加</span></span></button>
				<button class='btn' onclick="option('update');" hidefocus="true"><span><span>修改</span></span></button>
			</security:authorize>
			<security:authorize ifAnyGranted="wf_engine_wf_type_delete ">
				<button class='btn' onclick="option('delete');" hidefocus="true"><span><span>删除</span></span></button>
			</security:authorize>
		</div>
		<div id="message" class="onSuccess" style="display:none;"><s:actionmessage theme="mytheme" /></div>
		<form id="wf_type_form" name="wf_type_form" action="">
			<input type="hidden" name="id" id="wftypeId" value="" />
			<input type="hidden" name="typeIds" id="typeIds" value="" />
			<span id="note" style="display:none" class="onNote"></span>
		</form>
		<div id="opt-content" >
			<view:jqGrid url="${wfCtx}/engine/workflow-type.htm" pageName="page" code="WF_TYPE" gridId="main_table"></view:jqGrid>
		</div>
		<script>
			$(document).ready(function(){
				$('#_boxHeader').click(function(){
					if($(this).attr('checked')){
						$('input[class="_boxBody"]').attr('checked', 'checked');
					}else{
						$('input[class="_boxBody"]').attr('checked', '');
					}
				});
				$('._boxBody').click(function(){
					var boxes = $('input[class="_boxBody"]');
					var allChecked = true;
					for(var i = 0; i < boxes.length; i++){
						if(!$(boxes[i]).attr('checked')){
							allChecked = false;
						}
					}
					if(allChecked){
						$('#_boxHeader').attr('checked', 'checked');
					}else{
						$('#_boxHeader').attr('checked', '');
					}
				});
			});
		</script>
</aa:zone>
</div>
</div>	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
