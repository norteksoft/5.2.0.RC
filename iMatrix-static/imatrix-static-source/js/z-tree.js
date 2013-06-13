var ztools = {
		apply: function(fun, param, defaultValue) {
			if ((typeof fun) == "function") {
				return fun.apply(fun, param?param:[]);
			}
			return defaultValue;
           }
};

//view
var rootUrl ;
var title ;
var width ;
var height ;

//type
var treeType;
var showContent ;
var noDeparmentUser;
var onlineVisible ;

//data
var chkStyle;
var treeNodeData;
var chkboxType;
//显示的部门
var departmentShow ;

//leaf
var leafEnable ;
var multiLeafJson ;

//feedback
var feedbackEnable;
var showInput;
var showThing ;
var hiddenInput;
var hiddenThing;
var append;

var callback ;


var myztreeObj={
   pCheckNodeList:"",
   pCheckUserNodeList:"",
   pCheckDeparmentNodeList:"",
   pCheckWorkgroupNodeList:"",
   pCurrentCheckedNode:"",
   pCurrentClickNode:"",
   pCurrentClickParentNode:"",
   checkAll:""
};
//api
var API=[{
	myObj:{
	   pCheckNodeList:myztreeObj.pCheckNodeList,
	   pCurrentCheckedNode:myztreeObj.pCurrentCheckedNode
    },	
	multi:{	
    	//获取当前选择节点集合checkNodeList
	    getCheckNodeList:function(){
		  return myztreeObj.pCheckNodeList;
	    },
	    //根据类型取接节点list
	    getCheckNodeListByType:function(type){
	    	 if(type=='user'){
    		    return myztreeObj.pCheckUserNodeList;
    		  }else if(type=='department'){
    		    return myztreeObj.pCheckDeparmentNodeList;
    		  }else if(type=='workgroup'){
    		    return myztreeObj.pCheckWorkgroupNodeList;
    		  }
        },
        //根据节点属性名获取所有user节点的值字符串
        getCheckUserNodeByParam:function(param){
        	 var result = "";
        	   var checkUserNodeList = myztreeObj.pCheckUserNodeList;
        	   if(myztreeObj.checkAll=="CHECKALL"){
          	     return "CHECKALL";
          	   }else{
	        	   for(var i =0;i<checkUserNodeList.length;i++){
	        		      var data = JSON.parse(checkUserNodeList[i].data);
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
	        		       }
	        	   }
        	       return result.substring(0,result.length-1);
        	   }
        },
        //根据节点属性名获取所有department节点的值字符串
        getCheckDepartmentNodeByParam:function(param){
        	 var result = "";
        	 var checkDeparmentNodeList = myztreeObj.pCheckDeparmentNodeList;
        	    if(myztreeObj.checkAll=="CHECKALL"){
        	      return "CHECKALL";
        	    }else{
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
    	          return result.substring(0,result.length-1);
    	    }
        },
        //根据节点属性名获取所有workgroup节点的值字符串
        getCheckWorkgroupNodeByParam:function(param){
        	   var result = "";
        	   var checkWorkgroupNodeList = myztreeObj.pCheckWorkgroupNodeList;
        	   if(myztreeObj.checkAll=="CHECKALL"){
        	       return "CHECKALL";
        	    }else{
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
        	       return result.substring(0,result.length-1);
        	    }
        },
        //获取当前选择的节点
        getCurrentCheckNode:function(){
        	  return myztreeObj.pCurrentCheckedNode;
        }
     },
     single:{
    	 //获取当前点击的节点
    	 getCurrentClickNode:function(){
    	    return myztreeObj.pCurrentClickNode;
         },
         //获取当前点击节点的父节点
         getCurrentClickParentNode:function(){
        	 return myztreeObj.pCurrentClickParentNode;
         },
         //获取当前点击节点的id
         getCurrentClickNodeId:function(){
        	 return myztreeObj.pCurrentClickNode.id.split("_")[1];
         },
         //获取当前点击节点的parentId
         getCurrentClickNodeParentId:function(){
        	 return myztreeObj.pCurrentClickNode.pId.split("_")[1];
         },
         //获取当前点击节点的name
         getCurrentClickNodeName:function(){
             return myztreeObj.pCurrentClickNode.name;
         },
         //获取当前点击节点的type
         getCurrentClickNodeType:function(){
         return myztreeObj.pCurrentClickNode.type;
         },
         //获取当前点击节点的data
         getCurrentClickNodeData:function(){
         return myztreeObj.pCurrentClickNode.data;
         },
         //根据属性名得到user的值
         //param:id,name,pId,loginName,email,parentName
         getClickUserValueByParam:function(param){
	          var result = "";
	          var currentClickNode = myztreeObj.pCurrentClickNode;
	          if(currentClickNode.type=='user'){
	       	      var data = JSON.parse(currentClickNode.data);
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
	       	       }
	           }       
	           return result;
         },
         //根据节点属性名获取所有department节点的值字符串
         //param:id,name,pId,code,shortTitle,summary
         getClickDepartmentValueByParam:function(param){
	               var result = "";
	               var currentClickNode = myztreeObj.pCurrentClickNode;
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
         },
         //根据节点属性名获取所有workgroup节点的值字符串
         //param:id,name,pId,code,description
         getClickWorkgroupValueByParam:function(param){
               var result = "";
               var currentClickNode = myztreeObj.pCurrentClickNode;
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
     }
    }];

function popZtree(zSetting){
//view
rootUrl = zSetting.view.url;
title = zSetting.view.title;
width = zSetting.view.width;
height = zSetting.view.height;

//type
treeType = zSetting.type.treeType;//树的类型
showContent = ztools.apply(zSetting.type.showContent,'',zSetting.type.showContent);//设置树节点显示信息
noDeparmentUser = zSetting.type.noDeparmentUser;//是否显示无部门人员
onlineVisible = zSetting.type.onlineVisible;//是否标出在线人员

//data
chkStyle = zSetting.data.chkStyle;//多选单选
treeNodeData = ztools.apply(zSetting.data.treeNodeData,'',zSetting.data.treeNodeData);//设定树节点data属性
chkboxType = zSetting.data.chkboxType;//设置父子节点勾选关联关系
//显示的部门
departmentShow = ztools.apply(zSetting.data.departmentShow,'',zSetting.data.departmentShow);

//leaf
leafEnable = zSetting.leaf.enable;//是否启用多页签
multiLeafJson = ztools.apply(zSetting.leaf.multiLeafJson,'',zSetting.leaf.multiLeafJson);//页签设置

//feedback
feedbackEnable = zSetting.feedback.enable;//是否启用自动赋值
showInput = zSetting.feedback.showInput;//显示input框id
showThing = ztools.apply(zSetting.feedback.showThing,'',zSetting.feedback.showThing);//显示input内容设置
hiddenInput = zSetting.feedback.hiddenInput;//隐藏input框id
hiddenThing = ztools.apply(zSetting.feedback.hiddenThing,'',zSetting.feedback.hiddenThing);//隐藏input内容设置
append = zSetting.feedback.append;//是否追加(只针对多选)

callback = zSetting.callback.onClose;

var url = rootUrl+"portal/ztree-pop.action?treeType="+treeType
          +"&chkStyle="+chkStyle+"&treeNodeData="+treeNodeData+"&chkboxType="+chkboxType
          +"&treeNodeShowContent="+showContent+"&userWithoutDeptVisible="+noDeparmentUser
          +"&onlineVisible="+onlineVisible+"&leafEnable="+leafEnable+"&multiLeafJson="+multiLeafJson
          +"&feedbackEnable="+feedbackEnable+"&showInput="+showInput+"&showThing="+showThing
          +"&hiddenInput="+hiddenInput+"&hiddenThing="+hiddenThing+"&append="+append
          +"&departmentShow="+departmentShow;
	
$.colorbox({href:encodeURI(url),iframe:true, innerWidth:width, innerHeight:height,overlayClose:false,title:title,onClosed:function(){
	//执行回调
	ztools.apply(callback,API,callback);
}});
	return API;
}

var myZtree={
   clearInput:function(){
	    if(typeof(showInput)=='undefined'||showInput=='')return;
	    $("#"+showInput).attr("value","");
	    if(typeof(hiddenInput)=='undefined'||hiddenInput=='')return;
	    $("#"+hiddenInput).attr("value","");
   },
   deleteInput:function(formId){
	    if(typeof(showInput)=='undefined'||showInput=='')return;
	    var showContent = getCachFromCookieByKey('showCachContent'+showInput);
	    
	    if(typeof(hiddenInput)=='undefined'||hiddenInput=='')return;
	    var hiddenContent = getCachFromCookieByKey('hiddenCachContent'+hiddenInput);
	    
	    if(showContent==''){alert("显示缓存为空!");return;}
		if(hiddenContent==''){alert("隐藏缓存为空!");return;}
	  
		//校验输入字符串的合法性
		if(!isAvailable(showContent,hiddenContent)){alert("显示的格式和隐藏域格式设置不一致!");return;}
		
	    var showContentJson = createJson(showContent,hiddenContent);
	    var removeStaffJsonOld = $("#removeStaffJson").val();
		if(typeof(removeStaffJsonOld)=='undefined'){
		      $("#"+formId).append("<input type='hidden' id='removeStaffJson' name='removeStaffJson' value='"+showContentJson+"'/> ");
		}else{
		      $("#removeStaffJson").attr("value",showContentJson);
		}
		var url = rootUrl+"portal/remove-ztree.action?showInput="+showInput+"&hiddenInput="+hiddenInput;
		$.colorbox({href:encodeURI(url),iframe:true, innerWidth:400, innerHeight:400,overlayClose:false,title:"移除",onClosed:function(){}});
   }
};

//根据key值从缓存里取东西	
function getCachFromCookieByKey(key){
	var result = "";
	var cookieArr = unescape(document.cookie).split(";");
	for(var i=0;i<cookieArr.length-1;i++){
		var k = cookieArr[i].split("=")[0];
		var v = cookieArr[i].split("=")[1];
		if($.trim(k)==$.trim(key)){
			result = v;
		}
	}
	return result;
}
//拼接json
//两个字符串的类型(如 user/department/workgroup)的名称和数量都应该的一致的

function createJson(showContent,hiddenContent){
	
	var arr = showContent.split("+");
	var harr = hiddenContent.split("+");
	
	var json = '[';
	for(var i=0;i<arr.length;i++){
		var type=$.trim(arr[i].split(":")[0]);
		var valueArr = arr[i].split(":")[1].split(",");
		var hValueArr = harr[i].split(":")[1].split(",");
		if(type=='user'){
			for(var j=0;j<valueArr.length;j++){
			json = json+'{"type":"user","showValue":"'+valueArr[j]+'","hiddenValue":"'+hValueArr[j]+'"},';
			}
		}else if(type=='department'){
			for(var j=0;j<valueArr.length;j++){
			json = json+'{"type":"department","showValue":"'+valueArr[j]+'","hiddenValue":"'+hValueArr[j]+'"},';
			}
		}else if(type=='workgroup'){
			for(var j=0;j<valueArr.length;j++){
				json = json+'{"type":"workgroup","showValue":"'+valueArr[j]+'","hiddenValue":"'+hValueArr[j]+'"},';
			}
		}
	}
	json = json.substring(0,json.length-1)+']';
	return json;
}

function isAvailable(showContent,hiddenContent){
	var result=true;
	var showArr = showContent.split("+");
	var hiddenArr = hiddenContent.split("+");
	if(showArr.length!=hiddenArr.length){result=false;}
		
	if(showContent.indexOf("user:")!=-1&&hiddenContent.indexOf("user:")==-1){result=false ;}
	if(hiddenContent.indexOf("user:")!=-1&&showContent.indexOf("user:")==-1){result=false;}
	
	if(showContent.indexOf("department:")!=-1&&hiddenContent.indexOf("department:")==-1){result=false;}
	if(hiddenContent.indexOf("department:")!=-1&&showContent.indexOf("department:")==-1){result=false;}
	
	if(showContent.indexOf("workgroup:")!=-1&&hiddenContent.indexOf("workgroup:")==-1){result=false;}
	if(hiddenContent.indexOf("workgroup:")!=-1&&showContent.indexOf("workgroup:")==-1){result=false;}
	
	return result;
}