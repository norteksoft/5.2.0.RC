<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
   
	<title><s:text name="decorators.logManager"/></title>
	
	<%@ include file="/common/acs-meta.jsp"%>
	
	<link href="${resourcesCtx}/widgets/colorbox/colorbox.css" rel="stylesheet" type="text/css"/>
	<script src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js" type="text/javascript"></script>
	
	<script type="text/javascript" src="${resourcesCtx}/widgets/jstree/jquery.jstree.js"></script>
      
	<script type="text/javascript">
	$(function () {
		initSystemTree();
	});
	</script>
</head>
<body  onclick="$('#sysTableDiv').hide();$('#styleList').hide();">
	<script type="text/javascript">
		var secMenu="_log";
		var thirdMenu="_system_log";
	</script>
	
	<%@ include file="/menus/header.jsp"%>
	<%@ include file="/menus/acs-sec-nav.jsp"%>
	
	<div class="ui-layout-west">
		<%@ include file="/menus/acs-log-menu.jsp"%>
	</div>
	<div class="ui-layout-center">
		<input id="firstLoading" type="hidden" value="0">
		
		<security:authorize ifAnyGranted="log_data">
			<iframe id="myIFrame" name="myIFrame" src="${acsCtx}/log/log-data.action" frameborder="0" allowtransparency="no" style="height:99.3%" ></iframe>
		</security:authorize>
		<security:authorize ifNotGranted="log_data" >
			<iframe id="myIFrame" name="myIFrame" src="${acsCtx}/log/log!lookUserLoginLog.action" frameborder="0" allowtransparency="no" style="height:99.3%" ></iframe>
		</security:authorize>
		
	</div>	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
</html>