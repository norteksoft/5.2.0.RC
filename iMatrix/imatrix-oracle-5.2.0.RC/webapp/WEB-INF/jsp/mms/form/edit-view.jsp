<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>表单管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>

	<script type="text/javascript" src="${resourcesCtx }/widgets/jstree/jquery.jstree.js"></script>
	
	<script src="${mmsCtx}/js/list-view.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${mmsCtx}/js/list-column.js" type="text/javascript" charset="UTF-8"></script>
	
	<script type="text/javascript">
	$(function(){jQuery.extend($.jgrid.defaults,{
		height: 200
	});});
	
	</script>
</head>
<body>
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<button class="btn" onclick="saveColumn();"><span><span>保存</span></span></button>
	</div>
	<aa:zone name="viewTable">
		<div id="opt-content">
			<aa:zone name="columnTable">
				<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
				<form id="columnSaveForm" name="columnSaveForm" action="" method="post">
					<input name="viewId" value="${view.id }" type="hidden"></input>
					<view:formGrid gridId="listColumnId" code="MMS_LIST_COLUMN" entity="${view}" attributeName="columns"></view:formGrid>
				</form>
			</aa:zone>
		</div>
	</aa:zone>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
