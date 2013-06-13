<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>数据规则</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
</head>
<body>
	<aa:zone name="main_zone">
		<div class="opt-btn">
			<button class="btn" onclick="saveDataRule('${mmsCtx}/authority/data-rule-save.htm');"><span><span>保存</span></span></button>
			<button class="btn" onclick='setPageState();ajaxSubmit("defaultForm","${mmsCtx}/authority/data-rule.htm","main_zone");'><span><span >返回</span></span></button>
		</div>
		<div id="opt-content">
			<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
			<form id="saveForm" name="saveForm" action="" method="post">
				<input type="hidden" name="dataRuleId" id="dataRuleId" value="${dataRuleId}"/>
				<table class="form-table-without-border" style="width: auto;">
					<tr>
						<td  class="content-title">类&nbsp;&nbsp;&nbsp;别：</td>
						<td ><input type="text" id="ruleTypeName" name="ruleTypeName" readonly="readonly" style="width:400px;" value="${ruleTypeName }"/>
							<span class="required">*</span>
							<input type="hidden" id="ruleTypeId" name="ruleTypeId" value="${ruleTypeId }"/>
							<a href="#"  class="small-btn" onclick="selectRuleType();"><span id="choose-user"><span>选择</span></span></a>
						</td>
					</tr>
					<tr>
						<td class="content-title">编&nbsp;&nbsp;&nbsp;号：</td>
						<td><input type="text" id="code" name="code" maxlength="60" value="${code }" style="width:400px;"/>
						<span class="required">*</span></td>
					</tr>
					<tr>
						<td class="content-title">名&nbsp;&nbsp;&nbsp;称：</td>
						<td><input type="text" id="name" name="name" maxlength="60" value="${name }" style="width:400px;"/>
						<span class="required">*</span></td>
					</tr>
					<tr>
						<td  class="content-title">数据表：</td>
						<td ><input type="text" id="dataTableName" name="dataTableName"readonly="readonly" style="width:400px;" value="${dataTableName }"/>
							<span class="required">*</span>
							<input type="hidden" id="dataTableId" name="dataTableId" value="${dataTableId }"/>
							<input type="hidden" id="dataTableMenuId" name="dataTableMenuId" value=""/>
							<a href="#"  class="small-btn" onclick="selectDataTable();"><span id="choose-user"><span>选择</span></span></a>
						</td>
					</tr>
					<tr>
						<td class="content-title">备&nbsp;&nbsp;&nbsp;注：</td>
						<td>
						<textarea id="remark" onkeyup="if(this.value.length>60)this.value=this.value.substring(0,60);" cols="50" rows="2" name="remark">${remark }</textarea>
						</td>
					</tr>
					<tr>
						<td class="content-title">规则条件：</td>
						<td></td>
					</tr>
				</table>
				<div style="height: 10px;"></div>
				<view:formGrid gridId="conditionGrid" code="MMS_CONDITION" entity="${dataRule}" attributeName="conditions"></view:formGrid>
			</form>	
		</div>
	</aa:zone>
</body>
</html>