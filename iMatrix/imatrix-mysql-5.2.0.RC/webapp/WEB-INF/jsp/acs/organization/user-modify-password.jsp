<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
   <head>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
		<meta http-equiv="Cache-Control" content="no-store"/>
		<meta http-equiv="Pragma" content="no-cache"/>
		<meta http-equiv="Expires" content="0"/>
		<script language="javascript" type="text/javascript">
			var webRoot="${acsCtx}";
			var resourceRoot="${resourcesCtx}";
			var imatrixRoot="${imatrixCtx}";
		</script>
		
		<script type="text/javascript" src="${resourcesCtx}/js/jquery-all-1.0.js"></script>
		<link   type="text/css" href="${resourcesCtx}/css/<%=com.norteksoft.product.util.WebContextUtils.getTheme()%>/jquery-ui-1.8.16.custom.css" rel="stylesheet" id="_style"/>
			
  	<link rel="stylesheet" href="${acsCtx}/css/modifyPassword.css" type="text/css"/>
  	<link rel="stylesheet" href="${acsCtx}/css/validationEngine.jquery.css" type="text/css" media="screen" title="no title" charset="utf-8" />
  	<script src="${acsCtx}/js/jquery.validationEngine-fr.js" type="text/javascript"></script>
    <script src="${acsCtx}/js/jquery.validationEngine.js" type="text/javascript"></script>

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
       	}else{
			if(confirmPassword()){
			 window.parent.setPassWord($("#password").val());
			 window.parent.$.colorbox.close();
			}else{
			 $("#passwordConfirm").val("");
			 	  alert("确认密码与新密码不一致");
			}
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
<body style="padding: 10px 12px;">
<div class="ui-layout-center">
		<aa:zone name="acs_content">
		    <form action="${acsCtx}/organization/user!savePassWord.action" id="inputForm" method="post">
		           <input type="hidden" name="id" size="40" value="${id}" /> 
		           <input type="hidden" name="passWordCreateTime" size="40" value="${passWord_CreateTime}" />
					<input type="hidden" id="pass" name="pass" size="40" value="${user.password}" />
					
					<table style="width: 100%; height: 100%;border: 0px;">
					<tr><td style="text-align: center;">
					
						<table class="form_table" style="width: 100%;">
					        <tr style="padding: 6px 2px;">
								<td>姓名：</td>
								<td>${user.name}</td>
								<td>原密码：</td>
								<td><input type="password" id="levelpassword"
									name="levelpassword" value="${user.password}" readonly="readonly"/></td>
							</tr>
							<tr style="padding: 6px 2px;">
								<td>新密码：</td>
								<td><input type="password" id="password" name="user.password"
									onblur="checkLoginPassword(this)" value="" /></td>
								<td>确认密码：</td>
								<td><input type="password" name="passwordConfirm" value="" id="passwordConfirm"/></td>
							</tr>
							<tr style="padding: 6px 2px;">
							<td colspan="4"><font color="red">*注意：密码不能输入引号</font></td>
							</tr>
						</table>
					
					</td> </tr>
					<tr><td style="text-align: center;">
						<button type="button" class='btn' onclick="okBtn();"><span><span>确定</span></span></button>
						<button type="button"  class='btn' onclick="window.parent.$.colorbox.close();"><span><span>取消</span></span></button>
					</td> </tr>
					</table>
					
					</form>
				</aa:zone>
			</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
