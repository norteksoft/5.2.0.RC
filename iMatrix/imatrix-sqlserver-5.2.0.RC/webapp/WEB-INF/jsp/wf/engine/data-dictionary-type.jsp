<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<%@ include file="/common/wf-iframe-meta.jsp"%>
	<link href="${wfCtx}/css/workflow.css" rel="stylesheet" type="text/css"/>
	
	
	<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
	<script src="${resourcesCtx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${resourcesCtx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>
	
	<script src="${wfCtx}/js/map.js" type="text/javascript"></script>
	<script src="${wfCtx}/js/data-dictionary.js" type="text/javascript"></script>
<body>
<div class="ui-layout-center">
<div class="opt-body">
	<form id="defaultForm" name="defaultForm"action="">
		<input id="dict_id" type="hidden" name="id"></input>
		<input id="dictIds" name="dictIds" type="hidden"></input>
	</form>
	<aa:zone name="dict_zone">
		<div class="opt-btn">
			<button  class='btn' onclick="viewDictType('add');" hidefocus="true"><span><span>增加</span></span></button>
			<button class='btn' onclick="deleteDictType('${wfCtx}/engine/data-dictionary-type!delete.htm');" hidefocus="true"><span><span>删除</span></span></button>
		</div>
		<div id="queryDiv" style="display: none;">
			<form name="queryForm" id="queryForm" method="post" action="">
				<input id="queryTypeNo" name="queryTypeNo" maxlength="255"/>
				<input id="queryTypeName" name="queryTypeName" maxlength="255"/>
			</form>
		</div>
		<form name="jmesaForm" id="jmesaForm" method="post" action="${wfCtx}/engine/data-dictionary-type.htm">
		</form >
		<div id="opt-content" >
			<view:jqGrid url="${wfCtx}/engine/data-dictionary-type.htm" code="WF_DATA_DICTIONARY_TYPE" pageName="page" gridId="main_table"></view:jqGrid>
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