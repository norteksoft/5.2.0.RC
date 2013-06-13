<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>

<script type="text/javascript">
	function treeselected(currentId){
		//$("#selectedMu").attr("value", $(NODE).attr("id"));
		treechange(currentId);
	}
	function startProcess(){
		var id = $("#selectedMu").attr("value");
		if(id == "aaa"){
			$("#start_").unbind();
			$("#start_").removeClass("thickbox");
			$("#start_").attr("href", "process-input.html");
		}
	}

	$(document).ready(function () { 
		createTree();
	});

	function createTree(){
		$.ajaxSetup({cache:false});
		 if(thirdMenu == 'type-manager'){
			$("#type-tree").tree({
				ui : {
				context:[]
				},
				  data  : {
				    type  : "json",
				    json  : [
				      { attributes: { id : "form_def" }, data: "类型定义" }
				    ]
				  },
				  callback : {
			        	onselect    : function(NODE,TREE_OBJ) { treeselected(NODE,TREE_OBJ); },
				    	onload	: function(TREE_OBJ){selectDefaultNode('type-tree', 'form_def'); }
					}
				});
		}else if(thirdMenu == 'option-group'){
			$("#option-group-tree").tree({
				data : {
					type : "json",
					json : [ {attributes : { id : "option-def" }, data : "选项组维护" } ]
				},
				callback : {
					onselect : function(NODE,TREE_OBJ) { treeselected(NODE,TREE_OBJ); },//在节点选中时执行的方法
					onload : function(TREE_OBJ){selectDefaultNode('option-group-tree', 'option-def'); }//在加载时执行的方法
				}
			});
		}
	}
	
	function selectNode(treeId, currentId){
		var liss = $(".leaf");
			for(var i = 0; i < liss.length; i++){
	    		if($(liss[i]).attr("id") == currentId){
					$(liss[i]).children("a").addClass("clicked");
				}else{
					$(liss[i]).children("a").removeClass("clicked");
				}
	   		}
		treeselected(currentId);
	}
	function selectDefaultNode(treeId, currentId){
		$("#"+currentId).children("a").attr("class","clicked");
		if(currentId != "option-def"){
			treeselected(currentId);
		}
	}
</script>

<div id="col1" style="width: 150px;">
  	<div id="accordion" >
		<h3><a href="${ctx}/type/basic-type.htm" id="type-manager">类型管理</a></h3>
		<div>
			<div class="demo" id="type-tree" style="margin-top: 10px;"></div>
		</div>
		<h3><a href="${ctx}/options/option-group.htm" id="option-group">选项组管理</a></h3>
		<div>
			<div class="demo" id="option-group-tree" style="margin-top: 10px;"></div>
		</div>
	</div>
</div>

<link type="text/css" href="${ctx}/widgets/ui/ui.all.css" rel="stylesheet" />
<link type="text/css" href="${ctx}/widgets/ui/jquery-ui.css" rel="stylesheet" />
<link type="text/css" href="${ctx}/widgets/ui/demos.css" rel="stylesheet" />

<script type="text/javascript" src="${ctx}/widgets/tree/_lib/css.js"></script>
<link rel="stylesheet" type="text/css" href="${ctx}/widgets/tree/tree_component.css" />
<script type="text/javascript" src="${ctx}/widgets/tree/tree_component.js"></script>
	
<script type="text/javascript" src="${ctx}/widgets/ui/ui.core.js"></script>
<script type="text/javascript" src="${ctx}/widgets/ui/ui.accordion.js"></script>

<script type="text/javascript">
$(document).ready(function() {
	initAccordion();
});

function initAccordion(){
	$("#accordion").accordion({
		fillSpace: true,
		active: getIndex('#accordion'),
		change: function(event, ui) {
			location.href=$($(ui.newHeader[0]).children()[1]).attr('href');
		}
	});
}
function getIndex(id){
	var subs = $(id).children("h3");
	for(var i = 0; i < subs.length; i++){
		var hs0 = $($(subs[i]).children('a')[0]).attr('id');
			if(thirdMenu==hs0){
				return i;
			}
	}
	return 0;
}
</script>

