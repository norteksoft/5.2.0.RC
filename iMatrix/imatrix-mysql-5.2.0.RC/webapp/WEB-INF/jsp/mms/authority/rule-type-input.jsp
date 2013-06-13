<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>表单管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	<script type="text/javascript">
	$(document).ready(function() {
		validateRuleType();
	});
	
	//验证
	function  validateRuleType(){
		$("#ruleTypeForm").validate({
			submitHandler: function() {
				ajaxSubmitRuleTypeForm();
			},
			rules: {
				code: "required",
				name:"required"
			},
			messages: {
				code: "必填",
				name:"必填"
			}
		});
	}
	//提交保存
	function ajaxSubmitRuleTypeForm(){
		$.ajax({
			   type: "POST",
			   url: webRoot+"/authority/rule-type-validateCode.htm",
			   data: "code="+$("#code").attr("value")+"&id="+$("#id").attr("value"),
			   success: function(data){
			   		if(data=="true"){
			   			$("#ruleTypeForm").ajaxSubmit(function (data){
							var datas = data.split(":");
			   				parent.updateRuleTypeTree(datas[0],datas[1]);
			   				parent.$.colorbox.close();
			   			});
			   		}else{
			   			$("#message").html("<font class=\"onError\"><nobr>"+"此编号已经存在！"+"</nobr></font>");
						showMsg();
						$("#code").attr("value","");
				   	}
			   }
			}); 
	
		
	}
	</script>
</head>
<body >
<div class="ui-layout-center">
<div class="opt-body">
	<aa:zone name="form_main">
			<div class="opt-btn">
				<button class="btn" onclick="$('#ruleTypeForm').submit();"><span><span>保存</span></span></button>
				<button class="btn" onclick='window.parent.$.colorbox.close();' id="create"><span><span >取消</span></span></button>
			</div>
				<div id="opt-content">
				    <div id="message" style="display:none;"></div>
					<form id="ruleTypeForm" action="${mmsCtx }/authority/rule-type-save.htm" method="post" name="ruleTypeForm">
					<input type="hidden" name="parentRuleTypeId" id="parentRuleTypeId" value="${parent.id }">
					<input type="hidden" name="id" id="id" value="${id }">
					  	<table class="form-table-without-border" >
					  		<tr>
					  			<td class="content-title">编号：</td>
					  			<td><s:textfield  theme="simple" id="code" name="code" size="50" maxlength="64"></s:textfield><span class="required">*</span></td>
					  		</tr>
					  		<tr>
					  			<td class="content-title">名称：</td>
					  			<td><s:textfield  theme="simple" id="name" name="name"  maxlength="64" size="50"></s:textfield><span class="required">*</span></td>
					  		</tr>
				  		</table>
				  </form>
				</div>
	</aa:zone>
</div>
</div>
</body>
</html>
