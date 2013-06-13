<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">

<html>
	<head>
		<title>选择环节</title>
		<%@ include file="/common/mms-iframe-meta.jsp"%>
		<script type="text/javascript">
			function submitForm(url){
				getTache();
				var code = $("#tacheCode").attr("value");
				if(code==""||code==null){
					alert("请选择下一环节！");
				}else{
					$("#selectTacheForm").attr("action",url);
					$("#selectTacheForm").ajaxSubmit(function (id){
							window.parent.$.colorbox.close();
							if($("#closeFlag").attr('value')=='false'){
								window.parent.location.reload(true);
							}else{
								window.parent.parent.close();
								window.parent.location.reload(true);
							}
					});
				}
			}
	
			function getTache(){
				var rds = $("input[name='tacheName']");
				var tacheCode = "";
				for(var i = 0; i < rds.length; i++){
					if($(rds[i]).attr("checked")){
						$("#tacheCode").attr("value",$(rds[i]).attr("value"));
					}
				}
			}
		</script>
	</head>
	
	<body onload="getContentHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
				<aa:zone name="main">
					<div class="opt-btn">
						<s:if test="task==null">
							<button class='btn' onclick="submitForm('${mmsCtx}/common/assign.htm')"><span><span>提交</span></span></button>
						</s:if>
					</div>
					<div style="display: none;" id="message"><s:actionmessage theme="mytheme" /></div>
					<div id="opt-content" class="form-bg">
						<form  id="selectTacheForm" name="selectTacheForm" method="post" action="">
							<input type="hidden" name="taskId" id="taskId" value="${taskId }"></input>
							<input type="hidden" name="tacheCode" id="tacheCode" value="${tacheCode }"></input>
							<input type="hidden"  name="opinionflag" id="opinionflag" value="${opinionflag }"></input>
							<input type="hidden"  name="submitflag" id="submitflag" value="${submitflag }"></input>
							<input id="closeFlag" type="hidden"  value="${closeFlag}"/>
							<table class="form-table-border-left">
								<tr>
									<td>选择下一环节</td>
								</tr>
									<s:iterator value="choiceTransactor.keySet()" id="tache">
									<tr>
										<s:set id="tacheName" value="choiceTransactor.get(#tache)"></s:set>
										<td><input type="radio" value="${tache}" name="tacheName"/> ${tacheName}</td>
									</tr>
									</s:iterator>
							</table>
						</form>
					</div>
				</aa:zone>
			</div>
		</div>
	</body>
</html>