<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<p style="margin: 8px 0 0 0;">附&emsp;&emsp;件：
<a href="#" onclick="viewAccessory(this,'${mmsCtx}/engine/upload.htm','${taskId }');" state="1"><img id="accImg" src="${mmsCtx}/images/x1.png"></a> 
</p>
<form action="" name="viewAccessoryForm" id="viewAccessoryForm" method="post">
<input id="workflowId_upload" type="hidden" name="workflowId" value="${workflowId }"/>
<input id="taskId" type="hidden" name="taskId" value="${taskId }"/>
<input id="companyId" type="hidden" name="companyId" value="${companyId }"/>
</form>
<div style="display: none; margin-left: 30px;" id="accessory">
	<aa:zone name="viewAccessorylist">	
	</aa:zone>
</div>
	
