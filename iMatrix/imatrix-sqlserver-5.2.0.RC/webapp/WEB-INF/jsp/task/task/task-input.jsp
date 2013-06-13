<%@ page contentType="text/html;charset=UTF-8" %>
<%@page import="com.norteksoft.product.util.WebContextUtils"%>
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
		var webRoot="${taskCtx}";
	</script>
	<script type="text/javascript" src="${resourcesCtx}/js/jquery-all-1.0.js"></script>
	<link   type="text/css" href="${resourcesCtx}/css/<%=WebContextUtils.getTheme()%>/jquery-ui-1.8.16.custom.css" rel="stylesheet" id="_style"/>
	
	<script type="text/javascript">
		function initIframe(iframeId){
			$("#"+iframeId).height($(window).height()-5);
		}
		function getContentHeight(){
			var h = $('.ui-layout-center').height();
			if($(".opt-btn").height()>0){
				$("#opt-content").css("height",h-55+14); //14px是opt-content的外边距
			}else{
				$("#opt-content").css("height",h-20+14); 
			}		
		}
	</script>
	<style>
		body {
			color:#444444;
			font-family:宋体,Arial,Helvetica,sans-serif;
			font-size:14px;
			margin:0;
			padding:0;
		}
	</style>
</head>
<body onload="getContentHeight();" style="overflow: hidden;">
<div class="ui-layout-center">
	<div class="opt-body">
		<div id="opt-content" style="padding-left: 0px;padding-top: 0px;padding-right: 0px;">
			<iframe id="contentFrame"  onload="initIframe('contentFrame')" src="${url}" frameborder="0" allowTransparency="true" style="height:100%;width: 99.9%">
			</iframe>
		</div>
	</div>
</div>
<script src="${resourcesCtx}/js/jquery.timers-1.2.js" type="text/javascript"></script>
	<script src="${taskCtx}/js/task.js" type="text/javascript"></script>
</body>
</html>
