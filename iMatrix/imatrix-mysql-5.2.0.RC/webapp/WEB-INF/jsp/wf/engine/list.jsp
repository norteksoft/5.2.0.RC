<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/wf-meta.jsp"%>
	<title>工作流平台</title>
</head>
<body onclick="$('#sysTableDiv').hide();$('#styleList').hide();">
	<script type="text/javascript">
		var firstMenu = "oa-main_product";
		var secondMenu = "oa-wf";
		var thirdMenu ="my_init_process";
	</script>	
	
	<%@ include file="/menus/header.jsp"%>
	<%@ include file="/menus/wf-sec-menu.jsp"%>
	<div class="ui-layout-west">
		<%@ include file="/menus/wf-thd-platform.jsp"%>
	</div>
	<div class="ui-layout-center">
		<iframe id="myIFrame" name="myIFrame" src="${wfCtx}/engine/delegate-main!myDelegate.htm" style="height:100%;" frameborder="0" allowtransparency="no" ></iframe>
	</div>
</body>
</html>
