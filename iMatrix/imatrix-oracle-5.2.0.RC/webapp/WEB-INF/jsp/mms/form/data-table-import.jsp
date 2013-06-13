<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/mms-iframe-meta.jsp"%>
<link type="text/css" href="${mmsCtx}/css/style.css" rel="stylesheet" /> 
<script type="text/javascript">
$().ready(function(){
	if($($('.actionMessage').children('li')).html() != null){
		window.parent.$.colorbox.close();
		window.parent.$("#contentFrom").append(window.parent.createHiddenInput('tableId', $("#tableId").attr("value")));
		window.parent.ajaxSubmit("contentFrom",webRoot+"/form/data-table-dealWithTableColumn.htm","contentZone");
	}
});
function submitbutt(){
	if($("#filename").val()==""){
		alert("请选导入的文件！");
	}else{
		$('#importForm').submit();
	}
}
</script>
	<style type="text/css">
		.form-table-without-border td input{
			width:300px;
		}
	</style>
</head>
<body>
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<button class="btn" onclick="submitbutt();"><span><span>确定</span></span></button>
	</div>
	<div id="opt-content">
		<form id="importForm" name="importForm" action="${mmsCtx}/form/data-table!importInto.htm" method="post" enctype="multipart/form-data">
			<input name="tableId" value="${tableId }" id="tableId" type="hidden"/>
			<table class="form-table-without-border" >
				<tr>
					<td><s:actionmessage />
					 <input type="file" id="filename" name="file" /></td>
				</tr>
			</table>
			 
		</form>
	
	</div>
</div>
</div>
</body>
</html>