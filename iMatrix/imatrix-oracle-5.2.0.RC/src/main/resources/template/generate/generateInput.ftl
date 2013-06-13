<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title></title>
		<%@include file="/common/meta.jsp" %>	
	</head>
	
	<body onload="getContentHeight();">
		<div class="ui-layout-center">
		<div class="opt-body">
				<aa:zone name="main">
					<div class="opt-btn">
						<#if containWorkflow?if_exists>
						<wf:workflowButtonGroup taskId="${taskId }"></wf:workflowButtonGroup>
						<#else>
						<button class='btn' onclick="save${entityName}('${ctx}/${namespace}/${entityAttribute}-save.htm')"><span><span>保存</span></span></button>
						</#if>
						<button class='btn' onclick='setPageState();ajaxSubmit("defaultForm","${ctx}/${namespace}/${entityAttribute}-list.htm","main");'><span><span>返回</span></span></button>
					</div>
					<div id="opt-content" class="form-bg">
					<div style="display: none;" id="message"><font class=onSuccess><nobr>操作成功</nobr></font></div>
						<form  id="inputForm" name="inputForm" method="post" action="">
							<input type="hidden" name="id" id="id" value="${id }"/>
							<input type="hidden" name="taskTransact" id="taskTransact" value="${taskTransact }"/>
							<grid:formView code="${formCode}" entity="${entityObject}"></grid:formView>
						</form>
					</div>
					<script type="text/javascript">
					//流程环节设置的必填字段
					$(document).ready(function(){
						addFormValidate('${fieldPermission}','inputForm');
					});
				</script>
				</aa:zone>
			</div>
			</div>
	</body>
</html>