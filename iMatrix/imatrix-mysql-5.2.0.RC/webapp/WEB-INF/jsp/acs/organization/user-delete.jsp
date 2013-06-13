<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
   <head>
      <title><s:text name="user.userManager" /></title>
     <%@ include file="/common/acs-iframe-meta.jsp"%>
      <script type="text/javascript">
/**
		$(function () {
			$.ajaxSetup({cache:false});
		    $("#menu_tree").tree({
		    	ui : {
					theme_path:"${acsCtx}/widgets/jquerytree/themes/",
					context : []
				},
				data:{
			        type:"json",
			        url:"${ctx}/organization/load-tree!loadDepartmentTree.action",
			        async:true,
			        async_data:function (NODE){ return {currentId:$(NODE).attr("id") || "INITIALIZED_USERS"};}
			    },
			    callback : {
			    	onselect : function(NODE, TREE_OBJ) { 
			    		ayalisisTreeId($(NODE).attr("id")); 
			    	}
			    }
		    });
		});
*/
      function loadTreeContent(type, id){
			if(type=='USERSBYDEPARTMENT'){
			   $("#ajax_from").attr("action", "${acsCtx}/organization/user!getUserByDepartment.action?departmentId="+id);
			}
			if(type=='DEPARTMENTS'){
			   $("#ajax_from").attr("action", "${acsCtx}/organization/department!getDepartmentByCompany.action?companyId="+id);
			}
			if (type == 'DELETED_USER') {
				$("#ajax_from").attr("action","${acsCtx}/organization/user!deleteList.action");
			}
				
			ajaxAnywhere.formName = "ajax_from";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_button,acs_content,acs_footer";
			};
			ajaxAnywhere.onAfterResponseProcessing = function () {
				tableView();
			};
			ajaxAnywhere.submitAJAX();
		}


		function ayalisisTreeId(treeId){
			var index = treeId.indexOf(",");
			loadTreeContent(treeId.substring(0, index), treeId.substring(index+1, treeId.length));
		}
    
   
    	function itde() {
    		checkset = document.getElementsByName("userIds");
    		var result = 0;
    		var itemId = 0;
    		for ( var i = 0; i < checkset.length; i++) {
    			if (checkset[i].checked == true) {
    				result++;
    				$("#ajaxId").val(checkset[i].value);
    				break;
    			}
    		}
    		if (result == 0) {
    			alert('<s:text name="common.selectOne"/>');
    			return false;
    		}
    		return true;
    	}
    	//ajax提交方法
      	function deleteUser(url_) {
      		var ids = jQuery("#main_table").getGridParam('selarrrow');
    		if(ids==''){
    			alert("请先选择");
    			return;
    		}else{
    			if(confirm("确认删除吗？")){
    				ajaxSubmit("ajax_from",webRoot+"/organization/user!delete.action?ids="+ids.join(','),"acs_content");
    			}
    		}
      	}

      	function Dtree(){
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			if(uIds==''){
				 $("#notice").html("<span>请选择用户！</span>");
				 $("#notice").children("span").fadeOut(5000);
				 return;
			}else{
				$.colorbox({href:'${acsCtx}/organization/user!toDepartmentToUsersDel.action?ids='+uIds,iframe:true, innerWidth:600, innerHeight:500,overlayClose:false,title:"请选择部门"});
			}
		}
    	function deleteUserToDept(deptId){
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			if(uIds==''){
				 alert("请先选择");
			}else{
				var userIds = "";
				for(var i=0;i<uIds.length;i++){
                   userIds+=uIds[i]+",";
				}
				userIds=userIds.substring(0,userIds.length-1);
			     //ajaxSubmit('deleteUserAddDeptForm', 'deleteUserAddDeptForm', '${acsCtx}/organization/user!saveDepartmentToUserDel.action?ids='+userIds+"&departmentId="+deptId, 'acs_content');
			     $("#deleteUserAddDeptForm").attr("action","${acsCtx}/organization/user!saveDepartmentToUserDel.action?ids="+userIds+"&departmentId="+deptId);
			     ajaxAnywhere.formName = "deleteUserAddDeptForm";
					ajaxAnywhere.getZonesToReload = function() {
						return "acs_content";
					};
					ajaxAnywhere.onAfterResponseProcessing = function () {
						//tableView();
					};
					ajaxAnywhere.submitAJAX();
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
			ajaxSubmit("ajax_from", webRoot+'/organization/user!inputLook.action', "acs_content",getContentHeight);
		}

		//取消
		function cancel(){
		    $('#cancelForm').attr('action', '${acsCtx}/organization/user!deleteList.action');
			ajaxAnywhere.formName = "cancelForm";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_content";
			};
			ajaxAnywhere.onAfterResponseProcessing = function() {
				$('input').attr('disabled', '');
				$('select').attr('disabled', '');
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
	</form>
	<aa:zone name="acs_content">
		<div class="opt-btn">
			<security:authorize ifAnyGranted="deleteUserAll">
				<button  class='btn' onclick="deleteUser('${acsCtx}/organization/user!delete.action');"><span><span><s:text name="user.deleteComplate" /></span></span></button>
		    </security:authorize>
		    <security:authorize ifAnyGranted="addUserDeleteToDepartment"> 
		    	<button  class='btn' onclick="Dtree();"><span><span><s:text name="department.addDepartment" /></span></span></button>
		    	<!--  <button  class='btn' onclick="submit_r();"><span><span>提交</span></span></button> -->
		    </security:authorize>
		</div>
		<div id="notice"></div>
		<div id="opt-content">
			<form id="deleteUserAddDeptForm" name="deleteUserAddDeptForm" action="" method="post">
			    <input type="hidden" name="departmentIds" id="departmentIds" >
			         <aa:zone name="acs_table">   
						<view:jqGrid url="${acsCtx}/organization/user!deleteList.action?workGroupId=${workGroupId }&departmentId=${departmentId}&departmentIds=${oldDid}&oType=${oldType }&departmType=${departmType }" pageName="page" code="ACS_USER" gridId="main_table"></view:jqGrid>
					</aa:zone>
			</form>
		</div>
	</aa:zone>
</div>	
</div>		
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
