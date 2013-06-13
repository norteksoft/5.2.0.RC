<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>表单管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	<script type="text/javascript" src="${resourcesCtx }/widgets/jstree/jquery.jstree.js"></script>
		<script src="${mmsCtx}/js/authority.js" type="text/javascript"></script>
	<script src="${mmsCtx}/js/authority-rule-type.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			createRuleTypeTree("rule-type-tree");
		});

	</script>
</head>
<body >
<div class="ui-layout-center">
<div class="opt-body">
	<aa:zone name="form_main">
			<div class="opt-btn">
				<button class="btn" onclick='createRuleType();'><span><span >新建</span></span></button>
				<button class="btn" onclick="updateRuleType();"><span><span >修改</span></span></button>
				<button class="btn" onclick="deleterRuleType();"><span><span >删除</span></span></button>
			</div>
			<aa:zone name="viewTable">
				<div id="opt-content">
					<div id="message" style="display:none; "></div>
					<table >
						<tr >
							<td >
								<div class="demo" id="rule-type-tree" style="background: none;"></div>
							</td>
						</tr>
					</table>
				</div>
			</aa:zone>
	</aa:zone>
</div>
</div>
</body>
</html>
