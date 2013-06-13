<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/common/portal-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>个人消息管理</title>
	<%@ include file="/common/portal-meta.jsp"%>
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/jqgrid/ui.jqgrid.css" />
	<script type="text/javascript" src="${resourcesCtx}/widgets/jqgrid/jqgrid-all-1.0.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/jqgrid/jqGrid.custom.js"></script>
	
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript">
		isUsingComonLayout=false;
		function saveBaseSetting(formId,url){
			$("#"+formId).attr("action",url);
			$("#"+formId).ajaxSubmit(function (id){
				//parent.ajaxSubmit("defaultForm","${portalCtx}/index/my-message.htm?messageType=ONLINE_MESSAGE", "messageList");
				//if(id=='true'){
				//	parent.$("#dialog").dialog("open");
				//	parent.$("#message_visible").attr("value",'true');
				//}else{
				//	parent.$("#dialog").dialog("close");
				//	parent.$("#message_visible").attr("value",'false');
				//}
				parent.$.colorbox.close();
			});
		}
		function changeMessageVisible(obj){
			$("#messageVisible").attr("value",$(obj).attr("checked"));
		}
	</script>
</head>
<body>
<div class="opt-body">
	<div class="opt-btn">
		<a class="btn" href="#" onclick="saveBaseSetting('defaultForm','${portalCtx}/index/base-setting-save.htm');"><span><span >保存</span></span></a>
	</div>
	<div id="opt-content">
		<form action="" name="defaultForm" id="defaultForm" method="post">
			<input name="id" id="id" value="${id }" type="hidden">
			<FIELDSET><legend>个人消息设置</legend>
				<table class="form-table-without-border">
					<tr>
						<td>是否显示弹框:</td>
						<td><input name="messageVisible" id="messageVisible" type="hidden" value="${messageVisible }"></input><input id="ifMessageVisible" <s:if test="messageVisible">checked="checked"</s:if> type="checkbox" onclick="changeMessageVisible(this);"></input></td>
					</tr>
					<tr>
						<td>刷新时间间隔:</td><td><input style="width: 50%;" name="refreshTime" id="refreshTime" value="${refreshTime}" onkeyup="value=value.replace(/[^\d]/g,'');value=value.length>3?value.substr(0,3):value"/><span style="color: green;">（秒）</span></td>
					</tr>
					<tr>
						<td>显示条数:</td><td><input style="width: 50%;" name="showRows" id="showRows" value="${showRows}" onkeyup="value=value.replace(/[^\d]/g,'');value=value.length>3?value.substr(0,3):value"/><span style="color: green;">（条）</span></td>
					</tr>
				</table>
			</FIELDSET>	
		</form>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>