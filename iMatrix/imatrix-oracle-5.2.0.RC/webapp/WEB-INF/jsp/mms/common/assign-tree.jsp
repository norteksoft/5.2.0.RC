<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<html>
<head>
	<title>委托管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	
	<script type="text/javascript" src="${mmsCtx}/widgets/tree/_lib/css.js"></script>
	<link rel="stylesheet" type="text/css" href="${mmsCtx}/widgets/tree/tree_component.css" />
	<link href="${mmsCtx}/css/task.css" rel="stylesheet" type="text/css"/>
	<link href="${mmsCtx}/css/style.css" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="${mmsCtx}/widgets/tree/tree_component.js"></script>
	<style type="text/css">
		a{text-decoration:none;}
	</style>
	<script type="text/javascript">
	function selectMan(){
		var value=getInfo();
		if(value!=""){
			var type=value.substring(0,value.indexOf("_"));
			var loginName =value.substring( value.indexOf("-")+1,value.length);
			if(type == "user"){
				$("#wf_transactor").attr("value", loginName);
			}
		}
	}
	//指派
	function assignTo(){
		selectMan();
		var tor = $("#wf_transactor").attr("value");
		if(typeof tor == "undefined" || tor == ""){
			myShowMsg("请选择用户");
		}else{
			$.post(webRoot + "/common/assignto.htm", 
					"taskId=" + window.parent.getTaskId() + "&transactor=" + tor, 
					postSuccess);
		}
	}

	function myShowMsg(msg){
		$("#backMsg").html(msg);
		$("#backMsg").show();
		setTimeout('$("#backMsg").hide("show");',3000);
	}

	function postSuccess(msg){
		myShowMsg("任务已成功指派");
		$("#wf_ok").hide();
		$('#taskbutton',window.parent.document).hide();
		window.parent.tb_remove();
		window.parent.taskList();
		//$("#wf_cancel").html("返回");
		//$("#wf_cancel").attr("href", "${mmsCtx}/engine/task.htm");
	}
	</script>
</head>
<body>
<div class="ui-layout-center">
	<div class="openWindowDivTree">
	 	<p class="buttonP">
			<a id="wf_ok" href="#" onclick="assignTo()" class="btnStyle ">  确定</a>
		</p>
		<input id="wf_transactor" type="hidden" name="transactor">
		<div id="backMsg" style="margin: 0px 0 5px 16px; color: red;"></div>
		<acsTags:tree defaultable="true" treeType="MAN_DEPARTMENT_TREE" treeId="user_tree" multiple="false"></acsTags:tree>
		<!--<div id="user_tree" class="demo"></div>
	--></div>
</div>
</body>
</html>