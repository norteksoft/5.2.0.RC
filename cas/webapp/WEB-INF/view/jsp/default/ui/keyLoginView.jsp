<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html >
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<meta http-equiv="Cache-Control" content="no-store" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Expires" content="0" />
	<title>登录</title>
    <link rel="icon" href="<c:url value="/images/favicon.ico"/>" mce_href="<c:url value="/images/favicon.ico"/>" type="image/x-icon" /> 
	<link type="text/css" rel="stylesheet" href="<c:url value="/css/css.css"/>"/>
	<script src="<c:url value="/js/jquery.js"/>" type="text/javascript"></script>
	<script type="text/javascript">
		//此页面为客户特殊提供，通常都用casLoginView.jsp
	    function init() {
	        $('#submitBtn').click();
	   	}
	</script>
</head>

<body onload="init();">
<table style="width: 100%; height: 100%;">
	<tr>
		<td style="text-align: center;vertical-align: middle;">正在登录 ... </td>
	</tr>
</table>
<div style="display: none;">
<form:form method="post" id="fm1" cssClass="fm-v clearfix" commandName="${commandName}" htmlEscape="true">
			<form:errors path="*" cssClass="login-state" id="loginState" element="div" />
	        <div class="logo"></div>
			<div class="login-body">
			
            	<table width="300" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td>用户名:</td>
                    <td> <input type="text" autocomplete="false" size="25" value="a^&" tabindex="1" class="required" name="username" id="username"/> </td>
                  </tr>
                  <tr>
                    <td>密&nbsp;&nbsp;码:</td>
                    <td> <input type="password" autocomplete="off" size="25" value="123" tabindex="2" class="required" name="password" id="password" />  </td>
                  </tr>
                  <c:if test="${showCode}">
                  <tr>
                    <td>验证码:</td>
                    <td><input type="text" id="j_captcha_response" name="j_captcha_response" size="7" style="width: 100px;float: left;"/>&nbsp;<img 
                    		id="captcha" src="captcha.htm" alt="看不清，点击换一张" style="float: left;margin-left: 2px;cursor:hand;"
                    		onclick="this.src='captcha.htm?now='+ new Date().getTime();"/></td>
                  </tr>
                  </c:if>
                  <tr>
                    <td></td>
                    <td>
                    <input id="submitBtn" class="btn" name="submit" accesskey="l" value="登&nbsp;录" tabindex="4" type="submit" />
                    <input class="btn" name="reset" accesskey="c" value="清&nbsp;空" tabindex="5" type="reset" />
                    </td>
                  </tr>
                </table>
                
				<input type="hidden" name="lt" value="${flowExecutionKey}" />
				<input type="hidden" name="_eventId" value="submit" />
			</div>
			</form:form>
			<div class="login-foot">
				<a>北京北科汇智软件技术有限公司</a>
			</div>
</div>
</body>
</html>