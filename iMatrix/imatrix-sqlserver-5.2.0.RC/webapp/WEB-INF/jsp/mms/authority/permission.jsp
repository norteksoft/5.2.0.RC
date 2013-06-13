<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>数据权限</title>
	<%@ include file="/common/mms-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx }/widgets/jstree/jquery.jstree.js"></script>
		<script src="${mmsCtx}/js/authority.js" type="text/javascript"></script>
		<script type="text/javascript">
		$(document).ready(function() {
			ruleTypeDataTree('permission-manager_content','/authority/rule-type-data-tree.htm');
		});
		</script>
</head>
<body onclick="$('#sysTableDiv').hide();$('#styleList').hide();">
	<script type="text/javascript">
		var secondMenu = "mms-authority";
		var thirdMenu = "permission-manager";
  	</script>
  	<%@ include file="/menus/header.jsp"%>
	<%@ include file="/menus/mms-sec-menu.jsp"%>
	<div class="ui-layout-west">
		<%@ include file="/menus/mms-authority-thd-menu.jsp"%>
	</div>
	<div class="ui-layout-center">
		<iframe id="myIFrame" name="myIFrame" src="${mmsCtx}/authority/permission-data.htm"  style="height:100%;" frameborder="0" allowtransparency="no" ></iframe>
	</div>
</body>
</html>