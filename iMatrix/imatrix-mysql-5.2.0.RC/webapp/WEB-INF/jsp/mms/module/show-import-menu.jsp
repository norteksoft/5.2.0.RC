<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>页面管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	
	<script type="text/javascript" src="${resourcesCtx }/widgets/jstree/jquery.jstree.js"></script>
	
	<script src="${mmsCtx}/js/menu.js" type="text/javascript"></script>
	<style type="text/css">
		.actionMessage{ list-style-type: none; color: red;}
	</style>
	<script type="text/javascript">
		$().ready(function(){
			if($($('.actionMessage').children('li')).html() != null){
				if($($('.actionMessage').children('li')).html().indexOf('导入成功') > 0){
					setTimeout('refereshParentPage();',1200);
				}else{
					setTimeout('$(".actionMessage").hide("show");',3000);
				}
			}
		});
		function refereshParentPage(){
			window.parent.jQuery("#menu-tree").jstree("refresh",-1); 
			window.parent.$.colorbox.close();
		}
		function submitbutt(){
			if($("#filename").val()==""){
				_showMessage('_msg', "请选择导入的文件!");
			}else{
				$('#importForm').submit();
			}
		}
		function _showMessage(id, msg){
			if(msg != ""){
				$("#"+id).html(msg);
			}
			$("#"+id).show("show");
			$("."+id).show();
			setTimeout('$("#'+id+'").hide("show");$(".'+id+'").hide();',3000);
		}
	</script>
</head>
<body >
<div class="ui-layout-center">
	<form id="importForm" name="importForm" action="${mmsCtx}/module/import-menu.htm" method="post" enctype="multipart/form-data">
		<p style="padding-top: 8px;text-align: center;">
			<input type="file" id="filename" name="file"/>
			<a href="#" onclick="submitbutt();" title="确定"  class="small-btn"><span><span>确定</span></span></a>
		</p>
	</form>
	<s:actionmessage />
	<ul class="_msg" style="display: none;list-style-type: none; margin-top: 6px;">
		<li> <span id="_msg" style="color: red;display: none;list-style-type: none;"></span> </li>
	</ul>
</div>
</body>
</html>
