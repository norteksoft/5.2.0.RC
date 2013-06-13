<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title>用户管理</title>
	<script  type="text/javascript" src="${imatrixCtx}/widgets/calendar/WdatePicker.js"></script>
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/validation/cmxform.css"/>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	<script type="text/javascript">
		//工作组增加用户
		function addUsersToWorkgroup(){
			var workgroupId = $('#workGroupId').attr('value');
			if(workgroupId==''){
				alert('请在左边树上选择工作组');
				return;
			}
			ajaxSubmit('ajax_from', '${acsCtx}/organization/work-group!addUsersToWorkgroup.action', 'acs_content',getContentHeight);
		}
		//提交为工作组添加的用户
		function workgroupAddUserSubmit(){
			var users = getInfo("user");
			var userArr = eval(users);
			if(typeof(userArr)=='undefined'){
                alert("请选择用户！");return;
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
					document.getElementById("workgroupAddUserForm").appendChild(inpt);
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
						document.getElementById("workgroupAddUserForm").appendChild(inpt);
					}
				}
			}
			if(resultids!=""){
				$('#workgroupAddUserForm').submit();
			}else{
				alert("请选择用户");
			}
			
		}

		//移除用户
		function removeUsersToWorkgroup(){
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			var isSelectedUser = false; 
			if(uIds==''){
				alert('请选择要移除的用户');
				return;
			}else{
				$('#formName').attr('action', '${acsCtx}/organization/work-group!removeWorkgroupToUsers.action');
				var workGroupId = $('#workGroupId').attr('value');
				var inpt = document.createElement("input");
				inpt.setAttribute("name", "workGroupId");
				inpt.setAttribute("value", workGroupId);
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
		//查看用户表
		function viewUser(ts1,cellval,opts,rwdat,_act){
			var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"_click_fun("+opts.id+");\">" + ts1 + "</a>";
			return v;
		}
		
		function _click_fun(id){
			$("#ajaxId").attr("value",id);
			$("#look").attr("value","LOOK");
			$("#fromWorkgroup").attr("value","fromWorkgroup");
			ajaxSubmit("ajax_from", webRoot+'/organization/user!inputLook.action', "acs_content", getContentHeight);
		}
		//取消
		function cancel(){
		    var i=$("#departId").val();
		    var t=$("#oType").val();
		    $("#departmId").attr("value",i);
		    $("#departmType").attr("value",t);
		    if(t=='DELETED_USER'){
			    $('#cancelForm').attr('action', '${acsCtx}/organization/user!deleteList.action');
			}
			ajaxAnywhere.formName = "cancelForm";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_content";
			};
			ajaxAnywhere.onAfterResponseProcessing = function() {
				$('input').attr('disabled', '');
				$('select').attr('disabled', '');
				//initUserTable();
			};
			ajaxAnywhere.submitAJAX(); 
		}
	</script>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		 <form id="ajax_from" name="ajax_from" action="" method="post">  
		    <input type="hidden" name="userId" id="ajaxId" />
		    <input type="hidden" name="look" id="look" />
		    <input type="hidden" name="fromWorkgroup" id="fromWorkgroup" >
	        <input type="hidden" name="workGroupId" id="workGroupId" value="${workGroupId}">
		 </form>
		<aa:zone name="acs_content">
			<div class="opt-btn">
				<security:authorize ifAnyGranted="getUserByWorkGroup ">
					<button  class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="addWorkGroupToUser">
			        <button  class='btn' onclick="addUsersToWorkgroup();"><span><span><s:text name="workGroup.addUser" /></span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="workGroupRemoveUser">
				    <button  class='btn' onclick="removeUsersToWorkgroup();"><span><span><s:text name="workGroup.removeUser" /></span></span></button>
				</security:authorize>
			</div>
			<div id="notice"></div>	
			<div id="opt-content" >
			   <aa:zone name="acs_list">
				<form id="formName" name="formName" action="" method="post">
				     <view:jqGrid url="${acsCtx}/organization/work-group!getUserByWorkGroup.action?workGroupId=${workGroupId }" pageName="userPage" code="ACS_USER" gridId="main_table"></view:jqGrid>
				</form>
				</aa:zone>
			</div>
		</aa:zone>
	</div>
</div>  	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>

</html>
