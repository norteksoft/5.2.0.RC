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
		function selectPerson(){
			var acsSystemUrl = "${portalCtx}";
			popTree({ title :'选择人员',
				innerWidth:'300',
				treeType:'MAN_DEPARTMENT_TREE',
				defaultTreeValue:'loginName',
				leafPage:'false',
				multiple:'true',
				hiddenInputId:"loginNames",
				showInputId:"userNames",
				acsSystemUrl:imatrixRoot,
				onlineVisible:true,
				callBack:function(){customCallbackFun();}});
		}

		function customCallbackFun(){
			if($("#loginNames").val()!='ALLCOMPANYID'){
			$("#loginNames").attr("value",jstree.getLoginNames());
			}
		}

		function sumbitMessages(formId,url){
			$("#"+formId).attr("action",url);
			$("#"+formId).ajaxSubmit(function (id){
				if(id.split("-")[1]=='ONLINE_MESSAGE'){
					parent.ajaxSubmit("defaultForm","${portalCtx}/index/my-message.htm?messageType=ONLINE_MESSAGE", "messageList");
				}else{
					parent.ajaxSubmit("defaultForm","${portalCtx}/index/my-message.htm?messageType=SYSTEM_MESSAGE", "messageList");
				}
				parent.$.colorbox.close();
			});
		}
	</script>
</head>
<body>
<div class="opt-body">
	<div class="opt-btn">
		<a class="btn" href="#" onclick="sumbitMessages('defaultForm','${portalCtx}/index/my-message-save.htm');"><span><span >提交</span></span></a>
	</div>
	<div id="opt-content">
		<form action="" name="defaultForm" id="defaultForm" method="post">
			<input id="messageType" name="messageType" type="hidden" value="${messageType}">
			<table class="form-table-without-border">
				<tr>
					<td>接收人:<input readonly="readonly" name="userNames" id="userNames" value="${userNames}" />
								<input type="hidden" name="loginNames" id="loginNames" value="${loginNames}"/>
								<a href="#" onclick="selectPerson();" class="small-btn"><span><span>选择</span></span></a>
					</td>
				</tr>
				<tr>
					<td>消&nbsp;&nbsp;息：<textarea name="content" id="content" style="height: 350px;" onkeyup="if(this.value.length>500)this.value=this.value.substring(0,500);">${content}</textarea></td>
				</tr>
			</table>	
		</form>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>