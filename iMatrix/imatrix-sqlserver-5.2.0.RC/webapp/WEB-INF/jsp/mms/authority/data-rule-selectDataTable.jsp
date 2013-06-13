<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>数据表</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js"></script>
	<script type="text/javascript">
		function submitDataRule(){
			var id = jQuery("#dataTableGrid").getGridParam('selrow');
			if(id==null){
				alert("请选择一条数据！");
			}else{
				var dataTableName=window.parent.$("#dataTableName").val();
				var sign=false;
				if(dataTableName != ''){
					if(confirm("确定修改数据表吗？所选择的规则条件将被删除！")){
						window.parent.jQuery("#conditionGrid").jqGrid('clearGridData');
						window.parent._add_row('conditionGrid');
						sign=true;
					}
				}else{
					sign=true;
				}
				if(sign){
					window.parent.$("#dataTableName").attr("value",jQuery("#dataTableGrid").jqGrid('getCell',id,"alias"));
					window.parent.$("#dataTableId").attr("value",id);
					window.parent.$("#dataTableMenuId").attr("value",jQuery("#dataTableGrid").jqGrid('getCell',id,"menuId"));
				}
				window.parent.$.colorbox.close();
			}
		}
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:200px;
	}
	</style>
</head>
<body >
<div class="ui-layout-center">
<div class="opt-body">
	<form id="defaultForm" name="defaultForm"action="" method="post" ></form>
	<aa:zone name="main_zone">
		<div class="opt-btn">
			<button class="btn" onclick="submitDataRule();"><span><span >确定</span></span></button>
		</div>
		<div id="opt-content">
			<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
			<form action="${mmsCtx}/authority/data-rule-selectDataTable.htm" name="pageForm" id="pageForm" method="post">
				<view:jqGrid url="${mmsCtx}/authority/data-rule-selectDataTable.htm" code="MMS_DATA_RULE_DATA_TABLE" gridId="dataTableGrid" pageName="dataRulePage"></view:jqGrid>
			</form>
		</div>
	</aa:zone>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
