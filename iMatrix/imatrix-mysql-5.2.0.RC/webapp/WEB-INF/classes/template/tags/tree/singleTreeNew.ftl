<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>选择办理人</title>
	<script type="text/javascript" src="${resourceCtx}/js/jquery-all-1.0.js"></script>
	<script type="text/javascript" src="${resourceCtx}/widgets/jstree/jquery.jstree.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourceCtx}/css/${theme}/jquery-ui-1.8.16.custom.css" id="_style"/>
	<script type="text/javascript" src="${resourceCtx}/js/aa.js"></script>
	<script type="text/javascript" src="${resourceCtx}/js/public.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourceCtx}/widgets/colorbox/colorbox.css" />
	
</head>
<body onload="getContentHeight_ColorIframe();">
<div class="opt-body">
<div class="cbox-btn">
	<button class="btn" onclick="_ok_tree();" id="wf_ok"><span><span>确定</span></span></button> 
	<div style="display:inline;float:right;*margin-top:-28px;" >
		<table><tr><td >
			<input id="searchInput" /></td><td ><a class="search-btn" href="#" onclick="search_fun();" ><b class="ui-icon ui-icon-search"></b></a>
		</td></tr></table>
	</div>
</div>
<div id="opt-content">
<input id="defaultId" type="hidden" name="defaultId" >
<input id="defaultTreeType" type="hidden" value="${treeType}" >
<input id="defaultTreeValue" type="hidden" value="${defaultTreeValue}" >
<input id="leafPageId" type="hidden" value="${leafPage}" >

<div id="tabs" style="height:350px;">
<#if leafPage=='true'>
    <ul>
    <#list leafPageList as page >
    <li ><a href="#tabs-1" onclick="changeSelected('${page.type}','${page.value}');">${page.name}</a></li>
    </#list>
	</ul>
</#if>	
<div id="tabs-1">
<div id="${treeId}" class="demo" type="${treeType}">
<script type="text/javascript">
//<---------------解析树节点的分隔符-------------->
var split_one = "~~";
var split_two = "==";
var split_three = "*#";
var split_four = "|#";
var split_five = "+#";
var split_six = "~#";
var split_seven = "**";
var split_eight = "=#";
//<---------------单选树标签第三版-------------->
//初始化树
$(document).ready(function (){ 
    var leafPageVal = $("#leafPageId").val();
	if(leafPageVal=='true'){
	    //初始化页签
		$( "#tabs" ).tabs({select:function(event,ui){
		}});
		 $("#defaultTreeType").attr("value",'${defaultTreeType}');
         $("#defaultTreeValue").attr("value",'${defaultTreeValue}');
         singleLeafPageTree('${defaultTreeType}');
	}else{
	     singleTree();
	}
});

//树脚本
function singleTree(){
	$.ajaxSetup({cache:false});
	   $("#${treeId}").bind("search.jstree",function(e,data){
				$.jstree.rollback(data.rlbk); 
         }).jstree({
		"json_data":{
				"ajax" : { "url" : "${actionUrl}",
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
			"search" : {
					"ajax" : {
						"url" : "${searchUrl}",
						"async":true,
						// You get the search string as a parameter
						"data" : function (str) {
							return { 
								"searchValue" : str 
							}; 
						},
						"success":function(data){
							$("#${treeId}").find("li").find("a").removeClass("jstree-search");  
							var arr=eval(data);
							for(var i=0;i<arr.length;i++){
									var deptInfos = arr[i].split(";");
									var deptInfo = deptInfos[0];
									var parentInfo = deptInfos[1];
									$.jstree._reference($("#${treeId}")).open_node($("li[id="+deptInfo+"]"),
									function(){
										//打开子部门节点
										for(var j=0;j<arr.length;j++){
											var jdeptInfos = arr[j].split(";");
											var jdeptInfo = jdeptInfos[0];
											var jparentInfo = jdeptInfos[1];
											if(jparentInfo!=""){
												$.jstree._reference($("li[id="+jparentInfo+"]")).open_node($("li[id="+jdeptInfo+"]"),
												function(){
													var result = $("#${treeId}").find("a" +  ":" +"contains" + "(" + $("#searchInput").attr("value") + ")");
													result.addClass("jstree-search");
												},true);
											}
										}
										//添加选中样式
										var result = $("#${treeId}").find("a" +  ":" +"contains" + "(" + $("#searchInput").attr("value") + ")");
										result.addClass("jstree-search");
									},true);
									
							}
							
						}
					}
				},
			 "types" :{
					"types" : {
						"company" : {
							"icon" : {
								"image" : "${resourceCtx}/widgets/jstree/themes/root.gif"
							}
						},
						"folder" : {
							"icon" : {
								"image" : "${resourceCtx}/widgets/jstree/themes/folder.gif"
							}
						},
						"user" : {
							"icon" : {
								"image" : "${resourceCtx}/widgets/jstree/themes/file.gif"
							}
						},
						"onlineUser" : {
						"icon" : {
							"image" : "${resourceCtx}/widgets/jstree/themes/online.gif"
							}
						}
					}
				 }, 
			"ui":{"select_limit":1},
		   "plugins" : [ "themes", "json_data","types","ui","search" ]
		}).bind("select_node.jstree",function(e){
			id=$(".jstree-clicked").parent().attr("id");
			if(getType()=='user'){
				getId();
				getName();
				getType();
				getTreeType();
				getLoginName();
				getEmail();
				getHonorificName();
		        getWeight();
		        getUserDepartmentName();
			}else if(getType()=='department'){
				getDepartmentName();
				getDepartmentId();
			}else if(getType()=='workGroup'){
				getWorkGroupName();
				getWorkGroupId();
			}
			
		});
}
	
function search_fun(){
	$("#${treeId}").jstree("search",$("#searchInput").val());
}

var id="";	
//从树上获取json
//treeType:user,department,workGroup,默认为"user"
function getInfo(treeType){
    //id=$("#${treeId}").find("li a.jstree-clicked").parent().attr("id");
	var currentTreeType="user";
	if(treeType!=""&&typeof (treeType)!="undefined"){
		currentTreeType=treeType;
	}
 	if(id!=""){
        var info="[";
		if(currentTreeType=="user"){
	     	var type=id.substring(0,id.indexOf(split_one));
	     	if(type=="user"){
	     	 info+="{type:"+"\""+id.substring(0,id.indexOf(split_one))+"\""
			     +",id:"+"\""+id.substring(id.indexOf(split_one)+2,id.indexOf(split_two))+"\""
			     +",name:"+"\""+id.substring(id.indexOf(split_two)+2,id.indexOf(split_three))+"\""
			     +",loginName:"+"\""+id.substring(id.indexOf(split_three)+2,id.indexOf(split_four))+"\""
			     +",parentType:"+"\""+id.substring(id.indexOf(split_five)+2,id.indexOf(split_eight))+"\""
			     +",parentName:"+"\""+id.substring(id.indexOf(split_four)+2,id.indexOf(split_five))+"\""+"},";
	     	}
		}else if(currentTreeType=="department"){
		   var type=id.substring(0,id.indexOf(split_one));
		   if(type=="department"){
		    info+="{type:"+"\""+id.substring(0,id.indexOf(split_one))+"\""
			     +",id:"+"\""+id.substring(id.indexOf(split_one)+2,id.indexOf(split_two))+"\""
			     +",name:"+"\""+id.substring(id.indexOf(split_two)+2,id.indexOf(split_three))+"\""+"},";
		   }
		}else if(currentTreeType=="workGroup"){
		   var type=id.substring(0,id.indexOf(split_one));
		   if(type=="workGroup"){
		    info+="{type:"+"\""+id.substring(0,id.indexOf(split_one))+"\""
			     +",id:"+"\""+id.substring(id.indexOf(split_one)+2,id.indexOf(split_two))+"\""
			     +",name:"+"\""+id.substring(id.indexOf(split_two)+2,id.indexOf(split_three))+"\""+"},";
		   }
		}else if(currentTreeType=="departmentAndGroup"){
		   var type=id.substring(0,id.indexOf(split_one));
		   if(type=="department"){
		    info+="{type:"+"\""+id.substring(0,id.indexOf(split_one))+"\""
			     +",id:"+"\""+id.substring(id.indexOf(split_one)+2,id.indexOf(split_two))+"\""
			     +",name:"+"\""+id.substring(id.indexOf(split_two)+2,id.indexOf(split_three))+"\""+"},";
		   }else if(type=="workGroup"){
		    info+="{type:"+"\""+id.substring(0,id.indexOf(split_one))+"\""
			     +",id:"+"\""+id.substring(id.indexOf(split_one)+2,id.indexOf(split_two))+"\""
			     +",name:"+"\""+id.substring(id.indexOf(split_two)+2,id.indexOf(split_three))+"\""+"},";
		   }
		}
	    if(info.indexOf(",")>=0){
	    	info=info.substring(0,info.length-1);
	    }
       info+="]";
       window.parent.sInfor = info;
   return info;
  }else{
  	return "";
  }
}

//页签点击事件
function changeSelected(type,value){
    $("#defaultTreeType").attr("value",type);
    $("#defaultTreeValue").attr("value",value);
     singleLeafPageTree(type);
}
//初始化页签树
function singleLeafPageTree(type){
$.ajaxSetup({cache:false});
	   $("#${treeId}").jstree({
		"json_data":{
				"ajax" : { "url" : "${actionUrl}?treeType="+type,
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
			"search" : {
					"ajax" : {
						"url" : "${searchUrl}?treeType="+type,
						"async":true,
						// You get the search string as a parameter
						"data" : function (str) {
							return { 
								"searchValue" : str 
							}; 
						},
						"success":function(data){
							$("#${treeId}").find("li").find("a").removeClass("jstree-search");  
							var arr=eval(data);
							for(var i=0;i<arr.length;i++){
									var deptInfos = arr[i].split(";");
									var deptInfo = deptInfos[0];
									var parentInfo = deptInfos[1];
									$.jstree._reference($("#${treeId}")).open_node($("li[id="+deptInfo+"]"),
									function(){
										//打开子部门节点
										for(var j=0;j<arr.length;j++){
											var jdeptInfos = arr[j].split(";");
											var jdeptInfo = jdeptInfos[0];
											var jparentInfo = jdeptInfos[1];
											if(jparentInfo!=""){
												$.jstree._reference($("li[id="+jparentInfo+"]")).open_node($("li[id="+jdeptInfo+"]"),
												function(){
													var result = $("#${treeId}").find("a" +  ":" +"contains" + "(" + $("#${inputId}").attr("value") + ")");
													result.addClass("jstree-search");
												},true);
											}
										}
										//添加选中样式
										var result = $("#${treeId}").find("a" +  ":" +"contains" + "(" + $("#${inputId}").attr("value") + ")");
										result.addClass("jstree-search");
									},true);
									
							}
							
						}
					}
				},
			 "types" :{
					"types" : {
						"company" : {
							"icon" : {
								"image" : "${ctx}/widgets/jstree/themes/root.gif"
							}
						},
						"folder" : {
							"icon" : {
								"image" : "${ctx}/widgets/jstree/themes/folder.gif"
							}
						},
						"user" : {
							"icon" : {
								"image" : "${ctx}/widgets/jstree/themes/file.gif"
							}
						},
						"onlineUser" : {
						"icon" : {
							"image" : "${resourceCtx}/widgets/jstree/themes/online.gif"
							}
						}	
					}
				 }, 
			"ui":{"select_limit":1},
		   "plugins" : [ "themes", "json_data","types","ui","search" ]
		}).bind("select_node.jstree",function(e){
				id=$(".jstree-clicked").parent().attr("id");
					getId();
				getName();
				getType();
				getLoginName();
				}).bind("search.jstree",function(e,data){
		$.jstree.rollback(data.rlbk); 
     });
			
}
	//用户调用的方法
	//<---start--->
	//用户id
	function getId(){
		var ids=id.substring(id.indexOf(split_one)+2,id.indexOf(split_two));
		window.parent.singleId=ids;
		return ids;
	}
	//用户名称	
	function getName(){
		var name=id.substring(id.indexOf(split_two)+2,id.indexOf(split_three));
		window.parent.singleName=name;
		return name;
	}	
	//树类型		
	function getType(){
		var type=id.substring(0,id.indexOf(split_one));
		window.parent.singleType=type;
		return type;
	}	
	function getTreeType(){
	   var treeType = $("#defaultTreeType").val();
	   window.parent.mTreeType=treeType;     
	   return treeType;
	}
	//用户登录名
	function getLoginName(){
		var loginName=id.substring(id.indexOf(split_three)+2,id.indexOf(split_four));
		window.parent.singleLoginName=loginName;
		return loginName;
	}

	//部门名称
	function getDepartmentName(){
		var departmentName=id.substring(id.indexOf(split_three)+2,id.length);
		window.parent.singleDepartmentName=departmentName;     
		return departmentName;
	}
	//部门id
	function getDepartmentId(){
		var departmentId=id.substring(id.indexOf(split_one)+2,id.indexOf(split_two));
		window.parent.singleDepartmentId=departmentId;     
		return departmentId;
	}
	
	//工作组名称
	function getWorkGroupName(){
		var workGroupName=id.substring(id.indexOf(split_three)+2,id.length);
		window.parent.singleWorkGroupName=workGroupName;     
		return workGroupName;
	}
	//工作组id
	function getWorkGroupId(){
		var workGroupId=id.substring(id.indexOf(split_one)+2,id.indexOf(split_two));
		window.parent.singleWorkGroupId=workGroupId;     
		return workGroupId;
	}
	
	//用户邮件
	function getEmail(){
		var email=id.split(split_eight)[1].split(split_seven)[0];
		window.parent.singleEmail=email;     
		return email;
	}

	//用户尊称
	function getHonorificName(){
		var honorificName=id.split(split_eight)[1].split(split_seven)[1];
		window.parent.singleHonorificName=honorificName;     
		return honorificName;
	}
	//用户权重
	function getWeight(){
		var weight=id.split(split_eight)[1].split(split_seven)[2];
		window.parent.singleWeight=weight;     
		return weight;
	}
	//获取用户部门名称
	function getUserDepartmentName(){
		var deptName=id.substring(id.indexOf(split_five)+2,id.indexOf(split_six));
		window.parent.singleUserDeptName=deptName;     
		return deptName;
	}
//<---END--->

function returnParamater(obj,para){
    var result = "";
    if(para=='id'){
	result=obj.id;
	}else if(para=='name'){
	result=obj.name;
	}else if(para=='loginName'){
	result=obj.loginName;
	}else if(para=='email'){
	result=obj.email;
	}
	return 	result;
}
    
function createValueStr(obj,treeValue){
    var result = "";
    var values = treeValue.split(",");
    var val="";
	for(var j=0;j<values.length;j++){
		result+=returnParamater(obj,values[j])+",";
	}
	return result.substring(0,result.length-1);
}
		
function selectMan(treeValue){
	var info=getInfo("user");
	if(info!=""&&info!="[]"){
		var user=eval(info);
		if(user!=''){
			var type=user[0].type;
			if(type == "user"){
				window.parent.$("#${hiddenInputId}").attr("value", createValueStr(user[0],treeValue));
				window.parent.$("#${showInputId}").attr("value", user[0].name);
			}else{
				alert("请选择人员");
				return;
			}
		}else{
		      alert("请选择正确的节!");
		      return;
		}
	}else{
		alert("请选择人员");
		return;
	}
}
	
function selectDepartment(treeValue){
	var info=getInfo("department");
	if(info!=""&&info!="[]"){
		var user=eval(info);
		if(user!=''){
			var type=user[0].type;
			if(type == "department"){
				window.parent.$("#${hiddenInputId}").attr("value", createValueStr(user[0],treeValue));
				window.parent.$("#${showInputId}").attr("value", user[0].name);
			}else{
				alert("请选择部门");
				return;
			}
		}else{
		      alert("请选择正确的节!");
		      return;
		}
	}else{
		alert("请选择部门");
		return;
	}
}
function selectWorkGroup(treeValue){
	var info=getInfo("workGroup");
	if(info!=""&&info!="[]"){
		var user=eval(info);
		if(user!=''){
			var type=user[0].type;
			if(type == "workGroup"){
				window.parent.$("#${hiddenInputId}").attr("value",createValueStr(user[0],treeValue));
				window.parent.$("#${showInputId}").attr("value", user[0].name);
			}else{
				alert("请选择工作组");
				return;
			}
		}else{
		      alert("请选择正确的节!");
		      return;
		}
	}else{
		alert("请选择工作组");
		return;
	}
}
function selectDepartmentAndWorkGroup(treeValue){
	var info=getInfo("departmentAndGroup");
	if(info!=""&&info!="[]"){
		var user=eval(info);
		if(user!=''){
			var type=user[0].type;
			if(type == "department"){
				window.parent.$("#${hiddenInputId}").attr("value", createValueStr(user[0],treeValue));
				window.parent.$("#${showInputId}").attr("value", user[0].name);
			}else if(type == "workGroup"){
			    window.parent.$("#${hiddenInputId}").attr("value", createValueStr(user[0],treeValue));
				window.parent.$("#${showInputId}").attr("value", user[0].name);
			}else{
				alert("请选择部门或工作组");
				return;
			}
		}else{
		      alert("请选择正确的节!");
		      return;
		}
	}else{
		alert("请选择部门或工作组");
		return;
	}
}
//提交按钮点击事件
function _ok_tree(){
    var treeType = $("#defaultTreeType").val();
    var treeValue = $("#defaultTreeValue").val();
	if(treeType=='COMPANY'||treeType=='MAN_DEPARTMENT_GROUP_TREE'||treeType=='MAN_DEPARTMENT_TREE'||treeType=='MAN_GROUP_TREE'){
	    selectMan(treeValue);
	}else if(treeType=='DEPARTMENT_TREE'){
	    selectDepartment(treeValue);
	}else if(treeType=='GROUP_TREE'){
	    selectWorkGroup(treeValue);
	}else if(treeType=='DEPARTMENT_WORKGROUP_TREE'){
	    selectDepartmentAndWorkGroup(treeValue);
	}
	var tor = window.parent.$("#${hiddenInputId}").attr("value");
	if(typeof tor == "undefined" || tor == ""){
		//alert("请选择正确的节!");
	}else{
	    window.parent.okEnsure="OK";
		window.parent.$.colorbox.close();
	}
}
</script>
</div>
</div>
</div>	
</div>
</div>
</body>
<script type="text/javascript" src="${resourceCtx}/widgets/colorbox/jquery.colorbox.js"></script>
</html>


	
	
	
	
	
	