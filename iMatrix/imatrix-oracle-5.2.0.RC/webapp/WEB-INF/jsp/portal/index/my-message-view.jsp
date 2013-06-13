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
		function sumbitMessages(formId,url){
			$("#"+formId).attr("action",url);
			$("#"+formId).ajaxSubmit(function (id){
				if($("#isOpen").attr("value")=="true"){
					alert("回复成功！");
				}else{
					parent.getMeassagesTimeout();
					parent.$.colorbox.close();
				}
				
			});
		}
	</script>
</head>
<body>
<div class="opt-body">
	<div id="opt-content">
		<form action="" name="defaultForm" id="defaultForm" method="post">
			<table class="form-table-without-border">
				<tr>
					<td>
						<input type="hidden" id="isOpen" value="${isOpen}"/>
						<input type="hidden" name="userNames" id="userNames" value="${sender}"/>
						<input type="hidden" name="loginNames" id="loginNames" value="${senderLoginName}"/>
						<input type="hidden" name="messageType" id="messageType" value="${messageType}"/>
						消&nbsp;&nbsp;息：<textarea readonly="readonly" style="height: 350px;">${content}</textarea>
					</td>
				</tr>
				<s:if test="messageType.code=='online.message'">
					<tr>
						<td>回&nbsp;&nbsp;复：<textarea name="content" onkeyup="if(this.value.length>500)this.value=this.value.substring(0,500);" id="messages" style="height: 150px;"></textarea></td>
					</tr>
				</s:if>
			</table>	
		</form>
		<s:if test="messageType.code=='online.message'">
			<div>
				<a class="btn" href="#" onclick="sumbitMessages('defaultForm','${portalCtx}/index/my-message-save.htm');"><span><span >提交</span></span></a>
			</div>
		</s:if>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>