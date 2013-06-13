<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title></title>
	<%@include file="/common/meta.jsp" %>
<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
	<script type="text/javascript">
	</script>
</head>

<body>
	<script type="text/javascript">
		var secMenu="expenseReport";
		var thirdMenu="product";
	</script>
	
	<%@ include file="/menus/header.jsp" %>

	<%@ include file="/menus/second-menu.jsp" %>
	
	<div class="ui-layout-west">
		<%@ include file="/menus/third-menu.jsp" %>
	</div>
	<div class="ui-layout-center">
			<div class="opt-body">
				<aa:zone name="main">
					<div class="opt-btn">
						<button class='btn' onclick="iMatrix.addRow();"><span><span>新建</span></span></button>
						<button class='btn' onclick="iMatrix.delRow();"><span><span>删除</span></span></button>
						<button  class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
					</div>
					
					<div id="opt-content">
					<form id="sForm" name="sForm" method="post"  action=""></form>
					<div id="message"><s:actionmessage theme="mytheme" /></div>	
					<script type="text/javascript">setTimeout("$('#message').hide('show');",3000);</script>
						<form id="contentForm"  method="post"  action="">
							<grid:jqGrid gridId="${nameSpace}List" url="${ctx}/${nameSpace}/${lowCaseEntityName}-listDatas.htm" submitForm="sForm"  code="${listCode}"></grid:jqGrid>
						</form>
					</div>
				</aa:zone>
			</div>
	</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
</html>