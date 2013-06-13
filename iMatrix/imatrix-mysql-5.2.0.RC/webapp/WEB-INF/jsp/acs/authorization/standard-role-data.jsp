<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ taglib uri="http://ajaxanywhere.sourceforge.net/" prefix="aa" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
   
    <%@ include file="/common/acs-iframe-meta.jsp"%>
	
	<link href="${resourcesCtx}/widgets/colorbox/colorbox.css" rel="stylesheet" type="text/css"/>
	<script src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js" type="text/javascript"></script>
	
	<script type="text/javascript" src="${resourcesCtx}/widgets/jstree/jquery.jstree.js"></script>
      
	<style type="text/css">
		table.full{
			border:1px solid #C5DBEC;
			border-collapse:collapse;
			width: 100%;
		}
		table.full thead tr{
			background: url("../../images/ui-bg_glass_85_dfeffc_1x400.png") repeat-x scroll 50% 50% #DFEFFC;
			height:20px;
			width: 33.3%;
			color:#2E6E9E;
			font-weight:bold;
		}
		table.full thead tr th, table.full tbody tr td { border:1px solid #C5DBEC;}
		table.full thead tr th {  text-align: left; }
		table.full thead tr th,table.full tbody tr td{ padding-left: 20px;}
		.sng_row li {
			list-style:none outside none;
			text-align:left;
			line-height:1.5em;
		}
	</style>
	<script type="text/javascript">
		function opt(operate){
			var roleId = $("#id").attr("value");
			if(operate == "ADD_USER"){
				$("#ajax_from").attr("action", "${acsCtx}/authorization/role!listUsers.action");
			}else if(operate == "ADD_DEPARTMENT"){
				$("#ajax_from").attr("action", "${acsCtx}/authorization/role!listDepartments.action");
			}else if(operate == "ADD_WORKGROUP"){
				$("#ajax_from").attr("action", "${acsCtx}/authorization/role!roleToWorkGroupList.action");
			}else if(operate == "REMOVE_ALL"){
				if(validateRemove()) return;
				if(confirm("确定要从角色中移除所选项?")){
					$("#removeForm").submit();
					return;
				}else{ return; }
			}else{ return ;}
			ajaxAnywhere.formName = "ajax_from";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_button,acs_content";
			};
			ajaxAnywhere.onAfterResponseProcessing = function() {
				getContentHeight();
	 			showMsg();
	 		};
			ajaxAnywhere.submitAJAX();
		}
		function validateRemove(){
			var userList = document.getElementsByName("userIds");
			var deptList = document.getElementsByName("departmentsIds");
			var workgroupList = document.getElementsByName("workGroupIds");
			for(var i=0; i<userList.length; i++){
				var name=$("#name_"+$(userList[i]).attr("value")).html();
				if($(userList[i]).attr("checked")){
					var roleName=$("#roleName").val();
				if((name=="systemAdmin"&&roleName=="系统管理员")||(name=="securityAdmin"&&roleName=="安全管理员")||(name=="auditAdmin"&&roleName=="审计管理员")){ 
					alert("系统默认的管理员不能删除！");
					return true;
				}
				}
			}
			for(var i=0; i<userList.length; i++){
				if($(userList[i]).attr("checked")) return false; 
			}
			for(var i=0; i<deptList.length; i++){
				if($(deptList[i]).attr("checked")) return false;
			}
			for(var i=0; i<workgroupList.length; i++){
				if($(workgroupList[i]).attr("checked")) return false;
			}
			alert("请选择需要移除的项");
			return true;
		}
		function checkAll(obj){
			if($(obj).attr('id') == 'acs_users'){
				checkEveryOne(obj, 'userIds');
			}else if($(obj).attr('id') == 'acs_depts'){
				checkEveryOne(obj, 'departmentsIds');
			}else if($(obj).attr('id') == 'acs_workgroups'){
				checkEveryOne(obj, 'workGroupIds');
			}
		}
		function checkEveryOne(obj, name){
			var ids = $('input[name='+name+']');
			if($(obj).attr('checked')){
				for(var i = 0; i < ids.length; i++){
					var isAdmin = $(ids[i]).attr("aa");
					if(!isAdmin){
					   $(ids[i]).attr('checked', 'checked');
					}
				}
			}else{
				for(var i = 0; i < ids.length; i++){
					$(ids[i]).attr('checked', '');
				}
			}
		}

		function submitFormAuthorization(type){
			var value=getInfo();
			if(value!=""){
				var arr=eval(getInfo());
				for(var i=0;i<arr.length;i++){
					if(type=="user" && (arr[i].type=="user" || arr[i].type=="allDepartment" || arr[i].type=="company" )){
						var inpt = document.createElement("input");
						if(arr[i].type=="user"){
							inpt.setAttribute("name", "userIds");
							inpt.setAttribute("value", arr[i].id);
						}else if(arr[i].type=="allDepartment" || arr[i].type=="company"){
							inpt.setAttribute("name", "allInfos");
							inpt.setAttribute("value", type+"_"+arr[i].id);
						}
						inpt.setAttribute("type", "hidden");
						document.getElementById("acsAddFrom").appendChild(inpt);
					}else if(type=="department" && (arr[i].type=="department" || arr[i].type=="allDepartment" || arr[i].type=="company")){
						var inpt = document.createElement("input");
						if(arr[i].type=="department"){
							inpt.setAttribute("name", "departmentsIds");
							inpt.setAttribute("value", arr[i].id);
						}else if(arr[i].type=="allDepartment" || arr[i].type=="company"){
							inpt.setAttribute("name", "allInfos");
							inpt.setAttribute("value", type+"_"+arr[i].id);
						}
						inpt.setAttribute("type", "hidden");
						document.getElementById("acsAddFrom").appendChild(inpt);
					}else if(type=="workGroup" && (arr[i].type=="workGroup" || arr[i].type=="allWorkGroup" || arr[i].type=="company")){
						var inpt = document.createElement("input");
						if(arr[i].type=="workGroup"){
							inpt.setAttribute("name", "workGroupIds");
							inpt.setAttribute("value", arr[i].id);
						}else if(arr[i].type=="allWorkGroup" || arr[i].type=="company"){
							inpt.setAttribute("name", "allInfos");
							inpt.setAttribute("value", type+"_"+arr[i].id);
						}
						inpt.setAttribute("type", "hidden");
						document.getElementById("acsAddFrom").appendChild(inpt);
					}
				}
				$("#acsAddFrom").submit();
			}else{
				if(type=="user"){
					alert('<s:text name="user.seleteUser"/>');
				}else if(type=="department"){
					alert('<s:text name="department.selectDepartment"/>');
				}else if(type=="workGroup"){
					alert('<s:text name="workGroup.selectWorkGroup"/>');
				}
				return;
			}
		}

		function loadTreeContent(type, id){
			if(type == "BUSINESSSYSTEM"){
				return;
			}else if(type == "ROLE"){
				$("#id").attr("value", id);
				$("#roleId").attr("value", id);
				$("#ajax_from").attr("action", "${acsCtx}/authorization/standard-role!authoritys.action");
			}else return;
			ajaxAnywhere.formName = "ajax_from";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_button,acs_content,acs_footer";
			};
			ajaxAnywhere.submitAJAX();
		}

		function returnList(roleId){
			$("#ajax_from").attr("action", "${acsCtx}/authorization/standard-role!authoritys.action");
			ajaxAnywhere.formName = "ajax_from";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_button,acs_content";
			};
			ajaxAnywhere.submitAJAX();
		}
		function loadContent(obj){
			var index = obj.indexOf("_");
			loadTreeContent(obj.substring(0, index), obj.substring(index+1, obj.length));
		}
		//导出
		function exportRole(){
			ajaxSubmit("ajax_from","${acsCtx}/authorization/standard-role!exportRole.action");
		}
	</script>
</head>

<body onload="getContentHeight();">
<div class="ui-layout-center">
<div class="opt-body">
				<aa:zone name="acs_button">
				<div class="opt-btn">
					<security:authorize ifAnyGranted="roleToUser"><button  class='btn' onclick="opt('ADD_USER')"><span><span>添加用户</span></span></button></security:authorize>
					<s:if test="!isAdminRole">
						<security:authorize ifAnyGranted="roleToDepartment"><button  class='btn' onclick="opt('ADD_DEPARTMENT')"><span><span>添加部门</span></span></button></security:authorize>
						<security:authorize ifAnyGranted="roleToWorkGroup"><button  class='btn' onclick="opt('ADD_WORKGROUP')"><span><span>添加工作组</span></span></button></security:authorize>
					</s:if>
					<security:authorize ifAnyGranted="standardRoleRemoveUser"><button  class='btn' onclick="opt('REMOVE_ALL')"><span><span>移除</span></span></button></security:authorize>
					<security:authorize ifAnyGranted="exportRole"><button  class='btn' onclick="exportRole()"><span><span>导出</span></span></button></security:authorize>
				</div>
				</aa:zone>
					
				<aa:zone name="acs_content">
				<div id="opt-content">
					<div id="message"><s:actionmessage theme="mytheme"/></div>
					<script type="text/javascript">
						setTimeout('$("#message").hide();',3000);
					</script>
					<form id="ajax_from" name="ajax_from" action="" method="post">
						<input type="hidden" id="businessSystemId" name="businessSystemId" value="${businessSystemId}" />
						<input type="hidden" id="roleId" name="roleId" value="${roleId}"/>
						<input type="hidden" id="id" name="id" value="${roleId}"/>
					</form>
					<input type="hidden" id="roleName" name="name" value=""/>
					<form id="removeForm" name="removeForm" action="${acsCtx}/authorization/role!removeUsers.action">
					<input type="hidden" name="roleId" value="${roleId}">
						<div style="padding: 5px;">
							<table border="1" cellpadding="0" cellspacing="0" class="leadTable">
								<thead>
									<tr>
										<th style="text-align:left; padding-left:4px;"><input type="checkbox" id="acs_users" onclick="checkAll(this)"/><s:text name="user.user"/></th>
										<th style="text-align:left; padding-left:4px;"><input type="checkbox" id="acs_depts" onclick="checkAll(this)"/><s:text name="department.department"/></th>
										<th style="text-align:left; padding-left:4px;"><input type="checkbox" id="acs_workgroups" onclick="checkAll(this)"/><s:text name="workGroup.workGroup"/></th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td valign="top">
											<ul style="list-style-type:none;margin:0;padding-left: 0px;">
											<s:iterator value="users">
											<li>
											<s:if test="defaultAdmin.contains(loginName)&&isAdminRole==true">
											   <input type="checkbox" disabled="disabled" aa="admin" name="userIds" value="${id}" />
											</s:if>
											<s:else>
											   <input type="checkbox" name="userIds" value="${id}" />
											</s:else>
											<span id="name_${id}">${name}</span>
											</li>
											</s:iterator>
											</ul>
										</td>
										<td>
											<ul style="list-style-type:none;margin:0;padding-left: 0px;">
											<s:iterator value="departments">
											<li>
											<input type="checkbox" name="departmentsIds" value="${id}" />
											<span>${name}</span>
											</li>
											</s:iterator>
											</ul>
										</td>
										<td>
											<ul style="list-style-type:none;margin:0;padding-left: 0px;">
											<s:iterator value="workgroups">
											<li>
											<input type="checkbox" name="workGroupIds" value="${id}" />
											<span>${name}</span>
											</li>
											</s:iterator>
											</ul>
										</td>
									</tr>
								</tbody>
							</table>
						</div>	
					</form>
				</div>
				<style type="text/css">.leadTable{ background-color: #FCFDFD; } .leadTable tbody tr:hover, .leadTable tbody td:hover a{ background-color: #FCFDFD; } </style>
				</aa:zone>
			</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
	