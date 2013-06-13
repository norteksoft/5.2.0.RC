<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>表单管理</title>
	<%@ include file="/common/mms-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx }/widgets/jstree/jquery.jstree.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			createViewTree("form_manage_content");
		});

		//隐藏左侧和上侧，只留2级菜单
		function hideWestAndNorth(){
			$(".ui-layout-resizer-west .ui-layout-toggler-open").click();
			$("#header-resizer .hid-header").click();
			$("#header-resizer .hid-header").attr('class', 'show-header');
			//$("#header-resizer").css('display', 'none');
			//$("#header-resizer .ui-layout-resizer-west").css('display', 'none');
		}
		//显示左侧和上侧
		function showWestAndNorth(){
			$(".ui-layout-resizer-west .ui-layout-toggler-closed").click();
			$("#header-resizer .show-header").click();
			$("#header-resizer .show-header").attr('class', 'hid-header');
			//$("#header-resizer").css('display', 'block');
			//$("#header-resizer .ui-layout-resizer-west").css('display', 'block');
		}
		</script>
</head>
<body onclick="$('#sysTableDiv').hide();$('#styleList').hide();">
	<script type="text/javascript">
		var secondMenu = "mms-form";
		var thirdMenu = "form-manager";
  	</script>
  	<%@ include file="/menus/header.jsp"%>
	<%@ include file="/menus/mms-sec-menu.jsp"%>
	<div class="ui-layout-west">
		<%@ include file="/menus/mms-form-thd-menu.jsp"%>
	</div>
	<div class="ui-layout-center">
		<iframe id="myIFrame" name="myIFrame" src="${mmsCtx}/form/list-data.htm"  style="height:100%;" frameborder="0" allowtransparency="no" ></iframe>
	</div>
</body>
</html>
