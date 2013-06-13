<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/task-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	
	<title>流程通知</title>
	<%@ include file="/common/task-meta.jsp"%>
	<script src="${resourcesCtx}/js/jquery.timers-1.2.js" type="text/javascript"></script>
	<script src="${taskCtx}/js/task.js" type="text/javascript"></script>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		<aa:zone name="taskZone">
			<s:if test="active==0">
				<div class="opt-btn">
				</div>
			</s:if>
			<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
			<div id="opt-content">
				<form action="" name="taskSaveForm" id="taskSaveForm" method="post"> 
					<input name="id" type="hidden" value="${id }"/>
					<table class="form-table-without-border">
						<tr>
							<td class="content-title">主题：</td>
				  			<td><input id="notificationTheme" name="notificationTheme" value="${notificationTheme }" maxlength="100" readonly="readonly"></input></td>
						</tr>
						<tr>
							<td class="content-title">正文：</td>
				  			<td><textarea id="notificationContent" name="notificationContent" cols="55" rows="10" style="width: 500px;" readonly="readonly">${notificationContent }</textarea></td>
						</tr>
					</table>
				</form>
			</div>
		</aa:zone>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
