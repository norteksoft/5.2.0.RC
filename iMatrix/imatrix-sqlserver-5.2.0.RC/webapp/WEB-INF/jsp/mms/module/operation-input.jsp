<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>列表管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		<aa:zone name="pageTable">
			<div class="opt-btn">
				<button class="btn" onclick="saveOperation();"><span><span>保存</span></span></button>
				<button class="btn" onclick="backPage();"><span><span >返回</span></span></button>
			</div>
			<div id="opt-content">
				<aa:zone name="pageTablelist">
				<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
				<form action="" name="backForm" id="backForm" method="post"> 
					<input name="operationId" type="hidden" value="${operationId }"/>
					<input name="systemId" type="hidden" value="${systemId }"/>
				</form>
				<form action="" name="viewSaveForm" id="viewSaveForm" method="post"> 
					<input name="operationId" type="hidden" value="${operationId }" id="operationId"/>
					<input name="systemId" type="hidden" value="${systemId }" id="_systemId"/>
					<table class="form-table-without-border">
						<tr>
							<td class="content-title" style="width:100px">编号：</td>
				  			<td><s:textfield theme="simple" id="code" name="code" maxlength="255" size="60" onblur="parentCodeBlur();"></s:textfield><span class="required">*</span></td>
						</tr>
						<tr>
							<td class="content-title" style="width:100px">名称：</td>
				  			<td><s:textfield theme="simple" id="name" name="name" maxlength="255" size="60"></s:textfield><span class="required">*</span></td>
						</tr>
						<tr>
							<td class="content-title">备注：</td>
							<td><s:textarea  cols="30" rows="5" id="remark" name="remark" onkeyup="javascript:if(this.value.length >=120){this.value=this.value.slice(0,120);alert('字符不能超过120');return false;}"></s:textarea></td>
						</tr>
						<tr>
							<td class="content-title">子功能列表：</td>
							<td><view:formGrid gridId="childGridId" code="MMS_OPERATION_CHILED_EDIT" entity="${operation}" attributeName="children"></view:formGrid>
							</td>
						</tr>
					</table>
					</form>
				</aa:zone>
			</div>
		</aa:zone>
	</div>
</div>
</body>
</html>