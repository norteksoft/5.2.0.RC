<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<title>定时设置</title>
</head>
<body>
	<div class="ui-layout-center">
		<aa:zone name="groups_main">
			<div class="opt-btn">
				<a class="btn" href="#" onclick="ajaxSubmit('defaultForm','${settingCtx}/options/job-info.htm','groups_main');"><span><span>返回</span></span></a>
			</div>
			<div id="opt-content" >
				<form action="" name="jobInfoFrom" id="jobInfoFrom" method="post">
					<input type="hidden" name="id" id="id" value="${id}"/>
					<input type="hidden" name="systemId" id="systemId" value="${systemId}"/>
					<table>
						<tbody>
							<tr>
								<td>
									定时任务编号：<input readonly="readonly" type="text" name="code" id="jobCode" value="${code}" maxlength="60"/>
								</td>
								<td>
									定时任务址址：<input readonly="readonly" type="text" name="url" id="url" value="${url}" maxlength="60"/>
								</td>
								<td>
									定时任务备注：<input readonly="readonly" type="text" name="description" id="urlInfo" value="${description}" maxlength="60"/>
								</td>
							</tr>
							<tr>
								<td colspan="3">
									<view:formGrid gridId="corninfoId" code="BS_CORNINFO_VIEW" collection="${cornInfos}"></view:formGrid>
								</td>
							</tr>
						</tbody>
					</table>
				</form>
			</div>
		</aa:zone>
	</div>
</body>
</html>