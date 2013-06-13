<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>

<div id="accordion" >
	<h3><a href="${mmsCtx}/module/menu-tree.htm" id="menu_manage">菜单管理</a></h3>
	<div>
		<div class="demo" id="menu_manage_content" style="margin-top: 10px;"></div>
	</div>
	<h3><a href="${mmsCtx}/module/operation.htm" id="operation_manage">通用类别管理</a></h3>
	<div>
		<div class="demo" id="operation_manage_content" style="margin-top: 10px;"></div>
	</div>
</div>

<script type="text/javascript">
	$(function () {
		$("#accordion").accordion({fillSpace:true, change:accordionChange});
	});
	function accordionChange(event,ui){
		var url=ui.newHeader.children("a").attr("href");
		if(url=="${mmsCtx}/module/menu-tree.htm"){
			$("#myIFrame").attr("src",ui.newHeader.children("a").attr("href"));
		}else{
			if($("#operation_manage_content").html()==""){
				createSystemTree("operation_manage_content");
			}else{
				$("#operation_manage_content").jstree("deselect_all");
			}
			$("#myIFrame").attr("src",ui.newHeader.children("a").attr("href"));
		}
	}

	//创建页面树菜单
	function createSystemTree(treeId,url){
		$.ajaxSetup({cache:false});
		$("#"+treeId).bind("loaded.jstree",function(e){
		}).bind("select_node.jstree",function(e){
			var clickNodes=$("#"+treeId).find(".jstree-clicked");
			var node;
			if(clickNodes.length>0){
				node=clickNodes[0];
			}
			selectNode( $(node).parent().attr("id"),treeId);
		}).jstree({
			"json_data":{
					"ajax" : { "url" : "${mmsCtx}/module/operation-system-tree.htm",
						"data" : function (n) {  
							return {};   
						}
					}
		   },
		   "ui":{ "select_multiple_modifier" : "alt"},
		   "themes" : {  
			  "theme" : "default",  
			  "dots" : true,  
			  "icons" : true 
			 },
			 "plugins" : [ "themes", "json_data" ,"ui"]
		});
	}
	function selectNode(currentId,treeId){
		$("#myIFrame").attr("src","${mmsCtx}/module/operation.htm?systemId="+currentId);
	}
</script>

