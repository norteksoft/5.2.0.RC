<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
   
	<title>角色管理</title>
     <%@ include file="/common/acs-iframe-meta.jsp"%>
      
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/validation/cmxform.css"/>
	<style type="text/css">
		.message{ padding: 2px;color: red;}
	</style>
	<script type="text/javascript">
		function loadContent(treeId){
			var sysId = treeId.split('_')[1];
			$('#_systemId').attr('value', sysId);
			$('#businessSystemId').attr('value', sysId);
			if($("#role_table").attr('id') != "role_table"){
				returnRoleList();
			}else{
				if($("#role_table").attr('init') == '0'){
					initRoleTable();
					$("#role_table").attr('init', '1');
				}else{
					reloadRoleTable();
				}
			}
		}

		function createRole(){
			ajaxSubmit('roleForm', '${acsCtx}/authorization/role!input.action', 'acs_button,acs_content', roleValidate);
		}
		function updateRole(){
			var roles = jQuery("#main_table").getGridParam('selarrrow');
			if(roles.length == 1){
				$('#role_id').attr('value', roles);
				ajaxSubmit('roleForm', '${acsCtx}/authorization/role!input.action', 'acs_button,acs_content', roleValidate);
			}else{
				showMessage('role_msg', '请选择一个要修改的角色');
			}
		}
		function deleteRole(){
			var roles = jQuery("#main_table").getGridParam('selarrrow');
			if(roles.length > 0){
				if(deletable(roles)){
					for(var i=0; i<roles.length; i++){
						$("#roleForm").html($("#roleForm").html()+'<input type="hidden" name="roleIds" value="'+roles[i]+'">');
					}
					setPageState();
					ajaxSubmit('roleForm', '${acsCtx}/authorization/role!delete.action', 'acs_content');
				}else{
					showMessage('role_msg', '三员角色不允许删除 (acsSystemAdmin、acsSecurityAdmin、acsAuditAdmin)');
				}
			}else{
				showMessage('role_msg', '请选择要删除的角色');
			}
		}
		function deletable(roles){
			for(var i=0; i<roles.length;i++){
				var roleCode = $('tr#'+roles[i]+' td:eq(1)').attr('title');
				if(roleCode=='acsSystemAdmin'||roleCode=='acsSecurityAdmin'||roleCode=='acsAuditAdmin'){
					return false;
				}
			}
			return true;
		}
		function addResource(){
			var roles = jQuery("#main_table").getGridParam('selarrrow');
			if(roles.length == 1){
				$.colorbox({href:'${acsCtx}/authorization/role!roleToFunctionList.action?roleId='+roles[0],
					 iframe:true, innerWidth:500, innerHeight:400,overlayClose:false,title:"添加资源"});
			}else{
				showMessage('role_msg', '请选择一个要添加资源的角色');
			}
		}
		function removeResource(){
			var roles = jQuery("#main_table").getGridParam('selarrrow');
			if(roles.length == 1){
				$.colorbox({href:'${acsCtx}/authorization/role!roleRomoveFunctionList.action?roleId='+roles[0],
					 iframe:true, innerWidth:500, innerHeight:400,overlayClose:false,title:"移除资源"});
			}else{
				showMessage('role_msg', '请选择一个要移除资源的角色');
			}
		}
		function saveRole(){
			   $('#roleForm').submit();
		}
		function validateRolename(name){
			if(name.indexOf("(")>=0||name.indexOf(")")>=0||name.indexOf("()")>=0
					||name.indexOf("（")>=0||name.indexOf("）")>=0||name.indexOf("（）")>=0){
				return false;
			}
			return true;
		}
		function returnRoleList(){
			ajaxSubmit('ajax_from', '${acsCtx}/authorization/role.action', 'acs_button,acs_content');
		}
		function ajaxSubmit(form, url, zoons, ajaxCallback){
			var formId = "#"+form;
			if(url != ""){
				$(formId).attr("action", url);
			}
			ajaxAnywhere.formName = form;
			ajaxAnywhere.getZonesToReload = function() {
				return zoons;
			};
			ajaxAnywhere.onAfterResponseProcessing = function () {
				if(typeof(ajaxCallback) == "function"){
					ajaxCallback();
				}
			};
			ajaxAnywhere.submitAJAX();
		}
		function showMessage(id, msg){
			if(msg != ""){
				$("#"+id).html(msg);
			}
			$("#"+id).show("show");
			setTimeout('$("#'+id+'").hide("show");',3000);
		}

		function roleValidate(){
			$("#roleForm").validate({
				submitHandler: function() { 
				if(validateRolename($("#roleName").val())){
					    ajaxSubmit('roleForm', '${acsCtx}/authorization/role!save.action', 'acs_button,acs_content',roleValidate); 
					}else{
						$("#roleName").parent().append('<label  class="error">角色名称中不能包含括号！</label>');
					}
			},
			rules: {
				code: {
					required: true
				},
				name: {
					required: true
				}
				},
			messages: {
					'code':"必填",
			    	'name': "必填"
				}
		});
			showMessage('message');
		}
	</script>
</head>

<body>
<div class="ui-layout-center">
			<div class="opt-body">
				<form id="ajax_from" name="ajax_from" action="" method="post">
					<input type="hidden" id="_systemId" name="businessSystemId" value="${businessSystemId}" />
				</form>
				<aa:zone name="acs_button">
				<div class="opt-btn">
					<security:authorize ifAnyGranted="editRole"><button  class='btn' onclick="createRole();"><span><span>新建</span></span></button></security:authorize>
					<security:authorize ifAnyGranted="editRole"><button  class='btn' onclick="updateRole();"><span><span>修改</span></span></button></security:authorize>
					<security:authorize ifAnyGranted="acs_deleteRoles"><button  class='btn' onclick="deleteRole();"><span><span>删除</span></span></button></security:authorize>
					<security:authorize ifAnyGranted="acs_roleToFunction"><button  class='btn' onclick="addResource();"><span><span>添加资源</span></span></button></security:authorize>
					<security:authorize ifAnyGranted="roleRemoveFunction"><button  class='btn' onclick="removeResource();"><span><span>移除资源</span></span></button></security:authorize>
				</div>
				<div id="role_msg" style="color: red;"></div>
				</aa:zone>
				<aa:zone name="acs_content">
				<div id="message"><s:actionmessage theme="mytheme"/></div>
					<form id="roleForm" name="roleForm" action="#">
					<input type="hidden" name="id" id="role_id"/>
					<input type="hidden" id="businessSystemId" name="businessSystemId" value="${businessSystemId}" />
					<input type="hidden" id="ids" name="ids"/>
					</form>
				<div id="opt-content" >
					<view:jqGrid url="${acsCtx}/authorization/role.action?businessSystemId=${businessSystemId}" code="ACS_ROLES" pageName="page" gridId="main_table"></view:jqGrid>
				</div>	
				</aa:zone>
			</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
	