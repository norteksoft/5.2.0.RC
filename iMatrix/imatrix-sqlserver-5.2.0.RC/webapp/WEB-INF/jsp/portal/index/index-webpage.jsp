<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/common/portal-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
	<title>添加页签</title>
	<%@ include file="/common/portal-meta.jsp"%>
	<style type="text/css">
		table.Table,table.views{ 
			width: 100%; 
			background-color:#FFFFFF;
			border:1px solid #DEDEDE;
			border-collapse:collapse;}
		table.Table tbody tr td, table.Table thead tr th,
		table.views tbody tr td, table.views tbody tr th{
			border:1px solid #DEDEDE;
		}
		table.Table thead tr th,table.views tbody tr th{ background-color: #ADD5EF; }
		table.Table tbody tr td,table.views tbody tr td{ padding: 4px 6px;}
		table.Table tbody tr.even,table.views tbody tr.even{ background-color: #EFF8FF; }
		table.Table tbody tr.odd,table.views tbody tr.odd{ }
		table.Table tbody tr:HOVER,table.views tbody tr:HOVER{ background-color: #60b0e3; }
	</style>
	<script type="text/javascript">
	isUsingComonLayout=false;
		$(document).ready(function () {
			$("#pageForm").validate({
				submitHandler: function() {
					doAddWebpage();
				},
				rules: {
					webpageName: "required"
				},
				messages: {
					webpageName: "必填"
				}
			});
		});
		function doAddWebpage(){
			if($.trim($('#webpageName').attr('value'))==''){
				_showMessage('msg','请输入页签名称');
				return;
			}
			$.ajax({
				type: "POST",
				url: "${portalCtx}/index/index!savewebpage.htm",
				async: false,
				cache: false,
				data: getData(),
				success:function(data, textStatus){
					window.parent.location="${portalCtx}/index/index.htm?webpageId="+data;
				},
				error:function(){
					_showMessage('msg','添加页签错误!');
				}
			}); 
		}
		function getData(){
			var data = {};
			data['webpageName']=$('#webpageName').attr('value');
			data['webpageId']=$('#webpageId').attr('value');
			data['columnSize']=$("input[name='columnSize']:checked").attr('value');
			return data;
		}
		function _showMessage(id, msg){
			if(msg != ""){
				$("#"+id).html(msg);
			}
			$("#"+id).show("show");
			setTimeout('$("#'+id+'").hide("show");',3000);
		}
		function submitTag(){
			$("#pageForm").submit();
		}
	</script>
</head>

<body style="padding: 6px;">

	<div class="opt_btn" style="margin-bottom: 5px;">
		<button id='savebtn' class='btn' onclick="submitTag();"><span><span>保存</span></span></button>
	</div>
	<div id='msg' style="color: red;"></div>
	
	<form action="${portalCtx}/index/index!savewebpage.htm" method="post" name="pageForm" id="pageForm">
	<p>页签名称：<input size="60" maxlength="10" style="width:300px" name="webpageName" id="webpageName" value="${webpage.name}"/><span class="required">*</span></p>
		<table>
			<tr>
				<td> <img src="../../images/view_3.png"/> </td>
				<td> <img src="../../images/view_2.png"/> </td>
				<td> <img src="../../images/view_1.png"/> </td>
			</tr>
			<tr>
				<td> <input type="radio" <s:if test="webpageId==null||webpage.columns==3">checked="checked"</s:if> name="columnSize" value="3"/>三栏 </td>
				<td> <input type="radio" <s:if test="webpageId!=null&&webpage.columns==2">checked="checked"</s:if> name="columnSize" value="2"/>两栏 </td>
				<td> <input type="radio" <s:if test="webpageId!=null&&webpage.columns==1">checked="checked"</s:if> name="columnSize" value="1"/>一栏 </td>
			</tr>
		</table>
		<input id="webpageId" type="hidden" name="id" value="<s:if test="webpageId!=null">${webpage.id}</s:if>"/>
	</form>

</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>