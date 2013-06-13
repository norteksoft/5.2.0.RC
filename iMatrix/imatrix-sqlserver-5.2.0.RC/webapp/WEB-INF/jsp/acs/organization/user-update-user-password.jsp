<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 
<%@ taglib uri="http://ajaxanywhere.sourceforge.net/" prefix="aa" %>
<html>
   <head>
   <title>修改密码</title>
   	<%@ include file="/common/acs-iframe-meta.jsp"%>
  
     <script type="text/javascript">

      function checkLoginPassword() {
  		$.ajax({
  			   type: "POST",
  			   url: "user!checkLoginPassword.action",
  			   data:{orgPassword:$("#password").val()},
  			   success: function(msg, textStatus){
  				   if(msg!=""){
  					   alert(msg);
  					   $("#password").val("");
  				   }
  		      },
  				error : function(XMLHttpRequest, textStatus) {
  					alert(textStatus);
  				}
  		  }); 
  	}
      
      function okBtn(){
    	 if($("#password").val()==''){
  			alert("请输入新密码！");
  			return;
         }
    	  if(confirmPassword()){
    		  $("#inputForm").attr("action", "${acsCtx}/organization/user!savePassWord.action");
    			ajaxAnywhere.formName = "inputForm";
    			ajaxAnywhere.getZonesToReload = function() {
    				return "updatePassword";
    			};

    			ajaxAnywhere.onAfterResponseProcessing = function() {
        			if($("#isPasswordChange").attr("value")=="true"){
        				window.parent.close();
        			}else{
        				showMsg();
        			}
    			};
    			ajaxAnywhere.submitAJAX();
    	  }else{
    		  $("#passwordConfirm").val("");
        	  alert("确认密码与新密码不一致");
    	  }
      }

      function confirmPassword(){
          if($("#password").val()==$("#passwordConfirm").val()){
              return true;
          }
          return false;
      }

 	</script>
   </head>
<body>
<div class="ui-layout-center">
	<aa:zone name="updatePassword">
		<div id="message" style="padding-top: 5px;"><font style="color: red;"><s:actionmessage theme="mytheme"></s:actionmessage></font></div>
	    <form action="" id="inputForm" method="post">
	           <input type="hidden" name="id" size="40" value="${id}" /> 
	           <input type="hidden" name="passWordCreateTime" size="40" value="${passWord_CreateTime}" />
				<input type="hidden" id="oraginalPassword" name="oraginalPassword" size="40"
					value="${oraginalPassword}" />
				<input  type="hidden" id="isPasswordChange" name="isPasswordChange" size="40"
					value="${isPasswordChange}" />
				<table class="full edit" style="margin-left: 20px;width:93%;">
			        <tr>
						<td style="width:70px;"><s:text name="user.loginName" /></td>
						<td style="text-align: left;">${user.loginName}</td>
					</tr>
					<tr>
						<td><s:text name="userInfo.rawPassword" /></td>
						<td><input type="password" id="levelpassword"
							name="levelpassword" /></td>
					</tr>
					<tr>
						<td><s:text name="userInfo.newPassword" /></td>
						<td><input type="password" id="password" name="user.password"
							onblur="checkLoginPassword(this)" value="" /></td>
					</tr>
					<tr>
						<td><s:text name="user.passwordConfirm" /></td>
						<td><input type="password" name="passwordConfirm" value="" id="passwordConfirm"/></td>
					</tr>
				</table>
				<div style="text-align:left;margin-left: 10px;width:95%;"><font color="red">*注意：密码不能输入引号</font></div>
			</form>
			<div class="opt-btn" style="margin-left: 80px;margin-top: 10px">
			<button id='savebtn' type="button" class='btn' onclick="okBtn();"><span><span>确定</span></span></button>
			<button id='savebtn' type="button" class='btn' onclick="window.parent.close();"><span><span>取消</span></span></button>
			</div>
	</aa:zone>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
