<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title>用户管理</title>
	<script  type="text/javascript" src="${imatrixCtx}/widgets/calendar/WdatePicker.js"></script>
	
	<script type="text/javascript">
		//新建
		function addUser(id, url_, opt) {
			$("#ajaxId").attr("value","");
			checkUserRegister(id, url_, opt);
		}

		function checkUserRegister(id, url_, opt) {
			$.ajax( {
				data : {
					weburl : url_
				},
				type : "post",
				url : "${acsCtx}/organization/user!checkUserRegister.action",
				beforeSend : function(XMLHttpRequest) {
				},
				success : function(data, textStatus) {
					if (data == '1') {
						alert('<s:text name="user.userAlert"/>');
					} else {
						var deId = $("#departId").val();
						var oldType = $("#oType").val();
						if(oldType!='USERSBYDEPARTMENT'){
							$("#departId").attr("value",'');
						}
						$("#ajax_from").attr("action", data);
						ajaxAnywhere.formName = "ajax_from";
						ajaxAnywhere.getZonesToReload = function() {
							return "acs_content";
						};
						ajaxAnywhere.onAfterResponseProcessing = function() {
							HideSearchBox();
							var deId = $("#departId").val();
							var oldType = $("#oType").val();
							
							$("#oneDid").attr("value",deId);
							$("#oldDid").attr("value",deId);
							$("#oldType").attr("value",oldType);
							ruleInput();
							getContentHeight();
						};
						ajaxAnywhere.submitAJAX();
					}
				},
				error : function(XMLHttpRequest, textStatus) {
					alert(textStatus);
				}
			});
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
	    			if(!validateName($("#loginName").val())){
		    			$("#loginName").parent().append('<label  class="error">姓名、登录名必填且用户名、登录名中不能包含下划线(_)、等号(=)、横线(-)、竖线(|)、加号(+)、波浪线(~)</label>');
	    			}else if(!validateName($("#trueName1").val())){
		    			$("#trueName1").parent().append('<label  class="error">姓名、登录名必填且用户名、登录名中不能包含下划线(_)、等号(=)、横线(-)、竖线(|)、加号(+)、波浪线(~)</label>');
			    	}else if($("#mailboxDeploy").val()==''){
						alert("请选择邮箱配置！");
				    }else{
	    			 	saveUser(); 
	    			}
    			},
				rules: {
	    			passwordConfirm: {
	    				required: true,
	    				equalTo: "#password"
	    			},
	    			telephone: {
	    				digits: true
	    			},
	    			high: {
	    				number: true
	    			},
	    			weight: {
	    				number: true
	    			},
	    			IDcard: {
	    				creditcard: true
	    			},
	    			homePostCode: {
	    				digits: true
	    			},
	    			matePostCode: {
	    				digits: true
	    			},
	    			mateTelephone: {
	    				digits: true
	    			},
	    			FMPostCode: {
	    				digits: true
	    			}
			     },
				messages: {
			    	 'user.loginName':"必填",
			    	 'user.name':"必填",
			    	'user.password':"必填",
			    	passwordConfirm:{
			    	 	required:"必填",
			    	 	equalTo:"密码不一致"
			     	},
			    	'user.email':{
			    	 	required:"必填",
			    	 	email : "请输入正确的邮件地址"
				    },
					'user.mailSize':{
				    	required:"必填",
				    	number : "请输入8位以下的数字"
					},
					'IDcard':{
						creditcard:"请输入合法的身份证号码"
					},
					'homePostCode':{
						digits:"请只输入数字"
					},
					'matePostCode':{
						digits:"请只输入数字"
					},
					'mateTelephone':{
						digits:"请只输入数字"
					},
					'FMPostCode':{
						digits:"请只输入数字"
					}
				}
			});
		}

		//修改页面提交方法
		function submitForm() {
			$('#inputForm').submit();
		}

		function saveUser(){
			var departmentIds=$("#dids").attr("value");
			var departmentId=$("#deId").attr("value");
		    var i=$("#departId").val();
		    var t=$("#oType").val();
		    $("#oldDid").attr("value",i);
		    $("#oldType").attr("value",t);
			ajaxAnywhere.formName = "inputForm";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_content";
			};
			ajaxAnywhere.onAfterResponseProcessing = function() {
				ruleInput();
			    $('#message').show();
			    setTimeout("$('#message').hide()",3000);
			    getContentHeight();
			};
			ajaxAnywhere.submitAJAX(); 
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

		//ajax提交方法
		//修改
		function opt(url_, opt, id) {
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			if(uIds==''){
				alert("请先选择");
			}else if(uIds.length > 1){
				 alert("只能选择一条！");
			}else{
				if(opt!="LOOK"){
					$("#ajaxId").val(uIds);
					$("#edit").val(opt);
				}else{
					$("#ajaxId").val(id);
					$("#look").val(opt);
				}
				$("#ajax_from").attr("action", url_);
				ajaxAnywhere.formName = "ajax_from";
				ajaxAnywhere.getZonesToReload = function() {
					return "acs_content";
				};
				ajaxAnywhere.onAfterResponseProcessing = function() {
					HideSearchBox();
					$("#look").attr("value","");
					if(opt=="LOOK"){
						$('input').attr('disabled', 'disabled');
						$('select').attr('disabled', 'disabled');
					}else{
						ruleInput();
					}
					getContentHeight();
				};
				ajaxAnywhere.submitAJAX();
			}
		}

		//删除用户
		function opt_delete(url_, opt) {
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			if(uIds==''){
				alert('<s:text name="common.selectOne"/>');
				return;
			}else{
				if(confirm("确认删除吗？")){
					$.ajax({
						   type: "POST",
						   url: "user!checkIsAdmin.action?ids="+uIds,
						   success: function(data, textStatus){
							   if( data == "yes" ){
					                alert('<s:text name="common.delete.info"/>');
					                return;
								}else{
									setPageState();
									ajaxSubmit("ajax_from",webRoot+"/organization/user!falseDelete.action?ids="+uIds+"&departmType="+$("#oType").val(),"acs_list");
								}
						},error : function(XMLHttpRequest, textStatus) {
								alert(textStatus);
						}
					});
				}
			}
		} 

		//验证是否启动LDAP集成
		function ldapValidate(){
			$.ajax({
				   type: "POST",
				   url: "${acsCtx}/organization/user!validateLdapStart.action",
				   data:{},
				   success: function(data, textStatus) {
					   if( data == "true" ){
						   ldapSynchronous();
						}else{   
			            	alert('请配置LDAP信息 并启动LDAP集成');
						}
			      },
			      error : function(XMLHttpRequest, textStatus) {
						alert(textStatus);
					}
			  }); 
		}
		function ldapSynchronous(){
			$.ajax({
				   type: "POST",
				   url: "${acsCtx}/organization/user!synchronous.action",
				   data:{},
				   success: function(data, textStatus) {
					   parent.reinitUserTree();
					   alert(data);
					},
					error : function(XMLHttpRequest, textStatus) {
						alert(textStatus);
					}
			  }); 
		}

		//导入
		function importUser(){
			$.colorbox({href:'${acsCtx}/organization/user!showImportUser.action',
				iframe:true, innerWidth:350, innerHeight:100,overlayClose:false,title:"导入用户"});
		} 

		//导出
		function exportUser(){
			ajaxSubmit("ajax_from","${acsCtx}/organization/user!exportUser.action");
		}  

		//选择
		function Dtree2(treeStyle){
			if(treeStyle=='multiple'){
				$.colorbox({href:'${acsCtx}/organization/user!chooseDepartments.action?type=old',iframe:true, innerWidth:600, innerHeight:500,overlayClose:false,title:"请选择部门"});
			}else{
				$.colorbox({href:'${acsCtx}/organization/user!chooseOneDepartment.action?type=old',iframe:true, innerWidth:600, innerHeight:500,overlayClose:false,title:"请选择部门"});
			}
		}

		function checkLoginPassword(pass) {
			$.ajax({
				   type: "POST",
				   url: "user!checkLoginPassword.action",
				   data:{orgPassword:pass.value},
				   success: function(msg, textStatus){
					   if(msg!=""){
						   alert(msg);
						   $("#password").val("");
						   $("#password").blur();
					   }
			      },
					error : function(XMLHttpRequest, textStatus) {
						alert(textStatus);
					}
			  }); 
		}

		/**
		 *修改密码
		 *liudongxia
		 */
	  	function modifyPassWord(id,url_) {
	  		$.colorbox({href:url_+'?id='+id,iframe:true, innerWidth:640, innerHeight:150,overlayClose:false,title:"修改密码"});
	  		//$.colorbox({href:url_+'?id='+id,iframe:true, innerWidth:500, innerHeight:160,overlayClose:false,title:"修改密码"});
	  	}

	  	/**
		 *密码弹框”确定“按钮,设置密码
		 *liudongxia
		 */
	  	function setPassWord(password) {
	  		$("#password").attr("value",password);
	  		$("#passWordChange").attr("value","yes");
	  	}
	  	
	  	function shiftCheckbox(obj) {
			//checkset = document.getElementsByName("states");
			//if(indexs==0){
			//	checkset[1].checked = false;
			//}else{
			//	checkset[0].checked = false;
			//}
	  	  var objValue = $(obj).attr("checked");
          if(objValue==true){
       	   $(obj).attr('value','invocation');
          }else{
       	   $(obj).attr('value','enabled');
          }
		}
		function shiftCheckboxThree(obj){
           var objValue = $(obj).attr("checked");
           if(objValue==true){
        	   $(obj).attr('value','accountNonExpired');
           }else{
        	   $(obj).attr('value','accountNonExpiredNotChecked');
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

		function checkUserName(pass) {
	        var temp_Name = $("#temp_Name").val();
	        if(temp_Name==pass.value){
	        }
	        else{
	        	$.ajax({
	 			   type: "POST",
	 			   url: "user!checkUserName.action",
	 			   data:{userName:pass.value},
	 			   success: function(data, textStatus){
	 				   if( data == "true" ){
	 	 				   
	 	 			   }else{   
	 		            	$("#loginName").val("");
	 		            	$("#loginName").blur();
	 		            	alert(data+'<s:text name="user.registerAlready"/>');
	 					}
	 			    
	 		      },
	 				error : function(XMLHttpRequest, textStatus) {
	 					alert(textStatus);
	 				}
	 		  }); 
	        }
			
		}

		function clearInput(inputId){
			$("#"+inputId).attr("value","");
		}

		function unlockUser(){
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			if(uIds==''){
				alert('<s:text name="common.selectOne"/>');
				return;
			}else{
				if(confirm("确认解锁吗？")){
					$.ajax({
						   type: "POST",
						   url: "user!unlockUser.action?ids="+uIds,
						   success: function(data, textStatus){
									jQuery("#main_table").jqGrid().trigger("reloadGrid"); 
					                alert(data);
					                return;
						},error : function(XMLHttpRequest, textStatus) {
								alert(textStatus);
						}
					});
				}
			}
		}
		//显示提示信息，3秒后隐藏
		function showMsg(id,time){
			if(id==undefined)id="message";
			$("#"+id).show();
			if(time==undefined)time=3000;
			setTimeout('$("#'+id+'").hide();',time);
		}	
        //批量更换主职部门
		function changeMainDepartment(){
			 var uIds = jQuery("#main_table").getGridParam('selarrrow');
				if(uIds==''){
					 $("#notice").html("<span style='color: red;'>请选择用户！</span>");
					 $("#notice").children("span").fadeOut(5000);
					 return;
				}else{
					$.colorbox({href:'${acsCtx}/organization/user!toDepartmentToUsersDel.action?ids='+uIds+'&fromChangeMainDepartment=true',iframe:true, innerWidth:600, innerHeight:500,overlayClose:false,title:"请选择部门"});
				}
		}
		function changeBatchUserMainDepartment(deptId){
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			if(uIds==''){
				 alert("请先选择");
			}else{
				var userIds = "";
				for(var i=0;i<uIds.length;i++){
                   userIds+=uIds[i]+",";
				}
				 userIds=userIds.substring(0,userIds.length-1);
				 var departmentId = $("#departId").val();
			     $("#formName").attr("action","${acsCtx}/organization/user!batchChangeUserMainDepartment.action?ids="+userIds+"&newMainDepartmentId="+deptId+"&departmentId="+departmentId);
		         ajaxAnywhere.formName = "formName";
				 ajaxAnywhere.getZonesToReload = function() {
					return "acs_content";
				 };
				 ajaxAnywhere.onAfterResponseProcessing = function () {
				 };
				 ajaxAnywhere.submitAJAX();
			}
		}
		//工作组增加用户
		function addUsersToWorkgroup(){
			var workgroupId = $('#workGroupId').attr('value');
			if(workgroupId==''){
				alert('请在左边树上选择工作组');
				return;
			}
			ajaxSubmit('ajax_from', '${acsCtx}/organization/work-group!addUsersToWorkgroup.action', 'acs_content', loadUserTree);
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
		//部门人员树
		function loadUserTree(){
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
		}
		//提交为工作组添加的用户
		function workgroupAddUserSubmit(){
			var lists =allUsers("user_tree") ;
			if(lists.length <= 0){
				alert('<s:text name="user.seleteUser"/>');
				return;
			}
			var hasEffectiveUser = false;
			for(var i=0; i<lists.length; i++){
				    var type=lists[i].substring(0,lists[i].indexOf("_"));
				  if(type=="user"){  
					var parentLi = lists[i].substring(lists[i].indexOf("~")+1,lists[i].length);
					var parentDeptId = $('#tree_selected_id').attr('value');
					if(parentLi == parentDeptId){
						continue;
					}
					var userId= lists[i].substring(lists[i].indexOf("_")+1,lists[i].indexOf("="));
					if(userId.length > 0){
						var inpt = document.createElement("input");
						inpt.setAttribute("name", "userIds");
						inpt.setAttribute("value", userId);
						inpt.setAttribute("type", "hidden");
						document.getElementById("workgroupAddUserForm").appendChild(inpt);
						hasEffectiveUser = true;
					}
				  }
			}
			if(hasEffectiveUser){
				$('#workgroupAddUserForm').submit();
			}else alert('所选用户已在该部门');
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
	</script>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		 <form id="ajax_from" name="ajax_from" action="" method="post">  
            <input type="hidden" name="userId" id="ajaxId" />
	        <input type="hidden" name="departmentIds" id="departId" value="${oldDid }" />
	        <input type="hidden" name="oType" id="oType" value="${oldType }" />
	        <input type="hidden" name="look" id="look" />
	        <input type="hidden" name="comy" id="comy" value="${comy }"/>
	        <input type="hidden" name="edit" id="edit" />
	        <input type="hidden" name="fromWorkgroup" id="fromWorkgroup" value="${fromWorkgroup}">
	        <input type="hidden" name="workGroupId" id="workGroupId" value="${workGroupId}">
		</form>
		<form action="#" name="defaultForm" id="defaultForm"></form>
		<form action="${acsCtx}/organization/user!synchronous.action" id="ldapFormId"></form>
		<aa:zone name="acs_content">
			<s:if test="look==null">
			<div class="opt-btn">
				<security:authorize ifAnyGranted="query_queryUser">
				<button  id="searchButton" class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="addUser">
				<button  class='btn' onclick="addUser('null','${acsCtx}/organization/user!input.action','ADD');"><span><span>新建</span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="editUser">
				<button  class='btn' onclick="opt('${acsCtx}/organization/user!input.action','NEW');"><span><span>修改</span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="falseDelete">
				<button  class='btn' onclick="opt_delete('${acsCtx}/organization/user!falseDelete.action','NEW');"><span><span>删除</span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="userManager">
				<button  class='btn' onclick="opt('${acsCtx}/organization/user!userManger.action','NEW');"><span><span>管理</span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="acs_validateLdapStart">
				<button  class='btn' onclick="ldapValidate();"><span><span>同步LDAP</span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="acs_organization_user_showImportUser">
				<button  class='btn' onclick="importUser();"><span><span><s:text name="user.import"/></span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="acs_user_exportUser">
				<button  class='btn' onclick="exportUser();"><span><span><s:text name="user.export"/></span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="addUser">
				<button  class='btn' onclick="unlockUser();"><span><span>用户解锁</span></span></button>
				</security:authorize>
				<s:if test="departmType!='NODEPARTMENT'&&departmType!='NODEPARTMENT_USER'">
				<security:authorize ifAnyGranted="acs_organization_changeMainDepartment">
		    	<button  class='btn' onclick="changeMainDepartment();"><span><span>更换正职部门</span></span></button>
		        </security:authorize>
		        </s:if>
			</div>
			</s:if><s:else>
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
			</s:else>
			<div id="notice"> <s:actionmessage /> </div>	
			<div id="opt-content" >
			   <aa:zone name="acs_list">
				<form id="formName" name="formName" action="" method="post">
				     <input type="hidden" name="olDid" id="olDid" value="">
				     <input type="hidden" name="olType" id="olType" value="">
				     <input type="hidden" name="companyId" id="companyId" value="${companyId}">
				     <s:if test="comeFrom=='workgroup'">
				     <view:jqGrid url="${acsCtx}/organization/user.action?workGroupId=${workGroupId }&departmentId=${departmentId}&departmentIds=${oldDid}&oType=${oldType }&departmType=${departmType }" pageName="userPage" code="ACS_USER" gridId="main_table"></view:jqGrid>
				     </s:if>
				     <s:else>
				     <view:jqGrid url="${acsCtx}/organization/user.action?workGroupId=${workGroupId }&departmentId=${departmentId}&departmentIds=${oldDid}&oType=${oldType }&departmType=${departmType }" pageName="page" code="ACS_USER" gridId="main_table"></view:jqGrid>
				     </s:else>
				</form>
				</aa:zone>
			</div>
		</aa:zone>
	</div>
</div>    	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
