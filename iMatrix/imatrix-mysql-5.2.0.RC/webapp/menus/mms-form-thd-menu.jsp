<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<div id="accordion" >
	<h3><a href="list-data.htm" id="form-manager">表单管理</a></h3>
	<div>
		<div class="demo" id="form_manage_content" style="margin-top: 10px;"></div>
	</div>
		
	<h3><a href="list-view.htm" id="list-manager">列表管理</a></h3>
	<div>
		<div class="demo" id="list_manage_content" style="margin-top: 10px;"></div>
	</div>
	<h3><a href="data-table.htm" id="datatable-manager">数据表管理</a></h3>
	<div>
		<div class="demo" id="data_table_manage_content" style="margin-top: 10px;"></div>
	</div>
</div>

<script type="text/javascript">
	$(function () {
		$("#accordion").accordion({fillSpace:true, change:accordionChange});
	});
	function accordionChange(event,ui){
		var url=ui.newHeader.children("a").attr("href");
		if(url=="list-data.htm"){
			if($("#form_manage_content").html()==""){
				createViewTree("form_manage_content");
			}else{
				$("#form_manage_content").jstree("deselect_all");
			}
		}else if(url=="list-view.htm"){
			if($("#list_manage_content").html()==""){
				createViewTree("list_manage_content");
			}else{
				$("#list_manage_content").jstree("deselect_all");
			}
		}else if(url=="data-table.htm"){
			if($("#data_table_manage_content").html()==""){
				createViewTree("data_table_manage_content");
			}else{
				$("#data_table_manage_content").jstree("deselect_all");
			}
		}
		$("#myIFrame").attr("src",url);
	}
</script>
