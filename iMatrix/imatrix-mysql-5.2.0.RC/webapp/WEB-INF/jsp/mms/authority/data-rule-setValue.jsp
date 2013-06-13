<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>数据表字段</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js"></script>
	<script src="${mmsCtx}/js/authority-data-rule.js" type="text/javascript"></script>
	<script type="text/javascript">
	function getValue(){
		var rds = $("input[name='valueName']");
		var value = "";
		for(var i = 0; i < rds.length; i++){
			if($(rds[i]).attr("checked")){
				return $(rds[i]).attr("value");
			}
		}
		return "";
	}

	function submitDataRule(){
			var value = getValue();
			var currentInputId="${currentInputId }";
			var dataType="${dataType }";
			if(value==""){
				alert("请选择！");return;
			}else{
				setOperatorValue(currentInputId,value);
				window.parent.$.colorbox.close();
			}
		}
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:200px;
	}
	</style>
</head>
<body >
<div class="ui-layout-center">
<div class="opt-body">
	<form id="defaultForm" name="defaultForm"action="" method="post" ></form>
	<aa:zone name="main_zone">
		<div class="opt-btn">
			<button class="btn" onclick="submitDataRule();"><span><span >确定</span></span></button>
		</div>
		<div id="opt-content">
			<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
			<form action="" name="pageForm" id="pageForm" method="post">
				<table class="form-table-border-left" style="width: 150px;margin-left: 60px;margin-top: 10px;">
					<s:iterator value="values" var="bean">
						<tr>
							<td style="width: 25%;"><input name="valueName" type="radio" value="${bean[0] }"/></td>
							<td>${bean[1] }</td>
						</tr>
					</s:iterator>
				</table>
			</form>
		</div>
	</aa:zone>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
