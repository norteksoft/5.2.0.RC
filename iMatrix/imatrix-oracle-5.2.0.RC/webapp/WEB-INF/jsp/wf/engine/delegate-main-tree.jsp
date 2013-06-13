<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>委托管理</title>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
	
	<script type="text/javascript" src="${resourcesCtx}/widgets/tree/_lib/css.js"></script>
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/tree/tree_component.css" />
	<script type="text/javascript" src="${resourcesCtx}/widgets/tree/tree_component.js"></script>
	<link href="${wfCtx}/css/style.css" rel="stylesheet" type="text/css"/>
	
	<script type="text/javascript">
	

	function selectMan(id){
		var info=getInfo("user");
		if(info!=""){
			var user=eval(info);
			var type=user[0].type;
			if(type == "user"){
				window.parent.insertInputValue(user[0].loginName,user[0].name);
				window.parent.$("#selectBtn").colorbox.close();
			}else{
				alert("请选择人员");
			}
		}else{
			alert("请选择人员");
		}
	}
	</script>
</head>
<body onload="getContentHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
			<div class="opt-btn">
				<button id="wf_ok" class="btn" onclick="selectMan();"><span><span >确定</span></span></button>
			</div>
			<div id="opt-content">
				<div id="treeContect" style="text-align: left;overflow: autow;height: 500px">
					<acsTags:tree defaultable="true" treeType="MAN_DEPARTMENT_TREE" treeId="manTree" multiple="false"></acsTags:tree>
				</div>
			</div>
		</div>
	</div>
</body>
</html>