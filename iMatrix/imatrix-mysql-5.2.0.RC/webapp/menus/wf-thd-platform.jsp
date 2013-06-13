<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<div id="accordion" class="basic">
	<!-- 
	<h3><a href="workflow.htm">我发起的流程</a></h3>
	<div>
		<div class="demo" id="treeid" style="margin-top: 10px;"></div>
	</div>
	 -->
	<h3><a href="delegate-main!myDelegate.htm">任务委托监控</a></h3>
	<div>
		<div class="demo" id="tree-delegate-monitor" style="margin-top: 10px;"></div>
	</div>	
	<!-- 
	<security:authorize ifAnyGranted="wf_engine_process_definition_collection">
		<h3><a href="workflow!collection.htm">流程汇编</a></h3>
		<div>
			<div class="demo" id="treeid-form" style="margin-top: 10px;"></div>
		</div>
	</security:authorize>
	 -->
	<h3><a href="delegate-main!superviseAsTrustee.htm">任务受托监控</a></h3>
	<div>
		<div class="demo" id="supervise-as-trustee" style="margin-top: 10px;"></div>
	</div>
	
	<h3><a href="delegate-main.htm">委托管理</a></h3>
	<div>
		<div class="demo" id="delegate-tree" style="margin-top: 10px;"></div>
	</div>
</div>
<input id="currentTreeSelectedNode" type="hidden" value="">

<script type="text/javascript">

$(function () {
	$.ajaxSetup({cache:false});
	treeThree();
	});

function treeOne(){
	$("#treeid").bind("select_node.jstree",function(e){
			isFirstNode("treeid");})
		.jstree({
			"json_data":{
				"ajax" : { "url" : "${wfCtx}/engine/tree-myProcess.htm",
							"data" : function (n) {  
							 return { currentId : n!=-1 ? n.attr("id") : "INITIALIZED" };   
						}
					}
			   },
			   "themes" : {  
					 "theme" : "default",  
					 "dots" : true,  
					 "icons" : true 
				},  
				"ui" : {   
					 "select_multiple_modifier" : "alt", 
					"initially_select" : [ "ING" ]  
				},  
					"plugins" : [ "themes", "json_data","ui" ]
		});
}

function treeTwo(){
	$("#delegate-tree").bind("select_node.jstree",function(e){
			isFirstNode("delegate-tree");})
		.jstree({
			"json_data":{
				"data" : [{ attr: { id : "_MY_DELEGATE" }, data: "我的委托" },
					      { attr: { id : "_RECEIVE_DELEGATE" }, data: "我的受托" }]
		   },
		   "themes" : {  
				 "theme" : "default",  
				 "dots" : true,  
				 "icons" : true 
			},  
			"ui" : {    
				 "select_multiple_modifier" : "alt",
				"initially_select" : [ "_MY_DELEGATE" ]  
			},  
				"plugins" : [ "themes", "json_data","ui" ]
	});
}

function treeThree(){
	$("#tree-delegate-monitor").bind("select_node.jstree",function(e){
			isFirstNode("tree-delegate-monitor");})
		.jstree({
			"json_data":{
				"ajax" : { "url" : "${wfCtx}/engine/tree!delegateMonitor.htm",
							"data" : function (n) {  
							 return { currentId : n!=-1 ? n.attr("id") : "DELEGATE_MONITOR" };   
						}
					}
		   },
		   "themes" : {  
				 "theme" : "default",  
				 "dots" : true,  
				 "icons" : true 
			},  
			"ui" : {    
				 "select_multiple_modifier" : "alt",
				"initially_select" : [ "DEL_ING" ]  
			},  
				"plugins" : [ "themes", "json_data","ui" ]
	});	
}

function treeFour(){
	$("#supervise-as-trustee").bind("select_node.jstree",function(e){
			isFirstNode("supervise-as-trustee");})
		.jstree({
			"json_data":{
				"ajax" : { "url" : "${wfCtx}/engine/tree!superviseAsTrusteeTree.htm",
							"data" : function (n) {  
							 return { currentId : n!=-1 ? n.attr("id") : "DELEGATE_MONITOR" };   
						}
					}
		   },
		   "themes" : {  
				 "theme" : "default",  
				 "dots" : true,  
				 "icons" : true 
			},  
			"ui" : {    
				 "select_multiple_modifier" : "alt",
				"initially_select" : [ "TRUSTEE_ING" ]  
			},  
				"plugins" : [ "themes", "json_data","ui" ]
	});	
}
function initTree(){
	$("#treeid-form").bind("select_node.jstree",function(e){
			isFirstNode("treeid-form");})
		.jstree({
			"json_data":{
						"ajax" : { "url" : "${wfCtx}/engine/workflow!createTree.htm",
						"data" : function (n) {  
						 return { currentId : n!=-1 ? n.attr("id") : "asasas" };   
					}
				}
		   },
		   "themes" : {  
				 "theme" : "default",  
				 "dots" : true,  
				 "icons" : true 
			},  
			"ui" : {    
				 "select_multiple_modifier" : "alt",
				"initially_select" : [ 'WFDTYPE_' + '${firstTreeId}' ]  
			},  
				"plugins" : [ "themes", "json_data","ui" ]
	});
}
function treeselected(id,TREE_OBJ){
	$("#selectedMu").attr("value", id);
	treechange(id);
}
function treechange(id){
	var url="";
	if(id.indexOf('ING_WFD_') != -1){//办理中
		var temp=id.split('_')[2];
		url=webRoot+"/engine/workflow.htm?end=false&definitionId="+temp;
	}else if(id.indexOf('END_WFD_') != -1){//已完成
		var temp=id.split('_')[2];
		url=webRoot+"/engine/workflow.htm?end=true&definitionId="+temp;
	}else if(id.indexOf('ING_') != -1){ //办理中
		var temp=id.split('_')[1];
		url=webRoot+"/engine/workflow.htm?end=false&type="+temp;
	}else if(id.indexOf('END_') != -1){ //已完成
		var temp=id.split('_')[1];
		url=webRoot+"/engine/workflow.htm?end=true&type="+temp;
	}else if(id=='ING'){//所有办理中的
		url=webRoot+"/engine/workflow.htm?end=false";
	}else if(id=='END'){//所有已完成
		url=webRoot+"/engine/workflow.htm?end=true";
	}else if(id == '_MY_DELEGATE'){
		url=webRoot+"/engine/delegate-main.htm";
	}else if(id == '_RECEIVE_DELEGATE'){
		url=webRoot+"/engine/delegate-main!receive.htm";
	}else if(id=='DEL_ING'){//任务委托监控/办理中
		url=webRoot+"/engine/delegate-main!myDelegate.htm?isEnd=false";
	}else if(id=='DEL_END'){//任务委托监控/已完成
		url=webRoot+"/engine/delegate-main!myDelegate.htm?isEnd=true";
	}else if(id=='TRUSTEE_ING'){//任务受托监控/办理中
		url=webRoot+"/engine/delegate-main!superviseAsTrustee.htm?isDone=false";
	}else if(id=='TRUSTEE_END'){//任务受托监控/已完成
		url=webRoot+"/engine/delegate-main!superviseAsTrustee.htm?isDone=true";
	}else if(id.indexOf('WFDTYPE_') != -1){//流程汇编（流程类型）
		var temp=id.split('_')[1];
		url=webRoot+"/engine/workflow!collection.htm?type="+temp;
	}else if(id.indexOf('WFDID_') != -1){//流程汇编（具体流程）
		var temp=id.split('_')[1];
		url=webRoot+"/engine/workflow-definition-view.htm?wfdId="+temp;
	}
	$("#myIFrame").attr("src",url);
}

function startProcess(){
	var id = $("#selectedMu").attr("value");
	if(id == "aaa"){
		$("#start_").unbind();
		$("#start_").removeClass("thickbox");
		$("#start_").attr("href", "process-input.html");
	}
}

function isFirstNode(treeid){
	$("#currentTreeSelectedNode").attr('value', $("#"+treeid).find(".jstree-clicked").parent().attr("id"));
	treeselected($("#"+treeid).find(".jstree-clicked").parent().attr("id"));
}
function selectFirstNode(treeId, firstId){
	var lis = $("#"+treeId).children("ul").children("li");
	var isEnd = false;
	for(var i = 0; i < lis.length; i++){
		if($(lis[i]).attr("id") == firstId){
			$(lis[i]).children("a").attr("class","clicked");
			isEnd = true;
			break;
		}
	}
}
$(function () {
	$("#accordion").accordion({fillSpace:true, change:accordionChange});
});
function accordionChange(event,ui){
	var url=ui.newHeader.children("a").attr("href");
	if(url=="workflow.htm"){//我的流程
		if($("#treeid").html()==""){
			treeOne();
		}
	}else if(url=="delegate-main!myDelegate.htm"){//任务委托监控
		if($("#tree-delegate-monitor").html()==""){
			treeThree();
		}else{
		    $("#tree-delegate-monitor").jstree("refresh",-1);
		}
	}else if(url=="delegate-main!superviseAsTrustee.htm"){//任务受托监控（新添加）
		if($("#supervise-as-trustee").html()==""){
			treeFour();
		}
	}else if(url=="workflow!collection.htm"){//流程汇编
		if($("#treeid-form").html()==""){
			initTree();
		}
	}else if(url=="delegate-main.htm"){//委托管理
		if($("#delegate-tree").html()==""){
			treeTwo();
		}
	}
		
	$("#myIFrame").attr("src",url);

}

</script>