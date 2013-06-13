<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<style type="text/css">
		.actionMessage{ list-style-type: none; color: red;}
		.form-table-without-border TD INPUT{ width: 300px; }
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
			parent.parent.jQuery.jstree._reference("#company_user").destroy();
			parent.parent.initUserTree();
			window.parent.jQuery("#user_table").trigger("reloadGrid"); 
			window.parent.$.colorbox.close();
			
		}
	</script>
	</head>
	<body>
	<div class="ui-layout-center" >
	<div class="opt-body">
		<div class="opt-btn">
			<button class="btn" onclick="submitbutt();"><span><span>确定</span></span></button>
		</div>
		<div id="opt-content">
		    <div style="display: none;" id="message"><s:actionmessage theme="mytheme" /></div>
		    <div style="display: none;" id="_msg" class="onError"></div>
			<form id="importForm" name="importForm" action="${acsCtx}/organization/user!importUser.action" method="post" enctype="multipart/form-data">
			<table class="form-table-without-border" >
				<tr>
					<td><input type="file" id="filename" name="file" /></td>
				</tr>
			</table>
			</form>
	   </div>
	</div>
	</div>
	</body>
</html>