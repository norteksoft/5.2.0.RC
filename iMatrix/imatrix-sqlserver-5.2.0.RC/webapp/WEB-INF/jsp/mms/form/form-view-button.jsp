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
	function generateHtml(){
		//<a href='#' class='small_btn' onclick=\\\"jQuery('#").append(tableId).append("').jqGrid('editRow','\"+cl+\"');\\\"  ><span>编辑</span></a>
		var classStyle="";
		var styleContent="";
		var event="";
		if($("#classId").attr("value")!=""){
			classStyle=" class='"+$("#classId").attr("value")+"'";
		}
		if($("#styleId").attr("value")!=""){
			styleContent=" style='"+$("#styleId").attr("value")+"'";
		}
		var fun="";
		if($("#eventId").attr("value")!=""){
			fun=$("#eventId").attr("value");
			event=" onclick='"+fun+"'";
		}
		var html ="<input type='button' pluginType='BUTTON' " 
				+classStyle
				+styleContent
				+event
				+" fun='"+fun+"'"
				+" value='"+$("#name").attr("value")
				+"' id='"+$("#controlId").attr("value")
				+"' hiddenid='"+$("#showButtonControlId").attr("value")
				+"'/>";
		parent.html(html);
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
			<div style="margin: 10px;text-align: left;">
				<form name="textForm" id="textForm" action="${mmsCtx }/form/form-view!text.htm">
					
					<fieldset style="border: #f0f0ee solid 1px; padding: 3px;">
						<legend style="margin: 0 10px; color: #0046d5;">&nbsp;控件描述&nbsp;</legend>
						<table class="form-table-without-border">
						<tbody>
							<tr>
								<td class="content-title">控件类型</td>
								<td>
									<s:property value="formControl.controlType.code"/>
								</td>
								<td>
								</td>	
							</tr>	
							<tr>
								<td class="content-title">控件名：</td>
								<td>
									<s:textfield theme="simple" id="name" name="formControl.name"  cssClass="{required:true,messages: {required:'必填'}}" ></s:textfield>
								</td>
								<td>
									<span id="nameTip"></span>
								</td>	
							</tr>
							<tr>
								<td class="content-title">控件id：</td>
								<td>
									<s:textfield theme="simple" id="controlId" name="formControl.controlId"  cssClass="{required:true,messages: {required:'必填'}}" ></s:textfield>
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
							<tr>
								<td class="content-title">控件事件：</td>
								<td>
									<s:textfield theme="simple" id="eventId" name="formControl.clickEvent"></s:textfield>
								</td>
								<td></td>	
							</tr>
							 <tr >
					      		<td>绑定按钮的输入框的控件id：</td>
					      		<td><s:textfield  theme="simple" name="formControl.showButtonControlId" id="showButtonControlId"/></td>
					      		<td><span id="showButtonControlIdTip"></span></td>
					      </tr>	
						</tbody>
					</table>
					</fieldset>
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
			</div>
		</aa:zone>
	</div>
</div>
</div>	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>
