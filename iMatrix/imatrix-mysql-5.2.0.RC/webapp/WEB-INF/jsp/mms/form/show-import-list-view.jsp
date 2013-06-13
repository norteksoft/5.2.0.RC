<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>系统元数据管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	
	<style type="text/css">
		.actionMessage{ list-style-type: none; color: red;}
	</style>
	<script type="text/javascript">
		function submitbutt(){
			if($("#filename").val()==''){
				alert('请选择导入的文件。');
				return;
			}
			$("#importForm").ajaxSubmit(function (id){
				id=id.replace("<pre>","").replace("</pre>","");
				id=id.replace("<PRE>","").replace("</PRE>","");
				id=id.replace("<pre style=\"word-wrap: break-word; white-space: pre-wrap;\">","");
				alert(id);
				refereshParentPage();
			});
		}
		function refereshParentPage(){
			window.parent.listViewList(); 
			window.parent.$.colorbox.close();
		}
	</script>
</head>
<body >
<div class="ui-layout-center">
	<form id="importForm" name="importForm" action="${mmsCtx}/form/import-list-view.htm" method="post" enctype="multipart/form-data">
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
