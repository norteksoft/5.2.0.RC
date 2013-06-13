<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>表单管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	<script src="${imatrixCtx}/widgets/formeditor/kindeditor.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/lang/zh_CN.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/formeditor.js" type="text/javascript"></script>
	<link href="${imatrixCtx}/widgets/formeditor/formeditor.css" rel="stylesheet" type="text/css" />
	
	<script src="${mmsCtx}/js/form-view.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${resourcesCtx}/js/form.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js" ></script>
	<script type="text/javascript">
		$(document).ready(function() {
			addFormValidate('${validateSetting}');
			$("input[plugintype='DATA_ACQUISITION']").attr("disabled","disabled");
			$("input[plugintype='BUTTON']").attr("disabled","disabled");
			$("input[plugintype='STANDARD_LIST_CONTROL']").attr("disabled","disabled");
		});	
	</script>
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<button class="btn" onclick="$('#textForm').submit();"><span><span>确定</span></span></button>
	</div>
	<div id="opt-content">
		<aa:zone name="controlContent">
			<form name="textForm" id="textForm" action="" method="post">
			${formHtml }
			</form>
			<script type="text/javascript">
				$("#textForm").validate({
					submitHandler: function() {
						alert("验证成功");
					},
					errorPlacement: function(error, element) { //指定错误信息位置
						if (element.is(':radio') || element.is(':checkbox')) {  //如果是radio或checkbox
							var eid = element.attr('name');  //获取元素的name属性
		                    error.appendTo(element.parent());    //将错误信息添加当前元素的父结点后面
		                } else {
		                    error.insertAfter(element); 
		                }
		            }		         		            
				});
			</script>
		</aa:zone>
	</div>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
</html>
