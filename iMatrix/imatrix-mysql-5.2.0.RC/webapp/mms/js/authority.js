//创建规则类型树
var currentRuleTypeId;
function createRuleTypeTree(treeId){
	$.ajaxSetup({cache:false});
	$("#"+treeId).bind("select_node.jstree",function(e){
		currentRuleTypeId=$(".jstree-clicked").parent().attr("id");
		}).jstree({
		"json_data":{
				"ajax" : { "url" : webRoot+"/authority/rule-type-tree.htm",
					"data" : function (n) {  
						return { currentRuleTypeId : n!=-1 ? n.attr("id") : 0 };   
					}
				}
	   },
	   "themes" : {  
		  "theme" : "classic",  
		  "dots" : true,  
		  "icons" : true 
		 },
		 "ui":{
			 "select_multiple_modifier" : "alt"
		 },
		 "plugins" : [ "themes", "json_data" ,"ui","crrm"]
	});
}