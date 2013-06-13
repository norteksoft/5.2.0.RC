<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>表单管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>

	<script type="text/javascript" src="${resourcesCtx }/widgets/jstree/jquery.jstree.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/multiselect/jquery.multiselect.min.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/format.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/multiselect/jquery.multiselect.css" />
	
	<script src="${mmsCtx}/js/list-view.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${mmsCtx}/js/list-column.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${mmsCtx}/js/group-header.js" type="text/javascript" charset="UTF-8"></script>
	
	<script type="text/javascript">
		function back(){
			ajaxSubmit("defaultForm",webRoot+"/form/list-view.htm", "viewTable");
		}		
		function exportListView(){
			var ids = jQuery("#page").getGridParam('selarrrow');
			if(ids==''){
				if(confirm("是否导出当前系统下的所有列表?")){
					$("#exportForm").attr("action",webRoot+"/form/export-list-view.htm");
					$("#exportForm").submit();
				}
			}else{
				$("#view_Ids").attr("value",ids.join(","));
				$("#exportForm").attr("action",webRoot+"/form/export-list-view.htm");
				$("#exportForm").submit();
			}
		}
		function importListView(){
			$.colorbox({href:'${mmsCtx}/form/show-import-list-view.htm',
				iframe:true, innerWidth:350, innerHeight:100,overlayClose:false,title:"导入列表"});
		}

		function listViewList(){
			ajaxSubmit("exportForm",  webRoot+"/form/list-view-list-data.htm","viewTable");
		}

		function contentResizeCallback(){
			var w=$('#tabs-1').width();
			jQuery("#listColumnId").jqGrid('setGridWidth',w-3);
		}
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:400px;
	}
	</style>
</head>
<body>
<div class="ui-layout-center">
<form id="exportForm" name="exportForm"action="" method="post" target="_blank">
	<input type="hidden" id="menu_Id"  name="menuId" value="${menuId }"/>
	<input id="view_Ids" name="viewIds" type="hidden"></input>
</form>
<form id="defaultForm" name="defaultForm"action="" method="post" >
	<input type="hidden" id="menuId"  name="menuId" value="${menuId }"/>
	<input id="view_id" type="hidden" name="viewId" value="${viewId }"></input>
	<input id="viewIds" name="viewIds" type="hidden"></input>
</form>
<div class="opt-body">
	<aa:zone name="viewList">
		<s:if test="menuId!=null">
			<div class="opt-btn">
				<button class="btn" onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
				<button class="btn" onclick="copyView();"><span><span >复制</span></span></button>
				<button class="btn" onclick="updateView();"><span><span >修改</span></span></button>
				<button class="btn" onclick="deleteViews();"><span><span >删除</span></span></button>
				<button class="btn" onclick="defaultDisplaySet();"><span><span >设置默认</span></span></button>
				<button class="btn" onclick="exportListView();"><span><span >导出</span></span></button>
				<button class="btn" onclick="importListView();"><span><span >导入</span></span></button>
				<button class="btn" onclick="deleteEnableListView();"><span><span >删除已启用</span></span></button>
			</div>
			<aa:zone name="viewTable">
				<div id="opt-content">
					<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
					<form action="" name="pageForm" id="pageForm" method="post">
						<view:jqGrid url="${mmsCtx}/form/list-view-list-data.htm?menuId=${menuId }" code="MMS_VIEW" gridId="page" pageName="page"></view:jqGrid>
					</form>
				</div>
			</aa:zone>
		</s:if>
</aa:zone>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
