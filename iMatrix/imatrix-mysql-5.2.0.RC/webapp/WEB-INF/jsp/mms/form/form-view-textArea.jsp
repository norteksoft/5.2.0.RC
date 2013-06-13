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
	<script src="${mmsCtx}/js/formControl.js" type="text/javascript"></script>
	
	<script type="text/javascript">
	function choiceControl(){
		if($("#tableColumnId").get(0).selectedIndex==0){
			$("#name").attr("value","");
		}
		ajaxAnyWhereSubmit("textForm", "", "controlContent");
	}
	function generateHtml(){
			var dataType = $("#dataType").val();
			if(dataType!="TEXT"&&dataType!="CLOB"){
				alert("文本域只能建文本或大文本类型的字段");
				return;
			}
			var type = $("#controlType").attr("value");
			var name = $("#name").attr("value");
			var controlId = $("#controlId").attr("value");
			var styleId="";
			if($.trim(styleId)==''){
				styleId="width:354px;height:139px;";
			}else{alert(2);
				styleId=$("#styleId").attr("value");
			}
			if("${standard}"=="true"){
				if($("#tableColumnId").get(0).selectedIndex==0){
					alert("请选择对应字段");
					return;
				}else{
					parent.textAreaHtml($("#tableColumnId").attr("value")
							,$("#controlId").attr("value")
							,name
							,$("#title").attr("value")
							,$("#defaultValue").attr("value")
							,$("#maxLength").attr("value")
							,$("#dataType").attr("value")
							,$("#classId").attr("value")
							,styleId);
				}
			}else{
				parent.textAreaHtml(""
							,$("#controlId").attr("value")
							,name
							,$("#title").attr("value")
							,$("#defaultValue").attr("value")
							,$("#maxLength").attr("value")
							,$("#dataType").attr("value")
							,$("#classId").attr("value")
							,styleId);
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
		<button class="btn" onclick="$('#textForm').submit();"><span><span>确定</span></span></button>
		<button class="btn" onclick='parent.$.colorbox.close();'><span><span >取消</span></span></button>
	</div>
	<div id="opt-content">
		<aa:zone name="controlContent">
			<form name="textForm" id="textForm" action="${mmsCtx }/form/form-view!text.htm">
				<s:hidden name="id"></s:hidden>
				<s:hidden id="code" name="code"></s:hidden>
				<s:hidden id="version" name="version"></s:hidden>
				<s:hidden id="standard" name="standard"></s:hidden>
				<s:hidden id="occasion" name="occasion" value="changeSource"></s:hidden>
				<input id="maxLengthExist" value="${formControl.maxLength }" type="hidden"/>
				<table class="form-table-without-border">
					<tbody>
						<tr>
							<td class="content-title">控件类型：</td>
							<td class="no-edit">
								<s:property value="formControl.controlType.code"/>
								<s:hidden theme="simple" id="controlType" name="formControl.controlType" ></s:hidden>
							</td>
							<td>
							</td>	
						</tr>	
						<s:if test="standard">
							<tr>
								<td class="content-title">对应字段：</td>
								<td>
								<s:hidden id="dataType" name="formControl.dataType"></s:hidden>
									<s:select onchange="choiceControl();" id="tableColumnId" name="tableColumnId" list="columns" theme="simple" listKey="id" listValue="alias" headerKey="0" headerValue="请选择"></s:select>
								</td>
								<td></td>	
							</tr>
							<tr>
								<td class="content-title">字段名：</td>
								<td>
									<s:if test="tableColumnId==null||tableColumnId==0">
										<s:textfield theme="simple" id="name" name="formControl.name" cssClass="{required:true,messages: {required:'必填'}}"></s:textfield>
									</s:if>
									<s:else>
										<s:textfield theme="simple" id="name" name="formControl.name" readonly="true" cssClass="{required:true,messages: {required:'必填'}}"></s:textfield>
									</s:else>
									<s:hidden  theme="simple" name="formControl.dataType" id="dataType"/>
									<span class="required">*</span>
								</td>
								<td>
									<span id="nameTip"></span>
								</td>	
							</tr>
						</s:if>
						<s:else>
							<tr>
								<td class="content-title">字段名：</td>
								<td>
									<s:textfield theme="simple" id="name" name="formControl.name"  cssClass="{required:true,messages: {required:'必填'}}" maxlength="27"></s:textfield>
									<span class="required">*</span>
								</td>
								<td>
									<span id="nameTip"></span>
								</td>	
							</tr>
							<tr>	
								<td class="content-title">字段类型：</td>	
								<td>
									<s:select theme="simple" id="dataType" list="#{'TEXT':'文本','CLOB':'大文本'}" 
							 name="formControl.dataType" onchange="defautMaxlengthSet();"></s:select>
								</td>
							</tr>
						</s:else>
						<tr>
							<td class="content-title">字段别名：</td>
							<td>
								<s:textfield theme="simple" id="title" name="formControl.title"  cssClass="{required:true,messages: {required:'必填'}}"></s:textfield>
								<span class="required">*</span>
							</td>
							<td><span id="titleTip"></span></td>	
						</tr>
						<tr>
							<td class="content-title">控件id：</td>
							<td>
								<s:textfield theme="simple" id="controlId" name="formControl.controlId"  cssClass="{required:true,messages: {required:'必填'}}"></s:textfield>
								<span class="required">*</span>
							</td>
							<td><span id="controlIdTip"></span></td>	
						</tr>	
						<tr>
							<td class="content-title">样式类名：</td>
							<td>
								<s:textfield theme="simple" id="classId" name="formControl.classStyle"></s:textfield>
							</td>
							<td></td>	
						</tr>
						<tr>
							<td class="content-title">内联样式：</td>
							<td>
								<s:textfield theme="simple" id="styleId" name="formControl.styleContent"></s:textfield>
							</td>
							<td></td>	
						</tr>	
						<s:if test="formControl.controlType.enumName!='SELECT'">
							<tr>
								<td class="content-title">默认值：</td>
								<td>
									<s:textfield theme="simple" id="defaultValue" name="formControl.controlValue" ></s:textfield>
								</td>
								<td><span id="controlValueTip"></span></td>	
							</tr>	
						</s:if>
						<s:if test="standard">
							<tr>
								<td class="content-title">最大长度：</td>
								<td>
									<s:if test="tableColumnId==null||tableColumnId==0">
										<s:textfield theme="simple" id="maxLength" name="formControl.maxLength" onkeyup="value=this.value.replace(/[^0-9]/,'');"></s:textfield>
									</s:if><s:else>
										<s:textfield theme="simple" id="maxLength" name="formControl.maxLength" onkeyup="value=this.value.replace(/[^0-9]/,'');" readonly="true"></s:textfield>
									</s:else>
								</td>
							</tr>
						</s:if><s:else>
							<tr>
								<td class="content-title">最大长度：</td>
								<td>
									<s:textfield theme="simple" id="maxLength" name="formControl.maxLength" onkeyup="value=this.value.replace(/[^0-9]/,'');"></s:textfield>
								</td>
							</tr>
						</s:else>
					</tbody>
				</table>
			</form>
			<script type="text/javascript">
			function validateText(){
				$("#textForm").validate({
					submitHandler: function() {
						generateHtml();
					}
				});
			}
			validateText();
			</script>
		</aa:zone>
	</div>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>
