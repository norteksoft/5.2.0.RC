<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
	<title>流程管理</title>
	
</head>
<body>
<div class="ui-layout-center">
<div class="opt-body">
	<aa:zone name="myprocess">
		<aa:zone name="wf_definition">
			<aa:zone name="wf_task">
				<div class="opt-btn">
					<wf:workflowButtonGroup taskId="${taskId }" formCode="${form.code}" version="${form.version}"></wf:workflowButtonGroup>
					<a href="#" onclick="back_main();" class="btnStyle ">返回</a>
					<button class="btn" onclick="ajaxSubmit('defaultForm','${wfCtx}/engine/workflow!collection.htm','myprocess');"><span><span >返回</span></span></button>
				</div>
				<div id="successMessage" class="onSuccess"><s:actionmessage theme="mytheme"/></div>
				<div id="opt-content" >
					<aa:zone name="viewProcess">
						<%@ include file="task-fragment.jsp" %>
					</aa:zone>
				</div>
			</aa:zone>
		</aa:zone>
	</aa:zone>
</div> 
</div>
</body>
</html>
