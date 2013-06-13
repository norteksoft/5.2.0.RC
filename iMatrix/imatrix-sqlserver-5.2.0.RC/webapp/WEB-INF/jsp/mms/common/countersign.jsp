<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<html>
<head>
<title>流转历史</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
</head>
<body>
<div class="ui-layout-center">
	<aa:zone name="button_zone"></aa:zone>
	<aa:zone name="content_zone">
		<table class="form-table-border-left" >
				<thead>
					<tr>
						<th>办理人</th>
						<th>办理日期</th>
						<th>办理意见</th>
					</tr>
				</thead>
				<tbody>
					<s:iterator value="temps"> 
						<tr style="height: 22px;"><th colspan="2"> ${name} </th><th>(同意：${yesNum}&nbsp;&nbsp;&nbsp;&nbsp;不同意：${noNum} &nbsp;&nbsp;&nbsp;&nbsp;合计：${yesNum+noNum})</th></tr>
						<s:iterator value="task">
							<tr>
								<td width="200">${transactorName}</td>
								<td width="200"><s:date name="transactDate"  format="yyyy-MM-dd HH:mm" /></td>
								<td >
									<s:if test="taskProcessingResult.name=='transition.approval.result.agree'">同意</s:if><s:else>不同意</s:else>
								</td>
							</tr>
						</s:iterator>
					</s:iterator>
				</tbody>
			</table>
	</aa:zone>
</div>
</body>
</html>
