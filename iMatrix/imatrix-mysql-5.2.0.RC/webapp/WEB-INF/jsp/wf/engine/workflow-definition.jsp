<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
<%@ include file="/common/wf-meta.jsp"%>
<title>工作流管理</title>
<script type="text/javascript">
$(document).ready(function () {
	$.ajaxSetup({cache:false});
	treeDefinition();
});
function hideWestAndNorth(){
	$(".ui-layout-resizer-west .ui-layout-toggler-open").click();
	$("#header-resizer .hid-header").click();
	$("#header-resizer .hid-header").attr('class', 'show-header');
	//$("#header-resizer").css('display', 'none');
	//$("#header-resizer .ui-layout-resizer-west").css('display', 'none');
}
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
		var firstMenu = "oa-main_product";
		var secondMenu = "oa-wfm";
		var thirdMenu ="process_definition";
	</script>
		
	<%@ include file="/menus/header.jsp"%>
	<%@ include file="/menus/wf-sec-menu.jsp"%>
	<div class="ui-layout-west">
		<%@ include file="/menus/wf-thd-mng.jsp"%>
	</div>
	
	<div class="ui-layout-center">
		<iframe id="myIFrame" scrolling="no" name="myIFrame" src=""  frameborder="0" allowtransparency="no" ></iframe>
	</div>
</body>
</html>