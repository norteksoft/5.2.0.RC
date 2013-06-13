<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>委托管理</title>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
	
	<script type="text/javascript" src="${resourcesCtx}/widgets/tree/_lib/css.js"></script>
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/tree/tree_component.css" />
	<script type="text/javascript" src="${resourcesCtx}/widgets/tree/tree_component.js"></script>
	<script src="${wfCtx}/js/map.js" type="text/javascript"></script>
	
	<style type="text/css">
		a{text-decoration:none;}
	</style>
	<script type="text/javascript">
	$(function () {
		processTree();
	});

	function processTree(){
		$.ajaxSetup({cache:false});
		$("#process_tree").jstree({
					"json_data":{
						"ajax" : { "url" : "${wfCtx}/engine/data-dictionary!createProcessTree.htm",
									"data" : function (n) {  
										 return { currentId : n!=-1 ? n.attr("id") : 0 };   
									}
								}
					   },
					   "themes" : {  
						 "theme" : "classic",  
						 "dots" : true,  
						 "icons" : true 
						},
						 "types" :{ 
							"types" : {
								"root" : {
									"icon" : {
										"image" : "${wfCtx}/images/root.png"
									}
								},
								"process" : {
									"icon" : {
										"image" : "${wfCtx}/images/folder.png"
									}
								},
								"tache" : {
									"icon" : {
										"image" : "${wfCtx}/images/file.png"
									}
								}
							}
						 }, 
					   "plugins" : [ "themes", "json_data","checkbox","types"]
		});
	}
	function submitProcesses(){
		window.parent.addProcessTb();
		var lists = $("#process_tree").find("li.jstree-checked");
		if(lists.length <= 0){
			alert('请选择数据');
			return;
		}
		var process=getProcess(lists);
		var treeId1=$(lists[0]).attr("id");
		if(treeId1.substring(0,treeId1.indexOf("_"))=="all"){
			for(var j=0;j<process.length;j++){
				var processId=$(process[j]).attr("id");
				var info=processId.substring(processId.lastIndexOf("_")+1,processId.length);
				var viewInfo=processId.substring(processId.indexOf(";")+1,processId.length);
				window.parent.addProcess(info,viewInfo);
			}
		}else{
			var myProcessMap=new Map();
			var tache=getTache(lists);
			for(var i=0;i<tache.size();i++){
				var tacheId = $(tache.element(i).value).attr("id");
				for(var j=0;j<process.length;j++){
					var processId=$(process[j]).attr("id");
					if(tacheId.substring(tacheId.indexOf("_")+1,tacheId.indexOf("["))==processId){
						tache.remove(tache.element(i).key);
						i=i-1;
						if(!myProcessMap.containsKey(processId)){
							myProcessMap.put(processId,process[j]);
						}
						break;
					}
				}
			}
			var noTacheProcess=getNoTacheProcess(lists);
			for(var j=0;j<noTacheProcess.length;j++){
				var processId=$(noTacheProcess[j]).attr("id");
				myProcessMap.put(processId,noTacheProcess[j]);
			}
			for(var i=0;i<tache.size();i++){
				var tacheId = $(tache.element(i).value).attr("id");
				var info=tacheId.substring(tacheId.lastIndexOf("_")+1,tacheId.indexOf("]")+1);
				var viewInfo=tacheId.substring(tacheId.indexOf(";")+1,tacheId.indexOf("]")+1);
				window.parent.addProcess(info,viewInfo);
			}
			for(var i=0;i<myProcessMap.size();i++){
				var processId = $(myProcessMap.element(i).value).attr("id");
				var info=processId.substring(processId.lastIndexOf("_")+1,processId.length);
				var viewInfo=processId.substring(processId.indexOf(";")+1,processId.length);
				window.parent.addProcess(info,viewInfo);
			}
		}
		window.parent.$("#info").focus();
		window.parent.$.colorbox.close();
	}
//获得所有流程节点
	function getProcess(lists){
		var process=new Array();
		for(var i=0; i<lists.length; i++){
			var treeId = $(lists[i]).attr("id");
			if(treeId.substring(0,treeId.indexOf("_"))=="process"){
				process.push(lists[i]);
			}
		}
		return process;
	}
//获得所有环节节点
	function getTache(lists){
		var tache=new Map();
		for(var i=0; i<lists.length; i++){
			var treeId = $(lists[i]).attr("id");
			if(treeId.substring(0,treeId.indexOf("_"))=="tache"){
				var tacheName=treeId.substring(treeId.indexOf(";")+1,treeId.indexOf("]")+1);
				tache.put(tacheName,lists[i]);
			}
		}
		return tache;
	}
	//获得没有环节的流程
	function getNoTacheProcess(lists){
		var process=new Array();
		for(var i=0; i<lists.length; i++){
			var treeId = $(lists[i]).attr("id");
			if(treeId.substring(0,treeId.indexOf("_"))=="process"){
				var hasTache=false;
				var processId=$(lists[i]).attr("id");
				for(var j=0; j<lists.length; j++){
					var tacheTreeId = $(lists[j]).attr("id");
					if(tacheTreeId.substring(0,tacheTreeId.indexOf("_"))=="tache"){
						var tacheId=$(lists[j]).attr("id");
						if(tacheId.substring(tacheId.indexOf("_")+1,tacheId.indexOf("["))==processId){
							hasTache=true;
							break;
						}
					}
				}
				if(!hasTache){
					process.push(lists[i]);
				}
			}
		}
		return process;
	}
	</script>
</head>
<body style="padding: 0px 10px;">
<div class="ui-layout-center">
	<aa:zone name="dict_zone">
		<div class="opt-btn">
			<button  class='btn' onclick="submitProcesses();" hidefocus="true"><span><span>确定</span></span></button>
			<button class='btn' onclick="window.parent.$('#addProcess').colorbox.close();" hidefocus="true"><span><span>返回</span></span></button>
		</div>
		<div id="backMsg" style="margin: 5px 0 5px 16px; color: red;"></div>
		<input name="id" id="dict_id" value="${id}" type="hidden"/>
		<div style="overflow-y: auto; height: 400px;" >
		<div id="process_tree" class="demo" align="left" ></div>
		</div>
	</aa:zone>
</div>
</body>
</html>