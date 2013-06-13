<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>页面预览</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	
	<script src="${mmsCtx}/js/module-page.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${mmsCtx}/js/mmsapi.js" type="text/javascript" charset="UTF-8"></script>
	
	<script src="${mmsCtx}/js/form-view.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${resourcesCtx}/js/form.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js" ></script>
	<script type="text/javascript">
		$(document).ready(function(){
			$('.btn').attr("disabled","disabled");
		   });
	</script>
</head>
<body  onload="getContentHeight();">
<div class="ui-layout-center">
	<div class="opt-body">
		<button:button code="${myCode}"></button:button>
		<div id="opt-content">
			<div id="opt_message" style="display:none;"></div>
			<s:if test="type=='LIST_VIEW'">
				<view:jqGrid url="hasNoUrl" code="${viewCode}" gridId="main_table" ></view:jqGrid>
			</s:if><s:else>
				<view:formView code="${viewCode}"></view:formView>
			</s:else>
		</div>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
</html>
