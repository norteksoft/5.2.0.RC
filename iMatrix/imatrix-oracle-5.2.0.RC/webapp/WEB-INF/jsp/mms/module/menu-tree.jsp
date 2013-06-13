<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>菜单管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	
	<script src="${mmsCtx}/js/menu.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			createMenuTree();
		});

		function exportMenu(){
			ajaxSubmit("defaultForm",  webRoot+"/module/export-menu.htm");
			buttonFlag=0;
		}
		function importMenu(){
			$.colorbox({href:'${mmsCtx}/module/show-import-menu.htm',
				iframe:true, innerWidth:350, innerHeight:100,overlayClose:false,title:"导入菜单"});
		}
		function updatUrlCache(){
			ajaxSubmit("defaultForm",  "${mmsCtx}/module/update-url-cache.htm","",updateUrlCacheCallback);
		}

		function updateUrlCacheCallback(){
			$('#message').css("display","block");
			$('#message').html("<font class=\"onSuccess\"><nobr>更新URL缓存成功</nobr></font>");
			setTimeout('$("#message").css("display","none");',3000);
		}
	</script>
	<style type="text/css">
		.tree ul{
			z-index: 0;
		}
		.tree ul li{
			z-index: 0;
		}
		
	</style>
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
	<div class="opt-body">
	<form action="" method="post" name="defaultForm" id="defaultForm"></form>
		<div class="opt-btn">
			<button class="btn" onclick="createSystem();"><span><span>新建系统</span></span></button>
			<button class="btn" onclick="createMenu();"><span><span >新建菜单</span></span></button>
			<button class="btn" onclick="updatUrlCache();"><span><span >更新URL缓存</span></span></button>
			<button class="btn" onclick="updateMenu();"><span><span >修改</span></span></button>
			<button class="btn" onclick="enableMenu();"><span><span >启用</span></span></button>
			<button class="btn" onclick="disableMenu();"><span><span >禁用</span></span></button>
			<button class="btn" onclick="deleteMenu();"><span><span >删除</span></span></button>
			<button class="btn" onclick="exportMenu();"><span><span >导出</span></span></button>
			<button class="btn" onclick="importMenu();"><span><span >导入</span></span></button>
		</div>
		<div id="opt-content">
			<div id="message" style="display:none; "></div>
			<table >
				<tr >
					<td >
						<div class="demo" id="menu-tree" style="background: none;"></div>
					</td>
				</tr>
			</table>
		</div>
	</div>
</div>
</body>
</html>
