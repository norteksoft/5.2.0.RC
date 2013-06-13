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
		 <security:authorize ifAnyGranted="saveCreateWorkGroup">
		   <button  class='btn' onclick="submitFormWork();"><span><span><s:text name="common.submit"/></span></span></button>
		   <button  class='btn' onclick="setPageState();cancel();"><span><span><s:text name="common.cancel"/></span></span></button>
		 </security:authorize>  
		<script type="text/javascript">
		function cancel(){
	     location.href=webRoot+"/organization/work-group.action";
		}
	   </script>
	</div>
	<div id="opt-content">
	<form id="inputForm" name="inputForm" action="${acsCtx}/organization/work-group!saveWorkGroup.action" method="post">
		<input type="hidden" name="id" value="${id}" id="id"/>
		<input type="hidden" name="companyId" value="${companyId}" id="companyId" />
		<table class="form_table">
			<tr>
				<td class="content-title" ><s:text name="workGroup.workGroupCode"/>:</td>
                <td>
                <input  type="text" id="workGroupCode" name="code"  value="${code}" onblur="checkWorkCode();"/><font color="red">*</font>
                </td>
			</tr>
			<tr>
			<td class="content-title" ><s:text name="workGroup.workGroupName"/>:</td>
			<td>
              <input  type="text" id="workGroupName" name="name" value="${name}" onblur="checkWorkName();"/><font color="red">*</font>
            </td>
			</tr>
			<tr>
			<td class="content-title" ><s:text name="workGroup.workGroupSummary"/>:</td>
			<td><input type="text" name="description" id="workSummary" value="${description}"/></td>
			</tr>
		</table>
	  </form>	
	</div>
</aa:zone>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>

