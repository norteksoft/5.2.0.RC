<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 
<%@ taglib uri="http://ajaxanywhere.sourceforge.net/" prefix="aa" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	  <%@ include file="/common/acs-iframe-meta.jsp"%>
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/validation/cmxform.css"/>
</head>

<body>
<div class="ui-layout-center">
  <div class="opt-body">		
	<aa:zone name="acs_button">
		<div class="opt-btn">
			<security:authorize ifAnyGranted="saveAlterRole"><button  class='btn' onclick="saveRole();"><span><span>保存</span></span></button></security:authorize>
			<security:authorize ifAnyGranted="roleManager"><button  class='btn' onclick="setPageState();returnRoleList();"><span><span>返回</span></span></button></security:authorize>
		</div>
	</aa:zone>
	<aa:zone name="acs_content">
		<div id="message" style="color: red;"><s:actionmessage theme="mytheme"/></div>
		<div class="content">
			<form id="roleForm" name="roleForm" action="#">
			<input type="hidden" id="businessSystemId" name="businessSystemId" value="${businessSystemId}" />
			<input type="hidden" name="id" value="${id}">
			<table>
				<tr>
					<td>角色编号：</td>
					<td><input type="text"  name="code" value="${code}" <s:if test="id!=null">readonly="readonly"</s:if>> </td>
				</tr>
				<tr>
					<td>角色名称：</td>
					<td><input id="roleName" type="text"  name="name" value="${name}"> </td>
				</tr>
				<tr>
					<td>权重：</td>
					<td><input type="text"  name="weight" value="${weight}"> </td>
				</tr>
			</table>
			
			</form>
		</div>
	</aa:zone>
  </div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>