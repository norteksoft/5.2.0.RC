<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title><s:text name="user.addUser"/></title>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/validation/cmxform.css"/>
</head>
<body>
<div class="ui-layout-center">
<aa:zone name="acs_content">
	<div class="opt-btn">
		 <security:authorize ifAnyGranted="saveWorkGroupToUser">
		   <button  class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
		   <button  class='btn' onclick="submitForm();"><span><span><s:text name="common.submit"/></span></span></button>
		   <button  class='btn' onclick="cancel();"><span><span><s:text name="common.cancel"/></span></span></button>
		 </security:authorize>  
		 <script type="text/javascript">
		 function cancel(){
	       location.href=webRoot+"/organization/work-group.action";
		 }
	     </script>
	</div>
	<div id="opt-content">
	<form id="inputForm" name="inputForm" action="work-group!workGroupAddUser.action" method="post">
       <input type="hidden" id="workGroupId" name="workGroupIdStr" value="${workGroupId}" />
       <input type="hidden" id="isAddOrRomove"  name="isAddOrRomove" value="${isAddOrRomove}" />
       <input type="hidden" id="ids"  name="ids" value="${ids}" />
		<s:if test="isAddOrRomove==0">
			<view:jqGrid url="${acsCtx}/organization/work-group!addWorkGroupToUsers.action?workGroupId=${workGroupId }" pageName="userPage" code="DEPART_LIST_USER" gridId="main_table"></view:jqGrid>
		</s:if>
		<s:elseif test="isAddOrRomove==1">
			<view:jqGrid url="${acsCtx}/organization/work-group!removeWorkGroupToUsers.action?workGroupId=${workGroupId }" pageName="userPage" code="DEPART_LIST_USER" gridId="main_table"></view:jqGrid>
		</s:elseif>
	</form>
 </div>
</aa:zone>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
