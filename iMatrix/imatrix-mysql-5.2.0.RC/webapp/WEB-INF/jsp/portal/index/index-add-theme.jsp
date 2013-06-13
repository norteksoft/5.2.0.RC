<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/common/portal-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>注册小窗体</title>
	<%@ include file="/common/portal-meta.jsp"%>
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/jqgrid/ui.jqgrid.css" />
	
	<script type="text/javascript" src="${resourcesCtx}/widgets/jqgrid/jqgrid-all-1.0.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/jqgrid/jqGrid.custom.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/public.js"></script>
	
	<script src="${portalCtx}/js/index.js" type="text/javascript"></script>
	<script type="text/javascript">
		function changeThemeState(url){
			var ids = jQuery("#themeTable").getGridParam('selarrrow');
			if(ids.length<=0){
				alert("请选择记录");
			}else {
				$.ajax({
					data:{ids:ids.join(",")},
					type:"post",
					url:url,
					beforeSend:function(XMLHttpRequest){},
					success:function(data, textStatus){
						$("#message").show();
						$("#message").html('<font class="onSuccess"><nobr>'+data+'</nobr></font>');
						setTimeout('$("#message").hide("show");',3000);
					},
					complete:function(XMLHttpRequest, textStatus){
						jQuery("#themeTable").trigger("reloadGrid");
					},
			        error:function(){

					}
				});
			}
		}
	</script>
</head>
<body>
<form action="" name="defaultForm" id="defaultForm" method="post">
</form>
	<div class="ui-layout-center">
		<div class="opt-body">
			<aa:zone name="widget-zones">
				<div class="opt-btn">
					<button  class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
					<button class='btn' onclick="iMatrix.addRow();"><span><span>新建</span></span></button>
					<button class='btn' onclick="iMatrix.delRow();"><span><span>删除</span></span></button>
					<!--  
					<button class='btn' onclick="changeThemeState('${portalCtx}/index/change-theme-state.htm');"><span><span>启用/禁用</span></span></button>
					-->
				</div>
				<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
				<div id="opt-content">
					<form action="" name="pageForm" id="pageForm" method="post">
						<grid:jqGrid url="${portalCtx}/index/add-theme.htm" code="PORTAL_THEME" gridId="themeTable" pageName="themePage" ></grid:jqGrid>
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