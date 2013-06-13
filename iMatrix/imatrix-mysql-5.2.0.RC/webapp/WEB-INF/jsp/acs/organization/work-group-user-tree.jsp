<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html >
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title><s:text name="company.companyManager"/></title>
</head>
<body>
<div class="ui-layout-center">
<aa:zone name="acs_content">
<div class="opt-btn">
<button  class='btn' onclick="workgroupAddUserSubmit();"><span><span><s:text name="common.submit"/></span></span></button>
<button  class='btn' onclick="setPageState();cancel('${workGroupId}');"><span><span><s:text name="common.cancel"/></span></span></button>
<script type="text/javascript">
function cancel(id){
    $("#addUserTreeForm").attr("action", "${acsCtx}/organization/user!getUserByWorkGroup.action?workGroupId="+id);
	ajaxAnywhere.formName = "addUserTreeForm";
	ajaxAnywhere.getZonesToReload = function() {
		return "acs_content";
	};
	ajaxAnywhere.onAfterResponseProcessing = function () {
	};
	ajaxAnywhere.submitAJAX();
}
</script>
</div>
<div id="opt-content">
<form action="#" id="addUserTreeForm" name="addUserTreeForm"></form>
<form id="workgroupAddUserForm" name="inputForm" action="work-group!workgroupAddUser.action" method="post">
   	<input type="hidden" id="workgroupId" name="workGroupId" value="${workGroupId}" />
	<div class="content">
		<acsTags:tree treeId="user_tree" defaultable="true" treeType="MAN_DEPARTMENT_TREE" multiple="true" userWithoutDeptVisible="true"></acsTags:tree>
	</div>
</form>
</div>
</aa:zone>
</div>
</body>
</html>
