<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<html>
<head>
<title>流转历史</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
</head>
<body>
	<aa:zone name="button_zone"></aa:zone>
	<aa:zone name="content_zone">
		<wf:history companyId="${companyId}"
			url="${imatrixCtx}/widgets/workflowEditor/FlowChartProject.swf" 
			taskId="${taskId}"></wf:history>
	</aa:zone>
</body>
</html>
