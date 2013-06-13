<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>表单管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	
	<script type="text/javascript" src="${mmsCtx}/js/dataTable.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js" ></script>
	
	<script type="text/javascript">
		$(document).ready(function() {
			$("#men_id").attr("value",$("#menuId").val());
		});
		function exportDataTable(){
			$("#defaultForm").find("input[name='tableIds']").remove();
			var ids = jQuery("#dataTables").getGridParam('selarrrow');
			if(ids==''){
				if(confirm("是否导出当前系统下的所有数据表?")){
					$("#defaultForm").attr("action",webRoot+"/form/export-data-table.htm");
					$("#defaultForm").submit();
				}
			}else{
				$.each(ids, function(i){
					$("#defaultForm").append(createHiddenInput("tableIds", ids[i]));
				});
				$("#defaultForm").attr("action",webRoot+"/form/export-data-table.htm");
				$("#defaultForm").submit();
			}
		}
		function importDataTable(){
			$.colorbox({href:'${mmsCtx}/form/show-import-data-table.htm',
				iframe:true, innerWidth:350, innerHeight:100,overlayClose:false,title:"导入数据表"});
		}

		function dataTableList(){
			ajaxSubmit("defaultForm",  webRoot+"/form/data-table-list-data.htm","dataTablelist");
		}
		function deleteEnableTableInfo(){
			var ids = jQuery("#dataTables").getGridParam('selarrrow');
			if(ids==''){
				showMessage("message", "<font color=\"red\">请选择一条数据</font>");
			}else{
				if(confirm("将删除关联的列表、表单、页面，确定删除吗？")){
					$.each(ids, function(i){
						$("#contentFrom").append(createHiddenInput("tableIds", ids[i]));
					});
					$("#contentFrom").append(createHiddenInput("deleteEnable", true));
					ajaxSubmit("contentFrom", webRoot+"/form/data-table!delete.htm", "dataTablelist");
					$("#contentFrom").find("input[name='tableIds']").remove();
					$("#contentFrom").find("input[name='deleteEnable']").remove();
				}
			}
		}
		function generateCode(){
			var ids = jQuery("#dataTables").getGridParam('selarrrow');
			if(ids==""){
				showMessage("message", "<font color=\"red\">请选择数据</font>");
			}else if(ids.length >= 1){
				$("#contentFrom").attr("action",webRoot+"/form/generate-code.htm?ids="+ids);
				$("#contentFrom").submit();
			}
		}
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:250px;
	}
	</style>
</head>
<body>
<div class="ui-layout-center">
<form action="" method="post" name="defaultForm" id="defaultForm"  method="post" target="_blank">
<input type="hidden" id="menu_Id"  name="menuId" value="${menuId }"/>
</form>
<form id="defaultDataTableForm" name="defaultDataTableForm" action="">
	<input type="hidden" id="menuId"  name="menuId" value="${menuId }"/>
	<input id="page_id" type="hidden" name="pageId"></input>
	<input id="pageIds" name="pageIds" type="hidden"></input>
</form>
<div class="opt-body">
<aa:zone name="form_main">
	<aa:zone name="dataTableContent">
		<s:if test="menuId!=null ">
			<form id="contentFrom" name="contentFrom" method="post">
				<input type="hidden" name="states" value="all"/>
				<input type="hidden" id="men_id"  name="menuId" value="${menuId }"/>
			</form>
			<div class="opt-btn">
				<button class="btn" onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
				<button class="btn" onclick="createNewTable('${mmsCtx}/form/data-table-input.htm', createCallBack);"><span><span >新建</span></span></button>
				<button class="btn" onclick="changeTableInfo('${mmsCtx}/form/data-table-input.htm', createCallBack);"><span><span >修改</span></span></button>
				<button class="btn" onclick="changeTableStates();"><span><span >启用/禁用</span></span></button>
				<button class="btn" onclick="deleteTableInfo('${mmsCtx}/form/data-table!delete.htm');"><span><span >删除</span></span></button>
				<button class="btn" onclick="exportDataTable();"><span><span >导出</span></span></button>
				<button class="btn" onclick="importDataTable();"><span><span >导入</span></span></button>
				<button class="btn" onclick="deleteEnableTableInfo();"><span><span >删除已启用</span></span></button>
				<button class="btn" onclick="generateCode();"><span><span >代码生成</span></span></button>
			</div>
			<span id="searchContent"></span>
			<div id="opt-content">
				<aa:zone name="dataTablelist">
					<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
					<form action="" name="pageForm" id="pageForm" method="post">
						<view:jqGrid url="${mmsCtx}/form/data-table-list-data.htm?menuId=${menuId }" code="MMS_DATA_TABLE" gridId="dataTables" pageName="dataTables"></view:jqGrid>
					</form>
				</aa:zone>
			</div>
		</s:if>
	</aa:zone>
</aa:zone>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>