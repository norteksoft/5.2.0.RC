<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-meta.jsp"%>
	<title>用户上下级管理</title>
</head>

<body onclick="$('#sysTableDiv').hide();$('#styleList').hide();">
	<script type="text/javascript">
		var secMenu="_work";
		var thirdMenu="_tongzhi";
		var treeMenu = "unread_notice";
	</script>
	<%@ include file="/menus/header.jsp"%>
	<%@ include file="/menus/setting-sec-menu.jsp"%>
	<div class="ui-layout-west">
		<%@ include file="/menus/setting-basic-menu.jsp"%>
	</div>
	<div class="ui-layout-center">
		<iframe id="myIFrame" name="myIFrame" src="${settingCtx}/rank/list-data.htm" style="height:100%;" frameborder="0"></iframe>
	</div>
</body>

</html>