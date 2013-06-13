<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<p style="margin: 8px 0 0 0;height: 16px; line-height: 16px;">正&emsp;&emsp;文：
   <a href="#" onclick="viewText(this,'${wfCtx}/engine/office.htm?workflowId=${workflowId }&taskId=${taskId }','${taskId }');" state="1"><img id="textImg" src="${wfCtx}/images/x1.png"></a> 
</p>
<form action="" name="officeForm" id="officeForm" method="post"></form>
<div id="textDiv" style="display: none;">
	<aa:zone name="officelist">
	</aa:zone>
</div>


						