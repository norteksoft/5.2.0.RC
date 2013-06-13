<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
   <title><s:text name="role.roleManager"/></title>
    <%@ include file="/common/acs-meta.jsp"%>
    <script type="text/javascript">
    $().ready(function(){
		$.ajaxSetup({cache:false});
		initSystemRoleTree();
	});
    </script>
</head>

<body>
	<script type="text/javascript">
		var secMenu="_authorization_mgmt";
		var thirdMenu="_authorization";
	</script>
	
	<%@ include file="/menus/header.jsp"%>
	<%@ include file="/menus/acs-sec-nav.jsp"%>
	
	<div class="ui-layout-west">
		<%@ include file="/menus/authorization_menu.jsp"%>
	</div>	

	<div class="ui-layout-center">
		<iframe id="myIFrame" name="myIFrame" src="standard-role-data.action" frameborder="0" allowtransparency="no" style="height:99.3%" ></iframe>
	</div>
</body>
</html>
	