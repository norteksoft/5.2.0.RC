<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>表单管理</title>
</head>
<body>
<div class="ui-layout-center">
	<aa:zone name="btnZone">
		<div class="opt-btn">
			<button class="btn" onclick="saveColumns();"><span><span >保存</span></span></button>
			<button class="btn" onclick="setPageState();returnTableList();"><span><span >返回</span></span></button>
		</div>
	</aa:zone>
	<aa:zone name="contentZone">
		<form id="contentFrom" name="contentFrom" method="post">
			<input type="hidden" name="states" value="${states}"/>
			<input type="hidden" id="men_id"  name="menuId" value="${menuId }"/>
		</form>
		<input type="hidden" id="_canChange" value="${canChange}"/>
		<input type="hidden" id="tabelId" value="${tableId}"/>
		<aa:zone name="columnList">
		<div id="msg"></div>
		<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
			<form action="" id="columnForm" name="columnForm" method="post">
				<view:formGrid gridId="tableColumnId" code="MMS_TABLE_COLUMN" collection="${columns}"></view:formGrid>
			</form>
		</aa:zone>
	</aa:zone>
</div>
</body>
</html>