<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	
	<title>定时设置</title>
	<script type="text/javascript">
	function submitImport(url){
		if($("#file").val()==''){
			alert('请选择导入的文件。');
			return;
		}
		
		$("#submitImportForm").attr("action",url);
		$("#submitImportForm").ajaxSubmit(function (id){
			id=id.replace("<pre>","").replace("</pre>","");
			id=id.replace("<PRE>","").replace("</PRE>","");
			id=id.replace("<pre style=\"word-wrap: break-word; white-space: pre-wrap;\">","");
			alert(id);
			window.parent.location="${settingCtx}/options/import-definition.htm";
		});
	}
	</script>
</head>
<body onload="">
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<button class="btn" type="button" onclick="submitImport('${settingCtx}/options/import-definition-shift.htm');"><span><span>导入</span></span></button>
	</div>
	<div id="opt-content" >
		<form id="submitImportForm" name="submitImportForm" action="" method="post" enctype="multipart/form-data">
			<input type="hidden" name="importDefinitionId" value="${importDefinitionId }"/>
			<input type="file" name="file" id="file"/>
		</form>
	</div>
</div>
</div>
</body>
</html>