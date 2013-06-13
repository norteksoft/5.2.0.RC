<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title>部门管理</title>
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/validation/cmxform.css"/>
	
	<script type="text/javascript">
		//新建部门
		function createDept(){
			ajaxSubmit('ajax_from', '${acsCtx}/organization/department!input.action', 'acs_content', updateDeptCallBack);
		}

		function updateDeptCallBack(){
			ruleInput();
		}
		//名称是否不包含下划线(_)、等号(=)、横线(-)、竖线(|)、加号(+)、波浪线(~),包括时返回false,不含时返回true
		function validateName(name){
			//if(name.indexOf("_")>=0||name.indexOf("=")>=0||name.indexOf("-")>=0
			//		||name.indexOf("|")>=0||name.indexOf("+")>=0||name.indexOf("~")>=0){
			//	return false;
			//}
			return true;
		}
		//名称是否不包含下划线(_)、等号(=)、横线(-)、竖线(|)、加号(+)、波浪线(~),包括时返回false,不含时返回true
		function validateName(name){
			//if(name.indexOf("_")>=0||name.indexOf("=")>=0||name.indexOf("-")>=0
			//		||name.indexOf("|")>=0||name.indexOf("+")>=0||name.indexOf("~")>=0){
			//	return false;
			//}
			return true;
		}

		function ruleInput(){
    		$("#inputForm").validate({
    			submitHandler: function() { 
	    			if(!validateName($("#departmentCode").val())){
		    			$("#departmentCode").parent().append('<label  class="error">编号、名称必填且编号、名称中不能包含下划线(_)、等号(=)、横线(-)、竖线(|)、加号(+)、波浪线(~)</label>');
	    			}else if(!validateName($("#departmentName").val())){
		    			$("#departmentName").parent().append('<label  class="error">编号、名称必填且编号、名称中不能包含下划线(_)、等号(=)、横线(-)、竖线(|)、加号(+)、波浪线(~)</label>');
			    	}else{
	    			saveDept(); 
			    	}
    			},
				rules: {
    			    code: "required",
    			    name: "required",
    			    weight: {
	    				required: true
	    			},
	    			shortTitle: {
	    				maxlength: 30
	    			},
	    			summary: {
	    				maxlength: 60
	    			}
    		     }
		     	,messages: {
			    	'code':"必填",
			    	'name': "必填",
			    	'weight':"必填",
			    	shortTitle:{
		     			maxlength: "最长30字符"
			     	},
			    	summary:{
			     		maxlength : "最长60字符"
				    }
				}
				});
	     }

		//提交
		function submitFormDept(){
			checkDeptName(checkDeptCode);
		}
		function saveDept(){
			$("#inputForm").attr("action", "${acsCtx}/organization/department!saveDepartment.action");
			ajaxSubmit('inputForm', '${acsCtx}/organization/department!saveDepartment.action', 'acs_content', refreshAfterDeleteDept);
		}

		function checkDeptName(callback){
			$.ajax({
				type : "POST",
				url : "department!checkDeptName.action",
				data:{departmentName:$("#departmentName").val(),id:$("#id").val()},
				success : function(data){
		            if( data != "true" ){   
		            	$("#departmentName").val("");
		            	alert(data+"已注册，请更换部门名称 ");
					}else{
						if( typeof(callback)=="function"){
							callback(function(){$('#inputForm').submit();});
						}
					}
				},
				error: function(){alert("错误");},
				onerror : "",
				onwait : "正在对部门名称进行合法性校验，请稍候..."
			});
		}

		/*
		*验证部门编号
		*/
		function checkDeptCode(callback){
			$.ajax({
				type : "POST",
				url : "department!checkDeptCode.action",
				data:{departmentCode:$("#departmentCode").val(),id:$("#id").val()},
				success : function(data){
		            if( data != "true" ){   
		            	$("#departmentCode").val("");
		            	alert(data+"已注册，请更换部门编号 ");
					}else{
						if( typeof(callback)=="function"){
							callback();
						}
					}
				},
				error: function(){alert("错误");},
				onerror : "",
				onwait : "正在对部门编号进行合法性校验，请稍候..."
			});
		}

		//修改部门
		function updateDept(){
			var deptId = $('#tree_selected_id').attr('value');
			if(deptId==''){
				alert('请在左边树上选择部门');
				return;
			}
			$('#tree_selected_id').attr('value', '');
			ajaxSubmit('ajax_from', '${acsCtx}/organization/department!input.action?id='+deptId, 'acs_content', updateDeptCallBack);
		}

		//删除部门
		function deleteDept(){
			var deptId = $('#tree_selected_id').attr('value');
			var nodeId = $('#tree_selected_node').attr('value');
			if(deptId==''){
				alert('请在左边树上选择部门');
				return;
			}
			var child=parent.$("#company_department").find(".jstree-clicked").parent().find("ul");
			if(child.length>0){
				alert('请先删除子部门');
				return;
			}
			 if(confirm("确定要删除部门吗？")){
				$("#ajax_from").attr("action", "${acsCtx}/organization/department!delete.action?departmentId=" + deptId);
				setPageState();
				ajaxSubmit('ajax_from', '${acsCtx}/organization/department!delete.action?departmentId='+deptId, 'acs_content', refreshAfterDeleteDept);
				//$("#ajax_from").submit();
			}
		}

		function refreshAfterDeleteDept(){
			parent.jQuery.jstree._reference("#company_department").destroy();
			parent.initDepartmentTree();
		}

		//添加用户
		function addUsers(){
			var deptId = $('#tree_selected_id').attr('value');
			if(deptId==''){
				alert('请在左边树上选择部门');
				return;
			}
			ajaxSubmit('ajax_from', '${acsCtx}/organization/department!addDepartmentToUsers.action?departmentId='+deptId, 'acs_content',getContentHeight);
		}

		//部门人员树
		function loadUserTree(){
			$.ajaxSetup({cache:false});
		    $("#user_tree").jstree({ 
				"plugins" : [ "themes", "json_data" ,"checkbox","ui"],
				 "themes" : {  
					 "theme" : "default",  
					 "dots" : true,  
					 "icons" : true 
				},  
				"json_data" : { 
					"ajax" : {
						"url" : "${acsCtx}/organization/department!getCompanyNodes.action",
						"data" : function (n) {
							return { currentId : n.attr ? n.attr("id") :"INITIALIZED_USERS" };
						}  
					}
				}
			});
		    getContentHeight();
		}

		//移除用户
		function removeUsers(){
			var deptId = $('#tree_selected_id').attr('value');
			if(deptId==''){
				alert('请在左边树上选择部门');
				return;
			}
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			var isSelectedUser = false; 
			if(uIds==''){
				alert('请选择要移除的用户');
				return;
			}else{
				$('#formName').attr('action', '${acsCtx}/organization/department!removeDepartmentToUsers.action');
				var deptId = $('#tree_selected_id').attr('value');
				var inpt = document.createElement("input");
				inpt.setAttribute("name", "departmentId");
				inpt.setAttribute("value", deptId);
				inpt.setAttribute("type", "hidden");
				document.getElementById("formName").appendChild(inpt);
				for(var i=0;i<uIds.length;i++){
					var inpt1 = document.createElement("input");
					inpt1.setAttribute("name", "userIds");
					inpt1.setAttribute("value", uIds[i]);
					inpt1.setAttribute("type", "hidden");
					document.getElementById("formName").appendChild(inpt1);
				}
				$('#formName').submit();
			}
		}

		//提交为部门添加的用户，过滤掉已在该部门的用户
		function deptAddUserSubmit(){
			var users = getInfo("user");
			var userArr = eval(users);
			if(typeof(userArr)=='undefined'){
                 alert("请选择用户!");  
                 return;
			}
			var resultids="";
			var hasEffectiveUser = false;//是否已有用户，true：表示有，false表示没有
			for(var i=0;i<userArr.length;i++){
				if(userArr[i].type=="company"){
					resultids="0";
					var inpt = document.createElement("input");
					inpt.setAttribute("name", "userIds");
					inpt.setAttribute("value", "0");
					inpt.setAttribute("type", "hidden");
					document.getElementById("deptAddUserForm").appendChild(inpt);
					break;
				}else if(userArr[i].type=="user"){
					if(resultids.indexOf(userArr[i].id+",")<0){
						resultids = resultids+","+userArr[i].id;
						hasEffectiveUser=false;
					}else{
						hasEffectiveUser=true;
					}
					if(!hasEffectiveUser){
						var inpt = document.createElement("input");
						inpt.setAttribute("name", "userIds");
						inpt.setAttribute("value", userArr[i].id);
						inpt.setAttribute("type", "hidden");
						document.getElementById("deptAddUserForm").appendChild(inpt);
					}
				}
			}
			if(resultids!=""){
				$('#deptAddUserForm').submit();
			}else{
				alert("请选择用户");
			}
		}

		function allUsers(id){
	    	var lists = $("#"+id).find("li.jstree-checked");
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

		function setParentDeptInfo(id, name){
			$('#parentDepartmentId').attr('value', id);
			$('#parentDepartmentName').attr('value', name);
		}
	</script>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		<form action="#" id='defaultForm' name="defaultForm"></form>
		<form id="ajax_from" name="ajax_from" action="" method="post">
			<input type="hidden" id="tree_selected_id" name="parentId" value="${departmentId}">
			<input type="hidden" id="tree_selected_node" name="treeSelectedNode" value="${treeSelectedNode }">
		</form>
		<aa:zone name="acs_content">
			<div class="opt-btn">
				<security:authorize ifAnyGranted="createDepartment">
			        <button  class='btn' onclick="createDept();"><span><span><s:text name="common.create" /></span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="editDepartment">
			        <button  class='btn' onclick="updateDept();"><span><span><s:text name="common.alter" /></span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="deleteDepartment">
			        <button  class='btn' onclick="deleteDept();"><span><span><s:text name="common.deleteDepartment" /></span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="addDepartmentToUser">
			        <button  class='btn' onclick="addUsers();"><span><span><s:text name="department.addUser" /></span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="departmentRemoveUser">
			        <button  class='btn' onclick="removeUsers();"><span><span><s:text name="department.removeUser" /></span></span></button>
				</security:authorize>
			</div>
			<div id="opt-content" >
				<form name="formName" id="formName"></form>
				<view:jqGrid url="${acsCtx}/organization/department.action?departmentId=${departmentId}" pageName="userPage" code="DEPART_LIST_USER" gridId="main_table"></view:jqGrid>
			</div>
		</aa:zone>
	</div>
</div>   	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
