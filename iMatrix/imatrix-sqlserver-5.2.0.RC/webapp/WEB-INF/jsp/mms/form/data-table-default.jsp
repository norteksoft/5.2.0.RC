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
					ajaxSubmit("contentFrom", webRoot+"/form/data-table-deleteCustom.htm", "dataTablelist");
				}
			}
		}

		//删除一个数据表信息
		function deleteTableInfo(url){
			var ids = jQuery("#dataTables").getGridParam('selarrrow');
			if(ids==''){
				showMessage("message", "<font color=\"red\">请选择一条数据</font>");
			}else{
				var canPost = true;
				$.each(ids, function(i){
					var id = ids[i];
					var state=jQuery("#dataTables").jqGrid("getCell",id,"tableState");
					if(state!= "DRAFT"){
						showMessage("message", "<font color=\"red\">不能删已启用和禁用的数据表</font>");
						canPost = false;
					}
				});
				if(canPost){
					if(confirm("确定删除吗？")){
						$.each(ids, function(i){
							$("#contentFrom").append(createHiddenInput("tableIds", ids[i]));
						});
						setPageState();
						ajaxSubmit("contentFrom", url, "dataTablelist");
					}
				}
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
				<!-- <button class="btn" onclick="deleteTableInfo('${mmsCtx}/form/data-table-deleteCustom.htm');"><span><span >删除</span></span></button>
				 -->
				<button class="btn" onclick="deleteEnableTableInfo();"><span><span >删除已启用</span></span></button>
			</div>
			<span id="searchContent"></span>
			<div id="opt-content">
				<aa:zone name="dataTablelist">
					<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
					<form action="" name="pageForm" id="pageForm" method="post">
						<view:jqGrid url="${mmsCtx}/form/data-table-defaultDataTableList.htm?menuId=${menuId }" code="MMS_CUSTOM_DATA_TABLE" gridId="dataTables" pageName="dataTables"></view:jqGrid>
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