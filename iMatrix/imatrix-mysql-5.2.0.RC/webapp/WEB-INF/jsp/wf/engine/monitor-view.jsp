<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/task-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	
	<title>${title }</title>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<meta http-equiv="Cache-Control" content="no-store"/>
	<meta http-equiv="Pragma" content="no-cache"/>
	<meta http-equiv="Expires" content="0"/>
	<script language="javascript" type="text/javascript">
		var webRoot="${wfCtx}";
	</script>
	
	<%@include file="/common/wf-iframe-meta.jsp"%>	<script type="text/javascript">
		function initIframe(iframeId){
			$("#"+iframeId).height($(window).height()-5);
		}
	</script>
	<style>
	</style>
</head>
<body>
<div class="ui-layout-center">
<iframe id="contentFrame"  onload="initIframe('contentFrame')" src="${url}" frameborder="0" allowTransparency="true" style="height:100%;width: 99.9%">
</iframe>
</div>
</body>
</html>
