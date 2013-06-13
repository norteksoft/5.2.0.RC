<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>授权管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		<aa:zone name="pageTable">
			<div class="opt-btn">
				<button class="btn" onclick="validatePermissionSave();"><span><span>保存</span></span></button>
				<button class="btn" onclick="backPage();"><span><span >返回</span></span></button>
			</div>
			<div id="opt-content">
				<aa:zone name="pageTablelist">
				<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
				<form action="" name="backForm" id="backForm" method="post"> 
					<input name="permissionId" type="hidden" value="${permissionId }"/>
					<input name="dataRuleId" type="hidden" value="${dataRuleId }"/>
				</form>
				<form action="" name="viewSaveForm" id="viewSaveForm" method="post"> 
					<input name="permissionId" type="hidden" value="${permissionId }" id="permissionId"/>
					<input name="dataRuleId" type="hidden" value="${dataRuleId }" id="_dataRuleId"/>
					<table class="form-table-without-border">
						<tr>
							<td class="content-title" style="width:100px">数据规则：</td>
				  			<td><input id="data_Rule"  value="${permissionDataRule.name }" readonly="readonly"></input><span class="required">*</span></td>
						</tr>
						<tr>
							<td class="content-title" style="width:100px">优&nbsp;先&nbsp;级：</td>
				  			<td><input id="priority"  name="priority" value="${priority }" onkeyup="value=value.replace(/[^\d]/g,'')"></input><span class="required">*</span></td>
						</tr>
						<tr>
							<td class="content-title">操作权限：</td>
							<td><fieldset >
									<legend><input type="checkbox" onclick="selectAll(this, 'docAuthes')">权限</legend>
									<ul class="authorize" style="margin: 2px 10px;padding-left:20px;">
										<s:iterator value="@com.norteksoft.mms.authority.enumeration.PermissionAuthorize@values()"  id="auth">
											<s:set id="authResult" value="%{authority & #auth.code}"></s:set>
											<li><input name="docAuthes" type="checkbox" value="${auth}" <s:if test="#authResult !=0">checked="checked"</s:if> code="${auth.code }"><s:text name="%{#auth.i18nKey}"></s:text></li>
										</s:iterator>
									</ul>
								</fieldset></td>
						</tr>
						<tr>
							<td class="content-title">条件列表：</td>
							<td><view:formGrid gridId="childGridId" code="MMS_PERMISSION_ITEM_EDIT" entity="${permission}" attributeName="items"></view:formGrid>
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