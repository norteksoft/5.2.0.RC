<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>导入管理</title>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
</head>
<body>
<div class="ui-layout-center">
<aa:zone name="import_main">
	<aa:zone name="btnZone">
		<div class="opt-btn">
			<button class="btn" onclick="saveColumns();"><span><span >保存</span></span></button>
			<button class="btn" onclick='setPageState();ajaxSubmit("defaultForm","${settingCtx}/options/import-definition.htm","import_main");'><span><span >返回</span></span></button>
		</div>
	</aa:zone>
	<aa:zone name="importContext">
		<div id="msg"></div>
		<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
			<form action="" id="inputForm" name="inputForm" method="post">
				<input type="hidden" id="importDefinitionId" name="importDefinitionId" value="${importDefinitionId }"/>
				<view:formGrid gridId="importColumnId" code="BS_IMPORT_COLUMN" entity="${importDefinition}" attributeName="importColumns"></view:formGrid>
			</form>
	</aa:zone>
</aa:zone>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>