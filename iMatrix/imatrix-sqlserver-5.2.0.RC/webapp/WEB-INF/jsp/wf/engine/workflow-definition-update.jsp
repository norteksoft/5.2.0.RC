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
	<s:if test="option=='view'">
		<div class="opt-btn">
			<button  class='btn' onclick="setPageState();goBackWfd('wfdForm','${wfCtx}/engine/workflow-definition-data.htm','wfd_main','wfdPage');" hidefocus="true"><span><span>返回</span></span></button>
		</div>
	</s:if>
	<form id="wfdForm" name="wfdForm">
		<input type="hidden" name="type" value="${type }"/>
		<input id="system_id" name="sysId" type="hidden" value="${sysId }"/>
		<input id="vertion_type" name="vertionType" type="hidden" value="${vertionType }"/>
	</form>
	<input type="hidden" name="localeLang" id="localeLang" value="<%= request.getLocale().getLanguage() %>" />
	<input id="companyId" name="companyId" type="hidden" value="${companyId}"/>
	<input id="creator" name="creator" type="hidden" value="${creator}"/>
	<input id="type" name="type" type="hidden" value="${type}"/>
	<input type="hidden" name="wfdId" id="wfdId" value="${wfdId }"/>
	<input id="systemId" name="systemId" type="hidden" value="${systemId }"/>
	<div id="flashcontent" style="width:100%;">
	</div>	
</aa:zone>
</div>
</body>
</html>
