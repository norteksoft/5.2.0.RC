<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>选择办理人</title>
	<script type="text/javascript" src="${resourcesCtx}/js/jquery-all-1.0.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/css/${theme}/jquery-ui-1.8.16.custom.css" id="_style"/>
	<script type="text/javascript" src="${resourcesCtx}/js/aa.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/public.js"></script>
	
	
	<link rel="stylesheet" href="${resourcesCtx}/widgets/ztree/css/zTreeStyle/zTreeStyle.css" type="text/css">
	<script type="text/javascript" src="${resourcesCtx}/widgets/ztree/js/jquery.ztree.core-3.5.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/ztree/js/jquery.ztree.excheck-3.5.js"></script>
</head>
<script type="text/javascript">
var ztreeId="zTreeId",nodeList = [], parentNodeList = [],checkNodeList = [] ;
var checkUserNodeList = [] , checkDeparmentNodeList = [], checkWorkgroupNodeList = [];
var currentCheckedNode, currentClickNode ,currentClickParentNode;
var isCheckAll=false;
var companyName ;
var showCachContent="";
var hiddenCachContent="";
var leafType ;
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
				beforeAsync: beforeAsync,
				onAsyncSuccess: onAsyncSuccess,
				onAsyncError: onAsyncError,
				onClick: onClick,
				onCheck: onCheck
				
			}
		};

	function getUrl(treeId, treeNode) {
		var param = "";
		if(typeof(treeNode)!="undefined"&&treeNode!=null){
			param = "currentId="+treeNode.id;
        }else{
        	var param = "currentId=0";
         }
        //http://192.168.1.99:8888/imatrix/portal/ztree.action
        //多页签时
        if('${leafEnable}'=='true'){
	        if(leafType==""||typeof(leafType)=="undefined"){
	           return "${actionUrl}?treeType=${multiLeafList[0].type}&" + param;
	        }else{
	           return "${actionUrl}?treeType="+leafType+"&" + param;
	        }
        }else{//单页签时 
		  return "${actionUrl}&" + param;
		}
	}
	
	//切换页签点击方法
	var leafType;
	function multiTreeSelected(type){
	   leafType=type;
	   //更新缓存
	   document.cookie="showCachContent=";
	   document.cookie="hiddenCachContent=";
	   //刷新树
	   var treeObj = $.fn.zTree.getZTreeObj(ztreeId);
       treeObj.reAsyncChildNodes(null,"refresh");
      
	}
	
	//单选
	function onClick(event, treeId, treeNode){
	
	  //获取当前点击的节点
	  currentClickNode = treeNode;
	  
	  //获取当前用户的父节点
	  currentClickParentNode = treeNode.getParentNode();
	  
	  //给模板外赋值
      window.parent.myztreeObj.pCurrentClickNode=currentClickNode;
	  window.parent.myztreeObj.pCurrentClickParentNode=currentClickParentNode;
	  
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
	  
	  
	  //给模板外赋值
	  if(isCheckAll){//全选时
	      window.parent.myztreeObj.checkAll="CHECKALL";
	  }else{
		  window.parent.myztreeObj.pCheckNodeList=checkNodeList;
		  window.parent.myztreeObj.pCheckUserNodeList=checkUserNodeList;
		  window.parent.myztreeObj.pCheckDeparmentNodeList=checkDeparmentNodeList;
		  window.parent.myztreeObj.pCheckWorkgroupNodeList=checkWorkgroupNodeList;
	  }
	  //当前选择节
	  window.parent.myztreeObj.pCurrentCheckedNode=currentCheckedNode;
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
	
	//自动赋值
	function _ok_ztree(){
	
	  if('${feedbackEnable}'=='false'){return;}
	  
	  if('${chkStyle}'=='checkbox'){//多选时
	    var zTree = $.fn.zTree.getZTreeObj(ztreeId);
        var checkNodes = zTree.getCheckedNodes(true);
        if(checkNodes.length==0){
            alert("请选择!");
            return;
        }
	  }else{
	      if(currentClickNode==null){
	        alert("请选择!");
	        return;
	      }
	  }
	  
	  
	  //给显示框和隐藏域赋值
	  fillValue();
	  
	  //关闭弹框
	  window.parent.$.colorbox.close();
	}
	
	//给显示框和隐藏域赋值
	function fillValue(){
	 if('${leafEnable}'=='true'){//多页签赋值(不提供追加)
		     var ltype="";
	         if(typeof(leafType)=='undefined'){
	           ltype='${multiLeafList[0].type}';
	         }else{
	           ltype=leafType;
	         }
	         //显示框
		     <#list multiLeafList?if_exists as page >
		         if('${page.type}'==ltype){
		             //如果没有给定显示框id,则退出
		             <#if showInput?if_exists==''>
		               return;
		             </#if>
		             //显示域赋值
			      	 <#if page.showValue?if_exists==''>
				      fillInName('${showInput}','${chkStyle}',ltype,'showCachContent');
				     <#else>
				      fillInSingleShowContent('${showInput}','${page.showValue}','${chkStyle}','showCachContent'); 
				     </#if>
			    }
		     </#list>
		     
		     //影藏域
		     <#list multiLeafList?if_exists as page >
		        if('${page.type}'==ltype){
		             //如果没有给定隐藏域id,则退出
				     <#if hiddenInput?if_exists==''>
				      return;
				     </#if>
				     //隐藏域赋值
				      <#if page.hiddenValue?if_exists==''>
				      fillInId('${hiddenInput}','${chkStyle}',ltype,'hiddenCachContent');
				     <#else>
				      fillInSingleShowContent('${hiddenInput}','${page.hiddenValue}','${chkStyle}','hiddenCachContent');
				     </#if>
				  }   
		     </#list>
		      
	  }else{//单页签赋值
		     //如果没有给定显示框id,则退出
		     <#if showInput?if_exists==''>
		      return;
		     </#if>
		     //显示域赋值
		     <#if showThing?if_exists==''>
		      fillInName('${showInput}','${chkStyle}','${treeType}','showCachContent');
		     <#else>
		      fillInSingleShowContent('${showInput}','${showThing}','${chkStyle}','showCachContent');
		     </#if>
		     //如果没有给定隐藏域id,则退出
		     <#if hiddenInput?if_exists==''>
		      return;
		     </#if>
		      //隐藏域赋值
		     <#if hiddenThing?if_exists==''>
		        fillInId('${hiddenInput}','${chkStyle}','${treeType}','hiddenCachContent');
		     <#else>
		        fillInSingleShowContent('${hiddenInput}','${hiddenThing}','${chkStyle}','hiddenCachContent');
		     </#if>
	  }
	}
	
	
	//showThing为空时,给显示框默认赋名字
	function fillInName(showInput,chkStyle,treeType,cachName){
	  var defaultShowThing = getDefaultShowContent(treeType);
	  fillInSingleShowContent(showInput,defaultShowThing,chkStyle,cachName);
	}
	
	//默认显示名字
	function getDefaultShowContent(treeType){
	   if(treeType=='COMPANY'||treeType=='MAN_DEPARTMENT_TREE'||treeType=='MAN_GROUP_TREE'){
	     return '{"user":"name"}';
	   }else if(treeType=='DEPARTMENT_TREE'){
	     return '{"department":"name"}';
	   }else if(treeType=='GROUP_TREE'){
	     return '{"workgroup":"name"}';
	   }else if(treeType=='DEPARTMENT_WORKGROUP_TREE'){
	     return '{"department":"name","workgroup":"name"}';
	   }
	}
	
	//hiddenThing为空时,给隐藏框默认赋id 
	function fillInId(hiddenInput,chkStyle,treeType,cachName){
	  var defaultHiddenThing = getDefaultHiddenContent(treeType);
	  fillInSingleShowContent(hiddenInput,defaultHiddenThing,chkStyle,cachName);
	}
	
	//默认显示id
	function getDefaultHiddenContent(treeType){
	   if(treeType=='COMPANY'||treeType=='MAN_DEPARTMENT_TREE'||treeType=='MAN_GROUP_TREE'){
	     return '{"user":"id"}';
	   }else if(treeType=='DEPARTMENT_TREE'){
	     return '{"department":"id"}';
	   }else if(treeType=='GROUP_TREE'){
	     return '{"workgroup":"id"}';
	   }else if(treeType=='DEPARTMENT_WORKGROUP_TREE'){
	     return '{"department":"id","workgroup":"id"}';
	   }
	}
	
	
	//给显示框赋设定的值(多选/单选)
	//showThing:'{"company":"name","department":"id","user":"email","workgroup":"name"}'
	//showInput:显示input的id或者隐藏域的id
	//showThing:内容设置字符串
	//chkStyle:多选还是单选
	//isFill:是否往input框填值
	//cachName:缓存名称
	function fillInSingleShowContent(showInput,showThing,chkStyle,cachName,isFill){
	     var showThingSetting = JSON.parse(showThing);
	     var result = "";
	     var appendResult = "";
	     if(chkStyle!=''&&chkStyle=='checkbox'){//多选时
	          if(isCheckAll){//全选时
		            parent.$("#"+showInput).attr("value","CHECKALL");
		             return "CHECKALL";
		       }else{ 
			          if(typeof(showThingSetting.company)!="undefined"&&companyName!=''){
			             result=companyName+",";
			             appendResult = "company:"+companyName+"+";
			          }
			          if(typeof(showThingSetting.user)!="undefined"){
			           var users = getCheckUserNodeByParam(showThingSetting.user);
			             if(users!=''){
			             result=result+users+",";
			             appendResult =appendResult+"user:"+users+"+";
			             }
			          }
			           
			          if(typeof(showThingSetting.department)!="undefined"){
			           var departments = getCheckDepartmentNodeByParam(showThingSetting.department);
			             if(departments!=''){
			             result=result+departments+",";
			             appendResult =appendResult+"department:"+departments+"+";
			             }
			          }
			          
			          if(typeof(showThingSetting.workgroup)!="undefined"){
			             var workgroups = getCheckWorkgroupNodeByParam(showThingSetting.workgroup);
			             if(workgroups!=''){
			             result=result+workgroups+",";
			             appendResult =appendResult+"workgroup:"+workgroups+"+";
			             }
			          }
			          //赋值方法
			          var inputc = getValueByAppend(result.substring(0,result.length-1),appendResult.substring(0,appendResult.length-1),showInput,'${append}',cachName);
			          
			          if(isFill!='false')
			            parent.$("#"+showInput).attr("value",inputc);
			            
			            return inputc;
		     }
	     }else{//单选时
	                 
                   if(typeof(showThingSetting.company)!="undefined"){
                         var cName = getCurrentClickNodeName();   
                         if(cName!='')
		                 result=cName;
		           }  
	     
	               if(typeof(showThingSetting.user)!="undefined"){
	                     var userValue = getClickUserValueByParam(showThingSetting.user);
	                     if(userValue!='')
			             result=userValue;
			       }
			       
		           if(typeof(showThingSetting.department)!="undefined"){
		                 var departmentValue = getClickDepartmentValueByParam(showThingSetting.department);
		                 if(departmentValue!='')
			             result=departmentValue;
		           }
		           
	               if(typeof(showThingSetting.workgroup)!="undefined"){
	                     var workgroupValue = getClickWorkgroupValueByParam(showThingSetting.workgroup);
	                     if(workgroupValue!='')
			             result= workgroupValue;
			       }
			       if(isFill!='false')
			       parent.$("#"+showInput).attr("value",result);
			       
			       return result;
			       
	     
	     }
	}
	
	//根据是否append得到放入显示或者隐藏域的值(只针对多选)
	function getValueByAppend(noAppendContent,appendContent,showInput,append,cachName){
	  if(append!='true'){//不追加时
	   //放缓存
	   if(cachName=='showCachContent'){
	        document.cookie="showCachContent"+showInput+"="+escape(appendContent)
			                   +";expires="+setExpiresTime(30);
	   }else if(cachName=='hiddenCachContent'){
	        document.cookie="hiddenCachContent"+showInput+"="+escape(appendContent)
			                   +";expires="+setExpiresTime(30);
	   }
	    
	    return noAppendContent;
	  }else{//追加时
	    return getNoRepeatAppendContent(appendContent,cachName,showInput);
	  }
	}
	
	//得到去重复的追加的内容
	//appendContent:company:sda,dad+user:fd,daf,fa+department:fda,fdsaf,adf+workgroup:daf,fa,fd
	function getNoRepeatAppendContent(appendContent,cachName,showInput){
	     if(cachName=='showCachContent'){
		       if(showCachContent==''){
			       //把老的值缓存起来,设置cookie过期时间为30分钟
			       document.cookie="showCachContent"+showInput+"="+escape(appendContent)
			                       +";expires="+setExpiresTime(30);
			       showCachContent = appendContent;
			       
			       return getStandContent(appendContent);
			   }else{
			       var companyStr = getNoRepeatStr(getStrByType("company",showCachContent),getStrByType("company",appendContent));
			       
			       var userStr = getNoRepeatStr(getStrByType("user",showCachContent),getStrByType("user",appendContent));
			       
			       var departmentStr = getNoRepeatStr(getStrByType("department",showCachContent),getStrByType("department",appendContent));
			      
			       var workgroupStr = getNoRepeatStr(getStrByType("workgroup",showCachContent),getStrByType("workgroup",appendContent));
			       
			       //更新缓存
			       document.cookie="showCachContent"+showInput+"=";
			       var currentCach = getCachContent(companyStr,userStr,departmentStr,workgroupStr);
			       showCachContent=currentCach;
			       document.cookie="showCachContent"+showInput+"="+escape(currentCach);
			       
			       
				   return getStandResult(companyStr,userStr,departmentStr,workgroupStr);
			   }
		  }else if(cachName=='hiddenCachContent'){
			   if(hiddenCachContent==''){
			       //把老的值缓存起来,设置cookie过期时间为30分钟
			       document.cookie="hiddenCachContent"+showInput+"="+escape(appendContent)
			                      +";expires="+setExpiresTime(30);
			       hiddenCachContent=appendContent;
			       
			       return getStandContent(appendContent);
			   }else{
			       var companyStr = getNoRepeatStr(getStrByType("company",hiddenCachContent),getStrByType("company",appendContent));
			       
			       var userStr = getNoRepeatStr(getStrByType("user",hiddenCachContent),getStrByType("user",appendContent));
			       
			       var departmentStr = getNoRepeatStr(getStrByType("department",hiddenCachContent),getStrByType("department",appendContent));
			      
			       var workgroupStr = getNoRepeatStr(getStrByType("workgroup",hiddenCachContent),getStrByType("workgroup",appendContent));
			       
			       //更新缓存
			       document.cookie="hiddenCachContent"+showInput+"=";
			       var currentCach = getCachContent(companyStr,userStr,departmentStr,workgroupStr);
			       hiddenCachContent = currentCach;
			       document.cookie="hiddenCachContent"+showInput+"="+escape(currentCach);
			       
				   return getStandResult(companyStr,userStr,departmentStr,workgroupStr);
			   }
		  }
	}
	
	//设置cookie过期时间
	function setExpiresTime(minutes){
	   var date = new Date();
       date.setTime(date.getTime()+minutes*60*60);
       return date.toGMTString();
	}

	//根据key值从缓存里取东西	
	function getCachFromCookieByKey(key){
		var result = "";
		var cookieArr = unescape(document.cookie).split(";");
		for(var i=0;i<cookieArr.length;i++){
			var k = cookieArr[i].split("=")[0];
			var v = cookieArr[i].split("=")[1];
			if($.trim(k)==$.trim(key)){
				result = v;
			}
		}
		return result;
	}  

	
	function getCachContent(companyStr,userStr,departmentStr,workgroupStr){
	   var result = "";
	   if(companyStr!='0'){
	       result = "company:"+companyStr+"+";
	   }
	   if(userStr!='0'){
	       result = result+"user:"+userStr+"+";
	   }
	   if(departmentStr!='0'){
	       result = result+"department:"+departmentStr+"+";
	   }
	   
	    if(workgroupStr!='0'){
	       result = result+"workgroup:"+workgroupStr+"+";
	   }
	   return result.substring(0,result.length-1);
	}
	
	function getStandResult(companyStr,userStr,departmentStr,workgroupStr){
       var result = "";
	   if(companyStr!=''){
	       result = companyStr+",";
	   }
	   if(userStr!=''){
	       result = result+userStr+",";
	   }
	   if(departmentStr!=''){
	       result = result+departmentStr+",";
	   }
	   
	    if(workgroupStr!=''){
	       result = result+workgroupStr+",";
	   }
	   return result.substring(0,result.length-1);
	}
	
	
	
	function getNoRepeatStr(cachContent,newContent){
	  var result = "";
	  if(cachContent=='0'&&newContent!='0'){
	      result = newContent+",";
	  }else if(newContent=='0'&&cachContent!='0'){
	      result = cachContent+",";
	  }else if(newContent!='0'&&cachContent!='0'){
	      result = cachContent+",";
		  var arr = newContent.split(",");
		  for(var i=0;i<arr.length;i++){
		      if(!containTheItem(cachContent,arr[i])){
		         result+=arr[i]+",";
		      }
		  }
	  }
	  return result.substring(0,result.length-1);
	}
	
	function containTheItem(cachContent,item){
	  var arr = cachContent.split(",");
	  for(var i=0;i<arr.length;i++){
	    if(arr[i]==item)return true;
	  }
	  return false;
	}
	
	//根据类别得到字符串里对应的值
	function getStrByType(type,str){
	  if(str!=''){
		   var arr = str.split("+");
		   for(var i =0 ;i<arr.length;i++){
		     var typeStr = arr[i].split(":")[0];
		     var valueStr = arr[i].split(":")[1];
		     if(typeStr==type){
		      return valueStr;
		     }
		   }
	   }
	   return "0";
	}
	
	//得到input的标准值
	function getStandContent(appendContent){
	   var result = "";
	   if(appendContent!=''){
	 	   var oldArr = appendContent.split("+");
		   for(var i =0 ;i<oldArr.length;i++){
		     var oldValueStr = oldArr[i].split(":")[1];
		     result+=oldValueStr+",";
		   }
		   return result.substring(0,result.length-1);
	   }
	   return result;
	}
	
	$(document).ready(function(){
	    //初始化页签(启用时)
	    if($("#leafEnable").val()=='true'){
		    $( "#tabs" ).tabs();
	    }
	    
	    //初始化缓存
	     <#if append?if_exists=='true'>
	     <#if showInput?if_exists!=''>
	      showCachContent=getCachFromCookieByKey('showCachContent${showInput}');
	     </#if>
	     
	     <#if hiddenInput?if_exists!=''>
	       hiddenCachContent=getCachFromCookieByKey('hiddenCachContent${hiddenInput}');
	     </#if>
	     </#if>
		$.fn.zTree.init($("#"+ztreeId), setting);
		
		//搜索
		$("#searchInput").bind("keypress", function(event){
		   if(event.keyCode=='13'){
			     ajaxSearch();
		   }
		});
		
	});
	

//<---------------api--------------------------api------------------------------------------------->
//<---------------多选--------------------------api------------------------------------------------->
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

//<---------------单选--------------------------------------------------------------------------->
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

<body onload="getContentHeight_ColorIframe();">
<div class="opt-body">
	<input id="leafEnable" type="hidden" value="${leafEnable}" >
	<div class="cbox-btn">
		<table>
		<tr>
		<td style="width:80px;">
			<button class="btn" onclick="_ok_ztree();" id="ok"><span><span>确定</span></span></button>
		</td>
		<td >
			<input id="searchInput" style="width:180px;"/></td><td >
		</td>
		</tr>
		</table>
    </div>
    <div id="opt-content">
	<div id="tabs" style="height:350px;">
	<#if leafEnable=='true'>
	    <ul>
	    <#list multiLeafList?if_exists as page >
	    <li ><a href="#tabs-1" onclick="multiTreeSelected('${page.type}');">${page.name}</a></li>
	    </#list>
		</ul>
	</#if>	
	<div id="tabs-1">
	<ul id="zTreeId" class="ztree"></ul>
	</div>
	</div>
	</div>
</div>
</body>
</html>