<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 
<%@ taglib uri="http://ajaxanywhere.sourceforge.net/" prefix="aa" %>
<%@ page import="com.norteksoft.product.util.WebContextUtils"%>
<html>
<head>
	<title><s:text name="role.roleManager"/></title>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		<aa:zone name="acs_button">
			<div class="opt-btn">
				<security:authorize ifAnyGranted="roleToUser"><button  class='btn' onclick="submitFormAuthorization('user');"><span><span>保存</span></span></button></security:authorize>
				<security:authorize ifAnyGranted="standardRoleRemoveUser"><button  class='btn' onclick="returnList(${roleId});"><span><span>取消</span></span></button></security:authorize>
			</div>	
		</aa:zone>

		<aa:zone name="acs_content">
		<form id="ajax_from" name="ajax_from" action="" method="post">
			<input type="hidden" id="roleId" name="roleId" value="${roleId}"/>
			<input type="hidden" id="id" name="id" value="${roleId}"/>
		</form>
		<form action="${acsCtx}/authorization/role!addUsersToRole.action" id="acsAddFrom" name="acsAddFrom" method="post">
			<input type="hidden" id="ajax_roleId" name="roleId" value="${roleId}" />
			<input type="hidden" id="ajax_isAddOrRomove" name="isAddOrRomove" value="${isAddOrRomove}"/>
		</form>
		<div id="opt-content">
			<acsTags:tree treeId="userTree"  defaultable="true" treeType="MAN_DEPARTMENT_TREE" multiple="true" userWithoutDeptVisible="true"></acsTags:tree>
		</div>
		</aa:zone>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
