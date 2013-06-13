<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>表单管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	<script src="${imatrixCtx}/widgets/formeditor/kindeditor.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/lang/zh_CN.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/formeditor.js" type="text/javascript"></script>
	<link href="${imatrixCtx}/widgets/formeditor/formeditor.css" rel="stylesheet" type="text/css" />
	
	<script type="text/javascript">
	function choiceControl(){
		if($("#tableColumnId").get(0).selectedIndex==0){
			$("#name").attr("value","");
		}
		ajaxAnyWhereSubmit("textForm", "", "controlContent");
	}
	function generateHtml(){
		if($("#tableColumnId").get(0).selectedIndex==0){
			alert("请选择对应字段");
			return;
		}else{
			parent.standardListControlHtml($("#controlId").attr("value"),
					$("#cname").attr("value"),
					$("#title").attr("value"),
					$("#dataType").attr("value"),
					$("#listViewCode").attr("value"),
					$("#tableColumnId").attr("value"),
					$("#dbName").attr("value"));
		}
	}

	function ok(){
		if("${standard}"=="true"){
			generateHtml();
		}else{
			parent.$.colorbox.close();
		}
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
		<button class="btn" onclick="ok();"><span><span>确定</span></span></button>
		<button class="btn" onclick='parent.$.colorbox.close();'><span><span >取消</span></span></button>
	</div>
	<div id="opt-content">
		<aa:zone name="controlContent">
				<s:if test="standard">
				<form name="textForm" id="textForm" action="${mmsCtx }/form/form-view!text.htm">
					<s:hidden name="id"></s:hidden>
					<s:hidden id="code" name="code"></s:hidden>
					<s:hidden id="version" name="version"></s:hidden>
					<s:hidden id="standard" name="standard"></s:hidden>
					<s:hidden id="occasion" name="occasion" value="changeSource"></s:hidden>
					
					<fieldset style="border: #f0f0ee solid 1px; padding: 3px;">
						<legend style="margin: 0 10px; color: #0046d5;">&nbsp;字段描述&nbsp;</legend>
						<table class="form-table-without-border">
						<tbody>
							<tr>
								<td class="content-title">控件类型</td>
								<td>
									<s:property value="formControl.controlType.code"/>
									<s:hidden theme="simple" id="controlType" name="formControl.controlType" ></s:hidden>
								</td>
								<td>
								</td>	
							</tr>	
							<tr>
								<td class="content-title">对应字段：</td>
								<td>
									<s:hidden id="dataType" name="tableColumn.dataType.enumName"></s:hidden>
									<s:select onchange="choiceControl();" id="tableColumnId" name="tableColumnId" list="columns" theme="simple" listKey="id" listValue="alias" headerKey="0" headerValue="请选择"></s:select>
								</td>
								<td></td>	
							</tr>
							<tr>
								<td class="content-title">字段名：</td>
								<td>
									<s:if test="tableColumnId==null||tableColumnId==0">
										<s:textfield theme="simple" id="cname" name="formControl.name"  cssClass="{required:true,messages: {required:'必填'}}"></s:textfield>
									</s:if>
									<s:else>
										<s:textfield theme="simple" id="cname" name="formControl.name" readonly="true"  cssClass="{required:true,messages: {required:'必填'}}"></s:textfield>
									</s:else>
									<s:hidden  theme="simple" name="formControl.dbName" id="dbName"/>
								</td>
								<td>
									<span id="cnameTip"></span>
								</td>	
							</tr>
							<tr>
								<td class="content-title">字段别名：</td>
								<td>
									<s:textfield theme="simple" id="title" name="formControl.title"   cssClass="{required:true,messages: {required:'必填'}}"></s:textfield>
								</td>
								<td><span id="titleTip"></span></td>	
							</tr>
							<tr>
								<td class="content-title">控件id：</td>
								<td>
									<s:textfield theme="simple" id="controlId" name="formControl.controlId"   cssClass="{required:true,messages: {required:'必填'}}"></s:textfield>
								</td>
								<td><span id="controlIdTip"></span></td>	
							</tr>	
							<tr>
								<td class="content-title">对应列表：</td>
								<td>
									<s:select id="listViewCode" name="listViewCode" list="listViews" theme="simple" listKey="code" listValue="name" headerKey="0" headerValue="请选择"></s:select>
								</td>
								<td></td>	
							</tr>
						</tbody>
					</table>
					</fieldset>
				</form>
				<script type="text/javascript">
				function validateText(){
					$("#textForm").validate({
						submitHandler: function() {
							ok();
						}
					});
				}
				validateText();
				</script>
			</s:if>
			<s:else>
				不能创建<s:property value="formControl.controlType.i18nKey"/>控件
			</s:else>
		</aa:zone>
	</div>
</div>
</div>	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>

</html>
