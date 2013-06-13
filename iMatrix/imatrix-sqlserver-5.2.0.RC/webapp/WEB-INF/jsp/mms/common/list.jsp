<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>${modulePage.name }</title>
	<%@ include file="/common/mms-meta.jsp"%>
	
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/jqgrid/ui.jqgrid.css" />
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/colorbox/colorbox.css" />
	<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
	
	<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
	
	<!--上传js-->
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/swfupload.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/handlers.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/otherHandlers.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/wf/js/util.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/wf/js/text.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/wf/js/opinion.js"></script>
	
	<script src="${imatrixCtx}/wf/js/workflowTag.js"></script>
	<script src="${imatrixCtx}/mms/js/form-view.js"></script>
	<script src="${mmsCtx}/js/mmsapi.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/mms/js/mms-workflow.js" type="text/javascript"></script>
	<script src="${resourcesCtx}/js/form.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js" ></script>
	
</head>
<body onclick="$('#sysTableDiv').hide();$('#styleList').hide();" >
	<script type="text/javascript">
		var thirdMenu = "mmm_t_th";
	</script>
  	<%@ include file="/menus/header.jsp"%>
	<div id="secNav">
		<menu:secondMenu></menu:secondMenu>
		<div class="hid-header" onclick="headerChange(this);" title="隐藏"></div>
	</div>
	<div class="ui-layout-west">
		<menu:leftMenu></menu:leftMenu>
	</div>
	<div class="ui-layout-center">
	  <div class="opt-body">
	  		<form id="default_common_form" name="default_common_form" method="post">
				<input type='hidden' name="pageId" id="____pageId">
			</form>
			<aa:zone name="default_refresh_zone">
			    <input name="deleteMsg" id="deleteMsg" type="hidden" value="${deleteMsg}"/>
				<form id="default_refresh_form" name="default_refresh_form" method="post">
					<input type='hidden' name="pageId" value="${pageId}" id="_pageId">
				</form>
				<button:button code="${modulePage.code}"></button:button> 
				<div id="opt_message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
				<div id="opt-content" >
					<form id="default_list_form" name="default_list_form" method="post" action="">
						<input type='hidden' name="pageId" value="${pageId}" id="pageId">
						<input id="dataId" name="dataId" type="hidden"/>     
						<s:if test="listCode==null">
							<p>没有为该菜单定义页面</p>
							<p>&nbsp;</p>
							<p>请在 【页面管理】中，为该菜单设置页面</p>
						</s:if><s:else>
							<view:jqGrid url="${mmsCtx}/common/list.htm?menuId=${menuId }" pageName="page" code="${listCode}" gridId="main_table" submitForm="default_common_form"></view:jqGrid>
						</s:else>
					</form>
				</div>	
			</aa:zone>
	</div>
  </div>	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
