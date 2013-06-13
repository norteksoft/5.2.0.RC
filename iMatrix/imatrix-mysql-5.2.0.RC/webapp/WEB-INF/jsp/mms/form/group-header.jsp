<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>表单管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	
</head>
<body>
<div class="ui-layout-center">
<aa:zone name="viewList">
	<aa:zone name="btnZone">
				<div class="opt-btn">
					<button class="btn" onclick="saveGroupHeader();"><span><span>保存</span></span></button>
					<button class="btn" onclick='setPageState();listViewBack("columnSaveForm","${mmsCtx }/form/list-view.htm");'><span><span >返回</span></span></button>
				</div>
			</aa:zone>
			
	<aa:zone name="viewZone">
		<aa:zone name="columnTable">
		<input id="frozenColumnAmount" value="${frozenColumnAmount }" type="hidden"/>
			<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
			<form id="columnSaveForm" name="columnSaveForm" action="" method="post">
			<input name="viewId" value="${viewId }" type="hidden"></input>
			<input name="dataTableId" value="${dataTableId }" type="hidden"></input>
			<input id="menuId" name="menuId" value="${menuId}" type="hidden"></input>
			<view:formGrid gridId="groupHeaderGridId" code="MMS_GROUP_HEADER" collection="${groupHeaders}" entity="${listView}"></view:formGrid>
			</form>
		</aa:zone>
	</aa:zone>
</aa:zone>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
