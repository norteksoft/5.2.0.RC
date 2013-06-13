<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>紧急程度设置控件</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>

	<script src="${imatrixCtx}/widgets/formeditor/kindeditor.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/lang/zh_CN.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/formeditor.js" type="text/javascript"></script>
	<link href="${imatrixCtx}/widgets/formeditor/formeditor.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript">
	function generateHtml(){
		parent.specialTaskTransactorHtml();
	}
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:200px;
	}
	</style>
</head>
  <body onload="getContentHeight();">
  <div class="ui-layout-center">
  <div class="opt-body">
	<div class="opt-btn">
		<button class="btn" onclick="generateHtml();"><span><span>确定</span></span></button>
		<button class="btn" onclick='parent.$.colorbox.close();'><span><span >取消</span></span></button>
	</div>
	<div id="opt-content">
		<aa:zone name="controlContent">
				<form name="textForm" id="textForm" action="${mmsCtx }/form/form-view!text.htm">
					<s:hidden name="id"></s:hidden>
					<s:hidden name="dataTableId"></s:hidden>
					<div style="margin-bottom: 10px;">供用户选择特事特办的人员</div>
					<table class="form-table-without-border">
						<tr>
							<td class="content-title">控件类型：</td>
							<td>
								<input name="controlType" readonly="readonly" style="width: 150px;" value="${formControl.controlType.code }"></input>
								<s:hidden theme="simple" id="controlType" name="formControl.controlType" ></s:hidden>
							</td>
						</tr>	
						<tr>
							<td class="content-title">控件名：</td>
							<td>
								<input id="name" name="name" readonly="readonly" value="special_task_transactor" style="width: 150px;"></input>
							</td>
							<td>固定值，不可修改</td>
						</tr>
						<tr>
							<td class="content-title">控件标题：</td>
							<td>
								<input id="title" name="title" readonly="readonly" value="特事特办人员选择" style="width: 150px;"></input>
							</td>
							<td>固定值，不可修改</td>
						</tr>
				    </table>
				</form>
		</aa:zone>
	</div>
	</div>
  </div>
  </body>
  <script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>