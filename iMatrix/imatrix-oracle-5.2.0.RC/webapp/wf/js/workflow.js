/************************************************************
 模块名称: 我的流程主页面对应js
 创 建 者：吴荣
 说    明: 该js文件名和jsp主页文件名相同
 ************************************************************/


function deleteWorkflow(){
	var ids = jQuery("#main_table").getGridParam('selarrrow');
	if(ids==''){
		alert("请选择流程");
		return;
	}else{
		if(confirm("确认删除吗？")){
			delRow('main_table', webRoot + '/engine/workflow!delete.htm');
			//refreshtree();
		}
	}
}
function deleteCallback(){
	//resetJmessaPageNo("workflowInstances");
	refreshtree();
}
var preTreeNodeList = "";
function refreshtree(){
	treeOne();
	showMsg();
}

function selectTreeid(treeId, firstId){
	if(treeId=="treeid"&&firstId=='ING'&&$("#currentTreeSelectedNode").attr('value')!=""){
		firstId =$("#currentTreeSelectedNode").attr('value');
	}
	var lis = $("#"+treeId).find("li");
	var isEnd = false;
	for(var i = 0; i < lis.length; i++){
		if($(lis[i]).attr("id") == firstId){
			$(lis[i]).children("a").attr("class","clicked");
			isEnd = true;
		}
		if(treeId=="treeid"&&$("#currentTreeSelectedNode").attr('value')!=""){
		preTreeNodeState(lis[i]);
		}
	}
}

function preTreeNodeState(node){
	if(typeof(preTreeNodeList)!="undefinded" ){
		for(var i = 0; i < preTreeNodeList.length; i++){
			if(preTreeNodeList[i][0] == $(node).attr("id")){
				$(node).removeClass("closed");
				$(node).removeClass("open");
				$(node).removeClass("leaf");
				$(node).addClass(preTreeNodeList[i][1]);
				break;
			}
		}
	}
}


/*****************************************
附件上传
*****************************************/
function viewAccessory(obj,url,taskId){
	if(taskId==""){
		alert("请先保存");
	}else{
		if($(obj).attr("state") == "1"){
			$("#accessory").css("display", "block");
			$(obj).attr("state", "2");
			$("#accImg").attr("src", webRoot + "/images/x2.png");
			ajaxAnyWhereViewAccessory(url);
		}else{
			$("#accessory").css("display", "none");
			$(obj).attr("state", "1");
			$("#accImg").attr("src", webRoot + "/images/x1.png");
		}
	}
}

//局部刷新
function ajaxAnyWhereViewAccessory(url){
$("#viewAccessoryForm").attr("action",url);
ajaxAnywhere.formName = "viewAccessoryForm";
ajaxAnywhere.getZonesToReload = function(){
	return "viewAccessorylist";
};
ajaxAnywhere.onAfterResponseProcessing = function () {
	
	
};
ajaxAnywhere.submitAJAX();
}
