<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<html>
	<head>
		<title>流程类型管理</title>
	</head>
	<body>
	<div class="ui-layout-center">
		<aa:zone name="wf_type">
			<div class="opt-btn">
				<security:authorize ifAnyGranted="wf_engine_wf_type_save">
					<button  class='btn' onclick="save_form();" hidefocus="true"><span><span>保存</span></span></button>
				</security:authorize>	
				<button class='btn' onclick='setPageState();wfTypeBack("inputForm","${wfCtx}/engine/workflow-type.htm","wf_type");' hidefocus="true"><span><span>返回</span></span></button>
			</div>
			<div id="opt-content">
				<form action="${wfCtx}/engine/workflow-type!save.htm " name="inputForm" id="inputForm" method="post">
					<div id="message" style="display: none"><s:actionmessage theme="mytheme" /></div>					
					<input name="id" id="id" type="hidden" value="${id}"/>
					<p style="padding-left: 5px;">类型编号：<s:if test="code==null"><input id="code" name="code"  type="text" value="${code }"/></s:if><s:else><input id="code" name="code"  type="text" value="${code }" readonly="readonly"/></s:else><span class="required">*</span></p>
					<p style="padding-left: 5px;">类型名称：<input id="name" name="name"  type="text" value="${name }"/><span class="required">*</span></p>
					<p style="padding-left: 5px;">是否是审批系统：<input id="approveSystem" name="approveSystem" <s:if test="approveSystem">checked="checked"</s:if> value="true" type="checkbox"/></p>
				</form>
			</div>
		</aa:zone>
	</div>
	</body>
</html>
