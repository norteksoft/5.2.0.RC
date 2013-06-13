<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>表单管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	
	<script src="${imatrixCtx}/widgets/formeditor/kindeditor.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/lang/zh_CN.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/formeditor.js" type="text/javascript"></script>
	<link href="${imatrixCtx}/widgets/formeditor/formeditor.css" rel="stylesheet" type="text/css" />
	
	<script src="${mmsCtx}/js/form-view.js" type="text/javascript" charset="UTF-8"></script>
	
</head>
<body>
<div class="ui-layout-center">
	<aa:zone name="controlContent">
		<div style="margin: 10px;text-align: left;">
			<p class="buttonP">
				<a class="btnStyle" href="#" onclick="choiceControl();">确定</a>
				<a class="btnStyle" href="#" onclick="parent.$.colorbox.close()">取消</a>
			</p>
			<form id="controlForm" name="controlForm" action="${mmsCtx }/form/form-view!text.htm">
				<input type="hidden"  name="controlType" value="TEXT"></input>
				数据来源：
				<s:select name="tableColumnId" list="columns" theme="simple" listKey="id" listValue="alias" headerKey="0" headerValue="请选择"></s:select>
			</form>
		</div>	
	</aa:zone>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
