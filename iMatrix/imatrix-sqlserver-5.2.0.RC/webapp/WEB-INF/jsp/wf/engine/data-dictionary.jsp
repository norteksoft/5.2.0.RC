<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
<title>数据字典</title>
<%@ include file="/common/wf-iframe-meta.jsp"%>
	
	
	
	<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
	<script src="${resourcesCtx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${resourcesCtx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>
	
	<script src="${wfCtx}/js/map.js" type="text/javascript"></script>
	<script src="${wfCtx}/js/data-dictionary.js" type="text/javascript"></script>

	<script type="text/javascript">
		function jmesaSubmit() {
			var url = $("#jmesaForm").attr("action");
			$("#queryTypeName1").attr("value",$("#queryTypeName").attr("value"));
			$("#queryTypeNo1").attr("value",$("#queryTypeNo").attr("value"));
			$("#queryName1").attr("value",$("#queryName").attr("value"));
			ajaxSubmit("jmesaForm",url,"dict_list_zone");
		}
		
	</script>
	<style type="text/css">
		.noListStyle li{
	list-style:none;
	list-style-type: none;
	}
	</style>
</head>
<body>
<div class="ui-layout-center">
<div class="opt-body">
	<form id="defaultForm" name="defaultForm" action="" method="post">
		<input id="dict_id" type="hidden" name="id"></input>
		<input id="dictIds" name="dictIds" type="hidden"></input>
	</form>
	<form id="returnForm" name="returnForm" action=""  method="post">
	</form>
	<aa:zone name="dict_zone">
	<div class="opt-btn">
		<button class='btn' onclick="iMatrix.showSearchDIV(this);" hidefocus="true"><span><span>查询</span></span></button>
		<button  class='btn' onclick="viewDict('');" hidefocus="true"><span><span>增加</span></span></button>
		<button class='btn' onclick="deleteDict('${wfCtx}/engine/data-dictionary!delete.htm','dictPage');" hidefocus="true"><span><span>删除</span></span></button>
	</div>
		<aa:zone name="dict_list_zone">
			<form name="jmesaForm" id="jmesaForm" method="post" action="${wfCtx}/engine/data-dictionary.htm">
				<input id="queryTypeNo1" name="queryTypeNo" maxlength="255" type="hidden">
				<input id="queryTypeName1" name="queryTypeName" maxlength="255" type="hidden"/>
				<input id="queryName1" name="queryName" maxlength="255" type="hidden">
			</form >
		<div id="opt-content" >
			<form name="searchSubmit" id="searchSubmit" method="post" action="">
				<view:jqGrid url="${wfCtx}/engine/data-dictionary.htm" pageName="dictPage" code="WF_DATA_DICTIONARYS" gridId="main_table" submitForm="returnForm"></view:jqGrid>
			</form>
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
		</aa:zone>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
