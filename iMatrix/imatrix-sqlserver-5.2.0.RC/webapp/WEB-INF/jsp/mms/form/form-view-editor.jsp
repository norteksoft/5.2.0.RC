<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>表单管理</title>
	
</head>
<body style="height:100%;width: 100%;text-align: left;">
<div class="ui-layout-center">
	  	<aa:zone name="form_main">
	  		<div id="message" style="display:none;"></div>
			<div class="editor" style="width:100%;margin: 5px 5px 5px 5px;">
					<textarea id="content"  style="visibility:hidden;">${htmlCode}</textarea>
				</div>
				<s:actionmessage/>
			<div style="display: none;">
				<form id="inputForm" name="inputForm" action="${mmsCtx }/form/form-view!save.htm" method="post">
					<s:hidden id="formId" name="formId"></s:hidden>
					<s:hidden id="operation" name="operation"></s:hidden>
					<s:textfield id="menuId" name="menuId" theme="simple"></s:textfield>
					<s:textfield id="isStandard" name="isStandard" theme="simple"></s:textfield>
					<s:textfield id="code" name="code" theme="simple"></s:textfield>
					<s:textfield id="name" name="name" theme="simple"></s:textfield> 
					<s:textfield id="version" name="version" theme="simple"></s:textfield>
					<s:textfield id="formStates" name="formState" theme="simple"></s:textfield> 
					<s:textarea theme="simple"  name="remark" id="remark"  cols="55" rows="5" ></s:textarea>
					<input id="html" name="htmlResult"></input>
				</form>			
			</div>	
			<div style="display: none;">
				<div id="saveChoice" style="margin-top: 5px">
					<div class="opt-btn" style="margin-bottom: 5px">
						<button class="btn" onclick="ajaxSave();$.colorbox.close();"><span><span>更新当前版本</span></span></button>
						<button class="btn" onclick="saveNewVersion();$.colorbox.close();"><span><span>保存为新版本</span></span></button>
						<button class="btn" onclick="$.colorbox.close();"><span><span >取消</span></span></button>
					</div>
					<div style="margin-left: 5px">
					  <font color="red">更新表单后,请注意更新流程图！</font> 
					</div>
				</div>
			</div>
			<form name="backForm" id="backForm" action="${mmsCtx }/form/list-data.htm" method="post">
				<s:hidden id="menuId" name="menuId"></s:hidden>
				<s:hidden name="dataTableId"></s:hidden>
			</form>
		</aa:zone>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
