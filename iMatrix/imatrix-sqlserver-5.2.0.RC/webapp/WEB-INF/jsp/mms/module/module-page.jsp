<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>页面管理</title>
	<%@ include file="/common/mms-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx }/widgets/jstree/jquery.jstree.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			createViewTree("page_manage_content");
		});
	</script>
</head>
<body onclick="$('#sysTableDiv').hide();$('#styleList').hide();">
	<script type="text/javascript">
  		var secondMenu = "mms-page";
  		var thirdMenu = "page_manage";
  	</script>
  	<%@ include file="/menus/header.jsp"%>
	<%@ include file="/menus/mms-sec-menu.jsp"%>
	<div class="ui-layout-west">
		<%@ include file="/menus/mms-page-thd-menu.jsp"%>
	</div>
	<div class="ui-layout-center">
		<iframe id="myIFrame" name="myIFrame" src="${mmsCtx}/module/module-page-list.htm" style=" height:100%;" frameborder="0" allowtransparency="no"></iframe>
	</div>
</body>
</html>