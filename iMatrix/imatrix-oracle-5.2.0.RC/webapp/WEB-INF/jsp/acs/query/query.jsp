<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title>综合查询</title>
	<script  type="text/javascript" src="${imatrixCtx}/widgets/calendar/WdatePicker.js"></script>
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/validation/cmxform.css"/>
</head>
<body>
<div class="ui-layout-center">
	<form name="jmesaForm" id="jmesaForm" method="post" action=""></form >
	<div class="opt-body">
		<aa:zone name="myprocess">
			<div class="opt-btn">
				<security:authorize ifAnyGranted="query_userOnline">
				    <button  class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span><s:text name="common.search"/></span></span></button>
				</security:authorize>
			</div>
			<div id="opt-content" >
			    <form id="searchForm" name="searchForm" action="" method="post">
				<view:jqGrid url="${acsCtx}/query/query.action" pageName="page" code="ACS_LOGIN_USER_LOG" gridId="main_table"></view:jqGrid>
				</form>
			</div>
		</aa:zone>
	</div>
</div>    	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
