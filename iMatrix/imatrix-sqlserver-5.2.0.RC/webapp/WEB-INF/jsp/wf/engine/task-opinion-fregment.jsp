<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<p style="margin: 8px 0 0 0;height: 16px; line-height: 16px;">办理意见：
<a href="#" onclick="viewMsg(this,'${taskId}');" state="1"><img id="viewImg" src="${wfCtx}/images/x1.png"></a> 
</p>
<form name="parameterform" id="parameterform" method="post">
<input type="hidden" name="workflowId" value="${workflowId}"/>
<input type="hidden" name="taskId" value="${taskId}"/>
<input type="hidden" name="companyId" value="${companyId}"/>
</form>
<div id="comments" style="display: none;">
	<aa:zone name="opinionList">
	</aa:zone>
</div>
