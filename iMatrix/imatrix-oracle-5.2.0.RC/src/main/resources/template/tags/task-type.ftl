<div id="taskType" class="demo"></div>
<script type="text/javascript">
//初始化树
$(document).ready(function (){
	loadTree();
});
//树脚本
function loadTree(){     
	$.ajaxSetup({cache:false});
	$("#taskType").jstree({
		"json_data":{
				"ajax" : { "url" : "${taskCtx}/task/task-type-tree-portal.htm?companyId=${companyId}&userId=${userId}",
							"data" : function (n) {  
							}
						}
		   },
		   "themes" : {  
			  "theme" : "classic",  
			  "dots" : true,  
			  "icons" : true 
			 },
			 "ui":{ "select_multiple_modifier" : "alt"},
		   "plugins" : [ "themes", "json_data","ui" ]
		}).bind("select_node.jstree",function(e){
				selectNode();});
}

function selectNode(){
	var nodeId=$("#taskType").find(".jstree-clicked").parent().attr("id");
	var taskType=$("#taskType").find(".jstree-clicked").parent().parent().parent().attr("id");
	if(nodeId=="complete_task"||nodeId=="active_task"||nodeId=="cancel_task")nodeId="";
	location.href=encodeURI("${taskCtx}/task/task.htm?typeName="+nodeId+"&completed=false&currentNodeId="+nodeId+"&taskType="+taskType);
}
</script>