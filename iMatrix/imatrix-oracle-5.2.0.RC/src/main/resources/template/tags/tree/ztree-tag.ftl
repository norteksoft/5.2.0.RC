<script type="text/javascript">
var ztreeId="",nodeList = [], parentNodeList = [],checkNodeList = [] ;
var checkUserNodeList = [] , checkDeparmentNodeList = [], checkWorkgroupNodeList = [];
var currentCheckedNode, currentClickNode ,currentClickParentNode;
var isCheckAll=false;
var companyName ;
		var setting = {
			async: {
				enable: true,
				url: getUrl
			},
			check: {
				<#if chkStyle?if_exists !="">
			    <#if chkboxType?if_exists !="">
				chkboxType:${chkboxType},
				</#if>
				enable: true,
				chkStyle:"${chkStyle}"
				</#if>
			},
			data: {
				simpleData: {
					enable: true
				}
			},
			view: {
				expandSpeed: "",
				fontCss: getFontCss
			},
			callback: {
				beforeExpand: beforeExpand,
				beforeAsync: beforeAsync,
				onAsyncSuccess: onAsyncSuccess,
				onAsyncError: onAsyncError,
				onClick: onClick,
				onCheck: onCheck
				
			}
		};

	function getUrl(treeId, treeNode) {
		var param = "";
		if(typeof(treeNode)!="undefined"){
			param = "currentId="+treeNode.id;
        }else{
        	var param = "currentId=0";
         }
		//http://192.168.1.99:8888/imatrix/portal/ztree.action
		return "${actionUrl}&" + param;
	}
	
	//单选
	function onClick(event, treeId, treeNode){
	
	  //获取当前点击的节点
	  currentClickNode = treeNode;
	  
	  //获取当前用户的父节点
	  currentClickParentNode = treeNode.getParentNode();
	  
	}
	
	//多选checkbox
	function onCheck(event, treeId, treeNode){
	
	  //获取所有选择节点集合selectNodeList
	  var zTree = $.fn.zTree.getZTreeObj(ztreeId);
      var checkNodes = zTree.getCheckedNodes(true);
      var j = 0, k=0,m=0;
      checkUserNodeList=[],
      checkDeparmentNodeList=[],
      checkWorkgroupNodeList=[],
      isCheckAll=false;
	  for(var i=0, l=checkNodes.length; i<l; i++){
	  
	      checkNodeList[i] = checkNodes[i];
	      if(checkNodes[i].type=='company'){
	        if(!checkNodes[i].getCheckStatus().half){
	        isCheckAll = true;
	        }
	        companyName = checkNodes[i].name;
	      }else if(checkNodes[i].type=='department'){
	        checkDeparmentNodeList[k] = checkNodes[i];
	        k++;
	      }else if(checkNodes[i].type=='workgroup'){
	        checkWorkgroupNodeList[m] = checkNodes[i];
	        m++;
	      }else if(checkNodes[i].type=='user'){
	        checkUserNodeList[j] = checkNodes[i];
	        j++;
	      }
	      if(isCheckAll)break;
	  }
	  //获取当前选择节
	  currentCheckedNode = treeNode;
	  
	}
	
	function beforeExpand(treeId, treeNode) {
		//alert(treeNode.data);
		//var d = JSON.parse(treeNode.data);
		//var d = JSON.parse('{"weight" : "1" , "code" : "董事会办公室" , "shortTitle" : "董事会办公室" , "summary" : "董事会办公室" }');
		//alert(d.weight);
		//alert(d.code);
		//alert(d.shortTitle);
		//alert(d.summary);
	}

    function beforeAsync() {
	    curAsyncCount++;
	}
	function onAsyncSuccess(event, treeId, treeNode, msg) {
	  curAsyncCount--;
	  if(curStatus!="init"&&curAsyncCount <= 0){
	     searchNode();
	  }
	}
	function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
		var zTree = $.fn.zTree.getZTreeObj(ztreeId);
		alert("异步获取数据出现异常。");
		zTree.updateNode(treeNode);
	}
	
	
	function searchNode(){
	   var zTree = $.fn.zTree.getZTreeObj(ztreeId);
	   nodeList = zTree.getNodesByParam("name", $("#searchInput").val());
	   updateNodes(true);
	}
	
	function updateNodes(highlight) {
		var zTree = $.fn.zTree.getZTreeObj(ztreeId);
		for( var i=0, l=nodeList.length; i<l; i++) {
		    zTree.expandNode(nodeList[i].getParentNode(), true, true, true);
			nodeList[i].highlight = highlight;
			zTree.updateNode(nodeList[i]);
		}
	}
	function getFontCss(treeId, treeNode) {
			return (!!treeNode.highlight) ? {color:"#FF0000", "font-weight":"bold"} : {color:"#333", "font-weight":"normal"};
	}
	
	function ajaxSearch(){
       	$.ajax({
				data:{searchValue:$("#searchInput").val()},
				cache:false,
				type:"post",
				url:"${searchUrl}",
				success:function(data, textStatus){
				  if(data!=""){
				    var parentNodeIds = data.split(",");
				    var zTree = $.fn.zTree.getZTreeObj(ztreeId);
				    for(var i=0;i<parentNodeIds.length;i++){
                       parentNodeIds[i] = zTree.getNodeByParam("id", parentNodeIds[i]);
				    }
				    asyncNodes(parentNodeIds);
				  }
				 
				   
				},
				error:function(){
					    window.location.reload();
				}
		    });
	}
	var curAsyncCount = 0,curStatus = "init";
	function asyncNodes(nodes) {
		if (!nodes) return;
		var zTree = $.fn.zTree.getZTreeObj(ztreeId);
		for (var i=0, l=nodes.length; i<l; i++) {
			if (nodes[i].isParent && nodes[i].zAsync) {
				asyncNodes(nodes[i].children);
			} else {
				zTree.reAsyncChildNodes(nodes[i], "refresh", true);
				curStatus = "";
			}
		}
	}
	
	$(document).ready(function(){
	    ztreeId = $("#ztreeId").val();
		$.fn.zTree.init($("#"+ztreeId), setting);
		
		//搜索
		$("#searchInput").bind("keypress", function(event){
		   if(event.keyCode=='13'){
			     ajaxSearch();
		   }
		});
		
	});

//<---------------api--------------------------------------------------------------------------->

//<---------------(多选)------------------------------------------------------->
//获取当前选择节点集合checkNodeList
function getCheckNodeList(){
return checkNodeList;
}

//获取当前选择的节点
function getCurrentCheckNode(){
return currentCheckedNode;
}

//根据类型获取当前选择的节点集合
function getCheckUserNodeList(type){
  if(checkNodes[i].type=='user'){
    return checkUserNodeList;
  }else if(checkNodes[i].type=='department'){
    return checkDeparmentNodeList;
  }else if(checkNodes[i].type=='workgroup'){
    return checkWorkgroupNodeList;
  }
}

//根据节点属性名获取所有user节点的值字符串
//param:id,name,pId,loginName,email,parentName

function getCheckUserNodeByParam(param){
   var result = "";
   for(var i =0;i<checkUserNodeList.length;i++){
	      var data = JSON.parse(checkUserNodeList[i].data);
	      var parentNode = checkUserNodeList[i].getParentNode();
	       if(param=="id"){
	          var id = checkUserNodeList[i].id.split("_")[1];
	          result+=id+",";
	       }else if(param=="name"){
	          result+=checkUserNodeList[i].name+",";
	       }else if(param=="pId"){
	          var pId = checkUserNodeList[i].pId.split("_")[1];
	          result+=pId+",";
	       }else if(param=="loginName"){
	          result+=data.loginName+",";
	       }else if(param=="email"){
	          result+=data.email+",";
	       }else if(param=="parentName"){
	          result+=parentNode.name+",";
	       }
   }
   if(isCheckAll){
     return "CHECKALL";
   }else{
     return result.substring(0,result.length-1);
   }
}

//根据节点属性名获取所有department节点的值字符串
//param:id,name,pId,code,shortTitle,summary

function getCheckDepartmentNodeByParam(param){
   var result = "";
   for(var i =0;i<checkDeparmentNodeList.length;i++){
	      var data = JSON.parse(checkDeparmentNodeList[i].data);
	       if(param=="id"){
	          var id = checkDeparmentNodeList[i].id.split("_")[1];
	          result+=id+",";
	       }else if(param=="name"){
	          result+=checkDeparmentNodeList[i].name+",";
	       }else if(param=="pId"){
	          var pId = checkDeparmentNodeList[i].pId.split("_")[1];
	          result+=pId+",";
	       }else if(param=="code"){
	          result+=data.code+",";
	       }else if(param=="shortTitle"){
	          result+=data.shortTitle+",";
	       }else if(param=="summary"){
	          result+=data.summary+",";
	       }
   }
    if(isCheckAll){
      return "CHECKALL";
    }else{
      return result.substring(0,result.length-1);
    }
}

//根据节点属性名获取所有workgroup节点的值字符串
//param:id,name,pId,code,description

function getCheckWorkgroupNodeByParam(param){
   var result = "";
   for(var i =0;i<checkWorkgroupNodeList.length;i++){
	      var data = JSON.parse(checkWorkgroupNodeList[i].data);
	       if(param=="id"){
	          var id = checkWorkgroupNodeList[i].id.split("_")[1];
	          result+=id+",";
	       }else if(param=="name"){
	          result+=checkWorkgroupNodeList[i].name+",";
	       }else if(param=="pId"){
	          var pId = checkWorkgroupNodeList[i].pId.split("_")[1];
	          result+=pId+",";
	       }else if(param=="code"){
	          result+=data.code+",";
	       }else if(param=="description"){
	          result+=data.description+",";
	       }
   }
    if(isCheckAll){
       return "CHECKALL";
    }else{
       return result.substring(0,result.length-1);
    }
}


//<---------------(单选)------------------------------------------------------------------->
//获取当前点击的节点
function getCurrentClickNode(){
return currentClickNode;
}

//获取当前点击节点的id
function getCurrentClickNodeId(){
return currentClickNode.id.split("_")[1];
}

//获取当前点击节点的parentId
function getCurrentClickNodeParentId(){
return currentClickNode.pId.split("_")[1];
}

//获取当前点击节点的name
function getCurrentClickNodeName(){
return currentClickNode.name;
}

//获取当前点击节点的type
function getCurrentClickNodeType(){
return currentClickNode.type;
}

//获取当前点击节点的data
function getCurrentClickNodeData(){
return currentClickNode.data;
}

//获取当前点击节点的父节点
function getCurrentClickParentNode(){
return currentClickParentNode;
}
//根据属性名得到user的值
//param:id,name,pId,loginName,email,parentName
function getClickUserValueByParam(param){
   var result = "";
   if(currentClickNode.type=='user'){
	      var data = JSON.parse(currentClickNode.data);
	      var parentNode = currentClickNode.getParentNode();
	       if(param=="id"){
	          result = currentClickNode.id.split("_")[1];
	       }else if(param=="name"){
	          result=currentClickNode.name;
	       }else if(param=="pId"){
	          result = currentClickNode.pId.split("_")[1];
	       }else if(param=="loginName"){
	          result=data.loginName;
	       }else if(param=="email"){
	          result=data.email;
	       }else if(param=="parentName"){
	          result=parentNode.name;
	       }
	}       
    return result;
}

//根据节点属性名获取所有department节点的值字符串
//param:id,name,pId,code,shortTitle,summary

function getClickDepartmentValueByParam(param){
   var result = "";
         if(currentClickNode.type=='department'){
	      var data = JSON.parse(currentClickNode.data);
	       if(param=="id"){
	          result = currentClickNode.id.split("_")[1];
	       }else if(param=="name"){
	          result=currentClickNode.name;
	       }else if(param=="pId"){
	          result= currentClickNode.pId.split("_")[1];
	       }else if(param=="code"){
	          result=data.code;
	       }else if(param=="shortTitle"){
	          result=data.shortTitle;
	       }else if(param=="summary"){
	          result=data.summary;
	       }
	      }
      return result;
}

//根据节点属性名获取所有workgroup节点的值字符串
//param:id,name,pId,code,description

function getClickWorkgroupValueByParam(param){
   var result = "";
   if(currentClickNode.type=='workgroup'){
	      var data = JSON.parse(currentClickNode.data);
	       if(param=="id"){
	          result = currentClickNode.id.split("_")[1];
	       }else if(param=="name"){
	          result=currentClickNode.name;
	       }else if(param=="pId"){
	          result = currentClickNode.pId.split("_")[1];
	       }else if(param=="code"){
	          result=data.code;
	       }else if(param=="description"){
	          result=data.description;
	       }
	}
       return result;
}
</script>
<table><tr><td >
	<input id="searchInput" /></td><td ><span style="font-size:small; color: gray">(输入完请回车)</span>
</td></tr></table>
<input id="ztreeId" type="hidden" value="${treeId}" >
<ul id="${treeId}" class="ztree"></ul>

	
	
	
	
	
	