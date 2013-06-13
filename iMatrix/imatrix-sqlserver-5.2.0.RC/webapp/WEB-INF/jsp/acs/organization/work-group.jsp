<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title>用户管理</title>
	<script  type="text/javascript" src="${imatrixCtx}/widgets/calendar/WdatePicker.js"></script>
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/validation/cmxform.css"/>
	
	<script type="text/javascript">
		//新建
		function opt2(){                 
			$("#ajax_from").attr("action", "${acsCtx}/organization/work-group!inputWorkGroup.action");
			ajaxAnywhere.formName = "ajax_from";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_content";
			};
			ajaxAnywhere.onAfterResponseProcessing = function () {
				validateWork();
			};
			ajaxAnywhere.submitAJAX();
		}

		function validateWork(){
			$("#inputForm").validate({
				submitHandler: function() { saveWorkgroup(); },
				rules: {
				    code: "required",
				    name: "required",
				    description: {
	    				maxlength : 60
	    			}
    		     },messages: {
			    	'code':"必填",
			    	'name': "必填",
			    	description:{
			     		maxlength : "最长60字符"
				    }
				}
				});
		}
		function saveWorkgroup(){
			ajaxSubmit('inputForm', '${acsCtx}/organization/work-group!saveWorkGroup.action', 'acs_content', refreshWg);
		}
		
		//修改
		function opt(_url, opt, id){
			if(opt=='LOOK'){
				viewUser(_url, opt, id);
				return;
			}

			var ids = jQuery("#main_table").getGridParam('selarrrow');
			if(ids==''){
				alert("请先选择");
			}else if(ids.length > 1){
				 alert("只能选择一条！");
			}else{
				$("#id").attr("value",ids[0]);
				$("#ajax_from").attr("action", "${acsCtx}/organization/work-group!input.action");
				ajaxAnywhere.formName = "ajax_from";
				ajaxAnywhere.getZonesToReload = function() {
					return "acs_content";
				};
				ajaxAnywhere.onAfterResponseProcessing = function () {
					validateWork();
				};
				ajaxAnywhere.submitAJAX();
			}
		}

		function viewUser(_url, opt, id){
			$("#ajax_from").attr("action", _url+'?id='+id);
			ajaxAnywhere.formName = "ajax_from";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_content";
			};
			ajaxAnywhere.onAfterResponseProcessing = function () {
				$('#cpn_from').css('display', 'none');
				$('input').attr('disabled', 'disabled');
				$('select').attr('disabled', 'disabled');
			};
			ajaxAnywhere.submitAJAX();
		}

		var checkset;
		function itde() {
			checkset = document.getElementsByName("ids");
			var result = 0;
			for ( var i = 0; i < checkset.length; i++) {
				if (checkset[i].checked == true) {
					result++;
					$("#ajaxId").val(checkset[i].value);
				}
			}
			if (result == 0) {
				alert('<s:text name="common.selectOne"/>');
				return false;
			}else if(result > 1){
				alert('只能选择一项');
				return false;
			}
			return true;
		}

		//删除
		function optDelete(id, opt){     
			var ids = jQuery("#main_table").getGridParam('selarrrow');
			if(ids==''){
				alert("请先选择");
			}else{
				if(confirm("确定删除工作组？")){
					//delRow();
					var deleteResult = "";
					for(var i=0;i<ids.length;i++){
						deleteResult+=ids[i]+",";
					}
					$("#_delete_Id").attr("value",deleteResult.substring(0,deleteResult.length-1));
					setPageState();
					ajaxSubmit('ajax_from', '${acsCtx}/organization/work-group!delete.action', 'acs_content', refreshAfterDeleteWG);
				}
			}
		}
		function refreshAfterDeleteWG(){
			refreshWg();
			parent.jQuery.jstree._reference("#company_group").destroy();
			parent.initWorkgroupTree();
		}

		//增加用户
		function optAdd(id, opt){
			var ids = jQuery("#main_table").getGridParam('selarrrow');
			if(ids==''){
				alert("请先选择");
			}else if(ids.length > 1){
				 alert("只能选择一条！");
			}else{
				$("#id").attr("value",ids[0]);
				$("#_wg_Id").attr("value",ids[0]);
				$("#ajax_from").attr("action", "${acsCtx}/organization/work-group!addWorkGroupToUsers.action");
				ajaxAnywhere.formName = "ajax_from";
				ajaxAnywhere.getZonesToReload = function() {
					return "acs_content";
				};
				ajaxAnywhere.onAfterResponseProcessing = function () {
				};
				ajaxAnywhere.submitAJAX();
			}
		}

		//移除用户
		function optRemove(id, opt){
			var ids = jQuery("#main_table").getGridParam('selarrrow');
			if(ids==''){
				alert("请先选择");
			}else if(ids.length > 1){
				 alert("只能选择一条！");
			}else{
				$("#id").attr("value",ids[0]);
				$("#_wg_Id").attr("value",ids[0]);
				$("#ajax_from").attr("action", "${acsCtx}/organization/work-group!removeWorkGroupToUsers.action");
				ajaxAnywhere.formName = "ajax_from";
				ajaxAnywhere.getZonesToReload = function() {
					return "acs_content";
				};
				ajaxAnywhere.onAfterResponseProcessing = function () {
				};
				ajaxAnywhere.submitAJAX();
			}
		}

		//提交
		function submitFormWork(){
			checkWorkName(checkWorkCode);
		}

		function checkWorkName(callback){
			$.ajax({
				type : "POST",
				url : "work-group!checkWorkName.action",
				data:{workGroupName:$("#workGroupName").val(),id:$("#id").val()},
				success : function(data){
		            if( data != "true" )
					{
		            	$("#workGroupName").val("");
		            	alert(data+"已注册，请更换工作组名称");
					}else{
						if( typeof(callback)=="function"){
							callback(function(){$('#inputForm').submit();});
						}
					}
				},
				error: function(){alert("错误");}
			});
		}

		/*
		*验证工作组编号
		*liudongxia
		*/
		function checkWorkCode(callback){
			$.ajax({
				type : "POST",
				url : "work-group!checkWorkCode.action",
				data:{workGroupCode:$("#workGroupCode").val(),id:$("#id").val()},
				success : function(data){
		            if( data != "true" ){   
		            	$("#workGroupCode").val("");
		            	alert(data+"已注册，请更换工作组编号 ");
					}else{
						if( typeof(callback)=="function"){
							callback();
						}
					}
				},
				error: function(){alert("错误");},
				onerror : "",
				onwait : "正在对工作组编号进行合法性校验，请稍候..."
			});
		}
		function refreshWg(){
			parent.jQuery.jstree._reference("#company_group").destroy();
			parent.initWorkgroupTree();
		}
		//工作组管理-增加用户-提交
		function submitForm(){
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			if(uIds==''){
				alert('没有选中的用户！');
				return;
			}else{
				$("#ids").attr("value",uIds);
				var workGroupId = $("#_wg_Id").val();
				$("#inputForm").append("<input  type='hidden' name='workGroupId' value='"+workGroupId+"'> ");
				$("#inputForm").submit(); 
			}
		}
	</script>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		<form action="#" id="defaultForm" name="defaultForm"></form>
		<form action="#" id="defaultForm1" name="defaultForm1"></form>
		<form id="ajax_from" name="ajax_from" action="" method="post">
			<input id="_wg_Id" type="hidden" name="workGroupId" value="${workGroupId}"> 
			<input id="_delete_Id" type="hidden" name="ides" value="${ides}"> 
			<input id="id" type="hidden" name="id"> 
		</form>
		<aa:zone name="acs_content">
			<div class="opt-btn">
				<security:authorize ifAnyGranted="createWorkGroup">
				    <button  class='btn' onclick="opt2();"><span><span><s:text name="common.create" /></span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="editWorkGroup">
				    <button  class='btn' onclick="opt();"><span><span><s:text name="common.alter" /></span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="deleteWorkGroup">
				    <button  class='btn' onclick="optDelete();"><span><span><s:text name="common.delete" /></span></span></button>
				</security:authorize>
				<%-- 
				<security:authorize ifAnyGranted="addWorkGroupToUser">
				    <button  class='btn' onclick="optAdd();"><span><span><s:text name="workGroup.addUser" /></span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="workGroupRemoveUser">
				    <button  class='btn' onclick="optRemove();"><span><span><s:text name="workGroup.removeUser" /></span></span></button>
				</security:authorize>
				--%>
			</div>
			<div id="opt-content" >
				<form id="deleteForm" name="deleteForm" method="post"></form>
				<view:jqGrid url="${acsCtx}/organization/work-group.action" pageName="page" code="ACS_WORKGROUPS" gridId="main_table"></view:jqGrid>
			</div>
		</aa:zone>
	</div>
</div>   	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
