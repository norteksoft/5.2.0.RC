<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>系统元数据管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	
	<script type="text/javascript" src="${resourcesCtx }/widgets/jstree/jquery.jstree.js"></script>
	
	<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
	<script src="${resourcesCtx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${resourcesCtx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>
	
	<script src="${mmsCtx}/js/module-page.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js"> </script>
    <script type="text/javascript">
    
    </script>
</head>
<body >
<div class="ui-layout-center">
<form id="defaultForm" name="defaultForm"action="">
	<input type="hidden" id="menuId"  name="menuId" value="${menuId }"/>
	<input id="page_id" type="hidden" name="pageId"></input>
	<input id="pageIds" name="pageIds" type="hidden"></input>
</form>
	<div class="opt-body">
	<aa:zone name="pageTable">
		<div class="opt-btn">
			<button class="btn" onclick="createView();"><span><span>新建</span></span></button>
			<button class="btn" onclick="updateView();"><span><span >修改</span></span></button>
			<button class="btn" onclick="deleteViews();"><span><span >删除</span></span></button>
			<button class="btn" onclick="defaultDisplaySet();"><span><span >设置/取消默认</span></span></button>
			<button class="btn" onclick="enabelSet();"><span><span >启用/禁用</span></span></button>
		</div>
	   
		<div id="opt-content">
		<aa:zone name="pageTablelist">
			<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
			<form action="${mmsCtx}/form/list-data.htm" name="pageForm" id="pageForm" method="post">
				<view:jqGrid url="${mmsCtx}/module/module-page-list.htm?menuId=${menuId }" code="MMS_MODULE_PAGE" gridId="pageTableId" pageName="page"></view:jqGrid>
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
