<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>数据字典</title>
	<%@ include file="/common/setting-colorbox-meta.jsp"%>
	
	<link rel="stylesheet" type="text/css" href="${settingCtx}/css/style.css" />
	
	<style type="text/css">
		a{text-decoration:none;}
		#secNav{
			margin:0;
			padding: 0;
			background: url(../images/sec-background.jpg);
			border-bottom: 9px #f0f0f0 solid;
			height: 26px;
		}
		#secNav li{
			display: inline;
			border-right: 1px #9ea2a3 solid;
			margin:0px;
			padding: 0px;
			font-size: 10.5pt;
		    line-height: 26px;
		    text-align:center;
			float:left;
		}
		#secNav li a{
			font-size: 10.5pt;
			text-decoration: none;
			margin: 0 10px;
			color: #000;
		}
		#secNav li a:ACTIVE, #secNav li a:VISITED{
			color: #000;
		}
		#secNav li.selected{
			background-color: #f1f1f1;
		}
	</style>
	<script type="text/javascript">

	var selLi="companyli";
	//选择
	function selectContact(id){
		if(id=="companyli"){
			selLi="companyli";
			$("#companyli").attr("class","selected");
			$("#userli").attr("class","");
			$("#groupli").attr("class","");
			ajaxSubmit('defaultForm', '${settingCtx}/rank/user-tree.htm', 'wf_rank_tree');
		}else if(id=="userli"){
			selLi="userli";
			$("#companyli").attr("class","");
			$("#userli").attr("class","selected");
			$("#groupli").attr("class","");
			ajaxSubmit('defaultForm', '${settingCtx}/rank/dept-tree.htm', 'wf_rank_tree');
		}else if(id=="groupli"){
			selLi="groupli";
			$("#companyli").attr("class","");
			$("#userli").attr("class","");
			$("#groupli").attr("class","selected");
			ajaxSubmit('defaultForm', '${settingCtx}/rank/group-tree.htm', 'wf_rank_tree');
		}
	}

	function selUsers(value){
		var type;
		if(selLi=="companyli"){
			type="user";
		}else if(selLi=="userli"){
			type="department";
		}else if(selLi=="groupli"){
			type="workGroup";
		}
		if(value!=""){
			window.parent.$('#userDiv').html('');
			var arr=eval(value);
			var isAllUsers=false;
			var userNames="";
			var typeInfo;
			for(var i=0;i<arr.length;i++){
				if(type=="user"){
					typeInfo="0;";
				}else if(type=="department"){
					typeInfo="1;";
				}else if(type=="workGroup"){
					typeInfo="2;";
				}
				if(arr[i].type==type || arr[i].type=="allDepartment" || arr[i].type=="company"||arr[i].type=="allWorkGroup"){
					if(arr[i].type==type){
						if(type=="user"){
							typeInfo+=arr[i].loginName;
						}
						window.parent.$("#userDiv").append("<input name='userInfos' value='"+arr[i].name+";"+arr[i].id+";"+typeInfo+"'/>");
						userNames=userNames+arr[i].name+",";
					}else if(arr[i].type=="company"){
						if(type=="user"){
							alert("不能选择所有人员");
							isAllUsers=true;
						}else if(type=="department"){
							alert("不能选择所有部门");
							isAllUsers=true;
						}
					//	else if(type=="workGroup"){
					//		window.parent.$("#userDiv").append("<input name='userInfos' value='所有工作组;;"+typeInfo+"'/>");
					//		userNames="所有工作组";
					//	}
						break;
					}else if(arr[i].type=="allDepartment"){
						if(type=="user"){
							alert("不能选择所有人员");
							isAllUsers=true;
						}else if(type=="department"){
							alert("不能选择所有部门");
							isAllUsers=true;
						}
						break;
					}else if(arr[i].type=="allWorkGroup"){
						window.parent.$("#userDiv").append("<input name='userInfos' value='所有工作组;;"+typeInfo+"'/>");
						userNames="所有工作组";
						break;
					}
				}
			}
			if(!isAllUsers){
				if(userNames==""){
					if(type=="user"){
						alert("请选择人员");
					}else if(type=="department"){
						alert("请选择部门");
					}else if(type=="workGroup"){
						alert("请选择工作组");
					}
				}else{
					if(userNames.indexOf(",")>0){
						userNames=userNames.substring(0,userNames.lastIndexOf(","));
					}
					window.parent.$("#userNames").attr("value",userNames);
					window.parent.$("#title").focus();
					window.parent.$("#selectUser").colorbox.close();
				}
			}else{
				return;
			}
		}else{
			if(type=="user"){
				alert("请选择人员");
			}else if(type=="department"){
				alert("请选择部门");
			}else if(type=="workGroup"){
				alert("请选择工作组");
			}
		}
		
	}
	
	function selectUsers(){
		var value=getInfo();
		selUsers(value);
	}
	</script>
	<script type="text/javascript">
		$(document).ready(function() {
			$( "#tabs" ).tabs({select:function(event,ui){}});
		});
		function pageUlChange(x){
			if('a'==x){
				selectContact('companyli');
			}else if('b'==x){
				selectContact('userli');
			}
		}
	</script>
</head>
<body style="padding: 5px;">
	<aa:zone name="wf_task">
	<form id="defaultForm" name="defaultForm"></form>
		<div id="tabs">
			<ul>
				<li><a href="#tabs-1" onclick="pageUlChange('a');">选择人员</a></li>
				<li><a href="#tabs-1" onclick="pageUlChange('b')">选择部门</a></li>
			</ul>
			<div id="tabs-1">
				<div class="opt-btn">
					<button class="btn" onclick="selectUsers();"><span><span>确定</span></span></button>
				</div>
				<aa:zone name="wf_rank_tree">
					<acsTags:tree defaultable="true" treeId="companyTree" treeType="MAN_DEPARTMENT_TREE" multiple="true"></acsTags:tree>
				</aa:zone>
			</div>
		</div>
		<style type="text/css"> .jstree-classic.jstree-focused{background: none;} </style>
	</aa:zone>
</body>
</html>