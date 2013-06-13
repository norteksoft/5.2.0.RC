<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/task-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>选择办理人</title>
	<%@ include file="/common/task-meta.jsp"%>
	
	<script type="text/javascript" src="${taskCtx }/widgets/jstree/jquery.jstree.js"></script>
	<script type="text/javascript">
	function selectMan(){
		var info=getInfo("user");
		if(info!=""){
			var user=eval(info);
			var type=user[0].type;
			if(type == "user"){
				$("#wf_transactor").attr("value", user[0].loginName);
			}else{
				alert("请选择人员");
			}
		}else{
			alert("请选择人员");
		}
	}
	//指派
	function assignTo(){
		selectMan();
		var tor = $("#wf_transactor").attr("value");
		if(typeof tor == "undefined" || tor == ""){
			showMsg("请选择用户");
		}else{
			$.post(webRoot + "/task/task!assignTo.htm", 
					"id=" + window.parent.getTaskId() + "&transactor=" + tor, 
					postSuccess);
		}
	}


	function postSuccess(msg){
		$("#wf_ok").hide();
		$('#taskbutton',window.parent.document).hide();
		window.parent.$.colorbox.close();
		window.parent.taskListAssgin();
	}
	</script>
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
	<div class="opt-body">
		<div style="padding:5px 0px 0px 5px; ">
			<div class="opt-btn">
				<button class="btn" onclick="assignTo();" id="wf_ok"><span><span>确定</span></span></button>
			</div>
			<div id="opt-content">
				<input id="wf_transactor" type="hidden" name="transactor">
				<acsTags:tree defaultable="true" treeType="MAN_DEPARTMENT_TREE" treeId="user_tree" multiple="false"></acsTags:tree>
			</div>
		</div>
	</div>
</div>
</body>
</html>