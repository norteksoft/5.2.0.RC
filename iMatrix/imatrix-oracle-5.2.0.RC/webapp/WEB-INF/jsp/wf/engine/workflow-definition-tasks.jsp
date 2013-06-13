<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>流程管理</title>
<%@ include file="/common/wf-iframe-meta.jsp"%>
<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
<script src="${wfCtx }/js/workflow-definition.js" type="text/javascript"></script>
<style type="text/css">
table{ border-collapse: collapse;}
table.tasks tbody tr td{ border: solid 1px; padding: 1px 4px;}
</style>
<script type="text/javascript">
function deleteTasks(){
	//var taskIds = $("input[name='wtIds']:checked");
	var taskIds = jQuery("#main_table").getGridParam('selarrrow');
	if(taskIds.length<1){
		$("#message").html("<font class=\"onError\"><nobr>请选择一条记录</nobr></font>");
		showMsg("message");
	}else{
		for(var i=0;i<taskIds.length;i++){
			$("#taskIdDiv").append("<input name='taskIds' type='hidden' value='"+taskIds[i]+"'></input>");
		}
		ajaxSubmit('form', webRoot+'/engine/workflow-definition!delTasksBatch.htm?wfdId=${wfdId }&type=${type }&definitionCode=${definitionCode }', 'wfd_main',callBack); 
	}
}
function callBack(){
	showMsg();
	$($("#searchBtn").children().children()).html("查询");
	$("#taskIdDiv").html("");
	contentResize();
	getContentHeight();
}
function cancelDeleteTasks(){
	ajaxSubmit('form', '${wfCtx }/engine/workflow-definition!searchTaskDatas.htm?wfdId=${wfdId }&type=${type }&definitionCode=${definitionCode }', 'wfd_main'); 
}
function doDeleteTasks(){
	var taskIds = $("input:checked");
	if(taskIds.length==0){
		$("#message").html("<font class=\"onError\"><nobr>请选择记录</nobr></font>");
		showMsg("message");
		return;
	}
	for(var i=0;i<taskIds.length;i++){
		$("#taskIdDiv").append("<input name='taskIds' type='hidden' value='"+$(taskIds[i]).attr('value')+"'></input>");
	}
	ajaxSubmit('form', webRoot+'/engine/workflow-definition!delTasksBatch.htm?wfdId=${wfdId }&type=${type }&definitionCode=${definitionCode }', 'wfd_main',callBack); 
}
</script>
</head>
<body onload="getContentHeight_ColorIframe();">
<div class="ui-layout-center" style="top:0;left:0;border: 0px;">
	<div class="opt-body">
	<form action="" name="form" id="form" method="post">
		<div style="display:none;" id="taskIdDiv"></div>
	</form>
	<aa:zone name="wfd_main">
		<div class="opt-btn">
			<button class='btn' onclick="iMatrix.showSearchDIV(this);" id="searchBtn"><span><span>查询</span></span></button>
			<button class='btn' onclick="deleteTasks();" ><span><span>执行移除</span></span></button>
		</div>
		<div id="opt-content" >
			<aa:zone name="taskList">
			<span id="message" style="display:none;margin-left: 10px;" ><s:actionmessage theme="mytheme" /></span>
				<form action="${wfCtx }/engine/workflow-definition!searchTasks.htm" name="pageForm" id="pageForm" method="post">
					<view:jqGrid url="${wfCtx }/engine/workflow-definition!searchTaskDatas.htm?wfdId=${wfdId }&type=${type }&definitionCode=${definitionCode }" pageName="tasks" code="TASK_MONITOR" gridId="main_table" submitForm="form"></view:jqGrid>
				</form>
			</aa:zone>
		</div>
	</aa:zone>
	</div>
	</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
