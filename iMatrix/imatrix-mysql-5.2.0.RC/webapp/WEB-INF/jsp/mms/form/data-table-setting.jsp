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
			<button class="btn" onclick="saveSetting();"><span><span >保存</span></span></button>
			<button class="btn" onclick="setPageState();returnTableList();"><span><span >返回</span></span></button>
		</div>
	</aa:zone>
	<aa:zone name="contentZone">
		<form id="contentFrom" name="contentFrom" method="post">
			<input type="hidden" name="states" value="${states}"/>
			<input type="hidden" id="men_id"  name="menuId" value="${menuId }"/>
		</form>
		<aa:zone name="columnList">
		<div id="msg"></div>
		<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
			<form action="" id="inputForm" name="inputForm" method="post">
				<input type="hidden" name="settingId" id="settingId" value="${generateSetting.id}"/>
				<input type="hidden" name="tableId" id="tabelId" value="${generateSetting.tableId}"/>
				<table class="form-table-without-border">
					<tr>
						<td class="content-title" style="width: 90px;">是否生成实体：</td>
						<td> <s:select theme="simple" list="#{'true':'是','false':'否'}" name="generateSetting.entitative"></s:select> </td>
						<td></td>
					</tr>	
					<tr>
						<td class="content-title">是否走流程：</td>
						<td> <s:select id="flowable" theme="simple" list="#{'true':'是','false':'否'}" name="generateSetting.flowable"></s:select>  </td>
						<td></td>
					</tr>
					<tr>
						<td class="content-title">流程编号：</td>
						<td> <input id="workflowCode" name="generateSetting.workflowCode" value="${generateSetting.workflowCode}"/>  </td>
						<td></td>
					</tr>
				</table>
			</form>
		</aa:zone>
	</aa:zone>
</div>
</body>
</html>