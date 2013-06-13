<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>选择办理人</title>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
	
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/hl/style.css" media="all" />
    <script src="${resourcesCtx}/widgets/hl/highlight.js"></script>
    <script src="${resourcesCtx}/widgets/hl/lang-js.js"></script>
    <script src="${resourcesCtx}/widgets/hl/lang-xml.js"></script>
    <script src="${resourcesCtx}/widgets/hl/lang-html.js"></script>
    <script src="${resourcesCtx}/widgets/hl/lang-css.js"></script>
    <script src="${resourcesCtx}/widgets/hl/helpers.js"></script>

	<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
	<script src="${resourcesCtx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${resourcesCtx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>
	<script>
	$(function () {
		//初始化页签
		$( "#tabs" ).tabs({select:function(event,ui){
		}});
		var html = '<button class="btn" onclick="OK();"  hidefocus="true"><span><span>确定</span></span></button>';
		$("#myp").html(html);
		rankTree();
		
	});
	function rankTree(){  
		// alert(encodeURI("${ctx}/engine/data-dictionary!createRankTree.htm?aa=订单") );  
		$.ajaxSetup({cache:false});
		$("#rankTree").jstree({
						"json_data":{
							"ajax" : { "url" : "${wfCtx}/engine/data-dictionary!createRankTree.htm",
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
									"url" : "${wfCtx}/engine/data-dictionary!createRankTree.htm",
									// You get the search string as a parameter
									"data" : function (str) {
										return { 
											"searchValue" : str 
										}; 
									}
								}
							},
							"types" :{
								 "valid_children" : [ "root" ],  
									"types" : {
										"root" : {
											"icon" : {
												"image" : "${resourcesCtx}/widgets/jstree/themes/folder.png"
											}
										},
										"rank" : {
											"icon" : {
												"image" : "${resourcesCtx}/widgets/jstree/themes/file.png"
											}
										}
									}
								 }, 
						   "plugins" : [ "themes", "json_data","checkbox","types","search"  ]
			});
		
	}

	//function searchFun(treeId,inputId){
	//	$("#"+treeId).jstree("search",document.getElementById(inputId).value);
	//}

	var flag=false;
	//选择
	function selectContact(id){
		if(id=="companyli"){
			$("#companyli").attr("class","selected");
			$("#userli").attr("class","");
			$("#rankli").attr("class","");
			$("#companyTree").show();
			$("#userTree").hide();
			$("#rankTree").hide();
			$("#searchTbWg").css("display","none");
			showSearchTable("searchTb");
			var html = '<button class="btn" onclick="OK();"  hidefocus="true">'
				+'<span><span>确定</span></span></button>';

			//var html = '<button class="btn" onclick="OK();"  hidefocus="true"><span><span>确定</span></span></button><button class="btn" onclick="searchFun(\'companyTree\',\'searchValue\');"><span><span>查询</span></span></button><input id="searchValue" name="searchValue"/>';
			$("#myp").html(html);
		}else if(id=="userli"){
			if(!flag){
				multipleTreeTwo("DEPARTMENT_WORKGROUP_TREE");
				flag=true;
			}
			$("#searchTbWg").css("display","none");
			showSearchTable("searchTbTwo");
			$("#companyli").attr("class","");
			$("#userli").attr("class","selected");
			$("#rankli").attr("class","");
			$("#companyTree").hide();
			$("#userTree").show();
			$("#rankTree").hide();
			var html = '<button class="btn" onclick="OK();"  hidefocus="true">'
				+'<span><span>确定</span></span></button>';
				
			//var html = '<p style="margin: 10px 10px 10px 10px;text-align: left"><a class="btnStyle" href="#" onclick="OK();" style="color:black;" hidefocus="true">确定</a><a class="btnStyle" href="#" onclick="searchFun(\'userTree\',\'searchValue\');">查询</a><input id="searchValue" name="searchValue"/></p>';
			$("#myp").html(html);
		}else{
			$("#rankli").attr("class","selected");
			$("#companyli").attr("class","");
			$("#userli").attr("class","");
			$("#rankTree").show();
			$("#companyTree").hide();
			$("#userTree").hide();
			hiddenSearchTable();
			$("#searchTbWg").css("display","block");
			var html = '<button class="btn" onclick="OK();"  hidefocus="true">'
				+'<span><span>确定</span></span></button>';	
			//var html = '<p style="margin: 10px 10px 10px 10px;text-align: left"><a class="btnStyle" href="#" onclick="OK();" style="color:black;" hidefocus="true">确定</a><a class="btnStyle" href="#" onclick="searchFun(\'rankTree\',\'searchValue\');">查询</a><input id="searchValue" name="searchValue"/></p>';
			$("#myp").html(html);
		}
	}

	function selectMan(){
		var signs="";
		var value=getUserInfoWithTreeId("companyTree");
		
		if(value!=""){
			var arr=eval(value);
			for(var i=0;i<arr.length;i++){
				if(arr[i].type=="allDepartment" || arr[i].type=="company"){
					$("#personnelNames").attr("value","所有人员");
					$("#personnelIds").attr("value","");
					$("#departmentIds").attr("value","");
					$("#workGroupIds").attr("value","");
					break;
				}else{
					$("#personnelNames").attr("value","");
					for(var i=0; i<arr.length; i++){
						if(arr[i].type=="user"){
							signs+=arr[i].name+"["+arr[i].loginName+"],";
						}
					}
					if(signs!=""){
						signs=signs.substring(0,signs.length-1);
						$("#personnelIds").attr("value",signs);
					}
				}
			}
		}else{
			$("#personnelNames").attr("value","");
			$("#personnelIds").attr("value","");
			$("#departmentIds").attr("value","");
			$("#workGroupIds").attr("value","");
		}
	}

	function selectDeptAndWorkGroup(){
		if($("#personnelNames").attr("value")!="所有人员"){
			var value=getInfoWithTreeId("userTree");
			if(value!=""){
				//部门/工作组
				var userArr = eval(getInfoWithTreeId("userTree"));
				var deptSigns="";
				var workGroupSigns="";
					for(var i=0; i<userArr.length; i++){
						if(userArr[i].type=="department"){
							deptSigns+=userArr[i].name+"["+userArr[i].id+"],";
						}else if(userArr[i].type=="workGroup"){
							workGroupSigns+=userArr[i].name+"["+userArr[i].id+"],";
						}
					}
					if(deptSigns!=""){
						deptSigns=deptSigns.substring(0,deptSigns.length-1);
						$("#departmentIds").attr("value",deptSigns);
					}
					if(workGroupSigns!=""){
						workGroupSigns=workGroupSigns.substring(0,workGroupSigns.length-1);
						$("#workGroupIds").attr("value",workGroupSigns);
					}
			}else{
				$("#departmentIds").attr("value","");
				$("#workGroupIds").attr("value","");
			}
		}
	}

	function selectRank(){
		var signs="";
		var arr=eval(getRankJsonValue(getSelectRanks()));
		for(var i=0; i<arr.length; i++){
			signs+=arr[i].title+"["+arr[i].id+"],";
		}
		if(signs!=""){
			signs=signs.substring(0,signs.length-1);
		}
		return signs;
	}

	function getRankJsonValue(arr){
		var info="[";
     	for(var i=0; i<arr.length; i++){
	     	var type=arr[i].substring(0,arr[i].lastIndexOf("_"));
	     	if(type!="all_rank"){
			  info+="{type:"+"\"rank\""
			     +",id:"+"\""+arr[i].substring(arr[i].indexOf("_")+1,arr[i].indexOf("-"))+"\""
			     +",title:"+"\""+arr[i].substring(arr[i].indexOf("-")+1,arr[i].length)+"\""+"},";
			}
	    }
	    if(info.indexOf(",")>=0){
	    	info=info.substring(0,info.length-1);
	    }
       info+="]";
       return info;
	}

	function getSelectRanks(){
    	var lists = $("#rankTree").find("li.jstree-checked");
		var v="" ;
		for(var i=0; i<lists.length; i++){
			v+=$(lists[i]).attr("id");
			if(i!=lists.length-1)
				v+=";";
		}
		if(v!=""){
			var arr=v.split(";");
			return arr;
		}else{
			return "";
		}
	}

	//用户树多选 ；
	function OK(){
		var value="";
		//人员处理
		selectMan();
		//部门和工作组处理
		selectDeptAndWorkGroup();
		window.parent.addInfo($("#personnelIds").attr("value"),$("#departmentIds").attr("value"),$("#workGroupIds").attr("value"),$("#personnelNames").attr("value"),selectRank());
		window.parent.$("#info").focus();
		window.parent.$.colorbox.close();
	}
	</script>
								
</head>
<body>
<div class="ui-layout-center">
<form action="" id="pageForm" method="post"></form>
<aa:zone name="treeList"></aa:zone>
		<div id="tabs">
			<ul>
				<li ><a href="#tabs-1" onclick="selectContact('companyli');">选择人员</a></li>
				<li ><a href="#tabs-1" onclick="selectContact('userli');">选择部门和工作组</a></li>
				<li ><a href="#tabs-1" onclick="selectContact('rankli');">选择用户上下级关系</a></li>
			</ul>
			<div id="tabs-1">
		    <input id="personnelNames" name="personnelNames" type="hidden"/>
		    <input id="personnelIds" name="personnelIds" type="hidden" />
		    <input id="departmentIds" name="departmentIds" type="hidden" />
		    <input id="workGroupIds" name="workGroupIds" type="hidden"/>
		    <div class="opt-body">
				<div id="myp"></div>
			<div id="opt-content" >
			   	<div>
			   		<acsTags:tree defaultable="true" treeId="companyTree" treeType="MAN_DEPARTMENT_TREE" multiple="true"></acsTags:tree>
			   	</div>
			   	<acsTags:tree  defaultable="false" treeId="userTree" treeType="DEPARTMENT_WORKGROUP_TREE" multiple="true"></acsTags:tree>
			   	<table id="searchTbWg" style="display:none;"><tr><td >
					<input id="searchInputWg" /></td><td ><a class="search-btn" href="#" onclick="search_fun('rankTree','searchInputWg');" ><b class="ui-icon ui-icon-search"></b></a>
				</td></tr></table>
				<div id="rankTree" class="demo" style="display:none;"></div>
			</div>
			</div>
			</div>
			</div>
			<script type="text/javascript">
				//流程环节设置的必填字段
				$(document).ready(function(){
					getContentHeight();
				});
				function getContentHeight(){
					var h = $('.ui-layout-center').height();
					if($(".opt-btn").height()>0){
						$("#opt-content").css("height",h-125); 
					}else{
						$("#opt-content").css("height",h-90); 
					}		
				}
			</script>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
</html>
