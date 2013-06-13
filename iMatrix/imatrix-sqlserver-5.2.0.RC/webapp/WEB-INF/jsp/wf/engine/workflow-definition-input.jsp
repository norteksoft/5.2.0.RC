<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<title>流程管理</title>
</head>
<body >
<div class="ui-layout-center">
<aa:zone name="wfd_main">
	<form id="wfdForm" name="wfdForm">
		<input type="hidden" name="type" value="${type }"/>
		<input id="system_id" name="sysId" type="hidden" value="${sysId }"/>
		<input id="vertion_type" name="vertionType" type="hidden" value="${vertionType }"/>
	</form>
		<input id="companyId" name="defCompanyId" type="hidden" value="${defCompanyId}"/>
		<input id="creator" name="defCreator" type="hidden" value="${defCreator}"/>
		<input id="type" name="type" type="hidden" value="${type}"/>
		<input id="templateId" name="templateId" type="hidden" value="${templateId }"/>
		<input id="systemId" name="defSystemId" type="hidden" value="${defSystemId }"/>
		<input type="hidden" name="localeLang" id="localeLang" value="<%= request.getLocale().getLanguage() %>" />
		<div id="flashcontent" style="width:100%;">
		</div>	
</aa:zone>
</div>
</body>
</html>
