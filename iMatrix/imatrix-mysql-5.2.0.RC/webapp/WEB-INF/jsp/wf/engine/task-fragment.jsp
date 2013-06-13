<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<span id="get_back_msg" style="color: red;"></span>
<div id="form-info" style="margin: 5px 5px 5px 5px;" class="tabDiv">
	<input type="hidden" id="mustoption" name="must" value="${must }"/>
	<input type="hidden" id="editedoption" value="false"/>
	<!--发起流程时使用  -->
	<input type="hidden" name="definitionId" id="definitionId" value="${definitionId }"/>
	<form name="inputForm" id="inputForm" action=""  method="post">
		<input name="nullAssignmentException" id="nullAssignmentException" type="hidden" value="${nullAssignmentException}"></input>
		<input type="hidden" id="taskId" name="taskId" value="${taskId }" />
		<input type="hidden" id="wf_task_transact" name="transact" value="" >
		<input name="newTransactor" id="newTransactor" type="hidden" value=""></input>
		<input id="opt" type="hidden"></input>
		<div id="excepMessage" >
			<table >
				<tr><td>${messageTip}</td></tr>
				<s:iterator value="transitionNames" var="tran">
					<tr><td><input type="radio" name="transitionName" value="${tran[0]}"/>&nbsp;&nbsp;${tran[1]}</td></tr>
				</s:iterator>
			</table>
		</div>
		<div>
			${formHtml }
		</div>
	</form>
	
	<div style="display: none;" id="requiredFields">${requiredFields }</div>
	
	<script type="text/javascript">
		addFormValidate('${fieldPermission}');
		$("#inputForm").validate({
			submitHandler: function() {
				successSubmit($("#opt").attr("value"));
			}
		});
	</script>
		
	<%@ include file="task-opinion-fregment.jsp"%>
	<%@ include file="office-fragment.jsp"%>
	<%@ include file="upload-fregment.jsp"%>
</div>

