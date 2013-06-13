<html>
	<head>
		<title>密码修改</title>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
        <meta http-equiv="Cache-Control" content="no-store"/>
        <meta http-equiv="Pragma" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>
        
		<link href="${Parameters.resourceCtx}/css/default.css" type="text/css" rel="stylesheet"/>
        <script src="${Parameters.resourceCtx}/js/jquery.js" type="text/javascript"></script>
        <link href="${base}/js/validate/jquery.validate.css" type="text/css" rel="stylesheet" />
		<script src="${base}/js/validate/messages_cn.js" type="text/javascript"></script>
		
		<script type="text/javascript">
			
			function checkLoginPassword(pass){// 规则
				$.ajax({
					type: "POST",
					url: "${base}/portal/check-login-password.action",
					data:{orgPassword:pass.value},
					success: function(msg, textStatus){
						if(msg!=""){
							alert(msg);
							document.getElementById("password").value="";
						}
					},error : function(XMLHttpRequest, textStatus) {
						alert(textStatus);
					}
				}); 
			}
			
			var counts = 0;
			function checkOldPassword(){
				$.ajax({
					type: "POST",
					url: "${base}/portal/update-old-password.action",
					data:{oraginalPassword:$("#oldPassword").val(),password:$("#password").val(),id:$("#id").val()},
					success: function(msg, textStatus){
						if(msg!=""){
							counts = counts+1;
							if(msg == 'old_pwd_error'){
								alert('原密码错误');
							}
							if(counts == 3){
								alert("密码错误超过三次");
								redirect();
							}
						}else{
							alert('密码修改成功');
							redirect();
						}
					}, error : function(XMLHttpRequest, textStatus) {
						alert(textStatus);
					}
				}); 
			}
			
			function redirect(){
				var url = document.getElementById("redirectUrl").value;
				if(url.indexOf("http")==0){
					window.location.href=url;
				}else{
					window.location.href="${base}" + url;
				}
			}
		</script>
		<style type="text/css">
			body{background-color: #f5f5f5;}
			#contentTable tr{margin: 5px 0;}
			#contentTable tr td{height: 30px;line-height: 30px;}
		</style>
	</head>
	
	<body>
	
		<form action="${base}/update-old-password.action" id="inputForm" name="inputForm" method="post">
			<input type="hidden" id="id" name="id" size="40" value="${Parameters.id}" />
			<input type="hidden" id="redirectUrl" name="redirectUrl" size="40" value="${Parameters.url}" />
			<table width="90%;" height="80%">
				<tr><td align="center" valign="middle">
					<table id="contentTable">
						<caption style="color:red;">密码还有${Parameters.overdue}天过期，请修改密码。</caption>
						<tr><td>用户名：</td> <td>${Parameters.name }</td></tr>
						<tr><td>原密码：</td> <td><input type="password" id="oldPassword" name="oldPassword"/></td></tr>
						<tr>
						<td>新密码：</td> <td><input type="password" id ="password" name="password" onblur="checkLoginPassword(this)"/></td>
						</tr>
						<tr><td>重复密码：</td> <td><input type="password" name="passwordConfirm"/></td></tr>
						  
						      	<tr align="center">
						   <td colspan="2" align="center">
						   <input type="button" value="跳过" onclick="redirect();"/>&nbsp;
						   <input type="reset" value="清空" />&nbsp;
						   <input type="button" value="提交" onclick="checkOldPassword()"/>
						  </td>
						</tr>
					</table>
				</td></tr>
			</table>
		</form>
		
	</body>
</html>
