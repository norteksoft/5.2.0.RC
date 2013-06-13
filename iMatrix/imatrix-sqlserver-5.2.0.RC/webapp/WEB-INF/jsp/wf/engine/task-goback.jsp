<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>退回</title>
		<%@ include file="/common/wf-colorbox-meta.jsp"%>
			
		<script type="text/javascript">

		function goback(wfId, form){
			var taskName =getTaskName();
			if(taskName != ""){
				$("#okBtn").attr("disabled","disabled");
				$("#cancelBtn").attr("disabled","disabled");
				$.post("${wfCtx }/engine/task!goback.htm", "workflowId=" + wfId + "&backto=" + taskName+"&wfdId=${wfdId}", postSuccess);
			}
		}
		function postSuccess(msg){
			msg=msg+"";
			var arr=msg.split("=");
			$("#okBtn").remove("disabled");
			$("#cancelBtn").remove("disabled");
			if(arr[0]=="OK"){
				$("#back-msg").html("环节跳转成功");
				$("#back-msg").show("show");
				setTimeout('$("#back-msg").hide("show");',2000);
				window.parent.backViewClose($("#wfdId").attr("value"),"${position}","${type }","${definitionCode }");
				window.parent.$.colorbox.close();
			}else if(arr[0]=="RETURN_URL"){
				if("${position}"=="monitorManager"){
					window.parent.taskJumpAssignTransactor("${workflowId }",getTaskName(),$("#wfdId").attr("value"),"${position}","${type }","${definitionCode }");
				}else{
					window.parent.taskJumpAssignTransactor("${workflowId }",getTaskName(),$("#wfdId").attr("value"),"${position}");
				}
			}else if(arr[0]=="SINGLE_TRANSACTOR_CHOICE"){
				if("${position}"=="monitorManager"){
					window.parent.taskJumpChoiceTransactor(arr[1],"${workflowId }",getTaskName(),$("#wfdId").attr("value"),"${position}","${type }","${definitionCode }");
				}else{
					window.parent.taskJumpChoiceTransactor(arr[1],"${workflowId }",getTaskName(),$("#wfdId").attr("value"),"${position}");
				}
			}else {
				alert(arr[0]);
				window.parent.$.colorbox.close();
			}
		}

		function getTaskName(){
			var rds = $("input[name='backto']");
			var taskName = "";
			for(var i = 0; i < rds.length; i++){
				if($(rds[i]).attr("checked")){
					taskName = $(rds[i]).attr("value");
				}
			}
			return taskName;
		}
		</script>
	</head>
	
	<body onload="getContentHeight();">
	 <div class="ui-layout-center">
		 <div class="opt-body">
				<div class="opt-btn">
					<s:if test="canBackTo.size() != 0">
						<security:authorize ifAnyGranted="wf_task_go_back">
							<button class='btn' onclick="goback('${workflowId }');" hidefocus="true" id="okBtn"><span><span>确定</span></span></button>
						</security:authorize>
					</s:if>
					<button class='btn' onclick="window.parent.$.colorbox.close();" hidefocus="true" id="cancelBtn"><span><span>返回</span></span></button>
				</div>
				<div id="back-msg" style="margin: 5px 10px;color: red;" class="tabDiv"></div>
				<div id="opt-content">
					<div id="form-info" style="margin: 16px 10px;" class="tabDiv">
						<form id="goBackform"></form>
						<input id="wfdId" name="wfdId" value="${wfdId }" type="hidden"/>
						<input id="workflowId" type="hidden" name="workflowId" value="${workflowId }"/>
						<table>
						<s:if test="canBackTo && canBackTo.size() == 0">
							<tr>
								<td>
								流程已结束或有并发环节,无法跳转环节
								</td>
							</tr>
						</s:if><s:else>
							<s:iterator value="canBackTo" id="toTask">
								<tr>
									<td>
										<input value="${toTask}" type="radio" name="backto"/> ${toTask}
									</td>
								</tr>
							</s:iterator>
						</s:else>
						</table>
					</div>
				</div>
			</div>
	   </div>
	</body>
</html>